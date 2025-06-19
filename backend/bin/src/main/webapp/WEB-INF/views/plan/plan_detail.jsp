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
	#vmap {
		position: relative; /* 부모가 relative여야 자식 absolute가 지도 기준으로 붙음 */
	}

	#mapOverlayBox {
		position: absolute;
		top: 20px;
		left: 20px;
		padding: 15px;
		border-radius: 10px;
		box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
		z-index: 1000; /* 지도보다 위에 나오게 */
		font-size: 14px;
		background: rgba(255, 255, 255, 0.7);
	}

	#mapOverlayBox strong {
		font-size: 24px;
		margin-bottom: 14px;
	}

	#mapOverlayList {
		height: 550px;
		width: 300px;
	}

	.mapOverlayItem {
		display: flex;
		flex-direction: row;
		height: 30px;
		margin-bottom: 10px;
		gap: 10px;
		align-items: center;
	}

	.red-btn {
		background-color: #e74c3c; /* 빨간색 */
		color: white;              /* 글자색 */
		border: none;              /* 테두리 제거 */
		padding: 8px 12px;         /* 여백 */
		border-radius: 5px;        /* 둥근 모서리 */
		font-weight: bold;         /* 글자 두껍게 */
		cursor: pointer;           /* 마우스 커서 변경 */
		transition: background-color 0.2s ease;
	}

	.red-btn:hover {
		background-color: #c0392b; /* 호버 시 진한 빨강 */
	}

	.sop-control-zoom.sop-bar.sop-control {
		display: none !important;
	}
</style>

</head>
<body>
	<%@ include file="/fragments/header.jsp" %>

	<div class="container mt-5">
		<h2 class="mb-4">여행 계획!!</h2>
		<form>
			<div class="row g-2" style="margin-bottom: 15px;">
				<div class="col-md-6">
					<input id="loc-input" type="text" placeholder="장소 입력" style="width: 100%; height: 100%;"/>
				</div>

				<div class="col-md-2">
					<button class="btn btn-primary w-100" type="button" id="searchBtn">검색</button>
				</div>
			</div>
		</form>
	</div>

	<div id="vmap" style="width : 100%; height: 700px;">
		<div id="mapOverlayBox">
			<strong>🗺️ 계획 리스트</strong><br />
			<div id="mapOverlayList">
				<c:forEach var="item" items="${list}">
					<div class="mapOverlayItem">
						<button class="red-btn" onclick="onClickRedBtn(event, ${item.ppid})">X</button>
						${item.attraction.title}
					</div>
				</c:forEach>
			</div>
		</div>
	</div>
	<%@ include file="/fragments/footer.jsp" %>

	<script>
		const root = "${pageContext.request.contextPath}";
	</script>

	<script type = "text/javascript" src = "https://sgisapi.kostat.go.kr/OpenAPI3/auth/javascriptAuth?consumer_key=4b8c6fef973a4de8813d" ></script>
	<script src="${root }/js/keys.js"></script>
	<script src="https://cdnjs.cloudflare.com/ajax/libs/proj4js/2.7.5/proj4.js"></script>
	<script src="${root }/js/common.js"></script>

	<script>
		let accessToken;
		const map = sop.map("vmap");
		// marker 목록
		const markers = [];
		// 경계 목록
		const bounds = [];

		function addMapOverlayItem(ppid, title) {
			const container = document.getElementById("mapOverlayList");

			const div = document.createElement("div");
			div.className = "mapOverlayItem";

			// 버튼 생성
			const button = document.createElement("button");
			button.className = "red-btn";
			button.textContent = "X";
			button.onclick = function () {
				onClickRedBtn(ppid);
			};

			// 텍스트 노드 생성
			const text = document.createTextNode(" " + title);

			// 요소들 조립
			div.appendChild(button);
			div.appendChild(text);

			// 맨 아래에 추가
			container.appendChild(div);
		}

		function onClickRedBtn(e, id) {
			const formData = new URLSearchParams();
			formData.append('planattractionid', id);

			fetch(root + '/planAttractionController?action=planAttractionDelete', {
				method: 'POST',
				headers: {
					'Content-Type': 'application/x-www-form-urlencoded'
				},
				body: formData
			})
			.then(response => {
				if (response.status != 200) {
					alert("여행지 삭제에 실패했습니다.")
					return
				} else {
					const overlayItem = e.target.closest('.mapOverlayItem');
					if (overlayItem) {
						overlayItem.remove();
					}

					/*const index = markers.findIndex(marker => {
						const markerInfo = marker.getInfoWindow().getContent();
						console.log(markerInfo)
						return markerInfo.includes(ppid); // 또는 info.title 같은 걸로 비교
					});

					if (index !== -1) {
						map.removeLayer(markers[index]); // 또는 markers[index].remove();
						markers.splice(index, 1); // 배열에서도 제거
					}*/
				}
			})
			.catch(error => {
				console.error("데이터 불러오기 실패:", error);
			});
		}

		const onClickAddBtn = (planId, info) => {
			const formData = new URLSearchParams();
			formData.append('planid', planId);
			formData.append("addr1", info.addr1);
			formData.append("addr2", info.addr2);
			formData.append("areacode", info.areacode);
			formData.append("booktour", info.booktour);
			formData.append("cat1", info.cat1);
			formData.append("cat2", info.cat2);
			formData.append("cat3", info.cat3);
			formData.append("contentid", info.contentid);
			formData.append("contenttypeid", info.contenttypeid);
			formData.append("createdtime", info.createdtime);
			formData.append("firstimage", info.firstimage);
			formData.append("firstimage2", info.firstimage2);
			formData.append("mapx", info.mapx);
			formData.append("mapy", info.mapy);
			formData.append("mlevel", info.mlevel);
			formData.append("modifiedtime", info.modifiedtime);
			formData.append("no", info.no);
			formData.append("sigungucode", info.sigungucode);
			formData.append("tel", info.tel);
			formData.append("title", info.title);

			fetch(root + '/planAttractionController?action=planAttractionCreateData', {
				method: 'POST',
				headers: {
					'Content-Type': 'application/x-www-form-urlencoded'
				},
				body: formData
			})
			.then(response => {
				if (response.status != 200) {
					alert("여행지 저장에 실패했습니다.")
					return
				}
				return response.json();
			})
			.then(data => {
				addMapOverlayItem(data.ppid, info.title)

				const mappedData = [];

				const utmkCoords = convertToUTMK(info.mapy, info.mapx);
				mappedData.push({ ...info, utmk: { x: utmkCoords[0], y: utmkCoords[1] } });
				updateMapNoReset(mappedData);
			})
			.catch(error => {
				console.error("데이터 불러오기 실패:", error);
			});
		}

		// access token 가져오기
		const getAccessToken = async () => {
			try {
				const json = await getFetch("https://sgisapi.kostat.go.kr/OpenAPI3/auth/authentication.json", {
					consumer_key: "a5ecdbff674e4cd59ccb", // 서비스 id
					consumer_secret: "88d00df7a12741ae93ea", // 보안 key
				});
				accessToken = json.result.accessToken;
			} catch (e) {
				console.log(e);
			}
		};

		const getCoords = async (address) => {
			try {
				const json = await getFetch("https://sgisapi.kostat.go.kr/OpenAPI3/addr/geocode.json", {
					accessToken: accessToken,
					address: address,
					resultcount: 1,
				});
				if (json.errCd === -401) {
					await getAccessToken();
					return await getCoords(address);
				}
				return json.result.resultdata[0];
			} catch (e) {
				console.log(e);
			}
		};

		const updateMap = (infos) => {
			resetMarker();
			try {
				for (let i = 0; i < infos.length; i++) {
					const info = infos[i];
					console.log(info);
					const marker = sop.marker([info.utmk.x, info.utmk.y],{icon:setIcon(info.contenttypeid)});
					marker.addTo(map).bindInfoWindow(setInfo(info.title, info.firstimage ? info.firstimage : info.firstimage2, info.tel, info.addr1, info.addr2, info));
					markers.push(marker);
					bounds.push([info.utmk.x, info.utmk.y]);
				}
				// 경계를 기준으로 map을 중앙에 위치하도록 함
				if (bounds.length > 1) {
					map.setView(map._getBoundsCenterZoom(bounds).center, map._getBoundsCenterZoom(bounds).zoom);
				} else {
					map.setView(map._getBoundsCenterZoom(bounds).center, 9);
				}
			} catch (e) {
				console.log(e);
			}
		};

		const updateMapNoReset = (infos) => {
			try {
				for (let i = 0; i < infos.length; i++) {
					const info = infos[i];
					console.log(info);
					const marker = sop.marker([info.utmk.x, info.utmk.y],{icon:setIcon(info.contenttypeid)});
					marker.addTo(map).bindInfoWindow(setInfo(info.title, info.firstimage ? info.firstimage : info.firstimage2, info.tel, info.addr1, info.addr2, info));
					markers.push(marker);
					bounds.push([info.utmk.x, info.utmk.y]);
				}
				// 경계를 기준으로 map을 중앙에 위치하도록 함
				if (bounds.length > 1) {
					map.setView(map._getBoundsCenterZoom(bounds).center, map._getBoundsCenterZoom(bounds).zoom);
				} else {
					map.setView(map._getBoundsCenterZoom(bounds).center, 9);
				}
			} catch (e) {
				console.log(e);
			}
		};

		const resetMarker = () => {
			markers.forEach((item) => item.remove());
			markers.length = 0;
			bounds.length = 0;
		};

		const setInfo = (title,img,tel,address,addr2, info) => {

			if(!img){
				//대체 이미지 소스
				img = '/assets/img/noImage.png';
			}

			if(!tel){
				tel = "공개되지 않는 정보입니다."
			}

			if(!address){
				address = "공개되지 않는 정보입니다."
			}

			if(!addr2){
				addr2 = "";
			}

			const planId = "${plan.pid}";
			let result = `
				<div class="marker-info">
				  <h2>\${title}</h2>
				  <hr>
				  <img src="\${img}" alt="이미지 제공 X" width="200" height="150" />
				  <p>주소 : \${address}<br>\${addr2}</p>
				  <p>Tel : \${tel}</p>
				  <button onclick='onClickAddBtn(\${planId}, \${JSON.stringify(info)})'>
					추가
				  </button>
				</div>
			  `

			return result;
		}

		const setIcon = (contenttypeid) => {
			let img = "";

			if (contenttypeid === 15) { // 행사, 공연, 축제
				img = `${root}/img/marker/concert.png`;
			} else if (contenttypeid === 15) { // 문화시설
				img = `${root}/img/marker/movie.png`;
			} else if (contenttypeid === 12) { //관광지?
				img = `${root}/img/marker/camera.png`;
			}  else if (contenttypeid === 25) { //코스여행
				img = `${root}/img/marker/bus.png`;
			} else if (contenttypeid === 28) { //레포츠
				img = `${root}/img/marker/surfing.png`;
			} else if (contenttypeid === 32) { //숙박
				img = `${root}/img/marker/hotel.png`;
			} else if (contenttypeid === 38) { //쇼핑
				img = `${root}/img/marker/shopping.png`;
			} else if (contenttypeid === 39) { //음식
				img = `${root}/img/marker/food.png`;
			}else{ //없는 경우
				img = `${root}/img/marker/marker.png`;
			}

			let icon = sop.icon({
				iconUrl: img, // 변경할 마커 이미지 경로
				iconSize: [32, 37],      // 아이콘의 크기 (예시)
				iconAnchor: [16, 37],    // 아이콘에서 좌표에 해당하는 기준점 설정
				popupAnchor: [0, -37]    // 팝업(정보창) 위치 오프셋
			});

			return icon;
		}

		proj4.defs("EPSG:5179", "+proj=tmerc +lat_0=38 +lon_0=127.5 +k=0.9996 +x_0=1000000 +y_0=2000000 +ellps=GRS80 +towgs84=0,0,0,0,0,0,0 +units=m +no_defs");

		const convertToUTMK = (lat, lon) => {
			return proj4("EPSG:4326", "EPSG:5179", [parseFloat(lon), parseFloat(lat)]); // 경도, 위도 순서
		}

		document.getElementById("searchBtn").addEventListener("click", function (event) {
			event.preventDefault();

			let keyword = document.querySelector("#loc-input").value;

			fetch(root + `/attractionController?action=getSearchedDataByKeyword` + `&keyword=` + keyword)
					.then(response => {
						if (!response.ok) {
							throw new Error("서버 응답 실패");
						}
						return response.json();
					})
					.then(data => {
						console.log(data)
						const mappedData = [];

						const utmkCoords = convertToUTMK(data.mapy, data.mapx);
						mappedData.push({ ...data, utmk: { x: utmkCoords[0], y: utmkCoords[1] } });
						updateMap(mappedData);
					})
					.catch(error => {
						console.error("데이터 불러오기 실패:", error);
						alert("데이터를 불러오는 데 실패했습니다.");
					});
		});

		window.onload = async () => {
			await getAccessToken();

			updateMap([
				{
					address: "광주 광산구 하남산단6번로 133",
					utmk: await getCoords("광주 광산구 하남산단6번로 133"),
					title: "SSAFY 광주 캠퍼스",
					img : "/assets/img/campus.png",
					tel : "02-3429-5100 "
				},
			]);
		}
	</script>
</body>
</html>