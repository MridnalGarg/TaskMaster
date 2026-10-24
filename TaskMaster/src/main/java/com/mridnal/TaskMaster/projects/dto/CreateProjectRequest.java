package com.mridnal.TaskMaster.projects.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record CreateProjectRequest(
        @NotBlank
        @Size(max = 100, min = 8)
        String name,

        @Size(max = 255, min = 8)
        String description
) {
}
