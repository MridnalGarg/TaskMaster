package com.mridnal.TaskMaster.users.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(
        @NotBlank
        @Size(max = 100)
        String name,

        @Size(max = 255)
        String designation,

        @Min(value = 0)
        @Max(value = 50)
        Integer yearOfExp,

        @Size(max = 255)
        String bio

) {
}
