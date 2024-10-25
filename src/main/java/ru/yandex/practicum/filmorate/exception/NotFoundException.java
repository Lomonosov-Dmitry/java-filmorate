package ru.yandex.practicum.filmorate.exception;

public class NotFoundException extends RuntimeException {
    private String error;
    private String description;

    public NotFoundException(String error, String description) {
        this.error = error;
        this.description = description;
    }

    public String getError() {
        return error;
    }

    public String getDescription() {
        return description;
    }
}
