package com.example.taskflow.task.controller;

import com.example.taskflow.exception.TaskNotFoundException;
import com.example.taskflow.task.entity.Task;
import com.example.taskflow.task.repository.ITaskRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Test;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class TaskController {

    private final ITaskRepository taskRepository;


    @GetMapping({"/", " ", "/home", ""})
    public String showHomePage(Model modle) {
        modle.addAttribute("tasks", taskRepository.findAll());
        return "index";
    }

    @PostMapping("/add")
    public String add(@RequestParam String title) {
        Task task = Task.builder()
                .title(title)
                .status(false).build();
        taskRepository.save(task);
        return "redirect:/";
    }

    @GetMapping("/update/{id}")
    public String update(@PathVariable Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("This Task Is not Found with [" + id + "]"));
        task.setStatus(!task.isStatus());
        taskRepository.save(task);
        return "redirect:/";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("This Task Is not Found with [" + id + "]"));
        taskRepository.delete(task);
        return "redirect:/";
    }
}
