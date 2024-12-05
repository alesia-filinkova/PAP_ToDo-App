package net.javaguides.springboot.controller;

import net.javaguides.springboot.RegistrationLoginSystemApplication;
import net.javaguides.springboot.dto.TodoDto;
import net.javaguides.springboot.service.NoteService;
import net.javaguides.springboot.service.TodoService;
import net.javaguides.springboot.service.UserService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TodoService todoService;

    @MockBean
    private UserService userService;

    @MockBean
    private NoteService noteService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @Test
    @WithMockUser(username = "fff@gmail.com", roles = {"USER"})
    public void testAddTodo() throws Exception {
        TodoDto todoDto = new TodoDto();
        todoDto.setTitle("New todo");
        todoDto.setDescription("My new todo description");
        todoDto.setCompleted(false);
        todoDto.setDeadline(LocalDate.now());
        todoDto.setPriority("Low");

        mockMvc.perform(post("/todos/save")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("title", todoDto.getTitle())
                        .param("description", todoDto.getDescription())
                        .param("completed", String.valueOf(todoDto.getCompleted()))
                        .param("deadline", todoDto.getDeadline().toString())
                        .param("priority", todoDto.getPriority()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/todos"));
    }

    @Test
    @WithMockUser(username = "fff@gmail.com", roles = {"USER"})
    public void testTodosView() throws Exception {
        List<TodoDto> todos = List.of(
                new TodoDto(102L, "Todo 1", "Description 1", false, LocalDate.now(), "Low"),
                new TodoDto(103L, "Todo 2", "Description 2", true, LocalDate.now().plusDays(1), "High")
        );

        Mockito.when(todoService.getAllTodosByUser()).thenReturn(todos);

        mockMvc.perform(get("/todos"))
                .andExpect(status().isOk()) // Sprawdza, czy status odpowiedzi to 200 OK
                .andExpect(model().attribute("todos", Matchers.hasSize(2))) // Sprawdza, czy lista "todos" zawiera dwa zadania
                .andExpect(model().attribute("todo", Matchers.hasProperty("title", Matchers.nullValue())))
                .andExpect(model().attribute("todo", Matchers.hasProperty("description", Matchers.nullValue())))
                .andExpect(model().attribute("todo", Matchers.hasProperty("completed", Matchers.nullValue())))
                .andExpect(model().attribute("todo", Matchers.hasProperty("deadline", Matchers.nullValue())))
                .andExpect(model().attribute("todo", Matchers.hasProperty("priority", Matchers.nullValue())));
    }

    @Test
    @WithMockUser(username = "fff@gmail.com", roles = {"USER"})
    public void testDeleteTodo() throws Exception {
        Long todoId = 102L;

        Mockito.doNothing().when(todoService).deleteTodo(todoId);

        mockMvc.perform(get("/todos/{todoId}/delete", todoId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/todos"));

        Mockito.verify(todoService, Mockito.times(1)).deleteTodo(todoId);
    }
}

