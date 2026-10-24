package com.mridnal.TaskMaster.tasks;

import com.mridnal.TaskMaster.enums.TaskStatus;
import com.mridnal.TaskMaster.projects.Projects;
import com.mridnal.TaskMaster.users.Users;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;
import java.time.Instant;

@Entity
@Table(name = "tasks")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Tasks {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_id")
    private Long id;

    @Column
    private String title;

    @Column
    private String description;

    @Enumerated(EnumType.STRING)
    @Column
    private TaskStatus status;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "project_id")
    private Projects project;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "reporter_id", nullable = false)
    private Users reporter;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "assignee_id")
    private Users assignee;

    @Column
    private Date dueDate;

    @Column(updatable = false)
    private Instant createdAt;

    @Column
    private Instant updatedAt;

    public Tasks(String title, String description, Date dueDate, Users reporter, Users assignee, Projects project) {
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.reporter = reporter;
        this.assignee = assignee;
        this.project = project;
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.status = TaskStatus.NEW;
    }

}
