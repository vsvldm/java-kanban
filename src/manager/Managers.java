package manager;

import adapter.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.time.LocalDateTime;

public class Managers {

    public static TaskManager getDefaultInMemoryTaskManager() {
        return new InMemoryTaskManager();
    }
    public static TaskManager getDefaultTaskManager() {
        try {
            return getHttpTaskManager();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static FileBackedTasksManager getFileBackedTasksManager() {
        return new FileBackedTasksManager("resources/tasks.csv");
    }

    public static HttpTaskManager getHttpTaskManager() throws IOException, InterruptedException {
        return new HttpTaskManager("http://localhost:8078");
    }

    public static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.serializeNulls();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
        return gsonBuilder.create();
    }
}
