import { post, get } from '../../tool'

/**
 * 获取区域部门列表
 *
 * @param params
 * @return {AxiosPromise}
 */
export function listOfficeData(params) {
  return get('/sys/office/bspData', params)
}
