<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>SSAFY TRIP</title>
</head>
<body>
	<%@ include file="/WEB-INF/views/fragments/header.jsp" %>

	<div class="container mt-5">
		<h2 class="mb-4">관광지 검색</h2>
		<form>

			<!-- 지역 선택, 세부 지역 선택, 카테고리 선택 (두 번째 줄) -->
			<div class="row g-2">
				<!-- 지역 선택 -->
				<div class="col-md-3">
					<select class="form-select" name="region" id="region">
						<option value="">지역 선택</option>
						<c:if test="${not empty sidos}">
							<c:forEach var="sido" items="${sidos}">
								<option value="${sido.sidoCode}">${sido.sidoName}</option>
							</c:forEach>
						</c:if>
					</select>
				</div>

				<!-- 세부 지역 선택 -->
				<div class="col-md-3">
					<select class="form-select" name="subregion" id="subregion">
						<option value="">세부 지역 선택</option>
					</select>
				</div>

				<!-- 카테고리 선택 -->
				<div class="col-md-3">
					<select class="form-select" name="category" id="category">
						<option value="">전체</option>
						<c:if test="${not empty contentTypes}">
							<c:forEach var="contentType" items="${contentTypes}">
								<option value="${contentType.contentTypeId}">${contentType.contentTypeName}</option>
							</c:forEach>
						</c:if>
					</select>
				</div>

				<div class="col-md-2">
					<button class="btn btn-primary w-100" type="button" id="searchBtn">검색</button>
				</div>
			</div>
		</form>
	</div>

	<hr>

	<div id="resultContainer"></div>
	<div id="map" style="width : 100%; height: 700px;"></div>

	<%@ include file="/WEB-INF/views/fragments/footer.jsp"%>

	<script>
		const root = "${pageContext.request.contextPath}";
	</script>
	
	<script type = "text/javascript" src = "https://sgisapi.kostat.go.kr/OpenAPI3/auth/javascriptAuth?consumer_key=a5ecdbff674e4cd59ccb"></script>
	<script src="/js/keys.js"></script>
	<script src="https://cdnjs.cloudflare.com/ajax/libs/proj4js/2.7.5/proj4.js"></script>
	<script src="/js/common.js"></script>
	<script src="/js/kostat.js"></script>
	<script src="/js/attraction.js"></script>

	<script>
		document.addEventListener("DOMContentLoaded", function () {
		   const regionSelect = document.getElementById("region");
		   const subregionSelect = document.getElementById("subregion");
	
		   regionSelect.addEventListener("change", function () {
		       const sidoCode = this.value;
	
		       // 기존 옵션 초기화
		       subregionSelect.innerHTML = '<option value="">전체</option>';
		       subregionSelect.disabled = true;
	
		       if (!sidoCode) return;
		       
		       // AJAX 요청
		       fetch(root + `/guguns?sidoCode=` + sidoCode)
		           .then(response => {
		               if (!response.ok) {
		                   throw new Error("서버 응답 실패");
		               }
		               return response.json();
		           })
		           .then(data => {
		               if (data.data && data.data.length > 0) {
		                   data.data.forEach(item => {
		                       const option = document.createElement("option");
		                       option.value = item.gugunCode;
		                       option.textContent = item.gugunName;
		                       subregionSelect.appendChild(option);
		                   });
		                   subregionSelect.disabled = false;
		               }
		           })
		           .catch(error => {
		               console.error("데이터 불러오기 실패:", error);
		               alert("데이터를 불러오는 데 실패했습니다.");
		           });
		    }); 
		   
		});
	    
		function addHotplace(title) {
	        const root = "<%=request.getContextPath()%>";
	        fetch(root + "/hotplace", {
	            method: "POST",
	            headers: { "Content-Type": "application/x-www-form-urlencoded" },
	            body: "title=" + encodeURIComponent(title)
	        })
	        .then(response => response.text())
	        .then(text => {
	        	let data;
	            try {
	                data = JSON.parse(text);
	            } catch(e) {
	                // 파싱 오류가 발생하면, 정상 처리된 것으로 간주 (또는 추가 검증 로직 구현)
	                alert("즐겨찾기에 추가되었습니다.");
	                return;
	            }
	            if (data.success) {
	                alert("즐겨찾기에 추가되었습니다.");
	            } else {
	                alert("즐겨찾기 추가 실패: " + data.message);
	            }
	        })
	        .catch(error => {
	            console.error("즐겨찾기 추가 중 오류:", error);
	            alert("즐겨찾기 추가 중 오류가 발생했습니다.");
	        });
	    }
	</script>
</body>
</html>