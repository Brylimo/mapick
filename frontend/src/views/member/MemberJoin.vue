<template>
<div class="container">
		<div class="auth-container">
			<h3 class="text-center mb-4">회원가입</h3>
			<form>

				<div class="mb-3">
					<label class="form-label">이름*</label>
					<input type="text" name="name" v-model="member.name" class="form-control" placeholder="이름 입력" required>
				</div>
				
				<div class="mb-3">
					<label class="form-label">이메일*</label>
					<div class="input-group">
                        <input type="email" name="email" v-model="member.email" class="form-control" placeholder="이메일 입력" required />
                        <button class="btn btn-outline-secondary" type="button" @click="requestVerification">인증</button>
                    </div>
                </div>

                <div class="mb-3" v-if="showVerificationInput">
                    <label class="form-label">인증코드</label>
                    <div class="input-group">
                        <input type="text" v-model="member.code" class="form-control" placeholder="인증코드를 입력하세요" :disabled="confirmCode" />
                        <button :class="['btn', confirmCode ? 'btn-success' : 'btn-outline-success']" type="button" @click="verifyCode">{{ confirmCode ? '인증 완료' : '확인' }}</button>
                    </div>
                </div>
				
				<div class="mb-3">
					<label class="form-label">비밀번호*</label>
					<input type="password" name="password" v-model="member.password" class="form-control" placeholder="비밀번호 입력" required>
				</div>

				<div class="mb-3">
					<label class="form-label d-block">구분*</label>
					<div class="form-check form-check-inline">
						<input class="form-check-input" type="radio" name="role" value="ROLE_ADMIN" id="admin" v-model="member.role">
						<label class="form-check-label" for="admin">관리자</label>
					</div>
					<div class="form-check form-check-inline">
						<input class="form-check-input" type="radio" name="role" value="ROLE_USER" id="member" v-model="member.role">
						<label class="form-check-label" for="member">회원</label>
					</div>
				</div>
				
				<div class="d-grid">
					<button type="submit" class="btn btn-primary" :disabled="!confirmCode" @click.prevent="join">회원가입</button>
				</div>
			</form>
            <div class="alert alert-danger" role="alert" v-if="error">{{ error }}</div>
		</div>
	</div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';

import { useRegisterMutation } from '../../hooks/mutations/user/useRegisterMutation'
import * as emailAPI from '../../lib/api/emailAPI'
import * as userAPI from '../../lib/api/userAPI'

const { mutate: registerMutate } = useRegisterMutation()

const router = useRouter()
const member = ref({
    name:'',
    email:'',
    password:'',
    code: '',
    role:''
})
const error = ref('')
const confirmCode = ref(false)
const showVerificationInput = ref(false)

const join = async() =>{
    if (!confirmCode.value) {
        alert("인증코드를 입력해주세요!")
        return
    }

    const {name, email, password, role} = member.value

    if([name, email, password, role].some(field => field.trim() === '')){
        error.value = "빈 칸을 모두 입력하세요"
        return
    }

    try{
        registerMutate(
            {
                name,
                email,
                password,
                role
            },
            {
                onError: () => {
                    alert("회원가입 실패");
                    return;
                },
                onSuccess: () => {
                    alert("가입이 완료되었습니다!");
                    router.push({ name: 'login' })
                }
            }
        )

        /*const response = await fetch('/members/memberJoin', {
            method: 'POST',
            headers:{
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({name, email, password, role})
        })
        if(!response.ok){
            throw new Error("회원가입에 실패했습니다")
        }
        const res = await response.json()
        router.push({ name: 'login' })*/
    }catch(err){
        error.value = "회원가입 실패하였습니다"
    }
}

const requestVerification = async () => {
  showVerificationInput.value = true

  try {
    const res = await emailAPI.sendVerificationCode({ email: member.value.email })
    
    alert(`${member.value.email}로 인증코드를 전송했습니다.`)
  } catch (error) {
    alert('인증코드 전송을 실패했습니다.')
  }
}

const verifyCode = async () => {
    try {
        const res = await userAPI.verifyCode({ email: member.value.email, code: member.value.code })

        if (res.state === 200) {
            confirmCode.value = true
        } else {
            showVerificationInput.value = false
            alert('코드 인증에 실패했습니다')
        }
    } catch (error) {
        showVerificationInput.value = false
        alert('코드 인증에 실패했습니다')
    }
}

</script>

<style lang="scss" scoped>
.auth-container {
    max-width: 500px;
    margin: 4rem auto;
    padding: 2rem;
    background-color: #f8f9fa;
    border-radius: 0.75rem;
    box-shadow: 0 0.5rem 1rem rgba(0,0,0,0.05);
}
</style>
