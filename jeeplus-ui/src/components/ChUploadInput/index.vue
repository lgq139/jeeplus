<!--
  文件上传，文件名回写input
-->
<template>
  <el-input v-model="inputValue" v-bind="$attrs" @clear="handleClear">
    <template slot="append">
      <el-upload
        ref="upload"
        :limit="1"
        :headers="headers"
        :action="url"
        :on-progress="handleFileUploadProgress"
        :on-success="handleFileSuccess"
        :on-error="handleFileError"
        :auto-upload="true"
        :show-file-list="false"
      >
        <el-button>上传文件</el-button>
      </el-upload>
    </template>
  </el-input>

</template>

<script>
import {getToken} from "@/utils/auth";
export default {
  name: "ChUploadInput",
  model: {
    prop: 'value',
    event: 'upload'
  },
  props: {
    value: Object,
    //
    title: {
      type: String,
      default: '上传文件'
    },
    // 设置上传的请求头部
    headers: {
      type: Object,
      default: () => {
        return { token: getToken() }
      }
    },
  },
  computed: {
    url() {
      return process.env.VUE_APP_BASE_API + '/sys/webdisk/upload'
    }
  },
  data() {
    return {
      inputValue: this.value['fileName'] || ''
    }
  },
  watch: {
    value: {
      handler(val) {
        this.$nextTick(() => {
          this.inputValue = val['fileName'] || ''
        })
      },
      deep: true
    }
  },
  methods: {
    // 文件上传中处理
    handleFileUploadProgress(event, file, fileList) {
      this.isUploading = true;
    },
    // 文件上传成功处理
    handleFileSuccess(response, file, fileList) {
      this.isUploading = false;
      this.$refs.upload.clearFiles();
      if (response.code === 200) {
        this.inputValue = file.name
        this.$emit('upload', {
          fileName: file.name,
          fileId: response.data['userFileId']
        })
      } else {
        this.$message.error(response.msg)
      }
    },
    // 文件上传失败处理
    handleFileError(err, file, fileList) {
      this.$message.error(err)
    },
    // clearable
    handleClear() {
      this.$emit('upload', {})
    }
  }
}
</script>

<style scoped>

</style>
