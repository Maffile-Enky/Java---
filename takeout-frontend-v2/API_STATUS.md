# API Status — takeout-frontend-v2

> 记录 64 个后端接口在新前端中的调用情况。

## 图例

| 标记 | 含义 |
|------|------|
| ✅ | 页面中已调用 |
| ⚠️ | 接口已导入，但页面用占位/静态数据，未实际调用 |
| ❌ | 接口已导出但未在任何页面使用 |

---

## user.js — 17 个接口

| 接口 | 方法 | 路径 | 状态 | 页面 |
|------|------|------|------|------|
| login | POST | /auth/login | ✅ | LoginView |
| register | POST | /auth/register | ✅ | RegisterView |
| loginByPhone | POST | /auth/login/phone | ✅ | LoginView (SMS tab) |
| loginByWechat | POST | /auth/login/wechat | ❌ | — |
| sendSmsCode | POST | /auth/sms/send | ✅ | LoginView |
| getUserInfo | GET | /auth/info | ✅ | auth store |
| getProfile | GET | /user/profile | ⚠️ | ProfileView (用 auth store) |
| updateProfile | PUT | /user/profile | ❌ | — |
| changePassword | PUT | /user/password | ❌ | — |
| getAddressList | GET | /user/address/list | ✅ | AddressView |
| getAddressById | GET | /user/address/{id} | ❌ | — |
| saveAddress | POST | /user/address | ✅ | AddressView |
| updateAddress | PUT | /user/address/{id} | ✅ | AddressView |
| deleteAddress | DELETE | /user/address/{id} | ✅ | AddressView |
| setDefaultAddress | PUT | /user/address/{id}/default | ❌ | — |
| submitMerchantApplication | POST | /user/merchant-application | ❌ | — |
| getMerchantApplicationStatus | GET | /user/merchant-application/status | ❌ | — |

---

## order.js — 6 个接口

| 接口 | 方法 | 路径 | 状态 | 页面 |
|------|------|------|------|------|
| createOrder | POST | /order/create | ⚠️ | CartView (去结算跳转，未实际下单) |
| getOrderList | GET | /order/list | ✅ | OrdersView |
| getOrderDetail | GET | /order/{id} | ✅ | OrderDetailView, PaymentView |
| cancelOrder | PUT | /order/{id}/cancel | ✅ | OrderDetailView |
| getMerchantOrderList | GET | /order/merchant/list | ✅ | MerchantOrdersView |
| updateOrderStatus | PUT | /order/{id}/status | ✅ | MerchantOrdersView |

---

## merchant.js — 11 个接口

| 接口 | 方法 | 路径 | 状态 | 页面 |
|------|------|------|------|------|
| getStoreList | GET | /merchant/list | ⚠️ | RestaurantsView (用 searchMerchants) |
| getMerchantById | GET | /merchant/{id} | ✅ | RestaurantDetailView |
| getDishList | GET | /dish/list/{merchantId} | ✅ | RestaurantDetailView |
| getMyMerchant | GET | /merchant/my | ✅ | DishManageView, SettingsView |
| createMyMerchant | POST | /merchant/my | ❌ | — |
| updateMyMerchant | PUT | /merchant/my | ✅ | SettingsView |
| createDish | POST | /dish | ✅ | DishManageView |
| updateDish | PUT | /dish | ✅ | DishManageView |
| deleteDish | DELETE | /dish/{id} | ❌ | — |
| updateDishStock | PUT | /dish/stock/{id} | ❌ | — |

---

## admin.js — 9 个接口

| 接口 | 方法 | 路径 | 状态 | 页面 |
|------|------|------|------|------|
| getAdminStats | GET | /user/admin/stats | ✅ | Admin DashboardView |
| getAdminUserList | GET | /user/admin/user/list | ✅ | UserManageView |
| updateUserStatus | PUT | /user/admin/user/status/{id} | ❌ | — |
| updateUserRole | PUT | /user/admin/user/role/{id} | ❌ | — |
| getApplicationList | GET | /user/admin/merchant-application/list | ✅ | ApplicationManageView |
| approveApplication | PUT | /user/admin/merchant-application/approve/{id} | ✅ | ApplicationManageView |
| rejectApplication | PUT | /user/admin/merchant-application/reject/{id} | ✅ | ApplicationManageView |
| getAdminMerchantList | GET | /merchant/admin/list | ✅ | MerchantManageView |
| updateMerchantStatus | PUT | /merchant/admin/status/{id} | ✅ | MerchantManageView |
| updateMerchantSortWeight | PUT | /merchant/admin/sort-weight/{id} | ❌ | — |

---

## delivery.js — 11 个接口

| 接口 | 方法 | 路径 | 状态 | 页面 |
|------|------|------|------|------|
| registerRider | POST | /delivery/rider/register | ✅ | RiderRegisterView |
| getRiderInfo | GET | /delivery/rider/info | ⚠️ | rider store |
| updateRiderStatus | PUT | /delivery/rider/status | ✅ | RiderDashboardView |
| updateLocation | POST | /delivery/rider/location | ❌ | — |
| getRiderTasks | GET | /delivery/rider/tasks | ✅ | RiderDashboardView, RiderTasksView |
| pickupOrder | POST | /delivery/rider/tasks/{taskNo}/pickup | ⚠️ | RiderTaskDetailView (用 updateDeliveryStatus) |
| deliverOrder | POST | /delivery/rider/tasks/{taskNo}/deliver | ⚠️ | RiderTaskDetailView (用 updateDeliveryStatus) |
| getAvailableTasks | GET | /delivery/task/list | ❌ | — |
| getTaskDetail | GET | /delivery/task/{taskNo} | ✅ | RiderTaskDetailView |
| grabOrder | POST | /delivery/task/{taskNo}/grab | ❌ | — |
| trackDelivery | GET | /delivery/task/track/{orderNo} | ✅ | DeliveryTrackingView |

---

## payment.js — 5 个接口

| 接口 | 方法 | 路径 | 状态 | 页面 |
|------|------|------|------|------|
| createPayment | POST | /payment/create | ✅ | PaymentView (payOrder) |
| queryPaymentStatus | GET | /payment/status/{paymentNo} | ❌ | — |
| getPaymentList | GET | /payment/list | ❌ | — |
| applyRefund | POST | /payment/refund | ❌ | — |
| closePayment | POST | /payment/close | ❌ | — |

---

## notification.js — 2 个接口

| 接口 | 方法 | 路径 | 状态 | 页面 |
|------|------|------|------|------|
| getNotifications | GET | /notification/list | ✅ | NotificationView |
| getOnlineStatus | GET | /notification/online-status | ❌ | — |

---

## search.js — 4 个接口

| 接口 | 方法 | 路径 | 状态 | 页面 |
|------|------|------|------|------|
| searchMerchants | GET | /search/merchants | ✅ | RestaurantsView |
| searchDishes | GET | /search/dishes | ❌ | — |
| getHotMerchants | GET | /search/hot | ❌ | — |
| getNearbyMerchants | GET | /search/nearby | ❌ | — |

---

## WebSocket 端点

| 端点 | 状态 | 说明 |
|------|------|------|
| ws://host/ws/delivery/{orderId} | ✅ | DeliveryTrackingView 实时追踪 |
| ws://host/ws/notification | ❌ | 通知推送（未接入） |

---

## 统计

- **总接口数**: 64 + 2 WS = 66
- **已调用 (✅)**: 36
- **占位/部分调用 (⚠️)**: 7
- **未使用 (❌)**: 21 + 1 WS

### 未实现的主要功能

1. **微信登录** — loginByWechat
2. **个人资料编辑** — updateProfile, changePassword
3. **商家入驻申请** — submitMerchantApplication, getMerchantApplicationStatus
4. **菜品删除/库存** — deleteDish, updateDishStock
5. **用户状态/角色管理** — updateUserStatus, updateUserRole
6. **骑手位置上报** — updateLocation
7. **任务大厅抢单** — getAvailableTasks, grabOrder
8. **支付查询/退款** — queryPaymentStatus, getPaymentList, applyRefund, closePayment
9. **热门/附近搜索** — getHotMerchants, getNearbyMerchants, searchDishes
10. **通知 WebSocket 推送** — /ws/notification

> 这些接口在后端可能已实现，但前端页面暂未接入。后续可逐步完善。
