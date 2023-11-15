import java.util.ArrayList;

public class Epic extends Task{
    ArrayList<Integer> subtaskIds;

    public Epic(int id, String title, String description, String status, ArrayList<Integer> subtaskIds) {
        super(id, title, description, status);
        this.subtaskIds = subtaskIds;
    }

    public ArrayList<Integer> getSubtaskIds() {
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
