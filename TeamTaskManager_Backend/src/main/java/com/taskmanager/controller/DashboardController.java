package com.taskmanager.controller;

import com.taskmanager.dto.DashboardDTO;
import com.taskmanager.service.DashboardService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    @Autowired
    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
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
     * Get dashboard overview with all metrics
     */
    @GetMapping("/overview")
    public ResponseEntity<?> getDashboardOverview(HttpServletRequest request) {
        try {
            Long userId = getUserId(request);
            DashboardDTO dashboard = dashboardService.getDashboardOverview(userId);

            return ResponseEntity.ok(createSuccessResponse(dashboard, "Dashboard overview retrieved successfully"));
        } catch (com.taskmanager.exception.AccessDeniedException ex) {
            return ResponseEntity.status(403)
                    .body(createErrorResponse(ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(500)
                    .body(createErrorResponse("Failed to retrieve dashboard: " + ex.getMessage()));
        }
    }

    /**
     * Get tasks grouped by status
     */
    @GetMapping("/tasks-by-status")
    public ResponseEntity<?> getTasksByStatus(HttpServletRequest request) {
        try {
            Long userId = getUserId(request);
            Map<String, Long> tasksByStatus = dashboardService.getTasksByStatus(userId);

            return ResponseEntity.ok(createSuccessResponse(tasksByStatus, "Tasks by status retrieved successfully"));
        } catch (com.taskmanager.exception.AccessDeniedException ex) {
            return ResponseEntity.status(403)
                    .body(createErrorResponse(ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(500)
                    .body(createErrorResponse("Failed to retrieve tasks by status: " + ex.getMessage()));
        }
    }

    /**
     * Get tasks grouped by assigned user
     */
    @GetMapping("/tasks-per-user")
    public ResponseEntity<?> getTasksPerUser(HttpServletRequest request) {
        try {
            Long userId = getUserId(request);
            Map<String, Long> tasksPerUser = dashboardService.getTasksPerUser(userId);

            return ResponseEntity.ok(createSuccessResponse(tasksPerUser, "Tasks per user retrieved successfully"));
        } catch (com.taskmanager.exception.AccessDeniedException ex) {
            return ResponseEntity.status(403)
                    .body(createErrorResponse(ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(500)
                    .body(createErrorResponse("Failed to retrieve tasks per user: " + ex.getMessage()));
        }
    }

    /**
     * Get overdue tasks
     */
    @GetMapping("/overdue-tasks")
    public ResponseEntity<?> getOverdueTasks(HttpServletRequest request) {
        try {
            Long userId = getUserId(request);
            List<?> overdueTasks = dashboardService.getOverdueTasks(userId);

            return ResponseEntity.ok(createSuccessResponse(overdueTasks, "Overdue tasks retrieved successfully"));
        } catch (com.taskmanager.exception.AccessDeniedException ex) {
            return ResponseEntity.status(403)
                    .body(createErrorResponse(ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(500)
                    .body(createErrorResponse("Failed to retrieve overdue tasks: " + ex.getMessage()));
        }
    }

    /**
     * Get statistics for a specific project
     */
    @GetMapping("/project/{projectId}/statistics")
    public ResponseEntity<?> getProjectStatistics(@PathVariable Long projectId, HttpServletRequest request) {
        try {
            DashboardDTO statistics = dashboardService.getProjectStatistics(projectId);

            return ResponseEntity.ok(createSuccessResponse(statistics, "Project statistics retrieved successfully"));
        } catch (Exception ex) {
            return ResponseEntity.status(500)
                    .body(createErrorResponse("Failed to retrieve project statistics: " + ex.getMessage()));
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
