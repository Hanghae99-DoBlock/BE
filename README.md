# <img src="https://user-images.githubusercontent.com/108126419/207803210-3720fca9-3c05-43fe-a662-bd619bf1ce14.jpg" width="40">&nbsp;[Do!Block] 나를 쌓는 아주 작은 습관

## Do!Block 소개 | About Us

>###  할 일을 기록하고 공유하는 피드형 SNS! <br>

<img src="https://user-images.githubusercontent.com/108126419/207803730-a2a90832-b6d3-4948-8895-7385382cb9ec.png" width="800">

> - Do!Block은 사용자의 입장에서 "내가 한 일, 해야할 일을 TodoList로 관리하면서 동시에 공유할 수 있는 서비스가 없을까?" 하는 질문에서 시작한 프로젝트입니다.<br>
> - 매일 할 일을 Todo로 작성하여 기록하고, 완료한 Todo 중에 자랑/공유하고 싶은 Todo를 블록(피드)의 형태로 쌓아가며 사용자들과 소통하는 피드형 SNS입니다.<br>
> - 기존의 TodoList와 SNS 서비스에서 각각 분리되어있던 기능이 Do!Block에서는 Todo를 완료함과 동시에 블럭을 쌓아갈 수 있어 색다른 재미를 느낄 수 있습니다. 
<br>

- **[Do!Block 바로가기](https://www.doblock.shop/)<br>**
- **[발표 자료](https://www.miricanvas.com/v/11mzz4j)<br>**
- **[시연 영상](https://youtu.be/ndHV3PM1TOA)<br>**

  <br>
  <br>

## 🔭목차 | Contents
1. [📆 프로젝트기간 | Project Period <br>](#------------project-period--br-)
2. [🏗 서비스 아키텍쳐 | Service Architecture<br>](#--------------service-architecture-br-)
3. [📖 아키텍쳐 도입 배경 | Architecture Introduction Background<br>](#----------------architecture-introduction-background-br-)
4. [🎯 기술적 의사결정 | Technical Decision Making](#--------------technical-decision-making)
5. [💎 주요 기능 | Main Function](#-----------main-function)
6. [👨‍👩‍👧‍👦 팀 소개 | Team Introduction](#-------------------team-introduction)
7. [📚 기술 스택 | Technology Stack](#-----------technology-stack)
8. [🔧 사용 툴 | Tool Used](#----------tool-used)
9. [📜 API](#---api)
10. [🔐 ERD](#---erd)
11. [🚀 트러블 슈팅| Trouble Shooting](#-----------trouble-shooting)

<br>
<br>

## 📆 프로젝트기간 | Project Period <br>

<ul>
  <li>개발 기간: 2022/11/04 ~ 2022/12/15(6주)</li>
  <li>런칭: 2022/12/09</li>
  <li>유저 피드백: 2022/12/09 ~ 2022/12/15</li>
  <li>추가 업데이트: 2022/12/09 ~ 진행 중</li>
</ul>


<br>
<br>

## 🏗 서비스 아키텍쳐 | Service Architecture<br>

![image](https://user-images.githubusercontent.com/108126419/207886272-d38c3b62-2427-4e38-9813-d57b04ded842.png)

<br>
<br>

## 📖 아키텍쳐 도입 배경 | Architecture Introduction Background<br>
<details> 
  <summary><strong>Github Action</strong></summary><br>
  <li> 배포는 반복작업이기 때문에 CI/CD 구축 당시 구축된 환경에서 팀원들이 개발에만 집중할 수 있게 만드려는 것이 우리의 중점 과제이다.</li>
  <li> 선택지로는 Genkins Travis, Github Action이 있다. 그런데 Genkins와 Travis는 EC2서버 2대로 CI & CD를 구축해야한다.</li>
  <li> Git Action은 하나의 EC2 서버로 CI/CD구축이 가능하여 비용면에서 경제적이다.</li>
  <li> 어플리케이션의 규모가 커질수록 빌드, 배포 시간이 길어지는데 Github를 사용하여 프로젝트를 진행하는 과정에서 Github Action으로 자동화시키는 방법은 구축하기 쉬우면서도 추후에 시간낭비를 막을 수 있다고 판단했다..</li>
  <li> 비용, 시간, 난이도를 고려하여 현재 프로젝트에 가장 적합하다고 생각해 Github Action을 사용하여 배포환경을 구축했다.</li>
</details>
<details> 
  <summary><strong>소셜로그인</strong></summary><br>
  <li> 사용자의 관점에서 소셜 로그인은 번거로운 회원 가입 절차 없이 서비스를 이용할 수 있는 방법이다.</li>
  <li> 개발자의 입장에서 소셜 로그인은 사용자 확인을 간소화함과 동시에 보증된 소셜서비스에 인증/인가를 맡겨 사용자의 데이터에 안정적으로 접근할 수 있는 방법이다.</li>
  <li> 소셜 로그인 중 가장 접근성이 좋다고 판단하는 3사인 카카오, 네이버, 구글을 선택하여 진행하였다.</li>
  <li> Apple은 비용(1년간 약 12만원)이 발생하고, Github는 일반 사용자의 접근성이 떨어진다는 판단을 하였다.</li>
  <li> 결론적으로 사용자가 일반 로그인뿐만 아니라 카카오, 네이버, 구글로 소셜로그인을 할 수 있도록 유도하여 서비스에 접근성을 편리하게 하였다.</li>
</details>
<details> 
  <summary><strong>Nginx</strong></summary><br>
  <li> DB를 저장하기 위한 RDBMS로는 RDBS와 NOSQL이 존재한다.</li>
  <li> NOSQL은 검색속도가 월등하나 테이블간 연관관계를 설정할 수 없고 데이터의 형태가 정확하게 유지되지 않으며 데이터의 무결성이 지켜지지 않는다.</li>
  <li> RDBMS는 데이터의 무결성이 지켜지며 일정한 스키마로 데이터를 관리할 수 있어 테이블 내 데이터를 각각 관리할 스트레스가 줄어들며 연관관계로 테이블들을 관리할 수 있다.</li>
  <li> 상기 이유들로 RDBMS를 선택했으며 aws의 RDS인 MySql을 사용하기로 결정했다.</li>
</details>
<details> 
  <summary><strong>AWS RDS MySQL</strong></summary><br>
  <li> DB를 저장하기 위한 RDBMS로는 RDBS와 NOSQL이 존재한다.</li>
  <li> NOSQL은 검색속도가 월등하나 테이블간 연관관계를 설정할 수 없고 데이터의 형태가 정확하게 유지되지 않으며 데이터의 무결성이 지켜지지 않는다.</li>
  <li> RDBMS는 데이터의 무결성이 지켜지며 일정한 스키마로 데이터를 관리할 수 있어 테이블 내 데이터를 각각 관리할 스트레스가 줄어들며 연관관계로 테이블들을 관리할 수 있다.</li>
  <li> 상기 이유들로 RDBMS를 선택했으며 aws의 RDS인 MySql을 사용하기로 결정했다.</li>
</details>

<br>
<br>

## 🎯 기술적 의사결정 | Technical Decision Making

- **[TransactionalEventListner](https://github.com/Hanghae99-DoBlock/BE/wiki/TransactionalEventListner)<br>**
- **[HandlerExceptionResolver](https://github.com/Hanghae99-DoBlock/BE/wiki/HandlerExceptionResolver)<br>**
- **[SSE](https://github.com/Hanghae99-DoBlock/BE/wiki/SSE)<br>**
- **[SockJS + Stomp](https://github.com/Hanghae99-DoBlock/BE/wiki/SockJS---Stomp)<br>**
- **[QueryDSL](https://github.com/Hanghae99-DoBlock/BE/wiki/QueryDSL)<br>**
- **[ImgScalr](https://github.com/Hanghae99-DoBlock/BE/wiki/ImgScalr)<br>**
- **[RedisRepository](https://github.com/Hanghae99-DoBlock/BE/wiki/RedisRepository)<br>**

<br>
<br>

## 💎 주요 기능 | Main Function

<details>

  <summary><strong>📆 투두 리스트 (Drag & Drop, react-calendar)</strong></summary>

  <br/>

  <ul>
    <li>날짜별 투두 작성, 수정, 삭제, 조회</li>
    <li>드래그 앤 드롭</li>

<br>

  <img src="https://user-images.githubusercontent.com/108126419/207830787-d9e4711e-5cd4-4a8b-b526-0db0decb52bd.png" width="300">
  <img src="https://user-images.githubusercontent.com/108126419/207831025-333c96f3-f90c-4aac-b465-3677ac9e4711.png" width="302">
<br>
<br>
  <img src="https://user-images.githubusercontent.com/108126419/207831237-fd0b7099-2a1c-4db4-854b-aa80c8866350.png" width="300">
  <img src="https://user-images.githubusercontent.com/108126419/207832697-66c401a0-b5a7-4bbc-a104-05db3e2b4a47.png" width="301.5">

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

  <img src="https://user-images.githubusercontent.com/108126419/207832276-2dfc3ba6-a396-48b8-a891-e1f795cb222e.png" width="301">
  <img src="https://user-images.githubusercontent.com/108126419/207832354-ed0c11ed-7ebe-4aac-ae90-ad09d6d59d3c.png" width="300">

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

<img src="https://user-images.githubusercontent.com/108126419/207832626-c19d5b99-fda0-4842-8d10-f417acf9fdd5.png" width="300">
<img src="https://user-images.githubusercontent.com/108126419/207833056-bb358093-6891-4ef0-a14f-7bedfa93596b.png" width="300">
<br>
<br>
<img src="https://user-images.githubusercontent.com/108126419/207833122-9c678013-113e-4d32-a14d-7b3382f6e9f1.png" width="300">
<img src="https://user-images.githubusercontent.com/108126419/207835078-a7f1d80c-4d19-4911-a55f-d535237a7324.PNG" width="300.5">

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

<img src="https://user-images.githubusercontent.com/108126419/207833299-2b9b3b94-1961-4ab0-b6e1-309520093e2d.png" width="300">
<img src="https://user-images.githubusercontent.com/108126419/207833360-ec6fb20b-b646-486f-b98b-734fd25db5a1.png" width="300">
<br>
<br>
<img src="https://user-images.githubusercontent.com/108126419/207833419-6c9c8b26-683f-4ef1-bf30-ad4149c16003.png" width="300">
<img src="https://user-images.githubusercontent.com/108126419/207834561-77cdb0dd-3f3a-4f7c-a504-aa8d3d971e3d.PNG" width="301">

</ul>

</details>

<details>

  <summary><strong>🔍 검색</strong></summary>

  <br/>

  <ul>

<li>태그 검색/ 유저찾기 검색 구분하여 검색가능</li>
<li>항목 조회시 태그는 5개, 유저 찾기는 10개 단위로 무한스크롤 조회</li>
  <br/>    

<img src="https://user-images.githubusercontent.com/108126419/207835347-7e9e041b-111b-46fd-be8e-9a6083654bb4.png" width="300">
<img src="https://user-images.githubusercontent.com/108126419/207835462-e49fd69f-8154-413d-ae97-1a46653faddb.png" width="300">
  <br>

  </ul>

</details>

<details>

  <summary><strong>🥇 뱃지</strong></summary>

  <br/>

  <ul>

  <li>대표 뱃지 설정</li>
    <br/>

<img src="https://user-images.githubusercontent.com/108126419/207835720-bfaf48d6-fdc3-4269-b263-00a5c30e50b4.png" width="300">
<img src="https://user-images.githubusercontent.com/108126419/207835773-e8b82b84-5991-4627-adf0-5e5c7dd2fffe.png" width="300.5">

  </ul>

</details>

<details>

  <summary><strong>🙋 프로필</strong></summary>

  <br/>

  <ul>

  <li>내가 획득한 뱃지 보기(swiper)</li>
  <li>회원 정보 수정(닉네임/프로필 사진/비밀번호)</li>
  <li>관심사 설정</li>
  <li>팔로우/언팔로우 기능</li>
  <li>로그아웃</li>
  <li>내가 쌓은 블럭(내가 작성한 피드 모아보기)</li>
  <br/>

<img src="https://user-images.githubusercontent.com/108126419/207835912-86a3aaef-62ed-466c-a650-089ba04f3450.png" width="300">
<img src="https://user-images.githubusercontent.com/108126419/207835973-e66a78df-6992-4cd2-8c3a-b1be80eec52c.png" width="300">
<br>
<br>
<img src="https://user-images.githubusercontent.com/108126419/207836062-78e849ae-2a80-4775-a78d-08089bea2bf0.png" width="301">
<img src="https://user-images.githubusercontent.com/108126419/207836115-5052586b-69f7-4b95-87cd-30b612aabf2b.png" width="300">
<br>
<br>
<img src="https://user-images.githubusercontent.com/108126419/207836729-2da0100d-aad4-4e98-9847-412017d1bc6f.PNG" width="303">
<img src="https://user-images.githubusercontent.com/108126419/207836168-061dfd5f-7bc4-4c13-bc3b-dba1e18c9541.png" width="300">

  </ul>

</details>

<br>
<br>

## 👨‍👩‍👧‍👦 팀 소개 | Team Introduction

<table>
   <tr>
    <td align="center"><b><a href="https://github.com/Ming-gry">이민규</a> 리더</b></td>
    <td align="center"><b><a href="https://github.com/youngsungpark">박영성</a></b></td>
    <td align="center"><b><a href="https://github.com/shiminki">심민기</a></b></td>
    <td align="center"><b><a href="https://github.com/ose1012">오성은</a> 부리더</b></td>
    <td align="center"><b><a href="https://github.com/NyeongDev">김민영</a></b></td>
    <td align="center"><b><a href="https://github.com/Yeonwoo-Ga">가연우</a></b></td>
  </tr>
  <tr>
     <td align="center"><a href="https://github.com/Ming-gry"><img src="https://avatars.githubusercontent.com/u/113870305?v=4" width="100px" /></a></td>
     <td align="center"><a href="https://github.com/youngsungpark"><img src="https://avatars.githubusercontent.com/u/108126419?v=4" width="100px" /></a></td>
     <td align="center"><a href="https://github.com/shiminki"><img src="https://user-images.githubusercontent.com/79740505/161509182-6a56457f-b0e6-45f0-b40e-d95cbf48619c.png" width="100px" /></a></td>
     <td align="center"><a href="https://github.com/ose1012"><img src="https://avatars.githubusercontent.com/u/67879917?v=4" width="100px" /></a></td>
    <td align="center"><a href="https://github.com/NyeongDev"><img src="https://avatars.githubusercontent.com/u/110284486?v=4" width="100px" /></a></td>
    <td align="center"><a href="https://github.com/Yeonwoo-Ga"><img src="https://avatars.githubusercontent.com/u/100272045?v=4" width="100px" /></a></td>

  </tr>
  <tr>
     <td align="center"><b><img src="https://img.shields.io/badge/Back end-fcfd82?style=for-the-badge&logo=&logoColor=white"></b></td>
    <td align="center"><b><img src="https://img.shields.io/badge/Back end-fcfd82?style=for-the-badge&logo=&logoColor=white"></b></td>
    <td align="center"><b><img src="https://img.shields.io/badge/Back end-fcfd82?style=for-the-badge&logo=&logoColor=white"></b></td>
    <td align="center"><b><img src="https://img.shields.io/badge/front end-fcfd82?style=for-the-badge&logo=&logoColor=white"></b></td>
    <td align="center"><b><img src="https://img.shields.io/badge/front end-fcfd82?style=for-the-badge&logo=&logoColor=white"></b></td>
    <td align="center"><b><img src="https://img.shields.io/badge/front end-fcfd82?style=for-the-badge&logo=&logoColor=white"></b></td>
  </tr>
</table>

<br>
<br>

## 📚 기술 스택 | Technology Stack

### 💻 백엔드 | BackEnd

<br>

 <p align="center">
 <img src="https://img.shields.io/badge/java-007396?style=for-the-badge&logo=java&logoColor=white"> 
 <img src="https://img.shields.io/badge/Spring-6DB33F?style=for-the-badge&logo=Spring&logoColor=white">
 <img src="https://img.shields.io/badge/-Springboot-6DB33F?style=for-the-badge&logo=Springboot&logoColor=white">
 <img src="https://img.shields.io/badge/Spring Data JPA-6DB33F?style=for-the-badge&logoColor=white">
 <img src="https://img.shields.io/badge/Spring Security-6DB33F?style=for-the-badge&logo=Spring Security&logoColor=white">
 <img src="https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=MySQL&logoColor=white">
 <img src="https://img.shields.io/badge/NGINX-009639?style=for-the-badge&logo=NGINX&logoColor=white">
 <img src="https://img.shields.io/badge/GitHub Actions-2088FF?style=for-the-badge&logo=GitHub Actions&logoColor=white">
 <img src="https://img.shields.io/badge/QueryDsl-2088FF?style=for-the-badge&logo=&logoColor=white">
 <img src="https://img.shields.io/badge/Redis-DC382D?style=for-the-badge&logo=Redis&logoColor=white">
 <img src="https://img.shields.io/badge/sse-000000?style=for-the-badge&logoColor=white">
 <img src="https://img.shields.io/badge/Sock.js-000000?style=for-the-badge&logo=Sock.js&logoColor=white"/>
 <img src="https://img.shields.io/badge/Stomp-000000?style=for-the-badge&logo=Stomp&logoColor=white"/>
 <img src="https://img.shields.io/badge/Amazon S3-569A31?style=for-the-badge&logo=Amazon S3&logoColor=white">
 <img src="https://img.shields.io/badge/Amazon EC2-FF9900?style=for-the-badge&logo=Amazon EC2&logoColor=white">
 <img src="https://img.shields.io/badge/Amazon RDS-527FFF?style=for-the-badge&logo=Amazon RDS&logoColor=white">
 <img src="https://img.shields.io/badge/Amazon CodeDeploy-8A2BE2?style=for-the-badge&logoColor=white">
 <img src="https://img.shields.io/badge/Gabia-00498c?style=for-the-badge&&logoColor=white">
 <img src="https://img.shields.io/badge/SSL-721412?style=for-the-badge&logo=SSL&logoColor=white">
 <img src="https://img.shields.io/badge/Certbot-D30000?style=for-the-badge&logoColor=white">
 <img src="https://img.shields.io/badge/kakao login-FFCD00?style=for-the-badge&logo=kakao&logoColor=black">   
 <img src="https://img.shields.io/badge/google login-4285F4?style=for-the-badge&logo=google&logoColor=white">
 <img src="https://img.shields.io/badge/naver login-03C75A?style=for-the-badge&logo=naver&logoColor=white">

 </p>

### 💻 프론트엔드 | FrontEnd

<br>

<p align="center">
  <img src="https://img.shields.io/badge/HTML5-E34F26?style=for-the-badge&logo=HTML5&logoColor=white">
  <img src="https://img.shields.io/badge/CSS3-1572B6?style=for-the-badge&logo=CSS3&logoColor=white">
  <img src="https://img.shields.io/badge/JavaScript-F7DF1E?style=for-the-badge&logo=JavaScript&logoColor=white">
  <img src="https://img.shields.io/badge/React-61DAFB?style=for-the-badge&logo=React&logoColor=white">
  <img src="https://img.shields.io/badge/Yarn-2C8EBB?style=for-the-badge&logo=Yarn&logoColor=white">
  <img src="https://img.shields.io/badge/Axios-5A29E4?style=for-the-badge&logo=Axios&logoColor=white">
  <img src="https://img.shields.io/badge/styled components-DB7093?style=for-the-badge&logo=styled components&logoColor=white">
  <img src="https://img.shields.io/badge/cloudtype-000000?style=for-the-badge&logoColor=white"/>
  <img src="https://img.shields.io/badge/.ENV-ECD53F?style=for-the-badge&logo=.ENV&logoColor=white">
  <img src="https://img.shields.io/badge/ReduxToolkit-764ABC?style=for-the-badge&logo=ReduxToolkit&logoColor=white">
  <img src="https://img.shields.io/badge/kakao login-FFCD00?style=for-the-badge&logo=kakao&logoColor=black">   
  <img src="https://img.shields.io/badge/google login-4285F4?style=for-the-badge&logo=google&logoColor=white">
  <img src="https://img.shields.io/badge/naver login-03C75A?style=for-the-badge&logo=naver&logoColor=white">

</p>

<br>
<br>

## 🔧 사용 툴 | Tool Used

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

<br>
<br>

## 📜 API



<summary><strong><a href="https://legendary-scaffold-c21.notion.site/a7f31d0b36c344ed9cb4b9d89ce5a18c?v=56734b61f9264de9ab271b251c76eaa0">📃 API 명세서<a/></strong></summary>


<br>
<br>

## 🔐 ERD

<details>

  <summary><strong> ERD</strong></summary>
  <img src="https://user-images.githubusercontent.com/108126419/207908436-40ecc746-c423-49ad-b33f-dfef34737359.png">

</details>

<br>
<br>

## 🚀 트러블 슈팅| Trouble Shooting

- **[TransactionalEventListner 에서 Transaction 이 실행되지 않음](https://github.com/Hanghae99-DoBlock/BE/wiki/TransactionalEventListner-%EC%97%90%EC%84%9C-Transaction-%EC%9D%B4-%EC%8B%A4%ED%96%89%EB%90%98%EC%A7%80-%EC%95%8A%EC%9D%8C)<br>**
- **[소셜 로그인 시 닉네임 중복 문제](https://github.com/Hanghae99-DoBlock/BE/wiki/%EC%86%8C%EC%85%9C-%EB%A1%9C%EA%B7%B8%EC%9D%B8-%EC%8B%9C-%EB%8B%89%EB%84%A4%EC%9E%84-%EC%A4%91%EB%B3%B5-%EB%AC%B8%EC%A0%9C)<br>**
- **[ImageScalr 사용 후 프로젝트 패키지에 사진이 저장됨](https://github.com/Hanghae99-DoBlock/BE/wiki/ImageScalr-%EC%82%AC%EC%9A%A9-%ED%9B%84-%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8-%ED%8C%A8%ED%82%A4%EC%A7%80%EC%97%90-%EC%82%AC%EC%A7%84%EC%9D%B4-%EC%A0%80%EC%9E%A5%EB%90%A8)<br>**
- **[무한스크롤 시 페이지 당 피드가 무한으로 늘어남](https://github.com/Hanghae99-DoBlock/BE/wiki/%EB%AC%B4%ED%95%9C%EC%8A%A4%ED%81%AC%EB%A1%A4-%EC%8B%9C-%ED%8E%98%EC%9D%B4%EC%A7%80-%EB%8B%B9-%ED%94%BC%EB%93%9C%EA%B0%80-%EB%AC%B4%ED%95%9C%EC%9C%BC%EB%A1%9C-%EB%8A%98%EC%96%B4%EB%82%A8)<br>**
- **[추천 피드, 피드 태그 검색 시 검색 결과에 따라 중복된 피드가 생김](https://github.com/Hanghae99-DoBlock/BE/wiki/%EC%B6%94%EC%B2%9C-%ED%94%BC%EB%93%9C,-%ED%94%BC%EB%93%9C-%ED%83%9C%EA%B7%B8-%EA%B2%80%EC%83%89-%EC%8B%9C-%EA%B2%80%EC%83%89-%EA%B2%B0%EA%B3%BC%EC%97%90-%EB%94%B0%EB%9D%BC-%EC%A4%91%EB%B3%B5%EB%90%9C-%ED%94%BC%EB%93%9C%EA%B0%80-%EC%83%9D%EA%B9%80)<br>**
- **[피드 300 개 이상 시 피드 목록 조회 속도가 느림](https://github.com/Hanghae99-DoBlock/BE/wiki/%ED%94%BC%EB%93%9C-300-%EA%B0%9C-%EC%9D%B4%EC%83%81-%EC%8B%9C-%ED%94%BC%EB%93%9C-%EB%AA%A9%EB%A1%9D-%EC%A1%B0%ED%9A%8C-%EC%86%8D%EB%8F%84%EA%B0%80-%EB%8A%90%EB%A6%BC)<br>**