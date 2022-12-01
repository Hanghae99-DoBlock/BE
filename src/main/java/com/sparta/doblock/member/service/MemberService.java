package com.sparta.doblock.member.service;

import com.sparta.doblock.exception.DoBlockExceptions;
import com.sparta.doblock.exception.ErrorCodes;
import com.sparta.doblock.member.dto.request.MemberRequestDto;
import com.sparta.doblock.member.entity.Authority;
import com.sparta.doblock.member.entity.Member;
import com.sparta.doblock.member.repository.MemberRepository;
import com.sparta.doblock.security.token.TokenDto;
import com.sparta.doblock.util.LoginUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final LoginUtil loginUtil;

    @Value("${profile.image}")
    private String profileImage;

    @Transactional
    public ResponseEntity<?> signup(MemberRequestDto memberRequestDto) {

        if (memberRepository.existsByEmail(memberRequestDto.getEmail())){
            throw new DoBlockExceptions(ErrorCodes.DUPLICATED_EMAIL);
        }

        if (memberRepository.existsByNicknameAndAuthority(memberRequestDto.getNickname(), Authority.ROLE_MEMBER)){
            throw new DoBlockExceptions(ErrorCodes.DUPLICATED_NICKNAME);
        }

        Member member = Member.builder()
                .email(memberRequestDto.getEmail())
                .nickname(memberRequestDto.getNickname())
                .profileImage(profileImage)
                .password(passwordEncoder.encode(memberRequestDto.getPassword()))
                .authority(Authority.ROLE_MEMBER)
                .build();

        memberRepository.save(member);

        return ResponseEntity.ok("회원가입 성공");
    }

    public ResponseEntity<?> checkEmail(MemberRequestDto memberRequestDto) {

        if (memberRepository.existsByEmail(memberRequestDto.getEmail())){
            throw new DoBlockExceptions(ErrorCodes.DUPLICATED_EMAIL);
        } else return ResponseEntity.ok("사용 가능한 이메일입니다.");
    }

    public ResponseEntity<?> checkNickname(MemberRequestDto memberRequestDto) {

        if (memberRepository.existsByNicknameAndAuthority(memberRequestDto.getNickname(), Authority.ROLE_MEMBER)){
            throw new DoBlockExceptions(ErrorCodes.DUPLICATED_NICKNAME);
        } else return ResponseEntity.ok("사용 가능한 닉네임입니다.");
    }

    @Transactional
    public ResponseEntity<?> login(MemberRequestDto memberRequestDto) {

        Member member = memberRepository.findByEmail(memberRequestDto.getEmail()).orElseThrow(
                () -> new DoBlockExceptions(ErrorCodes.NOT_FOUND_MEMBER)
        );

        if(!passwordEncoder.matches(memberRequestDto.getPassword(), member.getPassword())){
            throw new DoBlockExceptions(ErrorCodes.NOT_VALID_PASSWORD);
        }

        TokenDto tokenDto = loginUtil.generateToken(member);

        HttpHeaders httpHeaders = loginUtil.setHttpHeaders(tokenDto);

        return ResponseEntity.ok().headers(httpHeaders).body("로그인 성공");
    }
}
