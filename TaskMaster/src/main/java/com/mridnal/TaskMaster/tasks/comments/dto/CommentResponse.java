package com.mridnal.TaskMaster.tasks.comments.dto;

import com.mridnal.TaskMaster.tasks.comments.TaskComments;

import java.time.Instant;

public record CommentResponse(
        Long id,
        Long taskId,
        Long authorId,
        String authorName,
        String comment,
        Instant createdAt,
        Instant updatedAt
) {
    public static CommentResponse from(TaskComments comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getTask().getId(),
                comment.getAuthor().getId(),
                comment.getAuthor().getName(),
                comment.getComment(),
                comment.getCreatedAt(),
                comment.getUpdatedAt()
        );
    }
}