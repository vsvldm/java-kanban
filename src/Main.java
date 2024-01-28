import manager.FileBackedTasksManager;
import manager.TaskManager;
import module.Epic;
import module.Subtask;
import module.Task;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        File file = new File("resources", "tasks.csv");
        TaskManager taskManagerFromFile = FileBackedTasksManager.loadFromFile(file);
        Scanner sc = new Scanner(System.in);
        int id = taskManagerFromFile.getId();
        System.out.println(taskManagerFromFile);
        System.out.println(taskManagerFromFile.getHistory().toString());
        System.out.println(taskManagerFromFile.getPrioritizedTasks().toString());


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
            System.out.println("0 - Выход");

            int command = Integer.parseInt(sc.nextLine());

            if (command == 0) {
                return;

            } else if (command == 1) {
                System.out.println("Название задачи:");
                String title = sc.nextLine();

                System.out.println("Описане:");
                String description = sc.nextLine();

                System.out.println("Продолжительность задачи: ");
                long duration = Long.parseLong(sc.nextLine());

                System.out.println("Час старта задачи: ");
                int startHour = Integer.parseInt(sc.nextLine());
                System.out.println("Минута старта задачи: ");
                int startMinute = Integer.parseInt(sc.nextLine());
                System.out.println("День старта задачи: ");
                int startDay = Integer.parseInt(sc.nextLine());
                System.out.println("Месяц старта задачи: ");
                int startMonth = Integer.parseInt(sc.nextLine());
                System.out.println("Год старта задачи: ");
                int startYear = Integer.parseInt(sc.nextLine());

                LocalDateTime startDateTime = LocalDateTime.of(startYear, startMonth, startDay, startHour, startMinute);
                LocalDateTime endDateTime = startDateTime.plusMinutes(duration);

                if(taskManagerFromFile.isIntersection(startDateTime, endDateTime)) {
                    System.out.println("Данная задача может пересекаться по времени с существующей задачей!");
                } else {
                    taskManagerFromFile.createTask(new Task(++id, title, description, duration, startDateTime));
                }
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

                System.out.println("Продолжительность задачи: ");
                long duration = Long.parseLong(sc.nextLine());

                System.out.println("Час старта задачи: ");
                int startHour = Integer.parseInt(sc.nextLine());
                System.out.println("Минута старта задачи: ");
                int startMinute = Integer.parseInt(sc.nextLine());
                System.out.println("День старта задачи: ");
                int startDay = Integer.parseInt(sc.nextLine());
                System.out.println("Месяц старта задачи: ");
                int startMonth = Integer.parseInt(sc.nextLine());
                System.out.println("Год старта задачи: ");
                int startYear = Integer.parseInt(sc.nextLine());

                LocalDateTime startDateTime = LocalDateTime.of(startYear, startMonth, startDay, startHour, startMinute);
                LocalDateTime endDateTime = startDateTime.plusMinutes(duration);

                if(taskManagerFromFile.isIntersection(startDateTime, endDateTime)) {
                    System.out.println("Данная задача может пересекаться по времени с существующей задачей!");
                } else {
                    taskManagerFromFile.createSubtask(epicId, new Subtask(++id, title, description, duration, startDateTime, epicId));
                }

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
                    for (Subtask subtaskByEpic : epic.getSubtasks()) {
                        if(subtaskByEpic.getId() == subtaskId) {
                            epic.getSubtasks().removeIf(subtask -> subtask.getId() == subtaskId);
                        }
                    }
                }
                System.out.println(taskManagerFromFile);

            } else if (command == 7) {
                System.out.println("Введите ID задачи, которую хотите обновить:");
                int taskId = Integer.parseInt(sc.nextLine());

                List<Task> tasks = taskManagerFromFile.getTasks();
                for (Task task : tasks) {
                    if(task.getId() == taskId) {
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
                    if(epic.getId() == epicId){
                        System.out.println(epic.getSubtasks().toString());

                        System.out.println("Введите ID подзадачи, которую вы сделали или готовы начать делать?");
                        int subtaskId = Integer.parseInt(sc.nextLine());

                        List<Subtask> subtasks = taskManagerFromFile.getSubtasks();
                        for (Subtask subtask : subtasks) {
                            if (subtask.getId() == subtaskId) {
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
    }
}
