package manager;

import module.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    private HistoryManager historyManager;

    @BeforeEach
    public void beforeEach() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    void addTest() {
        assertTrue(historyManager.toString().isBlank());
        historyManager.add(new Task(1, "Test Create Task 1", "Desription"));
        List<Task> history = historyManager.getHistory();
        assertNotNull(history);
        assertEquals(1, history.size());
        historyManager.add(new Task(2, "Test Create Task 2", "Desription"));
        historyManager.add(new Task(1, "Test Create Task 1", "Desription"));
        history = historyManager.getHistory();
        assertEquals(2, history.size());
    }

    @Test
    void remove() {
        historyManager.add(new Task(1, "Test Create Task 1", "Desription"));
        historyManager.remove(1);
        assertTrue(historyManager.getHistory().isEmpty());
        historyManager.add(new Task(1, "Test Create Task 1", "Desription"));
        historyManager.add(new Task(2, "Test Create Task 1", "Desription"));
        historyManager.add(new Task(3, "Test Create Task 1", "Desription"));
        historyManager.add(new Task(4, "Test Create Task 1", "Desription"));
        historyManager.add(new Task(5, "Test Create Task 1", "Desription"));
        String expectedString = "1,2,3,4,5";
        assertEquals(expectedString, historyManager.toString());
        historyManager.remove(3);
        expectedString = "1,2,4,5";
        assertEquals(expectedString, historyManager.toString());
        historyManager.remove(1);
        expectedString = "2,4,5";
        assertEquals(expectedString, historyManager.toString());
        historyManager.remove(5);
        expectedString = "2,4";
        assertEquals(expectedString, historyManager.toString());
    }

    @Test
    void getHistory() {
        historyManager.add(new Task(1, "Test Create Task 1", "Desription"));
        assertEquals(1, historyManager.getHistory().size());
    }
}