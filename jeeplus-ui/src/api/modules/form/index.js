import { get } from '../../tool'

/**
 *
 * @param params
 *  appCode
 *  orgCode
 *  Page
 *  Size
 *  formName
 *  isOnlyReleased:是否只返回已发布的表单
 * @return {AxiosPromise}
 */
export function getFormListPaginate(params = {}) {
  return get('/baseconfig/form/getFormListPaginate',params)
}
