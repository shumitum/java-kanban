package model;

import java.util.Objects;

public class Task {
    private String taskName;
    private String description;
    private Status status;
    private int id;

    public Task() {
        this.taskName = "Введите название";
        this.description = "Добавьте описание...";
        this.status = Status.NEW;

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

    @Override
    public String toString() {
        return "Task{" +
                "taskName='" + taskName + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status + '\'' +
                ", id=" + id +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(taskName, task.taskName)
                && Objects.equals(description, task.description)
                && status == task.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskName, description, status);
    }
}
