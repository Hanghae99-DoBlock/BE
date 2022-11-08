package com.sparta.doblock.feed.entity;

import com.sparta.doblock.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Feed {

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
    private String content;

    @Column(columnDefinition = "mediumblob")
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "post_img_url_list",
            joinColumns = @JoinColumn(name = "post_id")
    )
    private List<String> feedImageList;

    public void update(List<String> todoList, String content, List<String> feedImageList) {
        this.todoList = todoList;
        this.content = content;
        this.feedImageList = feedImageList;
    }
}
