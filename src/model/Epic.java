package model;

public class Epic extends Task {

    public Epic() {
        super();
    }

    public Epic(int id, String taskName, Status status, String description) {
        super(id, taskName, status, description);
        setTaskType(TaskType.EPIC);
    }

    @Override
    public String toString() {
        return getId() + "," + TaskType.EPIC + "," + getTaskName() + "," + getStatus() + "," + getDescription() + ",";
    }
}
