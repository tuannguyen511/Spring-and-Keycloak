package tuan.keycloakexample.task_management.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import tuan.keycloakexample.task_management.model.Task;
import tuan.keycloakexample.task_management.service.TaskService;

import java.util.List;

/**
 * @author cps
 **/
@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class WebTaskController {

    private final TaskService taskService;

    @GetMapping
    public List<Task> getTasks(@AuthenticationPrincipal OAuth2User principal) {
        String username = principal.getAttribute("preferred_username");
        System.out.println("Current user: " + username);
        return taskService.findAll();
    }
}
