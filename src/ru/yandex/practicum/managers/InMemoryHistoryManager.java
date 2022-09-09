package ru.yandex.practicum.managers;

import ru.yandex.practicum.node.Node;
import ru.yandex.practicum.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node<Task>> historyManager = new HashMap<>(); // Map для истории задач.
    private final CustomLinkedList customLinkedList = new CustomLinkedList(); // Кастомный двусвязный список задач.

    @Override
    public void add(Task task) {
        customLinkedList.removeNode(historyManager.get(task.getId())); // Метод пересвязывает ссылками Node-узлы и удаляет задачу из historyManager.
        customLinkedList.linkLast(task); // Метод, который пересвязывает ссылками Node-узлы и добавляет просмотренную задачу в конец истории.
    }

    @Override
    public List<Task> getHistory() { // Метод, который возвращает лист истории просмотренных задач.
        return customLinkedList.getTask();
    }

    @Override
    public void remove(int id) { // Если задачу удалили из taskManager, то метод удалит эту задачу из истории просмотров.
        customLinkedList.removeNode(historyManager.get(id)); // Метод пересвязывает ссылками Node-узлы и удаляет задачу из historyManager.
    }

    private class CustomLinkedList {
        private Node<Task> head;
        private Node<Task> tail;
        private int size = 0;

        public void removeNode(Node<Task> node) {
            if (node == null) { // Если такого значения нет, то выход из метода.
                return;
            }

            int id = node.getData().getId();

            if (size == 1) { // Если размер списка 1, значит в нем есть только head.
                head = null;
                historyManager.remove(id); // Удаляем задачу из истории.
            } else if (size == 2) { // Если размер списка 2, значит в нем есть 2 значения - head и tail. Уточняем, пересвязываем и удаляем.
                if (head == node) {
                    head = node.getNext();
                    tail = null;
                    historyManager.remove(id);
                } else {
                    head.setNext(null);
                    ;
                    tail = null;
                    historyManager.remove(id);
                }
            } else { // Если размер списка > 2, значит в нем есть head, другие узлы и tail. Уточняем, пересвязываем и удаляем.
                if (head == node) {
                    head = node.getNext();
                    head.setPrev(null);
                    historyManager.remove(id);
                } else if (tail == node) {
                    tail = node.getPrev();
                    tail.setNext(null);
                    historyManager.remove(id);
                } else {
                    node.getPrev().setNext(node.getNext());
                    node.getNext().setPrev(node.getPrev());
                    historyManager.remove(id);
                }
            }
            size--; // Уменьшаем размер двусвязного списка.
        }

        public void linkLast(Task task) {
            if (size == 0) { // // Если размер списка 0, значит первым элементом будет head.
                head = new Node<>(task, null, null);
                historyManager.put(task.getId(), head); // Кладем задачу в историю.
            } else if (size == 1) { // Если размер списка 1, значит head уже есть, и новым элементом будет tail.
                tail = new Node<>(task, head, null);
                head.setNext(tail);
                historyManager.put(task.getId(), tail); // Кладем задачу в историю.
            } else { // Если размер списка > 1, значит в нем уже есть head и tail. Добавляем новый tail. Пересвязываем узлы.
                Node<Task> node = tail;
                tail = new Node<>(task, node, null);
                node.setNext(tail);
                historyManager.put(task.getId(), tail); // Кладем задачу в историю.
            }
            size++; // Увеличиваем размер двусвязного списка.
        }

        private List<Task> getTask() { // Метод проходится по ссылкам узлов Node – от head до tail – и возвращает их
            List<Task> listHistory = new ArrayList<>(); // листом с объектами Task.
            Node<Task> node = head;

            while (node != null) {
                listHistory.add(node.getData());
                node = node.getNext();
            }
            return listHistory;
        }

        public int getSize() { // Метод возвращает размер двусвязного списка.
            return size;
        }
    }
}
