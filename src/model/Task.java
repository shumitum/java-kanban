package model;

import java.util.Objects;

public class Task {
    private String taskName;
    private String description;
    private Status status;
    private int id;
    private TaskType taskType;

    public Task() {
        this.taskName = "Введите название";
        this.description = "Добавьте описание...";
        this.status = Status.NEW;
        this.taskType = TaskType.TASK;
    }

    public Task(int id, String taskName, Status status, String description) {
        this.id = id;
        this.taskName = taskName;
        this.status = status;
        this.description = description;
        this.taskType = TaskType.TASK;

    }

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

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setTaskType(TaskType taskType) {
        this.taskType = taskType;
    }

    @Override
    public String toString() {
        return id + "," + taskType + "," + taskName + "," + status + "," + description + ",";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && taskName.equals(task.taskName) && description.equals(task.description) && status == task.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskName, description, status, id);
    }
}