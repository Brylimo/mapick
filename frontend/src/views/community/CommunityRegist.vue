<template>
    <CommunityLayout>
        <div class="container post-container">
            <div class="title-box">
                <h3>커뮤니티 가입하기</h3>
                <hr class="title-line" />
            </div>

            <form>
                <div class="mb-3">
                    <label class="form-label">커뮤니티 설명</label>
                    <div>{{ description || '불러오는 중...' }}</div>
                </div>

                <div class="mb-3">
                    <label class="form-label">별명</label>
                    <input type="text" name="nickname" v-model="regist.nickname" class="form-control" placeholder="별명 입력" />
                </div>

                <div class="mb-3">
                    <div>가입 사유</div>
                    <textarea v-model="regist.reason"></textarea>
                </div>

                <div class="mb-3">
                    <div>- 가입 신청이 수락되면 '내소식'에서 알려드립니다.</div>
                    <div>- 가입 신청 후 3개월 이상 수락되지 않으면 자동 신청 해제 됩니다.</div>
                </div>

                <div class="d-grid">
					<button type="submit" class="btn btn-primary" @click.prevent="join">동의 후 가입하기</button>
				</div>
            </form>
        </div>
    </CommunityLayout>
</template>

<script setup>
import { ref, onMounted  } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import CommunityLayout from '@/components/common/CommunityLayout.vue'
import { getCommunity } from '@/lib/api/communityAPI'
import * as communityMemberAPI from '../../lib/api/communityMemberAPI'

const route = useRoute()
const router = useRouter();
const description = ref('')

const regist = ref({
    communityId: route.params.id || '',
    nickname:'',
    reason: ''
})

onMounted(async () => {
  try {
    const data = await getCommunity({ communityId: regist.value.communityId })
    description.value = data.data.desc || '설명이 없습니다.'
  } catch (err) {
    console.error('커뮤니티 설명을 불러오는 데 실패했습니다.', err)
    description.value = '설명을 불러오는 데 실패했습니다.'
  }
})

const join = async () =>{

    try {
        const res = await communityMemberAPI.sendCommunityMemberReviewRequest({
            nickname: regist.value.nickname,
            reason: regist.value.reason,
            communityId: regist.value.communityId
        })

        if (res.state === 200) { // 전송 성공
            alert("가입 신청이 완료되었습니다.\n관리자의 승인을 기다려주세요!");
            router.push('/')
        }
    } catch(err){
        console.error(err)
    }
}

</script>

<style scoped>
.post-container {
    padding: 2rem;
}
</style>
