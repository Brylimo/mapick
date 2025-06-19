import client from './client'

export const createCommunityLike = async ({ communityId }) => {
    const res = await client.post('/community-likes', {
        communityId
    })

    return res.data
}

export const checkCommunityLike = async ({ communityId }) => {
    
    const res = await client.get('/community-likes/check', {
        params: { communityId }
    })

    return res.data
}

export const deleteCommunityLike = async ({ communityId }) => {
    const res = await client.delete(`/community-likes/${communityId}`)

    return res.data
}