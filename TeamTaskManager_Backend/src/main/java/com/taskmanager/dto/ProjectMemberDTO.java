package com.taskmanager.dto;

import com.taskmanager.entity.ProjectMember;
import java.time.LocalDateTime;

public class ProjectMemberDTO {
    private Long memberId;
    private Long projectId;
    private Long userId;
    private String userName;
    private String email;
    private String role;
    private LocalDateTime joinedAt;

    // Constructors
    public ProjectMemberDTO() {
    }

    public ProjectMemberDTO(ProjectMember member) {
        this.memberId = member.getMemberId();
        this.projectId = member.getProject().getProjectId();
        this.userId = member.getUser().getUserId();
        this.userName = member.getUser().getName();
        this.email = member.getUser().getEmail();
        this.role = member.getRole().toString();
        this.joinedAt = member.getJoinedAt();
    }

    // Getters and Setters
    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(LocalDateTime joinedAt) {
        this.joinedAt = joinedAt;
    }
}
