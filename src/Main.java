import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Manager manager = new Manager();
        Scanner sc = new Scanner(System.in);
        int id = manager.getId();

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
            System.out.println("0 - Выход");

            int command = Integer.parseInt(sc.nextLine());

             if (command == 0) {
                 return;

             } else if (command == 1) {
                 System.out.println("Название задачи:");
                 String title = sc.nextLine();

                 System.out.println("Описане:");
                 String description = sc.nextLine();

                 manager.createTask(new Task(++id, title, description, "NEW"));
                 System.out.println(manager.toString());

             } else if (command == 2) {
                 System.out.println("Название эпика:");
                 String title = sc.nextLine();

                 System.out.println("Описане:");
                 String description = sc.nextLine();

                 manager.createEpic(new Epic(++id, title, description, "NEW", new ArrayList<>()));

                 System.out.println(manager.toString());

             } else if (command == 3) {
                 System.out.println("Введите ID эпика:");
                 int epicId = Integer.parseInt(sc.nextLine());

                 System.out.println("Название подзадачи:");
                 String title = sc.nextLine();

                 System.out.println("Описане:");
                 String description = sc.nextLine();
                 manager.createSubtask(epicId, new Subtask(++id, title, description, "NEW", epicId));
                 System.out.println(manager.toString());

             } else if (command == 4) {
                 System.out.println("Введите ID задачи, которую хотите удалить:");
                 int taskId = Integer.parseInt(sc.nextLine());
                 manager.removeTask(taskId);
                 System.out.println(manager.toString());

             } else if (command == 5) {
                 System.out.println("Введите ID эпика, который хотите удалить:");
                 int epicId = Integer.parseInt(sc.nextLine());

                 manager.removeEpic(epicId);
                 System.out.println(manager.toString());
             } else if (command == 6) {
                 System.out.println("Введите ID задачи, которую хотите обновить:");
                 int taskId = Integer.parseInt(sc.nextLine());

                 Task task = manager.getTasks().get(taskId);

                 System.out.println("Введите новый статус для задачи(NEW, IN_PROGRESS, DONE):");
                 task.status = sc.nextLine();

                 manager.updateTask(task);
                 System.out.println(manager.toString());

             } else if (command == 7) {
                 System.out.println("Введите ID эпика, который хотите обновить:");
                 int epicId = Integer.parseInt(sc.nextLine());

                 Epic epic = manager.getEpics().get(epicId);

                 System.out.println(manager.getSubtasksByEpic(epicId).toString());

                 System.out.println("Введите ID подзадачи, которую вы сделали или готовы начать делать?");
                 int subtaskId = Integer.parseInt(sc.nextLine());

                 Subtask subtask = manager.getSubtasks().get(subtaskId);

                 System.out.println("Введите новый статус(NEW, IN_PROGRESS, DONE):");
                 subtask.status = sc.nextLine();


                 manager.updateSubtask(subtask);
                 manager.updateEpic(epic);

                 System.out.println(manager.toString());

             } else if (command == 8) {
                 System.out.println(manager.getTasks());
                 System.out.println(manager.getEpics());
                 System.out.println(manager.getSubtasks());
             } else if (command == 9) {
                 manager.removeAllTasks();
                 System.out.println(manager.toString());
             }
        }

    }
}
