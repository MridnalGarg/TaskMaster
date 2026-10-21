package com.mridnal.TaskMaster.auth.dto;

public record AuthResponse(
        String accessToken,
        String tokenType,
        long expiresIn
) {}