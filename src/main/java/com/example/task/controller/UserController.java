package com.example.task.controller;

import com.example.task.entity.User;
import com.example.task.entity.UserStatus;
import com.example.task.exception.BadRequestException;
import com.example.task.exception.ResourceNotFoundException;
import com.example.task.request.UserRequest;
import com.example.task.response.UserResponse;
import com.example.task.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        long start = System.currentTimeMillis();
        log.info("Fetching all users");
        List<User> users = userService.getAllUsers();
        log.info("Fetched {} users in {} ms", users.size(), System.currentTimeMillis() - start);
        return ResponseEntity.ok(users.stream().map(this::toResponse).toList());
    }

    @GetMapping("/search")
    public ResponseEntity<UserResponse> getUserByName(@RequestParam String name) {
        long start = System.currentTimeMillis();
        log.info("Searching user by name: {}", name);

        if (name.isBlank()) {
            throw new BadRequestException("Name parameter must not be blank");
        }

        User user = userService.getUserByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with name: ", name));

        log.info("Found user: {} in {} ms", name, System.currentTimeMillis() - start);
        return ResponseEntity.ok(toResponse(user));
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest request) {
        long start = System.currentTimeMillis();
        log.info("Creating user with email: {}", request.getEmail());
        User created = userService.createUser(request);
        log.info("User created with id: {} in {} ms", created.getId(), System.currentTimeMillis() - start);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @Valid @RequestBody UserRequest request) {
        long start = System.currentTimeMillis();
        log.info("Updating user with id: {}", id);
        User updated = userService.updateUser(id, request);
        log.info("User updated with id: {} in {} ms", id, System.currentTimeMillis() - start);
        return ResponseEntity.ok(toResponse(updated));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<UserResponse> updateStatus(@PathVariable Long id, @RequestParam UserStatus status) {
        log.info("Updating status to {} for user with id: {}", status, id);
        User updated = userService.updateStatus(id, status);
        return ResponseEntity.ok(toResponse(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        long start = System.currentTimeMillis();
        log.info("Deleting user with id: {}", id);
        userService.deleteUser(id);
        log.info("User deleted with id: {} in {} ms", id, System.currentTimeMillis() - start);
        return ResponseEntity.noContent().build();
    }

    private UserResponse toResponse(User user) {
        List<UserResponse.AccountInfo> accounts = user.getAccounts() == null ? List.of() :
                user.getAccounts().stream()
                .map(a -> new UserResponse.AccountInfo(
                        a.getIban(),
                        a.getAccountName(),
                        a.getAmount(),
                        a.getCurrency(),
                        a.getStatus(),
                        a.getCreatedAt()
                ))
                .toList();

        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getName(),
                user.getEmail(),
                user.getDefaultCurrency(),
                user.getPhone(),
                user.getDateOfBirth(),
                user.getStreet(),
                user.getCity(),
                user.getCountry(),
                user.getPostalCode(),
                user.getStatus(),
                user.getCreatedAt(),
                accounts
        );
    }
}