<template>
    <CommunityLayout>
        <div class="container post-container">
            <div class="title-box">
                <h3>가입 요청 목록</h3>
                <hr class="title-line" />
            </div>

            <div v-if="!joinRequests || joinRequests.length === 0" class="alert alert-warning text-center">
              확인할 가입 요청이 없습니다.
            </div>

            <table v-else class="table table-hover text-center">
                <thead class="table-dark">
                    <tr>
                        <th>#</th>
                        <th>신청자 별명</th>
                        <th>신청 사유</th>
                        <th>요청일</th>
                        <th>처리</th>
                    </tr>
                </thead>
                <tbody>
                    <tr v-for="(req, index) in (Array.isArray(joinRequests) ? joinRequests : [])" :key="req?.joinRequestId || index">
                        <td>{{ index + 1 }}</td>
                        <td>{{ req.nickname }}</td>
                        <td>{{ req.reason }}</td>
                        <td>{{ new Date(req.requestDate).toLocaleString() }}</td>
                        <td>
                            <button class="btn btn-success btn-sm me-1" @click="approveRequest(req)">수락</button>
                            <button class="btn btn-danger btn-sm" @click="rejectRequest(req)">거절</button>
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
    </CommunityLayout>
</template>

<script setup>
import { ref, onMounted, inject } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import CommunityLayout from '@/components/common/CommunityLayout.vue'
import { getJoinRequestsByCommunityId, approveJoinRequest, rejectJoinRequest } from '@/lib/api/communityMemberAPI'

const router = useRouter()
const route = useRoute()
const globalStatus = inject('globalStatus')
const joinRequests = ref([])
const communityId = Number(route.params.id)

onMounted(async () => {
    try {
        const res = await getJoinRequestsByCommunityId(communityId)
        joinRequests.value = res.data.data  || []
        console.log('res', res)
    } catch (err) {
        console.error(err)
        alert('가입 요청 목록을 불러오는데 실패했습니다.')
    }
})

const approveRequest = async (req) => {
    if (confirm('정말 수락하시겠습니까?')) {
        await approveJoinRequest(req.joinRequestId)
        alert('가입 수락 처리 완료')
        joinRequests.value = joinRequests.value.filter(r => r.joinRequestId !== req.joinRequestId)
    }
}

const rejectRequest = async (req) => {
    if (confirm('정말 거절하시겠습니까?')) {
        await rejectJoinRequest(req.joinRequestId)
        alert('가입 거절 처리 완료')
        joinRequests.value = joinRequests.value.filter(r => r.joinRequestId !== req.joinRequestId)
    }
}
</script>

<style scoped>
.post-container {
    padding: 2rem;
}
</style>
