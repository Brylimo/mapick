import client from './client'

export const getExpenseList = async (planId) => {
    const res = await client.get('/plan-expense',{
        params: { planId }
    })
    return res.data
}

export const addExpense = async (expenseData) => {
    const res = await client.post('/plan-expense',expenseData)
    return res.data
}

export const deleteExpense = async (expId) => {
const res = await client.delete(`/plan-expense/${expId}`)
    return res.data
}

export const uploadReceipt = async (expId, formData) => {
  const res = await client.post(`/plan-expense/${expId}/receipt`, formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
  return res.data
}

export const deleteReceipt = async (expId) => {
  const res = await client.delete(`/plan-expense/${expId}/receipt`)
  return res.data
}


export const downloadReceipt = async (expId) => {
  try {
    const res = await client.get(`/plan-expense/${expId}/receipt`, {
      responseType: 'blob',
    })

    const disposition = res.headers['content-disposition']
    let fileName = 'receipt.png' // 기본값

    if (disposition && disposition.includes('filename=')) {
      const fileNameMatch = disposition.match(/filename="?([^"]+)"?/)
      if (fileNameMatch.length > 1) {
        fileName = decodeURIComponent(fileNameMatch[1])
      }
    }

    const blob = res.data
    const url = URL.createObjectURL(blob)

    const a = document.createElement('a')
    a.href = url
    a.download = fileName
    a.click()

    URL.revokeObjectURL(url)
  } catch (e) {
    console.error('영수증 다운로드 실패:', e)
    alert('❌ 다운로드에 실패했습니다.')
  }
}




