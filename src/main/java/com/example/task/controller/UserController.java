package com.example.task.controller;

import com.example.task.entity.User;
import com.example.task.request.UserRequest;
import com.example.task.response.UserResponse;
import com.example.task.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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
        log.debug("Fetching all users");
        List<User> users = userService.getAllUsers();
        log.debug("Found {} users", users.size());
        List<UserResponse> responses = new ArrayList<>();
        for (User user : users) {
            responses.add(new UserResponse(user.getId(), user.getName(), user.getEmail()));
        }
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserResponse>> getUsersByName(@RequestParam String name) {
        log.debug("Searching users by name: {}", name);
        List<User> users = userService.getUsersByName(name);
        log.debug("Found {} users for name: {}", users.size(), name);
        List<UserResponse> responses = new ArrayList<>();
        for (User user : users) {
            responses.add(new UserResponse(user.getId(), user.getName(), user.getEmail()));
        }
        return ResponseEntity.ok(responses);
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody UserRequest request) {
        log.info("Create user request for email: {}", request.email());
        User created = userService.createUser(request);
        log.info("User created with id: {}", created.getId());
        UserResponse response = new UserResponse(created.getId(), created.getName(), created.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @RequestBody UserRequest request) {
        log.info("Update user request for id: {}", id);
        User updated = userService.updateUser(id, request);
        log.info("User updated with id: {}", id);
        UserResponse response = new UserResponse(updated.getId(), updated.getName(), updated.getEmail());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        log.info("Delete user request for id: {}", id);
        userService.deleteUser(id);
        log.info("User deleted with id: {}", id);
        return ResponseEntity.noContent().build();
    }
}