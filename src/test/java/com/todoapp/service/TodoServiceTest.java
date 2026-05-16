package com.todoapp.service;

import com.todoapp.model.TodoItem;
import com.todoapp.repository.TodoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TodoServiceTest {

  @Mock
  private TodoRepository todoRepository;

  @InjectMocks
  private TodoService todoService;

  @Test
  void updateModifiesExistingTodo() {
    TodoItem existing = new TodoItem("Old title", "Old description", false);
    TodoItem request = new TodoItem("New title", "New description", true);

    when(todoRepository.findById(1L)).thenReturn(Optional.of(existing));
    when(todoRepository.save(existing)).thenReturn(existing);

    Optional<TodoItem> updated = todoService.update(1L, request);

    assertThat(updated).isPresent();
    assertThat(updated.get().getTitle()).isEqualTo("New title");
    assertThat(updated.get().getDescription()).isEqualTo("New description");
    assertThat(updated.get().isCompleted()).isTrue();
    verify(todoRepository).save(existing);
  }

  @Test
  void updateReturnsEmptyWhenTodoDoesNotExist() {
    TodoItem request = new TodoItem("New title", "New description", true);

    when(todoRepository.findById(99L)).thenReturn(Optional.empty());

    Optional<TodoItem> updated = todoService.update(99L, request);

    assertThat(updated).isEmpty();
    verify(todoRepository).findById(99L);
    verifyNoMoreInteractions(todoRepository);
  }
}
