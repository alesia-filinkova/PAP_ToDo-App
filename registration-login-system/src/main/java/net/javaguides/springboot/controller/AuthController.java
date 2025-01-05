package net.javaguides.springboot.controller;

import jakarta.validation.Valid;
import net.javaguides.springboot.CurrentUser;
import net.javaguides.springboot.RegistrationLoginSystemApplication;
import net.javaguides.springboot.dto.SettingsDto;
import net.javaguides.springboot.dto.TodoDto;
import net.javaguides.springboot.dto.UserDto;
import net.javaguides.springboot.dto.NoteDto;
import net.javaguides.springboot.entity.User;
import net.javaguides.springboot.service.TodoService;
import net.javaguides.springboot.service.UserService;
import net.javaguides.springboot.service.NoteService;
import net.javaguides.springboot.service.impl.UserServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;




import java.util.List;

@Controller
public class AuthController {

    private final UserService userService;
    private final TodoService todoService;
    private final NoteService noteService;
    private final RegistrationLoginSystemApplication registrationLoginSystemApplication;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserService userService, TodoService todoService, RegistrationLoginSystemApplication registrationLoginSystemApplication, NoteService noteService,PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.todoService = todoService;
        this.noteService = noteService;
        this.registrationLoginSystemApplication = registrationLoginSystemApplication;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/index")
    public String home() {
        return "index";
    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("oauth2LoginUrl", "/oauth2/authorization/google");
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

    @GetMapping("/settings")
    public String setting(Model model) {
        UserDto userDto = new UserDto();
        userDto.setId(CurrentUser.user.getId());
        userDto.setName(CurrentUser.user.getName());
        userDto.setEmail(CurrentUser.user.getEmail());
        userDto.setPassword(CurrentUser.user.getPassword());
        model.addAttribute("user", userDto);
        return "settings";
    }

    @PostMapping("/settings/save")
    public String settings2(@Valid @ModelAttribute("user") SettingsDto user,
                            BindingResult result,
                            Model model) {
        User existingUser = userService.findUserByEmail(user.getEmail());

        if ((existingUser != null && existingUser.getEmail() != null && !existingUser.getEmail().isEmpty()) && existingUser.getId() != null && !existingUser.getId().equals(CurrentUser.user.getId())) {
            result.rejectValue("email", null,
                    "There is already an account registered with the same email");
        }

        if (result.hasErrors()) {
            model.addAttribute("user", user);
            return "settings";
        }
        userService.updateUserInformation(user);
        return "redirect:/settings?success";
    }

    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) {
        userService.sendPasswordResetToken(email);
        return ResponseEntity.ok("Password reset email sent!");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String token, @RequestParam String newPassword) {
        userService.resetPassword(token, newPassword);
        return ResponseEntity.ok("Password has been reset successfully!");
    }

    @GetMapping("/oauth2/authorization/google")
    public String redirectToGoogle() {
        return "redirect:/oauth2/authorization/google";
    }

    @GetMapping("/oauth2/success")
    public String oauth2Success(@AuthenticationPrincipal OAuth2User principal) {
        // Получаем email пользователя из данных Google
        String email = principal.getAttribute("email");
        String name = principal.getAttribute("name");

        // Ищем пользователя в базе данных
        User existingUser = userService.findUserByEmail(email);

        if (existingUser == null) {
            UserDto newUser = new UserDto();
            newUser.setEmail(email);
            newUser.setName(name);
            newUser.setPassword("");
            userService.saveUser(newUser);

            existingUser = userService.findUserByEmail(email);
        }

        CurrentUser.user = existingUser;

        return "redirect:/todos";
    }

    @GetMapping("/calendar")
    public String showCalendarPage() {
        return "calendar";
    }


    @GetMapping("/oauth2/error")
    public String oauth2Error() {
        return "error"; // Перенаправляем на страницу ошибки (например, error.html)
    }



}
