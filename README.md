# Mapick (frontend + backend)

## 프로젝트 정보
### 맵픽(Mapick)
맵픽은 지도 커뮤니티 기반 여행지 정리 및 추천 서비스입니다.

### 프로젝트 작업
- 프로젝트 작업 기간
  - 2025.03.21 ~ 2025.05.28
- 프로젝트 개발 투입 인원
  - 2명 (임채진, 박서진)      


# 🌍 Mapick
> **"나만의 장소를 공유하고, 나만의 여행을 설계하다"**  
> 맵픽은 위치 기반 커뮤니티와 AI 추천을 통해 여행 계획부터 후기 공유까지 한 번에 가능한  
> **커뮤니티 중심 여행 플랫폼**입니다.        


## 🧭 프로젝트 개요

기존 여행 플랫폼은 유명한 장소만 보여주고 끝났습니다.  
하지만 저희는 감성적인 장소, 숨은 명소, 취향 기반의 여행지를 공유하고 싶었습니다.  
Mapick은 **위치 기반 커뮤니티**와 **AI 추천**, 그리고 **일정 및 예산 계획** 기능을 통해  
나만의 여행을 설계할 수 있도록 도와줍니다.  
또한, **지도에서 찾은 장소를 즉시 나의 여행 일정에 반영**할 수 있어  
더 직관적이고 유기적인 여행 준비가 가능합니다.

---

## 📅 개발 기간 및 팀원

- **개발 기간**: 2024.04.23 ~ 2024.05.27
- **팀 구성**: 프론트엔드  / 백엔드 2명

| 이름 | 역할 | 주요 담당 |
|------|------|-----------|
| 임채진 | 팀장 / 백엔드 | jwt, 커뮤니티, vWorld 지도, 공공데이터 연동 |
| 임채진 | 팀장 / 프론트엔드 | 커뮤니티, 지도 디자인, UI |
| 박서진 | 팀원 / 백엔드 | 게시판, kakao 지도, AI 연동 |
| 박서진 | 팀원 / 프론트 | 여행 일정, 예산, UI |

## 아키텍처
![](https://velog.velcdn.com/images/brylimo/post/b8bd6aee-1cbf-4ca5-9814-1729fde1a90e/image.png)

## 🛠️ 사용 기술 스택

### 🔹 Frontend
- `Vue.js`, `Vite`, `Vue Router`, `Bootstrap`, `Axios`
- **지도 API**: `Kakao Map API`, `vWorld API`
- **OAuth 로그인**: `카카오 로그인` 연동

### 🔹 Backend
- `Spring Boot`, `Spring Security`, `Spring AI`, `MyBatis`
- `JWT` 인증 방식
- `OpenAI API` 기반 AI 챗봇
- `Gmail SMTP` (회원가입/비번 재설정 인증)
- **Database**: `MySQL` (Docker)



## 🧠 적용 알고리즘

- **KMP 알고리즘**  
  → 여행지 설명 내에서 사용자 키워드 등장 횟수 분석

- **지연 큐 (Delay Queue)**  
  → 예약 작업 처리, 좋아요 지속 여부 등 비동기 제어



## 📌 핵심 기능

| 기능 구분 | 설명 |
|----------|------|
| 📍 위치 기반 장소 등록 | 지도에 커뮤니티 사용자들이 장소 직접 등록 |
| 🗣️ 커뮤니티 가입 및 승인 | 커뮤니티 신청 → 방장 승인 구조 |
| 💬 후기 공유 | 장소별 후기 및 게시판 시스템 |
| 🧭 AI 여행지 추천 | 사용자 질문 → OpenAI 기반 맞춤 답변 |
| 🗓️ 여행 일정 구성 | 장소 선택 → 일정표로 시간/메모 추가 |
| 💰 예산 계산 기능 | 항목별 지출 관리 + 영수증 업로드 |
| 🔎 태그/카테고리 기반 필터링 | 관심사 기반 장소 탐색 기능 |



## 🔐 인증 및 보안

- `JWT` 기반 사용자 인증 및 인가
- `카카오 OAuth 2.0` 로그인 지원
- 이메일 인증 (`Gmail SMTP`)


## 📁 프로젝트 실행 방법

```bash
# Backend 실행
cd backend
./gradlew build
java -jar build/libs/mapick.jar

# Frontend 실행
cd frontend
npm install
npm run dev
```

## 화면 구성
### 로그인 및 회원가입
[로그인]
![](https://velog.velcdn.com/images/brylimo/post/11842ffa-32ea-479c-9ab5-41984e4a1800/image.png)
- 이메일, 비밀번호 입력 후 로그인 시도
- 카카오 로그인 버튼 클릭 시 소셜 로그인
- 입력값 유효성 검사
 
[회원가입]
![](https://velog.velcdn.com/images/brylimo/post/716d4c4c-50ab-4bd4-ac50-de9926edbb45/image.png)
- 이메일 인증 진행 (코드 발송 후 확인)
- 필수값 체크 및 형식 유효성 검사
 
### 메인화면
![](https://velog.velcdn.com/images/brylimo/post/02bd4a09-90b4-4f84-98fb-cca3cf8aa6e4/image.png)
- 인기 커뮤니티 및 핫플레이스 시각화로 사용자 인사이트 제공

### 관광지 조회
![](https://velog.velcdn.com/images/brylimo/post/62fb6c29-9e86-4a7f-a0c1-3441183f31fc/image.png)
- 선택된 조건에 따라 관광지 목록 필터링
- 지도 위 마커로 결과 표시

### 관광지 상세 결과
![](https://velog.velcdn.com/images/brylimo/post/e5c8b796-9ccc-4b9f-bae9-795cde1b45c0/image.png)
- 마커 클릭 시 상세 팝업 노출

### 커뮤니티 가입 요청 목록
![](https://velog.velcdn.com/images/brylimo/post/45d71d17-5e1f-4bf5-adc6-b80b855156b3/image.png)
- 커뮤니티 운영자가 가입 요청 수락/거절 가능

### 게시판 상세
![](https://velog.velcdn.com/images/brylimo/post/63447799-1de7-4b7c-b104-f2a3a5952d54/image.png)
- 댓글 작성 및 대댓글 작성 가능
- 작성자만 수정/삭제 가능

### 게시판 작성
![](https://velog.velcdn.com/images/brylimo/post/168a15ff-4734-4ce4-a2ed-4ca1c852e0a9/image.png)
- 예약일 입력 시 해당 시간에 자동 게시 가능

### 커뮤니티 리스트
![](https://velog.velcdn.com/images/brylimo/post/6ae66f58-1e73-4754-80e2-329f2372d001/image.png)
- 커뮤니티 목록 검색 및 생성 가능

### 커뮤니티 지도
![](https://velog.velcdn.com/images/brylimo/post/a8bbf8ac-d0e1-4cdb-b2f9-dc154ceb2d65/image.png)
- 커뮤니티 장소를 지도에 시각화
- 장소를 내 여행계획에 추가 가능

### 여행 계획 리스트
![](https://velog.velcdn.com/images/brylimo/post/3e7874a9-ef3e-4b47-b915-2af5bcead634/image.png)
- 사용자별 여행 계획 목록 확인 및 선택 가능

### 여행 계획 상세 - 지도 및 장소 추가
![](https://velog.velcdn.com/images/brylimo/post/20be1e35-089b-4f87-91cb-84d4acb75529/image.png)
- 장소를 검색하고 마커를 눌러 여행계획 리스트에 추가 가능
- 삭제 및 순서 조정 가능

### 여행 타임라인
![](https://velog.velcdn.com/images/brylimo/post/d1b2126e-413d-460d-bbbb-c10d08479214/image.png)
- 장소마다 시간 및 메모 작성 가능
- 일정 순으로 정렬 및 확인

### 여행 경비 계획
![](https://velog.velcdn.com/images/brylimo/post/05a4d2bd-912b-453c-852b-aa2f73bc6068/image.png)
- 여행에 필요한 경비 항목 추가/삭제 가능
- 전체 예산 실시간 합산 표시

### 여행 계획 경로 보기
![](https://velog.velcdn.com/images/brylimo/post/d6984308-08de-449f-812e-f8ef22959894/image.png)
- 여행 계획된 장소들을 순서대로 지도 경로로 연결 표시

### 공지사항 목록
![](https://velog.velcdn.com/images/brylimo/post/5a7ab08b-e6a2-4a82-baba-572d620dbc79/image.png)
- 공지사항 목록 확인, 새로운 글 작성 가능

### 공지사항 상세
![](https://velog.velcdn.com/images/brylimo/post/a9cbfe02-4f49-46b8-b70a-a4212bfaf88d/image.png)
- 관리자만 수정/삭제 가능
- 사용자 공지사항 상세 열람 가능

### 비밀번호 재설정
![](https://velog.velcdn.com/images/brylimo/post/d0d2583e-2f35-4b38-bd54-eee3a151b5c2/image.png)
- 이메일 입력 시 인증코드 발송
- 인증코드 확인 후 새 비밀번호 설정 가능

### 회원정보 수정
![](https://velog.velcdn.com/images/brylimo/post/174e0643-9152-4799-aec4-bc3d9e00f9b6/image.png)
- 사용자 정보 일부 수정 가능 (비밀번호 변경 포함)
- 저장 시 유효성 검사 및 업데이트

### 회원탈퇴
![](https://velog.velcdn.com/images/brylimo/post/5d347002-e458-4c90-a6af-3549ac8098be/image.png)
- 비밀번호 입력 후 본인 확인을 거쳐 탈퇴 처리
- 탈퇴 시 사용자 데이터 삭제 또는 비활성화 처리
