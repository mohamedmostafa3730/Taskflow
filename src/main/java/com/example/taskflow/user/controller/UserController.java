package com.example.taskflow.user.controller;

import com.example.taskflow.user.dto.UserResponse;
import com.example.taskflow.user.entity.User;
import com.example.taskflow.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // ── map an entity → safe DTO ───────────────────────────────────
    private static UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.isEnabled()
        );
    }

    /**
     * Returns the currently authenticated user (safe fields only).
     */
    @GetMapping("/me")
    public ResponseEntity<UserResponse> currentUser() {
        User user = (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        return ResponseEntity.ok(toResponse(user));
    }

    /**
     * Returns every user in the system — admin only.
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> allUsers() {
        List<UserResponse> responses = userService.allUsers()
                .stream()
                .map(UserController::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }
}