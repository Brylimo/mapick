<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>공지사항 생성</title>
<link type="text/css"
	  href="${pageContext.request.contextPath}/css/board_create.css"
	  rel="stylesheet">
</head>
<body>
	<%@ include file="../fragments/header.jsp" %>
	<c:if test="${!empty SPRING_SECURITY_CONTEXT }"> 
		<c:set var="principal" value="${SPRING_SECURITY_CONTEXT.authentication.principal }"></c:set>
	</c:if>
	<div class="container">
		<div class="title-box">
			<h3>공지사항 등록</h3>
			<hr class="title-line">
		</div>

		<div class="row justify-content-center">
			<div class="col-lg-6">
				<div class="card shadow-sm">
					<div class="card-body">
						<form action="${root }/notices/noticeCreate" method="post">
							<div class="mb-3">
								<label for="writer" class="form-label">작성자 ID</label>
								<input type="text" class="form-control" id="writer" name="writer" placeholder="작성자ID..." value=${principal.member.name} disabled>
							</div>

							<div class="mb-3">
								<label for="title" class="form-label">제목</label>
								<input type="text" class="form-control" id="title" name="title" placeholder="제목...">
							</div>

							<div class="mb-3">
								<label for="content" class="form-label">내용</label>
								<textarea class="form-control" id="content" name="content" placeholder="내용 입력" rows="5"></textarea>
							</div>

							<div class="text-center">
								<button type="submit" class="btn btn-primary">글작성</button>
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