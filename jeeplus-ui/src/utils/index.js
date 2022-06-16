import Vue from 'vue'
import router from '@/router'
import store from '@/store'
import $http from './httpRequest'
import config from '@/config'
import {Loading} from "element-ui";

/**
 * 是否有权限
 * @param {*} key
 */
export function hasPermission (key) {
  return JSON.parse(sessionStorage.getItem('permissions') || '[]').indexOf(key) !== -1 || false
}
/**
 * 树形数据转换
 * @param {*} data list数据
 * @param {*} id 主键ID
 * @param {*} pid 上级ID
 * @param childrenKey 子list数据的key
 */
export function treeDataTranslate (data, id = 'id', pid = 'parentId', childrenKey = 'children') {
  let res = []
  let temp = {}
  for (let i = 0; i < data.length; i++) {
    temp[data[i][id]] = data[i]
  }
  for (let k = 0; k < data.length; k++) {
    if (temp[data[k][pid]] && data[k][id] !== data[k][pid]) {
      if (!temp[data[k][pid]][childrenKey]) {
        temp[data[k][pid]][childrenKey] = []
      }
      if (!temp[data[k][pid]]['_level']) {
        temp[data[k][pid]]['_level'] = 1
      }
      data[k]['_level'] = temp[data[k][pid]]._level + 1
      temp[data[k][pid]][childrenKey].push(data[k])
    } else {
      res.push(data[k])
    }
  }
  return res
}

/**
 * 构造树型结构数据
 * @param {*} data 数据源
 * @param {*} id id字段 默认 'id'
 * @param {*} parentId 父节点字段 默认 'parentId'
 * @param {*} children 孩子节点字段 默认 'children'
 */
export function handleTree(data, id, parentId, children) {
  let config = {
    id: id || 'id',
    parentId: parentId || 'parentId',
    childrenList: children || 'children'
  };

  var childrenListMap = {};
  var nodeIds = {};
  var tree = [];

  for (let d of data) {
    let parentId = d[config.parentId];
    if (childrenListMap[parentId] == null) {
      childrenListMap[parentId] = [];
    }
    nodeIds[d[config.id]] = d;
    childrenListMap[parentId].push(d);
  }

  for (let d of data) {
    let parentId = d[config.parentId];
    if (nodeIds[parentId] == null) {
      tree.push(d);
    }
  }

  for (let t of tree) {
    adaptToChildrenList(t);
  }

  function adaptToChildrenList(o) {
    if (childrenListMap[o[config.id]] !== null) {
      o[config.childrenList] = childrenListMap[o[config.id]];
    }
    if (o[config.childrenList]) {
      for (let c of o[config.childrenList]) {
        adaptToChildrenList(c);
      }
    }
  }
  return tree;
}
/**
 * 清除登录信息
 */
export function clearLoginInfo () {
  Vue.cookie.delete(config.tokenName)
  store.commit('resetStore')
  router.options.isAddDynamicMenuRoutes = false
}

/**
 * 跳转至SSO统一身份认证登录
 */
export function redirectSsoLoginPage() {
  window.location.href = config.authorize_uri + '?client_id=' +  (process.env.NODE_ENV !== 'production' ? config.dev_client_id : config.prod_client_id)
    + '&response_type=code&scope=super&status=' + new Date().getTime().toString()
    + '&goto=' + location.origin + '/' + (process.env.NODE_ENV !== 'production' ? '': 'baseconfig/') + '#/login'
}

/**
 * 跳转至SSO统一身份认证登出
 */
export function redirectSsoLogoutPage() {
  window.location.href = config.access_logout_uri + '?client_id=' +  (process.env.NODE_ENV !== 'production' ? config.dev_client_id : config.prod_client_id)
    + '&goto=' + location.origin + '/' + (process.env.NODE_ENV !== 'production' ? '': 'baseconfig/') + '#/login'
}

/**
 * 表单对象赋值:
 * 对目标对象存在且源对象同样存在的属性，全部覆盖；
 * 目标对象不存在但是源对象存在的属性， 全部丢弃；
 * 目标对象存在但是源对象不存在的属性，如果是字符串赋值为空串，其余类型赋值为undefined
 */
export function recover (target, source) {
  if (target === undefined || target === null) { throw new TypeError('Cannot convert first argument to object') }
  var to = Object(target)
  if (source === undefined || source === null) { return to }
  var keysArray = Object.keys(Object(target))
  for (var nextIndex = 0, len = keysArray.length; nextIndex < len; nextIndex++) {
    var nextKey = keysArray[nextIndex]
    var desc = Object.getOwnPropertyDescriptor(target, nextKey)
    if (desc !== undefined && desc.enumerable) {
      if (to.hasOwnProperty(nextKey)) {
        if (to[nextKey] instanceof Array) {
          to[nextKey] = source[nextKey]
        } else if (to[nextKey] instanceof Object) {
          recover(to[nextKey], source[nextKey])
        } else if (source[nextKey] !== undefined) {
          to[nextKey] = source[nextKey]
        } else if (typeof (to[nextKey]) === 'string') {
          to[nextKey] = ''
        } else {
          to[nextKey] = undefined
        }
      }
    }
  }
  return to
}

/**
 * 表单对象赋值:
 * 对目标对象存在且源对象同样存在的属性，全部覆盖；
 * 目标对象不存在但是源对象存在的属性， 全部丢弃；
 * 目标对象存在但是源对象不存在的属性，保留目标对象的属性不做处理
 */
export function recoverNotNull (target, source) {
  if (target === undefined || target === null) { throw new TypeError('Cannot convert first argument to object') }
  var to = Object(target)
  if (source === undefined || source === null) { return to }
  var keysArray = Object.keys(Object(target))
  for (var nextIndex = 0, len = keysArray.length; nextIndex < len; nextIndex++) {
    var nextKey = keysArray[nextIndex]
    var desc = Object.getOwnPropertyDescriptor(target, nextKey)
    if (desc !== undefined && desc.enumerable) {
      if (to.hasOwnProperty(nextKey)) {
        if (to[nextKey] instanceof Array) {
          to[nextKey] = source[nextKey]
        } else if (to[nextKey] instanceof Object) {
          recover(to[nextKey], source[nextKey])
        } else if (source[nextKey] !== undefined) {
          to[nextKey] = source[nextKey]
        }
      }
    }
  }
  return to
}
export function download (url, params) {
  let loadingInstance = Loading.service({
    text: '数据正在导出......'
  });
  $http({
    method: 'get',
    url: url,
    params: params,
    responseType: 'blob'
  }).then(response => {
    // 以服务的方式调用的 Loading 需要异步关闭
    Vue.nextTick(() => {
      loadingInstance.close();
    });
    if (!response) {
      return
    }
    let link = document.createElement('a')
    link.href = window.URL.createObjectURL(new Blob([response.data]))
    link.target = '_blank'
    let filename = response.headers['content-disposition']
    link.download = decodeURI(filename)
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
  // eslint-disable-next-line handle-callback-err
  }).catch((error) => {
    Vue.nextTick(() => {
      loadingInstance.close();
    });
  })
}

export function escapeHTML (a) {
  a = '' + a
  return a.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;').replace(/'/g, '&apos;')
}
/**
* @function unescapeHTML 还原html脚本 < > & " '
* @param a -
*            字符串
*/
export function unescapeHTML (a) {
  a = '' + a
  return a.replace(/&lt;/g, '<').replace(/&gt;/g, '>').replace(/&amp;/g, '&').replace(/&quot;/g, '"').replace(/&apos;/g, "'")
}

export function printLogo () {
  console.info(
    '%c欢迎使用%cJEEPLUS',
    'color: #ffffff; background: #000000; padding:5px 10px 5px 10px;font-size:40px;border-radius:12px 0 0 12px;', 'color: #000000; background: #FE9A00; padding:5px 10px;font-size:40px;border-radius:0 12px 12px 0;')
}

/**
 * 对象深拷贝
 */
export function deepClone (data) {
  var type = getObjType(data)
  var obj
  if (type === 'array') {
    obj = []
  } else if (type === 'object') {
    obj = {}
  } else {
    // 不再具有下一层次
    return data
  }
  if (type === 'array') {
    for (var i = 0, len = data.length; i < len; i++) {
      data[i] = (function () {
        if (data[i] === 0) {
          return data[i]
        }
        return data[i]
      }())
      delete data[i].$parent
      obj.push(deepClone(data[i]))
    }
  } else if (type === 'object') {
    for (var key in data) {
      delete data.$parent
      obj[key] = deepClone(data[key])
    }
  }
  return obj
};

export function getObjType (obj) {
  var toString = Object.prototype.toString
  var map = {
    '[object Boolean]': 'boolean',
    '[object Number]': 'number',
    '[object String]': 'string',
    '[object Function]': 'function',
    '[object Array]': 'array',
    '[object Date]': 'date',
    '[object RegExp]': 'regExp',
    '[object Undefined]': 'undefined',
    '[object Null]': 'null',
    '[object Object]': 'object'
  }
  if (obj instanceof Element) {
    return 'element'
  }
  return map[toString.call(obj)]
};
export function validatenull (val) {
  // 特殊判断
  if (val && parseInt(val) === 0) return false
  var list = ['$parent']
  if (typeof val === 'boolean') {
    return false
  }
  if (typeof val === 'number') {
    return false
  }
  if (val instanceof Array) {
    if (val.length === 0) return true
  } else if (val instanceof Object) {
    val = (0, deepClone)(val)
    list.forEach(function (ele) {
      delete val[ele]
    })
    if (JSON.stringify(val) === '{}') return true
  } else {
    if (val === 'null' || val == null || val === 'undefined' || val === undefined || val === '') {
      return true
    }
    return false
  }
  return false
}
function handleImageAdded (file, Editor, cursorLocation, resetUploader) {
  // An example of using FormData
  // NOTE: Your key could be different such as:
  // formData.append('file', file)
  var formData = new FormData()
  formData.append('file', file)

  $http({
    url: '/sys/file/webupload/upload?uploadPath=/vueEditor',
    method: 'POST',
    data: formData,
    headers: { 'Content-Type': 'multipart/form-data' }
  })
    .then(result => {
      let url = result.data.url // Get url from response
      Editor.insertEmbed(cursorLocation, 'image', url)
      resetUploader()
    })
    .catch(err => {
      console.log(err)
    })
}
function hashCode (str) {
  var hash = 0
  if (str.length === 0) return hash
  for (let i = 0; i < str.length; i++) {
    let char = str.charCodeAt(i)
    hash = ((hash << 5) - hash) + char
    hash = hash & hash // Convert to 32bit integer
  }
  return hash
}

/**参数说明：
 * 根据长度截取先使用字符串，超长部分追加…
 * str 对象字符串
 * len 目标字节长度
 * 返回值： 处理结果字符串
 */
function cutString(str, len) {
  //length属性读出来的汉字长度为1
  if (str.length * 2 <= len) {
    return str;
  }
  var strlen = 0;
  var s = "";
  for (var i = 0; i < str.length; i++) {
    s = s + str.charAt(i);
    if (str.charCodeAt(i) > 128) {
      strlen = strlen + 2;
      if (strlen >= len) {
        return s.substring(0, s.length - 1) + "...";
      }
    } else {
      strlen = strlen + 1;
      if (strlen >= len) {
        return s.substring(0, s.length - 2) + "...";
      }
    }
  }
  return s;
}

export default {
  escapeHTML,
  hashCode,
  unescapeHTML,
  handleImageAdded,
  download,
  recover,
  recoverNotNull,
  hasPermission,
  treeDataTranslate,
  printLogo,
  deepClone,
  validatenull,
  cutString
}
