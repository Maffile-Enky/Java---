import request from './request'

export const createOrder = (data) => request.post('/order/create', data)
export const getOrderList = (params) => request.get('/order/list', { params })
export const getOrderDetail = (id) => request.get(`/order/${id}`)
export const cancelOrder = (id) => request.put(`/order/${id}/cancel`)
