# Team20_BE [K-SPOT] 사용법

### 기능개발순서

1. develop 브랜치에서 원하는 기능을 구현하기위한 feature 브랜치를 새로 생성
   (브랜치명 형식 : feature/kakao-login)
2. 개발 진행  
   a. 개발할 기능을 잘게 나눠 TODO_LIST.md에 작성  
   b. 테스트 할 방법을 글로 작성  
   c. 기능 구현  
   d. 테스트 코드 작성 후 테스트 모두 통과시 commit  
3. PR 생성 후 대기
4. Peer Review 후 승인 되면 develop branch에 병합
<img width="528" height="700" alt="image" src="https://github.com/user-attachments/assets/a7637021-50f4-44f7-8845-181253265b06" />


### Commit Message 규칙
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
