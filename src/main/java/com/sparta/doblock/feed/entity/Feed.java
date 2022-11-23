package com.sparta.doblock.feed.entity;

import com.sparta.doblock.member.entity.Member;
import com.sparta.doblock.util.TimeStamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Feed extends TimeStamp {

    @Id
    @Column(name = "feed_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(columnDefinition = "mediumblob")
    @ElementCollection(fetch = FetchType.LAZY)
    private List<String> todoList;

    @Column
    private String feedTitle;

    @Column
    private String feedContent;

    @Column(columnDefinition = "mediumblob")
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "post_img_url_list",
            joinColumns = @JoinColumn(name = "post_id")
    )
    private List<String> feedImageList;

    @Column
    private String feedColor;

    @Column
    private boolean eventFeed;

    public void update(String feedTitle, String feedContent, String feedColor) {
        if (!Objects.isNull(feedTitle))
            this.feedTitle = feedTitle;
        if (!Objects.isNull(feedContent))
            this.feedContent = feedContent;
        if (!Objects.isNull(feedColor))
            this.feedColor = feedColor;
    }
}
