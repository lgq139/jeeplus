import {get, post} from "@/api/tool";

// 字典事项类型数据
export function getStateData(params) {
  return get('/api/getDictdDirectories', params)
}
