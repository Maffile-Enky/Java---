import request from './request'

export const updateLocation = (data) => request.post('/delivery/location', data)
