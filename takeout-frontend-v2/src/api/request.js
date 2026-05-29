import axios from 'axios'

const baseURL = import.meta.env.VITE_API_BASE_URL || '/api'

const request = axios.create({
  baseURL,
  timeout: 15000,
  headers: {
    'Content-Type': 'application/json'
  }
})

let isRefreshing = false
let refreshQueue = []

function processQueue(token) {
  refreshQueue.forEach(cb => cb(token))
  refreshQueue = []
}

// Request Interceptor
request.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = 'Bearer ' + token
    }
    return config
  },
  error => Promise.reject(error)
)

// Response Interceptor
request.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code !== 200) {
      const error = new Error(res.message || '请求失败')
      error.code = res.code
      return Promise.reject(error)
    }
    return res
  },
  error => {
    const status = error.response?.status
    const message = error.response?.data?.message || error.message

    if (status === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      window.location.href = '/auth/login'
      return new Promise(() => {})
    }

    // Auto-refresh token on 403
    if (status === 403 && !error.config._retried) {
      if (isRefreshing) {
        return new Promise(resolve => {
          refreshQueue.push(newToken => {
            error.config.headers['Authorization'] = 'Bearer ' + newToken
            error.config._retried = true
            resolve(request(error.config))
          })
        })
      }

      isRefreshing = true
      error.config._retried = true

      return new Promise((resolve, reject) => {
        const token = localStorage.getItem('token')
        axios.post(`${baseURL}/auth/refresh`, null, {
          headers: { 'Authorization': 'Bearer ' + token },
          timeout: 5000
        }).then(refreshRes => {
          const data = refreshRes.data
          if (data.code === 200 && data.data?.token) {
            const newToken = data.data.token
            const newUserInfo = data.data.userInfo
            localStorage.setItem('token', newToken)
            if (newUserInfo) {
              localStorage.setItem('userInfo', JSON.stringify(newUserInfo))
            }
            processQueue(newToken)
            error.config.headers['Authorization'] = 'Bearer ' + newToken
            resolve(request(error.config))
          } else {
            processQueue(token)
            reject(error)
          }
        }).catch(() => {
          processQueue(token)
          reject(error)
        }).finally(() => {
          isRefreshing = false
        })
      })
    }

    // Network error
    if (!error.response) {
      error.message = '网络连接失败，请检查网络'
    }

    return Promise.reject(error)
  }
)

export default request
