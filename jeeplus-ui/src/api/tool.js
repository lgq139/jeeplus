import request from '@/utils/httpRequest'

export function get(url, params = {}) {
  return request({
    url,
    method: 'get',
    params
  })
}

export function post(url, data = {}) {
  return request({
    url,
    method: 'post',
    data
  })
}

export function put(url, data = {}) {
  return request({
    url,
    method: 'put',
    data
  })
}


export function deleted(url, params = {}) {
  return request({
    url,
    method: 'delete',
    params
  })
}

export function jsonPost(url, data = {}) {
  return request({
    url,
    method: 'post',
    headers: {
      'Content-Type': 'application/json;charset=utf-8'
    },
    data
  })
}

export function get_download(url) {
  return request({
    url,
    method: 'get',
    responseType: 'blob'
  })
}

export function post_upload(url, data) {
  return request({
    url,
    method: 'post',
    headers: {
      'Content-Type': 'multipart/form-data',
    },
    data
  })
}
