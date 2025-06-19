<template>
  <nav class="modern-navbar">
    <div class="navbar-container">
      <!-- 로고 섹션 -->
      <div class="logo-section">
        <RouterLink
          class="logo-link"
          :to="{ name: 'home' }"
        >
          <div class="logo-container">
            <img class="logo-image" src="@/assets/img/mapick.png" alt="Mapick" />
            <span class="logo-text">Mapick</span>
          </div>
        </RouterLink>
      </div>

      <!-- 모바일 토글 버튼 -->
      <button
        class="mobile-toggle"
        type="button"
        @click="toggleMobileMenu"
        :class="{ active: mobileMenuOpen }"
      >
        <span></span>
        <span></span>
        <span></span>
      </button>

      <!-- 네비게이션 메뉴 -->
      <div class="nav-content" :class="{ 'mobile-open': mobileMenuOpen }">
        <!-- 메인 메뉴 -->
        <ul class="main-nav" v-if="globalStatus.isLoggedIn">
          <li class="nav-item">
            <RouterLink class="nav-link" :to="{ name: 'communityList' }">
              <span class="nav-icon">👥</span>
              커뮤니티
            </RouterLink>
          </li>
          <li class="nav-item">
            <RouterLink class="nav-link" to="/attraction">
              <span class="nav-icon">🏛️</span>
              관광지 조회
            </RouterLink>
          </li>
          <li class="nav-item">
            <RouterLink class="nav-link" to="/plan">
              <span class="nav-icon">📋</span>
              여행 계획
            </RouterLink>
          </li>
          <li class="nav-item">
            <RouterLink class="nav-link" to="/notice">
              <span class="nav-icon">📢</span>
              공지사항
            </RouterLink>
          </li>
        </ul>

        <!-- 사용자 메뉴 -->
        <div class="user-section">
          <template v-if="globalStatus.isLoggedIn">
            <!-- 사용자 정보 -->
            <div class="user-info">
              <div class="user-avatar">
                {{ globalStatus.loginUser.name?.charAt(0) || 'U' }}
              </div>
              <div class="user-details">
                <span class="user-greeting">안녕하세요</span>
                <span class="user-name">{{ globalStatus.loginUser.name }}님</span>
              </div>
            </div>

            <!-- 드롭다운 메뉴 -->
            <div class="dropdown" @click="toggleDropdown" ref="dropdown">
              <button class="dropdown-toggle">
                <span class="dropdown-icon">⚙️</span>
              </button>
              <div class="dropdown-menu" :class="{ show: dropdownOpen }">
                <RouterLink class="dropdown-item" to="/members/member-detail">
                  <span class="item-icon">👤</span>
                  내 정보
                </RouterLink>
                <RouterLink class="dropdown-item" to="/members/member-update">
                  <span class="item-icon">✏️</span>
                  정보 수정
                </RouterLink>
                <button
                  @click="handleDelete(globalStatus.loginUser.mno)"
                  class="dropdown-item danger"
                >
                  <span class="item-icon">🗑️</span>
                  회원 탈퇴
                </button>
                <div class="dropdown-divider"></div>
                <button class="dropdown-item logout" @click="logout">
                  <span class="item-icon">🚪</span>
                  로그아웃
                </button>
              </div>
            </div>
          </template>

          <template v-else>
            <div class="auth-buttons">
              <RouterLink :to="{ name: 'register' }" class="auth-btn register-btn">
                회원가입
              </RouterLink>
              <RouterLink :to="{ name: 'login' }" class="auth-btn login-btn">
                로그인
              </RouterLink>
              <RouterLink :to="{ name: 'MemberFindPw' }" class="auth-link">
                비밀번호 찾기
              </RouterLink>
            </div>
          </template>
        </div>
      </div>
    </div>
  </nav>
</template>

<script setup>
import { inject, ref, onMounted, onUnmounted } from 'vue'
import { useLogoutQuery } from '../../hooks/queries/auth/useLogoutQuery'
import { useDeleteUserMutation } from '../../hooks/mutations/user/useDeleteUserMutation'
import { useRouter } from 'vue-router'

const globalStatus = inject('globalStatus')
const { logoutRefetch } = useLogoutQuery()
const { mutate: deleteUserMutate } = useDeleteUserMutation()
const router = useRouter()

const mobileMenuOpen = ref(false)
const dropdownOpen = ref(false)
const dropdown = ref(null)

const toggleMobileMenu = () => {
  mobileMenuOpen.value = !mobileMenuOpen.value
}

const toggleDropdown = () => {
  dropdownOpen.value = !dropdownOpen.value
}

const closeDropdown = (e) => {
  if (dropdown.value && !dropdown.value.contains(e.target)) {
    dropdownOpen.value = false
  }
}

onMounted(() => {
  document.addEventListener('click', closeDropdown)
})

onUnmounted(() => {
  document.removeEventListener('click', closeDropdown)
})

const logout = async () => {
  try {
    const res = await logoutRefetch()

    if (res?.data?.status === 200) {
      globalStatus.value.isLoggedIn = false
      globalStatus.value.loginUser = {}
      alert('로그아웃 되었습니다.')
      router.push({ name: 'home' })
    } else {
      alert('로그아웃에 실패했습니다.')
    }
  } catch (err) {
    console.error('로그아웃 오류:', err)
    alert('서버 오류로 로그아웃에 실패했습니다.')
  }
}

const handleDelete = async (id) => {
  if (confirm('정말 계정을 삭제하시겠습니까?')) {
    deleteUserMutate(id, {
      onSuccess: () => {
        globalStatus.value.isLoggedIn = false
        globalStatus.value.loginUser = {}
        alert('삭제되었습니다.')
        router.push({ name: 'home' })
      },
      onError: () => {
        alert('삭제가 실패했습니다.')
      },
    })
  }
}
</script>

<style scoped>
.modern-navbar {
  background: linear-gradient(135deg, #c5e1a5 0%, #81c784 100%);
  backdrop-filter: blur(8px);
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
  box-shadow: 0 6px 20px rgba(129, 199, 132, 0.1);
  position: sticky;
  top: 0;
  z-index: 1000;
  padding: 0;
}

.navbar-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 1rem 2rem;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

/* 로고 섹션 */
.logo-section {
  flex-shrink: 0;
}

.logo-link {
  text-decoration: none;
  color: inherit;
}

.logo-container {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.logo-image {
  width: 56px;
  height: 56px;
  border-radius: 8px;
  object-fit: cover;
}

.logo-text {
  font-size: 1.5rem;
  font-weight: 700;
  color: white;
  font-family: 'Inter', sans-serif;
}

/* 모바일 토글 */
.mobile-toggle {
  display: none;
  flex-direction: column;
  gap: 4px;
  background: none;
  border: none;
  cursor: pointer;
  padding: 8px;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.mobile-toggle span {
  width: 24px;
  height: 3px;
  background: white;
  border-radius: 2px;
  transition: all 0.3s ease;
}

.mobile-toggle.active span:nth-child(1) {
  transform: rotate(45deg) translate(6px, 6px);
}

.mobile-toggle.active span:nth-child(2) {
  opacity: 0;
}

.mobile-toggle.active span:nth-child(3) {
  transform: rotate(-45deg) translate(6px, -6px);
}

/* 네비게이션 콘텐츠 */
.nav-content {
  display: flex;
  align-items: center;
  gap: 2rem;
  flex: 1;
  justify-content: space-between;
  margin-left: 3rem;
}

.main-nav {
  display: flex;
  list-style: none;
  margin: 0;
  padding: 0;
  gap: 0.5rem;
}

.nav-item {
  position: relative;
}

.nav-link {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.75rem 1rem;
  color: rgba(255, 255, 255, 0.9);
  text-decoration: none;
  border-radius: 12px;
  font-weight: 500;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.nav-link:hover {
  color: white;
  background: rgba(255, 255, 255, 0.15);
  transform: translateY(-2px);
}

.nav-link.router-link-active {
  background: rgba(255, 255, 255, 0.2);
  color: white;
}

.nav-icon {
  font-size: 1.1rem;
}

/* 사용자 섹션 */
.user-section {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  color: white;
}

.user-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 0.9rem;
  color: white;
  border: 2px solid rgba(255, 255, 255, 0.3);
}

.user-details {
  display: flex;
  flex-direction: column;
  gap: 0.125rem;
}

.user-greeting {
  font-size: 0.8rem;
  opacity: 0.8;
}

.user-name {
  font-weight: 600;
  font-size: 0.9rem;
}

/* 드롭다운 */
.dropdown {
  position: relative;
}

.dropdown-toggle {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.15);
  border: none;
  color: white;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
}

.dropdown-toggle:hover {
  background: rgba(255, 255, 255, 0.25);
  transform: scale(1.05);
}

.dropdown-menu {
  position: absolute;
  top: calc(100% + 0.5rem);
  right: 0;
  background: white;
  border-radius: 12px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.15);
  padding: 0.5rem 0;
  min-width: 180px;
  opacity: 0;
  visibility: hidden;
  transform: translateY(-10px);
  transition: all 0.3s ease;
  z-index: 1000;
}

.dropdown-menu.show {
  opacity: 1;
  visibility: visible;
  transform: translateY(0);
}

.dropdown-item {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 0.75rem 1rem;
  color: #374151;
  text-decoration: none;
  border: none;
  background: none;
  width: 100%;
  text-align: left;
  cursor: pointer;
  transition: all 0.2s ease;
  font-size: 0.9rem;
}

.dropdown-item:hover {
  background: #f3f4f6;
  color: #1f2937;
}

.dropdown-item.danger {
  color: #dc2626;
}

.dropdown-item.danger:hover {
  background: #fef2f2;
  color: #b91c1c;
}

.dropdown-item.logout {
  color: #6366f1;
}

.dropdown-item.logout:hover {
  background: #eef2ff;
  color: #4f46e5;
}

.dropdown-divider {
  height: 1px;
  background: #e5e7eb;
  margin: 0.5rem 0;
}

.item-icon {
  font-size: 1rem;
}

/* 인증 버튼 */
.auth-buttons {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.auth-btn {
  padding: 0.5rem 1rem;
  border-radius: 8px;
  text-decoration: none;
  font-weight: 500;
  transition: all 0.3s ease;
  font-size: 0.9rem;
}

.register-btn {
  background: rgba(255, 255, 255, 0.15);
  color: white;
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.register-btn:hover {
  background: rgba(255, 255, 255, 0.25);
  color: white;
  transform: translateY(-1px);
}

.login-btn {
  background: white;
  color: #667eea;
  font-weight: 600;
}

.login-btn:hover {
  background: #f8fafc;
  color: #5a67d8;
  transform: translateY(-1px);
}

.auth-link {
  color: rgba(255, 255, 255, 0.8);
  text-decoration: none;
  font-size: 0.85rem;
  transition: all 0.3s ease;
}

.auth-link:hover {
  color: white;
  text-decoration: underline;
}

/* 반응형 디자인 */
@media (max-width: 1024px) {
  .navbar-container {
    padding: 1rem 1.5rem;
  }

  .nav-content {
    margin-left: 2rem;
  }

  .main-nav {
    gap: 0.25rem;
  }

  .nav-link {
    padding: 0.5rem 0.75rem;
    font-size: 0.9rem;
  }
}

@media (max-width: 768px) {
  .mobile-toggle {
    display: flex;
  }

  .nav-content {
    position: fixed;
    top: 100%;
    left: 0;
    right: 0;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    flex-direction: column;
    gap: 1rem;
    padding: 2rem;
    margin: 0;
    transform: translateY(-100%);
    opacity: 0;
    visibility: hidden;
    transition: all 0.3s ease;
    box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
  }

  .nav-content.mobile-open {
    transform: translateY(0);
    opacity: 1;
    visibility: visible;
  }

  .main-nav {
    flex-direction: column;
    width: 100%;
    gap: 0.5rem;
  }

  .nav-link {
    justify-content: flex-start;
    padding: 1rem;
    width: 100%;
    border-radius: 12px;
  }

  .user-section {
    width: 100%;
    flex-direction: column;
    gap: 1rem;
    align-items: stretch;
  }

  .user-info {
    justify-content: center;
    padding: 1rem;
    background: rgba(255, 255, 255, 0.1);
    border-radius: 12px;
  }

  .dropdown-menu {
    position: static;
    opacity: 1;
    visibility: visible;
    transform: none;
    box-shadow: none;
    background: rgba(255, 255, 255, 0.95);
    margin-top: 0.5rem;
  }

  .auth-buttons {
    flex-direction: column;
    width: 100%;
    gap: 0.75rem;
  }

  .auth-btn {
    text-align: center;
    padding: 0.75rem 1rem;
  }
}

@media (max-width: 480px) {
  .navbar-container {
    padding: 0.75rem 1rem;
  }

  .logo-text {
    font-size: 1.25rem;
  }

  .logo-image {
    width: 32px;
    height: 32px;
  }
}
</style>
