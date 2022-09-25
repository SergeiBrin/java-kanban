package ru.yandex.practicum.tasks;

public class Subtask extends Epic {
    private int epicIdForSubtask;

    public Subtask(String taskName, String taskDescription, TaskStatus status) {
        super(taskName, taskDescription, status);
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
    public String toString() {
        return "Tasks.Subtask{" +
                "id=" + getId() +
                ", status='" + getStatus() + '\'' +
                ", taskName='" + taskName + '\'' +
                ", taskDescription='" + taskDescription + '\'' +
                ", epicIdForSubtask=[" + epicIdForSubtask +"]" +
                '}';
    }
}
