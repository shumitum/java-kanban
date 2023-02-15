package model;

import java.time.LocalDateTime;

public class Epic extends Task {
    private LocalDateTime endTime;

    public Epic() {
        super();
        setTaskType(TaskType.EPIC);
    }

    public Epic(int id, String taskName, Status status, String description, LocalDateTime startTime, int duration) {
        super(id, taskName, status, description, startTime, duration);
        setTaskType(TaskType.EPIC);
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return getId() + ","
                + TaskType.EPIC + ","
                + getTaskName() + ","
                + getStatus() + ","
                + getDescription() + ","
                + timeReformat(getStartTime()) + ","
                + getDuration() + ","
                + timeReformat(getEndTime());
    }
}
