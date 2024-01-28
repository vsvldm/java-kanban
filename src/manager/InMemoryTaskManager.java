package manager;

import module.*;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected int id = 0;
    protected Map<Integer, Task> tasks;
    protected Map<Integer, Epic> epics;
    protected Map<Integer, Subtask> subtasks;
    protected HistoryManager historyManager;
    protected Set<Task> prioritizeTasks;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.tasks  = new HashMap<>();
        this.epics = new HashMap<>();
        this.subtasks = new HashMap<>();
        this.historyManager = historyManager;
        this.prioritizeTasks = new TreeSet<>(Comparator.comparing(Task::getStartDateTime));
    }

    @Override
    public void createTask(Task task) {
        if(task != null){
            if(!isIntersection(task)) {
                tasks.put(task.getId(), task);
                prioritizeTasks.add(task);
            } else {
                System.out.println("Найдено пересечение временных отрезков!!! Задача не добавлена!");
            }
        }
    }

    @Override
    public void createEpic(Epic epic) {
        if(epic != null) {
            epics.put(epic.getId(), epic);
        }
    }

    @Override
    public void createSubtask(Subtask subtask) {
        if (subtask != null && epics.containsKey(subtask.getEpicId())) {
            if(!isIntersection(subtask)) {
                subtasks.put(subtask.getId(), subtask);
                prioritizeTasks.add(subtask);
                Epic epic = epics.get(subtask.getEpicId());
                epic.getSubtasks().add(subtask);
                updateEpic(epic);
            } else {
                System.out.println("Найдено пересечение временных отрезков!!! Задача не добавлена!");
            }
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
        if(!getAllTasks().isEmpty()){
            List<Integer> ids = new ArrayList<>();
            for (Task task : getAllTasks()) {
                ids.add(task.getId());
            }
            if(ids.contains(taskId)) {
                tasks.remove(taskId);
                prioritizeTasks.removeIf(task -> task.getId() == taskId);
            }
        }
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
        prioritizeTasks.removeIf(subtask -> subtask.getId() == subtaskId);
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
    public List<Task> getAllTasks() {
        List<Task> allTasks = new ArrayList<>();
        allTasks.addAll(getTasks());
        allTasks.addAll(getEpics());
        allTasks.addAll(getSubtasks());
        return allTasks;
    }

    @Override
    public Set<Task> getPrioritizedTasks() {
        return prioritizeTasks;
    }

    private boolean isIntersection(Task beingCheckedTask) {
        return getPrioritizedTasks().stream()
.anyMatch(task -> beingCheckedTask.getStartDateTime().isBefore(task.getEndTime())
                            && beingCheckedTask.getEndTime().isAfter(task.getStartDateTime()));
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