public class Task {
    protected int id;
    protected TypeTask type;
    protected String title;
    protected String description;
    protected Status status;

    public Task(int id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
    }

    public Task(int id, TypeTask type, String title, String description, Status status) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.description = description;
        this.status = status;
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

    @Override
    public String toString() {
        return "{" + id + "," +
                type.toString() + "," +
                title + "," +
                status.toString() + "," +
                description + "}";
    }
}
