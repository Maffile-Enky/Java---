import request from './request'

export const getStoreList = (params) => request.get('/merchant/stores', { params })
export const getMenu = (storeId) => request.get('/merchant/stores/' + storeId + '/menu')
