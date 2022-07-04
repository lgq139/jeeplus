import request from '@/utils/httpRequest'

export function listJTheme(query) {
  return request({
    url: '/sys/jTheme/list',
    method: 'get',
    params: query
  })
}

// export function getJTheme(params) {
//   return get('/sys/jTheme/getById', params)
// }
//
// export function listJTheme(params) {
//   return get('/sys/jTheme/list', params)
// }
//
//
// export function saveJTheme(params) {
//   return post('/sys/jTheme/save', params)
// }
//
//
// export function deleteJTheme(params) {
//   return post('/sys/jTheme/delete', params)
// }
//
// export function updateJTheme(params) {
//   return post('/sys/jTheme/update', params)
// }

