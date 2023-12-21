import java.util.List;

public class Epic extends Task{
    private List<Integer> subtaskIds;

    public Epic(int id, String title, String description, List<Integer> subtaskIds) {
        super(id, title, description);
        this.subtaskIds = subtaskIds;
    }

    public Epic(int id, TypeTask type, String title, String description, Status status) {
        super(id, type, title, description, status);
    }

    public List<Integer> getSubtaskIds() {
        return subtaskIds;
    }
}