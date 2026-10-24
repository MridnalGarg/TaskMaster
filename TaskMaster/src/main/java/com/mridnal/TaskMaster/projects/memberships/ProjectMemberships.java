package com.mridnal.TaskMaster.projects.memberships;

import com.mridnal.TaskMaster.projects.Projects;
import com.mridnal.TaskMaster.users.Users;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "project_memberships")
@Getter
public class ProjectMemberships {

    @EmbeddedId
    private ProjectMembershipId id;

    @MapsId("projectId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false, updatable = false)
    private Projects project;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private Users user;

    @Column(name = "joined_at", nullable = false)
    private Instant joinedAt;

    protected ProjectMemberships() {}

    public ProjectMemberships(Projects project, Users user) {
        this.project = Objects.requireNonNull(project);
        this.user = Objects.requireNonNull(user);
        this.id = new ProjectMembershipId(project.getId(), user.getId());
        this.joinedAt = Instant.now();
    }
}
