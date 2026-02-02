package com.example.taskflow.auth.service;

import com.example.taskflow.auth.email.EmailService;
import com.example.taskflow.auth.dto.AuthResponse;
import com.example.taskflow.auth.dto.LoginRequest;
import com.example.taskflow.auth.dto.RegisterRequest;
import com.example.taskflow.user.entity.User;
import com.example.taskflow.user.repository.UserRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    // ─── SIGNUP ────────────────────────────────────────────────
    public User signup(RegisterRequest request) {
        // Check if email already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email is already registered");
        }

        // Create user
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEnabled(false);

        // Generate and set verification code
        String code = generateVerificationCode();
        user.setVerificationCode(code);
        user.setVerificationCodeExpiredAt(LocalDateTime.now().plusMinutes(10));

        User savedUser = userRepository.save(user);

        // Send verification email
        try {
            emailService.sendVerificationEmail(savedUser.getEmail(), code);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send verification email");
        }

        return savedUser;
    }

    // ─── LOGIN ─────────────────────────────────────────────────
    public User authenticate(LoginRequest request) {
        // This will throw BadCredentialsException if credentials are wrong
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // Credentials valid — load the user
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Block login if email is not verified
        if (!user.isEnabled()) {
            throw new RuntimeException("Email is not verified. Please check your inbox.");
        }

        return user;
    }

    // ─── VERIFY ────────────────────────────────────────────────
    public void VerifyUser(AuthResponse request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Already verified
        if (user.isEnabled()) {
            throw new RuntimeException("Account is already verified");
        }

        // Code mismatch
        if (!user.getVerificationCode().equals(request.getVerificationCode())) {
            throw new RuntimeException("Invalid verification code");
        }

        // Code expired
        if (user.getVerificationCodeExpiredAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Verification code has expired");
        }

        // All good — enable user and clear the code
        user.setEnabled(true);
        user.setVerificationCode(null);
        user.setVerificationCodeExpiredAt(null);
        userRepository.save(user);
    }

    // ─── RESEND ────────────────────────────────────────────────
    public void resendVerificationCode(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with this email"));

        if (user.isEnabled()) {
            throw new RuntimeException("Account is already verified");
        }

        // Generate new code and expiry
        String code = generateVerificationCode();
        user.setVerificationCode(code);
        user.setVerificationCodeExpiredAt(LocalDateTime.now().plusMinutes(10));
        userRepository.save(user);

        // Send email
        try {
            emailService.sendVerificationEmail(email, code);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send verification email");
        }
    }

    // ─── HELPER ────────────────────────────────────────────────
    private String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // always 6 digits
        return String.valueOf(code);
    }
}