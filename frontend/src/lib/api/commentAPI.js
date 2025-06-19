import client from './client'

// 댓글 목록 조회
export const getComments = async ({ boardId }) => {
  const res = await client.get(`/comment/list/${boardId}`)
  return res.data
}

// 댓글 작성
export const createComment = async ({ boardId, content }) => {
  const res = await client.post(`/comment/${boardId}`, { content })
  return res.data
}

// 댓글 수정
export const updateComment = async ({ boardId, commentId, content }) => {
  const res = await client.put(`/comment/${boardId}`, {
    commentId,
    content,
  })
  return res.data
}

// 댓글 삭제
export const deleteComment = async ({ boardId, commentId }) => {
  const res = await client.delete(`/comment/${boardId}`, {
    params: { commentId },
  })
  return res.data
}
