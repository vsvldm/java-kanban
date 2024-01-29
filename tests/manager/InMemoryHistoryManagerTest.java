package manager;

import module.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
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
        historyManager.add(new Task(1, "Test Create Task 1", "Description", 15, LocalDateTime.of(2024,1,29,20,0)));
        List<Task> history = historyManager.getHistory();
        assertNotNull(history);
        assertEquals(1, history.size());
        historyManager.add(new Task(2, "Test Create Task 2", "Description", 15, LocalDateTime.of(2024,1,29,20,40)));
        historyManager.add(new Task(1, "Test Create Task 1", "Description", 15, LocalDateTime.of(2024,1,29,20,0)));
        history = historyManager.getHistory();
        assertEquals(2, history.size());
    }

    @Test
    void remove() {
        historyManager.add(new Task(1, "Test Create Task 1", "Description", 15, LocalDateTime.of(2024,1,29,16,0)));
        historyManager.remove(1);
        assertTrue(historyManager.getHistory().isEmpty());
        historyManager.add(new Task(1, "Test Create Task 1", "Description", 15, LocalDateTime.of(2024,1,29,16,0)));
        historyManager.add(new Task(2, "Test Create Task 2", "Description", 15, LocalDateTime.of(2024,1,29,17,0)));
        historyManager.add(new Task(3, "Test Create Task 3", "Description", 15, LocalDateTime.of(2024,1,29,18,0)));
        historyManager.add(new Task(4, "Test Create Task 4", "Description", 15, LocalDateTime.of(2024,1,29,19,0)));
        historyManager.add(new Task(5, "Test Create Task 5", "Description", 15, LocalDateTime.of(2024,1,29,20,0)));
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
        historyManager.add(new Task(1, "Test Create Task 1", "Description", 15, LocalDateTime.of(2024,1,29,20,0)));
        assertEquals(1, historyManager.getHistory().size());
    }
}