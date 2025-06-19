<template>
    <CommunityLayout>
        <div class="container post-container">
            <div class="title-box">
                <h3>여행정보공유</h3>
                <hr class="title-line" />
            </div>

            <div class="row justify-content-center">
                <div class="col-lg-6">
                <div class="card shadow-sm">
                    <div class="card-body">
                    <form @submit.prevent="submitForm">
                        <input type="hidden" v-model="form.communityId" />

                        <div class="mb-3">
                        <label for="writer" class="form-label">작성자 ID</label>
                        <input
                            type="text"
                            class="form-control"
                            id="writer"
                            v-model="form.writer"
                            disabled
                        />
                        </div>

                        <div class="mb-3">
                        <label for="title" class="form-label">제목</label>
                        <input
                            type="text"
                            class="form-control"
                            id="title"
                            v-model="form.title"
                            placeholder="제목..."
                        />
                        </div>

                        <div class="mb-3">
                        <label for="content" class="form-label">내용</label>
                        <textarea
                            class="form-control"
                            id="content"
                            v-model="form.content"
                            placeholder="내용 입력"
                            rows="5"
                        ></textarea>
                        </div>

                        <div class="mb-3">
                        <div class="d-flex align-items-center gap-2">
                            <label for="flexSwitchCheckDefault" class="form-label mb-0">예약 날짜 설정</label>
                            <div class="form-check form-switch mb-0">
                            <input
                                class="form-check-input"
                                type="checkbox"
                                id="flexSwitchCheckDefault"
                                v-model="useReservation"
                            />
                            </div>
                            <div class="sm-txt">예약 날짜에 맞춰 공유글이 포스팅됩니다.</div>
                        </div>

                        <div v-if="useReservation" class="mt-2">
                            <input
                            type="date"
                            class="form-control mb-2"
                            v-model="form.reservationDate"
                            />
                            <input
                            type="time"
                            class="form-control"
                            v-model="form.reservationTime"
                            />
                        </div>
                        </div>

                        <div class="text-center">
                        <button type="submit" class="btn btn-primary">글작성</button>
                        <button type="reset" class="btn btn-danger" @click="resetForm">초기화</button>
                        </div>
                    </form>
                    </div>
                </div>
                </div>
            </div>
        </div>
    </CommunityLayout>
  </template>

  <script setup>
  import { ref, inject } from 'vue'
  import { useRoute, useRouter } from 'vue-router'
  import CommunityLayout from '@/components/common/CommunityLayout.vue'

  import * as boardAPI from '../../lib/api/boardAPI'

  const route = useRoute()
  const router = useRouter()

  const globalStatus = inject('globalStatus')

  const form = ref({
    communityId: route.params.id || '',
    writer: globalStatus.value.loginUser.name,
    title: '',
    content: '',
    reservationDate: '',
    reservationTime: ''
  })

  const useReservation = ref(false)

  const submitForm = async () => {
    // 예약이 꺼져있으면 날짜 정보 비움
    if (!useReservation.value) {
      form.value.reservationDate = ''
      form.value.reservationTime = ''
    }

    try {
        const res = await boardAPI.createBoard({
            communityId: form.value.communityId,
            title: form.value.title,
            content: form.value.content,
            reservationDate: form.value.reservationDate,
            reservationTime: form.value.reservationTime
        })

        if (res.state === 200) {
            router.push(`/community/${form.value.communityId}/community-board`)
        }
    } catch (e) {
        console.error('submit error:', e)
    }
  }

  const resetForm = () => {
    form.value.title = ''
    form.value.content = ''
    form.value.reservationDate = ''
    form.value.reservationTime = ''
    useReservation.value = false
  }
  </script>

  <style scoped>
  .sm-txt {
    font-size: 0.875rem;
    color: #666;
  }

  .post-container {
    padding: 2rem;
  }
  </style>
