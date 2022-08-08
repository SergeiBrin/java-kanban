package ru.yandex.practicum.tasks;

import java.util.ArrayList;

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
