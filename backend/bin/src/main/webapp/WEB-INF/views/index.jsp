<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Enjoy Trip</title>
<!-- Bootstrap -->
<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
	rel="stylesheet">
<script
	src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

<style>
body {
	background-color: #ffffff;
	font-family: 'Segoe UI', sans-serif;
}

.main-card {
	max-width: 600px;
	margin: 4rem auto;
	padding: 2.5rem;
	background-color: #f8f9fa;
	border-radius: 1rem;
	box-shadow: 0 0.5rem 1rem rgba(0, 0, 0, 0.05);
	text-align: center;
}
</style>
</head>
<body>
	<!-- header -->
	<%@ include file="fragments/header.jsp" %>
	<!-- main contents -->
	<div class="container">
		<div class="main-card">
			<c:choose>
				<c:when test="${not empty sessionScope.userName}">
					<h4 class="mb-4">
						환영합니다, <strong>${sessionScope.userName}</strong>님!
					</h4>
					<a href="${root}/member/logout"
						class="btn btn-outline-danger">로그아웃</a>
				</c:when>
				<c:otherwise>
					<p class="text-muted">로그인 후 열람 가능합니다.</p>
				</c:otherwise>
			</c:choose>
		</div>
	</div>
	<!-- footer -->
	<%@ include file="fragments/footer.jsp" %>
</body>
</html>