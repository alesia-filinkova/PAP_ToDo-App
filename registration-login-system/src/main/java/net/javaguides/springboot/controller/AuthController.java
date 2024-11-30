package net.javaguides.springboot.controller;

import jakarta.validation.Valid;
import net.javaguides.springboot.RegistrationLoginSystemApplication;
import net.javaguides.springboot.dto.TodoDto;
import net.javaguides.springboot.dto.UserDto;
import net.javaguides.springboot.dto.NoteDto;
import net.javaguides.springboot.entity.User;
import net.javaguides.springboot.service.TodoService;
import net.javaguides.springboot.service.UserService;
import net.javaguides.springboot.service.NoteService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class AuthController {

    private final UserService userService;
    private final TodoService todoService;
    private final NoteService noteService;
    private final RegistrationLoginSystemApplication registrationLoginSystemApplication;

    public AuthController(UserService userService, TodoService todoService, RegistrationLoginSystemApplication registrationLoginSystemApplication, NoteService noteService) {
        this.userService = userService;
        this.todoService = todoService;
        this.noteService = noteService;
        this.registrationLoginSystemApplication = registrationLoginSystemApplication;
    }

    @GetMapping("/index")
    public String home() {
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        UserDto user = new UserDto();
        model.addAttribute("user", user);
        return "register";
    }

    @PostMapping("/register/save")
    public String registration(@Valid @ModelAttribute("user") UserDto userDto,
                               BindingResult result,
                               Model model) {
        User existingUser = userService.findUserByEmail(userDto.getEmail());

        if (existingUser != null && existingUser.getEmail() != null && !existingUser.getEmail().isEmpty()) {
            result.rejectValue("email", null,
                    "There is already an account registered with the same email");
        }

        if (result.hasErrors()) {
            model.addAttribute("user", userDto);
            return "/register";
        }

        userService.saveUser(userDto);
        return "redirect:/register?success";
    }

    @GetMapping("/users")
    public String users(Model model) {
        List<UserDto> users = userService.findAllUsers();
        model.addAttribute("users", users);
        return "users";
    }

    @GetMapping("/todos")
    public String todos(Model model) {
        List<TodoDto> todos = todoService.getAllTodosByUser();
        model.addAttribute("todos", todos);
        model.addAttribute("todo", new TodoDto());
        return "todos";
    }

    @PostMapping("/todos/save")
    public String addTodo(@Valid @ModelAttribute("todo") TodoDto todo,
                          BindingResult result,
                          Model model) {
        if (result.hasErrors()) {
            List<TodoDto> todos = todoService.getAllTodosByUser();
            model.addAttribute("todos", todos);
            model.addAttribute("todo", todo);
            return "todos";
        }

        todoService.addTodo(todo);
        return "redirect:/todos";
    }

    @GetMapping("/todos/{todoId}/delete")
    public String deleteTodo(@PathVariable("todoId") Long todoId) {
        todoService.deleteTodo(todoId);
        return "redirect:/todos";
    }

    @GetMapping("/notes")
    public String notes(Model model) {
        List<NoteDto> notes = noteService.getAllNotesByUser();
        model.addAttribute("notes", notes);
        model.addAttribute("note", new NoteDto());
        return "notes";
    }

    @PostMapping("/notes/save")
    public String addNote(@ModelAttribute("note") NoteDto noteDto) {
        noteService.addNote(noteDto);
        return "redirect:/notes";
    }

    @PostMapping("/notes/{id}/update")
    public ResponseEntity<Void> updateNote(@PathVariable Long id, @RequestBody NoteDto noteDto) {
        try {
            NoteDto updatedNote = noteService.updateNote(noteDto, id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @DeleteMapping("/notes/{id}/delete")
    public ResponseEntity<Void> deleteNoteById(@PathVariable Long id) {
        try {
            noteService.deleteNoteById(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}
