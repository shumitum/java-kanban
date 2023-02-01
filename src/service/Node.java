package service;

import model.Task;

public class Node {
    Task data;
    Node next;
    Node prev;

    public Node(Node prev, Task data, Node next) {
        this.data = data;
        this.next = next;
        this.prev = prev;
    }
}