<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>SSAFY TRIP</title>
    <link type="text/css"
          href="${pageContext.request.contextPath}/css/plan_create.css"
          rel="stylesheet">

    <style>
        .title-box {
            display: flex;
            flex: 1;
            justify-content: center;
        }

        .title-box .title {
            width: 300px;
            border-bottom: 1px solid grey;
            display: flex;
            justify-content: center;
            font-size: 32px;
        }

        .timeline-box {
            margin-top: 30px;
            display: flex;
            justify-content: center;
            width: 100%;
        }

        .timeline-box .timeline {
            position: relative;
            max-width: 1200px;
            margin: auto;
            padding: 40px 0;
        }

        /* 세로선 */
        .timeline::after {
            content: '';
            position: absolute;
            width: 4px;
            background-color: #cce0f5;
            top: 0;
            bottom: 0;
            left: 50%;
            margin-left: -2px;
        }

        /* 블록 공통 스타일 */
        .timeline-item {
            padding: 20px 30px;
            position: relative;
            width: 50%;
            box-sizing: border-box;
        }

        /* 왼쪽/오른쪽 정렬 */
        .timeline-item.left {
            left: 0;
        }

        .timeline-item.right {
            left: 50%;
        }

        /* 박스 안 컨텐츠 */
        .timeline-content {
            background-color: #fff;
            padding: 15px;
            border-radius: 6px;
            border: 1px solid #ddd;
            box-shadow: 0 2px 6px rgba(0,0,0,0.1);
        }

        /* 이미지 */
        .timeline-content img {
            width: 100%;
            border-radius: 5px;
            object-fit: cover;
        }

        /* 제목 및 주소 */
        .timeline-content h3 {
            margin: 10px 0 5px;
        }

        .timeline-content p {
            font-size: 14px;
            color: #555;
            margin: 0 0 8px;
        }

        /* 아이콘 원형 배지 */
        .timeline-icon-left {
            position: absolute;
            top: 20px;
            right: -40px;
            transform: translateX(-50%);
            background: #fff;
            border: 4px solid #5fa8d3;
            width: 40px;
            height: 40px;
            border-radius: 50%;
            z-index: 1;
            display: flex;
            align-items: center;
            justify-content: center;
        }

        .timeline-icon-right {
            position: absolute;
            top: 20px;
            left: 0;
            transform: translateX(-50%);
            background: #fff;
            border: 4px solid #5fa8d3;
            width: 40px;
            height: 40px;
            border-radius: 50%;
            z-index: 1;
            display: flex;
            align-items: center;
            justify-content: center;
        }

        .timeline-icon-left img {
            width: 20px;
            height: 20px;
        }

        .timeline-icon-right img {
            width: 20px;
            height: 20px;
        }

    </style>
</head>
<body>
    <%@ include file="../fragments/header.jsp" %>

    <div class="container mt-5">
        <h2 class="mb-4">여행 계획!!</h2>
    </div>

    <div class="title-box">
        <div class="title">타임 라인</div>
    </div>

    <div class="timeline-box">
        <div class="timeline">
            <c:forEach var="plan" items="${list}" varStatus="status">
                <div class="timeline-item ${status.index % 2 == 0 ? 'left' : 'right'}">
                    <div class="timeline-content">
                        <img src="${plan.attraction.firstImage1}" alt="해운대해수욕장">
                        <h3>${plan.attraction.title}</h3>
                        <p>${plan.attraction.addr1}</p>
                        <label>간단메모:</label><br>
                        <textarea rows="3"></textarea>
                    </div>
                    <div class="${status.index % 2 == 0 ? 'timeline-icon-left' : 'timeline-icon-right' }">
                        <img src="/img/user.png" alt="해변 아이콘">
                    </div>
                </div>
            </c:forEach>
        </div>
    </div>

    <%@ include file="../fragments/footer.jsp" %>
</body>
</html>