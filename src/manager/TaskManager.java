package manager;

import module.Epic;
import module.Subtask;
import module.Task;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface TaskManager {
    void createTask(Task task);
    void createEpic(Epic epic);
    void createSubtask(Subtask subtask);
    List<Subtask> getSubtasksByEpicId(int epicId);
    boolean updateTask(Task updatedTask);
    boolean updateEpic(Epic updatedEpic);
    boolean updateSubtask(Subtask updatedSubtask);
    Task getTask(int taskId);
    Epic getEpic(int epicId);
    Subtask getSubtask(int subtaskId);
    List<Task> getAllTasks ();
    List<Task> getTasks();
    List<Epic> getEpics();
    List<Subtask> getSubtasks();
    void removeTask(int taskId);
    void removeEpic(int epicId);
    void removeSubtask(int subtaskId);
    int getMaxId();
    void removeAllTasks();
    int getId();
    void setId(int id);
    List<Task> getHistory();
    HistoryManager getHistoryManager();
    Set<Task> getPrioritizedTasks();
}
