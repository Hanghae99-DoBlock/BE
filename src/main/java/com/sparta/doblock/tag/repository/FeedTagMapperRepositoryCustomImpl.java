package com.sparta.doblock.tag.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.doblock.exception.DoBlockExceptions;
import com.sparta.doblock.exception.ErrorCodes;
import com.sparta.doblock.feed.entity.Feed;
import com.sparta.doblock.tag.mapper.MemberTagMapper;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

import static com.sparta.doblock.tag.mapper.QFeedTagMapper.feedTagMapper;

@RequiredArgsConstructor
public class FeedTagMapperRepositoryCustomImpl implements FeedTagMapperRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Feed> searchAllByFeedTagLike(Long lastId, String keyword) {

        BooleanBuilder booleanBuilder = new BooleanBuilder();

        if (keyword.isEmpty()){
            throw new DoBlockExceptions(ErrorCodes.NOT_FOUND_PAGE);

        } else {
            booleanBuilder.and(feedTagMapper.tag.tagContent.contains(keyword));
        }

        if (lastId != null){
            booleanBuilder.and(feedTagMapper.feed.id.lt(lastId));
        }

        return jpaQueryFactory.select(feedTagMapper.feed)
                .from(feedTagMapper)
                .where(booleanBuilder)
                .orderBy(feedTagMapper.id.desc())
                .limit(8)
                .fetch()
                .stream()
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public List<Feed> searchAllByMemberTagLike(Long lastId, List<MemberTagMapper> memberTagMapperList) {

        BooleanBuilder booleanBuilder = new BooleanBuilder();

        if (memberTagMapperList.isEmpty()){
            throw new DoBlockExceptions(ErrorCodes.NOT_FOUND_PAGE);

        } else {
            for (MemberTagMapper memberTagMapper : memberTagMapperList){
                booleanBuilder.or(feedTagMapper.tag.tagContent.contains(memberTagMapper.getTag().getTagContent()));
            }
        }

        if (lastId != null){
            booleanBuilder.and(feedTagMapper.feed.id.lt(lastId));
        }

        return jpaQueryFactory.select(feedTagMapper.feed)
                .from(feedTagMapper)
                .where(booleanBuilder)
                .orderBy(feedTagMapper.id.desc())
                .limit(8)
                .fetch()
                .stream()
                .distinct()
                .collect(Collectors.toList());
    }
}
