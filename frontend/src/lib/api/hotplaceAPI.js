import client from './client'

export const getHotplaces = async () => {
    const res = await client.get('/hotplaces/list')
    return res.data
}

export const createHotplace = async (attractionDTO) => {
    const res = await client.post('/hotplaces', attractionDTO)
    return res.data
}

export const deleteHotplace = async (hotplaceId) => {
    const res = await client.delete(`/hotplaces/${hotplaceId}`)
    return res.data
}
