package com.sparta.doblock.tag.repository;

import com.sparta.doblock.tag.entity.Tag;
import com.sparta.doblock.tag.mapper.TodoTagMapper;
import com.sparta.doblock.todo.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TodoTagMapperRepository extends JpaRepository<TodoTagMapper, Long> {

    List<TodoTagMapper> findByTodo(Todo todo);
    void deleteAllByTodo(Todo todo);
    boolean existsByTodoAndTag(Todo todo, Tag tag);
}
