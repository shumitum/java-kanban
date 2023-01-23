package service;

import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> history = new ArrayList<>();
    private final Map<Integer, Node> taskNodes = new HashMap<>();
    private Node head; //проглядел, private конечно же
    private Node tail; //проглядел, private конечно же

    private void linkLast(Task task) {
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

    private void getTasks() {
        history.clear();
        if (head != null) {
            history.add(head.data);
            Node currentNode = head.next;
            while (currentNode != null) {
                history.add(currentNode.data);
                currentNode = currentNode.next;
            }
        }
    }

    private Task removeNode(Node removedTaskNode) {
        Node task = taskNodes.remove(removedTaskNode.data.getId()); //(Что будет, если removedTaskNode==null?)
        //Будет NullPointerException. Добавил проверку передаваемой Ноды на null в методе add до вызова метода removeNode
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
                if (taskNodes.get(task.getId()) != null) {
                    linkLast(removeNode(taskNodes.get(task.getId())));
                }
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