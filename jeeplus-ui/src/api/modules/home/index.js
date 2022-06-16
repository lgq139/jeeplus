import {get, post} from '../../tool'

// 统计热点场景项目申报量
export function getHotspotProjectCount(params) {
  return get('/baseconfig/analyze/hotspotProject', params)
}
// 统计事项与场景式的定义情况
export function getBasicInfoCount(params) {
  return get('/baseconfig/analyze/basicInfo', params)
}
// 统计年度事项申报情况
export function getItemCountByMonth(params) {
  return get('/baseconfig/analyze/itemCountByMonth', params)
}
