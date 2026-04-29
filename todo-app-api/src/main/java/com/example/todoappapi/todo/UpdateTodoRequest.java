package com.example.todoappapi.todo;

public record UpdateTodoRequest(
        String title,
        Boolean completed,
        TodoPriority priority
) {
}

