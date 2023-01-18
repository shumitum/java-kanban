import model.Epic;
import model.Subtask;
import model.Task;
import service.Managers;
import service.TaskManager;

public class Main {

    public static void main(String[] args) {
        TaskManager inMemoryTaskManager = Managers.getDefault();

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
        inMemoryTaskManager.createTask(newTask2);

        inMemoryTaskManager.createEpic(epicTask1);
        inMemoryTaskManager.createEpic(epicTask2);

        inMemoryTaskManager.createSubtask(epicTask1, subtask1);
        inMemoryTaskManager.createSubtask(epicTask1, subtask2);

        inMemoryTaskManager.createSubtask(epicTask1, subtask3);

        for (int i = 0; i < 2; i++) {
            inMemoryTaskManager.getTaskById(newTask2.getId());
        }
        System.out.println("ЗАПРОС task 2 \n" + inMemoryTaskManager.getHistory());
        for (int i = 0; i < 2; i++) {
            inMemoryTaskManager.getEpicById(epicTask1.getId());
        }
        System.out.println("ЗАПРОС epic 1 \n" + inMemoryTaskManager.getHistory());
        for (int i = 0; i < 2; i++) {
            inMemoryTaskManager.getEpicById(epicTask2.getId());
        }
        System.out.println("ЗАПРОС epic 2 \n" + inMemoryTaskManager.getHistory());
        for (int i = 0; i < 2; i++) {
            inMemoryTaskManager.getSubtaskById(subtask3.getId());
        }
        System.out.println("ЗАПРОС подзадачи 3 \n" + inMemoryTaskManager.getHistory());
        for (int i = 0; i < 2; i++) {
            inMemoryTaskManager.getSubtaskById(subtask1.getId());
        }
        System.out.println("ЗАПРОС подзадачи 1 \n" + inMemoryTaskManager.getHistory());
        for (int i = 0; i < 2; i++) {
            inMemoryTaskManager.getSubtaskById(subtask2.getId());
        }
        System.out.println("ЗАПРОС подзадачи 2 \n" + inMemoryTaskManager.getHistory());
        for (int i = 0; i < 2; i++) {
            inMemoryTaskManager.getTaskById(newTask2.getId());
        }
        System.out.println("повторный ЗАПРОС task 2 \n" + inMemoryTaskManager.getHistory());
        for (int i = 0; i < 2; i++) {
            inMemoryTaskManager.getTaskById(newTask1.getId());
        }
        System.out.println("ЗАПРОС task 1 \n" + inMemoryTaskManager.getHistory());
        for (int i = 0; i < 2; i++) {
            inMemoryTaskManager.getEpicById(epicTask1.getId());
        }
        System.out.println("повторный ЗАПРОС epic 1 \n" + inMemoryTaskManager.getHistory());
        for (int i = 0; i < 2; i++) {
            inMemoryTaskManager.getSubtaskById(subtask1.getId());
        }
        System.out.println("повторный ЗАПРОС подзадачи 1 \n" + inMemoryTaskManager.getHistory());

        inMemoryTaskManager.deleteTaskById(2);
        System.out.println("УДАЛИТЬ task 2 \n" + inMemoryTaskManager.getHistory());
        inMemoryTaskManager.deleteSubtaskById(6);
        System.out.println("УДАЛИТЬ subtask 2 \n" + inMemoryTaskManager.getHistory());
        inMemoryTaskManager.deleteEpicById(3);
        System.out.println("УДАЛИТЬ epic 1 вместе с подзадачами 1 и 3 \n" + inMemoryTaskManager.getHistory());

        inMemoryTaskManager.clearEpicList();
        inMemoryTaskManager.clearTasksList();
        System.out.println("УДАЛИТЬ оставшееся \n" + inMemoryTaskManager.getHistory());
    }
}
