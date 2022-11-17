package com.sparta.doblock.auth.dto;

import lombok.Data;

@Data
public class KakaoProfile {

    public String id;
    public String connectedAt;
    public Properties properties;
    public KakaoAccount kakaoAccount;

    @Data
    public class Properties {
        public String nickname;
        public String profileImage; //이미지 경로 필드1
        public String thumbnailImage;
    }

    @Data
    public class KakaoAccount {
        public Boolean profileNicknameNeedsAgreement;
        public Boolean profileImageNeedsAgreement;
        public Profile profile;
        public Boolean hasEmail;
        public Boolean emailNeedsAgreement;
        public Boolean isEmailValid;
        public Boolean isEmailVerified;
        public String email;

        @Data
        public class Profile{
            public String nickname;
            public String thumbnailImageUrl;
            public String profileImageUrl; //이미지 경로 필드2
            public Boolean isDefaultImage;
        }
    }
}
