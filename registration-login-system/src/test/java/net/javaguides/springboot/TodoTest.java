package net.javaguides.springboot;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.assertj.core.api.Assertions.*;
import net.javaguides.springboot.entity.Todo;

public class TodoTest {

    @Test
    void shouldCorrectlySetAndGetTodoFields() {
        Todo todo = new Todo();
        todo.setTitle("Buy groceries");
        todo.setDescription("Buy fruits and vegetables");
        todo.setCompleted(false);
        todo.setDeadline(LocalDate.of(2025, 1, 10));

        assertThat(todo.getTitle()).isEqualTo("Buy groceries");
        assertThat(todo.getDescription()).isEqualTo("Buy fruits and vegetables");
        assertThat(todo.getCompleted()).isFalse();
        assertThat(todo.getDeadline()).isEqualTo(LocalDate.of(2025, 1, 10));
    }


    @Test
    void shouldMarkTodoAsCompleted() {
        Todo todo = new Todo();
        todo.setCompleted(false);

        todo.setCompleted(true);

        assertThat(todo.getCompleted()).isTrue();
    }


    @Test
    void shouldCompareTwoTodosBasedOnId() {
        Todo todo1 = new Todo();
        todo1.setId(1L);
        todo1.setTitle("Task 1");

        Todo todo2 = new Todo();
        todo2.setId(1L);
        todo2.setTitle("Task 1");

        assertThat(todo1).isEqualTo(todo2);
        assertThat(todo1.hashCode()).isEqualTo(todo2.hashCode());
    }


    @Test
    void shouldDifferentiateTodosWithDifferentIds() {
        Todo todo1 = new Todo();
        todo1.setId(1L);
        todo1.setTitle("Task 1");

        Todo todo2 = new Todo();
        todo2.setId(2L);
        todo2.setTitle("Task 2");

        assertThat(todo1).isNotEqualTo(todo2);
    }
}