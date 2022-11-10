package com.sparta.doblock.todo.repository;

import com.sparta.doblock.member.entity.Member;
import com.sparta.doblock.todo.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TodoRepository extends JpaRepository<Todo, Long> {
    List<Todo> findAllByMemberAndDate(Member member, LocalDate date);
}
