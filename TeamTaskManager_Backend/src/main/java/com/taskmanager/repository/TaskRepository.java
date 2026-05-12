package com.taskmanager.repository;

import com.taskmanager.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByProjectProjectId(Long projectId);
    List<Task> findByAssignedToUserId(Long userId);
    
    @Query("SELECT t FROM Task t WHERE t.dueDate < :today AND t.status != 'DONE'")
    List<Task> findOverdueTasks(@Param("today") LocalDate today);
    
    @Query("SELECT COUNT(t) FROM Task t WHERE t.project.projectId = :projectId")
    long countByProjectId(@Param("projectId") Long projectId);
    
    @Query("SELECT COUNT(t) FROM Task t WHERE t.assignedTo.userId = :userId")
    long countByAssignedUserId(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(t) FROM Task t WHERE t.project.projectId = :projectId AND t.status = 'TO_DO'")
    long countTodoByProjectId(@Param("projectId") Long projectId);
    
    @Query("SELECT COUNT(t) FROM Task t WHERE t.project.projectId = :projectId AND t.status = 'IN_PROGRESS'")
    long countInProgressByProjectId(@Param("projectId") Long projectId);
    
    @Query("SELECT COUNT(t) FROM Task t WHERE t.project.projectId = :projectId AND t.status = 'DONE'")
    long countDoneByProjectId(@Param("projectId") Long projectId);
}
