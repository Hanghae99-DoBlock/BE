package com.sparta.doblock.member.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberRequestDto {

    private String email;
    private String nickname;
    private String password;
}
