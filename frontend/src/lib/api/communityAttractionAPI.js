import qs from 'qs'
import client from './client'

export const getCommunityAttractionList = async ({ query, communityid, tags }) => {
  const res = await client.get('/community-attractions', {
    params: { query, communityid, tags },
    paramsSerializer: (params) => qs.stringify(params, { arrayFormat: 'repeat' }),
  })

  return res.data
}

export const createCommunityAttraction = async (formData) => {
  const res = await client.post('/community-attractions', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  })

  return res.data
}

export const getPopularAttractionList = async () => {
  const res = await client.get('/community-attractions/popular')

  console.log("tt")
  return res.data
}