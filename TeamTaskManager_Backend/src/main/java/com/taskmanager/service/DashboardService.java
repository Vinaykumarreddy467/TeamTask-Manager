package com.taskmanager.service;

import com.taskmanager.dto.DashboardDTO;
import com.taskmanager.dto.TaskDTO;
import com.taskmanager.entity.Project;
import com.taskmanager.entity.ProjectMember;
import com.taskmanager.entity.Task;
import com.taskmanager.repository.ProjectMemberRepository;
import com.taskmanager.repository.ProjectRepository;
import com.taskmanager.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class DashboardService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;

    @Autowired
    public DashboardService(TaskRepository taskRepository,
                           ProjectRepository projectRepository,
                           ProjectMemberRepository projectMemberRepository) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.projectMemberRepository = projectMemberRepository;
    }

    /**
     * Get all projects a user has access to (admin + member)
     */
    private List<Project> getUserProjects(Long userId) {
        Set<Long> seenIds = new HashSet<>();
        List<Project> allProjects = new ArrayList<>();

        // Admin projects
        List<Project> adminProjects = projectRepository.findByAdminUserUserId(userId);
        for (Project p : adminProjects) {
            seenIds.add(p.getProjectId());
            allProjects.add(p);
        }

        // Member projects (via join table)
        List<ProjectMember> memberships = projectMemberRepository.findByUserUserId(userId);
        for (ProjectMember pm : memberships) {
            Project p = pm.getProject();
            if (!seenIds.contains(p.getProjectId())) {
                seenIds.add(p.getProjectId());
                allProjects.add(p);
            }
        }

        return allProjects;
    }

    /**
     * Get dashboard overview for user
     */
    public DashboardDTO getDashboardOverview(Long userId) {
        DashboardDTO dashboard = new DashboardDTO();

        List<Project> userProjects = getUserProjects(userId);

        long totalTasks = 0;
        long toDoCount = 0;
        long inProgressCount = 0;
        long doneCount = 0;
        long overdueCount = 0;
        Map<String, Long> tasksPerUser = new HashMap<>();

        for (Project project : userProjects) {
            totalTasks += taskRepository.countByProjectId(project.getProjectId());
            toDoCount += taskRepository.countTodoByProjectId(project.getProjectId());
            inProgressCount += taskRepository.countInProgressByProjectId(project.getProjectId());
            doneCount += taskRepository.countDoneByProjectId(project.getProjectId());
        }

        // Count overdue tasks
        List<Task> overdueTasksList = taskRepository.findOverdueTasks(LocalDate.now());
        Set<Long> userProjectIds = new HashSet<>();
        for (Project p : userProjects) {
            userProjectIds.add(p.getProjectId());
        }
        overdueCount = overdueTasksList.stream()
                .filter(task -> userProjectIds.contains(task.getProject().getProjectId()))
                .count();

        dashboard.setTotalTasks(totalTasks);
        dashboard.setToDoCount(toDoCount);
        dashboard.setInProgressCount(inProgressCount);
        dashboard.setDoneCount(doneCount);
        dashboard.setOverdueCount(overdueCount);
        dashboard.setTasksPerUser(tasksPerUser);

        return dashboard;
    }

    /**
     * Get tasks grouped by status
     */
    public Map<String, Long> getTasksByStatus(Long userId) {
        Map<String, Long> tasksByStatus = new HashMap<>();

        List<Project> userProjects = getUserProjects(userId);

        long toDoCount = 0;
        long inProgressCount = 0;
        long doneCount = 0;

        for (Project project : userProjects) {
            toDoCount += taskRepository.countTodoByProjectId(project.getProjectId());
            inProgressCount += taskRepository.countInProgressByProjectId(project.getProjectId());
            doneCount += taskRepository.countDoneByProjectId(project.getProjectId());
        }

        tasksByStatus.put("TO_DO", toDoCount);
        tasksByStatus.put("IN_PROGRESS", inProgressCount);
        tasksByStatus.put("DONE", doneCount);

        return tasksByStatus;
    }

    /**
     * Get tasks grouped by assigned user
     */
    public Map<String, Long> getTasksPerUser(Long userId) {
        Map<String, Long> tasksPerUser = new HashMap<>();

        List<Project> userProjects = getUserProjects(userId);

        for (Project project : userProjects) {
            List<Task> projectTasks = taskRepository.findByProjectProjectId(project.getProjectId());
            
            for (Task task : projectTasks) {
                if (task.getAssignedTo() != null) {
                    String assignedUserName = task.getAssignedTo().getName();
                    tasksPerUser.put(assignedUserName, 
                                    tasksPerUser.getOrDefault(assignedUserName, 0L) + 1);
                }
            }
        }

        return tasksPerUser;
    }

    /**
     * Get overdue tasks
     */
    public List<TaskDTO> getOverdueTasks(Long userId) {
        List<Task> overdueTasks = taskRepository.findOverdueTasks(LocalDate.now());
        List<Project> userProjects = getUserProjects(userId);

        Set<Long> userProjectIds = new HashSet<>();
        for (Project p : userProjects) {
            userProjectIds.add(p.getProjectId());
        }

        List<TaskDTO> dtos = new ArrayList<>();
        for (Task task : overdueTasks) {
            if (userProjectIds.contains(task.getProject().getProjectId())) {
                dtos.add(new TaskDTO(task));
            }
        }

        return dtos;
    }

    /**
     * Get task statistics for a specific project
     */
    public DashboardDTO getProjectStatistics(Long projectId) {
        DashboardDTO dashboard = new DashboardDTO();

        long totalTasks = taskRepository.countByProjectId(projectId);
        long toDoCount = taskRepository.countTodoByProjectId(projectId);
        long inProgressCount = taskRepository.countInProgressByProjectId(projectId);
        long doneCount = taskRepository.countDoneByProjectId(projectId);

        List<Task> projectTasks = taskRepository.findByProjectProjectId(projectId);
        long overdueCount = projectTasks.stream()
                .filter(task -> task.getDueDate() != null && 
                        task.getDueDate().isBefore(LocalDate.now()) &&
                        !task.getStatus().equals(Task.Status.DONE))
                .count();

        dashboard.setTotalTasks(totalTasks);
        dashboard.setToDoCount(toDoCount);
        dashboard.setInProgressCount(inProgressCount);
        dashboard.setDoneCount(doneCount);
        dashboard.setOverdueCount(overdueCount);

        return dashboard;
    }
}
