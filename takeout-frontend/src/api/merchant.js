import request from './request'

export const getStoreList = (params) => request.get('/merchant/list', { params })
export const getMerchantById = (id) => request.get(`/merchant/${id}`)
export const getDishList = (merchantId) => request.get(`/dish/list/${merchantId}`)

// Merchant self-management — unwrap Result.data so callers get the entity directly
export const getMyMerchant = async () => {
  const res = await request.get('/merchant/my')
  return res.data ?? null
}
export const createMyMerchant = (data) => request.post('/merchant/my', data)
export const updateMyMerchant = (data) => request.put('/merchant/my', data)

// Dish management (merchant)
export const createDish = (data) => request.post('/dish', data)
export const updateDish = (data) => request.put('/dish', data)
export const deleteDish = (id) => request.delete(`/dish/${id}`)

// Stock management
export const updateDishStock = (id, stock) => request.put(`/dish/stock/${id}`, null, { params: { stock } })
