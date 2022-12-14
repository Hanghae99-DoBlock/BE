package com.sparta.doblock.member.repository;

import com.sparta.doblock.member.entity.Member;

import java.util.List;

public interface MemberRepositoryCustom {

    List<Member> searchAllByEmailOrNickname(Long lastId, String keyword);
}
