package service;

import static servers.KVServer.PORT;

public class Managers {

    public static HttpTaskManager getDefault() {
        return new HttpTaskManager("http://localhost:" + PORT);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
