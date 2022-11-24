package com.sparta.doblock.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.doblock.exception.ErrorCodes;
import com.sparta.doblock.member.entity.Member;
import com.sparta.doblock.member.entity.MemberDetailsImpl;
import com.sparta.doblock.member.repository.MemberRepository;
import com.sparta.doblock.security.token.TokenDto;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Key;
import java.time.Instant;
import java.util.Date;

@Slf4j
@Component
public class TokenProvider {

    private static final String AUTHORITIES_KEY = "auth";
    private static final String BEARER_TYPE = "bearer";
    private static final long ACCESS_TOKEN_EXPIRATION_TIME = 1000 * 60 * 60 * 24;
    private static final long REFRESH_TOKEN_EXPIRATION_TIME = 1000 * 60 * 60 * 24 * 7;

    private final MemberRepository memberRepository;
    private final Key key;

    @Autowired
    public TokenProvider(@Value("${jwt.secret.key}") String secretKey, MemberRepository memberRepository){
        this.memberRepository = memberRepository;
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public TokenDto generateTokenDto(Member member){

        long now = (new Date()).getTime();
        Date accessTokenExpiresIn = new Date(now + ACCESS_TOKEN_EXPIRATION_TIME);

        String accessToken = Jwts.builder()
                .setSubject(member.getEmail())
                .claim(AUTHORITIES_KEY, member.getAuthority())
                .claim("memberId", member.getId())
                .claim("nickname", member.getNickname())
                .claim("profileImage", member.getProfileImage())
                .setExpiration(accessTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        String refreshToken = Jwts.builder()
                .setExpiration(new Date(now + REFRESH_TOKEN_EXPIRATION_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return TokenDto.builder()
                .grantType(BEARER_TYPE)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accessTokenExpiresIn(accessTokenExpiresIn.getTime())
                .build();
    }

    public Authentication getAuthentication(String accessToken, HttpServletResponse response) throws IOException {

        Claims claims = parseClaims(accessToken);

        Assert.notNull(claims.get(AUTHORITIES_KEY), "권한 정보가 없는 서명입니다.");

        if (claims.getExpiration().toInstant().toEpochMilli() < Instant.now().toEpochMilli()) {
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().println(
                    new ObjectMapper().writeValueAsString(
                            ErrorCodes.NOT_VALID_TOKEN
                    )
            );
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }

        String email = claims.getSubject();
        Member member = memberRepository.findByEmail(email).orElseThrow(
                () -> new UsernameNotFoundException("유저를 찾을 수 없습니다.")
        );

        MemberDetailsImpl memberDetails = new MemberDetailsImpl(member);

        return new UsernamePasswordAuthenticationToken(memberDetails, accessToken, memberDetails.getAuthorities());
    }

    private Claims parseClaims(String accessToken){
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        }catch (ExpiredJwtException e){
            return e.getClaims();
        }
    }

    public boolean validateToken(String token){
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("유효하지 않은 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.info("만료된 서명입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 서명입니다.");
        } catch (IllegalArgumentException e) {
            log.info("서명을 입력해주세요.");
        }
        return false;
    }
}
