import client from './client'

export const getTags = async ({ communityId }) => {
  const res = await client.get(`/tags/list/${communityId}`)
    
  return res.data.data
}
