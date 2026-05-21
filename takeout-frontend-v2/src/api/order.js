import request from './request'

export const createOrder = (data) => request.post('/order/create', data)
export const getOrderList = (params) => request.get('/order/list', { params })
export const getOrderDetail = (id) => request.get(`/order/${id}`)
export const cancelOrder = (id) => request.put(`/order/${id}/cancel`)

// Merchant order management
export const getMerchantOrderList = (params) => request.get('/order/merchant/list', { params })
export const updateOrderStatus = (id, status) => request.put(`/order/${id}/status`, null, { params: { status } })
