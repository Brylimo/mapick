import client from './client'

export const likeBoard = async ({ boardId }) => {    
    const res = await client.post(`/board-likes/${boardId}`)
    return res.data
}