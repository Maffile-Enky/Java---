import request from './request'

export const searchMerchants = (params) => request.get('/search/merchants', { params })
export const searchDishes = (params) => request.get('/search/dishes', { params })
export const getHotMerchants = (limit = 10) => request.get('/search/hot', { params: { limit } })
export const getNearbyMerchants = (params) => request.get('/search/nearby', { params })
