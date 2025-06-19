<template>
  <div class="container plan-container">
    <div class="position-relative mb-4">
      <h2 class="page-title text-center w-100 m-0">여행 계획</h2>
      <button
        @click="makePlan"
        class="btn add-btn position-absolute end-0 top-50 translate-middle-y">
        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
          <line x1="12" y1="5" x2="12" y2="19"></line>
          <line x1="5" y1="12" x2="19" y2="12"></line>
        </svg>
      </button>
    </div>

    <div v-if="plans.length === 0" class="empty-state">
      <div class="empty-icon">📋</div>
      <p class="empty-text">아직 여행 계획이 없습니다</p>
      <p class="empty-subtext">새로운 여행을 계획해보세요!</p>
    </div>

    <div v-else>
      <div class="plans-grid">
        <div v-for="plan in paginatedPlans" :key="plan.pid" class="plan-card">
          <div class="plan-header">
            <h3 class="plan-title">
              <RouterLink :to="{ name: 'PlanDetail', query: { name: plan.name } }">
                {{ plan.name }}
              </RouterLink>
            </h3>
            <div class="plan-date">{{ formatDate(plan.createdDate) }}</div>
          </div>
          <div class="plan-author">
            <span class="author-label">작성자</span>
            <span class="author-name">{{ plan.memberName }}</span>
          </div>
        </div>
      </div>

      <div class="pagination">
        <button class="page-btn" :disabled="currentPage === 1" @click="currentPage--">◀ 이전</button>
        <span class="page-info">{{ currentPage }} / {{ totalPages }}</span>
        <button class="page-btn" :disabled="currentPage === totalPages" @click="currentPage++">다음 ▶</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { getPlans, createPlan } from '@/lib/api/planAPI'

const plans = ref([])
const currentPage = ref(1)
const pageSize = 9
const router = useRouter()

onMounted(async () => {
  try {
    plans.value = await getPlans()
  } catch (error) {
    console.error('계획 불러오기 실패', error)
  }
})

const paginatedPlans = computed(() =>
  plans.value.slice((currentPage.value - 1) * pageSize, currentPage.value * pageSize)
)

const totalPages = computed(() =>
  Math.ceil(plans.value.length / pageSize)
)

const makePlan = async () => {
  const name = prompt('여행 계획 이름을 입력하세요.')
  if (!name) return

  try {
    const response = await createPlan(name)
    if (response.state === 200) {
      plans.value = await getPlans()
      currentPage.value = 1 // 첫 페이지로 초기화
    }
  } catch (error) {
    console.error('계획 생성 실패', error)
  }
}

const formatDate = (dateString) => {
  const date = new Date(dateString)
  return date.toLocaleDateString('ko-KR', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}
</script>

<style lang="scss" scoped>
.plan-container {
  max-width: 1200px;
  margin: 3rem auto;
  padding: 2.5rem;
  background: linear-gradient(135deg, #f4fcef 0%, #d0eabf 100%);
  border-radius: 20px;
  box-shadow: 0 8px 32px rgba(106, 156, 106, 0.1);
  min-height: 500px;
}

.page-title {
  font-size: 2.2rem;
  font-weight: 700;
  color: #2d5a2d;
  letter-spacing: -0.5px;
  margin-bottom: 0;
}

.add-btn {
  width: 48px;
  height: 48px;
  background: linear-gradient(135deg, #7cb342 0%, #8bc34a 100%);
  border: none;
  border-radius: 50%;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(139, 195, 74, 0.3);

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 6px 20px rgba(139, 195, 74, 0.4);
    background: linear-gradient(135deg, #8bc34a 0%, #9ccc65 100%);
  }

  &:active {
    transform: translateY(0);
  }
}

.empty-state {
  text-align: center;
  padding: 4rem 2rem;
  color: #5a7a5a;

  .empty-icon {
    font-size: 4rem;
    margin-bottom: 1.5rem;
    opacity: 0.7;
  }

  .empty-text {
    font-size: 1.3rem;
    font-weight: 600;
    margin-bottom: 0.5rem;
    color: #4a6741;
  }

  .empty-subtext {
    font-size: 1rem;
    opacity: 0.8;
    margin: 0;
  }
}

.plans-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 1.5rem;
  margin-top: 1rem;
}

.plan-card {
  background: white;
  border-radius: 16px;
  padding: 1.8rem;
  box-shadow: 0 4px 20px rgba(106, 156, 106, 0.08);
  border: 1px solid rgba(139, 195, 74, 0.1);
  transition: all 0.3s ease;

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 12px 40px rgba(106, 156, 106, 0.15);
    border-color: rgba(139, 195, 74, 0.2);
  }
}

.plan-header {
  margin-bottom: 1.2rem;
  padding-bottom: 1rem;
  border-bottom: 1px solid #f0f8f0;
}

.plan-title {
  margin: 0 0 0.8rem 0;
  font-size: 1.2rem;
  font-weight: 600;

  a {
    color: #2d5a2d;
    text-decoration: none;
    transition: color 0.2s ease;

    &:hover {
      color: #7cb342;
    }
  }
}

.plan-date {
  font-size: 0.9rem;
  color: #6a9c6a;
  font-weight: 500;
}

.plan-author {
  display: flex;
  align-items: center;
  gap: 0.8rem;
}

.author-label {
  font-size: 0.85rem;
  color: #7a8a7a;
  font-weight: 500;
}

.author-name {
  font-size: 0.95rem;
  color: #4a6741;
  font-weight: 600;
  padding: 0.3rem 0.8rem;
  background: linear-gradient(135deg, #e8f5e8 0%, #f0f8f0 100%);
  border-radius: 12px;
  border: 1px solid rgba(139, 195, 74, 0.15);
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 1.5rem;
  margin-top: 2rem;
}

.page-btn {
  background: linear-gradient(135deg, #7cb342, #8bc34a);
  border: none;
  color: white;
  padding: 0.5rem 1rem;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.3s;

  &:disabled {
    background: #dcefdc;
    color: #9e9e9e;
    cursor: not-allowed;
  }
}

.page-info {
  font-weight: 500;
  color: #4a6741;
}

@media (max-width: 768px) {
  .plan-container {
    margin: 2rem 1rem;
    padding: 1.5rem;
  }

  .page-title {
    font-size: 1.8rem;
  }

  .add-btn {
    width: 44px;
    height: 44px;
  }

  .plans-grid {
    grid-template-columns: 1fr;
    gap: 1rem;
  }
}
</style>
