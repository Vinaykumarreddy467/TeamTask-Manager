package com.taskmanager.repository;

import com.taskmanager.entity.ProjectMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {
    Optional<ProjectMember> findByProjectProjectIdAndUserUserId(Long projectId, Long userId);
    boolean existsByProjectProjectIdAndUserUserId(Long projectId, Long userId);
    List<ProjectMember> findByProjectProjectId(Long projectId);
    List<ProjectMember> findByUserUserId(Long userId);
    void deleteByProjectProjectIdAndUserUserId(Long projectId, Long userId);
}
