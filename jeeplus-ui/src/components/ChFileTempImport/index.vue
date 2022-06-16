<template>
  <ch-dialog
    :title="title"
    :visible.sync="show"
    width="400px"
    v-loading="isUploading"
  >
    <el-upload
      ref="upload"
      :limit="1"
      accept=".xlsx, .xls"
      :headers="headers"
      :action="url + '?updateSupport=' + updateSupport"
      :disabled="isUploading"
      :on-progress="handleFileUploadProgress"
      :on-success="handleFileSuccess"
      :on-error="handleFileError"
      :auto-upload="false"
      drag
    >
      <i class="el-icon-upload"></i>
      <div class="el-upload__text">
        将文件拖到此处，或
        <em>点击上传</em>
      </div>
      <div class="el-upload__tip" slot="tip">
        <el-checkbox v-model="updateSupport" />是否更新已经存在的用户数据
        <el-link type="info" style="font-size:12px" @click="importTemplate">下载模板</el-link>
      </div>
      <div class="el-upload__tip" style="color:red" slot="tip">提示：仅允许导入“xls”或“xlsx”格式文件！</div>
    </el-upload>
    <template #action>
      <el-button type="primary" @click="submitFileForm">上 传</el-button>
      <el-button @click="show = false">关 闭</el-button>
    </template>
  </ch-dialog>
</template>

<script>

import { getToken } from '@/utils/auth'
import { downloadXlsx } from '@/utils/zipdownload'
import ChDialog from "@/components/ChDialog";

export default {
  name: 'ChFileTempImport',
  components: {ChDialog  },
  props: {
    title: {
      type: String,
      default: '数据导入'
    },
    visible: Boolean,
    // 设置上传的请求头部
    headers: {
      type: Object,
      default: () => {
        return { token: getToken() }
      }
    },
    // 上传的地址
    url: {
      type: String,
      required: true
      // default: process.env.VUE_APP_BASE_API + "/system/user/importData"
    },
    // 导入模板URL
    templateUrl: {
      type: String,
      required: true
    }
  },

  computed: {
    show: {
      get() {
        return this.visible
      },
      set(val) {
        this.$emit('update:visible', val)
      }
    },
  },

  data() {
    return {
      isUploading: false,
      // 是否更新已经存在的用户数据
      updateSupport: 0
    }
  },

  methods: {
    /** 下载模板操作 */
    importTemplate() {
      // downloadXlsx(this.templateUrl)
      this.$utils.download(this.templateUrl)
    },
    // 文件上传中处理
    handleFileUploadProgress(event, file, fileList) {
      this.isUploading = true;
    },
    // 文件上传成功处理
    handleFileSuccess(response, file, fileList) {
      this.isUploading = false;
      this.$refs.upload.clearFiles();
      if (response.code === 200) {
        // this.$alert('导入成功!', "导入结果", { dangerouslyUseHTMLString: true });
        this.show = false;
        this.$message.success('导入成功')
        this.$emit('import-success')
      } else {
        // this.$alert(response.msg, "导入结果", { dangerouslyUseHTMLString: true });
        this.$message.error(response.msg)
      }
    },
    // 文件上传失败处理
    handleFileError(err, file, fileList) {
      this.$message.error(err)
    },
    // 提交上传文件
    submitFileForm() {
      if (this.$refs.upload._data.uploadFiles.length > 0) {
        this.$refs.upload.submit();
      } else {
        this.$message.warning('请选择需要上传的文件')
      }
    }
  }
}
</script>

<style scoped>

</style>
