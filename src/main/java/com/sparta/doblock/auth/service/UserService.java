package com.sparta.doblock.auth.service;

import com.sparta.doblock.member.entity.Authority;
import com.sparta.doblock.member.entity.Member;
import com.sparta.doblock.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final MemberRepository memberRepository;

    public void processOAuthPostLogin(String nickname) {
        Member member = memberRepository.findByNickname(nickname).orElse(
                Member.builder()
                        .nickname(nickname)
                        .authority(Authority.ROLE_SOCIAL)
                        .build()
        );
        memberRepository.save(member);
    }

}