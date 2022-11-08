package com.sparta.doblock.todo.controller;

import com.sparta.doblock.member.entity.MemberDetailsImpl;
import com.sparta.doblock.todo.dto.request.TodoRequestDto;
import com.sparta.doblock.todo.service.TodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/todolist")
public class TodoController {
    private final TodoService todoService;

    @PostMapping("")
    public ResponseEntity<?> createTodo(@RequestBody TodoRequestDto todoRequestDto, @AuthenticationPrincipal MemberDetailsImpl memberDetails) {
        return todoService.createTodo(todoRequestDto, memberDetails);
    }
}
