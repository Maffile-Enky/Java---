import { ref } from 'vue'

const toastState = ref({
  message: '',
  type: 'info',
  duration: 3000
})

export function useToast() {
  function show(message, type = 'info', duration = 3000) {
    toastState.value = { message: '', type, duration }
    setTimeout(() => {
      toastState.value = { message, type, duration }
    }, 10)
  }

  function success(message, duration) {
    show(message, 'success', duration)
  }

  function error(message, duration) {
    show(message, 'error', duration)
  }

  function warning(message, duration) {
    show(message, 'warning', duration)
  }

  function info(message, duration) {
    show(message, 'info', duration)
  }

  return {
    toastState,
    show,
    success,
    error,
    warning,
    info
  }
}
