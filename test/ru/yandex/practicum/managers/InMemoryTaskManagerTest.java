package ru.yandex.practicum.managers;

import org.junit.jupiter.api.BeforeEach;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    public void createNewTaskManager() {
        taskManager = new InMemoryTaskManager();
    }
}
