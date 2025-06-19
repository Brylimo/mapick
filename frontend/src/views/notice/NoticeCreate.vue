<template>
  <div class="container">
    <div class="title-box">
      <h3>공지사항 등록</h3>
      <hr class="title-line" />
    </div>

    <div class="row justify-content-center">
      <div class="col-lg-6">
        <div class="card shadow-sm">
          <div class="card-body">
            <form @submit.prevent="submitForm">
              <div class="mb-3">
                <label for="writer" class="form-label">작성자 ID</label>
                <input
                  type="text"
                  class="form-control"
                  id="writer"
                  v-model="writer"
                  placeholder="작성자ID..."
                  disabled
                />
              </div>

              <div class="mb-3">
                <label for="title" class="form-label">제목</label>
                <input
                  type="text"
                  class="form-control"
                  id="title"
                  v-model="title"
                  placeholder="제목..."
                />
              </div>

              <div class="mb-3">
                <label for="content" class="form-label">내용</label>
                <textarea
                  class="form-control"
                  id="content"
                  v-model="content"
                  placeholder="내용 입력"
                  rows="5"
                ></textarea>
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
</template>

<script setup>
import {  inject,ref } from 'vue'
import { useRouter } from 'vue-router'
import { createNotice } from '@/lib/api/noticeAPI'

const router = useRouter()
const globalStatus = inject('globalStatus')


const writer = globalStatus?.value?.loginUser?.name
const title = ref('')
const content = ref('')

const resetForm = () => {
  title.value = ''
  content.value = ''
}

const submitForm = async () => {
  try {
    const res = await createNotice({
      title: title.value,
      content: content.value,
    })
    if(res.result === 'success'){
    alert('공지사항이 등록되었습니다.')
    router.push({ name: 'NoticeList' })
    }else{
      alert('공지사항 등록에 실패했습니다.')
    }
  } catch (err) {
    console.error('공지사항 등록 오류:', err)
    alert('등록 중 오류가 발생했습니다.')
  }
}
</script>

<style lang="scss" scoped>
/* 제목 스타일 */
.title-box {
    text-align: center;
    margin: 40px 0;
}

.title-box h3 {
    font-weight: bold;
    font-size: 24px;
    margin-bottom: 10px;
}

.title-line {
    width: 80px;
    height: 3px;
    background-color: #666;
    margin: 0 auto;
    border: none;
}

/* 카드 스타일 */
.card {
    border-radius: 10px;
    padding: 20px;
}

/* 입력 필드 스타일 */
.form-control {
    border-radius: 5px;
    padding: 10px;
}

/* 버튼 스타일 */
.btn {
    width: 100px;
    padding: 10px;
    font-size: 16px;
}
</style>
