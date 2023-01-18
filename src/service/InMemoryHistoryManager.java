package service;

import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> history = new ArrayList<>();
    private final Map<Integer, Node<? extends Task>> taskNodes = new HashMap<>();
    public Node head;
    public Node tail;

    private class Node<T extends Task> {
        public T data;
        public Node<T> next;
        public Node<T> prev;

        public Node(Node<T> prev, T data, Node<T> next) {
            this.data = data;
            this.next = next;
            this.prev = prev;
        }
    }

    public void linkLast(Task task) {
        final Node oldTail = tail;
        final Node newNode = new Node(oldTail, task, null);
        tail = newNode;
        if (oldTail == null) {
            head = newNode;
        } else {
            oldTail.next = newNode;
        }
        taskNodes.put(task.getId(), newNode);
    }

    public void getTasks() {
        history.clear();
        if (head != null) {
            history.add(head.data);
            Node x = head.next;
            while (x != null) {
                history.add(x.data);
                x = x.next;
            }
        }
    }

    public Task removeNode(Node x) {
        Node<? extends Task> task = taskNodes.remove(x.data.getId());
        final Task element = task.data;
        final Node next = task.next;
        final Node prev = task.prev;

        if (prev == null) {
            head = next;
        } else {
            prev.next = next;
            task.prev = null;
        }

        if (next == null) {
            tail = prev;
        } else {
            next.prev = prev;
            task.next = null;
        }
        task.data = null;
        return element;
    }

    @Override
    public void add(Task task) {
        if (task != null) {
            if (taskNodes.containsKey(task.getId())) {
                linkLast(removeNode(taskNodes.get(task.getId())));
            } else {
                linkLast(task);
            }
        }
    }

    @Override
    public List<Task> getHistory() {
        getTasks();
        return history;
    }

    @Override
    public void remove(int id) {
        history.removeIf(task -> task.getId() == id);
        if (taskNodes.containsKey(id)) {
            removeNode(taskNodes.get(id));
        }
    }
}