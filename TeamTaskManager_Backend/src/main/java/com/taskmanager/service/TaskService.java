package com.taskmanager.service;

import com.taskmanager.dto.TaskDTO;
import com.taskmanager.entity.Project;
import com.taskmanager.entity.Task;
import com.taskmanager.entity.User;
import com.taskmanager.exception.AccessDeniedException;
import com.taskmanager.exception.ResourceNotFoundException;
import com.taskmanager.repository.ProjectRepository;
import com.taskmanager.repository.TaskRepository;
import com.taskmanager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectService projectService;

    @Autowired
    public TaskService(TaskRepository taskRepository,
                      ProjectRepository projectRepository,
                      UserRepository userRepository,
                      ProjectService projectService) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.projectService = projectService;
    }

    /**
     * Create a new task (any project member can create)
     */
    @Transactional
    public TaskDTO createTask(Long projectId, TaskDTO taskDTO, Long userId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        // Verify user is project member
        if (!projectService.isProjectMember(projectId, userId) && 
            !projectService.isProjectAdmin(projectId, userId)) {
            throw new AccessDeniedException("You don't have access to this project");
        }

        User creator = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Task task = new Task();
        task.setProject(project);
        task.setTitle(taskDTO.getTitle());
        task.setDescription(taskDTO.getDescription());
        task.setPriority(Task.Priority.valueOf(taskDTO.getPriority() != null ? taskDTO.getPriority() : "MEDIUM"));
        task.setDueDate(taskDTO.getDueDate());
        task.setCreatedBy(creator);
        task.setStatus(Task.Status.TO_DO);
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());

        // Assign task if assignedToId provided
        if (taskDTO.getAssignedToId() != null) {
            User assignedTo = userRepository.findById(taskDTO.getAssignedToId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
            
            // Verify assigned user is project member
            if (!projectService.isProjectMember(projectId, taskDTO.getAssignedToId()) && 
                !projectService.isProjectAdmin(projectId, taskDTO.getAssignedToId())) {
                throw new IllegalArgumentException("Assigned user is not a member of this project");
            }
            
            task.setAssignedTo(assignedTo);
        }

        Task savedTask = taskRepository.save(task);
        return new TaskDTO(savedTask);
    }

    /**
     * Get all tasks in a project (member can view all, non-member cannot view)
     */
    public List<TaskDTO> getProjectTasks(Long projectId, Long userId) {
        projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        // Verify user has access
        if (!projectService.isProjectMember(projectId, userId) && 
            !projectService.isProjectAdmin(projectId, userId)) {
            throw new AccessDeniedException("You don't have access to this project");
        }

        List<Task> tasks = taskRepository.findByProjectProjectId(projectId);
        
        List<TaskDTO> dtos = new ArrayList<>();
        for (Task task : tasks) {
            dtos.add(new TaskDTO(task));
        }
        return dtos;
    }

    /**
     * Get task by ID (verify user has access)
     */
    public TaskDTO getTaskById(Long taskId, Long userId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        // Verify user has access to project
        if (!projectService.isProjectMember(task.getProject().getProjectId(), userId) && 
            !projectService.isProjectAdmin(task.getProject().getProjectId(), userId)) {
            throw new AccessDeniedException("You don't have access to this task");
        }

        return new TaskDTO(task);
    }

    /**
     * Update task (admin can update any; assigned member can update their own)
     */
    @Transactional
    public TaskDTO updateTask(Long taskId, TaskDTO taskDTO, Long userId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        Long projectId = task.getProject().getProjectId();
        boolean isAdmin = projectService.isProjectAdmin(projectId, userId);
        boolean isAssigned = task.getAssignedTo() != null && task.getAssignedTo().getUserId().equals(userId);

        if (!isAdmin && !isAssigned) {
            throw new AccessDeniedException("Only project admin or assigned user can update this task");
        }

        task.setTitle(taskDTO.getTitle());
        task.setDescription(taskDTO.getDescription());
        if (taskDTO.getPriority() != null) {
            task.setPriority(Task.Priority.valueOf(taskDTO.getPriority()));
        }
        task.setDueDate(taskDTO.getDueDate());
        task.setUpdatedAt(LocalDateTime.now());

        Task updatedTask = taskRepository.save(task);
        return new TaskDTO(updatedTask);
    }

    /**
     * Update task status (admin or assigned user)
     */
    @Transactional
    public TaskDTO updateTaskStatus(Long taskId, String status, Long userId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        Long projectId = task.getProject().getProjectId();
        boolean isAdmin = projectService.isProjectAdmin(projectId, userId);
        boolean isAssigned = task.getAssignedTo() != null && task.getAssignedTo().getUserId().equals(userId);

        if (!isAdmin && !isAssigned) {
            throw new AccessDeniedException("You don't have permission to update this task status");
        }

        task.setStatus(Task.Status.valueOf(status));
        task.setUpdatedAt(LocalDateTime.now());

        Task updatedTask = taskRepository.save(task);
        return new TaskDTO(updatedTask);
    }

    /**
     * Assign task to user (admin only)
     */
    @Transactional
    public TaskDTO assignTask(Long taskId, Long assignedToId, Long userId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        Long projectId = task.getProject().getProjectId();
        if (!projectService.isProjectAdmin(projectId, userId)) {
            throw new AccessDeniedException("Only project admin can assign tasks");
        }

        User assignedUser = userRepository.findById(assignedToId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Verify assigned user is project member
        if (!projectService.isProjectMember(projectId, assignedToId) && 
            !projectService.isProjectAdmin(projectId, assignedToId)) {
            throw new IllegalArgumentException("Assigned user is not a member of this project");
        }

        task.setAssignedTo(assignedUser);
        task.setUpdatedAt(LocalDateTime.now());

        Task updatedTask = taskRepository.save(task);
        return new TaskDTO(updatedTask);
    }

    /**
     * Delete task (admin only)
     */
    @Transactional
    public void deleteTask(Long taskId, Long userId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        Long projectId = task.getProject().getProjectId();
        if (!projectService.isProjectAdmin(projectId, userId)) {
            throw new AccessDeniedException("Only project admin can delete tasks");
        }

        taskRepository.delete(task);
    }

    /**
     * Get overdue tasks
     */
    public List<TaskDTO> getOverdueTasks(Long userId) {
        List<Task> overdueTasks = taskRepository.findOverdueTasks(LocalDate.now());
        
        List<TaskDTO> dtos = new ArrayList<>();
        for (Task task : overdueTasks) {
            // Only include if user has access to project
            if (projectService.isProjectMember(task.getProject().getProjectId(), userId) || 
                projectService.isProjectAdmin(task.getProject().getProjectId(), userId)) {
                dtos.add(new TaskDTO(task));
            }
        }
        return dtos;
    }

    /**
     * Get tasks assigned to user
     */
    public List<TaskDTO> getUserAssignedTasks(Long userId) {
        List<Task> tasks = taskRepository.findByAssignedToUserId(userId);
        
        List<TaskDTO> dtos = new ArrayList<>();
        for (Task task : tasks) {
            dtos.add(new TaskDTO(task));
        }
        return dtos;
    }
}
