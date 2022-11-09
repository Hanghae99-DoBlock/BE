package com.sparta.doblock.member.service;

import com.sparta.doblock.exception.CustomExceptions;
import com.sparta.doblock.member.dto.request.MemberRequestDto;
import com.sparta.doblock.member.entity.Authority;
import com.sparta.doblock.member.entity.Member;
import com.sparta.doblock.member.repository.MemberRepository;
import com.sparta.doblock.security.JwtAuthFilter;
import com.sparta.doblock.security.TokenProvider;
import com.sparta.doblock.security.token.RefreshToken;
import com.sparta.doblock.security.token.RefreshTokenRepository;
import com.sparta.doblock.security.token.TokenDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final TokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${profile.image}")
    private String profileImage;

    @Transactional
    public ResponseEntity<?> signup(MemberRequestDto memberRequestDto) {

        Member member = Member.builder()
                .email(memberRequestDto.getEmail())
                .nickname(memberRequestDto.getNickname())
                .profileImage(profileImage)
                .password(passwordEncoder.encode(memberRequestDto.getPassword()))
                .authority(Authority.ROLE_MEMBER)
                .build();

        memberRepository.save(member);

        return new ResponseEntity<>("회원가입 성공", HttpStatus.OK);
    }

    public ResponseEntity<?> checkEmail(MemberRequestDto memberRequestDto) {

        if (memberRepository.existsByEmail(memberRequestDto.getEmail())){
            return new ResponseEntity<>("이미 사용 중인 이메일입니다.", HttpStatus.BAD_REQUEST);
        } else return new ResponseEntity<>("사용 가능한 이메일입니다.", HttpStatus.OK);
    }

    public ResponseEntity<?> checkNickname(MemberRequestDto memberRequestDto) {

        if (memberRepository.existsByNickname(memberRequestDto.getNickname())){
            return new ResponseEntity<>("이미 사용 중인 닉네임입니다.", HttpStatus.BAD_REQUEST);
        } else return new ResponseEntity<>("사용 가능한 닉네임입니다.", HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<?> login(MemberRequestDto memberRequestDto, HttpServletResponse httpServletResponse) {

        Member member = memberRepository.findByEmail(memberRequestDto.getEmail()).orElseThrow(
                CustomExceptions.NotFoundMemberException::new
        );

        if(!passwordEncoder.matches(memberRequestDto.getPassword(), member.getPassword())) throw new CustomExceptions.NotMatchedPasswordException();

        TokenDto tokenDto = tokenProvider.generateTokenDto(member);

        RefreshToken refreshToken = RefreshToken.builder()
                .key(member.getEmail())
                .value(tokenDto.getRefreshToken())
                .build();

        refreshTokenRepository.save(refreshToken);

        tokenToHeader(tokenDto, httpServletResponse);

        return new ResponseEntity<>("로그인 완료", HttpStatus.OK);
    }

    public void tokenToHeader(TokenDto tokenDto, HttpServletResponse httpServletResponse){

        httpServletResponse.setHeader(JwtAuthFilter.AUTHORIZATION_HEADER, JwtAuthFilter.BEARER_PREFIX + tokenDto.getAccessToken());
        httpServletResponse.setHeader("RefreshToken", tokenDto.getRefreshToken());
        httpServletResponse.setHeader("AccessTokenExpireTime", tokenDto.getAccessTokenExpiresIn().toString());
    }
}
