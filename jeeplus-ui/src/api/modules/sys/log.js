import {get, post} from '../../tool'

export function listLog(params) {
  return get('/sys/log/list', params)
}

export function listLogDataMine(params) {
  return get('/sys/log/data/mine', params)
}
