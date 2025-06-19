<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>여행 계획</title>
<style>
.plan-container {
	max-width: 1100px;
	margin: 4rem auto;
	padding: 2rem;
	background-color: #f8f9fa;
	border-radius: 1rem;
	box-shadow: 0 0.5rem 1rem rgba(0, 0, 0, 0.05);
}

.btn-circle {
	width: 36px;
	height: 36px;
	font-size: 20px;
	border-radius: 50%;
	display: flex;
	justify-content: center;
	align-items: center;
	transition: background-color 0.2s ease;
}

.btn-circle:hover {
	background-color: #0056b3; /* 부트스트랩 primary hover 색상 */
}
</style>
</head>
<body>
	<%@ include file="/fragments/header.jsp"%>

	<div class="container plan-container">
		<div class="position-relative mb-4">
			<h2 class="text-primary fw-bold text-center w-100 m-0">여행 계획</h2>
            <a href="${root}/planController?action=planCreate"
               class="btn btn-primary position-absolute end-0 top-50 translate-middle-y d-flex justify-content-center align-items-center"
               style="width: 40px; height: 40px; border-radius: 50%; font-size: 24px; padding: 0;">
               +
            </a>
		</div>

		<c:choose>
			<c:when test="${empty list}">
				<div class="alert alert-warning text-center">여행 계획 정보가 없습니다.</div>
			</c:when>
			<c:otherwise>
				<div class="table-responsive">
					<table class="table table-hover text-center align-middle">
						<thead class="table-light">
							<tr>
								<th>제목</th>
								<th>작성자</th>
								<th>작성일</th>
							</tr>
						</thead>
						<tbody>
							<c:forEach var="plan" items="${list}">
								<tr>
									<td><a href="${root }/planController?action=planDetail&name=${plan.getName()}">${plan.name}</a></td>
									<td>${plan.getMember().getName()}</td>
									<td>${plan.getCreatedDate()}</td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</div>
			</c:otherwise>
		</c:choose>
	</div>

	<%@ include file="/fragments/footer.jsp"%>
</body>
</html>
