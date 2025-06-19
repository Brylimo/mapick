<template>
  <CommunityLayout>
    <div class="layer_modal_wrap" :class="{ 'layer-navi-inactive': isInactive }">
      <div class="layer_modal_con">
        <strong class="modalTit">커뮤니티지도</strong>
        <div class="layer-content">
          <!-- 탭 버튼 -->
          <ul class="layer_btn_change" id="pills-tab" role="tablist">
            <li
              :class="[
                'nav-item',
                'custom-nav',
                activeTab === 'regist' ? 'active nav-link' : 'nav-link',
              ]"
              @click="activeTab = 'regist'"
            >
              위치등록
            </li>
            <li
              :class="[
                'nav-item',
                'custom-nav',
                activeTab === 'search' ? 'active nav-link' : 'nav-link',
              ]"
              @click="activeTab = 'search'"
            >
              검색
            </li>
          </ul>

          <!-- 검색 탭 -->
          <div v-if="activeTab === 'search'" id="pills-home" class="layer_Zone pr20">
            <div class="formItem">
              <label for="searchAreaType" class="form-tit mr20 floatLt">검색 종류</label>
              <select id="searchAreaType" class="select-sm floatRt" v-model="searchType">
                <option value="all">전체</option>
                <option value="name">명칭</option>
                <option value="tag">태그</option>
              </select>
            </div>
            <div v-if="searchType == 'name'">
              검색 :
              <input type="text" placeholder="검색어를 입력하세요." v-model="searchKeyword" />
            </div>
            <div v-if="searchType === 'tag'" class="tag-list-wrapper">
              <div
                class="tag-item"
                v-for="(tag, index) in tagList"
                :key="index"
                :class="{ 'tag-active': activeTagIndexes.has(tag.name) }"
                @click="clickTag(tag.name)"
              >
                # {{ tag.name }}
              </div>
            </div>

            <img
              class="zoneInfo-zoneList-list-divisionLine"
              src="/img/cms/common/division_line.png"
              alt="division_line"
            />

            <div class="layer-toggle-group">
              <button
                @click="selectedLayer = 'marker'"
                :class="{ active: selectedLayer === 'marker' }"
              >
                마커
              </button>
              <button
                @click="selectedLayer = 'heatmap'"
                :class="{ active: selectedLayer === 'heatmap' }"
              >
                히트맵
              </button>
              <button
                @click="selectedLayer = 'cluster'"
                :class="{ active: selectedLayer === 'cluster' }"
              >
                클러스터
              </button>
            </div>

            <img
              class="zoneInfo-zoneList-list-divisionLine"
              src="/img/cms/common/division_line.png"
              alt="division_line"
            />

            <button class="cmnBtn search w-100 mt-2 mb-2" @click="searchMap">검색</button>
            <div class="zoneInfo">
              <div class="zoneInfo-zoneList">
                <div class="zoneInfo-zoneList-total">
                  <p>전체 건수</p>
                  <p class="totalZoneCount">{{ totalZoneCount }}</p>
                  <p>건</p>
                </div>

                <img
                  class="zoneInfo-zoneList-list-divisionLine"
                  src="/img/cms/common/division_line.png"
                  alt="division_line"
                />
                <div class="zoneInfo-zoneList-list">
                  <div id="zoneList" v-if="zoneList.length !== 0">
                    <div
                      class="zoneInfo-zoneList-list-item"
                      v-for="(zItem, index) in zoneList"
                      :key="index"
                      @click="
                        clickZoneInfoBlock(zItem.attraction.latitude, zItem.attraction.longitude)
                      "
                    >
                      <!--<input type="hidden" class="location" :value="getLocationString(item)" />-->
                      <img
                        class="zoneInfo-zoneList-list-item-disabled"
                        style="width: 100%; margin-bottom: 20px"
                        :src="zItem.attraction.firstImage1 || '/img/cms/sub/no-img.jpg'"
                        alt="명소 이미지"
                      />

                      <div class="zoneInfo-zoneList-list-item-text">
                        <p class="title">{{ zItem.attraction.title }}</p>
                        <p class="address">{{ zItem.attraction.addr1 }}</p>

                        <div class="tag-container" v-if="zItem.tags?.length">
                          <span v-for="(tag, i) in zItem.tags" :key="i" class="tag"
                            >#{{ tag.name }}</span
                          >
                        </div>
                      </div>

                      <!-- 좋아요 버튼 -->
                      <div class="community-main-actions">
                        <button @click.stop="toggleLike(zItem)" class="like-btn" :class="{ liked: zItem.liked }">
                          <i :class="[zItem.liked ? 'fas' : 'far', 'fa-heart']" style="font-family: 'Font Awesome 6 Free';"></i>
                          <span>{{ zItem.likeCnt }}</span>
                        </button>
                      </div>

                      <!-- 장바구니 버튼 -->
                      <div class="plan-button-container">
                        <button class="plan-button" @click.stop="addToPlan(zItem, index)">
                          🧳 여행 계획에 담기
                        </button>

                        <div v-if="visibleIdx === index" class="plan-tooltip">
                          <div class="tooltip-header">
                            <h4>🧭 여행 계획</h4>
                            <button class="close-btn" @click.stop="closePlanTooltip">×</button>
                          </div>
                          <ul>
                            <li
                              v-for="(item, i) in planList"
                              :key="i"
                              class="plan-item"
                              @click="handlePlanClick(item, zItem)"
                            >
                              {{ i + 1 }}. {{ item.name }}
                            </li>
                          </ul>
                        </div>
                      </div>

                      <div class="zoneInfo-zoneList-list-item-divisionLine"></div>
                    </div>
                  </div>
                  <div class="noSafetyAreaData" v-if="zoneList.length === 0">
                    조회결과가 존재하지 않습니다.
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 등록 탭 -->
          <div v-if="activeTab === 'regist'" id="pills-profile">
            <div class="formItem">
              <label for="registAreaType" class="form-tit mr20 floatLt">등록 타입</label>
              <select id="registAreaType" class="select-sm floatRt" v-model="registType">
                <option value="0">직접 등록</option>
                <option value="1">검색 등록</option>
              </select>
            </div>

            <div v-if="registType === '1'" class="row search-block">
              검색 :
              <input
                type="text"
                id="search-attraction"
                v-model="searchKeyword"
                @keyup.enter="handleSearch"
              />

              <button class="btn btn-primary w-100" @click="handleSearch">검색하기</button>
            </div>

            <div v-if="registFlag" class="row red-pin-block" style="flex-direction: column">
              이미지:
              <input
                v-if="registType === '0'"
                type="file"
                ref="imgFile"
                name="imgFile"
                accept="image/*"
                style="width: 100%"
                @change="(e) => (imgFile = e.target)"
              />
              <div v-if="registType === '1'">
                <img
                  class="zoneInfo-zoneList-list-item-disabled"
                  style="width: 100%"
                  :src="imgSrc"
                />
              </div>
              이름: <input type="text" id="red-pin-name" v-model="spotName" /> 현재 위도:
              <span id="red-pin-lat">{{ currentLat }}</span> 현재 경도:
              <span id="red-pin-lon">{{ currentLon }}</span>

              <div style="margin-bottom: 20px">
                태그:
                <div v-for="(tag, index) in hashtags" :key="index" class="hashtg"># {{ tag }}</div>

                <div class="hashtag-pin">
                  <input
                    type="text"
                    name="hashtag-txt"
                    v-model="hashtagInput"
                    placeholder="태그를 입력하세요"
                    @keyup.enter="addHashtag"
                  />
                </div>
              </div>

              <button class="btn btn-primary w-100" id="red-pin-regist" @click="addSpot">
                등록
              </button>
            </div>
          </div>
        </div>
      </div>
      <div id="layer-navi" @click="toggleLayer">
        <div></div>
      </div>
    </div>

    <div id="vMap" class="mapArea map">
      <!-- 지도 및 버튼 -->
      <div id="mapModeButton">
        <div id="2dButton" @click="changeLayer('2d-map')">
          <img src="/img/dash/layer_tit.png" /> 2D
        </div>
        <div id="satelliteButton" @click="changeLayer('SatelliteMap')">
          <img src="/img/dash/layer_tit.png" /> 위성영상
        </div>
        <div id="hybridButton" @click="changeLayer('HybridMap')">
          <img src="/img/dash/layer_tit.png" /> 하이브리드
        </div>
      </div>
    </div>

    <!-- 범례 -->
    <div id="legend">
      <div class="square-box" v-for="(label, color) in legendColors" :key="color">
        <div class="square-box-box">
          <div :class="color + '-square'"></div>
        </div>
        <span>{{ label }}</span>
      </div>
    </div>
  </CommunityLayout>
</template>

<script setup>
import { ref, onMounted, watch, provide } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Map, View } from 'ol'
import { fromLonLat } from 'ol/proj'
import TileLayer from 'ol/layer/Tile'
import XYZ from 'ol/source/XYZ'
import { defaults as defaultControls } from 'ol/control'
import Overlay from 'ol/Overlay'
import { useVWorldMap } from '@/composables/useVWorldMap'

import CommunityLayout from '@/components/common/CommunityLayout.vue'
import * as communityAttractionAPI from '../../lib/api/communityAttractionAPI'
import * as attractionAPI from '../../lib/api/attractionAPI'
import * as tagAPI from '../../lib/api/tagAPI'
import * as planAPI from '../../lib/api/planAPI'
import * as communityAPI from '../../lib/api/communityAPI'
import * as planattractionAPI from '../../lib/api/planattractionAPI'
import * as communityAttractionLikeAPI from '../../lib/api/communityAttractionLikeAPI'

const {
  setMap,
  pinRedMarker,
  moveTo,
  controlLayerVisibility,
  removeLayerByProperty,
  addZoneMarkers,
} = useVWorldMap()

const route = useRoute()

const communityId = route.params.id || route.path.split('/').pop()

const searchType = ref('all')
const searchKeyword = ref('')
const totalZoneCount = ref(0)
const zoneList = ref([])
const tagList = ref([])
const planList = ref([])
const registType = ref('1')
const activeTab = ref('search')
const visibleIdx = ref(-1)
const selectedLayer = ref('marker')
const liked = ref(false)

const hashtags = ref([])
const hashtagInput = ref('')
const activeTagIndexes = ref(new Set())

// 등록
const spotName = ref('')
const imgFile = ref(null)
const imgSrc = ref('')

const manualFlag = ref(false)
const registFlag = ref(false)

const currentLat = ref(0)
const currentLon = ref(0)

const community = ref({})

provide('manualFlag', manualFlag)

// 현재 위도와 경도를 전역으로 저장
provide('currentLat', currentLat)
provide('currentLon', currentLon)

const aKey = 'D5152B22-77A2-311E-B514-EFD1422CD4ED'

const legendColors = {
  blue: '대여',
  green: '반납',
  red: '불법주차',
}

const isInactive = ref(false)

function toggleLayer() {
  isInactive.value = !isInactive.value
}

const handlePlanClick = async (item, zoneInfo) => {
  if (confirm('해당 장소를 여행계획에 추가하시겠습니까?')) {
    const res = await planattractionAPI.createPlanAttraction2(item.pid, zoneInfo.attraction)
  }
}

const toggleLike = async (item) => {

  try {
    const res = await communityAttractionLikeAPI.checkCommunityAttractionLike({ communityAttractionId : item.communityAttractionId })

    if (res.data.checked) {
      const res = await communityAttractionLikeAPI.deleteCommunityAttractionLike({ communityAttractionId : item.communityAttractionId })

      if (res.state === 200) {
        item.likeCnt = item.likeCnt - 1;
        item.liked = !item.liked
      }
    } else {
      const res = await communityAttractionLikeAPI.createCommunityAttractionLike({ communityAttractionId : item.communityAttractionId })
      if (res.state === 200) {
        item.likeCnt = item.likeCnt + 1;
        item.liked = !item.liked
      }
    }
  } catch (e) {
    console.error(e)
    alert('좋아요 처리 중 오류 발생')
  }

}

const clickZoneInfoBlock = (lat, lon) => {
  moveTo(lon, lat, 15)
}

const closePlanTooltip = () => {
  visibleIdx.value = -1
}

const searchMap = async () => {
  try {
    const result = await communityAttractionAPI.getCommunityAttractionList({
      communityid: communityId,
      query: searchKeyword.value ? searchKeyword.value : null,
      tags: Array.from(activeTagIndexes.value),
    })

    if (!result || !Array.isArray(result.data)) {
      zoneList.value = []
      totalZoneCount.value = 0
      return
    }

    if (searchType.value === 'all') {
      zoneList.value = result.data
    } else if (searchType.value === 'name') {
      zoneList.value = result.data.filter((item) =>
        item.attraction?.title?.includes(searchKeyword.value),
      )
    } else if (searchType.value === 'tag') {
      zoneList.value = result.data
    }
    zoneList.value = result.data

    addZoneMarkers(zoneList.value, selectedLayer.value)

    totalZoneCount.value = zoneList.value.length
  } catch (error) {
    console.error('명소 조회 실패:', error)
    zoneList.value = []
    totalZoneCount.value = 0
  }
}

const changeLayer = (type) => {
  controlLayerVisibility(type)
}

const clickTag = (tagname) => {
  if (activeTagIndexes.value.has(tagname)) {
    activeTagIndexes.value.delete(tagname)
  } else {
    activeTagIndexes.value.add(tagname)
  }
}

const addHashtag = () => {
  const tag = hashtagInput.value.trim()
  if (tag) {
    hashtags.value.push(tag)
    hashtagInput.value = ''
  }
}

const handleMapClick = (evt) => {
  if (!manualFlag.value) return

  // 1. 좌표 추출
  const coordinate = evt.coordinate
  const [lon, lat] = ol.proj.toLonLat(coordinate)

  // 2. 마커 초기화 및 재설정
  removeLayerByProperty('layer_name', 'pin_red_marker')
  pinRedMarker(lon, lat)

  // 3. Vue 상태 반영
  currentLat.value = lat
  currentLon.value = lon
}

const handleSearch = async () => {
  if (searchKeyword.value.trim() !== '') {
    const res = await attractionAPI.fetchAttractionByKeyword({ keyword: searchKeyword.value })

    if (res && res.state === 200) {
      registFlag.value = true

      imgSrc.value = res?.data?.firstimage || ''

      spotName.value = res.data.title
      currentLat.value = res.data.mapy
      currentLon.value = res.data.mapx

      removeLayerByProperty('layer_name', 'pin_red_marker')
      pinRedMarker(res.data.mapx, res.data.mapy)
      moveTo(res.data.mapx, res.data.mapy, 14)
    }
  }
}

const addSpot = async () => {
  const communityId = route.params.id || route.path.split('/').pop()

  const formData = new FormData()
  formData.append('title', spotName.value)
  formData.append('mapx', currentLon.value)
  formData.append('mapy', currentLat.value)
  formData.append('communityid', communityId)
  if (imgFile.value?.files[0]) {
    formData.append('imgFile', imgFile.value.files[0])
  }

  hashtags.value.forEach((tag) => formData.append('hashtagList', tag))

  try {
    const res = await communityAttractionAPI.createCommunityAttraction(formData)

    // 등록값 초기화
    spotName.value = ''
    imgFile.value = null
    hashtags.value = []

    alert('등록이 완료되었습니다.')
  } catch (error) {
    if (error.response?.status !== 500) {
      alert(
        '마커 데이터 조회에 실패하였습니다.\n해당 문제가 지속될 경우 관리자에게 문의하여 주십시오.',
      )
    }
    console.error(error)
  }
}

const addToPlan = async (item, index) => {
  const res = await planAPI.getPlans()

  // 성공
  planList.value = res

  // 툴팁 표시
  visibleIdx.value = index
}

watch(registType, (newVal) => {
  if (newVal === '0') {
    manualFlag.value = true
    registFlag.value = true
  } else if (newVal === '1') {
    manualFlag.value = false
    registFlag.value = false
  }

  // 등록값 초기화
  spotName.value = ''
  imgFile.value = null
  hashtags.value = []
})

watch(searchType, (newVal) => {
  if (newVal === 'tag') {
    const fetchTags = async () => {
      const communityId = route.params.id || route.path.split('/').pop()

      try {
        const res = await tagAPI.getTags({ communityId })

        tagList.value = res
      } catch (error) {
        console.error('태그 불러오기 실패:', error)
      }
    }

    fetchTags()
  }
})

onMounted(() => {
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
    target: 'vMap',
    layers: [baseLayer, satelliteLayer, hybridLayer],
    view: new View({
      projection: 'EPSG:3857',
      center: fromLonLat([126.4989967, 33.4879725]),
      zoom: 15.3,
      minZoom: 7,
      maxZoom: 20,
    }),
    controls: defaultControls({ zoom: false }),
  })

  mapInstance.on('click', handleMapClick)

  setMap(mapInstance)

  // 테스트
  moveTo(126.978, 37.5665, 14) // 서울
  pinRedMarker(126.978, 37.5665)

  const fetchCommunity = async () => {
    try {
      const communityId = route.params.id || route.path.split('/').pop()
      const res = await communityAPI.getCommunity({ communityId: communityId })

      community.value = res.data
    } catch (error) {
      console.error('community 데이터 가져오기 실패', error)
    }
  }

  fetchCommunity()

  if (activeTab.value === 'search' && registType.value == '1') {
    searchMap()
  }
})
</script>

<style scoped>
.ol-tooltip {
  background: rgba(0, 0, 0, 0.7);
  color: #fff;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  white-space: nowrap;
}

.like-button {
  background: none;
  border: none;
  font-size: 18px;
  color: #999;
  cursor: pointer;
  transition: color 0.2s;
}

.like-button.liked {
  color: red; /* 좋아요 누른 상태 */
}

.community-main-actions {
  display: flex;
  justify-content: center;

  .like-btn {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 8px 16px;
    border: 2px solid #dee2e6;
    background: white;
    border-radius: 20px;
    cursor: pointer;
    transition: all 0.3s ease;
    color: #666;

    &:hover {
      border-color: #ff6b6b;
      color: #ff6b6b;
    }

    &.liked {
      border-color: #ff6b6b;
      background: #ff6b6b;
      color: white;
    }

    i {
      font-size: 16px;
    }

    span {
      font-weight: 600;
    }
  }
}

.layer-toggle-group {
  margin-top: 10px;
  display: flex;
  gap: 8px;
}

.layer-toggle-group button {
  padding: 6px 12px;
  border: 1px solid #ccc;
  border-radius: 4px;
  background: white;
  cursor: pointer;
}

.layer-toggle-group button.active {
  background-color: #007bff;
  color: white;
}

.zoneInfo-zoneList-list-divisionLine {
  width: 100%;
  margin: 10px 0;
  display: block;
}

.ol-zoom {
  display: none !important;
}

.tag-list-wrapper {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin: 10px 0;
}

.tag-item {
  background-color: #eee;
  padding: 6px 10px;
  border-radius: 12px;
  cursor: pointer;
}

.tag-item:hover {
  background-color: #ddd;
}

.tag-active {
  background-color: #98fb98;
}

#search-attraction {
  margin-bottom: 10px;
}

.zoneInfo-zoneList-list-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
  background: white;
  border: 1px solid #ccc;
  border-radius: 12px;
  padding: 12px;
  transition: all 0.3s ease;
  margin-bottom: 10px;
}

.zoneInfo-zoneList-list-item:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  background: #f0f8ff;
}

.zoneInfo-zoneList-list-item-image {
  width: 100%;
  border-radius: 8px;
  object-fit: cover;
}

.zoneInfo-zoneList-list-item-text .title {
  font-weight: bold;
  font-size: 16px;
  margin: 6px 0 2px;
}

.zoneInfo-zoneList-list-item-text .address {
  font-size: 14px;
  color: #666;
  margin-bottom: 6px;
}

.tag-container {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-top: 4px;
}

.tag {
  background-color: #e0f7fa;
  color: #00796b;
  padding: 4px 8px;
  border-radius: 10px;
  font-size: 13px;
  white-space: nowrap;
}

.plan-button-container {
  display: flex;
  justify-content: center;
  margin-top: 10px;
  position: relative;
}

.plan-button {
  background: linear-gradient(135deg, #ffafbd, #ffc3a0);
  color: #fff;
  border: none;
  padding: 10px 20px;
  font-size: 15px;
  font-weight: bold;
  border-radius: 20px;
  cursor: pointer;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
  transition: all 0.2s ease-in-out;
}

.plan-button:hover {
  background: linear-gradient(135deg, #ff7eb3, #ff758c);
  transform: translateY(-1px);
}

.plan-button:active {
  transform: scale(0.97);
}

.plan-item {
  cursor: pointer;
}

.plan-item:hover {
  background-color: rgb(218, 215, 215);
}

.plan-tooltip {
  position: absolute;
  top: 50px;
  width: 240px;
  background: #fffefa;
  border: 1px solid #ddd;
  border-radius: 12px;
  padding: 12px 16px 16px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  z-index: 1000;
  font-size: 14px;
}

.tooltip-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.tooltip-header h4 {
  font-size: 16px;
  margin: 0;
  color: #333;
}

.close-btn {
  background: none;
  border: none;
  font-size: 18px;
  cursor: pointer;
  color: #888;
  padding: 0;
}

.close-btn:hover {
  color: #ff6b6b;
}

.plan-tooltip ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.plan-tooltip li {
  margin: 6px 0;
  color: #555;
}

/* common.css */
@charset "utf-8";
html,
body {
  width: 100%;
}
html,
body {
  line-height: 1.15;
  -webkit-text-size-adjust: 100%;
}
body {
  margin: 0;
  font-size: 15px;
  font-weight: 400;
  color: #404040;
  letter-spacing: -0.8px;
}
em {
  font-style: normal;
}
main {
  display: block;
}
hr {
  box-sizing: content-box;
  height: 0;
  overflow: visible;
}
pre {
  font-family: 'CoreDream', 'sans-serif';
  font-size: 1em;
}
a {
  background-color: transparent;
}
abbr[title] {
  border-bottom: none;
  text-decoration: underline;
  text-decoration: underline dotted;
}
b,
strong {
  font-weight: bolder;
}
small {
  font-size: 14px;
}
sub,
sup {
  font-size: 14px;
  line-height: 0;
  position: relative;
  vertical-align: baseline;
}
sub {
  bottom: -0.25em;
}
sup {
  top: -0.5em;
}
img {
  border-style: none;
}

button,
input,
optgroup,
select,
textarea {
  font-family: 'CoreDream', 'sans-serif';
  line-height: 1.15;
  margin: 0;
}
button,
input {
  overflow: visible;
}
input {
  height: 35px;
  padding: 0px 10px;
  border: 1px solid rgb(230, 230, 230);
  font-size: 14px;
  vertical-align: middle;
  color: #616161;
  -webkit-border-radius: 0;
  background: #fff;
  border-radius: 5px;
}
input.readOnly {
  font-weight: 500;
  background: #f5f5f5;
}

input:-webkit-autofill {
  -webkit-box-shadow: 0 0 0 30px #edf1f4 inset;
  -webkit-text-fill-color: #3a3a3a;
}
input:-webkit-autofill,
input:-webkit-autofill:hover,
input:-webkit-autofill:focus,
input:-webkit-autofill:active {
  transition: background-color 5000s ease-in-out 0s;
}
button,
select {
  text-transform: none;
}
button {
  cursor: pointer;
}
button,
[type='button'],
[type='reset'],
[type='submit'] {
  -webkit-appearance: button;
}
button::-moz-focus-inner,
[type='button']::-moz-focus-inner,
[type='reset']::-moz-focus-inner,
[type='submit']::-moz-focus-inner {
  border-style: none;
  padding: 0;
}
button:-moz-focusring,
[type='button']:-moz-focusring,
[type='reset']:-moz-focusring,
[type='submit']:-moz-focusring {
  outline: 1px dotted;
}

fieldset {
  padding: 0.35em 0.75em 0.625em;
}
legend {
  box-sizing: border-box;
  color: inherit;
  display: table;
  max-width: 100%;
  padding: 0;
  white-space: normal;
}
progress {
  vertical-align: baseline;
}
textarea {
  overflow: auto;
}

[type='checkbox'],
[type='radio'] {
  box-sizing: border-box;
  padding: 0;
}
[type='number']::-webkit-inner-spin-button,
[type='number']::-webkit-outer-spin-button {
  height: auto;
}
[type='search'] {
  -webkit-appearance: textfield;
  outline-offset: -2px;
}
[type='search']::-webkit-search-decoration {
  -webkit-appearance: none;
}
::-webkit-file-upload-button {
  -webkit-appearance: button;
  font: inherit;
}

details {
  display: block;
}
summary {
  display: list-item;
}
template {
  display: none;
}
[hidden] {
  display: none;
}

* {
  margin: 0;
  padding: 0;
}

/* common css custom */
/**,
*:after,
*:before {
  box-sizing: border-box;
}*/
* {
  padding: 0;
  margin: 0;
  font-family: 'CoreDream', 'sans-serif';
}
li {
  list-style: none;
}
a {
  text-decoration: none;
}
table {
  border-collapse: collapse;
  border-spacing: 0;
  table-layout: fixed;
}
table th,
table td {
  text-overflow: ellipsis;
}
button {
  border: 0;
  background: #fff;
  vertical-align: middle;
  font-size: 13px;
  font-weight: 400;
}
select {
  height: 35px;
  padding: 0px 10px;
  border: 1px solid rgb(230, 230, 230);
  font-size: 14px;
  vertical-align: middle;
  color: #616161;
  -webkit-border-radius: 0;
  background: #fff;
  border-radius: 5px;
}
option {
  padding: 8px;
}
select,
option,
label {
  font-size: 14px;
}
label {
  vertical-align: middle;
}

table input[type='text'] {
  height: 30px;
  font-size: 13px;
}
table select {
  height: 30px;
  font-size: 13px;
}
table button {
  height: 30px;
  font-size: 13px;
}

.clear-fix:after {
  content: '';
  display: block;
  clear: both;
}
.skip {
  position: absolute;
  clip: rect(0 0 0 0);
  width: 1px;
  height: 1px;
  margin: -1px;
  overflow: hidden;
}
.col-empty {
  text-indent: -9999px;
  font-size: 0;
}

/*white space*/
.mg0 {
  margin: 0px auto;
}
.mt5 {
  margin-top: 5px !important;
}
.mt10 {
  margin-top: 10px !important;
}
.mt15 {
  margin-top: 15px !important;
}
.mt20 {
  margin-top: 20px !important;
}
.mt25 {
  margin-top: 25px !important;
}
.mt30 {
  margin-top: 30px !important;
}
.mt35 {
  margin-top: 35px !important;
}
.mt40 {
  margin-top: 40px !important;
}
.mt45 {
  margin-top: 45px !important;
}
.mt50 {
  margin-top: 50px !important;
}
.mt55 {
  margin-top: 55px !important;
}
.mt60 {
  margin-top: 60px !important;
}

.ml5 {
  margin-left: 5px !important;
}
.ml10 {
  margin-left: 10px !important;
}
.ml14 {
  margin-left: 14px !important;
}
.ml15 {
  margin-left: 15px !important;
}
.ml20 {
  margin-left: 20px !important;
}
.ml25 {
  margin-left: 25px !important;
}
.ml30 {
  margin-left: 30px !important;
}
.ml35 {
  margin-left: 35px !important;
}
.ml40 {
  margin-left: 40px !important;
}
.ml45 {
  margin-left: 45px !important;
}
.ml50 {
  margin-left: 50px !important;
}
.ml55 {
  margin-left: 55px !important;
}
.ml60 {
  margin-left: 60px !important;
}

.mr5 {
  margin-right: 5px !important;
}
.mr10 {
  margin-right: 10px !important;
}
.mr15 {
  margin-right: 15px !important;
}
.mr20 {
  margin-right: 20px !important;
}
.mr25 {
  margin-right: 25px !important;
}
.mr30 {
  margin-right: 30px !important;
}
.mr35 {
  margin-right: 35px !important;
}
.mr40 {
  margin-right: 40px !important;
}
.mr45 {
  margin-right: 45px !important;
}
.mr50 {
  margin-right: 50px !important;
}
.mr55 {
  margin-right: 55px !important;
}
.mr60 {
  margin-right: 60px !important;
}

.mb5 {
  margin-bottom: 5px !important;
}
.mb10 {
  margin-bottom: 10px !important;
}
.mb15 {
  margin-bottom: 15px !important;
}
.mb20 {
  margin-bottom: 20px !important;
}
.mb25 {
  margin-bottom: 25px !important;
}
.mb30 {
  margin-bottom: 30px !important;
}
.mb35 {
  margin-bottom: 35px !important;
}
.mb40 {
  margin-bottom: 40px !important;
}
.mb45 {
  margin-bottom: 45px !important;
}
.mb50 {
  margin-bottom: 50px !important;
}
.mb55 {
  margin-bottom: 55px !important;
}
.mb60 {
  margin-bottom: 60px !important;
}

/*padding*/
.pd5 {
  padding: 5px !important;
}
.pd10 {
  padding: 10px !important;
}
.pd20 {
  padding: 20px !important;
}
.pd30 {
  padding: 30px !important;
}
.pd40 {
  padding: 40px !important;
}
.pd50 {
  padding: 50px !important;
}

.pt5 {
  padding-top: 5px !important;
}
.pt10 {
  padding-top: 10px !important;
}
.pt15 {
  padding-top: 15px !important;
}
.pt20 {
  padding-top: 20px !important;
}
.pt25 {
  padding-top: 25px !important;
}
.pt30 {
  padding-top: 30px !important;
}
.pt35 {
  padding-top: 35px !important;
}
.pt40 {
  padding-top: 40px !important;
}
.pt45 {
  padding-top: 45px !important;
}
.pt50 {
  padding-top: 50px !important;
}
.pt55 {
  padding-top: 55px !important;
}
.pt60 {
  padding-top: 60px !important;
}

.pl5 {
  padding-left: 5px !important;
}
.pl10 {
  padding-left: 10px !important;
}
.pl15 {
  padding-left: 15px !important;
}
.pl20 {
  padding-left: 20px !important;
}
.pl25 {
  padding-left: 25px !important;
}
.pl30 {
  padding-left: 30px !important;
}
.pl35 {
  padding-left: 35px !important;
}
.pl40 {
  padding-left: 40px !important;
}
.pl45 {
  padding-left: 45px !important;
}
.pl50 {
  padding-left: 50px !important;
}
.pl55 {
  padding-left: 55px !important;
}
.pl60 {
  padding-left: 60px !important;
}

.pr5 {
  padding-right: 5px !important;
}
.pr10 {
  padding-right: 10px !important;
}
.pr15 {
  padding-right: 15px !important;
}
.pr20 {
  padding-right: 20px !important;
}
.pr25 {
  padding-right: 25px !important;
}
.pr30 {
  padding-right: 30px !important;
}
.pr35 {
  padding-right: 35px !important;
}
.pr40 {
  padding-right: 40px !important;
}
.pr45 {
  padding-right: 45px !important;
}
.pr50 {
  padding-right: 50px !important;
}
.pr55 {
  padding-right: 55px !important;
}
.pr60 {
  padding-right: 60px !important;
}

.pb5 {
  padding-bottom: 5px !important;
}
.pb10 {
  padding-bottom: 10px !important;
}
.pb15 {
  padding-bottom: 15px !important;
}
.pb20 {
  padding-bottom: 20px !important;
}
.pb25 {
  padding-bottom: 25px !important;
}
.pb30 {
  padding-bottom: 30px !important;
}
.pb35 {
  padding-bottom: 35px !important;
}
.pb40 {
  padding-bottom: 40px !important;
}
.pb45 {
  padding-bottom: 45px !important;
}
.pb50 {
  padding-bottom: 50px !important;
}
.pb55 {
  padding-bottom: 55px !important;
}
.pb60 {
  padding-bottom: 60px !important;
}

.colNum {
  width: 5%;
}
.col10 {
  width: 10%;
}
.col20 {
  width: 20%;
}
.col30 {
  width: 30%;
}
.col40 {
  width: 40%;
}
.col50 {
  width: 50%;
}
.colAuto {
  width: auto;
}

.txtLt {
  text-align: left;
}
.txtRt {
  text-align: right;
}
.txtCt {
  text-align: center;
}

.inline-block {
  display: inline-block;
  vertical-align: middle;
}
.floatLt {
  font-size: 15px;
  font-weight: 500;
  margin-bottom: 12px;
  float: left;
}
.floatRt {
  float: right;
}

.ellipsis {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* component.css */
@charset "utf-8";

/*공통폰트*/
@font-face {
  font-family: 'CoreDream';
  src:
    url('/fonts/SCDream1.otf'),
    url('https://cdn.jsdelivr.net/gh/projectnoonnu/noonfonts_six@1.2/S-CoreDream-1Thin.woff')
      format('woff');
  font-style: normal;
  font-weight: 100;
}
@font-face {
  font-family: 'CoreDream';
  src:
    url('../fonts/SCDream2.otf'),
    url('https://cdn.jsdelivr.net/gh/projectnoonnu/noonfonts_six@1.2/S-CoreDream-2ExtraLight.woff')
      format('woff');
  font-style: normal;
  font-weight: 200;
}
@font-face {
  font-family: 'CoreDream';
  src:
    url('../fonts/SCDream3.otf'),
    url('https://cdn.jsdelivr.net/gh/projectnoonnu/noonfonts_six@1.2/S-CoreDream-3Light.woff')
      format('woff');
  font-style: normal;
  font-weight: 300;
}
@font-face {
  font-family: 'CoreDream';
  src:
    url('../fonts/SCDream4.otf'),
    url('https://cdn.jsdelivr.net/gh/projectnoonnu/noonfonts_six@1.2/S-CoreDream-4Regular.woff')
      format('woff');
  font-style: normal;
  font-weight: 400;
}
@font-face {
  font-family: 'CoreDream';
  src:
    url('../fonts/SCDream5.otf'),
    url('https://cdn.jsdelivr.net/gh/projectnoonnu/noonfonts_six@1.2/S-CoreDream-5Medium.woff')
      format('woff');
  font-style: normal;
  font-weight: 500;
}
@font-face {
  font-family: 'CoreDream';
  src:
    url('../fonts/SCDream6.otf'),
    url('https://cdn.jsdelivr.net/gh/projectnoonnu/noonfonts_six@1.2/S-CoreDream-6Bold.woff')
      format('woff');
  font-style: normal;
  font-weight: 600;
}
@font-face {
  font-family: 'CoreDream';
  src:
    url('../fonts/SCDream7.otf'),
    url('https://cdn.jsdelivr.net/gh/projectnoonnu/noonfonts_six@1.2/S-CoreDream-7ExtraBold.woff')
      format('woff');
  font-style: normal;
  font-weight: 700;
}
@font-face {
  font-family: 'CoreDream';
  src:
    url('../fonts/SCDream8.otf'),
    url('https://cdn.jsdelivr.net/gh/projectnoonnu/noonfonts_six@1.2/S-CoreDream-8Heavy.woff')
      format('woff');
  font-style: normal;
  font-weight: 800;
}
@font-face {
  font-family: 'CoreDream';
  src:
    url('../fonts/SCDream9.otf'),
    url('https://cdn.jsdelivr.net/gh/projectnoonnu/noonfonts_six@1.2/S-CoreDream-9Black.woff')
      format('woff');
  font-style: normal;
  font-weight: 900;
}

/*공통텍스트*/
.subTxt1 {
  font-size: 24px;
  font-weight: 600;
}
.subTxt2 {
  font-size: 18px;
  font-weight: 600;
  display: block;
  margin-bottom: 10px;
  background: url('/img/sub/subtit.png') no-repeat left center;
  padding-left: 28px;
}
.subTxt3 {
  font-size: 20px;
  font-weight: 600;
}
.subTxt4 {
  font-size: 18px;
  font-weight: 600;
}

/*기본버튼*/
.cmnBtn {
  min-width: 80px;
  height: 34px;
  background: #262626;
  color: rgb(255, 255, 255);
  padding: 6px 16px;
  padding-top: 9px;
}
.cmnBtn.register {
  background: #1c8ae8;
  border-radius: 5px;
}
.cmnBtn.save {
  background: #23263e;
}
.cmnBtn.cancel {
  background: #3e3e3e;
  border-radius: 5px;
}
.cmnBtn.list {
  background: #383838;
  border-radius: 5px;
}
.cmnBtn.search {
  background: #0771cb url(/img/cmn/ico_search.png) no-repeat left 16px center;
  background-size: 13px;
  padding-left: 28px;
  border-radius: 5px;
}
.cmnBtn.delete {
  border: 1px solid rgb(33 118 204);
  color: rgb(33 118 204);
  background: rgb(255, 255, 255);
  font-weight: 500;
  border-radius: 5px;
}
.cmnBtn.etc1 {
  background: #5039ad;
}
.cmnBtn.etc2 {
  background: #7ec4ff;
  border-radius: 5px;
}
.excelBtn {
  padding: 10px 14px;
  background: url(/img/cmn/ico_download.png) no-repeat left 13px center;
  height: 34px;
  background-size: 15px;
  color: #008237;
  padding-left: 33px;
  display: inline-block;
  vertical-align: middle;
  font-size: 13px;
  border: 1px solid #3da36a;
  border-radius: 5px;
}
.excelBtn.type2 {
  background-color: #3f4244;
}

/*테이블 셀 안쪽, input 옆 태그*/
.cellFormBtn {
  min-width: 40px;
  height: 30px;
  background: #262626;
  color: rgb(255, 255, 255);
  padding: 5px 10px;
  font-weight: 300;
  line-height: 1.8px;
}
.cellFormBtn.cellFormBtn1 {
  background: #484848;
}
.cellFormBtn.cellFormBtn2 {
  background: rgb(26 99 193);
}
.cellFormBtn.cellFormBtn3 {
  border: 1px solid #3b3b3b;
  background: rgb(255, 255, 255);
  color: #262626;
}
.cellFormBtn.cellFormBtn4 {
  background: #6e6e6e;
}
.cellFormBtn.cellFormBtn5 {
  background: rgb(26 99 193);
}
.cellFormBtn.cellFormBtn6 {
  background: #5557d7;
}

.cellFormButton {
  min-width: 40px;
  height: 30px;
  background: #262626;
  color: rgb(255, 255, 255);
  padding: 5px 5px;
  font-weight: 300;
  line-height: 1.8px;
}
.cellFormButton.cellFormBtn4 {
  background: #6e6e6e;
}

/*폼태그*/
.inputTxt-xs {
  width: 50px;
}
.inputTxt-sm {
  width: 140px;
}
.inputTxt-md {
  width: 160px;
}
.inputTxt-lg {
  width: 180px;
}
.inputTxt-xlg {
  width: 200px;
}
.inputTxt-full {
  width: 100%;
}

.select-xs {
  min-width: 50px;
}
.select-sm {
  min-width: 140px;
}
.select-md {
  min-width: 160px;
}
.select-lg {
  min-width: 180px;
}
.select-xlg {
  min-width: 200px;
}
.select-full {
  min-width: 100%;
}

/*선택버튼*/
.formChoice-box {
  vertical-align: middle;
  display: inline-block;
}
.form-rdo,
.form-chk {
  vertical-align: middle;
  display: inline-block;
  height: inherit;
}
.form-name {
  vertical-align: middle;
  display: inline-block;
  margin: 0px 2px;
  margin-right: 8px;
  font-size: 15px;
}

/*라벨버튼*/
.label-box {
  vertical-align: middle;
  display: inline-block;
  float: left;
  padding-left: 8px;
}
.label .form-chk {
  vertical-align: middle;
  display: inline-block;
  height: inherit;
}
.label-name1 {
  vertical-align: middle;
  display: inline-block;
  margin: 0px 2px;
  margin-right: 8px;
  font-size: 15px;
  background: #777777;
  border-radius: 5px;
  padding: 10px 20px;
}
.label-name2 {
  vertical-align: middle;
  display: inline-block;
  margin: 0px 2px;
  margin-right: 8px;
  font-size: 15px;
  background: #337ab7;
  border-radius: 5px;
  padding: 10px 20px;
}
.label-name3 {
  vertical-align: middle;
  display: inline-block;
  margin: 0px 2px;
  margin-right: 8px;
  font-size: 15px;
  background: #09a55a;
  border-radius: 5px;
  padding: 10px 20px;
}
.label-name4 {
  vertical-align: middle;
  display: inline-block;
  margin: 0px 2px;
  margin-right: 8px;
  font-size: 15px;
  background: #00bfef;
  border-radius: 5px;
  padding: 10px 20px;
}
.label-name5 {
  vertical-align: middle;
  display: inline-block;
  margin: 0px 2px;
  margin-right: 8px;
  font-size: 15px;
  background: #f39c13;
  border-radius: 5px;
  padding: 10px 20px;
}
.label-name6 {
  vertical-align: middle;
  display: inline-block;
  margin: 0px 2px;
  margin-right: 8px;
  font-size: 15px;
  background: #de4c39;
  border-radius: 5px;
  padding: 10px 20px;
}

/*첨부파일*/
.fileBox {
  font-size: 0px;
}
.fileBox .upload {
  border: 0;
  border-bottom: 1px solid #c9c9c9;
  width: 280px;
  display: inline-block;
  vertical-align: middle;
  height: 35px;
  font-size: 15px;
  color: rgb(139, 139, 139);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin-right: 7px;
}
.fileBtn-box {
  position: relative;
  display: inline-block;
  vertical-align: middle;
  margin-left: -1px;
  margin-right: 10px;
}
.fileBtn-box .formFile-btn {
  height: 38px;
  padding: 2px 14px 0px 27px;
  color: #282828;
  border: 1px solid #c9c9c9;
  background: url('/img/cmn/ico_clip1.png') left 11px top 12px / 12px no-repeat;
  font-size: 15px;
}
.fileBtn-box .formFile-realBtn {
  position: absolute;
  inset: 0px;
  width: 100%;
  opacity: 0;
  overflow: hidden;
  cursor: pointer;
  z-index: 10;
}

/*달력*/
.calendar-wr {
  display: inline-block;
  vertical-align: middle;
}
.calendar-item {
  border: 1px solid rgb(230, 230, 230);
  display: inline-block;
  vertical-align: middle;
  position: relative;
  border-radius: 5px;
}
.calendar-item .date-picker {
  width: 180px;
  height: 35px;
  font-size: 14px;
  border: 0;
  padding-right: 40px;
}
.calendar-item .date-picker2 {
  width: 180px;
  height: 35px;
  font-size: 14px;
  border: 0;
  padding-right: 40px;
}
.calendar-item .calendar-btn {
  position: absolute;
  top: 10px;
  right: 6px;
}

/*시간*/
.time-wr {
  display: inline-block;
  vertical-align: middle;
}
.time-item {
  border: 1px solid rgb(230, 230, 230);
  display: inline-block;
  vertical-align: middle;
  position: relative;
  border-radius: 5px;
}
.time-item .time-picker {
  width: 120px;
  height: 35px;
  font-size: 14px;
  border: 0;
  padding-right: 40px;
}
.time-item .time-btn {
  position: absolute;
  top: 8px;
  right: 6px;
}

/*텍스트 에어리어*/
.textarea {
  width: 100%;
  border: 1px solid #c9c9c9;
  resize: none;
  padding: 20px;
  font-weight: 300;
}

/*탭*/
.cmnTabs {
  clear: both;
}
.cmnTabs > ul > li {
  float: left;
  border: 1px solid #c9c9c9;
  width: 25%;
  height: 60px;
  margin-left: -1px;
}
.cmnTabs > ul > li.current {
  background: linear-gradient(45deg, #2f7ed6, #622fd6);
  border: 0;
}
.cmnTabs > ul > li > a {
  display: block;
  color: inherit;
  text-align: left;
  font-weight: 400;
  line-height: 60px;
  text-align: center;
}
.tabCon {
  display: none;
  padding-top: 30px;
}
.cmnTabs > ul > li.current a {
  color: white;
  font-weight: 500;
}
.cmnTabs .tabCon.current {
  display: block;
}

/*페이지네이션*/
.pagination {
  width: 100%;
  margin: 20px 0;
}
.pagination ul {
  text-align: center;
  font-size: 0;
}
.pagination ul li {
  display: inline-block;
  vertical-align: middle;
  margin-right: 4px;
}
.pagination ul li.on a {
  color: #fff !important;
  background: linear-gradient(270deg, #055ab5, #1d8be9);
  border: 0;
  border-radius: 20px;
}
.pagination ul li a {
  width: 37px;
  height: 37px;
  line-height: 29px;
  padding: 5px;
  color: #a4a4a4;
  display: block;
  font-size: 15px;
  padding-bottom: 5px;
  border: 1px solid #e6e6e6;
  font-weight: 500;
  transition: 0.3s;
  border-radius: 20px;
}
.pagination ul li a:hover {
  background-color: #dbdbdb;
}
.pagination ul li.first a,
.pagination ul li.prev a,
.pagination ul li.last a,
.pagination ul li.next a {
  font-size: 0;
  overflow: hidden;
}
.pagination ul li.first a,
.pagination ul li.previous a,
.pagination ul li.last a,
.pagination ul li.next a {
  font-size: 0;
  overflow: hidden;
}
.pagination ul li.first a:hover {
  background-color: #efefef;
}
.pagination ul li.prev a:hover {
  background-color: #efefef;
}
.pagination ul li.previous a:hover {
  background-color: #efefef;
}
.pagination ul li.last a:hover {
  background-color: #efefef;
}
.pagination ul li.next a:hover {
  background-color: #efefef;
}

.pagination ul li.first a {
  background: url('/img/cmn/ico_paging_first.png') no-repeat center;
}
.pagination ul li.prev a {
  background: url('/img/cmn/ico_paging_prev.png') no-repeat center;
}
.pagination ul li.previous a {
  background: url('/img/cmn/ico_paging_prev.png') no-repeat center;
}
.pagination ul li.next a {
  background: url(/img/cmn/ico_paging_next.png) no-repeat left 16px center;
}
.pagination ul li.last a {
  background: url('/img/cmn/ico_paging_last.png') no-repeat left 11px center;
}

/*검색박스*/
.search-box {
  margin: 15px 0px 20px 0;
  transition: all 0.3s ease 0s;
  padding: 22px 24px;
  border: 1px solid #f0f0f0;
  background: #fff;
  box-shadow: 0px 5px 0px 0px #f1f1f1;
  border-radius: 5px;
}
.search-box > * {
  margin: 5px 0;
}
.search-box .form-tit {
  font-weight: 500;
  margin-right: 8px;
  display: inline-block;
  vertical-align: middle;
  font-size: 16px;
}
.search-box select {
  padding: 5px 10px;
}
.search-box select,
.search-box option {
  font-size: 14px;
  border: 1px solid rgb(230, 230, 230);
  border-radius: 5px;
}

.search-form {
  border-bottom: 1px solid #a8a8a8;
  display: inline-block;
  vertical-align: middle;
}
.search-form input[type='text'] {
  border: 0;
  font-size: 14px;
}
.search-form input[type='text']::-webkit-input-placeholder {
  font-size: 15px;
  font-weight: 300;
}

/*목록테이블*/
.listTable {
  text-align: center;
  width: 100%;
  margin-top: 10px;
}
.listTable th {
  padding: 15px 5px;
  font-weight: 500;
  font-size: 15px;
  background-color: #f9f9f9;
  border-top: 2px solid #1e3150;
  color: #1e3150;
}
.listTable td {
  font-size: 14px;
  border-bottom: 1px solid #e8e8e8;
  padding: 18px 4px;
  font-weight: 300;
  color: #4d4d4d;
}
.listTable .link {
  color: inherit;
  font-weight: 500;
}
.listTable .link:hover {
  text-decoration: underline;
}
.listTable tbody tr {
  transition: 0.3s;
}
.listTable tbody tr:hover {
  background: #f0f0f0;
}
.listTable .subject a {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  display: block;
  color: inherit;
}
.listTable .subject a:hover {
  text-decoration: underline;
}

/*리스트 제목 두개이상*/
.listTable.th-double thead th {
  border: 1px solid #dbdbdb;
}
.listTable.th-double thead th:first-child {
  border-left: 0;
}
.listTable.th-double thead tr:first-child th:first-child {
  border-bottom: 1px solid #ebebeb;
}
.listTable.th-double thead tr:last-child th {
  border-bottom: 1px solid #ebebeb;
}
.listTable.th-double thead th:last-child {
  border-right: 0;
}

/*상세조회테이블*/
.detailTable {
  width: 100%;
  margin: 15px 0;
}
.detailTable tr:first-child th,
.detailTable tr:first-child td {
  border-top: 1px solid #bdbdbd;
}
.detailTable th {
  padding: 18px 5px;
  font-weight: 500;
  font-size: 15px;
  border-bottom: 1px solid #dbdbdb;
  background-color: #f8f8f8;
  text-align: center;
  color: #404040;
}
.detailTable td {
  font-size: 15px;
  border-bottom: 1px solid #dbdbdb;
  padding: 12px 5px;
  text-align: center;
}

/*등록테이블*/
.registerTable {
  margin-bottom: 20px;
  width: 100%;
}
.registerTable tr:first-child th,
.registerTable tr:first-child td {
  border-top: 1px solid #bdbdbd;
}
.registerTable th {
  padding: 12px 5px;
  font-weight: 500;
  font-size: 15px;
  border-bottom: 1px solid #dbdbdb;
  background-color: #f9f9f9;
  text-align: left;
  padding-left: 25px;
}
.registerTable td {
  font-size: 15px;
  border-bottom: 1px solid #dbdbdb;
  padding: 12px 5px;
}

/*컬럼 이미지박스*/
.colImgBox {
  border: 1px solid #e5e5e5;
  position: relative;
}
.no-img {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
}

/*레이어팝업*/
/*.layerPop {width: 810px;position: absolute;top: 120px;left:50%;transform: translateX(-50%);background:#fff;z-index: 100;padding-bottom: 20px;border: 1px solid #e7e7e7;display: block;}*/
.layerPop {
  width: 810px;
  position: absolute;
  top: 40px;
  left: 76%;
  transform: translateX(-50%);
  background: #fff;
  z-index: 100;
  padding-bottom: 20px;
  border: 1px solid #e7e7e7;
  display: block;
}
.layerPop .layerPop-header {
  width: 100%;
  min-height: 60px;
  line-height: 60px;
  padding-left: 20px;
  background: #1b88e6;
  position: relative;
}
.layerPop .layerPop-header strong {
  float: left;
  font-size: 23px;
  font-weight: 500;
  color: #fff;
}
.layerPop .layerPop-header button {
  position: absolute;
  width: 32px;
  height: 32px;
  background: url('/img/cmn/ico_pop_close.png') no-repeat center;
  font-size: 0;
  top: 14px;
  right: 10px;
  background-size: initial;
}
.layerPop .layerPop-con {
  width: 100%;
  padding: 20px 20px;
  height: 142px;
  margin-bottom: 18px;
}

.layerPop2 {
  width: 810px;
  position: absolute;
  top: 45%;
  right: 15%;
  transform: translateX(-50%);
  background: #fff;
  z-index: 100;
  padding-bottom: 20px;
  border: 1px solid #e7e7e7;
  display: block;
}
.layerPop2 .layerPop-header {
  width: 100%;
  min-height: 60px;
  line-height: 60px;
  padding-left: 20px;
  background: #1b88e6;
  position: relative;
}
.layerPop2 .layerPop-header strong {
  float: left;
  font-size: 23px;
  font-weight: 500;
  color: #fff;
}
.layerPop2 .layerPop-header button {
  position: absolute;
  width: 32px;
  height: 32px;
  background: url('/img/cmn/ico_pop_close.png') no-repeat center;
  font-size: 0;
  top: 14px;
  right: 10px;
  background-size: initial;
}
.layerPop2 .layerPop-con {
  width: 100%;
  padding: 20px 20px;
  height: 180px;
  margin-bottom: 18px;
}

.layerPop3 {
  width: 810px;
  position: absolute;
  top: 28%;
  right: 17%;
  transform: translateX(-50%);
  background: #fff;
  z-index: 100;
  padding-bottom: 20px;
  border: 1px solid #e7e7e7;
  display: block;
}
.layerPop3 .layerPop-header {
  width: 100%;
  min-height: 60px;
  line-height: 60px;
  padding-left: 20px;
  background: #1b88e6;
  position: relative;
}
.layerPop3 .layerPop-header strong {
  float: left;
  font-size: 23px;
  font-weight: 500;
  color: #fff;
}
.layerPop3 .layerPop-header button {
  position: absolute;
  width: 32px;
  height: 32px;
  background: url('/img/cmn/ico_pop_close.png') no-repeat center;
  font-size: 0;
  top: 14px;
  right: 10px;
  background-size: initial;
}
.layerPop3 .layerPop-con {
  width: 100%;
  padding: 20px 20px;
  height: 230px;
  margin-bottom: 18px;
}

.layerPop4 {
  width: 810px;
  position: absolute;
  top: 12%;
  right: -16%;
  transform: translateX(-50%);
  background: #fff;
  z-index: 100;
  padding-bottom: 20px;
  border: 1px solid #e7e7e7;
  display: block;
}
.layerPop4 .layerPop-header {
  width: 100%;
  min-height: 60px;
  line-height: 60px;
  padding-left: 20px;
  background: #1b88e6;
  position: relative;
}
.layerPop4 .layerPop-header strong {
  float: left;
  font-size: 23px;
  font-weight: 500;
  color: #fff;
}
.layerPop4 .layerPop-header button {
  position: absolute;
  width: 32px;
  height: 32px;
  background: url('/img/cmn/ico_pop_close.png') no-repeat center;
  font-size: 0;
  top: 14px;
  right: 10px;
  background-size: initial;
}
.layerPop4 .layerPop-con {
  width: 100%;
  padding: 20px 20px;
  height: 230px;
  margin-bottom: 18px;
}

.layerPop5 {
  width: 810px;
  position: absolute;
  top: 12%;
  right: -16%;
  transform: translateX(-50%);
  background: #fff;
  z-index: 100;
  padding-bottom: 20px;
  border: 1px solid #e7e7e7;
  display: block;
}
.layerPop5 .layerPop-header {
  width: 100%;
  min-height: 60px;
  line-height: 60px;
  padding-left: 20px;
  background: #1b88e6;
  position: relative;
}
.layerPop5 .layerPop-header strong {
  float: left;
  font-size: 23px;
  font-weight: 500;
  color: #fff;
}
.layerPop5 .layerPop-header button {
  position: absolute;
  width: 32px;
  height: 32px;
  background: url('/img/cmn/ico_pop_close.png') no-repeat center;
  font-size: 0;
  top: 14px;
  right: 10px;
  background-size: initial;
}
.layerPop5 .layerPop-con {
  width: 100%;
  padding: 20px 20px;
  height: 388px;
  margin-bottom: 18px;
}

.layerPop6 {
  width: 810px;
  position: absolute;
  top: 41%;
  right: -16%;
  transform: translateX(-50%);
  background: #fff;
  z-index: 100;
  padding-bottom: 20px;
  border: 1px solid #e7e7e7;
  display: block;
}
.layerPop6 .layerPop-header {
  width: 100%;
  min-height: 60px;
  line-height: 60px;
  padding-left: 20px;
  background: #1b88e6;
  position: relative;
}
.layerPop6 .layerPop-header strong {
  float: left;
  font-size: 23px;
  font-weight: 500;
  color: #fff;
}
.layerPop6 .layerPop-header button {
  position: absolute;
  width: 32px;
  height: 32px;
  background: url('/img/cmn/ico_pop_close.png') no-repeat center;
  font-size: 0;
  top: 14px;
  right: 10px;
  background-size: initial;
}
.layerPop6 .layerPop-con {
  width: 100%;
  padding: 20px 20px;
  height: 388px;
  margin-bottom: 18px;
}

.layerPop7 {
  width: 810px;
  position: absolute;
  top: 11%;
  right: 19%;
  transform: translateX(-50%);
  background: #fff;
  z-index: 100;
  padding-bottom: 20px;
  border: 1px solid #e7e7e7;
  display: block;
}
.layerPop7 .layerPop-header {
  width: 100%;
  min-height: 60px;
  line-height: 60px;
  padding-left: 20px;
  background: #1b88e6;
  position: relative;
}
.layerPop7 .layerPop-header strong {
  float: left;
  font-size: 23px;
  font-weight: 500;
  color: #fff;
}
.layerPop7 .layerPop-header button {
  position: absolute;
  width: 32px;
  height: 32px;
  background: url('/img/cmn/ico_pop_close.png') no-repeat center;
  font-size: 0;
  top: 14px;
  right: 10px;
  background-size: initial;
}
.layerPop7 .layerPop-con {
  width: 100%;
  padding: 20px 20px;
  height: 230px;
  margin-bottom: 18px;
}

.layerPop8 {
  width: 810px;
  position: absolute;
  top: 11%;
  right: 2%;
  transform: translateX(-50%);
  background: #fff;
  z-index: 100;
  padding-bottom: 20px;
  border: 1px solid #e7e7e7;
  display: block;
}
.layerPop8 .layerPop-header {
  width: 100%;
  min-height: 60px;
  line-height: 60px;
  padding-left: 20px;
  background: #1b88e6;
  position: relative;
}
.layerPop8 .layerPop-header strong {
  float: left;
  font-size: 23px;
  font-weight: 500;
  color: #fff;
}
.layerPop8 .layerPop-header button {
  position: absolute;
  width: 32px;
  height: 32px;
  background: url('/img/cmn/ico_pop_close.png') no-repeat center;
  font-size: 0;
  top: 14px;
  right: 10px;
  background-size: initial;
}
.layerPop8 .layerPop-con {
  width: 100%;
  padding: 20px 20px;
  height: 500px;
  margin-bottom: 18px;
}

.pop-bg {
  display: none;
  width: 100%;
  height: 100%;
  position: fixed;
  background: rgba(0, 0, 0, 0.4);
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
}
.pop-table {
  width: 100%;
}
.pop-table thead th {
  font-weight: 500;
  padding: 10px 5px;
  text-align: center;
  background: #505050;
  font-size: 14px;
  color: #fff;
  border-right: 1px solid #707070;
}
.pop-table thead tr th:last-child {
  border-right: 0;
}
.pop-table tbody th {
  background: #f3f3f3;
  border-bottom: 1px solid #dfdfdf;
  padding: 10px 5px;
  border-top: 1px solid #505050;
  font-weight: 500;
}
.pop-table tbody td {
  text-align: center;
  padding: 10px 5px;
  background: #fff;
  font-size: 14px;
  border-bottom: 1px solid #dfdfdf;
  border-right: 1px solid #dfdfdf;
  font-weight: 300;
  border-top: 1px solid #505050;
}
.pop-table tbody tr td:last-child {
  border-right: 0;
}
.pop-search-box {
  padding: 12px 16px;
  margin-bottom: 10px;
  padding-top: 10px;
  border-radius: 5px;
}
.pop-search-box .pop-search-name {
  margin-right: 10px;
  font-weight: 600;
  display: inline-block;
  vertical-align: middle;
  margin-top: 5px;
}
.pop-search-box select {
  background: transparent;
  font-size: 14px;
  border: 0;
  border-bottom: 1px solid #3a3a3a;
  min-width: 104px;
  font-weight: 500;
  color: #545454;
}
.pop-search-box .search-form {
  border-bottom: 1px solid #3a3a3a;
  height: 35px;
}
.pop-search-box .search-form input[type='text'] {
  background: transparent;
  height: 100%;
  width: 180px;
}
.pop-search-box .search-form input[type='text']::-webkit-input-placeholder {
  font-size: 13px;
  font-weight: 500;
  color: #545454;
}

.layerPop .cellFormBtn {
  border-radius: 4px;
  font-size: 12px;
  height: inherit;
  padding: 7px 8px;
  background: #2672d1;
}

/*view Table*/
.viewTable .viewTop {
  border-top: 1px solid #3a3a3a;
  padding: 40px;
}
.viewTable .viewTop .viewTit {
  font-size: 24px;
  margin-bottom: 20px;
  display: block;
}
.viewTable .viewTop .dateInfo ol li {
  float: left;
  margin-right: 50px;
}
.viewTable .viewTop .dateInfo ol li em {
  position: relative;
  margin-right: 40px;
}
.viewTable .viewTop .dateInfo ol li em:after {
  position: absolute;
  content: '';
  top: 5px;
  right: -21px;
  width: 1px;
  height: 14px;
  background: #979797;
}
.viewTable .viewTop .dateInfo ol li span {
  text-align: left;
  color: #787878;
}

.viewTable .viewCon {
  padding: 40px;
  border-top: 1px solid #e8e8e8;
}
.viewTable .viewCon p {
  font-weight: 300;
  font-size: 16px;
  line-height: 2;
}

.view-attachFile {
  background: #f3f3f3;
  padding: 40px;
}
.view-attachFile strong {
  float: left;
  margin-right: 20px;
  font-size: 16px;
  font-weight: 500;
}
.view-attachFile .attachFile {
  float: left;
}
.attachFile {
  display: inline-block;
}
.attachFile .attachFile-name {
  display: inline-block;
  vertical-align: middle;
  font-size: 15px;
  background: url('/img/cmn/ico_clip1.png') no-repeat left center;
  padding-left: 20px;
  background-size: 14px auto;
  color: #787878;
  margin-right: 5px;
}
.attachFile .attachFile-name:hover {
  text-decoration: underline;
}
.attachFile .del-btn {
  display: inline-block;
  vertical-align: middle;
  width: 14px;
  height: 14px;
  font-size: 0;
  background: url('/img/cmn/ico_del_btn.png') no-repeat center;
}

/*write Table*/
.writeTable {
  width: 100%;
}
.writeTable tr:first-child th,
.writeTable tr:first-child td {
  border-top: 1px solid #bdbdbd;
}
.writeTable th {
  padding: 18px 5px;
  font-weight: 500;
  font-size: 15px;
  border-bottom: 1px solid #dbdbdb;
  background-color: #f9f9f9;
  text-align: left;
  padding-left: 25px;
}
.writeTable td {
  font-size: 15px;
  border-bottom: 1px solid #dbdbdb;
  padding: 12px 5px;
  padding-left: 25px;
}

.writeTable .board-attachFile td {
  height: 80px;
}

.board-attachFile .formFile-btn {
  background-image: none;
  padding: 10px 20px;
  display: inline-block;
  vertical-align: middle;
}
.board-attachFile .attachFile {
  display: inline-block;
  vertical-align: middle;
  margin-right: 20px;
}

.writeTable .attachFile-name {
  background-image: none;
  color: #adadad;
}
.writeTable .attachFile-name:hover {
  text-decoration: none;
}

/*edit Table*/
.editTable {
  width: 100%;
}
.editTable tr:first-child th,
.editTable tr:first-child td {
  border-top: 1px solid #bdbdbd;
}
.editTable th {
  padding: 18px 5px;
  font-weight: 500;
  font-size: 15px;
  border-bottom: 1px solid #dbdbdb;
  background-color: #f9f9f9;
  text-align: left;
  padding-left: 25px;
}
.editTable td {
  font-size: 15px;
  border-bottom: 1px solid #dbdbdb;
  padding: 12px 5px;
  padding-left: 25px;
}

.editTable .board-attachFile td {
  height: 80px;
}

.editTable .formFile-btn {
  background-image: none;
  padding: 10px 20px;
  display: inline-block;
  vertical-align: middle;
}
.editTable .attachFile {
  display: inline-block;
  vertical-align: middle;
  margin-right: 20px;
}

.editTable .attachFile-name {
  background-image: none;
  color: #adadad;
  padding: 0;
}
.editTable .attachFile-name:hover {
  text-decoration: none;
}

/*종합현황판*/

.contents_wrap {
  position: relative;
  padding: 10px;
  margin: 35px 0;
}

.subWrap {
  height: auto;
}

.header {
  width: 100%;
  height: 85px;
  position: relative;
  padding: 0 2%;
  background: #3d3d3d;
}

.contents {
  width: 100%;
  height: auto;
  min-height: auto;
  position: relative;
}

.header .logo {
  position: absolute;
  top: 0;
  left: 30px;
  height: 100%;
}

.header .logo a {
  height: 100%;
  display: block;
  width: 100%;
}

.header .logo a img {
  position: absolute;
  top: 19px;
  left: 18px;
}

.header .gnb {
  position: absolute;
  top: 14px;
  left: 515px;
}

.header .gnb > ul {
  text-align: center;
  font-size: 0;
}

.header .gnb > ul > li {
  float: left;
  position: relative;
  height: 100%;
}

.header .gnb > ul > li:hover:after {
  content: '';
  position: absolute;
  bottom: 0px;
  left: 50%;
  transform: translateX(-50%);
  width: 100%;
  height: 5px;
  background: #41b5b2;
}

.header .gnb > ul > li.on:after {
  content: '';
  position: absolute;
  bottom: 0px;
  left: 50%;
  transform: translateX(-50%);
  width: 100%;
  height: 5px;
  background: #41b5b2;
  color: #41b5b2;
}

.header .gnb > ul > li > a {
  font-size: 20px;
  color: #000;
  font-weight: 500;
  padding: 0 100px;
  height: 100%;
  line-height: 70px;
}

.header .util {
  position: absolute;
  top: 35px;
  right: 35px;
}

.header .util ul li {
  float: left;
  position: relative;
}

.header .util > ul > li > a {
  font-size: 15px;
  font-weight: 400;
  text-transform: capitalize;
  padding: 4px 27px;
  background: url('/img/system/ico_login_id_white.png') no-repeat;
}

.header .util ul li a span {
  margin-left: 8px;
}

.header .util ul li .login {
  background: url('/img/system/ico_login_id.png') no-repeat;
}

.header .util ul li .data {
  background: #808080;
  padding: 11px 22px 9px;
  border-radius: 20px;
  color: #ffffff;
}

.util ul .user-menu ul {
  position: absolute;
  top: 44px;
}

.dropdown-menu {
  position: absolute;
  top: 100%;
  left: 0;
  z-index: 1000;
  display: none;
  float: left;
  min-width: 160px;
  padding: 5px 0;
  margin: 2px 0 0;
  font-size: 14px;
  text-align: left;
  list-style: none;
  background-color: #fff;
  -webkit-background-clip: padding-box;
  background-clip: padding-box;
  border: 1px solid rgba(0, 0, 0, 0.15);
  border-radius: 4px;
  -webkit-box-shadow: 0 6px 12px rgba(0, 0, 0, 0.175);
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.175);
}

.open > .dropdown-menu {
  display: block;
}

#location {
  position: relative;
  z-index: 20;
  width: 100%;
  margin: 20px 0 5px;
  padding: 10px 0 0px;
  background: url(/common/tmpl/skinD/img/pattern.png) repeat-x left bottom;
  text-align: left;
}

.modal-open #location {
  z-index: 10;
}

#location:after {
  display: block;
  clear: both;
  visibility: hidden;
  content: '';
}

#location .page__title {
  float: left;
  line-height: 1.3;
  margin: 10px 0 0;
  font-size: 24px;
  font-weight: 700;
  font-family: 'Gmarket Sans';
  color: #3e434a;
}

#location .utile_wrap {
  display: block;
  float: right;
  margin: 10px 0 0 20px;
}

#location .utile_wrap:after {
  display: block;
  clear: both;
  visibility: hidden;
  content: '';
}

#location .utile_wrap .print_wrap {
  display: inline-block;
  position: relative;
  top: -3px;
}

#location .utile_wrap .print_wrap:before {
  display: inline-block;
  float: left;
  content: '';
  width: 1px;
  height: 12px;
  margin: 5px 10px 0;
  background: #c0c0c0;
  vertical-align: middle;
}

#location .utile_wrap .print_wrap:after {
  display: block;
  clear: both;
  visibility: hidden;
  content: '';
}

#location .utile_wrap .print_wrap > li {
  float: left;
  margin-left: 7px;
}

#location .utile_wrap .print_wrap > li .tmpl_ir {
  background: url(/common/tmpl/skinD/img/ico_ir.png) no-repeat;
  font-size: 0;
}

#location .utile_wrap .print_wrap > li > button {
  display: block;
  width: 32px;
  height: 20px;
  transition: all 0.3s ease-out;
  -webkit-transition: all 0.3s ease-out;
}

#location .utile_wrap .print_wrap > li.sns {
  position: relative;
}

#location .utile_wrap .print_wrap > li.sns > button {
}

#location .utile_wrap .print_wrap > li.sns > button span {
  display: block;
  width: 17px;
  height: 100%;
  background-position: 0 -108px;
}

#location .utile_wrap .print_wrap > li.sns > div {
  display: none;
  position: absolute;
  right: 6px;
  top: -4px;
  width: 32px;
  text-align: right;
}

#location .utile_wrap .print_wrap > li.sns.on > div {
  display: block;
}

#location .utile_wrap .print_wrap > li.sns > div button {
  display: block;
  width: 32px;
  height: 32px;
  margin: 0 0 10px;
  border-radius: 50%;
}

#location .utile_wrap .print_wrap > li.sns > div .fa {
  background-position: 0 -174px;
}

#location .utile_wrap .print_wrap > li.sns > div .tw {
  background-position: 0 -214px;
}

#location .utile_wrap .print_wrap > li.sns > div .bl {
  background-position: -105px -215px;
}

#location .utile_wrap .print_wrap > li.sns > div .band {
  background-position: -35px -215px;
}

#location .utile_wrap .print_wrap > li.sns > div .ca {
  background-position: -70px -215px;
}

#location .utile_wrap .print_wrap > li.sns > div .kakao {
  background-position: -140px -215px;
}

#location .utile_wrap .print_wrap > li.sns > div .clickClose {
  width: 32px;
  height: 32px;
  margin: 0 0 10px;
  border-radius: 50%;
  background: url(/common/tmpl/skinD/img/ico_ir.png) no-repeat -21px 0;
}

#location .utile_wrap .print_wrap > li.sns > div .clickClose span {
  display: block;
  width: 100%;
  height: 100%;
  background-position: 0 -134px;
}

#location .utile_wrap .print_wrap > li.n2 > button span {
  display: block;
  width: 21px;
  height: 100%;
  background-position: -20px -108px;
}

#location .utile_wrap .print_wrap > li.n3 > button span {
  display: block;
  width: 19px;
  height: 100%;
  background-position: -44px -108px;
}

#location .utile_wrap .location_wrap {
  display: inline-block;
  position: relative;
}

#location .utile_wrap .location_wrap:after {
  display: block;
  clear: both;
  visibility: hidden;
  content: '';
}

#location .utile_wrap .location_wrap li {
  float: left;
  position: relative;
}

#location .utile_wrap .location_wrap li:not(:last-child):after {
  display: inline-block;
  content: '';
  margin: 10px 12px 0;
  border-top: 3px solid transparent;
  border-bottom: 3px solid transparent;
  border-left: 5px solid #c5c5c5;
  vertical-align: top;
}

#location .utile_wrap .location_wrap li a {
  display: inline-block;
  position: relative;
  line-height: 25px;
  vertical-align: top;
  font-size: 15px;
}

#location .utile_wrap .location_wrap li.home a {
  width: 16px;
  height: 17px;
  margin: 4px 0 0;
  background: url(/common/tmpl/skinD/img/ico_ir.png) no-repeat -21px 0;
  font-size: 0;
}

#location .utile_wrap .location_wrap li:last-child a {
  font-weight: bold;
}

.menuLeft.on {
  background: #303030;
  color: #fff;
}

.menuRight.on {
  display: block;
}

.menuAll {
  margin: 15px 0px;
  transition: all 0.3s ease 0s;
  padding: 22px 24px;
  background: #eef7f7;
  border-radius: 5px 5px 0 0;
  width: 59%;
}

.browser {
  margin: 15px 0px;
  transition: all 0.3s ease 0s;
  padding: 25px;
  background: #eef7f7;
  width: 39%;
  border-radius: 5px 5px 0 0;
}

.formhd {
  background: url(/img/sub/subtit.png) no-repeat left;
}
.formhd-tit {
  font-size: 18px;
  padding-left: 23px;
}

.dayAll {
  margin: 15px 0px;
  transition: all 0.3s ease 0s;
  padding: 22px 24px;
  background: #eef7f7;
  border-radius: 5px 5px 0 0;
  width: 32%;
}

.monthAll {
  margin: 15px 0px;
  transition: all 0.3s ease 0s;
  padding: 22px 24px;
  background: #eef7f7;
  border-radius: 5px 5px 0 0;
  width: 32%;
}

.userAll {
  margin: 15px 0px;
  transition: all 0.3s ease 0s;
  padding: 22px 24px;
  background: #eef7f7;
  border-radius: 5px 5px 0 0;
  width: 32%;
}

.statsAll {
  transition: all 0.3s ease 0s;
  padding: 22px 24px;
  background: #1b88e614;
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 78px;
}

.toggleBar {
  display: flex;
  align-items: center;
  float: left;
}

.toggleSwitch {
  width: 52px;
  height: 28px;
  display: block;
  position: relative;
  border-radius: 30px;
  background-color: #fff;
  cursor: pointer;
  border: 1px solid #e2e2e2;
  margin-bottom: 3px;
  margin: auto;
}

.toggleSwitch .toggleButton {
  width: 20px;
  height: 20px;
  position: absolute;
  top: 50%;
  left: 2px;
  transform: translateY(-50%);
  border-radius: 50%;
  background: #7e7e7e;
}

.toggleBox {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 3px;
}

.toggleBox span {
  padding-left: 10px;
}

#recSt:checked ~ .toggleSwitch {
  background: #1c8ae8;
}

#recSt:checked ~ .toggleSwitch .toggleButton {
  left: calc(100% - 23px);
  background: #fff;
}

#toggle:checked ~ .toggleSwitch {
  background: #1c8ae8;
}

#toggle:checked ~ .toggleSwitch .toggleButton {
  left: calc(100% - 23px);
  background: #fff;
}

.toggleSwitch,
.toggleButton {
  transition: all 0.2s cubic-bezier(0.55, 0.06, 0.68, 0.19);
}

.inforAll {
  width: 49%;
  box-shadow: 0 0 20px 0px #00000026;
  margin: 15px;
}
.inforAll .inforArea {
  border: 1px solid #fff;
  padding: 20px;
  height: 245px;
  background: #fff;
}

.multi-wrapper {
  border: 1px solid #ccc;
  border-radius: 3px;
  overflow: hidden;
  width: 100%;
  margin: 15px 30px;
}

.multi-wrapper .non-selected-wrapper,
.multi-wrapper .selected-wrapper {
  box-sizing: border-box;
  display: inline-block;
  height: 200px;
  overflow-y: scroll;
  padding: 10px;
  vertical-align: top;
  width: 100%;
}

.multi-wrapper .non-selected-wrapper {
  background: #fafafa;
  border-right: 1px solid #ccc;
}

.multi-wrapper .selected-wrapper {
  background: #fff;
}

.multi-wrapper .item {
  cursor: pointer;
  display: block;
  padding: 5px 10px;
}

.multi-wrapper .item:hover {
  background: #ececec;
  border-radius: 2px;
}

.multi-wrapper .search-input {
  border: 0;
  border-bottom: 1px solid #ccc;
  border-radius: 0;
  display: block;
  font-size: 1em;
  margin: 0;
  outline: 0;
  padding: 10px 20px;
  width: 100%;
}

.multi-wrapper .non-selected-wrapper .item.selected {
  opacity: 0.5;
}

.container {
  box-sizing: border-box;
  margin: 0 auto;
  padding: 0 20px;
  width: 100%;
  background: transparent url(/img/database/switch.png) no-repeat 50% 50%;
  border-top: 2px solid #333333;
}

.multiSection {
  display: flex;
}

.modal div {
  z-index: 9999;
}

.modal {
  position: fixed;
  left: 0;
  top: 0;
  width: 100%;
  height: 100%;
  overflow: hidden;
  background-color: rgba(0, 0, 0, 0.4);
}

.modal-none {
  opacity: 0;
  z-index: -999;
}
.modal-show {
  opacity: 1;
  z-index: 9998;
}

/* layout.css */
@charset "utf-8";
html,
body {
  width: 100%;
  height: auto;
}

.subWrap {
  height: auto;
}

.header {
  width: 100%;
  height: 72px;
  position: fixed;
  background: linear-gradient(270deg, #055ab5, #1d8be9);
  z-index: 3000;
}

.contents {
  width: 100%;
  height: auto;
  min-height: 100vh;
  position: relative;
}

.header .logo {
  position: absolute;
  top: 0;
  height: 100%;
}

.header .logo .logo-title {
  color: white;
  font-size: 20px;
}

.header .logo a {
  height: 100%;
  display: block;
  width: 100%;
  display: flex;
  align-items: center;
}

.header .logo a img {
  position: absolute;
  top: 22px;
  left: 15px;
}

.header .gnb {
  position: absolute;
  top: 0;
  left: 410px;
}

.header .gnb > ul {
  text-align: center;
  font-size: 0;
}

.header .gnb > ul > li {
  float: left;
  position: relative;
  height: 100%;
}

.header .gnb > ul > li:hover:after {
  content: '';
  position: absolute;
  bottom: 0px;
  left: 50%;
  transform: translateX(-50%);
  width: 100%;
  height: 5px;
  background: #3d6ad6;
}

.header .gnb > ul > li.on:after {
  content: '';
  position: absolute;
  bottom: 0px;
  left: 50%;
  transform: translateX(-50%);
  width: 100%;
  height: 5px;
  background: #3d6ad6;
}

.header .gnb > ul > li > a {
  font-size: 17px;
  color: #000;
  font-weight: 500;
  padding: 0 28px;
  height: 100%;
  line-height: 70px;
}

.header .util {
  position: absolute;
  top: 26px;
  right: 30px;
}

.header .util ul li {
  float: left;
  position: relative;
}

.header .util ul li:after {
  position: absolute;
  content: '';
  width: 1px;
  height: 15px;
  background: #ffffff;
  top: 0px;
  left: 1px;
}

.header .util ul li:nth-child(1):after {
  width: 0;
}

.header .util ul li a {
  font-size: 15px;
  font-weight: 400;
  text-transform: capitalize;
  padding: 0 15px;
}

.user-footer div a {
  line-height: 40px;
  border-color: white;
}

.sub-open-btn {
  display: none;
  position: absolute;
  top: 25px;
  right: 26px;
  width: 26px;
  height: 22px;
  background: url('/img/mobile/ico_m_btn.png') no-repeat left top;
  font-size: 0;
}

.lnb_depth2__list > li > a {
  position: relative;
  display: block;
  line-height: 1.2;
  padding: 16px 35px 13px 20px;
}

.lnb_depth2__list > li > a:before {
  display: block;
  position: absolute;
  z-index: 1;
  right: 18px;
  top: 22px;
  content: '';
  width: 6px;
  height: 6px;
  border: solid #585c62;
  border-width: 0 1px 1px 0;
}

.lnb_depth2__list > li > a:after {
  display: block;
  position: absolute;
  right: 10px;
  top: 15px;
  content: '';
  width: 19px;
  height: 19px;
  /* border: 1px solid #cacaca; */
  border-radius: 50%;
}

.lnb_depth2__list > li > a[target='_blank']:before {
  display: inline-block;
  content: '';
  right: 15px;
  width: 11px;
  height: 12px;
  border: none;
  background: url(/common/tmpl/skinD/img/target.png) no-repeat;
  vertical-align: middle;
  transform: none;
  -webkit-transform: none;
}

.lnb_depth2__list > li > a[target='_blank']:after {
  display: none;
}

.lnb_depth2__list > li > a > span {
  position: relative;
  display: inline-block;
  line-height: 1.2;
  word-wrap: break-word;
  word-break: keep-all;
  font-size: 17px;
  font-weight: 500;
  color: #808080;
}

.lnb_depth2__list > li.on {
  padding: 0;
  margin-bottom: -2px;
  border-top: 0px;
  border-bottom: 0px;
}

.lnb_depth2__list > li.on > a {
}

.lnb_depth2__list > li.on > a:before {
  border-color: #fff;
  border-width: 0px 1px 1px 0;
}

.lnb_depth2__list > li.on > a > span {
  color: #fff;
  font-weight: 500;
}

.lnb_depth2__list .lnb_depth3__list {
  display: none;
  overflow: hidden;
  height: 0;
  margin: 0 10px 13px;
  text-align: left;
  transition: all 0.4s;
  -webkit-transition: all 0.4s;
  background: #152338;
}

.lnb_depth2__list .lnb_depth3__list > li {
  position: relative;
}

.lnb_depth2__list .lnb_depth3__list > li:before {
  background: url(/img/cmn/lnb3.png) no-repeat top 0px left 0px !important;
}

.lnb_depth2__list .lnb_depth3__list > li:last-child {
  background: none;
}

.lnb_depth2__list .lnb_depth3__list > li.on {
  background: #025bb466;
}

.lnb_depth2__list .lnb_depth3__list > li > a {
  display: block;
  position: relative;
  z-index: 2;
}

.lnb_depth2__list .lnb_depth3__list > li > a:after {
  display: block;
  position: absolute;
  right: 18px;
  top: 50%;
  width: 5px;
  height: 5px;
  margin-top: -3px;
  border: solid #cecfd1;
  border-width: 1px 1px 0 0;
  transform: rotate(45deg);
  -webkit-transform: rotate(45deg);
}

.lnb_depth2__list .lnb_depth3__list > li.on > a:after {
}

.lnb_depth2__list .lnb_depth3__list > li > a[target='_blank']:after {
  right: 5px;
  width: 11px;
  height: 12px;
  margin-top: -6px;
  border: none;
  background: url(/common/tmpl/skinD/img/target.png) no-repeat;
  transform: none;
  -webkit-transform: none;
}

.lnb_depth2__list .lnb_depth3__list > li > span {
  display: inline-block;
  position: relative;
  font-size: 16px;
  color: #3e434a;
}

.lnb_depth2__list .lnb_depth3__list > li.on > a span {
  font-weight: 500;
  color: #2c7b88;
}

.lnb_depth2__list > li > a:hover .lnb_depth3__list,
.lnb_depth2__list > li > a:focus .lnb_depth3__list,
.lnb_depth2__list > li.on .lnb_depth3__list,
.lnb_depth2__list > li.ov .lnb_depth3__list,
.lnb_depth3__list.on,
.lnb_depth3__list.open {
  display: block !important;
  visibility: visible;
  height: auto;
  margin-top: 20px;
}

.lnb_depth2__list > li.on .lnb_depth3__list {
  margin: 0px;
}

.lnb {
  width: 290px;
  background: #1d3150;
  height: 100%;
  position: fixed;
  top: 60px;
  padding-top: 30px;
  z-index: 2000;
}

.lnb ul li {
  position: relative;
  transition: 0.2s;
}

.lnb > ul > ul > li:not(.on) > a:hover {
  background: #04101c;
}

.lnb ul li a {
  position: relative;
}

.lnb > ul > ul > li.on,
.lnb > ul > ul > li:has(li.on) > a,
.lnb > ul > ul > li:has(li.on) > a:hover {
  background: #045bb4;
}

.treeview-menu > li:not(.on) {
  background: #04101c;
}

.treeview > ul {
  background: #025bb466;
}

.lnb ul li:before {
  position: absolute;
  content: '';
  top: 16px;
  left: 27px;
}

.lnb ul li a:before {
  position: absolute;
  content: '';
  top: 18px;
  left: 59px;
  background: #ffffff;
  width: 1px;
  height: 17px;
}

.lnb ul li.treeview:after {
  position: absolute;
  content: '';
  width: 5px;
  height: 10px;
  background: url('/img/cmn/ico_lnb_arrow.png') no-repeat center;
  top: 23px;
  right: 25px;
}

.lnb ul li.treeview.active:after {
  transform: rotate(90deg);
}

.lnb ul li:nth-child(1):before {
  width: 20px;
  height: 20px;
  background: url('/img/cmn/ico_lnb1.png');
  z-index: 99;
}

.lnb ul li:nth-child(2):before {
  width: 20px;
  height: 20px;
  background: url('/img/cmn/ico_lnb2.png');
  z-index: 99;
}

.lnb ul li:nth-child(3):before {
  width: 20px;
  height: 20px;
  background: url('/img/cmn/ico_lnb3.png');
  z-index: 99;
}

.lnb ul li:nth-child(4):before {
  width: 20px;
  height: 20px;
  background: url('/img/cmn/ico_lnb4.png');
  z-index: 99;
}

.lnb ul li:nth-child(5):before {
  width: 20px;
  height: 20px;
  background: url('/img/cmn/ico_lnb5.png');
  left: 26px;
  z-index: 99;
}

.lnb ul li:nth-child(6):before {
  width: 17px;
  height: 18px;
  background: url('/img/cmn/ico_lnb6.png');
  z-index: 99;
}

.lnb ul li:nth-child(7):before {
  width: 18px;
  height: 20px;
  background: url('/img/cmn/ico_lnb7.png');
  z-index: 99;
}

.lnb ul li:nth-child(8):before {
  width: 19px;
  height: 17px;
  background: url('/img/cmn/ico_lnb8.png');
  z-index: 99;
}

.lnb ul li:nth-child(9):before {
  width: 18px;
  height: 18px;
  background: url('/img/cmn/ico_lnb9.png');
  z-index: 99;
}

.lnb ul li:nth-child(10):before {
  width: 18px;
  height: 18px;
  background: url('/img/cmn/ico_lnb10.png');
  z-index: 99;
}

.lnb ul li:nth-child(11):before {
  width: 18px;
  height: 18px;
  background: url('/img/cmn/ico_lnb11.png');
  z-index: 99;
}

.lnb ul li a {
  color: #fff;
  font-weight: 400;
  padding: 19px 0;
  display: block;
  padding-left: 75px;
}

.contentsBody {
  float: left;
  width: 100%;
  min-height: 100%;
  padding: 115px 60px 30px 350px;
  position: relative;
}

.footer {
  border-top: 1px solid #dfdfdf;
  clear: left;
  background: #ffffff;
  position: relative;
  z-index: 5000;
}

.footer .footerLink {
  padding: 0 2%;
  border-bottom: 1px solid #dfdfdf;
}

.footer .footerLink ul li {
  float: left;
  position: relative;
}

.footer .footerLink ul li:after {
  position: absolute;
  content: '';
  top: 19px;
  left: 0;
  width: 1px;
  height: 12px;
  background: #bdbdbd;
}

.footer .footerLink ul li:first-child:after {
  width: 0;
}

.footer .footerLink ul li a {
  line-height: 50px;
  display: block;
  color: #000;
  padding: 0 15px;
}

.footer .footerInfo {
  padding: 0 2%;
  padding-top: 30px;
  padding-bottom: 30px;
}

.footer .footerInfo img {
  float: left;
  margin-right: 50px;
  margin-top: 22px;
}

.footer .footerInfo .footerTxt {
  float: left;
  font-weight: 300;
  line-height: 1.5;
}

.footer .footerInfo .selectSiteMap {
  float: right;
}

.m-menu-bg {
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.8);
  position: fixed;
  top: 0px;
  right: 0px;
  z-index: 3000;
  display: none;
  bottom: 0px;
  left: 0px;
}

.m-menu-con {
  position: fixed;
  top: 0px;
  bottom: 0;
  right: -100%;
  width: 80%;
  max-width: 460px;
  height: 100vh;
  background: rgb(255, 255, 255);
  z-index: 3300;
  display: none;
}

.m-menu-con .m-header-top {
  background: linear-gradient(44deg, #208be9, #055cb5);
  padding: 70px 20px 20px;
  position: relative;
}

.m-menu-con .m-header-top p {
  color: rgb(255, 255, 255);
  font-size: 20px;
  line-height: 27px;
  font-weight: 500;
}

.m-menu-con .m-header-top strong {
  display: block;
}

.m-menu-con .m-header-top .sub-open-close {
  width: 31px;
  height: 31px;
  position: absolute;
  top: 20px;
  right: 20px;
  border-radius: 50px;
  background: url('/img/mobile/ico_close_btn.png') left top no-repeat;
}

.m-menu-con .m-header-top .m-sub-btn {
  margin-top: 20px;
}

.m-menu-con .m-header-top .m-sub-btn a {
  color: rgb(255, 255, 255);
  display: inline-block;
  border: 1px solid rgb(255, 255, 255);
  padding: 7px 20px 4px 20px;
}

.m-menu-con .m-header-top .m-sub-btn .btn-login,
.m-menu-sub-btn {
  margin-right: 8px;
}

.m-menu-con .area-tit {
  width: 100%;
  height: 60px;
  display: block;
  background: rgb(32, 36, 41);
  color: rgb(255, 255, 255);
  text-align: center;
  line-height: 60px;
  font-size: 17px;
}

.m-menu-con .m-menu-area {
  background: rgb(43, 48, 54);
  height: 100%;
  position: relative;
}

.m-menu-area > ul {
  overflow: auto;
  height: 100%;
  background: #e5e5e5;
}

.m-menu-area > ul > li > a {
  background: #e5e5e5;
  display: block;
  width: 150px;
  padding: 17px 15px;
  color: #505050;
  font-weight: 500;
}

.m-menu-area .menuRight {
  position: absolute;
  top: 0;
  left: 150px;
  width: calc(100% - 150px);
  background: #fff;
  height: 100vh;
  padding-top: 12px;
  display: none;
}

.m-menu-area .menuRight > ul > li > a {
  color: #141414;
  font-weight: 500;
  font-size: 15px;
  padding: 14px 30px;
  padding-left: 24px;
  display: block;
  word-break: keep-all;
}

.menuLeft.on {
  background: #303030;
  color: #fff;
}

.menuRight.on {
  display: block;
}

.sidebar-menu {
  list-style: none;
  margin: 0;
  padding: 0;
}
.sidebar-menu > li {
  position: relative;
  margin: 0;
  padding: 0;
}
.sidebar-menu > li > a {
  padding: 12px 5px 12px 15px;
  display: block;
}
.sidebar-menu > li > a > .fa,
.sidebar-menu > li > a > .glyphicon,
.sidebar-menu > li > a > .ion {
  width: 20px;
}
.sidebar-menu > li .label,
.sidebar-menu > li .badge {
  margin-top: 3px;
  margin-right: 5px;
}
.sidebar-menu li.header {
  padding: 10px 25px 10px 15px;
  font-size: 12px;
}
.sidebar-menu li > a > .fa-angle-left {
  width: auto;
  height: auto;
  padding: 0;
  margin-right: 10px;
  margin-top: 3px;
}
.sidebar-menu li.active > a > .fa-angle-left {
  -webkit-transform: rotate(-90deg);
  -ms-transform: rotate(-90deg);
  -o-transform: rotate(-90deg);
  transform: rotate(-90deg);
}
.sidebar-menu li.active > .treeview-menu {
  display: block;
  padding: 0;
}
.sidebar-menu .treeview-menu {
  display: none;
  list-style: none;
  padding: 0;
  margin: 0;
}
.sidebar-menu .treeview-menu .treeview-menu {
  padding-left: 20px;
}
.sidebar-menu .treeview-menu > li {
  margin: 0;
  /* background:url(../images/btn_over.png) 5px 9px no-repeat */
}
/*.sidebar-menu .treeview-menu > li > a {
  padding: 0 3px 5px 18px;
  display: block;
  font-size: 14px;
  background-color: #2c3038;
  margin-top: 5px;
  height: 30px;
}*/
.sidebar-menu .treeview-menu > li > a > .fa,
.sidebar-menu .treeview-menu > li > a > .glyphicon,
.sidebar-menu .treeview-menu > li > a > .ion {
  width: 20px;
}
.sidebar-menu .treeview-menu > li > a > .fa-angle-left,
.sidebar-menu .treeview-menu > li > a > .fa-angle-down {
  width: auto;
}

/* modeling.css */
@charset "utf-8";
html,
body {
  width: 100%;
  height: 100%;
}
.subWrap {
  height: 100%;
}
.contents {
  width: 100%;
  position: relative;
  height: 100%;
  min-height: auto;
}
.mapArea {
  width: 100%;
  height: 100%;
  position: relative;
}

.leftBar {
  width: 470px;
  position: absolute;
  top: 65px;
  left: 12px;
  background: #fff;
  border-radius: 20px;
  float: left;
  padding: 30px 20px 20px 20px;
  z-index: 2000;
}
.leftBar strong {
  position: relative;
  display: block;
  background: linear-gradient(45deg, #2f7ed6, #622fd6);
  height: 65px;
  border-radius: 0 0 30px 30px;
  width: 80%;
  margin: 0 auto;
  margin-top: -60px;
  text-align: center;
  color: #fff;
  font-size: 21px;
  font-weight: 500;
  line-height: 63px;
}
.leftBar strong:after {
  position: absolute;
  content: '';
  top: 1px;
  right: -30px;
  width: 0px;
  height: 0px;
  border-top: 30px solid transparent;
  border-left: 30px solid #4320af;
}
.leftBar .adressTxt {
  font-size: 17px;
  font-weight: 500;
  text-align: center;
  position: relative;
  background: #efefef;
  padding: 11px 0;
  width: 80%;
  margin: 15px auto;
  border-radius: 30px;
  padding-left: 20px;
}
.leftBar .adressTxt:after {
  position: absolute;
  content: '';
  top: -10px;
  left: -20px;
  width: 67px;
  height: 63px;
  background: url('/img/modeling/ico_adress.png') no-repeat center;
}
.subTxt {
  text-align: center;
  width: 100%;
  margin: 10px auto;
}
.inputTxt {
  text-align: center;
  width: 100%;
  float: left;
  margin: 10px auto;
}
.inputTxt-sm {
  width: 140px;
}

/*팝업*/
.popupWrap {
  position: absolute;
  top: 40px;
  left: 30px;
  min-width: 240px;
  z-index: 3000;
  box-shadow: 0 0 20px 0px #00000026;
  border-radius: 20px;
}
.popupWrap .pop-header {
  background: linear-gradient(45deg, #2f7ed6, #622fd6);
  border-radius: 10px 10px 0 0;
}
.popupWrap .pop-header p {
  color: #fff;
  float: left;
  font-weight: 400;
  padding: 15px 0;
  padding-left: 20px;
  font-size: 19px;
}
.popupWrap .pop-header .close {
  float: right;
  width: 50px;
  height: 50px;
  background: url('/img/dash/ico_pop_close.png') no-repeat center;
  font-size: 0;
  letter-spacing: -9999px;
}
.popupWrap .pop-con {
  min-height: 120px;
  background: #fff;
  border-radius: 0 0 10px 10px;
  padding: 20px;
  padding-top: 20px;
}
.popupWrap .pop-con p {
  font-size: 16px;
  font-weight: 300;
  text-align: center;
  margin-bottom: 20px;
}

/*탭*/
.cmnTabs.modeling {
  clear: both;
}
.cmnTabs.modeling > ul {
  width: 90%;
  text-align: center;
  margin: 11px auto;
}
.cmnTabs.modeling > ul > li {
  position: relative;
  float: none;
  margin: 3px;
  border-radius: 50px;
  margin-right: 3px;
  display: inline-block;
  vertical-align: middle;
  height: 49px;
  width: 22%;
  padding: 7px;
}
.cmnTabs.modeling > ul > li.current {
  background: #3c76d6;
  border: 0;
}
.cmnTabs.modeling > ul > li.current:after {
  position: absolute;
  content: '';
  top: 44px;
  left: 28px;
  width: 0;
  height: 0;
  border-bottom: 20px solid transparent;
  border-top: 12px solid #3c76d6;
  border-left: 15px solid transparent;
  border-right: 15px solid transparent;
}
.cmnTabs.modeling > ul > li > a {
  display: block;
  color: inherit;
  text-align: left;
  font-weight: 400;
  line-height: 17px;
  text-align: center;
  font-size: 13px;
}
.cmnTabs.modeling .tabCon {
  display: none;
  padding: 10px 20px 0;
  padding-top: 9px;
}
.cmnTabs.modeling > ul > li.current a {
  color: white;
  font-weight: 500;
}
.cmnTabs.modeling .tabCon.current {
  display: block;
}

.modelingTable-box {
  height: 398px;
  overflow: auto;
  margin-bottom: 15px;
  width: 100%;
}
.modelingTable thead tr th {
  background: #f3f3f3;
  padding: 9px;
  border: 1px solid #e3e3e3;
  position: sticky;
  top: 0;
  border-collapse: separate;
  border-spacing: 0;
}
.modelingTable thead tr:nth-child(2) th {
  top: 27px;
  width: 30%;
  font-size: 13px;
}
.modelingTable thead tr:first-child th {
  border-left: 0;
}
.modelingTable thead tr:first-child th:last-child {
  border-right: 0;
  border: 1px solid #e3e3e3;
}
.modelingTable tbody tr td {
  border: 1px solid #e3e3e3;
  padding: 8px;
  text-align: center;
}
.modelingTable tbody tr td:first-child {
  border-left: 0;
}
.modelingTable tbody tr td:last-child {
  border-right: 0;
}

.button-area {
  text-align: center;
  border-top: 1px dotted #959595;
  padding-top: 7px;
}
.button-area button {
  display: inline-block;
  width: 126px;
  height: 35px;
  background: #3c76d6;
  border-radius: 5px;
  color: #fff;
  margin-right: 1px;
}

.button-area button.playBtn {
  display: inline-block;
  width: 100%;
  height: 35px;
  background: #424ec5;
  border-radius: 5px;
  color: #fff;
  margin-right: 1px;
  margin-bottom: 5px;
}
.button-area button.nextBtn {
  display: inline-block;
  width: 100%;
  height: 35px;
  background: #3c76d6;
  border-radius: 5px;
  color: #fff;
  margin-right: 1px;
}
.button-area button.addBtn {
  background: #3c76d6;
}
.button-area button.loadBtn {
  background: #4a56ce;
}
.button-area button.resetBtn {
  background: #545454;
}

.playBtn {
  width: 100%;
  height: 35px;
  background: #424ec5;
  border-radius: 5px;
  color: #fff;
}

.legend {
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translate(-50%, -50%);
}
.legend img {
  display: block;
  width: 100%;
}

.inputForm-group {
  margin-bottom: 10px;
}
.inputForm-group label span {
  font-size: 16px;
  margin-right: 5px;
  display: inline-block;
  min-width: 50px;
}
.inputForm-group label input[type='text'] {
  border: 0;
  background: #f1f1f1;
  border-radius: 5px;
  margin-right: 5px;
  width: 180px;
  margin: 10px;
}

.confirmBtn {
  border-radius: 5px;
  background: #3a6ed6;
  height: 35px;
  min-width: 80px;
  color: #fff;
}

.popupWrap.modflow {
  top: 120px;
  max-width: 573px;
  left: 600px;
}
.popupWrap.modflow .left {
  float: left;
  width: 49%;
  margin-right: 1%;
  height: 100%;
  border: 1px solid re;
}
.popupWrap.modflow .right {
  float: right;
  width: 50%;
  overflow: auto;
  height: 240px;
}
.popupWrap.modflow .calendar-item {
  width: 100%;
}
.popupWrap.modflow .calendar-item .date-picker {
  width: 100%;
}
.popupWrap.modflow .calendar-item .date-picker2 {
  width: 100%;
}

.popupWrap.modflow table {
  margin-top: 0;
}
.popupWrap.modflow table tr th {
  border: 1px solid #e8e8e8;
  border-top: 1px solid #bdbdbd;
}
.popupWrap.modflow table tr td {
  border: 1px solid #e8e8e8;
  padding: 13px 13px;
}

/*강수량 팝업*/
.popupWrap.rainfall {
  left: 500px;
  top: 444px;
  max-width: 250px;
}
.popupWrap.rainfall .left {
  float: left;
  width: 49%;
  margin-right: 1%;
  height: 100%;
  border: 1px solid re;
}
.popupWrap.rainfall .right {
  float: right;
  width: 50%;
  overflow: auto;
  height: 240px;
}
.popupWrap.rainfall .calendar-item {
  width: 100%;
}
.popupWrap.rainfall .calendar-item .date-picker {
  width: 100%;
}
.popupWrap.rainfall .calendar-item .date-picker2 {
  width: 100%;
}

.popupWrap.rainfall table {
  margin-top: 0;
}
.popupWrap.rainfall table tr th {
  border: 1px solid #e8e8e8;
  border-top: 1px solid #bdbdbd;
}
.popupWrap.rainfall table tr td {
  border: 1px solid #e8e8e8;
  padding: 13px 13px;
}

.popupWrap .inputForm-group label span {
  font-size: 16px;
  margin-right: 5px;
  display: inline-block;
  min-width: 10px;
  font-weight: 500;
}
.popupWrap .inputForm-group label input[type='text'] {
  border: 0;
  background: #f1f1f1;
  border-radius: 5px;
  margin-right: 5px;
  width: 100px;
  height: 30px;
}

/*레이어 슬라이드*/
.layer_modal_wrap {
  position: fixed;
  top: 72px;
  z-index: 1000;
  height: 100%;
  left: 600px;
}
.layer_modal_con {
  position: absolute;
  width: 310px;
  height: 100%;
  background: #fff;
  right: 0;
  overflow: auto;
}

.modalTit {
  background: linear-gradient(90deg, #1c8ae8, #065cb6);
  display: block;
  line-height: 51px;
  padding-left: 20px;
  font-size: 19px;
  color: #fff;
  font-weight: 500;
}
/*
.layerForm-box {padding: 10px 10px 0 0;padding-top: 10px;}
.layerForm-box .row {margin: 7px 0;padding: 0 20px 0 20px;display: grid;}
.layerForm-box .row:hover {}
.layerForm-box p {font-size: 15px;}
.layerForm-box span {font-size: 13px;vertical-align: middle;display: inline-block;margin-left: 5px;background: url("../images/dash/layer_tit.png") no-repeat left center;padding-left: 27px;font-weight: 300;}
*/

.layer-content {
  padding: 10px 10px 70px 0;
}
.layer-content .row {
  margin: 7px 0;
  padding: 0 20px 0 20px;
}
.layer-content .row:hover {
}
.layer-content p {
  font-size: 15px;
}
.layer-content span {
  font-size: 13px;
  vertical-align: middle;
  display: inline-block;
  margin-left: 5px;
  font-weight: 300;
}

.layer_btn_change {
  text-align: center;
}
.layer_btn_change .on,
.nav-item.active {
  background: #1b88e61f;
  padding: 12px 20px 10px 20px;
  font-size: 13px;
  border: 1px solid #0c6bc7;
  border-radius: 5px;
  color: #0c6bc7;
  width: 130px;
}
.layer_btn_change .off,
.nav-item {
  background: #f0f0f0;
  padding: 12px 20px 10px 20px;
  font-size: 13px;
  border: 1px solid #8c8c8c;
  border-radius: 5px;
  color: #404040;
  width: 130px;
}

.layer_Zone {
  padding: 25px 25px 100px 25px;
  text-align: center;
}

.row_detail {
  border: 1px solid #f0f0f0;
  padding: 10px 12px;
}

.row_detail .box {
}

.row_detail .box p {
  font-size: 13px;
  padding: 3px 0;
  margin: 5px;
  font-weight: 500;
}

@charset "utf-8";
@media (max-width: 1730px) {
  .header .gnb > ul > li > a {
    font-size: 16px;
  }
  .header .util ul li a {
    font-size: 15px;
  }
}
@media (max-width: 1680px) {
  .header .gnb > ul > li > a {
    font-size: 16px;
    padding: 0 21px;
  }
  .header .util ul li a {
    font-size: 14px;
  }
}
@media (max-width: 1556px) {
  .header .gnb > ul > li > a {
    font-size: 15px;
    padding: 0 15px;
  }
  .header .util ul li a {
    font-size: 14px;
  }
}
@media (max-width: 1440px) {
  .header .gnb {
    left: 360px;
  }
  .header .gnb > ul > li > a {
    padding: 0 13px;
  }
  .header .util ul li a {
    font-size: 14px;
  }
}
@media (max-width: 1340px) {
  .header .logo {
    width: 260px;
  }
  .header .logo a img {
    width: 100%;
  }
  .header .gnb {
    left: 310px;
  }
  .header .gnb > ul > li > a {
    padding: 0 13px;
  }
  .header .util ul li a {
    font-size: 13px;
  }
}
@media (min-width: 920px) {
  .btn-group {
    display: inline-block;
    vertical-align: middle;
  }
}
@media (max-width: 1280px) {
  .header .logo {
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    height: 36px;
  }
  .header .logo a img {
    position: static;
  }
  .sub-open-btn {
    display: block;
  }
  .util {
    display: none;
  }
  .gnb {
    display: none;
  }
  .lnb {
    display: none;
  }
  .contentsBody {
    padding-left: 30px;
  }
  .search-box .searchForm {
    width: calc(100% - 230px);
  }
  .m-menu-con {
    display: block;
  }
  .footer .footerInfo img {
    float: none;
    margin: 0 auto;
    display: block;
    margin-bottom: 30px;
  }
  .footer .footerInfo .footerTxt {
    float: none;
    text-align: center;
  }
  .footer .footerLink ul {
    text-align: center;
  }
  .footer .footerLink ul li {
    float: none;
    display: inline-block;
    vertical-align: middle;
  }
  .footer .footerInfo .selectSiteMap {
    display: none;
  }
  .search-box.searchCondition .formItem {
    width: 100%;
  }
  .search-box.searchCondition.loginData .formItem {
    width: auto;
  }
  .search-box.checkSet .formItem {
    width: auto;
  }
  .search-box.searchCondition .formItem .searchForm {
    width: calc(100% - 230px);
  }
  .search-box.searchCondition .formItem .searchForm.search100 {
    width: calc(100% - 80px);
  }
  .btn-group {
    width: 100%;
    text-align: center;
  }
  .btn-wrap {
    text-align: center !important;
  }
  .layerPop.ycWaterObsrSearch {
    width: 80%;
  }
  .layerPop.ycWaterObsrSearch .colImgBox {
    width: 100%;
    float: none;
  }
  .layerPop.ycWaterObsrSearch .pop-table {
    width: 100%;
    float: none;
  }
  .popupWrap .modflow {
    width: 100%;
    float: none;
  }
  .popupWrap .rainfall {
    width: 100%;
    float: none;
  }
  .popupWrap .pop-con {
    width: 100%;
    float: none;
  }
  .joinProcess ul li {
    width: 48%;
    margin-bottom: 9px;
  }
  .phoneCrtfcCheck-area .phoneCrtfcCheck-box {
    width: 100%;
  }
  .layerPop {
    display: none;
  }
  .popupWrap {
    display: none;
  }
  .ui-widget.ui-widget-content {
    display: none;
  }
}
@media (max-width: 920px) {
  .contentsBody {
    padding: 30px 20px 20px 20px;
  }
  .contentsHd .location {
    margin-top: 6px;
  }
  .search-box .searchForm {
    width: calc(100% - 230px);
  }
  .search-box .btn-group {
    text-align: center;
    margin-top: 15px;
  }
  .option-area .form-tit {
    display: block;
    margin-bottom: 5px;
  }
  .writeTable.conRegister th {
    width: 130px !important;
  }
  .ycWater-infoImg ul li {
    width: 100%;
    height: auto;
    float: none;
    margin-bottom: 20px;
  }
  .ycWater-infoImg ul li:last-child {
    display: none;
  }
  .ycWater-infoImg ul li img {
    height: auto;
  }
  .contentsReserch .reserchHd strong {
    width: 100%;
    display: block;
  }
  .contentsReserch .reserchHd .contact {
    float: none;
  }
  .researchCon p {
    float: none;
    margin-bottom: 10px;
  }
  .researchCon .formGroup {
    float: none;
    width: 100%;
  }
  .layerPop {
    width: 80%;
  }
  .layerPop .layerPop-header strong {
    font-size: 20px;
  }
  .layerPop .popTable-wrap {
    height: 260px;
    overflow-x: auto;
  }
  .layerPop .pop-table {
    width: 1000px;
  }
  .layerPop .layerPop-con {
    height: auto;
  }
  .layerPop.obsrRegister .pop-table {
    width: 100%;
  }
  .layerPop.obsrSearch .pop-table {
    width: 100%;
  }
  .layerPop.obsrSearch .colImgBox {
    height: 200px;
  }
  .layerPop.partData-using table {
    width: 100%;
  }
  .layerPop.gwData-using table {
    width: 100%;
  }
  .layerPop.apiSignUp table {
    width: 100%;
  }
  .joinBox {
    padding: 10% 5% !important;
  }
  .joinBox .formRow.pw input[type='password'] {
    width: 100%;
  }
  .joinBox .formRow.adress .formBox .adressDtl input[type='text'] {
    width: calc(100% - 80px);
  }
  .joinBox .formRow.user input[type='text'] {
    width: 100%;
  }
  .joinBox .formRow.id input[type='text'] {
    width: calc(100% - 90px);
  }
  .joinBox .formRow.adress .formBox .postFind input[type='text'] {
    width: calc(100% - 90px);
  }
  .contents.full.login .contentsBody {
    background: none;
    height: auto;
  }
  .loginWrap {
    width: 90%;
    top: 0;
    left: 0;
    transform: none;
    margin: 0 auto;
  }
  .reset-bar {
    margin-right: 15px !important;
  }
  .reset-bar::after {
    visibility: hidden;
  }
}

@media (max-width: 660px) {
  .search-box .searchForm {
    width: calc(100% - 230px);
  }
  .contentsHd .contentsTit {
    width: 100%;
    float: none;
    text-align: center;
    padding-bottom: 10px;
  }
  .contentsHd .location {
    float: none;
    width: 100%;
    padding-bottom: 20px;
  }
  .contentsHd .location ul {
    text-align: center;
  }
  .contentsHd .location ul li {
    display: inline-block;
    vertical-align: middle;
    float: none;
    margin: 5px 0;
  }
  .search-box.searchCondition .formItem .calendar-item .date-picker {
    width: 132px;
  }
  .search-box.searchCondition .formItem .calendar-item .date-picker2 {
    width: 132px;
  }
  .modelingTable {
    width: 100%;
  }
  .modelingTable thead tr th {
    /* font-size: 20px; */
    padding: 11px;
  }
  .modelingTable tbody tr td {
    /* font-size: 20px; */
    padding: 6px;
  }
  .tableWrap {
    overflow: auto;
  }
  .listTable {
    width: 1080px;
  }
  .listTable.hologramDevice {
    width: 800px;
  }
  .listTable.obsrStat {
    width: 800px;
  }
  .listTable.seawtItrsTb {
    width: 800px;
  }
  .listTable.seaGroundWtItrsTb {
    width: 1300px;
  }
  .listTable.conSetting {
    width: 800px;
  }
  .listTable.conSetting button {
    min-width: 60px;
  }
  .listTable.apiTable {
    width: 800px;
  }
  .layerPop.contentsFeedback table {
    width: 600px;
  }
  .layerPop.permission table {
    width: 600px;
  }
  .layerPop.userSearch table {
    width: 600px;
  }
  .layerPop.conChangeEdit table {
    width: 600px;
  }
  .btn-wrap button {
    width: 40%;
    margin-bottom: 5px;
  }
  .cmnTabs.siteInfo > ul > li {
    width: 50%;
    margin-top: -1px;
  }
  .layerPop.ycWaterObsrSearch .pop-table {
    width: 720px;
  }
  .layerPop.obsrSearch .colImgBox {
    width: 100%;
    height: 170px;
  }
  .phoneCrtfcCheck-area {
    padding: 10% 5%;
  }

  .layerPop.obsrSearch .layerPop-con {
    height: 400px;
  }
  .layerPop.obsrSearch .layerPop-con .popTable-wrap table {
    width: 600px;
  }
  .layerPop.obsrRegister .layerPop-con table {
    width: 600px;
  }
  .layerPop.checkInfo .layerPop-con {
    height: 530px;
  }
  .layerPop.checkInfo .layerPop-con table {
    width: 600px;
  }
  .option .pop-conTit {
    font-size: 15px;
    display: block;
    width: 100%;
    float: none;
    text-align: center;
    border: 1px solid #c7c7c7;
    padding: 8px;
    background: #ebebeb;
  }
  .option .pop-conTit + .userId {
    float: none;
  }
  .layerPop.alramUser table {
    width: 100%;
    font-size: 13px;
  }
  .layerPop.alramUser table button {
    min-width: auto;
  }
  .layerPop.obsrEdit .pop-table {
    width: 600px;
  }
  .layerPop .layerPop-header {
    min-height: 50px;
    line-height: 50px;
  }
  .layerPop .layerPop-header strong {
    font-size: 18px;
  }
  .layerPop .layerPop-header button {
    width: 20px;
    height: 20px;
    right: 16px;
  }
  .layerPop.apiSignUp table {
    width: 550px;
  }
  .layerPop.apiSignUp-sett table {
    width: 550px;
  }

  .loginWrap .loginLogo {
    width: 80%;
  }
  .loginWrap .formTit {
    font-size: 26px;
  }
  .joinBox .formRow .formTxt {
    float: none;
    border: 0;
  }
  .joinBox .formRow {
    border: 0;
    margin: 0;
  }
  .joinBox .formRow .formTxt label {
    min-height: auto;
    line-height: unset;
    background: #fff;
    padding: 9px 0;
  }
  .joinBox .formRow.adress .formTxt label {
    line-height: inherit;
  }
  .joinBox .formRow .formBox {
    float: none;
    width: 100%;
    padding: 0;
  }
  .legendTxt ul li span {
    font-size: 12px;
    display: block;
    position: absolute;
    top: -9px;
    left: 27px;
    font-weight: 600;
  }
  .legendTxt ul li:after {
    position: absolute;
    left: 11px;
    width: 11px;
  }

  .leftBar {
    width: 90%;
    top: 94px;
    left: 26px;
  }
  .leftBar strong {
    font-size: 22px;
  }
  .leftBar .adressTxt {
    /* font-size: 25px; */
    width: 90%;
  }
  .cmnTabs.modeling > ul > li {
    width: 40%;
    height: 49px;
    padding: 8px;
    margin-bottom: 11px;
  }
  .cmnTabs.modeling > ul > li > a {
    font-size: 15px;
    line-height: 16px;
  }

  .inputForm-group label b {
    /* font-size: 25px; */
  }
  .inputForm-group label span {
    /* font-size: 25px; */
    margin: 20px auto;
  }
  .subTxt {
    font-size: 15px;
  }
  .button-area button.nextBtn {
    height: 45px;
    font-size: 15px;
    width: 92%;
  }
  .button-area button.playBtn {
    height: 45px;
    font-size: 15px;
    width: 92%;
  }
  .button-area button.resetBtn {
    height: 45px;
    font-size: 15px;
    width: 30%;
  }
  .button-area button.loadBtn {
    height: 45px;
    font-size: 15px;
    width: 30%;
  }
  .button-area button.addBtn {
    height: 45px;
    font-size: 15px;
    width: 30%;
  }
  .inputForm-group label input[type='text'] {
    margin: 17px;
    padding: 10px;
    width: 180px;
    height: 38px;
  }
  .cmnTabs.modeling > ul > li.current:after {
    display: none;
  }
}

@media (max-width: 640px) {
  .button-area button.nextBtn {
    height: 45px;
    font-size: 15px;
    width: 92%;
  }
  .button-area button.playBtn {
    height: 45px;
    font-size: 15px;
    width: 92%;
  }
  .button-area button.resetBtn {
    height: 45px;
    font-size: 15px;
    width: 30%;
  }
  .button-area button.loadBtn {
    height: 45px;
    font-size: 15px;
    width: 30%;
  }
  .button-area button.addBtn {
    height: 45px;
    font-size: 15px;
    width: 30%;
  }
  .inputForm-group label input[type='text'] {
    margin: 17px;
    padding: 10px;
    width: 180px;
    height: 38px;
  }
  .modelingTable thead tr th {
    padding: 6px;
  }
}

@media (max-width: 559px) {
  .search-box select {
    width: 99%;
    margin: 5px 0;
  }
  .search-box input[type='text'] {
    width: 99%;
  }
  .search-box.searchCondition .formItem {
    width: 100%;
  }
  .search-box.searchCondition .formItem .form-tit {
    min-width: 80px;
    margin-bottom: 5px;
  }
  .search-box.searchCondition .formItem input[type='text'] {
    width: calc(100% - 151px);
  }
  .search-box.searchCondition .formItem select + input[type='text'] {
    width: 99%;
  }
  .search-box.searchCondition.loginData .formItem {
    width: 100%;
  }
  .search-box.searchCondition.loginData .formItem input[type='text'] {
    width: 99%;
  }
  .search-box.searchCondition.checkSet .formItem {
    width: 100%;
  }
  .search-box.searchCondition.checkSet .formItem select {
    width: 99%;
  }

  .search-box.searchCondition .formItem .searchForm.search100 {
    width: 99%;
  }

  .search-box button {
    width: 49%;
    margin-bottom: 5px;
  }
  .search-box button.excelBtn {
    width: 99%;
  }
  .btn-group.onlySearch button {
    width: 99%;
  }
  .search-box button.printingBtn {
    width: 99%;
  }
  .joinProcess ul li {
    width: 99%;
  }
  .joinProcess ul li > div {
    height: 52px;
    padding-top: 11px;
  }
  .joinProcess ul li > div i {
    width: 30px;
    height: 30px;
    background-size: 15px auto !important;
  }
  .joinProcess ul li > div p {
    font-size: 14px;
  }
  .legendBar-area {
    width: 100%;
    float: none;
    display: none;
  }
}
@media (max-width: 500px) {
  .search-box .form-tit {
    width: 100%;
    margin-bottom: 5px;
  }
  .search-box .searchForm {
    width: calc(100% - 150px);
  }
  .search-box .btn-group {
    margin-top: 5px;
    text-align: left;
  }
  .search-box.searchCondition .formItem .searchForm {
    width: calc(100% - 160px);
  }
  .m-menu-area > ul > li > a {
    font-size: 14px;
    width: 140px;
    padding-left: 19px;
  }
  .m-menu-area .menuRight {
    left: 140px;
    width: calc(100% - 140px);
  }
  .m-menu-area .menuRight > ul > li > a {
    padding: 14px 19px;
  }
  .joinBox .formRow .formBox .birth input[type='text'] {
    width: 60px;
  }
  .joinForm .cmnBtn {
    min-width: 49%;
  }
  .layerPop.find {
    width: 80%;
  }
  .pagination ul li {
    margin: 5px 2px;
  }
}
@media (max-width: 420px) {
  .search-box {
    padding: 13px 16px;
  }
  .search-box .form-tit {
    width: 100%;
    margin-bottom: 5px;
  }
  .search-box .searchForm {
    width: calc(100% - 145px);
  }
  .header .logo {
    width: 253px;
    left: 17px;
    transform: translate(0, -50%);
  }
  .footer .footerLink ul li a {
    font-size: 14px;
    padding: 0 11px;
  }
  .layerPop .layerPop-header strong {
    font-size: 19px;
  }
  .layerPop .layerPop-header button {
    width: 20px;
    height: 20px;
    top: 21px;
    right: 18px;
  }
  .layerPop .layerPop-con {
    overflow: auto;
  }
  .phoneCrtfcCheck-area .phoneCrtfcCheck-box img {
    width: 200px;
  }
  .loginOption .join {
    float: none;
    width: 100%;
    text-align: center;
    display: block;
    margin-bottom: 6px;
  }
  .loginOption .findBtn {
    float: none;
    width: 100%;
    text-align: center;
  }
  .loginWrap .loginLogo {
    display: none;
  }
  .loginWrap .formTit {
    margin-bottom: 40px;
  }
}
@media (max-width: 370px) {
  .pagination ul li a {
    width: 30px;
    height: 30px;
    line-height: 19px;
    font-size: 13px;
    font-weight: 300;
  }
  .pagination ul li.next a {
    background-position: center;
  }
  .pagination ul li.last a {
    background-position: center;
  }
  .m-menu-con .m-header-top p {
    font-size: 18px;
  }
}

/* sub.css */
@charset "utf-8";
.contents.full .contentsBody {
  padding-left: 30px;
  height: 100%;
}
.contents.full.login .contentsBody {
  height: 100vh;
  background: url('/img/system/bg.png') no-repeat right -191px bottom -70px;
}
.contents.full.login .contentsBody .contentsBody-inner {
  height: 100%;
}
.contentsHd {
  border-bottom: 1px solid #cdcdcd;
  margin-bottom: 35px;
}
.contentsHd .contentsTit {
  font-size: 23px;
  font-weight: 600;
  float: left;
  padding-bottom: 20px;
  background: url(/img/cmn/tit.png) no-repeat top 2px left 0;
  background-size: 18px;
  padding-left: 25px;
}
.contentsHd .location {
  float: right;
}
.contentsHd .location ul li {
  float: left;
  position: relative;
}
.contentsHd .location ul li a {
  padding: 0px 15px;
  color: #616161;
}
.contentsHd .location ul li.home {
  background: url('/img/sub/ico_location_home.png') no-repeat top 0px left 0px;
}
.contentsHd .location ul li.home a {
  font-size: 0px;
}
.contentsHd .location ul li::after {
  position: absolute;
  content: '';
  width: 10px;
  height: 10px;
  background: url('/img/sub/ico_location_arrow.png') center center no-repeat;
  top: 2px;
  left: -2px;
}
.contentsHd .location ul li:first-child::after {
  width: 0px;
}
.contentsHd .location ul li:last-child a {
  color: rgb(28 138 232);
  font-weight: 500;
}

.txt {
  font-weight: 300;
  line-height: 1.9;
  word-break: break-all;
  font-size: 15px;
}
.printingBtn {
  padding: 5px 14px;
  height: 35px;
  color: #3a3a3a;
  display: inline-block;
  vertical-align: middle;
  font-size: 14px;
  border: 1px solid #b5b5b5;
  border-radius: 5px;
}

.pop-conTit {
  font-size: 17px;
  font-weight: 500;
  margin: 11px 0;
}
.option {
  border-bottom: 1px dotted #b5b5b5;
  padding-bottom: 10px;
  margin-bottom: 10px;
}
.option .pop-conTit {
  float: left;
}
.option .pop-conTit + .userId {
  float: right;
  margin-top: 7px;
}
.option .pop-conTit + .userId label {
  font-weight: 500;
  margin-right: 10px;
}
.option .pop-conTit + .userId input[type='text'] {
  border-radius: 5px;
  height: 31px;
}

.ycWater-infoImg {
  margin-top: 30px;
  margin-bottom: 30px;
}
.ycWater-infoImg ul li {
  width: 59%;
  height: 430px;
  float: left;
  margin-right: 1%;
}
.ycWater-infoImg ul li img {
  width: 100%;
  height: 100%;
}
.ycWater-infoImg ul li:last-child {
  width: 40%;
  margin-right: 0px;
}

.contentsReserch {
  border: 1px solid rgb(217, 217, 217);
  padding: 20px;
}
.contentsReserch .reserchHd strong {
  display: block;
  font-size: 17px;
  font-weight: 500;
  background: url('/img/sub/ico_reserch_tit.png') left center no-repeat;
  padding: 7px 0px 7px 33px;
}
.contentsReserch .reserchHd dl {
  display: inline-block;
  vertical-align: middle;
  margin-top: 6px;
  position: relative;
  margin-right: 15px;
}
.contentsReserch .reserchHd dl:after {
  content: '';
  position: absolute;
  width: 1px;
  height: 13px;
  background: #a5a5a5;
  top: 4px;
  left: -10px;
}
.contentsReserch .reserchHd dl:first-child:after {
  width: 0;
}
.contentsReserch .reserchHd dl dt {
  display: inline-block;
  vertical-align: middle;
  font-weight: 500;
}
.contentsReserch .reserchHd dl dd {
  display: inline-block;
  vertical-align: middle;
  font-weight: 300;
}

.contentsReserch .researchCon {
  margin-top: 17px;
}
.contentsReserch .researchCon .feedback {
  margin-top: 20px;
  font-size: 0;
}
.contentsReserch .researchCon .feedback input[type='text'] {
  width: calc(100% - 87px);
}
.contentsReserch .researchCon .feedback button {
  height: 37px;
  min-width: 80px;
  background: #3378d6;
  color: #fff;
}

.tableScroll {
  overflow: auto;
}
.tableScroll .realTimeDataTb {
  width: 1800px;
}
.tableScroll .seawtItrsTb {
  width: 1800px;
}
.tableScroll .monthSeawtDataTb {
  width: 1800px;
}

.option-area {
  margin-top: 15px;
  border-top: 1px dotted #9d9d9d;
  padding-top: 10px;
}
.chartArea {
  border: 1px solid #ddd;
  padding: 20px;
  height: 380px;
  margin-bottom: 30px;
}
.chartArea-full {
  border: 1px solid #ddd;
  padding: 20px;
  height: calc(100vh - 280px);
  margin: 15px 0 30px 0;
}
.mapArea-full {
  border: 1px solid #ddd;
  height: 400px;
  width: 49%;
}

.listTotal {
  margin-top: 12px;
  font-weight: 500;
  padding: 3px 0px;
  padding-left: 21px;
  background: url('/img/cmn/ico_list_total.png') no-repeat left top 2px;
}
.listTotal span {
  display: inline-block;
  margin: 0 3px;
  margin-right: 1px;
  font-weight: 600;
  color: #1f8be8;
}

.search-box.searchCondition .formItem {
  display: inline-block;
  vertical-align: middle;
}

.option-item {
  display: inline-block;
  vertical-align: middle;
  margin: 3px 0;
}
.option-area select + select {
  margin: 5px 0;
}
.option-area .form-tit {
  margin-right: 5px;
}

.editorArea {
  border: 1px solid #cdcdcd;
  height: 260px;
  padding: 20px;
}

.serialTxt {
  margin-bottom: 20px;
  padding: 10px;
  background: #ebebeb;
  font-weight: 300;
  text-align: center;
}
.serialTxt span {
  font-weight: 500;
}

.cellBtn-map {
  background: #445dd6;
  border-radius: 5px;
  width: 40px;
  margin-left: 4px;
  height: 22px;
  color: #fff;
}

.layerPop.ycWaterObsrSearch {
  width: 1030px;
}
.layerPop.ycWaterObsrSearch .layerPop-con {
  height: auto;
}
.layerPop.ycWaterObsrSearch .colImgBox {
  width: 250px;
  height: 225px;
  float: left;
  margin-right: 20px;
}
.layerPop.ycWaterObsrSearch .pop-table {
  float: left;
  width: calc(100% - 270px);
}

.layerPop.obsrSearch .colImgBox {
  width: 32.33333%;
  height: 250px;
  margin-right: 1%;
  float: left;
  margin-bottom: 20px;
}
.layerPop.gwData-using .layerPop-con {
  height: 300px;
}
.layerPop.partData-using .layerPop-con {
  height: 300px;
}

.layerPop.apiSignUp-sett table {
  width: 100%;
}

/* responsive.css */
@charset "utf-8";
@media (max-width: 1730px) {
  .header .gnb > ul > li > a {
    font-size: 16px;
  }
  .header .util ul li a {
    font-size: 15px;
  }
}
@media (max-width: 1680px) {
  .header .gnb > ul > li > a {
    font-size: 16px;
    padding: 0 21px;
  }
  .header .util ul li a {
    font-size: 14px;
  }
}
@media (max-width: 1556px) {
  .header .gnb > ul > li > a {
    font-size: 15px;
    padding: 0 15px;
  }
  .header .util ul li a {
    font-size: 14px;
  }
}
@media (max-width: 1440px) {
  .header .gnb {
    left: 360px;
  }
  .header .gnb > ul > li > a {
    padding: 0 13px;
  }
  .header .util ul li a {
    font-size: 14px;
  }
}
@media (max-width: 1340px) {
  .header .logo {
    width: 260px;
  }
  .header .logo a img {
    width: 100%;
  }
  .header .gnb {
    left: 310px;
  }
  .header .gnb > ul > li > a {
    padding: 0 13px;
  }
  .header .util ul li a {
    font-size: 13px;
  }
}
@media (min-width: 920px) {
  .btn-group {
    display: inline-block;
    vertical-align: middle;
  }
}
@media (max-width: 1280px) {
  .header .logo {
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    height: 36px;
  }
  .header .logo a img {
    position: static;
  }
  .sub-open-btn {
    display: block;
  }
  .util {
    display: none;
  }
  .gnb {
    display: none;
  }
  .lnb {
    display: none;
  }
  .layer_modal_wrap {
    display: none;
  }
  .contentsBody {
    padding-left: 30px;
  }
  .search-box .searchForm {
    width: calc(100% - 230px);
  }
  .m-menu-con {
    display: block;
  }
  .footer .footerInfo img {
    float: none;
    margin: 0 auto;
    display: block;
    margin-bottom: 30px;
  }
  .footer .footerInfo .footerTxt {
    float: none;
    text-align: center;
  }
  .footer .footerLink ul {
    text-align: center;
  }
  .footer .footerLink ul li {
    float: none;
    display: inline-block;
    vertical-align: middle;
  }
  .footer .footerInfo .selectSiteMap {
    display: none;
  }
  .search-box.searchCondition .formItem {
    width: 100%;
  }
  .search-box.searchCondition.loginData .formItem {
    width: auto;
  }
  .search-box.checkSet .formItem {
    width: auto;
  }
  .search-box.searchCondition .formItem .searchForm {
    width: calc(100% - 230px);
  }
  .search-box.searchCondition .formItem .searchForm.search100 {
    width: calc(100% - 80px);
  }
  .btn-group {
    width: 100%;
    text-align: center;
  }
  .btn-wrap {
    text-align: center !important;
  }
  .layerPop.ycWaterObsrSearch {
    width: 80%;
  }
  .layerPop.ycWaterObsrSearch .colImgBox {
    width: 100%;
    float: none;
  }
  .layerPop.ycWaterObsrSearch .pop-table {
    width: 100%;
    float: none;
  }
  .joinProcess ul li {
    width: 48%;
    margin-bottom: 9px;
  }
  .phoneCrtfcCheck-area .phoneCrtfcCheck-box {
    width: 100%;
  }
  .layerPop {
    display: none;
  }
  .layerPop2 {
    display: none;
  }
  .layerPop3 {
    display: none;
  }
  .layerPop4 {
    display: none;
  }
  .layerPop5 {
    display: none;
  }
  .layerPop6 {
    display: none;
  }
  .layerPop7 {
    display: none;
  }
  .layerPop8 {
    display: none;
  }
  .progress-body {
    left: 0 !important;
    width: 100% !important;
  }
  .modal-show .layerPop {
    display: block;
  }
}
@media (max-width: 920px) {
  .contentsBody {
    padding: 100px 20px 20px 20px;
  }
  .contentsHd .location {
    margin-top: 6px;
  }
  .search-box .searchForm {
    width: calc(100% - 230px);
  }
  .search-box .btn-group {
    text-align: center;
    margin-top: 15px;
  }
  .option-area .form-tit {
    display: block;
    margin-bottom: 5px;
  }
  .writeTable.conRegister th {
    width: 130px !important;
  }
  .ycWater-infoImg ul li {
    width: 100%;
    height: auto;
    float: none;
    margin-bottom: 20px;
  }
  .ycWater-infoImg ul li:last-child {
    display: none;
  }
  .ycWater-infoImg ul li img {
    height: auto;
  }
  .contentsReserch .reserchHd strong {
    width: 100%;
    display: block;
  }
  .contentsReserch .reserchHd .contact {
    float: none;
  }
  .researchCon p {
    float: none;
    margin-bottom: 10px;
  }
  .researchCon .formGroup {
    float: none;
    width: 100%;
  }
  .layerPop {
    width: 80%;
  }
  .layerPop .layerPop-header strong {
    font-size: 20px;
  }
  .layerPop .popTable-wrap {
    height: 260px;
    overflow-x: auto;
  }
  .layerPop .pop-table {
    width: 1000px;
  }
  .layerPop .layerPop-con {
    height: auto;
  }
  .layerPop.obsrRegister .pop-table {
    width: 100%;
  }
  .layerPop.obsrSearch .pop-table {
    width: 100%;
  }
  .layerPop.obsrSearch .colImgBox {
    height: 200px;
  }
  .layerPop.partData-using table {
    width: 100%;
  }
  .layerPop.gwData-using table {
    width: 100%;
  }
  .layerPop.apiSignUp table {
    width: 100%;
  }
  .joinBox {
    padding: 10% 5% !important;
  }
  .joinBox .formRow.pw input[type='password'] {
    width: 100%;
  }
  .joinBox .formRow.adress .formBox .adressDtl input[type='text'] {
    width: calc(100% - 80px);
  }
  .joinBox .formRow.user input[type='text'] {
    width: 100%;
  }
  .joinBox .formRow.id input[type='text'] {
    width: calc(100% - 90px);
  }
  .joinBox .formRow.adress .formBox .postFind input[type='text'] {
    width: calc(100% - 90px);
  }
  .contents.full.login .contentsBody {
    background: none;
    height: auto;
  }
  .loginWrap {
    width: 90%;
    top: 0;
    left: 0;
    transform: none;
    margin: 0 auto;
  }
  .contents_wrap3 {
    display: flex;
  }
  .formhd-tit {
    font-size: 14px;
  }
  .inforAll .inforArea {
    font-size: 12px;
  }
  .form-name {
    font-size: 14px;
  }
  .cmnBtn {
    min-width: 100%;
    margin-bottom: 5px;
    margin: 5px !important;
  }
  .printingBtn {
    min-width: 100%;
    margin-bottom: 5px;
    margin: 3px !important;
  }
  .tableWrap {
    overflow: auto;
  }
  .listTable {
    width: 1080px;
  }
  .overflow {
    overflow: auto;
  }
  .popup-box {
    overflow: auto;
  }
  .detailTable {
    width: 1080px;
  }
  .mapTable {
    overflow: auto;
  }
}

@media (max-width: 660px) {
  .search-box .searchForm {
    width: calc(100% - 230px);
  }
  .contentsHd .contentsTit {
    width: 100%;
    float: none;
    text-align: center;
    padding-bottom: 10px;
    background: none;
  }
  .contentsHd .location {
    float: none;
    width: 100%;
    padding-bottom: 20px;
  }
  .contentsHd .location ul {
    text-align: center;
  }
  .contentsHd .location ul li {
    display: inline-block;
    vertical-align: middle;
    float: none;
    margin: 5px 0;
  }
  .search-box.searchCondition .formItem .calendar-item .date-picker {
    width: 132px;
  }
  .search-box.searchCondition .formItem .calendar-item .date-picker2 {
    width: 132px;
  }
  .time-item {
    width: 132px;
  }
  .tableWrap {
    overflow: auto;
  }
  .listTable {
    width: 1080px;
  }
  .listTable th {
    font-size: 13px;
  }
  .listTable td {
    font-size: 12px;
  }
  .listTable.hologramDevice {
    width: 800px;
  }
  .listTable.obsrStat {
    width: 800px;
  }
  .listTable.seawtItrsTb {
    width: 800px;
  }
  .listTable.seaGroundWtItrsTb {
    width: 1300px;
  }
  .listTable.conSetting {
    width: 800px;
  }
  .listTable.conSetting button {
    min-width: 60px;
  }
  .listTable.apiTable {
    width: 800px;
  }
  .layerPop.contentsFeedback table {
    width: 600px;
  }
  .layerPop.permission table {
    width: 600px;
  }
  .layerPop.userSearch table {
    width: 600px;
  }
  .layerPop.conChangeEdit table {
    width: 600px;
  }
  .btn-wrap button {
    width: 40%;
    margin-bottom: 5px;
  }
  .cmnTabs.siteInfo > ul > li {
    width: 50%;
    margin-top: -1px;
  }
  .layerPop.ycWaterObsrSearch .pop-table {
    width: 720px;
  }
  .layerPop.obsrSearch .colImgBox {
    width: 100%;
    height: 170px;
  }
  .phoneCrtfcCheck-area {
    padding: 10% 5%;
  }

  .layerPop.obsrSearch .layerPop-con {
    height: 400px;
  }
  .layerPop.obsrSearch .layerPop-con .popTable-wrap table {
    width: 600px;
  }
  .layerPop.obsrRegister .layerPop-con table {
    width: 600px;
  }
  .layerPop.checkInfo .layerPop-con {
    height: 530px;
  }
  .layerPop.checkInfo .layerPop-con table {
    width: 600px;
  }
  .option .pop-conTit {
    font-size: 15px;
    display: block;
    width: 100%;
    float: none;
    text-align: center;
    border: 1px solid #c7c7c7;
    padding: 8px;
    background: #ebebeb;
  }
  .option .pop-conTit + .userId {
    float: none;
  }
  .layerPop.alramUser table {
    width: 100%;
    font-size: 13px;
  }
  .layerPop.alramUser table button {
    min-width: auto;
  }
  .layerPop.obsrEdit .pop-table {
    width: 600px;
  }
  .layerPop .layerPop-header {
    min-height: 50px;
    line-height: 50px;
  }
  .layerPop .layerPop-header strong {
    font-size: 18px;
  }
  .layerPop .layerPop-header button {
    width: 20px;
    height: 20px;
    right: 16px;
  }
  .layerPop.apiSignUp table {
    width: 550px;
  }
  .layerPop.apiSignUp-sett table {
    width: 550px;
  }

  .loginWrap .loginLogo {
    width: 80%;
  }
  .loginWrap .formTit {
    font-size: 26px;
  }
  .joinBox .formRow .formTxt {
    float: none;
    border: 0;
  }
  .joinBox .formRow {
    border: 0;
    margin: 0;
  }
  .joinBox .formRow .formTxt label {
    min-height: auto;
    line-height: unset;
    background: #fff;
    padding: 9px 0;
  }
  .joinBox .formRow.adress .formTxt label {
    line-height: inherit;
  }
  .joinBox .formRow .formBox {
    float: none;
    width: 100%;
    padding: 0;
  }
}
@media (max-width: 559px) {
  .search-box select {
    width: 99%;
    margin: 5px 0;
  }
  .search-box input[type='text'] {
    /* width:99%; */
  }
  .search-box.searchCondition .formItem {
    width: 100%;
  }
  .search-box.searchCondition .formItem .form-tit {
    min-width: 80px;
    margin-bottom: 5px;
  }
  .search-box.searchCondition .formItem input[type='text'] {
    width: calc(100% - 151px);
  }
  .search-box.searchCondition .formItem select + input[type='text'] {
    width: 99%;
  }
  .search-box.searchCondition.loginData .formItem {
    width: 100%;
  }
  .search-box.searchCondition.loginData .formItem input[type='text'] {
    width: 99%;
  }
  .search-box.searchCondition.checkSet .formItem {
    width: 100%;
  }
  .search-box.searchCondition.checkSet .formItem select {
    width: 99%;
  }

  .search-box.searchCondition .formItem .searchForm.search100 {
    width: 99%;
  }

  .search-box button {
    width: 49%;
    margin-bottom: 5px;
  }
  .search-box button.excelBtn {
    width: 99%;
    width: 32% !important;
  }
  .btn-group.onlySearch button {
    width: 20%;
  }
  .search-box button.printingBtn {
    width: 99%;
  }
  .joinProcess ul li {
    width: 99%;
  }
  .joinProcess ul li > div {
    height: 52px;
    padding-top: 11px;
  }
  .joinProcess ul li > div i {
    height: 30px;
    background-size: 15px auto !important;
  }
  .joinProcess ul li > div p {
    font-size: 14px;
  }
}
@media (max-width: 500px) {
  .search-box .form-tit {
    width: 100%;
    margin-bottom: 5px;
  }
  .search-box .searchForm {
    width: calc(100% - 150px);
  }
  .search-box .btn-group {
    margin-top: 5px;
    text-align: center;
  }
  .search-box.searchCondition .formItem .searchForm {
    width: calc(100% - 160px);
  }
  .m-menu-area > ul > li > a {
    font-size: 14px;
    width: 140px;
    padding-left: 19px;
  }
  .m-menu-area .menuRight {
    left: 140px;
    width: calc(100% - 140px);
  }
  .m-menu-area .menuRight > ul > li > a {
    padding: 14px 19px;
  }
  .joinBox .formRow .formBox .birth input[type='text'] {
    width: 60px;
  }
  .joinForm .cmnBtn {
    min-width: 49%;
  }
  .layerPop.find {
    width: 80%;
  }
  .pagination ul li {
    margin: 5px 2px;
  }
  .toggleBar {
    display: flex;
  }
}
@media (max-width: 420px) {
  .search-box {
    padding: 13px 16px;
  }
  .search-box .form-tit {
    width: 100%;
    margin-bottom: 5px;
  }
  .search-box .searchForm {
    width: calc(100% - 145px);
  }
  .header .logo {
    width: 253px;
    left: 17px;
    transform: translate(0, -50%);
  }
  .footer .footerLink ul li a {
    font-size: 14px;
    padding: 0 11px;
  }
  .layerPop .layerPop-header strong {
    font-size: 19px;
  }
  .layerPop .layerPop-header button {
    width: 20px;
    height: 20px;
    top: 21px;
    right: 18px;
  }
  .layerPop .layerPop-con {
    overflow: auto;
  }
  .phoneCrtfcCheck-area .phoneCrtfcCheck-box img {
    width: 200px;
  }
  .loginOption .join {
    float: none;
    width: 100%;
    text-align: center;
    display: block;
    margin-bottom: 6px;
  }
  .loginOption .findBtn {
    float: none;
    width: 100%;
    text-align: center;
  }
  .loginWrap .loginLogo {
    display: none;
  }
  .loginWrap .formTit {
    margin-bottom: 40px;
  }
}
@media (max-width: 370px) {
  .pagination ul li a {
    width: 30px;
    height: 30px;
    line-height: 19px;
    font-size: 13px;
    font-weight: 300;
  }
  .pagination ul li.next a {
    background-position: center;
  }
  .pagination ul li.last a {
    background-position: center;
  }
  .m-menu-con .m-header-top p {
    font-size: 18px;
  }
}

/* cms.css */
@import url(https://fonts.googleapis.com/css?family=Source+Sans+Pro:300,400,600,700,300italic,400italic,600italic);

@import url(https://cdn.jsdelivr.net/gh/moonspam/NanumSquare@1.0/nanumsquare.css);
@import url(https://cdn.rawgit.com/moonspam/NanumSquare/master/nanumsquare.css);
/*!
 *   AdminLTE v2.3.0
 *   Author: Almsaeed Studio
 *	 Website: Almsaeed Studio <http://almsaeedstudio.com>
 *   License: Open source - MIT
 *           Please visit http://opensource.org/licenses/MIT for more information
!*/
/*
 * Core: General Layout Style
 * -------------------------
 */
/*html,
body {
  min-height: 100%;
  font-family: "NanumSquare", sans-serif;
}*/
.layout-boxed html,
.layout-boxed body {
  height: 100%;
}
/*body {
  -moz-osx-font-smoothing: grayscale;
  font-family: 'Source Sans Pro', 'Helvetica Neue', Helvetica, Arial, sans-serif;
  font-weight: 400;
  overflow-x: hidden;
  overflow-y: auto;
}*/

.ingWrap {
  float: left;
  margin: 15px 5px;
  color: #fff;
  width: 70%;
}

/* Layout */
.wrapper {
  min-height: 100%;
  position: static;
  overflow: hidden;
  /*  background-color: #181d27;*/
}
.wrapper:before,
.wrapper:after {
  content: ' ';
  display: table;
}
.wrapper:after {
  clear: both;
}
.layout-boxed .wrapper {
  max-width: 1250px;
  margin: 0 auto;
  min-height: 100%;
  box-shadow: 0 0 8px rgba(0, 0, 0, 0.5);
  position: relative;
}
.layout-boxed {
  /*background: url('../img/boxed-bg.jpg') repeat fixed;*/
}
/*
 * Content Wrapper - contains the main content
 * ```.right-side has been deprecated as of v2.0.0 in favor of .content-wrapper  ```
 */
.content-wrapper,
.right-side,
.main-footer {
  -webkit-transition:
    -webkit-transform 0.3s ease-in-out,
    margin 0.3s ease-in-out;
  -moz-transition:
    -moz-transform 0.3s ease-in-out,
    margin 0.3s ease-in-out;
  -o-transition:
    -o-transform 0.3s ease-in-out,
    margin 0.3s ease-in-out;
  transition:
    transform 0.3s ease-in-out,
    margin 0.3s ease-in-out;
  margin-left: 230px;
  z-index: 820;
}
.main-footer {
  margin-left: 0px !important;
  text-align: center !important;
}
.layout-top-nav .content-wrapper,
.layout-top-nav .right-side,
.layout-top-nav .main-footer {
  margin-left: 0;
}
@media (max-width: 767px) {
  .content-wrapper,
  .right-side {
    margin-left: 0;
    padding-top: 70px;
  }
}
@media (min-width: 768px) {
  .sidebar-collapse .content-wrapper,
  .sidebar-collapse .right-side,
  .sidebar-collapse .main-footer {
    margin-left: 0;
  }
  #calender {
    margin-left: 5% !important;
    margin-right: 5% !important;
  }
}
@media (max-width: 767px) {
  .sidebar-open .content-wrapper,
  .sidebar-open .right-side {
    -webkit-transform: translate(230px, 0);
    -ms-transform: translate(230px, 0);
    -o-transform: translate(230px, 0);
    transform: translate(230px, 0);
  }
}
.content-wrapper, /* 배경색 */
.right-side {
  min-height: 100%;
  background-color: #fafafa;
  z-index: 800;
}

.main-footer {
  background: #0f1521;
  height: 50px;
}
.main-footer div {
  margin: 0 auto;
  background: url(/img/240_50.svg);
  height: 50px;
  width: 240px;
}

/* Fixed layout */
.fixed .main-header,
.fixed .main-sidebar,
.fixed .left-side {
  position: fixed;
}
.fixed .main-header {
  top: 0;
  right: 0;
  left: 0;
}
.fixed .content-wrapper,
.fixed .right-side {
  padding-top: 50px;
}
@media (max-width: 767px) {
  .fixed .content-wrapper,
  .fixed .right-side {
    padding-top: 100px;
  }
}
.fixed.layout-boxed .wrapper {
  max-width: 100%;
}
body.hold-transition .content-wrapper,
body.hold-transition .right-side,
body.hold-transition .main-footer,
body.hold-transition .main-sidebar,
body.hold-transition .left-side,
body.hold-transition .main-header > .navbar,
body.hold-transition .main-header .logo {
  /* Fix for IE */
  -webkit-transition: none;
  -o-transition: none;
  transition: none;
}
/* Content */
.content {
  min-height: 250px;
  padding: 10px;
  margin-right: auto;
  margin-left: auto;
  padding-left: 15px;
  padding-right: 15px;
}
/* General Links */
a {
  color: #3c8dbc;
}
a:hover,
a:active,
a:focus {
  outline: none;
  text-decoration: none;
  color: #72afd2;
}
/* Page Header */
.page-header {
  margin: 10px 0 20px 0;
  font-size: 22px;
}
.page-header > small {
  color: #666;
  display: block;
  margin-top: 5px;
}
/*
 * Component: Main Header
 * ----------------------
 */
.main-header {
  position: relative;
  max-height: 70px;
  z-index: 1030;
  background-color: #080d19;
}
.main-header > .navbar {
  -webkit-transition: margin-left 0.3s ease-in-out;
  -o-transition: margin-left 0.3s ease-in-out;
  transition: margin-left 0.3s ease-in-out;
  margin-bottom: 0;
  margin-left: 230px;
  border: none;
  min-height: 50px;
  border-radius: 0;
}
.layout-top-nav .main-header > .navbar {
  margin-left: 0;
}
.main-header #navbar-search-input.form-control {
  background: rgba(255, 255, 255, 0.2);
  border-color: transparent;
}
.main-header #navbar-search-input.form-control:focus,
.main-header #navbar-search-input.form-control:active {
  border-color: rgba(0, 0, 0, 0.1);
  background: rgba(255, 255, 255, 0.9);
}
.main-header #navbar-search-input.form-control::-moz-placeholder {
  color: #ccc;
  opacity: 1;
}
.main-header #navbar-search-input.form-control:-ms-input-placeholder {
  color: #ccc;
}
.main-header #navbar-search-input.form-control::-webkit-input-placeholder {
  color: #ccc;
}
.main-header .navbar-custom-menu,
.main-header .navbar-right {
  float: right;
}
.main-header .navbar-custom-menu a,
.main-header .navbar-right a {
  color: inherit;
  background: transparent;
}

@media (max-width: 767px) {
  .main-header .navbar-right {
    float: none;
  }
  .navbar-collapse .main-header .navbar-right {
    margin: 7.5px -15px;
  }
  .main-header .navbar-right > li {
    color: inherit;
    border: 0;
  }
}

.sidebar-toggleNew {
  float: left;
  background-color: transparent;
  background-image: none;
  width: 40px;
  height: 40px;
  font-family: fontAwesome;
  background: url(/img/btn_togle.png) 8px center no-repeat;
}

.main-header .sidebar-toggle {
  float: left;
  background-color: transparent;
  background-image: none;
  padding: 30px 30px;
  font-family: fontAwesome;
  background: url(/img/btn_togle.png) 8px center no-repeat;
}
.main-header .sidebar-toggle:before {
  /*content: "\f0c9";*/
}
.main-header .sidebar-toggle:hover {
  color: #fff;
}
.main-header .sidebar-toggle:focus,
.main-header .sidebar-toggle:active {
  background: transparent;
}
.main-header .sidebar-toggle .icon-bar {
  display: none;
}

.main-header .navbar .nav > li.user > a > .fa,
.main-header .navbar .nav > li.user > a > .glyphicon,
.main-header .navbar .nav > li.user > a > .ion {
  margin-right: 5px;
}
.main-header .navbar .nav > li > a > .label {
  position: absolute;
  top: 9px;
  right: 7px;
  text-align: center;
  font-size: 9px;
  padding: 2px 3px;
  line-height: 0.9;
}
.main-header .logo {
  -webkit-transition: width 0.3s ease-in-out;
  -o-transition: width 0.3s ease-in-out;
  transition: width 0.3s ease-in-out;
  display: block;
  float: left;
  height: 70px;
  font-size: 18px;
  line-height: 65px;
  text-align: center;
  width: 230px;
  font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif;
  /* padding: 0 15px;*/
  font-weight: 300;
  overflow: hidden;
}
.main-header .logo .logo-mini {
  display: inline;
  /*background: url(/images/logo.png)no-repeat 0 0;*/
}
.main-header .logo .logo-lg {
  /*display: block;*/
}
.main-header .navbar-brand {
  color: #fff;
}
.content-header {
  position: relative;
  padding: 10px 15px 0 15px;
}
.content-header > h1 {
  margin: 0;
  font-size: 22px;
  float: left;
}
.content-header > h1 > small {
  font-size: 15px;
  display: inline-block;
  padding-left: 4px;
  font-weight: 300;
}
.info_color {
  color: #3e5bab;
}

.content-header > .breadcrumb {
  float: right;
  display: flex;
  background: transparent;
  margin-top: -10px;
  margin-bottom: 0;
  font-size: 16px;
  padding: 7px 5px;
  border-radius: 2px;
}
.content-header > .breadcrumb > li > a {
  color: #444;
  text-decoration: none;
  display: inline-block;
}
.content-header > .breadcrumb > li > a > .fa,
.content-header > .breadcrumb > li > a > .glyphicon,
.content-header > .breadcrumb > li > a > .ion {
  margin-right: 5px;
}
.content-header > .breadcrumb > li + li:before {
  content: '>\00a0';
}
@media (max-width: 991px) {
  .content-header > .breadcrumb {
    position: relative;
    top: 0;
    right: 0;
    padding-left: 10px;
  }
  .content-header > .breadcrumb li:before {
    color: #97a0b3;
  }
}
.navbar-toggle {
  color: #fff;
  border: 0;
  margin: 0;
  padding: 15px 15px;
}
@media (max-width: 991px) {
  .navbar-custom-menu .navbar-nav > li {
    float: left;
  }
  .navbar-custom-menu .navbar-nav {
    margin: 0;
    float: left;
  }
}
@media (max-width: 767px) {
  .main-header {
    position: relative;
  }
  .main-header .logo,
  .main-header .navbar {
    width: 100%;
    float: none;
  }
  .main-header .navbar {
    margin: 0;
    padding: 15px;
  }
  .main-header .navbar-custom-menu {
    float: right;
  }
  .content-header > .breadcrumb {
    visibility: hidden;
  }
  .navbar-custom-menu .navbar-nav > li > a {
    padding: 10px 0;
    line-height: 20px;
  }
}
@media (max-width: 991px) {
  .navbar-collapse.pull-left {
    float: none !important;
  }
  .navbar-collapse.pull-left + .navbar-custom-menu {
    display: block;
    position: absolute;
    top: 0;
    right: 40px;
  }
}
/*
 * Component: Sidebar
 * ------------------
 */
.main-sidebar,
.left-side {
  top: 0;
  left: 0;
  padding-top: 70px;
  background-color: #181d27;
  position: absolute;
  min-height: 100%;
  width: 230px;
  z-index: 810;
  -webkit-transition:
    -webkit-transform 0.3s ease-in-out,
    width 0.3s ease-in-out;
  -moz-transition:
    -moz-transform 0.3s ease-in-out,
    width 0.3s ease-in-out;
  -o-transition:
    -o-transform 0.3s ease-in-out,
    width 0.3s ease-in-out;
  transition:
    transform 0.3s ease-in-out,
    width 0.3s ease-in-out;
}

@media (max-width: 767px) {
  .main-sidebar,
  .left-side {
    padding-top: 140px;
    -webkit-transform: translate(-230px, 0);
    -ms-transform: translate(-230px, 0);
    -o-transform: translate(-230px, 0);
    transform: translate(-230px, 0);
  }
}
@media (min-width: 768px) {
  .sidebar-collapse .main-sidebar,
  .sidebar-collapse .left-side {
    -webkit-transform: translate(-230px, 0);
    -ms-transform: translate(-230px, 0);
    -o-transform: translate(-230px, 0);
    transform: translate(-230px, 0);
  }
}
@media (max-width: 767px) {
  .sidebar-open .main-sidebar,
  .sidebar-open .left-side {
    padding-top: 140px;
    -webkit-transform: translate(0, 0);
    -ms-transform: translate(0, 0);
    -o-transform: translate(0, 0);
    transform: translate(0, 0);
  }
}
.sidebar {
  padding-bottom: 10px;
}
.sidebar-form input:focus {
  border-color: transparent;
}
.user-panel {
  position: relative;
  width: 100%;
  padding: 10px;
  overflow: hidden;
}
.user-panel:before,
.user-panel:after {
  content: ' ';
  display: table;
}
.user-panel:after {
  clear: both;
}
.user-panel > .image > img {
  width: 100%;
  max-width: 45px;
  height: auto;
}
.user-panel > .info {
  padding: 5px 5px 5px 15px;
  line-height: 1;
  position: absolute;
  left: 55px;
}
.user-panel > .info > p {
  font-weight: 600;
  margin-bottom: 9px;
}
.user-panel > .info > a {
  text-decoration: none;
  padding-right: 5px;
  margin-top: 3px;
  font-size: 11px;
}
.user-panel > .info > a > .fa,
.user-panel > .info > a > .ion,
.user-panel > .info > a > .glyphicon {
  margin-right: 3px;
}
.sidebar-menu {
  list-style: none;
  margin: 0;
  padding: 0;
}
.sidebar-menu > li {
  position: relative;
  margin: 0;
  padding: 0;
}
.sidebar-menu > li > a {
  padding: 12px 5px 12px 15px;
  display: block;
}
.sidebar-menu > li > a > .fa,
.sidebar-menu > li > a > .glyphicon,
.sidebar-menu > li > a > .ion {
  width: 20px;
}
.sidebar-menu > li .label,
.sidebar-menu > li .badge {
  margin-top: 3px;
  margin-right: 5px;
}
.sidebar-menu li.header {
  padding: 10px 25px 10px 15px;
  font-size: 12px;
}
.sidebar-menu li > a > .fa-angle-left {
  width: auto;
  height: auto;
  padding: 0;
  margin-right: 10px;
  margin-top: 3px;
}
.sidebar-menu li.active > a > .fa-angle-left {
  -webkit-transform: rotate(-90deg);
  -ms-transform: rotate(-90deg);
  -o-transform: rotate(-90deg);
  transform: rotate(-90deg);
}
.sidebar-menu li.active > .treeview-menu {
  display: block;
  padding: 0;
}
.sidebar-menu .treeview-menu {
  display: none;
  list-style: none;
  padding: 0;
  margin: 0;
}
.sidebar-menu .treeview-menu .treeview-menu {
  padding-left: 20px;
}
.sidebar-menu .treeview-menu > li {
  margin: 0;
  /* background:url(../images/btn_over.png) 5px 9px no-repeat */
}
/*.sidebar-menu .treeview-menu > li > a {
  padding: 0 3px 5px 18px;
  display: block;
  font-size: 14px;
  background-color: #2c3038;
  margin-top: 5px;
  height: 30px;
}*/
.sidebar-menu .treeview-menu > li > a > .fa,
.sidebar-menu .treeview-menu > li > a > .glyphicon,
.sidebar-menu .treeview-menu > li > a > .ion {
  width: 20px;
}
.sidebar-menu .treeview-menu > li > a > .fa-angle-left,
.sidebar-menu .treeview-menu > li > a > .fa-angle-down {
  width: auto;
}
/*
 * Component: Sidebar Mini
 */
@media (min-width: 768px) {
  .sidebar-mini.sidebar-collapse .content-wrapper,
  .sidebar-mini.sidebar-collapse .right-side,
  .sidebar-mini.sidebar-collapse .main-footer {
    margin-left: 50px !important;
    z-index: 840;
  }
  .sidebar-mini.sidebar-collapse .main-sidebar {
    -webkit-transform: translate(0, 0);
    -ms-transform: translate(0, 0);
    -o-transform: translate(0, 0);
    transform: translate(0, 0);
    width: 50px !important;
    z-index: 850;
  }
  .sidebar-mini.sidebar-collapse .sidebar-menu > li {
    position: relative;
  }
  .sidebar-mini.sidebar-collapse .sidebar-menu > li > a {
    margin-right: 0;
  }
  .sidebar-mini.sidebar-collapse .sidebar-menu > li > a > span {
    border-top-right-radius: 4px;
  }
  .sidebar-mini.sidebar-collapse .sidebar-menu > li:not(.treeview) > a > span {
    border-bottom-right-radius: 4px;
  }
  .sidebar-mini.sidebar-collapse .sidebar-menu > li > .treeview-menu {
    padding-top: 5px;
    padding-bottom: 5px;
    border-bottom-right-radius: 4px;
  }
  .sidebar-mini.sidebar-collapse .sidebar-menu > li:hover > a > span:not(.pull-right),
  .sidebar-mini.sidebar-collapse .sidebar-menu > li:hover > .treeview-menu {
    display: block !important;
    position: absolute;
    width: 180px;
    left: 50px;
  }
  .sidebar-mini.sidebar-collapse .sidebar-menu > li:hover > a > span {
    top: 0;
    margin-left: -3px;
    padding: 12px 5px 12px 20px;
    background-color: inherit;
  }
  .sidebar-mini.sidebar-collapse .sidebar-menu > li:hover > .treeview-menu {
    top: 44px;
    margin-left: 0;
  }
  .sidebar-mini.sidebar-collapse .main-sidebar .user-panel > .info,
  .sidebar-mini.sidebar-collapse .sidebar-form,
  .sidebar-mini.sidebar-collapse .sidebar-menu > li > a > span,
  .sidebar-mini.sidebar-collapse .sidebar-menu > li > .treeview-menu,
  .sidebar-mini.sidebar-collapse .sidebar-menu > li > a > .pull-right,
  .sidebar-mini.sidebar-collapse .sidebar-menu li.header {
    display: none !important;
    -webkit-transform: translateZ(0);
  }
  .sidebar-mini.sidebar-collapse .main-header .logo {
    width: 50px;
  }
  .sidebar-mini.sidebar-collapse .main-header .logo > .logo-mini {
    display: block;
    margin-left: -15px;
    margin-right: -15px;
    font-size: 18px;
  }
  .sidebar-mini.sidebar-collapse .main-header .logo > .logo-lg {
    display: none;
  }
  .sidebar-mini.sidebar-collapse .main-header .navbar {
    margin-left: 50px;
  }
}
.sidebar-menu,
.main-sidebar .user-panel,
.sidebar-menu > li.header {
  white-space: nowrap;
  overflow: hidden;
}
.sidebar-menu:hover {
  overflow: visible;
}
.sidebar-form,
.sidebar-menu > li.header {
  overflow: hidden;
  text-overflow: clip;
}
.sidebar-menu li > a {
  position: relative;
}
.sidebar-menu li > a > .pull-right {
  position: absolute;
  top: 50%;
  right: 10px;
  margin-top: -7px;
}
/*
 * Component: Control sidebar. By default, this is the right sidebar.
 */
.control-sidebar-bg {
  position: fixed;
  z-index: 1000;
  bottom: 0;
}
.control-sidebar-bg,
.control-sidebar {
  top: 0;
  right: -230px;
  width: 230px;
  -webkit-transition: right 0.3s ease-in-out;
  -o-transition: right 0.3s ease-in-out;
  transition: right 0.3s ease-in-out;
}
.control-sidebar {
  position: absolute;
  padding-top: 50px;
  z-index: 1010;
}
@media (max-width: 768px) {
  .control-sidebar {
    padding-top: 100px;
  }
}
.control-sidebar > .tab-content {
  padding: 10px 15px;
}
.control-sidebar.control-sidebar-open,
.control-sidebar.control-sidebar-open + .control-sidebar-bg {
  right: 0;
}
.control-sidebar-open .control-sidebar-bg,
.control-sidebar-open .control-sidebar {
  right: 0;
}
@media (min-width: 768px) {
  .control-sidebar-open .content-wrapper,
  .control-sidebar-open .right-side,
  .control-sidebar-open .main-footer {
    margin-right: 230px;
  }
}
.nav-tabs.control-sidebar-tabs > li:first-of-type > a,
.nav-tabs.control-sidebar-tabs > li:first-of-type > a:hover,
.nav-tabs.control-sidebar-tabs > li:first-of-type > a:focus {
  border-left-width: 0;
}
.nav-tabs.control-sidebar-tabs > li > a {
  border-radius: 0;
}
.nav-tabs.control-sidebar-tabs > li > a,
.nav-tabs.control-sidebar-tabs > li > a:hover {
  border-top: none;
  border-right: none;
  border-left: 1px solid transparent;
  border-bottom: 1px solid transparent;
}
.nav-tabs.control-sidebar-tabs > li > a .icon {
  font-size: 16px;
}
.nav-tabs.control-sidebar-tabs > li.active > a,
.nav-tabs.control-sidebar-tabs > li.active > a:hover,
.nav-tabs.control-sidebar-tabs > li.active > a:focus,
.nav-tabs.control-sidebar-tabs > li.active > a:active {
  border-top: none;
  border-right: none;
  border-bottom: none;
}
@media (max-width: 768px) {
  .nav-tabs.control-sidebar-tabs {
    display: table;
  }
  .nav-tabs.control-sidebar-tabs > li {
    display: table-cell;
  }
}
.control-sidebar-heading {
  font-weight: 400;
  font-size: 16px;
  padding: 10px 0;
  margin-bottom: 10px;
}
.control-sidebar-subheading {
  display: block;
  font-weight: 400;
  font-size: 14px;
}
.control-sidebar-menu {
  list-style: none;
  padding: 0;
  margin: 0 -15px;
}
.control-sidebar-menu > li > a {
  display: block;
  padding: 10px 15px;
}
.control-sidebar-menu > li > a:before,
.control-sidebar-menu > li > a:after {
  content: ' ';
  display: table;
}
.control-sidebar-menu > li > a:after {
  clear: both;
}
.control-sidebar-menu > li > a > .control-sidebar-subheading {
  margin-top: 0;
}
.control-sidebar-menu .menu-icon {
  float: left;
  width: 35px;
  height: 35px;
  border-radius: 50%;
  text-align: center;
  line-height: 35px;
}
.control-sidebar-menu .menu-info {
  margin-left: 45px;
  margin-top: 3px;
}
.control-sidebar-menu .menu-info > .control-sidebar-subheading {
  margin: 0;
}
.control-sidebar-menu .menu-info > p {
  margin: 0;
  font-size: 11px;
}
.control-sidebar-menu .progress {
  margin: 0;
}
.control-sidebar-dark {
  color: #b8c7ce;
}
.control-sidebar-dark,
.control-sidebar-dark + .control-sidebar-bg {
  background: #222d32;
}
.control-sidebar-dark .nav-tabs.control-sidebar-tabs {
  border-bottom: #1c2529;
}
.control-sidebar-dark .nav-tabs.control-sidebar-tabs > li > a {
  background: #181f23;
  color: #b8c7ce;
}
.control-sidebar-dark .nav-tabs.control-sidebar-tabs > li > a,
.control-sidebar-dark .nav-tabs.control-sidebar-tabs > li > a:hover,
.control-sidebar-dark .nav-tabs.control-sidebar-tabs > li > a:focus {
  border-left-color: #141a1d;
  border-bottom-color: #141a1d;
}
.control-sidebar-dark .nav-tabs.control-sidebar-tabs > li > a:hover,
.control-sidebar-dark .nav-tabs.control-sidebar-tabs > li > a:focus,
.control-sidebar-dark .nav-tabs.control-sidebar-tabs > li > a:active {
  background: #1c2529;
}
.control-sidebar-dark .nav-tabs.control-sidebar-tabs > li > a:hover {
  color: #fff;
}
.control-sidebar-dark .nav-tabs.control-sidebar-tabs > li.active > a,
.control-sidebar-dark .nav-tabs.control-sidebar-tabs > li.active > a:hover,
.control-sidebar-dark .nav-tabs.control-sidebar-tabs > li.active > a:focus,
.control-sidebar-dark .nav-tabs.control-sidebar-tabs > li.active > a:active {
  background: #222d32;
  color: #fff;
}
.control-sidebar-dark .control-sidebar-heading,
.control-sidebar-dark .control-sidebar-subheading {
  color: #fff;
}
.control-sidebar-dark .control-sidebar-menu > li > a:hover {
  background: #1e282c;
}
.control-sidebar-dark .control-sidebar-menu > li > a .menu-info > p {
  color: #b8c7ce;
}
.control-sidebar-light {
  color: #5e5e5e;
}
.control-sidebar-light,
.control-sidebar-light + .control-sidebar-bg {
  background: #f9fafc;
  border-left: 1px solid #d2d6de;
}
.control-sidebar-light .nav-tabs.control-sidebar-tabs {
  border-bottom: #d2d6de;
}
.control-sidebar-light .nav-tabs.control-sidebar-tabs > li > a {
  background: #e8ecf4;
  color: #444444;
}
.control-sidebar-light .nav-tabs.control-sidebar-tabs > li > a,
.control-sidebar-light .nav-tabs.control-sidebar-tabs > li > a:hover,
.control-sidebar-light .nav-tabs.control-sidebar-tabs > li > a:focus {
  border-left-color: #d2d6de;
  border-bottom-color: #d2d6de;
}
.control-sidebar-light .nav-tabs.control-sidebar-tabs > li > a:hover,
.control-sidebar-light .nav-tabs.control-sidebar-tabs > li > a:focus,
.control-sidebar-light .nav-tabs.control-sidebar-tabs > li > a:active {
  background: #eff1f7;
}
.control-sidebar-light .nav-tabs.control-sidebar-tabs > li.active > a,
.control-sidebar-light .nav-tabs.control-sidebar-tabs > li.active > a:hover,
.control-sidebar-light .nav-tabs.control-sidebar-tabs > li.active > a:focus,
.control-sidebar-light .nav-tabs.control-sidebar-tabs > li.active > a:active {
  background: #f9fafc;
  color: #111;
}
.control-sidebar-light .control-sidebar-heading,
.control-sidebar-light .control-sidebar-subheading {
  color: #111;
}
.control-sidebar-light .control-sidebar-menu {
  margin-left: -14px;
}
.control-sidebar-light .control-sidebar-menu > li > a:hover {
  background: #f4f4f5;
}
.control-sidebar-light .control-sidebar-menu > li > a .menu-info > p {
  color: #5e5e5e;
}
/*
 * Component: Dropdown menus
 * -------------------------
 */
/*Dropdowns in general*/
.dropdown-menu {
  box-shadow: none;
  border-color: #eee;
}
.dropdown-menu > li > a {
  color: #777;
}
.dropdown-menu > li > a > .glyphicon,
.dropdown-menu > li > a > .fa,
.dropdown-menu > li > a > .ion {
  margin-right: 10px;
}
.dropdown-menu > li > a:hover {
  background-color: #e1e3e9;
  color: #333;
}
.dropdown-menu > .divider {
  background-color: #eee;
}
.navbar-nav > .notifications-menu > .dropdown-menu,
.navbar-nav > .messages-menu > .dropdown-menu,
.navbar-nav > .tasks-menu > .dropdown-menu {
  width: 280px;
  padding: 0 0 0 0;
  margin: 0;
  top: 100%;
}
.navbar-nav > .notifications-menu > .dropdown-menu > li,
.navbar-nav > .messages-menu > .dropdown-menu > li,
.navbar-nav > .tasks-menu > .dropdown-menu > li {
  position: relative;
}
.navbar-nav > .notifications-menu > .dropdown-menu > li.header,
.navbar-nav > .messages-menu > .dropdown-menu > li.header,
.navbar-nav > .tasks-menu > .dropdown-menu > li.header {
  border-top-left-radius: 4px;
  border-top-right-radius: 4px;
  border-bottom-right-radius: 0;
  border-bottom-left-radius: 0;
  background-color: #ffffff;
  padding: 7px 10px;
  border-bottom: 1px solid #f4f4f4;
  color: #444444;
  font-size: 14px;
}
.navbar-nav > .notifications-menu > .dropdown-menu > li.footer > a,
.navbar-nav > .messages-menu > .dropdown-menu > li.footer > a,
.navbar-nav > .tasks-menu > .dropdown-menu > li.footer > a {
  border-top-left-radius: 0;
  border-top-right-radius: 0;
  border-bottom-right-radius: 4px;
  border-bottom-left-radius: 4px;
  font-size: 12px;
  background-color: #fff;
  padding: 7px 10px;
  border-bottom: 1px solid #eeeeee;
  color: #444 !important;
  text-align: center;
}
@media (max-width: 991px) {
  .navbar-nav > .notifications-menu > .dropdown-menu > li.footer > a,
  .navbar-nav > .messages-menu > .dropdown-menu > li.footer > a,
  .navbar-nav > .tasks-menu > .dropdown-menu > li.footer > a {
    background: #fff !important;
    color: #444 !important;
  }
}
.navbar-nav > .notifications-menu > .dropdown-menu > li.footer > a:hover,
.navbar-nav > .messages-menu > .dropdown-menu > li.footer > a:hover,
.navbar-nav > .tasks-menu > .dropdown-menu > li.footer > a:hover {
  text-decoration: none;
  font-weight: normal;
}
.navbar-nav > .notifications-menu > .dropdown-menu > li .menu,
.navbar-nav > .messages-menu > .dropdown-menu > li .menu,
.navbar-nav > .tasks-menu > .dropdown-menu > li .menu {
  max-height: 200px;
  margin: 0;
  padding: 0;
  list-style: none;
  overflow-x: hidden;
}
.navbar-nav > .notifications-menu > .dropdown-menu > li .menu > li > a,
.navbar-nav > .messages-menu > .dropdown-menu > li .menu > li > a,
.navbar-nav > .tasks-menu > .dropdown-menu > li .menu > li > a {
  display: block;
  white-space: nowrap;
  /* Prevent text from breaking */
  border-bottom: 1px solid #f4f4f4;
}
.navbar-nav > .notifications-menu > .dropdown-menu > li .menu > li > a:hover,
.navbar-nav > .messages-menu > .dropdown-menu > li .menu > li > a:hover,
.navbar-nav > .tasks-menu > .dropdown-menu > li .menu > li > a:hover {
  background: #f4f4f4;
  text-decoration: none;
}
.navbar-nav > .notifications-menu > .dropdown-menu > li .menu > li > a {
  color: #444444;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  padding: 10px;
}
.navbar-nav > .notifications-menu > .dropdown-menu > li .menu > li > a > .glyphicon,
.navbar-nav > .notifications-menu > .dropdown-menu > li .menu > li > a > .fa,
.navbar-nav > .notifications-menu > .dropdown-menu > li .menu > li > a > .ion {
  width: 20px;
}
.navbar-nav > .messages-menu > .dropdown-menu > li .menu > li > a {
  margin: 0;
  padding: 10px 10px;
}
.navbar-nav > .messages-menu > .dropdown-menu > li .menu > li > a > div > img {
  margin: auto 10px auto auto;
  width: 40px;
  height: 40px;
}
.navbar-nav > .messages-menu > .dropdown-menu > li .menu > li > a > h4 {
  padding: 0;
  margin: 0 0 0 45px;
  color: #444444;
  font-size: 15px;
  position: relative;
}
.navbar-nav > .messages-menu > .dropdown-menu > li .menu > li > a > h4 > small {
  color: #999999;
  font-size: 10px;
  position: absolute;
  top: 0;
  right: 0;
}
.navbar-nav > .messages-menu > .dropdown-menu > li .menu > li > a > p {
  margin: 0 0 0 45px;
  font-size: 12px;
  color: #888888;
}
.navbar-nav > .messages-menu > .dropdown-menu > li .menu > li > a:before,
.navbar-nav > .messages-menu > .dropdown-menu > li .menu > li > a:after {
  content: ' ';
  display: table;
}
.navbar-nav > .messages-menu > .dropdown-menu > li .menu > li > a:after {
  clear: both;
}
.navbar-nav > .tasks-menu > .dropdown-menu > li .menu > li > a {
  padding: 10px;
}
.navbar-nav > .tasks-menu > .dropdown-menu > li .menu > li > a > h3 {
  font-size: 14px;
  padding: 0;
  margin: 0 0 10px 0;
  color: #666666;
}
.navbar-nav > .tasks-menu > .dropdown-menu > li .menu > li > a > .progress {
  padding: 0;
  margin: 0;
}
.navbar-nav > .user-menu > .dropdown-menu {
  padding: 1px 0 0 0;
  border: 1px solid #fff;
  border-top-width: 0;
  width: 150px;
}
.navbar-nav > .user-menu > .dropdown-menu,
.navbar-nav > .user-menu > .dropdown-menu > .user-body {
  border-bottom-right-radius: 4px;
  border-bottom-left-radius: 4px;
  background: #1b88e63f;
}
.navbar-nav > .user-menu > .dropdown-menu > li.user-header {
  height: 175px;
  padding: 10px;
  text-align: center;
}
.navbar-nav > .user-menu > .dropdown-menu > li.user-header > img {
  z-index: 5;
  height: 90px;
  width: 90px;
  border: 3px solid;
  border-color: transparent;
  border-color: rgba(255, 255, 255, 0.2);
}
.navbar-nav > .user-menu > .dropdown-menu > li.user-header > p {
  z-index: 5;
  color: #fff;
  color: rgba(255, 255, 255, 0.8);
  font-size: 17px;
  margin-top: 10px;
}
.navbar-nav > .user-menu > .dropdown-menu > li.user-header > p > small {
  display: block;
  font-size: 12px;
}
.navbar-nav > .user-menu > .dropdown-menu > .user-body {
  padding: 15px;
  border-bottom: 1px solid #f4f4f4;
  border-top: 1px solid #dddddd;
}
.navbar-nav > .user-menu > .dropdown-menu > .user-body:before,
.navbar-nav > .user-menu > .dropdown-menu > .user-body:after {
  content: ' ';
  display: table;
}
.navbar-nav > .user-menu > .dropdown-menu > .user-body:after {
  clear: both;
}
.navbar-nav > .user-menu > .dropdown-menu > .user-body a {
  color: #444 !important;
}
@media (max-width: 991px) {
  .navbar-nav > .user-menu > .dropdown-menu > .user-body a {
    background: #fff !important;
    color: #444 !important;
  }
}
.navbar-nav > .user-menu > .dropdown-menu > .user-footer {
  padding: 10px;
}
.navbar-nav > .user-menu > .dropdown-menu > .user-footer:before,
.navbar-nav > .user-menu > .dropdown-menu > .user-footer:after {
  content: ' ';
  display: table;
}
.navbar-nav > .user-menu > .dropdown-menu > .user-footer:after {
  clear: both;
}
.navbar-nav > .user-menu > .dropdown-menu > .user-footer .btn-default {
  color: #fff;
}
.navbar-nav > .user-menu > .dropdown-menu > .user-footer .btn-default:hover {
  background-color: #f9f9f9;
}

.navbar-nav > .user-menu .user-image {
  float: left;
  width: 25px;
  height: 25px;
  border-radius: 50%;
  margin-right: 10px;
  margin-top: -2px;
}
@media (max-width: 767px) {
  .navbar-nav > .user-menu .user-image {
    float: none;
    margin-right: 0;
    margin-top: -8px;
    line-height: 10px;
  }
}
/* Add fade animation to dropdown menus by appending
 the class .animated-dropdown-menu to the .dropdown-menu ul (or ol)*/
.open:not(.dropup) > .animated-dropdown-menu {
  backface-visibility: visible !important;
  -webkit-animation: flipInX 0.7s both;
  -o-animation: flipInX 0.7s both;
  animation: flipInX 0.7s both;
}
@keyframes flipInX {
  0% {
    transform: perspective(400px) rotate3d(1, 0, 0, 90deg);
    transition-timing-function: ease-in;
    opacity: 0;
  }
  40% {
    transform: perspective(400px) rotate3d(1, 0, 0, -20deg);
    transition-timing-function: ease-in;
  }
  60% {
    transform: perspective(400px) rotate3d(1, 0, 0, 10deg);
    opacity: 1;
  }
  80% {
    transform: perspective(400px) rotate3d(1, 0, 0, -5deg);
  }
  100% {
    transform: perspective(400px);
  }
}
@-webkit-keyframes flipInX {
  0% {
    -webkit-transform: perspective(400px) rotate3d(1, 0, 0, 90deg);
    -webkit-transition-timing-function: ease-in;
    opacity: 0;
  }
  40% {
    -webkit-transform: perspective(400px) rotate3d(1, 0, 0, -20deg);
    -webkit-transition-timing-function: ease-in;
  }
  60% {
    -webkit-transform: perspective(400px) rotate3d(1, 0, 0, 10deg);
    opacity: 1;
  }
  80% {
    -webkit-transform: perspective(400px) rotate3d(1, 0, 0, -5deg);
  }
  100% {
    -webkit-transform: perspective(400px);
  }
}
/* Fix dropdown menu in navbars */
.navbar-custom-menu > .navbar-nav > li {
  position: relative;
}
.navbar-custom-menu > .navbar-nav > li > .dropdown-menu {
  position: absolute;
  right: 0;
  left: auto;
}
@media (max-width: 991px) {
  .navbar-custom-menu > .navbar-nav {
    float: right;
  }
  .navbar-custom-menu > .navbar-nav > li {
    position: static;
  }
  .navbar-custom-menu > .navbar-nav > li > .dropdown-menu {
    position: absolute;
    left: auto;
    border: 1px solid #ddd;
    background: #fff;
  }
}
/*
 * Component: Form
 * ---------------
 */
.form-control {
  border-radius: 0;
  box-shadow: none;
  /*border-color: #d2d6de;*/
  color: #333;
  background: #eeeeee;
}
.form-control:focus {
  /*border-color: #4589d0;*/
  box-shadow: none;
}
.form-control::-moz-placeholder,
.form-control:-ms-input-placeholder,
.form-control::-webkit-input-placeholder {
  color: #bbb;
  opacity: 1;
}
.form-control:not(select) {
  -webkit-appearance: none;
  -moz-appearance: none;
  appearance: none;
}
.form-group.has-success label {
  color: #00a65a;
}
.form-group.has-success .form-control {
  border-color: #00a65a;
  box-shadow: none;
}
.form-group.has-warning label {
  color: #f39c12;
}
.form-group.has-warning .form-control {
  border-color: #f39c12;
  box-shadow: none;
}
.form-group.has-error label {
  color: #dd4b39;
}
.form-group.has-error .form-control {
  border-color: #dd4b39;
  box-shadow: none;
}
/* Input group */
.input-group .input-group-addon {
  border-radius: 10px;
  border-color: #d2d6de;
  background-color: #2c3038;
  font-weight: bold;
  color: white;
}
.input-group-addon:first-child {
  border-right: 0;
  padding-left: 20px;
  padding-right: 20px;
  min-width: 110px;
}
/* item group */
.item-group > div {
  float: left;
  margin-left: 20px;
  margin-bottom: 10px;
}
.item-group .selectBox {
  width: 200px;
}
@media (max-width: 767px) {
  .item-group > div {
    margin-left: 0;
  }
  .item-group .btn-group {
    text-align: center;
  }
  .item-group .selectBox {
    width: 100%;
  }
}
/* button groups */
.btn-group-vertical .btn.btn-flat:first-of-type,
.btn-group-vertical .btn.btn-flat:last-of-type {
  border-radius: 0;
}
.icheck > label {
  padding-left: 0;
}
/* support Font Awesome icons in form-control */
.form-control-feedback.fa {
  line-height: 34px;
}
.input-lg + .form-control-feedback.fa,
.input-group-lg + .form-control-feedback.fa,
.form-group-lg .form-control + .form-control-feedback.fa {
  line-height: 46px;
}
.input-sm + .form-control-feedback.fa,
.input-group-sm + .form-control-feedback.fa,
.form-group-sm .form-control + .form-control-feedback.fa {
  line-height: 30px;
}
/*
 * Component: Progress Bar
 * -----------------------
 */
.progress,
.progress > .progress-bar {
  -webkit-box-shadow: none;
  box-shadow: none;
}
.progress,
.progress > .progress-bar,
.progress .progress-bar,
.progress > .progress-bar .progress-bar {
  border-radius: 1px;
}
/* size variation */
.progress.sm,
.progress-sm {
  height: 10px;
}
.progress.sm,
.progress-sm,
.progress.sm .progress-bar,
.progress-sm .progress-bar {
  border-radius: 1px;
}
.progress.xs,
.progress-xs {
  height: 7px;
}
.progress.xs,
.progress-xs,
.progress.xs .progress-bar,
.progress-xs .progress-bar {
  border-radius: 1px;
}
.progress.xxs,
.progress-xxs {
  height: 3px;
}
.progress.xxs,
.progress-xxs,
.progress.xxs .progress-bar,
.progress-xxs .progress-bar {
  border-radius: 1px;
}
/* Vertical bars */
.progress.vertical {
  position: relative;
  width: 30px;
  height: 200px;
  display: inline-block;
  margin-right: 10px;
}
.progress.vertical > .progress-bar {
  width: 100%;
  position: absolute;
  bottom: 0;
}
.progress.vertical.sm,
.progress.vertical.progress-sm {
  width: 20px;
}
.progress.vertical.xs,
.progress.vertical.progress-xs {
  width: 10px;
}
.progress.vertical.xxs,
.progress.vertical.progress-xxs {
  width: 3px;
}
.progress-group .progress-text {
  font-weight: 600;
}
.progress-group .progress-number {
  float: right;
}
/* Remove margins from progress bars when put in a table */
.table tr > td .progress {
  margin: 0;
}
.progress-bar-light-blue,
.progress-bar-primary {
  background-color: #3c8dbc;
}
.progress-striped .progress-bar-light-blue,
.progress-striped .progress-bar-primary {
  background-image: -webkit-linear-gradient(
    45deg,
    rgba(255, 255, 255, 0.15) 25%,
    transparent 25%,
    transparent 50%,
    rgba(255, 255, 255, 0.15) 50%,
    rgba(255, 255, 255, 0.15) 75%,
    transparent 75%,
    transparent
  );
  background-image: -o-linear-gradient(
    45deg,
    rgba(255, 255, 255, 0.15) 25%,
    transparent 25%,
    transparent 50%,
    rgba(255, 255, 255, 0.15) 50%,
    rgba(255, 255, 255, 0.15) 75%,
    transparent 75%,
    transparent
  );
  background-image: linear-gradient(
    45deg,
    rgba(255, 255, 255, 0.15) 25%,
    transparent 25%,
    transparent 50%,
    rgba(255, 255, 255, 0.15) 50%,
    rgba(255, 255, 255, 0.15) 75%,
    transparent 75%,
    transparent
  );
}
.progress-bar-green,
.progress-bar-success {
  background-color: #00a65a;
}
.progress-striped .progress-bar-green,
.progress-striped .progress-bar-success {
  background-image: -webkit-linear-gradient(
    45deg,
    rgba(255, 255, 255, 0.15) 25%,
    transparent 25%,
    transparent 50%,
    rgba(255, 255, 255, 0.15) 50%,
    rgba(255, 255, 255, 0.15) 75%,
    transparent 75%,
    transparent
  );
  background-image: -o-linear-gradient(
    45deg,
    rgba(255, 255, 255, 0.15) 25%,
    transparent 25%,
    transparent 50%,
    rgba(255, 255, 255, 0.15) 50%,
    rgba(255, 255, 255, 0.15) 75%,
    transparent 75%,
    transparent
  );
  background-image: linear-gradient(
    45deg,
    rgba(255, 255, 255, 0.15) 25%,
    transparent 25%,
    transparent 50%,
    rgba(255, 255, 255, 0.15) 50%,
    rgba(255, 255, 255, 0.15) 75%,
    transparent 75%,
    transparent
  );
}
.progress-bar-aqua,
.progress-bar-info {
  background-color: #7c9dbc;
}
.progress-striped .progress-bar-aqua,
.progress-striped .progress-bar-info {
  background-image: -webkit-linear-gradient(
    45deg,
    rgba(255, 255, 255, 0.15) 25%,
    transparent 25%,
    transparent 50%,
    rgba(255, 255, 255, 0.15) 50%,
    rgba(255, 255, 255, 0.15) 75%,
    transparent 75%,
    transparent
  );
  background-image: -o-linear-gradient(
    45deg,
    rgba(255, 255, 255, 0.15) 25%,
    transparent 25%,
    transparent 50%,
    rgba(255, 255, 255, 0.15) 50%,
    rgba(255, 255, 255, 0.15) 75%,
    transparent 75%,
    transparent
  );
  background-image: linear-gradient(
    45deg,
    rgba(255, 255, 255, 0.15) 25%,
    transparent 25%,
    transparent 50%,
    rgba(255, 255, 255, 0.15) 50%,
    rgba(255, 255, 255, 0.15) 75%,
    transparent 75%,
    transparent
  );
}
.progress-bar-yellow,
.progress-bar-warning {
  background-color: #f39c12;
}
.progress-striped .progress-bar-yellow,
.progress-striped .progress-bar-warning {
  background-image: -webkit-linear-gradient(
    45deg,
    rgba(255, 255, 255, 0.15) 25%,
    transparent 25%,
    transparent 50%,
    rgba(255, 255, 255, 0.15) 50%,
    rgba(255, 255, 255, 0.15) 75%,
    transparent 75%,
    transparent
  );
  background-image: -o-linear-gradient(
    45deg,
    rgba(255, 255, 255, 0.15) 25%,
    transparent 25%,
    transparent 50%,
    rgba(255, 255, 255, 0.15) 50%,
    rgba(255, 255, 255, 0.15) 75%,
    transparent 75%,
    transparent
  );
  background-image: linear-gradient(
    45deg,
    rgba(255, 255, 255, 0.15) 25%,
    transparent 25%,
    transparent 50%,
    rgba(255, 255, 255, 0.15) 50%,
    rgba(255, 255, 255, 0.15) 75%,
    transparent 75%,
    transparent
  );
}
.progress-bar-red,
.progress-bar-danger {
  background-color: #dd4b39;
}
.progress-striped .progress-bar-red,
.progress-striped .progress-bar-danger {
  background-image: -webkit-linear-gradient(
    45deg,
    rgba(255, 255, 255, 0.15) 25%,
    transparent 25%,
    transparent 50%,
    rgba(255, 255, 255, 0.15) 50%,
    rgba(255, 255, 255, 0.15) 75%,
    transparent 75%,
    transparent
  );
  background-image: -o-linear-gradient(
    45deg,
    rgba(255, 255, 255, 0.15) 25%,
    transparent 25%,
    transparent 50%,
    rgba(255, 255, 255, 0.15) 50%,
    rgba(255, 255, 255, 0.15) 75%,
    transparent 75%,
    transparent
  );
  background-image: linear-gradient(
    45deg,
    rgba(255, 255, 255, 0.15) 25%,
    transparent 25%,
    transparent 50%,
    rgba(255, 255, 255, 0.15) 50%,
    rgba(255, 255, 255, 0.15) 75%,
    transparent 75%,
    transparent
  );
}
/*
 * Component: Small Box
 * --------------------
 */
.small-box {
  border-radius: 2px;
  position: relative;
  display: block;
  margin-bottom: 20px;
  box-shadow: 0 1px 1px rgba(0, 0, 0, 0.1);
}
.small-box > .inner {
  padding: 10px;
}
.small-box > .small-box-footer {
  position: relative;
  text-align: center;
  padding: 3px 0;
  color: #fff;
  color: rgba(255, 255, 255, 0.8);
  display: block;
  z-index: 10;
  background: rgba(0, 0, 0, 0.1);
  text-decoration: none;
}
.small-box > .small-box-footer:hover {
  color: #fff;
  background: rgba(0, 0, 0, 0.15);
}
.small-box h3 {
  font-size: 38px;
  font-weight: bold;
  margin: 0 0 10px 0;
  white-space: nowrap;
  padding: 0;
}
.small-box p {
  font-size: 15px;
}
.small-box p > small {
  display: block;
  color: #f9f9f9;
  font-size: 13px;
  margin-top: 5px;
}
.small-box h3,
.small-box p {
  z-index: 5;
}
.small-box .icon {
  -webkit-transition: all 0.3s linear;
  -o-transition: all 0.3s linear;
  transition: all 0.3s linear;
  position: absolute;
  top: -10px;
  right: 10px;
  z-index: 0;
  font-size: 90px;
  color: rgba(0, 0, 0, 0.15);
}
.small-box:hover {
  text-decoration: none;
  color: #f9f9f9;
}
.small-box:hover .icon {
  font-size: 95px;
}
@media (max-width: 767px) {
  .small-box {
    text-align: center;
  }
  .small-box .icon {
    display: none;
  }
  .small-box p {
    font-size: 12px;
  }
}
/*
 * Component: Box
 * --------------
 */
.box {
  position: relative;
  border-radius: 3px;
  background: #ffffff;
  border: 1px solid #d2d6de;
  margin-bottom: 20px;
  width: 100%;
  box-shadow: 0 1px 1px rgba(0, 0, 0, 0.1);
}
.box.box-primary {
  border-top-color: #e6e6e6;
}
.box.box-info {
  border-top-color: #f5f5f5;
}
.box.box-danger {
  border-top-color: #dd4b39;
}
.box.box-warning {
  border-top-color: #f39c12;
}
.box.box-success {
  border-top-color: #bbcbdc;
}
.box.box-default {
  border-top-color: #7a96ce;
}
.box.collapsed-box .box-body,
.box.collapsed-box .box-footer {
  display: none;
}
.box .nav-stacked > li {
  border-bottom: 1px solid #f4f4f4;
  margin: 0;
}
.box .nav-stacked > li:last-of-type {
  border-bottom: none;
}
.box.height-control .box-body {
  max-height: 300px;
  overflow: auto;
}
.box .border-right {
  border-right: 1px solid #f4f4f4;
}
.box .border-left {
  border-left: 1px solid #f4f4f4;
}
.box.box-solid {
  border-top: 0;
}
.box.box-solid > .box-header .btn.btn-default {
  background: transparent;
}
.box.box-solid > .box-header .btn:hover,
.box.box-solid > .box-header a:hover {
  background: rgba(0, 0, 0, 0.1);
}
.box.box-solid.box-default {
  border: 1px solid #d2d6de;
}
.box.box-solid.box-default > .box-header {
  color: #444444;
  background: #d2d6de;
  background-color: #d2d6de;
}
.box.box-solid.box-default > .box-header a,
.box.box-solid.box-default > .box-header .btn {
  color: #444444;
}
.box.box-solid.box-primary {
  border: 1px solid #3c8dbc;
}
.box.box-solid.box-primary > .box-header {
  color: #ffffff;
  background: #3c8dbc;
  background-color: #3c8dbc;
}
.box.box-solid.box-primary > .box-header a,
.box.box-solid.box-primary > .box-header .btn {
  color: #ffffff;
}
.box.box-solid.box-info {
  border: 1px solid #00c0ef;
}
.box.box-solid.box-info > .box-header {
  color: #ffffff;
  background: #00c0ef;
  background-color: #00c0ef;
}
.box.box-solid.box-info > .box-header a,
.box.box-solid.box-info > .box-header .btn {
  color: #ffffff;
}
.box.box-solid.box-danger {
  border: 1px solid #dd4b39;
}
.box.box-solid.box-danger > .box-header {
  color: #ffffff;
  background: #dd4b39;
  background-color: #dd4b39;
}
.box.box-solid.box-danger > .box-header a,
.box.box-solid.box-danger > .box-header .btn {
  color: #ffffff;
}
.box.box-solid.box-warning {
  border: 1px solid #f39c12;
}
.box.box-solid.box-warning > .box-header {
  color: #ffffff;
  background: #f39c12;
  background-color: #f39c12;
}
.box.box-solid.box-warning > .box-header a,
.box.box-solid.box-warning > .box-header .btn {
  color: #ffffff;
}
.box.box-solid.box-success {
  border: 1px solid #00a65a;
}
.box.box-solid.box-success > .box-header {
  color: #ffffff;
  background: #00a65a;
  background-color: #00a65a;
}
.box.box-solid.box-success > .box-header a,
.box.box-solid.box-success > .box-header .btn {
  color: #ffffff;
}
.box.box-solid > .box-header > .box-tools .btn {
  border: 0;
  box-shadow: none;
}
.box.box-solid[class*='bg'] > .box-header {
  color: #fff;
}
.box .box-group > .box {
  margin-bottom: 5px;
}
.box .knob-label {
  text-align: center;
  color: #333;
  font-weight: 100;
  font-size: 12px;
  margin-bottom: 0.3em;
}
.box > .overlay,
.overlay-wrapper > .overlay,
.box > .loading-img,
.overlay-wrapper > .loading-img {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
}
.box .overlay,
.overlay-wrapper .overlay {
  z-index: 50;
  background: rgba(255, 255, 255, 0.7);
  border-radius: 3px;
}
.box .overlay > .fa,
.overlay-wrapper .overlay > .fa {
  position: absolute;
  top: 50%;
  left: 50%;
  margin-left: -15px;
  margin-top: -15px;
  color: #000;
  font-size: 30px;
}
.box .overlay.dark,
.overlay-wrapper .overlay.dark {
  background: rgba(0, 0, 0, 0.5);
}
.box-header:before,
.box-body:before,
.box-footer:before,
.box-header:after,
.box-body:after,
.box-footer:after {
  content: ' ';
  display: table;
}
.box-header:after,
.box-body:after,
.box-footer:after {
  clear: both;
}
.box-header {
  color: #444;
  display: block;
  padding: 10px;
  position: relative;
}
.box-header.with-border {
  border-bottom: 1px solid #f4f4f4;
}
.collapsed-box .box-header.with-border {
  border-bottom: none;
}
.box-header > .fa,
.box-header > .glyphicon,
.box-header > .ion,
.box-header .box-title {
  display: inline-block;
  font-size: 18px;
  margin: 0;
  line-height: 1;
}
.box-header > .fa,
.box-header > .glyphicon,
.box-header > .ion {
  margin-right: 5px;
}
.box-header > .box-tools {
  position: absolute;
  right: 10px;
  top: 5px;
  padding: inherit;
}
.box-header > .box-tools [data-toggle='tooltip'] {
  position: relative;
}
.box-header > .box-tools.pull-right .dropdown-menu {
  right: 0;
  left: auto;
}
.btn-box-tool {
  padding: 5px;
  font-size: 12px;
  background: transparent;
  color: #f39c12;
}
.open .btn-box-tool,
.btn-box-tool:hover {
  color: #606c84;
}
.btn-box-tool.btn:active {
  box-shadow: none;
}
.box-body {
  border-top-left-radius: 0;
  border-top-right-radius: 0;
  border-bottom-right-radius: 3px;
  border-bottom-left-radius: 3px;
  padding: 10px;
}
.no-header .box-body {
  border-top-right-radius: 3px;
  border-top-left-radius: 3px;
}
.box-body > .table {
  margin-bottom: 0;
}
.box-body .fc {
  margin-top: 5px;
}
.box-body .full-width-chart {
  margin: -19px;
}
.box-body.no-padding .full-width-chart {
  margin: -9px;
}
.box-body .box-pane {
  border-top-left-radius: 0;
  border-top-right-radius: 0;
  border-bottom-right-radius: 0;
  border-bottom-left-radius: 3px;
}
.box-body .box-pane-right {
  border-top-left-radius: 0;
  border-top-right-radius: 0;
  border-bottom-right-radius: 3px;
  border-bottom-left-radius: 0;
}
.box-body .box-card .box-card-input {
  border: 2px solid #ccc;
  border-radius: 4px;
  height: 120px;
  font-size: 2rem;
  padding: 15px;
}
.box-card div {
  height: 60px;
}
.box-card-input > div:nth-child(2) {
  float: right;
}
.box-footer {
  border-top-left-radius: 0;
  border-top-right-radius: 0;
  border-bottom-right-radius: 3px;
  border-bottom-left-radius: 3px;
  /*border-top: 1px solid #f4f4f4;*/
  padding: 10px;
  background-color: #ffffff;
}
.chart-legend {
  margin: 10px 0;
}
@media (max-width: 991px) {
  .chart-legend > li {
    float: left;
    margin-right: 10px;
  }
}
.box-comments {
  background: #f7f7f7;
}
.box-comments .box-comment {
  padding: 8px 0;
  border-bottom: 1px solid #eee;
}
.box-comments .box-comment:before,
.box-comments .box-comment:after {
  content: ' ';
  display: table;
}
.box-comments .box-comment:after {
  clear: both;
}
.box-comments .box-comment:last-of-type {
  border-bottom: 0;
}
.box-comments .box-comment:first-of-type {
  padding-top: 0;
}
.box-comments .box-comment img {
  float: left;
}
.box-comments .comment-text {
  margin-left: 40px;
  color: #555;
}
.box-comments .username {
  color: #444;
  display: block;
  font-weight: 600;
}
.box-comments .text-muted {
  font-weight: 400;
  font-size: 12px;
}
/* Widget: TODO LIST */
.todo-list {
  margin: 0;
  padding: 0;
  list-style: none;
  overflow: auto;
}
.todo-list > li {
  border-radius: 2px;
  padding: 10px;
  background: #f4f4f4;
  margin-bottom: 2px;
  border-left: 2px solid #e6e7e8;
  color: #444;
}
.todo-list > li:last-of-type {
  margin-bottom: 0;
}
.todo-list > li > input[type='checkbox'] {
  margin: 0 10px 0 5px;
}
.todo-list > li .text {
  display: inline-block;
  margin-left: 5px;
  font-weight: 600;
}
.todo-list > li .label {
  margin-left: 10px;
  font-size: 9px;
}
.todo-list > li .tools {
  display: none;
  float: right;
  color: #dd4b39;
}
.todo-list > li .tools > .fa,
.todo-list > li .tools > .glyphicon,
.todo-list > li .tools > .ion {
  margin-right: 5px;
  cursor: pointer;
}
.todo-list > li:hover .tools {
  display: inline-block;
}
.todo-list > li.done {
  color: #999;
}
.todo-list > li.done .text {
  text-decoration: line-through;
  font-weight: 500;
}
.todo-list > li.done .label {
  background: #d2d6de !important;
}
.todo-list .danger {
  border-left-color: #dd4b39;
}
.todo-list .warning {
  border-left-color: #f39c12;
}
.todo-list .info {
  border-left-color: #00c0ef;
}
.todo-list .success {
  border-left-color: #00a65a;
}
.todo-list .primary {
  border-left-color: #3c8dbc;
}
.todo-list .handle {
  display: inline-block;
  cursor: move;
  margin: 0 5px;
}
/* Chat widget (DEPRECATED - this will be removed in the next major release. Use Direct Chat instead)*/
.chat {
  padding: 5px 20px 5px 10px;
}
.chat .item {
  margin-bottom: 10px;
}
.chat .item:before,
.chat .item:after {
  content: ' ';
  display: table;
}
.chat .item:after {
  clear: both;
}
.chat .item > img {
  width: 40px;
  height: 40px;
  border: 2px solid transparent;
  border-radius: 50%;
}
.chat .item > .online {
  border: 2px solid #00a65a;
}
.chat .item > .offline {
  border: 2px solid #dd4b39;
}
.chat .item > .message {
  margin-left: 55px;
  margin-top: -40px;
}
.chat .item > .message > .name {
  display: block;
  font-weight: 600;
}
.chat .item > .attachment {
  border-radius: 3px;
  background: #f4f4f4;
  margin-left: 65px;
  margin-right: 15px;
  padding: 10px;
}
.chat .item > .attachment > h4 {
  margin: 0 0 5px 0;
  font-weight: 600;
  font-size: 14px;
}
.chat .item > .attachment > p,
.chat .item > .attachment > .filename {
  font-weight: 600;
  font-size: 13px;
  font-style: italic;
  margin: 0;
}
.chat .item > .attachment:before,
.chat .item > .attachment:after {
  content: ' ';
  display: table;
}
.chat .item > .attachment:after {
  clear: both;
}
.box-input {
  max-width: 200px;
}
.modal .panel-body {
  color: #444;
}
/*
 * Component: Info Box
 * -------------------
 */
.info-box {
  display: block;
  min-height: 90px;
  background: #fff;
  width: 100%;
  box-shadow: 0 1px 1px rgba(0, 0, 0, 0.1);
  border-radius: 2px;
  margin-bottom: 15px;
}
.info-box small {
  font-size: 14px;
}
.info-box .progress {
  background: rgba(0, 0, 0, 0.2);
  margin: 5px -10px 5px -10px;
  height: 2px;
}
.info-box .progress,
.info-box .progress .progress-bar {
  border-radius: 0;
}
.info-box .progress .progress-bar {
  background: #fff;
}
.info-box-icon {
  border-top-left-radius: 2px;
  border-top-right-radius: 0;
  border-bottom-right-radius: 0;
  border-bottom-left-radius: 2px;
  display: block;
  float: left;
  height: 90px;
  width: 90px;
  text-align: center;
  font-size: 45px;
  line-height: 90px;
  background: rgba(0, 0, 0, 0.2);
}
.info-box-icon > img {
  max-width: 100%;
}
.info-box-content {
  padding: 5px 10px;
  margin-left: 90px;
}
.info-box-number {
  display: block;
  font-weight: bold;
  font-size: 18px;
}
.progress-description,
.info-box-text {
  display: block;
  font-size: 14px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.info-box-text {
  text-transform: uppercase;
}
.info-box-more {
  display: block;
}
.progress-description {
  margin: 0;
}
/*
 * Component: Timeline
 * -------------------
 */
.timeline {
  position: relative;
  margin: 0 0 30px 0;
  padding: 0;
  list-style: none;
}
.timeline:before {
  content: '';
  position: absolute;
  top: 0;
  bottom: 0;
  width: 4px;
  background: #ddd;
  left: 31px;
  margin: 0;
  border-radius: 2px;
}
.timeline > li {
  position: relative;
  margin-right: 10px;
  margin-bottom: 15px;
}
.timeline > li:before,
.timeline > li:after {
  content: ' ';
  display: table;
}
.timeline > li:after {
  clear: both;
}
.timeline > li > .timeline-item {
  -webkit-box-shadow: 0 1px 1px rgba(0, 0, 0, 0.1);
  box-shadow: 0 1px 1px rgba(0, 0, 0, 0.1);
  border-radius: 3px;
  margin-top: 0;
  background: #fff;
  color: #444;
  margin-left: 60px;
  margin-right: 15px;
  padding: 0;
  position: relative;
}
.timeline > li > .timeline-item > .time {
  color: #999;
  float: right;
  padding: 10px;
  font-size: 12px;
}
.timeline > li > .timeline-item > .timeline-header {
  margin: 0;
  color: #555;
  border-bottom: 1px solid #f4f4f4;
  padding: 10px;
  font-size: 16px;
  line-height: 1.1;
}
.timeline > li > .timeline-item > .timeline-header > a {
  font-weight: 600;
}
.timeline > li > .timeline-item > .timeline-body,
.timeline > li > .timeline-item > .timeline-footer {
  padding: 10px;
}
.timeline > li > .fa,
.timeline > li > .glyphicon,
.timeline > li > .ion {
  width: 30px;
  height: 30px;
  font-size: 15px;
  line-height: 30px;
  position: absolute;
  color: #666;
  background: #d2d6de;
  border-radius: 50%;
  text-align: center;
  left: 18px;
  top: 0;
}
.timeline > .time-label > span {
  font-weight: 600;
  padding: 5px;
  display: inline-block;
  background-color: #fff;
  border-radius: 4px;
}
.timeline-inverse > li > .timeline-item {
  background: #f0f0f0;
  border: 1px solid #ddd;
  -webkit-box-shadow: none;
  box-shadow: none;
}
.timeline-inverse > li > .timeline-item > .timeline-header {
  border-bottom-color: #ddd;
}
/*
 * Component: Button
 * -----------------
 */
.btn {
  /*height:30px;*/
  border-radius: 3px;
  -webkit-box-shadow: none;
  box-shadow: none;
  border: 1px solid #bebebe;
  background: none;
}
.btn-xs,
.btn-group-xs > .btn {
  margin: 1px 2px;
  padding: 3px 1px;
  width: 30px;
  font-size: 14px;
  line-height: 1.2;
  border-radius: 4px;
}
.btn.uppercase {
  text-transform: uppercase;
}
.btn.btn-flat {
  border-radius: 0;
  -webkit-box-shadow: none;
  -moz-box-shadow: none;
  box-shadow: none;
  border-width: 1px;
}
.btn:active {
  -webkit-box-shadow: inset 0 3px 5px rgba(0, 0, 0, 0.125);
  -moz-box-shadow: inset 0 3px 5px rgba(0, 0, 0, 0.125);
  box-shadow: inset 0 3px 5px rgba(0, 0, 0, 0.125);
}
.btn:focus {
  outline: none;
}
.btn.btn-file {
  position: relative;
  overflow: hidden;
}
.btn.btn-file > input[type='file'] {
  position: absolute;
  top: 0;
  right: 0;
  min-width: 100%;
  min-height: 100%;
  font-size: 100px;
  text-align: right;
  opacity: 0;
  filter: alpha(opacity=0);
  outline: none;
  background: white;
  cursor: inherit;
  display: block;
}
.btn-default {
  /* background-color: #f4f4f4; */
  color: #444;
  border-color: #ddd;
}
.btn-default:hover,
.btn-default:active,
.btn-default:focus,
.btn-default:visited {
  background-color: #e7e7e7 !important;
}
.btn-primary {
  background-color: #35383c;
  /*border-color: #367fa9;*/
}
.btn-primary:hover,
.btn-primary:active,
.btn-primary:focus,
.btn-primary:visited {
  background-color: #242628 !important;
  border-color: #242628 !important;
}
.btn-update {
  color: #fff;
  background-color: #66c4fc;
  border: 2px solid transparent;
  border-color: #68c1f5;
}
.btn-update:hover,
.btn-update:active,
.btn-update:focus,
.btn-update:visited {
  color: #008ee2;
  background-color: #fff;
  border: 2px solid transparent;
  border-color: #008ee2;
}
.btn-list {
  color: #fff;
  background-color: #67c5b7;
  border: 2px solid transparent;
  border-color: #59b5a7;
}
.btn-list:hover,
.btn-list:active,
.btn-list:focus,
.btn-list:visited {
  color: #48a597;
  background-color: #fff;
  border: 2px solid transparent;
  border-color: #48a597;
}
.btn-insert {
  color: #fff;
  background-color: #8497f2;
  border: 2px solid transparent;
  border-color: #7285e1;
}
.btn-insert:hover,
.btn-insert:active,
.btn-insert:focus,
.btn-insert:visited {
  color: #7285e1;
  background-color: #fff;
  border: 2px solid transparent;
  border-color: #7285e1;
}
.btn-info {
  color: #fff;
  background-color: #00c0ef;
  border: 2px solid transparent;
  border-color: #00acd6;
}
.btn-info:hover,
.btn-info:active,
.btn-info:focus,
.btn-info:visited {
  color: #4589d0 !important;
  background-color: #fff !important;
  border: 2px solid transparent;
  border-color: #4589d0 !important;
}
.btn-popup {
  color: #fff;
  background-color: #4589d0;
  border: 2px solid transparent;
  border-color: #4589d0;
}
.btn-popup:hover,
.btn-popup:active,
.btn-popup:focus,
.btn-popup:visited {
  color: #4589d0;
  background-color: #fff;
  border: 2px solid transparent;
  border-color: #4589d0;
}
.btn-danger {
  color: #fff;
  background-color: #df0100 !important;
  border: 2px solid transparent;
  border-color: #df0100 !important;
}
.btn-danger:hover,
.btn-danger:active,
.btn-danger:active,
.btn-danger:visited {
  color: #df0100 !important;
  background-color: #fff !important;
  border: 2px solid transparent;
  border-color: #df0100 !important;
}
.btn-reset {
  color: #fff;
  background-color: #2269b4;
  /*border-color: #e08e0b;*/
}
.btn-reset:hover,
.btn-reset:active,
.btn-reset:visited,
.btn-reset:focus {
  color: #fff;
  background-color: #0e56a0;
  border-color: #0e56a0;
}
.btn-warning {
  color: #fff;
  background-color: #f39c12;
  border: 2px solid transparent;
  border-color: #e08e0b;
}
.btn-warning:hover,
.btn-warning:active,
.btn-warning:focus,
.btn-warning:visited {
  color: #4589d0 !important;
  background-color: #fff !important;
  border: 2px solid transparent;
  border-color: #4589d0 !important;
}
.btn-warning.disabled {
  color: #fff;
  background-color: #c1c1c1;
  border: 2px solid transparent;
  border-color: #c1c1c1;
}
.btn-warning.disabled:hover,
.btn-warning.disabled:active,
.btn-warning.disabled:focus,
.btn-warning.disabled:visited {
  color: #fff !important;
  background-color: #c1c1c1 !important;
  border: 2px solid transparent;
  border-color: #c1c1c1 !important;
}
.btn-success {
  background-color: #4589d0;
  border: 2px solid transparent;
  border-color: #4589d0;
}
.btn-success:hover,
.btn-success:active,
.btn-success:focus,
.btn-success:visited {
  color: #4589d0;
  background-color: #fff !important;
  border: 2px solid transparent;
  border-color: #4589d0;
}
.btn-search {
  color: #fff;
  background-color: #4589d0 !important;
}
.btn-search:hover,
.btn-search:active,
.btn-search:focus,
.btn-search:visited {
  color: #fff;
  background-color: #357ac2 !important;
}
.btn-plus {
  color: #fff;
  background-color: #4589d0;
}
.btn-plus:hover,
.btn-plus:active,
.btn-plus:focus,
.btn-plus:visited {
  color: #fff;
  background-color: #357ac2 !important;
  border-color: #357ac2 !important;
}
.btn-user {
  color: #fff;
  background-color: #5795d5 !important;
  border: 2px solid transparent;
  border-color: #5795d5 !important;
}
.btn-user:hover,
.btn-user:active,
.btn-user:focus,
.btn-user:visited {
  color: #4589d0;
  background-color: #fff !important;
  border: 2px solid transparent;
  border-color: #4589d0;
}

.btn-outline {
  border: 1px solid #fff;
  background: transparent;
  color: #fff;
}
.btn-outline:hover,
.btn-outline:focus,
.btn-outline:active .btn-role:visited {
  color: rgba(255, 255, 255, 0.7);
  border-color: rgba(255, 255, 255, 0.7);
}
.btn-link {
  -webkit-box-shadow: none;
  box-shadow: none;
}
.btn[class*='bg-']:hover {
  -webkit-box-shadow: inset 0 0 100px rgba(0, 0, 0, 0.2);
  box-shadow: inset 0 0 100px rgba(0, 0, 0, 0.2);
}
.btn-app {
  border-radius: 3px;
  position: relative;
  padding: 15px 5px;
  margin: 0 0 10px 10px;
  min-width: 80px;
  height: 60px;
  text-align: center;
  color: #666;
  border: 1px solid #ddd;
  background-color: #f4f4f4;
  font-size: 12px;
}
.btn-app > .fa,
.btn-app > .glyphicon,
.btn-app > .ion {
  font-size: 20px;
  display: block;
}
.btn-app:hover {
  background: #f4f4f4;
  color: #444;
  border-color: #aaa;
}
.btn-app:active,
.btn-app:focus {
  -webkit-box-shadow: inset 0 3px 5px rgba(0, 0, 0, 0.125);
  -moz-box-shadow: inset 0 3px 5px rgba(0, 0, 0, 0.125);
  box-shadow: inset 0 3px 5px rgba(0, 0, 0, 0.125);
}
.btn-app > .badge {
  position: absolute;
  top: -3px;
  right: -10px;
  font-size: 10px;
  font-weight: 400;
}
/*
 * Component: Callout
 * ------------------
 */
.callout {
  border-radius: 3px;
  margin: 0 0 20px 0;
  padding: 15px 30px 15px 15px;
  border-left: 5px solid #eee;
}
.callout a {
  color: #fff;
  text-decoration: underline;
}
.callout a:hover {
  color: #eee;
}
.callout h4 {
  margin-top: 0;
  font-weight: 600;
}
.callout p:last-child {
  margin-bottom: 0;
}
.callout code,
.callout .highlight {
  background-color: #fff;
}
.callout.callout-danger {
  border-color: #c23321;
}
.callout.callout-warning {
  border-color: #c87f0a;
}
.callout.callout-info {
  border-color: #0097bc;
}
.callout.callout-success {
  border-color: #00733e;
}
/*
 * Component: alert
 * ----------------
 */
.alert {
  border-radius: 3px;
}
.alert h4 {
  font-weight: 600;
}
.alert .icon {
  margin-right: 10px;
}
.alert .close {
  color: #000;
  opacity: 0.2;
  filter: alpha(opacity=20);
}
.alert .close:hover {
  opacity: 0.5;
  filter: alpha(opacity=50);
}
.alert a {
  color: #fff;
  text-decoration: underline;
}
.alert-success {
  border-color: #008d4c;
}
.alert-danger,
.alert-error {
  border-color: #d73925;
}
.alert-warning {
  border-color: #e08e0b;
}
.alert-info {
  border-color: #00acd6;
}
/*
 * Component: Nav
 * --------------
 */
.nav > li > a:hover,
.nav > li > a:active,
.nav > li > a:focus {
  color: #444;
  background: #f7f7f7;
}
/* NAV PILLS */
.nav-pills > li > a {
  border-radius: 0;
  border-top: 3px solid transparent;
  color: #444;
}
.nav-pills > li > a > .fa,
.nav-pills > li > a > .glyphicon,
.nav-pills > li > a > .ion {
  margin-right: 5px;
}
.nav-pills > li.active > a,
.nav-pills > li.active > a:hover,
.nav-pills > li.active > a:focus {
  border-top-color: #3c8dbc;
}
.nav-pills > li.active > a {
  font-weight: 600;
}
/* NAV STACKED */
.nav-stacked > li > a {
  border-radius: 0;
  border-top: 0;
  border-left: 3px solid transparent;
  color: #444;
}
.nav-stacked > li.active > a,
.nav-stacked > li.active > a:hover {
  background: transparent;
  color: #444;
  border-top: 0;
  border-left-color: #3c8dbc;
}
.nav-stacked > li.header {
  border-bottom: 1px solid #ddd;
  color: #777;
  margin-bottom: 10px;
  padding: 5px 10px;
  text-transform: uppercase;
}
/* NAV TABS */
.nav-tabs-custom {
  margin-bottom: 20px;
  background: #fff;
  box-shadow: 0 1px 1px rgba(0, 0, 0, 0.1);
  border-radius: 3px;
}
.nav-tabs-custom > .nav-tabs {
  margin: 0;
  border-bottom-color: #f4f4f4;
  border-top-right-radius: 3px;
  border-top-left-radius: 3px;
}
.nav-tabs-custom > .nav-tabs > li {
  border-top: 3px solid transparent;
  margin-bottom: -2px;
  margin-right: 5px;
}
.nav-tabs-custom > .nav-tabs > li > a {
  color: #444;
  border-radius: 0;
}
.nav-tabs-custom > .nav-tabs > li > a.text-muted {
  color: #999;
}
.nav-tabs-custom > .nav-tabs > li > a,
.nav-tabs-custom > .nav-tabs > li > a:hover {
  background: transparent;
  margin: 0;
}
.nav-tabs-custom > .nav-tabs > li > a:hover {
  color: #999;
}
.nav-tabs-custom > .nav-tabs > li:not(.active) > a:hover,
.nav-tabs-custom > .nav-tabs > li:not(.active) > a:focus,
.nav-tabs-custom > .nav-tabs > li:not(.active) > a:active {
  border-color: transparent;
}
.nav-tabs-custom > .nav-tabs > li.active {
  border-top-color: #3c8dbc;
}
.nav-tabs-custom > .nav-tabs > li.active > a,
.nav-tabs-custom > .nav-tabs > li.active:hover > a {
  background-color: #fff;
  color: #444;
}
.nav-tabs-custom > .nav-tabs > li.active > a {
  border-top-color: transparent;
  border-left-color: #f4f4f4;
  border-right-color: #f4f4f4;
}
.nav-tabs-custom > .nav-tabs > li:first-of-type {
  margin-left: 0;
}
.nav-tabs-custom > .nav-tabs > li:first-of-type.active > a {
  border-left-color: transparent;
}
.nav-tabs-custom > .nav-tabs.pull-right {
  float: none !important;
}
.nav-tabs-custom > .nav-tabs.pull-right > li {
  float: right;
}
.nav-tabs-custom > .nav-tabs.pull-right > li:first-of-type {
  margin-right: 0;
}
.nav-tabs-custom > .nav-tabs.pull-right > li:first-of-type > a {
  border-left-width: 1px;
}
.nav-tabs-custom > .nav-tabs.pull-right > li:first-of-type.active > a {
  border-left-color: #f4f4f4;
  border-right-color: transparent;
}
.nav-tabs-custom > .nav-tabs > li.header {
  line-height: 35px;
  padding: 0 10px;
  font-size: 20px;
  color: #444;
}
.nav-tabs-custom > .nav-tabs > li.header > .fa,
.nav-tabs-custom > .nav-tabs > li.header > .glyphicon,
.nav-tabs-custom > .nav-tabs > li.header > .ion {
  margin-right: 5px;
}
.nav-tabs-custom > .tab-content {
  background: #fff;
  padding: 10px;
  border-bottom-right-radius: 3px;
  border-bottom-left-radius: 3px;
}
.nav-tabs-custom .dropdown.open > a:active,
.nav-tabs-custom .dropdown.open > a:focus {
  background: transparent;
  color: #999;
}
/* PAGINATION */
.pagination > .active > a,
.pagination > .active > span,
.pagination > .active > a:hover,
.pagination > .active > span:hover,
.pagination > .active > a:focus,
.pagination > .active > span:focus {
  z-index: 2;
  color: #777777;
  cursor: default;
  background-color: #d2d6de;
  border-color: #d2d6de;
}
.pagination > .disabled > a,
.pagination > .disabled > a:hover,
.pagination > .disabled > a:focus {
  color: #777;
  cursor: not-allowed;
  background-color: #f9f9f9;
  border-color: #ddd;
}
.pagination > li > a {
  background: #fafafa;
  color: #666;
}
.pagination.pagination-flat > li > a {
  border-radius: 0 !important;
}
/*
 * Component: Products List
 * ------------------------
 */
.products-list {
  list-style: none;
  margin: 0;
  padding: 0;
}
.products-list > .item {
  border-radius: 3px;
  -webkit-box-shadow: 0 1px 1px rgba(0, 0, 0, 0.1);
  box-shadow: 0 1px 1px rgba(0, 0, 0, 0.1);
  padding: 10px 0;
  background: #fff;
}
.products-list > .item:before,
.products-list > .item:after {
  content: ' ';
  display: table;
}
.products-list > .item:after {
  clear: both;
}
.products-list .product-img {
  float: left;
}
.products-list .product-img img {
  width: 50px;
  height: 50px;
}
.products-list .product-info {
  margin-left: 60px;
}
.products-list .product-title {
  font-weight: 600;
}
.products-list .product-description {
  display: block;
  color: #999;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}
.product-list-in-box > .item {
  -webkit-box-shadow: none;
  box-shadow: none;
  border-radius: 0;
  border-bottom: 1px solid #f4f4f4;
}
.product-list-in-box > .item:last-of-type {
  border-bottom-width: 0;
}
/*
 * Component: Table
 * ----------------
 */
.table > thead > tr > th,
.table > tbody > tr > th,
.table > tfoot > tr > th,
.table > thead > tr > td,
.table > tbody > tr > td,
.table > tfoot > tr > td {
  border-top: 1px solid #f4f4f4;
}
.table > thead > tr > th {
  border-bottom: 2px solid #f4f4f4;
}
.table tr td .progress {
  margin-top: 5px;
}
.table-bordered {
  border: 1px solid #f4f4f4;
}
.table-bordered > thead > tr > th,
.table-bordered > tbody > tr > th,
.table-bordered > tfoot > tr > th,
.table-bordered > thead > tr > td,
.table-bordered > tbody > tr > td,
.table-bordered > tfoot > tr > td {
  border: 1px solid #f4f4f4;
}
.table-bordered > thead > tr > th,
.table-bordered > thead > tr > td {
  border-bottom-width: 2px;
}
.table.no-border,
.table.no-border td,
.table.no-border th {
  border: 0;
}
/* .text-center in tables */
table.text-center,
table.text-center td,
table.text-center th {
  text-align: center;
}
.table.align th {
  text-align: left;
}
.table.align td {
  text-align: right;
}
/*
 * Component: Label
 * ----------------
 */
.label {
  display: inline;
  padding: 0.3em 0.6em 0.3em;
  font-size: 80%;
  font-weight: bold;
  line-height: 2;
  color: #fff;
  text-align: center;
  white-space: nowrap;
  vertical-align: baseline;
  border-radius: 6em;
}
.label-default {
  background-color: #96979a;
  color: #fff;
}
/*
 * Component: Direct Chat
 * ----------------------
 */
.direct-chat .box-body {
  border-bottom-right-radius: 0;
  border-bottom-left-radius: 0;
  position: relative;
  overflow-x: hidden;
  padding: 0;
}
.direct-chat.chat-pane-open .direct-chat-contacts {
  -webkit-transform: translate(0, 0);
  -ms-transform: translate(0, 0);
  -o-transform: translate(0, 0);
  transform: translate(0, 0);
}
.direct-chat-messages {
  -webkit-transform: translate(0, 0);
  -ms-transform: translate(0, 0);
  -o-transform: translate(0, 0);
  transform: translate(0, 0);
  padding: 10px;
  height: 250px;
  overflow: auto;
}
.direct-chat-msg,
.direct-chat-text {
  display: block;
}
.direct-chat-msg {
  margin-bottom: 10px;
}
.direct-chat-msg:before,
.direct-chat-msg:after {
  content: ' ';
  display: table;
}
.direct-chat-msg:after {
  clear: both;
}
.direct-chat-messages,
.direct-chat-contacts {
  -webkit-transition: -webkit-transform 0.5s ease-in-out;
  -moz-transition: -moz-transform 0.5s ease-in-out;
  -o-transition: -o-transform 0.5s ease-in-out;
  transition: transform 0.5s ease-in-out;
}
.direct-chat-text {
  border-radius: 5px;
  position: relative;
  padding: 5px 10px;
  background: #d2d6de;
  border: 1px solid #d2d6de;
  margin: 5px 0 0 50px;
  color: #444444;
}
.direct-chat-text:after,
.direct-chat-text:before {
  position: absolute;
  right: 100%;
  top: 15px;
  border: solid transparent;
  border-right-color: #d2d6de;
  content: ' ';
  height: 0;
  width: 0;
  pointer-events: none;
}
.direct-chat-text:after {
  border-width: 5px;
  margin-top: -5px;
}
.direct-chat-text:before {
  border-width: 6px;
  margin-top: -6px;
}
.right .direct-chat-text {
  margin-right: 50px;
  margin-left: 0;
}
.right .direct-chat-text:after,
.right .direct-chat-text:before {
  right: auto;
  left: 100%;
  border-right-color: transparent;
  border-left-color: #d2d6de;
}
.direct-chat-img {
  border-radius: 50%;
  float: left;
  width: 40px;
  height: 40px;
}
.right .direct-chat-img {
  float: right;
}
.direct-chat-info {
  display: block;
  margin-bottom: 2px;
  font-size: 12px;
}
.direct-chat-name {
  font-weight: 600;
}
.direct-chat-timestamp {
  color: #999;
}
.direct-chat-contacts-open .direct-chat-contacts {
  -webkit-transform: translate(0, 0);
  -ms-transform: translate(0, 0);
  -o-transform: translate(0, 0);
  transform: translate(0, 0);
}
.direct-chat-contacts {
  -webkit-transform: translate(101%, 0);
  -ms-transform: translate(101%, 0);
  -o-transform: translate(101%, 0);
  transform: translate(101%, 0);
  position: absolute;
  top: 0;
  bottom: 0;
  height: 250px;
  width: 100%;
  background: #222d32;
  color: #fff;
  overflow: auto;
}
.contacts-list > li {
  border-bottom: 1px solid rgba(0, 0, 0, 0.2);
  padding: 10px;
  margin: 0;
}
.contacts-list > li:before,
.contacts-list > li:after {
  content: ' ';
  display: table;
}
.contacts-list > li:after {
  clear: both;
}
.contacts-list > li:last-of-type {
  border-bottom: none;
}
.contacts-list-img {
  border-radius: 50%;
  width: 40px;
  float: left;
}
.contacts-list-info {
  margin-left: 45px;
  color: #fff;
}
.contacts-list-name,
.contacts-list-status {
  display: block;
}
.contacts-list-name {
  font-weight: 600;
}
.contacts-list-status {
  font-size: 12px;
}
.contacts-list-date {
  color: #aaa;
  font-weight: normal;
}
.contacts-list-msg {
  color: #999;
}
.direct-chat-danger .right > .direct-chat-text {
  background: #dd4b39;
  border-color: #dd4b39;
  color: #ffffff;
}
.direct-chat-danger .right > .direct-chat-text:after,
.direct-chat-danger .right > .direct-chat-text:before {
  border-left-color: #dd4b39;
}
.direct-chat-primary .right > .direct-chat-text {
  background: #3c8dbc;
  border-color: #3c8dbc;
  color: #ffffff;
}
.direct-chat-primary .right > .direct-chat-text:after,
.direct-chat-primary .right > .direct-chat-text:before {
  border-left-color: #3c8dbc;
}
.direct-chat-warning .right > .direct-chat-text {
  background: #f39c12;
  border-color: #f39c12;
  color: #ffffff;
}
.direct-chat-warning .right > .direct-chat-text:after,
.direct-chat-warning .right > .direct-chat-text:before {
  border-left-color: #f39c12;
}
.direct-chat-info .right > .direct-chat-text {
  background: #00c0ef;
  border-color: #00c0ef;
  color: #ffffff;
}
.direct-chat-info .right > .direct-chat-text:after,
.direct-chat-info .right > .direct-chat-text:before {
  border-left-color: #00c0ef;
}
.direct-chat-success .right > .direct-chat-text {
  background: #00a65a;
  border-color: #00a65a;
  color: #ffffff;
}
.direct-chat-success .right > .direct-chat-text:after,
.direct-chat-success .right > .direct-chat-text:before {
  border-left-color: #00a65a;
}
/*
 * Component: Users List
 * ---------------------
 */
.users-list > li {
  width: 25%;
  float: left;
  padding: 10px;
  text-align: center;
}
.users-list > li img {
  border-radius: 50%;
  max-width: 100%;
  height: auto;
}
.users-list > li > a:hover,
.users-list > li > a:hover .users-list-name {
  color: #999;
}
.users-list-name,
.users-list-date {
  display: block;
}
.users-list-name {
  font-weight: 600;
  color: #444;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}
.users-list-date {
  color: #999;
  font-size: 12px;
}
/*
 * Component: Carousel
 * -------------------
 */
.carousel-control.left,
.carousel-control.right {
  background-image: none;
}
.carousel-control > .fa {
  font-size: 40px;
  position: absolute;
  top: 50%;
  z-index: 5;
  display: inline-block;
  margin-top: -20px;
}
/*
 * Component: modal
 * ----------------
 */
.modal {
  background: rgba(0, 0, 0, 0.3);
}
.close {
  font-size: 24px;
}
h4 {
  font-size: 3rem;
  font-weight: 600;
  text-align: left;
  color: #505050;
  /*border-bottom: 2px solid #35383c;
    padding-bottom: .2em;
    line-height: 1.8;*/
}
.modal-content {
  background: #fff;
  border: 1px solid rgba(0, 0, 0, 0.2);
  border-radius: 6px;
  outline: 0;
  max-width: 100%;
  -webkit-box-shadow: 0 2px 3px rgba(0, 0, 0, 0.125);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.5);
}
@media (min-width: 768px) {
  .modal-content {
    -webkit-box-shadow: 0 2px 3px rgba(0, 0, 0, 0.125);
    box-shadow: 0 5px 20px rgba(0, 0, 0, 0.5);
  }
}
.modal-header {
  border-bottom-color: #f4f4f4;
  padding: 3px !important;
  height: 70px;
  background-color: #eff0f1 !important;
  text-align: center !important;
}
.modal-header h2 {
  text-align: center !important;
  color: #666666;
}
.modal-footer {
  /*border-top-color: #f4f4f4;*/
  /* border-top: 1px solid white; */
  background: #fff;
  border-radius: 2px;
  /* background-image: url(http://subtlepatterns2015.subtlepatterns.netdna-cdn.com/patterns/subtlenet2.png); */
}
.modal-primary .modal-header,
.modal-primary .modal-footer {
  border-color: #307095;
}
.modal-warning .modal-header,
.modal-warning .modal-footer {
  border-color: #c87f0a;
}
.modal-info .modal-header,
.modal-info .modal-footer {
  border-color: #0097bc;
}
.modal-success .modal-header,
.modal-success .modal-footer {
  border-color: #00733e;
}
.modal-danger .modal-header,
.modal-danger .modal-footer {
  border-color: #c23321;
}
/*
 * Component: Social Widgets
 * -------------------------
 */
.box-widget {
  border: none;
  position: relative;
}
.widget-user .widget-user-header {
  padding: 20px;
  height: 120px;
  border-top-right-radius: 3px;
  border-top-left-radius: 3px;
}
.widget-user .widget-user-username {
  margin-top: 0;
  margin-bottom: 5px;
  font-size: 25px;
  font-weight: 300;
  text-shadow: 0 1px 1px rgba(0, 0, 0, 0.2);
}
.widget-user .widget-user-desc {
  margin-top: 0;
}
.widget-user .widget-user-image {
  position: absolute;
  top: 65px;
  left: 50%;
  margin-left: -45px;
}
.widget-user .widget-user-image > img {
  width: 90px;
  height: auto;
  border: 3px solid #fff;
}
.widget-user .box-footer {
  padding-top: 30px;
}
.widget-user-2 .widget-user-header {
  padding: 20px;
  border-top-right-radius: 3px;
  border-top-left-radius: 3px;
}
.widget-user-2 .widget-user-username {
  margin-top: 5px;
  margin-bottom: 5px;
  font-size: 25px;
  font-weight: 300;
}
.widget-user-2 .widget-user-desc {
  margin-top: 0;
}
.widget-user-2 .widget-user-username,
.widget-user-2 .widget-user-desc {
  margin-left: 75px;
}
.widget-user-2 .widget-user-image > img {
  width: 65px;
  height: auto;
  float: left;
}
/*
 * Page: Mailbox
 * -------------
 */
.mailbox-messages > .table {
  margin: 0;
}
.mailbox-controls {
  padding: 5px;
}
.mailbox-controls.with-border {
  border-bottom: 1px solid #f4f4f4;
}
.mailbox-read-info {
  border-bottom: 1px solid #f4f4f4;
  padding: 10px;
}
.mailbox-read-info h3 {
  font-size: 20px;
  margin: 0;
}
.mailbox-read-info h5 {
  margin: 0;
  padding: 5px 0 0 0;
}
.mailbox-read-time {
  color: #999;
  font-size: 13px;
}
.mailbox-read-message {
  padding: 10px;
}
.mailbox-attachments li {
  float: left;
  width: 200px;
  border: 1px solid #eee;
  margin-bottom: 10px;
  margin-right: 10px;
}
.mailbox-attachment-name {
  font-weight: bold;
  color: #666;
}
.mailbox-attachment-icon,
.mailbox-attachment-info,
.mailbox-attachment-size {
  display: block;
}
.mailbox-attachment-info {
  padding: 10px;
  background: #f4f4f4;
}
.mailbox-attachment-size {
  color: #999;
  font-size: 12px;
}
.mailbox-attachment-icon {
  text-align: center;
  font-size: 65px;
  color: #666;
  padding: 20px 10px;
}
.mailbox-attachment-icon.has-img {
  padding: 0;
}
.mailbox-attachment-icon.has-img > img {
  max-width: 100%;
  height: auto;
}
/*
 * Page: Lock Screen
 * -----------------
 */
/* ADD THIS CLASS TO THE <BODY> TAG */
.lockscreen {
  background: #d2d6de;
}
.lockscreen-logo {
  font-size: 35px;
  text-align: center;
  margin-bottom: 25px;
  font-weight: 300;
}
.lockscreen-logo a {
  color: #444;
}
.lockscreen-wrapper {
  max-width: 400px;
  margin: 0 auto;
  margin-top: 10%;
}
/* User name [optional] */
.lockscreen .lockscreen-name {
  text-align: center;
  font-weight: 600;
}
/* Will contain the image and the sign in form */
.lockscreen-item {
  border-radius: 4px;
  padding: 0;
  background: #fff;
  position: relative;
  margin: 10px auto 30px auto;
  width: 290px;
}
/* User image */
.lockscreen-image {
  border-radius: 50%;
  position: absolute;
  left: -10px;
  top: -25px;
  background: #fff;
  padding: 5px;
  z-index: 10;
}
.lockscreen-image > img {
  border-radius: 50%;
  width: 70px;
  height: 70px;
}
/* Contains the password input and the login button */
.lockscreen-credentials {
  margin-left: 70px;
}
.lockscreen-credentials .form-control {
  border: 0;
}
.lockscreen-credentials .btn {
  background-color: #fff;
  border: 0;
  padding: 0 10px;
}
.lockscreen-footer {
  margin-top: 10px;
}
/*
 * Page: Login & Register
 * ----------------------
 */
.login-logo,
.register-logo {
  font-size: 35px;
  text-align: center;
  margin-bottom: 25px;
  font-weight: 300;
}
.login-logo a,
.register-logo a {
  color: #444;
}
.login-page,
.register-page {
  background: #f6f7fb;
  /* background: url(/images/bg2.jpg) no-repeat center; */
  width: 100%;
  height: 100%;
  left: 0;
  background-size: cover;
}
.login-box,
.register-box {
  /*width: 33%;*/
  margin: 7% auto;
}
@media (max-width: 768px) {
  .login-box,
  .register-box {
    width: 90%;
    margin-top: 20px;
  }
}
.login-box-body,
.register-box-body {
  background: url(/img/login_box_area.png) no-repeat;
  width: 831px;
  height: 495px;
  margin: 0 auto;
  display: block;
  padding: 40px 12%;
  color: #222;
  /*border:10px solid rgba(0, 94, 163, .7);
  border-radius:20px;*/
}
.login-box-body .titleDiv,
.register-box-body .titleDiv {
  color: #6d6d6b;
  text-align: center;
  padding: 5% 0 8%;
}
.login-box-body .titleDiv h4,
.register-box-body .titleDiv h4 {
  color: #6d6d6b;
  font-family: 'Nanum Gothic', '�굹�닎怨좊뵓';
  padding-top: 10px;
}
.login-box-body .titleDiv span,
.register-box-body .titleDiv span {
  color: #80dec9;
}

.btn-systemLogin {
  background: #80dec9;
  border-radius: 4px;
  height: 57px;
  color: #fff;
  border: 1px solid #8fcdbf;
}
.btn-systemLogin:hover {
  color: #fff;
  background: #80dec9;
}

.login-box-body .form-control-feedback,
.register-box-body .form-control-feedback {
  color: #777;
}
.login-box-msg,
.register-box-msg {
  margin: 0;
  text-align: center;
  padding: 0 20px 20px 20px;
}
.social-auth-links {
  margin: 10px 0;
}
/*
 * Page: 400 and 500 error pages
 * ------------------------------
 */
.error-page {
  width: 600px;
  margin: 20px auto 0 auto;
}
@media (max-width: 991px) {
  .error-page {
    width: 100%;
  }
}
.error-page > .headline {
  float: left;
  font-size: 100px;
  font-weight: 300;
}
@media (max-width: 991px) {
  .error-page > .headline {
    float: none;
    text-align: center;
  }
}
.error-page > .error-content {
  margin-left: 190px;
  display: block;
}
@media (max-width: 991px) {
  .error-page > .error-content {
    margin-left: 0;
  }
}
.error-page > .error-content > h3 {
  font-weight: 300;
  font-size: 25px;
}
@media (max-width: 991px) {
  .error-page > .error-content > h3 {
    text-align: center;
  }
}
/*
 * Page: Invoice
 * -------------
 */
.invoice {
  position: relative;
  background: #fff;
  border: 1px solid #f4f4f4;
  padding: 20px;
  margin: 10px 25px;
}
.invoice-title {
  margin-top: 0;
}
/*
 * Page: Profile
 * -------------
 */
.profile-user-img {
  margin: 0 auto;
  width: 100px;
  padding: 3px;
  border: 3px solid #d2d6de;
}
.profile-username {
  font-size: 21px;
  margin-top: 5px;
}
.post {
  border-bottom: 1px solid #d2d6de;
  margin-bottom: 15px;
  padding-bottom: 15px;
  color: #666;
}
.post:last-of-type {
  border-bottom: 0;
  margin-bottom: 0;
  padding-bottom: 0;
}
.post .user-block {
  margin-bottom: 15px;
}
/*
 * Social Buttons for Bootstrap
 *
 * Copyright 2013-2015 Panayiotis Lipiridis
 * Licensed under the MIT License
 *
 * https://github.com/lipis/bootstrap-social
 */
.btn-social {
  position: relative;
  padding-left: 44px;
  text-align: left;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.btn-social > :first-child {
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 32px;
  line-height: 34px;
  font-size: 1.6em;
  text-align: center;
  border-right: 1px solid rgba(0, 0, 0, 0.2);
}
.btn-social.btn-lg {
  padding-left: 61px;
}
.btn-social.btn-lg > :first-child {
  line-height: 45px;
  width: 45px;
  font-size: 1.8em;
}
.btn-social.btn-sm {
  padding-left: 38px;
}
.btn-social.btn-sm > :first-child {
  line-height: 28px;
  width: 28px;
  font-size: 1.4em;
}
.btn-social.btn-xs {
  padding-left: 30px;
}
.btn-social.btn-xs > :first-child {
  line-height: 20px;
  width: 20px;
  font-size: 1.2em;
}
.btn-social-icon {
  position: relative;
  padding-left: 44px;
  text-align: left;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  height: 34px;
  width: 34px;
  padding: 0;
}
.btn-social-icon > :first-child {
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 32px;
  line-height: 34px;
  font-size: 1.6em;
  text-align: center;
  border-right: 1px solid rgba(0, 0, 0, 0.2);
}
.btn-social-icon.btn-lg {
  padding-left: 61px;
}
.btn-social-icon.btn-lg > :first-child {
  line-height: 45px;
  width: 45px;
  font-size: 1.8em;
}
.btn-social-icon.btn-sm {
  padding-left: 38px;
}
.btn-social-icon.btn-sm > :first-child {
  line-height: 28px;
  width: 28px;
  font-size: 1.4em;
}
.btn-social-icon.btn-xs {
  padding-left: 30px;
}
.btn-social-icon.btn-xs > :first-child {
  line-height: 20px;
  width: 20px;
  font-size: 1.2em;
}
.btn-social-icon > :first-child {
  border: none;
  text-align: center;
  width: 100%;
}
.btn-social-icon.btn-lg {
  height: 45px;
  width: 45px;
  padding-left: 0;
  padding-right: 0;
}
.btn-social-icon.btn-sm {
  height: 30px;
  width: 30px;
  padding-left: 0;
  padding-right: 0;
}
.btn-social-icon.btn-xs {
  height: 22px;
  width: 22px;
  padding-left: 0;
  padding-right: 0;
}
.btn-adn {
  color: #ffffff;
  background-color: #d87a68;
  border-color: rgba(0, 0, 0, 0.2);
}
.btn-adn:hover,
.btn-adn:focus,
.btn-adn.focus,
.btn-adn:active,
.btn-adn.active,
.open > .dropdown-toggle.btn-adn {
  color: #ffffff;
  background-color: #ce563f;
  border-color: rgba(0, 0, 0, 0.2);
}
.btn-adn:active,
.btn-adn.active,
.open > .dropdown-toggle.btn-adn {
  background-image: none;
}
.btn-adn .badge {
  color: #d87a68;
  background-color: #ffffff;
}
.btn-bitbucket {
  color: #ffffff;
  background-color: #205081;
  border-color: rgba(0, 0, 0, 0.2);
}
.btn-bitbucket:hover,
.btn-bitbucket:focus,
.btn-bitbucket.focus,
.btn-bitbucket:active,
.btn-bitbucket.active,
.open > .dropdown-toggle.btn-bitbucket {
  color: #ffffff;
  background-color: #163758;
  border-color: rgba(0, 0, 0, 0.2);
}
.btn-bitbucket:active,
.btn-bitbucket.active,
.open > .dropdown-toggle.btn-bitbucket {
  background-image: none;
}
.btn-bitbucket .badge {
  color: #205081;
  background-color: #ffffff;
}
.btn-dropbox {
  color: #ffffff;
  background-color: #1087dd;
  border-color: rgba(0, 0, 0, 0.2);
}
.btn-dropbox:hover,
.btn-dropbox:focus,
.btn-dropbox.focus,
.btn-dropbox:active,
.btn-dropbox.active,
.open > .dropdown-toggle.btn-dropbox {
  color: #ffffff;
  background-color: #0d6aad;
  border-color: rgba(0, 0, 0, 0.2);
}
.btn-dropbox:active,
.btn-dropbox.active,
.open > .dropdown-toggle.btn-dropbox {
  background-image: none;
}
.btn-dropbox .badge {
  color: #1087dd;
  background-color: #ffffff;
}
.btn-facebook {
  color: #ffffff;
  background-color: #3b5998;
  border-color: rgba(0, 0, 0, 0.2);
}
.btn-facebook:hover,
.btn-facebook:focus,
.btn-facebook.focus,
.btn-facebook:active,
.btn-facebook.active,
.open > .dropdown-toggle.btn-facebook {
  color: #ffffff;
  background-color: #2d4373;
  border-color: rgba(0, 0, 0, 0.2);
}
.btn-facebook:active,
.btn-facebook.active,
.open > .dropdown-toggle.btn-facebook {
  background-image: none;
}
.btn-facebook .badge {
  color: #3b5998;
  background-color: #ffffff;
}
.btn-flickr {
  color: #ffffff;
  background-color: #ff0084;
  border-color: rgba(0, 0, 0, 0.2);
}
.btn-flickr:hover,
.btn-flickr:focus,
.btn-flickr.focus,
.btn-flickr:active,
.btn-flickr.active,
.open > .dropdown-toggle.btn-flickr {
  color: #ffffff;
  background-color: #cc006a;
  border-color: rgba(0, 0, 0, 0.2);
}
.btn-flickr:active,
.btn-flickr.active,
.open > .dropdown-toggle.btn-flickr {
  background-image: none;
}
.btn-flickr .badge {
  color: #ff0084;
  background-color: #ffffff;
}
.btn-foursquare {
  color: #ffffff;
  background-color: #f94877;
  border-color: rgba(0, 0, 0, 0.2);
}
.btn-foursquare:hover,
.btn-foursquare:focus,
.btn-foursquare.focus,
.btn-foursquare:active,
.btn-foursquare.active,
.open > .dropdown-toggle.btn-foursquare {
  color: #ffffff;
  background-color: #f71752;
  border-color: rgba(0, 0, 0, 0.2);
}
.btn-foursquare:active,
.btn-foursquare.active,
.open > .dropdown-toggle.btn-foursquare {
  background-image: none;
}
.btn-foursquare .badge {
  color: #f94877;
  background-color: #ffffff;
}
.btn-github {
  color: #ffffff;
  background-color: #444444;
  border-color: rgba(0, 0, 0, 0.2);
}
.btn-github:hover,
.btn-github:focus,
.btn-github.focus,
.btn-github:active,
.btn-github.active,
.open > .dropdown-toggle.btn-github {
  color: #ffffff;
  background-color: #2b2b2b;
  border-color: rgba(0, 0, 0, 0.2);
}
.btn-github:active,
.btn-github.active,
.open > .dropdown-toggle.btn-github {
  background-image: none;
}
.btn-github .badge {
  color: #444444;
  background-color: #ffffff;
}
.btn-google {
  color: #ffffff;
  background-color: #dd4b39;
  border-color: rgba(0, 0, 0, 0.2);
}
.btn-google:hover,
.btn-google:focus,
.btn-google.focus,
.btn-google:active,
.btn-google.active,
.open > .dropdown-toggle.btn-google {
  color: #ffffff;
  background-color: #c23321;
  border-color: rgba(0, 0, 0, 0.2);
}
.btn-google:active,
.btn-google.active,
.open > .dropdown-toggle.btn-google {
  background-image: none;
}
.btn-google .badge {
  color: #dd4b39;
  background-color: #ffffff;
}
.btn-instagram {
  color: #ffffff;
  background-color: #3f729b;
  border-color: rgba(0, 0, 0, 0.2);
}
.btn-instagram:hover,
.btn-instagram:focus,
.btn-instagram.focus,
.btn-instagram:active,
.btn-instagram.active,
.open > .dropdown-toggle.btn-instagram {
  color: #ffffff;
  background-color: #305777;
  border-color: rgba(0, 0, 0, 0.2);
}
.btn-instagram:active,
.btn-instagram.active,
.open > .dropdown-toggle.btn-instagram {
  background-image: none;
}
.btn-instagram .badge {
  color: #3f729b;
  background-color: #ffffff;
}
.btn-linkedin {
  color: #ffffff;
  background-color: #007bb6;
  border-color: rgba(0, 0, 0, 0.2);
}
.btn-linkedin:hover,
.btn-linkedin:focus,
.btn-linkedin.focus,
.btn-linkedin:active,
.btn-linkedin.active,
.open > .dropdown-toggle.btn-linkedin {
  color: #ffffff;
  background-color: #005983;
  border-color: rgba(0, 0, 0, 0.2);
}
.btn-linkedin:active,
.btn-linkedin.active,
.open > .dropdown-toggle.btn-linkedin {
  background-image: none;
}
.btn-linkedin .badge {
  color: #007bb6;
  background-color: #ffffff;
}
.btn-microsoft {
  color: #ffffff;
  background-color: #2672ec;
  border-color: rgba(0, 0, 0, 0.2);
}
.btn-microsoft:hover,
.btn-microsoft:focus,
.btn-microsoft.focus,
.btn-microsoft:active,
.btn-microsoft.active,
.open > .dropdown-toggle.btn-microsoft {
  color: #ffffff;
  background-color: #125acd;
  border-color: rgba(0, 0, 0, 0.2);
}
.btn-microsoft:active,
.btn-microsoft.active,
.open > .dropdown-toggle.btn-microsoft {
  background-image: none;
}
.btn-microsoft .badge {
  color: #2672ec;
  background-color: #ffffff;
}
.btn-openid {
  color: #ffffff;
  background-color: #f7931e;
  border-color: rgba(0, 0, 0, 0.2);
}
.btn-openid:hover,
.btn-openid:focus,
.btn-openid.focus,
.btn-openid:active,
.btn-openid.active,
.open > .dropdown-toggle.btn-openid {
  color: #ffffff;
  background-color: #da7908;
  border-color: rgba(0, 0, 0, 0.2);
}
.btn-openid:active,
.btn-openid.active,
.open > .dropdown-toggle.btn-openid {
  background-image: none;
}
.btn-openid .badge {
  color: #f7931e;
  background-color: #ffffff;
}
.btn-pinterest {
  color: #ffffff;
  background-color: #cb2027;
  border-color: rgba(0, 0, 0, 0.2);
}
.btn-pinterest:hover,
.btn-pinterest:focus,
.btn-pinterest.focus,
.btn-pinterest:active,
.btn-pinterest.active,
.open > .dropdown-toggle.btn-pinterest {
  color: #ffffff;
  background-color: #9f191f;
  border-color: rgba(0, 0, 0, 0.2);
}
.btn-pinterest:active,
.btn-pinterest.active,
.open > .dropdown-toggle.btn-pinterest {
  background-image: none;
}
.btn-pinterest .badge {
  color: #cb2027;
  background-color: #ffffff;
}
.btn-reddit {
  color: #000000;
  background-color: #eff7ff;
  border-color: rgba(0, 0, 0, 0.2);
}
.btn-reddit:hover,
.btn-reddit:focus,
.btn-reddit.focus,
.btn-reddit:active,
.btn-reddit.active,
.open > .dropdown-toggle.btn-reddit {
  color: #000000;
  background-color: #bcddff;
  border-color: rgba(0, 0, 0, 0.2);
}
.btn-reddit:active,
.btn-reddit.active,
.open > .dropdown-toggle.btn-reddit {
  background-image: none;
}
.btn-reddit .badge {
  color: #eff7ff;
  background-color: #000000;
}
.btn-soundcloud {
  color: #ffffff;
  background-color: #ff5500;
  border-color: rgba(0, 0, 0, 0.2);
}
.btn-soundcloud:hover,
.btn-soundcloud:focus,
.btn-soundcloud.focus,
.btn-soundcloud:active,
.btn-soundcloud.active,
.open > .dropdown-toggle.btn-soundcloud {
  color: #ffffff;
  background-color: #cc4400;
  border-color: rgba(0, 0, 0, 0.2);
}
.btn-soundcloud:active,
.btn-soundcloud.active,
.open > .dropdown-toggle.btn-soundcloud {
  background-image: none;
}
.btn-soundcloud .badge {
  color: #ff5500;
  background-color: #ffffff;
}
.btn-tumblr {
  color: #ffffff;
  background-color: #2c4762;
  border-color: rgba(0, 0, 0, 0.2);
}
.btn-tumblr:hover,
.btn-tumblr:focus,
.btn-tumblr.focus,
.btn-tumblr:active,
.btn-tumblr.active,
.open > .dropdown-toggle.btn-tumblr {
  color: #ffffff;
  background-color: #1c2d3f;
  border-color: rgba(0, 0, 0, 0.2);
}
.btn-tumblr:active,
.btn-tumblr.active,
.open > .dropdown-toggle.btn-tumblr {
  background-image: none;
}
.btn-tumblr .badge {
  color: #2c4762;
  background-color: #ffffff;
}
.btn-twitter {
  color: #ffffff;
  background-color: #55acee;
  border-color: rgba(0, 0, 0, 0.2);
}
.btn-twitter:hover,
.btn-twitter:focus,
.btn-twitter.focus,
.btn-twitter:active,
.btn-twitter.active,
.open > .dropdown-toggle.btn-twitter {
  color: #ffffff;
  background-color: #2795e9;
  border-color: rgba(0, 0, 0, 0.2);
}
.btn-twitter:active,
.btn-twitter.active,
.open > .dropdown-toggle.btn-twitter {
  background-image: none;
}
.btn-twitter .badge {
  color: #55acee;
  background-color: #ffffff;
}
.btn-vimeo {
  color: #ffffff;
  background-color: #1ab7ea;
  border-color: rgba(0, 0, 0, 0.2);
}
.btn-vimeo:hover,
.btn-vimeo:focus,
.btn-vimeo.focus,
.btn-vimeo:active,
.btn-vimeo.active,
.open > .dropdown-toggle.btn-vimeo {
  color: #ffffff;
  background-color: #1295bf;
  border-color: rgba(0, 0, 0, 0.2);
}
.btn-vimeo:active,
.btn-vimeo.active,
.open > .dropdown-toggle.btn-vimeo {
  background-image: none;
}
.btn-vimeo .badge {
  color: #1ab7ea;
  background-color: #ffffff;
}
.btn-vk {
  color: #ffffff;
  background-color: #587ea3;
  border-color: rgba(0, 0, 0, 0.2);
}
.btn-vk:hover,
.btn-vk:focus,
.btn-vk.focus,
.btn-vk:active,
.btn-vk.active,
.open > .dropdown-toggle.btn-vk {
  color: #ffffff;
  background-color: #466482;
  border-color: rgba(0, 0, 0, 0.2);
}
.btn-vk:active,
.btn-vk.active,
.open > .dropdown-toggle.btn-vk {
  background-image: none;
}
.btn-vk .badge {
  color: #587ea3;
  background-color: #ffffff;
}
.btn-yahoo {
  color: #ffffff;
  background-color: #720e9e;
  border-color: rgba(0, 0, 0, 0.2);
}
.btn-yahoo:hover,
.btn-yahoo:focus,
.btn-yahoo.focus,
.btn-yahoo:active,
.btn-yahoo.active,
.open > .dropdown-toggle.btn-yahoo {
  color: #ffffff;
  background-color: #500a6f;
  border-color: rgba(0, 0, 0, 0.2);
}
.btn-yahoo:active,
.btn-yahoo.active,
.open > .dropdown-toggle.btn-yahoo {
  background-image: none;
}
.btn-yahoo .badge {
  color: #720e9e;
  background-color: #ffffff;
}
/*
 * Insert source
 * ---------------------
 */

.fa-chevron-right {
  display: inline-block;
  font: normal normal normal 10px/1 FontAwesome !important;
  color: #4589d0;
  font-size: inherit;
  text-rendering: auto;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}
div.dataTables_info {
  padding-top: 10px !important;
  white-space: nowrap;
  text-align: left;
}
table.dataTable {
  width: 100%;
}
.text-like {
  color: #f39c12;
}
.datepicker {
  padding: 4px;
  border-radius: 0px !important;
  direction: ltr;
}

/*input {
    display: block;
    width: 100%;
    height: 34px;
    padding: 6px 12px;
    font-size: 14px;
    line-height: 1.42857143;
    color: #aaa;
    background-color: #fff;
    background-image: none;
    border: 1px solid #ccc;
    -webkit-box-shadow: inset 0 1px 1px rgba(0, 0, 0, .075);
    box-shadow: inset 0 1px 1px rgba(0, 0, 0, .075);
    -webkit-transition: border-color ease-in-out .15s, -webkit-box-shadow ease-in-out .15s;
    -o-transition: border-color ease-in-out .15s, box-shadow ease-in-out .15s;
    transition: border-color ease-in-out .15s, box-shadow ease-in-out .15s;
}*/

/*
 * Plugin: Full Calendar
 * ---------------------
 */
.fc-button {
  background: #f4f4f4;
  background-image: none;
  color: #444;
  border-color: #ddd;
  border-bottom-color: #ddd;
}
.fc-button:hover,
.fc-button:active,
.fc-button.hover {
  background-color: #e9e9e9;
}
.fc-header-title h2 {
  font-size: 15px;
  line-height: 1.6em;
  color: #666;
  margin-left: 10px;
}
.fc-header-right {
  padding-right: 10px;
}
.fc-header-left {
  padding-left: 10px;
}
.fc-widget-header {
  /*background: #fafafa;*/
}
.fc-grid {
  width: 100%;
  border: 0;
}
.fc-widget-header:first-of-type,
.fc-widget-content:first-of-type {
  border-left: 0;
  border-right: 0;
}
.fc-widget-header:last-of-type,
.fc-widget-content:last-of-type {
  border-right: 0;
}
.fc-toolbar {
  padding: 10px;
  margin: 0;
}
.fc-day-number {
  font-size: 20px;
  font-weight: 300;
  padding-right: 10px;
}
.fc-color-picker {
  list-style: none;
  margin: 0;
  padding: 0;
}
.fc-color-picker > li {
  float: left;
  font-size: 30px;
  margin-right: 5px;
  line-height: 30px;
}
.fc-color-picker > li .fa {
  -webkit-transition: -webkit-transform linear 0.3s;
  -moz-transition: -moz-transform linear 0.3s;
  -o-transition: -o-transform linear 0.3s;
  transition: transform linear 0.3s;
}
.fc-color-picker > li .fa:hover {
  -webkit-transform: rotate(30deg);
  -ms-transform: rotate(30deg);
  -o-transform: rotate(30deg);
  transform: rotate(30deg);
}
#add-new-event {
  -webkit-transition: all linear 0.3s;
  -o-transition: all linear 0.3s;
  transition: all linear 0.3s;
}
.external-event {
  padding: 5px 10px;
  font-weight: bold;
  margin-bottom: 4px;
  box-shadow: 0 1px 1px rgba(0, 0, 0, 0.1);
  text-shadow: 0 1px 1px rgba(0, 0, 0, 0.1);
  border-radius: 3px;
  cursor: move;
}
.external-event:hover {
  box-shadow: inset 0 0 90px rgba(0, 0, 0, 0.2);
}
/*
 * Plugin: Select2
 * ---------------
 */
.select2-container--default.select2-container--focus,
.select2-selection.select2-container--focus,
.select2-container--default:focus,
.select2-selection:focus,
.select2-container--default:active,
.select2-selection:active {
  outline: none;
}
.select2-container--default .select2-selection--single,
.select2-selection .select2-selection--single {
  border: 1px solid #d2d6de;
  border-radius: 0;
  padding: 6px 12px;
  height: 34px;
}
.select2-container--default.select2-container--open {
  border-color: #3c8dbc;
}
.select2-dropdown {
  border: 1px solid #d2d6de;
  border-radius: 0;
}
.select2-container--default .select2-results__option--highlighted[aria-selected] {
  background-color: #3c8dbc;
  color: white;
}
.select2-results__option {
  padding: 6px 12px;
  user-select: none;
  -webkit-user-select: none;
}
.select2-container .select2-selection--single .select2-selection__rendered {
  padding-left: 0;
  padding-right: 0;
  height: auto;
  margin-top: -4px;
}
.select2-container[dir='rtl'] .select2-selection--single .select2-selection__rendered {
  padding-right: 6px;
  padding-left: 20px;
}
.select2-container--default .select2-selection--single .select2-selection__arrow {
  height: 28px;
  right: 3px;
}
.select2-container--default .select2-selection--single .select2-selection__arrow b {
  margin-top: 0;
}
.select2-dropdown .select2-search__field,
.select2-search--inline .select2-search__field {
  border: 1px solid #d2d6de;
}
.select2-dropdown .select2-search__field:focus,
.select2-search--inline .select2-search__field:focus {
  outline: none;
  border: 1px solid #3c8dbc;
}
.select2-container--default .select2-results__option[aria-disabled='true'] {
  color: #999;
}
.select2-container--default .select2-results__option[aria-selected='true'] {
  background-color: #ddd;
}
.select2-container--default .select2-results__option[aria-selected='true'],
.select2-container--default .select2-results__option[aria-selected='true']:hover {
  color: #444;
}
.select2-container--default .select2-selection--multiple {
  border: 1px solid #d2d6de;
  border-radius: 0;
}
.select2-container--default .select2-selection--multiple:focus {
  border-color: #3c8dbc;
}
.select2-container--default.select2-container--focus .select2-selection--multiple {
  border-color: #d2d6de;
}
.select2-container--default .select2-selection--multiple .select2-selection__choice {
  background-color: #3c8dbc;
  border-color: #367fa9;
  padding: 1px 10px;
  color: #fff;
}
.select2-container--default .select2-selection--multiple .select2-selection__choice__remove {
  margin-right: 5px;
  color: rgba(255, 255, 255, 0.7);
}
.select2-container--default .select2-selection--multiple .select2-selection__choice__remove:hover {
  color: #fff;
}
.select2-container .select2-selection--single .select2-selection__rendered {
  padding-right: 10px;
}
/*
 * General: Miscellaneous
 * ----------------------
 */
.pad {
  padding: 10px;
}
.margin {
  margin: 10px;
}
.margin-bottom {
  margin-bottom: 20px;
}
.margin-bottom-none {
  margin-bottom: 0;
}
.margin-r-5 {
  margin-right: 5px;
}
.inline {
  display: inline;
}
.description-block {
  display: block;
  margin: 10px 0;
  text-align: center;
}
.description-block.margin-bottom {
  margin-bottom: 25px;
}
.description-block > .description-header {
  margin: 0;
  padding: 0;
  font-weight: 600;
  font-size: 16px;
}
.description-block > .description-text {
  text-transform: uppercase;
}
.bg-red,
.bg-yellow,
.bg-aqua,
.bg-blue,
.bg-light-blue,
.bg-green,
.bg-navy,
.bg-teal,
.bg-olive,
.bg-lime,
.bg-orange,
.bg-fuchsia,
.bg-purple,
.bg-maroon,
.bg-black,
.bg-red-active,
.bg-yellow-active,
.bg-aqua-active,
.bg-blue-active,
.bg-light-blue-active,
.bg-green-active,
.bg-navy-active,
.bg-teal-active,
.bg-olive-active,
.bg-lime-active,
.bg-orange-active,
.bg-fuchsia-active,
.bg-purple-active,
.bg-maroon-active,
.bg-black-active,
.callout.callout-danger,
.callout.callout-warning,
.callout.callout-info,
.callout.callout-success,
.alert-success,
.alert-danger,
.alert-error,
.alert-warning,
.alert-info,
.label-danger,
.label-info,
.label-warning,
.label-primary,
.label-success,
.modal-primary .modal-body,
.modal-primary .modal-header,
.modal-primary .modal-footer,
.modal-warning .modal-body,
.modal-warning .modal-header,
.modal-warning .modal-footer,
.modal-info .modal-body,
.modal-info .modal-header,
.modal-info .modal-footer,
.modal-success .modal-body,
.modal-success .modal-header,
.modal-success .modal-footer,
.modal-danger .modal-body,
.modal-danger .modal-header,
.modal-danger .modal-footer {
  color: #fff !important;
}
.bg-gray {
  color: #000;
  background-color: #d2d6de !important;
}
.bg-gray-light {
  background-color: #f7f7f7;
}
.bg-black {
  background-color: #111111 !important;
}
.bg-red,
.callout.callout-danger,
.alert-danger,
.alert-error,
.label-danger,
.modal-danger .modal-body {
  background-color: #dd4b39 !important;
}
.bg-yellow,
.callout.callout-warning,
.alert-warning,
.label-warning,
.modal-warning .modal-body {
  /*background-color: #4589D0 !important;*/
  background-color: #f39c12 !important;
}
.bg-aqua,
.callout.callout-info,
.alert-info,
.label-info,
.modal-info .modal-body {
  /*background-color: #4589D0 !important;*/
  background-color: #00c0ef !important;
}
.bg-blue {
  background-color: #0073b7 !important;
}
.bg-light-blue,
.label-primary,
.modal-primary .modal-body {
  background-color: #337ab7 !important;
}
.bg-green,
.callout.callout-success,
.alert-success,
.label-success,
.modal-success .modal-body {
  background-color: #00a65a !important;
}
.bg-navy {
  background-color: #001f3f !important;
}
.bg-teal {
  background-color: #39cccc !important;
}
.bg-olive {
  background-color: #3d9970 !important;
}
.bg-lime {
  background-color: #01ff70 !important;
}
.bg-orange {
  background-color: #ff851b !important;
}
.bg-fuchsia {
  background-color: #f012be !important;
}
.bg-purple {
  background-color: #605ca8 !important;
}
.bg-maroon {
  background-color: #d81b60 !important;
}
.bg-gray-active {
  color: #000;
  background-color: #b5bbc8 !important;
}
.bg-black-active {
  background-color: #000000 !important;
}
.bg-red-active,
.modal-danger .modal-header,
.modal-danger .modal-footer {
  background-color: #d33724 !important;
}
.bg-yellow-active,
.modal-warning .modal-header,
.modal-warning .modal-footer {
  background-color: #db8b0b !important;
}
.bg-aqua-active,
.modal-info .modal-header,
.modal-info .modal-footer {
  background-color: #00a7d0 !important;
}
.bg-blue-active {
  background-color: #005384 !important;
}
.bg-light-blue-active,
.modal-primary .modal-header,
.modal-primary .modal-footer {
  background-color: #357ca5 !important;
}
.bg-green-active,
.modal-success .modal-header,
.modal-success .modal-footer {
  background-color: #008d4c !important;
}
.bg-navy-active {
  background-color: #001a35 !important;
}
.bg-teal-active {
  background-color: #30bbbb !important;
}
.bg-olive-active {
  background-color: #368763 !important;
}
.bg-lime-active {
  background-color: #00e765 !important;
}
.bg-orange-active {
  background-color: #ff7701 !important;
}
.bg-fuchsia-active {
  background-color: #db0ead !important;
}
.bg-purple-active {
  background-color: #555299 !important;
}
.bg-maroon-active {
  background-color: #ca195a !important;
}
[class^='bg-'].disabled {
  opacity: 0.65;
  filter: alpha(opacity=65);
}
.text-red {
  color: #dd4b39 !important;
}
.text-yellow {
  color: #f39c12 !important;
}
.text-aqua {
  color: #00c0ef !important;
}
.text-blue {
  color: #0073b7 !important;
}
.text-black {
  color: #111111 !important;
}
.text-light-blue {
  color: #3c8dbc !important;
}
.text-green {
  color: #00a65a !important;
}
.text-gray {
  color: #d2d6de !important;
}
.text-navy {
  color: #001f3f !important;
}
.text-teal {
  color: #39cccc !important;
}
.text-olive {
  color: #3d9970 !important;
}
.text-lime {
  color: #01ff70 !important;
}
.text-orange {
  color: #ff851b !important;
}
.text-fuchsia {
  color: #f012be !important;
}
.text-purple {
  color: #605ca8 !important;
}
.text-maroon {
  color: #d81b60 !important;
}
.link-muted {
  color: #7a869d;
}
.link-muted:hover,
.link-muted:focus {
  color: #606c84;
}
.link-black {
  color: #666;
}
.link-black:hover,
.link-black:focus {
  color: #999;
}
.hide {
  display: none !important;
}
.no-border {
  border: 0 !important;
}
.no-padding {
  padding: 0 !important;
}
.no-margin {
  margin: 0 !important;
}
.no-shadow {
  box-shadow: none !important;
}
.list-unstyled,
.chart-legend,
.contacts-list,
.users-list,
.mailbox-attachments {
  list-style: none;
  margin: 0;
  padding: 0;
}
.list-group-unbordered > .list-group-item {
  border-left: 0;
  border-right: 0;
  border-radius: 0;
  padding-left: 0;
  padding-right: 0;
}
.flat {
  border-radius: 0 !important;
}
.text-bold,
.text-bold.table td,
.text-bold.table th {
  font-weight: 700;
}
.text-sm {
  font-size: 12px;
}
.jqstooltip {
  padding: 5px !important;
  width: auto !important;
  height: auto !important;
}
.bg-teal-gradient {
  background: #39cccc !important;
  background: -webkit-gradient(
    linear,
    left bottom,
    left top,
    color-stop(0, #39cccc),
    color-stop(1, #7adddd)
  ) !important;
  background: -ms-linear-gradient(bottom, #39cccc, #7adddd) !important;
  /*background: -moz-linear-gradient(center bottom, #39cccc 0%, #7adddd 100%) !important;*/
  background: -o-linear-gradient(#7adddd, #39cccc) !important;
  filter: progid:DXImageTransform.Microsoft.gradient(startColorstr='#7adddd', endColorstr='#39cccc', GradientType=0) !important;
  color: #fff;
}
.bg-light-blue-gradient {
  background: #3c8dbc !important;
  background: -webkit-gradient(
    linear,
    left bottom,
    left top,
    color-stop(0, #3c8dbc),
    color-stop(1, #67a8ce)
  ) !important;
  background: -ms-linear-gradient(bottom, #3c8dbc, #67a8ce) !important;
  /*background: -moz-linear-gradient(center bottom, #3c8dbc 0%, #67a8ce 100%) !important;*/
  background: -o-linear-gradient(#67a8ce, #3c8dbc) !important;
  filter: progid:DXImageTransform.Microsoft.gradient(startColorstr='#67a8ce', endColorstr='#3c8dbc', GradientType=0) !important;
  color: #fff;
}
.bg-blue-gradient {
  background: #0073b7 !important;
  background: -webkit-gradient(
    linear,
    left bottom,
    left top,
    color-stop(0, #0073b7),
    color-stop(1, #0089db)
  ) !important;
  background: -ms-linear-gradient(bottom, #0073b7, #0089db) !important;
  /*background: -moz-linear-gradient(center bottom, #0073b7 0%, #0089db 100%) !important;*/
  background: -o-linear-gradient(#0089db, #0073b7) !important;
  filter: progid:DXImageTransform.Microsoft.gradient(startColorstr='#0089db', endColorstr='#0073b7', GradientType=0) !important;
  color: #fff;
}
.bg-aqua-gradient {
  background: #00c0ef !important;
  background: -webkit-gradient(
    linear,
    left bottom,
    left top,
    color-stop(0, #00c0ef),
    color-stop(1, #14d1ff)
  ) !important;
  background: -ms-linear-gradient(bottom, #00c0ef, #14d1ff) !important;
  /*background: -moz-linear-gradient(center bottom, #00c0ef 0%, #14d1ff 100%) !important;*/
  background: -o-linear-gradient(#14d1ff, #00c0ef) !important;
  filter: progid:DXImageTransform.Microsoft.gradient(startColorstr='#14d1ff', endColorstr='#00c0ef', GradientType=0) !important;
  color: #fff;
}
.bg-yellow-gradient {
  background: #f39c12 !important;
  background: -webkit-gradient(
    linear,
    left bottom,
    left top,
    color-stop(0, #f39c12),
    color-stop(1, #f7bc60)
  ) !important;
  background: -ms-linear-gradient(bottom, #f39c12, #f7bc60) !important;
  /*background: -moz-linear-gradient(center bottom, #f39c12 0%, #f7bc60 100%) !important;*/
  background: -o-linear-gradient(#f7bc60, #f39c12) !important;
  filter: progid:DXImageTransform.Microsoft.gradient(startColorstr='#f7bc60', endColorstr='#f39c12', GradientType=0) !important;
  color: #fff;
}
.bg-purple-gradient {
  background: #605ca8 !important;
  background: -webkit-gradient(
    linear,
    left bottom,
    left top,
    color-stop(0, #605ca8),
    color-stop(1, #9491c4)
  ) !important;
  background: -ms-linear-gradient(bottom, #605ca8, #9491c4) !important;
  /*background: -moz-linear-gradient(center bottom, #605ca8 0%, #9491c4 100%) !important;*/
  background: -o-linear-gradient(#9491c4, #605ca8) !important;
  filter: progid:DXImageTransform.Microsoft.gradient(startColorstr='#9491c4', endColorstr='#605ca8', GradientType=0) !important;
  color: #fff;
}
.bg-green-gradient {
  background: #00a65a !important;
  background: -webkit-gradient(
    linear,
    left bottom,
    left top,
    color-stop(0, #00a65a),
    color-stop(1, #00ca6d)
  ) !important;
  background: -ms-linear-gradient(bottom, #00a65a, #00ca6d) !important;
  /*background: -moz-linear-gradient(center bottom, #00a65a 0%, #00ca6d 100%) !important;*/
  background: -o-linear-gradient(#00ca6d, #00a65a) !important;
  filter: progid:DXImageTransform.Microsoft.gradient(startColorstr='#00ca6d', endColorstr='#00a65a', GradientType=0) !important;
  color: #fff;
}
.bg-red-gradient {
  background: #dd4b39 !important;
  background: -webkit-gradient(
    linear,
    left bottom,
    left top,
    color-stop(0, #dd4b39),
    color-stop(1, #e47365)
  ) !important;
  background: -ms-linear-gradient(bottom, #dd4b39, #e47365) !important;
  /*background: -moz-linear-gradient(center bottom, #dd4b39 0%, #e47365 100%) !important;*/
  background: -o-linear-gradient(#e47365, #dd4b39) !important;
  filter: progid:DXImageTransform.Microsoft.gradient(startColorstr='#e47365', endColorstr='#dd4b39', GradientType=0) !important;
  color: #fff;
}
.bg-black-gradient {
  background: #111111 !important;
  background: -webkit-gradient(
    linear,
    left bottom,
    left top,
    color-stop(0, #111111),
    color-stop(1, #2b2b2b)
  ) !important;
  background: -ms-linear-gradient(bottom, #111111, #2b2b2b) !important;
  /*background: -moz-linear-gradient(center bottom, #111111 0%, #2b2b2b 100%) !important;*/
  background: -o-linear-gradient(#2b2b2b, #111111) !important;
  filter: progid:DXImageTransform.Microsoft.gradient(startColorstr='#2b2b2b', endColorstr='#111111', GradientType=0) !important;
  color: #fff;
}
.bg-maroon-gradient {
  background: #d81b60 !important;
  background: -webkit-gradient(
    linear,
    left bottom,
    left top,
    color-stop(0, #d81b60),
    color-stop(1, #e73f7c)
  ) !important;
  background: -ms-linear-gradient(bottom, #d81b60, #e73f7c) !important;
  /*background: -moz-linear-gradient(center bottom, #d81b60 0%, #e73f7c 100%) !important;*/
  background: -o-linear-gradient(#e73f7c, #d81b60) !important;
  filter: progid:DXImageTransform.Microsoft.gradient(startColorstr='#e73f7c', endColorstr='#d81b60', GradientType=0) !important;
  color: #fff;
}
.description-block .description-icon {
  font-size: 16px;
}
.no-pad-top {
  padding-top: 0;
}
.position-static {
  position: static !important;
}
.list-header {
  font-size: 15px;
  padding: 10px 4px;
  font-weight: bold;
  color: #666;
}
.list-seperator {
  height: 1px;
  background: #f4f4f4;
  margin: 15px 0 9px 0;
}
.list-link > a {
  padding: 4px;
  color: #777;
}
.list-link > a:hover {
  color: #222;
}
.font-light {
  font-weight: 300;
}
.user-block:before,
.user-block:after {
  content: ' ';
  display: table;
}
.user-block:after {
  clear: both;
}
.user-block img {
  width: 40px;
  height: 40px;
  float: left;
}
.user-block .username,
.user-block .description,
.user-block .comment {
  display: block;
  margin-left: 50px;
}
.user-block .username {
  font-size: 16px;
  font-weight: 600;
}
.user-block .description {
  color: #999;
  font-size: 13px;
}
.user-block.user-block-sm .username,
.user-block.user-block-sm .description,
.user-block.user-block-sm .comment {
  margin-left: 40px;
}
.user-block.user-block-sm .username {
  font-size: 14px;
}
.img-sm,
.img-md,
.img-lg,
.box-comments .box-comment img,
.user-block.user-block-sm img {
  float: left;
}
.img-sm,
.box-comments .box-comment img,
.user-block.user-block-sm img {
  width: 30px !important;
  height: 30px !important;
}
.img-sm + .img-push {
  margin-left: 40px;
}
.img-md {
  width: 60px;
  height: 60px;
}
.img-md + .img-push {
  margin-left: 70px;
}
.img-lg {
  width: 100px;
  height: 100px;
}
.img-lg + .img-push {
  margin-left: 110px;
}
.img-bordered {
  border: 3px solid #d2d6de;
  padding: 3px;
}
.img-bordered-sm {
  border: 2px solid #d2d6de;
  padding: 2px;
}
.attachment-block {
  border: 1px solid #f4f4f4;
  padding: 5px;
  margin-bottom: 10px;
  background: #f7f7f7;
}
.attachment-block .attachment-img {
  max-width: 100px;
  max-height: 100px;
  height: auto;
  float: left;
}
.attachment-block .attachment-pushed {
  margin-left: 110px;
}
.attachment-block .attachment-heading {
  margin: 0;
}
.attachment-block .attachment-text {
  color: #555;
}
.connectedSortable {
  min-height: 100px;
}
.ui-helper-hidden-accessible {
  border: 0;
  clip: rect(0 0 0 0);
  height: 1px;
  margin: -1px;
  overflow: hidden;
  padding: 0;
  position: absolute;
  width: 1px;
}
.sort-highlight {
  background: #f4f4f4;
  border: 1px dashed #ddd;
  margin-bottom: 10px;
}
.full-opacity-hover {
  opacity: 0.65;
  filter: alpha(opacity=65);
}
.full-opacity-hover:hover {
  opacity: 1;
  filter: alpha(opacity=100);
}
.chart {
  position: relative;
  overflow: hidden;
  width: 100%;
}
.chart svg,
.chart canvas {
  width: 100% !important;
}
/*
 * Misc: print
 * -----------
 */
@media print {
  .no-print,
  .main-sidebar,
  .left-side,
  .main-header,
  .content-header {
    display: none !important;
  }
  .content-wrapper,
  .right-side,
  .main-footer {
    margin-left: 0 !important;
    min-height: 0 !important;
    -webkit-transform: translate(0, 0) !important;
    -ms-transform: translate(0, 0) !important;
    -o-transform: translate(0, 0) !important;
    transform: translate(0, 0) !important;
  }
  .fixed .content-wrapper,
  .fixed .right-side {
    padding-top: 0 !important;
  }
  .invoice {
    width: 100%;
    border: 0;
    margin: 0;
    padding: 0;
  }
  .invoice-col {
    float: left;
    width: 33.3333333%;
  }
  .table-responsive {
    overflow: auto;
  }
  .table-responsive > .table tr th,
  .table-responsive > .table tr td {
    white-space: normal !important;
  }
}

.chart_left > div {
  background: #f3f8fc;
  border-bottom: #e0e0e0 1px solid;
  border-right: #e0e0e0 1px solid;
  line-height: 40px;
  text-align: left;
  padding: 0 20px;
}

.DB_chart .title_07 {
  background: #f1f2f8;
  text-align: center;
  padding: 0;
}

.DB_chart .title_07 {
  height: 41px;
  line-height: 41px;
}

.DB_chart {
  border-top: 2px #0099cc solid;
  text-align: center;
  margin-bottom: 25px;
}

.DB_chart .chart_left {
  float: left;
  width: 20%;
  font-weight: bold;
}

.DB_chart .chart_right {
  float: left;
  width: 80%;
  overflow: auto;
}

.DB_chart .chart_right_wrapper {
  width: 99999%;
}

.DB_chart .chart_right_wrapper .data {
  float: left;
  width: 130px;
}

.DB_chart .chart_right_wrapper span {
  line-height: 40px;
  background: #f1f2f8;
  border-bottom: #e0e0e0 1px solid;
  border-right: #e0e0e0 1px solid;
  width: 130px;
  display: inline-block;
  font-weight: bold;
  box-sizing: border-box;
}

.DB_chart .chart_right_wrapper .data div {
  background: #fff;
  border-bottom: #e0e0e0 1px solid;
  border-right: #e0e0e0 1px solid;
  line-height: 40px;
}

/* input[type="text"]{
    width: 100px;
    height: 30px;
} */

.ft {
  float: right;
  margin-top: 3px;
}
/* 異붽��걹 */

.IPBOX input[type='text'] {
  width: 500px !important;
}
.card .card-title {
  color: #001737;
  margin-bottom: 0.6rem;
  text-transform: capitalize;
  font-size: 1rem;
  font-weight: 600;
}
.material-icons {
  font-size: 16px !important;
  padding-top: 5px;
}

/* ## use ## */

html,
body {
  font-family: 'NanumSquare', sans-serif;
}

/* 사이드바 폰트 */
.main-header .logo {
  font-family: 'NanumSquare', sans-serif;
}

.cesium-viewer-bottom,
.vw-logo logobtn {
  display: none;
}

/** progress **/
.progress-body {
  position: fixed;
  display: none;
  background: white;
  width: calc(100% - 290px);
  height: 100%;
  z-index: 4444;
  top: 72px;
  left: 290px;
  opacity: 0.5;
}
.progress-content {
  border-radius: 20px;
  width: 120px;
  height: 120px;
  position: absolute;
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%);
  background: url('/img/spinner.gif');
  background-size: cover;
}

/* pm.css */
.chartdiv {
  width: 100%;
  height: 100%;
  /*margin-bottom: 30px;*/
}

.pieChartdiv {
  height: 100%;
  position: relative;
}

.pieModalChartDiv {
  height: 100%;
  position: relative;
}

.chartpie {
  height: 100%;
  position: relative;
}

.chart-wrapper {
  display: flex;
  flex-direction: row;
  flex-wrap: nowrap;
  gap: 2px;
  /*margin-bottom: 20px;*/
}

.loc-wordcloud-acc-vs {
  display: flex;
}

.time-lchart-acc-vs {
  display: flex;
  justify-content: space-between;
}

.loc-wordcloud-usage-vs {
  display: flex;
}

.operator-bchart-usage-vs {
  display: flex;
  justify-content: space-between;
}

.left-chart-div {
  display: flex;
  flex: 1;
  align-items: center;
}

.right-chart-div {
  flex: 2;
}

.chart-div {
  flex: 1;
}

.loc-lchart-div {
  flex: 3;
}

.time-lchart-acc-wrapper {
  flex: 1;
}

.time-lchart-usage-wrapper {
  flex: 3;
}

.dow-bchart-acc-wrapper {
  flex: 1;
}

.operator-bchart-usage-wrapper {
  flex: 2;
}

.comb-bheatmap-usage-wrapper {
  flex: 3;
}

.dow-bchart-usage-wrapper {
  flex: 2;
}

.div-inner {
  position: absolute;
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
}

.no-data {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
}

.exportdiv {
  width: 100%;
  height: 40px;
  border-bottom: 1px solid #eee;
}

.custom-modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px !important;
  border-top-right-radius: 5px;
  border-top-left-radius: 5px;
  background-color: #eff0f1;
}
.custom-modal-title {
  font-size: 23px;
}
.custom-close {
  color: #aaa;
  float: right;
  font-weight: bold;
  font-size: 23px;
  line-height: 1;
  border: none;
}
.custom-close:hover {
  cursor: pointer;
  color: #000;
  text-decoration: none;
}
.custom-modal-footer {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  padding: 16px;
  border-bottom-right-radius: 5px;
  border-bottom-left-radius: 5px;
  background-color: #eff0f1;
}
.custom-modal-body {
  padding: 0px;
}
.custom-modal-lg {
  max-width: 810px;
}
.custom-modal {
  position: fixed;
  left: 0;
  top: 0;
  width: 100%;
  height: 100%;
  overflow: auto;
  background-color: rgba(0, 0, 0, 0.4);
}
.custom-modal-content {
  width: 100%;
}
.custom-modal-dialog {
  position: relative;
  margin: 30px auto;
  max-width: 55%;
}
.custom-modal-none {
  opacity: 0;
  z-index: -999;
}
.custom-modal-show {
  opacity: 1;
  z-index: 9999;
}

/*chart*/
.chart-header {
  color: #444;
  padding: 3px 10px;
  position: relative;
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 42px;
}

.chart-header-option {
  color: #444;
  position: relative;
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-right: 10px;
  height: 100%;
}

.btn-group :not(button:last-child) {
  margin-right: 15px;
}

.chart-header-wrapper {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
}

.chart-header-subwrapper {
  display: flex;
  flex-direction: row;
}

.chart-header-title {
  display: inline-block;
  font-size: 18px;
  margin: 0;
  line-height: 0;
}

.chart-header-title-wrapper {
  display: flex;
  align-items: center;
}

.chart-header-search-wrapper {
  gap: 50px;
  display: flex;
}

#vw-notice {
  position: absolute;
  bottom: 0;
  right: 0;
  z-index: 9999;
  color: red;
  font-size: 20px;
  display: none;
  background-color: rgba(255, 255, 255, 0.5);
  padding: 15px 15px;
}

/*legend*/
#legend {
  position: absolute;
  z-index: 99999;
  left: 600px;
  bottom: 0;
  background-color: rgba(255, 255, 255, 0.5);
  padding: 10px 10px;
  display: flex;
  flex-direction: column;
  display: none;
}

.square-box {
  display: flex;
}

.square-box-box {
  display: flex;
  align-items: center;
  margin-right: 10px;
}

.blue-square {
  width: 10px;
  height: 10px;
  background-color: blue;
}

.green-square {
  width: 10px;
  height: 10px;
  background-color: green;
}

.red-square {
  width: 10px;
  height: 10px;
  background-color: red;
}

/*range*/
.gis-range-wrapper {
  width: 100%;
  position: absolute;
  z-index: 99999;
  left: 600px;
  top: 72px;
  background-color: transparent;
  height: 60px;
  padding-left: 10px;
  display: none;
  margin-top: 20px;
}

.gis-range {
  position: relative;
  width: 710px;
  height: 5px;
}

.gis-range input {
  width: 100%;
  position: absolute;
  top: 2px;
  height: 0;
  -webkit-appearance: none;
}

.gis-range input::-webkit-slider-thumb {
  -webkit-appearance: none;
  width: 18px;
  height: 18px;
  margin: -8px 0 0;
  border-radius: 50%;
  background: #37adbf;
  cursor: pointer;
  border: 0 !important;
}

.gis-range input::-moz-range-thumb {
  width: 18px;
  height: 18px;
  margin: -8px 0 0;
  border-radius: 50%;
  background: #37adbf;
  cursor: pointer;
  border: 0 !important;
}

.gis-range input::-ms-thumb {
  width: 18px;
  height: 18px;
  margin: -8px 0 0;
  border-radius: 50%;
  background: #37adbf;
  cursor: pointer;
  border: 0 !important;
}

.gis-range input::-webkit-slider-runnable-track {
  width: 100%;
  height: 2px;
  cursor: pointer;
  background: #b2b2b2;
}

.gis-range input::-moz-range-track {
  width: 100%;
  height: 2px;
  cursor: pointer;
  background: #b2b2b2;
}

.gis-range input::-ms-track {
  width: 100%;
  height: 2px;
  cursor: pointer;
  background: #b2b2b2;
}

.gis-range input:focus {
  background: none;
  outline: none;
}

.gis-range input::-ms-track {
  width: 100%;
  cursor: pointer;
  background: transparent;
  border-color: transparent;
  color: transparent;
}

.range-labels {
  margin: 10px 0 0;
  padding: 0;
  list-style: none;
}

.range-labels li {
  position: relative;
  float: left;
  width: 30px;
  text-align: center;
  color: #b2b2b2;
  font-size: 14px;
  cursor: pointer;
}

.range-labels li::before {
  position: absolute;
  top: -17px;
  right: 0;
  left: 0;
  content: '';
  margin: 0 auto;
  width: 9px;
  height: 9px;
  background: #b2b2b2;
  border-radius: 50%;
}

.range-labels .active {
  color: #37adbf;
}

.range-labels .selected::before {
  background: #37adbf;
}

.range-labels .active.selected::before {
  display: none;
}

.option-style {
  height: 80%;
  padding: 5px;
  border-radius: 10px;
}

.ill-pk-wrapper {
  height: 34px;
  display: flex;
  gap: 10px;
  align-items: center;
}

.ill-pk-cell {
  height: 100%;
  display: flex;
  align-items: center;
}

table.dataTable tbody > tr > td {
  background-color: transparent;
}

.table-hover tbody tr:hover {
  background-color: #e0f3ff;
}

.dataTables_scrollHead th {
  text-align: center;
}

.popup-wrapper {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 250px;
  text-align: center;
  background: #fff;
  z-index: 1000;
  border-radius: 10px;
  padding: 15px 10px;
  font-weight: bold;
  display: none;
}

.glayout {
  display: flex;
  flex-direction: column;
  gap: 8px;
  height: 100%;
}

.graph-frame {
  height: calc(100vh - 70px);
}

.graph-main-frame {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.flex1 {
  flex: 1;
}

.graph-section {
  padding: 0 10px 0 10px;
  height: 100%;
}

.graph-calc-height {
  height: 100%;
}

/*.zoom-icon {
    font-size: 25px !important;
    cursor: pointer;
    padding-top: 0 !important;
}*/

.zoom-icon:hover {
  color: grey;
}

.material-icons-wrapper {
  display: flex;
  align-items: center;
  padding-right: 3px;
}

#first-chart {
  width: 100%;
  height: 100%;
}

.second-chart-show {
  width: 100%;
  height: 50% !important;
}

.first-chart-half {
  height: 50% !important;
}

.reset-bar {
  position: relative;
  margin-right: 30px !important;
}

.reset-bar::after {
  position: absolute;
  left: calc(100% + 16px);
  top: 3px;
  content: '';
  width: 2px;
  height: calc(100% - 6px);
  background-color: grey;
}

/* board.css */

.col-xs-12 {
  width: 100%;
}
.col-xs-1,
.col-xs-2,
.col-xs-3,
.col-xs-4,
.col-xs-5,
.col-xs-6,
.col-xs-7,
.col-xs-8,
.col-xs-9,
.col-xs-10,
.col-xs-11,
.col-xs-12 {
  float: left;
}
.col-xs-4 {
  width: 33.33333333%;
}
.col-xs-1,
.col-sm-1,
.col-md-1,
.col-lg-1,
.col-xs-2,
.col-sm-2,
.col-md-2,
.col-lg-2,
.col-xs-3,
.col-sm-3,
.col-md-3,
.col-lg-3,
.col-xs-4,
.col-sm-4,
.col-md-4,
.col-lg-4,
.col-xs-5,
.col-sm-5,
.col-md-5,
.col-lg-5,
.col-xs-6,
.col-sm-6,
.col-md-6,
.col-lg-6,
.col-xs-7,
.col-sm-7,
.col-md-7,
.col-lg-7,
.col-xs-8,
.col-sm-8,
.col-md-8,
.col-lg-8,
.col-xs-9,
.col-sm-9,
.col-md-9,
.col-lg-9,
.col-xs-10,
.col-sm-10,
.col-md-10,
.col-lg-10,
.col-xs-11,
.col-sm-11,
.col-md-11,
.col-lg-11,
.col-xs-12,
.col-sm-12,
.col-md-12,
.col-lg-12 {
  position: relative;
  min-height: 1px;
  /*padding-right: 15px;
    padding-left: 15px;*/
}
.input-group-addon,
.input-group-btn {
  width: 1%;
  white-space: nowrap;
  vertical-align: middle;
}
.input-group-addon {
  padding: 6px 12px;
  font-size: 14px;
  font-weight: normal;
  line-height: 1;
  color: #555;
  text-align: center;
  background-color: #eee;
  border: 1px solid #ccc;
  border-radius: 4px;
}
.input-group-addon.input-sm {
  padding: 5px 10px;
  font-size: 12px;
  border-radius: 3px;
}
.input-group-addon.input-lg {
  padding: 10px 16px;
  font-size: 18px;
  border-radius: 6px;
}
.input-group-addon input[type='radio'],
.input-group-addon input[type='checkbox'] {
  margin-top: 0;
}
.input-group .form-control:first-child,
.input-group-addon:first-child,
.input-group-btn:first-child > .btn,
.input-group-btn:first-child > .btn-group > .btn,
.input-group-btn:first-child > .dropdown-toggle,
.input-group-btn:last-child > .btn:not(:last-child):not(.dropdown-toggle),
.input-group-btn:last-child > .btn-group:not(:last-child) > .btn {
  border-top-right-radius: 0;
  border-bottom-right-radius: 0;
}
.input-group-addon:first-child {
  border-right: 0;
}
.input-group .form-control:last-child,
.input-group-addon:last-child,
.input-group-btn:last-child > .btn,
.input-group-btn:last-child > .btn-group > .btn,
.input-group-btn:last-child > .dropdown-toggle,
.input-group-btn:first-child > .btn:not(:first-child),
.input-group-btn:first-child > .btn-group:not(:first-child) > .btn {
  border-top-left-radius: 0;
  border-bottom-left-radius: 0;
}
.input-group-addon:last-child {
  border-left: 0;
}
.input-group-btn {
  position: relative;
  font-size: 0;
  white-space: nowrap;
}
.input-group-btn > .btn {
  position: relative;
}
.input-group-btn > .btn + .btn {
  margin-left: -1px;
}
.input-group-btn > .btn:hover,
.input-group-btn > .btn:focus,
.input-group-btn > .btn:active {
  z-index: 2;
}
.input-group-btn:first-child > .btn,
.input-group-btn:first-child > .btn-group {
  margin-right: -1px;
}
.input-group-btn:last-child > .btn,
.input-group-btn:last-child > .btn-group {
  z-index: 2;
  margin-left: -1px;
}
div.dataTables_wrapper div.dataTables_length label {
  font-weight: normal;
  text-align: left;
  white-space: nowrap;
}
div.dataTables_wrapper div.dataTables_paginate ul.pagination {
  margin: 2px 0;
  white-space: nowrap;
  justify-content: flex-end;
}
.pull-right {
  float: right !important;
}
.btn {
  display: inline-block;
  padding: 6px 12px;
  margin-bottom: 0;
  font-size: 14px;
  font-weight: normal;
  line-height: 1.42857143;
  text-align: center;
  white-space: nowrap;
  vertical-align: middle;
  -ms-touch-action: manipulation;
  touch-action: manipulation;
  cursor: pointer;
  -webkit-user-select: none;
  -moz-user-select: none;
  -ms-user-select: none;
  user-select: none;
  background-image: none;
  border: 1px solid #bebebe;
  border-radius: 4px;
}

/* selectStyle.css */
/*버튼*/

/*보라색*/
.btn-outline-primary {
  color: #767676;
  border-color: #dcd3f1 !important;
  font-weight: bold !important;
}

.btn-outline-primary:hover {
  color: #fff !important;
  background-color: #767676;
  border-color: #000000;
}

.btn-outline-primary:focus,
.btn-outline-primary.focus {
  box-shadow: 0 0 0 0.2rem rgba(0, 0, 0, 0.5);
}

.btn-outline-primary.disabled,
.btn-outline-primary:disabled {
  color: #767676;
  background-color: transparent;
}

.btn-outline-primary:not(:disabled):not(.disabled):active,
.btn-outline-primary:not(:disabled):not(.disabled).active,
.show > .btn-outline-primary.dropdown-toggle {
  color: #fff !important;
  background-color: #767676;
  border-color: #000000;
}

.btn-outline-primary:not(:disabled):not(.disabled):active:focus,
.btn-outline-primary:not(:disabled):not(.disabled).active:focus,
.show > .btn-outline-primary.dropdown-toggle:focus {
  box-shadow: 0 0 0 0.2rem rgba(0, 0, 0, 0.5);
}

/*빨간색*/
.btn-outline-red {
  color: #ff3939;
  border-color: #ff6666 !important;
  font-weight: bold !important;
}

.btn-outline-red:hover {
  color: #fff !important;
  background-color: #ff3939;
  border-color: #ff3939;
}

.btn-outline-red:focus,
.btn-outline-primary.focus {
  box-shadow: 0 0 0 0.2rem rgba(102, 64, 178, 0.5);
}

.btn-outline-red.disabled,
.btn-outline-red:disabled {
  color: #ff3939;
  background-color: transparent;
}

.btn-outline-red:not(:disabled):not(.disabled):active,
.btn-outline-red:not(:disabled):not(.disabled).active,
.show > .btn-outline-primary.dropdown-toggle {
  color: #fff !important;
  background-color: #ff3939;
  border-color: #ff3939;
}

.btn-outline-red:not(:disabled):not(.disabled):active:focus,
.btn-outline-primary:not(:disabled):not(.disabled).active:focus,
.show > .btn-outline-primary.dropdown-toggle:focus {
  box-shadow: 0 0 0 0.2rem rgba(102, 64, 178, 0.5);
}

/*파랑*/
.btn-outline-blue {
  color: #399cff;
  border-color: #77bbff !important;
  font-weight: bold !important;
}

.btn-outline-blue:hover {
  color: #fff !important;
  background-color: #399cff;
  border-color: #399cff;
}

.btn-outline-blue:focus,
.btn-outline-primary.focus {
  box-shadow: 0 0 0 0.2rem rgba(102, 64, 178, 0.5);
}

.btn-outline-blue.disabled,
.btn-outline-blue:disabled {
  color: #399cff;
  background-color: transparent;
}

.btn-outline-blue:not(:disabled):not(.disabled):active,
.btn-outline-blue:not(:disabled):not(.disabled).active,
.show > .btn-outline-primary.dropdown-toggle {
  color: #fff !important;
  background-color: #399cff;
  border-color: #399cff;
}

.btn-outline-blue:not(:disabled):not(.disabled):active:focus,
.btn-outline-primary:not(:disabled):not(.disabled).active:focus,
.show > .btn-outline-primary.dropdown-toggle:focus {
  box-shadow: 0 0 0 0.2rem rgba(102, 64, 178, 0.5);
}

/*초록*/

.btn-outline-green {
  color: #00e600;
  border-color: #00e600 !important;
  font-weight: bold !important;
}

.btn-outline-green:hover {
  color: #fff !important;
  background-color: #00e600;
  border-color: #00e600;
}

.btn-outline-green:focus,
.btn-outline-primary.focus {
  box-shadow: 0 0 0 0.2rem rgba(102, 64, 178, 0.5);
}

.btn-outline-green.disabled,
.btn-outline-green:disabled {
  color: #00e600;
  background-color: transparent;
}

.btn-outline-green:not(:disabled):not(.disabled):active,
.btn-outline-green:not(:disabled):not(.disabled).active,
.show > .btn-outline-primary.dropdown-toggle {
  color: #fff !important;
  background-color: #00e600;
  border-color: #00e600;
}

.btn-outline-green:not(:disabled):not(.disabled):active:focus,
.btn-outline-primary:not(:disabled):not(.disabled).active:focus,
.show > .btn-outline-primary.dropdown-toggle:focus {
  box-shadow: 0 0 0 0.2rem rgba(102, 64, 178, 0.5);
}

/*노랑*/
.btn-outline-yellow {
  color: #eaea00;
  border-color: #eaea00 !important;
  font-weight: bold !important;
}

.btn-outline-yellow:hover {
  color: #fff !important;
  background-color: #eaea00;
  border-color: #eaea00;
}

.btn-outline-yellow:focus,
.btn-outline-primary.focus {
  box-shadow: 0 0 0 0.2rem rgba(102, 64, 178, 0.5);
}

.btn-outline-yellow.disabled,
.btn-outline-yellow:disabled {
  color: #eaea00;
  background-color: transparent;
}

.btn-outline-yellow:not(:disabled):not(.disabled):active,
.btn-outline-yellow:not(:disabled):not(.disabled).active,
.show > .btn-outline-primary.dropdown-toggle {
  color: #fff !important;
  background-color: #eaea00;
  border-color: #eaea00;
}

.btn-outline-yellow:not(:disabled):not(.disabled):active:focus,
.btn-outline-primary:not(:disabled):not(.disabled).active:focus,
.show > .btn-outline-primary.dropdown-toggle:focus {
  box-shadow: 0 0 0 0.2rem rgba(102, 64, 178, 0.5);
}

/*검정*/
.btn-outline-black {
  color: #000000;
  border-color: #b8b8b8 !important;
  font-weight: bold !important;
}

.btn-outline-black:hover {
  color: #fff !important;
  background-color: #000000;
  border-color: #000000;
}

.btn-outline-black:focus,
.btn-outline-primary.focus {
  box-shadow: 0 0 0 0.2rem rgba(102, 64, 178, 0.5);
}

.btn-outline-black.disabled,
.btn-outline-black:disabled {
  color: #000000;
  background-color: transparent;
}

.btn-outline-black:not(:disabled):not(.disabled):active,
.btn-outline-black:not(:disabled):not(.disabled).active,
.show > .btn-outline-primary.dropdown-toggle {
  color: #fff !important;
  background-color: #000000;
  border-color: #000000;
}

.btn-outline-black:not(:disabled):not(.disabled):active:focus,
.btn-outline-primary:not(:disabled):not(.disabled).active:focus,
.show > .btn-outline-primary.dropdown-toggle:focus {
  box-shadow: 0 0 0 0.2rem rgba(102, 64, 178, 0.5);
}

/*테이블 관련*/
.cmsTable {
  width: 100%;
  font-size: 14px;
  border-collapse: collapse;
}
.cmsTable th {
  width: 20%;
  padding: 5px 15px !important;
  background-color: #2c3038 !important;
  color: #fff !important;
  border: 1px solid #bdbdbd !important;
  line-height: 30px !important;
  text-align: center !important;
  height: 30px !important;
}
.cmsTable td {
  width: 30%;
  padding: 5px 15px !important;
  background-color: #fff !important;
  color: #000 !important;
  border: 1px solid #bdbdbd !important;
  line-height: 30px !important;
  text-align: center !important;
  height: 30px !important;
}

@media (max-width: 575.98px) {
  table.cmsTable th,
  table.cmsTable td {
    margin-top: -1px;
    display: block;
    float: left;
    box-sizing: border-box;
    height: 40px !important;
    vertical-align: middle;
  }
  table.cmsTable th {
    width: 40%;
  }
  table.cmsTable td {
    width: 60%;
  }
}

.table-condensed th {
  padding-right: 30px !important;
  border: 0px !important;
  line-height: 30px !important;
  height: 30px !important;
}
.table-condensed td {
  padding-right: 30px !important;
  color: #000 !important;
  border: 0px !important;
  /* text-align: center!important; */
  height: 30px !important;
}

/*width관련*/

@media (min-width: 768px) {
  .w10 {
    width: 10% !important;
  }

  .w20 {
    width: 20% !important;
  }

  .w30 {
    width: 30% !important;
  }

  .w40 {
    width: 40% !important;
  }

  .w50 {
    width: 50% !important;
  }

  .w60 {
    width: 60% !important;
  }

  .w70 {
    width: 70% !important;
  }

  .w80 {
    width: 80% !important;
  }

  .w90 {
    width: 90% !important;
  }

  .w100 {
    width: 100% !important;
  }
}
@media (max-width: 767px) {
  .w10 {
    width: 70% !important;
  }

  .w20 {
    width: 70% !important;
  }

  .w30 {
    width: 70% !important;
  }

  .w40 {
    width: 70% !important;
  }

  .w50 {
    width: 70% !important;
  }

  .w60 {
    width: 70% !important;
  }

  .w70 {
    width: 70% !important;
  }

  .w80 {
    width: 80% !important;
  }

  .w90 {
    width: 90% !important;
  }

  .w100 {
    width: 100% !important;
  }
}

/* bootstrap.css */
/*!
 * Bootstrap v3.3.5 (http://getbootstrap.com)
 * Copyright 2011-2015 Twitter, Inc.
 * Licensed under MIT (https://github.com/twbs/bootstrap/blob/master/LICENSE)
 */
/*! normalize.css v3.0.3 | MIT License | github.com/necolas/normalize.css */
@import url(https://cdn.jsdelivr.net/gh/moonspam/NanumSquare@1.0/nanumsquare.css);
@import url(https://cdn.rawgit.com/moonspam/NanumSquare/master/nanumsquare.css);

html {
  font-family: 'NanumSquare', sans-serif;
  -webkit-text-size-adjust: 100%;
  -ms-text-size-adjust: 100%;
}
body {
  margin: 0;
}
article,
aside,
details,
figcaption,
figure,
footer,
header,
hgroup,
main,
menu,
nav,
section,
summary {
  display: block;
}
audio,
canvas,
progress,
video {
  display: inline-block;
  vertical-align: baseline;
}
audio:not([controls]) {
  display: none;
  height: 0;
}
[hidden],
template {
  display: none;
}
a {
  background-color: transparent;
}
a:active,
a:hover {
  outline: 0;
}
abbr[title] {
  border-bottom: 1px dotted;
}
b,
strong {
  font-weight: bold;
}
dfn {
  font-style: italic;
}
h1 {
  margin: 0.67em 0;
  font-size: 2em;
}
mark {
  color: #000;
  background: #ff0;
}
small {
  font-size: 80%;
}
sub,
sup {
  position: relative;
  font-size: 75%;
  line-height: 0;
  vertical-align: baseline;
}
sup {
  top: -0.5em;
}
sub {
  bottom: -0.25em;
}
img {
  border: 0;
}
svg:not(:root) {
  overflow: hidden;
}
figure {
  margin: 1em 40px;
}
hr {
  height: 0;
  -webkit-box-sizing: content-box;
  -moz-box-sizing: content-box;
  box-sizing: content-box;
}
pre {
  overflow: auto;
}
code,
kbd,
pre,
samp {
  font-family: monospace, monospace;
  font-size: 1em;
}
button,
input,
optgroup,
select,
textarea {
  margin: 0;
  font: inherit;
  color: inherit;
}
button {
  overflow: visible;
}
button,
select {
  text-transform: none;
}
button,
html input[type='button'],
input[type='reset'],
input[type='submit'] {
  -webkit-appearance: button;
  cursor: pointer;
}
button[disabled],
html input[disabled] {
  cursor: default;
}
button::-moz-focus-inner,
input::-moz-focus-inner {
  padding: 0;
  border: 0;
}
input {
  line-height: normal;
}
input[type='password'] {
  font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif;
}
input[type='checkbox'],
input[type='radio'] {
  -webkit-box-sizing: border-box;
  -moz-box-sizing: border-box;
  box-sizing: border-box;
  padding: 0;
}
input[type='number']::-webkit-inner-spin-button,
input[type='number']::-webkit-outer-spin-button {
  height: auto;
}
input[type='search'] {
  -webkit-box-sizing: content-box;
  -moz-box-sizing: content-box;
  box-sizing: content-box;
  -webkit-appearance: textfield;
}
input[type='search']::-webkit-search-cancel-button,
input[type='search']::-webkit-search-decoration {
  -webkit-appearance: none;
}
fieldset {
  padding: 0.35em 0.625em 0.75em;
  margin: 0 2px;
  border: 1px solid #c0c0c0;
}
legend {
  padding: 0;
  border: 0;
}
textarea {
  overflow: auto;
}
optgroup {
  font-weight: bold;
}
table {
  border-spacing: 0;
  border-collapse: collapse;
}
td,
th {
  padding: 0;
}
/*! Source: https://github.com/h5bp/html5-boilerplate/blob/master/src/css/main.css */
@media print {
  *,
  *:before,
  *:after {
    color: #000 !important;
    text-shadow: none !important;
    background: transparent !important;
    -webkit-box-shadow: none !important;
    box-shadow: none !important;
  }
  a,
  a:visited {
    text-decoration: underline;
  }
  a[href]:after {
    content: ' (' attr(href) ')';
  }
  abbr[title]:after {
    content: ' (' attr(title) ')';
  }
  a[href^='#']:after,
  a[href^='javascript:']:after {
    content: '';
  }
  pre,
  blockquote {
    border: 1px solid #999;

    page-break-inside: avoid;
  }
  thead {
    display: table-header-group;
  }
  tr,
  img {
    page-break-inside: avoid;
  }
  img {
    max-width: 100% !important;
  }
  p,
  h2,
  h3 {
    orphans: 3;
    widows: 3;
  }
  h2,
  h3 {
    page-break-after: avoid;
  }
  .navbar {
    display: none;
  }
  .btn > .caret,
  .dropup > .btn > .caret {
    border-top-color: #000 !important;
  }
  .label {
    border: 1px solid #000;
  }
  .table {
    border-collapse: collapse !important;
  }
  .table td,
  .table th {
    background-color: #fff !important;
  }
  .table-bordered th,
  .table-bordered td {
    border: 1px solid #ddd !important;
  }
}
@font-face {
  font-family: 'Glyphicons Halflings';

  src: url('../fonts/glyphicons-halflings-regular.eot');
  src:
    url('../fonts/glyphicons-halflings-regular.eot?#iefix') format('embedded-opentype'),
    url('../fonts/glyphicons-halflings-regular.woff2') format('woff2'),
    url('../fonts/glyphicons-halflings-regular.woff') format('woff'),
    url('../fonts/glyphicons-halflings-regular.ttf') format('truetype'),
    url('../fonts/glyphicons-halflings-regular.svg#glyphicons_halflingsregular') format('svg');
}
.glyphicon {
  position: relative;
  top: 1px;
  display: inline-block;
  font-family: 'Glyphicons Halflings';
  font-style: normal;
  font-weight: normal;
  line-height: 1;

  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}
.glyphicon-asterisk:before {
  content: '\2a';
}
.glyphicon-plus:before {
  content: '\2b';
}
.glyphicon-euro:before,
.glyphicon-eur:before {
  content: '\20ac';
}
.glyphicon-minus:before {
  content: '\2212';
}
.glyphicon-cloud:before {
  content: '\2601';
}
.glyphicon-envelope:before {
  content: '\2709';
}
.glyphicon-pencil:before {
  content: '\270f';
}
.glyphicon-glass:before {
  content: '\e001';
}
.glyphicon-music:before {
  content: '\e002';
}
.glyphicon-search:before {
  content: '\e003';
}
.glyphicon-heart:before {
  content: '\e005';
}
.glyphicon-star:before {
  content: '\e006';
}
.glyphicon-star-empty:before {
  content: '\e007';
}
.glyphicon-user:before {
  content: '\e008';
}
.glyphicon-film:before {
  content: '\e009';
}
.glyphicon-th-large:before {
  content: '\e010';
}
.glyphicon-th:before {
  content: '\e011';
}
.glyphicon-th-list:before {
  content: '\e012';
}
.glyphicon-ok:before {
  content: '\e013';
}
.glyphicon-remove:before {
  content: '\e014';
}
.glyphicon-zoom-in:before {
  content: '\e015';
}
.glyphicon-zoom-out:before {
  content: '\e016';
}
.glyphicon-off:before {
  content: '\e017';
}
.glyphicon-signal:before {
  content: '\e018';
}
.glyphicon-cog:before {
  content: '\e019';
}
.glyphicon-trash:before {
  content: '\e020';
}
.glyphicon-home:before {
  content: '\e021';
}
.glyphicon-file:before {
  content: '\e022';
}
.glyphicon-time:before {
  content: '\e023';
}
.glyphicon-road:before {
  content: '\e024';
}
.glyphicon-download-alt:before {
  content: '\e025';
}
.glyphicon-download:before {
  content: '\e026';
}
.glyphicon-upload:before {
  content: '\e027';
}
.glyphicon-inbox:before {
  content: '\e028';
}
.glyphicon-play-circle:before {
  content: '\e029';
}
.glyphicon-repeat:before {
  content: '\e030';
}
.glyphicon-refresh:before {
  content: '\e031';
}
.glyphicon-list-alt:before {
  content: '\e032';
}
.glyphicon-lock:before {
  content: '\e033';
}
.glyphicon-flag:before {
  content: '\e034';
}
.glyphicon-headphones:before {
  content: '\e035';
}
.glyphicon-volume-off:before {
  content: '\e036';
}
.glyphicon-volume-down:before {
  content: '\e037';
}
.glyphicon-volume-up:before {
  content: '\e038';
}
.glyphicon-qrcode:before {
  content: '\e039';
}
.glyphicon-barcode:before {
  content: '\e040';
}
.glyphicon-tag:before {
  content: '\e041';
}
.glyphicon-tags:before {
  content: '\e042';
}
.glyphicon-book:before {
  content: '\e043';
}
.glyphicon-bookmark:before {
  content: '\e044';
}
.glyphicon-print:before {
  content: '\e045';
}
.glyphicon-camera:before {
  content: '\e046';
}
.glyphicon-font:before {
  content: '\e047';
}
.glyphicon-bold:before {
  content: '\e048';
}
.glyphicon-italic:before {
  content: '\e049';
}
.glyphicon-text-height:before {
  content: '\e050';
}
.glyphicon-text-width:before {
  content: '\e051';
}
.glyphicon-align-left:before {
  content: '\e052';
}
.glyphicon-align-center:before {
  content: '\e053';
}
.glyphicon-align-right:before {
  content: '\e054';
}
.glyphicon-align-justify:before {
  content: '\e055';
}
.glyphicon-list:before {
  content: '\e056';
}
.glyphicon-indent-left:before {
  content: '\e057';
}
.glyphicon-indent-right:before {
  content: '\e058';
}
.glyphicon-facetime-video:before {
  content: '\e059';
}
.glyphicon-picture:before {
  content: '\e060';
}
.glyphicon-map-marker:before {
  content: '\e062';
}
.glyphicon-adjust:before {
  content: '\e063';
}
.glyphicon-tint:before {
  content: '\e064';
}
.glyphicon-edit:before {
  content: '\e065';
}
.glyphicon-share:before {
  content: '\e066';
}
.glyphicon-check:before {
  content: '\e067';
}
.glyphicon-move:before {
  content: '\e068';
}
.glyphicon-step-backward:before {
  content: '\e069';
}
.glyphicon-fast-backward:before {
  content: '\e070';
}
.glyphicon-backward:before {
  content: '\e071';
}
.glyphicon-play:before {
  content: '\e072';
}
.glyphicon-pause:before {
  content: '\e073';
}
.glyphicon-stop:before {
  content: '\e074';
}
.glyphicon-forward:before {
  content: '\e075';
}
.glyphicon-fast-forward:before {
  content: '\e076';
}
.glyphicon-step-forward:before {
  content: '\e077';
}
.glyphicon-eject:before {
  content: '\e078';
}
.glyphicon-chevron-left:before {
  content: '\e079';
}
.glyphicon-chevron-right:before {
  content: '\e080';
}
.glyphicon-plus-sign:before {
  content: '\e081';
}
.glyphicon-minus-sign:before {
  content: '\e082';
}
.glyphicon-remove-sign:before {
  content: '\e083';
}
.glyphicon-ok-sign:before {
  content: '\e084';
}
.glyphicon-question-sign:before {
  content: '\e085';
}
.glyphicon-info-sign:before {
  content: '\e086';
}
.glyphicon-screenshot:before {
  content: '\e087';
}
.glyphicon-remove-circle:before {
  content: '\e088';
}
.glyphicon-ok-circle:before {
  content: '\e089';
}
.glyphicon-ban-circle:before {
  content: '\e090';
}
.glyphicon-arrow-left:before {
  content: '\e091';
}
.glyphicon-arrow-right:before {
  content: '\e092';
}
.glyphicon-arrow-up:before {
  content: '\e093';
}
.glyphicon-arrow-down:before {
  content: '\e094';
}
.glyphicon-share-alt:before {
  content: '\e095';
}
.glyphicon-resize-full:before {
  content: '\e096';
}
.glyphicon-resize-small:before {
  content: '\e097';
}
.glyphicon-exclamation-sign:before {
  content: '\e101';
}
.glyphicon-gift:before {
  content: '\e102';
}
.glyphicon-leaf:before {
  content: '\e103';
}
.glyphicon-fire:before {
  content: '\e104';
}
.glyphicon-eye-open:before {
  content: '\e105';
}
.glyphicon-eye-close:before {
  content: '\e106';
}
.glyphicon-warning-sign:before {
  content: '\e107';
}
.glyphicon-plane:before {
  content: '\e108';
}
.glyphicon-calendar:before {
  content: '\e109';
}
.glyphicon-random:before {
  content: '\e110';
}
.glyphicon-comment:before {
  content: '\e111';
}
.glyphicon-magnet:before {
  content: '\e112';
}
.glyphicon-chevron-up:before {
  content: '\e113';
}
.glyphicon-chevron-down:before {
  content: '\e114';
}
.glyphicon-retweet:before {
  content: '\e115';
}
.glyphicon-shopping-cart:before {
  content: '\e116';
}
.glyphicon-folder-close:before {
  content: '\e117';
}
.glyphicon-folder-open:before {
  content: '\e118';
}
.glyphicon-resize-vertical:before {
  content: '\e119';
}
.glyphicon-resize-horizontal:before {
  content: '\e120';
}
.glyphicon-hdd:before {
  content: '\e121';
}
.glyphicon-bullhorn:before {
  content: '\e122';
}
.glyphicon-bell:before {
  content: '\e123';
}
.glyphicon-certificate:before {
  content: '\e124';
}
.glyphicon-thumbs-up:before {
  content: '\e125';
}
.glyphicon-thumbs-down:before {
  content: '\e126';
}
.glyphicon-hand-right:before {
  content: '\e127';
}
.glyphicon-hand-left:before {
  content: '\e128';
}
.glyphicon-hand-up:before {
  content: '\e129';
}
.glyphicon-hand-down:before {
  content: '\e130';
}
.glyphicon-circle-arrow-right:before {
  content: '\e131';
}
.glyphicon-circle-arrow-left:before {
  content: '\e132';
}
.glyphicon-circle-arrow-up:before {
  content: '\e133';
}
.glyphicon-circle-arrow-down:before {
  content: '\e134';
}
.glyphicon-globe:before {
  content: '\e135';
}
.glyphicon-wrench:before {
  content: '\e136';
}
.glyphicon-tasks:before {
  content: '\e137';
}
.glyphicon-filter:before {
  content: '\e138';
}
.glyphicon-briefcase:before {
  content: '\e139';
}
.glyphicon-fullscreen:before {
  content: '\e140';
}
.glyphicon-dashboard:before {
  content: '\e141';
}
.glyphicon-paperclip:before {
  content: '\e142';
}
.glyphicon-heart-empty:before {
  content: '\e143';
}
.glyphicon-link:before {
  content: '\e144';
}
.glyphicon-phone:before {
  content: '\e145';
}
.glyphicon-pushpin:before {
  content: '\e146';
}
.glyphicon-usd:before {
  content: '\e148';
}
.glyphicon-gbp:before {
  content: '\e149';
}
.glyphicon-sort:before {
  content: '\e150';
}
.glyphicon-sort-by-alphabet:before {
  content: '\e151';
}
.glyphicon-sort-by-alphabet-alt:before {
  content: '\e152';
}
.glyphicon-sort-by-order:before {
  content: '\e153';
}
.glyphicon-sort-by-order-alt:before {
  content: '\e154';
}
.glyphicon-sort-by-attributes:before {
  content: '\e155';
}
.glyphicon-sort-by-attributes-alt:before {
  content: '\e156';
}
.glyphicon-unchecked:before {
  content: '\e157';
}
.glyphicon-expand:before {
  content: '\e158';
}
.glyphicon-collapse-down:before {
  content: '\e159';
}
.glyphicon-collapse-up:before {
  content: '\e160';
}
.glyphicon-log-in:before {
  content: '\e161';
}
.glyphicon-flash:before {
  content: '\e162';
}
.glyphicon-log-out:before {
  content: '\e163';
}
.glyphicon-new-window:before {
  content: '\e164';
}
.glyphicon-record:before {
  content: '\e165';
}
.glyphicon-save:before {
  content: '\e166';
}
.glyphicon-open:before {
  content: '\e167';
}
.glyphicon-saved:before {
  content: '\e168';
}
.glyphicon-import:before {
  content: '\e169';
}
.glyphicon-export:before {
  content: '\e170';
}
.glyphicon-send:before {
  content: '\e171';
}
.glyphicon-floppy-disk:before {
  content: '\e172';
}
.glyphicon-floppy-saved:before {
  content: '\e173';
}
.glyphicon-floppy-remove:before {
  content: '\e174';
}
.glyphicon-floppy-save:before {
  content: '\e175';
}
.glyphicon-floppy-open:before {
  content: '\e176';
}
.glyphicon-credit-card:before {
  content: '\e177';
}
.glyphicon-transfer:before {
  content: '\e178';
}
.glyphicon-cutlery:before {
  content: '\e179';
}
.glyphicon-header:before {
  content: '\e180';
}
.glyphicon-compressed:before {
  content: '\e181';
}
.glyphicon-earphone:before {
  content: '\e182';
}
.glyphicon-phone-alt:before {
  content: '\e183';
}
.glyphicon-tower:before {
  content: '\e184';
}
.glyphicon-stats:before {
  content: '\e185';
}
.glyphicon-sd-video:before {
  content: '\e186';
}
.glyphicon-hd-video:before {
  content: '\e187';
}
.glyphicon-subtitles:before {
  content: '\e188';
}
.glyphicon-sound-stereo:before {
  content: '\e189';
}
.glyphicon-sound-dolby:before {
  content: '\e190';
}
.glyphicon-sound-5-1:before {
  content: '\e191';
}
.glyphicon-sound-6-1:before {
  content: '\e192';
}
.glyphicon-sound-7-1:before {
  content: '\e193';
}
.glyphicon-copyright-mark:before {
  content: '\e194';
}
.glyphicon-registration-mark:before {
  content: '\e195';
}
.glyphicon-cloud-download:before {
  content: '\e197';
}
.glyphicon-cloud-upload:before {
  content: '\e198';
}
.glyphicon-tree-conifer:before {
  content: '\e199';
}
.glyphicon-tree-deciduous:before {
  content: '\e200';
}
.glyphicon-cd:before {
  content: '\e201';
}
.glyphicon-save-file:before {
  content: '\e202';
}
.glyphicon-open-file:before {
  content: '\e203';
}
.glyphicon-level-up:before {
  content: '\e204';
}
.glyphicon-copy:before {
  content: '\e205';
}
.glyphicon-paste:before {
  content: '\e206';
}
.glyphicon-alert:before {
  content: '\e209';
}
.glyphicon-equalizer:before {
  content: '\e210';
}
.glyphicon-king:before {
  content: '\e211';
}
.glyphicon-queen:before {
  content: '\e212';
}
.glyphicon-pawn:before {
  content: '\e213';
}
.glyphicon-bishop:before {
  content: '\e214';
}
.glyphicon-knight:before {
  content: '\e215';
}
.glyphicon-baby-formula:before {
  content: '\e216';
}
.glyphicon-tent:before {
  content: '\26fa';
}
.glyphicon-blackboard:before {
  content: '\e218';
}
.glyphicon-bed:before {
  content: '\e219';
}
.glyphicon-apple:before {
  content: '\f8ff';
}
.glyphicon-erase:before {
  content: '\e221';
}
.glyphicon-hourglass:before {
  content: '\231b';
}
.glyphicon-lamp:before {
  content: '\e223';
}
.glyphicon-duplicate:before {
  content: '\e224';
}
.glyphicon-piggy-bank:before {
  content: '\e225';
}
.glyphicon-scissors:before {
  content: '\e226';
}
.glyphicon-bitcoin:before {
  content: '\e227';
}
.glyphicon-btc:before {
  content: '\e227';
}
.glyphicon-xbt:before {
  content: '\e227';
}
.glyphicon-yen:before {
  content: '\00a5';
}
.glyphicon-jpy:before {
  content: '\00a5';
}
.glyphicon-ruble:before {
  content: '\20bd';
}
.glyphicon-rub:before {
  content: '\20bd';
}
.glyphicon-scale:before {
  content: '\e230';
}
.glyphicon-ice-lolly:before {
  content: '\e231';
}
.glyphicon-ice-lolly-tasted:before {
  content: '\e232';
}
.glyphicon-education:before {
  content: '\e233';
}
.glyphicon-option-horizontal:before {
  content: '\e234';
}
.glyphicon-option-vertical:before {
  content: '\e235';
}
.glyphicon-menu-hamburger:before {
  content: '\e236';
}
.glyphicon-modal-window:before {
  content: '\e237';
}
.glyphicon-oil:before {
  content: '\e238';
}
.glyphicon-grain:before {
  content: '\e239';
}
.glyphicon-sunglasses:before {
  content: '\e240';
}
.glyphicon-text-size:before {
  content: '\e241';
}
.glyphicon-text-color:before {
  content: '\e242';
}
.glyphicon-text-background:before {
  content: '\e243';
}
.glyphicon-object-align-top:before {
  content: '\e244';
}
.glyphicon-object-align-bottom:before {
  content: '\e245';
}
.glyphicon-object-align-horizontal:before {
  content: '\e246';
}
.glyphicon-object-align-left:before {
  content: '\e247';
}
.glyphicon-object-align-vertical:before {
  content: '\e248';
}
.glyphicon-object-align-right:before {
  content: '\e249';
}
.glyphicon-triangle-right:before {
  content: '\e250';
}
.glyphicon-triangle-left:before {
  content: '\e251';
}
.glyphicon-triangle-bottom:before {
  content: '\e252';
}
.glyphicon-triangle-top:before {
  content: '\e253';
}
.glyphicon-console:before {
  content: '\e254';
}
.glyphicon-superscript:before {
  content: '\e255';
}
.glyphicon-subscript:before {
  content: '\e256';
}
.glyphicon-menu-left:before {
  content: '\e257';
}
.glyphicon-menu-right:before {
  content: '\e258';
}
.glyphicon-menu-down:before {
  content: '\e259';
}
.glyphicon-menu-up:before {
  content: '\e260';
}
* {
  -webkit-box-sizing: border-box;
  -moz-box-sizing: border-box;
  box-sizing: border-box;
}
/* *:before,
*:after {
  -webkit-box-sizing: border-box;
  -moz-box-sizing: border-box;
  box-sizing: border-box;
} */
html {
  font-size: 10px;

  -webkit-tap-highlight-color: rgba(0, 0, 0, 0);
}

/* 사이드바 */

body {
  font-family: 'NanumSquare', sans-serif !important;
  font-size: 16px !important;
  line-height: 1.42857143 !important;
  color: #333 !important;
  background-color: #fff !important;
}
input,
button,
select,
textarea {
  font-family: inherit;
  font-size: inherit;
  line-height: inherit;
}
a {
  color: #337ab7;
  text-decoration: none;
}
a:hover,
a:focus {
  color: #23527c;
  text-decoration: underline;
}
a:focus {
  outline: thin dotted;
  outline: 5px auto -webkit-focus-ring-color;
  outline-offset: -2px;
}
figure {
  margin: 0;
}
img {
  vertical-align: middle;
}
.img-responsive,
.thumbnail > img,
.thumbnail a > img,
.carousel-inner > .item > img,
.carousel-inner > .item > a > img {
  display: block;
  max-width: 100%;
  height: auto;
}
.img-rounded {
  border-radius: 6px;
}
.img-thumbnail {
  display: inline-block;
  max-width: 100%;
  height: auto;
  padding: 4px;
  line-height: 1.42857143;
  background-color: #fff;
  border: 1px solid #ddd;
  border-radius: 4px;
  -webkit-transition: all 0.2s ease-in-out;
  -o-transition: all 0.2s ease-in-out;
  transition: all 0.2s ease-in-out;
}
.img-circle {
  border-radius: 50%;
}
hr {
  margin-top: 20px;
  margin-bottom: 20px;
  border: 0;
  border-top: 1px solid #eee;
}
.sr-only {
  position: absolute;
  width: 1px;
  height: 1px;
  padding: 0;
  margin: -1px;
  overflow: hidden;
  clip: rect(0, 0, 0, 0);
  border: 0;
}
.sr-only-focusable:active,
.sr-only-focusable:focus {
  position: static;
  width: auto;
  height: auto;
  margin: 0;
  overflow: visible;
  clip: auto;
}
[role='button'] {
  cursor: pointer;
}
h1,
h2,
h3,
h4,
h5,
h6,
.h1,
.h2,
.h3,
.h4,
.h5,
.h6 {
  font-family: inherit;
  font-weight: 500;
  line-height: 1.1;
  color: inherit;
}
h1 small,
h2 small,
h3 small,
h4 small,
h5 small,
h6 small,
.h1 small,
.h2 small,
.h3 small,
.h4 small,
.h5 small,
.h6 small,
h1 .small,
h2 .small,
h3 .small,
h4 .small,
h5 .small,
h6 .small,
.h1 .small,
.h2 .small,
.h3 .small,
.h4 .small,
.h5 .small,
.h6 .small {
  font-weight: normal;
  line-height: 1;
  color: #777;
}
h1,
.h1,
h2,
.h2,
h3,
.h3 {
  margin-top: 20px;
  margin-bottom: 10px;
}
h1 small,
.h1 small,
h2 small,
.h2 small,
h3 small,
.h3 small,
h1 .small,
.h1 .small,
h2 .small,
.h2 .small,
h3 .small,
.h3 .small {
  font-size: 65%;
}
h4,
.h4,
h5,
.h5,
h6,
.h6 {
  margin-top: 10px;
  margin-bottom: 10px;
}
h4 small,
.h4 small,
h5 small,
.h5 small,
h6 small,
.h6 small,
h4 .small,
.h4 .small,
h5 .small,
.h5 .small,
h6 .small,
.h6 .small {
  font-size: 75%;
}
h1,
.h1 {
  font-size: 36px;
}
h2,
.h2 {
  font-size: 30px;
}
h3,
.h3 {
  font-size: 24px;
}
h4,
.h4 {
  font-size: 18px;
}
h5,
.h5 {
  font-size: 14px;
}
h6,
.h6 {
  font-size: 12px;
}
p {
  margin: 0 0 10px;
}
.lead {
  margin-bottom: 20px;
  font-size: 16px;
  font-weight: 300;
  line-height: 1.4;
}
@media (min-width: 768px) {
  .lead {
    font-size: 21px;
  }
}
small,
.small {
  font-size: 85%;
}
mark,
.mark {
  padding: 0.2em;
  background-color: #fcf8e3;
}
.text-left {
  text-align: left;
}
.text-right {
  text-align: right;
}
.text-center {
  text-align: center;
}
.text-justify {
  text-align: justify;
}
.text-nowrap {
  white-space: nowrap;
}
.text-lowercase {
  text-transform: lowercase;
}
.text-uppercase {
  text-transform: uppercase;
}
.text-capitalize {
  text-transform: capitalize;
}
.text-muted {
  color: #777;
}
.text-primary {
  color: #337ab7;
}
a.text-primary:hover,
a.text-primary:focus {
  color: #286090;
}
.text-success {
  color: #3c763d;
}
a.text-success:hover,
a.text-success:focus {
  color: #2b542c;
}
.text-info {
  color: #31708f;
}
a.text-info:hover,
a.text-info:focus {
  color: #245269;
}
.text-warning {
  color: #8a6d3b;
}
a.text-warning:hover,
a.text-warning:focus {
  color: #66512c;
}
.text-danger {
  color: #a94442;
}
a.text-danger:hover,
a.text-danger:focus {
  color: #843534;
}
.bg-primary {
  color: #fff;
  background-color: #337ab7;
}
a.bg-primary:hover,
a.bg-primary:focus {
  background-color: #286090;
}
.bg-success {
  background-color: #dff0d8;
}
a.bg-success:hover,
a.bg-success:focus {
  background-color: #c1e2b3;
}
.bg-info {
  background-color: #d9edf7;
}
a.bg-info:hover,
a.bg-info:focus {
  background-color: #afd9ee;
}
.bg-warning {
  background-color: #fcf8e3;
}
a.bg-warning:hover,
a.bg-warning:focus {
  background-color: #f7ecb5;
}
.bg-danger {
  background-color: #f2dede;
}
a.bg-danger:hover,
a.bg-danger:focus {
  background-color: #e4b9b9;
}
.page-header {
  padding-bottom: 9px;
  margin: 40px 0 20px;
  border-bottom: 1px solid #eee;
}
ul,
ol {
  margin-top: 0;
  margin-bottom: 10px;
}
ul ul,
ol ul,
ul ol,
ol ol {
  margin-bottom: 0;
}
.list-unstyled {
  padding-left: 0;
  list-style: none;
}
.list-inline {
  padding-left: 0;
  margin-left: -5px;
  list-style: none;
}
.list-inline > li {
  display: inline-block;
  padding-right: 5px;
  padding-left: 5px;
}
dl {
  margin-top: 0;
  margin-bottom: 20px;
}
dt,
dd {
  line-height: 1.42857143;
}
dt {
  font-weight: normal;
}
dd {
  margin-left: 0;
}
@media (min-width: 768px) {
  .dl-horizontal dt {
    float: left;
    width: 160px;
    overflow: hidden;
    clear: left;
    text-align: right;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
  .dl-horizontal dd {
    margin-left: 180px;
  }
}
abbr[title],
abbr[data-original-title] {
  cursor: help;
  border-bottom: 1px dotted #777;
}
.initialism {
  font-size: 90%;
  text-transform: uppercase;
}
blockquote {
  padding: 10px 20px;
  margin: 0 0 20px;
  font-size: 17.5px;
  border-left: 5px solid #eee;
}
blockquote p:last-child,
blockquote ul:last-child,
blockquote ol:last-child {
  margin-bottom: 0;
}
blockquote footer,
blockquote small,
blockquote .small {
  display: block;
  font-size: 80%;
  line-height: 1.42857143;
  color: #777;
}
blockquote footer:before,
blockquote small:before,
blockquote .small:before {
  content: '\2014 \00A0';
}
.blockquote-reverse,
blockquote.pull-right {
  padding-right: 15px;
  padding-left: 0;
  text-align: right;
  border-right: 5px solid #eee;
  border-left: 0;
}
.blockquote-reverse footer:before,
blockquote.pull-right footer:before,
.blockquote-reverse small:before,
blockquote.pull-right small:before,
.blockquote-reverse .small:before,
blockquote.pull-right .small:before {
  content: '';
}
.blockquote-reverse footer:after,
blockquote.pull-right footer:after,
.blockquote-reverse small:after,
blockquote.pull-right small:after,
.blockquote-reverse .small:after,
blockquote.pull-right .small:after {
  content: '\00A0 \2014';
}
address {
  margin-bottom: 20px;
  font-style: normal;
  line-height: 1.42857143;
}
code,
kbd,
pre,
samp {
  font-family: Menlo, Monaco, Consolas, 'Courier New', monospace;
}
code {
  padding: 2px 4px;
  font-size: 90%;
  color: #c7254e;
  background-color: #f9f2f4;
  border-radius: 4px;
}
kbd {
  padding: 2px 4px;
  font-size: 90%;
  color: #fff;
  background-color: #333;
  border-radius: 3px;
  -webkit-box-shadow: inset 0 -1px 0 rgba(0, 0, 0, 0.25);
  box-shadow: inset 0 -1px 0 rgba(0, 0, 0, 0.25);
}
kbd kbd {
  padding: 0;
  font-size: 100%;
  font-weight: bold;
  -webkit-box-shadow: none;
  box-shadow: none;
}
pre {
  display: block;
  padding: 9.5px;
  margin: 0 0 10px;
  font-size: 13px;
  line-height: 1.42857143;
  color: #333;
  word-break: break-all;
  word-wrap: break-word;
  background-color: #f5f5f5;
  border: 1px solid #ccc;
  border-radius: 4px;
}
pre code {
  padding: 0;
  font-size: inherit;
  color: inherit;
  white-space: pre-wrap;
  background-color: transparent;
  border-radius: 0;
}
.pre-scrollable {
  max-height: 340px;
  overflow-y: scroll;
}
.container {
  padding-right: 15px;
  padding-left: 15px;
  margin-right: auto;
  margin-left: auto;
}
@media (min-width: 768px) {
  .container {
    width: 750px;
  }
}
@media (min-width: 992px) {
  .container {
    width: 970px;
  }
}
@media (min-width: 1200px) {
  .container {
    width: 1170px;
  }
}
.container-fluid {
  padding-right: 15px;
  padding-left: 15px;
  margin-right: auto;
  margin-left: auto;
}
.row {
  margin: 0 -15px;
}
.row-mod {
  margin-right: -15px;
  margin-left: -15px;
  height: 85px;
  margin-bottom: 20px;
}
.col-xs-1,
.col-sm-1,
.col-md-1,
.col-lg-1,
.col-xs-2,
.col-sm-2,
.col-md-2,
.col-lg-2,
.col-xs-3,
.col-sm-3,
.col-md-3,
.col-lg-3,
.col-xs-4,
.col-sm-4,
.col-md-4,
.col-lg-4,
.col-xs-5,
.col-sm-5,
.col-md-5,
.col-lg-5,
.col-xs-6,
.col-sm-6,
.col-md-6,
.col-lg-6,
.col-xs-7,
.col-sm-7,
.col-md-7,
.col-lg-7,
.col-xs-8,
.col-sm-8,
.col-md-8,
.col-lg-8,
.col-xs-9,
.col-sm-9,
.col-md-9,
.col-lg-9,
.col-xs-10,
.col-sm-10,
.col-md-10,
.col-lg-10,
.col-xs-11,
.col-sm-11,
.col-md-11,
.col-lg-11,
.col-xs-12,
.col-sm-12,
.col-md-12,
.col-lg-12 {
  position: relative;
  min-height: 1px;
  padding-right: 15px;
  padding-left: 15px;
}
.col-xs-1,
.col-xs-2,
.col-xs-3,
.col-xs-4,
.col-xs-5,
.col-xs-6,
.col-xs-7,
.col-xs-8,
.col-xs-9,
.col-xs-10,
.col-xs-11,
.col-xs-12 {
  float: left;
}
.col-xs-12 {
  width: 100%;
}
.col-xs-11 {
  width: 91.66666667%;
}
.col-xs-10 {
  width: 83.33333333%;
}
.col-xs-9 {
  width: 75%;
}
.col-xs-8 {
  width: 66.66666667%;
}
.col-xs-7 {
  width: 58.33333333%;
}
.col-xs-6 {
  width: 50%;
}
.col-xs-5 {
  width: 41.66666667%;
}
.col-xs-4 {
  width: 33.33333333%;
}
.col-xs-3 {
  width: 25%;
}
.col-xs-2 {
  width: 16.66666667%;
}
.col-xs-1 {
  width: 8.33333333%;
}
.col-xs-pull-12 {
  right: 100%;
}
.col-xs-pull-11 {
  right: 91.66666667%;
}
.col-xs-pull-10 {
  right: 83.33333333%;
}
.col-xs-pull-9 {
  right: 75%;
}
.col-xs-pull-8 {
  right: 66.66666667%;
}
.col-xs-pull-7 {
  right: 58.33333333%;
}
.col-xs-pull-6 {
  right: 50%;
}
.col-xs-pull-5 {
  right: 41.66666667%;
}
.col-xs-pull-4 {
  right: 33.33333333%;
}
.col-xs-pull-3 {
  right: 25%;
}
.col-xs-pull-2 {
  right: 16.66666667%;
}
.col-xs-pull-1 {
  right: 8.33333333%;
}
.col-xs-pull-0 {
  right: auto;
}
.col-xs-push-12 {
  left: 100%;
}
.col-xs-push-11 {
  left: 91.66666667%;
}
.col-xs-push-10 {
  left: 83.33333333%;
}
.col-xs-push-9 {
  left: 75%;
}
.col-xs-push-8 {
  left: 66.66666667%;
}
.col-xs-push-7 {
  left: 58.33333333%;
}
.col-xs-push-6 {
  left: 50%;
}
.col-xs-push-5 {
  left: 41.66666667%;
}
.col-xs-push-4 {
  left: 33.33333333%;
}
.col-xs-push-3 {
  left: 25%;
}
.col-xs-push-2 {
  left: 16.66666667%;
}
.col-xs-push-1 {
  left: 8.33333333%;
}
.col-xs-push-0 {
  left: auto;
}
.col-xs-offset-12 {
  margin-left: 100%;
}
.col-xs-offset-11 {
  margin-left: 91.66666667%;
}
.col-xs-offset-10 {
  margin-left: 83.33333333%;
}
.col-xs-offset-9 {
  margin-left: 75%;
}
.col-xs-offset-8 {
  margin-left: 66.66666667%;
}
.col-xs-offset-7 {
  margin-left: 58.33333333%;
}
.col-xs-offset-6 {
  margin-left: 50%;
}
.col-xs-offset-5 {
  margin-left: 41.66666667%;
}
.col-xs-offset-4 {
  margin-left: 33.33333333%;
}
.col-xs-offset-3 {
  margin-left: 25%;
}
.col-xs-offset-2 {
  margin-left: 16.66666667%;
}
.col-xs-offset-1 {
  margin-left: 8.33333333%;
}
.col-xs-offset-0 {
  margin-left: 0;
}
@media (min-width: 768px) {
  .col-sm-1,
  .col-sm-2,
  .col-sm-3,
  .col-sm-4,
  .col-sm-5,
  .col-sm-6,
  .col-sm-7,
  .col-sm-8,
  .col-sm-9,
  .col-sm-10,
  .col-sm-11,
  .col-sm-12 {
    float: left;
  }
  .col-sm-12 {
    width: 100%;
  }
  .col-sm-11 {
    width: 91.66666667%;
  }
  .col-sm-10 {
    width: 83.33333333%;
  }
  .col-sm-9 {
    width: 75%;
  }
  .col-sm-8 {
    width: 66.66666667%;
  }
  .col-sm-7 {
    width: 58.33333333%;
  }
  .col-sm-6 {
    width: 50%;
  }
  .col-sm-5 {
    width: 41.66666667%;
  }
  .col-sm-4 {
    width: 33.33333333%;
  }
  .col-sm-3 {
    width: 25%;
  }
  .col-sm-2 {
    width: 16.66666667%;
  }
  .col-sm-1 {
    width: 8.33333333%;
  }
  .col-sm-pull-12 {
    right: 100%;
  }
  .col-sm-pull-11 {
    right: 91.66666667%;
  }
  .col-sm-pull-10 {
    right: 83.33333333%;
  }
  .col-sm-pull-9 {
    right: 75%;
  }
  .col-sm-pull-8 {
    right: 66.66666667%;
  }
  .col-sm-pull-7 {
    right: 58.33333333%;
  }
  .col-sm-pull-6 {
    right: 50%;
  }
  .col-sm-pull-5 {
    right: 41.66666667%;
  }
  .col-sm-pull-4 {
    right: 33.33333333%;
  }
  .col-sm-pull-3 {
    right: 25%;
  }
  .col-sm-pull-2 {
    right: 16.66666667%;
  }
  .col-sm-pull-1 {
    right: 8.33333333%;
  }
  .col-sm-pull-0 {
    right: auto;
  }
  .col-sm-push-12 {
    left: 100%;
  }
  .col-sm-push-11 {
    left: 91.66666667%;
  }
  .col-sm-push-10 {
    left: 83.33333333%;
  }
  .col-sm-push-9 {
    left: 75%;
  }
  .col-sm-push-8 {
    left: 66.66666667%;
  }
  .col-sm-push-7 {
    left: 58.33333333%;
  }
  .col-sm-push-6 {
    left: 50%;
  }
  .col-sm-push-5 {
    left: 41.66666667%;
  }
  .col-sm-push-4 {
    left: 33.33333333%;
  }
  .col-sm-push-3 {
    left: 25%;
  }
  .col-sm-push-2 {
    left: 16.66666667%;
  }
  .col-sm-push-1 {
    left: 8.33333333%;
  }
  .col-sm-push-0 {
    left: auto;
  }
  .col-sm-offset-12 {
    margin-left: 100%;
  }
  .col-sm-offset-11 {
    margin-left: 91.66666667%;
  }
  .col-sm-offset-10 {
    margin-left: 83.33333333%;
  }
  .col-sm-offset-9 {
    margin-left: 75%;
  }
  .col-sm-offset-8 {
    margin-left: 66.66666667%;
  }
  .col-sm-offset-7 {
    margin-left: 58.33333333%;
  }
  .col-sm-offset-6 {
    margin-left: 50%;
  }
  .col-sm-offset-5 {
    margin-left: 41.66666667%;
  }
  .col-sm-offset-4 {
    margin-left: 33.33333333%;
  }
  .col-sm-offset-3 {
    margin-left: 25%;
  }
  .col-sm-offset-2 {
    margin-left: 16.66666667%;
  }
  .col-sm-offset-1 {
    margin-left: 8.33333333%;
  }
  .col-sm-offset-0 {
    margin-left: 0;
  }
}
@media (min-width: 992px) {
  .col-md-1,
  .col-md-2,
  .col-md-3,
  .col-md-4,
  .col-md-5,
  .col-md-6,
  .col-md-7,
  .col-md-8,
  .col-md-9,
  .col-md-10,
  .col-md-11,
  .col-md-12 {
    float: left;
  }
  .col-md-12 {
    width: 100%;
  }
  .col-md-11 {
    width: 91.66666667%;
  }
  .col-md-10 {
    width: 83.33333333%;
  }
  .col-md-9 {
    width: 75%;
  }
  .col-md-8 {
    width: 66.66666667%;
  }
  .col-md-7 {
    width: 58.33333333%;
  }
  .col-md-6 {
    width: 50%;
  }
  .col-md-5 {
    width: 41.66666667%;
  }
  .col-md-4 {
    width: 33.33333333%;
  }
  .col-md-3 {
    width: 25%;
  }
  .col-md-2 {
    width: 16.66666667%;
  }
  .col-md-1 {
    width: 8.33333333%;
  }
  .col-md-pull-12 {
    right: 100%;
  }
  .col-md-pull-11 {
    right: 91.66666667%;
  }
  .col-md-pull-10 {
    right: 83.33333333%;
  }
  .col-md-pull-9 {
    right: 75%;
  }
  .col-md-pull-8 {
    right: 66.66666667%;
  }
  .col-md-pull-7 {
    right: 58.33333333%;
  }
  .col-md-pull-6 {
    right: 50%;
  }
  .col-md-pull-5 {
    right: 41.66666667%;
  }
  .col-md-pull-4 {
    right: 33.33333333%;
  }
  .col-md-pull-3 {
    right: 25%;
  }
  .col-md-pull-2 {
    right: 16.66666667%;
  }
  .col-md-pull-1 {
    right: 8.33333333%;
  }
  .col-md-pull-0 {
    right: auto;
  }
  .col-md-push-12 {
    left: 100%;
  }
  .col-md-push-11 {
    left: 91.66666667%;
  }
  .col-md-push-10 {
    left: 83.33333333%;
  }
  .col-md-push-9 {
    left: 75%;
  }
  .col-md-push-8 {
    left: 66.66666667%;
  }
  .col-md-push-7 {
    left: 58.33333333%;
  }
  .col-md-push-6 {
    left: 50%;
  }
  .col-md-push-5 {
    left: 41.66666667%;
  }
  .col-md-push-4 {
    left: 33.33333333%;
  }
  .col-md-push-3 {
    left: 25%;
  }
  .col-md-push-2 {
    left: 16.66666667%;
  }
  .col-md-push-1 {
    left: 8.33333333%;
  }
  .col-md-push-0 {
    left: auto;
  }
  .col-md-offset-12 {
    margin-left: 100%;
  }
  .col-md-offset-11 {
    margin-left: 91.66666667%;
  }
  .col-md-offset-10 {
    margin-left: 83.33333333%;
  }
  .col-md-offset-9 {
    margin-left: 75%;
  }
  .col-md-offset-8 {
    margin-left: 66.66666667%;
  }
  .col-md-offset-7 {
    margin-left: 58.33333333%;
  }
  .col-md-offset-6 {
    margin-left: 50%;
  }
  .col-md-offset-5 {
    margin-left: 41.66666667%;
  }
  .col-md-offset-4 {
    margin-left: 33.33333333%;
  }
  .col-md-offset-3 {
    margin-left: 25%;
  }
  .col-md-offset-2 {
    margin-left: 16.66666667%;
  }
  .col-md-offset-1 {
    margin-left: 8.33333333%;
  }
  .col-md-offset-0 {
    margin-left: 0;
  }
}
@media (min-width: 1200px) {
  .col-lg-1,
  .col-lg-2,
  .col-lg-3,
  .col-lg-4,
  .col-lg-5,
  .col-lg-6,
  .col-lg-7,
  .col-lg-8,
  .col-lg-9,
  .col-lg-10,
  .col-lg-11,
  .col-lg-12 {
    float: left;
  }
  .col-lg-12 {
    width: 100%;
  }
  .col-lg-11 {
    width: 91.66666667%;
  }
  .col-lg-10 {
    width: 83.33333333%;
  }
  .col-lg-9 {
    width: 75%;
  }
  .col-lg-8 {
    width: 66.66666667%;
  }
  .col-lg-7 {
    width: 58.33333333%;
  }
  .col-lg-6 {
    width: 50%;
  }
  .col-lg-5 {
    width: 41.66666667%;
  }
  .col-lg-4 {
    width: 33.33333333%;
  }
  .col-lg-3 {
    width: 25%;
  }
  .col-lg-2 {
    width: 16.66666667%;
  }
  .col-lg-1 {
    width: 8.33333333%;
  }
  .col-lg-pull-12 {
    right: 100%;
  }
  .col-lg-pull-11 {
    right: 91.66666667%;
  }
  .col-lg-pull-10 {
    right: 83.33333333%;
  }
  .col-lg-pull-9 {
    right: 75%;
  }
  .col-lg-pull-8 {
    right: 66.66666667%;
  }
  .col-lg-pull-7 {
    right: 58.33333333%;
  }
  .col-lg-pull-6 {
    right: 50%;
  }
  .col-lg-pull-5 {
    right: 41.66666667%;
  }
  .col-lg-pull-4 {
    right: 33.33333333%;
  }
  .col-lg-pull-3 {
    right: 25%;
  }
  .col-lg-pull-2 {
    right: 16.66666667%;
  }
  .col-lg-pull-1 {
    right: 8.33333333%;
  }
  .col-lg-pull-0 {
    right: auto;
  }
  .col-lg-push-12 {
    left: 100%;
  }
  .col-lg-push-11 {
    left: 91.66666667%;
  }
  .col-lg-push-10 {
    left: 83.33333333%;
  }
  .col-lg-push-9 {
    left: 75%;
  }
  .col-lg-push-8 {
    left: 66.66666667%;
  }
  .col-lg-push-7 {
    left: 58.33333333%;
  }
  .col-lg-push-6 {
    left: 50%;
  }
  .col-lg-push-5 {
    left: 41.66666667%;
  }
  .col-lg-push-4 {
    left: 33.33333333%;
  }
  .col-lg-push-3 {
    left: 25%;
  }
  .col-lg-push-2 {
    left: 16.66666667%;
  }
  .col-lg-push-1 {
    left: 8.33333333%;
  }
  .col-lg-push-0 {
    left: auto;
  }
  .col-lg-offset-12 {
    margin-left: 100%;
  }
  .col-lg-offset-11 {
    margin-left: 91.66666667%;
  }
  .col-lg-offset-10 {
    margin-left: 83.33333333%;
  }
  .col-lg-offset-9 {
    margin-left: 75%;
  }
  .col-lg-offset-8 {
    margin-left: 66.66666667%;
  }
  .col-lg-offset-7 {
    margin-left: 58.33333333%;
  }
  .col-lg-offset-6 {
    margin-left: 50%;
  }
  .col-lg-offset-5 {
    margin-left: 41.66666667%;
  }
  .col-lg-offset-4 {
    margin-left: 33.33333333%;
  }
  .col-lg-offset-3 {
    margin-left: 25%;
  }
  .col-lg-offset-2 {
    margin-left: 16.66666667%;
  }
  .col-lg-offset-1 {
    margin-left: 8.33333333%;
  }
  .col-lg-offset-0 {
    margin-left: 0;
  }
}
table {
  background-color: transparent;
}
caption {
  padding-top: 8px;
  padding-bottom: 8px;
  color: #777;
  text-align: left;
}
th {
  text-align: left;
}
.table {
  width: 100%;
  max-width: 100%;
}
.table > thead > tr > th,
.table > tbody > tr > th,
.table > tfoot > tr > th,
.table > thead > tr > td,
.table > tbody > tr > td,
.table > tfoot > tr > td {
  padding: 4px;
  line-height: 1.42857143;
  vertical-align: center;
  border-top: 1px solid #ddd;
}
.table > thead > tr > th {
  vertical-align: bottom;
  border-bottom: 2px solid #ddd;
}
.table > caption + thead > tr:first-child > th,
.table > colgroup + thead > tr:first-child > th,
.table > thead:first-child > tr:first-child > th,
.table > caption + thead > tr:first-child > td,
.table > colgroup + thead > tr:first-child > td,
.table > thead:first-child > tr:first-child > td {
  border-top: 0;
}
.table > tbody + tbody {
  border-top: 2px solid #ddd;
}
.table .table {
  background-color: #fff;
}
.table-condensed > thead > tr > th,
.table-condensed > tbody > tr > th,
.table-condensed > tfoot > tr > th,
.table-condensed > thead > tr > td,
.table-condensed > tbody > tr > td,
.table-condensed > tfoot > tr > td {
  padding: 5px;
}
.table-bordered {
  border: 1px solid #ddd;
}
.table-bordered > thead > tr > th,
.table-bordered > tbody > tr > th,
.table-bordered > tfoot > tr > th,
.table-bordered > thead > tr > td,
.table-bordered > tbody > tr > td,
.table-bordered > tfoot > tr > td {
  border: 1px solid #ddd;
}
.table-bordered > thead > tr > th,
.table-bordered > thead > tr > td {
  border-bottom-width: 2px;
}
.table-striped > tbody > tr:nth-of-type(odd) {
  background-color: #f9f9f9;
}
.table-hover > tbody > tr:hover {
  background-color: #f5f5f5;
}
table col[class*='col-'] {
  position: static;
  display: table-column;
  float: none;
}
table td[class*='col-'],
table th[class*='col-'] {
  position: static;
  display: table-cell;
  float: none;
}
.table > thead > tr > td.active,
.table > tbody > tr > td.active,
.table > tfoot > tr > td.active,
.table > thead > tr > th.active,
.table > tbody > tr > th.active,
.table > tfoot > tr > th.active,
.table > thead > tr.active > td,
.table > tbody > tr.active > td,
.table > tfoot > tr.active > td,
.table > thead > tr.active > th,
.table > tbody > tr.active > th,
.table > tfoot > tr.active > th {
  background-color: #f5f5f5;
}
.table-hover > tbody > tr > td.active:hover,
.table-hover > tbody > tr > th.active:hover,
.table-hover > tbody > tr.active:hover > td,
.table-hover > tbody > tr:hover > .active,
.table-hover > tbody > tr.active:hover > th {
  background-color: #e8e8e8;
}
.table > thead > tr > td.success,
.table > tbody > tr > td.success,
.table > tfoot > tr > td.success,
.table > thead > tr > th.success,
.table > tbody > tr > th.success,
.table > tfoot > tr > th.success,
.table > thead > tr.success > td,
.table > tbody > tr.success > td,
.table > tfoot > tr.success > td,
.table > thead > tr.success > th,
.table > tbody > tr.success > th,
.table > tfoot > tr.success > th {
  background-color: #dff0d8;
}
.table-hover > tbody > tr > td.success:hover,
.table-hover > tbody > tr > th.success:hover,
.table-hover > tbody > tr.success:hover > td,
.table-hover > tbody > tr:hover > .success,
.table-hover > tbody > tr.success:hover > th {
  background-color: #d0e9c6;
}
.table > thead > tr > td.info,
.table > tbody > tr > td.info,
.table > tfoot > tr > td.info,
.table > thead > tr > th.info,
.table > tbody > tr > th.info,
.table > tfoot > tr > th.info,
.table > thead > tr.info > td,
.table > tbody > tr.info > td,
.table > tfoot > tr.info > td,
.table > thead > tr.info > th,
.table > tbody > tr.info > th,
.table > tfoot > tr.info > th {
  background-color: #d9edf7;
}
.table-hover > tbody > tr > td.info:hover,
.table-hover > tbody > tr > th.info:hover,
.table-hover > tbody > tr.info:hover > td,
.table-hover > tbody > tr:hover > .info,
.table-hover > tbody > tr.info:hover > th {
  background-color: #c4e3f3;
}
.table > thead > tr > td.warning,
.table > tbody > tr > td.warning,
.table > tfoot > tr > td.warning,
.table > thead > tr > th.warning,
.table > tbody > tr > th.warning,
.table > tfoot > tr > th.warning,
.table > thead > tr.warning > td,
.table > tbody > tr.warning > td,
.table > tfoot > tr.warning > td,
.table > thead > tr.warning > th,
.table > tbody > tr.warning > th,
.table > tfoot > tr.warning > th {
  background-color: #fcf8e3;
}
.table-hover > tbody > tr > td.warning:hover,
.table-hover > tbody > tr > th.warning:hover,
.table-hover > tbody > tr.warning:hover > td,
.table-hover > tbody > tr:hover > .warning,
.table-hover > tbody > tr.warning:hover > th {
  background-color: #faf2cc;
}
.table > thead > tr > td.danger,
.table > tbody > tr > td.danger,
.table > tfoot > tr > td.danger,
.table > thead > tr > th.danger,
.table > tbody > tr > th.danger,
.table > tfoot > tr > th.danger,
.table > thead > tr.danger > td,
.table > tbody > tr.danger > td,
.table > tfoot > tr.danger > td,
.table > thead > tr.danger > th,
.table > tbody > tr.danger > th,
.table > tfoot > tr.danger > th {
  background-color: #f2dede;
}
.table-hover > tbody > tr > td.danger:hover,
.table-hover > tbody > tr > th.danger:hover,
.table-hover > tbody > tr.danger:hover > td,
.table-hover > tbody > tr:hover > .danger,
.table-hover > tbody > tr.danger:hover > th {
  background-color: #ebcccc;
}
.table-responsive {
  min-height: 0.01%;
  overflow-x: auto;
}
@media screen and (max-width: 767px) {
  .table-responsive {
    width: 100%;
    margin-bottom: 15px;
    overflow-y: hidden;
    -ms-overflow-style: -ms-autohiding-scrollbar;
    border: 1px solid #ddd;
  }
  .table-responsive > .table {
    margin-bottom: 0;
  }
  .table-responsive > .table > thead > tr > th,
  .table-responsive > .table > tbody > tr > th,
  .table-responsive > .table > tfoot > tr > th,
  .table-responsive > .table > thead > tr > td,
  .table-responsive > .table > tbody > tr > td,
  .table-responsive > .table > tfoot > tr > td {
    white-space: nowrap;
  }
  .table-responsive > .table-bordered {
    border: 0;
  }
  .table-responsive > .table-bordered > thead > tr > th:first-child,
  .table-responsive > .table-bordered > tbody > tr > th:first-child,
  .table-responsive > .table-bordered > tfoot > tr > th:first-child,
  .table-responsive > .table-bordered > thead > tr > td:first-child,
  .table-responsive > .table-bordered > tbody > tr > td:first-child,
  .table-responsive > .table-bordered > tfoot > tr > td:first-child {
    border-left: 0;
  }
  .table-responsive > .table-bordered > thead > tr > th:last-child,
  .table-responsive > .table-bordered > tbody > tr > th:last-child,
  .table-responsive > .table-bordered > tfoot > tr > th:last-child,
  .table-responsive > .table-bordered > thead > tr > td:last-child,
  .table-responsive > .table-bordered > tbody > tr > td:last-child,
  .table-responsive > .table-bordered > tfoot > tr > td:last-child {
    border-right: 0;
  }
  .table-responsive > .table-bordered > tbody > tr:last-child > th,
  .table-responsive > .table-bordered > tfoot > tr:last-child > th,
  .table-responsive > .table-bordered > tbody > tr:last-child > td,
  .table-responsive > .table-bordered > tfoot > tr:last-child > td {
    border-bottom: 0;
  }
}
fieldset {
  min-width: 0;
  padding: 0;
  margin: 0;
  border: 0;
}
legend {
  display: block;
  width: 100%;
  padding: 0;
  margin-bottom: 20px;
  font-size: 21px;
  line-height: inherit;
  color: #333;
  border: 0;
  border-bottom: 1px solid #e5e5e5;
}
label {
  display: inline-block;
  max-width: 100%;
  margin-bottom: 5px;
  font-weight: bold;
}
input[type='search'] {
  -webkit-box-sizing: border-box;
  -moz-box-sizing: border-box;
  box-sizing: border-box;
}
input[type='radio'],
input[type='checkbox'] {
  margin: 4px 0 0;
  margin-top: 1px \9;
  line-height: normal;
}
input[type='file'] {
  display: block;
}
input[type='range'] {
  display: block;
  width: 100%;
}
select[multiple],
select[size] {
  height: auto;
}
input[type='file']:focus,
input[type='radio']:focus,
input[type='checkbox']:focus {
  outline: thin dotted;
  outline: 5px auto -webkit-focus-ring-color;
  outline-offset: -2px;
}
output {
  display: block;
  padding-top: 7px;
  font-size: 14px;
  line-height: 1.42857143;
  color: #555;
}
.form-control {
  display: block;
  width: 100%;
  height: 34px;
  padding: 6px 12px;
  font-size: 14px;
  line-height: 1.42857143;
  color: #555;
  background-color: #fff;
  background-image: none;
  border: 1px solid #ccc;
  border-radius: 4px;
  -webkit-box-shadow: inset 0 1px 1px rgba(0, 0, 0, 0.075);
  box-shadow: inset 0 1px 1px rgba(0, 0, 0, 0.075);
  -webkit-transition:
    border-color ease-in-out 0.15s,
    -webkit-box-shadow ease-in-out 0.15s;
  -o-transition:
    border-color ease-in-out 0.15s,
    box-shadow ease-in-out 0.15s;
  transition:
    border-color ease-in-out 0.15s,
    box-shadow ease-in-out 0.15s;
}
.form-control:focus {
  border-color: #767676;
  outline: 0;
  -webkit-box-shadow:
    inset 0 1px 1px rgba(0, 0, 0, 0.075),
    0 0 8px rgb(189, 189, 189);
  box-shadow:
    inset 0 1px 1px rgba(0, 0, 0, 0.075),
    0 0 8px rgb(189, 189, 189);
}
.form-control::-moz-placeholder {
  color: #999;
  opacity: 1;
}
.form-control:-ms-input-placeholder {
  color: #999;
}
.form-control::-webkit-input-placeholder {
  color: #999;
}
.form-control[disabled],
.form-control[readonly],
fieldset[disabled] .form-control {
  background-color: #eee;
  opacity: 1;
}
.form-control[disabled],
fieldset[disabled] .form-control {
  cursor: not-allowed;
}
textarea.form-control {
  height: auto;
}
input[type='search'] {
  -webkit-appearance: none;
}
@media screen and (-webkit-min-device-pixel-ratio: 0) {
  input[type='date'].form-control,
  input[type='time'].form-control,
  input[type='datetime-local'].form-control,
  input[type='month'].form-control {
    line-height: 34px;
  }
  input[type='date'].input-sm,
  input[type='time'].input-sm,
  input[type='datetime-local'].input-sm,
  input[type='month'].input-sm,
  .input-group-sm input[type='date'],
  .input-group-sm input[type='time'],
  .input-group-sm input[type='datetime-local'],
  .input-group-sm input[type='month'] {
    line-height: 30px;
  }
  input[type='date'].input-lg,
  input[type='time'].input-lg,
  input[type='datetime-local'].input-lg,
  input[type='month'].input-lg,
  .input-group-lg input[type='date'],
  .input-group-lg input[type='time'],
  .input-group-lg input[type='datetime-local'],
  .input-group-lg input[type='month'] {
    line-height: 46px;
  }
}
.form-group {
  margin-bottom: 15px;
}
.radio,
.checkbox {
  position: relative;
  display: block;
  margin-top: 10px;
  margin-bottom: 10px;
}
.radio label,
.checkbox label {
  min-height: 20px;
  padding-left: 20px;
  margin-bottom: 0;
  font-weight: normal;
  cursor: pointer;
}
.radio input[type='radio'],
.radio-inline input[type='radio'],
.checkbox input[type='checkbox'],
.checkbox-inline input[type='checkbox'] {
  position: absolute;
  margin-top: 4px \9;
  margin-left: -20px;
}
.radio + .radio,
.checkbox + .checkbox {
  margin-top: -5px;
}
.radio-inline,
.checkbox-inline {
  position: relative;
  display: inline-block;
  padding-left: 20px;
  margin-bottom: 0;
  font-weight: normal;
  vertical-align: middle;
  cursor: pointer;
}
.radio-inline + .radio-inline,
.checkbox-inline + .checkbox-inline {
  margin-top: 0;
  margin-left: 10px;
}
input[type='radio'][disabled],
input[type='checkbox'][disabled],
input[type='radio'].disabled,
input[type='checkbox'].disabled,
fieldset[disabled] input[type='radio'],
fieldset[disabled] input[type='checkbox'] {
  cursor: not-allowed;
}
.radio-inline.disabled,
.checkbox-inline.disabled,
fieldset[disabled] .radio-inline,
fieldset[disabled] .checkbox-inline {
  cursor: not-allowed;
}
.radio.disabled label,
.checkbox.disabled label,
fieldset[disabled] .radio label,
fieldset[disabled] .checkbox label {
  cursor: not-allowed;
}
.form-control-static {
  min-height: 34px;
  padding-top: 7px;
  padding-bottom: 7px;
  margin-bottom: 0;
}
.form-control-static.input-lg,
.form-control-static.input-sm {
  padding-right: 0;
  padding-left: 0;
}
.input-sm {
  height: 30px;
  padding: 5px 10px;
  font-size: 12px;
  line-height: 1.5;
  border-radius: 3px;
}
select.input-sm {
  height: 30px;
  line-height: 30px;
}
textarea.input-sm,
select[multiple].input-sm {
  height: auto;
}
.form-group-sm .form-control {
  height: 30px;
  padding: 5px 10px;
  font-size: 12px;
  line-height: 1.5;
  border-radius: 3px;
}
.form-group-sm select.form-control {
  height: 30px;
  line-height: 30px;
}
.form-group-sm textarea.form-control,
.form-group-sm select[multiple].form-control {
  height: auto;
}
.form-group-sm .form-control-static {
  height: 30px;
  min-height: 32px;
  padding: 6px 10px;
  font-size: 12px;
  line-height: 1.5;
}
.input-lg {
  height: 46px;
  padding: 10px 16px;
  font-size: 18px;
  line-height: 1.3333333;
  border-radius: 6px;
}
select.input-lg {
  height: 46px;
  line-height: 46px;
}
textarea.input-lg,
select[multiple].input-lg {
  height: auto;
}
.form-group-lg .form-control {
  height: 46px;
  padding: 10px 16px;
  font-size: 18px;
  line-height: 1.3333333;
  border-radius: 6px;
}
.form-group-lg select.form-control {
  height: 46px;
  line-height: 46px;
}
.form-group-lg textarea.form-control,
.form-group-lg select[multiple].form-control {
  height: auto;
}
.form-group-lg .form-control-static {
  height: 46px;
  min-height: 38px;
  padding: 11px 16px;
  font-size: 18px;
  line-height: 1.3333333;
}
.has-feedback {
  position: relative;
}
.has-feedback .form-control {
  padding-right: 42.5px;
}
.form-control-feedback {
  position: absolute;
  top: 0;
  right: 0;
  z-index: 2;
  display: block;
  width: 34px;
  height: 34px;
  line-height: 34px;
  text-align: center;
  pointer-events: none;
}
.input-lg + .form-control-feedback,
.input-group-lg + .form-control-feedback,
.form-group-lg .form-control + .form-control-feedback {
  width: 46px;
  height: 46px;
  line-height: 46px;
}
.input-sm + .form-control-feedback,
.input-group-sm + .form-control-feedback,
.form-group-sm .form-control + .form-control-feedback {
  width: 30px;
  height: 30px;
  line-height: 30px;
}
.has-success .help-block,
.has-success .control-label,
.has-success .radio,
.has-success .checkbox,
.has-success .radio-inline,
.has-success .checkbox-inline,
.has-success.radio label,
.has-success.checkbox label,
.has-success.radio-inline label,
.has-success.checkbox-inline label {
  color: #3c763d;
}
.has-success .form-control {
  border-color: #3c763d;
  -webkit-box-shadow: inset 0 1px 1px rgba(0, 0, 0, 0.075);
  box-shadow: inset 0 1px 1px rgba(0, 0, 0, 0.075);
}
.has-success .form-control:focus {
  border-color: #2b542c;
  -webkit-box-shadow:
    inset 0 1px 1px rgba(0, 0, 0, 0.075),
    0 0 6px #67b168;
  box-shadow:
    inset 0 1px 1px rgba(0, 0, 0, 0.075),
    0 0 6px #67b168;
}
.has-success .input-group-addon {
  color: #3c763d;
  background-color: #dff0d8;
  border-color: #3c763d;
}
.has-success .form-control-feedback {
  color: #3c763d;
}
.has-warning .help-block,
.has-warning .control-label,
.has-warning .radio,
.has-warning .checkbox,
.has-warning .radio-inline,
.has-warning .checkbox-inline,
.has-warning.radio label,
.has-warning.checkbox label,
.has-warning.radio-inline label,
.has-warning.checkbox-inline label {
  color: #8a6d3b;
}
.has-warning .form-control {
  border-color: #8a6d3b;
  -webkit-box-shadow: inset 0 1px 1px rgba(0, 0, 0, 0.075);
  box-shadow: inset 0 1px 1px rgba(0, 0, 0, 0.075);
}
.has-warning .form-control:focus {
  border-color: #66512c;
  -webkit-box-shadow:
    inset 0 1px 1px rgba(0, 0, 0, 0.075),
    0 0 6px #c0a16b;
  box-shadow:
    inset 0 1px 1px rgba(0, 0, 0, 0.075),
    0 0 6px #c0a16b;
}
.has-warning .input-group-addon {
  color: #8a6d3b;
  background-color: #fcf8e3;
  border-color: #8a6d3b;
}
.has-warning .form-control-feedback {
  color: #8a6d3b;
}
.has-error .help-block,
.has-error .control-label,
.has-error .radio,
.has-error .checkbox,
.has-error .radio-inline,
.has-error .checkbox-inline,
.has-error.radio label,
.has-error.checkbox label,
.has-error.radio-inline label,
.has-error.checkbox-inline label {
  color: #a94442;
}
.has-error .form-control {
  border-color: #a94442;
  -webkit-box-shadow: inset 0 1px 1px rgba(0, 0, 0, 0.075);
  box-shadow: inset 0 1px 1px rgba(0, 0, 0, 0.075);
}
.has-error .form-control:focus {
  border-color: #843534;
  -webkit-box-shadow:
    inset 0 1px 1px rgba(0, 0, 0, 0.075),
    0 0 6px #ce8483;
  box-shadow:
    inset 0 1px 1px rgba(0, 0, 0, 0.075),
    0 0 6px #ce8483;
}
.has-error .input-group-addon {
  color: #a94442;
  background-color: #f2dede;
  border-color: #a94442;
}
.has-error .form-control-feedback {
  color: #a94442;
}
.has-feedback label ~ .form-control-feedback {
  top: 25px;
}
.has-feedback label.sr-only ~ .form-control-feedback {
  top: 0;
}
.help-block {
  display: block;
  margin-top: 5px;
  margin-bottom: 10px;
  color: #737373;
}
@media (min-width: 768px) {
  .form-inline .form-group {
    display: inline-block;
    margin-bottom: 0;
    vertical-align: middle;
  }
  .form-inline .form-control {
    display: inline-block;
    width: auto;
    vertical-align: middle;
  }
  .form-inline .form-control-static {
    display: inline-block;
  }
  .form-inline .input-group {
    display: inline-table;
    vertical-align: middle;
  }
  .form-inline .input-group .input-group-addon,
  .form-inline .input-group .input-group-btn,
  .form-inline .input-group .form-control {
    width: auto;
  }
  .form-inline .input-group > .form-control {
    width: 100%;
  }
  .form-inline .control-label {
    margin-bottom: 0;
    vertical-align: middle;
  }
  .form-inline .radio,
  .form-inline .checkbox {
    display: inline-block;
    margin-top: 0;
    margin-bottom: 0;
    vertical-align: middle;
  }
  .form-inline .radio label,
  .form-inline .checkbox label {
    padding-left: 0;
  }
  .form-inline .radio input[type='radio'],
  .form-inline .checkbox input[type='checkbox'] {
    position: relative;
    margin-left: 0;
  }
  .form-inline .has-feedback .form-control-feedback {
    top: 0;
  }
}
.form-horizontal .radio,
.form-horizontal .checkbox,
.form-horizontal .radio-inline,
.form-horizontal .checkbox-inline {
  padding-top: 7px;
  margin-top: 0;
  margin-bottom: 0;
}
.form-horizontal .radio,
.form-horizontal .checkbox {
  min-height: 27px;
}
.form-horizontal .form-group {
  margin-right: -15px;
  margin-left: -15px;
}
@media (min-width: 768px) {
  .form-horizontal .control-label {
    padding-top: 7px;
    margin-bottom: 0;
    text-align: right;
  }
}
.form-horizontal .has-feedback .form-control-feedback {
  right: 15px;
}
@media (min-width: 768px) {
  .form-horizontal .form-group-lg .control-label {
    padding-top: 14.333333px;
    font-size: 18px;
  }
}
@media (min-width: 768px) {
  .form-horizontal .form-group-sm .control-label {
    padding-top: 6px;
    font-size: 12px;
  }
}
.btn {
  display: inline-block;
  padding: 6px 12px;
  margin-bottom: 0;
  font-size: 14px;
  font-weight: normal;
  line-height: 1.42857143;
  text-align: center;
  white-space: nowrap;
  vertical-align: middle;
  -ms-touch-action: manipulation;
  touch-action: manipulation;
  cursor: pointer;
  -webkit-user-select: none;
  -moz-user-select: none;
  -ms-user-select: none;
  user-select: none;
  background-image: none;
  border: 1px solid transparent;
  border-radius: 4px;
}
.btn:focus,
.btn:active:focus,
.btn.active:focus,
.btn.focus,
.btn:active.focus,
.btn.active.focus {
  outline: thin dotted;
  outline: 5px auto -webkit-focus-ring-color;
  outline-offset: -2px;
}
.btn:hover,
.btn:focus,
.btn.focus {
  color: #333;
  text-decoration: none;
}
.btn:active,
.btn.active {
  background-image: none;
  outline: 0;
  -webkit-box-shadow: inset 0 3px 5px rgba(0, 0, 0, 0.125);
  box-shadow: inset 0 3px 5px rgba(0, 0, 0, 0.125);
}
.btn.disabled,
.btn[disabled],
fieldset[disabled] .btn {
  cursor: not-allowed;
  filter: alpha(opacity=65);
  -webkit-box-shadow: none;
  box-shadow: none;
  opacity: 0.65;
}
a.btn.disabled,
fieldset[disabled] a.btn {
  pointer-events: none;
}
.btn-default {
  color: #333;
  background-color: #fff;
  border-color: #ccc;
}
.btn-default:focus,
.btn-default.focus {
  color: #333;
  background-color: #e6e6e6;
  border-color: #8c8c8c;
}
.btn-default:hover {
  color: #333;
  background-color: #e6e6e6;
  border-color: #adadad;
}
.btn-default:active,
.btn-default.active,
.open > .dropdown-toggle.btn-default {
  color: #333;
  background-color: #e6e6e6;
  border-color: #adadad;
}
.btn-default:active:hover,
.btn-default.active:hover,
.open > .dropdown-toggle.btn-default:hover,
.btn-default:active:focus,
.btn-default.active:focus,
.open > .dropdown-toggle.btn-default:focus,
.btn-default:active.focus,
.btn-default.active.focus,
.open > .dropdown-toggle.btn-default.focus {
  color: #333;
  background-color: #d4d4d4;
  border-color: #8c8c8c;
}
.btn-default:active,
.btn-default.active,
.open > .dropdown-toggle.btn-default {
  background-image: none;
}
.btn-default.disabled,
.btn-default[disabled],
fieldset[disabled] .btn-default,
.btn-default.disabled:hover,
.btn-default[disabled]:hover,
fieldset[disabled] .btn-default:hover,
.btn-default.disabled:focus,
.btn-default[disabled]:focus,
fieldset[disabled] .btn-default:focus,
.btn-default.disabled.focus,
.btn-default[disabled].focus,
fieldset[disabled] .btn-default.focus,
.btn-default.disabled:active,
.btn-default[disabled]:active,
fieldset[disabled] .btn-default:active,
.btn-default.disabled.active,
.btn-default[disabled].active,
fieldset[disabled] .btn-default.active {
  background-color: #fff;
  border-color: #ccc;
}
.btn-default .badge {
  color: #fff;
  background-color: #333;
}
.btn-primary {
  color: #fff;
  background-color: #000000;
  border-color: #767676;
  font-weight: bold;
}
.btn-primary:focus,
.btn-primary.focus {
  color: #fff;
  background-color: #286090;
  border-color: #122b40;
}
.btn-primary:hover {
  color: #fff;
  background-color: #b5d2ea;
  border-color: #204d74;
}
.btn-primary:active,
.btn-primary.active,
.open > .dropdown-toggle.btn-primary {
  color: #fff;
  background-color: #286090;
  border-color: #204d74;
}
.btn-primary:active:hover,
.btn-primary.active:hover,
.open > .dropdown-toggle.btn-primary:hover,
.btn-primary:active:focus,
.btn-primary.active:focus,
.open > .dropdown-toggle.btn-primary:focus,
.btn-primary:active.focus,
.btn-primary.active.focus,
.open > .dropdown-toggle.btn-primary.focus {
  color: #fff;
  background-color: #204d74;
  border-color: #122b40;
}
.btn-primary:active,
.btn-primary.active,
.open > .dropdown-toggle.btn-primary {
  background-image: none;
}
.btn-primary.disabled,
.btn-primary[disabled],
fieldset[disabled] .btn-primary,
.btn-primary.disabled:hover,
.btn-primary[disabled]:hover,
fieldset[disabled] .btn-primary:hover,
.btn-primary.disabled:focus,
.btn-primary[disabled]:focus,
fieldset[disabled] .btn-primary:focus,
.btn-primary.disabled.focus,
.btn-primary[disabled].focus,
fieldset[disabled] .btn-primary.focus,
.btn-primary.disabled:active,
.btn-primary[disabled]:active,
fieldset[disabled] .btn-primary:active,
.btn-primary.disabled.active,
.btn-primary[disabled].active,
fieldset[disabled] .btn-primary.active {
  background-color: #337ab7;
  border-color: #2e6da4;
}
.btn-primary .badge {
  color: #337ab7;
  background-color: #fff;
}
.btn-success {
  color: #fff;
  background-color: #5cb85c;
  border-color: #4cae4c;
}
.btn-success:focus,
.btn-success.focus {
  color: #fff;
  background-color: #449d44;
  border-color: #255625;
}
.btn-success:hover {
  color: #fff;
  background-color: #449d44;
  border-color: #398439;
}
.btn-success:active,
.btn-success.active,
.open > .dropdown-toggle.btn-success {
  color: #fff;
  background-color: #449d44;
  border-color: #398439;
}
.btn-success:active:hover,
.btn-success.active:hover,
.open > .dropdown-toggle.btn-success:hover,
.btn-success:active:focus,
.btn-success.active:focus,
.open > .dropdown-toggle.btn-success:focus,
.btn-success:active.focus,
.btn-success.active.focus,
.open > .dropdown-toggle.btn-success.focus {
  color: #fff;
  background-color: #398439;
  border-color: #255625;
}
.btn-success:active,
.btn-success.active,
.open > .dropdown-toggle.btn-success {
  background-image: none;
}
.btn-success.disabled,
.btn-success[disabled],
fieldset[disabled] .btn-success,
.btn-success.disabled:hover,
.btn-success[disabled]:hover,
fieldset[disabled] .btn-success:hover,
.btn-success.disabled:focus,
.btn-success[disabled]:focus,
fieldset[disabled] .btn-success:focus,
.btn-success.disabled.focus,
.btn-success[disabled].focus,
fieldset[disabled] .btn-success.focus,
.btn-success.disabled:active,
.btn-success[disabled]:active,
fieldset[disabled] .btn-success:active,
.btn-success.disabled.active,
.btn-success[disabled].active,
fieldset[disabled] .btn-success.active {
  background-color: #5cb85c;
  border-color: #4cae4c;
}
.btn-success .badge {
  color: #5cb85c;
  background-color: #fff;
}
.btn-info {
  color: #fff;
  background-color: #5bc0de;
  border-color: #46b8da;
}
.btn-info:focus,
.btn-info.focus {
  color: #fff;
  background-color: #31b0d5;
  border-color: #1b6d85;
}
.btn-info:hover {
  color: #fff;
  background-color: #31b0d5;
  border-color: #269abc;
}
.btn-info:active,
.btn-info.active,
.open > .dropdown-toggle.btn-info {
  color: #fff;
  background-color: #31b0d5;
  border-color: #269abc;
}
.btn-info:active:hover,
.btn-info.active:hover,
.open > .dropdown-toggle.btn-info:hover,
.btn-info:active:focus,
.btn-info.active:focus,
.open > .dropdown-toggle.btn-info:focus,
.btn-info:active.focus,
.btn-info.active.focus,
.open > .dropdown-toggle.btn-info.focus {
  color: #fff;
  background-color: #269abc;
  border-color: #1b6d85;
}
.btn-info:active,
.btn-info.active,
.open > .dropdown-toggle.btn-info {
  background-image: none;
}
.btn-info.disabled,
.btn-info[disabled],
fieldset[disabled] .btn-info,
.btn-info.disabled:hover,
.btn-info[disabled]:hover,
fieldset[disabled] .btn-info:hover,
.btn-info.disabled:focus,
.btn-info[disabled]:focus,
fieldset[disabled] .btn-info:focus,
.btn-info.disabled.focus,
.btn-info[disabled].focus,
fieldset[disabled] .btn-info.focus,
.btn-info.disabled:active,
.btn-info[disabled]:active,
fieldset[disabled] .btn-info:active,
.btn-info.disabled.active,
.btn-info[disabled].active,
fieldset[disabled] .btn-info.active {
  background-color: #5bc0de;
  border-color: #46b8da;
}
.btn-info .badge {
  color: #5bc0de;
  background-color: #fff;
}
.btn-warning {
  color: #fff;
  background-color: #f0ad4e;
  border-color: #eea236;
}
.btn-warning:focus,
.btn-warning.focus {
  color: #fff;
  background-color: #ec971f;
  border-color: #985f0d;
}
.btn-warning:hover {
  color: #fff;
  background-color: #ec971f;
  border-color: #d58512;
}
.btn-warning:active,
.btn-warning.active,
.open > .dropdown-toggle.btn-warning {
  color: #fff;
  background-color: #ec971f;
  border-color: #d58512;
}
.btn-warning:active:hover,
.btn-warning.active:hover,
.open > .dropdown-toggle.btn-warning:hover,
.btn-warning:active:focus,
.btn-warning.active:focus,
.open > .dropdown-toggle.btn-warning:focus,
.btn-warning:active.focus,
.btn-warning.active.focus,
.open > .dropdown-toggle.btn-warning.focus {
  color: #fff;
  background-color: #d58512;
  border-color: #985f0d;
}
.btn-warning:active,
.btn-warning.active,
.open > .dropdown-toggle.btn-warning {
  background-image: none;
}
.btn-warning.disabled,
.btn-warning[disabled],
fieldset[disabled] .btn-warning,
.btn-warning.disabled:hover,
.btn-warning[disabled]:hover,
fieldset[disabled] .btn-warning:hover,
.btn-warning.disabled:focus,
.btn-warning[disabled]:focus,
fieldset[disabled] .btn-warning:focus,
.btn-warning.disabled.focus,
.btn-warning[disabled].focus,
fieldset[disabled] .btn-warning.focus,
.btn-warning.disabled:active,
.btn-warning[disabled]:active,
fieldset[disabled] .btn-warning:active,
.btn-warning.disabled.active,
.btn-warning[disabled].active,
fieldset[disabled] .btn-warning.active {
  background-color: #f0ad4e;
  border-color: #eea236;
}
.btn-warning .badge {
  color: #f0ad4e;
  background-color: #fff;
}
.btn-danger {
  color: #fff;
  background-color: #e17a77;
  border-color: #d43f3a;
  font-weight: bold;
}
.btn-danger:focus,
.btn-danger.focus {
  color: #fff;
  background-color: #c9302c;
  border-color: #761c19;
}
.btn-danger:hover {
  color: #fff;
  background-color: #e99a98;
  border-color: #ac2925;
}
.btn-danger:active,
.btn-danger.active,
.open > .dropdown-toggle.btn-danger {
  color: #fff;
  background-color: #c9302c;
  border-color: #ac2925;
}
.btn-danger:active:hover,
.btn-danger.active:hover,
.open > .dropdown-toggle.btn-danger:hover,
.btn-danger:active:focus,
.btn-danger.active:focus,
.open > .dropdown-toggle.btn-danger:focus,
.btn-danger:active.focus,
.btn-danger.active.focus,
.open > .dropdown-toggle.btn-danger.focus {
  color: #fff;
  background-color: #ac2925;
  border-color: #761c19;
}
.btn-danger:active,
.btn-danger.active,
.open > .dropdown-toggle.btn-danger {
  background-image: none;
}
.btn-danger.disabled,
.btn-danger[disabled],
fieldset[disabled] .btn-danger,
.btn-danger.disabled:hover,
.btn-danger[disabled]:hover,
fieldset[disabled] .btn-danger:hover,
.btn-danger.disabled:focus,
.btn-danger[disabled]:focus,
fieldset[disabled] .btn-danger:focus,
.btn-danger.disabled.focus,
.btn-danger[disabled].focus,
fieldset[disabled] .btn-danger.focus,
.btn-danger.disabled:active,
.btn-danger[disabled]:active,
fieldset[disabled] .btn-danger:active,
.btn-danger.disabled.active,
.btn-danger[disabled].active,
fieldset[disabled] .btn-danger.active {
  background-color: #d9534f;
  border-color: #d43f3a;
}
.btn-danger .badge {
  color: #d9534f;
  background-color: #fff;
}
.btn-link {
  font-weight: normal;
  color: #337ab7;
  border-radius: 0;
}
.btn-link,
.btn-link:active,
.btn-link.active,
.btn-link[disabled],
fieldset[disabled] .btn-link {
  background-color: transparent;
  -webkit-box-shadow: none;
  box-shadow: none;
}
.btn-link,
.btn-link:hover,
.btn-link:focus,
.btn-link:active {
  border-color: transparent;
}
.btn-link:hover,
.btn-link:focus {
  color: #23527c;
  text-decoration: underline;
  background-color: transparent;
}
.btn-link[disabled]:hover,
fieldset[disabled] .btn-link:hover,
.btn-link[disabled]:focus,
fieldset[disabled] .btn-link:focus {
  color: #777;
  text-decoration: none;
}
.btn-lg,
.btn-group-lg > .btn {
  padding: 10px 16px;
  font-size: 18px;
  line-height: 1.3333333;
  border-radius: 6px;
}
.btn-sm,
.btn-group-sm > .btn {
  padding: 5px 10px;
  font-size: 12px;
  line-height: 1;
  border-radius: 3px;
}
.btn-xs,
.btn-group-xs > .btn {
  padding: 1px 5px;
  font-size: 12px;
  line-height: 1.5;
  border-radius: 3px;
}
.btn-block {
  display: block;
  width: 100%;
}
.btn-block + .btn-block {
  margin-top: 5px;
}
input[type='submit'].btn-block,
input[type='reset'].btn-block,
input[type='button'].btn-block {
  width: 100%;
}
.fade {
  opacity: 0;
  -webkit-transition: opacity 0.15s linear;
  -o-transition: opacity 0.15s linear;
  transition: opacity 0.15s linear;
}
.fade.in {
  opacity: 1;
}
.collapse {
  display: none;
}
.collapse.in {
  display: block;
}
tr.collapse.in {
  display: table-row;
}
tbody.collapse.in {
  display: table-row-group;
}
.collapsing {
  position: relative;
  height: 0;
  overflow: hidden;
  -webkit-transition-timing-function: ease;
  -o-transition-timing-function: ease;
  transition-timing-function: ease;
  -webkit-transition-duration: 0.35s;
  -o-transition-duration: 0.35s;
  transition-duration: 0.35s;
  -webkit-transition-property: height, visibility;
  -o-transition-property: height, visibility;
  transition-property: height, visibility;
}
.caret {
  display: inline-block;
  width: 0;
  height: 0;
  margin-left: 2px;
  vertical-align: middle;
  border-top: 4px dashed;
  border-top: 4px solid \9;
  border-right: 4px solid transparent;
  border-left: 4px solid transparent;
}
.dropup,
.dropdown {
  position: relative;
}
.dropdown-toggle:focus {
  outline: 0;
}
.dropdown-menu {
  position: absolute;
  top: 100%;
  left: 0;
  z-index: 1000;
  display: none;
  float: left;
  min-width: 160px;
  padding: 5px 0;
  margin: 2px 0 0;
  font-size: 14px;
  text-align: left;
  list-style: none;
  background-color: #fff;
  -webkit-background-clip: padding-box;
  background-clip: padding-box;
  border: 1px solid #ccc;
  border: 1px solid rgba(0, 0, 0, 0.15);
  border-radius: 4px;
  -webkit-box-shadow: 0 6px 12px rgba(0, 0, 0, 0.175);
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.175);
}
.dropdown-menu.pull-right {
  right: 0;
  left: auto;
}
.dropdown-menu .divider {
  height: 1px;
  margin: 9px 0;
  overflow: hidden;
  background-color: #e5e5e5;
}
.dropdown-menu > li > a {
  display: block;
  padding: 3px 20px;
  clear: both;
  font-weight: normal;
  line-height: 1.42857143;
  color: #333;
  white-space: nowrap;
}
.dropdown-menu > li > a:hover,
.dropdown-menu > li > a:focus {
  color: #262626;
  text-decoration: none;
  background-color: #f5f5f5;
}
.dropdown-menu > .active > a,
.dropdown-menu > .active > a:hover,
.dropdown-menu > .active > a:focus {
  color: #fff;
  text-decoration: none;
  background-color: #767676;
  outline: 0;
}
.dropdown-menu > .disabled > a,
.dropdown-menu > .disabled > a:hover,
.dropdown-menu > .disabled > a:focus {
  color: #777;
}
.dropdown-menu > .disabled > a:hover,
.dropdown-menu > .disabled > a:focus {
  text-decoration: none;
  cursor: not-allowed;
  background-color: transparent;
  background-image: none;
  filter: progid:DXImageTransform.Microsoft.gradient(enabled = false);
}
.open > .dropdown-menu {
  display: block;
}
.open > a {
  outline: 0;
}
.dropdown-menu-right {
  right: 0;
  left: auto;
}
.dropdown-menu-left {
  right: auto;
  left: 0;
}
.dropdown-header {
  display: block;
  padding: 3px 20px;
  font-size: 12px;
  line-height: 1.42857143;
  color: #777;
  white-space: nowrap;
}
.dropdown-backdrop {
  position: fixed;
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;
  z-index: 990;
}
.pull-right > .dropdown-menu {
  right: 0;
  left: auto;
}
.dropup .caret,
.navbar-fixed-bottom .dropdown .caret {
  content: '';
  border-top: 0;
  border-bottom: 4px dashed;
  border-bottom: 4px solid \9;
}
.dropup .dropdown-menu,
.navbar-fixed-bottom .dropdown .dropdown-menu {
  top: auto;
  bottom: 100%;
  margin-bottom: 2px;
}
@media (min-width: 768px) {
  .navbar-right .dropdown-menu {
    right: 0;
    left: auto;
  }
  .navbar-right .dropdown-menu-left {
    right: auto;
    left: 0;
  }
}
.btn-group,
.btn-group-vertical {
  position: relative;
  display: inline-block;
  vertical-align: middle;
}
.btn-group > .btn,
.btn-group-vertical > .btn {
  position: relative;
  float: left;
}
.btn-group > .btn:hover,
.btn-group-vertical > .btn:hover,
.btn-group > .btn:focus,
.btn-group-vertical > .btn:focus,
.btn-group > .btn:active,
.btn-group-vertical > .btn:active,
.btn-group > .btn.active,
.btn-group-vertical > .btn.active {
  z-index: 2;
}
.btn-group .btn + .btn,
.btn-group .btn + .btn-group,
.btn-group .btn-group + .btn,
.btn-group .btn-group + .btn-group {
  margin-left: -1px;
}
.btn-toolbar {
  margin-left: -5px;
}
.btn-toolbar .btn,
.btn-toolbar .btn-group,
.btn-toolbar .input-group {
  float: left;
}
.btn-toolbar > .btn,
.btn-toolbar > .btn-group,
.btn-toolbar > .input-group {
  margin-left: 5px;
}
.btn-group > .btn:not(:first-child):not(:last-child):not(.dropdown-toggle) {
  border-radius: 0;
}
.btn-group > .btn:first-child {
  margin-left: 0;
}
.btn-group > .btn:first-child:not(:last-child):not(.dropdown-toggle) {
  border-top-right-radius: 0;
  border-bottom-right-radius: 0;
}
.btn-group > .btn:last-child:not(:first-child),
.btn-group > .dropdown-toggle:not(:first-child) {
  border-top-left-radius: 0;
  border-bottom-left-radius: 0;
}
.btn-group > .btn-group {
  float: left;
}
.btn-group > .btn-group:not(:first-child):not(:last-child) > .btn {
  border-radius: 0;
}
.btn-group > .btn-group:first-child:not(:last-child) > .btn:last-child,
.btn-group > .btn-group:first-child:not(:last-child) > .dropdown-toggle {
  border-top-right-radius: 0;
  border-bottom-right-radius: 0;
}
.btn-group > .btn-group:last-child:not(:first-child) > .btn:first-child {
  border-top-left-radius: 0;
  border-bottom-left-radius: 0;
}
.btn-group .dropdown-toggle:active,
.btn-group.open .dropdown-toggle {
  outline: 0;
}
.btn-group > .btn + .dropdown-toggle {
  padding-right: 8px;
  padding-left: 8px;
}
.btn-group > .btn-lg + .dropdown-toggle {
  padding-right: 12px;
  padding-left: 12px;
}
.btn-group.open .dropdown-toggle {
  -webkit-box-shadow: inset 0 3px 5px rgba(0, 0, 0, 0.125);
  box-shadow: inset 0 3px 5px rgba(0, 0, 0, 0.125);
}
.btn-group.open .dropdown-toggle.btn-link {
  -webkit-box-shadow: none;
  box-shadow: none;
}
.btn .caret {
  margin-left: 0;
}
.btn-lg .caret {
  border-width: 5px 5px 0;
  border-bottom-width: 0;
}
.dropup .btn-lg .caret {
  border-width: 0 5px 5px;
}
.btn-group-vertical > .btn,
.btn-group-vertical > .btn-group,
.btn-group-vertical > .btn-group > .btn {
  display: block;
  float: none;
  width: 100%;
  max-width: 100%;
}
.btn-group-vertical > .btn-group > .btn {
  float: none;
}
.btn-group-vertical > .btn + .btn,
.btn-group-vertical > .btn + .btn-group,
.btn-group-vertical > .btn-group + .btn,
.btn-group-vertical > .btn-group + .btn-group {
  margin-top: -1px;
  margin-left: 0;
}
.btn-group-vertical > .btn:not(:first-child):not(:last-child) {
  border-radius: 0;
}
.btn-group-vertical > .btn:first-child:not(:last-child) {
  border-top-right-radius: 4px;
  border-bottom-right-radius: 0;
  border-bottom-left-radius: 0;
}
.btn-group-vertical > .btn:last-child:not(:first-child) {
  border-top-left-radius: 0;
  border-top-right-radius: 0;
  border-bottom-left-radius: 4px;
}
.btn-group-vertical > .btn-group:not(:first-child):not(:last-child) > .btn {
  border-radius: 0;
}
.btn-group-vertical > .btn-group:first-child:not(:last-child) > .btn:last-child,
.btn-group-vertical > .btn-group:first-child:not(:last-child) > .dropdown-toggle {
  border-bottom-right-radius: 0;
  border-bottom-left-radius: 0;
}
.btn-group-vertical > .btn-group:last-child:not(:first-child) > .btn:first-child {
  border-top-left-radius: 0;
  border-top-right-radius: 0;
}
.btn-group-justified {
  display: table;
  width: 100%;
  table-layout: fixed;
  border-collapse: separate;
}
.btn-group-justified > .btn,
.btn-group-justified > .btn-group {
  display: table-cell;
  float: none;
  width: 1%;
}
.btn-group-justified > .btn-group .btn {
  width: 100%;
}
.btn-group-justified > .btn-group .dropdown-menu {
  left: auto;
}
[data-toggle='buttons'] > .btn input[type='radio'],
[data-toggle='buttons'] > .btn-group > .btn input[type='radio'],
[data-toggle='buttons'] > .btn input[type='checkbox'],
[data-toggle='buttons'] > .btn-group > .btn input[type='checkbox'] {
  position: absolute;
  clip: rect(0, 0, 0, 0);
  pointer-events: none;
}
.input-group {
  position: relative;
  display: table;
  border-collapse: separate;
}
.input-group-mod {
  position: relative;
  display: table;
  border-collapse: separate;
  margin: 5px;
}
.input-group[class*='col-'] {
  float: none;
  padding-right: 0;
  padding-left: 0;
}
.input-group .form-control {
  position: relative;
  z-index: 2;
  float: left;
  width: 100%;
  margin-bottom: 0;
}
.input-group-lg > .form-control,
.input-group-lg > .input-group-addon,
.input-group-lg > .input-group-btn > .btn {
  height: 46px;
  padding: 10px 16px;
  font-size: 18px;
  line-height: 1.3333333;
  border-radius: 6px;
}
select.input-group-lg > .form-control,
select.input-group-lg > .input-group-addon,
select.input-group-lg > .input-group-btn > .btn {
  height: 46px;
  line-height: 46px;
}
textarea.input-group-lg > .form-control,
textarea.input-group-lg > .input-group-addon,
textarea.input-group-lg > .input-group-btn > .btn,
select[multiple].input-group-lg > .form-control,
select[multiple].input-group-lg > .input-group-addon,
select[multiple].input-group-lg > .input-group-btn > .btn {
  height: auto;
}
.input-group-sm > .form-control,
.input-group-sm > .input-group-addon,
.input-group-sm > .input-group-btn > .btn {
  height: 30px;
  padding: 5px 10px;
  font-size: 12px;
  line-height: 1.5;
  border-radius: 3px;
}
select.input-group-sm > .form-control,
select.input-group-sm > .input-group-addon,
select.input-group-sm > .input-group-btn > .btn {
  height: 30px;
  line-height: 30px;
}
textarea.input-group-sm > .form-control,
textarea.input-group-sm > .input-group-addon,
textarea.input-group-sm > .input-group-btn > .btn,
select[multiple].input-group-sm > .form-control,
select[multiple].input-group-sm > .input-group-addon,
select[multiple].input-group-sm > .input-group-btn > .btn {
  height: auto;
}
.input-group-addon,
.input-group-btn,
.input-group .form-control {
  display: table-cell;
}
.input-group-addon:not(:first-child):not(:last-child),
.input-group-btn:not(:first-child):not(:last-child),
.input-group .form-control:not(:first-child):not(:last-child) {
  border-radius: 0;
}
.input-group-addon,
.input-group-btn {
  width: 1%;
  white-space: nowrap;
  vertical-align: middle;
}
.input-group-addon {
  padding: 6px 12px;
  font-size: 14px;
  font-weight: normal;
  line-height: 1;
  color: #555;
  text-align: center;
  background-color: #eee;
  border: 1px solid #ccc;
  border-radius: 4px;
}
.input-group-addon.input-sm {
  padding: 5px 10px;
  font-size: 12px;
  border-radius: 3px;
}
.input-group-addon.input-lg {
  padding: 10px 16px;
  font-size: 18px;
  border-radius: 6px;
}
.input-group-addon input[type='radio'],
.input-group-addon input[type='checkbox'] {
  margin-top: 0;
}
.input-group .form-control:first-child,
.input-group-addon:first-child,
.input-group-btn:first-child > .btn,
.input-group-btn:first-child > .btn-group > .btn,
.input-group-btn:first-child > .dropdown-toggle,
.input-group-btn:last-child > .btn:not(:last-child):not(.dropdown-toggle),
.input-group-btn:last-child > .btn-group:not(:last-child) > .btn {
  border-top-right-radius: 0;
  border-bottom-right-radius: 0;
}
.input-group-addon:first-child {
  border-right: 0;
}
.input-group .form-control:last-child,
.input-group-addon:last-child,
.input-group-btn:last-child > .btn,
.input-group-btn:last-child > .btn-group > .btn,
.input-group-btn:last-child > .dropdown-toggle,
.input-group-btn:first-child > .btn:not(:first-child),
.input-group-btn:first-child > .btn-group:not(:first-child) > .btn {
  border-top-left-radius: 0;
  border-bottom-left-radius: 0;
}
.input-group-addon:last-child {
  border-left: 0;
}
.input-group-btn {
  position: relative;
  font-size: 0;
  white-space: nowrap;
}
.input-group-btn > .btn {
  position: relative;
}
.input-group-btn > .btn + .btn {
  margin-left: -1px;
}
.input-group-btn > .btn:hover,
.input-group-btn > .btn:focus,
.input-group-btn > .btn:active {
  z-index: 2;
}
.input-group-btn:first-child > .btn,
.input-group-btn:first-child > .btn-group {
  margin-right: -1px;
}
.input-group-btn:last-child > .btn,
.input-group-btn:last-child > .btn-group {
  z-index: 2;
  margin-left: -1px;
}
.nav {
  padding-left: 0;
  margin-bottom: 0;
  list-style: none;
}
.nav > li {
  position: relative;
  display: block;
}
.nav > li > a {
  position: relative;
  display: block;
  padding: 10px 15px;
}
.nav > li > a:hover,
.nav > li > a:focus {
  text-decoration: none;
  background-color: #eee;
}
.nav > li.disabled > a {
  color: #777;
}
.nav > li.disabled > a:hover,
.nav > li.disabled > a:focus {
  color: #777;
  text-decoration: none;
  cursor: not-allowed;
  background-color: transparent;
}
.nav .open > a,
.nav .open > a:hover,
.nav .open > a:focus {
  background-color: #eee;
  border-color: #337ab7;
}
.nav .nav-divider {
  height: 1px;
  margin: 9px 0;
  overflow: hidden;
  background-color: #e5e5e5;
}
.nav > li > a > img {
  max-width: none;
}
.nav-tabs {
  border-bottom: 1px solid #ddd;
}
.nav-tabs > li {
  float: left;
  margin-bottom: -1px;
}
.nav-tabs > li > a {
  margin-right: 2px;
  line-height: 1.42857143;
  border: 1px solid transparent;
  border-radius: 4px 4px 0 0;
}
.nav-tabs > li > a:hover {
  border-color: #eee #eee #ddd;
}
.nav-tabs > li.active > a,
.nav-tabs > li.active > a:hover,
.nav-tabs > li.active > a:focus {
  color: #555;
  cursor: default;
  background-color: #fff;
  border: 1px solid #ddd;
  border-bottom-color: transparent;
}
.nav-tabs.nav-justified {
  width: 100%;
  border-bottom: 0;
}
.nav-tabs.nav-justified > li {
  float: none;
}
.nav-tabs.nav-justified > li > a {
  margin-bottom: 5px;
  text-align: center;
}
.nav-tabs.nav-justified > .dropdown .dropdown-menu {
  top: auto;
  left: auto;
}
@media (min-width: 768px) {
  .nav-tabs.nav-justified > li {
    display: table-cell;
    width: 1%;
  }
  .nav-tabs.nav-justified > li > a {
    margin-bottom: 0;
  }
}
.nav-tabs.nav-justified > li > a {
  margin-right: 0;
  border-radius: 4px;
}
.nav-tabs.nav-justified > .active > a,
.nav-tabs.nav-justified > .active > a:hover,
.nav-tabs.nav-justified > .active > a:focus {
  border: 1px solid #ddd;
}
@media (min-width: 768px) {
  .nav-tabs.nav-justified > li > a {
    border-bottom: 1px solid #ddd;
    border-radius: 4px 4px 0 0;
  }
  .nav-tabs.nav-justified > .active > a,
  .nav-tabs.nav-justified > .active > a:hover,
  .nav-tabs.nav-justified > .active > a:focus {
    border-bottom-color: #fff;
  }
}
.nav-pills > li {
  float: left;
}
.nav-pills > li > a {
  border-radius: 4px;
}
.nav-pills > li + li {
  margin-left: 2px;
}
.nav-pills > li.active > a,
.nav-pills > li.active > a:hover,
.nav-pills > li.active > a:focus {
  color: #fff;
  background-color: #337ab7;
}
.nav-stacked > li {
  float: none;
}
.nav-stacked > li + li {
  margin-top: 2px;
  margin-left: 0;
}
.nav-justified {
  width: 100%;
}
.nav-justified > li {
  float: none;
}
.nav-justified > li > a {
  margin-bottom: 5px;
  text-align: center;
}
.nav-justified > .dropdown .dropdown-menu {
  top: auto;
  left: auto;
}
@media (min-width: 768px) {
  .nav-justified > li {
    display: table-cell;
    width: 1%;
  }
  .nav-justified > li > a {
    margin-bottom: 0;
  }
}
.nav-tabs-justified {
  border-bottom: 0;
}
.nav-tabs-justified > li > a {
  margin-right: 0;
  border-radius: 4px;
}
.nav-tabs-justified > .active > a,
.nav-tabs-justified > .active > a:hover,
.nav-tabs-justified > .active > a:focus {
  border: 1px solid #ddd;
}
@media (min-width: 768px) {
  .nav-tabs-justified > li > a {
    border-bottom: 1px solid #ddd;
    border-radius: 4px 4px 0 0;
  }
  .nav-tabs-justified > .active > a,
  .nav-tabs-justified > .active > a:hover,
  .nav-tabs-justified > .active > a:focus {
    border-bottom-color: #fff;
  }
}
.tab-content > .tab-pane {
  display: none;
}
.tab-content > .active {
  display: block;
}
.nav-tabs .dropdown-menu {
  margin-top: -1px;
  border-top-left-radius: 0;
  border-top-right-radius: 0;
}
.navbar {
  position: relative;
  min-height: 50px;
  margin-bottom: 20px;
  border: 1px solid transparent;
}
@media (min-width: 768px) {
  .navbar {
    border-radius: 4px;
  }
}
@media (min-width: 768px) {
  .navbar-header {
    float: left;
  }
}
.navbar-collapse {
  padding-right: 15px;
  padding-left: 15px;
  overflow-x: visible;
  -webkit-overflow-scrolling: touch;
  border-top: 1px solid transparent;
  -webkit-box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.1);
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.1);
}
.navbar-collapse.in {
  overflow-y: auto;
}
@media (min-width: 768px) {
  .navbar-collapse {
    width: auto;
    border-top: 0;
    -webkit-box-shadow: none;
    box-shadow: none;
  }
  .navbar-collapse.collapse {
    display: block !important;
    height: auto !important;
    padding-bottom: 0;
    overflow: visible !important;
  }
  .navbar-collapse.in {
    overflow-y: visible;
  }
  .navbar-fixed-top .navbar-collapse,
  .navbar-static-top .navbar-collapse,
  .navbar-fixed-bottom .navbar-collapse {
    padding-right: 0;
    padding-left: 0;
  }
}
.navbar-fixed-top .navbar-collapse,
.navbar-fixed-bottom .navbar-collapse {
  max-height: 340px;
}
@media (max-device-width: 480px) and (orientation: landscape) {
  .navbar-fixed-top .navbar-collapse,
  .navbar-fixed-bottom .navbar-collapse {
    max-height: 200px;
  }
}
.container > .navbar-header,
.container-fluid > .navbar-header,
.container > .navbar-collapse,
.container-fluid > .navbar-collapse {
  margin-right: -15px;
  margin-left: -15px;
}
@media (min-width: 768px) {
  .container > .navbar-header,
  .container-fluid > .navbar-header,
  .container > .navbar-collapse,
  .container-fluid > .navbar-collapse {
    margin-right: 0;
    margin-left: 0;
  }
}
.navbar-static-top {
  z-index: 1000;
  border-width: 0 0 1px;
}
@media (min-width: 768px) {
  .navbar-static-top {
    border-radius: 0;
  }
}
.navbar-fixed-top,
.navbar-fixed-bottom {
  position: fixed;
  right: 0;
  left: 0;
  z-index: 1030;
}
@media (min-width: 768px) {
  .navbar-fixed-top,
  .navbar-fixed-bottom {
    border-radius: 0;
  }
}
.navbar-fixed-top {
  top: 0;
  border-width: 0 0 1px;
}
.navbar-fixed-bottom {
  bottom: 0;
  margin-bottom: 0;
  border-width: 1px 0 0;
}
.navbar-brand {
  float: left;
  height: 50px;
  padding: 15px 15px;
  font-size: 18px;
  line-height: 20px;
}
.navbar-brand:hover,
.navbar-brand:focus {
  text-decoration: none;
}
.navbar-brand > img {
  display: block;
}
@media (min-width: 768px) {
  .navbar > .container .navbar-brand,
  .navbar > .container-fluid .navbar-brand {
    margin-left: -15px;
  }
}
.navbar-toggle {
  position: relative;
  float: right;
  padding: 9px 10px;
  margin-top: 8px;
  margin-right: 15px;
  margin-bottom: 8px;
  background-color: transparent;
  background-image: none;
  border: 1px solid transparent;
  border-radius: 4px;
}
.navbar-toggle:focus {
  outline: 0;
}
.navbar-toggle .icon-bar {
  display: block;
  width: 22px;
  height: 2px;
  border-radius: 1px;
}
.navbar-toggle .icon-bar + .icon-bar {
  margin-top: 4px;
}
@media (min-width: 768px) {
  .navbar-toggle {
    display: none;
  }
}
.navbar-nav {
  margin: 7.5px -15px;
}
.navbar-nav > li > a {
  padding: 25px 10px;
  line-height: 20px;
}
@media (max-width: 767px) {
  .navbar-nav .open .dropdown-menu {
    position: static;
    float: none;
    width: auto;
    margin-top: 0;
    background-color: transparent;
    border: 0;
    -webkit-box-shadow: none;
    box-shadow: none;
  }
  .navbar-nav .open .dropdown-menu > li > a,
  .navbar-nav .open .dropdown-menu .dropdown-header {
    padding: 5px 15px 5px 25px;
  }
  .navbar-nav .open .dropdown-menu > li > a {
    line-height: 20px;
  }
  .navbar-nav .open .dropdown-menu > li > a:hover,
  .navbar-nav .open .dropdown-menu > li > a:focus {
    background-image: none;
  }
}
@media (min-width: 768px) {
  .navbar-nav {
    float: left;
    margin: 0;
  }
  .navbar-nav > li {
    float: left;
  }
}
.navbar-form {
  padding: 10px 15px;
  margin: 8px -15px;
  border-top: 1px solid transparent;
  border-bottom: 1px solid transparent;
  -webkit-box-shadow:
    inset 0 1px 0 rgba(255, 255, 255, 0.1),
    0 1px 0 rgba(255, 255, 255, 0.1);
  box-shadow:
    inset 0 1px 0 rgba(255, 255, 255, 0.1),
    0 1px 0 rgba(255, 255, 255, 0.1);
}
@media (min-width: 768px) {
  .navbar-form .form-group {
    display: inline-block;
    margin-bottom: 0;
    vertical-align: middle;
  }
  .navbar-form .form-control {
    display: inline-block;
    width: auto;
    vertical-align: middle;
  }
  .navbar-form .form-control-static {
    display: inline-block;
  }
  .navbar-form .input-group {
    display: inline-table;
    vertical-align: middle;
  }
  .navbar-form .input-group .input-group-addon,
  .navbar-form .input-group .input-group-btn,
  .navbar-form .input-group .form-control {
    width: auto;
  }
  .navbar-form .input-group > .form-control {
    width: 100%;
  }
  .navbar-form .control-label {
    margin-bottom: 0;
    vertical-align: middle;
  }
  .navbar-form .radio,
  .navbar-form .checkbox {
    display: inline-block;
    margin-top: 0;
    margin-bottom: 0;
    vertical-align: middle;
  }
  .navbar-form .radio label,
  .navbar-form .checkbox label {
    padding-left: 0;
  }
  .navbar-form .radio input[type='radio'],
  .navbar-form .checkbox input[type='checkbox'] {
    position: relative;
    margin-left: 0;
  }
  .navbar-form .has-feedback .form-control-feedback {
    top: 0;
  }
}
@media (max-width: 767px) {
  .navbar-form .form-group {
    margin-bottom: 5px;
  }
  .navbar-form .form-group:last-child {
    margin-bottom: 0;
  }
}
@media (min-width: 768px) {
  .navbar-form {
    width: auto;
    padding-top: 0;
    padding-bottom: 0;
    margin-right: 0;
    margin-left: 0;
    border: 0;
    -webkit-box-shadow: none;
    box-shadow: none;
  }
}
.navbar-nav > li > .dropdown-menu {
  margin-top: 0;
  border-top-left-radius: 0;
  border-top-right-radius: 0;
}
.navbar-fixed-bottom .navbar-nav > li > .dropdown-menu {
  margin-bottom: 0;
  border-top-left-radius: 4px;
  border-top-right-radius: 4px;
  border-bottom-right-radius: 0;
  border-bottom-left-radius: 0;
}
.navbar-btn {
  margin-top: 8px;
  margin-bottom: 8px;
}
.navbar-btn.btn-sm {
  margin-top: 10px;
  margin-bottom: 10px;
}
.navbar-btn.btn-xs {
  margin-top: 14px;
  margin-bottom: 14px;
}
.navbar-text {
  margin-top: 15px;
  margin-bottom: 15px;
}
@media (min-width: 768px) {
  .navbar-text {
    float: left;
    margin-right: 15px;
    margin-left: 15px;
  }
}
@media (min-width: 768px) {
  .navbar-left {
    float: left !important;
  }
  .navbar-right {
    float: right !important;
    margin-right: -15px;
  }
  .navbar-right ~ .navbar-right {
    margin-right: 0;
  }
}
.navbar-default {
  background-color: #f8f8f8;
  border-color: #e7e7e7;
}
.navbar-default .navbar-brand {
  color: #777;
}
.navbar-default .navbar-brand:hover,
.navbar-default .navbar-brand:focus {
  color: #5e5e5e;
  background-color: transparent;
}
.navbar-default .navbar-text {
  color: #777;
}
.navbar-default .navbar-nav > li > a {
  color: #777;
}
.navbar-default .navbar-nav > li > a:hover,
.navbar-default .navbar-nav > li > a:focus {
  color: #333;
  background-color: transparent;
}
.navbar-default .navbar-nav > .active > a,
.navbar-default .navbar-nav > .active > a:hover,
.navbar-default .navbar-nav > .active > a:focus {
  color: #555;
  background-color: #e7e7e7;
}
.navbar-default .navbar-nav > .disabled > a,
.navbar-default .navbar-nav > .disabled > a:hover,
.navbar-default .navbar-nav > .disabled > a:focus {
  color: #ccc;
  background-color: transparent;
}
.navbar-default .navbar-toggle {
  border-color: #ddd;
}
.navbar-default .navbar-toggle:hover,
.navbar-default .navbar-toggle:focus {
  background-color: #ddd;
}
.navbar-default .navbar-toggle .icon-bar {
  background-color: #888;
}
.navbar-default .navbar-collapse,
.navbar-default .navbar-form {
  border-color: #e7e7e7;
}
.navbar-default .navbar-nav > .open > a,
.navbar-default .navbar-nav > .open > a:hover,
.navbar-default .navbar-nav > .open > a:focus {
  color: #555;
  background-color: #e7e7e7;
}
@media (max-width: 767px) {
  .navbar-default .navbar-nav .open .dropdown-menu > li > a {
    color: #777;
  }
  .navbar-default .navbar-nav .open .dropdown-menu > li > a:hover,
  .navbar-default .navbar-nav .open .dropdown-menu > li > a:focus {
    color: #333;
    background-color: transparent;
  }
  .navbar-default .navbar-nav .open .dropdown-menu > .active > a,
  .navbar-default .navbar-nav .open .dropdown-menu > .active > a:hover,
  .navbar-default .navbar-nav .open .dropdown-menu > .active > a:focus {
    color: #555;
    background-color: #e7e7e7;
  }
  .navbar-default .navbar-nav .open .dropdown-menu > .disabled > a,
  .navbar-default .navbar-nav .open .dropdown-menu > .disabled > a:hover,
  .navbar-default .navbar-nav .open .dropdown-menu > .disabled > a:focus {
    color: #ccc;
    background-color: transparent;
  }
}
.navbar-default .navbar-link {
  color: #777;
}
.navbar-default .navbar-link:hover {
  color: #333;
}
.navbar-default .btn-link {
  color: #777;
}
.navbar-default .btn-link:hover,
.navbar-default .btn-link:focus {
  color: #333;
}
.navbar-default .btn-link[disabled]:hover,
fieldset[disabled] .navbar-default .btn-link:hover,
.navbar-default .btn-link[disabled]:focus,
fieldset[disabled] .navbar-default .btn-link:focus {
  color: #ccc;
}
.navbar-inverse {
  background-color: #222;
  border-color: #080808;
}
.navbar-inverse .navbar-brand {
  color: #9d9d9d;
}
.navbar-inverse .navbar-brand:hover,
.navbar-inverse .navbar-brand:focus {
  color: #fff;
  background-color: transparent;
}
.navbar-inverse .navbar-text {
  color: #9d9d9d;
}
.navbar-inverse .navbar-nav > li > a {
  color: #9d9d9d;
}
.navbar-inverse .navbar-nav > li > a:hover,
.navbar-inverse .navbar-nav > li > a:focus {
  color: #fff;
  background-color: transparent;
}
.navbar-inverse .navbar-nav > .active > a,
.navbar-inverse .navbar-nav > .active > a:hover,
.navbar-inverse .navbar-nav > .active > a:focus {
  color: #fff;
  background-color: #080808;
}
.navbar-inverse .navbar-nav > .disabled > a,
.navbar-inverse .navbar-nav > .disabled > a:hover,
.navbar-inverse .navbar-nav > .disabled > a:focus {
  color: #444;
  background-color: transparent;
}
.navbar-inverse .navbar-toggle {
  border-color: #333;
}
.navbar-inverse .navbar-toggle:hover,
.navbar-inverse .navbar-toggle:focus {
  background-color: #333;
}
.navbar-inverse .navbar-toggle .icon-bar {
  background-color: #fff;
}
.navbar-inverse .navbar-collapse,
.navbar-inverse .navbar-form {
  border-color: #101010;
}
.navbar-inverse .navbar-nav > .open > a,
.navbar-inverse .navbar-nav > .open > a:hover,
.navbar-inverse .navbar-nav > .open > a:focus {
  color: #fff;
  background-color: #080808;
}
@media (max-width: 767px) {
  .navbar-inverse .navbar-nav .open .dropdown-menu > .dropdown-header {
    border-color: #080808;
  }
  .navbar-inverse .navbar-nav .open .dropdown-menu .divider {
    background-color: #080808;
  }
  .navbar-inverse .navbar-nav .open .dropdown-menu > li > a {
    color: #9d9d9d;
  }
  .navbar-inverse .navbar-nav .open .dropdown-menu > li > a:hover,
  .navbar-inverse .navbar-nav .open .dropdown-menu > li > a:focus {
    color: #fff;
    background-color: transparent;
  }
  .navbar-inverse .navbar-nav .open .dropdown-menu > .active > a,
  .navbar-inverse .navbar-nav .open .dropdown-menu > .active > a:hover,
  .navbar-inverse .navbar-nav .open .dropdown-menu > .active > a:focus {
    color: #fff;
    background-color: #080808;
  }
  .navbar-inverse .navbar-nav .open .dropdown-menu > .disabled > a,
  .navbar-inverse .navbar-nav .open .dropdown-menu > .disabled > a:hover,
  .navbar-inverse .navbar-nav .open .dropdown-menu > .disabled > a:focus {
    color: #444;
    background-color: transparent;
  }
}
.navbar-inverse .navbar-link {
  color: #9d9d9d;
}
.navbar-inverse .navbar-link:hover {
  color: #fff;
}
.navbar-inverse .btn-link {
  color: #9d9d9d;
}
.navbar-inverse .btn-link:hover,
.navbar-inverse .btn-link:focus {
  color: #fff;
}
.navbar-inverse .btn-link[disabled]:hover,
fieldset[disabled] .navbar-inverse .btn-link:hover,
.navbar-inverse .btn-link[disabled]:focus,
fieldset[disabled] .navbar-inverse .btn-link:focus {
  color: #444;
}
.breadcrumb {
  padding: 8px 15px;
  margin-bottom: 20px;
  list-style: none;
  background-color: #f5f5f5;
  border-radius: 4px;
}
.breadcrumb > li {
  display: inline-block;
  line-height: 40px;
}
.breadcrumb > li + li:before {
  padding: 0 5px;
  color: #ccc;
  content: '/\00a0';
}
.breadcrumb > .active {
  color: #777;
}
.pagination {
  display: inline-block;
  padding-left: 0;
  margin: 20px 0;
  border-radius: 4px;
}
.pagination > li {
  display: inline;
}
.pagination > li > a,
.pagination > li > span {
  position: relative;
  float: left;
  padding: 6px 12px;
  margin-left: -1px;
  line-height: 1.42857143;
  color: #767676;
  text-decoration: none;
  background-color: #fff;
  border: 1px solid #ddd;
}
.pagination > li:first-child > a,
.pagination > li:first-child > span {
  margin-left: 0;
  border-top-left-radius: 4px;
  border-bottom-left-radius: 4px;
}
.pagination > li:last-child > a,
.pagination > li:last-child > span {
  border-top-right-radius: 4px;
  border-bottom-right-radius: 4px;
}
.pagination > li > a:hover,
.pagination > li > span:hover,
.pagination > li > a:focus,
.pagination > li > span:focus {
  z-index: 3;
  color: #000000;
  background-color: #eee;
  border-color: #ddd;
}
.pagination > .active > a,
.pagination > .active > span,
.pagination > .active > a:hover,
.pagination > .active > span:hover,
.pagination > .active > a:focus,
.pagination > .active > span:focus {
  z-index: 2;
  color: #fff;
  cursor: default;
  background-color: #767676;
  border-color: #767676;
}
.pagination > .disabled > span,
.pagination > .disabled > span:hover,
.pagination > .disabled > span:focus,
.pagination > .disabled > a,
.pagination > .disabled > a:hover,
.pagination > .disabled > a:focus {
  color: #777;
  cursor: not-allowed;
  background-color: #fff;
  border-color: #ddd;
}
.pagination-lg > li > a,
.pagination-lg > li > span {
  padding: 10px 16px;
  font-size: 18px;
  line-height: 1.3333333;
}
.pagination-lg > li:first-child > a,
.pagination-lg > li:first-child > span {
  border-top-left-radius: 6px;
  border-bottom-left-radius: 6px;
}
.pagination-lg > li:last-child > a,
.pagination-lg > li:last-child > span {
  border-top-right-radius: 6px;
  border-bottom-right-radius: 6px;
}
.pagination-sm > li > a,
.pagination-sm > li > span {
  padding: 5px 10px;
  font-size: 12px;
  line-height: 1.5;
}
.pagination-sm > li:first-child > a,
.pagination-sm > li:first-child > span {
  border-top-left-radius: 3px;
  border-bottom-left-radius: 3px;
}
.pagination-sm > li:last-child > a,
.pagination-sm > li:last-child > span {
  border-top-right-radius: 3px;
  border-bottom-right-radius: 3px;
}
.pager {
  padding-left: 0;
  margin: 20px 0;
  text-align: center;
  list-style: none;
}
.pager li {
  display: inline;
}
.pager li > a,
.pager li > span {
  display: inline-block;
  padding: 5px 14px;
  background-color: #fff;
  border: 1px solid #ddd;
  border-radius: 15px;
}
.pager li > a:hover,
.pager li > a:focus {
  text-decoration: none;
  background-color: #eee;
}
.pager .next > a,
.pager .next > span {
  float: right;
}
.pager .previous > a,
.pager .previous > span {
  float: left;
}
.pager .disabled > a,
.pager .disabled > a:hover,
.pager .disabled > a:focus,
.pager .disabled > span {
  color: #777;
  cursor: not-allowed;
  background-color: #fff;
}
.label {
  display: inline;
  padding: 0.2em 0.6em 0.3em;
  font-size: 75%;
  font-weight: bold;
  line-height: 1;
  color: #fff;
  text-align: center;
  white-space: nowrap;
  vertical-align: baseline;
  border-radius: 0.25em;
}
a.label:hover,
a.label:focus {
  color: #fff;
  text-decoration: none;
  cursor: pointer;
}
.label:empty {
  display: none;
}
.btn .label {
  position: relative;
  top: -1px;
}
.label-default {
  background-color: #777;
}
.label-default[href]:hover,
.label-default[href]:focus {
  background-color: #5e5e5e;
}
.label-primary {
  background-color: #337ab7;
}
.label-primary[href]:hover,
.label-primary[href]:focus {
  background-color: #286090;
}
.label-success {
  background-color: #5cb85c;
}
.label-success[href]:hover,
.label-success[href]:focus {
  background-color: #449d44;
}
.label-info {
  background-color: #5bc0de;
}
.label-info[href]:hover,
.label-info[href]:focus {
  background-color: #31b0d5;
}
.label-warning {
  background-color: #f0ad4e;
}
.label-warning[href]:hover,
.label-warning[href]:focus {
  background-color: #ec971f;
}
.label-danger {
  background-color: #d9534f;
}
.label-danger[href]:hover,
.label-danger[href]:focus {
  background-color: #c9302c;
}
.badge {
  display: inline-block;
  min-width: 10px;
  padding: 3px 7px;
  font-size: 12px;
  font-weight: bold;
  line-height: 1;
  color: #fff;
  text-align: center;
  white-space: nowrap;
  vertical-align: middle;
  background-color: #777;
  border-radius: 10px;
}
.badge:empty {
  display: none;
}
.btn .badge {
  position: relative;
  top: -1px;
}
.btn-xs .badge,
.btn-group-xs > .btn .badge {
  top: 0;
  padding: 1px 5px;
}
a.badge:hover,
a.badge:focus {
  color: #fff;
  text-decoration: none;
  cursor: pointer;
}
.list-group-item.active > .badge,
.nav-pills > .active > a > .badge {
  color: #337ab7;
  background-color: #fff;
}
.list-group-item > .badge {
  float: right;
}
.list-group-item > .badge + .badge {
  margin-right: 5px;
}
.nav-pills > li > a > .badge {
  margin-left: 3px;
}
.jumbotron {
  padding-top: 30px;
  padding-bottom: 30px;
  margin-bottom: 30px;
  color: inherit;
  background-color: #eee;
}
.jumbotron h1,
.jumbotron .h1 {
  color: inherit;
}
.jumbotron p {
  margin-bottom: 15px;
  font-size: 21px;
  font-weight: 200;
}
.jumbotron > hr {
  border-top-color: #d5d5d5;
}
.container .jumbotron,
.container-fluid .jumbotron {
  border-radius: 6px;
}
.jumbotron .container {
  max-width: 100%;
}
@media screen and (min-width: 768px) {
  .jumbotron {
    padding-top: 48px;
    padding-bottom: 48px;
  }
  .container .jumbotron,
  .container-fluid .jumbotron {
    padding-right: 60px;
    padding-left: 60px;
  }
  .jumbotron h1,
  .jumbotron .h1 {
    font-size: 63px;
  }
}
.thumbnail {
  display: block;
  padding: 4px;
  margin-bottom: 20px;
  line-height: 1.42857143;
  background-color: #fff;
  border: 1px solid #ddd;
  border-radius: 4px;
  -webkit-transition: border 0.2s ease-in-out;
  -o-transition: border 0.2s ease-in-out;
  transition: border 0.2s ease-in-out;
}
.thumbnail > img,
.thumbnail a > img {
  margin-right: auto;
  margin-left: auto;
}
a.thumbnail:hover,
a.thumbnail:focus,
a.thumbnail.active {
  border-color: #337ab7;
}
.thumbnail .caption {
  padding: 9px;
  color: #333;
}
.alert {
  padding: 15px;
  margin-bottom: 20px;
  border: 1px solid transparent;
  border-radius: 4px;
}
.alert h4 {
  margin-top: 0;
  color: inherit;
}
.alert .alert-link {
  font-weight: bold;
}
.alert > p,
.alert > ul {
  margin-bottom: 0;
}
.alert > p + p {
  margin-top: 5px;
}
.alert-dismissable,
.alert-dismissible {
  padding-right: 35px;
}
.alert-dismissable .close,
.alert-dismissible .close {
  position: relative;
  top: -2px;
  right: -21px;
  color: inherit;
}
.alert-success {
  color: #3c763d;
  background-color: #dff0d8;
  border-color: #d6e9c6;
}
.alert-success hr {
  border-top-color: #c9e2b3;
}
.alert-success .alert-link {
  color: #2b542c;
}
.alert-info {
  color: #31708f;
  background-color: #d9edf7;
  border-color: #bce8f1;
}
.alert-info hr {
  border-top-color: #a6e1ec;
}
.alert-info .alert-link {
  color: #245269;
}
.alert-warning {
  color: #8a6d3b;
  background-color: #fcf8e3;
  border-color: #faebcc;
}
.alert-warning hr {
  border-top-color: #f7e1b5;
}
.alert-warning .alert-link {
  color: #66512c;
}
.alert-danger {
  color: #a94442;
  background-color: #f2dede;
  border-color: #ebccd1;
}
.alert-danger hr {
  border-top-color: #e4b9c0;
}
.alert-danger .alert-link {
  color: #843534;
}
@-webkit-keyframes progress-bar-stripes {
  from {
    background-position: 40px 0;
  }
  to {
    background-position: 0 0;
  }
}
@-o-keyframes progress-bar-stripes {
  from {
    background-position: 40px 0;
  }
  to {
    background-position: 0 0;
  }
}
@keyframes progress-bar-stripes {
  from {
    background-position: 40px 0;
  }
  to {
    background-position: 0 0;
  }
}
.progress {
  height: 20px;
  margin-bottom: 20px;
  overflow: hidden;
  position: relative;
  background-color: #f5f5f5;
  border-radius: 4px;
  -webkit-box-shadow: inset 0 1px 2px rgba(0, 0, 0, 0.1);
  box-shadow: inset 0 1px 2px rgba(0, 0, 0, 0.1);
}
.progress-bar {
  float: left;
  width: 0;
  height: 100%;
  font-size: 12px;
  line-height: 20px;
  color: #fff;
  text-align: center;
  background-color: #337ab7;
  -webkit-box-shadow: inset 0 -1px 0 rgba(0, 0, 0, 0.15);
  box-shadow: inset 0 -1px 0 rgba(0, 0, 0, 0.15);
  -webkit-transition: width 0.6s ease;
  -o-transition: width 0.6s ease;
  transition: width 0.6s ease;
}
.progress-striped .progress-bar,
.progress-bar-striped {
  background-image: -webkit-linear-gradient(
    45deg,
    rgba(255, 255, 255, 0.15) 25%,
    transparent 25%,
    transparent 50%,
    rgba(255, 255, 255, 0.15) 50%,
    rgba(255, 255, 255, 0.15) 75%,
    transparent 75%,
    transparent
  );
  background-image: -o-linear-gradient(
    45deg,
    rgba(255, 255, 255, 0.15) 25%,
    transparent 25%,
    transparent 50%,
    rgba(255, 255, 255, 0.15) 50%,
    rgba(255, 255, 255, 0.15) 75%,
    transparent 75%,
    transparent
  );
  background-image: linear-gradient(
    45deg,
    rgba(255, 255, 255, 0.15) 25%,
    transparent 25%,
    transparent 50%,
    rgba(255, 255, 255, 0.15) 50%,
    rgba(255, 255, 255, 0.15) 75%,
    transparent 75%,
    transparent
  );
  -webkit-background-size: 40px 40px;
  background-size: 40px 40px;
}
.progress.active .progress-bar,
.progress-bar.active {
  -webkit-animation: progress-bar-stripes 2s linear infinite;
  -o-animation: progress-bar-stripes 2s linear infinite;
  animation: progress-bar-stripes 2s linear infinite;
}
.progress-bar-success {
  background-color: #5cb85c;
}
.progress-striped .progress-bar-success {
  background-image: -webkit-linear-gradient(
    45deg,
    rgba(255, 255, 255, 0.15) 25%,
    transparent 25%,
    transparent 50%,
    rgba(255, 255, 255, 0.15) 50%,
    rgba(255, 255, 255, 0.15) 75%,
    transparent 75%,
    transparent
  );
  background-image: -o-linear-gradient(
    45deg,
    rgba(255, 255, 255, 0.15) 25%,
    transparent 25%,
    transparent 50%,
    rgba(255, 255, 255, 0.15) 50%,
    rgba(255, 255, 255, 0.15) 75%,
    transparent 75%,
    transparent
  );
  background-image: linear-gradient(
    45deg,
    rgba(255, 255, 255, 0.15) 25%,
    transparent 25%,
    transparent 50%,
    rgba(255, 255, 255, 0.15) 50%,
    rgba(255, 255, 255, 0.15) 75%,
    transparent 75%,
    transparent
  );
}
.progress-bar-info {
  background-color: #7c9dbc;
}
.progress-striped .progress-bar-info {
  background-image: -webkit-linear-gradient(
    45deg,
    rgba(255, 255, 255, 0.15) 25%,
    transparent 25%,
    transparent 50%,
    rgba(255, 255, 255, 0.15) 50%,
    rgba(255, 255, 255, 0.15) 75%,
    transparent 75%,
    transparent
  );
  background-image: -o-linear-gradient(
    45deg,
    rgba(255, 255, 255, 0.15) 25%,
    transparent 25%,
    transparent 50%,
    rgba(255, 255, 255, 0.15) 50%,
    rgba(255, 255, 255, 0.15) 75%,
    transparent 75%,
    transparent
  );
  background-image: linear-gradient(
    45deg,
    rgba(255, 255, 255, 0.15) 25%,
    transparent 25%,
    transparent 50%,
    rgba(255, 255, 255, 0.15) 50%,
    rgba(255, 255, 255, 0.15) 75%,
    transparent 75%,
    transparent
  );
}
.progress-bar-warning {
  background-color: #f0ad4e;
}
.progress-striped .progress-bar-warning {
  background-image: -webkit-linear-gradient(
    45deg,
    rgba(255, 255, 255, 0.15) 25%,
    transparent 25%,
    transparent 50%,
    rgba(255, 255, 255, 0.15) 50%,
    rgba(255, 255, 255, 0.15) 75%,
    transparent 75%,
    transparent
  );
  background-image: -o-linear-gradient(
    45deg,
    rgba(255, 255, 255, 0.15) 25%,
    transparent 25%,
    transparent 50%,
    rgba(255, 255, 255, 0.15) 50%,
    rgba(255, 255, 255, 0.15) 75%,
    transparent 75%,
    transparent
  );
  background-image: linear-gradient(
    45deg,
    rgba(255, 255, 255, 0.15) 25%,
    transparent 25%,
    transparent 50%,
    rgba(255, 255, 255, 0.15) 50%,
    rgba(255, 255, 255, 0.15) 75%,
    transparent 75%,
    transparent
  );
}
.progress-bar-danger {
  background-color: #d9534f;
}
.progress-striped .progress-bar-danger {
  background-image: -webkit-linear-gradient(
    45deg,
    rgba(255, 255, 255, 0.15) 25%,
    transparent 25%,
    transparent 50%,
    rgba(255, 255, 255, 0.15) 50%,
    rgba(255, 255, 255, 0.15) 75%,
    transparent 75%,
    transparent
  );
  background-image: -o-linear-gradient(
    45deg,
    rgba(255, 255, 255, 0.15) 25%,
    transparent 25%,
    transparent 50%,
    rgba(255, 255, 255, 0.15) 50%,
    rgba(255, 255, 255, 0.15) 75%,
    transparent 75%,
    transparent
  );
  background-image: linear-gradient(
    45deg,
    rgba(255, 255, 255, 0.15) 25%,
    transparent 25%,
    transparent 50%,
    rgba(255, 255, 255, 0.15) 50%,
    rgba(255, 255, 255, 0.15) 75%,
    transparent 75%,
    transparent
  );
}
.progress > .progress-completed {
  position: absolute;
  right: 0px;
  font-weight: 800;
  color: #000;
  padding: 0px 10px 1px;
}
.progress-completed {
  font-size: 12px;
}
.media {
  margin-top: 15px;
}
.media:first-child {
  margin-top: 0;
}
.media,
.media-body {
  overflow: hidden;
  zoom: 1;
}
.media-body {
  width: 10000px;
}
.media-object {
  display: block;
}
.media-object.img-thumbnail {
  max-width: none;
}
.media-right,
.media > .pull-right {
  padding-left: 10px;
}
.media-left,
.media > .pull-left {
  padding-right: 10px;
}
.media-left,
.media-right,
.media-body {
  display: table-cell;
  vertical-align: top;
}
.media-middle {
  vertical-align: middle;
}
.media-bottom {
  vertical-align: bottom;
}
.media-heading {
  margin-top: 0;
  margin-bottom: 5px;
}
.media-list {
  padding-left: 0;
  list-style: none;
}
.list-group {
  padding-left: 0;
  margin-bottom: 20px;
}
.list-group-item {
  position: relative;
  display: block;
  padding: 10px 15px;
  margin-bottom: -1px;
  background-color: #fff;
  border: 1px solid #ddd;
}
.list-group-item:first-child {
  border-top-left-radius: 4px;
  border-top-right-radius: 4px;
}
.list-group-item:last-child {
  margin-bottom: 0;
  border-bottom-right-radius: 4px;
  border-bottom-left-radius: 4px;
}
a.list-group-item,
button.list-group-item {
  color: #555;
}
a.list-group-item .list-group-item-heading,
button.list-group-item .list-group-item-heading {
  color: #333;
}
a.list-group-item:hover,
button.list-group-item:hover,
a.list-group-item:focus,
button.list-group-item:focus {
  color: #555;
  text-decoration: none;
  background-color: #f5f5f5;
}
button.list-group-item {
  width: 100%;
  text-align: left;
}
.list-group-item.disabled,
.list-group-item.disabled:hover,
.list-group-item.disabled:focus {
  color: #777;
  cursor: not-allowed;
  background-color: #eee;
}
.list-group-item.disabled .list-group-item-heading,
.list-group-item.disabled:hover .list-group-item-heading,
.list-group-item.disabled:focus .list-group-item-heading {
  color: inherit;
}
.list-group-item.disabled .list-group-item-text,
.list-group-item.disabled:hover .list-group-item-text,
.list-group-item.disabled:focus .list-group-item-text {
  color: #777;
}
.list-group-item.active,
.list-group-item.active:hover,
.list-group-item.active:focus {
  z-index: 2;
  color: #fff;
  background-color: #337ab7;
  border-color: #337ab7;
}
.list-group-item.active .list-group-item-heading,
.list-group-item.active:hover .list-group-item-heading,
.list-group-item.active:focus .list-group-item-heading,
.list-group-item.active .list-group-item-heading > small,
.list-group-item.active:hover .list-group-item-heading > small,
.list-group-item.active:focus .list-group-item-heading > small,
.list-group-item.active .list-group-item-heading > .small,
.list-group-item.active:hover .list-group-item-heading > .small,
.list-group-item.active:focus .list-group-item-heading > .small {
  color: inherit;
}
.list-group-item.active .list-group-item-text,
.list-group-item.active:hover .list-group-item-text,
.list-group-item.active:focus .list-group-item-text {
  color: #c7ddef;
}
.list-group-item-success {
  color: #3c763d;
  background-color: #dff0d8;
}
a.list-group-item-success,
button.list-group-item-success {
  color: #3c763d;
}
a.list-group-item-success .list-group-item-heading,
button.list-group-item-success .list-group-item-heading {
  color: inherit;
}
a.list-group-item-success:hover,
button.list-group-item-success:hover,
a.list-group-item-success:focus,
button.list-group-item-success:focus {
  color: #3c763d;
  background-color: #d0e9c6;
}
a.list-group-item-success.active,
button.list-group-item-success.active,
a.list-group-item-success.active:hover,
button.list-group-item-success.active:hover,
a.list-group-item-success.active:focus,
button.list-group-item-success.active:focus {
  color: #fff;
  background-color: #3c763d;
  border-color: #3c763d;
}
.list-group-item-info {
  color: #31708f;
  background-color: #d9edf7;
}
a.list-group-item-info,
button.list-group-item-info {
  color: #31708f;
}
a.list-group-item-info .list-group-item-heading,
button.list-group-item-info .list-group-item-heading {
  color: inherit;
}
a.list-group-item-info:hover,
button.list-group-item-info:hover,
a.list-group-item-info:focus,
button.list-group-item-info:focus {
  color: #31708f;
  background-color: #c4e3f3;
}
a.list-group-item-info.active,
button.list-group-item-info.active,
a.list-group-item-info.active:hover,
button.list-group-item-info.active:hover,
a.list-group-item-info.active:focus,
button.list-group-item-info.active:focus {
  color: #fff;
  background-color: #31708f;
  border-color: #31708f;
}
.list-group-item-warning {
  color: #8a6d3b;
  background-color: #fcf8e3;
}
a.list-group-item-warning,
button.list-group-item-warning {
  color: #8a6d3b;
}
a.list-group-item-warning .list-group-item-heading,
button.list-group-item-warning .list-group-item-heading {
  color: inherit;
}
a.list-group-item-warning:hover,
button.list-group-item-warning:hover,
a.list-group-item-warning:focus,
button.list-group-item-warning:focus {
  color: #8a6d3b;
  background-color: #faf2cc;
}
a.list-group-item-warning.active,
button.list-group-item-warning.active,
a.list-group-item-warning.active:hover,
button.list-group-item-warning.active:hover,
a.list-group-item-warning.active:focus,
button.list-group-item-warning.active:focus {
  color: #fff;
  background-color: #8a6d3b;
  border-color: #8a6d3b;
}
.list-group-item-danger {
  color: #a94442;
  background-color: #f2dede;
}
a.list-group-item-danger,
button.list-group-item-danger {
  color: #a94442;
}
a.list-group-item-danger .list-group-item-heading,
button.list-group-item-danger .list-group-item-heading {
  color: inherit;
}
a.list-group-item-danger:hover,
button.list-group-item-danger:hover,
a.list-group-item-danger:focus,
button.list-group-item-danger:focus {
  color: #a94442;
  background-color: #ebcccc;
}
a.list-group-item-danger.active,
button.list-group-item-danger.active,
a.list-group-item-danger.active:hover,
button.list-group-item-danger.active:hover,
a.list-group-item-danger.active:focus,
button.list-group-item-danger.active:focus {
  color: #fff;
  background-color: #a94442;
  border-color: #a94442;
}
.list-group-item-heading {
  margin-top: 0;
  margin-bottom: 5px;
}
.list-group-item-text {
  margin-bottom: 0;
  line-height: 1.3;
}
.panel {
  margin-bottom: 20px;
  background-color: #fff;
  border: 1px solid transparent;
  border-radius: 4px;
  -webkit-box-shadow: 0 1px 1px rgba(0, 0, 0, 0.05);
  box-shadow: 0 1px 1px rgba(0, 0, 0, 0.05);
}
.panel-body {
  padding: 15px;
}
.panel-heading {
  padding: 10px 15px;
  border-bottom: 1px solid transparent;
  border-top-left-radius: 3px;
  border-top-right-radius: 3px;
}
.panel-heading > .dropdown .dropdown-toggle {
  color: inherit;
}
.panel-title {
  margin-top: 0;
  margin-bottom: 0;
  font-size: 16px;
  color: inherit;
}
.panel-title > a,
.panel-title > small,
.panel-title > .small,
.panel-title > small > a,
.panel-title > .small > a {
  color: inherit;
}
.panel-footer {
  padding: 10px 15px;
  background-color: #f5f5f5;
  border-top: 1px solid #ddd;
  border-bottom-right-radius: 3px;
  border-bottom-left-radius: 3px;
}
.panel > .list-group,
.panel > .panel-collapse > .list-group {
  margin-bottom: 0;
}
.panel > .list-group .list-group-item,
.panel > .panel-collapse > .list-group .list-group-item {
  border-width: 1px 0;
  border-radius: 0;
}
.panel > .list-group:first-child .list-group-item:first-child,
.panel > .panel-collapse > .list-group:first-child .list-group-item:first-child {
  border-top: 0;
  border-top-left-radius: 3px;
  border-top-right-radius: 3px;
}
.panel > .list-group:last-child .list-group-item:last-child,
.panel > .panel-collapse > .list-group:last-child .list-group-item:last-child {
  border-bottom: 0;
  border-bottom-right-radius: 3px;
  border-bottom-left-radius: 3px;
}
.panel > .panel-heading + .panel-collapse > .list-group .list-group-item:first-child {
  border-top-left-radius: 0;
  border-top-right-radius: 0;
}
.panel-heading + .list-group .list-group-item:first-child {
  border-top-width: 0;
}
.list-group + .panel-footer {
  border-top-width: 0;
}
.panel > .table,
.panel > .table-responsive > .table,
.panel > .panel-collapse > .table {
  margin-bottom: 0;
}
.panel > .table caption,
.panel > .table-responsive > .table caption,
.panel > .panel-collapse > .table caption {
  padding-right: 15px;
  padding-left: 15px;
}
.panel > .table:first-child,
.panel > .table-responsive:first-child > .table:first-child {
  border-top-left-radius: 3px;
  border-top-right-radius: 3px;
}
.panel > .table:first-child > thead:first-child > tr:first-child,
.panel > .table-responsive:first-child > .table:first-child > thead:first-child > tr:first-child,
.panel > .table:first-child > tbody:first-child > tr:first-child,
.panel > .table-responsive:first-child > .table:first-child > tbody:first-child > tr:first-child {
  border-top-left-radius: 3px;
  border-top-right-radius: 3px;
}
.panel > .table:first-child > thead:first-child > tr:first-child td:first-child,
.panel
  > .table-responsive:first-child
  > .table:first-child
  > thead:first-child
  > tr:first-child
  td:first-child,
.panel > .table:first-child > tbody:first-child > tr:first-child td:first-child,
.panel
  > .table-responsive:first-child
  > .table:first-child
  > tbody:first-child
  > tr:first-child
  td:first-child,
.panel > .table:first-child > thead:first-child > tr:first-child th:first-child,
.panel
  > .table-responsive:first-child
  > .table:first-child
  > thead:first-child
  > tr:first-child
  th:first-child,
.panel > .table:first-child > tbody:first-child > tr:first-child th:first-child,
.panel
  > .table-responsive:first-child
  > .table:first-child
  > tbody:first-child
  > tr:first-child
  th:first-child {
  border-top-left-radius: 3px;
}
.panel > .table:first-child > thead:first-child > tr:first-child td:last-child,
.panel
  > .table-responsive:first-child
  > .table:first-child
  > thead:first-child
  > tr:first-child
  td:last-child,
.panel > .table:first-child > tbody:first-child > tr:first-child td:last-child,
.panel
  > .table-responsive:first-child
  > .table:first-child
  > tbody:first-child
  > tr:first-child
  td:last-child,
.panel > .table:first-child > thead:first-child > tr:first-child th:last-child,
.panel
  > .table-responsive:first-child
  > .table:first-child
  > thead:first-child
  > tr:first-child
  th:last-child,
.panel > .table:first-child > tbody:first-child > tr:first-child th:last-child,
.panel
  > .table-responsive:first-child
  > .table:first-child
  > tbody:first-child
  > tr:first-child
  th:last-child {
  border-top-right-radius: 3px;
}
.panel > .table:last-child,
.panel > .table-responsive:last-child > .table:last-child {
  border-bottom-right-radius: 3px;
  border-bottom-left-radius: 3px;
}
.panel > .table:last-child > tbody:last-child > tr:last-child,
.panel > .table-responsive:last-child > .table:last-child > tbody:last-child > tr:last-child,
.panel > .table:last-child > tfoot:last-child > tr:last-child,
.panel > .table-responsive:last-child > .table:last-child > tfoot:last-child > tr:last-child {
  border-bottom-right-radius: 3px;
  border-bottom-left-radius: 3px;
}
.panel > .table:last-child > tbody:last-child > tr:last-child td:first-child,
.panel
  > .table-responsive:last-child
  > .table:last-child
  > tbody:last-child
  > tr:last-child
  td:first-child,
.panel > .table:last-child > tfoot:last-child > tr:last-child td:first-child,
.panel
  > .table-responsive:last-child
  > .table:last-child
  > tfoot:last-child
  > tr:last-child
  td:first-child,
.panel > .table:last-child > tbody:last-child > tr:last-child th:first-child,
.panel
  > .table-responsive:last-child
  > .table:last-child
  > tbody:last-child
  > tr:last-child
  th:first-child,
.panel > .table:last-child > tfoot:last-child > tr:last-child th:first-child,
.panel
  > .table-responsive:last-child
  > .table:last-child
  > tfoot:last-child
  > tr:last-child
  th:first-child {
  border-bottom-left-radius: 3px;
}
.panel > .table:last-child > tbody:last-child > tr:last-child td:last-child,
.panel
  > .table-responsive:last-child
  > .table:last-child
  > tbody:last-child
  > tr:last-child
  td:last-child,
.panel > .table:last-child > tfoot:last-child > tr:last-child td:last-child,
.panel
  > .table-responsive:last-child
  > .table:last-child
  > tfoot:last-child
  > tr:last-child
  td:last-child,
.panel > .table:last-child > tbody:last-child > tr:last-child th:last-child,
.panel
  > .table-responsive:last-child
  > .table:last-child
  > tbody:last-child
  > tr:last-child
  th:last-child,
.panel > .table:last-child > tfoot:last-child > tr:last-child th:last-child,
.panel
  > .table-responsive:last-child
  > .table:last-child
  > tfoot:last-child
  > tr:last-child
  th:last-child {
  border-bottom-right-radius: 3px;
}
.panel > .panel-body + .table,
.panel > .panel-body + .table-responsive,
.panel > .table + .panel-body,
.panel > .table-responsive + .panel-body {
  border-top: 1px solid #ddd;
}
.panel > .table > tbody:first-child > tr:first-child th,
.panel > .table > tbody:first-child > tr:first-child td {
  border-top: 0;
}
.panel > .table-bordered,
.panel > .table-responsive > .table-bordered {
  border: 0;
}
.panel > .table-bordered > thead > tr > th:first-child,
.panel > .table-responsive > .table-bordered > thead > tr > th:first-child,
.panel > .table-bordered > tbody > tr > th:first-child,
.panel > .table-responsive > .table-bordered > tbody > tr > th:first-child,
.panel > .table-bordered > tfoot > tr > th:first-child,
.panel > .table-responsive > .table-bordered > tfoot > tr > th:first-child,
.panel > .table-bordered > thead > tr > td:first-child,
.panel > .table-responsive > .table-bordered > thead > tr > td:first-child,
.panel > .table-bordered > tbody > tr > td:first-child,
.panel > .table-responsive > .table-bordered > tbody > tr > td:first-child,
.panel > .table-bordered > tfoot > tr > td:first-child,
.panel > .table-responsive > .table-bordered > tfoot > tr > td:first-child {
  border-left: 0;
}
.panel > .table-bordered > thead > tr > th:last-child,
.panel > .table-responsive > .table-bordered > thead > tr > th:last-child,
.panel > .table-bordered > tbody > tr > th:last-child,
.panel > .table-responsive > .table-bordered > tbody > tr > th:last-child,
.panel > .table-bordered > tfoot > tr > th:last-child,
.panel > .table-responsive > .table-bordered > tfoot > tr > th:last-child,
.panel > .table-bordered > thead > tr > td:last-child,
.panel > .table-responsive > .table-bordered > thead > tr > td:last-child,
.panel > .table-bordered > tbody > tr > td:last-child,
.panel > .table-responsive > .table-bordered > tbody > tr > td:last-child,
.panel > .table-bordered > tfoot > tr > td:last-child,
.panel > .table-responsive > .table-bordered > tfoot > tr > td:last-child {
  border-right: 0;
}
.panel > .table-bordered > thead > tr:first-child > td,
.panel > .table-responsive > .table-bordered > thead > tr:first-child > td,
.panel > .table-bordered > tbody > tr:first-child > td,
.panel > .table-responsive > .table-bordered > tbody > tr:first-child > td,
.panel > .table-bordered > thead > tr:first-child > th,
.panel > .table-responsive > .table-bordered > thead > tr:first-child > th,
.panel > .table-bordered > tbody > tr:first-child > th,
.panel > .table-responsive > .table-bordered > tbody > tr:first-child > th {
  border-bottom: 0;
}
.panel > .table-bordered > tbody > tr:last-child > td,
.panel > .table-responsive > .table-bordered > tbody > tr:last-child > td,
.panel > .table-bordered > tfoot > tr:last-child > td,
.panel > .table-responsive > .table-bordered > tfoot > tr:last-child > td,
.panel > .table-bordered > tbody > tr:last-child > th,
.panel > .table-responsive > .table-bordered > tbody > tr:last-child > th,
.panel > .table-bordered > tfoot > tr:last-child > th,
.panel > .table-responsive > .table-bordered > tfoot > tr:last-child > th {
  border-bottom: 0;
}
.panel > .table-responsive {
  margin-bottom: 0;
  border: 0;
}
.panel-group {
  margin-bottom: 20px;
}
.panel-group .panel {
  margin-bottom: 0;
  border-radius: 4px;
}
.panel-group .panel + .panel {
  margin-top: 5px;
}
.panel-group .panel-heading {
  border-bottom: 0;
}
.panel-group .panel-heading + .panel-collapse > .panel-body,
.panel-group .panel-heading + .panel-collapse > .list-group {
  border-top: 1px solid #ddd;
}
.panel-group .panel-footer {
  border-top: 0;
}
.panel-group .panel-footer + .panel-collapse .panel-body {
  border-bottom: 1px solid #ddd;
}
.panel-default {
  border-color: #ddd;
}
.panel-default > .panel-heading {
  color: #333;
  background-color: #f5f5f5;
  border-color: #ddd;
}
.panel-default > .panel-heading + .panel-collapse > .panel-body {
  border-top-color: #ddd;
}
.panel-default > .panel-heading .badge {
  color: #f5f5f5;
  background-color: #333;
}
.panel-default > .panel-footer + .panel-collapse > .panel-body {
  border-bottom-color: #ddd;
}
.panel-primary {
  border-color: #337ab7;
}
.panel-primary > .panel-heading {
  color: #fff;
  background-color: #337ab7;
  border-color: #337ab7;
}
.panel-primary > .panel-heading + .panel-collapse > .panel-body {
  border-top-color: #337ab7;
}
.panel-primary > .panel-heading .badge {
  color: #337ab7;
  background-color: #fff;
}
.panel-primary > .panel-footer + .panel-collapse > .panel-body {
  border-bottom-color: #337ab7;
}
.panel-success {
  border-color: #d6e9c6;
}
.panel-success > .panel-heading {
  color: #3c763d;
  background-color: #dff0d8;
  border-color: #d6e9c6;
}
.panel-success > .panel-heading + .panel-collapse > .panel-body {
  border-top-color: #d6e9c6;
}
.panel-success > .panel-heading .badge {
  color: #dff0d8;
  background-color: #3c763d;
}
.panel-success > .panel-footer + .panel-collapse > .panel-body {
  border-bottom-color: #d6e9c6;
}
.panel-info {
  border-color: #bce8f1;
}
.panel-info > .panel-heading {
  color: #31708f;
  background-color: #d9edf7;
  border-color: #bce8f1;
}
.panel-info > .panel-heading + .panel-collapse > .panel-body {
  border-top-color: #bce8f1;
}
.panel-info > .panel-heading .badge {
  color: #d9edf7;
  background-color: #31708f;
}
.panel-info > .panel-footer + .panel-collapse > .panel-body {
  border-bottom-color: #bce8f1;
}
.panel-warning {
  border-color: #faebcc;
}
.panel-warning > .panel-heading {
  color: #8a6d3b;
  background-color: #fcf8e3;
  border-color: #faebcc;
}
.panel-warning > .panel-heading + .panel-collapse > .panel-body {
  border-top-color: #faebcc;
}
.panel-warning > .panel-heading .badge {
  color: #fcf8e3;
  background-color: #8a6d3b;
}
.panel-warning > .panel-footer + .panel-collapse > .panel-body {
  border-bottom-color: #faebcc;
}
.panel-danger {
  border-color: #ebccd1;
}
.panel-danger > .panel-heading {
  color: #a94442;
  background-color: #f2dede;
  border-color: #ebccd1;
}
.panel-danger > .panel-heading + .panel-collapse > .panel-body {
  border-top-color: #ebccd1;
}
.panel-danger > .panel-heading .badge {
  color: #f2dede;
  background-color: #a94442;
}
.panel-danger > .panel-footer + .panel-collapse > .panel-body {
  border-bottom-color: #ebccd1;
}
.embed-responsive {
  position: relative;
  display: block;
  height: 0;
  padding: 0;
  overflow: hidden;
}
.embed-responsive .embed-responsive-item,
.embed-responsive iframe,
.embed-responsive embed,
.embed-responsive object,
.embed-responsive video {
  position: absolute;
  top: 0;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 100%;
  border: 0;
}
.embed-responsive-16by9 {
  padding-bottom: 56.25%;
}
.embed-responsive-4by3 {
  padding-bottom: 75%;
}
.well {
  min-height: 20px;
  padding: 19px;
  margin-bottom: 20px;
  background-color: #f5f5f5;
  border: 1px solid #e3e3e3;
  border-radius: 4px;
  -webkit-box-shadow: inset 0 1px 1px rgba(0, 0, 0, 0.05);
  box-shadow: inset 0 1px 1px rgba(0, 0, 0, 0.05);
}
.well blockquote {
  border-color: #ddd;
  border-color: rgba(0, 0, 0, 0.15);
}
.well-lg {
  padding: 24px;
  border-radius: 6px;
}
.well-sm {
  padding: 9px;
  border-radius: 3px;
}
.close {
  float: right;
  font-size: 21px;
  font-weight: bold;
  line-height: 1;
  color: #000;
  text-shadow: 0 1px 0 #fff;
  filter: alpha(opacity=20);
  opacity: 0.2;
}
.close:hover,
.close:focus {
  color: #000;
  text-decoration: none;
  cursor: pointer;
  filter: alpha(opacity=50);
  opacity: 0.5;
}
button.close {
  -webkit-appearance: none;
  padding: 0;
  cursor: pointer;
  background: transparent;
  border: 0;
}
.modal-open {
  overflow: hidden;
}
.modal {
  position: fixed;
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;
  z-index: 1050;
  display: none;
  overflow: hidden;
  -webkit-overflow-scrolling: touch;
  outline: 0;
}
.modal.fade .modal-dialog {
  -webkit-transition: -webkit-transform 0.3s ease-out;
  -o-transition: -o-transform 0.3s ease-out;
  transition: transform 0.3s ease-out;
  -webkit-transform: translate(0, -25%);
  -ms-transform: translate(0, -25%);
  -o-transform: translate(0, -25%);
  transform: translate(0, -25%);
}
.modal.in .modal-dialog {
  -webkit-transform: translate(0, 0);
  -ms-transform: translate(0, 0);
  -o-transform: translate(0, 0);
  transform: translate(0, 0);
}
.modal-open .modal {
  overflow-x: hidden;
  overflow-y: auto;
}
.modal-dialog {
  position: relative;
  width: auto;
  margin: 10px;
}
.modal-content {
  position: relative;
  background-color: #fff;
  -webkit-background-clip: padding-box;
  background-clip: padding-box;
  border: 1px solid #999;
  border: 1px solid rgba(0, 0, 0, 0.2);
  border-radius: 6px;
  outline: 0;
  -webkit-box-shadow: 0 3px 9px rgba(0, 0, 0, 0.5);
  box-shadow: 0 3px 9px rgba(0, 0, 0, 0.5);
}
.modal-backdrop {
  position: fixed;
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;
  z-index: 1040;
  background-color: #000;
}
.modal-backdrop.fade {
  filter: alpha(opacity=0);
  opacity: 0;
}
.modal-backdrop.in {
  filter: alpha(opacity=50);
  opacity: 0.5;
}
.modal-header {
  min-height: 16.42857143px;
  padding: 15px;
  border-bottom: 1px solid #e5e5e5;
}
.modal-header .close {
  margin-top: -2px;
}
.modal-title {
  margin: 0;
  line-height: 1.42857143;
}
.modal-body {
  position: relative;
  padding: 15px;
}
.modal-footer {
  padding: 0 15px 15px 15px;
  text-align: center;
}
.modal-footer .btn + .btn {
  margin-bottom: 0;
  margin-left: 5px;
}
.modal-footer .btn-group .btn + .btn {
  margin-left: -1px;
}
.modal-footer .btn-block + .btn-block {
  margin-left: 0;
}
.modal-scrollbar-measure {
  position: absolute;
  top: -9999px;
  width: 50px;
  height: 50px;
  overflow: scroll;
}
@media (min-width: 768px) {
  .modal-dialog {
    width: 600px;
    margin: 30px auto;
  }
  .modal-content {
    -webkit-box-shadow: 0 5px 15px rgba(0, 0, 0, 0.5);
    box-shadow: 0 5px 15px rgba(0, 0, 0, 0.5);
  }
  .modal-sm {
    width: 300px;
  }
}
@media (min-width: 992px) {
  .modal-lg {
    width: 900px;
  }
}
.tooltip {
  position: absolute;
  z-index: 1070;
  display: block;
  font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif;
  font-size: 12px;
  font-style: normal;
  font-weight: normal;
  line-height: 1.42857143;
  text-align: left;
  text-align: start;
  text-decoration: none;
  text-shadow: none;
  text-transform: none;
  letter-spacing: normal;
  word-break: normal;
  word-spacing: normal;
  word-wrap: normal;
  white-space: normal;
  filter: alpha(opacity=0);
  opacity: 0;

  line-break: auto;
}
.tooltip.in {
  filter: alpha(opacity=90);
  opacity: 0.9;
}
.tooltip.top {
  padding: 5px 0;
  margin-top: -3px;
}
.tooltip.right {
  padding: 0 5px;
  margin-left: 3px;
}
.tooltip.bottom {
  padding: 5px 0;
  margin-top: 3px;
}
.tooltip.left {
  padding: 0 5px;
  margin-left: -3px;
}
.tooltip-inner {
  max-width: 200px;
  padding: 3px 8px;
  color: #fff;
  text-align: center;
  background-color: #000;
  border-radius: 4px;
}
.tooltip-arrow {
  position: absolute;
  width: 0;
  height: 0;
  border-color: transparent;
  border-style: solid;
}
.tooltip.top .tooltip-arrow {
  bottom: 0;
  left: 50%;
  margin-left: -5px;
  border-width: 5px 5px 0;
  border-top-color: #000;
}
.tooltip.top-left .tooltip-arrow {
  right: 5px;
  bottom: 0;
  margin-bottom: -5px;
  border-width: 5px 5px 0;
  border-top-color: #000;
}
.tooltip.top-right .tooltip-arrow {
  bottom: 0;
  left: 5px;
  margin-bottom: -5px;
  border-width: 5px 5px 0;
  border-top-color: #000;
}
.tooltip.right .tooltip-arrow {
  top: 50%;
  left: 0;
  margin-top: -5px;
  border-width: 5px 5px 5px 0;
  border-right-color: #000;
}
.tooltip.left .tooltip-arrow {
  top: 50%;
  right: 0;
  margin-top: -5px;
  border-width: 5px 0 5px 5px;
  border-left-color: #000;
}
.tooltip.bottom .tooltip-arrow {
  top: 0;
  left: 50%;
  margin-left: -5px;
  border-width: 0 5px 5px;
  border-bottom-color: #000;
}
.tooltip.bottom-left .tooltip-arrow {
  top: 0;
  right: 5px;
  margin-top: -5px;
  border-width: 0 5px 5px;
  border-bottom-color: #000;
}
.tooltip.bottom-right .tooltip-arrow {
  top: 0;
  left: 5px;
  margin-top: -5px;
  border-width: 0 5px 5px;
  border-bottom-color: #000;
}
.popover {
  position: absolute;
  top: 0;
  left: 0;
  z-index: 1060;
  display: none;
  max-width: 276px;
  padding: 1px;
  font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif;
  font-size: 14px;
  font-style: normal;
  font-weight: normal;
  line-height: 1.42857143;
  text-align: left;
  text-align: start;
  text-decoration: none;
  text-shadow: none;
  text-transform: none;
  letter-spacing: normal;
  word-break: normal;
  word-spacing: normal;
  word-wrap: normal;
  white-space: normal;
  background-color: #fff;
  -webkit-background-clip: padding-box;
  background-clip: padding-box;
  border: 1px solid #ccc;
  border: 1px solid rgba(0, 0, 0, 0.2);
  border-radius: 6px;
  -webkit-box-shadow: 0 5px 10px rgba(0, 0, 0, 0.2);
  box-shadow: 0 5px 10px rgba(0, 0, 0, 0.2);

  line-break: auto;
}
.popover.top {
  margin-top: -10px;
}
.popover.right {
  margin-left: 10px;
}
.popover.bottom {
  margin-top: 10px;
}
.popover.left {
  margin-left: -10px;
}
.popover-title {
  padding: 8px 14px;
  margin: 0;
  font-size: 14px;
  background-color: #f7f7f7;
  border-bottom: 1px solid #ebebeb;
  border-radius: 5px 5px 0 0;
}
.popover-content {
  padding: 9px 14px;
}
.popover > .arrow,
.popover > .arrow:after {
  position: absolute;
  display: block;
  width: 0;
  height: 0;
  border-color: transparent;
  border-style: solid;
}
.popover > .arrow {
  border-width: 11px;
}
.popover > .arrow:after {
  content: '';
  border-width: 10px;
}
.popover.top > .arrow {
  bottom: -11px;
  left: 50%;
  margin-left: -11px;
  border-top-color: #999;
  border-top-color: rgba(0, 0, 0, 0.25);
  border-bottom-width: 0;
}
.popover.top > .arrow:after {
  bottom: 1px;
  margin-left: -10px;
  content: ' ';
  border-top-color: #fff;
  border-bottom-width: 0;
}
.popover.right > .arrow {
  top: 50%;
  left: -11px;
  margin-top: -11px;
  border-right-color: #999;
  border-right-color: rgba(0, 0, 0, 0.25);
  border-left-width: 0;
}
.popover.right > .arrow:after {
  bottom: -10px;
  left: 1px;
  content: ' ';
  border-right-color: #fff;
  border-left-width: 0;
}
.popover.bottom > .arrow {
  top: -11px;
  left: 50%;
  margin-left: -11px;
  border-top-width: 0;
  border-bottom-color: #999;
  border-bottom-color: rgba(0, 0, 0, 0.25);
}
.popover.bottom > .arrow:after {
  top: 1px;
  margin-left: -10px;
  content: ' ';
  border-top-width: 0;
  border-bottom-color: #fff;
}
.popover.left > .arrow {
  top: 50%;
  right: -11px;
  margin-top: -11px;
  border-right-width: 0;
  border-left-color: #999;
  border-left-color: rgba(0, 0, 0, 0.25);
}
.popover.left > .arrow:after {
  right: 1px;
  bottom: -10px;
  content: ' ';
  border-right-width: 0;
  border-left-color: #fff;
}
.carousel {
  position: relative;
}
.carousel-inner {
  position: relative;
  width: 100%;
  overflow: hidden;
}
.carousel-inner > .item {
  position: relative;
  display: none;
  -webkit-transition: 0.6s ease-in-out left;
  -o-transition: 0.6s ease-in-out left;
  transition: 0.6s ease-in-out left;
}
.carousel-inner > .item > img,
.carousel-inner > .item > a > img {
  line-height: 1;
}
@media all and (transform-3d), (-webkit-transform-3d) {
  .carousel-inner > .item {
    -webkit-transition: -webkit-transform 0.6s ease-in-out;
    -o-transition: -o-transform 0.6s ease-in-out;
    transition: transform 0.6s ease-in-out;

    -webkit-backface-visibility: hidden;
    backface-visibility: hidden;
    -webkit-perspective: 1000px;
    perspective: 1000px;
  }
  .carousel-inner > .item.next,
  .carousel-inner > .item.active.right {
    left: 0;
    -webkit-transform: translate3d(100%, 0, 0);
    transform: translate3d(100%, 0, 0);
  }
  .carousel-inner > .item.prev,
  .carousel-inner > .item.active.left {
    left: 0;
    -webkit-transform: translate3d(-100%, 0, 0);
    transform: translate3d(-100%, 0, 0);
  }
  .carousel-inner > .item.next.left,
  .carousel-inner > .item.prev.right,
  .carousel-inner > .item.active {
    left: 0;
    -webkit-transform: translate3d(0, 0, 0);
    transform: translate3d(0, 0, 0);
  }
}
.carousel-inner > .active,
.carousel-inner > .next,
.carousel-inner > .prev {
  display: block;
}
.carousel-inner > .active {
  left: 0;
}
.carousel-inner > .next,
.carousel-inner > .prev {
  position: absolute;
  top: 0;
  width: 100%;
}
.carousel-inner > .next {
  left: 100%;
}
.carousel-inner > .prev {
  left: -100%;
}
.carousel-inner > .next.left,
.carousel-inner > .prev.right {
  left: 0;
}
.carousel-inner > .active.left {
  left: -100%;
}
.carousel-inner > .active.right {
  left: 100%;
}
.carousel-control {
  position: absolute;
  top: 0;
  bottom: 0;
  left: 0;
  width: 15%;
  font-size: 20px;
  color: #fff;
  text-align: center;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.6);
  filter: alpha(opacity=50);
  opacity: 0.5;
}
.carousel-control.left {
  background-image: -webkit-linear-gradient(
    left,
    rgba(0, 0, 0, 0.5) 0%,
    rgba(0, 0, 0, 0.0001) 100%
  );
  background-image: -o-linear-gradient(left, rgba(0, 0, 0, 0.5) 0%, rgba(0, 0, 0, 0.0001) 100%);
  background-image: -webkit-gradient(
    linear,
    left top,
    right top,
    from(rgba(0, 0, 0, 0.5)),
    to(rgba(0, 0, 0, 0.0001))
  );
  background-image: linear-gradient(to right, rgba(0, 0, 0, 0.5) 0%, rgba(0, 0, 0, 0.0001) 100%);
  filter: progid:DXImageTransform.Microsoft.gradient(startColorstr='#80000000', endColorstr='#00000000', GradientType=1);
  background-repeat: repeat-x;
}
.carousel-control.right {
  right: 0;
  left: auto;
  background-image: -webkit-linear-gradient(
    left,
    rgba(0, 0, 0, 0.0001) 0%,
    rgba(0, 0, 0, 0.5) 100%
  );
  background-image: -o-linear-gradient(left, rgba(0, 0, 0, 0.0001) 0%, rgba(0, 0, 0, 0.5) 100%);
  background-image: -webkit-gradient(
    linear,
    left top,
    right top,
    from(rgba(0, 0, 0, 0.0001)),
    to(rgba(0, 0, 0, 0.5))
  );
  background-image: linear-gradient(to right, rgba(0, 0, 0, 0.0001) 0%, rgba(0, 0, 0, 0.5) 100%);
  filter: progid:DXImageTransform.Microsoft.gradient(startColorstr='#00000000', endColorstr='#80000000', GradientType=1);
  background-repeat: repeat-x;
}
.carousel-control:hover,
.carousel-control:focus {
  color: #fff;
  text-decoration: none;
  filter: alpha(opacity=90);
  outline: 0;
  opacity: 0.9;
}
.carousel-control .icon-prev,
.carousel-control .icon-next,
.carousel-control .glyphicon-chevron-left,
.carousel-control .glyphicon-chevron-right {
  position: absolute;
  top: 50%;
  z-index: 5;
  display: inline-block;
  margin-top: -10px;
}
.carousel-control .icon-prev,
.carousel-control .glyphicon-chevron-left {
  left: 50%;
  margin-left: -10px;
}
.carousel-control .icon-next,
.carousel-control .glyphicon-chevron-right {
  right: 50%;
  margin-right: -10px;
}
.carousel-control .icon-prev,
.carousel-control .icon-next {
  width: 20px;
  height: 20px;
  font-family: serif;
  line-height: 1;
}
.carousel-control .icon-prev:before {
  content: '\2039';
}
.carousel-control .icon-next:before {
  content: '\203a';
}
.carousel-indicators {
  position: absolute;
  bottom: 10px;
  left: 50%;
  z-index: 15;
  width: 60%;
  padding-left: 0;
  margin-left: -30%;
  text-align: center;
  list-style: none;
}
.carousel-indicators li {
  display: inline-block;
  width: 10px;
  height: 10px;
  margin: 1px;
  text-indent: -999px;
  cursor: pointer;
  background-color: #000 \9;
  background-color: rgba(0, 0, 0, 0);
  border: 1px solid #fff;
  border-radius: 10px;
}
.carousel-indicators .active {
  width: 12px;
  height: 12px;
  margin: 0;
  background-color: #fff;
}
.carousel-caption {
  position: absolute;
  right: 15%;
  bottom: 20px;
  left: 15%;
  z-index: 10;
  padding-top: 20px;
  padding-bottom: 20px;
  color: #fff;
  text-align: center;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.6);
}
.carousel-caption .btn {
  text-shadow: none;
}
@media screen and (min-width: 768px) {
  .carousel-control .glyphicon-chevron-left,
  .carousel-control .glyphicon-chevron-right,
  .carousel-control .icon-prev,
  .carousel-control .icon-next {
    width: 30px;
    height: 30px;
    margin-top: -15px;
    font-size: 30px;
  }
  .carousel-control .glyphicon-chevron-left,
  .carousel-control .icon-prev {
    margin-left: -15px;
  }
  .carousel-control .glyphicon-chevron-right,
  .carousel-control .icon-next {
    margin-right: -15px;
  }
  .carousel-caption {
    right: 20%;
    left: 20%;
    padding-bottom: 30px;
  }
  .carousel-indicators {
    bottom: 20px;
  }
}
.clearfix:before,
.clearfix:after,
.dl-horizontal dd:before,
.dl-horizontal dd:after,
.container:before,
.container:after,
.container-fluid:before,
.container-fluid:after,
.row:before,
.row:after,
.form-horizontal .form-group:before,
.form-horizontal .form-group:after,
.btn-toolbar:before,
.btn-toolbar:after,
.btn-group-vertical > .btn-group:before,
.btn-group-vertical > .btn-group:after,
.nav:before,
.nav:after,
.navbar:before,
.navbar:after,
.navbar-header:before,
.navbar-header:after,
.navbar-collapse:before,
.navbar-collapse:after,
.pager:before,
.pager:after,
.panel-body:before,
.panel-body:after,
.modal-footer:before,
.modal-footer:after {
  display: table;
  content: ' ';
}
.clearfix:after,
.dl-horizontal dd:after,
.container:after,
.container-fluid:after,
.row:after,
.form-horizontal .form-group:after,
.btn-toolbar:after,
.btn-group-vertical > .btn-group:after,
.nav:after,
.navbar:after,
.navbar-header:after,
.navbar-collapse:after,
.pager:after,
.panel-body:after,
.modal-footer:after {
  clear: both;
}
.center-block {
  display: block;
  margin-right: auto;
  margin-left: auto;
}
.pull-right {
  float: right !important;
}
.pull-left {
  float: left !important;
}
.hide {
  display: none !important;
}
.show {
  display: block !important;
}
.invisible {
  visibility: hidden;
}
.text-hide {
  font: 0/0 a;
  color: transparent;
  text-shadow: none;
  background-color: transparent;
  border: 0;
}
.hidden {
  display: none !important;
}
.affix {
  position: fixed;
}
@-ms-viewport {
  width: device-width;
}
.visible-xs,
.visible-sm,
.visible-md,
.visible-lg {
  display: none !important;
}
.visible-xs-block,
.visible-xs-inline,
.visible-xs-inline-block,
.visible-sm-block,
.visible-sm-inline,
.visible-sm-inline-block,
.visible-md-block,
.visible-md-inline,
.visible-md-inline-block,
.visible-lg-block,
.visible-lg-inline,
.visible-lg-inline-block {
  display: none !important;
}
@media (max-width: 767px) {
  .visible-xs {
    display: block !important;
  }
  table.visible-xs {
    display: table !important;
  }
  tr.visible-xs {
    display: table-row !important;
  }
  th.visible-xs,
  td.visible-xs {
    display: table-cell !important;
  }
}
@media (max-width: 767px) {
  .visible-xs-block {
    display: block !important;
  }
}
@media (max-width: 767px) {
  .visible-xs-inline {
    display: inline !important;
  }
}
@media (max-width: 767px) {
  .visible-xs-inline-block {
    display: inline-block !important;
  }
}
@media (min-width: 768px) and (max-width: 991px) {
  .visible-sm {
    display: block !important;
  }
  table.visible-sm {
    display: table !important;
  }
  tr.visible-sm {
    display: table-row !important;
  }
  th.visible-sm,
  td.visible-sm {
    display: table-cell !important;
  }
}
@media (min-width: 768px) and (max-width: 991px) {
  .visible-sm-block {
    display: block !important;
  }
}
@media (min-width: 768px) and (max-width: 991px) {
  .visible-sm-inline {
    display: inline !important;
  }
}
@media (min-width: 768px) and (max-width: 991px) {
  .visible-sm-inline-block {
    display: inline-block !important;
  }
}
@media (min-width: 992px) and (max-width: 1199px) {
  .visible-md {
    display: block !important;
  }
  table.visible-md {
    display: table !important;
  }
  tr.visible-md {
    display: table-row !important;
  }
  th.visible-md,
  td.visible-md {
    display: table-cell !important;
  }
}
@media (min-width: 992px) and (max-width: 1199px) {
  .visible-md-block {
    display: block !important;
  }
}
@media (min-width: 992px) and (max-width: 1199px) {
  .visible-md-inline {
    display: inline !important;
  }
}
@media (min-width: 992px) and (max-width: 1199px) {
  .visible-md-inline-block {
    display: inline-block !important;
  }
}
@media (min-width: 1200px) {
  .visible-lg {
    display: block !important;
  }
  table.visible-lg {
    display: table !important;
  }
  tr.visible-lg {
    display: table-row !important;
  }
  th.visible-lg,
  td.visible-lg {
    display: table-cell !important;
  }
}
@media (min-width: 1200px) {
  .visible-lg-block {
    display: block !important;
  }
}
@media (min-width: 1200px) {
  .visible-lg-inline {
    display: inline !important;
  }
}
@media (min-width: 1200px) {
  .visible-lg-inline-block {
    display: inline-block !important;
  }
}
/* @media (max-width: 767px) { */
/*   .hidden-xs { */
/*     display: none !important; */
/*   } */
/* } */
@media (min-width: 768px) and (max-width: 991px) {
  .hidden-sm {
    display: none !important;
  }
}
@media (min-width: 992px) and (max-width: 1199px) {
  .hidden-md {
    display: none !important;
  }
}
@media (min-width: 1200px) {
  .hidden-lg {
    display: none !important;
  }
}
.visible-print {
  display: none !important;
}
@media print {
  .visible-print {
    display: block !important;
  }
  table.visible-print {
    display: table !important;
  }
  tr.visible-print {
    display: table-row !important;
  }
  th.visible-print,
  td.visible-print {
    display: table-cell !important;
  }
}
.visible-print-block {
  display: none !important;
}
@media print {
  .visible-print-block {
    display: block !important;
  }
}
.visible-print-inline {
  display: none !important;
}
@media print {
  .visible-print-inline {
    display: inline !important;
  }
}
.visible-print-inline-block {
  display: none !important;
}
@media print {
  .visible-print-inline-block {
    display: inline-block !important;
  }
}
@media print {
  .hidden-print {
    display: none !important;
  }
}

.ellipsis {
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  /*display: -webkit-box; */
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* morris.css */
.morris-hover {
  position: absolute;
  z-index: 1090;
}
.morris-hover.morris-default-style {
  border-radius: 10px;
  padding: 6px;
  color: #f9f9f9;
  background: rgba(0, 0, 0, 0.8);
  border: solid 2px rgba(0, 0, 0, 0.9);
  font-weight: 600;
  font-size: 14px;
  text-align: center;
}
.morris-hover.morris-default-style .morris-hover-row-label {
  font-weight: bold;
  margin: 0.25em 0;
}
.morris-hover.morris-default-style .morris-hover-point {
  white-space: nowrap;
  margin: 0.1em 0;
}

/* ol.css */
.ol-box {
  box-sizing: border-box;
  border-radius: 2px;
  border: 2px solid #00f;
}
.ol-mouse-position {
  top: 8px;
  right: 8px;
  position: absolute;
}
.ol-scale-line {
  background: rgba(0, 60, 136, 0.3);
  border-radius: 4px;
  bottom: 8px;
  left: 8px;
  padding: 2px;
  position: absolute;
}
.ol-scale-line-inner {
  border: 1px solid #eee;
  border-top: none;
  color: #eee;
  font-size: 10px;
  text-align: center;
  margin: 1px;
  will-change: contents, width;
  transition: all 0.25s;
}
.ol-scale-bar {
  position: absolute;
  bottom: 8px;
  left: 8px;
}
.ol-scale-step-marker {
  width: 1px;
  height: 15px;
  background-color: #000;
  float: right;
  z-index: 10;
}
.ol-scale-step-text {
  position: absolute;
  bottom: -5px;
  font-size: 12px;
  z-index: 11;
  color: #000;
  text-shadow:
    -2px 0 #fff,
    0 2px #fff,
    2px 0 #fff,
    0 -2px #fff;
}
.ol-scale-text {
  position: absolute;
  font-size: 14px;
  text-align: center;
  bottom: 25px;
  color: #000;
  text-shadow:
    -2px 0 #fff,
    0 2px #fff,
    2px 0 #fff,
    0 -2px #fff;
}
.ol-scale-singlebar {
  position: relative;
  height: 10px;
  z-index: 9;
  box-sizing: border-box;
  border: 1px solid #000;
}
.ol-unsupported {
  display: none;
}
.ol-unselectable,
.ol-viewport {
  -webkit-touch-callout: none;
  -webkit-user-select: none;
  -moz-user-select: none;
  -ms-user-select: none;
  user-select: none;
  -webkit-tap-highlight-color: transparent;
}
.ol-selectable {
  -webkit-touch-callout: default;
  -webkit-user-select: text;
  -moz-user-select: text;
  -ms-user-select: text;
  user-select: text;
}
.ol-grabbing {
  cursor: -webkit-grabbing;
  cursor: -moz-grabbing;
  cursor: grabbing;
}
.ol-grab {
  cursor: move;
  cursor: -webkit-grab;
  cursor: -moz-grab;
  cursor: grab;
}
.ol-control {
  position: absolute;
  background-color: rgba(255, 255, 255, 0.4);
  border-radius: 4px;
  padding: 2px;
}
.ol-control:hover {
  background-color: rgba(255, 255, 255, 0.6);
}
.ol-zoom {
  top: 0.5em;
  left: 0.5em;
}
.ol-rotate {
  top: 0.5em;
  right: 0.5em;
  transition:
    opacity 0.25s linear,
    visibility 0s linear;
}
.ol-rotate.ol-hidden {
  opacity: 0;
  visibility: hidden;
  transition:
    opacity 0.25s linear,
    visibility 0s linear 0.25s;
}
.ol-zoom-extent {
  top: 4.643em;
  left: 0.5em;
}
.ol-full-screen {
  right: 0.5em;
  top: 0.5em;
}
.ol-control button {
  display: block;
  margin: 1px;
  padding: 0;
  color: #fff;
  font-size: 1.14em;
  font-weight: 700;
  text-decoration: none;
  text-align: center;
  height: 1.375em;
  width: 1.375em;
  line-height: 0.4em;
  background-color: rgba(0, 60, 136, 0.5);
  border: none;
  border-radius: 2px;
}
.ol-control button::-moz-focus-inner {
  border: none;
  padding: 0;
}
.ol-zoom-extent button {
  line-height: 1.4em;
}
.ol-compass {
  display: block;
  font-weight: 400;
  font-size: 1.2em;
  will-change: transform;
}
.ol-touch .ol-control button {
  font-size: 1.5em;
}
.ol-touch .ol-zoom-extent {
  top: 5.5em;
}
.ol-control button:focus,
.ol-control button:hover {
  text-decoration: none;
  background-color: rgba(0, 60, 136, 0.7);
}
.ol-zoom .ol-zoom-in {
  border-radius: 2px 2px 0 0;
}
.ol-zoom .ol-zoom-out {
  border-radius: 0 0 2px 2px;
}
.ol-attribution {
  text-align: right;
  bottom: 0.5em;
  right: 0.5em;
  max-width: calc(100% - 1.3em);
}
.ol-attribution ul {
  margin: 0;
  padding: 0 0.5em;
  color: #000;
  text-shadow: 0 0 2px #fff;
}
.ol-attribution li {
  display: inline;
  list-style: none;
}
.ol-attribution li:not(:last-child):after {
  content: ' ';
}
.ol-attribution img {
  max-height: 2em;
  max-width: inherit;
  vertical-align: middle;
}
.ol-attribution button,
.ol-attribution ul {
  display: inline-block;
}
.ol-attribution.ol-collapsed ul {
  display: none;
}
.ol-attribution:not(.ol-collapsed) {
  background: rgba(255, 255, 255, 0.8);
}
.ol-attribution.ol-uncollapsible {
  bottom: 0;
  right: 0;
  border-radius: 4px 0 0;
}
.ol-attribution.ol-uncollapsible img {
  margin-top: -0.2em;
  max-height: 1.6em;
}
.ol-attribution.ol-uncollapsible button {
  display: none;
}
.ol-zoomslider {
  top: 4.5em;
  left: 0.5em;
  height: 200px;
}
.ol-zoomslider button {
  position: relative;
  height: 10px;
}
.ol-touch .ol-zoomslider {
  top: 5.5em;
}
.ol-overviewmap {
  left: 0.5em;
  bottom: 0.5em;
}
.ol-overviewmap.ol-uncollapsible {
  bottom: 0;
  left: 0;
  border-radius: 0 4px 0 0;
}
.ol-overviewmap .ol-overviewmap-map,
.ol-overviewmap button {
  display: inline-block;
}
.ol-overviewmap .ol-overviewmap-map {
  border: 1px solid #7b98bc;
  height: 150px;
  margin: 2px;
  width: 150px;
}
.ol-overviewmap:not(.ol-collapsed) button {
  bottom: 1px;
  left: 2px;
  position: absolute;
}
.ol-overviewmap.ol-collapsed .ol-overviewmap-map,
.ol-overviewmap.ol-uncollapsible button {
  display: none;
}
.ol-overviewmap:not(.ol-collapsed) {
  background: rgba(255, 255, 255, 0.8);
}
.ol-overviewmap-box {
  border: 2px dotted rgba(0, 60, 136, 0.7);
}
.ol-overviewmap .ol-overviewmap-box:hover {
  cursor: move;
}

/* gis.css */
/*레이어 슬라이드*/
/*.layer_modal_wrap { position:absolute; top: 0; z-index: 4000; height: 100%;}

.layerForm-box { padding: 10px 20px 0 0; }
.layerForm-box .row { margin: 7px 0; padding: 0 20px 0 20px; }
.layerForm-box .row:hover { background: #efefef; }
.layerForm-box span { font-size: 15px; vertical-align: middle; display: inline-block; margin-left: 10px; font-weight: 300; }*/

.modlockhubMark {
  border-radius: 50%;
  background: green;
  border: 1px solid black;
  width: 8px;
  height: 8px;
}

.smarthubMark {
  width: 0;
  height: 0;
  border-left: 4px solid transparent;
  border-right: 4px solid transparent;
  border-bottom: 8px solid deeppink;
}

.firewaterMark {
  background: deepskyblue;
  border: 1px solid black;
  width: 8px;
  height: 8px;
}

.stationMark {
  width: 8px;
  height: 8px;
  background-color: darkorange;
  transform: rotate(45deg);
  border: 1px solid black;
}

.deathAccMark {
  border-radius: 50%;
  background: brown;
  border: 1px solid black;
  width: 8px;
  height: 8px;
}

.walkerAccMark {
  fill: rgba(0, 255, 255, 0.3);
  background: rgba(0, 255, 255, 0.3);
  color: #a5a5a5;
}

.twowheelerAccMark {
  fill: rgba(231, 84, 128, 0.3);
  background: rgba(231, 84, 128, 0.3);
  color: #a5a5a5;
}

.roadIdxAccMark {
  fill: rgba(255, 140, 0, 0.3);
  background: rgba(255, 140, 0, 0.3);
  color: #a5a5a5;
}

.linkAccMark {
  fill: rgba(128, 128, 128, 0.3);
  background: rgba(128, 128, 128, 0.3);
  color: #a5a5a5;
}

.bicycleAccMark {
  fill: rgba(255, 0, 0, 0.3);
  background: rgba(255, 0, 0, 0.3);
  color: #a5a5a5;
}

.elderAccMark {
  fill: rgba(128, 0, 128, 0.3);
  background: rgba(128, 0, 128, 0.3);
  color: #a5a5a5;
}

.childrenAccMark {
  fill: rgba(0, 128, 0, 0.3);
  background: rgba(0, 128, 0, 0.3);
  color: #a5a5a5;
}

.schoolzoneAccMark {
  fill: rgba(255, 165, 0, 0.3);
  background: rgba(255, 165, 0, 0.3);
  color: #a5a5a5;
}

.jaywalkAccMark {
  fill: rgba(0, 0, 255, 0.3);
  background: rgba(0, 0, 255, 0.3);
  color: #a5a5a5;
}

.accTableFrame table {
  text-align: center;
  width: 700px;
  border-top: 1px solid black;
}

.accTableFrame table tr {
  height: 40px;
  border-bottom: 1px solid #ddd;
}

.accTableFrame table th {
  white-space: nowrap;
  padding: 5px 10px !important;
  background-color: #2c3038 !important;
  color: #fff !important;
}

.accTableFrame table td {
  background-color: #fff !important;
  color: #000 !important;
}

.accTableFrame table th,
.accTableFrame table td {
  font-size: 14px;
  border: 1px solid #bdbdbd !important;
  text-align: center !important;
  height: 30px !important;
}

.tableListFrame table {
  text-align: center;
  border: 1px solid black;
}

.tableListFrame table td {
  padding: 5px 10px;
  border: 1px solid black;
  text-align: center;
}

.gis-control {
  cursor: pointer;
  font-size: 25px;
}
.contents {
  position: fixed !important;
  height: 100%;
}
.layer_modal_con {
  height: 100%;
}
.collapse {
  display: none;
}
.collapse.in {
  display: block;
}
tr.collapse.in {
  display: table-row;
}
tbody.collapse.in {
  display: table-row-group;
}
.collapsing {
  position: relative;
  height: 0;
  overflow: hidden;
  -webkit-transition-timing-function: ease;
  -o-transition-timing-function: ease;
  transition-timing-function: ease;
  -webkit-transition-duration: 0.35s;
  -o-transition-duration: 0.35s;
  transition-duration: 0.35s;
  -webkit-transition-property: height, visibility;
  -o-transition-property: height, visibility;
  transition-property: height, visibility;
}
.fade {
  opacity: 0;
  -webkit-transition: opacity 0.15s linear;
  -o-transition: opacity 0.15s linear;
  transition: opacity 0.15s linear;
}
.fade.in {
  opacity: 1;
}
.active {
  display: block !important;
}

.tab-pane {
  display: none;
}
.layer_btn_change {
  display: flex;
  justify-content: center;
}
#pills-profile-tab {
  margin-right: 4px;
}
.formItem {
  height: 35px;
  margin-bottom: 10px;
}
.custom-nav {
  cursor: pointer;
}
/** progress **/
.progress-body {
  display: none;
  position: fixed;
  background: white;
  width: calc(100% - 290px);
  height: 100%;
  z-index: 1500;
  top: 70px;
  left: 290px;
  opacity: 0.5;
}
.progress-content {
  border-radius: 20px;
  width: 120px;
  height: 120px;
  position: absolute;
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%);
  background: url('/img/spinner.gif');
  background-size: cover;
}
.layer-navi-inactive {
  transform: translate(-310px, 0px);
}
.layer_modal_wrap {
  transition: all ease 1s;
}
#layer-navi {
  background: #065cb6;
  float: left;
  width: 30px;
  height: 51px;
  position: absolute;
  cursor: pointer;
  border-bottom-right-radius: 20px;
}
#layer-navi div {
  width: 20px;
  height: 20px;
  top: 15px;
  left: 5px;
  position: absolute;
  background-image: url('/img/tw/map/minus.png');
  background-size: cover;
}
.layer-navi-inactive #layer-navi div {
  background-image: url('/img/tw/map/plus.png');
}

#mapModeButton {
  position: absolute;
  background: rgba(27, 136, 230, 0.26);
  color: #fff;
  cursor: pointer;
  text-align: center;
  border: 1px solid #0c6bc7;
  z-index: 99;
  right: 0;
  height: 30px;
  line-height: 30px;
}

#mapModeButton > div {
  font-size: 13px;
}
#mapModeButton > div {
  width: 90px;
  float: left;
}
#mapModeButton > div.select,
#mapModeButton > div:hover {
  cursor: pointer;
  background: rgba(27, 136, 230, 0.77);
}
#mapModeButton img {
  padding-top: 5px;
  width: 15px;
  height: 15px;
}

/* community_main.css */
header {
  width: 100%;
  height: 72px;
  position: fixed;
  background: linear-gradient(270deg, #055ab5, #1d8be9);
  z-index: 3000;
}

.map-aside {
  position: fixed;
  z-index: 10;
  width: 7rem;
  background-color: white;
  height: 100%;
  box-shadow: rgb(208, 215, 222) calc(max(1px, 0.0625rem) * -1) 0px inset;
}

.map-aside ul {
  display: flex;
  flexdirection: column;
  justifycontent: space-between;
  height: 100%;
}

.map-aside ul li {
  position: relative;
  transition: 0.2s;
  height: 8rem;
  font-size: 1.4rem;
  color: black;
  text-align: center;
}

.hashtag-pin,
.hashtag-search {
  height: 6rem;
  width: 100%;
  display: flex;
  flex-direction: row;
  flex-wrap: wrap;
  gap: 0.2rem;
  overflow: auto;
}

.hashtg {
  border-radius: 2rem;
  background-color: #98fb98;
  padding: 0.3rem 0.9rem;
}

.hashtag-pin input[name='hashtag-txt'],
.hashtag-search input[name='hashtag-txt'] {
  height: 100%;
  border: none;
  outline: none;
  font-size: 16px;
}

.search-area-block {
  display: none;
}
</style>
