package model;

public enum Status {
    NEW("новая задача"), DONE("задача завершена"), IN_PROGRESS("задача в процессе ыфполнения");

    private String translation;

    Status(String translation) {
        this.translation = translation;
    }

    @Override
    public String toString() {
        return translation;
    }
}
