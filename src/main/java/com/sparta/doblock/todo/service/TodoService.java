package com.sparta.doblock.todo.service;

import com.sparta.doblock.member.entity.MemberDetailsImpl;
import com.sparta.doblock.tag.entity.Tag;
import com.sparta.doblock.tag.mapper.TodoTagMapper;
import com.sparta.doblock.tag.repository.TagRepository;
import com.sparta.doblock.tag.repository.TodoTagMapperRepository;
import com.sparta.doblock.todo.dto.request.TodoRequestDto;
import com.sparta.doblock.todo.entity.Todo;
import com.sparta.doblock.todo.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

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
                .content(todoRequestDto.getTodo())
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
}
