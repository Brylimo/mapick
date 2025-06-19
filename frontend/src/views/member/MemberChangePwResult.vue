<template>
  <div class="container">
    <div class="auth-container">
      <h4 class="mb-3">
        <strong>{{ email }}</strong>님의 비밀번호가<br />
        성공적으로 변경되었습니다.
      </h4>
      <div class="mt-4">
        <RouterLink :to="{ name: 'login' }" class="btn btn-primary">로그인하기</RouterLink>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRoute } from 'vue-router'

const route = useRoute()
const email = route.query.email || '사용자'
</script>

<style scoped>
.auth-container {
  max-width: 500px;
  margin: 4rem auto;
  padding: 2rem;
  background-color: #f8f9fa;
  border-radius: 0.75rem;
  box-shadow: 0 0.5rem 1rem rgba(0, 0, 0, 0.05);
  text-align: center;
}
</style>
