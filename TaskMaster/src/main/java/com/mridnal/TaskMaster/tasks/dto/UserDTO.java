package com.mridnal.TaskMaster.tasks.dto;

import com.mridnal.TaskMaster.users.Users;

public record UserDTO(
        Long userId,
        String name
) {
    public static UserDTO from(Users user) {
        return new UserDTO(user.getId(), user.getName());
    }
}
