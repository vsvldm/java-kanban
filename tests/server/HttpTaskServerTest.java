package server;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import manager.HistoryManager;
import manager.InMemoryHistoryManager;
import manager.Managers;
import module.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
class HttpTaskServerTest {
    private HttpTaskServer server;
    private HttpClient client;
    private Gson gson;
    private final String tasksFilePath = "resources";
    private final String tasksFileName = "tasks-test.csv";

    private Path getTasksFileFullPath() {
        return Path.of(String.join("/", tasksFilePath, tasksFileName));
    }
    @BeforeEach
    public void beforeEach() throws IOException {
        gson = Managers.getGson();
        Files.createFile(getTasksFileFullPath());
        server = new HttpTaskServer(getTasksFileFullPath().toString());
        server.start();
        client = HttpClient.newHttpClient();
    }

    @AfterEach
    public void afterEach() throws IOException {
        Files.deleteIfExists(getTasksFileFullPath());
        server.stop();
    }

    @Test
    public void testGetTask() throws IOException, InterruptedException {
        URI createUrl = URI.create("http://localhost:8080/tasks/task");
        Task task = new Task(1, "Test Create Task 1", "Description", 15, LocalDateTime.of(2024,1,29,20,0));
        String json = gson.toJson(task);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest createRequest = HttpRequest.newBuilder()
                .uri(createUrl)
                .POST(body)
                .build();
        client.send(createRequest, HttpResponse.BodyHandlers.ofString());


        URI getUrl = URI.create("http://localhost:8080/tasks/task?id=1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(getUrl)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Task actualTask = gson.fromJson(response.body(), Task.class);

        assertEquals(task.toString(), actualTask.toString());

    }

    @Test
    public void testGetEpic() throws IOException, InterruptedException {
        URI createEpicUrl = URI.create("http://localhost:8080/tasks/epic");
        Epic epic = new Epic(4, "Test Create Epic 1", "Description", new ArrayList<>());
        String jsonEpic = gson.toJson(epic);
        final HttpRequest.BodyPublisher bodyEpic = HttpRequest.BodyPublishers.ofString(jsonEpic);
        HttpRequest createEpicRequest = HttpRequest.newBuilder().uri(createEpicUrl).POST(bodyEpic).build();
        client.send(createEpicRequest, HttpResponse.BodyHandlers.ofString());


        URI getUrl = URI.create("http://localhost:8080/tasks/epic?id=4");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(getUrl)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Epic actualEpic = gson.fromJson(response.body(), Epic.class);

        assertEquals(epic.toString(), actualEpic.toString());

    }

    @Test
    public void getSubtaskTest() throws IOException, InterruptedException {
        URI createEpicUrl = URI.create("http://localhost:8080/tasks/epic");
        Epic epic = new Epic(4, "Test Create Epic 1", "Description", new ArrayList<>());
        String jsonEpic = gson.toJson(epic);
        final HttpRequest.BodyPublisher bodyEpic = HttpRequest.BodyPublishers.ofString(jsonEpic);
        HttpRequest createEpicRequest = HttpRequest.newBuilder().uri(createEpicUrl).POST(bodyEpic).build();
        client.send(createEpicRequest, HttpResponse.BodyHandlers.ofString());

        URI createSubtaskUrl = URI.create("http://localhost:8080/tasks/subtask");
        Subtask subtask = new Subtask(7, "Test Subtask 1", "Description", 10, LocalDateTime.of(2024, 1, 27, 10, 0), 4);
        String jsonSubtask = gson.toJson(subtask);
        final HttpRequest.BodyPublisher bodySubtask = HttpRequest.BodyPublishers.ofString(jsonSubtask);
        HttpRequest createSubtaskRequest = HttpRequest.newBuilder().uri(createSubtaskUrl).POST(bodySubtask).build();
        client.send(createSubtaskRequest, HttpResponse.BodyHandlers.ofString());


        URI getUrl = URI.create("http://localhost:8080/tasks/subtask?id=7");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(getUrl)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Subtask actualTask = gson.fromJson(response.body(), Subtask.class);

        assertEquals(subtask.toString(), actualTask.toString());

    }
    @Test
    public void testGetEpicSubtasks() throws IOException, InterruptedException {

        URI createEpicUrl = URI.create("http://localhost:8080/tasks/epic");
        Epic epic = new Epic(4, "Test Create Epic 1", "Description", new ArrayList<>());
        String jsonEpic = gson.toJson(epic);
        final HttpRequest.BodyPublisher bodyEpic = HttpRequest.BodyPublishers.ofString(jsonEpic);
        HttpRequest createEpicRequest = HttpRequest.newBuilder().uri(createEpicUrl).POST(bodyEpic).build();
        client.send(createEpicRequest, HttpResponse.BodyHandlers.ofString());

        URI createSubtaskUrl = URI.create("http://localhost:8080/tasks/subtask");
        Subtask subtask = new Subtask(7, "Test Subtask 1", "Description", 10, LocalDateTime.of(2024, 1, 27, 10, 0), 4);
        String jsonSubtask = gson.toJson(subtask);
        final HttpRequest.BodyPublisher bodySubtask = HttpRequest.BodyPublishers.ofString(jsonSubtask);
        HttpRequest createSubtaskRequest = HttpRequest.newBuilder().uri(createSubtaskUrl).POST(bodySubtask).build();
        client.send(createSubtaskRequest, HttpResponse.BodyHandlers.ofString());


        URI getUrl = URI.create("http://localhost:8080/tasks/subtask/epic?id=4");
        HttpRequest request = HttpRequest.newBuilder().uri(getUrl).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Type subtasksType = new  TypeToken<List<Subtask>>(){}.getType();
        List<Subtask> actualSubtasks = gson.fromJson(response.body(), subtasksType);
        List<Subtask> expectedSubtasksList = List.of(subtask);

        assertEquals(expectedSubtasksList, actualSubtasks);

    }

    @Test
    public void getTasksTest() throws IOException, InterruptedException {
        URI createTaskUrl = URI.create("http://localhost:8080/tasks/task");
        Task task = new Task(1, "Test Create Task 1", "Description", 15, LocalDateTime.of(2024,1,29,20,0));
        String jsonTask = gson.toJson(task);
        final HttpRequest.BodyPublisher bodyTask = HttpRequest.BodyPublishers.ofString(jsonTask);
        HttpRequest createTaskRequest = HttpRequest.newBuilder().uri(createTaskUrl).POST(bodyTask).build();
        client.send(createTaskRequest, HttpResponse.BodyHandlers.ofString());

        URI createTaskTwoUrl = URI.create("http://localhost:8080/tasks/task");
        Task taskTwo = new Task(2, "Test Create Task 2", "Description", 15, LocalDateTime.of(2024,1,30,20,0));
        String jsonTaskTwo = gson.toJson(taskTwo);
        final HttpRequest.BodyPublisher bodyTaskTwo = HttpRequest.BodyPublishers.ofString(jsonTaskTwo);
        HttpRequest createTaskTwoRequest = HttpRequest.newBuilder().uri(createTaskTwoUrl).POST(bodyTaskTwo).build();
        client.send(createTaskTwoRequest, HttpResponse.BodyHandlers.ofString());

        URI getUrl = URI.create("http://localhost:8080/tasks/task");
        HttpRequest request = HttpRequest.newBuilder().uri(getUrl).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Type tasksType = new  TypeToken<List<Task>>(){}.getType();
        List<Task> actualTasks = gson.fromJson(response.body(), tasksType);
        List<Task> expectedTasksList = List.of(task, taskTwo);

        assertEquals(expectedTasksList, actualTasks);
    }

    @Test
    public void getEpicsTest() throws IOException, InterruptedException {
        URI createEpicUrl = URI.create("http://localhost:8080/tasks/epic");
        Epic epic = new Epic(4, "Test Create Epic 1", "Description", new ArrayList<>());
        String jsonEpic = gson.toJson(epic);
        final HttpRequest.BodyPublisher bodyEpic = HttpRequest.BodyPublishers.ofString(jsonEpic);
        HttpRequest createEpicRequest = HttpRequest.newBuilder().uri(createEpicUrl).POST(bodyEpic).build();
        client.send(createEpicRequest, HttpResponse.BodyHandlers.ofString());

        URI createEpicTwoUrl = URI.create("http://localhost:8080/tasks/epic");
        Epic epicTwo = new Epic(5, "Test Create Epic 2", "Description", new ArrayList<>());
        String jsonEpicTwo = gson.toJson(epicTwo);
        final HttpRequest.BodyPublisher bodyEpicTwo = HttpRequest.BodyPublishers.ofString(jsonEpicTwo);
        HttpRequest createEpicTwoRequest = HttpRequest.newBuilder().uri(createEpicTwoUrl).POST(bodyEpicTwo).build();
        client.send(createEpicTwoRequest, HttpResponse.BodyHandlers.ofString());

        URI getUrl = URI.create("http://localhost:8080/tasks/epic");
        HttpRequest request = HttpRequest.newBuilder().uri(getUrl).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Type epicsType = new  TypeToken<List<Epic>>(){}.getType();
        List<Epic> actualEpics = gson.fromJson(response.body(), epicsType);
        List<Epic> expectedEpicsList = List.of(epic, epicTwo);

        assertEquals(expectedEpicsList, actualEpics);
    }

    @Test
    public void getSubtasksTest() throws IOException, InterruptedException {
        URI createEpicUrl = URI.create("http://localhost:8080/tasks/epic");
        Epic epic = new Epic(4, "Test Create Epic 1", "Description", new ArrayList<>());
        String jsonEpic = gson.toJson(epic);
        final HttpRequest.BodyPublisher bodyEpic = HttpRequest.BodyPublishers.ofString(jsonEpic);
        HttpRequest createEpicRequest = HttpRequest.newBuilder().uri(createEpicUrl).POST(bodyEpic).build();
        client.send(createEpicRequest, HttpResponse.BodyHandlers.ofString());

        URI createSubtaskUrl = URI.create("http://localhost:8080/tasks/subtask");
        Subtask subtask = new Subtask(7, "Test Subtask 1", "Description", 10, LocalDateTime.of(2024, 1, 27, 10, 0), 4);
        String jsonSubtask = gson.toJson(subtask);
        final HttpRequest.BodyPublisher bodySubtask = HttpRequest.BodyPublishers.ofString(jsonSubtask);
        HttpRequest createSubtaskRequest = HttpRequest.newBuilder().uri(createSubtaskUrl).POST(bodySubtask).build();
        client.send(createSubtaskRequest, HttpResponse.BodyHandlers.ofString());

        URI createSubtaskTwoUrl = URI.create("http://localhost:8080/tasks/subtask");
        Subtask subtaskTwo = new Subtask(8, "Test Subtask 2", "Description", 10, LocalDateTime.of(2024, 1, 27, 11, 0), 4);
        String jsonSubtaskTwo = gson.toJson(subtaskTwo);
        final HttpRequest.BodyPublisher bodySubtaskTwo = HttpRequest.BodyPublishers.ofString(jsonSubtaskTwo);
        HttpRequest createSubtaskTwoRequest = HttpRequest.newBuilder().uri(createSubtaskTwoUrl).POST(bodySubtaskTwo).build();
        client.send(createSubtaskTwoRequest, HttpResponse.BodyHandlers.ofString());

        URI getUrl = URI.create("http://localhost:8080/tasks/subtask");
        HttpRequest request = HttpRequest.newBuilder().uri(getUrl).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Type subtasksType = new  TypeToken<List<Subtask>>(){}.getType();
        List<Subtask> actualSubtasks = gson.fromJson(response.body(), subtasksType);
        List<Subtask> expectedSubtasksList = List.of(subtask, subtaskTwo);

        assertEquals(expectedSubtasksList, actualSubtasks);
    }

    @Test
    public void getAllTasksTest() throws IOException, InterruptedException {
        URI createTaskUrl = URI.create("http://localhost:8080/tasks/task");
        Task task = new Task(1, "Test Create Task 1", "Description", 15, LocalDateTime.of(2024,1,29,20,0));
        String jsonTask = gson.toJson(task);
        final HttpRequest.BodyPublisher bodyTask = HttpRequest.BodyPublishers.ofString(jsonTask);
        HttpRequest createTaskRequest = HttpRequest.newBuilder().uri(createTaskUrl).POST(bodyTask).build();
        client.send(createTaskRequest, HttpResponse.BodyHandlers.ofString());

        URI createEpicUrl = URI.create("http://localhost:8080/tasks/epic");
        Epic epic = new Epic(2, "Test Create Epic 1", "Description", new ArrayList<>());
        String jsonEpic = gson.toJson(epic);
        final HttpRequest.BodyPublisher bodyEpic = HttpRequest.BodyPublishers.ofString(jsonEpic);
        HttpRequest createEpicRequest = HttpRequest.newBuilder().uri(createEpicUrl).POST(bodyEpic).build();
        client.send(createEpicRequest, HttpResponse.BodyHandlers.ofString());

        URI createSubtaskUrl = URI.create("http://localhost:8080/tasks/subtask");
        Subtask subtask = new Subtask(3, "Test Subtask 1", "Description", 10, LocalDateTime.of(2024, 1, 27, 10, 0), 2);
        String jsonSubtask = gson.toJson(subtask);
        final HttpRequest.BodyPublisher bodySubtask = HttpRequest.BodyPublishers.ofString(jsonSubtask);
        HttpRequest createSubtaskRequest = HttpRequest.newBuilder().uri(createSubtaskUrl).POST(bodySubtask).build();
        client.send(createSubtaskRequest, HttpResponse.BodyHandlers.ofString());

        URI getUrl = URI.create("http://localhost:8080/tasks/all");
        HttpRequest request = HttpRequest.newBuilder().uri(getUrl).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Type tasksType = new TypeToken<List<Task>>(){}.getType();
        List<Task> actualTasks = gson.fromJson(response.body(), tasksType);
        List<Task> expectedTasksList = List.of(task, epic, subtask);

        assertEquals(expectedTasksList.size(), actualTasks.size());
    }

    @Test
    public void getHistoryTest() throws IOException, InterruptedException {
        URI createUrl = URI.create("http://localhost:8080/tasks/task");
        Task taskOneCreate = new Task(1, "Test Create Task 1", "Description", 15, LocalDateTime.of(2024,1,29,20,0));
        String json = gson.toJson(taskOneCreate);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest createRequest = HttpRequest.newBuilder().uri(createUrl).POST(body).build();
        client.send(createRequest, HttpResponse.BodyHandlers.ofString());

        URI createTaskTwoUrl = URI.create("http://localhost:8080/tasks/task");
        Task taskTwoCreate = new Task(2, "Test Create Task 2", "Description", 15, LocalDateTime.of(2024,1,30,20,0));
        String jsonTaskTwo = gson.toJson(taskTwoCreate);
        final HttpRequest.BodyPublisher bodyTaskTwo = HttpRequest.BodyPublishers.ofString(jsonTaskTwo);
        HttpRequest createTaskTwoRequest = HttpRequest.newBuilder().uri(createTaskTwoUrl).POST(bodyTaskTwo).build();
        client.send(createTaskTwoRequest, HttpResponse.BodyHandlers.ofString());

        URI getUrl = URI.create("http://localhost:8080/tasks/task?id=1");
        HttpRequest request = HttpRequest.newBuilder().uri(getUrl).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Task taskOne = gson.fromJson(response.body(), Task.class);

        URI getTaskTwoUrl = URI.create("http://localhost:8080/tasks/task?id=2");
        HttpRequest requestTaskTwo = HttpRequest.newBuilder().uri(getTaskTwoUrl).GET().build();
        HttpResponse<String> responseTaskTwo = client.send(requestTaskTwo, HttpResponse.BodyHandlers.ofString());
        Task taskTwo = gson.fromJson(responseTaskTwo.body(), Task.class);

        URI getHistoryUrl = URI.create("http://localhost:8080/tasks/history");
        HttpRequest requestHistory = HttpRequest.newBuilder().uri(getHistoryUrl).GET().build();
        HttpResponse<String> responseHistory = client.send(requestHistory, HttpResponse.BodyHandlers.ofString());

        Type tasksType = new TypeToken<List<Task>>(){}.getType();
        List<Task> historyFromJson = gson.fromJson(responseHistory.body(), tasksType);
        List<Integer> actualHistoryList = new ArrayList<>();
        for (Task taskFromJson : historyFromJson) {
            int id = taskFromJson.getId();
            actualHistoryList.add(id);
        }
        String actualHistory = actualHistoryList.toString().replaceFirst(" ", "");

        HistoryManager expectedHistoryManager = new InMemoryHistoryManager();
        expectedHistoryManager.add(taskOne);
        expectedHistoryManager.add(taskTwo);
        List<HistoryManager> expectedHistoryList = List.of(expectedHistoryManager);

        assertEquals(expectedHistoryList.toString(), actualHistory);
    }

    @Test
    public void getIdTest() throws IOException, InterruptedException {
        URI createUrl = URI.create("http://localhost:8080/tasks/task");
        Task task = new Task(1, "Test Create Task 1", "Description", 15, LocalDateTime.of(2024,1,29,20,0));
        String json = gson.toJson(task);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest createRequest = HttpRequest.newBuilder().uri(createUrl).POST(body).build();
        client.send(createRequest, HttpResponse.BodyHandlers.ofString());

        URI getIdUrl = URI.create("http://localhost:8080/tasks/id");
        HttpRequest requestId = HttpRequest.newBuilder().uri(getIdUrl).GET().build();
        HttpResponse<String> responseId = client.send(requestId, HttpResponse.BodyHandlers.ofString());

        int actualId = gson.fromJson(responseId.body(), Integer.class);

        assertEquals(1, actualId);
    }

    @Test
    public void getPrioritizedTasks() throws IOException, InterruptedException {
        URI createTaskUrl = URI.create("http://localhost:8080/tasks/task");
        Task task = new Task(1, "Test Create Task 1", "Description", 15, LocalDateTime.of(2024,1,29,20,0));
        String jsonTask = gson.toJson(task);
        final HttpRequest.BodyPublisher bodyTask = HttpRequest.BodyPublishers.ofString(jsonTask);
        HttpRequest createTaskRequest = HttpRequest.newBuilder().uri(createTaskUrl).POST(bodyTask).build();
        client.send(createTaskRequest, HttpResponse.BodyHandlers.ofString());

        URI createTaskTwoUrl = URI.create("http://localhost:8080/tasks/task");
        Task taskTwo = new Task(2, "Test Create Task 2", "Description", 15, LocalDateTime.of(2024,1,27,20,0));
        String jsonTaskTwo = gson.toJson(taskTwo);
        final HttpRequest.BodyPublisher bodyTaskTwo = HttpRequest.BodyPublishers.ofString(jsonTaskTwo);
        HttpRequest createTaskTwoRequest = HttpRequest.newBuilder().uri(createTaskTwoUrl).POST(bodyTaskTwo).build();
        client.send(createTaskTwoRequest, HttpResponse.BodyHandlers.ofString());

        URI getUrl = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(getUrl).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Type tasksType = new TypeToken<Set<Task>>(){}.getType();
        Set<Task> actualPrioritizedTasks = gson.fromJson(response.body(), tasksType);

        Set<Task> expectedPrioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartDateTime));
        expectedPrioritizedTasks.add(task);
        expectedPrioritizedTasks.add(taskTwo);

        assertEquals(expectedPrioritizedTasks, actualPrioritizedTasks);
    }

    @Test
    public void removeTaskTest() throws IOException, InterruptedException {
        URI createTaskUrl = URI.create("http://localhost:8080/tasks/task");
        Task task = new Task(1, "Test Create Task 1", "Description", 15, LocalDateTime.of(2024,1,29,20,0));
        String jsonTask = gson.toJson(task);
        final HttpRequest.BodyPublisher bodyTask = HttpRequest.BodyPublishers.ofString(jsonTask);
        HttpRequest createTaskRequest = HttpRequest.newBuilder().uri(createTaskUrl).POST(bodyTask).build();
        client.send(createTaskRequest, HttpResponse.BodyHandlers.ofString());

        URI getUrl = URI.create("http://localhost:8080/tasks/task?id=1");
        HttpRequest request = HttpRequest.newBuilder().uri(getUrl).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Task actualTask = gson.fromJson(response.body(), Task.class);

        assertEquals(task.toString(), actualTask.toString());

        URI deleteUrl = URI.create("http://localhost:8080/tasks/task?id=1");
        HttpRequest requestDelete = HttpRequest.newBuilder().uri(deleteUrl).DELETE().build();
        client.send(requestDelete, HttpResponse.BodyHandlers.ofString());

        URI getUrlTwo = URI.create("http://localhost:8080/tasks/task?id=1");
        HttpRequest requestTwo = HttpRequest.newBuilder().uri(getUrlTwo).GET().build();
        HttpResponse<String> responseTwo = client.send(requestTwo, HttpResponse.BodyHandlers.ofString());
        Task actualValue = gson.fromJson(responseTwo.body(), Task.class);

        assertNull(actualValue);
    }

    @Test
    public void removeEpicTest() throws IOException, InterruptedException {
        URI createEpicUrl = URI.create("http://localhost:8080/tasks/epic");
        Epic epic = new Epic(4, "Test Create Epic 1", "Description", new ArrayList<>());
        String jsonEpic = gson.toJson(epic);
        final HttpRequest.BodyPublisher bodyEpic = HttpRequest.BodyPublishers.ofString(jsonEpic);
        HttpRequest createEpicRequest = HttpRequest.newBuilder().uri(createEpicUrl).POST(bodyEpic).build();
        client.send(createEpicRequest, HttpResponse.BodyHandlers.ofString());

        URI getUrl = URI.create("http://localhost:8080/tasks/epic?id=4");
        HttpRequest request = HttpRequest.newBuilder().uri(getUrl).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Epic actualEpic = gson.fromJson(response.body(), Epic.class);

        assertEquals(epic.toString(), actualEpic.toString());

        URI deleteUrl = URI.create("http://localhost:8080/tasks/epic?id=4");
        HttpRequest requestDelete = HttpRequest.newBuilder().uri(deleteUrl).DELETE().build();
        client.send(requestDelete, HttpResponse.BodyHandlers.ofString());

        URI getUrlTwo = URI.create("http://localhost:8080/tasks/epic?id=4");
        HttpRequest requestTwo = HttpRequest.newBuilder().uri(getUrlTwo).GET().build();
        HttpResponse<String> responseTwo = client.send(requestTwo, HttpResponse.BodyHandlers.ofString());
        Epic actualValue = gson.fromJson(responseTwo.body(), Epic.class);

        assertNull(actualValue);
    }

    @Test
    public void removeSubtaskTest() throws IOException, InterruptedException {
        URI createEpicUrl = URI.create("http://localhost:8080/tasks/epic");
        Epic epic = new Epic(4, "Test Create Epic 1", "Description", new ArrayList<>());
        String jsonEpic = gson.toJson(epic);
        final HttpRequest.BodyPublisher bodyEpic = HttpRequest.BodyPublishers.ofString(jsonEpic);
        HttpRequest createEpicRequest = HttpRequest.newBuilder().uri(createEpicUrl).POST(bodyEpic).build();
        client.send(createEpicRequest, HttpResponse.BodyHandlers.ofString());

        URI createSubtaskUrl = URI.create("http://localhost:8080/tasks/subtask");
        Subtask subtask = new Subtask(7, "Test Subtask 1", "Description", 10, LocalDateTime.of(2024, 1, 27, 10, 0), 4);
        String jsonSubtask = gson.toJson(subtask);
        final HttpRequest.BodyPublisher bodySubtask = HttpRequest.BodyPublishers.ofString(jsonSubtask);
        HttpRequest createSubtaskRequest = HttpRequest.newBuilder().uri(createSubtaskUrl).POST(bodySubtask).build();
        client.send(createSubtaskRequest, HttpResponse.BodyHandlers.ofString());

        URI getUrl = URI.create("http://localhost:8080/tasks/subtask?id=7");
        HttpRequest request = HttpRequest.newBuilder().uri(getUrl).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Subtask actualTask = gson.fromJson(response.body(), Subtask.class);

        assertEquals(subtask.toString(), actualTask.toString());

        URI deleteUrl = URI.create("http://localhost:8080/tasks/subtask?id=7");
        HttpRequest requestDelete = HttpRequest.newBuilder().uri(deleteUrl).DELETE().build();
        client.send(requestDelete, HttpResponse.BodyHandlers.ofString());

        URI getUrlTwo = URI.create("http://localhost:8080/tasks/subtask?id=7");
        HttpRequest requestTwo = HttpRequest.newBuilder().uri(getUrlTwo).GET().build();
        HttpResponse<String> responseTwo = client.send(requestTwo, HttpResponse.BodyHandlers.ofString());
        Subtask actualValue = gson.fromJson(responseTwo.body(), Subtask.class);

        assertNull(actualValue);
    }

    @Test
    public void removeAllTasksTest() throws IOException, InterruptedException {
        URI createTaskUrl = URI.create("http://localhost:8080/tasks/task");
        Task task = new Task(1, "Test Create Task 1", "Description", 15, LocalDateTime.of(2024,1,29,20,0));
        String jsonTask = gson.toJson(task);
        final HttpRequest.BodyPublisher bodyTask = HttpRequest.BodyPublishers.ofString(jsonTask);
        HttpRequest createTaskRequest = HttpRequest.newBuilder().uri(createTaskUrl).POST(bodyTask).build();
        client.send(createTaskRequest, HttpResponse.BodyHandlers.ofString());

        URI createEpicUrl = URI.create("http://localhost:8080/tasks/epic");
        Epic epic = new Epic(2, "Test Create Epic 1", "Description", new ArrayList<>());
        String jsonEpic = gson.toJson(epic);
        final HttpRequest.BodyPublisher bodyEpic = HttpRequest.BodyPublishers.ofString(jsonEpic);
        HttpRequest createEpicRequest = HttpRequest.newBuilder().uri(createEpicUrl).POST(bodyEpic).build();
        client.send(createEpicRequest, HttpResponse.BodyHandlers.ofString());

        URI createSubtaskUrl = URI.create("http://localhost:8080/tasks/subtask");
        Subtask subtask = new Subtask(3, "Test Subtask 1", "Description", 10, LocalDateTime.of(2024, 1, 27, 10, 0), 2);
        String jsonSubtask = gson.toJson(subtask);
        final HttpRequest.BodyPublisher bodySubtask = HttpRequest.BodyPublishers.ofString(jsonSubtask);
        HttpRequest createSubtaskRequest = HttpRequest.newBuilder().uri(createSubtaskUrl).POST(bodySubtask).build();
        client.send(createSubtaskRequest, HttpResponse.BodyHandlers.ofString());

        URI getUrl = URI.create("http://localhost:8080/tasks/all");
        HttpRequest request = HttpRequest.newBuilder().uri(getUrl).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Type tasksType = new TypeToken<List<Task>>(){}.getType();
        List<Task> actualTasks = gson.fromJson(response.body(), tasksType);
        List<Task> expectedTasksList = List.of(task, epic, subtask);

        assertEquals(expectedTasksList.size(), actualTasks.size());

        URI deleteUrl = URI.create("http://localhost:8080/tasks/all-tasks");
        HttpRequest requestDelete = HttpRequest.newBuilder().uri(deleteUrl).DELETE().build();
        client.send(requestDelete, HttpResponse.BodyHandlers.ofString());

        URI getUrlTwo = URI.create("http://localhost:8080/tasks/all");
        HttpRequest requestTwo = HttpRequest.newBuilder().uri(getUrlTwo).GET().build();
        HttpResponse<String> responseTwo = client.send(requestTwo, HttpResponse.BodyHandlers.ofString());

        Type type = new TypeToken<List<Task>>(){}.getType();
        List<Task> actualEmptyTasks = gson.fromJson(responseTwo.body(), type);

        assertTrue(actualEmptyTasks.isEmpty());
    }

    @Test
    public void createTaskTest() throws IOException, InterruptedException {
        URI getEmptyUrl = URI.create("http://localhost:8080/tasks/task?id=1");
        HttpRequest requestEmpty = HttpRequest.newBuilder().uri(getEmptyUrl).GET().build();
        HttpResponse<String> responseEmpty = client.send(requestEmpty, HttpResponse.BodyHandlers.ofString());
        Task actualEmpty = gson.fromJson(responseEmpty.body(), Task.class);

        assertNull(actualEmpty);

        URI createTaskUrl = URI.create("http://localhost:8080/tasks/task");
        Task task = new Task(1, "Test Create Task 1", "Description", 15, LocalDateTime.of(2024,1,29,20,0));
        String jsonTask = gson.toJson(task);
        final HttpRequest.BodyPublisher bodyTask = HttpRequest.BodyPublishers.ofString(jsonTask);
        HttpRequest createTaskRequest = HttpRequest.newBuilder().uri(createTaskUrl).POST(bodyTask).build();
        client.send(createTaskRequest, HttpResponse.BodyHandlers.ofString());

        URI getUrl = URI.create("http://localhost:8080/tasks/task?id=1");
        HttpRequest request = HttpRequest.newBuilder().uri(getUrl).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Task actualTask = gson.fromJson(response.body(), Task.class);

        assertEquals(task.toString(), actualTask.toString());
    }

    @Test
    public void createEpicTest() throws IOException, InterruptedException {
        URI getEmptyUrl = URI.create("http://localhost:8080/tasks/epic?id=4");
        HttpRequest requestEmpty = HttpRequest.newBuilder().uri(getEmptyUrl).GET().build();
        HttpResponse<String> responseEmpty = client.send(requestEmpty, HttpResponse.BodyHandlers.ofString());
        Epic actualEmpty = gson.fromJson(responseEmpty.body(), Epic.class);

        assertNull(actualEmpty);

        URI createEpicUrl = URI.create("http://localhost:8080/tasks/epic");
        Epic epic = new Epic(4, "Test Create Epic 1", "Description", new ArrayList<>());
        String jsonEpic = gson.toJson(epic);
        final HttpRequest.BodyPublisher bodyEpic = HttpRequest.BodyPublishers.ofString(jsonEpic);
        HttpRequest createEpicRequest = HttpRequest.newBuilder().uri(createEpicUrl).POST(bodyEpic).build();
        client.send(createEpicRequest, HttpResponse.BodyHandlers.ofString());


        URI getUrl = URI.create("http://localhost:8080/tasks/epic?id=4");
        HttpRequest request = HttpRequest.newBuilder().uri(getUrl).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Epic actualEpic = gson.fromJson(response.body(), Epic.class);

        assertEquals(epic.toString(), actualEpic.toString());
    }
    @Test
    public void createSubtaskTest() throws IOException, InterruptedException {
        URI getEmptyUrl = URI.create("http://localhost:8080/tasks/subtask?id=7");
        HttpRequest requestEmpty = HttpRequest.newBuilder().uri(getEmptyUrl).GET().build();
        HttpResponse<String> responseEmpty = client.send(requestEmpty, HttpResponse.BodyHandlers.ofString());
        Subtask actualEmpty = gson.fromJson(responseEmpty.body(), Subtask.class);

        assertNull(actualEmpty);

        URI createEpicUrl = URI.create("http://localhost:8080/tasks/epic");
        Epic epic = new Epic(4, "Test Create Epic 1", "Description", new ArrayList<>());
        String jsonEpic = gson.toJson(epic);
        final HttpRequest.BodyPublisher bodyEpic = HttpRequest.BodyPublishers.ofString(jsonEpic);
        HttpRequest createEpicRequest = HttpRequest.newBuilder().uri(createEpicUrl).POST(bodyEpic).build();
        client.send(createEpicRequest, HttpResponse.BodyHandlers.ofString());

        URI createSubtaskUrl = URI.create("http://localhost:8080/tasks/subtask");
        Subtask subtask = new Subtask(7, "Test Subtask 1", "Description", 10, LocalDateTime.of(2024, 1, 27, 10, 0), 4);
        String jsonSubtask = gson.toJson(subtask);
        final HttpRequest.BodyPublisher bodySubtask = HttpRequest.BodyPublishers.ofString(jsonSubtask);
        HttpRequest createSubtaskRequest = HttpRequest.newBuilder().uri(createSubtaskUrl).POST(bodySubtask).build();
        client.send(createSubtaskRequest, HttpResponse.BodyHandlers.ofString());


        URI getUrl = URI.create("http://localhost:8080/tasks/subtask?id=7");
        HttpRequest request = HttpRequest.newBuilder().uri(getUrl).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Subtask actualTask = gson.fromJson(response.body(), Subtask.class);

        assertEquals(subtask.toString(), actualTask.toString());
    }

    @Test
    public void updateTaskTest() throws IOException, InterruptedException {
        URI createTaskUrl = URI.create("http://localhost:8080/tasks/task");
        Task task = new Task(1, "Test Create Task 1", "Description", 15, LocalDateTime.of(2024,1,29,20,0));
        String jsonTask = gson.toJson(task);
        final HttpRequest.BodyPublisher bodyTask = HttpRequest.BodyPublishers.ofString(jsonTask);
        HttpRequest createTaskRequest = HttpRequest.newBuilder().uri(createTaskUrl).POST(bodyTask).build();
        client.send(createTaskRequest, HttpResponse.BodyHandlers.ofString());

        URI getUrl = URI.create("http://localhost:8080/tasks/task?id=1");
        HttpRequest request = HttpRequest.newBuilder().uri(getUrl).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Task actualTask = gson.fromJson(response.body(), Task.class);

        assertEquals(task.toString(), actualTask.toString());

        URI createSubtaskUrl = URI.create("http://localhost:8080/tasks/task/update");
        Task taskUpdate = new Task(1, TypeTask.TASK,"Test Create Task 1", "Description", Status.IN_PROGRESS, 15, LocalDateTime.of(2024,1,29,20,0));
        String jsonSubtask = gson.toJson(taskUpdate);
        final HttpRequest.BodyPublisher bodyUpdateTask = HttpRequest.BodyPublishers.ofString(jsonSubtask);
        HttpRequest updateTaskRequest = HttpRequest.newBuilder().uri(createSubtaskUrl).POST(bodyUpdateTask).build();
        client.send(updateTaskRequest, HttpResponse.BodyHandlers.ofString());

        URI getUrlTwo = URI.create("http://localhost:8080/tasks/task?id=1");
        HttpRequest requestTwo = HttpRequest.newBuilder().uri(getUrlTwo).GET().build();
        HttpResponse<String> responseTwo = client.send(requestTwo, HttpResponse.BodyHandlers.ofString());
        Task actualUpdateTask = gson.fromJson(responseTwo.body(), Task.class);

        String actualStatusTask = actualUpdateTask.getStatus().toString();
        String expectedStatus = Status.IN_PROGRESS.toString();

        assertNotEquals(actualTask, actualUpdateTask);
        assertEquals(expectedStatus, actualStatusTask);
    }

    @Test
    public void updateEpicTest() throws IOException, InterruptedException {
        URI createEpicUrl = URI.create("http://localhost:8080/tasks/epic");
        Epic epic = new Epic(4, "Test Create Epic 1", "Description", new ArrayList<>());
        Subtask subtask = new Subtask(7, "Test Subtask 1", "Description", 10, LocalDateTime.of(2024, 1, 27, 10, 0), 4);
        epic.getSubtasks().add(subtask);
        String jsonEpic = gson.toJson(epic);
        final HttpRequest.BodyPublisher bodyEpic = HttpRequest.BodyPublishers.ofString(jsonEpic);
        HttpRequest createEpicRequest = HttpRequest.newBuilder().uri(createEpicUrl).POST(bodyEpic).build();
        client.send(createEpicRequest, HttpResponse.BodyHandlers.ofString());

        URI getUrl = URI.create("http://localhost:8080/tasks/epic?id=4");
        HttpRequest request = HttpRequest.newBuilder().uri(getUrl).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Epic actualEpic = gson.fromJson(response.body(), Epic.class);

        assertEquals(epic.toString(), actualEpic.toString());

        URI updateEpicUrl = URI.create("http://localhost:8080/tasks/epic/update");
        Epic epicUpdate = new Epic(4, "Test Create Epic 1", "Description", new ArrayList<>());
        epicUpdate.getSubtasks().add(new Subtask(7, TypeTask.SUBTASK, "Test Subtask 1", "Description", Status.IN_PROGRESS, 10, LocalDateTime.of(2024, 1, 27, 10, 0), 4));
        String jsonUpdateEpic = gson.toJson(epicUpdate);
        final HttpRequest.BodyPublisher bodyUpdateTask = HttpRequest.BodyPublishers.ofString(jsonUpdateEpic);
        HttpRequest updateTaskRequest = HttpRequest.newBuilder().uri(updateEpicUrl).POST(bodyUpdateTask).build();
        client.send(updateTaskRequest, HttpResponse.BodyHandlers.ofString());

        URI getUpdateUrl = URI.create("http://localhost:8080/tasks/epic?id=4");
        HttpRequest requestUpdate = HttpRequest.newBuilder().uri(getUpdateUrl).GET().build();
        HttpResponse<String> responseUpdate = client.send(requestUpdate, HttpResponse.BodyHandlers.ofString());
        Epic actualUpdateEpic = gson.fromJson(responseUpdate.body(), Epic.class);

        String expectedStatus = Status.IN_PROGRESS.toString();
        String actualStatus = actualUpdateEpic.getStatus().toString();

        assertNotEquals(epic, epicUpdate);
        assertEquals(expectedStatus, actualStatus);
    }

    @Test
    public void updateSubtaskTest() throws IOException, InterruptedException {
        URI createEpicUrl = URI.create("http://localhost:8080/tasks/epic");
        Epic epic = new Epic(4, "Test Create Epic 1", "Description", new ArrayList<>());
        String jsonEpic = gson.toJson(epic);
        final HttpRequest.BodyPublisher bodyEpic = HttpRequest.BodyPublishers.ofString(jsonEpic);
        HttpRequest createEpicRequest = HttpRequest.newBuilder().uri(createEpicUrl).POST(bodyEpic).build();
        client.send(createEpicRequest, HttpResponse.BodyHandlers.ofString());

        URI createSubtaskUrl = URI.create("http://localhost:8080/tasks/subtask");
        Subtask subtask = new Subtask(7, "Test Subtask 1", "Description", 10, LocalDateTime.of(2024, 1, 27, 10, 0), 4);
        String jsonSubtask = gson.toJson(subtask);
        final HttpRequest.BodyPublisher bodySubtask = HttpRequest.BodyPublishers.ofString(jsonSubtask);
        HttpRequest createSubtaskRequest = HttpRequest.newBuilder().uri(createSubtaskUrl).POST(bodySubtask).build();
        client.send(createSubtaskRequest, HttpResponse.BodyHandlers.ofString());



        URI getUrl = URI.create("http://localhost:8080/tasks/subtask?id=7");
        HttpRequest request = HttpRequest.newBuilder().uri(getUrl).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Subtask actualSubtask = gson.fromJson(response.body(), Subtask.class);

        assertEquals(subtask.toString(), actualSubtask.toString());

        URI updateSubtaskUrl = URI.create("http://localhost:8080/tasks/subtask/update");
        Subtask subtaskUpdate = new Subtask(7, TypeTask.SUBTASK, "Test Subtask 1", "Description", Status.IN_PROGRESS, 10, LocalDateTime.of(2024, 2, 27, 10, 0), 4);
        String jsonUpdateSubtask = gson.toJson(subtaskUpdate);
        final HttpRequest.BodyPublisher bodyUpdateSubtask = HttpRequest.BodyPublishers.ofString(jsonUpdateSubtask);
        HttpRequest updateSubtaskRequest = HttpRequest.newBuilder().uri(updateSubtaskUrl).POST(bodyUpdateSubtask).build();
        client.send(updateSubtaskRequest, HttpResponse.BodyHandlers.ofString());

        URI getUpdateUrl = URI.create("http://localhost:8080/tasks/subtask?id=7");
        HttpRequest requestUpdate = HttpRequest.newBuilder().uri(getUpdateUrl).GET().build();
        HttpResponse<String> responseUpdate = client.send(requestUpdate, HttpResponse.BodyHandlers.ofString());
        Subtask actualUpdate = gson.fromJson(responseUpdate.body(), Subtask.class);

        String expectedStatus = Status.IN_PROGRESS.toString();
        String actualStatus = actualUpdate.getStatus().toString();

        assertNotEquals(subtask, subtaskUpdate);
        assertEquals(expectedStatus, actualStatus);
    }

}