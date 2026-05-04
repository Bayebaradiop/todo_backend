package com.todoapp.service;

import com.todoapp.model.TodoItem;
import com.todoapp.repository.TodoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

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

  public void deleteById(Long id) {
    todoRepository.deleteById(id);
  }
}
