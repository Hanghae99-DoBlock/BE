package com.sparta.doblock.auth.dto;

import lombok.Data;

@Data
public class OauthToken {
    private String accessToken;
    private String tokenType;
    private String refreshToken;
    private int expiresIn;
    private String scope;
    private String idToken;
    private int refreshTokenExpiresIn;
}
