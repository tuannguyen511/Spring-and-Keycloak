package tuan.keycloakexample.task_management.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * @author cps
 **/
public record TaskRequest(
        @NotBlank
        @Size(min = 2, message = "Title must be at least 2 characters long")
        String title,
        String description
) {}
