package ru.yandex.practicum.tasks;

import ru.yandex.practicum.tasks.enums.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Subtask extends Epic {
    private int epicIdForSubtask;

    public Subtask(String taskName, String taskDescription, TaskStatus status, LocalDateTime startTime, Duration duration) {
        super(taskName, taskDescription, status, startTime, duration);
    }

    public int getEpicIdForSubtask() {
        return epicIdForSubtask;
    }

    public void setEpicIdForSubtask(int epicId) {
        this.epicIdForSubtask = epicId;
    }

    @Override
    public String getClassName() {
        return "Subtask";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Subtask subtask = (Subtask) o;
        return epicIdForSubtask == subtask.epicIdForSubtask;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicIdForSubtask);
    }

    @Override
    public String toString() {
        return "Tasks.Subtask{" +
                "id=" + getId() +
                ", status='" + getStatus() + '\'' +
                ", taskName='" + taskName + '\'' +
                ", taskDescription='" + taskDescription + '\'' +
                ", startTime=[" + startTime + "]" +
                ", duration=[" + duration + "]" +
                ", endTime=[" + getEndTime() + "]" +
                ", epicIdForSubtask=[" + epicIdForSubtask + "]" +
                '}';
    }
}
