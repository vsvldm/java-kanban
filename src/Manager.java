import java.util.ArrayList;
import java.util.HashMap;

public class Manager {
    private int id = 0;
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();

    @Override
    public String toString() {
        return "Manager{" +
                "tasks=" + tasks.toString() +
                ", epics=" + epics.toString() +
                ", subtasks=" + subtasks.toString() +
                '}';
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

    public HashMap<Integer, Task> getTasks() {
        return tasks;
    }

    public HashMap<Integer, Epic> getEpics() {
        return epics;
    }

    public HashMap<Integer, Subtask> getSubtasks() {
        return subtasks;
    }

    public void createTask(String title, String description) {
        Task task = new Task(++id, title, description, "NEW");
        tasks.put(task.getId(), task);
    }

    public void createEpic(String title, String description) {
        Epic epic = new Epic(++id, title, description, "NEW", new ArrayList<>());
        epics.put(epic.getId(), epic);
    }

    public void createSubtask(int epicId, String title, String description) {
        if (epics.containsKey(epicId)) {
            Subtask subtask = new Subtask(++id, title, description, "NEW", epicId);
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
    }

    private void removeEpics() {
        epics.clear();
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

    public ArrayList<Subtask> getSubtasksByEpic(int epicId) {
        Epic epic = getEpic(epicId);
        if (epic == null) {
            return null;
        }

        ArrayList<Subtask> subtasks = new ArrayList<>();

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

        if (!newStatus.equals(epic.getStatus())) {
            Epic updatedEpic = new Epic(epicId, epic.getTitle(), epic.getDescription(), newStatus, epic.getSubtaskIds());
            updateEpic(updatedEpic);
        }
    }
}
