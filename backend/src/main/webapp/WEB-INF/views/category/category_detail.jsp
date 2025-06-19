<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="com.enjoytrip.model.dto.CategoryDTO" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>카테고리 상세 조회</title>
<link type="text/css"
      href="${pageContext.request.contextPath}/css/board_detail.css"
      rel="stylesheet">
</head>
<body>
    <%@ include file="../fragments/header.jsp" %>

    <div class="container">
        <div class="title-box">
            <h3>카테고리</h3>
            <hr class="title-line">
        </div>

        <%
            CategoryDTO dto = (CategoryDTO) request.getAttribute("dto");
            if (dto == null) {
        %>
            <p>회원 정보가 없습니다.</p>
        <%
        } else {
        %>
            <div class="title-block">
                <h2>${dto.categoryName}</h2>
            </div>
            <hr>

            <br>
            <div class="d-flex justify-content-end">
                 <button type="button" class="btn btn-outline-primary" onclick="location.href='${root }/category/list'">글목록</button>
                 <button type="button" class="btn btn-outline-success" onclick="location.href='${root }/category/categoryUpdateForm?categoryId=${dto.categoryId}'">글수정</button>
                 <button type="button" id="category-delete" class="btn btn-outline-danger">글삭제</button>

            </div>
        <%
            }
        %>
    </div>

    <%@ include file="../fragments/footer.jsp" %>

    <script>
        document.getElementById("category-delete").addEventListener("click", (e) => {
            if (confirm("정말 삭제하시겠습니까?")) {
                location.href='${root }/category/categoryDelete?categoryId=${dto.categoryId}'
            }
        })
    </script>
</body>
</html>