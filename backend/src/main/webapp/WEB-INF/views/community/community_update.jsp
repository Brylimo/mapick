<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <title>커뮤니티 수정</title>

  <!-- 절대 경로로 바꿔서 오류 방지 -->
  <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/board_create.css"/>

  <style>
    .sm-txt { font-size: 10px; }
    #timeInputWrapper { gap: 10px; }
  </style>
</head>
<body>
<%@ include file="../fragments/header.jsp" %>

<c:if test="${!empty SPRING_SECURITY_CONTEXT}">
  <c:set var="principal" value="${SPRING_SECURITY_CONTEXT.authentication.principal}" />
</c:if>

<div class="container">
  <div class="title-box">
    <h3>커뮤니티 수정</h3>
    <hr class="title-line">
  </div>

  <div class="row justify-content-center">
    <div class="col-lg-6">
      <div class="card shadow-sm">
        <div class="card-body">
          <form id="communityForm" action="${pageContext.request.contextPath}/communities/communityUpdate" method="post">
            <input type="hidden" name="communityId" value="${dto.communityId}">
            <input type="hidden" name="registeredBy" value="${dto.registeredBy}">
            <!-- 커뮤니티 이름 -->
            <div class="mb-3">
              <label for="communityName" class="form-label">커뮤니티 이름</label>
              <input class="form-control" type="text" id="communityName" name="communityName" value="${dto.communityName}">
            </div>

            <!-- 커뮤니티 소개 -->
            <div class="mb-3">
              <label for="desc" class="form-label">커뮤니티 소개</label>
              <textarea class="form-control" id="desc" name="desc" rows="5">${dto.desc}</textarea>
            </div>

           <div class="mb-3">
			  <label class="form-label">카테고리</label>
			  <div id="categoryContainer">
			    <c:forEach var="category" items="${dto.categoryList}" varStatus="status">
			      <div class="input-group mb-2">
			        <input type="text" class="form-control" name="categoryList[${status.index}].categoryName" value="${category.categoryName}">
			        <button type="button" class="btn btn-danger" onclick="removeAndReindex(this, 'categoryList', 'categoryName')">삭제</button>
			      </div>
			    </c:forEach>
			  </div>
			  <button type="button" class="btn btn-outline-primary mt-2" onclick="addCategory()">카테고리 추가</button>
			</div>
			<div class="mb-3">
			  <label class="form-label">태그</label>
			  <div id="tagContainer">
			    <c:forEach var="tag" items="${dto.tagList}" varStatus="status">
			      <div class="input-group mb-2">
			        <input type="text" class="form-control" name="tagList[${status.index}].name" value="${tag.name}">
			        <button type="button" class="btn btn-danger" onclick="removeAndReindex(this, 'tagList', 'name')">삭제</button>
			      </div>
			    </c:forEach>
			  </div>
			  <button type="button" class="btn btn-outline-primary mt-2" onclick="addTag()">태그 추가</button>
			</div>


            <!-- 제출 버튼 -->
            <div class="text-center">
              <button type="submit" class="btn btn-primary">수정하기</button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</div>

<%@ include file="../fragments/footer.jsp" %>

<script>
    function addCategory() {
	  const container = document.getElementById("categoryContainer");
	  const index = container.children.length;
	  const div = document.createElement("div");
	  div.className = "input-group mb-2";
	  div.innerHTML = `
	    <input type="text" class="form-control" name="categoryList[${index}].categoryName" placeholder="카테고리 입력">
	    <button type="button" class="btn btn-danger" onclick="removeAndReindex(this, 'categoryList', 'categoryName')">삭제</button>`;
	  container.appendChild(div);
	}

	function addTag() {
	  const container = document.getElementById("tagContainer");
	  const index = container.children.length;
	  const div = document.createElement("div");
	  div.className = "input-group mb-2";
	  div.innerHTML = `
	    <input type="text" class="form-control" name="tagList[${index}].name" placeholder="태그 입력">
	    <button type="button" class="btn btn-danger" onclick="removeAndReindex(this, 'tagList', 'name')">삭제</button>`;
	  container.appendChild(div);
	}

	function removeAndReindex(button, listName, fieldName) {
	  const container = button.closest('.mb-3').querySelector('div[id$="Container"]');
	  button.parentElement.remove();

	  // 🔁 인덱스 재조정
	  const children = container.querySelectorAll('.input-group');
	  children.forEach((div, idx) => {
	    const input = div.querySelector('input');
	    input.name = `${listName}[${idx}].${fieldName}`;
	  });
	}

</script>
</body>
</html>
