<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>비밀번호 변경</title>
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
		    <c:choose>
		        <c:when test="${empty dto}">
                    <h4 class="text-danger">일치하는 계정이 없습니다.</h4>
                </c:when>
                <c:otherwise>
                    <h3 class="text-center mb-4">비밀번호 변경</h3>
                    <form action="${root}/member/memberChangePw" method="post">
                        <input type="hidden" name="email" value="${dto.email}">
                        <div class="mb-3">
                            <label class="form-label">비밀번호</label>
                            <input type="password" name="password" class="form-control" placeholder="비밀번호 입력" required><br>
                            <label class="form-label">비밀번호 확인</label>
                            <input type="password" name="password2" class="form-control" placeholder="비밀번호 재입력" required>
                        </div>

                        <div class="d-grid">
                            <button type="submit" class="btn btn-primary">제출</button>
                        </div>
                    </form>
                </c:otherwise>
            </c:choose>
		</div>
	</div>

	<%@ include file="../fragments/footer.jsp" %>
</body>
</html>