<template>
  <div class="app-container">
    <AppHeader v-if="!hideHeader" />

    <main class="main-content">
      <router-view :key="$route.fullPath" />
    </main>

    <!-- footer 고정 -->
    <AppFooter
      v-if="!hideHeader"
      class="fixed-footer"
    />
  </div>
</template>

<script setup>
import { useRoute } from 'vue-router'
import { computed } from 'vue'

const route = useRoute()

import AppHeader from '@/components/common/AppHeader.vue'
import AppFooter from '@/components/common/AppFooter.vue'

// 현재 경로가 /community/community-main/숫자일 경우 header 숨김
const hideHeader = computed(() => {
  return /^\/community\/\d+\/community-(main|board|regist|join-permit)(\/.*)?$/.test(route.path)
})
</script>

<style scoped>
.app-container {
  position: relative;
  min-height: 100vh;      /* 화면 전체 높이 확보 */
  display: flex;
  flex-direction: column;
}

header {
  line-height: 1.5;
}

.main-content {
  flex: 1;                /* 남은 공간 차지 */
  /* footer 높이(예: 60px) 만큼 여유 주기 */
  padding-bottom: 60px;
}

/* 고정 푸터 */
.fixed-footer {
  position: fixed;
  bottom: 0;
  left: 0;
  width: 100%;
  z-index: 1000;          /* 필요에 따라 조절 */
}

.logo {
  display: block;
  margin: 0 auto 2rem;
}

.post-container {
  padding: 2rem;
}

@media (min-width: 1024px) {
  header {
    display: flex;
    place-items: center;
    padding-right: calc(var(--section-gap) / 2);
  }

  .logo {
    margin: 0 2rem 0 0;
  }

  header .wrapper {
    display: flex;
    place-items: flex-start;
    flex-wrap: wrap;
  }
}
</style>