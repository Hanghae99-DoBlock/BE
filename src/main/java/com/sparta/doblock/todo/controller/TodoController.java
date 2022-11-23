package com.sparta.doblock.todo.controller;

import com.sparta.doblock.member.entity.MemberDetailsImpl;
import com.sparta.doblock.todo.dto.request.TodoIdOrderRequestDto;
import com.sparta.doblock.todo.dto.request.TodoRequestDto;
import com.sparta.doblock.todo.service.TodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/todolist")
public class TodoController {
    private final TodoService todoService;

    @PostMapping("")
    public ResponseEntity<?> createTodo(@RequestBody TodoRequestDto todoRequestDto,
                                        @AuthenticationPrincipal MemberDetailsImpl memberDetails) {
        return todoService.createTodo(todoRequestDto, memberDetails);
    }

    @PutMapping("/switch")
    public ResponseEntity<?> switchOrder(@RequestBody TodoIdOrderRequestDto todoIdOrderRequestDto, @AuthenticationPrincipal MemberDetailsImpl memberDetails) {
        return todoService.switchOrder(todoIdOrderRequestDto, memberDetails);
    }

    @GetMapping("")
    public ResponseEntity<?> getTodayTodo(@RequestParam("year") int year, @RequestParam("month") int month, @RequestParam("day") int day,
                                                              @AuthenticationPrincipal MemberDetailsImpl memberDetails){
        return todoService.getTodayTodo(year, month, day, memberDetails);
    }

    @GetMapping("/{todo_id}")
    public ResponseEntity<?> getTodo(@PathVariable(name = "todo_id") Long id,
                                     @AuthenticationPrincipal MemberDetailsImpl memberDetails) {
        return todoService.getTodo(id, memberDetails);
    }

    @PatchMapping("/{todo_id}/completed")
    public ResponseEntity<?> completedTodo(@PathVariable(name = "todo_id") Long id,
                                           @AuthenticationPrincipal MemberDetailsImpl memberDetails) {
        return todoService.completedTodo(id, memberDetails);
    }

    @PatchMapping("/{todo_id}/edit")
    public ResponseEntity<?> editTodo(@PathVariable(name = "todo_id") Long id,
                                      @RequestBody TodoRequestDto todoRequestDto,
                                      @AuthenticationPrincipal MemberDetailsImpl memberDetails) {
        return todoService.editTodo(id, todoRequestDto, memberDetails);
    }

    @DeleteMapping("/{todo_id}/remove")
    public ResponseEntity<?> deleteTodo(@PathVariable(name = "todo_id") Long id,
                                        @AuthenticationPrincipal MemberDetailsImpl memberDetails) {
        return todoService.deleteTodo(id, memberDetails);
    }

    @GetMapping("/calendar")
    public ResponseEntity<?> getMonthTodo(@RequestParam("year") int year, @RequestParam("month") int month,
                                          @AuthenticationPrincipal MemberDetailsImpl memberDetails){
        return todoService.getMonthTodo(year, month, memberDetails);
    }
}
