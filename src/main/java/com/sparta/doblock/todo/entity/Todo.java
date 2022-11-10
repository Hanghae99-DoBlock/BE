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
    private String todoContent;

    @Column(nullable = false)
    private boolean completed;

    public void completeTask() {
        this.completed = true;
    }

    public void edit(TodoRequestDto todoRequestDto) {

        this.date = todoRequestDto.getYear() != 0 ? LocalDate.of(todoRequestDto.getYear(), this.date.getMonth(), this.date.getDayOfMonth()) : this.date;
        this.date = todoRequestDto.getMonth() != 0 ? LocalDate.of(this.date.getYear(), todoRequestDto.getMonth(), this.date.getDayOfMonth()) : this.date;
        this.date = todoRequestDto.getDay() != 0 ? LocalDate.of(this.date.getYear(), this.date.getMonth(), todoRequestDto.getDay()) : this.date;

        this.startTime = todoRequestDto.getStartHour() != 0 ? LocalTime.of(todoRequestDto.getStartHour(), this.startTime.getMinute()) : this.startTime;
        this.startTime = todoRequestDto.getStartMinute() != 0 ? LocalTime.of(this.startTime.getHour(), todoRequestDto.getStartMinute()) : this.startTime;

        this.endTime = todoRequestDto.getEndHour() != 0 ? LocalTime.of(todoRequestDto.getEndHour(), this.endTime.getMinute()) : this.endTime;
        this.endTime = todoRequestDto.getEndMinute() != 0 ? LocalTime.of(this.endTime.getHour(), todoRequestDto.getEndMinute()) : this.endTime;

        this.todoContent = todoRequestDto.getTodoContent() != null ? todoRequestDto.getTodoContent() : this.todoContent;
    }
}
