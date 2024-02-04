package manager;

import module.Epic;
import module.Status;
import module.Subtask;
import module.Task;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T manager;

    @Test
    public void createTaskTest() {
        Task expectedTask = new Task(1, "Test Create Task 1", "Description", 15, LocalDateTime.of(2024,1,29,20,0));
        manager.createTask(expectedTask);
        Task task = manager.getTask(1);
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
        manager.createEpic(new Epic(1, "Test Create Epic 1", "Description", new ArrayList<>()));
        Subtask expectedSubtask = new Subtask(2, "Test Subtask 1", "Description", 10, LocalDateTime.of(2024, 1, 27, 10, 0), 1);
        manager.createSubtask(expectedSubtask);
        Subtask subtask = manager.getSubtask(2);
        assertEquals(expectedSubtask, subtask);
    }

    @Test
    public void updateTaskTest() {
        manager.createTask(new Task(1, "Test Create Task 1", "Description", 15, LocalDateTime.of(2024,1,29,20,0)));
        Task updateTask = manager.getTask(1);
        updateTask.setStatus(Status.IN_PROGRESS);
        manager.updateTask(updateTask);

        Status expectedTaskStatus = Status.IN_PROGRESS;
        Status taskStatus = updateTask.getStatus();

        assertEquals(expectedTaskStatus, taskStatus);
    }

    @Test
    public void updateEpicTest() {
        manager.createEpic(new Epic(1, "Test Create Epic 1", "Description", new ArrayList<>()));
        manager.createSubtask(new Subtask(2, "Test Subtask 1", "Description", 10, LocalDateTime.of(2024, 1, 27, 10, 0), 1));
        Epic updateEpic = manager.getEpic(1);
        updateEpic.getSubtasks().get(0).setStatus(Status.DONE);
        manager.updateEpic(updateEpic);
        Status expectedEpicStatus = Status.DONE;
        Status epicStatus = updateEpic.getStatus();

        assertEquals(expectedEpicStatus, epicStatus);
    }

    @Test
    public void updateSubtaskTest() {
        manager.createEpic(new Epic(1, "Test Create Epic 1", "Description", new ArrayList<>()));
        manager.createSubtask(new Subtask(2, "Test Subtask 1", "Description", 10, LocalDateTime.of(2024, 1, 27, 10, 0), 1));
        Subtask updateSubtask = manager.getSubtask(2);
        updateSubtask.setStatus(Status.IN_PROGRESS);
        manager.updateSubtask(updateSubtask);

        Status expectedSubtaskStatus = Status.IN_PROGRESS;
        Status subtaskStatus = updateSubtask.getStatus();

        assertEquals(expectedSubtaskStatus, subtaskStatus);
    }

    @Test
    public void getTaskTestNormal() {
        Task expectedTask = new Task(1, "Test Create Task 1", "Description", 15, LocalDateTime.of(2024,1,29,20,0));
        manager.createTask(expectedTask);
        Task task = manager.getTask(1);
        assertEquals(expectedTask, task);
    }

    @Test
    public void getTaskTestExistentIdReturnNull() {
        assertNull(manager.getTask(15));
    }

    @Test
    public void getTaskTestAddHistory() {
        manager.createTask(new Task(1, "Test Create Task 1", "Description", 15, LocalDateTime.of(2024,1,29,20,0)));
        Task expectedTask = manager.getTask(1);
        Task task = manager.getHistory().get(0);

        assertEquals(expectedTask, task);
    }

    @Test
    public void getEpicTestNormal() {
        Epic expectedEpic = new Epic(6, "Test Create Epic 3", "Description", new ArrayList<>());
        manager.createEpic(expectedEpic);
        Epic epic = manager.getEpic(6);
        assertEquals(expectedEpic, epic);
    }

    @Test
    public void getEpicTestExistentIdReturnNull() {
        assertNull(manager.getEpic(15));
    }

    @Test
    public void getEpicTestAddHistory() {
        manager.createEpic(new Epic(6, "Test Create Epic 6", "Description", new ArrayList<>()));
        Epic expectedEpic = manager.getEpic(6);
        Task epic = manager.getHistory().get(0);

        assertEquals(expectedEpic, epic);
    }

    @Test
    public void getSubtaskTestNormal() {
        manager.createEpic(new Epic(1, "Test Create Epic 1", "Description", new ArrayList<>()));
        Subtask expectedSubtask = new Subtask(2, "Test Subtask 1", "Description", 10, LocalDateTime.of(2024, 1, 27, 11, 0), 1);
        manager.createSubtask(expectedSubtask);
        Subtask subtask = manager.getSubtask(2);
        assertEquals(expectedSubtask, subtask);
    }

    @Test
    public void getSubtaskTestExistentIdReturnNull() {
        assertNull(manager.getSubtask(15));
    }

    @Test
    public void getSubtaskTestAddHistory() {
        manager.createEpic(new Epic(1, "Test Create Epic 6", "Description", new ArrayList<>()));
        manager.createSubtask(new Subtask(2, "Test Subtask 3", "Description", 10, LocalDateTime.of(2024, 1, 27, 11, 0), 1));
        Subtask expectedSubtask = manager.getSubtask(2);
        Task subtask = manager.getHistory().get(0);

        assertEquals(expectedSubtask, subtask);
    }

    @Test
    public void getAllTasksTestCompletedList() {
        TaskManager managerForGetAllTasks = Managers.getDefaultInMemoryTaskManager();
        managerForGetAllTasks.createTask(new Task(1, "Test Create Task 1", "Description", 15, LocalDateTime.of(2024,1,29,20,0)));
        managerForGetAllTasks.createTask(new Task(2, "Test Create Task 2", "Description", 15, LocalDateTime.of(2024,1,29,20,20)));
        managerForGetAllTasks.createTask(new Task(3, "Test Create Task 3", "Description", 15, LocalDateTime.of(2024,1,29,20,40)));
        List<Task> expectedList = new ArrayList<>();
        expectedList.add(new Task(1, "Test Create Task 1", "Description", 15, LocalDateTime.of(2024,1,29,20,0)));
        expectedList.add(new Task(2, "Test Create Task 2", "Description", 15, LocalDateTime.of(2024,1,29,20,20)));
        expectedList.add(new Task(3, "Test Create Task 3", "Description", 15, LocalDateTime.of(2024,1,29,20,40)));


        List<Task> list = managerForGetAllTasks.getAllTasks();

        assertEquals(expectedList, list);
    }

    @Test
    public void getAllTasksTestEmptyList() {
        TaskManager managerForGetAllTasks = Managers.getDefaultInMemoryTaskManager();
        assertTrue(managerForGetAllTasks.getAllTasks().isEmpty());
    }

    @Test
    public void getTasksTestCompletedList() {
        List<Task> expectedTasks = new ArrayList<>();
        expectedTasks.add(new Task(1, "Test Create Task 1", "Description", 15, LocalDateTime.of(2024,1,29,20,0)));
        expectedTasks.add(new Task(2, "Test Create Task 2", "Description", 15, LocalDateTime.of(2024,1,29,20,20)));
        expectedTasks.add(new Task(3, "Test Create Task 3", "Description", 15, LocalDateTime.of(2024,1,29,20,40)));

        manager.createTask(new Task(1, "Test Create Task 1", "Description", 15, LocalDateTime.of(2024,1,29,20,0)));
        manager.createTask(new Task(2, "Test Create Task 2", "Description", 15, LocalDateTime.of(2024,1,29,20,20)));
        manager.createTask(new Task(3, "Test Create Task 3", "Description", 15, LocalDateTime.of(2024,1,29,20,40)));

        List<Task> tasks = manager.getTasks();

        assertEquals(expectedTasks, tasks);
    }

    @Test
    public void getTasksTestEmptyList() {
        TaskManager managerForGetTasks = Managers.getDefaultInMemoryTaskManager();
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

        manager.createEpic(new Epic(4, "Test Create Epic 1", "Description", new ArrayList<>()));
        manager.createEpic(new Epic(5, "Test Create Epic 2", "Description", new ArrayList<>()));
        manager.createEpic(new Epic(6, "Test Create Epic 3", "Description", new ArrayList<>()));
        manager.createSubtask(new Subtask(7, "Test Subtask 1", "Description", 10, LocalDateTime.of(2024, 1, 27, 10, 0), 4));
        manager.createSubtask(new Subtask(8, "Test Subtask 2", "Description", 10, LocalDateTime.of(2024, 1, 27, 10, 30), 4));
        manager.createSubtask(new Subtask(9, "Test Subtask 3", "Description", 10, LocalDateTime.of(2024, 1, 27, 11, 0), 5));

        List<Epic> epics = manager.getEpics();

        assertEquals(expectedEpics, epics);
    }

    @Test
    public void getEpicsTestEmptyList() {
        TaskManager managerForGetEpics = Managers.getDefaultInMemoryTaskManager();
        assertTrue(managerForGetEpics.getAllTasks().isEmpty());
    }

    @Test
    public void getSubtasksTestCompletedList() {
        List<Subtask> expectedSubtasks = new ArrayList<>();
        expectedSubtasks.add(new Subtask(7, "Test Subtask 1", "Description", 10, LocalDateTime.of(2024, 1, 27, 10, 0), 4));
        expectedSubtasks.add(new Subtask(8, "Test Subtask 2", "Description", 10, LocalDateTime.of(2024, 1, 27, 10, 30), 4));
        expectedSubtasks.add(new Subtask(9, "Test Subtask 3", "Description", 10, LocalDateTime.of(2024, 1, 27, 11, 0), 5));

        manager.createEpic(new Epic(4, "Test Create Epic 1", "Description", new ArrayList<>()));
        manager.createEpic(new Epic(5, "Test Create Epic 2", "Description", new ArrayList<>()));
        manager.createEpic(new Epic(6, "Test Create Epic 3", "Description", new ArrayList<>()));
        manager.createSubtask(new Subtask(7, "Test Subtask 1", "Description", 10, LocalDateTime.of(2024, 1, 27, 10, 0), 4));
        manager.createSubtask(new Subtask(8, "Test Subtask 2", "Description", 10, LocalDateTime.of(2024, 1, 27, 10, 30), 4));
        manager.createSubtask(new Subtask(9, "Test Subtask 3", "Description", 10, LocalDateTime.of(2024, 1, 27, 11, 0), 5));

        List<Subtask> subtasks = manager.getSubtasks();

        assertEquals(expectedSubtasks, subtasks);
    }
    @Test
    public void getSubtasksTestEmptyList() {
        TaskManager managerForGetSubtasks = Managers.getDefaultInMemoryTaskManager();
        assertTrue(managerForGetSubtasks.getAllTasks().isEmpty());
    }

    @Test
    public void getSubtasksByEpicIdTest() {
        manager.createEpic(new Epic(5, "Test Create Epic 2", "Description", new ArrayList<>()));
        manager.createSubtask(new Subtask(9, "Test Subtask 3", "Description", 10, LocalDateTime.of(2024, 1, 27, 11, 0), 5));
        List<Subtask> expectedSubtasksByEpicId = manager.getEpic(5).getSubtasks();
        List<Subtask> subtasksByEpicId = manager.getSubtasksByEpicId(5);
        assertEquals(expectedSubtasksByEpicId, subtasksByEpicId);
    }

    @Test
    public void removeTaskTest() {
        Task expectedTask = new Task(1, "Test Create Task 1", "Desription", 15, LocalDateTime.of(2024,1,29,20,0));
        manager.createTask(expectedTask);
        Task task = manager.getTask(1);
        assertEquals(expectedTask, task);
        manager.removeTask(1);
        assertNull(manager.getTask(1));
    }

    @Test
    public void removeEpicTest() {
        Epic expectedEpic = new Epic(10, "Test Create Epic 1", "Description", new ArrayList<>());
        Subtask subtask = new Subtask(11, "Test Subtask 1", "Description", 10, LocalDateTime.of(2024, 1, 27, 10, 0), 10);
        manager.createEpic(expectedEpic);
        manager.createSubtask(subtask);
        assertNotNull(manager.getEpic(10));
        assertFalse(manager.getSubtasksByEpicId(10).isEmpty());
        manager.removeEpic(10);
        assertNull(manager.getEpic(10));
        assertTrue(manager.getSubtasksByEpicId(10).isEmpty());
    }

    @Test
    public void removeSubtask() {
        manager.createEpic(new Epic(5, "Test Create Epic 2", "Description", new ArrayList<>()));
        Subtask expectedSubtask = new Subtask(10, "Test Subtask 1", "Description", 10, LocalDateTime.of(2024, 1, 27, 10, 0), 5);
        manager.createSubtask(expectedSubtask);
        Subtask subtask = manager.getSubtask(10);
        assertEquals(expectedSubtask, subtask);
        manager.removeSubtask(10);
        assertNull(manager.getSubtask(10));
    }

    @Test
    public void removeAllTasksTest() {
        manager.createTask(new Task(1, "Test Create Task 1", "Description", 15, LocalDateTime.of(2024,1,29,20,0)));
        manager.createEpic(new Epic(2, "Test Epic 1", "Description", new ArrayList<>()));
        manager.createSubtask(new Subtask(3, "Test Subtask 1", "Description", 10, LocalDateTime.of(2024, 1, 27, 10, 0), 2));
        manager.setId(manager.getMaxId());
        int expectedId = 3;
        int id = manager.getId();

        assertEquals(expectedId, id);
        assertNotNull(manager.getTask(1));
        assertNotNull(manager.getEpic(2));
        assertNotNull(manager.getSubtask(3));

        manager.removeAllTasks();

        int expectedIdAfterRemove = 0;
        int idAfterRemove = manager.getId();

        assertEquals(expectedIdAfterRemove, idAfterRemove);
        assertNull(manager.getTask(1));
        assertNull(manager.getEpic(2));
        assertNull(manager.getSubtask(3));
    }

    @Test
    public void getIdTest() {
        manager.createTask(new Task(1, "Test Create Task 1", "Description", 15, LocalDateTime.of(2024,1,29,20,0)));
        manager.setId(manager.getMaxId());
        int expectedId = 1;
        int id = manager.getId();
        assertEquals(expectedId, id);
    }

    @Test
    public void getMaxIdTest() {
        TaskManager managerForGetMaxId = Managers.getDefaultInMemoryTaskManager();
        managerForGetMaxId.createTask(new Task(150, "Test Create Task 1", "Description", 15, LocalDateTime.of(2024,1,29,20,0)));
        managerForGetMaxId.createTask(new Task(1, "Test Create Task 2", "Description", 15, LocalDateTime.of(2024,1,29,21,0)));
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
    public void getHistoryTest() {
        List<Task> expectedHistory = new ArrayList<>();
        expectedHistory.add(new Task(1, "Test Create Task 1", "Description", 15, LocalDateTime.of(2024,1,29,20,0)));
        expectedHistory.add(new Task(2, "Test Create Task 2", "Description", 15, LocalDateTime.of(2024,1,29,20,20)));
        expectedHistory.add(new Task(3, "Test Create Task 3", "Description", 15, LocalDateTime.of(2024,1,29,20,40)));

        manager.createTask(new Task(1, "Test Create Task 1", "Description", 15, LocalDateTime.of(2024,1,29,20,0)));
        manager.createTask(new Task(2, "Test Create Task 2", "Description", 15, LocalDateTime.of(2024,1,29,20,20)));
        manager.createTask(new Task(3, "Test Create Task 3", "Description", 15, LocalDateTime.of(2024,1,29,20,40)));

        manager.getTask(1);
        manager.getTask(2);
        manager.getTask(3);
        List<Task> tasksHistory = manager.getHistory();

        assertEquals(expectedHistory, tasksHistory);
    }

    @Test
    public void taskInProgressTest() {
        manager.createTask(new Task(1, "Test Create Task 1", "Description", 15, LocalDateTime.of(2024,1,29,20,0)));
        Task task = manager.getTask(1);
        Status statusTask = task.getStatus();
        Status beforeChangeStatus = Status.NEW;
        assertEquals(statusTask, beforeChangeStatus);
        task.setStatus(Status.IN_PROGRESS);
        statusTask = task.getStatus();
        Status afterChangeStatus = Status.IN_PROGRESS;
        assertEquals(statusTask, afterChangeStatus);
    }

    @Test
    public void taskIsDoneTest() {
        manager.createTask(new Task(1, "Test Create Task 1", "Description", 15, LocalDateTime.of(2024,1,29,20,0)));
        Task task = manager.getTask(1);
        Status statusTask = task.getStatus();
        Status beforeChangeStatus = Status.NEW;
        assertEquals(statusTask, beforeChangeStatus);
        task.setStatus(Status.DONE);
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
        manager.createTask(new Task(1, "Test1", "Description", 10, LocalDateTime.of(2024, 1, 26, 12, 0)));
        manager.createTask(new Task(2, "Test2", "Description", 15, LocalDateTime.of(2024, 1, 26, 13, 0)));
        manager.createTask(new Task(3, "Test3", "Description", 5, LocalDateTime.of(2024, 1, 26, 12, 16)));

        List<Task> expectedList = new ArrayList<>();
        expectedList.add(new Task(1, "Test1", "Description", 10, LocalDateTime.of(2024, 1, 26, 12, 0)));
        expectedList.add(new Task(3, "Test3", "Description", 5, LocalDateTime.of(2024, 1, 26, 12, 16)));
        expectedList.add(new Task(2, "Test2", "Description", 15, LocalDateTime.of(2024, 1, 26, 13, 0)));

        Set<Task> prioritizedList = manager.getPrioritizedTasks();

        assertArrayEquals(expectedList.toArray(), prioritizedList.toArray());
    }
}
