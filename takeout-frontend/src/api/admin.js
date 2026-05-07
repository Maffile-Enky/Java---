import request from './request'

// Admin - Stats
export const getAdminStats = () => request.get('/user/admin/stats')

// Admin - User Management
export const getAdminUserList = (params) => request.get('/user/admin/user/list', { params })
export const updateUserStatus = (id, status) => request.put(`/user/admin/user/status/${id}`, null, { params: { status } })
export const updateUserRole = (id, role) => request.put(`/user/admin/user/role/${id}`, null, { params: { role } })

// Admin - Merchant Application Management
export const getApplicationList = (params) => request.get('/user/admin/merchant-application/list', { params })
export const approveApplication = (id) => request.put(`/user/admin/merchant-application/approve/${id}`)
export const rejectApplication = (id, adminNote) => request.put(`/user/admin/merchant-application/reject/${id}`, { adminNote })

// Admin - Merchant Management
export const getAdminMerchantList = () => request.get('/merchant/admin/list')
export const updateMerchantStatus = (id, status) => request.put(`/merchant/admin/status/${id}`, null, { params: { status } })
export const updateMerchantSortWeight = (id, sortWeight) => request.put(`/merchant/admin/sort-weight/${id}`, null, { params: { sortWeight } })
