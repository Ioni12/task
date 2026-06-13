package com.example.task.service;

import com.example.task.entity.User;
import com.example.task.entity.UserStatus;
import com.example.task.exception.BadRequestException;
import com.example.task.exception.ResourceNotFoundException;
import com.example.task.repository.UserRepository;
import com.example.task.request.UserRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Cacheable(value = "users", key = "'all'")
    public List<User> getAllUsers() {
        log.debug("Fetching all users");
        return userRepository.findAll();
    }

    @Cacheable(value = "users", key = "#name")
    public Optional<User> getUserByName(String name) {
        log.debug("Searching users by name: {}", name);
        return userRepository.searchByName(name);
    }

    @CacheEvict(value = "users", allEntries = true)
    public User createUser(UserRequest request) {
        log.info("Creating user with email: {}", request.getEmail());
        User user = new User();
        user.setName(request.getName().toLowerCase().trim());
        user.setPersonalId(request.getPersonalId());
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setDefaultCurrency(request.getDefaultCurrency());
        user.setPhone(request.getPhone());
        user.setDateOfBirth(request.getDateOfBirth());
        user.setStreet(request.getStreet());
        user.setCity(request.getCity());
        user.setCountry(request.getCountry());
        user.setPostalCode(request.getPostalCode());
        user.setStatus(UserStatus.ACTIVE);
        user.setCreatedAt(LocalDateTime.now());
        User saved = userRepository.save(user);
        log.info("User created with id: {}", saved.getId());
        return saved;
    }

    @CacheEvict(value = "users", allEntries = true)
    public User updateUser(Long id, UserRequest request) {
        log.info("Updating user with id: {}", id);
        User existing = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("User not found with id: {}", id);
                    return new ResourceNotFoundException("User not found with id: ", id);
                });

        existing.setUsername(request.getUsername());
        existing.setEmail(request.getEmail());
        existing.setPhone(request.getPhone());
        existing.setStreet(request.getStreet());
        existing.setCity(request.getCity());
        existing.setCountry(request.getCountry());
        existing.setPostalCode(request.getPostalCode());

        if (request.getDefaultCurrency() != null && !request.getDefaultCurrency().isBlank()) {
            existing.setDefaultCurrency(request.getDefaultCurrency());
        }

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            existing.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        User updated = userRepository.save(existing);
        log.info("User updated with id: {}", updated.getId());
        return updated;
    }

    @CacheEvict(value = "users", allEntries = true)
    public User updateStatus(Long id, UserStatus status) {
        log.info("Updating status to {} for user with id: {}", status, id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("User not found with id: {}", id);
                    return new ResourceNotFoundException("User not found with id: ", id);
                });
        user.setStatus(status);
        return userRepository.save(user);
    }

    @CacheEvict(value = "users", allEntries = true)
    public void deleteUser(Long id) {
        log.info("Deleting user with id: {}", id);
        if (!userRepository.existsById(id)) {
            log.warn("User not found with id: {}", id);
            throw new ResourceNotFoundException("User not found with id: ", id);
        }
        userRepository.deleteById(id);
        log.info("User deleted with id: {}", id);
    }

    @CacheEvict(value = "users", allEntries = true)
    public void changePassword(String username, String currentPassword, String newPassword) {
        log.info("Changing password for user: {}", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", username));

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new BadRequestException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        log.info("Password changed for user: {}", username);
    }
}