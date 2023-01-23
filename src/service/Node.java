package service;

import model.Task;

public class Node { //Не нашел более причин использовать дженерики в реализации класса, объявил поле data с типом Task
    //в которое можно положить всех наследников класса Task(в котором есть все нужные методы)
    Task data;
    Node next;
    Node prev;

    public Node(Node prev, Task data, Node next) {
        this.data = data;
        this.next = next;
        this.prev = prev;
    }
}