import {get, post} from "@/api/tool";

export function getCatalogListData(params) {
  return get('/convience/catalog/inventory/list', params)
}
export function lookCatalogListData(params) {
  return get('/convience/catalog/inventory/getDetil', params)
}
