package com.sparta.doblock.todo.service;

import com.sparta.doblock.events.entity.BadgeEvents;
import com.sparta.doblock.exception.DoBlockExceptions;
import com.sparta.doblock.exception.ErrorCodes;
import com.sparta.doblock.member.entity.MemberDetailsImpl;
import com.sparta.doblock.todo.dto.request.TodoRequestDto;
import com.sparta.doblock.todo.dto.response.TodoResponseDto;
import com.sparta.doblock.todo.entity.Todo;
import com.sparta.doblock.todo.repository.TodoRepository;
import com.sparta.doblock.todo.dto.request.TodoIdOrderRequestDto;
import com.sparta.doblock.todo.entity.TodoDate;
import com.sparta.doblock.todo.repository.TodoDateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@Service
@RequiredArgsConstructor
public class TodoService {

    private final TodoRepository todoRepository;
    private final TodoDateRepository todoDateRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional
    public ResponseEntity<?> createTodo(TodoRequestDto todoRequestDto, MemberDetailsImpl memberDetails) {

        LocalDate date = LocalDate.of(todoRequestDto.getYear(), todoRequestDto.getMonth(), todoRequestDto.getDay());

        TodoDate todoDate = todoDateRepository.findByDateAndMember(date, memberDetails.getMember()).orElse(
                TodoDate.builder()
                        .date(date)
                        .member(memberDetails.getMember())
                        .lastIndex(0)
                        .build()
        );

        todoDateRepository.save(todoDate);

        int todoIndex = todoDate.getTodoIndex();

        Todo todo = Todo.builder()
                .member(memberDetails.getMember())
                .todoDate(todoDate)
                .todoIndex(todoIndex)
                .todoContent(todoRequestDto.getTodoContent())
                .completed(false)
                .todoMemo(todoRequestDto.getTodoMemo())
                .build();

        todoRepository.save(todo);

        TodoResponseDto todoResponseDto = TodoResponseDto.builder()
                .todoId(todo.getId())
                .todoContent(todo.getTodoContent())
                .todoMemo(todo.getTodoMemo())
                .completed(todo.isCompleted())
                .build();

        return ResponseEntity.ok(todoResponseDto);
    }

    @Transactional
    public ResponseEntity<?> switchOrder(TodoIdOrderRequestDto todoIdOrderRequestDto, MemberDetailsImpl memberDetails) {

        LocalDate date = LocalDate.of(todoIdOrderRequestDto.getYear(), todoIdOrderRequestDto.getMonth(), todoIdOrderRequestDto.getDay());
        TodoDate todoDate = todoDateRepository.findByDateAndMember(date, memberDetails.getMember()).orElseThrow(
                () -> new DoBlockExceptions(ErrorCodes.NOT_MATCHED_TODO_DATE)
        );

        if (todoIdOrderRequestDto.getTodoIdList().size() != todoRepository.findAllByMemberAndTodoDate(memberDetails.getMember(), todoDate).size()) {
            throw new DoBlockExceptions(ErrorCodes.NOT_MATCHED_TODO_COUNT);
        }

        Set<Long> todoIdSet = new HashSet<>(todoIdOrderRequestDto.getTodoIdList());

        for (Todo todo : todoRepository.findAllByMemberAndTodoDate(memberDetails.getMember(), todoDate)) {

            if (!todoIdSet.contains(todo.getId())) {
                throw new DoBlockExceptions(ErrorCodes.NOT_MATCHED_TODO_DATE);
            }
        }

        for (int i = 0; i < todoIdOrderRequestDto.getTodoIdList().size(); i++) {

            Long todoId = todoIdOrderRequestDto.getTodoIdList().get(i);
            Todo todo = todoRepository.findById(todoId).orElseThrow(
                    () -> new DoBlockExceptions(ErrorCodes.NOT_FOUND_TODO)
            );

            todo.editTodoIndex(i);
        }

        todoDate.editLastIndex(todoIdOrderRequestDto.getTodoIdList().size());

        return ResponseEntity.ok("성공적으로 투두 순서를 바꾸었습니다");
    }

    public ResponseEntity<?> getTodayTodo(int year, int month, int day, MemberDetailsImpl memberDetails) {

        LocalDate date = LocalDate.of(year, month, day);

        TodoDate todoDate = todoDateRepository.findByDateAndMember(date, memberDetails.getMember()).orElseThrow(
                () -> new DoBlockExceptions(ErrorCodes.NOT_MATCHED_TODO_DATE)
        );

        List<Todo> todoList = todoRepository.findAllByMemberAndTodoDateOrderByTodoIndex(memberDetails.getMember(), todoDate);
        List<TodoResponseDto> todoResponseDtoList = new ArrayList<>();

        for (Todo todo : todoList) {
            todoResponseDtoList.add(
                    TodoResponseDto.builder()
                            .todoId(todo.getId())
                            .todoContent(todo.getTodoContent())
                            .completed(todo.isCompleted())
                            .todoMemo(todo.getTodoMemo())
                            .build()
            );
        }

        return ResponseEntity.ok(todoResponseDtoList);
    }

    public ResponseEntity<?> getTodo(Long id, MemberDetailsImpl memberDetails) {

        Todo todo = todoRepository.findById(id).orElseThrow(
                () -> new DoBlockExceptions(ErrorCodes.NOT_FOUND_TODO)
        );

        if (!todo.getMember().getId().equals(memberDetails.getMember().getId())) {
            throw new DoBlockExceptions(ErrorCodes.NOT_VALID_WRITER);
        }

        TodoResponseDto todoResponseDto = TodoResponseDto.builder()
                .todoContent(todo.getTodoContent())
                .todoMemo(todo.getTodoMemo())
                .completed(todo.isCompleted())
                .build();

        return ResponseEntity.ok(todoResponseDto);
    }

    @Transactional
    public ResponseEntity<?> completedTodo(Long id, MemberDetailsImpl memberDetails) {

        Todo todo = todoRepository.findById(id).orElseThrow(
                () -> new DoBlockExceptions(ErrorCodes.NOT_FOUND_TODO)
        );

        if (!todo.getMember().getId().equals(memberDetails.getMember().getId())) {
            throw new DoBlockExceptions(ErrorCodes.NOT_VALID_WRITER);
        }

        if (LocalDate.now(ZoneId.of("Asia/Tokyo")).isBefore(todo.getTodoDate().getDate())) {
            throw new DoBlockExceptions(ErrorCodes.NOT_ABLE_COMPLETE_TODO);
        }

        todo.completeTask();

        applicationEventPublisher.publishEvent(new BadgeEvents.CompletedTodoBadgeEvent(memberDetails));

        if (todo.isCompleted()) {
            return ResponseEntity.ok("투두가 완료되었습니다.");
        } else return ResponseEntity.ok("투두 완료가 취소되었습니다.");
    }

    @Transactional
    public ResponseEntity<?> editTodo(Long id, TodoRequestDto todoRequestDto, MemberDetailsImpl memberDetails) {

        Todo todo = todoRepository.findById(id).orElseThrow(
                () -> new DoBlockExceptions(ErrorCodes.NOT_FOUND_TODO)
        );

        if (!todo.getMember().getId().equals(memberDetails.getMember().getId())) {
            throw new DoBlockExceptions(ErrorCodes.NOT_VALID_WRITER);
        }

        LocalDate date = LocalDate.of(todoRequestDto.getYear(), todoRequestDto.getMonth(), todoRequestDto.getDay());

        TodoDate todoDate = todoDateRepository.findByDateAndMember(date, memberDetails.getMember()).orElse(
                TodoDate.builder()
                        .date(date)
                        .member(memberDetails.getMember())
                        .lastIndex(0)
                        .build()
        );

        todoDateRepository.save(todoDate);

        todo.edit(todoRequestDto, todoDate);

        return ResponseEntity.ok("투두 수정이 완료되었습니다.");
    }

    @Transactional
    public ResponseEntity<?> deleteTodo(Long id, MemberDetailsImpl memberDetails) {

        Todo todo = todoRepository.findById(id).orElseThrow(
                () -> new DoBlockExceptions(ErrorCodes.NOT_FOUND_TODO)
        );

        if (!todo.getMember().getId().equals(memberDetails.getMember().getId())) {
            throw new DoBlockExceptions(ErrorCodes.NOT_VALID_WRITER);
        }

        todoRepository.deleteById(id);

        return ResponseEntity.ok("투두 삭제가 완료되었습니다.");
    }

    public ResponseEntity<?> getMonthTodo(int year, int month, MemberDetailsImpl memberDetails) {

        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = LocalDate.of(year, month, startDate.lengthOfMonth()).plusDays(1);

        List<TodoResponseDto> todoResponseDtoList = new ArrayList<>();

        for (LocalDate date = startDate; date.isBefore(endDate); date = date.plusDays(1)) {

            if (!todoDateRepository.existsByDateAndMember(date, memberDetails.getMember())) {
                continue;
            }

            TodoDate todoDate = todoDateRepository.findByDateAndMember(date, memberDetails.getMember()).orElseThrow(
                    () -> new DoBlockExceptions(ErrorCodes.NOT_FOUND_TODO_DATE)
            );

            List<Todo> todoList = todoRepository.findAllByMemberAndTodoDateOrderByTodoIndex(memberDetails.getMember(), todoDate);

            for (Todo todo : todoList) {
                todoResponseDtoList.add(
                        TodoResponseDto.builder()
                                .todoId(todo.getId())
                                .todoContent(todo.getTodoContent())
                                .completed(todo.isCompleted())
                                .day(todoDate.getDate().getDayOfMonth())
                                .build()
                );
            }
        }

        return ResponseEntity.ok(todoResponseDtoList);
    }
}
