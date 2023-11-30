package io.github.devtae.taskmanagementsystem.service;

import io.github.devtae.taskmanagementsystem.exception.TaskNotFoundException;
import io.github.devtae.taskmanagementsystem.model.Task;
import io.github.devtae.taskmanagementsystem.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskServiceImpl taskService;

    private Task task1;
    private Task task2;

    private final Long expectedId1 = 1L;
    private final String expectedTitle1 = "Test Task";
    private final String expectedDescription1 = "Test Task Description";
    private final Long expectedId2 = 2L;
    private final String expectedTitle2 = "Another Task";
    private final String expectedDescription2 = "Another Task Description";

    @BeforeEach
    void setUp() {
        task1 = new Task();
        task1.setId(expectedId1);
        task1.setTitle(expectedTitle1);
        task1.setDescription(expectedDescription1);

        task2 = new Task();
        task2.setId(expectedId2);
        task2.setTitle(expectedTitle2);
        task2.setDescription(expectedDescription2);
    }

    @Test
    void whenGetTaskById_thenReturnsTask() {
        when(taskRepository.findById(expectedId1)).thenReturn(Optional.of(task1));

        Task foundTask = taskService.getTaskById(expectedId1);

        assertThat(foundTask).isEqualTo(task1);
        assertThat(foundTask.getId()).isEqualTo(expectedId1);
        assertThat(foundTask.getTitle()).isEqualTo(expectedTitle1);
        assertThat(foundTask.getDescription()).isEqualTo(expectedDescription1);
    }

    @Test
    void whenGetTaskById_withNonExistentId_thenThrowsException() {
        Long taskId = 1L;
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.getTaskById(taskId))
                .isInstanceOf(TaskNotFoundException.class);
    }

    @Test
    void whenGetAllTasks_thenReturnsTasksList() {
        when(taskRepository.findAll()).thenReturn(Arrays.asList(task1, task2));

        List<Task> tasks = taskService.getAllTasks();

        assertThat(tasks).hasSize(2);
        assertThat(tasks.get(0)).isEqualTo(task1);
        assertThat(tasks.get(1)).isEqualTo(task2);
    }

    @Test
    void whenSaveTask_thenTaskIsSaved() {
        when(taskRepository.save(task1)).thenReturn(task1);

        Task savedTask = taskService.saveTask(task1);

        assertThat(savedTask).isNotNull();
        assertThat(savedTask).isEqualTo(task1);
        verify(taskRepository).save(task1);
    }

    @Test
    void whenUpdateTask_withChanges_thenTaskIsUpdated() {
        Task updatedTask = task1;
        String updatedTitle = "Updated Task";
        String updatedDescription = "Updated Task Description";
        updatedTask.setTitle(updatedTitle);
        updatedTask.setDescription(updatedDescription);

        when(taskRepository.findById(expectedId1)).thenReturn(Optional.of(task1));

        Task result = taskService.updateTask(expectedId1, updatedTask);

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo(updatedTitle);
        assertThat(result.getDescription()).isEqualTo(updatedDescription);
    }

    @Test
    void whenUpdateTask_withoutChanges_thenNoUpdateOccurs() {
        when(taskRepository.findById(expectedId1)).thenReturn(Optional.of(task1));

        Task result = taskService.updateTask(expectedId1, task1);

        verify(taskRepository, never()).save(any(Task.class));
        assertThat(result.getTitle()).isEqualTo(expectedTitle1);
        assertThat(result.getDescription()).isEqualTo(expectedDescription1);
    }

    @Test
    void whenCompleteTask_thenTaskIsMarkedCompleted() {
        when(taskRepository.findById(expectedId1)).thenReturn(Optional.of(task1));
        when(taskRepository.save(any(Task.class))).thenAnswer(i -> i.getArgument(0, Task.class));

        Task result = taskService.completeTask(expectedId1);

        assertThat(result.isCompleted()).isTrue();
    }

    @Test
    void whenDeleteTask_thenTaskIsDeleted() {
        when(taskRepository.findById(expectedId1)).thenReturn(Optional.of(task1));
        doNothing().when(taskRepository).delete(any(Task.class));

        taskService.deleteTask(expectedId1);

        verify(taskRepository).delete(task1);
    }
}