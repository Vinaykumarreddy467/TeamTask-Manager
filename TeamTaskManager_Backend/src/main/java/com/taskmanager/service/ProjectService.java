package com.taskmanager.service;

import com.taskmanager.dto.ProjectDTO;
import com.taskmanager.dto.ProjectMemberDTO;
import com.taskmanager.entity.Project;
import com.taskmanager.entity.ProjectMember;
import com.taskmanager.entity.User;
import com.taskmanager.exception.AccessDeniedException;
import com.taskmanager.exception.ResourceNotFoundException;
import com.taskmanager.repository.ProjectMemberRepository;
import com.taskmanager.repository.ProjectRepository;
import com.taskmanager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final UserRepository userRepository;

    @Autowired
    public ProjectService(ProjectRepository projectRepository,
                         ProjectMemberRepository projectMemberRepository,
                         UserRepository userRepository) {
        this.projectRepository = projectRepository;
        this.projectMemberRepository = projectMemberRepository;
        this.userRepository = userRepository;
    }

    /**
     * Create a new project (creator becomes admin)
     */
    @Transactional
    public ProjectDTO createProject(ProjectDTO projectDTO, Long adminUserId) {
        User admin = userRepository.findById(adminUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Project project = new Project();
        project.setProjectName(projectDTO.getProjectName());
        project.setDescription(projectDTO.getDescription());
        project.setAdminUser(admin);
        project.setCreatedAt(LocalDateTime.now());
        project.setUpdatedAt(LocalDateTime.now());

        Project savedProject = projectRepository.save(project);

        // Add creator as ADMIN member
        ProjectMember adminMember = new ProjectMember();
        adminMember.setProject(savedProject);
        adminMember.setUser(admin);
        adminMember.setRole(ProjectMember.Role.ADMIN);
        adminMember.setJoinedAt(LocalDateTime.now());
        projectMemberRepository.save(adminMember);

        return new ProjectDTO(savedProject);
    }

    /**
     * Get all projects for a user (both admin and member)
     */
    public List<ProjectDTO> getUserProjects(Long userId) {
        // Get projects where user is admin
        List<Project> adminProjects = projectRepository.findByAdminUserUserId(userId);
        
        // Get projects where user is member
        List<ProjectMember> memberships = projectMemberRepository.findByUserUserId(userId);
        
        // Merge and deduplicate
        java.util.Set<Long> seenIds = new java.util.HashSet<>();
        List<ProjectDTO> dtos = new ArrayList<>();
        
        for (Project project : adminProjects) {
            seenIds.add(project.getProjectId());
            dtos.add(new ProjectDTO(project));
        }
        for (ProjectMember member : memberships) {
            Project project = member.getProject();
            if (!seenIds.contains(project.getProjectId())) {
                seenIds.add(project.getProjectId());
                dtos.add(new ProjectDTO(project));
            }
        }
        
        return dtos;
    }

    /**
     * Get project by ID (verify user has access)
     */
    public ProjectDTO getProjectById(Long projectId, Long userId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        // Verify user has access
        boolean isAdmin = project.getAdminUser().getUserId().equals(userId);
        boolean isMember = projectMemberRepository
                .existsByProjectProjectIdAndUserUserId(projectId, userId);

        if (!isAdmin && !isMember) {
            throw new AccessDeniedException("You don't have access to this project");
        }

        return new ProjectDTO(project);
    }

    /**
     * Update project details (admin only)
     */
    @Transactional
    public ProjectDTO updateProject(Long projectId, ProjectDTO projectDTO, Long userId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        if (!project.getAdminUser().getUserId().equals(userId)) {
            throw new AccessDeniedException("Only project admin can update this project");
        }

        project.setProjectName(projectDTO.getProjectName());
        project.setDescription(projectDTO.getDescription());
        project.setUpdatedAt(LocalDateTime.now());

        Project updatedProject = projectRepository.save(project);
        return new ProjectDTO(updatedProject);
    }

    /**
     * Delete project (admin only)
     */
    @Transactional
    public void deleteProject(Long projectId, Long userId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        if (!project.getAdminUser().getUserId().equals(userId)) {
            throw new AccessDeniedException("Only project admin can delete this project");
        }

        projectRepository.delete(project);
    }

    /**
     * Add member to project by user ID (admin only)
     */
    @Transactional
    public ProjectMemberDTO addMemberToProject(Long projectId, Long memberId, Long adminUserId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        // Verify requester is admin
        if (!project.getAdminUser().getUserId().equals(adminUserId)) {
            throw new AccessDeniedException("Only project admin can add members");
        }

        User newMember = userRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Check if already member
        boolean alreadyMember = projectMemberRepository
                .existsByProjectProjectIdAndUserUserId(projectId, memberId);

        if (alreadyMember) {
            throw new IllegalArgumentException("User is already a member of this project");
        }

        ProjectMember member = new ProjectMember();
        member.setProject(project);
        member.setUser(newMember);
        member.setRole(ProjectMember.Role.MEMBER);
        member.setJoinedAt(LocalDateTime.now());
        ProjectMember savedMember = projectMemberRepository.save(member);

        return new ProjectMemberDTO(savedMember);
    }

    /**
     * Add member to project by email (admin only)
     */
    @Transactional
    public ProjectMemberDTO addMemberToProjectByEmail(Long projectId, String email, Long adminUserId) {
        User newMember = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        return addMemberToProject(projectId, newMember.getUserId(), adminUserId);
    }

    /**
     * Remove member from project (admin only)
     */
    @Transactional
    public void removeMemberFromProject(Long projectId, Long memberId, Long adminUserId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        if (!project.getAdminUser().getUserId().equals(adminUserId)) {
            throw new AccessDeniedException("Only project admin can remove members");
        }

        ProjectMember member = projectMemberRepository
                .findByProjectProjectIdAndUserUserId(projectId, memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found in project"));

        projectMemberRepository.delete(member);
    }

    /**
     * Get all members of a project
     */
    public List<ProjectMemberDTO> getProjectMembers(Long projectId, Long userId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        // Verify user has access
        boolean isAdmin = project.getAdminUser().getUserId().equals(userId);
        boolean isMember = projectMemberRepository
                .existsByProjectProjectIdAndUserUserId(projectId, userId);

        if (!isAdmin && !isMember) {
            throw new AccessDeniedException("You don't have access to this project");
        }

        List<ProjectMember> members = projectMemberRepository.findByProjectProjectId(projectId);
        
        List<ProjectMemberDTO> dtos = new ArrayList<>();
        for (ProjectMember member : members) {
            dtos.add(new ProjectMemberDTO(member));
        }
        return dtos;
    }

    /**
     * Verify if user is project admin
     */
    public boolean isProjectAdmin(Long projectId, Long userId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        return project.getAdminUser().getUserId().equals(userId);
    }

    /**
     * Verify if user is project member
     */
    public boolean isProjectMember(Long projectId, Long userId) {
        return projectMemberRepository.existsByProjectProjectIdAndUserUserId(projectId, userId);
    }
}
