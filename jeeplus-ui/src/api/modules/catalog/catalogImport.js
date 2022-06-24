import {get, post} from "@/api/tool";

// 导入列表数据
export function getCatalogListData(params) {
  return get('/convience/catalog/file/list', params)
}
// 导入列表查看数据
export function lookCatalogListData(params) {
  return get('/convience/catalog/file/queryById', params)
}
