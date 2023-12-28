import java.util.List;

public interface TaskManager {
    void removeAllTasks();
    int getId();
    Task getTask(int taskId);
    Epic getEpic(int epicId);
    Subtask getSubtask(int subtaskId);
    List<Task> getTasks();
    List<Epic> getEpics();
    List<Subtask> getSubtasks();
    void createTask(Task task);
    void createEpic(Epic epic);
    void createSubtask(int epicId, Subtask subtask);
    void removeTask(int taskId);
    void removeEpic(int epicId);
    void removeSubtask(int subtaskId);
    List<Subtask> getSubtasksByEpic(int epicId);
    boolean updateTask(Task updatedTask);
    boolean updateEpic(Epic updatedEpic);
    boolean updateSubtask(Subtask updatedSubtask);
    List<Task> getHistory();
    void taskInProgress(Task task);
    void taskIsDone(Task task);
    HistoryManager getHistoryManager();
    List<Task> getAllTasks ();
    void setId(int id);
    void putTask(Task task);
    void putEpic(Epic epic);
    void putSubtask(Subtask subtask);
}
