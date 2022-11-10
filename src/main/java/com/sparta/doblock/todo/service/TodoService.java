package com.sparta.doblock.todo.service;

import com.sparta.doblock.member.entity.Member;
import com.sparta.doblock.member.entity.MemberDetailsImpl;
import com.sparta.doblock.tag.entity.Tag;
import com.sparta.doblock.tag.mapper.FeedTagMapper;
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
                .todoContent(todoRequestDto.getTodoContent())
                .completed(false)
                .build();

        todoRepository.save(todo);

        for (String tagContent : todoRequestDto.getTagList()) {
            if (! tagRepository.existsByTagContent(tagContent)) {
                Tag tag = Tag.builder()
                        .tagContent(tagContent)
                        .build();
                tagRepository.save(tag);
            }

            Tag tag = tagRepository.findByTagContent(tagContent).orElseThrow(NullPointerException::new);

            TodoTagMapper todoTagMapper = TodoTagMapper.builder()
                    .todo(todo)
                    .tag(tag)
                    .build();

            todoTagMapperRepository.save(todoTagMapper);
        }

        return ResponseEntity.ok("성공적으로 투두를 생성하였습니다");
    }

    public ResponseEntity<?> getTodayTodo(TodoRequestDto todoRequestDto, MemberDetailsImpl memberDetails) {

        LocalDate date = LocalDate.of(todoRequestDto.getYear(), todoRequestDto.getMonth(), todoRequestDto.getDay());

        List<Todo> todoList = todoRepository.findAllByMemberAndDate(memberDetails.getMember(), date);
        List<TodoResponseDto> todoResponseDtoList = new ArrayList<>();

        for (Todo todo : todoList) {

            List<String> tagList = new ArrayList<>();
            for (TodoTagMapper todoTagMapper : todoTagMapperRepository.findByTodo(todo)) {
                tagList.add(todoTagMapper.getTag().getTagContent());
            }

            TodoResponseDto todoResponseDto = TodoResponseDto.builder()
                    .todoId(todo.getId())
                    .todoContent(todo.getTodoContent())
                    .tagList(tagList)
                    .completed(todo.isCompleted())
                    .build();

            todoResponseDtoList.add(todoResponseDto);
        }

        return ResponseEntity.ok(todoResponseDtoList);
    }

    public ResponseEntity<?> getTodo(Long id, MemberDetailsImpl memberDetails) {

        Todo todo = todoRepository.findById(id).orElseThrow(
                () -> new RuntimeException("투두가 존재하지 않습니다."));

        TodoResponseDto todoResponseDto = TodoResponseDto.builder()
                .todoId(todo.getId())
                .todoContent(todo.getTodoContent())
                .tagList((todoTagMapperRepository.findByTodo(todo).stream()
                        .map(todoTagMapper -> todoTagMapper.getTag().getTagContent()).collect(Collectors.toList())))
                .completed(todo.isCompleted())
                .build();

        return ResponseEntity.ok(todoResponseDto);
    }

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

    @Transactional
    public ResponseEntity<?> editTodo(Long id, TodoRequestDto todoRequestDto, Member member) {

        Todo todo = todoRepository.findById(id).orElseThrow(
                ()->new RuntimeException("수정할 투두가 없습니다.")
        );

        if(!todo.getMember().getNickname().equals(member.getNickname())) {
            throw new RuntimeException("본인이 작성한 투두만 삭제가 가능합니다.");
        }

        todo.edit(todoRequestDto);

        todoTagMapperRepository.deleteAllByTodo(todo);

        for (String tagContent : todoRequestDto.getTagList()) {
            Tag tag = tagRepository.findByTagContent(tagContent).orElse(Tag.builder().tagContent(tagContent).build());

            tagRepository.save(tag);

            if (! todoTagMapperRepository.existsByTodoAndTag(todo, tag)) {
                TodoTagMapper feedTagMapper = TodoTagMapper.builder()
                        .todo(todo)
                        .tag(tag)
                        .build();

                todoTagMapperRepository.save(feedTagMapper);
            }
        }

        todoRepository.save(todo);

        return ResponseEntity.ok("투두 수정이 완료되었습니다.");
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

        return ResponseEntity.ok("투두 삭제가 완료되었습니다.");
    }

    public ResponseEntity<?> getMonthTodo(TodoRequestDto todoRequestDto, MemberDetailsImpl memberDetails) {

        LocalDate startDate = LocalDate.of(todoRequestDto.getYear(), todoRequestDto.getMonth(), 1);
        LocalDate endDate = LocalDate.of(todoRequestDto.getYear(), todoRequestDto.getMonth(), startDate.lengthOfMonth());

        List<TodoResponseDto> todoResponseDtoList = new ArrayList<>();

        for (LocalDate date = startDate; date.isBefore(endDate); date = date.plusDays(1)){

            List<Todo> todoList = todoRepository.findAllByMemberAndDate(memberDetails.getMember(), date);

            for (Todo todo : todoList) {

                List<String> tagList = new ArrayList<>();

                for (TodoTagMapper todoTagMapper : todoTagMapperRepository.findByTodo(todo)) {
                    tagList.add(todoTagMapper.getTag().getTagContent());
                }

                TodoResponseDto todoResponseDto = TodoResponseDto.builder()
                        .todoId(todo.getId())
                        .todoContent(todo.getTodoContent())
                        .tagList(tagList)
                        .completed(todo.isCompleted())
                        .day(todo.getDate().getDayOfMonth())
                        .build();

                todoResponseDtoList.add(todoResponseDto);
            }
        }

        return ResponseEntity.ok(todoResponseDtoList);
    }

}
