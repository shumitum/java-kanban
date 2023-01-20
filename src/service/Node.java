package service;

import model.Task;

public class Node<T extends Task> { //Класс Node служит единственной цели хранить элементы истории просмотров,
    //а каждый элемент в истории просмотров это Task или наследник класса Task
    public T data;
    public Node<T> next;
    public Node<T> prev;

    public Node(Node<T> prev, T data, Node<T> next) {
        this.data = data;
        this.next = next;
        this.prev = prev;
    }
}
