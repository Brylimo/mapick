<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>비밀번호 찾기</title>
<style>
.auth-container {
	max-width: 500px;
	margin: 4rem auto;
	padding: 2rem;
	background-color: #f8f9fa;
	border-radius: 0.75rem;
	box-shadow: 0 0.5rem 1rem rgba(0, 0, 0, 0.05);
	text-align: center;
}
</style>
</head>
<body>
	<%@ include file="../fragments/header.jsp" %>

	<div class="container">
		<div class="auth-container">
            <h4 class="mb-3">
                <strong>${dto.name}</strong>님의 비밀번호가<br> 성공적으로 변경되었습니다.
            </h4>
            <div class="mt-4">
                <a
                    href="${pageContext.request.contextPath}/member/loginForm"
                    class="btn btn-primary">로그인하기</a>
            </div>
		</div>
	</div>

	<%@ include file="../fragments/footer.jsp" %>
</body>
</html>