package com.sparta.doblock.feed.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.doblock.feed.entity.Feed;
import com.sparta.doblock.member.entity.Member;
import com.sparta.doblock.profile.entity.Follow;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.sparta.doblock.feed.entity.QFeed.feed;

@RequiredArgsConstructor
public class FeedRepositoryCustomImpl implements FeedRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Feed> searchAllByFollow(Long lastId, Member fromMember, List<Follow> followingList) {

        BooleanBuilder booleanBuilder = new BooleanBuilder();

        for (Follow follow : followingList) {
            booleanBuilder.or(feed.member.email.eq(follow.getToMember().getEmail()));
        }

        if (lastId != null) {
            booleanBuilder.and(feed.id.lt(lastId));
        }

        return jpaQueryFactory.selectFrom(feed)
                .where(booleanBuilder.or(feed.member.email.eq(fromMember.getEmail())))
                .orderBy(feed.id.desc())
                .limit(8)
                .fetch();
    }

    @Override
    public List<Feed> searchAllByMember(Long lastId, Member member) {

        BooleanBuilder booleanBuilder = new BooleanBuilder();

        if (lastId != null) {
            booleanBuilder.and(feed.id.lt(lastId));
        }

        return jpaQueryFactory.selectFrom(feed)
                .where(booleanBuilder.and(feed.member.email.eq(member.getEmail())))
                .orderBy(feed.id.desc())
                .limit(8)
                .fetch();
    }
}
