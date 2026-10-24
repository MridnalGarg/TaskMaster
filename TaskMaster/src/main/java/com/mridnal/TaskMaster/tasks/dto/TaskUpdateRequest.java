package com.mridnal.TaskMaster.tasks.dto;

import jakarta.validation.constraints.*;

import java.sql.Date;

public record TaskUpdateRequest(
        @PositiveOrZero
        Long taskId,

        @NotBlank
        @Size(max = 100)
        String title,

        @Size(max = 255)
        String description,

        Date dueDate,

        @PositiveOrZero
        Long reporterId,

        @PositiveOrZero
        Long assigneeId
) {}
