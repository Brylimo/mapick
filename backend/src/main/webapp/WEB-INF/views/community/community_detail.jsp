<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>커뮤니티 생성</title>
<link type="text/css"
	  href="${pageContext.request.contextPath}/css/board_create.css"
	  rel="stylesheet">
<style>
	.sm-txt {
		font-size: 10px;
	}
	#timeInputWrapper {
		gap: 10px;
	}
</style>
</head>
<body>
	<%@ include file="../fragments/header.jsp" %>
	<c:if test="${!empty SPRING_SECURITY_CONTEXT }">
		<c:set var="principal" value="${SPRING_SECURITY_CONTEXT.authentication.principal }"></c:set>
	</c:if>
	<div class="container">
		<div class="title-box">
			<h3>커뮤니티</h3>
			<hr class="title-line">
		</div>

		<div class="row justify-content-center">
			<div class="col-lg-6">
				<div class="card shadow-sm">
					<div class="card-body">
						<form id="communityForm" action="${ root }/communities/communityUpdateForm" method="get">
						<input type="hidden" name="communityId" value="${dto.communityId}" />
							<div class="mb-3">
								<label for="communityName" class="form-label">커뮤니티 이름</label>
							  	<input class="form-control" type="text" id="communityName" value="${dto.communityName}" readonly>
							 </div>
							 <div class="mb-3">
							 	<label for="desc" class="form-label">커뮤니티 소개</label>
							  	<textarea class="form-control" id="desc" rows="5" readonly>${dto.desc}</textarea>
							 </div>
							 <div class="mb-3">
								  <label for="categories" class="form-label">카테고리</label>
								  <c:forEach var="category" items="${dto.categoryList}" varStatus="status">
								  	    <input type="text" class="form-control" value="${category.categoryName}" readonly>
								  </c:forEach>
							 </div>
							 <div class="mb-3">
								  <label for="tags" class="form-label">태그</label>
								  <c:forEach var="tag" items="${dto.tagList}" varStatus="status">
									  <input type="text" class="form-control" value="${tag.name}" readonly>
								  </c:forEach>
							 </div>
							<c:if test="${not empty principal and dto.registeredBy == principal.member.mno}">
								<div class="text-center">
									<button type="submit" class="btn btn-primary">커뮤니티 수정</button>
									<button type="button" id="community-delete" class="btn btn-primary">커뮤니티 삭제</button>
								</div>
							</c:if>
						</form>
					</div>
				</div>
			</div>
		</div>
	</div>
	
	<%@ include file="../fragments/footer.jsp" %>
	 <script>
        document.getElementById("community-delete").addEventListener("click", (e) => {
            if (confirm("정말 삭제하시겠습니까?")) {
                location.href='${root }/communities/communityDelete?communityId=${dto.communityId}'
            }
        })
    </script>
</body>
</html>