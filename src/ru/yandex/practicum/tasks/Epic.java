package ru.yandex.practicum.tasks;

import ru.yandex.practicum.tasks.enums.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {
    private final ArrayList<Integer> subtasksIdForEpic;
    private LocalDateTime endTime;

    public Epic(String taskName, String taskDescription, TaskStatus status, LocalDateTime startTime, Duration duration) {
        super(taskName, taskDescription, status, startTime, duration);

        // New!
        // Если эпику при создании назначены startTime и duration – посчитать его endTime.
        if (this.getClassName().equals("Epic") && startTime != null && duration != null) {
            endTime = startTime.plus(duration);
        }
        subtasksIdForEpic = new ArrayList<>();
    }

    public ArrayList<Integer> getSubtaskIdForEpic() {
        return subtasksIdForEpic;
    }

    public void setSubtasksIdForEpic(int subtasksId) {
        this.subtasksIdForEpic.add(subtasksId);
    }

    public void removeStIdForEpic(Integer removeSubtaskId) {
        subtasksIdForEpic.remove(removeSubtaskId);
    }

    public void clearStIdForEpic() {
        subtasksIdForEpic.clear();
    }

    // new!
    // Если Subtask, то возвращаем метод getEndTime из Task,
    // иначе возвращаем endTime из класса Epic
    @Override
    public LocalDateTime getEndTime() {
        if (this.getClassName().equals("Subtask")) {
            return super.getEndTime();
        }
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    // New!
    // Переопределил из Task setStartTime() и setDuration(), чтобы изменялся
    // endTime у Эпика при изменении его значений. Но, меняться значения будут,
    // только если у Эпика нет подзадач.
    @Override
    public void setStartTime(LocalDateTime startTime) {
        // Проход для Subtask
        if (this.getClassName().equals("Subtask")) {
            super.setStartTime(startTime);
        }

        // Алгоритм для Эпика
        if (this.getClassName().equals("Epic")) {
            super.setStartTime(startTime);
            if (this.subtasksIdForEpic.isEmpty()) {
                if (this.startTime != null) {
                    endTime = this.startTime.plus(this.duration);
                } else {
                    endTime = null;
                }
            }
        }
    }

    @Override
    public void setDuration(Duration duration) {
        if (this.getClassName().equals("Subtask")) {
            super.setDuration(duration);
        }

        if (this.getClassName().equals("Epic") && this.subtasksIdForEpic.isEmpty() && this.startTime != null) {
            super.setDuration(duration);
            endTime = this.startTime.plus(this.duration);
        } else {
            endTime = null;
        }
    }

    @Override
    public String getClassName() {
        return "Epic";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subtasksIdForEpic, epic.subtasksIdForEpic) && Objects.equals(endTime, epic.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtasksIdForEpic, endTime);
    }

    @Override
    public String toString() {
        return "Tasks.Epic{" +
                "id=" + getId() +
                ", status='" + getStatus() + '\'' +
                ", taskName='" + taskName + '\'' +
                ", taskDescription='" + taskDescription + '\'' +
                ", startTime=[" + startTime + "]" +
                ", duration=[" + duration + "]" +
                ", endTime=[" + this.getEndTime() + "]" +
                ", subTaskIdForEpic=" + subtasksIdForEpic +
                '}';
    }
}
