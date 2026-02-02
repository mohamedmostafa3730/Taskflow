package com.example.taskflow.auth.controller;

import com.example.taskflow.auth.dto.AuthResponse;
import com.example.taskflow.auth.dto.LoginRequest;
import com.example.taskflow.auth.dto.RegisterRequest;
import com.example.taskflow.auth.service.AuthenticationService;
import com.example.taskflow.user.entity.User;
import com.example.taskflow.user.service.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtService jwtService;
    private final AuthenticationService authenticationService;

    // ─── LOGIN ─────────────────────────────────────────────────

    @GetMapping("/login")
    public String showLoginPage(Model model, @RequestParam(required = false) String error) {
        if (error != null) {
            model.addAttribute("error", "Invalid email or password");
        }
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        HttpServletResponse response,
                        RedirectAttributes redirectAttributes) {
        try {
            // Build a LoginRequest and authenticate
            LoginRequest loginRequest = new LoginRequest();
            loginRequest.setEmail(email);
            loginRequest.setPassword(password);

            User authenticatedUser = authenticationService.authenticate(loginRequest);

            // Generate JWT and store in HttpOnly cookie
            String jwtToken = jwtService.generateToken(authenticatedUser);
            Cookie cookie = new Cookie("jwt", jwtToken);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge(3600); // 1 hour
            response.addCookie(cookie);

            return "redirect:/";

        } catch (Exception e) {
            redirectAttributes.addAttribute("error", "true");
            return "redirect:/auth/login";
        }
    }

    // ─── SIGNUP ────────────────────────────────────────────────

    @GetMapping("/signup")
    public String showSignupPage(Model model) {
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(@RequestParam String username,
                         @RequestParam String email,
                         @RequestParam String password,
                         @RequestParam String confirmPassword,
                         RedirectAttributes redirectAttributes) {
        try {
            // Passwords must match
            if (!password.equals(confirmPassword)) {
                redirectAttributes.addFlashAttribute("error", "Passwords do not match");
                redirectAttributes.addFlashAttribute("email", email);
                redirectAttributes.addFlashAttribute("name", username);
                return "redirect:/auth/signup";
            }

            RegisterRequest registerRequest = new RegisterRequest();
            registerRequest.setUsername(username);
            registerRequest.setEmail(email);
            registerRequest.setPassword(password);

            authenticationService.signup(registerRequest);

            // Redirect to verify page, pass email so it knows where the code was sent
            redirectAttributes.addFlashAttribute("email", email);
            redirectAttributes.addFlashAttribute("success", "Account created! Check your email for the verification code.");
            return "redirect:/auth/verify";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addFlashAttribute("email", email);
            redirectAttributes.addFlashAttribute("name", username);
            return "redirect:/auth/signup";
        }
    }

    // ─── VERIFY ────────────────────────────────────────────────

    @GetMapping("/verify")
    public String showVerifyPage(Model model) {
        // email and success come via flash attributes after signup redirect
        // If accessed directly without flash attributes, email will be null
        if (!model.containsAttribute("email")) {
            model.addAttribute("email", "");
        }
        return "verify";
    }

    @PostMapping("/verify")
    public String verify(@RequestParam String email,
                         @RequestParam("otp") String[] otpDigits,
                         RedirectAttributes redirectAttributes) {
        try {
            // Join the 6 individual digit inputs into one code string
            String code = String.join("", otpDigits);

            AuthResponse authResponse = new AuthResponse();
            authResponse.setEmail(email);
            authResponse.setVerificationCode(code);

            authenticationService.VerifyUser(authResponse);

            // Verified — send to login with success message
            redirectAttributes.addFlashAttribute("success", "Email verified! You can now sign in.");
            return "redirect:/auth/login";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addFlashAttribute("email", email);
            return "redirect:/auth/verify";
        }
    }

    // ─── RESEND ────────────────────────────────────────────────

    @GetMapping("/resend")
    public String showResendPage(Model model) {
        // If email is already in flash attributes (from verify page), keep it
        if (!model.containsAttribute("email")) {
            model.addAttribute("email", "");
        }
        return "resend";
    }

    @PostMapping("/resend")
    public String resend(@RequestParam String email,
                         RedirectAttributes redirectAttributes) {
        try {
            authenticationService.resendVerificationCode(email);

            redirectAttributes.addFlashAttribute("email", email);
            redirectAttributes.addFlashAttribute("success", "A new verification code has been sent to your email.");
            return "redirect:/auth/verify";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addFlashAttribute("email", email);
            return "redirect:/auth/resend";
        }
    }
}