import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    protected int id = 0;
    protected Map<Integer, Task> tasks;
    protected Map<Integer, Epic> epics;
    protected Map<Integer, Subtask> subtasks;
    protected List<Task> allTasks;
    protected HistoryManager historyManager;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.tasks  = new HashMap<>();
        this.epics = new HashMap<>();
        this.subtasks = new HashMap<>();
        this.historyManager = historyManager;
        this.allTasks = new ArrayList<>();
    }

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
        if (epics.containsKey(epicId)) {
            Epic epic = epics.get(epicId);

            List<Subtask> subtasks = new ArrayList<>();

            for (Integer subtaskId : epic.getSubtaskIds()) {
                if (epics.containsKey(epicId)) {
                Subtask subtask = subtasks.get(subtaskId);
                }
            }
            return subtasks;
        } else {
            return null;
        }
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
        return "Manager" + "\n" +
                "tasks=" + tasks.values() + "\n" +
                "epics=" + epics.values() + "\n" +
                "subtasks=" + subtasks.values() + "\n";
    }

    @Override
    public  List<Task> getHistory() {
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
    public  HistoryManager getHistoryManager() {
        return historyManager;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public void putTask(Task task) {
        tasks.put(task.id, task);
    }

    @Override
    public void putEpic(Epic epic) {
        epics.put(epic.id, epic);
    }

    @Override
    public void putSubtask(Subtask subtask) {
        subtasks.put(subtask.id, subtask);
    }

    @Override
    public List<Task> getAllTasks() {
        allTasks.clear();
        allTasks.addAll(tasks.values());
        allTasks.addAll(epics.values());
        allTasks.addAll(subtasks.values());
        return allTasks;
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