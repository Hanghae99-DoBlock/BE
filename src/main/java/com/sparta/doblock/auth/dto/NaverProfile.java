package com.sparta.doblock.auth.dto;

import lombok.Data;

@Data
public class NaverProfile {
    public Response response;

    @Data
    public class Response { //(1)
        private String id;
        private String profile_image; // 이미지 경로 필드1
        private String email;
        private String name;
    }
}