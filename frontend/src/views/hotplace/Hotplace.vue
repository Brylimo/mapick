<template>
  <div class="container hotplace-container">
    <h2 class="text-center text-primary mb-4">HotPlace 리스트</h2>

    <div v-if="hotplaces.length === 0" class="alert alert-warning text-center">
      HotPlace 정보가 없습니다.
    </div>

    <div v-else class="table-responsive">
      <table class="table table-hover text-center align-middle">
        <thead class="table-light">
          <tr>
            <th>제목</th>
            <th>주소</th>
            <th>전화번호</th>
            <th>생성일</th>
            <th>삭제</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="hotplace in hotplaces" :key="hotplace.hpid">
            <td>{{ hotplace.name }}</td>
            <td>{{ hotplace.address }}</td>
            <td>{{ hotplace.tel || '준비중입니다' }}</td>
            <td>{{ formatDate(hotplace.createdDate) }}</td>
            <td>
              <button class="btn btn-sm btn-outline-danger" @click="onDelete(hotplace.hpid)">
                삭제
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getHotplaces, deleteHotplace } from '@/lib/api/hotplaceAPI'

const hotplaces = ref([])

onMounted(async () => {
  const res = await getHotplaces()
  hotplaces.value = res.data || []
})

const onDelete = async (id) => {
  await deleteHotplace(id)
  hotplaces.value = hotplaces.value.filter((h) => h.hpid !== id)
}

const formatDate = (dateStr) => {
  const date = new Date(dateStr)
  return date.toLocaleDateString('ko-KR')
}
</script>
<style scoped>
.hotplace-container {
  max-width: 1100px;
  margin: 4rem auto;
  padding: 2rem;
  background-color: #f8f9fa;
  border-radius: 1rem;
  box-shadow: 0 0.5rem 1rem rgba(0, 0, 0, 0.05);
}
</style>
