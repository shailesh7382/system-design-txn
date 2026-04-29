package com.example.todoappapi.todo;

import java.time.Instant;
import java.util.UUID;

public record TodoResponse(
        UUID id,
        String title,
        boolean completed,
        TodoPriority priority,
        Instant createdAt
) {
    public static TodoResponse fromEntity(TodoEntity entity) {
        return new TodoResponse(
                entity.getId(),
                entity.getTitle(),
                entity.isCompleted(),
                entity.getPriority(),
                entity.getCreatedAt()
        );
    }
}

