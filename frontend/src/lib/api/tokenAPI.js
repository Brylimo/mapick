import client from './client';

export const login = async ({ email, password }) => {
    const res = await client.post('/token/login', { email, password });
    return res.data;
}

export const logout = () => client.get('/token/logout');

export const validate = () => client.get('/token/validate');