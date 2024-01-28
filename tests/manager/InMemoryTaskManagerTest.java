package manager;

import module.Epic;
import module.Status;
import module.Subtask;
import module.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    @BeforeEach
    public void beforeEach() {
        manager = new InMemoryTaskManager(new InMemoryHistoryManager());
        manager.createTask(new Task(1, "Test Create Task 1", "Desription"));
        manager.createTask(new Task(2, "Test Create Task 2", "Desription"));
        manager.createTask(new Task(3, "Test Create Task 3", "Desription"));
        manager.createEpic(new Epic(4, "Test Create Epic 1", "Description", new ArrayList<>()));
        manager.createEpic(new Epic(5, "Test Create Epic 2", "Description", new ArrayList<>()));
        manager.createEpic(new Epic(6, "Test Create Epic 3", "Description", new ArrayList<>()));
        manager.createSubtask(4 ,new Subtask(7, "Test Subtask 1", "Description", 10, LocalDateTime.of(2024, 1, 27, 10, 0), 4));
        manager.createSubtask(4 ,new Subtask(8, "Test Subtask 2", "Description", 10, LocalDateTime.of(2024, 1, 27, 10, 30), 4));
        manager.createSubtask(5 ,new Subtask(9, "Test Subtask 3", "Description", 10, LocalDateTime.of(2024, 1, 27, 11, 0), 5));
        manager.setId(manager.getMaxId());
    }

    @Test
    public void createTaskTest() {
        Task expectedTask = new Task(10, "Test Create Task 1", "Desription");
        manager.createTask(expectedTask);
        Task task = manager.getTask(10);
        assertEquals(expectedTask, task);
    }

    @Test
    public void createEpicTest() {
        Epic expectedEpic = new Epic(10, "Test Create Epic 3", "Description", new ArrayList<>());
        manager.createEpic(expectedEpic);
        Epic epic = manager.getEpic(10);
        assertEquals(expectedEpic, epic);
    }

    @Test
    public void createSubtaskTest() {
        Subtask expectedSubtask = new Subtask(10, "Test Subtask 1", "Description", 10, LocalDateTime.of(2024, 1, 27, 10, 0), 4);
        manager.createSubtask(6, expectedSubtask);
        Subtask subtask = manager.getSubtask(10);
        assertEquals(expectedSubtask, subtask);
    }

    @Test
    public void updateTaskTest() {
        Task updateTask = manager.getTask(3);
        manager.taskInProgress(updateTask);
        manager.updateTask(updateTask);

        Status expectedTaskStatus = Status.IN_PROGRESS;
        Status taskStatus = updateTask.getStatus();

        assertEquals(expectedTaskStatus, taskStatus);
    }

    @Test
    public void updateEpicTest() {
        Epic updateEpic = manager.getEpic(4);
        for (Subtask subtask : updateEpic.getSubtasks()) {
            subtask.setStatus(Status.DONE);
        }
        manager.updateEpic(updateEpic);
        Status expectedEpicStatus = Status.DONE;
        Status epicStatus = updateEpic.getStatus();

        assertEquals(expectedEpicStatus, epicStatus);
    }

    @Test
    public void updateSubtaskTest() {
        Subtask updateSubtask = manager.getSubtask(7);
        manager.taskInProgress(updateSubtask);
        manager.updateSubtask(updateSubtask);

        Status expectedSubtaskStatus = Status.IN_PROGRESS;
        Status subtaskStatus = updateSubtask.getStatus();

        assertEquals(expectedSubtaskStatus, subtaskStatus);
    }

    @Test
    public void getTaskTestNormal() {
        Task expectedTask = new Task(1, "Test Create Task 1", "Desription");
        Task task = manager.getTask(1);
        assertEquals(expectedTask, task);
    }

    @Test
    public void getTaskTestExistentIdReturnNull() {
        assertNull(manager.getTask(15));
    }

    @Test
    public void getTaskTestAddHistory() {
        Task expectedTask = manager.getTask(1);

        assertNotNull(manager.getHistory());

        Task task = manager.getHistory().get(0);

        assertEquals(expectedTask, task);
    }

    @Test
    public void getEpicTestNormal() {
        Epic expectedEpic = new Epic(6, "Test Create Epic 3", "Description", new ArrayList<>());
        Epic epic = manager.getEpic(6);
        assertEquals(expectedEpic, epic);
    }

    @Test
    public void getEpicTestExistentIdReturnNull() {
        assertNull(manager.getEpic(15));
    }

    @Test
    public void getEpicTestAddHistory() {
        Epic expectedEpic = manager.getEpic(6);

        assertNotNull(manager.getHistory());

        Task epic = manager.getHistory().get(0);

        assertEquals(expectedEpic, epic);
    }

    @Test
    public void getSubtaskTestNormal() {
        Subtask expectedSubtask = new Subtask(9, "Test Subtask 3", "Description", 10, LocalDateTime.of(2024, 1, 27, 11, 0), 5);
        Subtask subtask = manager.getSubtask(9);
        assertEquals(expectedSubtask, subtask);
    }

    @Test
    public void getSubtaskTestExistentIdReturnNull() {
        assertNull(manager.getSubtask(15));
    }

    @Test
    public void getSubtaskTestAddHistory() {
        Subtask expectedSubtask = manager.getSubtask(9);

        assertNotNull(manager.getHistory());

        Task subtask = manager.getHistory().get(0);

        assertEquals(expectedSubtask, subtask);
    }

    @Test
    public void getAllTasksTestCompletedList() {
        TaskManager managerForGetAllTasks = Managers.getDefaultTaskManager();
        managerForGetAllTasks.createTask(new Task(1, "Test Create Task 1", "Desription"));
        managerForGetAllTasks.createTask(new Task(2, "Test Create Task 2", "Desription"));
        managerForGetAllTasks.createTask(new Task(3, "Test Create Task 3", "Desription"));
        List<Task> expectedList = new ArrayList<>();
        expectedList.add(new Task(1, "Test Create Task 1", "Desription"));
        expectedList.add(new Task(2, "Test Create Task 2", "Desription"));
        expectedList.add(new Task(3, "Test Create Task 3", "Desription"));


        List<Task> list = managerForGetAllTasks.getAllTasks();

        assertEquals(expectedList, list);
    }

    @Test
    public void getAllTasksTestEmptyList() {
        TaskManager managerForGetAllTasks = Managers.getDefaultTaskManager();
        assertTrue(managerForGetAllTasks.getAllTasks().isEmpty());
    }

    @Test
    public void getTasksTestCompletedList() {
        List<Task> expectedTasks = new ArrayList<>();
        expectedTasks.add(new Task(1, "Test Create Task 1", "Desription"));
        expectedTasks.add(new Task(2, "Test Create Task 2", "Desription"));
        expectedTasks.add(new Task(3, "Test Create Task 3", "Desription"));

        List<Task> tasks = manager.getTasks();

        assertEquals(expectedTasks, tasks);
    }

    @Test
    public void getTasksTestEmptyList() {
        TaskManager managerForGetTasks = Managers.getDefaultTaskManager();
        assertTrue(managerForGetTasks.getAllTasks().isEmpty());
    }


    @Test
    public void getEpicsTestCompletedList() {
        List<Epic> expectedEpics = new ArrayList<>();
        expectedEpics.add(new Epic(4, "Test Create Epic 1", "Description", new ArrayList<>()));
        expectedEpics.get(0).getSubtasks().add(new Subtask(7, "Test Subtask 1", "Description", 10, LocalDateTime.of(2024, 1, 27, 10, 0), 4));
        expectedEpics.get(0).getSubtasks().add(new Subtask(8, "Test Subtask 2", "Description", 10, LocalDateTime.of(2024, 1, 27, 10, 30), 4));
        expectedEpics.add(new Epic(5, "Test Create Epic 2", "Description", new ArrayList<>()));
        expectedEpics.get(1).getSubtasks().add(new Subtask(9, "Test Subtask 3", "Description", 10, LocalDateTime.of(2024, 1, 27, 11, 0), 5));
        expectedEpics.add(new Epic(6, "Test Create Epic 3", "Description", new ArrayList<>()));

        List<Epic> epics = manager.getEpics();

        assertEquals(expectedEpics, epics);
    }

    @Test
    public void getEpicsTestEmptyList() {
        TaskManager managerForGetEpics = Managers.getDefaultTaskManager();
        assertTrue(managerForGetEpics.getAllTasks().isEmpty());
    }

    @Test
    public void getSubtasksTestCompletedList() {
        List<Subtask> expectedSubtasks = new ArrayList<>();
        expectedSubtasks.add(new Subtask(7, "Test Subtask 1", "Description", 10, LocalDateTime.of(2024, 1, 27, 10, 0), 4));
        expectedSubtasks.add(new Subtask(8, "Test Subtask 2", "Description", 10, LocalDateTime.of(2024, 1, 27, 10, 30), 4));
        expectedSubtasks.add(new Subtask(9, "Test Subtask 3", "Description", 10, LocalDateTime.of(2024, 1, 27, 11, 0), 5));

        List<Subtask> subtasks = manager.getSubtasks();

        assertEquals(expectedSubtasks, subtasks);
    }
    @Test
    public void getSubtasksTestEmptyList() {
        TaskManager managerForGetSubtasks = Managers.getDefaultTaskManager();
        assertTrue(managerForGetSubtasks.getAllTasks().isEmpty());
    }

    @Test
    public void getSubtasksByEpicIdTest() {
        List<Subtask> expectedSubtasksByEpicId = manager.getEpic(5).getSubtasks();
        List<Subtask> subtasksByEpicId = manager.getSubtasksByEpicId(5);
        assertEquals(expectedSubtasksByEpicId, subtasksByEpicId);
    }

    @Test
    public void removeTaskTest() {
        Task expectedTask = new Task(10, "Test Create Task 1", "Desription");
        manager.createTask(expectedTask);
        Task task = manager.getTask(10);
        assertEquals(expectedTask, task);
        manager.removeTask(10);
        assertNull(manager.getTask(10));
    }

    @Test
    public void removeEpicTest() {
        Epic expectedEpic = new Epic(10, "Test Create Epic 1", "Description", new ArrayList<>());
        Subtask subtask = new Subtask(11, "Test Subtask 1", "Description", 10, LocalDateTime.of(2024, 1, 27, 10, 0), 10);
        manager.createEpic(expectedEpic);
        manager.createSubtask(10, subtask);
        assertNotNull(manager.getEpic(10));
        assertFalse(manager.getSubtasksByEpicId(10).isEmpty());
        manager.removeEpic(10);
        assertNull(manager.getEpic(10));
        assertTrue(manager.getSubtasksByEpicId(10).isEmpty());
    }

    @Test
    public void removeSubtask() {
        Subtask expectedSubtask = new Subtask(10, "Test Subtask 1", "Description", 10, LocalDateTime.of(2024, 1, 27, 10, 0), 4);
        manager.createSubtask(6, expectedSubtask);
        Subtask subtask = manager.getSubtask(10);
        assertEquals(expectedSubtask, subtask);
        manager.removeSubtask(10);
        assertNull(manager.getSubtask(10));
    }

    @Test
    public void removeAllTasksTest() {
        manager.createTask(new Task(10, "Test Task 1", "Desription"));
        manager.createEpic(new Epic(11, "Test Epic 1", "Description", new ArrayList<>()));
        manager.createSubtask(11, new Subtask(12, "Test Subtask 1", "Description", 10, LocalDateTime.of(2024, 1, 27, 10, 0), 11));
        manager.setId(manager.getMaxId());
        int expectedId = 12;
        int id = manager.getId();

        assertEquals(expectedId, id);
        assertNotNull(manager.getTask(10));
        assertNotNull(manager.getEpic(11));
        assertNotNull(manager.getSubtask(12));

        manager.removeAllTasks();

        int expectedIdAfterRemove = 0;
        int idAfterRemove = manager.getId();

        assertEquals(expectedIdAfterRemove, idAfterRemove);
        assertNull(manager.getTask(10));
        assertNull(manager.getEpic(11));
        assertNull(manager.getSubtask(12));
    }

    @Test
    public void getIdTest() {
        manager.createTask(new Task(10, "Test Task", "Desription"));
        manager.setId(manager.getMaxId());
        int expectedId = 10;
        int id = manager.getId();
        assertEquals(expectedId, id);
    }

    @Test
    public void getMaxIdTest() {
        TaskManager managerForGetMaxId = Managers.getDefaultTaskManager();
        managerForGetMaxId.createTask(new Task(150, "Test Task", "Desription"));
        managerForGetMaxId.createTask(new Task(1, "Test Task", "Desription"));
        manager.setId(managerForGetMaxId.getMaxId());
        int expectedId = 150;
        int id = manager.getId();
        assertEquals(expectedId, id);
    }

    @Test
    public void setIdTest() {
        int expectedId = 233;
        manager.setId(expectedId);
        int id = manager.getId();
        assertEquals(expectedId, id);
    }
    @Test
    public void putTaskTest() {
        Task expectedTask = new Task(10, "Test Task", "Desription");
        manager.putTask(expectedTask);
        Task task = manager.getTask(10);
        assertEquals(expectedTask, task);
    }

    @Test
    public void putEpicTest() {
        Epic expectedEpic = new Epic(10, "Test Epic", "Description", new ArrayList<>());
        manager.putEpic(expectedEpic);
        Epic epic = manager.getEpic(10);
        assertEquals(expectedEpic, epic);
    }

    @Test
    public void putSubtaskTest() {
        Subtask expectedSubtask = new Subtask(10, "Test Subtask", "Description", 10, LocalDateTime.of(2024, 1, 27, 10, 0), 4);
        manager.putSubtask(expectedSubtask);
        Subtask subtask = manager.getSubtask(10);
        assertEquals(expectedSubtask, subtask);
    }

    @Test
    public void getHistoryTest() {
        List<Task> expectedHistory = new ArrayList<>();
        expectedHistory.add(new Task(1, "Test Create Task 1", "Desription"));
        expectedHistory.add(new Task(2, "Test Create Task 2", "Desription"));
        expectedHistory.add(new Task(3, "Test Create Task 3", "Desription"));

        manager.getTask(1);
        manager.getTask(2);
        manager.getTask(3);
        List<Task> tasksHistory = manager.getHistory();

        assertEquals(expectedHistory, tasksHistory);
    }

    @Test
    public void taskInProgressTest() {
        Task task = manager.getTask(1);
        Status statusTask = task.getStatus();
        Status beforeChangeStatus = Status.NEW;
        assertEquals(statusTask, beforeChangeStatus);
        manager.taskInProgress(task);
        statusTask = task.getStatus();
        Status afterChangeStatus = Status.IN_PROGRESS;
        assertEquals(statusTask, afterChangeStatus);
    }

    @Test
    public void taskIsDoneTest() {
        Task task = manager.getTask(1);
        Status statusTask = task.getStatus();
        Status beforeChangeStatus = Status.NEW;
        assertEquals(statusTask, beforeChangeStatus);
        manager.taskIsDone(task);
        statusTask = task.getStatus();
        Status afterChangeStatus = Status.DONE;
        assertEquals(statusTask, afterChangeStatus);
    }

    @Test
    public void getHistoryManagerTest() {
        assertNotNull(manager.getHistoryManager());
    }

    @Test
    public void getPrioritizedTasksTest() {
        TaskManager managerForPrioritized = Managers.getDefaultTaskManager();
        managerForPrioritized.createTask(new Task(1, "Test1", "Description", 10, LocalDateTime.of(2024, 1, 26, 12, 0)));
        managerForPrioritized.createTask(new Task(2, "Test2", "Description", 15, LocalDateTime.of(2024, 1, 26, 13, 0)));
        managerForPrioritized.createTask(new Task(3, "Test3", "Description", 5, LocalDateTime.of(2024, 1, 26, 12, 16)));

        List<Task> expectedList = new ArrayList<>();
        expectedList.add(new Task(1, "Test1", "Description", 10, LocalDateTime.of(2024, 1, 26, 12, 0)));
        expectedList.add(new Task(3, "Test3", "Description", 5, LocalDateTime.of(2024, 1, 26, 12, 16)));
        expectedList.add(new Task(2, "Test2", "Description", 15, LocalDateTime.of(2024, 1, 26, 13, 0)));

        Set<Task> prioritizedList = managerForPrioritized.getPrioritizedTasks();

        assertArrayEquals(expectedList.toArray(), prioritizedList.toArray());
    }

    @Test
    public void isIntersectionTest() {
        TaskManager managerForSearchIntersections = Managers.getDefaultTaskManager();
        managerForSearchIntersections.createTask(new Task(1, "Test1", "Description", 10, LocalDateTime.of(2024, 1, 26, 12, 0)));
        managerForSearchIntersections.createTask(new Task(2, "Test2", "Description", 15, LocalDateTime.of(2024, 1, 26, 13, 0)));
        managerForSearchIntersections.createTask(new Task(3, "Test3", "Description", 5, LocalDateTime.of(2024, 1, 26, 12, 16)));

        LocalDateTime start = LocalDateTime.of(2024, 1, 27, 10, 30);
        LocalDateTime end = start.plusMinutes(10);
        assertFalse(managerForSearchIntersections.isIntersection(start, end));

        LocalDateTime startTrue = LocalDateTime.of(2024, 1, 26, 12, 18);
        LocalDateTime endTrue = startTrue.plusMinutes(10);
        assertTrue(managerForSearchIntersections.isIntersection(startTrue, endTrue));
    }

}
