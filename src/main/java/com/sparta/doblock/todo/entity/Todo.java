package com.sparta.doblock.todo.entity;

import com.sparta.doblock.member.entity.Member;
import com.sparta.doblock.todo.dto.request.TodoRequestDto;
import com.sparta.doblock.util.TimeStamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Todo extends TimeStamp {

    @Id
    @Column(name = "todo_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    @Column(nullable = false)
    private String todocontent;

    @Column(nullable = false)
    private boolean completed;

    public void completeTask() {
        this.completed = true;
    }

    public void edit(TodoRequestDto todoRequestDto) {
        this.todocontent = todoRequestDto.getTodocontent();
    }
}
