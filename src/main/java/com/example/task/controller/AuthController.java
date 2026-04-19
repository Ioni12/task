package com.example.task.controller;

import com.example.task.entity.User;
import com.example.task.request.LoginRequest;
import com.example.task.request.UserRequest;
import com.example.task.response.UserResponse;
import com.example.task.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    public AuthController(UserService userService,
                          AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody UserRequest request) {
        log.info("Register request for email: {}", request.email());
        User created = userService.createUser(request);
        log.info("User registered with id: {}", created.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new UserResponse(created.getId(), created.getName(), created.getEmail()));
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request,
                                        HttpServletRequest httpRequest) {
        log.info("Login attempt for user: {}", request.username());
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.username(),
                            request.password()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(auth);

            HttpSession session = httpRequest.getSession(true);
            session.setAttribute(
                    HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                    SecurityContextHolder.getContext()
            );

            log.info("Login successful for user: {}", request.username());
            return ResponseEntity.ok("Login successful");

        } catch (AuthenticationException e) {
            log.warn("Login failed for user: {}", request.username());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }
}