package com.example.taskflow.task.repository;

import com.example.taskflow.task.entity.Task;
import com.example.taskflow.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ITaskRepository extends JpaRepository<Task, Long> {

    /**
     * Returns only the tasks that belong to the given user.
     */
    List<Task> findByUser(User user);
}