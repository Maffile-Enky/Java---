import { defineStore } from 'pinia'
import { ref, computed, watch } from 'vue'

export const useCartStore = defineStore('cart', () => {
  const items = ref(JSON.parse(localStorage.getItem('cartItems') || '[]'))
  const merchantId = ref(localStorage.getItem('cartMerchantId') || null)
  const merchantName = ref(localStorage.getItem('cartMerchantName') || '')

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

  function addItem(dish, mId, mName) {
    // If adding from a different merchant, confirm clearing
    if (merchantId.value && merchantId.value !== mId && items.value.length > 0) {
      if (!confirm('购物车中已有其他商家的商品，是否清空后添加？')) {
        return false
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
    addItem, removeItem, updateQuantity, clearCart, getQuantity, saveToStorage
  }
})
