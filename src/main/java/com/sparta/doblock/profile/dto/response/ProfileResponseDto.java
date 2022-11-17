package com.sparta.doblock.profile.dto.response;

import com.sparta.doblock.feed.dto.response.FeedResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class ProfileResponseDto {

    private Long memberId;
    private String profileImage;
    private String nickname;
    private String email;
    private boolean followOrNot;
    private Long countFeed;
    private Long countFollower;
    private Long countFollowing;
    private List<FeedResponseDto> feedResponseDtoList;
}
