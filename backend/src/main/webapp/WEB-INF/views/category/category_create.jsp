<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>카테고리 생성</title>
<link type="text/css"
	  href="${pageContext.request.contextPath}/css/board_create.css"
	  rel="stylesheet">
</head>
<body>
	<%@ include file="../fragments/header.jsp" %>
	<div class="container">
		<div class="title-box">
			<h3>카테고리 생성</h3>
			<hr class="title-line">
		</div>

		<div class="row justify-content-center">
			<div class="col-lg-6">
				<div class="card shadow-sm">
					<div class="card-body">
						<form action="${root }/category/categoryCreate" method="post">
							<div class="mb-3">
								<label for="categoryName" class="form-label">카테고리 이름</label>
								<input type="text" class="form-control" id="categoryName" name="categoryName" placeholder="카테고리 이름...">
							</div>
							<div class="text-center">
								<button type="submit" class="btn btn-primary">생성</button>
								<button type="reset" class="btn btn-danger">초기화</button>
							</div>
						</form>
					</div>
				</div>
			</div>
		</div>
	</div>

	<%@ include file="../fragments/footer.jsp" %>
</body>
</html>