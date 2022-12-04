package model;

public class Epic extends Task{

    public Epic () {
        super();
    }

    @Override
    public String toString() {
        return "EPIC{" +
                "epicName='" + getTaskName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", id=" + getId() +
                '}';
    }
}
