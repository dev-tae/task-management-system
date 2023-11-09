package io.github.devtae.taskmanagementsystem.service;

import io.github.devtae.taskmanagementsystem.exception.TaskNotFoundException;
import io.github.devtae.taskmanagementsystem.model.Task;
import io.github.devtae.taskmanagementsystem.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;

    @Autowired
    public TaskServiceImpl(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public Task saveTask(Task task) {
        // Save can be used for both creating and updating.
        // If task.getId() is null, it's a create operation, otherwise it's an update.
        return taskRepository.save(task);
    }

    @Override
    public Task updateTask(Long taskId, Task taskDetails) {
        // Find the existing task and throw an exception if not found.
        Task existingTask = getTaskById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with id: " + taskId));

        // Check for each field in taskDetails if it should be updated
        if (taskDetails.getTitle() != null && !taskDetails.getTitle().isEmpty()) {
            existingTask.setTitle(taskDetails.getTitle());
        }
        if (taskDetails.getDescription() != null && !taskDetails.getDescription().isEmpty()) {
            existingTask.setDescription(taskDetails.getDescription());
        }
        if (taskDetails.getDueDate() != null) {
            existingTask.setDueDate(taskDetails.getDueDate());
        }
        // Completed Status can't be undone
        existingTask.setCompleted(taskDetails.isCompleted());

        // Save only if there's an actual change
        if (existingTask.hasUpdates(taskDetails)) {
            return taskRepository.save(existingTask);
        } else {
            return existingTask;
        }
    }

    @Override
    public void deleteTask(Long taskId) {
        // Check if the task exists before attempting to delete.
        Task task = getTaskById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with id: " + taskId));
        taskRepository.delete(task);
    }

    @Override
    public Optional<Task> getTaskById(Long taskId) {
        // This method returns Optional, which is then handled in the calling code.
        return taskRepository.findById(taskId);
    }

    @Override
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    @Override
    public Task completeTask(Long taskId) {
        Task task = getTaskById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with id: " + taskId));
        task.setCompleted(true);
        return taskRepository.save(task);
    }
}
