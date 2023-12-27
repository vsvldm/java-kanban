import java.io.*;
import java.util.*;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private FileBackedTasksManager(HistoryManager historyManager,
                                  int id,
                                  Map<Integer, Task> tasks,
                                  Map<Integer, Epic> epics,
                                  Map<Integer, Subtask> subtasks) {
        super(historyManager);
        this.id = id;
        this.tasks = tasks;
        this.epics = epics;
        this.subtasks = subtasks;
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

    private String taskToString(Task task) {
        return task.toString();
    }

    private static Task stringToTask(String string) {
        String[] split = string.split(",");
        int id = Integer.parseInt(split[0]);
        TypeTask type = TypeTask.valueOf(split[1]);
        String title = split[2];
        Status status = Status.valueOf(split[3]);
        String description = split[4];
        if(type.equals(TypeTask.TASK)){
            return new Task(id, type, title, description, status);
        } else if(type.equals(TypeTask.EPIC)) {
            return new Epic(id, type, title, description, status, new ArrayList<>());
        } else {
            int epicId = Integer.parseInt(split[5]);
            return new Subtask(id, type, title, description, status, epicId);
        }
    }

    private static String historyToString(HistoryManager historyManager) {
        List<Integer> history = new ArrayList<>();
        for (Task task : historyManager.getHistory()) {
            history.add(task.id);
        }
        return history.toString().replace("[", "")
                .replace(" ", "")
                .replace("]", "");
    }

    private static List<Integer> stringToHistory(String string) {
        String[] split = string.split(",");
        List<Integer> history = new ArrayList<>();
        for (String s : split) {
            history.add(Integer.parseInt(s));
        }
        return history;
    }

    private static FileBackedTasksManager loadFromFile(File file) throws IOException {
        Map<Integer, Task> tasks = new HashMap<>();
        Map<Integer, Epic> epics = new HashMap<>();
        Map<Integer, Subtask> subtasks = new HashMap<>();
        List<String> tasksList = new ArrayList<>();
        HistoryManager historyManager = Managers.getDefaultHistory();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int i = 1;
            while ((line = reader.readLine()) != null) {
                if (i == 1){
                    i = 0;
                    continue;
                }
                if (!line.isEmpty()) {
                    tasksList.add(line);
                }
            }
            String historyString = tasksList.get(tasksList.size() - 1);
            tasksList.remove(tasksList.size() - 1);
            for (String taskString : tasksList) {
                Task task = stringToTask(taskString);
                if (task.type.equals(TypeTask.TASK)) {
                    tasks.put(task.id, task);
                } else if (task.type.equals(TypeTask.EPIC)) {
                    Epic epic = (Epic) task;
                    epics.put(epic.id, epic);
                } else {
                    Subtask subtask = (Subtask) task;
                    subtasks.put(subtask.id, subtask);
                    epics.get(subtask.getEpicId()).getSubtaskIds().add(subtask.id);
                }
            }
            List<Integer> historyList = stringToHistory(historyString);
            for (Integer taskId : historyList) {
                Task task = tasks.getOrDefault(taskId, null);
                Epic epic = epics.getOrDefault(taskId, null);
                Subtask subtask = subtasks.getOrDefault(taskId, null);
                if (task != null) {
                    historyManager.add(task);
                } else if (epic != null) {
                    historyManager.add(epic);
                } else {
                    historyManager.add(subtask);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new FileBackedTasksManager(historyManager, tasksList.size(), tasks, epics, subtasks);
    }

    private void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(("resources/tasks.csv")))) {
            writer.write("id,type,title,status,description,epic" + "\n");
            for (Task task : getAllTasks()) {
                writer.write(taskToString(task) + "\n");
            }
            writer.write("\n");
            writer.write(historyToString(getHistoryManager()));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


    public static void main(String[] args) {
        File file = new File("resources", "tasks.csv");
        try {
            TaskManager taskManagerFromFile = loadFromFile(file);
            Scanner sc = new Scanner(System.in);
            int id = taskManagerFromFile.getId();
            System.out.println(taskManagerFromFile.toString());
            System.out.println(taskManagerFromFile.getAllTasks().toString());
            System.out.println(taskManagerFromFile.getHistory().toString());

            while (true) {
                System.out.println("Введите команду:");
                System.out.println("1 - Создать задачу");
                System.out.println("2 - Создать эпик");
                System.out.println("3 - Создать подзадачу");
                System.out.println("4 - Удалить task");
                System.out.println("5 - Удалить epic");
                System.out.println("6 - Удалить subtask");
                System.out.println("7 - Обновить статус task");
                System.out.println("8 - Обновить статус epic");
                System.out.println("9 - Работа методов get...");
                System.out.println("10 - Удалить все");
                System.out.println("11 - Сохранить");
                System.out.println("0 - Выход");

                int command = Integer.parseInt(sc.nextLine());

                if (command == 0) {
                    return;

                } else if (command == 1) {
                    System.out.println("Название задачи:");
                    String title = sc.nextLine();

                    System.out.println("Описане:");
                    String description = sc.nextLine();

                    taskManagerFromFile.createTask(new Task(++id, title, description));
                    System.out.println(taskManagerFromFile);

                } else if (command == 2) {
                    System.out.println("Название эпика:");
                    String title = sc.nextLine();

                    System.out.println("Описане:");
                    String description = sc.nextLine();

                    taskManagerFromFile.createEpic(new Epic(++id, title, description, new ArrayList<>()));

                    System.out.println(taskManagerFromFile);

                } else if (command == 3) {
                    System.out.println("Введите ID эпика:");
                    int epicId = Integer.parseInt(sc.nextLine());

                    System.out.println("Название подзадачи:");
                    String title = sc.nextLine();

                    System.out.println("Описане:");
                    String description = sc.nextLine();
                    taskManagerFromFile.createSubtask(epicId, new Subtask(++id, title, description, epicId));
                    System.out.println(taskManagerFromFile);
                }  else if (command == 4) {
                    System.out.println("Введите ID задачи, которую хотите удалить:");
                    int taskId = Integer.parseInt(sc.nextLine());
                    taskManagerFromFile.removeTask(taskId);
                    System.out.println(taskManagerFromFile);

                } else if (command == 5) {
                    System.out.println("Введите ID эпика, который хотите удалить:");
                    int epicId = Integer.parseInt(sc.nextLine());

                    taskManagerFromFile.removeEpic(epicId);
                    System.out.println(taskManagerFromFile);
                } else if (command == 6) {
                    System.out.println("Введите ID подзадачи, которую хотите удалить:");
                    int subtaskId = Integer.parseInt(sc.nextLine());

                    taskManagerFromFile.removeSubtask(subtaskId);
                    for (Epic epic : taskManagerFromFile.getEpics()) {
                        for (Integer epicSubtaskId : epic.getSubtaskIds()) {
                            if(epicSubtaskId == subtaskId) {
                                epic.getSubtaskIds().remove(subtaskId);
                            }
                        }
                    }
                    System.out.println(taskManagerFromFile);

                } else if (command == 7) {
                    System.out.println("Введите ID задачи, которую хотите обновить:");
                    int taskId = Integer.parseInt(sc.nextLine());

                    List<Task> tasks = taskManagerFromFile.getTasks();
                    for (Task task : tasks) {
                        if(task.id == taskId) {
                            System.out.println("Выберите новый статус для задачи(1 - IN_PROGRESS, 2 - DONE):");

                            int choice = Integer.parseInt(sc.nextLine());

                            switch (choice) {
                                case 1:
                                    taskManagerFromFile.taskInProgress(task);
                                    break;
                                case 2:
                                    taskManagerFromFile.taskIsDone(task);
                                    break;
                                default:
                                    break;
                            }
                            taskManagerFromFile.updateTask(task);
                            System.out.println(taskManagerFromFile);
                        } else {
                            System.out.println("Нет задачи с таким ID!");
                        }
                    }

                } else if (command == 8) {
                    System.out.println("Введите ID эпика, который хотите обновить:");
                    int epicId = Integer.parseInt(sc.nextLine());

                    List<Epic> epics = taskManagerFromFile.getEpics();
                    for (Epic epic : epics) {
                        if(epic.id == epicId){
                            System.out.println(taskManagerFromFile.getSubtasksByEpic(epicId).toString());

                            System.out.println("Введите ID подзадачи, которую вы сделали или готовы начать делать?");
                            int subtaskId = Integer.parseInt(sc.nextLine());

                            List<Subtask> subtasks = taskManagerFromFile.getSubtasks();
                            for (Subtask subtask : subtasks) {
                                if (subtask.id == subtaskId) {
                                    System.out.println("Выберите новый статус для подзадачи(1 - IN_PROGRESS, 2 - DONE):");

                                    int choice = Integer.parseInt(sc.nextLine());

                                    switch (choice) {
                                        case 1:
                                            taskManagerFromFile.taskInProgress(subtask);
                                            break;
                                        case 2:
                                            taskManagerFromFile.taskIsDone(subtask);
                                            break;
                                        default:
                                            break;
                                    }
                                    taskManagerFromFile.updateSubtask(subtask);
                                }
                            }
                            taskManagerFromFile.updateEpic(epic);
                        }
                    }
                    System.out.println(taskManagerFromFile);

                } else if (command == 9) {
                    System.out.println("Посмтреть: 1 - Task, 2 - Epic, 3 - Subtask");
                    int choice = Integer.parseInt(sc.nextLine());

                    System.out.println("Введите ID задачи:");
                    int tasksId = Integer.parseInt(sc.nextLine());

                    switch (choice) {
                        case 1:
                            System.out.println(taskManagerFromFile.getTask(tasksId));
                            break;
                        case 2:
                            System.out.println(taskManagerFromFile.getEpic(tasksId));
                            break;
                        case 3:
                            System.out.println(taskManagerFromFile.getSubtask(tasksId));
                            break;
                        default:
                            break;
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}