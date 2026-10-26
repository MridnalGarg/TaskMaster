package com.mridnal.TaskMaster.users;

import com.mridnal.TaskMaster.users.dto.UserResponse;
import com.mridnal.TaskMaster.users.dto.UserUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "User Controller", description = "User Management")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/me")
    @Operation(summary = "Get Current User Details")
    public UserResponse currentUser(Authentication authentication) {
        return UserResponse.from(
            userService.getCurrentUser(authentication)
        );
    }

    @PutMapping("/update")
    @Operation(summary = "Update user details")
    public UserResponse updateUserDetails(Authentication authentication,
                                          @Valid @RequestBody UserUpdateRequest userUpdateRequest) {
        return UserResponse.from(
                userService.updateUserDetails(authentication, userUpdateRequest)
        );
    }
}