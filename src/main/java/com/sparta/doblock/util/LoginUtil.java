package com.sparta.doblock.util;

import com.sparta.doblock.exception.DoBlockExceptions;
import com.sparta.doblock.exception.ErrorCodes;
import com.sparta.doblock.member.entity.Member;
import com.sparta.doblock.member.entity.MemberDetailsImpl;
import com.sparta.doblock.security.TokenProvider;
import com.sparta.doblock.security.entity.RefreshToken;
import com.sparta.doblock.security.repository.RefreshTokenRepository;
import com.sparta.doblock.security.dto.TokenDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;

@Component
@RequiredArgsConstructor
public class LoginUtil {

    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public TokenDto generateToken(Member member) {

        TokenDto tokenDto = tokenProvider.generateTokenDto(member);

        RefreshToken refreshToken = RefreshToken.builder()
                .id(member.getEmail())
                .value(tokenDto.getRefreshToken())
                .build();

        refreshTokenRepository.save(refreshToken);

        return tokenDto;
    }

    public HttpHeaders setHttpHeaders(TokenDto tokenDto) {

        HttpHeaders httpHeaders = new HttpHeaders();

        httpHeaders.set("Authorization", "Bearer " + tokenDto.getAccessToken());
        httpHeaders.set("RefreshToken", tokenDto.getRefreshToken());
        httpHeaders.set("Access-Token-Expire-Time", String.valueOf(tokenDto.getAccessTokenExpiresIn()));

        return httpHeaders;
    }

    public void forceLogin(Member member) {

        MemberDetailsImpl memberDetails = new MemberDetailsImpl(member);
        Authentication authentication = new UsernamePasswordAuthenticationToken(memberDetails, null, memberDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    public void validateRefreshToken(HttpServletRequest httpServletRequest, Member member) {

        if (!tokenProvider.validateToken(httpServletRequest.getHeader("RefreshToken"))) {
            throw new DoBlockExceptions(ErrorCodes.NOT_VALID_AUTHENTICATION);
        }

        RefreshToken refreshToken = refreshTokenRepository.findById(member.getEmail()).orElseThrow(
                () -> new DoBlockExceptions(ErrorCodes.NOT_VALID_AUTHENTICATION)
        );



        if (!refreshToken.getValue().equals(httpServletRequest.getHeader("RefreshToken"))) {
            throw new DoBlockExceptions(ErrorCodes.NOT_VALID_AUTHENTICATION);
        }
    }
}
