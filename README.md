# 🎬 K-SPOT

> **한국 콘텐츠 촬영지 탐방 서비스**  
> 체계적인 아키텍처 설계와 현대적 개발 워크플로우를 통해 구축된 프로덕션 레디 웹 애플리케이션

## 아키텍처 구조
<img width="573" height="182" alt="수정 drawio (1)" src="https://github.com/user-attachments/assets/e1036555-3625-43bd-8047-4c3d6a0d7d6b" />

- 해당 Repo의 develop 브랜치에 수정사항 발생시, Github Action을 통해 AWS EC2로 배포
- main 브랜치는 주차별 기록용도

## 기능구현 목록
- server swagger 참고 (https://k-spot.kro.kr/swagger-ui/index.html#/)

## 기초 데이터 및 DB구조
<img width="736" height="1234" alt="image" src="https://github.com/user-attachments/assets/2843c39f-624b-4244-8d99-b6fb84c89220" />

[TMDB](https://www.themoviedb.org/) : 컨텐츠 관련 데이터 API로 제공
- Artist
- Contents

[공공데이터포털](https://www.data.go.kr/data/15111405/fileData.do) : 한국문화정보원_미디어콘텐츠 영상 촬영지 데이터
- Locations

## 테이블 별 설계 목적
- location_review : 해당 장소에 대한 사용자들의 리뷰를 달 수 있도록
- itineraries : 사용자가 원하는 동선 정보를 저장할 수 있도록
- email_verification_tokens : 회원가입시 이메일 인증에 사용할 토큰
- title_alias : Contents의 별명이나 줄임말을 저장해 검색하기 편하게 만들기 위해
- content_location : 장소-컨텐츠 연관관계 맵핑 목적으로 생성. 해당 contents에서 어떤 장면에서 나왔는지 설명도 첨부
- location_images : 장소에 대한 간략한 사진을 통해 찾아 갔을때 '여기네!' 할 수 있도록
- itinerary_location : 여행 동선 안에서 순서를 저장하기위해

## 도메인 및 패키지 구성
<pre><code>
com.example.kspot
├── aiitineraries      # AI 여행 일정 관리
├── artists            # 아티스트(배우, 가수 등)
├── auth.jwt           # JWT 인증 및 인가
├── config             # 보안, Swagger, CORS 등 환경 설정
├── contents           # 영화·드라마,KPOP 콘텐츠 관리
├── email              # 이메일 인증 및 발송
├── exception          # 전역 예외 처리
├── external.tmdb      # 외부 API 연동 (TMDB, Kakao 등)
├── global.dto         # 공용 DTO
├── itineraries        # 일반 여행 일정 관리
├── locationReview     # 장소 리뷰 기능
├── locations          # 촬영지 정보 관리
└── users              # 사용자 관리
</code></pre>


## Commit Message 규칙
**Type: 간단한 메세지**
- feat: Add kakao-login api
- docs: README에 commit message 규칙 추가

[사용되는 type 종류](https://velog.io/@jiheon/Git-Commit-message-%EA%B7%9C%EC%B9%99)
- feat : 새로운 기능 추가, 기존의 기능을 요구 사항에 맞추어 수정
- fix : 기능에 대한 버그 수정
- build : 빌드 관련 수정
- chore : 패키지 매니저 수정, 그 외 기타 수정 ex) .gitignore
- ci : CI 관련 설정 수정
- docs : 문서(주석) 수정
- style : 코드 스타일, 포맷팅에 대한 수정
- refactor : 기능의 변화가 아닌 코드 리팩터링 ex) 변수 이름 변경
- test : 테스트 코드 추가/수정
- release : 버전 릴리즈
