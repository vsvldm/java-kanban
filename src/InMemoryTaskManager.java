import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    private int id = 0;
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void removeAllTasks() {
        removeTasks();
        removeEpics();
        removeSubtasks();
        id = 0;
    }

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public Task getTask(int taskId) {
        if (tasks.containsKey(taskId)) {
            historyManager.add(tasks.get(taskId));
            return tasks.get(taskId);
        }
        return null;
    }

    @Override
    public Epic getEpic(int epicId) {
        if (epics.containsKey(epicId)) {
            historyManager.add(epics.get(epicId));
            return epics.get(epicId);
        }
        return null;
    }

    @Override
    public Subtask getSubtask(int subtaskId) {
        if (subtasks.containsKey(subtaskId)) {
            historyManager.add(subtasks.get(subtaskId));
            return subtasks.get(subtaskId);
        }
        return null;
    }

    @Override
    public void createTask(Task task) {
        task.type = TypeTask.TASK;
        task.status = Status.NEW;
        tasks.put(task.getId(), task);
    }

    @Override
    public void createEpic(Epic epic) {
        epic.type = TypeTask.EPIC;
        epic.status = Status.NEW;
        epics.put(epic.getId(), epic);
    }

    @Override
    public void createSubtask(int epicId, Subtask subtask) {
        if (epics.containsKey(epicId)) {
            subtask.type = TypeTask.SUBTASK;
            subtask.status = Status.NEW;
            subtasks.put(subtask.getId(), subtask);
            Epic epic = epics.get(epicId);
            epic.getSubtaskIds().add(subtask.getId());
            updateEpic(epic);
        }
    }

    @Override
    public void removeTask(int taskId) {
        tasks.remove(taskId);
    }

    @Override
    public void removeEpic(int epicId) {
        Epic removedEpic = epics.remove(epicId);
        if (removedEpic == null) {
            return;
        }
        for (Integer subtaskId : removedEpic.getSubtaskIds()) {
            removeSubtask(subtaskId);
        }
    }

    @Override
    public void removeSubtask(int subtaskId) {
        Subtask removedSubtask = subtasks.remove(subtaskId);
        if (removedSubtask == null) {
            return;
        }
        Epic epic = getEpics().get(removedSubtask.getEpicId());

        if (epic == null) {
            return;
        }

        epic.getSubtaskIds().remove((Integer) subtaskId);
    }

    @Override
    public List<Subtask> getSubtasksByEpic(int epicId) {
        Epic epic = getEpics().get(epicId);
        if (epic == null) {
            return null;
        }

        List<Subtask> subtasks = new ArrayList<>();

        for (Integer subtaskId : epic.getSubtaskIds()) {
            Subtask subtask = getSubtasks().get(subtaskId);
            if (subtask != null) {
                subtasks.add(subtask);
            }
        }

        return subtasks;
    }

    @Override
    public boolean updateTask(Task updatedTask) {
        if (!tasks.containsKey(updatedTask.getId())) {
            return false;
        }
        tasks.put(updatedTask.getId(), updatedTask);
        return true;
    }

    @Override
    public boolean updateEpic(Epic updatedEpic) {
        if (!epics.containsKey(updatedEpic.getId())) {
            return false;
        }
        epics.put(updatedEpic.getId(), updatedEpic);
        generateNewEpicStatus(updatedEpic.getId());
        return true;
    }

    @Override
    public boolean updateSubtask(Subtask updatedSubtask) {
        if (!subtasks.containsKey(updatedSubtask.getId())) {
            return false;
        }
        subtasks.put(updatedSubtask.getId(), updatedSubtask);
        generateNewEpicStatus(updatedSubtask.getEpicId());
        return true;
    }

    @Override
    public String toString() {
        return "Manager{" +
                "tasks=" + tasks +
                ", epics=" + epics +
                ", subtasks=" + subtasks +
                '}';
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public void taskInProgress(Task task) {
        task.status = Status.IN_PROGRESS;

    }

    @Override
    public void taskIsDone(Task task) {
        task.status = Status.DONE;
    }

    @Override
    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    private void generateNewEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return;
        }

        boolean onlyNewStatuses = true;
        boolean onlyDoneStatuses = true;

        for (Integer subtaskId : epic.getSubtaskIds()) {
            Subtask subtask = subtasks.get(subtaskId);
            if (subtask == null) {
                continue;
            }

            if (!subtask.getStatus().equals(Status.NEW)) {
                onlyNewStatuses = false;
            }

            if (!subtask.getStatus().equals(Status.DONE)) {
                onlyDoneStatuses = false;
            }

            if (!onlyNewStatuses && !onlyDoneStatuses) {
                break;
            }
        }

        Status newStatus;

        if (onlyNewStatuses) {
            newStatus = Status.NEW;
        } else if (onlyDoneStatuses) {
            newStatus = Status.DONE;
        } else {
            newStatus = Status.IN_PROGRESS;
        }

        epic.setStatus(newStatus);

    }

    private void removeSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubtaskIds().clear();
        }
    }

    private void removeEpics() {
        epics.clear();
        subtasks.clear();
    }

    private void removeTasks() {
        tasks.clear();
    }
}