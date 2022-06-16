
export default {
  productName: '基础配置系统',
  tokenName: 'base_config_access_token',
  //------------------------
  // 统一认证，开发环境clientId
  dev_client_id: 'R6SuZuilfQ',
  // 统一认证，部署环境clientId
  prod_client_id: 'rS7n9Dv05G',
  // 统一认证，认证地址
  authorize_uri: 'http://59.195.195.94:8807/zwmh/oauth2/authorize',
  // 统一认证，登出地址
  access_logout_uri: 'http://59.195.195.94:8807/zwmh/oauth2/logout',
  //-------------------------
  // 电子材料系统下载接口
  webdisk_download_uri: 'http://59.195.195.94:8807/webdisk/manager/filetransfer/downloadFiles?fileId=',
  // 电子材料系统文件预览接口
  webdisk_preview_uri: 'http://59.195.195.94:8807/webdisk/manager/filetransfer/preview?userFileId=',
  //-------------------------
  // 表单系统主页地址
  form_system: 'http://59.195.195.94:8807/form/',
  // 表单系统接口地址
  form_system_uri: 'http://59.195.195.94:8807/form/manager/app/dubbo',
  // 表单系统预览地址
  form_preview_uri: 'http://59.195.195.94:8807/form/#/GenerateForm?type=iframe&method=view&formId=',
  //-------------------------
  // 综合窗口热点场景式办理情况统计
  window_uri: 'http://59.195.195.94:8801/window/base',
  window_client_id: 'MOynUkZby4',
  window_secret: 'E5yEfjzABGjYHbflRXZd',
  window_projectCountByCatalog: 'http://59.195.195.94:8801/window/base/api/service/count/projectCountByCatalog?client_id=F0wptALZff&time=20220521113245&sign=b4c2510469b7ad0036ec35c85f956e59'
}
// http://59.195.195.94:8801/zwmh/oauth2/authorize?client_id=G47CNvxONX&response_type=code&scope=super&status=1638929295724&goto=http://localhost:3000/#/home
