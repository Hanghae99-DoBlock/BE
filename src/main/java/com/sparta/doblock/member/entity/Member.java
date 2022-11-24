package com.sparta.doblock.member.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "nickname", "authority" }) })
public class Member {

    @Id
    @Column(name = "member_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Column(unique = true)
    private String socialId;

    @Column
    private String socialCode;

    @Column
    private String profileImage;

    @Column(nullable = false)
    private String password;

    @JsonIgnore
    @Enumerated(EnumType.STRING)
    @Column(name = "authority")
    private Authority authority;
    
    // compares two member entity
    public boolean isEqual(Member other) {
        return this.id.equals(other.getId());
    }

    public void editProfileImage(String profileImage){
        this.profileImage = profileImage;
    }

    public void editNickname(String nickname){
        this.nickname = nickname;
    }

    public void editPassword(String password){
        this.password = password;
    }
}
