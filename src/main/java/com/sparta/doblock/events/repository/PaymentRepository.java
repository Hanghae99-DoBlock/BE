package com.sparta.doblock.events.repository;

import com.sparta.doblock.events.entity.Payment;
import com.sparta.doblock.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByMemberOrderByPostedAtDesc(Member member);
}
