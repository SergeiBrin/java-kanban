package ru.yandex.practicum.tasks;

import ru.yandex.practicum.tasks.enums.TaskStatus;

import java.util.Objects;

public class Task {
    protected String taskName;
    protected String taskDescription;
    protected TaskStatus status;
    protected int id;

    public Task(String taskName, String taskDescription, TaskStatus status) {
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.status = status;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getClassName() {
        return "Task";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && Objects.equals(taskName, task.taskName) && Objects.equals(taskDescription, task.taskDescription) && status == task.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskName, taskDescription, status, id);
    }

    @Override
    public String toString() {
        return "Tasks.Task{" +
                "id=" + id +
                ", status='" + status + '\'' +
                ", taskName='" + taskName + '\'' +
                ", taskDescription='" + taskDescription + '\'' +
                '}';
    }
}


