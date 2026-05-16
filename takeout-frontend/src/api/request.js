import axios from 'axios'

const request = axios.create({
  baseURL: 'http://47.99.34.251:9999',
  timeout: 5000
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
      return new Promise(() => {})
    }

    // Auto-refresh token on 403 (role may have changed in DB but JWT is stale)
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
        axios.post('http://47.99.34.251:9999/auth/refresh', null, {
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

    return Promise.reject(error)
  }
)

export default request
