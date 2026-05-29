import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { getRiderInfo, updateRiderStatus } from '@/api/delivery'

export const useRiderStore = defineStore('rider', () => {
  const rider = ref(null)

  const isRider = computed(() => !!rider.value)
  const isOnline = computed(() => rider.value?.status === 'ONLINE')
  const isBusy = computed(() => rider.value?.status === 'BUSY')

  async function loadRiderInfo() {
    try {
      const res = await getRiderInfo()
      rider.value = res.data || null
      return rider.value
    } catch {
      rider.value = null
      return null
    }
  }

  async function toggleOnline() {
    if (!rider.value) return
    const newStatus = isOnline.value ? 'OFFLINE' : 'ONLINE'
    const res = await updateRiderStatus(newStatus)
    rider.value = res.data || { ...rider.value, status: newStatus }
  }

  function clearRider() {
    rider.value = null
  }

  return {
    rider, isRider, isOnline, isBusy,
    loadRiderInfo, toggleOnline, clearRider
  }
})
