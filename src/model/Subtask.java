package model;

public class Subtask extends Task {
    private int EpicId;

    public Subtask() {
        super();
    }

    public Subtask(int id, String taskName, Status status, String description, int EpicId) {
        super(id, taskName, status, description);
        setTaskType(TaskType.SUBTASK);
        this.EpicId = EpicId;
    }

    public int getEpicId() {
        return EpicId;
    }

    public void setEpicId(int epicId) {
        EpicId = epicId;
    }

    @Override
    public String toString() {
        return getId() + "," + TaskType.SUBTASK + "," + getTaskName() + "," + getStatus() + "," + getDescription() + ","
                + EpicId;
    }
}