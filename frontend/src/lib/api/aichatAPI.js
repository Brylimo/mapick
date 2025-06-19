// src/lib/api/aichatAPI.js

import client from './client'

export const getSimpleChat = async (message) => {
    const res = await client.post('/ai/simple', { message })
    return res.data.data.message  
}
