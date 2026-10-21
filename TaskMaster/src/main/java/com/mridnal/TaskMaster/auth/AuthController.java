package com.mridnal.TaskMaster.auth;

import com.mridnal.TaskMaster.auth.dto.AuthResponse;
import com.mridnal.TaskMaster.auth.dto.LoginRequest;
import com.mridnal.TaskMaster.auth.dto.RegisterRequest;
import com.mridnal.TaskMaster.users.dto.UserResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(
            @Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(
            @Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }
}