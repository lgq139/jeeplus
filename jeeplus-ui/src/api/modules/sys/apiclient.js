import {post, get, deleted} from '../../tool'

export function listApiClient(params) {
  return get('/sys/apiClient/list', params)
}

export function saveApiClient(params) {
  return post('/sys/apiClient/save', params)
}

export function deleteApiClient(params) {
  return post('/sys/apiClient/delete', params)
}

export function refreshApiClient() {
  return post('/sys/apiClient/refresh')
}
