import model.Epic;
import model.Subtask;
import model.Task;
import service.TaskManager;

public class Main {

    public static void main(String[] args) {
        //код ниже не несёт в себе никакого функионала, служит для проверки работы методов
        TaskManager taskManager = new TaskManager();

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
        System.out.println(taskManager.getTasksList() + " добавить первую задачу");
        taskManager.createTask(newTask2);
        System.out.println(taskManager.getTasksList() + " добавить вторую задачу");
        Task newTask21 = new Task();
        newTask21.setTaskName("upd task 2");
        newTask21.setId(newTask2.getId());
        taskManager.updateTask(newTask21);
        System.out.println(taskManager.getTasksList() + " обновить вторую задачу");
        System.out.println(taskManager.getListOfTasks() + " получить список задач");
        System.out.println(taskManager.getTaskById(2) + " получить задачу по ID");

        taskManager.deleteTaskById(newTask1.getId());
        System.out.println(taskManager.getTasksList() + " удалить первую задачу");
        taskManager.clearTasksList();
        System.out.println(taskManager.getTasksList() + " очистить список задач");

        taskManager.createEpic(epicTask1);
        taskManager.createEpic(epicTask2);
        System.out.println(taskManager.getEpicList() + " создать эпики 1 и 2 ");
        taskManager.createSubtask(epicTask1, subtask1);
        System.out.println(taskManager.getSubtaskList() + " добавить подзадачу 1 к эпику 1");
        taskManager.createSubtask(epicTask1, subtask2);
        System.out.println(taskManager.getSubtaskList() + " добавить подзадачу 2 к эпику 1");
        taskManager.createSubtask(epicTask2, subtask3);
        System.out.println(taskManager.getSubtaskList() + " добавить подзадачу 3 к эпику 2");
        Epic epicTask3 = new Epic();
        epicTask3.setTaskName("upd epic 1");
        epicTask3.setId(epicTask1.getId());
        taskManager.updateEpic(epicTask3);
        System.out.println(taskManager.getEpicList() + " обновить эпик 1");
        Subtask subtask5 = new Subtask();
        subtask5.setId(subtask1.getId());
        subtask5.setTaskName("upd sub 1"); //
        taskManager.updateSubtask(subtask5); //1111111111111
        System.out.println(taskManager.getSubtaskList() + " обновить подзадачу 3"); ///1111
        System.out.println(taskManager.getListOfEpics() + " показать список всех эпиков");
        System.out.println(taskManager.getEpicById(epicTask2.getId()) + " получить эпик по ID");
        System.out.println(taskManager.getListOfSubtasks() + " показать список всех подзадач");
        System.out.println(taskManager.getSubtaskById(subtask3.getId()) + " получить подзадачу по ID");
        taskManager.deleteSubtaskById(subtask3.getId());
        System.out.println(taskManager.getSubtaskList() + " удалить подзадачу 3");
        Subtask subtask4 = new Subtask();
        taskManager.createSubtask(epicTask2, subtask4);
        System.out.println(taskManager.getSubtaskList() + " добавить подзадачу 4 к эпику 2");
        System.out.println(taskManager.getEpicsSubtaskList(epicTask1.getId()) + " получить список подзадач эпика 1");

        taskManager.clearSubtaskList();
        System.out.println(taskManager.getSubtaskList() + " удалить ВСЕ подзадачи");

        taskManager.deleteEpicById(epicTask1.getId());
        System.out.println(taskManager.getEpicList() + " удалить эпик 1");
        taskManager.clearEpicList();
        System.out.println(taskManager.getEpicList() + " очистить список эпиков");
    }
}
