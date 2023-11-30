package io.github.devtae.taskmanagementsystem.service;

import io.github.devtae.taskmanagementsystem.model.Task;

import java.util.List;

public interface TaskService {
    Task getTaskById(Long taskId);
    List<Task> getAllTasks();
    Task saveTask(Task task);
    Task updateTask(Long taskId, Task task);
    Task completeTask(Long taskId);
    void deleteTask(Long taskId);
}

