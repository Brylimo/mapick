<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ page import="java.util.*" %>
<%@ page import="com.enjoytrip.model.dto.NoticeDTO" %>
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
            <h3>공지사항 리스트</h3>
            <hr class="title-line">
        </div>

        <%
        List<NoticeDTO> list = (List<NoticeDTO>) request.getAttribute("list");
        
        if (list == null || list.isEmpty()) {
        %>
            <div class="alert alert-warning text-center">공지사항 정보가 없습니다.</div>
        <%
        } else {
        %>
        <table class="table table-hover text-center">
            <thead class="table-dark">
            <tr>
                <th>글번호</th>
                <th>제목</th>
                <th>작성자</th>
                <th>작성일</th>
            </tr>
            </thead>
            <tbody>

            <c:forEach var="notice" items="${list}" varStatus="status">
                <tr>
                    <td>${status.index + 1}</td>
                    <td>
                        <a href="${root }/notices/noticeDetail?id=${notice.getId()}">
                                ${notice.getTitle()}
                        </a>
                    </td>
                    <td>${notice.getMember().getName()}</td>
                    <td>${notice.getCreatedAt()}</td>
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