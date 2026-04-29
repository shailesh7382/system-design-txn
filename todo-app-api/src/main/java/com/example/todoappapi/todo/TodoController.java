package com.example.todoappapi.todo;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/todos")
@CrossOrigin(origins = "*")
public class TodoController {

    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @GetMapping
    public List<TodoResponse> listTodos(
            @RequestParam(defaultValue = "all") String filter,
            @RequestParam(defaultValue = "") String search
    ) {
        return todoService.list(filter, search);
    }

    @PostMapping
    public TodoResponse createTodo(@Valid @RequestBody CreateTodoRequest request) {
        return todoService.create(request);
    }

    @PatchMapping("/{id}")
    public TodoResponse updateTodo(@PathVariable UUID id, @RequestBody UpdateTodoRequest request) {
        return todoService.update(id, request);
    }

    @PatchMapping("/{id}/toggle")
    public TodoResponse toggleTodo(@PathVariable UUID id) {
        return todoService.toggle(id);
    }

    @DeleteMapping("/{id}")
    public void deleteTodo(@PathVariable UUID id) {
        todoService.delete(id);
    }

    @DeleteMapping("/completed")
    public Map<String, Long> clearCompleted() {
        long removed = todoService.clearCompleted();
        return Map.of("deleted", removed);
    }

    @DeleteMapping
    public void clearAll() {
        todoService.clearAll();
    }
}

