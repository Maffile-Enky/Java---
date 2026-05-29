import request from './request'

export const getNotifications = (params) => request.get('/notification/list', { params })
export const getOnlineStatus = () => request.get('/notification/online-status')
