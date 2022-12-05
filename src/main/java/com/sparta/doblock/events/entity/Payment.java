package com.sparta.doblock.events.entity;

import com.sparta.doblock.member.entity.Member;
import com.sparta.doblock.util.TimeStamp;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment extends TimeStamp {

    @Id
    @Column(name = "payment_id")
    private String paymentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column
    private String tid;

    @Column
    private int amount;

    @Column
    private boolean paycheck;

    public void updateTid(String tid){
        this.tid = tid;
    }

    public void checkedPayment(){
        this.paycheck = !this.paycheck;
    }
}
