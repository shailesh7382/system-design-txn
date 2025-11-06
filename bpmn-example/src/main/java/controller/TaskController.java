package controller;

import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.task.Task;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tasks")
public class TaskController {

  private final TaskService taskService;

  public TaskController(TaskService taskService) {
    this.taskService = taskService;
  }

  @GetMapping
  public List<Map<String, String>> list() {
    return taskService.createTaskQuery().list().stream()
        .map(t -> Map.of("id", t.getId(),
                         "name", t.getName(),
                         "assignee", t.getAssignee(),
                         "processInstanceId", t.getProcessInstanceId()))
        .toList();
  }

  @PostMapping("/{taskId}/complete")
  public ResponseEntity<?> complete(@PathVariable String taskId) {
    Task t = taskService.createTaskQuery().taskId(taskId).singleResult();
    if (t == null) return ResponseEntity.notFound().build();
    taskService.complete(taskId);
    return ResponseEntity.ok(Map.of("completed", taskId));
  }
}
