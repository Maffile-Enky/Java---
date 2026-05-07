import request from './request'

// Auth
export const login = (data) => request.post('/auth/login', data)
export const register = (data) => request.post('/auth/register', data)
export const loginByPhone = (data) => request.post('/auth/login/phone', data)
export const loginByWechat = (data) => request.post('/auth/login/wechat', data)
export const sendSmsCode = (data) => request.post('/auth/sms/send', data)
export const getUserInfo = () => request.get('/auth/info')

// Profile
export const getProfile = () => request.get('/user/profile')
export const updateProfile = (data) => request.put('/user/profile', data)
export const changePassword = (data) => request.put('/user/password', data)

// Address
export const getAddressList = () => request.get('/user/address/list')
export const getAddressById = (id) => request.get(`/user/address/${id}`)
export const saveAddress = (data) => request.post('/user/address', data)
export const updateAddress = (id, data) => request.put(`/user/address/${id}`, data)
export const deleteAddress = (id) => request.delete(`/user/address/${id}`)
export const setDefaultAddress = (id) => request.put(`/user/address/${id}/default`)
