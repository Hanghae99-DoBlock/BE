package com.sparta.doblock.feed.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@AllArgsConstructor
public class FeedRequestDto {
    private List<Long> todoIdList;
    private String content;
    private MultipartFile feedImage1;
    private MultipartFile feedImage2;
    private MultipartFile feedImage3;
    private MultipartFile feedImage4;
    private List<String> tagList;
}
