package manager;

import org.junit.jupiter.api.BeforeEach;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    public void beforeEach() {
        manager = new InMemoryTaskManager(new InMemoryHistoryManager());
    }
}

