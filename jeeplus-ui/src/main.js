import Vue from 'vue'
import App from '@/App'
import router from '@/router'
import store from '@/store'
import VueCookie from 'vue-cookie'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import '@/assets/scss/index.scss'
import httpRequest from '@/utils/httpRequest'
import dictUtils from '@/utils/dictUtils'
import utils from '@/utils'
import '@/utils/filter'
import validator from '@/utils/validator'
import cloneDeep from 'lodash/cloneDeep'
import lodash from 'lodash/object'
import axios from 'axios'
import moment from 'moment'
import 'font-awesome/css/font-awesome.min.css'
import VueClipboard from 'vue-clipboard2'
import VCharts from 'v-charts'
import './directive'
import VueEditor from 'vue2-editor'
import 'xe-utils'
import VXETable from 'vxe-table'
import 'vxe-table/lib/style.css'


Vue.use(VueEditor)

VueClipboard.config.autoSetContainer = true
Vue.use(VCharts)
Vue.use(VueClipboard)
Vue.use(VueCookie)
Vue.use(ElementUI)
Vue.use(VXETable)

Vue.config.productionTip = false
// 挂载全局
Vue.prototype.$http = httpRequest // ajax请求方法
Vue.prototype.hasPermission = utils.hasPermission // 权限方法
Vue.prototype.treeDataTranslate = utils.treeDataTranslate // 树形数据转换
Vue.prototype.$utils = utils
Vue.prototype.$window = window
Vue.prototype.$dictUtils = dictUtils
Vue.prototype.recover = utils.recover
Vue.prototype.recoverNotNull = utils.recoverNotNull
Vue.prototype.$axios = axios
Vue.prototype.validator = validator
Vue.prototype.lodash = lodash
Vue.prototype.moment = moment
Vue.prototype.deepClone = utils.deepClone
Vue.prototype.validatenull = utils.validatenull
Vue.prototype.$events = new Vue()

// 保存整站vuex本地储存初始状态
window.SITE_CONFIG = {}
window.SITE_CONFIG['storeState'] = cloneDeep(store.state)

/* eslint-disable no-new */
new Vue({
  el: '#app',
  router,
  store,
  render: h => h(App)
})
