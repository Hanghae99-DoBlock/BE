package com.sparta.doblock.events.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BadgeType {

    CTT("갓생스타터", "https://sparta-doblock.s3.ap-northeast-2.amazonaws.com/badge/%ED%88%AC%EB%91%901.png",
            "https://sparta-doblock.s3.ap-northeast-2.amazonaws.com/badge/%ED%88%AC%EB%91%901-1.png",
            "일정 3개를 달성하셨네요! 갓생은 이제부터 시작입니다&#x1F423"),
    CTTY("계란 한판","https://sparta-doblock.s3.ap-northeast-2.amazonaws.com/badge/%ED%88%AC%EB%91%902.png",
            "https://sparta-doblock.s3.ap-northeast-2.amazonaws.com/badge/%ED%88%AC%EB%91%902-2.png",
            "일정 30개를 달성하셨네요! 시간 참 빠르죠? 벌써 계란 한판&#x1F602"),
    CTF("진짜 갓생러", "https://sparta-doblock.s3.ap-northeast-2.amazonaws.com/badge/%ED%88%AC%EB%91%903.png",
            "https://sparta-doblock.s3.ap-northeast-2.amazonaws.com/badge/%ED%88%AC%EB%91%903-3.png",
            "일정 50개를 달성하셨네요! 진짜 갓생러가 나타났다&#x1F913"),

    FMS("저도 친구 있어요", "https://sparta-doblock.s3.ap-northeast-2.amazonaws.com/badge/%ED%8C%94%EB%A1%9C%EC%9A%B01.png",
            "https://sparta-doblock.s3.ap-northeast-2.amazonaws.com/badge/%ED%8C%94%EB%A1%9C%EC%9A%B01-1.png",
            "친구 7명을 팔로우하셨네요! 소중한 인연을 더 찾아봐요&#x1F64B"),
    FMF("준 연예인", "https://sparta-doblock.s3.ap-northeast-2.amazonaws.com/badge/%ED%8C%94%EB%A1%9C%EC%9A%B02.png",
            "https://sparta-doblock.s3.ap-northeast-2.amazonaws.com/badge/%ED%94%BC%EB%93%9C2-2.png",
            "친구 50명을 팔로우하셨네요! 이러다가 연예계 진출하겠어요&#x1F607"),
    FMH("이곳의 셀럽", "https://sparta-doblock.s3.ap-northeast-2.amazonaws.com/badge/%ED%8C%94%EB%A1%9C%EC%9A%B03.png",
            "https://sparta-doblock.s3.ap-northeast-2.amazonaws.com/badge/%ED%94%BC%EB%93%9C3-3.png",
            "친구 150명을 팔로우하셨네요! 아직도 저를 모르는 분이 있나요&#x1F929"),

    CFO("수줍은 첫 피드", "https://sparta-doblock.s3.ap-northeast-2.amazonaws.com/badge/%ED%94%BC%EB%93%9C1.png",
            "https://sparta-doblock.s3.ap-northeast-2.amazonaws.com/badge/%ED%94%BC%EB%93%9C1-1.png",
            "첫 피드를 작성하셨네요! 저희와 더 많은 블록을 쌓아봐요&#x1FAE3"),
    CFT("블럭 수집가", "https://sparta-doblock.s3.ap-northeast-2.amazonaws.com/badge/%ED%94%BC%EB%93%9C2.png",
            "https://sparta-doblock.s3.ap-northeast-2.amazonaws.com/badge/%ED%94%BC%EB%93%9C2-2.png",
            "피드 30개를 작성하셨네요! 쌓아놓은 블록만 봐도 배가 부릅니다&#x1F4AA"),
    CFF("소통왕", "https://sparta-doblock.s3.ap-northeast-2.amazonaws.com/badge/%ED%94%BC%EB%93%9C3.png",
            "https://sparta-doblock.s3.ap-northeast-2.amazonaws.com/badge/%ED%94%BC%EB%93%9C3-3.png",
            "피드 50개를 작성하셨네요! 왕좌를 오래토록 지켜주시옵소서&#x1F3C6"),

    SAO("따뜻함의 시작", "https://sparta-doblock.s3.ap-northeast-2.amazonaws.com/badge/%EB%A6%AC%EC%95%A1%EC%85%981.png",
            "https://sparta-doblock.s3.ap-northeast-2.amazonaws.com/badge/%EB%A6%AC%EC%95%A1%EC%85%981-1.png",
            "첫 소셜 활동을 하셨네요! 누군가 당신의 따뜻함을 기억할거에요&#x1F970"),
    SAT("분위기 메이커", "https://sparta-doblock.s3.ap-northeast-2.amazonaws.com/badge/%EB%A6%AC%EC%95%A1%EC%85%982.png",
            "https://sparta-doblock.s3.ap-northeast-2.amazonaws.com/badge/%EB%A6%AC%EC%95%A1%EC%85%982-2.png",
            "소셜 활동을 30번 달성하셨네요! 이 구역 분위기는 내가 집도한다&#x1F64C"),
    SAF("센스쟁이", "https://sparta-doblock.s3.ap-northeast-2.amazonaws.com/badge/%EB%A6%AC%EC%95%A1%EC%85%983.png",
            "https://sparta-doblock.s3.ap-northeast-2.amazonaws.com/badge/%EB%A6%AC%EC%95%A1%EC%85%983-3.png",
            "소셜 활동을 50번 달성하셨네요! 덕분에 기분좋은 날이 될 것만 같아요&#x1F973");

    private final String badgeName;
    private final String grayImage;
    private final String badgeImage;
    private final String badgeDetail;
}
