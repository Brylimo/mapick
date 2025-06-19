<template>
  <div>
    <!-- 인트로 카드 뉴스 -->
    <section class="intro-section animate-on-scroll visible">
      <div class="intro-text">
        <h2>여행의 모든 것,<br />Mapick과 함께!</h2>
        <p>다양한 여행지 정보 제공과<br />커뮤니티 활동을 통해 여행을 더욱 즐겁게!</p>
        <div class="intro-decoration">
          <div class="floating-circle circle-1"></div>
          <div class="floating-circle circle-2"></div>
          <div class="floating-circle circle-3"></div>
        </div>
      </div>
      <div class="intro-graphic">
        <img :src="introImage" alt="여행 일러스트" />
      </div>
    </section>

    <section class="bottom-section">
      <!-- 인기 커뮤니티 -->
      <div class="community-section animate-on-scroll">
        <h3><span class="emoji pulse">🔥</span> 인기 커뮤니티</h3>
        <div class="community-cards">
          <div
            class="community-card"
            v-for="(item, i) in popularCommunities"
            :key="item.id"
            :class="`fade-in-${i + 1}`"
            @click='() => clickCommunityCard(item.id)'
          >
            <img
              :src="getRandomImage()"
              alt="썸네일"
              @error="handleImageError"
            />
            <div class="card-content">
              <strong>{{ item.title }}</strong>
              <p>{{ item.subtitle }}</p>
            </div>
            <div class="tag-list">
              <span
                v-for="tag in item.tags"
                :key="tag"
                class="tag"
              >
                {{ tag }}
              </span>
            </div>
            <div class="card-hover-effect"></div>
          </div>
        </div>
      </div>

      <!-- 핫플 지도 -->
      <div class="hotplace-section animate-on-scroll">
        <h3><span class="emoji bounce">📍</span> Hot Place</h3>
        <div id="hotplace-map" class="hotplace-map"></div>
        <div class="clicked-info slide-up">
          <button class="nav-button prev" @click="goPrevHotplace">&#10094;</button>

          <div class="info-box">
            <h3>{{ currentHotplaceIdx + 1 }}등 {{ currentHotplace?.title }} 🏅</h3>
            <p>주소: {{ currentHotplace?.addr1 || '준비중입니다.' }}</p>
            <p>전화번호: {{ currentHotplace?.tel || '준비중입니다.' }}</p>
          </div>

          <button class="nav-button next" @click="goNextHotplace">&#10095;</button>
        </div>
      </div>
    </section>

    <!-- 챗봇 -->
    <button class="ai-chat-btn" @click="goToAiChat">
      <img src="@/assets/img/ai-bot.png" alt="AI 채팅" />
      <div class="chat-ripple"></div>
    </button>
  </div>
</template>

<script setup>
import { onMounted, ref, onActivated } from 'vue'
import { useRouter } from 'vue-router'
import { getHotplaces } from '@/lib/api/hotplaceAPI'
import { getPopularCommunities } from '@/lib/api/communityAPI'
import KakaoMap from '@/utils/kakaoMap'
import introImage from '@/assets/img/intro.png'
import { getTags } from '@/lib/api/tagAPI'

import { Map, View } from 'ol'
import { fromLonLat } from 'ol/proj'
import TileLayer from 'ol/layer/Tile'
import XYZ from 'ol/source/XYZ'
import { defaults as defaultControls } from 'ol/control'
import Overlay from 'ol/Overlay'
import { useVWorldMap } from '@/composables/useVWorldMap'

import * as communityAttractionAPI from '../lib/api/communityAttractionAPI'

const {
  setMap,
  pinRedMarker,
  moveTo,
  controlLayerVisibility,
  removeLayerByProperty,
  addZoneMarkers,
  addHeatMapLayer,
  showHeatMapLayer
} = useVWorldMap()

const aKey = 'D5152B22-77A2-311E-B514-EFD1422CD4ED'

const router = useRouter()
const currentHotplace = ref(null)
const popularSpots = ref([])
const currentHotplaceIdx = ref(0)

const goToAiChat = () => router.push({ name: 'AiChat' })

const popularCommunities = ref([])

// 랜덤 이미지 배열 - 여행 관련 무료 이미지들
const travelImages = [
  'https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=400&h=300&fit=crop&crop=center',
  'https://images.unsplash.com/photo-1551632811-561732d1e306?w=400&h=300&fit=crop&crop=center',
  'https://images.unsplash.com/photo-1469474968028-56623f02e42e?w=400&h=300&fit=crop&crop=center',
  'https://images.unsplash.com/photo-1476514525535-07fb3b4ae5f1?w=400&h=300&fit=crop&crop=center',
  'https://images.unsplash.com/photo-1488646953014-85cb44e25828?w=400&h=300&fit=crop&crop=center',
  'https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=400&h=300&fit=crop&crop=center',
  'https://images.unsplash.com/photo-1524492412937-b28074a5d7da?w=400&h=300&fit=crop&crop=center',
  'https://images.unsplash.com/photo-1530521954074-e64f6810b32d?w=400&h=300&fit=crop&crop=center',
  'https://images.unsplash.com/photo-1571896349842-33c89424de2d?w=400&h=300&fit=crop&crop=center',
  'https://images.unsplash.com/photo-1527004013197-933c4bb611b3?w=400&h=300&fit=crop&crop=center',
  'https://images.unsplash.com/photo-1559827260-dc66d52bef19?w=400&h=300&fit=crop&crop=center',
  'https://images.unsplash.com/photo-1578662996442-48f60103fc96?w=400&h=300&fit=crop&crop=center',
  'https://images.unsplash.com/photo-1502780402662-acc01917957e?w=400&h=300&fit=crop&crop=center',
  'https://images.unsplash.com/photo-1539650116574-75c0c6d73c6e?w=400&h=300&fit=crop&crop=center',
  'https://images.unsplash.com/photo-1516026672322-bc52d61a55d5?w=400&h=300&fit=crop&crop=center'
]

// 기본 이미지 (이미지 로드 실패시 사용)
const defaultImage = 'https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=400&h=300&fit=crop&crop=center'

// 랜덤 이미지 선택 함수
const getRandomImage = () => {
  const randomIndex = Math.floor(Math.random() * travelImages.length)
  return travelImages[randomIndex]
}

// 이미지 로드 실패시 처리
const handleImageError = (event) => {
  event.target.src = defaultImage
}

onMounted(async () => {

  const result = await getPopularCommunities()
  console.log('커뮤니티 API 응답:', result.data)
  const list = Array.isArray(result.data) ? result.data : []
  popularCommunities.value = await Promise.all(
    list.map(async item => {
      let tags = []
      try {
        const tagRes = await getTags({ communityId: item.communityId })
        console.log(`커뮤니티 ${item.communityId} 태그:`, tagRes)
        tags = tagRes.map(t => t.tagName || t.name)
      } catch {
        console.warn(`태그 로드 실패: communityId=${item.communityId}`)
      }
      return {
        id:       item.communityId,
        title:    item.communityName,
        subtitle: item.desc,
        tags
      }
    })
  )

  const baseLayer = new TileLayer({
    source: new XYZ({
      url: 'https://xdworld.vworld.kr/2d/Base/service/{z}/{x}/{y}.png',
      crossOrigin: 'anonymous',
    }),
  })

  const satelliteLayer = new TileLayer({
    source: new XYZ({
      url: `http://api.vworld.kr/req/wmts/1.0.0/${aKey}/Satellite/{z}/{y}/{x}.jpeg`,
    }),
    visible: false,
    title: 'SatelliteMap',
  })

  const hybridLayer = new TileLayer({
    source: new XYZ({
      url: `http://api.vworld.kr/req/wmts/1.0.0/${aKey}/Hybrid/{z}/{y}/{x}.png`,
    }),
    visible: false,
    title: 'HybridMap',
  })

  const mapInstance = new Map({
    target: 'hotplace-map',
    layers: [baseLayer, satelliteLayer, hybridLayer],
    view: new View({
      projection: 'EPSG:3857',
      center: fromLonLat([126.4989967, 33.4879725]),
      zoom: 6,
      minZoom: 6,
      maxZoom: 20,
    }),
    controls: defaultControls({ zoom: false }),
  })

  setMap(mapInstance)

  //moveTo(128.1172, 36.4163, 6.8)

  const res = await communityAttractionAPI.getPopularAttractionList()

  showHeatMapLayer(res.data)
  popularSpots.value = res.data

  currentHotplace.value = res.data[0]
  currentHotplaceIdx.value = 0

  console.log(popularSpots)

  moveTo(currentHotplace.value.longitude, currentHotplace.value.latitude)
})

const goPrevHotplace = () => { 
  if (currentHotplaceIdx.value > 0) { 
    currentHotplace.value = popularSpots.value[currentHotplaceIdx.value - 1]
    currentHotplaceIdx.value = currentHotplaceIdx.value - 1

    moveTo(currentHotplace.value.longitude, currentHotplace.value.latitude)
  }
}

const goNextHotplace = () => { 
  if (currentHotplaceIdx.value < popularSpots.value.length - 1) {
    currentHotplace.value = popularSpots.value[currentHotplaceIdx.value + 1]
    currentHotplaceIdx.value = currentHotplaceIdx.value + 1

    moveTo(currentHotplace.value.longitude, currentHotplace.value.latitude)
  }
}

const clickCommunityCard = (id) => { 
  router.push(`community/${id}/community-main`)
}

</script>

<style scoped>
/* 전체 애니메이션 및 기본 설정 */
* {
  box-sizing: border-box;
}

/* 키프레임 애니메이션 정의 */
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes float {
  0%, 100% { transform: translateY(0px); }
  50% { transform: translateY(-10px); }
}

@keyframes pulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.1); }
}

@keyframes bounce {
  0%, 20%, 50%, 80%, 100% { transform: translateY(0); }
  40% { transform: translateY(-5px); }
  60% { transform: translateY(-3px); }
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes ripple {
  0% {
    transform: scale(0);
    opacity: 1;
  }
  100% {
    transform: scale(4);
    opacity: 0;
  }
}

@keyframes gradient {
  0% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
  100% { background-position: 0% 50%; }
}

/* 인트로 섹션 */
.intro-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  max-width: 1100px;
  margin: 3rem auto;
  padding: 3rem;
  background: linear-gradient(135deg, #e8f5e8, #f0fff0, #e6ffe6);
  background-size: 400% 400%;
  animation: gradient 8s ease infinite;
  border-radius: 25px;
  box-shadow: 0 20px 40px rgba(76, 175, 80, 0.1);
  position: relative;
  overflow: hidden;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(129, 199, 132, 0.2);
}

.intro-section::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle, rgba(76, 175, 80, 0.05) 0%, transparent 70%);
  animation: float 6s ease-in-out infinite;
}

.intro-text {
  flex: 1;
  padding-right: 2rem;
  position: relative;
  z-index: 2;
}

.intro-text h2 {
  font-size: 2.5rem;
  line-height: 1.4;
  font-weight: 800;
  background: linear-gradient(135deg, #2e7d32, #4caf50, #66bb6a);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin-bottom: 1rem;
  animation: fadeInUp 1s ease-out;
}

.intro-text p {
  margin-top: 1rem;
  font-size: 1.1rem;
  color: #1b5e20;
  font-weight: 500;
  line-height: 1.6;
  animation: fadeInUp 1s ease-out 0.2s both;
}

.intro-decoration {
  position: absolute;
  top: 0;
  right: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
}

.floating-circle {
  position: absolute;
  border-radius: 50%;
  background: linear-gradient(135deg, rgba(76, 175, 80, 0.1), rgba(129, 199, 132, 0.1));
  animation: float 4s ease-in-out infinite;
}

.circle-1 {
  width: 60px;
  height: 60px;
  top: 20%;
  right: 20%;
  animation-delay: 0s;
}

.circle-2 {
  width: 40px;
  height: 40px;
  top: 60%;
  right: 10%;
  animation-delay: 1s;
}

.circle-3 {
  width: 30px;
  height: 30px;
  top: 40%;
  right: 40%;
  animation-delay: 2s;
}

.intro-graphic {
  flex: 1;
  text-align: right;
  position: relative;
  z-index: 2;
}

.intro-graphic img {
  width: 80%;
  max-height: 250px;
  object-fit: contain;
  filter: drop-shadow(0 10px 20px rgba(76, 175, 80, 0.2));
  animation: float 3s ease-in-out infinite;
}

/* 하단 섹션 */
.bottom-section {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 3rem;
  max-width: 1100px;
  margin: 3rem auto;
  padding: 0 1rem;
  flex-wrap: wrap;
}

.community-section, .hotplace-section {
  flex: 1;
  min-width: 320px;
  animation: fadeInUp 0.8s ease-out 0.4s both;
}

.community-section h3, .hotplace-section h3 {
  font-size: 1.5rem;
  font-weight: 700;
  color: #2e7d32;
  margin-bottom: 1.5rem;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.emoji {
  font-size: 1.2em;
}

.pulse {
  animation: pulse 2s infinite;
}

.bounce {
  animation: bounce 2s infinite;
}

.community-cards {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
  margin-top: 1rem;
}

.community-card {
  background: linear-gradient(135deg, #ffffff, #f8fff8);
  border-radius: 20px;
  padding: 1.5rem;
  box-shadow: 0 8px 25px rgba(76, 175, 80, 0.08);
  display: flex;
  gap: 1rem;
  align-items: center;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
  border: 1px solid rgba(129, 199, 132, 0.1);
  cursor: pointer;
}

.community-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(76, 175, 80, 0.1), transparent);
  transition: left 0.5s;
}

.community-card:hover::before {
  left: 100%;
}

.community-card:hover {
  transform: translateY(-8px) scale(1.02);
  box-shadow: 0 15px 35px rgba(76, 175, 80, 0.15);
  border-color: rgba(76, 175, 80, 0.3);
}

.community-card img {
  width: 90px;
  height: 90px;
  object-fit: cover;
  border-radius: 15px;
  transition: transform 0.3s ease;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
}

.community-card:hover img {
  transform: scale(1.1);
}

.card-content {
  flex: 1;
}

.card-content strong {
  font-size: 1.1rem;
  color: #1b5e20;
  font-weight: 600;
}

.card-content p {
  margin: 0.5rem 0;
  color: #2e7d32;
  font-size: 0.9rem;
  line-height: 1.4;
}

.tag-list {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
  margin-top: 0.8rem;
}

.tag {
  display: inline-block;
  font-size: 0.8rem;
  background: linear-gradient(135deg, #4caf50, #66bb6a);
  color: white;
  padding: 0.4rem 0.8rem;
  border-radius: 999px;
  font-weight: 500;
  box-shadow: 0 2px 8px rgba(76, 175, 80, 0.3);
  transition: all 0.3s ease;
}

.community-card:hover .tag {
  transform: scale(1.05);
  box-shadow: 0 4px 12px rgba(76, 175, 80, 0.4);
}

/* 카드별 지연 애니메이션 */
.fade-in-1 { animation: fadeInUp 0.6s ease-out 0.1s both; }
.fade-in-2 { animation: fadeInUp 0.6s ease-out 0.3s both; }
.fade-in-3 { animation: fadeInUp 0.6s ease-out 0.5s both; }

/* 핫플레이스 지도 */
.hotplace-map {
  width: 100%;
  height: 350px;
  margin-top: 1rem;
  border-radius: 20px;
  border: 2px solid rgba(129, 199, 132, 0.2);
  box-shadow: 0 10px 30px rgba(76, 175, 80, 0.1);
  overflow: hidden;
  transition: all 0.3s ease;
}

.hotplace-map:hover {
  border-color: rgba(76, 175, 80, 0.4);
  box-shadow: 0 15px 40px rgba(76, 175, 80, 0.15);
}

/* 클릭된 정보 박스 */
.clicked-info {
  margin-top: 20px;
  background: linear-gradient(135deg, #e8f5e8, #f1f8e9);
  padding: 25px 50px; /* 좌우 여백 증가 */
  border-radius: 18px;
  box-shadow: 0 8px 25px rgba(76, 175, 80, 0.12);
  border: 1px solid rgba(129, 199, 132, 0.2);
  position: relative;
  overflow: hidden;
}

.clicked-info .info-box {
  font-size: 1.1rem;
  color: #1b5e20;
}

.clicked-info h3 {
  font-weight: 700;
  font-size: 1.3rem;
  color: #2e7d32;
  margin-bottom: 1rem;
}

.clicked-info p {
  margin-top: 0.8rem;
  color: #388e3c;
  line-height: 1.5;
}

/* 버튼 스타일 */
.nav-button {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  width: 36px;
  height: 36px;
  border-radius: 50%;
  border: none;
  background: #a5d6a7;
  color: white;
  font-size: 1.2rem;
  cursor: pointer;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  transition: background 0.3s ease;
  z-index: 2;
}

.nav-button:hover {
  background: #81c784;
}

.nav-button.prev {
  left: 10px;
}

.nav-button.next {
  right: 10px;
}


/* AI 챗봇 버튼 */
.ai-chat-btn {
  position: fixed !important;
  bottom: 30px !important;
  right: 30px !important;
  left: auto !important; /* 혹시 left 값이 설정되어 있다면 초기화 */
  width: 70px;
  height: 70px;
  background: linear-gradient(135deg, #66bb6a, #4caf50);
  border: none;
  border-radius: 50%;
  box-shadow: 0 8px 25px rgba(76, 175, 80, 0.3);
  padding: 8px;
  z-index: 9999; /* z-index를 더 높게 설정 */
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
}

.ai-chat-btn::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  width: 0;
  height: 0;
  background: rgba(255, 255, 255, 0.3);
  border-radius: 50%;
  transform: translate(-50%, -50%);
  transition: all 0.3s ease;
}

.ai-chat-btn:hover::before {
  width: 100%;
  height: 100%;
}

.ai-chat-btn img {
  width: 45px; /* 이미지 크기를 명확히 지정 */
  height: 45px;
  object-fit: contain;
  position: relative;
  z-index: 2;
  filter: brightness(0) invert(1);
}

.ai-chat-btn:hover {
  transform: scale(1.15);
  box-shadow: 0 12px 35px rgba(76, 175, 80, 0.4);
}

.ai-chat-btn:active {
  transform: scale(1.05);
}

.chat-ripple {
  position: absolute;
  top: 50%;
  left: 50%;
  width: 20px;
  height: 20px;
  border: 2px solid rgba(255, 255, 255, 0.6);
  border-radius: 50%;
  transform: translate(-50%, -50%);
  animation: ripple 2s infinite;
}


/* 반응형 디자인 */
@media (max-width: 768px) {
  .intro-section {
    flex-direction: column;
    text-align: center;
    padding: 2rem;
  }

  .intro-text {
    padding-right: 0;
    margin-bottom: 2rem;
  }

  .intro-text h2 {
    font-size: 2rem;
  }

  .bottom-section {
    flex-direction: column;
    gap: 2rem;
  }

  .community-card {
    padding: 1rem;
  }

  .hotplace-map {
    height: 250px;
  }

  .ai-chat-btn {
    width: 65px !important;
    height: 65px !important;
    bottom: 25px !important;
    right: 25px !important;
    left: auto !important;
  }

  .ai-chat-btn img {
    width: 40px;
    height: 40px;
  }
}



@media (max-width: 480px) {
  .ai-chat-btn {
    width: 60px !important;
    height: 60px !important;
    bottom: 20px !important;
    right: 20px !important;
    left: auto !important;
  }

  .ai-chat-btn img {
    width: 35px;
    height: 35px;
  }
}


/* 스크롤 애니메이션을 위한 추가 클래스 */
.animate-on-scroll {
  opacity: 0;
  transform: translateY(50px);
  transition: all 0.6s ease-out;
}

.animate-on-scroll.visible {
  opacity: 1;
  transform: translateY(0);
}
</style>
