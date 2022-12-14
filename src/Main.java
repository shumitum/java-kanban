import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import service.InMemoryTaskManager;

public class Main {

    public static void main(String[] args) {
        //код ниже служит для проверки работы методов и не несёт в себе никакого функционала
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        System.out.println("МАНИПУЛЯЦИИ С ЗАДАЧАМИ");
        Epic epicTask1 = new Epic();
        epicTask1.setTaskName("epic 1");
        Epic epicTask2 = new Epic();
        epicTask2.setTaskName("epic 2");

        Task newTask1 = new Task();
        newTask1.setTaskName("task 1");
        Task newTask2 = new Task();
        newTask2.setTaskName("task 2");

        Subtask subtask1 = new Subtask();
        subtask1.setTaskName("sub 1");
        Subtask subtask2 = new Subtask();
        subtask2.setTaskName("sub 2");
        Subtask subtask3 = new Subtask();
        subtask3.setTaskName("sub 3");

        taskManager.createTask(newTask1);
        System.out.println("Добавить первую задачу " + taskManager.getTasksList());
        taskManager.createTask(newTask2);
        System.out.println("Добавить вторую задачу " + taskManager.getTasksList());
        Task newTask21 = new Task();
        newTask21.setTaskName("upd task 2");
        newTask21.setId(newTask2.getId());
        taskManager.updateTask(newTask21);
        System.out.println("Обновить вторую задачу " + taskManager.getTasksList());
        System.out.println("Получить список задач " + taskManager.getListOfTasks());
        System.out.println("Получить задачу по ID " + taskManager.getTaskById(2));
        taskManager.deleteTaskById(newTask1.getId());
        System.out.println("Удалить первую задачу " + taskManager.getTasksList());
        taskManager.clearTasksList();
        System.out.println("Очистить список задач " + taskManager.getTasksList());

        System.out.println("МАНИПУЛЯЦИИ С ЭПИКАМИ И ПОДЗАДАЧАМИ");
        taskManager.createEpic(epicTask1);
        taskManager.createEpic(epicTask2);
        System.out.println("Создать эпики 1 и 2 " + taskManager.getEpicList());
        taskManager.createSubtask(epicTask1, subtask1);
        System.out.println("Добавить подзадачу 1 к эпику 1 " + taskManager.getSubtaskList());
        taskManager.createSubtask(epicTask1, subtask2);
        System.out.println("Добавить подзадачу 2 к эпику 1 " + taskManager.getSubtaskList());
        taskManager.createSubtask(epicTask2, subtask3);
        System.out.println("Добавить подзадачу 3 к эпику 2 " + taskManager.getSubtaskList());
        Epic epicTask3 = new Epic();
        epicTask3.setTaskName("upd epic 1");
        epicTask3.setId(epicTask1.getId());
        taskManager.updateEpic(epicTask3);
        System.out.println("Обновить эпик 1 " + taskManager.getEpicList());
        Subtask subtask6 = new Subtask();
        subtask6.setTaskName("SUB 6");
        taskManager.createSubtask(epicTask1, subtask6);
        Subtask subtask5 = new Subtask();
        subtask5.setId(subtask1.getId());
        subtask5.setTaskName("upd sub 1");
        subtask2.setStatus(Status.IN_PROGRESS);
        subtask5.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask5);
        System.out.println("Обновить подзадачу 1 и 2 " + taskManager.getSubtaskList());
        System.out.println("Показать список всех эпиков " + taskManager.getListOfEpics());
        System.out.println("Получить эпик 1 по ID " + taskManager.getEpicById(epicTask1.getId()));
        System.out.println("Получить эпик 2 по ID " + taskManager.getEpicById(epicTask2.getId()));
        subtask3.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubtask(subtask3);
        System.out.println("Показать список всех подзадач " + taskManager.getListOfSubtasks());
        System.out.println("Получить список подзадач эпика 1 " + taskManager.getEpicsSubtaskList(epicTask1.getId()));
        System.out.println("Получить список подзадач эпика 2 " + taskManager.getEpicsSubtaskList(epicTask2.getId()));
        System.out.println("Получить подзадачу 3 по ID. " + taskManager.getSubtaskById(subtask3.getId()));
        taskManager.deleteSubtaskById(subtask3.getId());
        System.out.println("Удалить подзадачу 3 эпика 2 " + taskManager.getSubtaskList());
        System.out.println("Получить эпик 2 по ID " + taskManager.getEpicById(epicTask2.getId()));
        System.out.println("Получить список подзадач эпика 2 " + taskManager.getEpicsSubtaskList(epicTask2.getId()));
        Subtask subtask4 = new Subtask();
        taskManager.createSubtask(epicTask2, subtask4);
        System.out.println("Добавить подзадачу 4 к эпику 2 " + taskManager.getSubtaskList());
        taskManager.clearSubtaskList();
        System.out.println("Удалить ВСЕ подзадачи " + taskManager.getSubtaskList());
        taskManager.deleteEpicById(epicTask2.getId());
        System.out.println("Удалить эпик 2, после удаления подзадач статус должен поменяться на NEW "
                + taskManager.getEpicList());
        taskManager.clearEpicList();
        System.out.println("Удалить ВСЕ эпики " + taskManager.getEpicList());
    }
}
