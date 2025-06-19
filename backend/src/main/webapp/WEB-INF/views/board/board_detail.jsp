<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="com.enjoytrip.model.dto.BoardDTO" %>
<%@ page import="com.enjoytrip.model.dto.BoardResDTO" %>
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
	<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
	<%@ include file="../fragments/header.jsp" %>
    <c:if test="${!empty SPRING_SECURITY_CONTEXT }">
        <c:set var="principal" value="${SPRING_SECURITY_CONTEXT.authentication.principal }"></c:set>
    </c:if>
    <div class="container">
        <div class="title-box">
            <h3>여행정보공유</h3>
            <hr class="title-line">
        </div>

        <%
            BoardResDTO dto = (BoardResDTO) request.getAttribute("dto");
            if (dto == null) {
        %>
            <p>게시글 정보가 없습니다.</p>
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
					<form id="like-form" action="${root}/boards/boardLike/${communityId}" method="post" style="display: inline;">
					    <input type="hidden" name="id" value="${dto.id}" />
					    <button type="submit" class="like-button">
					        <i class="fa-heart ${dto.liked ? 'fas liked' : 'far'}"></i>
					    </button>
					</form>
					<span>${dto.likeCnt}</span>
                </div>
            </div>
            <hr>

            <br>
            <p class="board-content">
                ${dto.content}
            </p>
            <div class="d-flex justify-content-end">
                <button type="button" class="btn btn-outline-primary" onclick="location.href='${root }/boards/list/${communityId}'">글목록</button>
                <c:if test="${not empty principal and dto.author == principal.member.mno}">
                    <button type="button" class="btn btn-outline-success" onclick="location.href='${root }/boards/boardUpdateForm/${communityId}?id=${dto.id}'">글수정</button>
                    <button type="button" id="board-delete" class="btn btn-outline-danger">글삭제</button>
                </c:if>
            </div>
        <%
            }
        %>
	    <div class="comment-section">
	    <h4>댓글</h4>
	
	    <!-- 댓글 작성 폼 -->
	    <form action="${root}/comment/insertComment/${communityId}/${dto.id}" method="post">
	        <textarea name="content" rows="3" cols="80" placeholder="댓글을 입력하세요..." required></textarea>
	        <input type="hidden" name="parentId" value="0" /> <!-- 일반 댓글일 경우 -->
	        <button type="submit" class="btn btn-primary">등록</button>
	    </form>
	
	    <br>
	
	    <!-- 댓글 리스트 -->
	    <c:forEach var="comment" items="${commentList}">
	        <div class="comment">
	            <p><strong>${comment.writerName}</strong> - ${comment.createdAt}</p>
	            <p>${comment.content}</p>
	
	            <!-- 대댓글 작성 버튼 -->
	            <button type="button" onclick="toggleReplyForm(${comment.id})">답글</button>
	
	            <!-- 대댓글 작성 폼 -->
	            <form action="${root}/comment/insertSubComment/${communityId}/${comment.id}" method="post" style="display:none;" id="reply-form-${comment.id}">
	                <textarea name="content" rows="2" cols="70" placeholder="답글을 입력하세요..." required></textarea>
	                <input type="hidden" name="parentId" value="${comment.id}" />
	                <button type="submit" class="btn btn-secondary">답글 등록</button>
	            </form>
	
	            <!-- 대댓글 출력 -->
	            <c:forEach var="child" items="${comment.childComments}">
	                <div class="child-comment" style="margin-left: 20px;">
	                    <p><strong>${child.writerName}</strong> - ${child.createdAt}</p>
	                    <p>${child.content}</p>
	                </div>
	            </c:forEach>
	        </div>
	        <hr>
		    </c:forEach>
		</div>   
    </div>
	
    <%@ include file="../fragments/footer.jsp" %>

    <script>
        document.getElementById("board-delete").addEventListener("click", (e) => {
            if (confirm("정말 삭제하시겠습니까?")) {
                location.href='${root }/boards/boardDelete/${communityId}?id=${dto.id}'
            }
        })
        
        function toggleReplyForm(commentId) {
		    const form = document.getElementById("reply-form-" + commentId);
		    if (form.style.display === "none") {
		        form.style.display = "block";
		    } else {
		        form.style.display = "none";
		    }
		}
    </script>
</body>
</html>