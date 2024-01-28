package module;

import org.junit.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class EpicTest {
    @Test
    public void shouldReturnStatusNewIfSubtaskNull() {
        Epic epic = getEpicForTests();

        epic.getSubtasks().clear();
        Status expectedStatus = Status.NEW;

        Status status = epic.getStatus();

        assertEquals(expectedStatus, status);
    }
    @Test
    public void shouldReturnStatusDoneIfSubtasksDone() {
        Epic epic = getEpicForTests();
        for (Subtask subtask : epic.getSubtasks()) {
            subtask.setStatus(Status.DONE);
        }

        Status expectedStatus = Status.DONE;

        Status status = epic.getStatus();

        assertEquals(expectedStatus, status);
    }

    @Test
    public void shouldReturnStatusInProgressIfSubtasksNewAndDone() {
        Epic epic = getEpicForTests();

        epic.getSubtasks().get(0).setStatus(Status.DONE);

        Status expectedStatus = Status.IN_PROGRESS;

        Status status = epic.getStatus();

        assertEquals(expectedStatus, status);
    }

    @Test
    public void shouldReturnStatusInProgressIfSubtasksInProgress() {
        Epic epic = getEpicForTests();

        for (Subtask subtask : epic.getSubtasks()) {
            subtask.setStatus(Status.IN_PROGRESS);
        }

        Status expectedStatus = Status.IN_PROGRESS;

        Status status = epic.getStatus();

        assertEquals(expectedStatus, status);
    }

    @Test
    public void getDurationTest() {
        Epic epic = getEpicForTests();
        long expectedDuration = 30;

        long duration = epic.getDuration();
        assertEquals(expectedDuration, duration);
    }

    @Test
    public void getStartDateTimeTest() {
        Epic epic = getEpicForTests();
        LocalDateTime expectedStartDateTime = LocalDateTime.of(2000, 1, 1, 0, 0);

        LocalDateTime startDateTime = epic.getStartDateTime();
        assertEquals(expectedStartDateTime, startDateTime);
    }

    @Test
    public void getEndTimeTest() {
        Epic epic = getEpicForTests();
        LocalDateTime expectedEndTime = LocalDateTime.of(2000, 1, 1, 0, 30);

        LocalDateTime endTime = epic.getEndTime();
        assertEquals(expectedEndTime, endTime);
    }

    private Epic getEpicForTests() {
        Epic epic = new Epic(1,"Test EPIC title", "Description Test EPIC",new ArrayList<>());
        epic.getSubtasks().add(new Subtask(2,
                "Test SUBTASK 1 title",
                "Desription Tset SUBTASK",
                15,
                LocalDateTime.of(2000, 1, 1, 0, 0), 1));
        epic.getSubtasks().add(new Subtask(3,
                "Test SUBTASK 2 title",
                "Desription Tset SUBTASK",
                15,
                LocalDateTime.of(2001, 1, 1, 0, 0), 1));
        return epic;
    }
}