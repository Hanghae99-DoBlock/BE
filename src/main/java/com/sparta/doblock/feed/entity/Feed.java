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
    private String feedContent;

    @Column(columnDefinition = "mediumblob")
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "post_img_url_list",
            joinColumns = @JoinColumn(name = "post_id")
    )
    private List<String> feedImageList;

    public boolean isEqual(Feed other) {
        return this.id.equals(other.getId());
    }

    public void update(String feedContent, List<String> feedImageList) {
        if (!Objects.isNull(feedContent))
            this.feedContent = feedContent;
        if (!Objects.isNull(feedImageList) && !feedImageList.isEmpty())
            this.feedImageList = feedImageList;
    }
}
