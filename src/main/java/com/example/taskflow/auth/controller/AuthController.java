package com.example.taskflow.auth.controller;

import com.example.taskflow.auth.dto.AuthResponse;
import com.example.taskflow.auth.dto.LoginRequest;
import com.example.taskflow.auth.dto.RegisterRequest;
import com.example.taskflow.auth.service.AuthenticationService;
import com.example.taskflow.user.entity.User;
import com.example.taskflow.user.service.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
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
    public String showLoginPage(Model model) {
        // error / success arrive as flash attributes and are already
        // merged into the model by Spring before this method runs.
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        HttpServletResponse response,
                        RedirectAttributes redirectAttributes) {
        try {
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
            redirectAttributes.addFlashAttribute("error", "Invalid email or password");
            return "redirect:/auth/login";
        }
    }

    // ─── SIGNUP ────────────────────────────────────────────────

    @GetMapping("/signup")
    public String showSignupPage(Model model) {
        // flash attributes (error / name / email) are already in the model
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(@RequestParam String username,
                         @RequestParam String email,
                         @RequestParam String password,
                         @RequestParam String confirmPassword,
                         HttpSession session,
                         RedirectAttributes redirectAttributes) {
        try {
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

            // Store email in the session so the verify page can read it
            // even after a browser refresh or direct URL access.
            session.setAttribute("pendingEmail", email);

            redirectAttributes.addFlashAttribute("email", email);
            redirectAttributes.addFlashAttribute("success",
                    "Account created! Check your email for the verification code.");
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
    public String showVerifyPage(Model model, HttpSession session) {
        // Flash attributes (email, success, error) are merged automatically.
        // Fall back to the session-stored email if flash didn't carry one
        // (e.g. the user refreshed the page).
        if (!model.containsAttribute("email") || "".equals(model.getAttribute("email"))) {
            String sessionEmail = (String) session.getAttribute("pendingEmail");
            model.addAttribute("email", sessionEmail != null ? sessionEmail : "");
        }
        return "verify";
    }

    @PostMapping("/verify")
    public String verify(@RequestParam String email,
                         @RequestParam("otp") String[] otpDigits,
                         HttpSession session,
                         RedirectAttributes redirectAttributes) {
        try {
            String code = String.join("", otpDigits);

            AuthResponse authResponse = new AuthResponse();
            authResponse.setEmail(email);
            authResponse.setVerificationCode(code);

            authenticationService.VerifyUser(authResponse);

            // Verification complete — clear the pending-email from session
            session.removeAttribute("pendingEmail");

            redirectAttributes.addFlashAttribute("success",
                    "Email verified! You can now sign in.");
            return "redirect:/auth/login";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addFlashAttribute("email", email);
            return "redirect:/auth/verify";
        }
    }

    // ─── RESEND ────────────────────────────────────────────────

    @GetMapping("/resend")
    public String showResendPage(Model model, HttpSession session) {
        // Same fallback strategy as the verify page
        if (!model.containsAttribute("email") || "".equals(model.getAttribute("email"))) {
            String sessionEmail = (String) session.getAttribute("pendingEmail");
            model.addAttribute("email", sessionEmail != null ? sessionEmail : "");
        }
        return "resend";
    }

    @PostMapping("/resend")
    public String resend(@RequestParam String email,
                         HttpSession session,
                         RedirectAttributes redirectAttributes) {
        try {
            authenticationService.resendVerificationCode(email);

            // Keep the session in sync in case it was empty
            session.setAttribute("pendingEmail", email);

            redirectAttributes.addFlashAttribute("email", email);
            redirectAttributes.addFlashAttribute("success",
                    "A new verification code has been sent to your email.");
            return "redirect:/auth/verify";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addFlashAttribute("email", email);
            return "redirect:/auth/resend";
        }
    }
}