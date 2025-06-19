<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Error Page</title>
</head>
<body>
	<%@ include file="../fragments/header.jsp" %>
	<c:choose>
		<c:when test="${not empty message}">
			<h2>${message}</h2>
		</c:when>
		<c:otherwise>
			<h2>오류가 발생했습니다.</h2>
		</c:otherwise>
	</c:choose>
	
	<h3><a href="${root}/">Go Home</a></h3>
	
	<%@ include file="../fragments/footer.jsp" %>
</body>
</html>