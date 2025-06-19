import camera from '@/assets/img/marker/camera.png'
import concert from '@/assets/img/marker/concert.png'
import bus from '@/assets/img/marker/bus.png'
import surfing from '@/assets/img/marker/surfing.png'
import hotel from '@/assets/img/marker/hotel.png'
import shopping from '@/assets/img/marker/shopping.png'
import food from '@/assets/img/marker/food.png'
import markerDefault from '@/assets/img/marker/marker.png'

const KakaoMap = {
  map: null,
  markers: [],
  bounds: null,
  routeMode: false, // true: 경로탐색, false: 일반지도
  polylines: [],
  isMainView: false,
  clickedHotplaceCallback: null,

  initMap(containerId) {
    const container = document.getElementById(containerId)
    const options = {
      center: new kakao.maps.LatLng(35.2225, 126.8444),
      level: 3,
    }
    this.map = new kakao.maps.Map(container, options)
    this.bounds = new kakao.maps.LatLngBounds()

    window.closeCustomOverlay = this.closeCustomOverlay.bind(this)
  },

  setIsMainView(isMain) {
    this.isMainView = isMain // 외부에서 main view 여부 설정
  },

  setClickedHotplaceCallback(callback) {
    this.clickedHotplaceCallback = callback // 콜백 함수 설정
  },

  drawRouteMarkers(coords) {
    this.resetMarkers()

    coords.forEach((point) => {
      const position = new kakao.maps.LatLng(point.lat, point.lng)

      const markerImage = new kakao.maps.MarkerImage(markerDefault, new kakao.maps.Size(32, 37), {
        offset: new kakao.maps.Point(16, 37),
      })

      const marker = new kakao.maps.Marker({
        position,
        image: markerImage,
      })

      marker.setMap(this.map)
      this.markers.push(marker)
      this.bounds.extend(position)
    })

    if (!this.bounds.isEmpty()) {
      this.map.setBounds(this.bounds)
    }
  },

  drawRouteFromCoords(coords) {
    if (!this.routeMode || !this.map || !coords?.length) return

    console.log('💬 경로 좌표들', coords)

    // 기존 선 제거
    this.polylines.forEach((line) => line.setMap(null))
    this.polylines = []

    const path = coords.map((c) => new kakao.maps.LatLng(c.Ma, c.La))
    const polyline = new kakao.maps.Polyline({
      path,
      strokeWeight: 5,
      strokeColor: '#5fa8d3',
      strokeOpacity: 0.8,
      strokeStyle: 'solid',
    })

    polyline.setMap(this.map)
    this.polylines.push(polyline)

    const bounds = new kakao.maps.LatLngBounds()
    path.forEach((latlng) => bounds.extend(latlng))
    this.map.setBounds(bounds)
  },

  async getCoordsFromKakao(address) {
    const res = await fetch(
      `https://dapi.kakao.com/v2/local/search/address.json?query=${encodeURIComponent(address)}`,
      {
        headers: {
          Authorization: 'KakaoAK 4917d2973df3fb95f3059a8c7e158416',
        },
      },
    )
    const json = await res.json()
    if (json.documents.length > 0) {
      const { x, y } = json.documents[0]
      return { x: parseFloat(x), y: parseFloat(y) }
    } else {
      console.warn('주소를 찾을 수 없습니다:', address)
      return null
    }
  },

  updateMap(infos, type = 'attraction') {
    this.resetMarkers()

    infos.forEach((info) => {
      if (!info.latlng) return

      const lat = parseFloat(info.latlng.y)
      const lng = parseFloat(info.latlng.x)
      const position = new kakao.maps.LatLng(lat, lng)

      const marker = new kakao.maps.Marker({
        map: this.map,
        position,
        image: this.setIcon(info.contenttypeid),
      })

      const content = this.setInfoContent(info, type)

      const overlay = new kakao.maps.CustomOverlay({
        position,
        content,
        yAnchor: 1.2,
      })

      kakao.maps.event.addListener(marker, 'click', () => {
        if (this.isMainView && this.clickedHotplaceCallback) {
          this.clickedHotplaceCallback(info)
        } else {
          if (this.currentOverlay) this.currentOverlay.setMap(null)
          overlay.setMap(this.map)
          this.currentOverlay = overlay
        }
      })

      this.markers.push(marker)
      this.bounds.extend(position)
    })

    if (!this.bounds.isEmpty()) {
      this.map.setBounds(this.bounds)
    }
  },

  resetMarkers() {
    this.markers.forEach((m) => m.setMap(null))
    this.markers = []
    this.bounds = new kakao.maps.LatLngBounds()
  },

  setIcon(contenttypeid) {
    const icons = {
      15: concert,
      12: camera,
      25: bus,
      28: surfing,
      32: hotel,
      38: shopping,
      39: food,
    }
    const iconSrc = icons[contenttypeid] || markerDefault
    return new kakao.maps.MarkerImage(iconSrc, new kakao.maps.Size(32, 37), {
      offset: new kakao.maps.Point(16, 37),
    })
  },

  setInfoContent(info, type) {
    const noImagePath = new URL('@/assets/img/noImage.png', import.meta.url).href
    const img = info.firstimage || info.img || noImagePath
    const tel = info.tel || '공개되지 않는 정보입니다.'
    const addr1 = info.addr1 || '공개되지 않는 정보입니다.'
    const addr2 = info.addr2 || ''

    return `
      <div class="custom-overlay">
        <div class="overlay-title">
          ${info.title}
          <span class="close" onclick="window.closeCustomOverlay()" title="닫기">&times;</span>
        </div>
        <div class="overlay-body">
          <img src="${img}" alt="이미지 없음" onerror="this.onerror=null;this.src='${noImagePath}'" />
          <div class="desc">
            <p><strong>주소:</strong> ${addr1} ${addr2}</p>
            <p><strong>Tel:</strong> ${tel}</p>
            ${
              type === 'plan'
                ? `<button onclick='window.addToPlan(${JSON.stringify(info)})'>추가</button>`
                : ''
            }
          </div>
        </div>
      </div>
    `
  },

  closeCustomOverlay() {
    if (this.currentOverlay) {
      this.currentOverlay.setMap(null)
      this.currentOverlay = null
    }
  },
}



export default KakaoMap
