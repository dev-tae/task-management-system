package io.github.devtae.taskmanagementsystem.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.devtae.taskmanagementsystem.controller.TaskController;
import io.github.devtae.taskmanagementsystem.model.Task;
import io.github.devtae.taskmanagementsystem.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = {TaskController.class, GlobalExceptionHandler.class})
public class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TaskService taskService;

    // Test for TaskNotFoundException handling
    @Test
    public void whenTaskNotFoundExceptionThrown_thenRespondWithNotFoundStatus() throws Exception {
        // Given
        given(taskService.getTaskById(anyLong())).willThrow(new TaskNotFoundException("Not found"));

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/tasks/{id}", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Task not found")))
                .andExpect(jsonPath("$.timestamp", is(notNullValue())));
    }

    @Test
    public void whenValidationExceptionThrown_thenRespondWithBadRequestStatus() throws Exception {
        Task invalidTask = new Task();
        invalidTask.setTitle(null); // Null title should fail @NotBlank validation

        // Perform a post request with the invalid task object
        mockMvc.perform(MockMvcRequestBuilders.post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(invalidTask))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.timestamp", is(notNullValue())));
    }

    @Test
    public void whenGeneralExceptionThrown_thenRespondWithInternalServerErrorStatus() throws Exception {
        given(taskService.getTaskById(null)).willThrow(new RuntimeException("Unexpected error"));

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/tasks/some-invalid-endpoint")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.errors", is("An error occurred")))
                .andExpect(jsonPath("$.timestamp", is(notNullValue())));
    }
}
