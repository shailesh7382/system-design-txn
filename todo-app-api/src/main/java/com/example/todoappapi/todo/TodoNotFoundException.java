package com.example.todoappapi.todo;

import java.util.UUID;

public class TodoNotFoundException extends RuntimeException {
    public TodoNotFoundException(UUID id) {
        super("Todo not found: " + id);
    }
}

