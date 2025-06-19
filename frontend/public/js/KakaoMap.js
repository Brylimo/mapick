const KakaoMap = {
  root: window.root || "",
  map: null,
  markers: [],
  bounds: null,
  initMap: (id) => {
    const container = document.getElementById(id);
    const options = {
      center: new kakao.maps.LatLng(35.2225, 126.8444), // 광주 SSAFY 캠퍼스
      level: 3,
    };
    KakaoMap.map = new kakao.maps.Map(container, options);
    KakaoMap.bounds = new kakao.maps.LatLngBounds();
  },
  getCoordsFromKakao: async function (address) {
      const res = await fetch("https://dapi.kakao.com/v2/local/search/address.json?query=" + encodeURIComponent(address), {
        headers: {
          Authorization: "KakaoAK 4917d2973df3fb95f3059a8c7e158416",
        },
      });

      const json = await res.json();
      if (json.documents.length > 0) {
        const { x, y } = json.documents[0]; // x = 경도, y = 위도
        return { x: parseFloat(x), y: parseFloat(y) };
      } else {
        console.warn("주소를 찾을 수 없습니다:", address);
        return null;
      }
  },
  updateMap: (infos, type, package) => {
    KakaoMap.resetMarker();
    infos.forEach((info) => {
      if (!info.latlng) return;

      const lat = parseFloat(info.latlng.y);
      const lng = parseFloat(info.latlng.x);
      const position = new kakao.maps.LatLng(lat, lng);

      const marker = new kakao.maps.Marker({
        map: KakaoMap.map,
        position: position,
        image: KakaoMap.setIcon(info.contenttypeid),
      });

      console.log("apap", type)
      let infowindow = null;
      if (type === "attraction") {
        infowindow = new kakao.maps.InfoWindow({
          content: KakaoMap.setInfoAttraction(type, info.title, info.firstimage || info.img, info.tel, info.addr1, info.addr2),
        });
      } else if (type === "plan") {
        infowindow = new kakao.maps.InfoWindow({
          content: KakaoMap.setInfoPlan(type, info.title, info.firstimage || info.img, info.tel, info.addr1, info.addr2, info, package),
        });
      }

      kakao.maps.event.addListener(marker, "click", function () {
        infowindow.open(KakaoMap.map, marker);
      });

      KakaoMap.markers.push(marker);
      KakaoMap.bounds.extend(position);
    });

    if (!KakaoMap.bounds.isEmpty()) {
      KakaoMap.map.setBounds(KakaoMap.bounds);
    }
  },
  updateMapNoReset: (infos) => {
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
  },
  resetMarker: () => {
    KakaoMap.markers.forEach((m) => m.setMap(null));
    KakaoMap.markers.length = 0;
    KakaoMap.bounds = new kakao.maps.LatLngBounds();
  },
  setInfoAttraction: function (type, title, img, tel, addr1, addr2) {
    if (!img) img = "/assets/img/noImage.png";
    if (!tel) tel = "공개되지 않는 정보입니다.";
    if (!addr1) addr1 = "공개되지 않는 정보입니다.";
    if (!addr2) addr2 = "";

    return `
    <div class="marker-info">
      <h2>${title}</h2>
      <hr>
      <img src="${img}" alt="이미지 제공 X" width="200" height="150" />
      <p>주소 : ${addr1}<br>${addr2}</p>
      <p>Tel : ${tel}</p>
      <form action="${type === 'attraction' ? KakaoMap.root + '/hotplaces' : KakaoMap.root + '/plan-attractions'}" method="post">
        <input type="hidden" name="title" value="${title}">
        <button type="submit">추가</button>
      </form>
    </div>
  `;
  },
  setInfoPlan: function (type, title, img, tel, addr1, addr2, info, planId) {
    if (!img) img = "/assets/img/noImage.png";
    if (!tel) tel = "공개되지 않는 정보입니다.";
    if (!addr1) addr1 = "공개되지 않는 정보입니다.";
    if (!addr2) addr2 = "";

    const escapedInfo = JSON.stringify(info);
    return `
    <div class="marker-info">
      <h2>${title}</h2>
      <hr>
      <img src="${img}" alt="이미지 제공 X" width="200" height="150" />
      <p>주소 : ${addr1}<br>${addr2}</p>
      <p>Tel : ${tel}</p>
      <button onclick='KakaoMap.onClickAddBtn(${planId}, ${escapedInfo})'>추가</button>
    </div>
  `;
  },
  onClickAddBtn: (planId, info) => {

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
    formData.append("no", info.no == null ? null : info.no);
    formData.append("sigungucode", info.sigungucode);
    formData.append("tel", info.tel);
    formData.append("title", info.title);

    fetch('/plan-attractions', {
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
          addMapOverlayItem(data.data.ppid, info.title)

          const mappedData = [];

          const utmkCoords = convertToUTMK(info.mapy, info.mapx);
          mappedData.push({ ...info, utmk: { x: utmkCoords[0], y: utmkCoords[1] } });
          KakaoMap.updateMapNoReset(mappedData);
        })
        .catch(error => {
          console.error("데이터 불러오기 실패:", error);
        });
  },
  setIcon: (contenttypeid) => {
    let img = `${KakaoMap.root}/img/marker/marker.png`; // 기본 마커

    if (contenttypeid === 15) { // 행사, 공연, 축제
      img = `${KakaoMap.root}/img/marker/concert.png`;
    } else if (contenttypeid === 12) { // 관광지
      img = `${KakaoMap.root}/img/marker/camera.png`;
    } else if (contenttypeid === 25) { // 코스여행
      img = `${KakaoMap.root}/img/marker/bus.png`;
    } else if (contenttypeid === 28) { // 레포츠
      img = `${KakaoMap.root}/img/marker/surfing.png`;
    } else if (contenttypeid === 32) { // 숙박
      img = `${KakaoMap.root}/img/marker/hotel.png`;
    } else if (contenttypeid === 38) { // 쇼핑
      img = `${KakaoMap.root}/img/marker/shopping.png`;
    } else if (contenttypeid === 39) { // 음식
      img = `${KakaoMap.root}/img/marker/food.png`;
    }

    return new kakao.maps.MarkerImage(img, new kakao.maps.Size(32, 37), {
      offset: new kakao.maps.Point(16, 37), // 아래 끝을 좌표 기준점으로
    });
  }
}


