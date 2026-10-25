package com.mridnal.TaskMaster.tasks.attachments.dto;

import com.mridnal.TaskMaster.tasks.attachments.TaskAttachments;

import java.time.Instant;

public record AttachmentResponse(
        Long id,
        Long taskId,
        Long uploaderId,
        String originalFilename,
        String contentType,
        long fileSize,
        Instant uploadedAt
) {
    public static AttachmentResponse from(TaskAttachments attachment) {
        return new AttachmentResponse(
                attachment.getId(),
                attachment.getTask().getId(),
                attachment.getUploader().getId(),
                attachment.getOriginalFilename(),
                attachment.getContentType(),
                attachment.getFileSize(),
                attachment.getUploadedAt()
        );
    }
}