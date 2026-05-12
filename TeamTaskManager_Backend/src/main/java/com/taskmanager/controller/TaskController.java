package com.taskmanager.controller;

import com.taskmanager.dto.TaskDTO;
import com.taskmanager.service.TaskService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class TaskController {

    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    /**
     * Get current user ID from request
     */
    private Long getUserId(HttpServletRequest request) {
        Object userId = request.getAttribute("userId");
        if (userId == null) {
            throw new com.taskmanager.exception.AccessDeniedException("User not authenticated");
        }
        return (Long) userId;
    }

    /**
     * Create a new task in a project
     */
    @PostMapping("/projects/{projectId}/tasks")
    public ResponseEntity<?> createTask(@PathVariable Long projectId,
                                       @RequestBody TaskDTO taskDTO,
                                       HttpServletRequest request) {
        try {
            if (taskDTO.getTitle() == null || taskDTO.getTitle().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("Task title is required"));
            }

            Long userId = getUserId(request);
            TaskDTO createdTask = taskService.createTask(projectId, taskDTO, userId);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(createSuccessResponse(createdTask, "Task created successfully"));
        } catch (com.taskmanager.exception.ResourceNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(ex.getMessage()));
        } catch (com.taskmanager.exception.AccessDeniedException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(createErrorResponse(ex.getMessage()));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(createErrorResponse(ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to create task: " + ex.getMessage()));
        }
    }

    /**
     * Get all tasks in a project
     */
    @GetMapping("/projects/{projectId}/tasks")
    public ResponseEntity<?> getProjectTasks(@PathVariable Long projectId, HttpServletRequest request) {
        try {
            Long userId = getUserId(request);
            List<TaskDTO> tasks = taskService.getProjectTasks(projectId, userId);

            return ResponseEntity.ok(createSuccessResponse(tasks, "Tasks retrieved successfully"));
        } catch (com.taskmanager.exception.ResourceNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(ex.getMessage()));
        } catch (com.taskmanager.exception.AccessDeniedException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(createErrorResponse(ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to retrieve tasks: " + ex.getMessage()));
        }
    }

    /**
     * Get task by ID
     */
    @GetMapping("/tasks/{taskId}")
    public ResponseEntity<?> getTaskById(@PathVariable Long taskId, HttpServletRequest request) {
        try {
            Long userId = getUserId(request);
            TaskDTO task = taskService.getTaskById(taskId, userId);

            return ResponseEntity.ok(createSuccessResponse(task, "Task retrieved successfully"));
        } catch (com.taskmanager.exception.ResourceNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(ex.getMessage()));
        } catch (com.taskmanager.exception.AccessDeniedException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(createErrorResponse(ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to retrieve task: " + ex.getMessage()));
        }
    }

    /**
     * Update task details
     */
    @PutMapping("/tasks/{taskId}")
    public ResponseEntity<?> updateTask(@PathVariable Long taskId,
                                       @RequestBody TaskDTO taskDTO,
                                       HttpServletRequest request) {
        try {
            Long userId = getUserId(request);
            TaskDTO updatedTask = taskService.updateTask(taskId, taskDTO, userId);

            return ResponseEntity.ok(createSuccessResponse(updatedTask, "Task updated successfully"));
        } catch (com.taskmanager.exception.ResourceNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(ex.getMessage()));
        } catch (com.taskmanager.exception.AccessDeniedException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(createErrorResponse(ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to update task: " + ex.getMessage()));
        }
    }

    /**
     * Update task status
     */
    @PatchMapping("/tasks/{taskId}/status")
    public ResponseEntity<?> updateTaskStatus(@PathVariable Long taskId,
                                             @RequestBody Map<String, String> requestBody,
                                             HttpServletRequest request) {
        try {
            String status = requestBody.get("status");
            if (status == null || status.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("Status is required"));
            }

            Long userId = getUserId(request);
            TaskDTO updatedTask = taskService.updateTaskStatus(taskId, status, userId);

            return ResponseEntity.ok(createSuccessResponse(updatedTask, "Task status updated successfully"));
        } catch (com.taskmanager.exception.ResourceNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(ex.getMessage()));
        } catch (com.taskmanager.exception.AccessDeniedException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(createErrorResponse(ex.getMessage()));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(createErrorResponse("Invalid status: " + ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to update task status: " + ex.getMessage()));
        }
    }

    /**
     * Assign task to a user
     */
    @PatchMapping("/tasks/{taskId}/assign")
    public ResponseEntity<?> assignTask(@PathVariable Long taskId,
                                       @RequestBody Map<String, Long> requestBody,
                                       HttpServletRequest request) {
        try {
            Long assignedToId = requestBody.get("assignedTo");
            if (assignedToId == null) {
                return ResponseEntity.badRequest().body(createErrorResponse("assignedTo is required"));
            }

            Long userId = getUserId(request);
            TaskDTO updatedTask = taskService.assignTask(taskId, assignedToId, userId);

            return ResponseEntity.ok(createSuccessResponse(updatedTask, "Task assigned successfully"));
        } catch (com.taskmanager.exception.ResourceNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(ex.getMessage()));
        } catch (com.taskmanager.exception.AccessDeniedException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(createErrorResponse(ex.getMessage()));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(createErrorResponse(ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to assign task: " + ex.getMessage()));
        }
    }

    /**
     * Delete task
     */
    @DeleteMapping("/tasks/{taskId}")
    public ResponseEntity<?> deleteTask(@PathVariable Long taskId, HttpServletRequest request) {
        try {
            Long userId = getUserId(request);
            taskService.deleteTask(taskId, userId);

            return ResponseEntity.ok(createSuccessResponse(null, "Task deleted successfully"));
        } catch (com.taskmanager.exception.ResourceNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(ex.getMessage()));
        } catch (com.taskmanager.exception.AccessDeniedException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(createErrorResponse(ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to delete task: " + ex.getMessage()));
        }
    }

    /**
     * Helper method to create error response
     */
    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "ERROR");
        response.put("message", message);
        return response;
    }

    /**
     * Helper method to create success response
     */
    private Map<String, Object> createSuccessResponse(Object data, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "SUCCESS");
        response.put("data", data);
        response.put("message", message);
        return response;
    }
}
