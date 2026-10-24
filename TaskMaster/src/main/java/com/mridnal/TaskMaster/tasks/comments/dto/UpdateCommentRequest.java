package com.mridnal.TaskMaster.tasks.comments.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateCommentRequest(
        @NotBlank(message = "Comment cannot be blank")
        @Size(max = 999, message = "Comment cannot exceed 999 characters")
        String comment
) {
}
