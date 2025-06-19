<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>로그인</title>
<style>
  .auth-container {
    max-width: 400px;
    margin: 4rem auto;
    padding: 2rem;
    background-color: #f8f9fa;
    border-radius: 0.75rem;
    box-shadow: 0 0.5rem 1rem rgba(0,0,0,0.05);
  }
</style>
</head>
<body>
	<%@ include file="../fragments/header.jsp" %>

<div class="container">
		<div class="auth-container">
			<h3 class="text-center mb-4">로그인</h3>
			<form action="${root}/members/login" method="post">
				
				<div class="mb-3">
					<label class="form-label">이메일</label>
					<input type="email" name="email" class="form-control" placeholder="이메일 입력"
					       value="${cookie.email.value}" required>
				</div>
				
				<div class="mb-3">
					<label class="form-label">비밀번호</label>
					<input type="password" name="password" class="form-control" placeholder="비밀번호 입력" required>
				</div>

				<div class="form-check mb-3">
					<input class="form-check-input" type="checkbox" name="rememberId" id="rememberId" value="rememberId"
					       ${not empty cookie.email.value ? "checked" : ""}>
					<label class="form-check-label" for="rememberId">아이디 저장</label>
				</div>
				
				<div class="d-grid">
					<button type="submit" class="btn btn-primary">로그인</button>
				</div>
			</form>
		</div>
	</div>
	
	<%@ include file="../fragments/footer.jsp" %>
</body>
</html>