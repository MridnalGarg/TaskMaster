package com.mridnal.TaskMaster.tasks.attachments;

import com.mridnal.TaskMaster.tasks.Tasks;
import com.mridnal.TaskMaster.users.Users;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "task_attachments")
@Getter
public class TaskAttachments {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attachment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "task_id", nullable = false, updatable = false)
    private Tasks task;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "uploader_id", nullable = false, updatable = false)
    private Users uploader;

    @Column(name = "original_filename", nullable = false)
    private String originalFilename;

    @Column(name = "storage_key", nullable = false, unique = true, updatable = false)
    private String storageKey;

    @Column(name = "content_type", nullable = false)
    private String contentType;

    @Column(name = "file_size", nullable = false)
    private long fileSize;

    @Column(name = "uploaded_at", nullable = false, updatable = false)
    private Instant uploadedAt;

    protected TaskAttachments() {
    }

    public TaskAttachments(
            Tasks task,
            Users uploader,
            String originalFilename,
            String storageKey,
            String contentType,
            long fileSize) {

        this.task = Objects.requireNonNull(task);
        this.uploader = Objects.requireNonNull(uploader);
        this.originalFilename = Objects.requireNonNull(originalFilename);
        this.storageKey = Objects.requireNonNull(storageKey);
        this.contentType = Objects.requireNonNull(contentType);
        this.fileSize = fileSize;
    }

    @PrePersist
    protected void prePersist() {
        if (uploadedAt == null) {
            uploadedAt = Instant.now();
        }
    }
}
