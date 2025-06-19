<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="com.enjoytrip.model.dto.NoticeDTO" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>공지사항 수정</title>
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
			NoticeDTO dto = (NoticeDTO) request.getAttribute("dto");
			if (dto == null) {
		%>
			<p>공지사항 정보가 없습니다.</p>
		<%
		} else {
		%>
		<div class="row justify-content-center">
			<div class="col-lg-6">
				<div class="card shadow-sm">
					<div class="card-body">
						<form action="${root }/notices/noticeUpdate" method="post">
							<input type="hidden" name="id" value="${dto.id}">

							<div class="mb-3">
								<label for="writer" class="form-label">작성자 ID</label>
								<input type="text" class="form-control" id="writer" name="writer" placeholder="작성자ID..." value=${sessionScope.userName} disabled>
							</div>

							<div class="mb-3">
								<label for="title" class="form-label">제목</label>
								<input type="text" class="form-control" id="title" name="title" placeholder="제목..." value="${dto.title}">
							</div>

							<div class="mb-3">
								<label for="content" class="form-label">내용</label>
								<textarea class="form-control" id="content" name="content" placeholder="내용 입력" rows="5">${dto.content}</textarea>
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