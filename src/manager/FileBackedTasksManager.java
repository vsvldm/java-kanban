package manager;

import module.*;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

public class FileBackedTasksManager extends InMemoryTaskManager {
    String path = "resources/tasks.csv";

    public FileBackedTasksManager(HistoryManager historyManager) {
        super(historyManager);
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void createSubtask(int epicId, Subtask subtask) {
        super.createSubtask(epicId, subtask);
        save();
    }

    @Override
    public void removeTask(int taskId) {
        super.removeTask(taskId);
        save();
    }

    @Override
    public void removeEpic(int epicId) {
        super.removeEpic(epicId);
        save();
    }

    @Override
    public void removeSubtask(int subtaskId) {
        super.removeSubtask(subtaskId);
        save();
    }

    @Override
    public boolean updateTask(Task updatedTask) {
        boolean isUpdateTask = super.updateTask(updatedTask);
        save();
        return isUpdateTask;
    }

    @Override
    public boolean updateEpic(Epic updatedEpic) {
        boolean isUpdateEpic = super.updateEpic(updatedEpic);
        save();
        return isUpdateEpic;
    }

    @Override
    public boolean updateSubtask(Subtask updatedSubtask) {
        boolean isUpdateSubtask = super.updateSubtask(updatedSubtask);
        save();
        return isUpdateSubtask;
    }


    @Override
    public Task getTask(int taskId) {
        Task task = super.getTask(taskId);
        save();
        return task;
    }

    @Override
    public Epic getEpic(int epicId) {
        Epic epic = super.getEpic(epicId);
        save();
        return epic;
    }

    @Override
    public Subtask getSubtask(int subtaskId) {
        Subtask subtask = super.getSubtask(subtaskId);
        save();
        return subtask;
    }

    private static Task stringToTask(String string) {
        String[] split = string.split(",");
        int id = Integer.parseInt(split[0]);
        TypeTask type = TypeTask.valueOf(split[1]);
        String title = split[2];
        Status status = Status.valueOf(split[3]);
        LocalDateTime startDateTime = null;
        if (!split[4].equals("null")) {
            startDateTime = LocalDateTime.parse(split[4]);
        }
        long duration = Long.parseLong(split[5]);
        String description = split[6];
        if (type.equals(TypeTask.TASK)) {
            return new Task(id, type, title, description, status, duration, startDateTime);
        } else if (type.equals(TypeTask.EPIC)) {
            return new Epic(id, type, title, description, status, new ArrayList<>());
        } else {
            int epicId = Integer.parseInt(split[7]);
            return new Subtask(id, type, title, description, status, duration, startDateTime, epicId);
        }
    }

    private static String historyToString(HistoryManager historyManager) {
        return historyManager.toString();
    }

    private static List<Integer> stringToHistory(String string) {
        List<Integer> history = new ArrayList<>();
        if (string != null) {
            String[] split = string.split(",");
            for (String s : split) {
                history.add(Integer.parseInt(s));
            }
        }
        return history;
    }

    public static FileBackedTasksManager loadFromFile(File file) {
        FileBackedTasksManager fbTaskManager = Managers.getFileBackedTasksManager();
        fbTaskManager.path = file.getPath();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            if (reader.readLine() != null) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.isEmpty()) {
                        break;
                    }
                    Task task = stringToTask(line);
                    if (task.getType().equals(TypeTask.TASK)) {
                        fbTaskManager.putTask(task);
                    } else if (task.getType().equals(TypeTask.EPIC)) {
                        Epic epic = (Epic) task;
                        fbTaskManager.putEpic(epic);
                    } else {
                        Subtask subtask = (Subtask) task;
                        fbTaskManager.putSubtask(subtask);
                        List<Epic> epicsList = fbTaskManager.getEpics();
                        for (Epic epic : epicsList) {
                            if (epic.getId() == subtask.getEpicId()) {
                                epic.getSubtasks().add(subtask);
                            }
                        }
                    }
                }
                if (line == null || line.isBlank()) {
                    String historyString = reader.readLine();
                    List<Integer> historyList = stringToHistory(historyString);
                    List<Task> allTasks = fbTaskManager.getAllTasks();
                    for (Integer taskId : historyList) {
                        for (Task task : allTasks) {
                            if (taskId.equals(task.getId())) {
                                fbTaskManager.historyManager.add(task);
                            }
                        }
                    }
                }
                fbTaskManager.setId(fbTaskManager.getMaxId());
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при загрузки файла!");
        }
        return fbTaskManager;
    }

    private void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            writer.write("id,type,title,status,start,duration,description,epic" + "\n");
            for (Task task : getAllTasks()) {
                writer.write(task.toString() + "\n");
            }
            writer.write("\n");
            writer.write(historyToString(getHistoryManager()));
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении данных");
        }
    }
}