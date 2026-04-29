package com.example.todoappapi.todo;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@Transactional
public class TodoService {

    private final TodoRepository todoRepository;

    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    @Transactional(readOnly = true)
    public List<TodoResponse> list(String filter, String search) {
        String normalizedFilter = filter == null ? "all" : filter.trim().toLowerCase(Locale.ROOT);
        String normalizedSearch = search == null ? "" : search.trim().toLowerCase(Locale.ROOT);

        return todoRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"))
                .stream()
                .filter(todo -> switch (normalizedFilter) {
                    case "active" -> !todo.isCompleted();
                    case "completed" -> todo.isCompleted();
                    default -> true;
                })
                .filter(todo -> normalizedSearch.isEmpty() || todo.getTitle().toLowerCase(Locale.ROOT).contains(normalizedSearch))
                .map(TodoResponse::fromEntity)
                .toList();
    }

    public TodoResponse create(CreateTodoRequest request) {
        TodoEntity entity = new TodoEntity();
        entity.setTitle(request.title().trim());
        entity.setPriority(request.priority() == null ? TodoPriority.MEDIUM : request.priority());
        entity.setCompleted(false);
        return TodoResponse.fromEntity(todoRepository.save(entity));
    }

    public TodoResponse update(UUID id, UpdateTodoRequest request) {
        TodoEntity entity = findOrThrow(id);

        if (request.title() != null && !request.title().trim().isEmpty()) {
            entity.setTitle(request.title().trim());
        }
        if (request.completed() != null) {
            entity.setCompleted(request.completed());
        }
        if (request.priority() != null) {
            entity.setPriority(request.priority());
        }

        return TodoResponse.fromEntity(todoRepository.save(entity));
    }

    public TodoResponse toggle(UUID id) {
        TodoEntity entity = findOrThrow(id);
        entity.setCompleted(!entity.isCompleted());
        return TodoResponse.fromEntity(todoRepository.save(entity));
    }

    public void delete(UUID id) {
        if (!todoRepository.existsById(id)) {
            throw new TodoNotFoundException(id);
        }
        todoRepository.deleteById(id);
    }

    public long clearCompleted() {
        return todoRepository.deleteByCompletedTrue();
    }

    public void clearAll() {
        todoRepository.deleteAll();
    }

    private TodoEntity findOrThrow(UUID id) {
        return todoRepository.findById(id).orElseThrow(() -> new TodoNotFoundException(id));
    }
}

