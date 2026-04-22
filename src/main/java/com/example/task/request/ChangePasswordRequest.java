package com.example.task.request;

public record ChangePasswordRequest(String currentPassword, String newPassword) {
}
