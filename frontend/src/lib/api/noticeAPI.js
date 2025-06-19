import client from './client'

export const getNotices = async () => {
    const res = await client.get('/notices/list')
    return res.data.data
}

export const createNotice = async (noticeData) => {
  const res = await client.post('/notices', noticeData)
  return res.data
}

export const getNotice  = async (id) => {
  const res = await client.get(`/notices/${id}`)
  return res.data
}

export const deleteNotice   = async (id) => {
  const res = await client.delete(`/notices/${id}`)
  return res.data
}

export const updateNotice = async (id, noticeData) => {
  const res = await client.put(`/notices/${id}`, noticeData)
  return res.data
}
