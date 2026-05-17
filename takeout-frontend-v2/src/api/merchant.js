import request from './request'

export const getStoreList = (params) => request.get('/merchant/list', { params })
export const getMerchantById = (id) => request.get(`/merchant/${id}`)
export const getDishList = (merchantId) => request.get(`/dish/list/${merchantId}`)

// Merchant self-management
export const getMyMerchant = (id) => {
  if (id) return request.get(`/merchant/${id}`)
  return request.get('/merchant/my')
}
export const createMyMerchant = (data) => request.post('/merchant/my', data)
export const updateMyMerchant = (data) => request.put('/merchant/my', data)

// Dish management
export const createDish = (data) => request.post('/dish', data)
export const updateDish = (data) => request.put('/dish', data)
export const deleteDish = (id) => request.delete(`/dish/${id}`)

// Stock management
export const updateDishStock = (id, stock) => request.put(`/dish/stock/${id}`, null, { params: { stock } })
