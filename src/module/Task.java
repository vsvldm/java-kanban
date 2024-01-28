package module;

import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    protected int id;
    protected TypeTask type;
    protected String title;
    protected String description;
    protected Status status;
    protected long duration;
    protected LocalDateTime startDateTime;

    public Task(int id, String title, String description) {
        this.id = id;
        this.type = TypeTask.TASK;
        this.title = title;
        this.description = description;
        this.status = Status.NEW;
    }

    public Task(int id, String title, String description, long duration, LocalDateTime startDateTime) {
        this.id = id;
        this.type = TypeTask.TASK;
        this.title = title;
        this.description = description;
        this.status = Status.NEW;
        this.duration = duration;
        this.startDateTime = startDateTime;
    }

    public Task(int id, TypeTask type, String title, String description, Status status) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.description = description;
        this.status = status;
    }

    public Task(int id,
                TypeTask type,
                String title,
                String description,
                Status status,
                long duration,
                LocalDateTime startDateTime) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.description = description;
        this.status = status;
        this.duration = duration;
        this.startDateTime = startDateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task)) return false;
        Task task = (Task) o;
        return id == task.id && duration == task.duration && type == task.type && Objects.equals(title, task.title) && Objects.equals(description, task.description) && status == task.status && Objects.equals(startDateTime, task.startDateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type, title, description, status, duration, startDateTime);
    }

    @Override
    public String toString() {
        return getId() + "," +
                getType().toString() + "," +
                getTitle() + "," +
                getStatus().toString() + "," +
                getStartDateTime() + "," +
                getDuration() + "," +
                getDescription();
    }

    public LocalDateTime getEndTime() {
        return startDateTime.plusMinutes(duration);
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public TypeTask getType() {
        return type;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setType(TypeTask type) {
        this.type = type;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getDuration() {
        return duration;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }
}
