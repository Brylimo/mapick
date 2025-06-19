<template>
    <div class="container">
		<div class="auth-container">
			<h4 class="mb-3 text-primary" v-if="message">{message}</h4>

			<p class="fw-bold">{{member.name}}님, 회원가입이 성공적으로 완료되었습니다!</p>

			<div class="mt-4">
                <button @click="goLogin" class="btn btn-primary">로그인하기</button>
			</div>
		</div>
	</div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
const router = useRouter()
const message = ref('')
const state = history.state || {}
const member = state.member || {}

const goLogin = () => {
    router.push('/loginForm')
}
</script>

<style lang="scss" scoped>
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