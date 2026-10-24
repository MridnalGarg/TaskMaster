package com.mridnal.TaskMaster.projects.dto;

import com.mridnal.TaskMaster.projects.Projects;
import com.mridnal.TaskMaster.users.dto.UserResponse;

import java.time.Instant;

public record ProjectResponse(
        Long projectId,
        String name,
        String description,
        Long ownerId,
        Instant createdAt
) {

    public static ProjectResponse from(Projects projects) {
        return new ProjectResponse(
                projects.getId(),
                projects.getName(),
                projects.getDescription(),
                projects.getOwner().getId(),
                projects.getCreatedAt()
        );
    }
}
