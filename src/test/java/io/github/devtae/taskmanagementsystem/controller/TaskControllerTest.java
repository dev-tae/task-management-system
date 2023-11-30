package io.github.devtae.taskmanagementsystem.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.devtae.taskmanagementsystem.model.Task;
import io.github.devtae.taskmanagementsystem.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(TaskController.class)
public class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

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
        task1.setId(1L);
        task1.setTitle("Test Task");
        task1.setDescription("Test Task Description");

        task2 = new Task();
        task2.setId(2L);
        task2.setTitle("Another Task");
        task2.setDescription("Another Task Description");

    }

    @Test
    public void whenGetTaskById_thenReturns200() throws Exception {
        given(taskService.getTaskById(1L)).willReturn(Optional.of(task1));

        mockMvc.perform(get("/api/tasks/{taskId}", expectedId1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expectedId1))
                .andExpect(jsonPath("$.title").value(expectedTitle1))
                .andExpect(jsonPath("$.description").value(expectedDescription1));
    }

    @Test
    public void whenGetAllTask_thenReturns200() throws Exception {
        given(taskService.getAllTasks()).willReturn(Arrays.asList(task1, task2));

        mockMvc.perform(get("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(expectedId1))
                .andExpect(jsonPath("$[0].title").value(expectedTitle1))
                .andExpect(jsonPath("$[0].description").value(expectedDescription1))
                .andExpect(jsonPath("$[1].id").value(expectedId2))
                .andExpect(jsonPath("$[1].title").value(expectedTitle2))
                .andExpect(jsonPath("$[1].description").value(expectedDescription2));
    }

    @Test
    public void whenCreateTask_thenReturns201() throws Exception {
        given(taskService.saveTask(task1)).willReturn(task1);

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(task1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(expectedId1))
                .andExpect(jsonPath("$.title").value(expectedTitle1))
                .andExpect(jsonPath("$.description").value(expectedDescription1));
    }

    @Test
    public void whenUpdateTask_thenReturns200() throws Exception {
        String updatedTitle1 = "Updated Task";
        String updatedDescription1 = "Updated Description";

        Task updatedTask = task1;
        updatedTask.setTitle(updatedTitle1);
        updatedTask.setDescription(updatedDescription1);

        given(taskService.updateTask(expectedId1, updatedTask)).willReturn(updatedTask);

        mockMvc.perform(put("/api/tasks/{taskId}", expectedId1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(updatedTask)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expectedId1))
                .andExpect(jsonPath("$.title").value(updatedTitle1))
                .andExpect(jsonPath("$.description").value(updatedDescription1));
    }

    @Test
    public void whenDeleteTask_thenReturns204() throws Exception {
        doNothing().when(taskService).deleteTask(expectedId2);

        mockMvc.perform(delete("/api/tasks/{taskId}", expectedId2)
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isNoContent());
    }
}