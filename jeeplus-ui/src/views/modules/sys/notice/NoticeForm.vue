<template>
  <ch-dialog
    :title="title + '通知公告'"
    :visible.sync="visible"
    width="70%"
    v-loading="loading"
    @close="doClose"
    @submit="doSubmit"
  >
    <el-form ref="form" form="form" :model="form" :rules="rules" label-width="80px" size="small">
      <el-form-item label="标题" prop="title">
        <el-input v-model="form.title" />
      </el-form-item>
      <el-form-item label="类型" prop="type">
        <el-select v-model="form.type">
          <el-option v-for="(item, index) in typeOptions" :key="index" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="发布">
        <el-radio-group v-model="form.enabled">
          <el-radio label="1">立即发布</el-radio>
          <el-radio label="2">延后发布</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="内容">
        <ch-tinymce-editor v-if="visible" v-model="form.content" :disabled="disabled" ref="editor" />
      </el-form-item>
    </el-form>
  </ch-dialog>
</template>

<script>
import ChDialog from "@/components/ChDialog";
import ChTinymceEditor from "@/components/ChTinymceEditor";
import {saveNotice} from "@/api/modules/sys/notice";
export default {
  name: "NoticeForm",
  components: {ChTinymceEditor, ChDialog},
  data() {
    return {
      title: '',
      visible: false,
      loading: false,
      disabled: false,
      form: {
        id: undefined,
        title: '',
        type: '1',
        content: '',
        enabled: '1',
        publishTime: undefined
      },
      rules: {
        title: [
          {required: true, message: '公告标题不能为空', trigger: 'blur'}
        ],
        type: [
          {required: true, message: '公开信息类型为必须', trigger: 'change'}
        ]
      },
      typeOptions: []
    }
  },
  methods: {
    showEdit(row) {
      if (row) {
        this.title = '修改'
        Object.keys(this.form).forEach(key => this.form[key] = row[key])
      } else {
        this.title = '新建'
      }
      this.typeOptions = this.$dictUtils.getDictList('notice_type')
      this.visible = true
    },

    doClose() {
      this.$refs.form.resetFields()
      this.form = this.$options.data().form
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
