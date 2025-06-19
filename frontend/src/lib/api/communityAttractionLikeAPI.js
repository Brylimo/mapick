import client from './client'

export const createCommunityAttractionLike = async ({ communityAttractionId }) => {
    const res = await client.post('/community-attraction-likes', {
        communityAttractionId
    })

    return res.data
}

export const checkCommunityAttractionLike = async ({ communityAttractionId }) => {
    
    const res = await client.get('/community-attraction-likes/check', {
        params: { communityAttractionId }
    })

    return res.data
}

export const deleteCommunityAttractionLike = async ({ communityAttractionId }) => {
    const res = await client.delete(`/community-attraction-likes/${communityAttractionId}`)

    return res.data
}