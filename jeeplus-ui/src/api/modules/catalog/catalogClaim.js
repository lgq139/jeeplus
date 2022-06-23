import {get, post} from "@/api/tool";

export function getCatalogClaimData(params) {
  return get('/convience/catalog/maintain/claimList', params)
}
// 目录认领
export function catalogClaimData(params) {
  return post('/convience/catalog/maintain/cataClaim', params)
}

