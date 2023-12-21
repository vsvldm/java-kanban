import java.util.List;

public class Epic extends Task{
    private List<Integer> subtaskIds;

    public Epic(int id, String title, String description, List<Integer> subtaskIds) {
        super(id, title, description);
        this.subtaskIds = subtaskIds;
    }

    public List<Integer> getSubtaskIds() {
        return subtaskIds;
    }
}