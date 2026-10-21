package com.mridnal.TaskMaster.auth;

import com.mridnal.TaskMaster.auth.dto.AuthResponse;
import com.mridnal.TaskMaster.auth.dto.LoginRequest;
import com.mridnal.TaskMaster.auth.dto.RegisterRequest;
import com.mridnal.TaskMaster.users.dto.UserResponse;
import com.mridnal.TaskMaster.auth.jwt.JwtService;
import com.mridnal.TaskMaster.users.UserRepository;
import com.mridnal.TaskMaster.users.Users;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private static final long TOKEN_EXPIRATION_SECONDS = 900;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private DatabaseUserDetailsService userDetailsService;

    @Autowired
    private JwtService jwtService;

    @Transactional
    public UserResponse register(RegisterRequest request) {
        String email = request.email().trim().toLowerCase();

        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "An account with this email already exists"
            );
        }

        Users user = new Users(
                request.name().trim(),
                email,
                passwordEncoder.encode(request.password())
        );

        Users saved = userRepository.saveAndFlush(user);

        return UserResponse.from(saved);
    }

    public AuthResponse login(LoginRequest request) {
        String email = request.email().trim().toLowerCase();

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        email,
                        request.password()
                )
        );

        UserDetails userDetails =
                userDetailsService.loadUserByUsername(email);

        String token = jwtService.generateToken(userDetails);

        return new AuthResponse(
                token,
                "Bearer",
                TOKEN_EXPIRATION_SECONDS
        );
    }
}