import request from './request'

// 创建支付订单
export const createPayment = (data) => request.post('/payment/create', data)

// 查询支付状态
export const queryPaymentStatus = (paymentNo) => request.get(`/payment/status/${paymentNo}`)

// 查询用户支付记录
export const getPaymentList = (params) => request.get('/payment/list', { params })

// 申请退款
export const applyRefund = (paymentNo, reason) =>
  request.post('/payment/refund', null, { params: { paymentNo, reason } })

// 关闭支付订单
export const closePayment = (paymentNo) =>
  request.post('/payment/close', null, { params: { paymentNo } })

// 模拟支付成功（沙箱环境专用）
export const mockPaymentSuccess = (paymentNo) =>
  request.post('/payment/mock-success', null, { params: { paymentNo } })
