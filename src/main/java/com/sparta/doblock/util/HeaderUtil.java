package com.sparta.doblock.util;

import com.sparta.doblock.security.token.TokenDto;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
public class HeaderUtil {

    public HttpHeaders getHttpHeaders(TokenDto tokenDto){
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", "Bearer " + tokenDto.getAccessToken());
        httpHeaders.set("RefreshToken", tokenDto.getRefreshToken());
        httpHeaders.set("Access-Token-Expire-Time", String.valueOf(tokenDto.getAccessTokenExpiresIn()));
        return httpHeaders;
    }
}
