package com.sparta.doblock.member.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sparta.doblock.profile.controller.EditProfileRequestDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Member {

    @Id
    @Column(name = "member_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column
    private String profileImage;

    @Column(nullable = false)
    private String password;

    @JsonIgnore
    @Enumerated(EnumType.STRING)
    private Authority authority;

    public void editProfileImage(String profileImage){
        this.profileImage = profileImage;
    }

    public void editNickname(String nickname){
        this.nickname = nickname != null ? nickname : this.getNickname();
    }

    public void editPassword(String password){
        this.password = password;
    }
}
