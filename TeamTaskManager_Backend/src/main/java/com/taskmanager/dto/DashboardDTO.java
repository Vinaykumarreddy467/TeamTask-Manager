package com.taskmanager.dto;

import java.util.List;
import java.util.Map;

public class DashboardDTO {
    private long totalTasks;
    private long toDoCount;
    private long inProgressCount;
    private long doneCount;
    private long overdueCount;
    private Map<String, Long> tasksPerUser;
    private List<TaskDTO> overdueTasks;

    // Constructors
    public DashboardDTO() {
    }

    // Getters and Setters
    public long getTotalTasks() {
        return totalTasks;
    }

    public void setTotalTasks(long totalTasks) {
        this.totalTasks = totalTasks;
    }

    public long getToDoCount() {
        return toDoCount;
    }

    public void setToDoCount(long toDoCount) {
        this.toDoCount = toDoCount;
    }

    public long getInProgressCount() {
        return inProgressCount;
    }

    public void setInProgressCount(long inProgressCount) {
        this.inProgressCount = inProgressCount;
    }

    public long getDoneCount() {
        return doneCount;
    }

    public void setDoneCount(long doneCount) {
        this.doneCount = doneCount;
    }

    public long getOverdueCount() {
        return overdueCount;
    }

    public void setOverdueCount(long overdueCount) {
        this.overdueCount = overdueCount;
    }

    public Map<String, Long> getTasksPerUser() {
        return tasksPerUser;
    }

    public void setTasksPerUser(Map<String, Long> tasksPerUser) {
        this.tasksPerUser = tasksPerUser;
    }

    public List<TaskDTO> getOverdueTasks() {
        return overdueTasks;
    }

    public void setOverdueTasks(List<TaskDTO> overdueTasks) {
        this.overdueTasks = overdueTasks;
    }
}
