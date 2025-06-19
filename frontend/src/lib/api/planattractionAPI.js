import client from './client'

export const getPlanAttractions = async (planid) => {
  const res = await client.get('/plan-attractions/list', {
    params: { planid },
  })
  return res.data.data
}

export const createPlanAttraction = async (planid, attractionData) => {
  const res = await client.post('/plan-attractions', attractionData, {
    params: { planid },
  })
  return res.data
}

export const createPlanAttraction2 = async (planid, attractionData) => {
  const res = await client.post('/plan-attractions/direct', attractionData, {
    params: { planid },
  })
  return res.data
}

export const updatePlanAttraction = async (attractionData) => {
  const res = await client.put('/plan-attractions', attractionData)
  return res.data
}

export const deletePlanAttraction = async (id) => {
  const res = await client.delete(`/plan-attractions/${id}`)
  return res
}
