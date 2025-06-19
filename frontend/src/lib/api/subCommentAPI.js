import client from './client'

// 대댓글 목록 조회
export const getSubComments = async ({ commentId }) => {
  const res = await client.get(`/sub-comment/list/${commentId}`)
  return res.data
}

// 대댓글 작성
export const createSubComment = async ({ commentId, content }) => {
  const res = await client.post(`/sub-comment/${commentId}`, { content })
  return res.data
}

// 대댓글 수정
export const updateSubComment = async ({ commentId, subCommentId, content }) => {
  const res = await client.put(`/sub-comment/${commentId}`, {
    subCommentId,
    content,
  })
  return res.data
}

// 대댓글 삭제
export const deleteSubComment = async ({ commentId, subCommentId }) => {
  const res = await client.delete(`/sub-comment/${commentId}`, {
    params: { subCommentId },
  })
  return res.data
}
