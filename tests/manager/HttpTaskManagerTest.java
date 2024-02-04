package manager;

import module.Epic;
import module.Subtask;
import module.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.KVServer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HttpTaskManagerTest extends TaskManagerTest<HttpTaskManager> {
    private KVServer server;
    @BeforeEach
    public void beforeEach() throws IOException, InterruptedException {
        server = new KVServer();
        server.start();
        manager = Managers.getHttpTaskManager();
    }
    @AfterEach
    public void afterEach() throws IOException {
        server.stop();
    }
    @Test
    public void saveTestEmptyListTasks() throws IOException {
        assertTrue(manager.getAllTasks().isEmpty());
    }
    @Test
    public void saveTestCompletedListTasks() throws IOException, InterruptedException {
        manager.createTask(new Task(1, "Test Create Task 1", "Description", 15, LocalDateTime.of(2024,1,29,20,0)));
        manager.createTask(new Task(2, "Test Create Task 1", "Description", 15, LocalDateTime.of(2024,1,29,20,20)));
        manager.createTask(new Task(3, "Test Create Task 1", "Description", 15, LocalDateTime.of(2024,1,29,21,0)));
        manager.createEpic(new Epic(4, "Test Create Epic 1", "Description", new ArrayList<>()));
        manager.createEpic(new Epic(5, "Test Create Epic 2", "Description", new ArrayList<>()));
        manager.createEpic(new Epic(6, "Test Create Epic 3", "Description", new ArrayList<>()));
        manager.createSubtask(new Subtask(7, "Test Subtask 1", "Description", 10, LocalDateTime.of(2024, 1, 27, 10, 0), 4));
        manager.createSubtask(new Subtask(8, "Test Subtask 2", "Description", 10, LocalDateTime.of(2024, 1, 27, 10, 30), 4));
        manager.createSubtask(new Subtask(9, "Test Subtask 3", "Description", 10, LocalDateTime.of(2024, 1, 27, 11, 0), 5));
        manager.getTask(1);
        manager.getTask(2);
        manager.getTask(3);
        manager.getEpic(4);
        manager.getEpic(5);
        manager.getSubtask(9);

        HttpTaskManager taskManager = Managers.getHttpTaskManager();


        String expectedHistory = manager.getHistoryManager().toString();
        assertEquals(expectedHistory, taskManager.getHistoryManager().toString());

        String expectedString = manager.getAllTasks().toString();
        assertEquals(expectedString, taskManager.getAllTasks().toString());
    }

    @Test
    public void saveTestCompletedListEpicWithoutSubtasks() throws IOException, InterruptedException {
        manager.createEpic(new Epic(4, "Test Create Epic 1", "Description", new ArrayList<>()));
        HttpTaskManager taskManager = Managers.getHttpTaskManager();
        String expectedString = manager.getAllTasks().toString();
        assertEquals(expectedString, taskManager.getAllTasks().toString());
    }

}