<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>회원 상세 조회</title>
<style>
.detail-container {
	max-width: 700px;
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
		<div class="detail-container">
			<h3 class="text-center mb-4">내 정보</h3>

			<c:choose>
				<c:when test="${empty dto}">
					<p class="text-danger text-center">회원 정보가 없습니다.</p>
				</c:when>
				<c:otherwise>
					<table class="table table-bordered text-center">
						<thead class="table-light">
							<tr>
								<th>이름</th>
								<th>이메일</th>
								<th>역할</th>
							</tr>
						</thead>
						<tbody>
							<tr>
								<td>${dto.name}</td>
								<td>${dto.email}</td>
								<td>${dto.role}</td>
							</tr>
						</tbody>
					</table>
				</c:otherwise>
			</c:choose>
		</div>
	</div>

	<%@ include file="../fragments/footer.jsp" %>
</body>
</html>