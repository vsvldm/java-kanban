public class Subtask extends Task {
    private final int epicId;

    public Subtask(int id, String title, String description, int epicId) {
        super(id, title, description);
        this.epicId = epicId;
    }

    public Subtask(int id, TypeTask type, String title, String description, Status status, int epicId) {
        super(id, type, title, description, status);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return id + "," +
                type.toString() + "," +
                title + "," +
                status.toString() + "," +
                description + "," +
                epicId;
    }
}
