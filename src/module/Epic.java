package module;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class Epic extends Task{
    private final List<Subtask> subtasks;
    public Epic(int id,
                String title,
                String description,
                List<Subtask> subtasks) {
        super(id, title, description);
        this.type = TypeTask.EPIC;
        this.status = Status.NEW;
        this.subtasks = subtasks;
    }

    public Epic(int id,
                TypeTask type,
                String title,
                String description,
                Status status,
                List<Subtask> subtasks) {
        super(id, type, title, description, status);
        this.subtasks = subtasks;
    }

    @Override
    public Status getStatus() {
        boolean onlyNewStatuses = true;
        boolean onlyDoneStatuses = true;

        for (Subtask subtask : getSubtasks()) {
            if (subtask == null) {
                continue;
            }
            if (!subtask.getStatus().equals(Status.NEW)) {
                onlyNewStatuses = false;
            }
            if (!subtask.getStatus().equals(Status.DONE)) {
                onlyDoneStatuses = false;
            }
            if (!onlyNewStatuses && !onlyDoneStatuses) {
                break;
            }
        }
        Status newStatus;

        if (onlyNewStatuses) {
            newStatus = Status.NEW;
        } else if (onlyDoneStatuses) {
            newStatus = Status.DONE;
        } else {
            newStatus = Status.IN_PROGRESS;
        }
        return newStatus;
    }

    @Override
    public long getDuration() {
        long duration = 0;
        for (Subtask subtask : subtasks) {
            duration += subtask.getDuration();
        }
        return duration;
    }

    @Override
    public LocalDateTime getStartDateTime() {
        LocalDateTime startDateTime = null;
        for (Subtask subtask : subtasks) {
            LocalDateTime subtaskStartDateTime = subtask.getStartDateTime();
            if (startDateTime == null || subtaskStartDateTime.isBefore(startDateTime)) {
                startDateTime = subtaskStartDateTime;
            }
        }
        return startDateTime;
    }

    @Override
    public LocalDateTime getEndTime() {
        return getStartDateTime().plusMinutes(getDuration());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Epic)) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subtasks, epic.subtasks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtasks);
    }

    public List<Subtask> getSubtasks() {
        return subtasks;
    }
}