import client from './client'

export const register = async ({ name, email, password, role }) => {
  const res = await client.post('/members', { name, email, password, role })
  return res.data
}

export const getUser = async () => {
    const res = await client.get('/members')
    return res.data
}

export const updateUser = async ({ name, email, password, role }) => {
  const res = await client.put('/members', {
    name, email, password, role
  })

  return res.data;
}

export const deleteUser = async (id) => {
  const res = await client.delete(`/members/${id}`)
  return res.data
}

export const verifyCode = async ({ email, code }) => {
  const res = await client.post('/members/verification-code', {
      email, code
  })

  return res.data
}

export const findPasswordRequest = async ({ name, email }) => {
  const res = await client.post('/members/memberFindPw', { name, email })
  return res.data
}

export const changePassword  = async ({ email, password, password2 }) => {
  const res = await client.put('/members/memberChangePw', {
    email,
    password,
    password2
  })
  return res.data
}
