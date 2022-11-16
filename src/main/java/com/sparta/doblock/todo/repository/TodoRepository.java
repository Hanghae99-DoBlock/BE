package com.sparta.doblock.todo.repository;

import com.sparta.doblock.member.entity.Member;
import com.sparta.doblock.todo.entity.Todo;
import com.sparta.doblock.todo.entity.TodoDate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TodoRepository extends JpaRepository<Todo, Long> {

    List<Todo> findAllByMemberAndTodoDate(Member member, TodoDate todoDate);
    List<Todo> findAllByMemberAndTodoDateOrderByTodoIndex(Member member, TodoDate todoDate);
    long countAllByMemberAndCompleted(Member member, boolean completed);
}
