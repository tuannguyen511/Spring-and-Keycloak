package tuan.keycloakexample.task_management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tuan.keycloakexample.task_management.model.Task;

import java.util.List;

/**
 * @author cps
 **/
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByCreatedBy(String username);
}
