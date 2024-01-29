package manager;

import module.Epic;
import module.Subtask;
import module.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {

    private final String tasksFilePath = "resources";
    private final String tasksFileName = "tasks-test.csv";

    private Path getTasksFileFullPath() {
        return Path.of(String.join("/", tasksFilePath, tasksFileName));
    }

    @AfterEach
    public void afterEach() throws IOException {
        Files.deleteIfExists(getTasksFileFullPath());
    }

    @BeforeEach
    public void beforeEach() throws IOException {
        Files.createFile(getTasksFileFullPath());
        manager = FileBackedTasksManager.loadFromFile(new File(tasksFilePath, tasksFileName));
    }

    @Test
    public void saveTestEmptyListTasks() throws IOException {
            List<String> string = Files.readAllLines(getTasksFileFullPath());
            assertTrue(string.isEmpty());
    }

    @Test
    public void saveTestCompletedListTasks() throws IOException {
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
        List<String> listFromFile = Files.readAllLines(getTasksFileFullPath());
        listFromFile.remove(0);
        String history = listFromFile.get(listFromFile.size() - 1); //
        listFromFile.remove(listFromFile.size() - 1);
        listFromFile.remove(listFromFile.size() - 1);

        String expectedHistory = manager.getHistoryManager().toString();
        assertEquals(expectedHistory, history);

        String expectedString = manager.getAllTasks().toString();
        assertEquals(expectedString, listFromFile.toString());
    }

    @Test
    public void saveTestCompletedListEpicWithoutSubtasks() throws IOException {
        Epic epic = new Epic(6, "Test Create Epic 3", "Description", new ArrayList<>());
        manager.createEpic(epic);
        List<String> listFromFile = Files.readAllLines(getTasksFileFullPath());
        listFromFile.remove(0);
        listFromFile.remove(listFromFile.size() - 1);
        String actualListFromFile = listFromFile.toString()
                .replace("[", "")
                .replace("]", "");
        String expectedString = epic.toString();

        assertEquals(expectedString, actualListFromFile);
    }

    @Test
    public void loadFromFileTest() throws IOException {
        manager.createTask(new Task(1, "Test Create Task 1", "Description", 15, LocalDateTime.of(2024,1,29,20,0)));
        manager.createEpic(new Epic(2, "Test Create Epic 3", "Description", new ArrayList<>()));
        manager.createSubtask(new Subtask(3, "Test Subtask 3", "Description", 10, LocalDateTime.of(2024, 1, 27, 11, 0), 2));
        List<String> listFromFile = Files.readAllLines(getTasksFileFullPath());
        listFromFile.remove(0);
        String[] splitListFromFile = listFromFile.toString().split(", ");
        String actualListFromFile = "tasks=" + splitListFromFile[0] + "]\n" + "epics=[" + splitListFromFile[1] + "]\n" + "subtasks=[" + splitListFromFile[2] + "]";

        String expectedManagerToString = manager.toString().replace("Manager\n", "");
        assertEquals(expectedManagerToString, actualListFromFile);
    }
}

