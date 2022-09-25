package ru.yandex.practicum.managers;

import ru.yandex.practicum.exception.ManagerSaveException;
import ru.yandex.practicum.tasks.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private final Path path;

    public FileBackedTasksManager(Path path) {
        this.path = path;
    }

    // main для проверки работы менеджера
    public static void main(String[] args) {
        Task task = new Task("Переехать", "Денег болт поэтому поеду в Чехию цыганом", TaskStatus.NEW);
        Task task1 = new Task("Купить машину", "Хотя бы Жигули", TaskStatus.NEW);

        Epic epic = new Epic("Найти время чтоб отдохнуть", "Совсем ничего не делать", TaskStatus.NEW);
        Subtask subtask = new Subtask("Лечь на кровать", "Аккуратно чтоб не прихватило спину", TaskStatus.NEW);
        Subtask subtask1 = new Subtask("Уснуть", "Закрыть глазки и шёпотом считать овечек", TaskStatus.NEW);

        Epic epic1 = new Epic("Проснуться с утра ", "Будильник включится в 6:00", TaskStatus.NEW);
        Subtask subtask2 = new Subtask("Вставить спички в глаза", "По три штуки на глаз", TaskStatus.NEW);

        Path of = Path.of("resources/backed_file.csv");

        // Этап 1: сохранение в csv файл.
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(of);

        fileBackedTasksManager.createTask(task);
        fileBackedTasksManager.createEpic(epic);
        fileBackedTasksManager.createSubtask(epic, subtask);
        fileBackedTasksManager.createSubtask(epic, subtask1);

        fileBackedTasksManager.getEpicById(2);
        fileBackedTasksManager.getSubtaskById(4);

        // fileBackedTasksManager.deleteSubtaskById(4);
        // fileBackedTasksManager.deleteEpicById(2);

        fileBackedTasksManager.printTaskList();
        fileBackedTasksManager.printEpicList();
        fileBackedTasksManager.printSubtaskList();

        System.out.println();

        List<Task> lists = fileBackedTasksManager.getHistory();
        for (Task list : lists) {
            System.out.println(list);
        }



        /* Этап 2: загрузка из csv файла.
        FileBackedTasksManager newFileBackedTasksManager = FileBackedTasksManager.loadFromFile(of);

        newFileBackedTasksManager.printTaskList();
        newFileBackedTasksManager.printEpicList();
        newFileBackedTasksManager.printSubtaskList();

        System.out.println();

        List<Task> lists = newFileBackedTasksManager.getHistory();
        for (Task list : lists) {
            System.out.println(list);
        }
        */
    }

    public void save() {
        // Собираю информацию о всех типах задач.
        List<Task> tasks = getTasksList();
        List<Epic> epics = getEpicsList();
        List<Subtask> subtasks = getSubTasksList();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path.toFile()))) {
            // Если все пусто, то return.
            boolean isNoTasks = (tasks.size() == 0) && (epics.size() == 0) && (subtasks.size() == 0);
            if (isNoTasks) {
                return;
            }

            // Добавляю Хедер в файл
            String line = TaskCsvFormatter.getFileHeader();
            writer.write(line + "\n");
            writer.flush();

            // А потом по очереди все задачи и историю – всё, что есть.
            for (Task task : tasks) {
                line = TaskCsvFormatter.toString(task);
                writer.write(line + "\n");
                writer.flush();
            }

            for (Task epic : epics) {
                line = TaskCsvFormatter.toString(epic);
                writer.write(line + "\n");
                writer.flush();
            }

            for (Task subtask : subtasks) {
                line = TaskCsvFormatter.toString(subtask);
                writer.write(line + "\n");
                writer.flush();
            }

            // Пробел
            writer.write("\n");
            writer.flush();

            // Добавяю id истории файл
            String historyId = TaskCsvFormatter.historyToString(getHistory());
            writer.write(historyId);
            writer.flush();
        } catch (IOException e) {
            throw new ManagerSaveException();
        }
    }

    public static FileBackedTasksManager loadFromFile(Path path) {
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(path);
        int taskId = 0;
        String csvFile = "";

        // Чтение из файла.
        try {
            csvFile = Files.readString(path);
        } catch (IOException e) {
            System.out.println("Невозможно прочитать файлы с отчётом. Возможно, файл не находится в нужной директории.");
        }

        // Если пусто, то дальше идти нет смысла.
        if (csvFile.isEmpty()) {
        return fileBackedTasksManager;
        }

        // Режем по \n и проходимся по массиву со второй строки, так как 1 строка - хедер.
        String[] lines = csvFile.split("\n");

        for (int i = 1; i < lines.length; i++) {
            if (lines[0].isEmpty()) { // Возможно это лишнее.
                break;
            }

            // Пока строка не пустая, это линии задач. Переводим строку в Task и ложим в соответсвующий Map.
            if (!(lines[i].isEmpty())) {
                Task task = TaskCsvFormatter.fromString(lines[i]);

                // Ищем самый большой Id и позже положим его в taskId.
                if (taskId < task.getId()) {
                    taskId = task.getId();
                }

                switch (task.getClassName()) {
                    case "Task":
                        fileBackedTasksManager.setTasksMap(task);
                        break;
                    case "Epic":
                        fileBackedTasksManager.setEpicsMap(task);
                        break;
                    case "Subtask":
                        fileBackedTasksManager.setSubtasksMap(task);

                        // Алгоритм действий для связи id подзадач c эпиком.
                        Map<Integer, Epic> epics = fileBackedTasksManager.getEpics();
                        Subtask subtask = (Subtask) task;
                        int subtaskId = subtask.getId();
                        int epicIdForSubtask = subtask.getEpicIdForSubtask();

                        if (epics.containsKey(epicIdForSubtask)) {
                            epics.get(epicIdForSubtask).setSubtasksIdForEpic(subtaskId);
                        }
                        break;
                }
            } else {
                // Получаем id истории. Находим, к какому типу задачи относятся эти id
                // и отправляем эти id d соответствующие методы get...Id() для дальнейшей обработки.
                List<Integer> idHistories = TaskCsvFormatter.historyFromString(lines[++i]);

                for (Integer idHistory : idHistories) {
                    Map<Integer, Task> tasks = fileBackedTasksManager.getTasks();
                    Map<Integer, Epic> epics = fileBackedTasksManager.getEpics();
                    Map<Integer, Subtask> subtask = fileBackedTasksManager.getSubTasks();

                    if (tasks.containsKey(idHistory)) {
                        fileBackedTasksManager.getTaskById(idHistory);
                    } else if (epics.containsKey(idHistory)) {
                        fileBackedTasksManager.getEpicById(idHistory);
                    } else if (subtask.containsKey(idHistory)) {
                        fileBackedTasksManager.getSubtaskById(idHistory);
                    }
                }
            }
        }
        fileBackedTasksManager.setTaskId(taskId);

        return fileBackedTasksManager;
    }

    // Переопределил только те методы, где есть добавленный функционал.
    @Override
    public Integer createTask(Task task) {
        int taskId = super.createTask(task);
        save();
        return taskId;
    }

    @Override
    public Integer createEpic(Epic epic) {
        int epicId = super.createEpic(epic);
        save();
        return epicId;
    }

    @Override
    public Integer createSubtask(Epic epic, Subtask subtask) {
        int subtaskId = super.createSubtask(epic, subtask);
        save();
        return subtaskId;
    }

    @Override
    public void clearAllTasks() {
        super.clearAllTasks();
        save();
    }

    @Override
    public void clearTasks() {
        super.clearTasks();
        save();
    }

    @Override
    public void clearEpics() {
        super.clearEpics();
        save();
    }

    @Override
    public void clearSubtasks() {
        super.clearSubtasks();
        save();
    }

    @Override
    public void deleteTaskById(int removeTask) {
        super.deleteTaskById(removeTask);
        save();
    }

    @Override
    public void deleteEpicById(int removeEpic) {
        super.deleteEpicById(removeEpic);
        save();
    }

    @Override
    public void deleteSubtaskById(int removeSubtask) {
        super.deleteSubtaskById(removeSubtask);
        save();
    }

    @Override
    public Task getTaskById(int idTask) {
        Task task = super.getTaskById(idTask);
        save();
        return task;
    }

    @Override
    public Epic getEpicById(int idEpic) {
        Epic epic = super.getEpicById(idEpic);
        save();
        return epic;
    }

    @Override
    public Subtask getSubtaskById(int idSubtask) {
        Subtask subtask = super.getSubtaskById(idSubtask);
        save();
        return subtask;
    }

    @Override
    public void updateTask(Task newTask) {
        super.updateTask(newTask);
        save();
    }

    @Override
    public void updateEpic(Epic newEpic) {
        super.updateEpic(newEpic);
        save();
    }

    @Override
    public void updateSubtask(Subtask newSubtask) {
        super.updateSubtask(newSubtask);
        save();
    }
}
