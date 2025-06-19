<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ page import="java.util.*" %>
<%@ page import="com.enjoytrip.model.dto.CategoryDTO" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>공지사항 리스트 조회</title>
<link type="text/css"
      href="${pageContext.request.contextPath}/css/board_list.css"
      rel="stylesheet">
</head>
<body>
    <%@ include file="../fragments/header.jsp" %>
	
	<div class="container post-container">
        <div class="title-box">
            <h3>카테고리 리스트</h3>
            <hr class="title-line">
        </div>
        <div class="d-flex justify-content-between mb-4">
            <button class="btn btn-outline-primary btn-sm" style="width: 90px;" onclick="location.href='${root }/category/categoryCreateForm'">카테고리 생성</button>
        </div>
        <%
        List<CategoryDTO> list = (List<CategoryDTO>) request.getAttribute("list");
        
        if (list == null || list.isEmpty()) {
        %>
            <div class="alert alert-warning text-center">카테고리 정보가 없습니다.</div>
        <%
        } else {
        %>
        <table class="table table-hover text-center">
            <thead class="table-dark">
            <tr>
                <th>카테고리 번호</th>
                <th>카테고리 제목</th>
            </tr>
            </thead>
            <tbody>

            <c:forEach var="category" items="${list}" varStatus="status">
                <tr>
                    <td>${status.index + 1}</td>
                    <td>
                        <a href="${root }/category/categoryDetail?categoryId=${category.categoryId}">
                                ${category.categoryName}
                        </a>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
        <%
            }
        %>
    </div>

    <%@ include file="../fragments/footer.jsp" %>
</body>
</html>