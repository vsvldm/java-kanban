import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Manager {
    private int id = 0;
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();

    public int getId() {
        return id;
    }

    public Task getTask(int taskId) {
        if (tasks.containsKey(taskId)) {
            return tasks.get(taskId);
        }
        return null;
    }

    public Epic getEpic(int epicId) {
        if (epics.containsKey(epicId)) {
            return epics.get(epicId);
        }
        return null;
    }

    public Subtask getSubtask(int subtaskId) {
        if (subtasks.containsKey(subtaskId)) {
            return subtasks.get(subtaskId);
        }
        return null;
    }

    public List<Task> getTasks() {
        List<Task> listTasks = new ArrayList<>(tasks.values());
        return listTasks;
    }

    public List<Epic> getEpics() {
        List<Epic> listEpics = new ArrayList<>(epics.values());
        return listEpics;
    }

    public List<Subtask> getSubtasks() {
        List<Subtask> listSubtasks = new ArrayList<>(subtasks.values());
        return listSubtasks;
    }

    public void createTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void createEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    public void createSubtask(int epicId, Subtask subtask) {
        if (epics.containsKey(epicId)) {
            subtasks.put(subtask.getId(), subtask);
            Epic epic = epics.get(epicId);
            epic.subtaskIds.add(subtask.getId());
            updateEpic(epic);
        }
    }

    public void removeAllTasks() {
        removeTasks();
        removeEpics();
        removeSubtasks();
        id = 0;
    }

    private void removeSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.subtaskIds.clear();
        }
    }

    private void removeEpics() {
        epics.clear();
        subtasks.clear();
    }

    public void removeTasks() {
        tasks.clear();
    }

    public void removeTask(int taskId) {
        tasks.remove(taskId);
    }

    public void removeEpic(int epicId) {
        Epic removedEpic = epics.remove(epicId);
        if (removedEpic == null) {
            return;
        }
        for (Integer subtaskId : removedEpic.subtaskIds) {
            removeSubtask(subtaskId);
        }
    }

    public void removeSubtask(int subtaskId) {
        Subtask removedSubtask = subtasks.remove(subtaskId);
        if (removedSubtask == null) {
            return;
        }

        Epic epic = getEpic(removedSubtask.getEpicId());
        if (epic == null) {
            return;
        }

        epic.getSubtaskIds().remove((Integer) subtaskId);
    }

    public List<Subtask> getSubtasksByEpic(int epicId) {
        Epic epic = getEpic(epicId);
        if (epic == null) {
            return null;
        }

        List<Subtask> subtasks = new ArrayList<>();

        for (Integer subtaskId : epic.subtaskIds) {
            Subtask subtask = getSubtask(subtaskId);
            if (subtask != null) {
                subtasks.add(subtask);
            }
        }

        return subtasks;
    }

    public boolean updateTask(Task updatedTask) {
        if (!tasks.containsKey(updatedTask.getId())) {
            return false;
        }
        tasks.put(updatedTask.getId(), updatedTask);
        return true;
    }

    public boolean updateEpic(Epic updatedEpic) {
        if (!epics.containsKey(updatedEpic.getId())) {
            return false;
        }
        epics.put(updatedEpic.getId(), updatedEpic);
        generateNewEpicStatus(updatedEpic.getId());
        return true;
    }

    public boolean updateSubtask(Subtask updatedSubtask) {
        if (!subtasks.containsKey(updatedSubtask.getId())) {
            return false;
        }
        subtasks.put(updatedSubtask.getId(), updatedSubtask);
        generateNewEpicStatus(updatedSubtask.getEpicId());
        return true;
    }

    private void generateNewEpicStatus(int epicId) {
        Epic epic = getEpic(epicId);
        if (epic == null) {
            return;
        }

        boolean onlyNewStatuses = true;
        boolean onlyDoneStatuses = true;

        for (Integer subtaskId : epic.subtaskIds) {
            Subtask subtask = getSubtask(subtaskId);
            if (subtask == null) {
                continue;
            }

            if (!subtask.getStatus().equals("NEW")) {
                onlyNewStatuses = false;
            }

            if (!subtask.getStatus().equals("DONE")) {
                onlyDoneStatuses = false;
            }

            if (!onlyNewStatuses && !onlyDoneStatuses) {
                break;
            }
        }

        String newStatus;

        if (onlyNewStatuses) {
            newStatus = "NEW";
        } else if (onlyDoneStatuses) {
            newStatus = "DONE";
        } else {
            newStatus = "IN_PROGRESS";
        }

        epic.setStatus(newStatus);

    }
    @Override
    public String toString() {
        return "Manager{" +
                "tasks=" + tasks.toString() +
                ", epics=" + epics.toString() +
                ", subtasks=" + subtasks.toString() +
                '}';
    }
}
