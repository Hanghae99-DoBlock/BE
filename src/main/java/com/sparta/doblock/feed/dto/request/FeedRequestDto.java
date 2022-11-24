package com.sparta.doblock.feed.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@AllArgsConstructor
public class FeedRequestDto {

    private List<Long> todoIdList;
    private String feedTitle;
    private String feedContent;
    private List<MultipartFile> feedImageList;
    private List<String> tagList;
    private String feedColor;
}
