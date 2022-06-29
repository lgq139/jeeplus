import {get, post} from "@/api/tool";
// 政务目录导入
export function importGovernmentCatalogData(params) {
  return get('/approve/catalog/import/list', params)
}
// 政务目录导入保存
export function saveGovernmentCatalogData(params) {
  return get('/approve/catalog/import/saveImport', params)
}
// 政务目录导入删除
export function deleteGovernmentCatalogData(params) {
  return post('/approve/catalog/import/delete', params)
}
