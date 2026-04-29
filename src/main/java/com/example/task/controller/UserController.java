package com.example.task.controller;

import com.example.task.entity.User;
import com.example.task.exception.BadRequestException;
import com.example.task.exception.ResourceNotFoundException;
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
        long start = System.currentTimeMillis();
        log.info("Fetching all users: {}",  start);
        List<User> users = userService.getAllUsers();
        log.info("Found {} users", users.size());
        List<UserResponse> responses = new ArrayList<>();

        for (User user : users) {
            responses.add(new UserResponse(user.getId(), user.getUsername(), user.getEmail()));
        }

        log.info("fetched users in {} ms", System.currentTimeMillis() - start);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserResponse>> getUsersByName(@RequestParam String name) {
        long start = System.currentTimeMillis();
        log.info("Searching users by name: {}, time: {}", name, start);

        if (name.isBlank()) {
            throw new BadRequestException("name parameter must not be blank");
        }

        List<User> users = userService.getUsersByName(name);

        if (users.isEmpty()) {
            throw new ResourceNotFoundException("User", name);
        }


        log.info("Found {} users for name: {}", users.size(), name);
        List<UserResponse> responses = new ArrayList<>();
        for (User user : users) {
            responses.add(new UserResponse(user.getId(), user.getUsername(), user.getEmail()));
        }

        log.info("searched users in {} ms", System.currentTimeMillis() - start);
        return ResponseEntity.ok(responses);
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody UserRequest request) {
        long start = System.currentTimeMillis();
        log.info("Create user request for email: {}, time: {}", request.email(), start);
        User created = userService.createUser(request);
        log.info("User created with id: {}", created.getId());
        log.info("created user in {} ms", System.currentTimeMillis() - start);
        UserResponse response = new UserResponse(created.getId(), created.getUsername(), created.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @RequestBody UserRequest request) {
        long start = System.currentTimeMillis();
        log.info("Update user request for id: {}, time: {}", id, start);
        User updated = userService.updateUser(id, request);
        log.info("User updated with id: {}, time: {}", id, System.currentTimeMillis() - start);
        UserResponse response = new UserResponse(updated.getId(), updated.getUsername(), updated.getEmail());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        long start = System.currentTimeMillis();
        log.info("Delete user request for id: {}, time: {}", id, start);
        userService.deleteUser(id);
        log.info("User deleted with id: {}, time: {}", id, System.currentTimeMillis() - start);
        return ResponseEntity.noContent().build();
    }
}