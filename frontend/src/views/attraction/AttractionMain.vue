<template>
  <div class="attraction-search-page">
    <!-- 히어로 섹션 -->
    <div class="hero-section">
      <div class="hero-container">
        <div class="hero-content">
          <div class="hero-text">
            <h1 class="hero-title">
              <span class="hero-title">관광지 검색</span>
            </h1>
            <p class="hero-subtitle">
              원하는 지역과 카테고리로 완벽한 여행지를 찾아보세요
            </p>
          </div>
          <div class="hero-illustration">
            <div class="floating-card">
              <div class="search-icon">🔍</div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 검색 섹션 -->
    <div class="search-section">
      <div class="search-container">
        <div class="search-card">
          <div class="search-header">
            <h3>검색 조건을 선택해주세요</h3>
            <p>지역, 세부 지역, 카테고리를 선택하여 원하는 관광지를 찾을 수 있습니다</p>
          </div>

          <form class="search-form">
            <div class="form-grid">
              <!-- 지역 선택 -->
              <div class="form-group">
                <label class="form-label">
                  <span class="label-icon">📍</span>
                  지역 선택
                </label>
                <select
                  v-model="selectedSido"
                  class="form-select modern-select"
                  :class="{ 'has-value': selectedSido }"
                >
                  <option value="">지역을 선택하세요</option>
                  <option v-for="sido in sidos" :key="sido.sidoCode" :value="sido.sidoCode">
                    {{ sido.sidoName }}
                  </option>
                </select>
              </div>

              <!-- 세부 지역 선택 -->
              <div class="form-group">
                <label class="form-label">
                  <span class="label-icon">🗺️</span>
                  세부 지역
                </label>
                <select
                  v-model="selectedGugun"
                  class="form-select modern-select"
                  :class="{ 'has-value': selectedGugun, 'disabled': guguns.length === 0 }"
                  :disabled="guguns.length === 0"
                >
                  <option value="">세부 지역을 선택하세요</option>
                  <option v-for="gugun in guguns" :key="gugun.gugunCode" :value="gugun.gugunCode">
                    {{ gugun.gugunName }}
                  </option>
                </select>
              </div>

              <!-- 카테고리 선택 -->
              <div class="form-group">
                <label class="form-label">
                  <span class="label-icon">🏷️</span>
                  카테고리
                </label>
                <select
                  v-model="selectedCategory"
                  class="form-select modern-select"
                  :class="{ 'has-value': selectedCategory }"
                >
                  <option value="">전체 카테고리</option>
                  <option v-for="category in contentTypes" :key="category.contentTypeId" :value="category.contentTypeId">
                    {{ category.contentTypeName }}
                  </option>
                </select>
              </div>
              <!-- 검색 버튼 -->
              <div class="form-group search-button-center">
                <button
                  @click="searchAttraction"
                  class="search-btn"
                  type="button"
                  :disabled="isSearching"
                >
                  <span v-if="!isSearching" class="btn-content">
                    <span class="btn-icon">🔍</span>
                    <span class="btn-text">검색하기</span>
                  </span>
                  <span v-else class="btn-loading">
                    <span class="loading-spinner"></span>
                    검색 중...
                  </span>
                </button>
              </div>
            </div>

            <!-- 검색 팁 -->
            <div class="search-tips">
              <div class="tip-item">
                <span class="tip-icon">💡</span>
                <span class="tip-text">지역을 먼저 선택하면 세부 지역 옵션이 나타납니다</span>
              </div>
              <div class="tip-item">
                <span class="tip-icon">🎯</span>
                <span class="tip-text">카테고리를 선택하지 않으면 모든 관광지가 검색됩니다</span>
              </div>
            </div>
          </form>
        </div>
      </div>
    </div>

    <!-- 지도 섹션 -->
    <div class="map-section">
      <div class="map-container">
        <div class="map-header">
          <h3>검색 결과</h3>
          <div class="map-controls">
            <button class="control-btn" @click="resetMap">
              <span class="control-icon">🔄</span>
              지도 초기화
            </button>
          </div>
        </div>

        <div class="map-wrapper">
          <div id="map" class="kakao-map"></div>
          <div class="map-overlay" v-if="!hasSearchResults">
            <div class="overlay-content">
              <div class="overlay-icon">🗺️</div>
              <h4>검색 결과가 없습니다</h4>
              <p>다른 조건으로 검색해보세요</p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 결과 통계 -->
    <div class="stats-section" v-if="searchStats.total > 0">
      <div class="stats-container">
        <div class="stat-card">
          <div class="stat-number">{{ searchStats.total }}</div>
          <div class="stat-label">총 검색 결과</div>
        </div>
        <div class="stat-card">
          <div class="stat-number">{{ searchStats.region }}</div>
          <div class="stat-label">검색 지역</div>
        </div>
        <div class="stat-card">
          <div class="stat-number">{{ searchStats.category }}</div>
          <div class="stat-label">선택된 카테고리</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
import KakaoMap from '@/utils/kakaoMap.js'
import '@/assets/css/kakao-infowindow.css'
import { fetchGuguns, fetchSidos, fetchContentTypes, fetchAttractions } from '@/lib/api/attractionAPI.js'

const sidos = ref([])
const guguns = ref([])
const contentTypes = ref([])

const selectedSido = ref('')
const selectedGugun = ref('')
const selectedCategory = ref('')

const isSearching = ref(false)
const hasSearchResults = ref(true)
const searchStats = ref({
  total: 0,
  region: '',
  category: ''
})

const loadGuguns = async (sidoCode) => {
  try {
    guguns.value = await fetchGuguns(sidoCode)
    selectedGugun.value = '' // 지역 변경 시 세부 지역 초기화
  } catch(error) {
    console.error("데이터 불러오기 실패:", error)
    showNotification("데이터를 불러오는 데 실패했습니다.", 'error')
  }
}

const searchAttraction = async () => {
  if (!selectedSido.value) {
    showNotification("지역을 먼저 선택해주세요.", 'warning')
    return
  }

  isSearching.value = true

  try {
    const result = await fetchAttractions({
      region: selectedSido.value,
      subregion: selectedGugun.value,
      category: selectedCategory.value,
    })

    if (result?.length > 0) {
      const mappedData = result.map(item => ({
        ...item,
        latlng: { x: item.mapx, y: item.mapy },
      }))

      window.KakaoMap.updateMap(mappedData, 'attraction')
      hasSearchResults.value = true

      // 통계 업데이트
      const selectedSidoName = sidos.value.find(s => s.sidoCode === selectedSido.value)?.sidoName || ''
      const selectedCategoryName = selectedCategory.value
        ? contentTypes.value.find(c => c.contentTypeId === selectedCategory.value)?.contentTypeName || ''
        : '전체'

      searchStats.value = {
        total: result.length,
        region: selectedSidoName,
        category: selectedCategoryName
      }

      showNotification(`${result.length}개의 관광지를 찾았습니다!`, 'success')
    } else {
      hasSearchResults.value = false
      searchStats.value.total = 0
      showNotification("검색 결과가 없습니다. 다른 조건으로 검색해보세요.", 'info')
    }
  } catch (error) {
    console.error("검색 오류:", error)
    showNotification("검색 중 오류가 발생했습니다.", 'error')
    hasSearchResults.value = false
  } finally {
    isSearching.value = false
  }
}

const resetMap = () => {
  if (window.KakaoMap) {
    window.KakaoMap.initMap("map")
    hasSearchResults.value = true
    searchStats.value.total = 0
  }
}

const centerMap = () => {
  if (navigator.geolocation) {
    navigator.geolocation.getCurrentPosition(
      (position) => {
        const { latitude, longitude } = position.coords
        if (window.KakaoMap) {
          window.KakaoMap.moveToLocation(latitude, longitude)
        }
      },
      () => {
        showNotification("위치 정보를 가져올 수 없습니다.", 'warning')
      }
    )
  }
}

const showNotification = (message, type = 'info') => {
  // 간단한 알림 대신 더 나은 토스트 알림을 구현할 수 있습니다
  alert(message)
}

watch(selectedSido, async (newVal) => {
  if (!newVal) {
    guguns.value = []
    return
  }
  await loadGuguns(newVal)
})

onMounted(async () => {
  window.KakaoMap = KakaoMap
  KakaoMap.setIsMainView(false)

  try {
    sidos.value = await fetchSidos()
    contentTypes.value = await fetchContentTypes()
  } catch (e) {
    console.error("초기 데이터 로딩 실패:", e)
    showNotification("초기 데이터를 불러오는 데 실패했습니다.", 'error')
  }

  // 지도 초기화
  kakao.maps.load(async () => {
    window.KakaoMap.initMap("map")
    const coord = await window.KakaoMap.getCoordsFromKakao("광주 광산구 하남산단6번로 133")
    if (coord) {
      window.KakaoMap.updateMap([
        {
          title: "SSAFY 광주 캠퍼스",
          latlng: coord,
          img: "/assets/img/campus.png",
          tel: "02-3429-5100",
          addr1: "광주 광산구 하남산단6번로 133"
        }
      ], "attraction")
    }
  })
})
</script>

<style scoped>
.attraction-search-page {
  min-height: 100vh;
  background: linear-gradient(to bottom, #f4f1fb 0%, #e0fdfd 100%);
}

/* 히어로 섹션 */
.hero-section {
  background: linear-gradient(135deg, #f0f9e8 0%, #d0eabf 100%);
  color: #4a6741;
  padding: 3rem 0 2rem;
  position: relative;
  overflow: hidden;
}

.hero-section::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 20"><defs><radialGradient id="a" cx="50%" cy="50%"><stop offset="0%" stop-color="rgba(255,255,255,.1)"/><stop offset="100%" stop-color="rgba(255,255,255,0)"/></radialGradient></defs><circle fill="url(%23a)" cx="50" cy="10" r="10"/></svg>') repeat;
  opacity: 0.1;
}

.hero-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 2rem;
  position: relative;
  z-index: 1;
}

.hero-content {
  display: flex;
  align-items: center;
  gap: 3rem;
}

.hero-text {
  flex: 1;
}

.hero-title {
  font-size: 2rem;
  font-weight: 800;
  margin-bottom: 1rem;
  line-height: 1.2;
  color: #4a6741;
}

.gradient-text {
  background: linear-gradient(135deg, #fff 0%, #e0e7ff 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.hero-subtitle {
  font-size: 1rem;
  margin-bottom: 0;
  line-height: 1.6;
}

.hero-illustration {
  flex-shrink: 0;
}

.floating-card {
  width: 120px;
  height: 120px;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid rgba(255, 255, 255, 0.2);
  animation: float 3s ease-in-out infinite;
}

.search-icon {
  font-size: 3rem;
}

@keyframes float {
  0%, 100% { transform: translateY(0px); }
  50% { transform: translateY(-10px); }
}

/* 검색 섹션 */
.search-section {
  padding: 2rem 0;
  position: relative;
}

.search-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 2rem;
}

.search-card {
  background: white;
  border-radius: 20px;
  padding: 2.5rem;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  position: relative;
  overflow: hidden;
}

.search-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #aed581 0%, #9ccc65 100%);
}

.search-header {
  text-align: center;
  margin-bottom: 2rem;
}

.search-header h3 {
  font-size: 1.75rem;
  font-weight: 700;
  color: #1f2937;
  margin-bottom: 0.5rem;
}

.search-header p {
  color: #6b7280;
  font-size: 1rem;
  margin: 0;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 1.5rem;
  margin-bottom: 2rem;
}

.search-button-center .search-btn {
  width: fit-content;
  min-width: 160px;
}

.form-group {
  display: flex;
  flex-direction: column;
}

.search-button-center {
  grid-column: 1 / -1;
  display: flex;
  justify-content: center;
  align-items: center;
}

.form-label {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-weight: 600;
  color: #374151;
  margin-bottom: 0.5rem;
  font-size: 0.9rem;
}

.label-icon {
  font-size: 1rem;
}

.modern-select {
  padding: 0.875rem 1rem;
  border: 2px solid #e5e7eb;
  border-radius: 12px;
  font-size: 1rem;
  background: white;
  transition: all 0.3s ease;
  appearance: none;
  background-image: url("data:image/svg+xml,%3csvg xmlns='http://www.w3.org/2000/svg' fill='none' viewBox='0 0 20 20'%3e%3cpath stroke='%236b7280' stroke-linecap='round' stroke-linejoin='round' stroke-width='1.5' d='M6 8l4 4 4-4'/%3e%3c/svg%3e");
  background-position: right 0.75rem center;
  background-repeat: no-repeat;
  background-size: 1.5em 1.5em;
  padding-right: 2.5rem;
}

.modern-select:focus {
  outline: none;
  border-color: #9ccc65;
  box-shadow: 0 0 0 3px rgba(156, 204, 101, 0.2);
}

.modern-select.has-value {
  border-color: #7cb342;
  background-color: #f1f8e9;
}

.modern-select.disabled {
  background-color: #f9fafb;
  color: #9ca3af;
  cursor: not-allowed;
}

.search-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  padding: 0.875rem 1.5rem;
  background: linear-gradient(135deg, #aed581, #7cb342);
  box-shadow: 0 4px 12px rgba(124, 179, 66, 0.3);
  color: white;
  border: none;
  border-radius: 12px;
  font-weight: 600;
  font-size: 1rem;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.search-btn:hover:not(:disabled) {
  background: linear-gradient(135deg, #689f38, #558b2f);
  box-shadow: 0 6px 18px rgba(104, 159, 56, 0.4);
  transform: translateY(-2px);
}

.search-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
  transform: none;
}

.btn-content,
.btn-loading {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
}

.loading-spinner {
  width: 1rem;
  height: 1rem;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top: 2px solid white;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.search-tips {
  display: flex;
  gap: 2rem;
  justify-content: center;
  flex-wrap: wrap;
  padding-top: 1.5rem;
  border-top: 1px solid #e5e7eb;
}

.tip-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  color: #6b7280;
  font-size: 0.875rem;
}

/* 지도 섹션 */
.map-section {
  padding: 3rem 0;
}

.map-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 2rem;
}

.map-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
}

.map-header h3 {
  font-size: 1.5rem;
  font-weight: 700;
  color: #1f2937;
  margin: 0;
}

.map-controls {
  display: flex;
  gap: 0.75rem;
}

.control-btn {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.5rem 1rem;
  background: white;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  color: #374151;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 0.875rem;
}

.control-btn:hover {
  background: #f9fafb;
  border-color: #9ccc65;
  color: #7cb342;
}

.map-wrapper {
  position: relative;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.1);
}

.kakao-map {
  width: 100%;
  height: 600px;
}

.map-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.95);
  display: flex;
  align-items: center;
  justify-content: center;
  backdrop-filter: blur(5px);
}

.overlay-content {
  text-align: center;
  color: #6b7280;
}

.overlay-icon {
  font-size: 4rem;
  margin-bottom: 1rem;
}

.overlay-content h4 {
  font-size: 1.25rem;
  font-weight: 600;
  margin-bottom: 0.5rem;
  color: #374151;
}

/* 통계 섹션 */
.stats-section {
  padding: 2rem 0 3rem;
}

.stats-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 0 2rem;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1.5rem;
}

.stat-card {
  background: white;
  padding: 2rem;
  border-radius: 16px;
  text-align: center;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.stat-number {
  font-size: 2rem;
  font-weight: 800;
  color: #689f38;
  margin-bottom: 0.5rem;
}

.stat-label {
  color: #6b7280;
  font-weight: 500;
}

/* 반응형 디자인 */
@media (max-width: 768px) {
  .hero-title {
    font-size: 2rem;
  }

  .hero-content {
    flex-direction: column;
    text-align: center;
    gap: 2rem;
  }

  .search-card {
    padding: 1.5rem;
  }

  .form-grid {
    grid-template-columns: 1fr;
    gap: 1rem;
  }

  .search-tips {
    flex-direction: column;
    gap: 0.75rem;
  }

  .map-header {
    flex-direction: column;
    gap: 1rem;
    align-items: stretch;
  }

  .map-controls {
    justify-content: center;
  }

  .kakao-map {
    height: 400px;
  }
}

@media (max-width: 480px) {
  .hero-section {
    padding: 2rem 0 1rem;
  }

  .hero-container,
  .search-container,
  .map-container {
    padding: 0 1rem;
  }

  .floating-card {
    width: 80px;
    height: 80px;
  }

  .search-icon {
    font-size: 2rem;
  }
}
</style>
