package com.sparta.doblock.todo.repository;

import com.sparta.doblock.todo.entity.TodoDate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface TodoDateRepository extends JpaRepository<TodoDate, Long> {

    Optional<TodoDate> findByDate(LocalDate date);
    boolean existsByDate(LocalDate date);
}
