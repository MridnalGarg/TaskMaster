package com.mridnal.TaskMaster.projects;

import com.mridnal.TaskMaster.users.Users;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "projects")
@Getter
@Setter
public class Projects {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_id")
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", length = 255)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private Users owner;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected Projects() {
    }

    public Projects(String name, String description, Users owner) {
        this.name = name;
        this.description = description;
        this.owner = owner;
    }

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }
}
