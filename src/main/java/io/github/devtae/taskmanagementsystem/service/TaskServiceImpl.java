package io.github.devtae.taskmanagementsystem.service;

import io.github.devtae.taskmanagementsystem.exception.TaskNotFoundException;
import io.github.devtae.taskmanagementsystem.model.Task;
import io.github.devtae.taskmanagementsystem.repository.TaskRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;

    public TaskServiceImpl(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    @Transactional
    public Task saveTask(Task task) {
        return taskRepository.save(task);
    }

    @Override
    @Transactional
    public Task updateTask(Long taskId, Task taskDetails) {
        Task existingTask = getTaskById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with id: " + taskId));

        boolean needsUpdate = checkForUpdates(existingTask, taskDetails);

        if (needsUpdate) {
            return taskRepository.save(existingTask);
        } else {
            return existingTask;
        }
    }

    @Override
    @Transactional
    public void deleteTask(Long taskId) {
        // Check if the task exists before attempting to delete.
        Task task = getTaskById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with id: " + taskId));
        taskRepository.delete(task);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Task> getTaskById(Long taskId) {
        // This method returns Optional, which is then handled in the calling code.
        return taskRepository.findById(taskId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    @Override
    @Transactional
    public Task completeTask(Long taskId) {
        Task task = getTaskById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with id: " + taskId));
        task.setCompleted(true);
        return taskRepository.save(task);
    }

    /**
     * Checks if the current task entity has updates compared to the details provided.
     * Compares editable fields of the task to see if any changes were made.
     *
     * @param existingTask the existing task to update
     * @param taskDetails the task details to compare against.
     * @return true if there are changes, false otherwise.
     */
    private boolean checkForUpdates(Task existingTask, Task taskDetails) {
        boolean updated = false;
        // Check title for changes, as it should not be null or empty
        if (!Objects.equals(existingTask.getTitle(), taskDetails.getTitle())) {
            existingTask.setTitle(taskDetails.getTitle());
            updated = true;
        }
        // Check description for changes. It could be an update from non-empty to empty.
        if (!Objects.equals(existingTask.getDescription(), taskDetails.getDescription())) {
            existingTask.setDescription(taskDetails.getDescription());
            updated = true;
        }
        // Check dueDate for changes. The date might have been adjusted.
        if (!Objects.equals(existingTask.getDueDate(), taskDetails.getDueDate())) {
            existingTask.setDueDate(taskDetails.getDueDate());
            updated = true;
        }
        // Check completed status for changes, which can be toggled by the user.
        if (existingTask.isCompleted() != taskDetails.isCompleted()) {
            existingTask.setCompleted(taskDetails.isCompleted());
            updated = true;
        }

        return updated;
    }
}
