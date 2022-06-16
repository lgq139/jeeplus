import {post, get, jsonPost} from '../../tool'

//-------------------------------------------
// 事项目录配置接口
//-------------------------------------------

export function listItemBasicCode(params) {
  return get('/baseconfig/item/itemBasicCode/list', params)
}

export function saveItemBasicCode(params) {
  return post('/baseconfig/item/itemBasicCode/save', params)
}

export function deleteItemBasicCode(params) {
  return post('/baseconfig/item/itemBasicCode/delete', params)
}
// 列出事项目录及标准事项
export function listOfStandardItem(params) {
  return get('/baseconfig/item/itemBasicCode/listOfStandardItem', params)
}
// 字典化列出目录列表
export function dictItemBasicCode(params) {
  return get('/baseconfig/item/itemBasicCode/dict', params)
}

/**
 * 以类似树的方式列出事项目录及目录材料
 *
 * @param params
 * @returns {AxiosPromise}
 */
export function listBasicCodeAndBasicMaterial(params) {
  return get('/baseconfig/item/itemBasicCode/listOfMaterial')
}

//-------------------------------------------
// 事项信息配置接口
//-------------------------------------------

export function listItemInfo(params) {
  return get('/baseconfig/item/itemInfo/list', params)
}
// 获取事项扩展信息
export function getItemInfoExtend(params) {
  return get('/baseconfig/item/itemInfo/getExtend', params)
}

//以不分页的形式加载（包含事项材料信息）
export function listAllItemInfo(params) {
  return get('/baseconfig/item/itemInfo/allList', params)
}

export function addItemByRegion(params) {
  return post('/baseconfig/item/itemInfo/addByRegion', params)
}

export function listItemMaterial(params) {
  return get('/baseconfig/item/itemInfo/materialList', params)
}

export function getItemContent(params) {
  return get('/baseconfig/item/itemInfo/itemContent', params)
}

//绑定
export function saveItemInfo(params) {
  return post('/baseconfig/item/itemInfo/saveExtend', params)
}

//-------------------------------------------
// 基本标准事项
//-------------------------------------------
// 获取标准事项列表
export function listItemBasicStandardList(params) {
  return get('/baseconfig/item/basicstandard/list', params)
}
// 保存更新标准事项信息
export function saveItemBasicStandard(params) {
  return post('/baseconfig/item/basicstandard/save', params)
}
// 删除标准事项
export function deleteItemBasicStandardList(params) {
  return post('/baseconfig/item/basicstandard/delete', params)
}
// 标准事项材料关联列表
export function listStandardMaterialAssociate(params) {
  return get('/baseconfig/item/basicstandard/material/list', params)
}
// 标准事项材料关联配置
export function updateAssociateMaterial(bizItemCode, data) {
  return jsonPost(`/baseconfig/item/basicstandard/material/associate/${bizItemCode}`, data)
}
// 标准事项与事项关联列表
export function listStandardItemAssociate(params) {
  return get('/baseconfig/item/basicstandard/itemInfo/list', params)
}
// 标准事项与事项关联配置
export function updateAssociateItem(params) {
  return post('/baseconfig/item/basicstandard/itemInfo/associate', params)
}
// 获取已绑定到标准事项的事项列表
export function listStandardItemInfo(params) {
  return get('/baseconfig/item/basicstandard/itemMaterial/list', params)
}

// 标准事项材料与事项材料关联配置
export function updateAssociateItemMaterial(params) {
  return post('/baseconfig/item/basicstandard/itemMaterial/associate', params)
}
