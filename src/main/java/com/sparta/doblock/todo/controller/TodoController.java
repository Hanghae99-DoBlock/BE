package com.sparta.doblock.todo.controller;

import com.sparta.doblock.member.entity.MemberDetailsImpl;
import com.sparta.doblock.todo.dto.request.TodoRequestDto;
import com.sparta.doblock.todo.service.TodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/todolist")
public class TodoController {
    private final TodoService todoService;

    //투두 작성
    @PostMapping("")
    public ResponseEntity<?> createTodo(@RequestBody TodoRequestDto todoRequestDto,
                                        @AuthenticationPrincipal MemberDetailsImpl memberDetails) {
        return todoService.createTodo(todoRequestDto, memberDetails);
    }
    //투두 일별 조회
    @GetMapping("")
    public ResponseEntity<?> getTodayTodo(@RequestBody TodoRequestDto todoRequestDto,
                                                              @AuthenticationPrincipal MemberDetailsImpl memberDetails){
        return todoService.getTodayTodo(todoRequestDto, memberDetails);
    }
    //투두 단건 조회
    @GetMapping("/{todo_id}")
    public ResponseEntity<?> getTodo(@PathVariable(name = "todo_id") Long id,
                                     @AuthenticationPrincipal MemberDetailsImpl memberDetails) {
        return todoService.getTodo(id, memberDetails);
    }
    //투두 완료 POST??
    @PatchMapping("/{todo_id}/completed")
    public ResponseEntity<?> completedTodo(@PathVariable(name = "todo_id") Long id,
                                           @AuthenticationPrincipal MemberDetailsImpl memberDetails) {
        return todoService.completedTodo(id, memberDetails);
    }
    //투두 수정
    @PatchMapping("/{todo_id}/edit")
    public ResponseEntity<?> editTodo(@PathVariable(name = "todo_id") Long id,
                                      @RequestBody TodoRequestDto todoRequestDto,
                                      @AuthenticationPrincipal MemberDetailsImpl memberDetails) {
        return todoService.editTodo(id, todoRequestDto, memberDetails.getMember());
    }
    //투두 삭제
    @DeleteMapping("/{todo_id}/remove")
    public ResponseEntity<?> deleteTodo(@PathVariable(name = "todo_id") Long id,
                                        @AuthenticationPrincipal MemberDetailsImpl memberDetails) {
        return todoService.deleteTodo(id, memberDetails);
    }
//    //캘린더 월별 조회
}
