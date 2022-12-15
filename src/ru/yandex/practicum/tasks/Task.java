package ru.yandex.practicum.tasks;

import ru.yandex.practicum.tasks.enums.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task implements Comparable<Task> {

    protected int id;
    protected String taskName;
    protected String taskDescription;
    protected TaskStatus status;
    protected LocalDateTime startTime;
    protected Duration duration = Duration.ZERO;

    public Task(String taskName, String taskDescription, TaskStatus status, LocalDateTime startTime, Duration duration) {
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.status = status;
        setTimeAndDuration(startTime, duration);
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

    public LocalDateTime getEndTime() {
//        if (startTime == null || duration == null) {
//            return null;
//        }
        if (startTime == null) {
            return null;
        } else if (duration == null) {
            duration = Duration.ZERO;
        }
        return startTime.plus(duration);
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    // Если передаем null-startTime, то обнуляем Duration
    public void setStartTime(LocalDateTime startTime) {
        if (startTime == null) {
            this.startTime = null;
            this.duration = Duration.ZERO;
        } else {
            this.startTime = startTime;
        }
    }

    public Duration getDuration() {
        return duration;
    }

    // New! Если передаем duration null, то делаем его Duration.Zero.
    public void setDuration(Duration duration) {
        if (this.startTime == null || duration == null) {
            this.duration = Duration.ZERO;
        } else {
            this.duration = duration;
        }
    }

    public String getClassName() {
        return "Task";
    }

    // New!
    // Если введенное, или загруженное из FileBackedTasksManager.loadFromFile время
    // задачи startTime или endTime уже в прошлом, то метод скажет об этом (это просто инфо).
    // startTime.plusSeconds(10) и endTime.plusSeconds(10) из-за того, что от момента создания задачи,
    // до этого момента в коде проходит некоторое количество времени время.
    private void setTimeAndDuration(LocalDateTime startTime, Duration duration) {
        if (startTime == null) {
            System.out.println("startTime для этой задачи: «" + this.taskName + "» не назначен.");
            return;
        }

        if (duration == null) {
            duration = Duration.ZERO;
        }

        LocalDateTime now = LocalDateTime.now();

        if (startTime.plusSeconds(10).isBefore(now)) {
            System.out.println("StartTime для этой задачи: " + this.taskName + " прошёл.");
        }

        this.startTime = startTime;
        this.duration = duration;
        LocalDateTime endTime = this.startTime.plus(this.duration);

        if (endTime.plusSeconds(10).isBefore(now)) {
            System.out.println("EndTime для этой задачи: " + this.taskName + " прошёл.");
        }
    }

    // new! Метод сортировки. Задачи с null временем тоже должны сортироваться.
    @Override
    public int compareTo(Task o) {
        if (this.startTime == null) {
            return 1;
        } else if (o.startTime == null) {
            return -1;
        } else if (this.equals(o)) {
            return 0;
        } else if (this.startTime.isBefore(o.startTime)) {
            return -1;
        }
        return 1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && Objects.equals(taskName, task.taskName) && Objects.equals(taskDescription, task.taskDescription) && status == task.status && Objects.equals(startTime, task.startTime) && Objects.equals(duration, task.duration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskName, taskDescription, status, id, startTime, duration);
    }

    @Override
    public String toString() {
        return "Tasks.Task{" +
                "id=" + id +
                ", status='" + status + '\'' +
                ", taskName='" + taskName + '\'' +
                ", taskDescription='" + taskDescription + '\'' + '}' +
                ", startTime=[" + startTime + "]" +
                ", duration=[" + duration + "]" +
                ", endTime=[" + getEndTime() + "]" +
                '}';
    }
}


