import {post, get} from "@/api/tool";

/**
 * 登录
 *
 * @param params
 */
export function login(params) {
  return post('/sys/login', params)
}

/**
 * 加载系统菜单
 *
 */
export function getSystemMenus() {
  return get('/sys/user/getMenus')
}

/**
 * 登出
 *
 * @param params
 * @return {AxiosPromise}
 */
export function logout(params) {
  return get('/sys/logout')
}
