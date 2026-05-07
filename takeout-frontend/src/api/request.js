import axios from 'axios'

const request = axios.create({
  baseURL: '/api',
  timeout: 5000
})

// Request Interceptor
request.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = 'Bearer ' + token
    }
    console.log('[API Request]', config.method?.toUpperCase(), config.url)
    return config
  },
  error => {
    console.error('[API Request Error]', error)
    return Promise.reject(error)
  }
)

// Response Interceptor
request.interceptors.response.use(
  response => {
    const res = response.data
    console.log('[API Response]', response.config.url, 'code:', res.code)
    if (res.code !== 200) {
      return Promise.reject(new Error(res.message || 'Error'))
    }
    return res
  },
  error => {
    const status = error.response?.status
    const url = error.config?.url
    console.error('[API Error]', url, 'status:', status, 'message:', error.message)
    if (status === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      window.location.href = '/auth/login'
      return new Promise(() => {}) // 永挂，阻止后续代码执行
    }
    return Promise.reject(error)
  }
)

export default request
