<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>회원 삭제 성공</title>
<style>
  .delete-container {
    max-width: 500px;
    margin: 4rem auto;
    padding: 2rem;
    background-color: #f8f9fa;
    border-radius: 0.75rem;
    box-shadow: 0 0.5rem 1rem rgba(0,0,0,0.05);
    text-align: center;
  }
</style>
</head>
<body>
	<%@ include file="../fragments/header.jsp" %>

	
<div class="container">
	<div class="delete-container">
		<h3 class="text-primary mb-3">회원 탈퇴가 완료되었습니다!</h3>
		<p>그동안 이용해주셔서 감사합니다.</p>

		<a href="${pageContext.request.contextPath}/members/" class="btn btn-primary mt-3">HOME</a>
	</div>
</div>
	
	<%@ include file="../fragments/footer.jsp" %>
</body>
</html>