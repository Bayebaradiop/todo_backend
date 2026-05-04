package com.todoapp.controller;

import com.todoapp.model.TodoItem;
import com.todoapp.service.TodoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/todos")
public class TodoController {

  private final TodoService todoService;

  public TodoController(TodoService todoService) {
    this.todoService = todoService;
  }

  @GetMapping
  public List<TodoItem> getAll() {
    return todoService.findAll();
  }

  @GetMapping("/{id}")
  public ResponseEntity<TodoItem> getById(@PathVariable Long id) {
    TodoItem item = todoService.findById(id);
    return item != null ? ResponseEntity.ok(item) : ResponseEntity.notFound().build();
  }

  @PostMapping
  public TodoItem create(@RequestBody TodoItem todoItem) {
    return todoService.save(todoItem);
  }

  @PutMapping("/{id}")
  public ResponseEntity<TodoItem> update(@PathVariable Long id, @RequestBody TodoItem todoItem) {
    TodoItem existing = todoService.findById(id);
    if (existing == null) {
      return ResponseEntity.notFound().build();
    }
    existing.setTitle(todoItem.getTitle());
    existing.setDescription(todoItem.getDescription());
    existing.setCompleted(todoItem.isCompleted());
    return ResponseEntity.ok(todoService.save(existing));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    if (todoService.findById(id) == null) {
      return ResponseEntity.notFound().build();
    }
    todoService.deleteById(id);
    return ResponseEntity.noContent().build();
  }
}
