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

    /*Я так понимаю, что это единственный способ связаться с вами.
    Не могу понять для чего мне добавлять в TaskManager метод getHistory(), если этим уже занимается HistoryManager?*/
}
