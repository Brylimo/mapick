<template>
  <div class="container mt-5">
    <div class="tab-buttons text-center mb-4">
      <button @click="currentTab = 'timeline'" :class="{ active: currentTab === 'timeline' }">
        🗺️ 여행 타임라인
      </button>
      <button @click="currentTab = 'expenses'" :class="{ active: currentTab === 'expenses' }">
        💰 경비 계획
      </button>
    </div>

    <div v-if="currentTab === 'timeline'">
      <h2 class="mb-4 text-center fw-bold fs-1">🗺️ 여행 타임라인</h2>
      <div class="timeline-box">
        <div class="timeline">
          <div
            v-for="(plan, index) in plans"
            :key="plan.ppid"
            :class="['timeline-item', index % 2 === 0 ? 'left' : 'right']"
          >
            <div class="timeline-content">
              <img :src="plan.attraction.firstImage1 || noImage" alt="이미지" />
              <div class="timeline-details">
                <h3>{{ plan.attraction.title }}</h3>
                <p class="address">📍 {{ plan.attraction.addr1 }}</p>

                <div class="time-toggle">
                  <label>🕒 시간:</label>
                  <template v-if="!showTimeInput[index]">
                    <span>{{ plan.time || '미지정' }}</span>
                    <button class="btn-time" @click="toggleTimeInput(index)">변경</button>
                  </template>
                  <template v-else>
                    <input
                      type="time"
                      v-model="plan.time"
                      @blur="toggleTimeInput(index)"
                    />
                  </template>
                </div>

                <label class="memo-label">📝 간단 메모</label>
                <textarea
                  class="memo-box"
                  v-model="plan.exp"
                  rows="3"
                  :placeholder="plan.exp ? '' : '메모를 입력하세요...'"
                ></textarea>

                <div class="save-wrapper">
                  <button class="save-btn" @click="savePlanAttraction(plan)">💾 저장</button>
                </div>
              </div>
            </div>
            <div :class="index % 2 === 0 ? 'timeline-icon-left' : 'timeline-icon-right'">
              <img src="@/assets/img/user.png" alt="아이콘" />
            </div>
          </div>
        </div>
      </div>
    </div>

    <div v-else-if="currentTab === 'expenses'">
      <h2 class="mb-4 text-center fw-bold fs-1">💰 경비 계획</h2>
      <div class="expenses-box">
        <div class="add-expense-form">
          <input v-model="newExpense.content" type="text" placeholder="결제 항목" />
          <input v-model.number="newExpense.price" type="number" placeholder="결제 금액" />
          <button class="save-btn" @click="addNewExpense">📅 추가</button>
        </div>

    <ul class="expense-list">
      <li v-for="expense in expenses" :key="expense.expId" class="expense-item">
        <div class="expense-header">
          <div class="expense-title">
            💳 {{ expense.content }}
            <span class="expense-price">{{ expense.price?.toLocaleString?.() || '0' }}₩</span>
          </div>
        </div>

        <div class="receipt-actions">
          <div v-if="expense.receiptPath" class="receipt-preview">
            <img :src="expense.receiptPath" alt="영수증" />
          </div>

          <label class="custom-upload">
            📎 파일첨부
            <input type="file" @change="onFileChange($event, expense.expId)" hidden />
          </label>

          <button class="icon-btn" @click="fetchDownloadReceipt(expense.expId)">⬇️</button>
          <button class="icon-btn" @click="fetchDeleteReceipt(expense.expId)">🗑️</button>
        </div>

        <div class="delete-button-area">
          <button class="remove-btn" @click="removeExpenseItem(expense.expId)">❌ 항목 삭제</button>
        </div>
      </li>
    </ul>


        <div class="total">
          총계: {{ totalPrice.toLocaleString() }}₩
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { getTimeline } from '@/lib/api/planAPI'
import { updatePlanAttraction } from '@/lib/api/planattractionAPI'
import { addExpense, getExpenseList, deleteExpense, uploadReceipt, deleteReceipt, downloadReceipt  } from '@/lib/api/planexpenseAPI'
import noImage from '@/assets/img/noimage.png'

const currentTab = ref('timeline')
const route = useRoute()
const plans = ref([])
const showTimeInput = ref([])

const expenses = ref([])
const newExpense = ref({ content: '', price: null })
const planId = ref(null)

const totalPrice = computed(() => {
  return Array.isArray(expenses.value)
    ? expenses.value.reduce((sum, e) => sum + (typeof e?.price === 'number' ? e.price : 0), 0)
    : 0
})

const fetchTimeline = async () => {
  const name = route.query.name
  try {
    const res = await getTimeline(name)
    plans.value = res.data
    showTimeInput.value = new Array(res.data.length).fill(false)
    if (res.data.length > 0) {
      planId.value = res.data[0].planid
      console.log("planId:" + planId.value)
      if (currentTab.value === 'expenses') fetchExpenses()
    }
  } catch (e) {
    console.error('타임라인 불러오기 실패:', e)
  }
}

const toggleTimeInput = (index) => {
  showTimeInput.value[index] = !showTimeInput.value[index]
}

const savePlanAttraction = async (plan) => {
  const time = plan.time?.trim?.() || null
  const memo = plan.exp?.trim?.() || ''

  if (!time && memo === '') {
    alert('⛔ 수정할 내용이 없습니다.')
    return
  }

  try {
    const attractionData = {
      ppid: plan.ppid,
      time,
      exp: memo
    }
    await updatePlanAttraction(attractionData)
    alert('저장되었습니다!')
    await fetchTimeline()
  } catch (e) {
    console.error('저장 실패:', e)
    alert('저장 실패')
  }
}

const fetchExpenses = async () => {
  if (!planId.value) return
  try {
    const res = await getExpenseList(planId.value)
    if (res?.data) {
      expenses.value = res.data.filter(e => e && typeof e === 'object')
    } else {
      expenses.value = []
    }
  } catch (e) {
    console.error('경비 불러오기 실패:', e)
  }
}

const addNewExpense = async () => {
  if (!newExpense.value.content || newExpense.value.price == null) {
    alert('항목과 금액을 입력해주세요')
    return
  }
  try {
    await addExpense({
      planId: planId.value,
      content: newExpense.value.content,
      price: newExpense.value.price
    })
    newExpense.value = { content: '', price: null }
    await fetchExpenses()
  } catch (e) {
    console.error('경비 추가 실패:', e)
  }
}

const removeExpenseItem = async (expId) => {
  try {
    await deleteExpense(expId)
    await fetchExpenses()
  } catch (e) {
    console.error('경비 삭제 실패:', e)
    expenses.value = []
  }
}

const onFileChange = async (event, expId) => {
  const file = event.target.files[0]
  if (!file) return
  const formData = new FormData()
  formData.append('file', file)
  try {
    await uploadReceipt(expId, formData)
    await fetchExpenses()
  } catch (e) {
    console.error('영수증 업로드 실패:', e)
  }
}

const fetchDeleteReceipt = async (expId) => {
  try {
    await deleteReceipt(expId)
    await fetchExpenses()
  } catch (e) {
    console.error('영수증 삭제 실패:', e)
  }
}

const fetchDownloadReceipt = async (expId) => {
  try {
    await downloadReceipt(expId)
  } catch (e) {
    console.error('영수증 다운로드 실패:', e)
  }
}


onMounted(() => {
  fetchTimeline()
})

watch(currentTab, (newVal) => {
  if (newVal === 'expenses') fetchExpenses()
})
</script>

<style scoped>
/* 전체 컨테이너 */
.container {
  max-width: 1600px; /* 더 넓게 확장 */
  margin: 0 auto;
  padding: 0 20px;
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
}

/* 탭 버튼 스타일 */
.tab-buttons {
  display: flex;
  justify-content: center;
  gap: 8px;
  margin-bottom: 40px;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(20px);
  border-radius: 16px;
  padding: 8px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.tab-buttons button {
  padding: 14px 28px;
  border: none;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  background: transparent;
  color: #64748b;
  position: relative;
  overflow: hidden;
}

.tab-buttons button::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
  transition: left 0.5s;
}

.tab-buttons button:hover::before {
  left: 100%;
}

.tab-buttons button.active {
  background: linear-gradient(135deg, #c5e1a5 0%, #81c784 100%);
  color: white;
  box-shadow: 0 8px 24px rgba(132, 204, 22, 0.3);
  transform: translateY(-2px);
}

.tab-buttons button:hover:not(.active) {
  background: rgba(132, 204, 22, 0.1);
  color: #16a34a;
  transform: translateY(-1px);
}

/* 페이지 제목 */
h2 {
  font-size: 2.5rem;
  font-weight: 800;
  background-clip: text;
  text-align: center;
  margin-bottom: 50px;
  letter-spacing: -0.02em;
}

/* 타임라인 스타일 */
.timeline-box {
  margin-top: 30px;
  display: flex;
  justify-content: center;
  width: 100%;
  position: relative;
}

.timeline {
  position: relative;
  max-width: 1400px; /* 더 넓게 확장 */
  margin: auto;
  padding: 60px 0;
  width: 100%;
}

.timeline::after {
  content: '';
  position: absolute;
  z-index: 1;
  width: 6px;
  background: linear-gradient(180deg, #84cc16 0%, #16a34a 50%);
  top: 0;
  bottom: 0;
  left: 50%;
  margin-left: -3px;
  border-radius: 3px;
  box-shadow: 0 0 20px rgba(132, 204, 22, 0.3);
}

.timeline-item {
  padding: 30px 50px; /* 좌우 패딩 증가 */
  position: relative;
  width: 50%;
  box-sizing: border-box;
  animation: fadeInUp 0.8s ease-out;
}

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

.timeline-item.left {
  left: 0;
}

.timeline-item.right {
  left: 50%;
}

.timeline-content {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  padding: 24px;
  border-radius: 20px;
  border: 1px solid rgba(255, 255, 255, 0.3);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
}

.timeline-content::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(135deg, #c5e1a5 0%, #81c784 100%);
  border-radius: 20px 20px 0 0;
}

.timeline-content:hover {
  transform: translateY(-8px);
  box-shadow: 0 32px 64px rgba(0, 0, 0, 0.15);
}

.timeline-content img {
  width: 100%;
  height: 200px;
  border-radius: 16px;
  object-fit: cover;
  margin-bottom: 16px;
  transition: transform 0.3s ease;
}

.timeline-content:hover img {
  transform: scale(1.05);
}

.timeline-content h3 {
  margin: 0 0 8px;
  font-size: 1.4rem;
  font-weight: 700;
  color: #1e293b;
  line-height: 1.3;
}

.timeline-content .address {
  font-size: 14px;
  color: #64748b;
  margin: 0 0 16px;
  display: flex;
  align-items: center;
  gap: 4px;
}

/* 타임라인 아이콘 */
.timeline-icon-left,
.timeline-icon-right {
  position: absolute;
  top: 30px;
  background: linear-gradient(135deg, #c5e1a5 0%, #81c784 100%);
  border: 4px solid white;
  width: 48px;
  height: 48px;
  border-radius: 50%;
  z-index: 20;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8px 24px rgba(132, 204, 22, 0.3);
  transition: all 0.3s ease;
}

.timeline-icon-left {
  right: -44px;
  transform: translateX(-50%);
}

.timeline-icon-right {
  left: 4px;
  transform: translateX(-50%);
}

.timeline-icon-left:hover,
.timeline-icon-right:hover {
  transform: translateX(-50%) scale(1.1);
  box-shadow: 0 12px 32px rgba(132, 204, 22, 0.4);
}

.timeline-icon-left img,
.timeline-icon-right img {
  width: 24px;
  height: 24px;
  filter: brightness(0) invert(1);
  z-index: 999;
}

/* 시간 토글 */
.time-toggle {
  display: flex;
  align-items: center;
  gap: 12px;
  margin: 16px 0;
  padding: 12px;
  background: rgba(132, 204, 22, 0.05);
  border-radius: 12px;
  border: 1px solid rgba(132, 204, 22, 0.1);
}

.time-toggle label {
  font-weight: 600;
  color: #475569;
  font-size: 14px;
}

.time-toggle span {
  font-weight: 600;
  color: #16a34a;
  background: rgba(132, 204, 22, 0.1);
  padding: 4px 8px;
  border-radius: 6px;
}

.time-toggle input[type='time'] {
  border: 2px solid rgba(132, 204, 22, 0.2);
  border-radius: 8px;
  padding: 8px 12px;
  font-size: 14px;
  background: white;
  transition: all 0.3s ease;
}

.time-toggle input[type='time']:focus {
  outline: none;
  border-color: #16a34a;
  box-shadow: 0 0 0 3px rgba(132, 204, 22, 0.1);
}

.btn-time {
  font-size: 12px;
  padding: 6px 12px;
  background: linear-gradient(135deg, #84cc16 0%, #16a34a 100%);
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-weight: 600;
}

.btn-time:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(132, 204, 22, 0.3);
}

/* 메모 영역 */
.memo-label {
  display: block;
  margin: 20px 0 8px;
  font-weight: 600;
  color: #374151;
  font-size: 15px;
}

.memo-box {
  width: 100%;
  border: 2px solid rgba(132, 204, 22, 0.1);
  border-radius: 12px;
  padding: 16px;
  font-size: 14px;
  resize: none;
  background: rgba(248, 250, 252, 0.8);
  transition: all 0.3s ease;
  font-family: inherit;
  line-height: 1.5;
}

.memo-box:focus {
  outline: none;
  border-color: #16a34a;
  box-shadow: 0 0 0 3px rgba(132, 204, 22, 0.1);
  background: white;
}

.memo-box::placeholder {
  color: #9ca3af;
  font-style: italic;
}

/* 저장 버튼 */
.save-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.save-btn {
  padding: 10px 20px; /* 경비 부분에서는 작게 */
  border-radius: 12px;
  background: linear-gradient(135deg, #84cc16 0%, #16a34a 100%);
  color: white;
  border: none;
  font-size: 13px; /* 경비 부분에서는 작게 */
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 4px 16px rgba(132, 204, 22, 0.3);
}

/* 타임라인의 저장 버튼은 크게 유지 */
.timeline-content .save-btn {
  padding: 12px 24px;
  font-size: 14px;
}

.save-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(132, 204, 22, 0.4);
}

.save-btn:active {
  transform: translateY(0);
}

/* 경비 계획 스타일 - 컴팩트하게 수정 */
.expenses-box {
  max-width: 700px; /* 더 작게 */
  margin: 0 auto;
  padding: 24px; /* 패딩 줄임 */
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 20px; /* 모서리 약간 줄임 */
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.3);
  position: relative;
  overflow: hidden;
}

.expenses-box::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(135deg, #c5e1a5 0%, #81c784 100%);
}

.add-expense-form {
  display: flex;
  justify-content: center;
  gap: 12px; /* 간격 줄임 */
  margin-bottom: 24px; /* 마진 줄임 */
  flex-wrap: wrap;
  padding: 18px; /* 패딩 줄임 */
  background: rgba(132, 204, 22, 0.05);
  border-radius: 14px; /* 모서리 약간 줄임 */
  border: 1px solid rgba(132, 204, 22, 0.1);
}

.add-expense-form input {
  padding: 10px 14px; /* 패딩 줄임 */
  border: 2px solid rgba(132, 204, 22, 0.2);
  border-radius: 10px; /* 모서리 약간 줄임 */
  width: 160px; /* 너비 줄임 */
  font-size: 14px; /* 폰트 사이즈 줄임 */
  background: white;
  transition: all 0.3s ease;
  font-family: inherit;
}

.add-expense-form input:focus {
  outline: none;
  border-color: #16a34a;
  box-shadow: 0 0 0 3px rgba(132, 204, 22, 0.1);
}

.add-expense-form input::placeholder {
  color: #9ca3af;
}

.expense-list {
  list-style: none;
  padding: 0;
  margin-bottom: 24px; /* 마진 줄임 */
  display: flex;
  flex-direction: column;
  gap: 12px; /* 간격 줄임 */
}

.expense-item {
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(132, 204, 22, 0.1);
  border-radius: 14px; /* 모서리 약간 줄임 */
  padding: 18px; /* 패딩 줄임 */
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.05);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
}

.expense-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: linear-gradient(135deg, #c5e1a5 0%, #81c784 100%);
}

.expense-item:hover {
  transform: translateY(-4px);
  box-shadow: 0 16px 32px rgba(0, 0, 0, 0.1);
}

.expense-header {
  margin-bottom: 12px; /* 마진 줄임 */
}

.expense-title {
  font-size: 16px; /* 폰트 사이즈 줄임 */
  font-weight: 700;
  color: #1e293b;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px; /* 간격 줄임 */
}

.expense-price {
  font-weight: 700;
  color: #16a34a;
  font-size: 16px; /* 폰트 사이즈 줄임 */
  background: rgba(132, 204, 22, 0.1);
  padding: 4px 10px; /* 패딩 줄임 */
  border-radius: 6px; /* 모서리 약간 줄임 */
}

.receipt-actions {
  display: flex;
  align-items: center;
  gap: 10px; /* 간격 줄임 */
  flex-wrap: wrap;
  margin-bottom: 12px; /* 마진 줄임 */
}

.receipt-preview img {
  max-height: 60px; /* 높이 줄임 */
  border-radius: 10px; /* 모서리 약간 줄임 */
  border: 2px solid rgba(132, 204, 22, 0.2);
  object-fit: cover;
  transition: transform 0.3s ease;
}

.receipt-preview img:hover {
  transform: scale(1.05);
}

.custom-upload {
  background: linear-gradient(135deg, #c5e1a5 0%, #81c784 100%);
  color: white;
  border: none;
  padding: 8px 14px; /* 패딩 줄임 */
  border-radius: 8px; /* 모서리 약간 줄임 */
  font-size: 12px; /* 폰트 사이즈 줄임 */
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(132, 204, 22, 0.3);
}

.custom-upload:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(132, 204, 22, 0.4);
}

.icon-btn {
  background: rgba(248, 250, 252, 0.8);
  border: 2px solid rgba(132, 204, 22, 0.2);
  padding: 8px 10px; /* 패딩 줄임 */
  border-radius: 8px; /* 모서리 약간 줄임 */
  font-size: 14px; /* 폰트 사이즈 약간 줄임 */
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
}

.icon-btn:hover {
  background: rgba(132, 204, 22, 0.1);
  border-color: #16a34a;
  transform: translateY(-1px);
}

.delete-button-area {
  display: flex;
  justify-content: flex-end;
  margin-top: 12px; /* 마진 줄임 */
}

.remove-btn {
  background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
  color: white;
  border: none;
  border-radius: 8px; /* 모서리 약간 줄임 */
  padding: 6px 12px; /* 패딩 줄임 */
  cursor: pointer;
  font-weight: 600;
  font-size: 12px; /* 폰트 사이즈 줄임 */
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(239, 68, 68, 0.3);
}

.remove-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(239, 68, 68, 0.4);
}

/* 총계 */
.total {
  font-size: 1.3rem; /* 폰트 사이즈 줄임 */
  font-weight: 800;
  text-align: center;
  padding: 16px; /* 패딩 줄임 */
  background: linear-gradient(135deg, #c5e1a5 0%, #81c784 100%);
  color: white;
  border-radius: 14px; /* 모서리 약간 줄임 */
  box-shadow: 0 8px 24px rgba(132, 204, 22, 0.3);
  letter-spacing: -0.02em;
}

/* 반응형 디자인 */
@media (max-width: 768px) {
  .timeline::after {
    left: 20px;
  }

  .timeline-item {
    width: 100%;
    left: 0 !important;
    padding-left: 60px;
    padding-right: 20px; /* 모바일에서 우측 패딩 조정 */
  }

  .timeline-icon-left,
  .timeline-icon-right {
    left: 0 !important;
    right: auto !important;
    transform: translateX(0) !important;
  }

  .tab-buttons {
    flex-direction: column;
    gap: 8px;
  }

  .add-expense-form {
    flex-direction: column;
    align-items: center;
  }

  .add-expense-form input {
    width: 100%;
    max-width: 250px; /* 모바일에서 너비 줄임 */
  }

  .expense-title {
    flex-direction: column;
    align-items: flex-start;
    gap: 6px; /* 간격 줄임 */
  }

  .receipt-actions {
    justify-content: center;
  }
}

/* 스크롤바 스타일링 */
::-webkit-scrollbar {
  width: 8px;
}

::-webkit-scrollbar-track {
  background: rgba(248, 250, 252, 0.5);
  border-radius: 4px;
}

::-webkit-scrollbar-thumb {
  background: linear-gradient(135deg, #84cc16 0%, #16a34a 100%);
  border-radius: 4px;
}

::-webkit-scrollbar-thumb:hover {
  background: linear-gradient(135deg, #65a30d 0%, #15803d 100%);
}

/* 부드러운 애니메이션 */
* {
  scroll-behavior: smooth;
}

/* 추가 호버 효과 */
.timeline-content,
.expense-item,
.tab-buttons button,
.save-btn,
.custom-upload,
.icon-btn,
.remove-btn {
  will-change: transform;
}

/* 글래스모피즘 효과 강화 */
.timeline-content,
.expenses-box,
.tab-buttons {
  background: rgba(255, 255, 255, 0.25);
  box-shadow: 0 8px 32px 0 rgba(31, 38, 135, 0.37);
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
  border: 1px solid rgba(255, 255, 255, 0.18);
}


</style>
