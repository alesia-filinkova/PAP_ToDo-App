package net.javaguides.springboot.controller;

import jakarta.validation.Valid;
import net.javaguides.springboot.RegistrationLoginSystemApplication;
import net.javaguides.springboot.dto.TodoDto;
import net.javaguides.springboot.dto.UserDto;
import net.javaguides.springboot.entity.Todo;
import net.javaguides.springboot.entity.User;
import net.javaguides.springboot.service.TodoService;
import net.javaguides.springboot.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class AuthController {

    private final UserService userService;
    private final TodoService todoService;
    private final RegistrationLoginSystemApplication registrationLoginSystemApplication;

    public AuthController(UserService userService, TodoService todoService, RegistrationLoginSystemApplication registrationLoginSystemApplication) {
        this.userService = userService;
        this.todoService = todoService;
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
}

