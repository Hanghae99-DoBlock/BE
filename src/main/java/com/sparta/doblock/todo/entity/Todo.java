package com.sparta.doblock.todo.entity;

import com.sparta.doblock.member.entity.Member;
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
public class Todo {

    @Id
    @Column(name = "todo_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(nullable = false)
    private int year;

    @Column(nullable = false)
    private int month;

    @Column(nullable = false)
    private int day;

    @Column
    private int startHour;

    @Column
    private int startMinute;

    @Column
    private int endHour;

    @Column
    private int endMinute;

    @Column(nullable = false)
    private String todo;

    @Column(nullable = false)
    private boolean completed;

    public void completeTask() {
        this.completed = true;
    }
}
