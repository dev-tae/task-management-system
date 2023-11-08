package io.github.devtae.taskmanagementsystem.service;

import io.github.devtae.taskmanagementsystem.model.Task;

import java.util.List;
import java.util.Optional;

public interface TaskService {
    Task saveTask(Task task);

    Task updateTask(Long taskId, Task task);
    void deleteTask(Long taskId);
    Optional<Task> getTaskById(Long taskId);
    List<Task> getAllTasks();
}

