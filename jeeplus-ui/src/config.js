
export default {
  productName: '基层便民服务后台管理系统',
  tokenName: 'base_config_access_token',
  //------------------------
  // 统一认证，开发环境clientId
  dev_client_id: 'zlcn4ZKxsH',
  // 统一认证，部署环境clientId
  prod_client_id: 'rS7n9Dv05G',
  // 统一认证，认证地址
  authorize_uri: 'http://172.16.16.231:8230/zwmh/oauth2/authorize',
  // 统一认证，登出地址
  access_logout_uri: 'http://172.16.16.231:8230/zwmh/oauth2/logout',
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
  form_preview_uri: 'http://59.195.195.94:8807/form/#/GenerateForm?type=iframe&method=view&formId='
}
