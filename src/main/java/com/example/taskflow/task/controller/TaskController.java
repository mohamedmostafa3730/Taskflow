package com.example.taskflow.task.controller;

import com.example.taskflow.exception.TaskNotFoundException;
import com.example.taskflow.task.entity.Task;
import com.example.taskflow.task.repository.ITaskRepository;
import com.example.taskflow.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class TaskController {

    private final ITaskRepository taskRepository;

    // ── helper: pull the logged-in User out of the SecurityContext ──
    private User currentUser() {
        return (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }

    // ── helper: load a task that belongs to the current user, or 404 ──
    private Task ownedTaskOrThrow(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() ->
                        new TaskNotFoundException("Task not found with id [" + id + "]"));

        if (!task.getUser().getId().equals(currentUser().getId())) {
            throw new TaskNotFoundException("Task not found with id [" + id + "]");
        }
        return task;
    }

    // ─── LIST ──────────────────────────────────────────────────────
    @GetMapping({"/", "/home"})
    public String showHomePage(Model model) {
        model.addAttribute("tasks", taskRepository.findByUser(currentUser()));
        return "index";
    }

    // ─── ADD ───────────────────────────────────────────────────────
    @PostMapping("/add")
    public String add(@RequestParam String title) {
        Task task = Task.builder()
                .title(title)
                .status(false)
                .user(currentUser())   // ← stamp the owner
                .build();
        taskRepository.save(task);
        return "redirect:/";
    }

    // ─── TOGGLE STATUS ─────────────────────────────────────────────
    // POST so the browser sends a CSRF token automatically via th:action
    @PostMapping("/update/{id}")
    public String update(@PathVariable Long id) {
        Task task = ownedTaskOrThrow(id);
        task.setStatus(!task.isStatus());
        taskRepository.save(task);
        return "redirect:/";
    }

    // ─── DELETE ────────────────────────────────────────────────────
    // POST so the browser sends a CSRF token automatically via th:action
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        Task task = ownedTaskOrThrow(id);
        taskRepository.delete(task);
        return "redirect:/";
    }
}