<template>
  <div class="container mt-5">
    <div class="title-section">
      <h2 class="page-title">여행 계획</h2>
      <div class="title-decoration"></div>
    </div>

    <form class="search-form mb-4">
      <div class="row g-2">
        <div class="col-md-6">
          <input v-model="keyword" class="form-control search-input" type="text" placeholder="장소를 입력해주세요..." />
        </div>
        <div class="col-md-2">
          <button class="btn search-btn w-100" type="button" @click="searchKeyword">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <circle cx="11" cy="11" r="8"></circle>
              <path d="m21 21-4.35-4.35"></path>
            </svg>
            검색
          </button>
        </div>
      </div>
    </form>

    <div id="map" class="map-container">
      <div id="searchRoute" @click="navigation">
        <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <polygon points="3 11 22 2 13 21 11 13 3 11"></polygon>
        </svg>
      </div>
      <div id="mapOverlayBox">
        <div class="overlay-header">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"></path>
            <circle cx="12" cy="10" r="3"></circle>
          </svg>
          <strong>계획 리스트</strong>
        </div>
        <div id="mapOverlayList">
          <div v-for="item in planItems" :key="item.ppid" class="mapOverlayItem">
            <button class="remove-btn" @click="removeAttraction(item.ppid)">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <line x1="18" y1="6" x2="6" y2="18"></line>
                <line x1="6" y1="6" x2="18" y2="18"></line>
              </svg>
            </button>
            <span class="attraction-name">{{ item.attractionName }}</span>
          </div>
        </div>
        <button class="btn timeline-btn w-100" @click="goToTimeline">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M8 6h13"></path>
            <path d="M8 12h13"></path>
            <path d="M8 18h13"></path>
            <path d="M3 6h.01"></path>
            <path d="M3 12h.01"></path>
            <path d="M3 18h.01"></path>
          </svg>
          계획 보기
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import KakaoMap from '@/utils/kakaoMap'
import { getPlanAttractions, createPlanAttraction, deletePlanAttraction  } from '@/lib/api/planattractionAPI'
import {fetchAttractionByKeyword} from '@/lib/api/attractionAPI'
import { getPlan, getRoute } from '@/lib/api/planAPI'


const route = useRoute()
const router = useRouter()
const planName = route.query.name
const keyword = ref('')
const planItems = ref([])
const planId = ref(null)

const searchKeyword = async () => {
  try {
    const response = await fetchAttractionByKeyword({ keyword: keyword.value})
    const result = response?.data
    if (result) {
      const mappedData = [{
        ...result,
        latlng: { x: result.mapx, y: result.mapy },
      }]
      KakaoMap.updateMap(mappedData, 'plan')
    }
    keyword.value = ''
  } catch (e) {
    console.error("검색 실패:", e)
  }
}

const removeAttraction = async (ppid) => {
  try {
    const res = await deletePlanAttraction(ppid)
    if (res.status === 200) {
      planItems.value = planItems.value.filter((item) => item.ppid !== ppid)
      console.log("삭제 성공")
    }
  } catch (e) {
    console.error("삭제 실패:", e)
    alert("여행지 삭제에 실패했습니다.")
  }
}

const goToTimeline = () => {
  router.push({ path: '/plan/timeline', query: { name: planName } })
}

onMounted(async () => {
  window.KakaoMap = KakaoMap
  KakaoMap.setIsMainView(false);
    window.addToPlan = async (info) => {
    if (!planId.value) return
    if (confirm(`${info.title} 을(를) 추가하시겠습니까?`)) {
      try {
        const response = await createPlanAttraction(planId.value, info)
        planItems.value.push({
          ppid: response.data.ppid,
          idx: response.data.idx,
          planid: response.data.planid,
          attractionid: response.data.attractionid,
          attractionName: response.data.attractionName,
          exp: response.data.exp || ''
        })

        const result = await getPlanAttractions(planId.value)
        console.log("계획 상세 조회 결과:", result)
        planItems.value = Array.isArray(result) ? result : []
      } catch (e) {
        console.error('계획 추가 실패:', e)
        alert('계획 추가에 실패했습니다.')
      }
    }

  }

  kakao.maps.load(async () => {
    KakaoMap.routeMode = true
    KakaoMap.initMap('map')
    const coord = await KakaoMap.getCoordsFromKakao('광주 광산구 하남산단6번로 133')
    if (coord) {
      KakaoMap.updateMap([
        {
          title: 'SSAFY 광주 캠퍼스',
          latlng: coord,
          img: '/assets/img/campus.png',
          tel: '02-3429-5100',
          addr1: '광주 광산구 하남산단6번로 133'
        }
      ], 'plan')
    }
  })

  try {
    const detail = await getPlan(planName)
    planId.value = detail?.data?.pid
    if (!planId.value) throw new Error("planid 없음")
    const result = await getPlanAttractions(planId.value)
    console.log("계획 상세 조회 결과:", result)
    planItems.value = Array.isArray(result) ? result : []
  } catch (e) {
    console.error("계획 상세 조회 실패:", e)
  }
})

const navigation = async () => {
  try {
    const res = await getRoute(planName)
    const coords = res.data
    if(coords.length<2){
      alert("2개 이상의 경유지가 필요하며, 시간 설정이 필요합니다.")
      return
    }
    const origin = {
      x: coords[0].lng,
      y: coords[0].lat
    }

    const destination = {
      x: coords.at(-1).lng,
      y: coords.at(-1).lat
    }

    const waypoints = coords.slice(1, -1).map((p, i) => ({
      name: p.attractionName || `경유지${i + 1}`,
      x: p.lng,
      y: p.lat
    }))

    const response = await fetch('https://apis-navi.kakaomobility.com/v1/waypoints/directions', {
      method: 'POST',
      headers: {
        'Authorization': 'KakaoAK 4917d2973df3fb95f3059a8c7e158416', // ⚠ REST API 키 반드시 KakaoAK 붙이기
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        origin,
        destination,
        waypoints,
        priority: 'RECOMMEND'
      })
    })

    const result = await response.json()
    const routeData = result?.routes?.[0]?.sections;
    if (!routeData) {
      console.error("경로 응답 데이터가 예상과 다릅니다:", result);
      alert("경로 데이터가 올바르지 않습니다.");
      return;
    }

    const vertexes = routeData.flatMap(section =>
      section.roads.flatMap(road => road.vertexes)
    )

    const path = []
    for (let i = 0; i < vertexes.length; i += 2) {
      path.push(new kakao.maps.LatLng(vertexes[i + 1], vertexes[i])) // (lat, lng)
    }
    KakaoMap.drawRouteFromCoords(path)
    KakaoMap.drawRouteMarkers(coords)
  } catch (e) {
    console.error('경로 요청 실패:', e)
    alert('길찾기 실패')
  }
}
</script>

<style scoped>
body {
  background: #f6fddc;
  min-height: 100vh;
}

.container {
  max-width: 1200px;
  margin: 2rem auto;
  padding: 2rem;
}

.title-section {
  text-align: center;
  margin-bottom: 2.5rem;
}

.page-title {
  font-size: 2.2rem;
  font-weight: 700;
  color: #3a5a40;
  margin-bottom: 1rem;
  letter-spacing: -0.5px;
  text-shadow: 0 2px 8px rgba(167, 243, 208, 0.2);
}

.title-decoration {
  width: 60px;
  height: 3px;
  background: linear-gradient(90deg, #a7f3d0, #bef264, #4ade80);
  border-radius: 2px;
  margin: 0 auto;
}

.search-form {
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  padding: 1.5rem;
  border: 1px solid rgba(167, 243, 208, 0.3);
  box-shadow: 0 4px 16px rgba(167, 243, 208, 0.1);
}

.search-input {
  border: 2px solid #d1fae5;
  border-radius: 12px;
  padding: 0.8rem 1.2rem;
  font-size: 1rem;
  background: rgba(255, 255, 255, 0.9);
  transition: all 0.3s ease;
  font-weight: 500;

  &:focus {
    border-color: #bef264;
    outline: none;
    box-shadow: 0 0 0 4px rgba(167, 243, 208, 0.2);
    background: white;
  }

  &::placeholder {
    color: #9ca3af;
    font-weight: 400;
  }
}

.search-btn {
  background: #b5e48c;
  border: none;
  color: #3a5a40;
  padding: 0.8rem 1.5rem;
  border-radius: 12px;
  font-weight: 600;
  font-size: 0.95rem;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(167, 243, 208, 0.3);

  &:hover {
    background: linear-gradient(135deg, #bef264, #4ade80, #22c55e);
    transform: translateY(-2px);
    box-shadow: 0 6px 16px rgba(167, 243, 208, 0.4);
    color: #064e3b;
  }
}

.map-container {
  width: 100%;
  height: 700px;
  position: relative;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 8px 32px rgba(167, 243, 208, 0.2);
  border: 2px solid rgba(167, 243, 208, 0.3);
}

#searchRoute {
  position: absolute;
  bottom: 24px;
  right: 24px;
  width: 60px;
  height: 60px;
  background: linear-gradient(135deg, #a7f3d0, #bef264);
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
  z-index: 1000;
  color: #365314;
  border: 3px solid white;
  box-shadow: 0 6px 20px rgba(167, 243, 208, 0.4);
  transition: all 0.3s ease;

  &:hover {
    background: #99d98c;
    transform: translateY(-2px) scale(1.05);
    box-shadow: 0 8px 24px rgba(167, 243, 208, 0.5);
  }
}

#mapOverlayBox {
  position: absolute;
  top: 24px;
  left: 24px;
  min-width: 340px;
  max-width: 400px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(15px);
  border-radius: 20px;
  padding: 1.8rem;
  z-index: 1000;
  border: 2px solid rgba(167, 243, 208, 0.3);
  box-shadow: 0 12px 40px rgba(167, 243, 208, 0.2);
}

.overlay-header {
  display: flex;
  align-items: center;
  gap: 0.8rem;
  margin-bottom: 1.5rem;
  color: #365314;
}

.overlay-header strong {
  font-size: 1.3rem;
  font-weight: 700;
  letter-spacing: -0.3px;
}

.overlay-header svg {
  color: #bef264;
}

#mapOverlayList {
  height: 300px;
  overflow-y: auto;
  margin-bottom: 1.5rem;
  padding-right: 8px;
  scrollbar-width: thin;
  scrollbar-color: #d9f99d #f0fdf4;
}

#mapOverlayList::-webkit-scrollbar {
  width: 6px;
}

#mapOverlayList::-webkit-scrollbar-track {
  background: #f0fdf4;
  border-radius: 10px;
}

#mapOverlayList::-webkit-scrollbar-thumb {
  background: b5e48c;
  border-radius: 10px;
}

.mapOverlayItem {
  display: flex;
  align-items: center;
  gap: 0.8rem;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 14px;
  padding: 1rem;
  margin-bottom: 0.8rem;
  border: 1.5px solid rgba(167, 243, 208, 0.2);
  transition: all 0.3s ease;
  backdrop-filter: blur(5px);

  &:hover {
    background: rgba(240, 253, 244, 0.9);
    border-color: b5e48c;
    transform: translateX(4px);
    box-shadow: 0 4px 12px rgba(167, 243, 208, 0.2);
  }
}

.remove-btn {
  background: linear-gradient(135deg, #fecaca, #f87171);
  border: none;
  color: #dc2626;
  padding: 0.4rem;
  border-radius: 8px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  box-shadow: 0 2px 6px rgba(248, 113, 113, 0.2);

  &:hover {
    background: linear-gradient(135deg, #f87171, #ef4444);
    color: white;
    transform: scale(1.1);
    box-shadow: 0 3px 8px rgba(248, 113, 113, 0.3);
  }
}

.attraction-name {
  font-weight: 600;
  color: #365314;
  font-size: 0.95rem;
  flex: 1;
}

.timeline-btn {
  background: #b5e48c;
  border: none;
  color: #3a5a40;
  padding: 0.9rem 1.5rem;
  border-radius: 14px;
  font-weight: 600;
  font-size: 1rem;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.6rem;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(167, 243, 208, 0.3);

  &:hover {
    background: linear-gradient(135deg, #bef264, #84cc16, #22c55e);
    transform: translateY(-2px);
    box-shadow: 0 6px 16px rgba(167, 243, 208, 0.4);
    color: #064e3b;
  }
}

@media (max-width: 768px) {
  .container {
    margin: 1rem;
    padding: 1.5rem;
  }

  .page-title {
    font-size: 1.8rem;
  }

  .search-form {
    padding: 1.2rem;
  }

  .map-container {
    height: 500px;
    border-radius: 16px;
  }

  #mapOverlayBox {
    min-width: 280px;
    max-width: calc(100vw - 48px);
    padding: 1.5rem;
    top: 16px;
    left: 16px;
  }

  #mapOverlayList {
    height: 200px;
  }

  #searchRoute {
    width: 50px;
    height: 50px;
    bottom: 16px;
    right: 16px;
  }

  .search-btn,
  .timeline-btn {
    padding: 0.7rem 1.2rem;
    font-size: 0.9rem;
  }
}

@media (max-width: 480px) {
  .search-form .row {
    flex-direction: column;
  }

  .search-form .col-md-6,
  .search-form .col-md-2 {
    width: 100%;
    margin-bottom: 0.8rem;
  }
}
</style>
