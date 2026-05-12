package com.taskmanager.controller;

import com.taskmanager.dto.ProjectDTO;
import com.taskmanager.dto.ProjectMemberDTO;
import com.taskmanager.service.ProjectService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    @Autowired
    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
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
     * Create a new project
     */
    @PostMapping
    public ResponseEntity<?> createProject(@RequestBody ProjectDTO projectDTO, HttpServletRequest request) {
        try {
            if (projectDTO.getProjectName() == null || projectDTO.getProjectName().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("Project name is required"));
            }

            Long userId = getUserId(request);
            ProjectDTO createdProject = projectService.createProject(projectDTO, userId);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(createSuccessResponse(createdProject, "Project created successfully"));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to create project: " + ex.getMessage()));
        }
    }

    /**
     * Get all projects for authenticated user
     */
    @GetMapping
    public ResponseEntity<?> getUserProjects(HttpServletRequest request) {
        try {
            Long userId = getUserId(request);
            List<ProjectDTO> projects = projectService.getUserProjects(userId);

            return ResponseEntity.ok(createSuccessResponse(projects, "Projects retrieved successfully"));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to retrieve projects: " + ex.getMessage()));
        }
    }

    /**
     * Get project by ID
     */
    @GetMapping("/{projectId}")
    public ResponseEntity<?> getProjectById(@PathVariable Long projectId, HttpServletRequest request) {
        try {
            Long userId = getUserId(request);
            ProjectDTO project = projectService.getProjectById(projectId, userId);

            return ResponseEntity.ok(createSuccessResponse(project, "Project retrieved successfully"));
        } catch (com.taskmanager.exception.ResourceNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(ex.getMessage()));
        } catch (com.taskmanager.exception.AccessDeniedException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(createErrorResponse(ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to retrieve project: " + ex.getMessage()));
        }
    }

    /**
     * Update project
     */
    @PutMapping("/{projectId}")
    public ResponseEntity<?> updateProject(@PathVariable Long projectId, 
                                          @RequestBody ProjectDTO projectDTO,
                                          HttpServletRequest request) {
        try {
            Long userId = getUserId(request);
            ProjectDTO updatedProject = projectService.updateProject(projectId, projectDTO, userId);

            return ResponseEntity.ok(createSuccessResponse(updatedProject, "Project updated successfully"));
        } catch (com.taskmanager.exception.ResourceNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(ex.getMessage()));
        } catch (com.taskmanager.exception.AccessDeniedException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(createErrorResponse(ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to update project: " + ex.getMessage()));
        }
    }

    /**
     * Delete project
     */
    @DeleteMapping("/{projectId}")
    public ResponseEntity<?> deleteProject(@PathVariable Long projectId, HttpServletRequest request) {
        try {
            Long userId = getUserId(request);
            projectService.deleteProject(projectId, userId);

            return ResponseEntity.ok(createSuccessResponse(null, "Project deleted successfully"));
        } catch (com.taskmanager.exception.ResourceNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(ex.getMessage()));
        } catch (com.taskmanager.exception.AccessDeniedException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(createErrorResponse(ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to delete project: " + ex.getMessage()));
        }
    }

    /**
     * Add member to project (by user ID)
     */
    @PostMapping("/{projectId}/members")
    public ResponseEntity<?> addMember(@PathVariable Long projectId,
                                      @RequestBody Map<String, Object> requestBody,
                                      HttpServletRequest request) {
        try {
            Long userId = getUserId(request);
            ProjectMemberDTO member;

            // Support both memberId and email
            if (requestBody.containsKey("email")) {
                String email = (String) requestBody.get("email");
                if (email == null || email.trim().isEmpty()) {
                    return ResponseEntity.badRequest().body(createErrorResponse("email is required"));
                }
                member = projectService.addMemberToProjectByEmail(projectId, email, userId);
            } else {
                Long memberId = requestBody.get("memberId") != null 
                    ? ((Number) requestBody.get("memberId")).longValue() 
                    : null;
                if (memberId == null) {
                    return ResponseEntity.badRequest().body(createErrorResponse("memberId or email is required"));
                }
                member = projectService.addMemberToProject(projectId, memberId, userId);
            }

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(createSuccessResponse(member, "Member added successfully"));
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
                    .body(createErrorResponse("Failed to add member: " + ex.getMessage()));
        }
    }

    /**
     * Get project members
     */
    @GetMapping("/{projectId}/members")
    public ResponseEntity<?> getProjectMembers(@PathVariable Long projectId, HttpServletRequest request) {
        try {
            Long userId = getUserId(request);
            List<ProjectMemberDTO> members = projectService.getProjectMembers(projectId, userId);

            return ResponseEntity.ok(createSuccessResponse(members, "Members retrieved successfully"));
        } catch (com.taskmanager.exception.ResourceNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(ex.getMessage()));
        } catch (com.taskmanager.exception.AccessDeniedException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(createErrorResponse(ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to retrieve members: " + ex.getMessage()));
        }
    }

    /**
     * Remove member from project
     */
    @DeleteMapping("/{projectId}/members/{memberId}")
    public ResponseEntity<?> removeMember(@PathVariable Long projectId,
                                         @PathVariable Long memberId,
                                         HttpServletRequest request) {
        try {
            Long userId = getUserId(request);
            projectService.removeMemberFromProject(projectId, memberId, userId);

            return ResponseEntity.ok(createSuccessResponse(null, "Member removed successfully"));
        } catch (com.taskmanager.exception.ResourceNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(ex.getMessage()));
        } catch (com.taskmanager.exception.AccessDeniedException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(createErrorResponse(ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to remove member: " + ex.getMessage()));
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
