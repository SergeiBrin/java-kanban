package ru.yandex.practicum.tasks;

import java.util.ArrayList;
import java.util.List;

public class TaskCsvFormatter {
    public static String getFileHeader() { // Метод возвращает заголовок для scv файла.
        return "id,type,name,status,description,epic";
    }

    public static String toString(Task task) { // Метод сохранения задачи в строку
        // Применил СтрингФормат, потому что все переменные известны.
        // В таком виде выглядит красиво.
        switch (task.getClassName()) {
            case "Subtask":
                return String.format("%s,%s,%s,%s,%s,%s", task.getId(), TaskType.SUBTASK, task.getTaskName(),
                        task.getStatus(), task.getTaskDescription(), ((Subtask) task).getEpicIdForSubtask());
            case "Epic":
                return String.format("%s,%s,%s,%s,%s", task.getId(), TaskType.EPIC, task.getTaskName(),
                        task.getStatus(), task.getTaskDescription());
            case "Task":
                return String.format("%s,%s,%s,%s,%s", task.getId(), TaskType.TASK, task.getTaskName(),
                        task.getStatus(), task.getTaskDescription());
            default:
                return "";
        }
    }

    public static Task fromString(String value) { // Метод получения задачи из строки.
        Task task = null;
        String[] values = value.split(",");

        // Переменные для будущей инициализации и добавления в Task.
        int id = 0;
        int epicId = 0;
        String type = "";
        String name = "";
        String description = "";
        TaskStatus status = TaskStatus.NEW;

        // Инициализация переменных.
        for (int i = 0; i < values.length; i++) {
            switch (i) {
                case 0:
                    id = Integer.parseInt(values[i]);
                    break;
                case 1:
                    type = values[i];
                    break;
                case 2:
                    name = values[i];
                    break;
                case 3:
                    if (values[i].equals("IN_PROGRESS")) {
                        status = TaskStatus.IN_PROGRESS;
                    } else if (value.equals("DONE")) {
                        status = TaskStatus.DONE;
                    }
                    break;
                case 4:
                    description = values[i];
                    break;
                case 5:
                    epicId = Integer.parseInt(values[i]);
                    break;
            }
        }

        // Воссоздание Task из переменных.
        switch (type) {
            case "TASK":
                task = new Task(name, description, status);
                task.setId(id);
                break;
            case "EPIC":
                task = new Epic(name, description, status);
                task.setId(id);
                break;
            case "SUBTASK":
                Subtask subtask = new Subtask(name, description, status);
                subtask.setId(id);
                subtask.setEpicIdForSubtask(epicId);
                task = subtask;
                break;
        }
        return task;
    }

    public static String historyToString(List<Task> manager) { // Сохранение истории.
        // Использовал Стрингбилдер, потому что тут подходит поочередное добавление в строку.
        StringBuilder builder = new StringBuilder();
        int count = 1;

        for (Task task : manager) {
            if (count == manager.size()) {
                builder.append(task.getId());
            } else {
                builder.append(task.getId()).append(",");
            }
            count++;
        }
        return builder.toString();
    }

    public static List<Integer> historyFromString(String value) { // Восстановление истории.
        List<Integer> idList = new ArrayList<>();
        String[] idHistory = value.split(",");

        for (String id : idHistory) {
            idList.add(Integer.parseInt(id));
        }
        return idList;
    }
}
