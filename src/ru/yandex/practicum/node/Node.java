package ru.yandex.practicum.node;

import ru.yandex.practicum.tasks.Task;

import java.util.Objects;

public class Node<T extends Task> {
    private final T data;
    private Node<T> next;
    private Node<T> prev;

    public Node(T data, Node<T> prev, Node<T> next) {
        this.data = data;
        this.next = next;
        this.prev = prev;
    }

    public T getData() {
        return data;
    }

    public Node<T> getNext() {
        return next;
    }

    public void setNext(Node<T> next) {
        this.next = next;
    }

    public Node<T> getPrev() {
        return prev;
    }

    public void setPrev(Node<T> prev) {
        this.prev = prev;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node<?> node = (Node<?>) o;
        return Objects.equals(data, node.data) && Objects.equals(next, node.next) && Objects.equals(prev, node.prev);
    }
}
