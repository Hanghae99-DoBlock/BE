package com.sparta.doblock.profile.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.Pattern;
import java.lang.reflect.Field;

@Getter
@AllArgsConstructor
public class EditPasswordRequestDto {

    @Pattern(regexp = "^(?=.*\\d)(?=.*[a-zA-Z])(?=.*[!@#$%^&*])[a-z0-9A-Z!@#$%^&*]{8,20}$")
    private String currentPassword;

    @Pattern(regexp = "^(?=.*\\d)(?=.*[a-zA-Z])(?=.*[!@#$%^&*])[a-z0-9A-Z!@#$%^&*]{8,20}$")
    private String newPassword;

    public boolean checkNull() throws IllegalAccessException {

        for (Field f : getClass().getDeclaredFields()){
            if (f.get(this) != null)
                return false;
        } return true;
    }
}
