<template>
  <div class="container">
    <div class="auth-container">
      <h3 class="text-center mb-4">로그인</h3>
      <form action="${root}/members/login" method="post">
        <div class="mb-3">
          <label class="form-label">이메일</label>
          <input
            type="email"
            v-model="member.email"
            name="email"
            class="form-control"
            placeholder="이메일 입력"
            required
          />
        </div>
        <div class="mb-3">
          <label class="form-label">비밀번호</label>
          <input
            type="password"
            v-model="member.password"
            name="password"
            class="form-control"
            placeholder="비밀번호 입력"
            required
          />
        </div>

        <div class="form-check mb-3">
          <input
            class="form-check-input"
            v-model="saveEmail"
            type="checkbox"
            name="rememberId"
            id="rememberId"
            value="rememberId"
          />
          <label class="form-check-label" for="rememberId">아이디 저장</label>
        </div>

        <div class="d-grid">
          <button type="submit" class="btn btn-primary" @click.prevent="login">로그인</button>
        </div>
      </form>

      <a href="http://localhost:8080/oauth2/authorization/kakao">
        <button class="btn btn-kakao">
          카카오로 로그인
        </button>
      </a>
    </div>
  </div>
</template>

<script setup>
import { ref, inject, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useLoginMutation } from '../../hooks/mutations/auth/useLoginMutation'

const router = useRouter()
const globalStatus = inject('globalStatus')

const member = ref({
  email: '',
  password: '',
})
const saveEmail = ref(false)
onMounted(() => {
  const saved = localStorage.getItem('rememberEmail')
  if (saved) {
    member.value.email = saved
    saveEmail.value = true
  }
})

const { mutate: loginMutate } = useLoginMutation()

const login = () => {
  try {
    loginMutate(
      {
        email: member.value.email,
        password: member.value.password,
      },
      {
        onError: () => {
          alert('로그인에 실패했습니다')

          member.value.email = ''
          member.value.password = ''
        },
        onSuccess: (res) => {
          // 전역적으로 loginUser 저장
          globalStatus.value.loginUser = res.data
          globalStatus.value.isLoggedIn = true

          if (saveEmail.value) {
            localStorage.setItem('rememberEmail', member.value.email)
          } else {
            localStorage.removeItem('rememberEmail')
          }

          router.push({ name: 'home' })
        },
      },
    )

    /*const response = await fetch(`${root}/members/login`,{
			method: 'POST',
			headers:{
				'Content-Type': 'application/x-www-form-urlencoded'
			},
			body: params.toString()
		})*/

    /*if(!response.ok){
			throw new Error("로그인에 실패했습니다");
		}

		const res = await response.json()

		globalStatus.value.loginUser = res.data
		globalStatus.value.isLoggedIn = true

		if(saveEmail.value){
			localStorage.setItem('email', member.value.email)
		}else{
			localStorage.removeItem('email')
		}
		router.push('/')*/
  } catch (err) {
    alert('로그인에 실패했습니다')
    member.value.password = ''
  }
}
</script>

<style lang="scss" scoped>
.auth-container {
  max-width: 400px;
  margin: 4rem auto;
  padding: 2rem;
  background-color: #f8f9fa;
  border-radius: 0.75rem;
  box-shadow: 0 0.5rem 1rem rgba(0, 0, 0, 0.05);
}

.btn-kakao {
  margin-top: 15px;
  background-color: #fee500;
  color: #3c1e1e;
  border: none;
  padding: 0.6rem 1rem;
  font-weight: 600;
  font-size: 15px;
  border-radius: 6px;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  width: 100%;
  justify-content: center;

  &:hover {
    background-color: #ffeb3b;
  }
}

.kakao-icon {
  width: 20px;
  height: 20px;
}
</style>
