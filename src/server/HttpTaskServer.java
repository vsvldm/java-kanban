package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import manager.FileBackedTasksManager;
import manager.Managers;
import manager.TaskManager;
import module.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.regex.Pattern;

public class HttpTaskServer {
    private static final int PORT = 8080;

    private final HttpServer server;
    private final Gson gson;
    private final TaskManager taskManager;

    public HttpTaskServer() throws IOException {
        this.taskManager = Managers.getFileBackedTasksManager();
        this.gson = Managers.getGson();
        this.server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        server.createContext("/tasks", this::tasks);
    }
    public HttpTaskServer(String filePath) throws IOException {
        this.taskManager = new FileBackedTasksManager(filePath);
        this.gson = Managers.getGson();
        this.server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        server.createContext("/tasks", this::tasks);
    }

    public void start() {
        System.out.println("Запускаем сервер на порту " + PORT);
        System.out.println("Открой в браузере http://localhost:" + PORT + "/");
        server.start();
    }

    public void stop() {
        System.out.println("Сервер остановлен.");
        server.stop(0);
    }

    private void tasks(HttpExchange httpExchange) throws IOException {
        try {
            String path = httpExchange.getRequestURI().toString();
            String requestMethod = httpExchange.getRequestMethod();
            switch(requestMethod) {
                case "GET":
                    if(Pattern.matches("^/tasks/task[?]id=\\d+$", path)){
                        String pathId = path.replaceFirst("/tasks/task[?]id=", "");
                        int id = parsePathId(pathId);
                        if (id != -1) {
                            String response = gson.toJson(taskManager.getTask(id));
                            httpExchange.sendResponseHeaders(200, 0);
                            httpExchange.getResponseBody().write(response.getBytes());
                            break;
                        } else {
                            System.out.println("Получен некорректный id = " + pathId);
                            httpExchange.sendResponseHeaders(405, 0);
                            break;
                        }
                    }
                    if(Pattern.matches("^/tasks/epic[?]id=\\d+$", path)){
                        String pathId = path.replaceFirst("/tasks/epic[?]id=", "");
                        int id = parsePathId(pathId);
                        if (id != -1) {
                            String response = gson.toJson(taskManager.getEpic(id));
                            httpExchange.sendResponseHeaders(200, 0);
                            httpExchange.getResponseBody().write(response.getBytes());
                            break;
                        } else {
                            System.out.println("Получен некорректный id = " + pathId);
                            httpExchange.sendResponseHeaders(405, 0);
                            break;
                        }
                    }
                    if(Pattern.matches("^/tasks/subtask[?]id=\\d+$", path)){
                        String pathId = path.replaceFirst("/tasks/subtask[?]id=", "");
                        int id = parsePathId(pathId);
                        if (id != -1) {
                            String response = gson.toJson(taskManager.getSubtask(id));
                            httpExchange.sendResponseHeaders(200, 0);
                            httpExchange.getResponseBody().write(response.getBytes());
                            break;
                        } else {
                            System.out.println("Получен некорректный id = " + pathId);
                            httpExchange.sendResponseHeaders(405, 0);
                            break;
                        }
                    }
                    if(Pattern.matches("^/tasks/subtask/epic[?]id=\\d+$", path)){
                        String pathId = path.replaceFirst("/tasks/subtask/epic[?]id=", "");
                        int id = parsePathId(pathId);
                        if (id != -1) {
                            String response = gson.toJson(taskManager.getSubtasksByEpicId(id));
                            httpExchange.sendResponseHeaders(200, 0);
                            httpExchange.getResponseBody().write(response.getBytes());
                            break;
                        } else {
                            System.out.println("Получен некорректный id = " + pathId);
                            httpExchange.sendResponseHeaders(405, 0);
                            break;
                        }
                    }
                    if(Pattern.matches("^/tasks/task$", path)) {
                        String response = gson.toJson(taskManager.getTasks());
                        httpExchange.sendResponseHeaders(200, 0);
                        httpExchange.getResponseBody().write(response.getBytes());
                        break;
                    }
                    if(Pattern.matches("^/tasks/epic$", path)) {
                        String response = gson.toJson(taskManager.getEpics());
                        httpExchange.sendResponseHeaders(200, 0);
                        httpExchange.getResponseBody().write(response.getBytes());
                        break;
                    }
                    if(Pattern.matches("^/tasks/subtask$", path)) {
                        String response = gson.toJson(taskManager.getSubtasks());
                        httpExchange.sendResponseHeaders(200, 0);
                        httpExchange.getResponseBody().write(response.getBytes());
                        break;
                    }
                    if(Pattern.matches("^/tasks/all$", path)) {
                        String response = gson.toJson(taskManager.getAllTasks());
                        httpExchange.sendResponseHeaders(200, 0);
                        httpExchange.getResponseBody().write(response.getBytes());
                        break;
                    }
                    if(Pattern.matches("^/tasks/history$", path)) {
                        String response = gson.toJson(taskManager.getHistory());
                        httpExchange.sendResponseHeaders(200, 0);
                        httpExchange.getResponseBody().write(response.getBytes());
                        break;
                    }
                    if(Pattern.matches("^/tasks/id$", path)) {
                        String response = gson.toJson(taskManager.getMaxId());
                        httpExchange.sendResponseHeaders(200, 0);
                        httpExchange.getResponseBody().write(response.getBytes());
                        break;
                    }
                    if(Pattern.matches("^/tasks$", path)) {
                        String response = gson.toJson(taskManager.getPrioritizedTasks());
                        httpExchange.sendResponseHeaders(200, 0);
                        httpExchange.getResponseBody().write(response.getBytes());
                        break;
                    }
                case "DELETE":
                    if(Pattern.matches("^/tasks/task[?]id=\\d+$", path)){
                        String pathId = path.replaceFirst("/tasks/task[?]id=", "");
                        int id = parsePathId(pathId);
                        if (id != -1) {
                            taskManager.removeTask(id);
                            System.out.println("Задача с id = " + id + " удалена." );
                            httpExchange.sendResponseHeaders(200, 0);
                            break;
                        } else {
                            System.out.println("Получен некорректный id = " + pathId);
                            httpExchange.sendResponseHeaders(405, 0);
                            break;
                        }
                    }
                    if(Pattern.matches("^/tasks/epic[?]id=\\d+$", path)){
                        String pathId = path.replaceFirst("/tasks/epic[?]id=", "");
                        int id = parsePathId(pathId);
                        if (id != -1) {
                            taskManager.removeEpic(id);
                            System.out.println("Эпик с id = " + id + " удален." );
                            httpExchange.sendResponseHeaders(200, 0);
                            break;
                        } else {
                            System.out.println("Получен некорректный id = " + pathId);
                            httpExchange.sendResponseHeaders(405, 0);
                            break;
                        }
                    }
                    if(Pattern.matches("^/tasks/subtask[?]id=\\d+$", path)){
                        String pathId = path.replaceFirst("/tasks/subtask[?]id=", "");
                        int id = parsePathId(pathId);
                        if (id != -1) {
                            taskManager.removeSubtask(id);
                            System.out.println("Подзадача с id = " + id + " удалена." );
                            httpExchange.sendResponseHeaders(200, 0);
                            break;
                        } else {
                            System.out.println("Получен некорректный id = " + pathId);
                            httpExchange.sendResponseHeaders(405, 0);
                            break;
                        }
                    }
                    if(Pattern.matches("^/tasks/all-tasks$", path)){
                        taskManager.removeAllTasks();
                        System.out.println("Все задачи удалены.");
                        httpExchange.sendResponseHeaders(200, 0);
                        break;
                    }
                case "POST":
                    if(Pattern.matches("^/tasks/task$", path)){
                        String taskString = new String(httpExchange.getRequestBody().readAllBytes());
                        if(taskString.isBlank()){
                            System.out.println("Пустое тело запроса.");
                            httpExchange.sendResponseHeaders(405, 0);
                            return;
                        }
                        Task task = gson.fromJson(taskString,Task.class);
                        if(!task.getType().equals(TypeTask.TASK)) {
                            System.out.println("Ожидался тип задачи TASK, а получили " + task.getType());
                            httpExchange.sendResponseHeaders(405, 0);
                            return;
                        }
                        taskManager.createTask(task);
                        httpExchange.sendResponseHeaders(200, 0);
                        break;
                    }
                    if(Pattern.matches("^/tasks/epic$", path)){
                        String epicString = new String(httpExchange.getRequestBody().readAllBytes());
                        if(epicString.isBlank()){
                            System.out.println("Пустое тело запроса.");
                            httpExchange.sendResponseHeaders(405, 0);
                            return;
                        }
                        Epic epic = gson.fromJson(epicString,Epic.class);
                        if(!epic.getType().equals(TypeTask.EPIC)) {
                            System.out.println("Ожидался тип задачи EPIC, а получили " + epic.getType());
                            httpExchange.sendResponseHeaders(405, 0);
                            return;
                        }
                        taskManager.createEpic(epic);
                        httpExchange.sendResponseHeaders(200, 0);
                    }
                    if(Pattern.matches("^/tasks/subtask$", path)){
                        String subtaskString = new String(httpExchange.getRequestBody().readAllBytes());
                        if(subtaskString.isBlank()){
                            System.out.println("Пустое тело запроса.");
                            httpExchange.sendResponseHeaders(405, 0);
                            return;
                        }
                        Subtask subtask = gson.fromJson(subtaskString,Subtask.class);
                        if(!subtask.getType().equals(TypeTask.SUBTASK)) {
                            System.out.println("Ожидался тип задачи SUBTASK, а получили " + subtask.getType());
                            httpExchange.sendResponseHeaders(405, 0);
                            return;
                        }
                        taskManager.createSubtask(subtask);
                        httpExchange.sendResponseHeaders(200, 0);
                    }
                    if(Pattern.matches("^/tasks/task/update$", path)){
                        String taskString = new String(httpExchange.getRequestBody().readAllBytes());
                        if(taskString.isBlank()){
                            System.out.println("Пустое тело запроса.");
                            httpExchange.sendResponseHeaders(405, 0);
                            return;
                        }
                        Task task = gson.fromJson(taskString,Task.class);
                        taskManager.updateTask(task);
                        httpExchange.sendResponseHeaders(200, 0);
                    }
                    if(Pattern.matches("^/tasks/epic/update$", path)){
                        String epicString = new String(httpExchange.getRequestBody().readAllBytes());
                        if(epicString.isBlank()){
                            System.out.println("Пустое тело запроса.");
                            httpExchange.sendResponseHeaders(405, 0);
                            return;
                        }
                        Epic epic = gson.fromJson(epicString,Epic.class);
                        taskManager.updateEpic(epic);
                        httpExchange.sendResponseHeaders(200, 0);
                    }
                    if(Pattern.matches("^/tasks/subtask/update$", path)){
                        String subtaskString = new String(httpExchange.getRequestBody().readAllBytes());
                        if(subtaskString.isBlank()){
                            System.out.println("Пустое тело запроса.");
                            httpExchange.sendResponseHeaders(405, 0);
                            return;
                        }
                        Subtask subtask = gson.fromJson(subtaskString,Subtask.class);
                        taskManager.createSubtask(subtask);
                        httpExchange.sendResponseHeaders(200, 0);
                    }
                    break;
                default:
                    System.out.println("Ждем методы GET, DELETE или POST, а получаем - " + requestMethod);
                    httpExchange.sendResponseHeaders(405, 0);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            httpExchange.close();
        }
    }

    private int parsePathId(String path) {
        try {
            return Integer.parseInt((path));
        } catch (NumberFormatException exception) {
            return -1;
        }
    }


}
