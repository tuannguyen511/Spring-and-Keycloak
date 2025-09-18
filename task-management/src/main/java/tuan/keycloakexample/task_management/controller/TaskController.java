package tuan.keycloakexample.task_management.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import tuan.keycloakexample.task_management.dto.TaskRequest;
import tuan.keycloakexample.task_management.model.Task;
import tuan.keycloakexample.task_management.service.TaskService;

import java.util.List;
import java.util.Map;

/**
 * @author cps
 **/
@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @GetMapping
    public ResponseEntity<List<Task>> getTasks(@AuthenticationPrincipal Jwt principal) {
        String username = principal.getClaim("preferred_username");
        String department = principal.getClaimAsString("department");
        System.out.println("User: " + username + " | Department: " + department);

        boolean isAdmin = principal.getClaim("realm_access") != null &&
            ((Map<String, List<String>>) principal.getClaim("realm_access"))
                .getOrDefault("roles", List.of())
                .contains("ADMIN");

        List<Task> tasks = taskService.getTasksForUser(username, isAdmin);
        return ResponseEntity.ok(tasks);
    }

    @PostMapping
    public ResponseEntity<Task> createTask(@Valid @RequestBody TaskRequest taskRequest, @AuthenticationPrincipal Jwt principal) {
        String username = principal.getClaim("preferred_username");
        Task saved = taskService.createTask(
            Task.builder()
                .title(taskRequest.title())
                .description(taskRequest.description())
                .build(),
            username
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/assign")
    public ResponseEntity<Void> assignTask() {
        return ResponseEntity.noContent().build();
    }
}
