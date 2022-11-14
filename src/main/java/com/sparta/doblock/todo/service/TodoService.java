package com.sparta.doblock.todo.service;

import com.sparta.doblock.member.entity.MemberDetailsImpl;
import com.sparta.doblock.todo.dto.request.TodoRequestDto;
import com.sparta.doblock.todo.dto.response.TodoResponseDto;
import com.sparta.doblock.todo.entity.Todo;
import com.sparta.doblock.todo.repository.TodoRepository;
import com.sparta.doblock.todo.dto.request.TodoIdOrderRequestDto;
import com.sparta.doblock.todo.entity.TodoDate;
import com.sparta.doblock.todo.repository.TodoDateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class TodoService {

    private final TodoRepository todoRepository;
    private final TodoDateRepository todoDateRepository;

    @Transactional
    public ResponseEntity<?> createTodo(TodoRequestDto todoRequestDto, MemberDetailsImpl memberDetails) {

        if (Objects.isNull(memberDetails)) {
            throw new NullPointerException("로그인이 필요합니다.");
        }

        LocalDate date = LocalDate.of(todoRequestDto.getYear(), todoRequestDto.getMonth(), todoRequestDto.getDay());

        TodoDate todoDate = todoDateRepository.findByDate(date).orElse(TodoDate.builder()
                .date(date).lastIndex(0).build());

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

        return ResponseEntity.ok("성공적으로 투두를 생성하였습니다");
    }

    @Transactional
    public ResponseEntity<?> switchOrder(TodoIdOrderRequestDto todoIdOrderRequestDto, MemberDetailsImpl memberDetails) {

        if (Objects.isNull(memberDetails)) {
            throw new NullPointerException("로그인이 필요합니다.");
        }

        LocalDate date = LocalDate.of(todoIdOrderRequestDto.getYear(), todoIdOrderRequestDto.getMonth(), todoIdOrderRequestDto.getDay());
        TodoDate todoDate = todoDateRepository.findByDate(date).orElseThrow(
                () -> new NullPointerException("해당 날짜에 등록된 투두가 없습니다")
        );

        if (todoIdOrderRequestDto.getTodoIdList().size() != todoRepository.findAllByMemberAndTodoDate(memberDetails.getMember(), todoDate).size()){
            throw new IllegalArgumentException("투두리스트 갯수가 정확하지 않습니다.");
        }

        Set<Long> todoIdSet = new HashSet<>(todoIdOrderRequestDto.getTodoIdList());

        for(Todo todo : todoRepository.findAllByMemberAndTodoDate(memberDetails.getMember(), todoDate)){

            if (! todoIdSet.contains(todo.getId())){
                throw new IllegalArgumentException("해당 날짜의 투두가 아닙니다.");
            }
        }

        for (int i = 0; i < todoIdOrderRequestDto.getTodoIdList().size(); i++) {

            Long todoId = todoIdOrderRequestDto.getTodoIdList().get(i);
            Todo todo = todoRepository.findById(todoId).orElseThrow(
                    () -> new NullPointerException("해당 투두가 없습니다")
            );

            todo.setTodoIndex(i);
        }

        todoDate.setLastIndex(todoIdOrderRequestDto.getTodoIdList().size());

        return ResponseEntity.ok("성공적으로 투두 순서를 바꾸었습니다");
    }

    public ResponseEntity<?> getTodayTodo(int year, int month, int day, MemberDetailsImpl memberDetails) {

        if (Objects.isNull(memberDetails)) {
            throw new NullPointerException("로그인이 필요합니다.");
        }

        LocalDate date = LocalDate.of(year, month, day);

        TodoDate todoDate = todoDateRepository.findByDate(date).orElseThrow(
                () -> new NullPointerException("해당 날짜에 등록한 투두가 없습니다")
        );

        List<Todo> todoList = todoRepository.findAllByMemberAndTodoDateOrderByTodoIndex(memberDetails.getMember(), todoDate);
        List<TodoResponseDto> todoResponseDtoList = new ArrayList<>();

        for (Todo todo : todoList) {

            TodoResponseDto todoResponseDto = TodoResponseDto.builder()
                    .todoId(todo.getId())
                    .todoContent(todo.getTodoContent())
                    .completed(todo.isCompleted())
                    .build();

            todoResponseDtoList.add(todoResponseDto);
        }

        return ResponseEntity.ok(todoResponseDtoList);
    }

    public ResponseEntity<?> getTodo(Long id, MemberDetailsImpl memberDetails) {

        if (Objects.isNull(memberDetails)) {
            throw new NullPointerException("로그인이 필요합니다.");
        }

        Todo todo = todoRepository.findById(id).orElseThrow(
                () -> new RuntimeException("투두가 존재하지 않습니다."));

        TodoResponseDto todoResponseDto = TodoResponseDto.builder()
                .todoContent(todo.getTodoContent())
                .todoMemo(todo.getTodoMemo())
                .completed(todo.isCompleted())
                .build();

        return ResponseEntity.ok(todoResponseDto);
    }

    @Transactional
    public ResponseEntity<?> completedTodo(Long id, MemberDetailsImpl memberDetails) {

        if (Objects.isNull(memberDetails)) {
            throw new NullPointerException("로그인이 필요합니다.");
        }

        Todo todo = todoRepository.findById(id).orElseThrow(
                ()->new RuntimeException("완료할 투두가 없습니다.")
        );

        if(!todo.getMember().getNickname().equals(memberDetails.getMember().getNickname())) {
            throw new RuntimeException("본인이 작성한 투두만 완료가 가능합니다.");
        }

        todo.completeTask();

        if (todo.isCompleted()){
            return ResponseEntity.ok("투두가 완료되었습니다.");
        } else return ResponseEntity.ok("투두 완료가 취소되었습니다.");
    }

    @Transactional
    public ResponseEntity<?> editTodo(Long id, TodoRequestDto todoRequestDto, MemberDetailsImpl memberDetails) {

        if (Objects.isNull(memberDetails)) {
            throw new NullPointerException("로그인이 필요합니다.");
        }

        Todo todo = todoRepository.findById(id).orElseThrow(
                ()->new RuntimeException("수정할 투두가 없습니다.")
        );

        if(!todo.getMember().getNickname().equals(memberDetails.getMember().getNickname())) {
            throw new RuntimeException("본인이 작성한 투두만 삭제가 가능합니다.");
        }

        LocalDate date = LocalDate.of(todoRequestDto.getYear(), todoRequestDto.getMonth(), todoRequestDto.getDay());

        TodoDate todoDate = todoDateRepository.findByDate(date).orElse(TodoDate.builder()
                .date(date).lastIndex(0).build());

        todoDateRepository.save(todoDate);

        todo.edit(todoRequestDto, todoDate);

        return ResponseEntity.ok("투두 수정이 완료되었습니다.");
    }

    @Transactional
    public ResponseEntity<?> deleteTodo(Long id, MemberDetailsImpl memberDetails) {

        if (Objects.isNull(memberDetails)) {
            throw new NullPointerException("로그인이 필요합니다.");
        }

        Todo todo = todoRepository.findById(id).orElseThrow(
                () -> new RuntimeException("삭제할 투두가 존재하지 않습니다.")
        );

        if(!todo.getMember().getNickname().equals(memberDetails.getMember().getNickname())) {
            throw new RuntimeException("본인이 작성한 투두만 삭제가 가능합니다.");
        }

        todoRepository.deleteById(id);

        return ResponseEntity.ok("투두 삭제가 완료되었습니다.");
    }

    public ResponseEntity<?> getMonthTodo(int year, int month, MemberDetailsImpl memberDetails) {

        if (Objects.isNull(memberDetails)) {
            throw new NullPointerException("로그인이 필요합니다.");
        }

        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = LocalDate.of(year, month, startDate.lengthOfMonth()).plusDays(1);

        List<TodoResponseDto> todoResponseDtoList = new ArrayList<>();

        for (LocalDate date = startDate; date.isBefore(endDate); date = date.plusDays(1)){

            if (!todoDateRepository.existsByDate(date)){
                continue;
            }

            TodoDate todoDate = todoDateRepository.findByDate(date).orElseThrow(
                    () -> new NullPointerException("날짜에 해당하는 투두가 없습니다.")
            );

            List<Todo> todoList = todoRepository.findAllByMemberAndTodoDateOrderByTodoIndex(memberDetails.getMember(), todoDate);

            for (Todo todo : todoList) {

                TodoResponseDto todoResponseDto = TodoResponseDto.builder()
                        .todoId(todo.getId())
                        .todoContent(todo.getTodoContent())
                        .completed(todo.isCompleted())
                        .day(todoDate.getDate().getDayOfMonth())
                        .build();

                todoResponseDtoList.add(todoResponseDto);
            }
        }

        return ResponseEntity.ok(todoResponseDtoList);
    }
}
