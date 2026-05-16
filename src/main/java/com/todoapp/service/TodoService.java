package com.todoapp.service;

import com.todoapp.model.TodoItem;
import com.todoapp.repository.TodoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TodoService {

  private final TodoRepository todoRepository;

  public TodoService(TodoRepository todoRepository) {
    this.todoRepository = todoRepository;
  }

  public List<TodoItem> findAll() {
    return todoRepository.findAll();
  }

  public TodoItem findById(Long id) {
    return todoRepository.findById(id).orElse(null);
  }

  public TodoItem save(TodoItem todoItem) {
    return todoRepository.save(todoItem);
  }

  public Optional<TodoItem> update(Long id, TodoItem todoItem) {
    return todoRepository.findById(id).map(existing -> {
      existing.setTitle(todoItem.getTitle());
      existing.setDescription(todoItem.getDescription());
      existing.setCompleted(todoItem.isCompleted());
      return todoRepository.save(existing);
    });
  }

  public void deleteById(Long id) {
    todoRepository.deleteById(id);
  }
}
