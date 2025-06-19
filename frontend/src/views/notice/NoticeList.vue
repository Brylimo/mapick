<template>
  <div class="container post-container">
    <div class="header-section">
      <div class="notice-badge">
        <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path>
          <path d="M13 8H7"></path>
          <path d="M17 12H7"></path>
        </svg>
        공지사항
      </div>
      <h3 class="page-title">공지사항</h3>
      <div class="title-underline"></div>
    </div>

    <div class="action-bar">
      <div class="notice-info">
        <span class="notice-count">총 {{ notices.length }}개의 공지사항</span>
      </div>
      <RouterLink :to="{ name: 'NoticeCreate' }" class="btn write-btn">
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M14.828 14.828a4 4 0 0 1-5.656 0M9 10h1m4 0h1m-6 4h1m0 0h1"></path>
          <path d="m15 5 4 4L7 21H3v-4L15 5z"></path>
        </svg>
        글 작성
      </RouterLink>
    </div>

    <div v-if="notices.length === 0" class="empty-notice">
      <div class="empty-icon">📢</div>
      <p class="empty-title">등록된 공지사항이 없습니다</p>
      <p class="empty-subtitle">새로운 공지사항을 작성해보세요</p>
    </div>

    <div v-else class="notice-table-container">
      <table class="notice-table">
        <thead>
          <tr>
            <th class="number-header">번호</th>
            <th class="title-header">제목</th>
            <th class="author-header">작성자</th>
            <th class="date-header">작성일</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(notice, index) in notices" :key="notice.id" class="notice-row">
            <td class="number-cell">
              <div class="notice-number">{{ index + 1 }}</div>
            </td>
            <td class="title-cell">
              <RouterLink :to="{ name: 'NoticeDetail', params: { id: notice.id } }" class="notice-title-link">
                <div class="title-content">
                  <span class="title-text">{{ notice.title }}</span>
                  <div class="title-indicator"></div>
                </div>
              </RouterLink>
            </td>
            <td class="author-cell">
              <div class="author-badge">{{ notice.authorName }}</div>
            </td>
            <td class="date-cell">
              <div class="date-info">{{ formatDate(notice.createdAt) }}</div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getNotices } from '@/lib/api/noticeAPI'
import { RouterLink } from 'vue-router'

const notices = ref([])

onMounted(async () => {
  try {
    notices.value = await getNotices()
  } catch (err) {
    console.error('공지사항 불러오기 실패:', err)
  }
})

const formatDate = (dateStr) => {
  const date = new Date(dateStr)
  return date.toLocaleDateString('ko-KR', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit'
  })
}
</script>

<style scoped>
.post-container {
  max-width: 1200px;
  margin: 3rem auto;
  padding: 2.5rem;
  background: linear-gradient(145deg, #f8fdf8 0%, #f0f8f0 50%, #e8f5e8 100%);
  border-radius: 24px;
  box-shadow:
    0 10px 40px rgba(106, 156, 106, 0.12),
    0 0 0 1px rgba(139, 195, 74, 0.08);
  position: relative;
  overflow: hidden;
}

.post-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #7cb342, #8bc34a, #aed581);
}

.header-section {
  text-align: center;
  margin-bottom: 3rem;
  position: relative;
}

.notice-badge {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  background: linear-gradient(135deg, #7cb342 0%, #8bc34a 100%);
  color: white;
  padding: 0.6rem 1.2rem;
  border-radius: 20px;
  font-size: 0.9rem;
  font-weight: 600;
  margin-bottom: 1.5rem;
  box-shadow: 0 4px 12px rgba(139, 195, 74, 0.3);
}

.page-title {
  font-size: 2.4rem;
  font-weight: 800;
  color: #1b4332;
  margin-bottom: 0.3rem;
  letter-spacing: -1px;
}

.title-underline {
  width: 120px;
  height: 3px;
  background: linear-gradient(90deg, #7cb342, #8bc34a);
  border-radius: 2px;
  margin: 0 auto;
}

.action-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
  padding: 1rem 1.5rem;
  background: white;
  border-radius: 16px;
  box-shadow: 0 2px 12px rgba(106, 156, 106, 0.08);
  border: 1px solid rgba(139, 195, 74, 0.15);
}

.notice-info {
  display: flex;
  align-items: center;
}

.notice-count {
  color: #4a6741;
  font-weight: 600;
  font-size: 1rem;
}

.write-btn {
  background: linear-gradient(135deg, #6a9c6a 0%, #7cb342 100%);
  border: none;
  color: white;
  padding: 0.5rem 1.8rem;
  border-radius: 12px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 0.6rem;
  transition: all 0.3s ease;
  box-shadow: 0 4px 16px rgba(106, 156, 106, 0.25);
  text-decoration: none;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 8px 25px rgba(106, 156, 106, 0.35);
    background: linear-gradient(135deg, #7cb342 0%, #8bc34a 100%);
    color: white;
  }
}

.empty-notice {
  text-align: center;
  padding: 5rem 2rem;
  background: white;
  border-radius: 20px;
  box-shadow: 0 4px 20px rgba(106, 156, 106, 0.08);
  border: 2px dashed rgba(139, 195, 74, 0.3);

  .empty-icon {
    font-size: 5rem;
    margin-bottom: 2rem;
    opacity: 0.7;
  }

  .empty-title {
    font-size: 1.4rem;
    font-weight: 700;
    color: #2d5a2d;
    margin-bottom: 0.8rem;
  }

  .empty-subtitle {
    font-size: 1.1rem;
    color: #6a9c6a;
    margin: 0;
  }
}

.notice-table-container {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 8px 32px rgba(106, 156, 106, 0.12);
  border: 1px solid rgba(139, 195, 74, 0.15);
}

.notice-table {
  width: 100%;
  border-collapse: collapse;
  margin: 0;
  font-size: 0.97rem;
}

.notice-table thead {
  background: linear-gradient(135deg, #2d5a2d 0%, #4a6741 100%);
}

.notice-table th {
  padding: 0.7rem 0.5rem;
  font-weight: 700;
  color: white;
  text-align: center;
  font-size: 0.95rem;
  letter-spacing: 0.5px;
  border: none;
  position: relative;
}

.number-header { width: 10%; }
.title-header { width: 50%; }
.author-header { width: 20%; }
.date-header { width: 20%; }

.notice-row {
  transition: all 0.3s ease;
  border-bottom: 1px solid rgba(139, 195, 74, 0.12);
  position: relative;

  &:hover {
    background: linear-gradient(90deg, rgba(139, 195, 74, 0.04) 0%, rgba(139, 195, 74, 0.08) 50%, rgba(139, 195, 74, 0.04) 100%);
    transform: translateX(4px);
  }

  &:last-child {
    border-bottom: none;
  }
}

.notice-table td {
  padding: 0.6rem 0.5rem;
  text-align: center;
  vertical-align: middle;
  font-size: 0.95rem;
}

.notice-number {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 22px;
  height: 22px;
  background: linear-gradient(135deg, #e8f5e8 0%, #f0f8f0 100%);
  border-radius: 50%;
  color: #4a6741;
  font-weight: 700;
  font-size: 0.8rem;
}

.title-cell {
  text-align: left;
}

.notice-title-link {
  text-decoration: none;
  display: block;
}

.title-content {
  display: flex;
  align-items: center;
  gap: 0.3rem;
  padding: 0.3rem 0.5rem;
  border-radius: 6px;
  font-size: 0.95rem;
  transition: all 0.2s ease;

  &:hover {
    background: rgba(139, 195, 74, 0.1);
  }
}

.title-text {
  color: #1b4332;
  font-weight: 600;
  font-size: 1.05rem;
  flex: 1;
}

.title-indicator {
  width: 6px;
  height: 6px;
  background: #8bc34a;
  border-radius: 50%;
  opacity: 0;
  transition: opacity 0.2s ease;
}

.notice-title-link:hover .title-indicator {
  opacity: 1;
}

.author-badge {
  display: inline-block;
  background: linear-gradient(135deg, #f0f8f0 0%, #e8f5e8 100%);
  color: #2d5a2d;
  padding:  0.2rem 0.6rem;
  border-radius: 7px;
  font-weight: 600;
  font-size: 0.8rem;
  border: 1px solid rgba(139, 195, 74, 0.2);
}

.date-info {
  color: #6a9c6a;
  font-weight: 600;
  font-size: 0.8rem;
  font-family: 'Courier New', monospace;
}

@media (max-width: 768px) {
  .post-container {
    margin: 2rem 1rem;
    padding: 1.5rem;
  }

  .page-title {
    font-size: 2rem;
  }

  .action-bar {
    flex-direction: column;
    gap: 1rem;
    padding: 1rem;
  }

  .notice-table th,
  .notice-table td {
    padding: 0.4rem 0.2rem;
    font-size: 0.78rem;
  }

  .author-header,
  .date-header {
    display: none;
  }

  .author-cell,
  .date-cell {
    display: none;
  }

  .number-header { width: 15%; }
  .title-header { width: 85%; }
}
</style>
