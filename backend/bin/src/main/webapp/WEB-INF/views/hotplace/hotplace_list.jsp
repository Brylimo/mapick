<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="com.enjoytrip.model.dto.HotplaceDTO"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>hotplace 조회</title>
<style>
.hotplace-container {
	max-width: 1100px;
	margin: 4rem auto;
	padding: 2rem;
	background-color: #f8f9fa;
	border-radius: 1rem;
	box-shadow: 0 0.5rem 1rem rgba(0, 0, 0, 0.05);
}
</style>
</head>
<body>
	<%@ include file="../fragments/header.jsp" %>

	<div class="container hotplace-container">
		<h2 class="text-center text-primary mb-4">HotPlace 리스트</h2>

		<c:choose>
			<c:when test="${empty list}">
				<div class="alert alert-warning text-center">HotPlace 정보가
					없습니다.</div>
			</c:when>
			<c:otherwise>
				<div class="table-responsive">
					<table class="table table-hover text-center align-middle">
						<thead class="table-light">
							<tr>
								<th>제목</th>
								<th>주소</th>
								<th>전화번호</th>
								<th>생성일</th>
								<th>삭제</th>
							</tr>
						</thead>
						<tbody>
							<c:forEach var="hotplace" items="${list}">
								<tr>
									<td>${hotplace.name}</td>
									<td>${hotplace.address}</td>
									<td>${empty hotplace.tel ? '준비중입니다' : hotplace.tel}</td>
									<td>${hotplace.createdDate}</td>
									<td>
										<button class="btn btn-sm btn-outline-danger delete-btn" data-id="${hotplace.hpid}">삭제</button>
									</td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</div>
			</c:otherwise>
		</c:choose>
	</div>

	<%@ include file="../fragments/footer.jsp" %>

	<script>
		document.addEventListener("DOMContentLoaded", function () {
			const deleteBtns = document.querySelectorAll(".delete-btn");

			deleteBtns.forEach(btn => {
				btn.addEventListener("click", function () {
					if (confirm('삭제하시겠습니까?')) {
						fetch(`${pageContext.request.contextPath}/hotplace/\${this.dataset.id}`, {
							method: "DELETE"
						})
						.then(response => {
							if (response.ok) {
								// 삭제 성공했으면 리스트 페이지로 이동
								location.href = `${pageContext.request.contextPath}/hotplace/list`;
							} else {
								alert("핫플레이스 삭제에 실패했습니다.");
							}
						})
						.catch(error => {
							alert("핫플레이스 삭제에 실패했습니다.");
						});
					}
				});
			});
		})
	</script>
</body>
</html>