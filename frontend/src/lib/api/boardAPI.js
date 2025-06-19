import client from './client'

export const createBoard = async ({ title, content, communityId, reservationDate,  reservationTime}) => {
    const res = await client.post(`/boards/${communityId}`, {
        title,
        content,
        reservationDate: reservationDate || '',
        reservationTime: reservationTime || ''
    })

    return res.data
}

export const getBoards = async ({ communityId, opt, query }) => {
    const res = await client.get(`/boards/list`, {
        params: {
            communityId,
            opt,
            query
        }
    })

    return res.data
}

export const getBoard = async ({ boardId }) => {
    const res = await client.get(`/boards/${boardId}`)

    return res.data
}

export const deleteBoard = async ({ boardId }) => {
    const res = await client.delete(`/boards/${boardId}`)

    return res.data
}

export const updateBoard = async ({ boardId, title, content }) => {
    const res = await client.put(`/boards/list`, {
        boardId,
        title,
        content
    })

    return res.data
}

