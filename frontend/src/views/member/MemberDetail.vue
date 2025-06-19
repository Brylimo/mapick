<template>
  <div class="container">
    <div class="detail-container">
      <h3 class="text-center mb-4">내 정보</h3>

      <template v-if="!dto">
        <p class="text-danger text-center">회원 정보가 없습니다.</p>
      </template>
      <template v-else>
        <table class="table table-bordered text-center">
          <thead class="table-light">
            <tr>
              <th>이름</th>
              <th>이메일</th>
              <th>역할</th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td>{{ dto.name }}</td>
              <td>{{ dto.email }}</td>
              <td>{{ roleParser }}</td>
            </tr>
          </tbody>
        </table>
      </template>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, computed, inject } from 'vue'
// import { useGetUserQuery } from '../../hooks/queries/user/useGetUserQuery'

const globalStatus = inject('globalStatus')
const dto = ref({
  name: globalStatus.value.loginUser.name,
  email: globalStatus.value.loginUser.email,
  password: '',
  role: globalStatus.value.loginUser.role,
})

// 유저가 어떤 권한이지 파싱해서 보여주는 computed 함수
const roleParser = computed(() => {
  if (!dto.value || !dto.value.role) return ''
  return dto.value.role == '관리자' ? '관리자' : '일반 유저'
})

</script>

<style scoped>
.detail-container {
  max-width: 700px;
  margin: 4rem auto;
  padding: 2rem;
  background-color: #f8f9fa;
  border-radius: 0.75rem;
  box-shadow: 0 0.5rem 1rem rgba(0, 0, 0, 0.05);
}
</style>
