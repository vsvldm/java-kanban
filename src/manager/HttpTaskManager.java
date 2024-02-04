package manager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import module.Epic;
import module.Subtask;
import module.Task;
import server.KVTaskClient;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

public class HttpTaskManager extends FileBackedTasksManager{
    private KVTaskClient client;
    private Gson gson;

    public HttpTaskManager(String url) throws IOException, InterruptedException {
        super(url);
        this.client = new KVTaskClient(url);
        this.gson = Managers.getGson();
        String tasksJson = client.load("tasks");
        String epicsJson = client.load("epics");
        String subtasksJson = client.load("subtasks");
        String historyJson = client.load("history");
        Type tasksType = new TypeToken<Map<Integer,Task>>(){}.getType();
        Type epicsType = new TypeToken<Map<Integer, Epic>>(){}.getType();
        Type subtasksType = new TypeToken<Map<Integer, Subtask>>(){}.getType();
        Type historyType = new TypeToken<List<Integer>>(){}.getType();

        tasks = Objects.requireNonNullElse(gson.fromJson(tasksJson, tasksType), new HashMap<>());
        epics = Objects.requireNonNullElse(gson.fromJson(epicsJson, epicsType), new HashMap<>());
        subtasks = Objects.requireNonNullElse(gson.fromJson(subtasksJson, subtasksType), new HashMap<>());


        List<Integer> historyList = Objects.requireNonNullElse(gson.fromJson(historyJson, historyType), new ArrayList<>());
        List<Task> allTasks = getAllTasks();
        for (Integer taskId : historyList) {
            for (Task task : allTasks) {
                if (taskId.equals(task.getId())) {
                    getHistoryManager().add(task);
                }
            }
        }
    }

    @Override
    public void save() {
        String tasksJson = gson.toJson(tasks);
        String epicsJson = gson.toJson(epics);
        String subtasksJson = gson.toJson(subtasks);
        String historyJson = gson.toJson(getHistoryManager()
                .getHistory()
                .stream()
                .mapToInt(Task::getId)
                .toArray()
        );

        try {
            client.put("tasks", tasksJson);
            client.put("epics", epicsJson);
            client.put("subtasks", subtasksJson);
            client.put("history", historyJson);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
