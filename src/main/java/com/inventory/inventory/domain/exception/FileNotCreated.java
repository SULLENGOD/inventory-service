package com.inventory.inventory.domain.exception;

public class FileNotCreated extends RuntimeException {
    public FileNotCreated(String message) {
        super(message);
    }

    public FileNotCreated(String message, Throwable cause) {
        super(message, cause);
    }
}
