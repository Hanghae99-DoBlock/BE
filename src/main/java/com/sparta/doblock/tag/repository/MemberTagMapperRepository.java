package com.sparta.doblock.tag.repository;

import com.sparta.doblock.member.entity.Member;
import com.sparta.doblock.tag.entity.Tag;
import com.sparta.doblock.tag.mapper.MemberTagMapper;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberTagMapperRepository extends JpaRepository<MemberTagMapper, Long> {

    List<MemberTagMapper> findAllByMember(Member member);
    void deleteAllByMember(Member member);
    boolean existsByMemberAndTag(Member member, Tag tag);
}
