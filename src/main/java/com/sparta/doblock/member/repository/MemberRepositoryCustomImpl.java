package com.sparta.doblock.member.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.doblock.exception.DoBlockExceptions;
import com.sparta.doblock.exception.ErrorCodes;
import com.sparta.doblock.member.entity.Member;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

import static com.sparta.doblock.member.entity.QMember.member;

@RequiredArgsConstructor
public class MemberRepositoryCustomImpl implements MemberRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Member> searchAllByEmailOrNickname(Long lastId, String keyword) {

        BooleanBuilder booleanBuilder = new BooleanBuilder();

        if (keyword.isEmpty()){
            throw new DoBlockExceptions(ErrorCodes.NOT_FOUND_PAGE);

        } else {
            booleanBuilder.and(member.nickname.contains(keyword).or(member.email.contains(keyword)));
        }

        if (lastId != null){
            booleanBuilder.and(member.id.lt(lastId));
        }

        return jpaQueryFactory.selectFrom(member)
                .where(booleanBuilder)
                .orderBy(member.id.desc())
                .limit(10)
                .fetch()
                .stream()
                .distinct()
                .collect(Collectors.toList());
    }
}
