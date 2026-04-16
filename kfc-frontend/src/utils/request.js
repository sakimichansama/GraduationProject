import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// 响应拦截器：兼容统一包装格式与直接返回数据
request.interceptors.response.use(
  response => {
    const res = response.data
    // 如果响应数据包含 'code' 字段，则按统一格式处理
    if (res && typeof res === 'object' && 'code' in res) {
      if (res.code !== 200) {
        ElMessage.error(res.msg || '请求失败')
        return Promise.reject(new Error(res.msg || 'Error'))
      }
      return res.data
    }
    // 否则直接返回原始数据（例如数组、对象）
    return res
  },
  error => {
    const msg = error.response?.data?.msg || error.message || '网络错误'
    ElMessage.error(msg)
    return Promise.reject(error)
  }
)

export default request