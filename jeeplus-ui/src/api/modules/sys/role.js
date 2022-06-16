import {get, post} from '../../tool'

export function listRole(params) {
  return get('/sys/role/list', params)
}

export function pushRole() {
  return post('/sys/role/pushRole')
}
