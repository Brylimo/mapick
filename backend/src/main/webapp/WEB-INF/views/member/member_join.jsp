<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>회원가입</title>
<style>
  .auth-container {
    max-width: 500px;
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
			<h3 class="text-center mb-4">회원가입</h3>
			<form action="${root}/members/memberJoin" method="post">

				<div class="mb-3">
					<label class="form-label">이름</label>
					<input type="text" name="name" class="form-control" placeholder="이름 입력" required>
				</div>
				
				<div class="mb-3">
					<label class="form-label">이메일</label>
					<input type="email" name="email" class="form-control" placeholder="이메일 입력" required>
				</div>
				
				<div class="mb-3">
					<label class="form-label">비밀번호</label>
					<input type="password" name="password" class="form-control" placeholder="비밀번호 입력" required>
				</div>

				<div class="mb-3">
					<label class="form-label d-block">구분</label>
					<div class="form-check form-check-inline">
						<input class="form-check-input" type="radio" name="role" value="admin" id="admin">
						<label class="form-check-label" for="admin">관리자</label>
					</div>
					<div class="form-check form-check-inline">
						<input class="form-check-input" type="radio" name="role" value="member" id="member">
						<label class="form-check-label" for="member">회원</label>
					</div>
				</div>
				
				<div class="d-grid">
					<button type="submit" class="btn btn-primary">회원가입</button>
				</div>
			</form>
		</div>
	</div>
	
	<%@ include file="../fragments/footer.jsp" %>
</body>
</html>