package service;

import exceptions.ManagerSaveException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static service.FileBackedTasksManager.HEADER;
import static service.FileBackedTasksManager.PATH;

class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {
    @Override
    FileBackedTasksManager getRightTypeOfManager() {
        return new FileBackedTasksManager(new File(PATH));
    }

    @BeforeEach
    void clearDataCsv() {
        try (Writer taskWriter = new FileWriter(PATH, StandardCharsets.UTF_8)) {
            taskWriter.write(HEADER);
        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось сохранить файл", e);
        }
    }

    @Test
    void shouldReturnFileBackedTasksManagerWithoutTasks() {
        TaskManager fileBackedTasksManager = FileBackedTasksManager.loadFromFile(new File(PATH));
        assertEquals(0, fileBackedTasksManager.getHistory().size(), "История просмотров должна быть пустой");
        assertEquals(0, fileBackedTasksManager.getListOfTasks().size(), "Список задач должен быть пуст");
    }

    @Test
    void shouldReturnFileBackedTasksManagerWithEpic() {
        taskManager.createEpic(epic);
        taskManager.getEpicById(epic.getId());
        TaskManager fileBackedTasksManager = FileBackedTasksManager.loadFromFile(new File(PATH));

        assertEquals(1, fileBackedTasksManager.getHistory().size(),
                "В истории просмотров должен быть один элемент");
        assertEquals(1, fileBackedTasksManager.getListOfEpics().size(),
                "В списке должен быть одни эпик");
    }

    @Test
    void shouldReturnFileBackedTasksManagerWithEmptyHistory() {
        taskManager.createTask(task);
        taskManager.createEpic(epic);

        TaskManager fileBackedTasksManager = FileBackedTasksManager.loadFromFile(new File(PATH));
        assertEquals(1, fileBackedTasksManager.getListOfTasks().size(), "В списке должна быть одна задача");
        assertEquals(1, fileBackedTasksManager.getListOfEpics().size(), "В списке должен быть одни эпик");
        assertEquals(0, fileBackedTasksManager.getHistory().size(), "История просмотров должна быть пустой");
    }
}