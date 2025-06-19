import client from './client'

export const sendCommunityMemberReviewRequest = async ({ nickname, reason, communityId }) => {
    const res = await client.post('/community-members/review-request', {
        nickname,
        reason,
        communityId
    });
    return res.data
}

export const getJoinRequestsByCommunityId = async (communityId) => {
    return await client.get(`/community-members/request/${communityId}`)
}

export const approveJoinRequest = async (joinRequestId) => {
    return await client.post(`/community-members/${joinRequestId}/approve`)
}

export const rejectJoinRequest = async (joinRequestId) => {
    return await client.post(`/community-members/${joinRequestId}/reject`)
}

export const checkIsMember = async (communityId) => {
  const res = await client.get(`/community-members/${communityId}/check-member`);
  return res.data
}

