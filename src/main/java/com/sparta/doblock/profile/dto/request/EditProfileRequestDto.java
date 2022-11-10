package com.sparta.doblock.profile.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@AllArgsConstructor
public class EditProfileRequestDto {

    private String nickname;
    private MultipartFile profileImage;
    private String currentPassword;
    private String newPassword;
}
