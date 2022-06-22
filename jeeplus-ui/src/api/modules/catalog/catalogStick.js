import {get, post} from "@/api/tool";

export function getCatalogStickData(params) {
  return get('/convience/catalog/maintain/list', params)
}
// 新增目录
export function addCatalogStickData(params) {
  return get('/convience/catalog/maintain/save', params)
}
// 目录维护 启用停用状态改变  目录变更
export function editCatalogStickData(params) {
  return post('/convience/catalog/maintain/edit', params)
}
// 目录查看
export function lookCatalogStickData(params) {
  return get('/convience/catalog/maintain/getDetil', params)
}
