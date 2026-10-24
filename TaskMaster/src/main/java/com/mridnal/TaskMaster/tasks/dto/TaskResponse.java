package com.mridnal.TaskMaster.tasks.dto;

import com.mridnal.TaskMaster.enums.TaskStatus;
import com.mridnal.TaskMaster.tasks.Tasks;
import com.mridnal.TaskMaster.users.Users;
import com.mridnal.TaskMaster.users.dto.UserResponse;

import java.sql.Date;
import java.time.Instant;

public record TaskResponse(
    Long taskId,
    String title,
    String description,
    Date dueDate,
    TaskStatus status,
    UserDTO reporter,
    UserDTO assignee,
    Instant createdAt,
    Instant updatedAt

) {

    public static TaskResponse from(Tasks task) {
        return new TaskResponse(
          task.getId(),
          task.getTitle(),
          task.getDescription(),
          task.getDueDate(),
          task.getStatus(),
          UserDTO.from(task.getReporter()),
          task.getAssignee() != null
            ? UserDTO.from(task.getAssignee())
            : null,
          task.getCreatedAt(),
          task.getUpdatedAt()
        );
    }
}
