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

	<div class="container">
		<div class="title-box">
			<h3>커뮤니티</h3>
			<hr class="title-line">
		</div>

		<div class="row justify-content-center">
			<div class="col-lg-6">
				<div class="card shadow-sm">
					<div class="card-body">
						<form id="communityForm">
							<div class="mb-3">
								<label for="communityName" class="form-label">커뮤니티 이름</label>
							  	<input class="form-control" type="text" id="communityName" name="communityName" placeholder="이름...">
							 </div>
							 <div class="mb-3">
							 	<label for="desc" class="form-label">커뮤니티 소개</label>
							  	<textarea class="form-control" id="desc" name="desc" placeholder="소개 입력" rows="5"></textarea>
							 </div>
							 <div class="mb-3">
							  <label for="categories" class="form-label">카테고리 (쉼표로 구분)</label>
							  <input type="text" class="form-control" id="categories" placeholder="예: 맛집,음식">
							</div>
							<div class="mb-3">
							  <label for="tags" class="form-label">태그 (쉼표로 구분)</label>
							  <input type="text" class="form-control" id="tags" placeholder="예: 한식,분식">
							</div>
							<div class="text-center">
								<button type="button" class="btn btn-primary"  onclick="submitCommunity()">생성</button>
							</div>
						</form>
					</div>
				</div>
			</div>
		</div>
	</div>
	
	<%@ include file="../fragments/footer.jsp" %>
</body>
<script>
function submitCommunity() {
	  const communityName = document.getElementById("communityName").value;
	  const desc = document.getElementById("desc").value;
	  const tags = document.getElementById("tags").value.split(',').map(t => t.trim()).filter(t => t);
	  const categories = document.getElementById("categories").value.split(',').map(c => c.trim()).filter(c => c);
	  const tagList = tags.map(name => ({ name }));
	  const categoryList = categories.map(categoryName => ({ categoryName }));
	  
	  fetch("/communities/communityCreate", {
	    method: "POST",
	    headers: {
	      "Content-Type": "application/json"
	    },
	    body: JSON.stringify({
	        communityName,
	        desc,
	        tagList,
	        categoryList
	    })
	  })
	  .then(response => response.json())
	  .then(data => {
	    console.log("생성 성공:", data);
	    window.location.href = "/communities/communityList";
	  })
	  .catch(error => {
	    console.error("에러 발생:", error);
	    alert("커뮤니티 생성 실패");
	  });
}
</script>
</html>