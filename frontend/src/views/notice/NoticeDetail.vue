<template>
  <div class="notice-detail">
    <!-- Hero Section -->
    <div class="hero-section">
      <div class="hero-content">
        <div class="breadcrumb">
          <span class="breadcrumb-item">홈</span>
          <span class="breadcrumb-separator">›</span>
          <span class="breadcrumb-item active">공지사항</span>
        </div>
      </div>
    </div>

    <div class="main-container">
      <!-- Notice Not Found -->
      <div v-if="!notice" class="empty-state">
        <div class="empty-icon">📄</div>
        <h3>공지사항을 찾을 수 없습니다</h3>
        <p>요청하신 공지사항이 존재하지 않거나 삭제되었습니다.</p>
        <button class="btn-primary" @click="goToList">
          목록으로 돌아가기
        </button>
      </div>

      <!-- Notice Content -->
      <div v-else class="notice-container">
        <!-- Notice Header -->
        <div class="notice-header">
          <div class="notice-meta">
            <span class="notice-badge">공지</span>
            <span class="notice-date">{{ formatDate(notice.createdAt) }}</span>
          </div>
          <h1 class="notice-title">{{ notice.title }}</h1>

          <!-- Author Info -->
          <div class="author-section">
            <div class="author-avatar">
              <img src="@/assets/img/user.png" alt="작성자" />
            </div>
            <div class="author-info">
              <div class="author-name">{{ notice.authorName }}</div>
              <div class="author-role">관리자</div>
            </div>
          </div>
        </div>

        <!-- Notice Content -->
        <div class="notice-content">
          <div class="content-wrapper">
            <p class="content-text">{{ notice.content }}</p>
          </div>
        </div>

        <!-- Action Buttons -->
        <div class="action-section">
          <button class="btn-secondary" @click="goToList">
            <span class="btn-icon">←</span>
            목록으로
          </button>

          <div class="admin-actions" v-if="globalStatus.isLoggedIn && Number(notice.authorId) === globalStatus.loginUser.mno">
            <button class="btn-edit" @click="goToEdit">
              <span class="btn-icon">✏️</span>
              수정
            </button>
            <button class="btn-delete" @click="() => delNotice(notice.id)">
              <span class="btn-icon">🗑️</span>
              삭제
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, inject } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getNotice, deleteNotice } from '@/lib/api/noticeAPI'

const route = useRoute()
const router = useRouter()

const notice = ref(null)
const globalStatus = inject('globalStatus')

const fetchNotice = async () => {
  try {
    const res = await getNotice(route.params.id)
    notice.value = res.data
  } catch (err) {
    console.error('공지사항 상세 조회 실패:', err)
  }
}

const delNotice = async () => {
  if (!confirm('정말 삭제하시겠습니까?')) return

  try {
    const res = await deleteNotice(route.params.id)
    if (res.result === 'success') {
      alert('삭제되었습니다.')
      router.push({ name: 'NoticeList' })
    } else {
      alert('삭제 실패: ' + (res.message || '알 수 없는 오류'))
    }
  } catch (err) {
    console.error('삭제 오류:', err)
    alert('삭제 중 오류가 발생했습니다.')
  }
}

const goToList = () => router.push({ name: 'NoticeList' })
const goToEdit = () => router.push({ name: 'NoticeUpdate', params: { id: notice.value.id } })

const formatDate = (str) => {
  const date = new Date(str)
  return date.toLocaleDateString('ko-KR', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

onMounted(() => {
  fetchNotice()
})
</script>

<style lang="scss" scoped>
.notice-detail {
  min-height: 100vh;
  background: #f8fff8;
}

/* Hero Section */
.hero-section {
  position: relative;
  padding: 100px 0 80px;
  background: linear-gradient(135deg, #f0f9e8 0%, #d0eabf 100%);
  color: white;
  overflow: hidden;
}

.hero-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  text-align: center;
  position: relative;
  z-index: 2;
}

.breadcrumb {
  margin-bottom: 20px;
  opacity: 0.9;
}

.breadcrumb-item {
  font-size: 14px;
  font-weight: 500;
}

.breadcrumb-separator {
  margin: 0 10px;
  opacity: 0.7;
}

.breadcrumb-item.active {
  color: #ffffff;
}

.hero-title {
  font-size: 3.5rem;
  font-weight: 800;
  margin-bottom: 20px;
  color: white;
}

/* Main Container */
.main-container {
  max-width: 1000px;
  margin: -50px auto 0;
  padding: 0 20px 80px;
  position: relative;
  z-index: 2;
}

/* Empty State */
.empty-state {
  background: white;
  border-radius: 20px;
  padding: 80px 40px;
  text-align: center;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
}

.empty-icon {
  font-size: 4rem;
  margin-bottom: 30px;
}

.empty-state h3 {
  font-size: 1.8rem;
  font-weight: 700;
  color: #2d3748;
  margin-bottom: 15px;
}

.empty-state p {
  color: #718096;
  font-size: 1.1rem;
  margin-bottom: 30px;
}

/* Notice Container */
.notice-container {
  background: white;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
}

/* Notice Header */
.notice-header {
  padding: 50px 50px 40px;
  background: linear-gradient(135deg, #f7fafc 0%, #edf2f7 100%);
  border-bottom: 1px solid #e2e8f0;
}

.notice-meta {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 25px;
}

.notice-badge {
  background: linear-gradient(135deg, #7dd87a, #5cb85c);
  color: white;
  padding: 6px 16px;
  border-radius: 20px;
  font-size: 0.875rem;
  font-weight: 600;
}

.notice-date {
  color: #718096;
  font-size: 0.9rem;
  font-weight: 500;
}

.notice-title {
  font-size: 2.2rem;
  font-weight: 800;
  color: #2d3748;
  margin-bottom: 30px;
  line-height: 1.3;
}

.author-section {
  display: flex;
  align-items: center;
  gap: 15px;
}

.author-avatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  overflow: hidden;
  border: 3px solid white;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.author-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.author-info .author-name {
  font-weight: 700;
  color: #2d3748;
  font-size: 1.1rem;
}

.author-info .author-role {
  color: #718096;
  font-size: 0.9rem;
}

/* Notice Content */
.notice-content {
  padding: 50px;
}

.content-wrapper {
  background: #f7fafc;
  border-radius: 15px;
  padding: 40px;
  border-left: 5px solid #7dd87a;
}

.content-text {
  font-size: 1.1rem;
  line-height: 1.8;
  color: #4a5568;
  margin: 0;
  white-space: pre-wrap;
}

/* Action Section */
.action-section {
  padding: 30px 50px 50px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #f7fafc;
  border-top: 1px solid #e2e8f0;
}

.admin-actions {
  display: flex;
  gap: 12px;
}

/* Buttons */
.btn-primary, .btn-secondary, .btn-edit, .btn-delete {
  padding: 12px 24px;
  border-radius: 12px;
  font-weight: 600;
  font-size: 0.95rem;
  border: none;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 8px;
  text-decoration: none;
}

.btn-primary {
  background: linear-gradient(135deg, #7dd87a, #5cb85c);
  color: white;
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(125, 216, 122, 0.4);
}

.btn-secondary {
  background: #e2e8f0;
  color: #4a5568;
}

.btn-secondary:hover {
  background: #cbd5e0;
  transform: translateY(-1px);
}

.btn-edit {
  background: linear-gradient(135deg, #48bb78, #38a169);
  color: white;
}

.btn-edit:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(72, 187, 120, 0.4);
}

.btn-delete {
  background: linear-gradient(135deg, #f56565, #e53e3e);
  color: white;
}

.btn-delete:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(245, 101, 101, 0.4);
}

.btn-icon {
  font-size: 1rem;
}

/* Responsive */
@media (max-width: 768px) {
  .hero-title {
    font-size: 2.5rem;
  }

  .hero-subtitle {
    font-size: 1rem;
  }

  .hero-subtitle {
    font-size: 1rem;
  }

  .main-container {
    margin-top: -30px;
    padding: 0 15px 50px;
  }

  .notice-header, .notice-content {
    padding: 30px 25px;
  }

  .action-section {
    padding: 20px 25px 30px;
    flex-direction: column;
    gap: 20px;
  }

  .admin-actions {
    width: 100%;
    justify-content: center;
  }

  .notice-title {
    font-size: 1.8rem;
  }

  .content-wrapper {
    padding: 25px;
  }
}

@media (max-width: 480px) {
  .hero-section {
    padding: 60px 0 50px;
  }

  .hero-title {
    font-size: 2rem;
  }

  .notice-header, .notice-content, .action-section {
    padding: 20px;
  }

  .admin-actions {
    flex-direction: column;
    width: 100%;
  }

  .btn-primary, .btn-secondary, .btn-edit, .btn-delete {
    width: 100%;
    justify-content: center;
  }
}
</style>
