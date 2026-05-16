import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useCartStore = defineStore('cart', () => {
  const items = ref(JSON.parse(localStorage.getItem('cartItems') || '[]'))
  const merchantId = ref(localStorage.getItem('cartMerchantId') || null)
  const merchantName = ref(localStorage.getItem('cartMerchantName') || '')

  // Callback for merchant conflict — views set this to show a GlassModal
  let onMerchantConflict = null

  const totalCount = computed(() =>
    items.value.reduce((sum, item) => sum + item.quantity, 0)
  )

  const totalPrice = computed(() =>
    items.value.reduce((sum, item) => sum + item.price * item.quantity, 0)
  )

  const isEmpty = computed(() => items.value.length === 0)

  function saveToStorage() {
    localStorage.setItem('cartItems', JSON.stringify(items.value))
    localStorage.setItem('cartMerchantId', merchantId.value || '')
    localStorage.setItem('cartMerchantName', merchantName.value || '')
  }

  function setMerchantConflictHandler(handler) {
    onMerchantConflict = handler
  }

  async function addItem(dish, mId, mName) {
    // If adding from a different merchant, handle conflict
    if (merchantId.value && merchantId.value !== mId && items.value.length > 0) {
      if (onMerchantConflict) {
        const confirmed = await onMerchantConflict(mName)
        if (!confirmed) return false
      } else {
        // Fallback to confirm for non-Vue contexts
        if (!confirm('购物车中已有其他商家的商品，是否清空后添加？')) {
          return false
        }
      }
      clearCart()
    }

    merchantId.value = mId
    merchantName.value = mName

    const existing = items.value.find(item => item.dishId === dish.id)
    if (existing) {
      existing.quantity++
    } else {
      items.value.push({
        dishId: dish.id,
        dishName: dish.name,
        price: dish.price,
        quantity: 1,
        description: dish.description || ''
      })
    }
    saveToStorage()
    return true
  }

  function removeItem(dishId) {
    items.value = items.value.filter(item => item.dishId !== dishId)
    if (items.value.length === 0) {
      merchantId.value = null
      merchantName.value = ''
    }
    saveToStorage()
  }

  function updateQuantity(dishId, quantity) {
    if (quantity <= 0) {
      removeItem(dishId)
      return
    }
    const item = items.value.find(item => item.dishId === dishId)
    if (item) {
      item.quantity = quantity
    }
    saveToStorage()
  }

  function clearCart() {
    items.value = []
    merchantId.value = null
    merchantName.value = ''
    saveToStorage()
  }

  function getQuantity(dishId) {
    const item = items.value.find(item => item.dishId === dishId)
    return item ? item.quantity : 0
  }

  return {
    items, merchantId, merchantName,
    totalCount, totalPrice, isEmpty,
    addItem, removeItem, updateQuantity, clearCart, getQuantity, saveToStorage,
    setMerchantConflictHandler
  }
})
