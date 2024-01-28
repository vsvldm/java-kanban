package manager;

import module.*;

import java.time.LocalDateTime;
import java.util.*;

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
    public void createTask(Task task) {
        tasks.put(task.getId(), task);
    }

    @Override
    public void createEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    @Override
    public void createSubtask(int epicId, Subtask subtask) {
        if (epics.containsKey(epicId)) {
            subtasks.put(subtask.getId(), subtask);
            Epic epic = epics.get(epicId);
            epic.getSubtasks().add(subtask);
            updateEpic(epic);
        }
    }

    @Override
    public int getId() {
        return id;
    }
    @Override
    public int getMaxId() {
        if(!getAllTasks().isEmpty()) {
            List<Integer> ids = new ArrayList<>();
            for (Task task : getAllTasks()) {
                ids.add(task.getId());
            }
            return Collections.max(ids);
        }
        return 0;
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
    public void removeTask(int taskId) {
        tasks.remove(taskId);
    }

    @Override
    public void removeEpic(int epicId) {
        Epic removedEpic = epics.remove(epicId);
        if (removedEpic == null) {
            return;
        }
        for (Subtask subtask : removedEpic.getSubtasks()) {
            removeSubtask(subtask.getId());
        }
    }

    @Override
    public void removeSubtask(int subtaskId) {
        Subtask removedSubtask = subtasks.remove(subtaskId);
        if (removedSubtask == null) {
            return;
        }
        Epic epic = epics.get(removedSubtask.getEpicId());

        if (epic == null) {
            return;
        }

        epic.getSubtasks().removeIf(subtask -> subtask.getId() == subtaskId);
    }

    @Override
    public List<Subtask> getSubtasksByEpicId(int epicId) {
        List<Subtask> subtasksByEpic = new ArrayList<>();

        if (epics.containsKey(epicId)) {
            Epic epic = epics.get(epicId);
            subtasksByEpic.addAll(epic.getSubtasks());
        }
        return subtasksByEpic;
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

        return true;
    }

    @Override
    public boolean updateSubtask(Subtask updatedSubtask) {
        if (!subtasks.containsKey(updatedSubtask.getId())) {
            return false;
        }
        subtasks.put(updatedSubtask.getId(), updatedSubtask);
        return true;
    }

    @Override
    public String toString() {
        return "Manager" + "\n" +
                "tasks=" + tasks.values() + "\n" +
                "epics=" + epics.values() + "\n" +
                "subtasks=" + subtasks.values();
    }

    @Override
    public  List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public void taskInProgress(Task task) {
        if(task != null) {
            task.setStatus(Status.IN_PROGRESS);
        }
    }

    @Override
    public void taskIsDone(Task task) {
        if(task != null) {
            task.setStatus(Status.DONE);
        }
    }

    @Override
    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    @Override
    public void setId(int id) {
        if(id >= getMaxId()) {
            this.id = id;
        }
    }

    @Override
    public void putTask(Task task) {
        if(task != null) {
            tasks.put(task.getId(), task);
        }
    }

    @Override
    public void putEpic(Epic epic) {
        if(epic != null) {
            epics.put(epic.getId(), epic);
        }
    }

    @Override
    public void putSubtask(Subtask subtask) {
        if(subtask != null) {
            subtasks.put(subtask.getId(), subtask);
        }
    }

    @Override
    public List<Task> getAllTasks() {
        allTasks.clear();
        allTasks.addAll(getTasks());
        allTasks.addAll(getEpics());
        allTasks.addAll(getSubtasks());
        return allTasks;
    }

    @Override
    public Set<Task> getPrioritizedTasks() {
        Set<Task> prioritizedTasks = new TreeSet<>((task1, task2) -> {
            if (task1.getStartDateTime() == null && task2.getStartDateTime() == null) {
                return 0;
            } else if (task1.getStartDateTime() == null && task2.getStartDateTime() != null) {
                return 1;
            } else if (task1.getStartDateTime() != null && task2.getStartDateTime() == null) {
                return -1;
            } else if (task1.getStartDateTime().isBefore(task2.getStartDateTime())) {
                return -1;
            } else if(task1.getStartDateTime().isAfter(task2.getStartDateTime())) {
                return 1;
            } else {
                return 0;
            }
        });
        prioritizedTasks.addAll(getTasks());
        prioritizedTasks.addAll(getSubtasks());

        return prioritizedTasks;
    }

    @Override
    public boolean isIntersection(LocalDateTime start, LocalDateTime end) {
        return getPrioritizedTasks().stream()
                .anyMatch(task -> start.isBefore(task.getEndTime())
                            && end.isAfter(task.getStartDateTime()));
    }

    private void removeSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubtasks().clear();
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