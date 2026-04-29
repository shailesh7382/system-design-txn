package com.example.todoappapi.todo;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TodoRepository extends JpaRepository<TodoEntity, UUID> {
    long deleteByCompletedTrue();
}

