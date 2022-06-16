<template>
  <div class="tinymce-editor">
    <editor v-model="myValue"
            :init="init"
            :disabled="disabled"
            @onClick="onClick">
    </editor>
  </div>
</template>

<script>
import tinymce from 'tinymce/tinymce'
// import 'tinymce/themes/modern/theme'
import 'tinymce/themes/silver'  // 引入主题包 themes.js
import 'tinymce/icons/default'  // // 引入图标包 icons.js
import Editor from '@tinymce/tinymce-vue'
// 引入编辑器插件（基本免费插件都在这儿了）
import 'tinymce/plugins/table'
import 'tinymce/plugins/lists'
import 'tinymce/plugins/preview'
import 'tinymce/plugins/hr'
import 'tinymce/plugins/fullscreen' //全屏
import 'tinymce/plugins/print'  //打印
import 'tinymce/plugins/advlist'  //高级列表
import 'tinymce/plugins/autolink'  //自动链接
import 'tinymce/plugins/link'  //超链接
import 'tinymce/plugins/image'  //插入编辑图片
import 'tinymce/plugins/lists' //列表插件
import 'tinymce/plugins/charmap'  //特殊字符
import 'tinymce/plugins/media' //插入编辑媒体
import 'tinymce/plugins/wordcount'//字数统计
import '@npkg/tinymce-plugins/imagetools'
import '@npkg/tinymce-plugins/upfile' // 文件上传

import {upload} from "@/api/modules/sys/webdisk";
import config from '@/config'

export default {
  name: "ChTinymceEditor",
  components: {
    Editor
  },
  props: {
    //传入一个value，使组件支持v-model绑定
    value: {
      type: String,
      default: ''
    },
    disabled: {
      type: Boolean,
      default: false
    },
    plugins: {
      type: [String, Array],
      default: 'lists link image media table charmap wordcount preview hr fullscreen print upfile'
      // default: [
      //   'advlist autolink lists link image charmap print preview anchor',
      //   'searchreplace visualblocks code fullscreen',
      //   'insertdatetime media table paste code help wordcount'
      // ]
    },
    toolbar: {
      type: [String, Array],
      default: 'undo redo |  formatselect | bold italic forecolor backcolor | ' +
        'alignleft aligncenter alignright alignjustify | bullist numlist outdent indent | ' +
        'lists link upfile image media table'
    }
  },
  data() {
    return {
      //初始化配置
      init: {
        language_url: './static/tinymce/langs/zh_CN.js',
        language: 'zh_CN',
        // 引入皮肤
        // skin_url: './static/tinymce/skins/lightgray',
        skin_url: './static/tinymce/skins/ui/oxide',
        height: 300,
        plugins: this.plugins,
        toolbar: this.toolbar,
        // 设为false时，隐藏编辑器界面右下角的“Powered by Tiny”（官方汉化为：由Tiny驱动）字样。
        branding: false,
        // 设为false时，可隐藏底栏的元素路径
        elementpath: false,
        menubar: true,
        placeholder: '请输入通告内容...',
        //此处为图片上传处理函数，这个直接用了base64的图片形式上传图片，
        //如需ajax上传可参考https://www.tiny.cloud/docs/configure/file-image-upload/#images_upload_handler
        images_upload_handler: (blobInfo, success, failure) => {
          // const img = 'data:image/jpeg;base64,' + blobInfo.base64()
          // success(img)
          // 转化为易于理解的file对象
          let data = new FormData();
          data.append('file', blobInfo.blob());
          upload(data).then(({data}) => {
            if (data && data.success) {
              if (data.data) {
                success(config.webdisk_download_uri + data.data['userFileId'])
              }
            }
          }).catch(e => failure(e))
        },
        // 附件
        attachment_max_size: 209715200,
        attachment_style:'.attachment>img{display:inline-block!important;max-width:30px!important;}',
        attachment_icons_path: 'https://unpkg.com/@npkg/tinymce-plugins/plugins/attachment/icons',
        attachment_upload_handler: function (file, success, failure, progressCallback) {
          let data = new FormData();
          data.append('file', file);
          upload(data).then(({data}) => {
            if (data && data.success) {
              if (data.data) {
                success(config.webdisk_download_uri + data.data['userFileId'])
              }
            }
          }).catch(e => failure(e))
        },
        file_callback: function (file, success, failure) {
          // 转化为易于理解的file对象
          let data = new FormData();
          data.append('file', file);
          upload(data).then(({data}) => {
            if (data && data.success) {
              if (data.data) {
                success(config.webdisk_download_uri + data.data['userFileId'], {
                  text: decodeURI(data.data['fileName']) + '.' + data.data['extendName']
                })
              }
            }
          }).catch(e => failure(e))
        }
      },
      myValue: this.value
    }
  },
  mounted() {
    tinymce.init({})
  },
  methods: {
    //添加相关的事件，可用的事件参照文档=> https://github.com/tinymce/tinymce-vue => All available events
    //需要什么事件可以自己增加
    onClick(e) {
      this.$emit('onClick', e, tinymce)
    },
    //可以添加一些自己的自定义事件，如清空内容
    clear() {
      this.myValue = ''
    }
  },
  watch: {
    value(newValue) {
      this.myValue = newValue
    },
    myValue(newValue) {
      this.$emit('input', newValue)
    }
  }
}
</script>

<style scoped>

</style>
