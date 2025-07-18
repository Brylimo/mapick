<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="com.enjoytrip.model.dto.BoardDTO" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>게시글 상세 조회</title>
<link type="text/css"
      href="/css/board_detail.css"
      rel="stylesheet">
</head>
<body>
	<%@ include file="../fragments/header.jsp" %>

    <div class="container">
        <div class="title-box">
            <h3>여행정보공유</h3>
            <hr class="title-line">
        </div>

        <%
            BoardDTO dto = (BoardDTO) request.getAttribute("dto");
            if (dto == null) {
        %>
            <p>회원 정보가 없습니다.</p>
        <%
        } else {
        %>
            <div class="title-block">
                <h2>${dto.title}</h2>
            </div>
            <div class="content-block">
                <div class="img-block"></div>
                <div class="writer-block">
                    <div class="writer-name">${dto.member.name}</div>
                    <div>${dto.createdAt}</div>
                </div>
            </div>
            <hr>

            <br>
            <p class="board-content">
                ${dto.content}
            </p>
            <div class="d-flex justify-content-end">
                <button type="button" class="btn btn-outline-primary" onclick="location.href='${root }/board/list'">글목록</button>

                <%
                    if (dto.getAuthor() == (int)session.getAttribute("userId")) {
                %>
                    <button type="button" class="btn btn-outline-success" onclick="location.href='${root }/board/boardUpdateForm?id=${dto.id}'">글수정</button>
                    <button type="button" id="board-delete" class="btn btn-outline-danger">글삭제</button>
                <%
                    }
                %>
            </div>
        <%
            }
        %>
    </div>
	
    <%@ include file="../fragments/footer.jsp" %>

    <script>
        document.getElementById("board-delete").addEventListener("click", (e) => {
            if (confirm("정말 삭제하시겠습니까?")) {
                location.href='${root }/board/boardDelete?id=${dto.id}'
            }
        })
    </script>
</body>
</html>