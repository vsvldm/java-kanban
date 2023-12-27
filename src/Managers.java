public class Managers {
    public static TaskManager getDefaultTaskManager() {
        return new InMemoryTaskManager(new InMemoryHistoryManager());
    }
    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
