package com.sparta.doblock.todo.entity;

import com.sparta.doblock.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "member_id", "date" }) })
public class TodoDate {

    @Id
    @Column(name = "tododate_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column
    private LocalDate date;

    @Column
    private int lastIndex;

    public int getTodoIndex() {
        return ++ this.lastIndex;
    }

    public void editLastIndex(int lastIndex) {
        this.lastIndex = lastIndex;
    }
}
