import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import service.Managers;
import service.TaskManager;

public class Main {

    public static void main(String[] args) {
        //код ниже служит для проверки работы методов и не несёт в себе никакого функционала
        TaskManager inMemoryTaskManager = Managers.getDefault();

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

        inMemoryTaskManager.createTask(newTask1);
        System.out.println("Добавить первую задачу " + inMemoryTaskManager.getTasksList());
        inMemoryTaskManager.createTask(newTask2);
        System.out.println("Добавить вторую задачу " + inMemoryTaskManager.getTasksList());
        Task newTask21 = new Task();
        newTask21.setTaskName("upd task 2");
        newTask21.setId(newTask2.getId());
        inMemoryTaskManager.updateTask(newTask21);
        System.out.println("Обновить вторую задачу " + inMemoryTaskManager.getTasksList());
        System.out.println("Получить список задач " + inMemoryTaskManager.getListOfTasks());
        System.out.println("Получить задачу по ID " + inMemoryTaskManager.getTaskById(newTask1.getId()));
        System.out.println("Получить задачу по ID " + inMemoryTaskManager.getTaskById(newTask2.getId()));
        inMemoryTaskManager.deleteTaskById(newTask1.getId());
        System.out.println("Удалить первую задачу " + inMemoryTaskManager.getTasksList());
        inMemoryTaskManager.clearTasksList();
        System.out.println("Очистить список задач " + inMemoryTaskManager.getTasksList());

        System.out.println("МАНИПУЛЯЦИИ С ЭПИКАМИ И ПОДЗАДАЧАМИ");
        inMemoryTaskManager.createEpic(epicTask1);
        inMemoryTaskManager.createEpic(epicTask2);
        System.out.println("Создать эпики 1 и 2 " + inMemoryTaskManager.getEpicList());
        inMemoryTaskManager.createSubtask(epicTask1, subtask1);
        System.out.println("Добавить подзадачу 1 к эпику 1 " + inMemoryTaskManager.getSubtaskList());
        inMemoryTaskManager.createSubtask(epicTask1, subtask2);
        System.out.println("Добавить подзадачу 2 к эпику 1 " + inMemoryTaskManager.getSubtaskList());
        inMemoryTaskManager.createSubtask(epicTask2, subtask3);
        System.out.println("Добавить подзадачу 3 к эпику 2 " + inMemoryTaskManager.getSubtaskList());
        Epic epicTask3 = new Epic();
        epicTask3.setTaskName("upd epic 1");
        epicTask3.setId(epicTask1.getId());
        inMemoryTaskManager.updateEpic(epicTask3);
        System.out.println("Обновить эпик 1 " + inMemoryTaskManager.getEpicList());
        Subtask subtask6 = new Subtask();
        subtask6.setTaskName("SUB 6");
        inMemoryTaskManager.createSubtask(epicTask1, subtask6);
        Subtask subtask5 = new Subtask();
        subtask5.setId(subtask1.getId());
        subtask5.setTaskName("upd sub 1");
        subtask2.setStatus(Status.IN_PROGRESS);
        subtask5.setStatus(Status.DONE);
        inMemoryTaskManager.updateSubtask(subtask5);
        System.out.println("Обновить подзадачу 1 и 2 " + inMemoryTaskManager.getSubtaskList());
        System.out.println("Показать список всех эпиков " + inMemoryTaskManager.getListOfEpics());
        System.out.println("Получить эпик 1 по ID " + inMemoryTaskManager.getEpicById(epicTask1.getId()));
        System.out.println("Получить эпик 2 по ID " + inMemoryTaskManager.getEpicById(epicTask2.getId()));
        subtask3.setStatus(Status.IN_PROGRESS);
        inMemoryTaskManager.updateSubtask(subtask3);
        System.out.println("Показать список всех подзадач " + inMemoryTaskManager.getListOfSubtasks());
        System.out.println("Получить список подзадач эпика 1 " + inMemoryTaskManager.getEpicsSubtaskList(epicTask1.getId()));
        System.out.println("Получить список подзадач эпика 2 " + inMemoryTaskManager.getEpicsSubtaskList(epicTask2.getId()));
        System.out.println("Получить подзадачу 3 по ID. " + inMemoryTaskManager.getSubtaskById(subtask3.getId()));
        inMemoryTaskManager.deleteSubtaskById(subtask3.getId());
        System.out.println("Удалить подзадачу 3 эпика 2 " + inMemoryTaskManager.getSubtaskList());
        System.out.println("Получить эпик 2 по ID " + inMemoryTaskManager.getEpicById(epicTask2.getId()));
        System.out.println("Получить список подзадач эпика 2 " + inMemoryTaskManager.getEpicsSubtaskList(epicTask2.getId()));
        Subtask subtask4 = new Subtask();
        inMemoryTaskManager.createSubtask(epicTask2, subtask4);
        System.out.println("Добавить подзадачу 4 к эпику 2 " + inMemoryTaskManager.getSubtaskList());
        for (int i = 0; i < 20; i++) {
            inMemoryTaskManager.getEpicById(epicTask1.getId());
        }
        for (int i = 0; i < 2; i++) {
            inMemoryTaskManager.getSubtaskById(subtask2.getId());
        }
        inMemoryTaskManager.clearSubtaskList();
        System.out.println("Удалить ВСЕ подзадачи " + inMemoryTaskManager.getSubtaskList());
        inMemoryTaskManager.deleteEpicById(epicTask2.getId());
        System.out.println("Удалить эпик 2, после удаления подзадач статус должен поменяться на NEW "
                + inMemoryTaskManager.getEpicList());
        inMemoryTaskManager.clearEpicList();
        System.out.println("Удалить ВСЕ эпики " + inMemoryTaskManager.getEpicList());

        System.out.println("\nИСТОРИЯ ПРОСМТОРА ЗАДАЧ");
        System.out.println(inMemoryTaskManager.getHistory());
    }
}