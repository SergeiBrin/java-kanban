package ru.yandex.practicum.tasks;

import ru.yandex.practicum.tasks.enums.TaskStatus;

import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {
    private final ArrayList<Integer> subtasksIdForEpic;

    public Epic(String taskName, String taskDescription, TaskStatus status) {
        super(taskName, taskDescription, status);
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
        return Objects.equals(subtasksIdForEpic, epic.subtasksIdForEpic);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtasksIdForEpic);
    }

    @Override
    public String toString() {
        return "Tasks.Epic{" +
                "id=" + getId() +
                ", status='" + getStatus() + '\'' +
                ", taskName='" + taskName + '\'' +
                ", taskDescription='" + taskDescription + '\'' +
                ", subTaskIdForEpic=" + subtasksIdForEpic +
                '}';
    }
}
