package module;

import java.time.LocalDateTime;
import java.util.Objects;

public class Subtask extends Task {
    private final int epicId;

    public Subtask(int id,
                   String title,
                   String description,
                   long duration,
                   LocalDateTime startDateTime,
                   int epicId) {
        super(id, title, description, duration, startDateTime);
        this.type = TypeTask.SUBTASK;
        this.status = Status.NEW;
        this.epicId = epicId;
    }

    public Subtask(int id,
                   TypeTask type,
                   String title,
                   String description,
                   Status status,
                   long duration,
                   LocalDateTime startDateTime,
                   int epicId) {
        super(id, type, title, description, status, duration, startDateTime);
        this.epicId = epicId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Subtask)) return false;
        if (!super.equals(o)) return false;
        Subtask subtask = (Subtask) o;
        return epicId == subtask.epicId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId);
    }

    @Override
    public String toString() {
        return getId() + "," +
                getType().toString() + "," +
                getTitle() + "," +
                getStatus().toString() + "," +
                getStartDateTime() + "," +
                getDuration() + "," +
                getDescription() + "," +
                getEpicId();
    }

    public int getEpicId() {
        return epicId;
    }
}
