import request from './request'

export const createOrder = (data) => request.post('/order/create', data)
export const getOrderList = (params) => request.get('/order/list', { params })
