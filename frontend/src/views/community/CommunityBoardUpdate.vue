<template>
    <CommunityLayout>
        <div class="container">
            <div class="title-box">
                <h3>게시글 수정</h3>
                <hr class="title-line" />
            </div>
        
            <div v-if="!board">
                <p>게시글 정보가 없습니다.</p>
            </div>
            <div v-else>
                <div class="row justify-content-center">
                <div class="col-lg-6">
                    <div class="card shadow-sm">
                    <div class="card-body">
                        <form @submit.prevent="submitForm">
                        <div class="mb-3">
                            <label for="writer" class="form-label">작성자</label>
                            <input
                            type="text"
                            class="form-control"
                            id="writer"
                            :value="board.authorName"
                            disabled
                            />
                        </div>
        
                        <div class="mb-3">
                            <label for="title" class="form-label">제목</label>
                            <input
                            type="text"
                            class="form-control"
                            id="title"
                            v-model="board.title"
                            placeholder="제목..."
                            />
                        </div>
        
                        <div class="mb-3">
                            <label for="content" class="form-label">내용</label>
                            <textarea
                            class="form-control"
                            id="content"
                            v-model="board.content"
                            placeholder="내용 입력"
                            rows="5"
                            ></textarea>
                        </div>
        
                        <div class="text-center">
                            <button type="submit" class="btn btn-primary">글수정</button>
                            <button type="reset" class="btn btn-danger" @click="resetForm">초기화</button>
                        </div>
                        </form>
                    </div>
                    </div>
                </div>
                </div>
            </div>
        </div>
    </CommunityLayout>
  </template>
  
  <script setup>
  import { ref, onMounted } from 'vue'
  import { useRoute, useRouter } from 'vue-router'
  import { getBoard, updateBoard } from '@/lib/api/boardAPI'
  import CommunityLayout from '@/components/common/CommunityLayout.vue'
    
  const route = useRoute()
  const router = useRouter()
  
  const board = ref(null)

  const communityId = route.params.id || route.path.split('/').pop()
  const boardId = route.params.bid || route.path.split('/').pop()
  
  const fetchBoard = async () => {
    try {
      const res = await getBoard({ boardId: boardId })
      board.value = res.data
    } catch (err) {
      console.error('게시글 조회 실패:', err)
      alert('게시글 정보를 불러올 수 없습니다.')
    }
  }
  
  const submitForm = async () => {
    try {
      const res = await updateBoard({
        boardId: boardId,
        title: board.value.title,
        content: board.value.content
      })

      if (res.state === 200) {
        alert('수정이 완료되었습니다.')
        router.push(`/community/${communityId}/community-board/detail/${boardId}`)
      } else {
        alert('수정 실패')
      }
    } catch (err) {
      console.error('수정 중 오류:', err)
      alert('서버 오류가 발생했습니다.')
    }
  }
  
  const resetForm = () => {
    fetchBoard()
  }
  
  onMounted(() => {
    fetchBoard()
  })
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
  