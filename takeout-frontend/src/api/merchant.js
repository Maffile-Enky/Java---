import request from './request'

export const getStoreList = (params) => request.get('/merchant/list', { params })
export const getMerchantById = (id) => request.get(`/merchant/${id}`)
export const getDishList = (merchantId) => request.get(`/dish/list/${merchantId}`)
