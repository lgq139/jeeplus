/**
 * 全站路由配置
 * 代码中路由统一使用path属性跳转
 */
import Vue from 'vue'
import Router from 'vue-router'
import {isURL} from '@/utils/validate'
import {clearLoginInfo, redirectSsoLoginPage} from '@/utils'
import config from '@/config'
import {getSystemMenus, login} from "@/api";

Vue.use(Router)

const routerPush = Router.prototype.push
Router.prototype.push = function push (location) {
  return routerPush.call(this, location).catch(error => error)
}
// 开发环境不使用懒加载
const _import = require('./import-' + process.env.NODE_ENV)
// 全局路由
const globalRoutes = [
  {
    path: '/login',
    component: _import('modules/sys/login/login'),
    name: 'login',
    meta: {title: '登录'}}
]

// 主入口路由
const mainRoutes = {
  path: '/',
  component: _import('main'),
  name: 'main',
  redirect: {name: 'home'},
  meta: {title: '整体布局'},
  children: [
    {
      path: '/home',
      // component: _import('modules/sys/dashboard/analysis/index'),
      component: _import('modules/home/index'),
      name: 'home',
      meta: {title: '首页', backgroundType: '2'}
    },
    {
      path: '/404',
      component: _import('common/404'),
      name: '404',
      meta: {title: '404未找到'}
    }
  ],

  // 路由拦截
  beforeEnter (to, from, next) {
    let token = Vue.cookie.get(config.tokenName)
    if (!token || !/\S/.test(token)) {
      clearLoginInfo()
      next({
        path: '/login?app_redirect_url=' + to.fullPath
        // name: 'login'
      })
    }
    next()
  }
}

const router = new Router({
  mode: 'hash',
  // mode: 'history',
  scrollBehavior: () => ({y: 0}),
  isAddDynamicMenuRoutes: false, // 是否已经添加动态(菜单)路由
  routes: globalRoutes.concat(mainRoutes)
})

router.beforeEach((to, from, next) => {
  // 添加动态(菜单)路由
  if (router.options.isAddDynamicMenuRoutes || fnCurrentRouteType(to, globalRoutes) === 'global') {
    let token = Vue.cookie.get(config.tokenName)
    if (!token || !/\S/.test(token)) {
      if (to.query.code) {

        login({
          code: to.query.code,
          redirectUri: location.origin + '/' + (process.env.NODE_ENV !== 'production' ? '': 'baseconfig/') + '#/login'
        }).then(({data}) => {
          if (data && data.success) {
            // next()
            Vue.cookie.set(config.tokenName, data.token)
            Vue.cookie.set('refreshToken', data.refreshToken)
            next({name: 'home', replace: true})
          }
        })
      } else {
        next({name: 'home', replace: true})
      }
    } else {
      // next({name: 'home', replace: true})
      if (to.path === '/login') {
        next({name: 'home', replace: true})
      } else {
        next()
      }
    }
  } else {
    let token = Vue.cookie.get(config.tokenName)
    if (!token || !/\S/.test(token)) {
      redirectSsoLoginPage()
    } else {
      if (!router.options.isAddDynamicMenuRoutes) {
        getSystemMenus().then(({data}) => {
          if (data && data.success) {
            //
            fnAddDynamicMenuRoutes(data.routerList)
            //
            router.options.isAddDynamicMenuRoutes = true
            sessionStorage.setItem('allMenuList', JSON.stringify(data.menuList || '[]'))
            sessionStorage.setItem('permissions', JSON.stringify(data.permissions || '[]'))
            sessionStorage.setItem('dictList', JSON.stringify(data.dictList || '[]'))
            next({...to, replace: true})
          } else {
            sessionStorage.setItem('allMenuList', '[]')
            sessionStorage.setItem('permissions', '[]')
            sessionStorage.setItem('dictList', '[]')
            next()
          }
        }).catch((e) => {
          console.log(`%c${e} 请求菜单列表和权限失败，跳转至登录页！！`, 'color:blue')
        })
      }
    }

  }
})

/**
 * 判断当前路由类型, global: 全局路由, main: 主入口路由
 * @param {*} route 当前路由
 */
function fnCurrentRouteType (route, globalRoutes = []) {
  let temp = []
  for (let i = 0; i < globalRoutes.length; i++) {
    if (route.path === globalRoutes[i].path) {
      return 'global'
    } else if (globalRoutes[i].children && globalRoutes[i].children.length >= 1) {
      temp = temp.concat(globalRoutes[i].children)
    }
  }
  return temp.length >= 1 ? fnCurrentRouteType(route, temp) : 'main'
}

/**
 * 添加动态(菜单)路由
 * @param {*} menuList 菜单列表
 * @param {*} routes 递归创建的动态(菜单)路由
 */
function fnAddDynamicMenuRoutes (menuList = [], routes = []) {
  let temp = []
  for (let i = 0; i < menuList.length; i++) {
    if (menuList[i].children && menuList[i].children.length >= 1) {
      temp = temp.concat(menuList[i].children)
    }

    if (menuList[i].href && /\S/.test(menuList[i].href)) {
      menuList[i].href = menuList[i].href.replace(/[/]$/, '')
      const route = {
        path: menuList[i].href.split('?')[0],
        component: null,
        name: menuList[i].href.replace(/^\//g, '').replace(/[/]/g, '-').replace(/[?]/g, '-').replace(/&/g, '-').replace(/=/g, '-'),
        meta: {
          parentIds: menuList[i].parentIds,
          menuId: menuList[i].id,
          title: menuList[i].name,
          isDynamic: true,
          type: menuList[i].target,
          backgroundType: menuList[i].backgroundType,
          iframeUrl: ''
        }
      }
      // url以http[s]://开头, 通过iframe展示
      if (isURL(menuList[i].href)) {
        route.path = '/' + route.path
        route.meta.iframeUrl = menuList[i].href
        route['meta']['type'] = 'iframe'
      } else if (menuList[i].target === 'iframe') {
        route['meta']['iframeUrl'] = `${process.env.VUE_APP_SERVER_URL}${menuList[i].href}`
      } else {
        try {
          route['component'] = _import(`modules${menuList[i].href.split('?')[0]}`) || null
        } catch (e) {
          console.log(e)
        }
      }
      routes.push(route)
    }
  }
  if (temp.length >= 1) {
    fnAddDynamicMenuRoutes(temp, routes)
  } else {
    mainRoutes.name = 'main-dynamic'
    mainRoutes.children = routes
    router.addRoutes([
      mainRoutes,
      {path: '*', redirect: {name: '404'}}
    ])
    sessionStorage.setItem('dynamicMenuRoutes', JSON.stringify(mainRoutes.children || []))
  }
}

export default router
