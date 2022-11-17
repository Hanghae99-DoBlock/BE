package com.sparta.doblock.todo.entity;

import com.sparta.doblock.member.entity.Member;
import com.sparta.doblock.todo.dto.request.TodoRequestDto;
import com.sparta.doblock.util.TimeStamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "date_id", nullable = false)
    private TodoDate todoDate;

    @Column(nullable = false)
    private String todoContent;

    @Column(nullable = false)
    private boolean completed;

    @Column
    private String todoMemo;

    @Column
    private int todoIndex;

    public void setTodoIndex(int index) {
        this.todoIndex = index;
    }

    public void completeTask() {
        this.completed = !completed;
    }

    public void edit(TodoRequestDto todoRequestDto, TodoDate todoDate) {
        this.todoDate = todoDate;
        this.todoContent = todoRequestDto.getTodoContent() != null ? todoRequestDto.getTodoContent() : this.todoContent;
        this.todoMemo = todoRequestDto.getTodoMemo() != null ? todoRequestDto.getTodoMemo() : this.todoMemo;
    }
}
