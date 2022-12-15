# <img src="https://user-images.githubusercontent.com/108126419/207803210-3720fca9-3c05-43fe-a662-bd619bf1ce14.jpg" width="40">&nbsp;[Do!Block] 피드형 Todolist SNS

## Do!Block 소개 | About Us

### 할 일을 기록하고 공유하는 피드형 SNS! <br>

<img src="https://user-images.githubusercontent.com/108126419/207803730-a2a90832-b6d3-4948-8895-7385382cb9ec.png" width="800">

> 하루 한 줌은 체득하고 싶은 습관들을 기록하여 꾸준히 이뤄나갈 수 있도록 도와주는 서비스입니다.<br>
> 사용자들이 단순히 자신의 습관을 만드는 것보다 좀 더 재미있게 습관을 형성할 수 있도록 하자는 취지에서 시작된 프로젝트입니다. <br>
> 따라서 내 습관뿐만 아니라 친구의 습관까지 확인하고 독려할 수 있습니다.
<br>

- **[Do!Block 바로가기](https://www.doblock.shop/)<br>**
- **[발표 자료](https://docs.google.com/presentation/d/1u2x1SL4Bt863htJeiWeb8mTztDs20Rne1hU_DN310EU/edit?usp=sharing)<br>**
- **[팀 노션 주소](https://legendary-scaffold-c21.notion.site/Do-Block-03bf205c16b44de293a37f1a738eadac)**
- **[시연 영상](https://youtu.be/P7UCIujReOk)<br>**
  <br>
  <br>

## 📆 프로젝트 기간 <br>

<ul>
  <li>개발 기간: 2022/11/04 ~ 2022/12/15(6주)</li>
  <li>런칭: 2022/12/09</li>
  <li>유저 피드백: 2022/12/09 ~ 2022/12/15</li>
  <li>추가 업데이트: 2022/12/09 ~ 진행 중</li>
</ul>


<br>
<br>

## 🏗 서비스 아키텍쳐<br>

![image](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2Fbg3Vqy%2FbtrNjyBAtmG%2Fz58lk6MglF7kHzwkWhkgBK%2Fimg.png)

<br>
<br>

## 📖 아키텍쳐 도입 배경<br>
<details> 
  <summary><strong>Git Action</strong></summary><br>
  <li> CI & CD 구축 당시 구축된 환경에서 팀원들이 개발에만 집중할 수 있게 만드려는 것이 우리의 중점 과제였다.</li>
  <li> 대안으로는 Genkins Travis가 존재했으나 둘다 EC2서버를 두대로 CI & CD 구축해야 한다는 차이점이 존재했다.</li>
  <li> Git Action은 하나의 서버로 CI & CD구축이 가능하여 서버 비용의 문제 감당 시 비용 최소화를 할 수 있다고 <br>생각했다.</li>
  <li> 레퍼런스도 많고 러닝커브가 적으며 원격 저장소로 Git Hub를 사용하는 우리에겐 git action은 난이도도 <br>적용하기도 제일 쉽다고 생각했다.</li>
  <li> 상기 이유들로 비용 최소화 , 최소한의 시간으로 구축된 환경을 만족한다고 생각하여 Git Action으로 자동 <br>배포환경을 구축했다.</li>
</details>
<details> 
  <summary><strong>google, kakao, naver 소셜로그인 </strong></summary><br>
  <li> 로그인을 구현하게 되었을 때 사용자들의 편의성을 고려하는 단계에서 일반 로그인은 편의성을 떨어뜨린다고 <br>판단했다.</li>
  <li> 소셜 로그인으로 인증 , 인가를 보증된 소셜(kakao등)에 맡겨 간편한 로그인 처리 방식으로 편의성을 향상시키고자 하였다.</li>
  <li> 소셜 로그인 중 애플의 경우 (1년간 9~12만원의 비용) 결제금액의 이슈로 카카오 , 네이버 , 구글 3개의 <br>소셜로그인을 선택하게 되었다.</li>
  <li> git hub는 일반 사용자들에겐 접근성이 떨어진다고 판단했고 facebook은 naver, goolge로 대체 가능하다 판단했다.</li>
  <li> 상기 이유들로 3개의 소셜 로그인을 선택하게 되었고 그에따라 편의성을 향상시킬 수 있었다.</li>
</details>
<details> 
  <summary><strong>Redis</strong></summary><br>
  <li> 데이터의 I/O가 잦은 경우 변동성이 적은 데이터일때 매번 DB를 조회하는 것은 트래픽 부하와 성능 저하를 해결할 수 없었다.</li>
  <li> 데이터를 캐싱 처리하는 경우 트래픽을 줄이고 성능을 향상시킬 수 있는데 이때 로컬캐시 , Redis를 고려하게 <br>되었다.</li>
  <li> 로컬캐시(caffeine cache)를 고려하게 되었으나 무중단 배포 환경에서 휘발성 캐시가 사라질 위험이 존재한다고 <br>판단했고 scale-out시 데이터 정합성 문제가 생긴다고 판단했다.</li>
  <li> Redis의 경우 여러 자료구를 지원하여 캐싱 처리, 데이터를 처리하기 편리하다고 생각했고 무중단 배포환경에서 <br>서버의 자원을 사용하기에 데이터가 사라질 위험이 존재하지 않았다.</li>
  <li> Redis는 여러 서버간 데이터 정합성 문제도 해결할 수 있다고 생각했다.</li>
  <li> 상기 이유들로 Redis를 캐싱처리를 위해 사용하기로 결정했다.</li>
</details>
<details> 
  <summary><strong>aws RDS MySql</strong></summary><br>
  <li> DB를 저장하기 위한 RDBMS로는 RDBS와 NOSQL이 존재한다.</li>
  <li> NOSQL은 검색속도가 월등하나 테이블간 연관관계를 설정할 수 없고 데이터의 형태가 정확하게 유지되지 않으며 데이터의 무결성이 지켜지지 않는다.</li>
  <li> RDBMS는 데이터의 무결성이 지켜지며 일정한 스키마로 데이터를 관리할 수 있어 테이블 내 데이터를 각각 관리할 스트레스가 줄어들며 연관관계로 테이블들을 관리할 수 있다.</li>
  <li> 상기 이유들로 RDBMS를 선택했으며 aws의 RDS인 MySql을 사용하기로 결정했다.</li>
</details>
<details> 
  <summary><strong>router53 , Amazon ELB</strong></summary><br>
  <li> Front-End와 통신시 HTTP프로토콜로만 통신하는 것은 보안상의 위험성을 야기한다고 생각한다.</li>
  <li> Back-End 배포시 HTTPS 프로토콜을 사용하여 보안을 높히고자 하였고 이때 aws의 router53, Amazon ELB를 <br>도입하는 것이 EC2를 사용하는 우리가 바로 적용할 수 있는 부분이라고 생각했다.</li>
  <li> 상기 이유들로 HTTP,HTTPS프로토콜을 통신할 수 있는 배포 환경을 구축하는 것에 aws의 router53과 Amazon <br>ELB를 이용하기로 결정했다. </li>
</details>
<details> 
  <summary><strong>SSE</strong></summary><br>
  <li> 실시간 알림을 구현하기 위해선 기존의 HTTP 통신 방식(폴링 , 긴폴링)을 사용하기엔 자원의 낭비가 발생하여 <br>새로운 방식을 도입해야 했다.</li>
  <li> 기존의 HTTP프로토콜을 사용하는 streaming방식의 SSE와 WebSocket을 사용하는 웹소켓 두가지가 존재했으나 <br>우리가 구현하려는 알림은 양방향의 알림이 아니었다.</li>
  <li> 배터리 소모량이 적고 연결이 끊어지면 재연결을 시도하며 pollyfill로 모든 브라우저 지원이 가능하게할 수 있는 SSE가 우리의 알림과 맞는다고 판단했다.</li>
  <li> SSE는 첫 연결 이후 매번 재요청을 하지않고 서버의 응답을 줄 수 있어 비용을 아낄 수 있는 측면과 웹소켓의 차이 , 프로젝트의 방향성을 고려하여 사용하기로 결정했다.</li>
</details>
<br>
<br>

## 💖 주요 기능

<details>

  <summary><strong>📆 투두 리스트 (Drag & Drop, react-calendar)</strong></summary>

  <br/>

  <ul>
    <li>날짜별 투두 작성, 수정, 삭제, 조회</li>
    <li>드래그 앤 드롭</li>

<br>

  <img src="https://user-images.githubusercontent.com/108126419/207796871-982de0b8-120a-4aab-9d39-cd072e31e355.png" width="600">
  <img src="https://user-images.githubusercontent.com/108126419/207797028-0b0a9e7f-0d52-4943-996d-05e3c5580e1d.png" width="600">

<br>

  </ul>

</details>

<details>

  <summary><strong>🖋 피드 작성</strong></summary>

  <br/>

  <ul>

<li>완료된 투두 목록만 투두 선택창에 불러오기</li>
<li>태그 추가하기</li>
<li>최대 4장까지 사진 업로드 가능</li>
<li>피드에 적용될 컬러 선택 가능</li>
  <br/>

<img src="https://user-images.githubusercontent.com/108126419/207797718-613acb38-8165-475b-ba65-bd1fc3f113fe.png" width="600">

  </ul>

</details>

<details>

  <summary><strong> 👀 피드 목록 조회</strong></summary>

  <br/>

  <ul>

  <li>추천 피드</li>
    - 선택한 관심사 태그를 바탕으로 게시글을 보여줌 <br>
    - 선택한 관심사 태그가 없다면 관심사 선택 페이지로 유도 <br>
    - 추천 태그를 선택하거나 커스텀 태그를 만들어 선택할 수 있음
  <li>팔로잉 피드</li>
    - 팔로우한 사람의 게시글과 자신의 게시글을 조회할 수 있음 <br>
  <li>무한 스크롤</li>
    - 게시글을 5개씩 불러옴 <br>
    <br/>

<img src="https://user-images.githubusercontent.com/108126419/207798786-93eadccf-0554-46e4-b7ef-92698f1ee20b.png" width="900">

  </ul>

</details>

<details>

  <summary><strong>👓 피드 단건 조회</strong></summary>

<br/>

<ul>
  <li>게시글 수정, 삭제</li>
  <li>이미지 페이지네이션</li>
  <li>태그를 눌러 검색할 수 있음</li>
  <li>댓글 작성, 수정, 삭제</li>
  <li>리액션</li>
  <br />

<img src="https://user-images.githubusercontent.com/108126419/207799750-efa10128-b719-4074-861e-d9322bf222a9.png" width="900">

</ul>

</details>

<details>

  <summary><strong>🔍 검색</strong></summary>

  <br/>

  <ul>

<li>태그 검색/ 유저찾기 검색 구분하여 검색가능</li>
<li>항목 조회시 태그는 5개, 유저 찾기는 10개 단위로 무한스크롤 조회</li>
    <br/>    

<img src="https://user-images.githubusercontent.com/108126419/207800208-560486ec-bdcb-4f82-b5e4-bf3a414a70bf.png" width="600">
    <br>

  </ul>

</details>

<details>

  <summary><strong>🥇 뱃지</strong></summary>

  <br/>

  <ul>

  <li>대표 뱃지 설정</li>
    <br/>

<img src="https://user-images.githubusercontent.com/108126419/207800757-56bb0e27-9d46-4595-b86f-e21b351de6a6.png" width="600">

  </ul>

</details>

<details>

  <summary><strong>🙋 프로필</strong></summary>

  <br/>

  <ul>

  <li>내가 획득한 뱃지 보기 (swiper)</li>
  <li>회원 정보 수정(닉네임/프로필 사진/비밀번호)</li>
  <li>관심사 설정</li>
  <li>팔로우/언팔로우 기능</li>
  <li>로그아웃</li>
  <li>내가 쌓은 블럭(내가 작성한 피드 모아보기)</li>
    <br/>

<img src="https://user-images.githubusercontent.com/108126419/207801404-eb4a2ff9-acdb-4518-8cf6-39d4469c804d.png" width="600">
<img src="https://user-images.githubusercontent.com/108126419/207801442-fffdf321-17d8-4366-a464-c1cf1f00d93b.png" width="600">

  </ul>

</details>

<br>
<br>

## 👨‍👩‍👧‍👦 TEAM 소개

|                                         [이민규](https://github.com/Ming-gry) 리더                                                                                |                                                                               [박영성](https://github.com/youngsungpark)                                                                            |                                                       [심민기](https://github.com/shiminki)                                                   |                                                 [오성은](https://github.com/ose1012) 부리더                                              |                                                                                 [김민영](https://github.com/NyeongDev)                                                                               |                                                                                 [가연우](https://github.com/Yeonwoo-Ga)                                                                               |
|:----------------------------------------------------------------------------------------------------------------------------------------------------------------:|:---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------:|:---------------------------------------------------------------------------------------------------------------------------------------------:|:---------------------------------------------------------------------------------------------------------------------------------------:|:----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------:|:----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------:|
|                                <img src="https://img.shields.io/badge/Back end-fcfd82?style=for-the-badge&logo=&logoColor=white">                                |                                                 <img src="https://img.shields.io/badge/Back end-fcfd82?style=for-the-badge&logo=&logoColor=white">                                                  |                      <img src="https://img.shields.io/badge/Back end-fcfd82?style=for-the-badge&logo=&logoColor=white">                       |                   <img src="https://img.shields.io/badge/front end-fcfd82?style=for-the-badge&logo=&logoColor=white">                   |                                                 <img src="https://img.shields.io/badge/front end-fcfd82?style=for-the-badge&logo=&logoColor=white">                                                  |                                                 <img src="https://img.shields.io/badge/front end-fcfd82?style=for-the-badge&logo=&logoColor=white">                                                  |
| ![](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FDO9Ma%2FbtrNhOrVyfo%2F0tAlwnBSxOvKYDMD682Zik%2Fimg.png) | ![KakaoTalk_Photo_2022-03-30-14-34-07](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FzR6lR%2FbtrNjzHoynR%2FI4iKHEHRzPhXzKSm8xWxL0%2Fimg.png) | ![KakaoTalk_Photo_2022-03-30-14-41-33](https://user-images.githubusercontent.com/79740505/161509182-6a56457f-b0e6-45f0-b40e-d95cbf48619c.png) | ![KakaoTalk_Photo_2022-03-30-14-41-33](https://perday-onespoon.s3.ap-northeast-2.amazonaws.com/KakaoTalk_Photo_2022-09-29-22-08-14.png) | ![KakaoTalk_Photo_2022-03-30-14-41-33](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2Fcb1y70%2FbtrNjz1HUuc%2FeMbRbc12c8KQWzWLGTWKsK%2Fimg.png) | ![KakaoTalk_Photo_2022-03-30-14-41-33](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2Fcb1y70%2FbtrNjz1HUuc%2FeMbRbc12c8KQWzWLGTWKsK%2Fimg.png) |

<br>

## 📚 기술 스택

### 💻 백엔드

<br>

 <p align="center">
 <img src="https://img.shields.io/badge/java-007396?style=for-the-badge&logo=java&logoColor=white"> 
 <img src="https://img.shields.io/badge/Spring-6DB33F?style=for-the-badge&logo=Spring&logoColor=white">
 <img src="https://img.shields.io/badge/-Springboot-6DB33F?style=for-the-badge&logo=Springboot&logoColor=white">
 <img src="https://img.shields.io/badge/Spring Data JPA-6DB33F?style=for-the-badge&logo=S&logoColor=white">
 <img src="https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=MySQL&logoColor=white">
 <img src="https://img.shields.io/badge/NGINX-009639?style=for-the-badge&logo=NGINX&logoColor=white">
 <img src="https://img.shields.io/badge/GitHub Actions-2088FF?style=for-the-badge&logo=GitHub Actions&logoColor=white">
 <img src="https://img.shields.io/badge/QueryDsl-2088FF?style=for-the-badge&logo=&logoColor=white">
 <img src="https://img.shields.io/badge/Redis-DC382D?style=for-the-badge&logo=Redis&logoColor=white">
 <img src="https://img.shields.io/badge/Amazon S3-569A31?style=for-the-badge&logo=Amazon S3&logoColor=white">
 <img src="https://img.shields.io/badge/Amazon EC2-FF9900?style=for-the-badge&logo=Amazon EC2&logoColor=white">
 <img src="https://img.shields.io/badge/Amazon RDS-527FFF?style=for-the-badge&logo=Amazon RDS&logoColor=white">
 <img src="https://img.shields.io/badge/Amazon CodeDeploy-8A2BE2?style=for-the-badge&logo=Amazon RDS&logoColor=white">
 <img src="https://img.shields.io/badge/Amazon Route 53-00498c?style=for-the-badge&logo=Amazon RDS&logoColor=white">
 <img src="https://img.shields.io/badge/SSL-721412?style=for-the-badge&logo=SSL&logoColor=white">
 <img src="https://img.shields.io/badge/Cerbot-000000?style=for-the-badge&logoColor=white">
 <img src="https://img.shields.io/badge/kakao login-FFCD00?style=for-the-badge&logo=kakao&logoColor=black">   
 <img src="https://img.shields.io/badge/google login-4285F4?style=for-the-badge&logo=google&logoColor=white">  
 </p>

### 💻 프론트엔드

<br>

<p align="center">
  <img src="https://img.shields.io/badge/JavaScript-F7DF1E?style=for-the-badge&logo=JavaScript&logoColor=white">
  <img src="https://img.shields.io/badge/React-61DAFB?style=for-the-badge&logo=React&logoColor=white">
  <img src="https://img.shields.io/badge/React Query-FF4154?style=for-the-badge&logo=React Query&logoColor=white">
  <img src="https://img.shields.io/badge/Recoil-2088FF?style=for-the-badge&logo=&logoColor=white">
  <img src="https://img.shields.io/badge/Axios-5A29E4?style=for-the-badge&logo=Axios&logoColor=white">
  <img src="https://img.shields.io/badge/styled components-DB7093?style=for-the-badge&logo=styled components&logoColor=white">
  <img src="https://img.shields.io/badge/cloudtype-000000?style=for-the-badge&logoColor=white"/>

  <br>

## 🔧 사용 툴

<br>

<p align="center">
  <img src="https://img.shields.io/badge/GitHub-181717?style=for-the-badge&logo=GitHub&logoColor=white"/>
  <img src="https://img.shields.io/badge/Git-F05032?style=for-the-badge&logo=Git&logoColor=white"/>
  <img src="https://img.shields.io/badge/Sourcetree-0052CC?style=for-the-badge&logo=Sourcetree&logoColor=white"/>
  <img src="https://img.shields.io/badge/Slack-4A154B?style=for-the-badge&logo=Slack&logoColor=white"/>
  <img src="https://img.shields.io/badge/Notion-000000?style=for-the-badge&logo=Notion&logoColor=white">
  <img src="https://img.shields.io/badge/Figma-F24E1E?style=for-the-badge&logo=Figma&logoColor=white">
  <img src="https://img.shields.io/badge/IntelliJ IDEA-000000?style=for-the-badge&logo=IntelliJ IDEA&logoColor=white"/>
  <img src="https://img.shields.io/badge/Postman-FF6C37?style=for-the-badge&logo=Postman&logoColor=white"/>




## 🎯 개발 포인트

- **[TransactionalEventListner](https://github.com/PerDayOneSpoon/PerDayOneSpoon-BE/wiki/ERD)<br>**
- **[HandlerExceptionResolver]()<br>**
- **[SSE](https://github.com/PerDayOneSpoon/PerDayOneSpoon-BE/wiki/%EC%9D%B4%EB%AF%B8%EC%A7%80-%EB%A6%AC%EC%82%AC%EC%9D%B4%EC%A7%95)<br>**
- **[SockJS + Stomp](https://github.com/PerDayOneSpoon/PerDayOneSpoon-BE/wiki/Jasypt-%EC%95%94%ED%98%B8%ED%99%94-,-%EB%B3%B5%ED%98%B8%ED%99%94-application-yaml%ED%8C%8C%EC%9D%BC-%EC%A0%81%EC%9A%A9)<br>**
- **[QueryDSL](https://github.com/PerDayOneSpoon/PerDayOneSpoon-BE/wiki/SSE-%EC%9D%B4%EB%B2%A4%ED%8A%B8-%EB%B0%9C%ED%96%89-,-%EA%B5%AC%EB%8F%85-%EC%A0%81%EC%9A%A9-%EB%B0%8F-%ED%94%84%EB%A1%9C%EC%84%B8%EC%8A%A4-%EC%88%9C%EC%84%9C%EC%97%90-%EB%94%B0%EB%A5%B8-%EC%9D%B4%EB%B2%A4%ED%8A%B8-%EB%B0%9C%EC%83%9D-%EC%A0%81%EC%9A%A9(@TransactionalEventListner-,-@Transactional))<br>**
- **[ImgScalr](https://github.com/PerDayOneSpoon/PerDayOneSpoon-BE/wiki/swagger-%EC%A0%81%EC%9A%A9)<br>**
- **[RedisRepository](https://github.com/PerDayOneSpoon/PerDayOneSpoon-BE/wiki/QueryDsl-%EB%8F%99%EC%A0%81-%EC%BF%BC%EB%A6%AC)<br>**

<br>
<br>

## 🚀 트러블슈팅

- **[TransactionalEventListner 에서 Transaction 이 실행되지 않음](https://github.com/PerDayOneSpoon/PerDayOneSpoon-BE/wiki/%EC%86%8C%EC%85%9C-%EB%A1%9C%EA%B7%B8%EC%9D%B8-%EC%A0%81%EC%9A%A9(%EC%9D%B8%EA%B0%80-%EC%BD%94%EB%93%9C-%EB%B3%80%EC%A1%B0))<br>**
- **[소셜 로그인 시 닉네임 중복 문제](https://github.com/PerDayOneSpoon/PerDayOneSpoon-BE/wiki/Linux-%EC%9D%B4%EB%AF%B8%EC%A7%80-%EC%97%85%EB%A1%9C%EB%93%9C-%EC%8B%A4%ED%8C%A8-%ED%98%84%EC%83%81)<br>**
- **[ImageScalr 사용 후 프로젝트 패키지에 사진이 저장됨](https://github.com/PerDayOneSpoon/PerDayOneSpoon-BE/wiki/QueryDsl-Fetchjoin(%EC%B9%B4%ED%85%8C%EC%8B%9C%EC%95%88-%EA%B3%B1(Cartesian-Product),-Multiplebag-%EB%AC%B8%EC%A0%9C)<br>**
- **[무한스크롤 시 페이지 당 피드가 무한으로 늘어남](https://github.com/PerDayOneSpoon/PerDayOneSpoon-BE/wiki/Hikari-pool-time-out)<br>**
- **[추천 피드, 피드 태그 검색 시 검색 결과에 따라 중복된 피드가 생김](https://github.com/PerDayOneSpoon/PerDayOneSpoon-BE/wiki/EC2-%EB%A9%94%EB%AA%A8%EB%A6%AC-%EC%82%AC%EC%9A%A9%EB%9F%89%EC%9C%BC%EB%A1%9C-%EC%9D%B8%ED%95%B4-%EC%84%9C%EB%B2%84-%EB%8B%A4%EC%9A%B4)<br>**