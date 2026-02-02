package com.example.taskflow.exception;

public class UserNameIsNotFoundException extends RuntimeException {
    public UserNameIsNotFoundException(String message) {
        super(message);
    }
}
