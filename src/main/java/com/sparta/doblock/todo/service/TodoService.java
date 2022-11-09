package com.sparta.doblock.todo.service;

import com.sparta.doblock.member.entity.Member;
import com.sparta.doblock.member.entity.MemberDetailsImpl;
import com.sparta.doblock.tag.entity.Tag;
import com.sparta.doblock.tag.mapper.TodoTagMapper;
import com.sparta.doblock.tag.repository.TagRepository;
import com.sparta.doblock.tag.repository.TodoTagMapperRepository;
import com.sparta.doblock.todo.dto.request.TodoRequestDto;
import com.sparta.doblock.todo.dto.response.TodoResponseDto;
import com.sparta.doblock.todo.entity.Todo;
import com.sparta.doblock.todo.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class TodoService {

    private final TodoRepository todoRepository;
    private final TagRepository tagRepository;
    private final TodoTagMapperRepository todoTagMapperRepository;

    @Transactional
    public ResponseEntity<?> createTodo(TodoRequestDto todoRequestDto, MemberDetailsImpl memberDetails) {
        if (Objects.isNull(memberDetails)) {
            return new ResponseEntity<>("로그인이 필요합니다", HttpStatus.UNAUTHORIZED);
        }

        Todo todo = Todo.builder()
                .member(memberDetails.getMember())
                .date(LocalDate.of(todoRequestDto.getYear(), todoRequestDto.getMonth(), todoRequestDto.getDay()))
                .startTime(LocalTime.of(todoRequestDto.getStartHour(), todoRequestDto.getStartMinute()))
                .endTime(LocalTime.of(todoRequestDto.getEndHour(), todoRequestDto.getEndMinute()))
                .todocontent(todoRequestDto.getTodocontent())
                .completed(false)
                .build();

        todoRepository.save(todo);

        for (String tagContent : todoRequestDto.getTagList()) {
            if (! tagRepository.existsByContent(tagContent)) {
                Tag tag = Tag.builder()
                        .content(tagContent)
                        .build();
                tagRepository.save(tag);
            }
            Tag tag = tagRepository.findByContent(tagContent).orElseThrow(NullPointerException::new);

            TodoTagMapper todoTagMapper = TodoTagMapper.builder()
                    .todo(todo)
                    .tag(tag)
                    .build();
            todoTagMapperRepository.save(todoTagMapper);
        }

        return ResponseEntity.ok("성공적으로 투두를 생성하였습니다");
    }

    //투두 일별 조회
    public ResponseEntity<?> getTodayTodo(TodoRequestDto todoRequestDto, MemberDetailsImpl memberDetails) {
        LocalDate date = LocalDate.of(todoRequestDto.getYear(), todoRequestDto.getMonth(), todoRequestDto.getDay());
        List<Todo> todoList = todoRepository.findByMemberAndDate(memberDetails.getMember(), date);

        List<TodoResponseDto> todoResponseDtoList = new ArrayList<>();

        for (Todo todo : todoList) {
            List<String> tagList = new ArrayList<>();
            for (TodoTagMapper todoTagMapper : todoTagMapperRepository.findByTodo(todo)) {
                tagList.add(todoTagMapper.getTag().getContent());
            }
            TodoResponseDto todoResponseDto = TodoResponseDto.builder()
                    .todoId(todo.getId())
                    .todocontent(todo.getTodocontent())
                    .tagList(tagList)
                    .completed(todo.isCompleted())
                    .build();
            todoResponseDtoList.add(todoResponseDto);
        }
        return ResponseEntity.ok(todoResponseDtoList);
    }

    //투두 단건 조회
    public ResponseEntity<?> getTodo(Long id, MemberDetailsImpl memberDetails) {

        Todo todo = todoRepository.findById(id).orElseThrow(
                () -> new RuntimeException("투두가 존재하지 않습니다."));
        TodoResponseDto todoResponseDto = TodoResponseDto.builder()
                .todoId(todo.getId())
                .todocontent(todo.getTodocontent())
                .tagList((todoTagMapperRepository.findByTodo(todo).stream()
                        .map(todoTagMapper -> todoTagMapper.getTag().getContent()).collect(Collectors.toList())))
                .completed(todo.isCompleted())
                .build();
        return new ResponseEntity<>(todoResponseDto, HttpStatus.OK);
    }
    //투두 완료
    @Transactional
    public ResponseEntity<?> completedTodo(Long id, MemberDetailsImpl memberDetails) {
        Todo todo = todoRepository.findById(id).orElseThrow(
                ()->new RuntimeException("완료할 투두가 없습니다.")
        );
        if(!todo.getMember().getNickname().equals(memberDetails.getMember().getNickname())) {
            throw new RuntimeException("본인이 작성한 투두만 완료가 가능합니다.");
        }
        todo.completeTask();
        todoRepository.save(todo);
        return ResponseEntity.ok("투두가 완료되었습니다.");

    }
    //투두 수정
    @Transactional
    public ResponseEntity<?> editTodo(Long id, TodoRequestDto todoRequestDto, Member member) {
        Todo todo = todoRepository.findById(id).orElseThrow(
                ()->new RuntimeException("수정할 투두가 없습니다.")
        );
        if(!todo.getMember().getNickname().equals(member.getNickname())) {
            throw new RuntimeException("본인이 작성한 투두만 삭제가 가능합니다.");
        }
        todo.edit(todoRequestDto);
        todoRepository.save(todo);
        return new ResponseEntity<>("투두 수정이 완료되었습니다.", HttpStatus.OK);
    }

    //투두 삭제
    @Transactional
    public ResponseEntity<?> deleteTodo(Long id, MemberDetailsImpl memberDetails) {
        Todo todo = todoRepository.findById(id).orElseThrow(
                () -> new RuntimeException("삭제할 투두가 존재하지 않습니다.")
        );
        if(!todo.getMember().getNickname().equals(memberDetails.getMember().getNickname())) {
            throw new RuntimeException("본인이 작성한 투두만 삭제가 가능합니다.");
        }
        todoTagMapperRepository.deleteAllByTodo(todo);
        todoRepository.deleteById(id);
        return new ResponseEntity<>("투두 삭제가 완료되었습니다.", HttpStatus.OK);
    }
//
//
//
//
//    //캘린더 월별 조회
//
}
