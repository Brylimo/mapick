<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>

<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
	rel="stylesheet" crossorigin="anonymous">
<script
	src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"
	crossorigin="anonymous"></script>
<link type="text/css"
	href="${pageContext.request.contextPath}/css/common2.css"
	rel="stylesheet">
<br>
<!--<h1>
	<a href="${root }/members/"
		class="navbar-brand text-primary fw-bold" style="font-size: 2em;"><img src="/images/mapick.png" /></a>
</h1>-->
<style>
.navbar-nav .nav-link {
	margin-right: 1rem;
}
</style>
<c:if test="${!empty SPRING_SECURITY_CONTEXT }">
	<c:set var="principal" value="${SPRING_SECURITY_CONTEXT.authentication.principal }"></c:set>
</c:if>

<nav class="navbar navbar-expand-lg navbar-light bg-white shadow-sm px-4">
	<div class="container-fluid">
		<a class="navbar-brand text-primary fw-bold"
			href="${root}/members/"
			style="font-size: 1.8rem;"><img class="logo" src="/images/mapick.png" /></a>

		<button class="navbar-toggler" type="button" data-bs-toggle="collapse"
			data-bs-target="#navbarContent">
			<span class="navbar-toggler-icon"></span>
		</button>

		<div class="collapse navbar-collapse justify-content-between"
			id="navbarContent">
			<ul class="navbar-nav me-auto mb-2 mb-lg-0">
				<!-- 로그인한 사용자 메뉴 -->

				<c:if test="${!empty SPRING_SECURITY_CONTEXT}">
					<li class="nav-item"><a class="nav-link"
						href="${root}/communities/communityList">커뮤니티</a></li>
					<li class="nav-item"><a class="nav-link"
						href="${root}/attractions/main">관광지
							조회</a></li>
                    <li class="nav-item"><a class="nav-link"
                        href="${root}/plans/planList">여행 계획 조회</a></li>
					<li class="nav-item"><a class="nav-link"
						href="${root}/hotplaces/list">HotPlace
							조회</a></li>
					<li class="nav-item"><a class="nav-link"
						href="${root}/notices/list">공지사항 조회</a></li>
					<li class="nav-item"><a class="nav-link"
						href="${root}/boards/list">게시판 조회</a></li>
					<c:if test="${principal.member.role eq 'admin'}">
						<li class="nav-item"><a class="nav-link text-danger"
							href="${root}/notices/noticeCreateForm">공지 생성</a></li>
					</c:if>
				</c:if>
			</ul>

			<ul class="navbar-nav mb-2 mb-lg-0">
				<c:choose>
					<c:when test="${!empty SPRING_SECURITY_CONTEXT}">
						<li class="nav-item d-flex align-items-center me-3"><span
							class="text-secondary">안녕하세요, <strong>${principal.member.name}</strong>님
						</span></li>
						<li class="nav-item"><a class="nav-link"
							href="${root}/members/memberDetail">내 정보</a></li>
						<li class="nav-item"><a class="nav-link"
							href="${root}/members/memberUpdateForm">정보 수정</a></li>
						<!-- <li class="nav-item"><a class="nav-link" href="${root}/memberController?action=memberDelete">회원 탈퇴</a></li> -->
						<li class="nav-item d-flex align-items-center">
							<form action="${root}/members/memberDelete" method="post"
								onsubmit="return confirm('회원 탈퇴를 진행하시겠습니까?');" class="d-inline">
								<button type="submit"
									class="nav-link btn btn-link p-0 text-danger"
									style="text-decoration: none;">회원 탈퇴</button>
							</form>
						</li>
						<li class="nav-item d-flex align-items-center"><a
							class="btn btn-sm btn-outline-primary ms-2"
							href="${root}/members/logout">로그아웃</a></li>
					</c:when>
					<c:otherwise>
						<li class="nav-item"><a class="nav-link"
							href="${root}/members/memberJoinForm">회원가입</a></li>
						<li class="nav-item"><a class="nav-link"
							href="${root}/members/loginForm">로그인</a></li>
						<li class="nav-item"><a class="nav-link"
							href="${root}/members/findPwFrom">비밀번호
								찾기</a></li>
					</c:otherwise>
				</c:choose>
			</ul>
		</div>
	</div>
</nav>