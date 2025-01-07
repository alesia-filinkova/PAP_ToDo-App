package net.javaguides.springboot.service.impl;

import lombok.AllArgsConstructor;
import net.javaguides.springboot.config.SpringSecurity;
import net.javaguides.springboot.dto.TodoDto;
import net.javaguides.springboot.entity.Todo;
import net.javaguides.springboot.repository.TodoRepository;
import net.javaguides.springboot.repository.UserRepository;
import net.javaguides.springboot.service.TodoService;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TodoServiceImpl implements TodoService {

    private TodoRepository todoRepository;
    private UserRepository userRepository;

    @Override
    public void addTodo(TodoDto todoDto) {
        Todo todo = new Todo();
        todo.setTitle(todoDto.getTitle());
        todo.setDescription(todoDto.getDescription());
        todo.setUser(userRepository.findByEmail(SpringSecurity.getCurrentUserName()).get());
        todo.setDeadline(todoDto.getDeadline());
        todo.setPriority(todoDto.getPriority());

        todoRepository.save(todo);
    }

    @Override
    public TodoDto getTodo(Long id) {
        Todo todo = todoRepository.findById(id).get();
        return mapToTodoDto(todo);
    }

    @Override
    public List<TodoDto> getAllTodos() {
        List<Todo> todos = todoRepository.findAll();
        return todos.stream().map((todo) -> mapToTodoDto(todo)).collect(Collectors.toList());
    }


    @Override
    public List<TodoDto> getAllTodosByUser() {
        Long userId = userRepository.findByEmail(SpringSecurity.getCurrentUserName()).get().getId();
        List<Todo> todos = todoRepository.findTodoByUserId(userId);
        return todos.stream().map(this::mapToTodoDto).collect(Collectors.toList());
    }



    @Override
    public TodoDto updateTodo(TodoDto todoDto, Long id) {
        Todo todo = todoRepository.findById(id).get();
        todo.setTitle(todoDto.getTitle());
        todo.setDescription(todoDto.getDescription());
        todo.setCompleted(todoDto.getCompleted());
        todo.setDeadline(todoDto.getDeadline());
        todo.setPriority(todoDto.getPriority());

        Todo updatedTodo = todoRepository.save(todo);

        return mapToTodoDto(updatedTodo);
    }

    @Override
    public void deleteTodo(Long id) {
        Todo todo = todoRepository.findById(id).get();
        todoRepository.deleteById(id);
    }

    @Override
    public TodoDto completeTodo(Long id) {
        return null;
    }

    @Override
    public TodoDto incompleteTodo(Long id) {
        return null;
    }

    private TodoDto mapToTodoDto(Todo todo){
        TodoDto todoDto = new TodoDto(
                todo.getId(),
                todo.getTitle(),
                todo.getDescription(),
                todo.getCompleted(),
                todo.getDeadline(),
                todo.getPriority()
        );

        return todoDto;
    }

    private Todo mapToTodo(TodoDto todoDto){
        Todo todo = new Todo(
                todoDto.getId(),
                todoDto.getTitle(),
                todoDto.getDescription(),
                todoDto.getCompleted(),
                userRepository.findByEmail(SpringSecurity.getCurrentUserName()).get(),
                todoDto.getDeadline(),
                todoDto.getPriority()
        );
        return todo;
    }

}
