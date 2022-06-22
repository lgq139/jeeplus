import {get, post} from "@/api/tool";

export function getCatalogListData(params) {
  return get('/convience/catalog/file/list', params)
}
