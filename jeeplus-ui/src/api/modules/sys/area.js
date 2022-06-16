import { post, get } from '../../tool'

// 获取全量区划数据
export function listRegionTreeData(params) {
  return get('/sys/area/areaList', params)
}
// 获取省市数据
export function listRegionCities() {
  return get('/sys/area/cities')
}
// 获取县区数据
export function listRegionCounties(params) {
  return get('/sys/area/counties', params)
}

//******************** 获取授权

// 获取授权可建立场景的区划数据
export function ListRegionAuthCatalog(params) {
  return get('/sys/areaAuth/listRegionNode', params)
}

export function listAreaAuth(params) {
  return get('/sys/areaAuth/query', params)
}

export function saveAreaAuth(params) {
  return post('/sys/areaAuth/save', params)
}

export function deleteAreaAuth(params) {
  return post('/sys/areaAuth/delete', params)
}

export function refreshAreaAuth() {
  return post('/sys/areaAuth/refresh')
}
