let accessToken;
const map = sop.map("map");
// marker 목록
const markers = [];
// 경계 목록
const bounds = [];

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

// 주소를 UTM-K좌표로 변환해서 반환: - json의 errCd ==-401에서 access token 확보!!
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
      marker.addTo(map).bindInfoWindow(setInfo(info.title, info.firstimage ? info.firstimage : info.firstimage2, info.tel, info.addr1, info.addr2));
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

// 마커와 경계 초기화
const resetMarker = () => {
  markers.forEach((item) => item.remove());
  markers.length = 0;
  bounds.length = 0;
};


const setInfo = (title,img,tel,address,addr2) => {
  
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

  let result = `
    <div class="marker-info">
      <h2>${title}</h2>
      <hr>
      <img src="${img}" alt="이미지 제공 X" width="200" height="150" />
      <p>주소 : ${address}<br>${addr2}</p>
      <p>Tel : ${tel}</p>
      <form action="${root}/hotplace" method="post">
        <input type="hidden" name="title" value="${title}">
        <button type="submit">추가</button>
      </form>
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