package com.mridnal.TaskMaster.tasks.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.sql.Date;

public record TaskCreateRequest(
        @NotBlank
        @Size(max = 100)
        String title,

        @Size(max = 255)
        String description,

        @PositiveOrZero
        Long projectId,

        @PositiveOrZero
        Long assigneeId,

        Date dueDate
) {}
