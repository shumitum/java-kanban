package model;

public class Subtask extends Task{

    public Subtask() {
        super();
    }

    @Override
    public String toString() {
        return "SUBTASK{" +
                "subtaskName='" + getTaskName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", id=" + getId() +
                '}';
    }
}
