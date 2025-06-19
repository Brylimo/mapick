<template>
    <CommunityLayout>
      <div class="container post-container">
        <div class="title-box">
          <h3>여행 정보 공유</h3>
          <hr class="title-line" />
        </div>

        <div class="d-flex justify-content-between mb-4">
          <button v-if="isCommunityMember" class="btn btn-outline-primary btn-sm" style="width: 90px;" @click="goToBoardCreate">
              글작성
          </button>

          <div class="d-flex" style="gap: 6px;">
            <select v-model="searchOpt">
              <option value="">검색조건</option>
              <option value="name">제목</option>
              <option value="name_content">제목+내용</option>
              <option value="writer">작성자</option>
            </select>
            <div class="d-flex">
              <input type="text" placeholder="검색어.." class="px-2" v-model="searchQuery" />
              <button id="search-btn" type="button" class="btn btn-dark" style="width: 60px;" @click="searchBoards">검색</button>
            </div>
          </div>
        </div>

        <table class="table table-hover text-center">
          <thead class="table-dark">
            <tr>
              <th>글번호</th>
              <th>제목</th>
              <th>작성자</th>
              <th>작성일</th>
              <th>좋아요수</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(board, index) in boardList" :key="board.id">
              <td>{{ index + 1 }}</td>
              <td>
                <RouterLink :to="`/community/${communityId}/community-board/detail/${board.id}`">{{ board.title }}</RouterLink>
              </td>
              <td>{{ board.authorName }}</td>
              <td>{{ formatDate(board.createdAt) }}</td>
              <td>{{ board.likeCnt }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </CommunityLayout>
  </template>

  <script setup>
  import { ref, onMounted, inject } from 'vue'
  import { useRoute, useRouter } from 'vue-router'
  import CommunityLayout from '@/components/common/CommunityLayout.vue'
  import * as boardAPI from '../../lib/api/boardAPI'
  import * as communityMemberAPI from '@/lib/api/communityMemberAPI'

  const router = useRouter()
  const route = useRoute()
  const communityId = route.params.id
  const isCommunityMember = ref(false)

  const boardList = ref([])
  const searchOpt = ref('')
  const searchQuery = ref('')


const checkMemberStatus = async () => {
  try {
    const res = await communityMemberAPI.checkIsMember(communityId)
    isCommunityMember.value = res.data === true
  } catch (e) {
    console.error('커뮤니티 멤버 여부 확인 실패:', e)
  }
}

  const fetchBoardList = async () => {
    try {
      const res = await boardAPI.getBoards({ communityId, opt: searchOpt.value, query: searchQuery.value })
      boardList.value = res.data || []
      console.log(boardList.value);
    } catch (e) {
      console.error('게시글 조회 실패:', e)
    }
  }

  const searchBoards = () => {
    fetchBoardList()
  }

  const goToBoardCreate = () => {
    router.push(`/community/${communityId}/community-board/create`)
  }

  const formatDate = (dateStr) => {
    return new Date(dateStr).toLocaleDateString()
  }

  onMounted(() => {
    fetchBoardList()
    checkMemberStatus()
  })
  </script>

  <style scoped>
  .post-container {
    padding: 2rem;
  }
  </style>
