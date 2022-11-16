package com.sparta.doblock.auth.dto;

import lombok.Data;

@Data //(2)
public class OauthToken { //(1)
    private String accessToken;
    private String tokenType;
    private String refreshToken;
    private int expiresIn;
    private String scope;
    private String idToken;
    private int refreshTokenExpiresIn;

}