package net.javaguides.springboot.service.impl;

import lombok.AllArgsConstructor;
import net.javaguides.springboot.CurrentUser;
import net.javaguides.springboot.dto.TodoDto;
import net.javaguides.springboot.dto.UserDto;
import net.javaguides.springboot.entity.Todo;
import net.javaguides.springboot.entity.User;
import net.javaguides.springboot.repository.TodoRepository;
import net.javaguides.springboot.service.TodoService;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TodoServiceImpl implements TodoService {

    private TodoRepository todoRepository;

    @Override
    public TodoDto addTodo(TodoDto todoDto) {
        return null;
    }

    @Override
    public TodoDto getTodo(Long id) {
        Todo todo = todoRepository.findById(id).get();

        return mapToUserDto(todo);
    }

    @Override
    public List<TodoDto> getAllTodos() {
        List<Todo> todos = todoRepository.findAll();
        return todos.stream().map((todo) -> mapToUserDto(todo)).collect(Collectors.toList());
    }

    @Override
    public List<TodoDto> getAllTodosByUser() {
        List<Todo> todos = todoRepository.findTodoByUserId(CurrentUser.user.getId());
        return todos.stream().map((todo) -> mapToUserDto(todo)).collect(Collectors.toList());
    }

    @Override
    public TodoDto updateTodo(TodoDto todoDto, Long id) {
        Todo todo = todoRepository.findById(id).get();
        todo.setTitle(todoDto.getTitle());
        todo.setDescription(todoDto.getDescription());
        todo.setCompleted(todoDto.isCompleted());

        Todo updatedTodo = todoRepository.save(todo);

        return mapToUserDto(updatedTodo);
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

    private TodoDto mapToUserDto(Todo todo){
        TodoDto todoDto = new TodoDto(
                todo.getId(),
                todo.getTitle(),
                todo.getDescription(),
                false
        );

        return todoDto;
    }

}
