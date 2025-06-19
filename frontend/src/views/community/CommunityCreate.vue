<template>
  <div class="container">
    <div class="title-box">
      <h3>커뮤니티</h3>
      <hr class="title-line" />
    </div>

    <div class="row justify-content-center">
      <div class="col-lg-6">
        <div class="card shadow-sm">
          <div class="card-body">
            <form id="communityForm">
              <div class="mb-3">
                <label for="communityName" class="form-label">커뮤니티 이름</label>
                <input
                  v-model="communityName"
                  class="form-control"
                  type="text"
                  id="communityName"
                  name="communityName"
                  placeholder="이름..."
                />
              </div>
              <div class="mb-3">
                <label for="desc" class="form-label">커뮤니티 소개</label>
                <textarea
                  v-model="desc"
                  class="form-control"
                  id="desc"
                  name="desc"
                  placeholder="소개 입력"
                  rows="5"
                ></textarea>
              </div>
              <div class="mb-3">
                <label for="categories" class="form-label">카테고리 (쉼표로 구분)</label>
                <input
                  v-model="categories"
                  type="text"
                  class="form-control"
                  id="categories"
                  placeholder="예: 맛집,음식"
                />
              </div>
              <div class="mb-3">
                <label for="tags" class="form-label">태그 (쉼표로 구분)</label>
                <input
                  v-model="tags"
                  type="text"
                  class="form-control"
                  id="tags"
                  placeholder="예: 한식,분식"
                />
              </div>
              <div class="text-center">
                <button type="button" class="btn btn-primary" @click="submitCommunity">생성</button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useCommunityCreateMutation } from '../../hooks/mutations/community/useCommunityCreateMutation'
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const { mutate: communityCreateMutate } = useCommunityCreateMutation()

const communityName = ref('')
const desc = ref('')
const tags = ref('')
const categories = ref('')
const router = useRouter()

const submitCommunity = async () => {
  const tagList = tags.value
    .split(',')
    .map((t) => t.trim())
    .filter((t) => t)
    .map((name) => ({ name }))
  const categoryList = categories.value
    .split(',')
    .map((c) => c.trim())
    .filter((c) => c)
    .map((categoryName) => ({ categoryName }))

  try {
    /*const response = await fetch('/communities/communityCreate', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        communityName: communityName.value,
        desc: desc.value,
        tagList,
        categoryList
      })
    })*/
    console.log('execute', communityName.value, desc.value, tagList, categoryList)
    await communityCreateMutate(
      {
        communityName: communityName.value,
        desc: desc.value,
        tagList,
        categoryList,
      },
      {
        onSuccess: (res) => {
          console.log(res)
          router.push('/community/community-list')
        },
        onError: () => {
          return
        },
      },
    )
    //if (!response.ok) throw new Error('네트워크 응답 실패')
    //const data = await response.json()
    //console.log('생성 성공:', data)
    //router.push('/communities/communityList')
  } catch (error) {
    console.error('에러 발생:', error)
    alert('커뮤니티 생성 실패')
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
