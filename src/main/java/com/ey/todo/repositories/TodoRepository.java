package com.ey.todo.repositories;

import com.ey.todo.entites.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoRepository extends JpaRepository<Todo, Integer> {
}
