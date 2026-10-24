package com.mridnal.TaskMaster.tasks.comments;

import com.mridnal.TaskMaster.tasks.Tasks;
import com.mridnal.TaskMaster.users.Users;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "task_comments")
@Getter
public class TaskComments {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "task_id", nullable = false, updatable = false)
    private Tasks task;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private Users author;

    @Column(name = "comment", nullable = false, columnDefinition = "TEXT")
    private String comment;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected TaskComments() {
    }

    public TaskComments(Tasks task, Users author, String comment) {
        this.task = Objects.requireNonNull(task);
        this.author = Objects.requireNonNull(author);
        this.comment = Objects.requireNonNull(comment).trim();
    }

    public void updateContent(String comment) {
        this.comment = Objects.requireNonNull(comment).trim();
        this.updatedAt = Instant.now();
    }

    @PrePersist
    protected void prePersist() {
        Instant now = Instant.now();
        if (createdAt == null) {
            createdAt = now;
        }
        if (updatedAt == null) {
            updatedAt = now;
        }
    }
}
