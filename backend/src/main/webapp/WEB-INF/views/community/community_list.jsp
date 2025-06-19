<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>

<%@ page import="java.util.*" %>
<%@ page import="com.enjoytrip.model.dto.CommunityDTO" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>커뮤니티 리스트 조회</title>
    <link type="text/css"
          href="${pageContext.request.contextPath}/css/board_list.css"
          rel="stylesheet">
</head>
<body>
<%@ include file="../fragments/header.jsp" %>

<div class="container post-container">
    <div class="title-box">
        <h3>커뮤니티 리스트</h3>
        <hr class="title-line">
    </div>
    <div class="d-flex justify-content-between mb-4">
        <button class="btn btn-outline-primary btn-sm" style="width: 90px;" onclick="location.href='${root }/communities/communityCreateForm'">커뮤니티 생성</button>
         <div class="d-flex">
	         <select name="opt">
	             <option value="">검색조건</option>
	             <option value="name">이름</option>
	             <option value="category">카테고리</option>
	             <option value="tag">태그</option>
	         </select>
             <input type="text" placeholder="검색어.." class="px-2" name="query">
             <button id="search-btn" type="button" class="btn btn-dark" style="width: 60px;">검색</button>
         </div>
    </div>
    <%
        List<CommunityDTO> list = (List<CommunityDTO>) request.getAttribute("list");

        if (list == null || list.isEmpty()) {
    %>
    <div class="alert alert-warning text-center">커뮤니티 정보가 없습니다.</div>
    <%
    } else {
    %>
    <table class="table table-hover text-center">
        <thead class="table-dark">
        <tr>
            <th>커뮤니티 번호</th>
            <th>커뮤니티 제목</th>
        </tr>
        </thead>
        <tbody>

        <c:forEach var="community" items="${list}" varStatus="status">
            <tr>
                <td>${status.index + 1}</td>
                <td>
                    <a href="${root }/communities/communityMain/${community.communityId}">
                            ${community.communityName}
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
<script>
    document.getElementById("search-btn").addEventListener("click", function (event) {
        event.preventDefault();

        let opt = document.querySelector("select[name='opt']").value;
        let query = document.querySelector("input[name='query']").value;

        if (!opt) {
            alert("검색조건을 설정해주세요!")
            return
        }

        fetch("${pageContext.request.contextPath}" + `/communities` + `/searchCommunity` + `?opt=` + opt + `&query=` + query)
            .then(response => response.json())
            .then(data => {
                const tbody = document.querySelector('table tbody');
                tbody.innerHTML = '';

                if (data.data && data.data.length > 0) {
                    data.data.forEach((community, idx) => {
                        const row = document.createElement('tr');

                        const createdAt = new Date(community.regDt);
                        const year = createdAt.getFullYear();
                        const month = (createdAt.getMonth() + 1).toString().padStart(2, '0'); // Month is 0-indexed, so add 1
                        const day = createdAt.getDate().toString().padStart(2, '0');
                        const hours = createdAt.getHours().toString().padStart(2, '0');
                        const minutes = createdAt.getMinutes().toString().padStart(2, '0');
                        const seconds = createdAt.getSeconds().toString().padStart(2, '0');

                        const createdAtFormatted = `\${year}-\${month}-\${day} \${hours}:\${minutes}:\${seconds}`;

                        row.innerHTML = `
                            <td>\${idx + 1}</td>
                            <td><a href="${root}/communities/communityDetail?id=\${community.communityId}">\${community.communityName}</a></td>
                        `;

                        tbody.appendChild(row);
                    });
                } else {
                    const row = document.createElement('tr');
                    row.innerHTML = `<td colspan="4">검색 결과가 없습니다.</td>`;
                    tbody.appendChild(row);
                }
            })
            .catch(error => {
                console.log(error)
                alert("검색 도중 오류가 발생했습니다");
            });
    });

</script>
</html>