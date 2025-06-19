import client from './client'

export const sendVerificationCode = async ({ email }) => {
    const res = await client.post('/email/verification-code', {
        email
    })

    return res.data
}

export const sendVerificationCodeChangePw = async ({ email }) => {
    const res = await client.post('/email/verification-code/changePw', {
        email
    })

    return res.data
}
