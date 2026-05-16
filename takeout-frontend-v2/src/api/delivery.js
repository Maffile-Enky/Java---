import request from './request'

// Rider management
export const registerRider = (data) => request.post('/delivery/rider/register', data)
export const getRiderInfo = () => request.get('/delivery/rider/info')
export const updateRiderStatus = (status) => request.put('/delivery/rider/status', null, { params: { status } })
export const updateLocation = (data) => request.post('/delivery/rider/location', data)
export const getRiderTasks = (params) => request.get('/delivery/rider/tasks', { params })
export const pickupOrder = (taskNo) => request.post(`/delivery/rider/tasks/${taskNo}/pickup`)
export const deliverOrder = (taskNo) => request.post(`/delivery/rider/tasks/${taskNo}/deliver`)

// Task marketplace
export const getAvailableTasks = () => request.get('/delivery/task/list')
export const getTaskDetail = (taskNo) => request.get(`/delivery/task/${taskNo}`)
export const grabOrder = (taskNo) => request.post(`/delivery/task/${taskNo}/grab`)

// Customer tracking
export const trackDelivery = (orderNo) => request.get(`/delivery/task/track/${orderNo}`)
