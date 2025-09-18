package tuan.keycloakexample.task_management.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tuan.keycloakexample.task_management.model.Task;
import tuan.keycloakexample.task_management.repository.TaskRepository;

import java.util.List;

/**
 * @author cps
 **/
@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    public List<Task> getTasksForUser(String username, boolean isAdmin) {
        if (isAdmin) {
            return taskRepository.findAll();
        }
        return taskRepository.findByCreatedBy(username);
    }

    public Task createTask(Task task, String username) {
        task.setCreatedBy(username);
        return taskRepository.save(task);
    }

    public void deleteById(Long id) {
        taskRepository.deleteById(id);
    }

    public List<Task> findAll() {
        return taskRepository.findAll();
    }
}
