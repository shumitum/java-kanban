package model;

import java.time.LocalDateTime;

public class Subtask extends Task {
    private int EpicId;

    public Subtask() {
        super();
        setTaskType(TaskType.SUBTASK);
    }

    public Subtask(int id, String taskName, Status status, String description, LocalDateTime startTime, int duration, int EpicId) {
        super(id, taskName, status, description, startTime, duration);
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
        return getId() + ","
                + TaskType.SUBTASK + ","
                + getTaskName() + ","
                + getStatus() + ","
                + getDescription() + ","
                + timeReformat(getStartTime()) + ","
                + getDuration() + ","
                + timeReformat(getEndTime()) + ","
                + EpicId;
    }
}