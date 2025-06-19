<template>
    <div class="container post-container">
        <div class="title-section">
            <h3 class="page-title">커뮤니티</h3>
            <div class="title-decoration"></div>
        </div>

        <div class="controls-section">
            <button class="btn create-btn" @click="goToCreateForm">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <line x1="12" y1="5" x2="12" y2="19"></line>
                    <line x1="5" y1="12" x2="19" y2="12"></line>
                </svg>
                커뮤니티 생성
            </button>

            <div class="search-group">
                <select v-model="searchOpt" class="search-select">
                    <option value="">검색조건</option>
                    <option value="name">이름</option>
                    <option value="category">카테고리</option>
                    <option value="tag">태그</option>
                </select>
                <input
                    type="text"
                    v-model="searchQuery"
                    class="search-input"
                    placeholder="검색어를 입력하세요..."
                />
                <button class="btn search-btn" @click="search">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <circle cx="11" cy="11" r="8"></circle>
                        <path d="m21 21-4.35-4.35"></path>
                    </svg>
                    검색
                </button>
            </div>
        </div>

        <div v-if="communities.length === 0" class="empty-state">
            <div class="empty-icon">🌱</div>
            <p class="empty-text">등록된 커뮤니티가 없습니다</p>
            <p class="empty-subtext">새로운 커뮤니티를 만들어보세요!</p>
        </div>

        <div v-else class="table-wrapper">
            <table class="community-table">
                <thead>
                    <tr>
                        <th class="number-col">번호</th>
                        <th class="title-col">커뮤니티 제목</th>
                    </tr>
                </thead>
                <tbody>
                    <tr v-for="(community, idx) in paginatedCommunities" :key="community.communityId" class="table-row">
                      <td class="number-cell">{{ (currentPage - 1) * pageSize + idx + 1 }}</td>
                      <td class="title-cell">
                        <RouterLink :to="{ name: 'communityMain', params: { id: community.communityId } }" class="community-link">
                          {{ community.communityName }}
                        </RouterLink>
                      </td>
                    </tr>
                </tbody>
            </table>
            <div class="pagination">
              <button class="page-btn" :disabled="currentPage === 1" @click="currentPage--">◀ 이전</button>
              <span class="page-info">{{ currentPage }} / {{ totalPages }}</span>
              <button class="page-btn" :disabled="currentPage === totalPages" @click="currentPage++">다음 ▶</button>
            </div>
        </div>
    </div>

</template>

<script setup>
import { ref, computed, watchEffect } from 'vue'
import { useRouter } from 'vue-router'

import { useGetCommunityListQuery } from '../../hooks/queries/community/useGetCommunityListQuery'
import { getSearchedCommunities } from '@/lib/api/communityAPI'

const router = useRouter()

const communities = ref([])
const searchOpt = ref('')
const searchQuery = ref('')

const currentPage = ref(1)
const pageSize = 8

const { data: communityData } = useGetCommunityListQuery()

watchEffect(() => {
  if (communityData.value) {
    communities.value = communityData.value
  }
})

const paginatedCommunities = computed(() =>
  communities.value.slice((currentPage.value - 1) * pageSize, currentPage.value * pageSize)
)

const totalPages = computed(() =>
  Math.ceil(communities.value.length / pageSize)
)

const search = async () => {
  if (!searchOpt.value) {
    alert('검색조건을 설정해주세요!')
    return
  }

  try {
    const res = await getSearchedCommunities({
      opt: searchOpt.value,
      query: searchQuery.value
    })
    communities.value = Array.isArray(res.data) ? res.data : []
    currentPage.value = 1 // 검색 결과 첫 페이지로
  } catch (e) {
    console.error(e)
    alert('검색 도중 오류가 발생했습니다')
  }
}

const goToCreateForm = () => {
  router.push('/community/community-form')
}
</script>

<style scoped>
/* 커뮤니티 리스트 스타일 (연녹색 테마 적용 전체) */

.post-container {
  max-width: 1200px;
  margin: 2rem auto;
  padding: 2rem;
  background: white;
  border-radius: 12px;
  border: 1px solid #e5e7eb;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.05);
  min-height: 500px;
}

.title-section {
  text-align: center;
  margin: 1rem 0 2.5rem 0;
}

.page-title {
  font-size: 1.8rem;
  font-weight: 600;
  color: #1a1a1a;
  margin-bottom: 1rem;
  letter-spacing: -0.3px;
}

.title-decoration {
  width: 40px;
  height: 2px;
  background: linear-gradient(135deg, #81c784, #66bb6a);
  border-radius: 2px;
  margin: 0 auto;
}

.controls-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
  gap: 1.5rem;
  flex-wrap: wrap;
}

.create-btn,
.search-btn,
.page-btn {
  background: linear-gradient(135deg, #81c784, #66bb6a);
  color: white;
  box-shadow: 0 2px 6px rgba(129, 199, 132, 0.25);
  border: none;
  font-weight: 600;
  transition: all 0.3s ease;
  cursor: pointer;
  padding: 0.6rem 1.2rem;
  border-radius: 6px;
  font-size: 0.85rem;
  display: flex;
  align-items: center;
  gap: 0.4rem;
}

.create-btn:hover:not(:disabled),
.search-btn:hover:not(:disabled),
.page-btn:hover:not(:disabled) {
  background: linear-gradient(135deg, #66bb6a, #4caf50);
  color: white;
  transform: translateY(-1px);
  box-shadow: 0 3px 8px rgba(76, 175, 80, 0.4);
}

.create-btn:disabled,
.search-btn:disabled,
.page-btn:disabled {
  background: #f3f4f6;
  color: #9ca3af;
  cursor: not-allowed;
  box-shadow: none;
  transform: none;
}

.search-group {
  display: flex;
  gap: 0.6rem;
  align-items: center;
  flex: 1;
  max-width: 500px;
}

.search-select,
.search-input {
  border: 1px solid #d1d5db;
  border-radius: 6px;
  padding: 0.6rem 1rem;
  font-size: 0.85rem;
  font-weight: 400;
  transition: border-color 0.2s ease;
  background: white;
}

.search-select {
  min-width: 120px;
  color: #374151;
}

.search-input {
  flex: 1;
  min-width: 200px;
}

.search-input:focus,
.search-select:focus {
  border-color: #66bb6a;
  outline: none;
  box-shadow: 0 0 0 3px rgba(129, 199, 132, 0.2);
}

.search-input::placeholder {
  color: #9ca3af;
  font-weight: 400;
}

.empty-state {
  text-align: center;
  padding: 4rem 2rem;
  color: #6b7280;
}

.empty-state .empty-icon {
  font-size: 3rem;
  margin-bottom: 1.5rem;
  opacity: 0.7;
}

.empty-state .empty-text {
  font-size: 1.2rem;
  font-weight: 600;
  margin-bottom: 0.5rem;
  color: #374151;
}

.empty-state .empty-subtext {
  font-size: 0.95rem;
  opacity: 0.8;
  margin: 0;
  font-weight: 400;
}

.table-wrapper {
  background: white;
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid #e5e7eb;
}

.community-table {
  width: 100%;
  margin: 0;
  border-collapse: collapse;
}

.community-table thead {
  background: linear-gradient(135deg, #f0fdf4, #ecfdf5);
}

.community-table th {
  padding: 1rem;
  font-weight: 600;
  color: #065f46;
  text-align: center;
  font-size: 0.9rem;
  border-bottom: 1px solid #e5e7eb;
}

.number-col {
  width: 15%;
}

.title-col {
  width: 85%;
}

.table-row {
  transition: background-color 0.2s ease;
  border-bottom: 1px solid #f3f4f6;
}

.table-row:hover {
  background: linear-gradient(135deg, #f0fdf4, #ecfdf5);
}

.number-cell,
.title-cell {
  padding: 1rem;
  font-size: 0.9rem;
}

.number-cell {
  text-align: center;
  font-weight: 500;
  color: #6b7280;
}

.title-cell {
  text-align: left;
  padding-left: 1.5rem;
}

.community-link {
  color: #1f2937;
  text-decoration: none;
  font-weight: 700;
  display: inline-block;
  padding: 0.5rem 0;
  transition: color 0.2s ease;
}

.community-link:hover {
  color: #059669;
  text-shadow: 0 0 8px rgba(167, 243, 208, 0.4);
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 1.5rem;
  padding: 1.5rem;
  background: linear-gradient(135deg, #f0fdf4, #ecfdf5);
  border-top: 1px solid #e5e7eb;
}

.page-info {
  font-weight: 500;
  font-size: 0.9rem;
  color: #065f46;
}

@media (max-width: 768px) {
  .post-container {
    margin: 1rem;
    padding: 1.5rem;
    border-radius: 8px;
  }

  .controls-section {
    flex-direction: column;
    gap: 1.5rem;
    align-items: stretch;
  }

  .search-group {
    flex-direction: column;
    gap: 0.8rem;
    max-width: none;
  }

  .search-input {
    min-width: auto;
  }

  .page-title {
    font-size: 1.5rem;
  }

  .community-table th,
  .community-table td {
    padding: 0.8rem 0.6rem;
    font-size: 0.85rem;
  }

  .title-cell {
    padding-left: 1rem;
  }

  .pagination {
    gap: 1rem;
    padding: 1.2rem 1rem;
  }

  .page-btn {
    padding: 0.5rem 1rem;
    font-size: 0.8rem;
  }
}

</style>
