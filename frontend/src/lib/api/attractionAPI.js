import client from './client'

export const fetchGuguns = async (sidoCode) => {
    const res = await client.get('/guguns', {
    params: { sidoCode },
    })
    return res.data.data
}

export const fetchSidos = async () => {
    const res = await client.get('/attractions/sidos')
    return res.data.data
}

export const fetchContentTypes = async () => {
  const res = await client.get('/attractions/content-type')
  return res.data.data
}

export const fetchAttractions = async ({ region, subregion, category }) => {
  const res = await client.get('/attractions',{
    params: { region, subregion, category },
  })
  return res.data.data
}

export const fetchAttractionByKeyword = async ({ keyword }) => {
  const res = await client.get('/attractions/keyword', {
    params: { keyword }
  })
  console.log(res.data)
  return res.data
}
