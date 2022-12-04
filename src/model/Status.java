package model;

public enum Status {
    NEW("новая"), DONE("завершённая"), IN_PROGRESS("выполняется");

    private String title;

    Status(String translation) {
        this.title = translation;
    }

    @Override
    public String toString() {
        return title;
    }
}
