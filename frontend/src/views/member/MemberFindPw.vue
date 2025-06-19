<template>
  <div class="container">
    <div class="auth-container">
      <h3 class="text-center mb-4">비밀번호 변경 요청</h3>

      <form @submit.prevent="sendVerification">
        <div class="mb-3">
          <label class="form-label">이름*</label>
          <input type="text" v-model="name" class="form-control" placeholder="이름 입력" required />
        </div>

        <div class="mb-3">
          <label class="form-label">이메일*</label>
          <input type="email" v-model="email" class="form-control" placeholder="이메일 입력" required />
        </div>

        <div class="d-grid mb-4">
          <button type="submit" class="btn btn-primary">인증번호 요청</button>
        </div>
      </form>

      <div v-if="verificationSent">
        <div class="alert alert-info mb-3">
          인증번호가 이메일로 전송되었습니다. 5분 내에 입력해주세요.
        </div>

        <form @submit.prevent="verifyCodeAndGo">
          <div class="mb-3">
            <label class="form-label">인증코드*</label>
            <input type="text" v-model="code" class="form-control" placeholder="인증코드 입력" required />
          </div>

          <div class="d-grid">
            <button type="submit" class="btn btn-success">인증 및 비밀번호 변경</button>
          </div>
        </form>
      </div>

      <div class="alert alert-danger mt-3" v-if="error">{{ error }}</div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { findPasswordRequest, verifyCode, } from '@/lib/api/userAPI'
import {sendVerificationCodeChangePw} from '@/lib/api/emailAPI'

const name = ref('')
const email = ref('')
const code = ref('')
const error = ref('')
const verificationSent = ref(false)

const router = useRouter()

const sendVerification = async () => {
  error.value = ''
  try {
    const res = await findPasswordRequest({ name: name.value, email: email.value })

    if (res.state === 200) {
      await sendVerificationCodeChangePw({ email: email.value })
      verificationSent.value = true
    } else {
      error.value = '일치하는 계정이 없습니다.'
    }
  } catch (err) {
    error.value = '서버 오류가 발생했습니다.'
  }
}

const verifyCodeAndGo = async () => {
  error.value = ''
  try {
    const res = await verifyCode({ email: email.value, code: code.value })
    if (res.state === 200) {
      router.push({ name: 'MemberChangePw', query: { email: email.value } })
    } else {
      error.value = '인증코드가 올바르지 않습니다.'
    }
  } catch (err) {
    error.value = '인증코드 확인 중 오류가 발생했습니다.'
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
