import client from './client'

export const getPlans = async () => {
    const res = await client.get('/plans/list')
    return res.data.data
}

export const createPlan = async (name) => {
  const res = await client.post('/plans', {
    name
  })
  return res.data
}

export const getPlan  = async (name) => {
  const res = await client.get(`/plans`,{
    params: { name }
  })
  return res.data
}

export const getTimeline = async (name) => {
  const res = await client.get('/plans/timeline',{
    params: { name }
  })
  console.log("요청완료:" , res.data)
  return res.data
}

export const getRoute = async (name) => {
  const res = await client.get('/plans/route',{
    params: { name }
  })
  console.log("route 요청완료:" , res.data)
  return res.data
}
