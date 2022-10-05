package com.ey.todo.controllers;

import com.ey.todo.dto.TodoDto;
import com.ey.todo.entites.Status;
import com.ey.todo.entites.Todo;
import com.ey.todo.services.TodoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/todos")
public class TodoController {

    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @GetMapping
    public ResponseEntity<List<Todo>> getAll() {
        return ResponseEntity.ok(todoService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Todo> getOne(@PathVariable Integer id) {

        Optional<Todo> todoOptional = todoService.getOne(id);

        return todoOptional
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    @PostMapping
    public ResponseEntity<Todo> save(@RequestBody @Valid TodoDto todoDto) throws URISyntaxException {
        Todo createdTodo = todoService.save(todoDto);

        return ResponseEntity.created(new URI("/todos/" + createdTodo.getId())).body(createdTodo);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        todoService.delete(id);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Todo> update(@PathVariable Integer id, @RequestBody TodoDto todoDto) {
        Optional<Todo> todoOptional = todoService.update(id, todoDto);

        return todoOptional
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    @PatchMapping("/{id}/status/{status}")
    public ResponseEntity<Todo> updateStatus(@PathVariable Integer id, @PathVariable Status status) {
        Optional<Todo> todoOptional = todoService.updateTodo(id, status);

        return todoOptional
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
