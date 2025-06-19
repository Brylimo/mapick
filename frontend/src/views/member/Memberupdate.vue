<template>
    <div class="auth-container">
			<h5 v-if="!member" class="text-danger text-center">회원 정보가 없습니다.</h5>
            <div v-else>
                <h3 class="text-center mb-4">회원 정보 수정</h3>
                <form @submit.prevent="updateMember">

                    <div class="mb-3">
                        <label class="form-label">이름</label> <input type="text"
                            name="name" class="form-control" placeholder="이름 입력"
                            v-model="member.name" required>
                    </div>

                    <div class="mb-3">
                        <label class="form-label">이메일</label> <input type="email"
                            name="email" class="form-control" v-model="member.email" disabled>
                    </div>

                    <div class="mb-3">
                        <label class="form-label">비밀번호</label> <input type="password"
                            name="password" class="form-control" v-model="member.password" placeholder="비밀번호를 변경하려면 입력하세요"/>
                    </div>

                    <div class="mb-3">
                        <label class="form-label d-block">구분</label>
                        <div>
                            {{member.role == '관리자' ? '관리자' : '회원'}}
                        </div>
                    </div>

                    <div class="d-grid">
                        <button type="submit" class="btn btn-primary">회원 정보 수정</button>
                    </div>
                </form>
            </div>
            <div class="alert alert-danger mt-3" v-if="error">{{ error }}</div>
		</div>
</template>

<script setup>
import {ref, inject} from 'vue'
import { useRouter } from 'vue-router'
import { useUpdateUserMutation } from '../../hooks/mutations/user/useUpdateUserMutation'

const router = useRouter()
const globalStatus = inject('globalStatus')

const { mutate : updateUserMutate } = useUpdateUserMutation()

const error = ref('')

const member = ref({
    name: globalStatus.value.loginUser.name,
    email: globalStatus.value.loginUser.email,
    password: '',
    role: globalStatus.value.loginUser.role
})

const updateMember = async () =>{
    const {name, email, password, role} = member.value
    try {
        updateUserMutate(
            {
                name,
                email,
                password,
                role
            },
            {
                onSuccess: (res) => {
                    // 전역적으로 loginUser 저장
                    globalStatus.value.loginUser = res.data

                    alert("회원 정보 수정에 성공했습니다.")
                    router.push('/members/member-detail')
                },
                onError: () => {
                    alert("회원 정보 수정에 실패했습니다")
                    return
                }
            }
        )

    } catch (err) {
        error.value = "회원 정보 수정 실패하였습니다"
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
	box-shadow: 0 0.5rem 1rem rgba(0, 0, 0, 0.05);
}
</style>
