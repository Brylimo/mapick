<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="com.enjoytrip.model.dto.CategoryDTO" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>카테고리 수정</title>
<link type="text/css"
	  href="${pageContext.request.contextPath}/css/board_create.css"
	  rel="stylesheet">
</head>
<body>
	<%@ include file="../fragments/header.jsp" %>

	<div class="container">
		<div class="title-box">
			<h3>공지사항 수정</h3>
			<hr class="title-line">
		</div>

		<%
			CategoryDTO dto = (CategoryDTO) request.getAttribute("dto");
			if (dto == null) {
		%>
			<p>카테고리 정보가 없습니다.</p>
		<%
		} else {
		%>
		<div class="row justify-content-center">
			<div class="col-lg-6">
				<div class="card shadow-sm">
					<div class="card-body">
						<form action="${root }/category/categoryUpdate" method="post">
							<input type="hidden" name="categoryId" value="${dto.categoryId}">

							<div class="mb-3">
								<label for="categoryName" class="form-label">카테고리 제목</label>
								<input type="text" class="form-control" id="categoryName" name="categoryName" placeholder="카테고리 제목..." value="${dto.categoryName}">
							</div>
							<div class="text-center">
								<button type="submit" class="btn btn-primary">글수정</button>
								<button type="reset" class="btn btn-danger">초기화</button>
							</div>
						</form>
					</div>
				</div>
			</div>
		</div>
		<%
			}
		%>
	</div>

	<%@ include file="../fragments/footer.jsp" %>
</body>
</html>