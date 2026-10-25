package com.mridnal.TaskMaster.tasks.attachments;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TaskAttachmentsRepository
        extends JpaRepository<TaskAttachments, Long> {

    List<TaskAttachments> findByTask_IdOrderByUploadedAtDesc(
            Long taskId);

    Optional<TaskAttachments> findByIdAndTask_Id(
            Long attachmentId,
            Long taskId);
}
