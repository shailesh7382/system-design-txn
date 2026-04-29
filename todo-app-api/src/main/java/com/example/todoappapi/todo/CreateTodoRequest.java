package com.example.todoappapi.todo;

import jakarta.validation.constraints.NotBlank;

public record CreateTodoRequest(
        @NotBlank(message = "title is required")
        String title,
        TodoPriority priority
) {
}

