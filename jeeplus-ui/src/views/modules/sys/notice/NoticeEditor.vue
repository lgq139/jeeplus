<template>
  <div v-loading="loading">
    <el-form ref="form" form="form" :model="form" :rules="rules" label-width="80px" size="small">
      <el-form-item label="标题" prop="title">
        <el-input v-model="form.title" />
      </el-form-item>
      <el-form-item label="发布">
        <el-radio-group v-model="form.enabled">
          <el-radio label="1">立即发布</el-radio>
          <el-radio label="2">延后发布</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="内容">
        <ch-tinymce-editor v-model="form.content" ref="editor" />
      </el-form-item>
      <el-form-item>
        <div style="text-align: right;">
          <el-button type="primary" @click="doSubmit">保 存</el-button>
        </div>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import ChTinymceEditor from "@/components/ChTinymceEditor";
import {getNotice, saveNotice} from "@/api/modules/sys/notice";
export default {
  name: "NoticeEditor",
  components: {ChTinymceEditor},
  data() {
    return {
      loading: false,
      disabled: false,
      form: {
        id: undefined,
        title: '',
        type: '2',
        content: '',
        enabled: '1',
        publishTime: undefined
      },
      rules: {
        title: [
          {required: true, message: '公告标题不能为空', trigger: 'blur'}
        ]
      }
    }
  },
  activated() {
    if (this.$route.query['id']) {
      this.showEdit(this.$route.query['id'])
    }
  },
  methods: {
    showEdit(id) {
      getNotice({id: id})
        .then(({data}) => {
          if (data && data.success) {
            Object.keys(this.form).forEach(key => this.form[key] = data.data[key])
          }
        })
    },

    doClose() {
      // this.$refs.editor.destroy()
    },

    doSubmit() {
      this.$refs.form.validate((valid) => {
        if (valid) {
          this.loading = true
          saveNotice(this.form)
            .then(({data}) => {
              this.loading = false
              if (data && data.success) {
                this.$message.success(data.msg)
                this.$emit("submit-success")
                this.visible = false
              }
            })
        }
      })
    }
  }
}
</script>

<style scoped>

</style>
