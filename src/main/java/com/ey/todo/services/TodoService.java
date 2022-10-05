package com.ey.todo.services;

import com.ey.todo.dto.TodoDto;
import com.ey.todo.entites.Status;
import com.ey.todo.entites.Todo;
import com.ey.todo.repositories.TodoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.springframework.util.StringUtils.hasText;

@Service
public class TodoService {

    private final TodoRepository todoRepository;

    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    public List<Todo> getAll() {
        return todoRepository.findAll();
    }

    public Optional<Todo> getOne(Integer id) {
        return todoRepository.findById(id);
    }

    public Todo save(TodoDto todoDto) {
        Todo todo = new Todo();

        todo.setName(todoDto.getName());
        todo.setDescription(todoDto.getDescription());

        todo.setCreatedAt(LocalDateTime.now());
        todo.setUpdatedAt(LocalDateTime.now());
        todo.setStatus(Status.TODO);

        return todoRepository.save(todo);
    }

    public void delete(Integer id) {
        Optional<Todo> todoOptional = todoRepository.findById(id);
        todoOptional.ifPresent(todoRepository::delete);
    }

    public Optional<Todo> updateTodo(Integer id, Status status) {

        Optional<Todo> todoOptional = todoRepository.findById(id);

        if (todoOptional.isEmpty()) return Optional.empty();

        Todo todo = todoOptional.get();

        todo.setStatus(status);
        todo.setUpdatedAt(LocalDateTime.now());

        return Optional.of(todoRepository.save(todo));
    }

    public Optional<Todo> update(Integer id, TodoDto todoDto) {
        Optional<Todo> todoOptional = todoRepository.findById(id);

        if (todoOptional.isEmpty()) return Optional.empty();

        Todo todo = todoOptional.get();

        if (hasText(todoDto.getName())) {
            todo.setName(todoDto.getName());
        }
        if (hasText(todoDto.getDescription())) {
            todo.setDescription(todoDto.getDescription());
        }

        todo.setUpdatedAt(LocalDateTime.now());

        return Optional.of(todoRepository.save(todo));
    }
}
