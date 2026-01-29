package com.example.taskflow.task.repository;

import com.example.taskflow.task.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ITaskRepository extends JpaRepository<Task, Long> {
}
