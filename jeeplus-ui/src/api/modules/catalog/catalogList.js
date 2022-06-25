import {get, post} from "@/api/tool";

export function getCatalogListData(params) {
  return get('/convience/catalog/inventory/list', params)
}
export function lookCatalogListData(params) {
  return get('/convience/catalog/inventory/getDetil', params)
}

export function getImportCatalogListData(params) {
  return get('/convience/catalog/import/list', params)
}

export function deleteImportCatalog(params) {
  return post('/convience/catalog/import/delete', params)
}

export function saveImportCatalog(params) {
  return post('/convience/catalog/import/saveImport', params)
}
