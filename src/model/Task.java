package model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    private String taskName;
    private String description;
    private Status status;
    private int id;
    private TaskType taskType;
    private LocalDateTime startTime;
    private int duration;

    public Task() {
        this.taskType = TaskType.TASK;
        this.taskName = "Введите название";
        this.status = Status.NEW;
        this.description = "Добавьте описание...";
        this.duration = 0;
    }

    public Task(int id, String taskName, Status status, String description, LocalDateTime startTime, int duration) {
        this.id = id;
        this.taskName = taskName;
        this.status = status;
        this.description = description;
        this.taskType = TaskType.TASK;
        this.startTime = startTime;
        this.duration = duration;
    }

    //DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");

    public String getTaskName() {
        return taskName;
    }

    public String getDescription() {
        return description;
    }

    public Status getStatus() {
        return status;
    }

    public int getId() {
        return id;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setTaskType(TaskType taskType) {
        this.taskType = taskType;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public int getDuration() {
        return duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        if (Objects.equals(startTime, null) || duration <= 0) {
            return null;
        }
        return startTime.plusMinutes(duration);
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return id + ","
                + taskType + ","
                + taskName + ","
                + status + ","
                + description + ","
                + startTime + ","
                + duration + ","
                + getEndTime();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && duration == task.duration
                && Objects.equals(taskName, task.taskName)
                && Objects.equals(description, task.description)
                && status == task.status
                && taskType == task.taskType
                && Objects.equals(startTime, task.startTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskName, description, status, id, taskType, startTime, duration);
    }
}