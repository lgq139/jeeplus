import {get, post} from "@/api/tool";

export function getMenuById(params) {
  return get('/sys/menu/queryById', params)
}

export function getTreeData(params) {
  return get('/sys/menu/treeData', params)
}

export function getTreeData2(params) {
  return get('/sys/menu/treeData2', params)
}

export function saveMenu(params) {
  return post('/sys/menu/save', params)
}

export function deleteMenu(params) {
  return post('/sys/menu/delete', params)
}
