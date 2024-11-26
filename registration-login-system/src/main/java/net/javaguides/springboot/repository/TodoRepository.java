package net.javaguides.springboot.repository;

import net.javaguides.springboot.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TodoRepository extends JpaRepository<Todo, Long> {
    List<Todo> findTodoByUserId(Long id);
    Optional<Todo> findById(Long id);
}

