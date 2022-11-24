package com.sparta.doblock.profile.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Pattern;
import java.lang.reflect.Field;
import java.util.List;

@Getter
@AllArgsConstructor
public class EditProfileRequestDto {

    @Pattern(regexp = "^(?=.*[a-z0-9A-Z가-힣])[a-z0-9A-Z가-힣]{2,6}$")
    private String nickname;

    private MultipartFile profileImage;

    private String currentPassword;

    @Pattern(regexp = "^(?=.*\\d)(?=.*[a-zA-Z])(?=.*[!@#$%^&*])[a-z0-9A-Z!@#$%^&*]{8,20}$")
    private String newPassword;

    private List<String> tagList;

    public boolean checkNull() throws IllegalAccessException {

        for (Field f : getClass().getDeclaredFields()){
            if (f.get(this) != null)
                return false;
        } return true;
    }
}
