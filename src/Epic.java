import java.util.List;

public class Epic extends Task{
    private List<Integer> subtaskIds;

    public Epic(int id, String title, String description, String status, List<Integer> subtaskIds) {
        super(id, title, description, status);
        this.subtaskIds = subtaskIds;
    }

    public List<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subtaskIds=" + subtaskIds.toString() +
                ", id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
