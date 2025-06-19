import client from './client'

export const createCommunity = async ({ communityName, desc, tagList, categoryList }) => {
  const res = await client.post('/communities/communityCreate', {
    communityName,
    desc,
    tagList,
    categoryList,
  })

  return res.data
}

export const getCommunityList = async () => {
  const res = await client.get('/communities/list');
  return res.data
}

export const getCommunity = async ({ communityId }) => {
  const res = await client.get('/communities', {
      params: { communityId }
  })
  return res.data
}

export const getSearchedCommunities = async ({ opt, query }) => {
  const res = await client.get('/communities/searchCommunity', {
    params: {
      opt,
      query
    }
  })
  return res.data
}

export const getPopularCommunities = async () => {
  const res = await client.get('/communities/top3')
  return res.data
}
