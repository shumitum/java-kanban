package service;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    @Override
    InMemoryTaskManager getRightTypeOfManager() {
        return new InMemoryTaskManager();
    }
}