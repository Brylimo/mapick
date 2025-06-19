<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>회원 정보 수정</title>
<style>
.auth-container {
	max-width: 500px;
	margin: 4rem auto;
	padding: 2rem;
	background-color: #f8f9fa;
	border-radius: 0.75rem;
	box-shadow: 0 0.5rem 1rem rgba(0, 0, 0, 0.05);
}
</style>
</head>
<body>
	<%@ include file="../fragments/header.jsp" %>

	<div class="container">
		<div class="auth-container">
			<c:choose>
				<c:when test="${empty dto}">
					<h5 class="text-danger text-center">회원 정보가 없습니다.</h5>
				</c:when>
				<c:otherwise>
					<h3 class="text-center mb-4">회원 정보 수정</h3>
					<form action="${root}/member/memberUpdate" method="post">

						<div class="mb-3">
							<label class="form-label">이름</label> <input type="text"
								name="name" class="form-control" placeholder="이름 입력"
								value="${dto.name}" required>
						</div>

						<div class="mb-3">
							<label class="form-label">이메일</label> <input type="email"
								name="email" class="form-control" value="${dto.email}" disabled>
						</div>

						<div class="mb-3">
							<label class="form-label">비밀번호</label> <input type="password"
								name="password" class="form-control" placeholder="비밀번호를 변경하려면 입력하세요"/>
						</div>

						<div class="mb-3">
							<label class="form-label d-block">구분</label>
							<div>
									${dto.role eq 'admin' ? '관리자' : '회원'}
							</div>
						</div>

						<div class="d-grid">
							<button type="submit" class="btn btn-primary">회원 정보 수정</button>
						</div>
					</form>
				</c:otherwise>
			</c:choose>
		</div>
	</div>

	<%@ include file="../fragments/footer.jsp" %>
</body>
</html>