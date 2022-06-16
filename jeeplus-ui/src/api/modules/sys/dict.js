import {post, get, deleted} from '../../tool'

//-------------------------------------------
// 字典类型接口
//-------------------------------------------

export function listAllDictType() {
  return get('/sys/dict/type/allList')
}

export function listDictType(params) {
  return get('/sys/dict/type/list', params)
}

/**
 * 保存字典类型
 * @param params
 * @return {AxiosPromise}
 */
export function saveDictType(params) {
  return post('/sys/dict/save', params)
}

export function deleteDictType(params) {
  return post('/sys/dict/delete', params)
}

/**
 * 刷新缓存
 * @returns {AxiosPromise}
 */
export function refreshCache() {
  return post('/sys/dict/refresh')
}

//-------------------------------------------
// 列表型字典值接口
//-------------------------------------------

export function listDictValue(params) {
  return get('/sys/dict/getDictValue', params)
}

export function saveDictValue(params) {
  return post('/sys/dict/saveDictValue', params)
}

export function deleteDictValue(params) {
  return post('/sys/dict/deleteDictValue', params)
}

//-------------------------------------------
// 树形字典值接口
//-------------------------------------------

export function listDictTreeValue(params) {
  return get('/sys/dict/getDictTreeValue', params)
}

export function saveDictTreeValue(params) {
  return post('/sys/dict/saveDictTreeValue', params)
}

export function deleteDictTreeValue(params) {
  return post('/sys/dict/deleteDictTreeValue', params)
}


//-------------------------------------------
// 国民经济行业分类数据接口
//-------------------------------------------

export function listIndustry() {
  return get('/sys/dict/industry/list')
}

// 刷新行业分类缓存数据
export function refreshIndustryCache() {
  return post('/sys/dict/industry/refresh')
}

//-------------------------------------------
// 产业结构调整指导目录
//-------------------------------------------

export function listIndustryStructure() {
  return get('/sys/dict/industryStructure/list')
}

// 刷新产业结构调整指导目录缓存数据
export function refreshIndustryStructureCache() {
  return post('/sys/dict/industryStructure/refresh')
}
