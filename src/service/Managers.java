package service;

public class Managers {

    //public static TaskManager getDefault() { return new InMemoryTaskManager(); } тесты еще не писал, поэтому метод
    // оставался (вместе с классом main) в качестве проверки, что пока писал новый функционал, не покалечил случайно старый.

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

}