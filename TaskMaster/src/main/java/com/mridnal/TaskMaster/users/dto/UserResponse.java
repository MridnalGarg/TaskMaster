package com.mridnal.TaskMaster.users.dto;

import com.mridnal.TaskMaster.users.Users;

public record UserResponse(
        Long userId,
        String name,
        String email,
        String designation,
        Integer yearOfExp,
        String bio
) {
    public static UserResponse from(Users user){
        return new UserResponse(
          user.getId(),
          user.getName(),
          user.getEmail(),
          user.getDesignation(),
          user.getYearOfExp(),
          user.getBio()
        );
    }
}
