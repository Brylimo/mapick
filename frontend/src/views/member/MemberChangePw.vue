<template>
  <div class="container">
    <div class="auth-container">
      <h3 class="text-center mb-4">비밀번호 변경</h3>
      <div class="alert alert-info text-center">
        <strong>{{ email }}</strong> 계정의 비밀번호를 변경합니다.
      </div>

      <form @submit.prevent="submitPassword">
        <div class="mb-3">
          <label class="form-label">새 비밀번호*</label>
          <input type="password" v-model="password" class="form-control" required />
        </div>

        <div class="mb-3">
          <label class="form-label">비밀번호 확인*</label>
          <input type="password" v-model="password2" class="form-control" required />
        </div>

        <div class="d-grid">
          <button type="submit" class="btn btn-primary">변경</button>
        </div>
      </form>

      <div class="alert alert-danger mt-3" v-if="error">{{ error }}</div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { changePassword } from '@/lib/api/userAPI'

const route = useRoute()
const router = useRouter()

const email = route.query.email || ''
const password = ref('')
const password2 = ref('')
const error = ref('')

const submitPassword = async () => {
  if (password.value !== password2.value) {
    error.value = '비밀번호가 일치하지 않습니다.'
    return
  }

  try {
    const res = await changePassword({
      email,
      password: password.value,
      password2: password2.value
    })

    if (res.state === 200) {
      alert('비밀번호가 성공적으로 변경되었습니다.')
      router.push({ name: 'login' })
    } else {
      error.value = '비밀번호 변경에 실패했습니다.'
    }
  } catch {
    error.value = '비밀번호 변경 요청 중 오류가 발생했습니다.'
  }
}
</script>

<style scoped>
.auth-container {
  max-width: 400px;
  margin: 4rem auto;
  padding: 2rem;
  background-color: #f8f9fa;
  border-radius: 0.75rem;
  box-shadow: 0 0.5rem 1rem rgba(0, 0, 0, 0.05);
}
</style>
