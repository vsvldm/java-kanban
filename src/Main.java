import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();
        HistoryManager historyManager = Managers.getDefaultHistory();
        Scanner sc = new Scanner(System.in);
        int id = taskManager.getId();

        while (true) {
            System.out.println("Введите команду:");
            System.out.println("1 - Создать задачу");
            System.out.println("2 - Создать эпик");
            System.out.println("3 - Создать подзадачу");
            System.out.println("4 - Удалить задачу");
            System.out.println("5 - Удалить эпик");
            System.out.println("6 - Обновить статус задачи");
            System.out.println("7 - Обновить статус эпика");
            System.out.println("8 - Работа методов get...");
            System.out.println("9 - Удалить все");
            System.out.println("10 - История");
            System.out.println("0 - Выход");

            int command = Integer.parseInt(sc.nextLine());

             if (command == 0) {
                 return;

             } else if (command == 1) {
                 System.out.println("Название задачи:");
                 String title = sc.nextLine();

                 System.out.println("Описане:");
                 String description = sc.nextLine();

                 taskManager.createTask(new Task(++id, title, description, "NEW"));
                 System.out.println(taskManager);

             } else if (command == 2) {
                 System.out.println("Название эпика:");
                 String title = sc.nextLine();

                 System.out.println("Описане:");
                 String description = sc.nextLine();

                 taskManager.createEpic(new Epic(++id, title, description, "NEW", new ArrayList<>()));

                 System.out.println(taskManager);

             } else if (command == 3) {
                 System.out.println("Введите ID эпика:");
                 int epicId = Integer.parseInt(sc.nextLine());

                 System.out.println("Название подзадачи:");
                 String title = sc.nextLine();

                 System.out.println("Описане:");
                 String description = sc.nextLine();
                 taskManager.createSubtask(epicId, new Subtask(++id, title, description, "NEW", epicId));
                 System.out.println(taskManager);

             } else if (command == 4) {
                 System.out.println("Введите ID задачи, которую хотите удалить:");
                 int taskId = Integer.parseInt(sc.nextLine());
                 taskManager.removeTask(taskId);
                 System.out.println(taskManager);

             } else if (command == 5) {
                 System.out.println("Введите ID эпика, который хотите удалить:");
                 int epicId = Integer.parseInt(sc.nextLine());

                 taskManager.removeEpic(epicId);
                 System.out.println(taskManager);
             } else if (command == 6) {
                 System.out.println("Введите ID задачи, которую хотите обновить:");
                 int taskId = Integer.parseInt(sc.nextLine());

                 Task task = taskManager.getTask(taskId);

                 System.out.println("Введите новый статус для задачи(NEW, IN_PROGRESS, DONE):");
                 task.status = sc.nextLine();

                 taskManager.updateTask(task);
                 System.out.println(taskManager);

             } else if (command == 7) {
                 System.out.println("Введите ID эпика, который хотите обновить:");
                 int epicId = Integer.parseInt(sc.nextLine());

                 List<Epic> epics = taskManager.getEpics();
                 for (Epic epic : epics) {
                     if(epic.id == epicId){
                         System.out.println(taskManager.getSubtasksByEpic(epicId).toString());

                         System.out.println("Введите ID подзадачи, которую вы сделали или готовы начать делать?");
                         int subtaskId = Integer.parseInt(sc.nextLine());

                         List<Subtask> subtasks = taskManager.getSubtasks();
                         for (Subtask subtask : subtasks) {
                             if (subtask.id == subtaskId) {
                                 System.out.println("Введите новый статус(NEW, IN_PROGRESS, DONE):");
                                 subtask.status = sc.nextLine();
                                 taskManager.updateSubtask(subtask);
                             }
                         }
                         taskManager.updateEpic(epic);
                     }
                 }
                 System.out.println(taskManager);

             } else if (command == 8) {
                 System.out.println("Посмтреть: 1 - Task, 2 - Epic, 3 - Subtask");
                 int choice = Integer.parseInt(sc.nextLine());

                 System.out.println("Введите ID задачи:");
                 int tasksId = Integer.parseInt(sc.nextLine());

                 switch(choice){
                     case 1:
                         System.out.println(taskManager.getTask(tasksId));
                         historyManager.add(taskManager.getTask(tasksId));
                         break;
                     case 2:
                         System.out.println(taskManager.getEpic(tasksId));
                         historyManager.add(taskManager.getEpic(tasksId));
                         break;
                     case 3:
                         System.out.println(taskManager.getSubtask(tasksId));
                         historyManager.add(taskManager.getSubtask(tasksId));
                         break;
                     default:
                         break;
                 }
             } else if (command == 9) {
                 taskManager.removeAllTasks();
                 System.out.println(taskManager);
             } else if (command == 10) {
                 System.out.println(historyManager.getHistory().toString());
             }
        }

    }
}
