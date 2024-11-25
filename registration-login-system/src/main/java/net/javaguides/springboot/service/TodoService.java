package net.javaguides.springboot.service;

import net.javaguides.springboot.dto.TodoDto;

import java.util.List;

public interface TodoService {
    void addTodo(TodoDto todoDto);

    TodoDto getTodo(Long id);

    List<TodoDto> getAllTodos();

    List<TodoDto> getAllTodosByUser();

    TodoDto updateTodo(TodoDto todoDto, Long id);

    void deleteTodo(Long id);

    TodoDto completeTodo(Long id);

    TodoDto incompleteTodo(Long id);

}
