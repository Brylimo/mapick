<template>
  <CommunityLayout>
    <div class="board-detail-container">
      <!-- 제목 섹션 -->
      <div class="title-section">
        <h2 class="page-title">게시글</h2>
        <div class="title-underline"></div>
      </div>

      <!-- 로딩 상태 -->
      <div v-if="loading" class="loading-container">
        <div class="loading-spinner"></div>
        <p>게시글을 불러오는 중...</p>
      </div>

      <!-- 게시글이 없는 경우 -->
      <div v-else-if="!board" class="no-content">
        <div class="no-content-icon">📝</div>
        <p>게시글 정보를 찾을 수 없습니다.</p>
        <button @click="goToList" class="btn btn-primary">목록으로 돌아가기</button>
      </div>

      <!-- 게시글 내용 -->
      <div v-else class="board-content-wrapper">
        <!-- 게시글 헤더 -->
        <div class="board-header">
          <h1 class="board-title">{{ board.title }}</h1>

          <div class="board-meta">
            <div class="author-info">
              <div class="author-avatar">
                <img :src="userimg" alt="작성자" />
              </div>
              <div class="author-details">
                <div class="author-name">{{ board.authorName }}</div>
                <div class="post-date">{{ formatDate(board.createdAt) }}</div>
              </div>
            </div>

            <div class="board-actions">
              <button @click="toggleLike" class="like-btn" :class="{ liked: board.liked }">
                <i :class="['fa-heart', board.liked ? 'fas' : 'far']"></i>
                <span>{{ board.likeCnt }}</span>
              </button>
            </div>
          </div>
        </div>

        <!-- 게시글 본문 -->
        <div class="board-body">
          <div class="content-text">{{ board.content }}</div>
        </div>

        <!-- 게시글 하단 버튼 -->
        <div class="board-footer">
          <div class="button-group">
            <button @click="goToList" class="btn btn-outline">
              <i class="fas fa-list"></i>
              목록
            </button>
            <div v-if="globalStatus.isLoggedIn && Number(board.authorId) === globalStatus.loginUser.mno" class="owner-buttons">
              <button @click="goToEdit" class="btn btn-success">
                <i class="fas fa-edit"></i>
                수정
              </button>
              <button @click="() => delNotice(board.id)" class="btn btn-danger">
                <i class="fas fa-trash"></i>
                삭제
              </button>
            </div>
          </div>
        </div>

        <!-- 댓글 섹션 -->
        <div class="comments-section">
          <div class="comments-header">
            <h3>댓글 <span class="comment-count">{{ comments.length }}</span></h3>
          </div>

          <!-- 댓글 작성 폼 -->
          <div v-if="globalStatus.isLoggedIn" class="comment-form">
            <div class="comment-input-wrapper">
              <div class="user-avatar">
                <img :src="userimg" alt="사용자" />
              </div>
              <div class="input-area">
                <textarea
                  v-model="newComment"
                  placeholder="댓글을 입력하세요..."
                  @keydown.ctrl.enter="submitComment"
                ></textarea>
                <div class="form-actions">
                  <button @click="submitComment" class="btn btn-primary" :disabled="!newComment.trim()">
                    댓글 작성
                  </button>
                </div>
              </div>
            </div>
          </div>

          <!-- 로그인 안내 -->
          <div v-else class="login-notice">
            <p>댓글을 작성하려면 로그인이 필요합니다.</p>
          </div>

          <!-- 댓글 목록 -->
          <div class="comments-list">
            <div v-for="comment in comments" :key="comment.commentId" class="comment-item">
              <div class="comment-avatar">
                <img :src="userimg" alt="댓글 작성자" />
              </div>
              <div class="comment-content">
                <div class="comment-header">
                  <span class="comment-author">{{ comment.memberName }}</span>
                  <span class="comment-date"> {{ formatDate(comment.modDate || comment.regDate) }}</span>
                </div>

                <div v-if="editingComment === comment.commentId" class="comment-edit">
                  <textarea v-model="editCommentText"></textarea>
                  <div class="edit-actions">
                    <button @click="updateComment(comment.commentId)" class="btn btn-sm btn-success">저장</button>
                    <button @click="cancelEdit" class="btn btn-sm btn-outline">취소</button>
                  </div>
                </div>

                <div v-else class="comment-text">{{ comment.content }}</div>

                <div class="comment-actions">
                  <button @click="toggleSubComments(comment.commentId)" class="reply-btn">
                    <i class="fas fa-reply"></i>
                    답글 {{ comment.subComments ? comment.subComments.length : 0 }}
                  </button>

                  <div v-if="globalStatus.isLoggedIn && Number(comment.memberId) === globalStatus.loginUser.mno" class="comment-owner-actions">
                    <button @click="startEdit(comment)" class="btn-text">수정</button>
                    <button @click="deleteComment(comment.commentId)" class="btn-text text-danger">삭제</button>
                  </div>
                </div>

                <!-- 대댓글 섹션 -->
                <div v-if="comment.showSubComments" class="sub-comments-section">
                  <!-- 대댓글 작성 폼 -->
                  <div v-if="globalStatus.isLoggedIn" class="sub-comment-form">
                    <div class="sub-comment-input">
                      <textarea
                        v-model="newSubComments[comment.commentId]"
                        placeholder="답글을 입력하세요..."
                        @keydown.ctrl.enter="submitSubComment(comment.commentId)"
                      ></textarea>
                      <button @click="submitSubComment(comment.commentId)" class="btn btn-sm btn-primary"
                              :disabled="!newSubComments[comment.commentId]?.trim()">
                        답글 작성
                      </button>
                    </div>
                  </div>

                  <!-- 대댓글 목록 -->
                  <div class="sub-comments-list">
                    <div v-for="subComment in comment.subComments" :key="subComment.subCommentId" class="sub-comment-item">
                      <div class="sub-comment-avatar">
                        <img :src="userimg" alt="답글 작성자" />
                      </div>
                      <div class="sub-comment-content">
                        <div class="sub-comment-header">
                          <span class="sub-comment-author">{{ subComment.memberName }}</span>
                          <span class="sub-comment-date"> {{ formatDate(subComment.modDate || subComment.regDate) }}</span>
                        </div>

                        <div v-if="editingSubComment === subComment.subCommentId" class="sub-comment-edit">
                          <textarea v-model="editSubCommentText"></textarea>
                          <div class="edit-actions">
                            <button @click="updateSubComment(comment.commentId, subComment.subCommentId)" class="btn btn-sm btn-success">저장</button>
                            <button @click="cancelSubEdit" class="btn btn-sm btn-outline">취소</button>
                          </div>
                        </div>

                        <div v-else class="sub-comment-text">{{ subComment.content }}</div>

                        <div v-if="globalStatus.isLoggedIn && Number(subComment.memberId) === globalStatus.loginUser.mno" class="sub-comment-actions">
                          <button @click="startSubEdit(subComment)" class="btn-text">수정</button>
                          <button @click="deleteSubComment(comment.commentId, subComment.subCommentId)" class="btn-text text-danger">삭제</button>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <div v-if="comments.length === 0" class="no-comments">
              <div class="no-comments-icon">💬</div>
              <p>첫 번째 댓글을 작성해보세요!</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </CommunityLayout>
</template>

<script setup>
import { ref, onMounted, inject, reactive } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import * as boardAPI from '../../lib/api/boardAPI'
import * as boardLikeAPI from '../../lib/api/boardLikeAPI'
import * as commentAPI from '../../lib/api/commentAPI'
import * as subCommentAPI from '../../lib/api/subCommentAPI'
import CommunityLayout from '@/components/common/CommunityLayout.vue'
import userimg from '@/assets/img/user.png'

const route = useRoute()
const router = useRouter()

const board = ref(null)
const loading = ref(true)
const comments = ref([])
const newComment = ref('')
const newSubComments = reactive({})
const editingComment = ref(null)
const editCommentText = ref('')
const editingSubComment = ref(null)
const editSubCommentText = ref('')

const globalStatus = inject('globalStatus')

const communityId = route.params.id || route.path.split('/').pop()
const boardId = route.params.bid || route.path.split('/').pop()

const fetchBoard = async () => {
  try {
    loading.value = true
    const res = await boardAPI.getBoard({ boardId })
    board.value = res.data
  } catch (err) {
    console.error('게시글 조회 실패:', err)
  } finally {
    loading.value = false
  }
}

const fetchComments = async () => {
  try {
    const res = await commentAPI.getComments({ boardId })
    console.log('댓글 조회 결과:', res)
    if (res.state === 200) {
      comments.value = res.data.map(comment => ({
        ...comment,
        showSubComments: false,
        subComments: []
      }))
    }
  } catch (err) {
    console.error('댓글 조회 실패:', err)
    comments.value = []
  }
}

const fetchSubComments = async (commentId) => {
  try {
    const res = await subCommentAPI.getSubComments({ commentId })
    if (res.state === 200) {
      const comment = comments.value.find(c => c.commentId === commentId)
      if (comment) {
        comment.subComments = res.data
      }
    }
  } catch (err) {
    console.error('대댓글 조회 실패:', err)
  }
}

const toggleLike = async () => {
  try {
    const res = await boardLikeAPI.likeBoard({ boardId })
    if (res.state === 200) {
      board.value.likeCnt = res.data
      board.value.liked = !board.value.liked
    }
  } catch (error) {
    console.error('좋아요 처리 중 오류 발생:', error)
    alert('좋아요 처리에 실패했습니다.')
  }
}

const submitComment = async () => {
  if (!newComment.value.trim()) return

  try {
    const res = await commentAPI.createComment({
      boardId,
      content: newComment.value.trim()
    })

    if (res.state === 200) {
      newComment.value = ''
      fetchComments()
    }
  } catch (error) {
    console.error('댓글 작성 실패:', error)
    alert('댓글 작성에 실패했습니다.')
  }
}

const startEdit = (comment) => {
  editingComment.value = comment.commentId
  editCommentText.value = comment.content
}

const cancelEdit = () => {
  editingComment.value = null
  editCommentText.value = ''
}

const updateComment = async (commentId) => {
  try {
    const res = await commentAPI.updateComment({
      boardId,
      commentId,
      content: editCommentText.value.trim()
    })

    if (res.state === 200) {
      cancelEdit()
      fetchComments()
    }
  } catch (error) {
    console.error('댓글 수정 실패:', error)
    alert('댓글 수정에 실패했습니다.')
  }
}

const deleteComment = async (commentId) => {
  if (!confirm('댓글을 삭제하시겠습니까?')) return

  try {
    const res = await commentAPI.deleteComment({ boardId, commentId })
    if (res.state === 200) {
      fetchComments()
    }
  } catch (error) {
    console.error('댓글 삭제 실패:', error)
    alert('댓글 삭제에 실패했습니다.')
  }
}

const toggleSubComments = async (commentId) => {
  const comment = comments.value.find(c => c.commentId === commentId)
  if (comment) {
    comment.showSubComments = !comment.showSubComments
    if (comment.showSubComments && comment.subComments.length === 0) {
      await fetchSubComments(commentId)
    }
  }
}

const submitSubComment = async (commentId) => {
  const content = newSubComments[commentId]
  if (!content?.trim()) return

  try {
    const res = await subCommentAPI.createSubComment({
      commentId,
      content: content.trim()
    })

    if (res.state === 200) {
      newSubComments[commentId] = ''
      await fetchSubComments(commentId)
    }
  } catch (error) {
    console.error('대댓글 작성 실패:', error)
    alert('답글 작성에 실패했습니다.')
  }
}

const startSubEdit = (subComment) => {
  editingSubComment.value = subComment.subCommentId
  editSubCommentText.value = subComment.content
}

const cancelSubEdit = () => {
  editingSubComment.value = null
  editSubCommentText.value = ''
}

const updateSubComment = async (commentId, subCommentId) => {
  try {
    const res = await subCommentAPI.updateSubComment({
      commentId,
      subCommentId,
      content: editSubCommentText.value.trim()
    })

    if (res.state === 200) {
      cancelSubEdit()
      await fetchSubComments(commentId)
    }
  } catch (error) {
    console.error('대댓글 수정 실패:', error)
    alert('답글 수정에 실패했습니다.')
  }
}

const deleteSubComment = async (commentId, subCommentId) => {
  if (!confirm('답글을 삭제하시겠습니까?')) return

  try {
    const res = await subCommentAPI.deleteSubComment({ commentId, subCommentId })
    if (res.state === 200) {
      await fetchSubComments(commentId)
    }
  } catch (error) {
    console.error('대댓글 삭제 실패:', error)
    alert('답글 삭제에 실패했습니다.')
  }
}

const delNotice = async (id) => {
  if (!confirm('정말 삭제하시겠습니까?')) return

  try {
    const res = await boardAPI.deleteBoard({ boardId: id })
    if (res.state === 200) {
      alert('삭제되었습니다.')
      router.push(`/community/${communityId}/community-board`)
    } else {
      alert('삭제에 실패했습니다.')
    }
  } catch (err) {
    console.error('삭제 오류:', err)
    alert('삭제 중 오류가 발생했습니다.')
  }
}

const goToList = () => router.push(`/community/${communityId}/community-board`)
const goToEdit = () => router.push(`/community/${communityId}/community-board/update/${boardId}`)

const formatDate = (str) => {
  const date = new Date(str)
  const now = new Date()
  const diff = now - date

  if (diff < 60000) return '방금 전'
  if (diff < 3600000) return `${Math.floor(diff / 60000)}분 전`
  if (diff < 86400000) return `${Math.floor(diff / 3600000)}시간 전`
  if (diff < 604800000) return `${Math.floor(diff / 86400000)}일 전`

  return date.toLocaleDateString('ko-KR')
}

onMounted(() => {
  fetchBoard()
  fetchComments()
})
</script>

<style lang="scss" scoped>
.board-detail-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.title-section {
  text-align: center;
  margin-bottom: 40px;

  .page-title {
    font-size: 28px;
    font-weight: 700;
    color: #2c3e50;
    margin-bottom: 12px;
  }

  .title-underline {
    width: 60px;
    height: 4px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    margin: 0 auto;
    border-radius: 2px;
  }
}

.loading-container {
  text-align: center;
  padding: 60px 0;

  .loading-spinner {
    width: 40px;
    height: 40px;
    border: 4px solid #f3f3f3;
    border-top: 4px solid #667eea;
    border-radius: 50%;
    animation: spin 1s linear infinite;
    margin: 0 auto 20px;
  }

  p {
    color: #666;
    font-size: 16px;
  }
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.no-content {
  text-align: center;
  padding: 60px 0;

  .no-content-icon {
    font-size: 48px;
    margin-bottom: 20px;
  }

  p {
    color: #666;
    font-size: 18px;
    margin-bottom: 30px;
  }
}

.board-content-wrapper {
  .board-header {
    margin-bottom: 30px;

    .board-title {
      font-size: 24px;
      font-weight: 700;
      color: #2c3e50;
      margin-bottom: 20px;
      line-height: 1.4;
    }

    .board-meta {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 20px;
      background: #f8f9fa;
      border-radius: 8px;

      .author-info {
        display: flex;
        align-items: center;
        gap: 12px;

        .author-avatar {
          width: 50px;
          height: 50px;
          border-radius: 50%;
          overflow: hidden;
          border: 2px solid #e9ecef;

          img {
            width: 100%;
            height: 100%;
            object-fit: cover;
          }
        }

        .author-details {
          .author-name {
            font-weight: 600;
            color: #2c3e50;
            font-size: 16px;
          }

          .post-date {
            color: #666;
            font-size: 14px;
            margin-top: 2px;
          }
        }
      }

      .board-actions {
        .like-btn {
          display: flex;
          align-items: center;
          gap: 8px;
          padding: 8px 16px;
          border: 2px solid #dee2e6;
          background: white;
          border-radius: 20px;
          cursor: pointer;
          transition: all 0.3s ease;
          color: #666;

          &:hover {
            border-color: #ff6b6b;
            color: #ff6b6b;
          }

          &.liked {
            border-color: #ff6b6b;
            background: #ff6b6b;
            color: white;
          }

          i {
            font-size: 16px;
          }

          span {
            font-weight: 600;
          }
        }
      }
    }
  }

  .board-body {
    margin-bottom: 40px;

    .content-text {
      font-size: 16px;
      line-height: 1.6;
      color: #333;
      white-space: pre-wrap;
      padding: 30px;
      background: #fafafa;
      border-radius: 8px;
      border-left: 4px solid #667eea;
    }
  }

  .board-footer {
    margin-bottom: 40px;

    .button-group {
      display: flex;
      justify-content: space-between;
      align-items: center;

      .owner-buttons {
        display: flex;
        gap: 10px;
      }
    }
  }
}

.btn {
  padding: 10px 20px;
  border-radius: 6px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  text-decoration: none;
  border: none;

  &.btn-primary {
    background: #667eea;
    color: white;

    &:hover {
      background: #5a67d8;
    }

    &:disabled {
      background: #cbd5e0;
      cursor: not-allowed;
    }
  }

  &.btn-success {
    background: #48bb78;
    color: white;

    &:hover {
      background: #38a169;
    }
  }

  &.btn-danger {
    background: #f56565;
    color: white;

    &:hover {
      background: #e53e3e;
    }
  }

  &.btn-outline {
    background: white;
    border: 2px solid #e2e8f0;
    color: #4a5568;

    &:hover {
      border-color: #667eea;
      color: #667eea;
    }
  }

  &.btn-sm {
    padding: 6px 12px;
    font-size: 14px;
  }
}

.comments-section {
  border-top: 2px solid #e2e8f0;
  padding-top: 40px;

  .comments-header {
    margin-bottom: 30px;

    h3 {
      font-size: 20px;
      font-weight: 700;
      color: #2c3e50;

      .comment-count {
        color: #667eea;
        font-size: 18px;
      }
    }
  }

  .comment-form {
    margin-bottom: 30px;

    .comment-input-wrapper {
      display: flex;
      gap: 12px;

      .user-avatar {
        width: 40px;
        height: 40px;
        border-radius: 50%;
        overflow: hidden;
        border: 2px solid #e9ecef;
        flex-shrink: 0;

        img {
          width: 100%;
          height: 100%;
          object-fit: cover;
        }
      }

      .input-area {
        flex: 1;

        textarea {
          width: 100%;
          min-height: 100px;
          padding: 12px;
          border: 2px solid #e2e8f0;
          border-radius: 8px;
          resize: vertical;
          font-size: 14px;

          &:focus {
            outline: none;
            border-color: #667eea;
          }
        }

        .form-actions {
          margin-top: 10px;
          text-align: right;
        }
      }
    }
  }

  .login-notice {
    text-align: center;
    padding: 20px;
    background: #f8f9fa
  }
}

.comments-list {
  .comment-item {
    display: flex;
    gap: 12px;
    margin-bottom: 20px;
    padding-bottom: 20px;
    border-bottom: 1px solid #e2e8f0;

    .comment-avatar {
      width: 40px;
      height: 40px;
      border-radius: 50%;
      overflow: hidden;
      border: 2px solid #e9ecef;
      flex-shrink: 0;

      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }
    }

    .comment-content {
      flex: 1;

      .comment-header {
        display: flex;
        justify-content: space-between;
        font-size: 14px;
        color: #4a5568;
        margin-bottom: 6px;

        .comment-author {
          font-weight: 600;
        }

        .comment-date {
          font-size: 13px;
          color: #a0aec0;
        }
      }

      .comment-text {
        font-size: 15px;
        color: #2d3748;
        white-space: pre-wrap;
        margin-bottom: 8px;
      }

      .comment-actions {
        display: flex;
        gap: 10px;
        align-items: center;
        font-size: 13px;
        color: #718096;

        .reply-btn {
          background: none;
          border: none;
          cursor: pointer;
          color: #667eea;
          padding: 0;

          i {
            margin-right: 4px;
          }

          &:hover {
            text-decoration: underline;
          }
        }

        .comment-owner-actions {
          display: flex;
          gap: 8px;

          .btn-text {
            background: none;
            border: none;
            cursor: pointer;
            color: #4a5568;

            &:hover {
              text-decoration: underline;
            }

            &.text-danger {
              color: #e53e3e;

              &:hover {
                text-decoration: underline;
              }
            }
          }
        }
      }

      .comment-edit textarea {
        width: 100%;
        padding: 8px;
        font-size: 14px;
        border: 2px solid #cbd5e0;
        border-radius: 6px;
        resize: vertical;
        margin-bottom: 8px;

        &:focus {
          outline: none;
          border-color: #667eea;
        }
      }

      .edit-actions {
        display: flex;
        gap: 8px;
      }
    }
  }
}

.sub-comments-section {
  margin-top: 16px;
  padding-left: 48px;
  border-left: 2px solid #e2e8f0;

  .sub-comment-form {
    margin-bottom: 16px;

    .sub-comment-input {
      display: flex;
      flex-direction: column;
      gap: 8px;

      textarea {
        width: 100%;
        min-height: 80px;
        padding: 10px;
        font-size: 14px;
        border: 2px solid #e2e8f0;
        border-radius: 6px;
        resize: vertical;

        &:focus {
          outline: none;
          border-color: #667eea;
        }
      }

      button {
        align-self: flex-end;
      }
    }
  }

  .sub-comments-list {
    display: flex;
    flex-direction: column;
    gap: 16px;
  }

  .sub-comment-item {
    display: flex;
    gap: 10px;

    .sub-comment-avatar {
      width: 36px;
      height: 36px;
      border-radius: 50%;
      overflow: hidden;
      border: 2px solid #e2e8f0;
      flex-shrink: 0;

      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }
    }

    .sub-comment-content {
      flex: 1;

      .sub-comment-header {
        display: flex;
        justify-content: space-between;
        font-size: 13px;
        color: #4a5568;
        margin-bottom: 4px;

        .sub-comment-author {
          font-weight: 600;
        }

        .sub-comment-date {
          color: #a0aec0;
        }
      }

      .sub-comment-text {
        font-size: 14px;
        color: #2d3748;
        white-space: pre-wrap;
        margin-bottom: 6px;
      }

      .sub-comment-edit textarea {
        width: 100%;
        padding: 8px;
        font-size: 14px;
        border: 2px solid #cbd5e0;
        border-radius: 6px;
        resize: vertical;
        margin-bottom: 8px;

        &:focus {
          outline: none;
          border-color: #667eea;
        }
      }

      .edit-actions {
        display: flex;
        gap: 8px;
      }

      .sub-comment-actions {
        display: flex;
        gap: 8px;
        font-size: 13px;

        .btn-text {
          background: none;
          border: none;
          cursor: pointer;
          color: #4a5568;

          &:hover {
            text-decoration: underline;
          }

          &.text-danger {
            color: #e53e3e;

            &:hover {
              text-decoration: underline;
            }
          }
        }
      }
    }
  }
}

</style>
