public class Subtask extends Task {
    private final int epicId;

    public Subtask(int id, String title, String description, String status, int epicId) {
        super(id, title, description, status);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "{" + id + "," +
                type.toString() + "," +
                title + "," +
                status.toString() + "," +
                description + "," +
                epicId + "}";
    }
}
