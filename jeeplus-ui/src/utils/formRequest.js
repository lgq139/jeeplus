import Vue from 'vue'
import axios from 'axios'
import merge from 'lodash/merge'
import qs from 'qs'
import {
  Message,
  Loading
} from 'element-ui'
import config from '@/config'

// 非生产环境 && 开启代理, 接口前缀统一使用[/api]前缀做代理拦截!
// const BASE_URL = process.env.NODE_ENV !== 'production' ? process.env.VUE_APP_BASE_API : process.env.VUE_APP_SERVER_URL
const BASE_URL = config.form_system_uri

/**
 * @description axios初始化
 */
const instance = axios.create({
  baseURL: BASE_URL,
  timeout: 100000,
  withCredentials: true,
  headers: {
    'Content-Type': 'application/x-www-form-urlencoded; charset=utf-8',
  },
})

/**
 * 请求拦截
 */
let loading
instance.interceptors.request.use(config => {
  let showLoading = false
  if (config.loading === true) {
    showLoading = true
  }
  if (showLoading) {
    loading = Loading.service({
      text: config.loadingText || 'Loading...',
      spinner: 'el-icon-loading',
      background: 'rgba(0, 0, 0, 0.7)'
    })
  }
  // 请求地址处理
  config.url = BASE_URL + config.url
  const type = config.method
  const defaults = {}
  const arrayFormat = config.headers.arrayFormat || 'indices'
  if (type === 'post' && config.headers['Content-Type'] === 'application/x-www-form-urlencoded; charset=utf-8') {
    // post请求参数处理
    config.data = qs.stringify(config.data, { allowDots: true, arrayFormat: arrayFormat })
  } else if (type === 'get') {
    // get请求参数处理
    config.params = qs.stringify(config.params, { allowDots: true, arrayFormat: arrayFormat })
    config.params = qs.parse(config.params)
    config.params = merge(defaults, config.params)
  }
  return config
}, error => {
  return Promise.reject(error)
})

/**
 * 响应拦截
 */
instance.interceptors.response.use(response => {
  debugger
  if (loading) {
    loading.close()
  }
  if (response.data && response.data.success === false) {
    Message({
      message: response.data.msg,
      type: 'error',
      showClose: true,
      dangerouslyUseHTMLString: true,
      duration: 3000,
      customClass: 'zZindex'
    })
  }
  return response
}, error => {
  if (loading) {
    loading.close()
  }
  if (error.response.status === 401) { // 超时自动刷新
    //
  } else if (error.response.status === 402 || error.response.status === 403) { // 402 未登录或者refresh token过时， 403 账号在其他地方登录
    //
  } else if (error.response.status === 404) { // 路径找不到
    Message({
      message: '404，路径找不到' + ':' + error.response.data.path,
      type: 'error',
      showClose: true,
      duration: 3000
    })
  } else if (error.response.status === 504) {
    Message({
      message: '网络连接错误' + ':' + error.response.data,
      type: 'error',
      showClose: true,
      duration: 3000,
      customClass: 'zZindex'
    })
  } else {
    Message({
      message: error.response.data.msg || error.response.data.exception || error.response.data || error.response || error,
      type: 'error',
      showClose: true,
      duration: 5000,
      customClass: 'zZindex'
    })
  }

  return Promise.reject(error)
})

export default instance
