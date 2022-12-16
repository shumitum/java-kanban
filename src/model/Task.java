package model;

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

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Task{" +
                "taskName='" + taskName + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status + '\'' +
                ", id=" + id +
                '}' + "\n";
    }
}