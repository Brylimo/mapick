<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>게시판 등록</title>
<link type="text/css"
	  href="${pageContext.request.contextPath}/css/board_create.css"
	  rel="stylesheet">
<style>
	.sm-txt {
		font-size: 10px;
	}
	#timeInputWrapper {
		gap: 10px;
	}
</style>
</head>
<body>
	<%@ include file="../fragments/header.jsp" %>

	<div class="container">
		<div class="title-box">
			<h3>여행정보공유</h3>
			<hr class="title-line">
		</div>

		<div class="row justify-content-center">
			<div class="col-lg-6">
				<div class="card shadow-sm">
					<div class="card-body">
						<form action="${root }/board/boardCreate" method="post" id="boardForm">
							<div class="mb-3">
								<label for="writer" class="form-label">작성자 ID</label>
								<input type="text" class="form-control" id="writer" name="writer" placeholder="작성자ID..." value=${sessionScope.userName} disabled>
							</div>

							<div class="mb-3">
								<label for="title" class="form-label">제목</label>
								<input type="text" class="form-control" id="title" name="title" placeholder="제목...">
							</div>

							<div class="mb-3">
								<label for="content" class="form-label">내용</label>
								<textarea class="form-control" id="content" name="content" placeholder="내용 입력" rows="5"></textarea>
							</div>

							<div class="mb-3">
								<div class="d-flex align-items-center gap-2">
									<label for="flexSwitchCheckDefault" class="form-label mb-0">예약 날짜 설정</label>
									<div class="form-check form-switch mb-0">
										<input class="form-check-input" type="checkbox" role="switch" id="flexSwitchCheckDefault">
									</div>
									<div class="sm-txt">예약 날짜에 맞춰 공유글이 포스팅됩니다.</div>
								</div>

								<div id="timeInputWrapper" style="display: none;">
									<input type="date" class="form-control" id="dateInput" name="reservationDate">
									<input type="time" class="form-control" id="timeInput" name="reservationTime">
								</div>
							</div>

							<div class="text-center">
								<button type="submit" class="btn btn-primary">글작성</button>
								<button type="reset" class="btn btn-danger">초기화</button>
							</div>
						</form>
					</div>
				</div>
			</div>
		</div>
	</div>
	
	<%@ include file="../fragments/footer.jsp" %>

	<script>
		const switchInput = document.getElementById('flexSwitchCheckDefault');
		const timeInputWrapper = document.getElementById('timeInputWrapper');

		document.querySelector("#boardForm").addEventListener("click", (event) => {
			const dateInput = document.getElementById("dateInput");
			const timeInput = document.getElementById("timeInput");

			const date = dateInput.value;
			const time = timeInput.value;

			if (!date || !time) return; // 값이 비어있으면 검사하지 않음

			const selectedDateTime = new Date(`\${date}T\${time}`);
			const now = new Date();

			if (selectedDateTime < now) {
				event.preventDefault(); // 폼 제출 막기
				alert("예약 시간은 현재보다 미래여야 합니다.");
			}
		})

		switchInput.addEventListener('change', function () {
			if (this.checked) {
				timeInputWrapper.style.display = 'flex';
			} else {
				timeInputWrapper.style.display = 'none';
			}
		});
	</script>
</body>
</html>