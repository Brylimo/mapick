# SSAFY13_광주5_관통_Framework_10팀_임채진_박서진

## 프로젝트 정보
### :sparkles: ENJOY TRIP :sparkles:

### 참여자
10팀 [ 임채진, 박서진 ]

## 현재 진척 상황 (05.09)

- 지역별 관광지 정보 수집 기능 (F01)
- 관광지, 숙박, 음식점 조회 기능 (F02)
- 문화시설, 공연, 여행코스, 쇼핑 조회 기능 (F03)
- 여행 계획 경로 설정 기능 (F04)
- Hotplace 등록 기능 (F05)
- 회원관리 기능 : 회원가입, 수정, 조회, 탈퇴 (F07)
- 로그인 관리 기능 : 로그인, 로그아웃, 비밀번호 찾기 (F08)
- 공지사항 기능 : 공지사항 등록, 수정, 삭제, 조회 (F09)
- 공유게시판 기능 : 게시판 등록, 수정, 삭제, 조회 (F10)
- 스프링 시큐리티 기반 인가/인증 코드 교체 작업
- 지도 변경 (SGIS -> 카카오 맵)
- 지도 커뮤니티 CRUD 작업
- 지도 커뮤니티 태그, 카테고리 기능 개발

## 개인별 진척 상황 (05.09)
- 박서진
  - 지도 커뮤니티 태그, 카테고리 기능 개발
  - 지도 커뮤니티 CRUD 작업

- 임채진
  - 여행 계획 경로 설정 기능 개발
  - 지도 커뮤니티 여행지 등록 기능 개발

## 팀원별 역할 분배 (05.09)
- 박서진
  - 지도 커뮤니티 게시판 기능 개발 
  - 게시판 댓글, 좋아요 기능 개발

- 임채진
  - 지도 커뮤니티 검색 기능 개발(태그별, 여행지별)
  - 지도 분석 기능 개발(히트맵, 클러스터, 핀 등)

## 데이터베이스
![ERD](docs/gif/erd.png)

DB 스키마 파일
경로 : /src/main/webapp/sql/tables.sql

*- attraction 관련 데이터는 api를 통해 데이터를 불러와 프로젝트 내 list로 사용중입니다.*

DB 덤프 파일
경로 : /src/main/webapp/sql/dump
dump 폴더 안에 sql 덤프 파일들을 넣어두었습니다.

---

#### 실행 방법
1. 위의 sql을 이용해서 테이블을 생성한다.
2. DB에 데이터 dump 필요 없이 그냥 프로젝트를 구동한다. (프로젝트가 처음 로드되면서 외부 API를 이용해 필요한 데이터를 불러온다.)

---

## Swagger (REST API)

### [attractionController]
![attractionController](docs/gif/swagger/attractionController.png)

### [boardController]
![boardController](docs/gif/swagger/boardController.png)

### [categoryController]
![categoryController](docs/gif/swagger/categoryController.png)

### [communityAttractionController]
![communityAttractionController](docs/gif/swagger/communityAttractionController.png)

### [communityController]
![communityController](docs/gif/swagger/communityController.png)

### [gugunController]
![gugunController](docs/gif/swagger/gugunController.png)

### [hotplaceController]
![hotplaceController](docs/gif/swagger/hotplaceController.png)

### [memberController]
![memberController](docs/gif/swagger/memberController.png)

### [noticeController]
![noticeController](docs/gif/swagger/noticeController.png)

### [planAttractionController]
![planAttractionController](docs/gif/swagger/planAttractionController.png)

### [planController]
![planController](docs/gif/swagger/planController.png)

### [tagController]
![tagController](docs/gif/swagger/tagController.png)
---

## 기능 안내

## 📌 커뮤니티 생성
![커뮤니티_생성](docs/gif/커뮤니티_생성.png)

## 📌 커뮤니티 등록
![커뮤니티_직접_등록](docs/gif/커뮤니티_직접_등록.png)

![커뮤니티_검색_등록](docs/gif/커뮤니티_검색_등록.png)

## 📌 커뮤니티 검색
![커뮤니티_전체_검색](docs/gif/커뮤니티_전체_검색.png)

## 📌 여행계획
![어행계획_리스트](docs/gif/여행계획_리스트.png)

![어행계획](docs/gif/여행계획_메인.png)

![타임라인](docs/gif/타임라인.png)

## 📌 지역별 관광지 정보 수집 기능

![지역별_관광지_정보_수집](docs/gif/지역별_관광지_정보_수집.gif)

1. **지역을 select box를 이용해 입력**하면 지역에 맞는 관광지가 **지도, 마커와 함께 조회**된다.

## 📌 관광지, 문화시설, 여행코스 등 카테고리에 따른 조회

![관광지](docs/gif/관광지.gif)

![문화시설](docs/gif/문화시설.gif)

![여행코스](docs/gif/여행코스.gif)

![행사](docs/gif/행사.gif)

![레포츠](docs/gif/레포츠.gif)

![숙박](docs/gif/숙박.gif)

![쇼핑](docs/gif/쇼핑.gif)

![음식점](docs/gif/음식점.gif)

1. 관광지, 문화시설, 여행코스, 행사, 레포츠, 숙박, 쇼핑, 음식점으로 **총 8가지 카테고리로 나눠 조회**된다.
2. 마커로 카테고리에 따른 아이콘을 표시한다.

![관광지_클릭시_상세보기](docs/gif/관광지_클릭시_상세보기.gif)

3. 관광지를 클릭하면 **작은 창을 통해 관광지의 사진과 주소, 전화번호를 알 수 있다.**

## 📌 HotPlace 등록

![핫플레이스_추가](docs/gif/핫플레이스_추가.gif)

1. 지도에서 아이콘을 클릭한 뒤 정보 창에서 추가 버튼을 누르면 **핫플레이스 리스트에 해당 장소가 추가**된다.

![핫플레이스_조회](docs/gif/핫플레이스_조회.gif)

2. 핫플레이스 리스트에서 **회원이 추가한 관광지들을 조회**한다.

![핫플레이스_삭제](docs/gif/핫플레이스_삭제.gif)

3. 리스트 내 삭제 버튼을 누르면 **해당 관광지가 삭제**된다.

## 📌 회원관리 (회원가입|수정|조회|탈퇴)

![회원가입](docs/gif/회원가입.gif)

1. 이름, 이메일, 비밀번호와 구분을 등록해 **회원가입을 진행**한다.

![내정보_조회](docs/gif/내정보_조회.gif)

![내정보_수정](docs/gif/내정보_수정.gif)

2. 로그인 중인 회원은 **자신의 정보를 조회**할 수 있고, **이름과 비밀번호를 수정**할 수 있다.

![회원탈퇴](docs/gif/회원탈퇴.gif)

3. 회원 탈퇴 버튼을 누르면 **회원 탈퇴**를 진행한다.

## 📌 로그인 관리 (로그인|로그아웃|비밀번호 찾기)

![로그인_로그아웃](docs/gif/로그인_로그아웃.gif)

1. 회원은 이메일, 비밀번호를 통해 **로그인**을 진행한다. 이때, 쿠키를 이용한 **아이디 저장**을 통해 재로그인 시 편리함을 제공한다. 또한 **세션에 값을 부여함으로써 로그인 상태를 유지**한다.

![비밀번호_찾기](docs/gif/비밀번호_찾기.gif)

2. 회원을 이메일을 작성하여 자신의 **비밀번호를 조회**한다.

## 📌 게시판 (공지사항|공유게시판)

![공지사항_조회](docs/gif/공지사항_조회.gif)

1. 회원은 **공지사항을 조회**한다.

![게시글_등록](docs/gif/게시글_등록.gif)

2. 회원은 **게시글을 등록**한다.

![게시글_수정](docs/gif/게시글_수정.gif)

3. 회원은 **게시글을 수정**한다.

![게시글_삭제](docs/gif/게시글_삭제.gif)

4. 회원은 **게시글을 삭제**한다.

![게시글_검색](docs/gif/게시글_검색.gif)

5. 회원은 **게시글을 제목, 작성자에 따라 검색**할 수 있다.