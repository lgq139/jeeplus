<template>
  <ch-dialog
    :title="title + '客户端配置'"
    :visible.sync="visible"
    @close="close"
    @submit="doSubmit"
  >
    <el-form ref="form" :model="form" :rules="rules" label-width="120px" size="small" v-loading="loading">
<!--      <el-form-item label="系统ID">-->
<!--        <el-input v-model="form.id" />-->
<!--      </el-form-item>-->
<!--      <el-form-item label="访问密钥">-->
<!--        <el-input v-model="form.secret" />-->
<!--      </el-form-item>-->
      <el-form-item label="客户端名称" prop="systemName">
        <el-input v-model="form.systemName" />
      </el-form-item>
      <el-form-item label="客户端描述" prop="description">
        <el-input v-model="form.description" />
      </el-form-item>
    </el-form>
  </ch-dialog>
</template>

<script>
import ChDialog from "@/components/ChDialog";
import {saveApiClient} from "@/api/modules/sys/apiclient";
export default {
  name: "ApiClientForm",
  components: {ChDialog},
  data() {
    return {
      title: '',
      visible: false,
      loading: false,
      form: {
        id: undefined,
        systemName: undefined,
        // secret: undefined,
        description: undefined
      },
      rules: {
        systemName: [
          {required: true, message: '客户端名称不萌为空', trigger: 'blur'}
        ],
        description: [
          {required: true, message: '客户端描述不能为空', trigger: 'blur'}
        ]
      }
    }
  },
  methods: {
    showEdit(action, row) {
      if (action === 'add') {
        this.title = '新增'
      } else if (action === 'edit') {
        this.title = '修改'
        Object.keys(this.form).forEach(key => this.form[key] = row[key])
      }
      this.visible = true
    },

    close() {
      this.$refs.form.resetFields()
      this.form = this.$options.data().form
    },

    doSubmit() {
      this.$refs.form.validate(valid => {
        if (valid) {
          this.loading = true
          saveApiClient(this.form).then(({data}) => {
            this.loading = false
            if (data && data.success) {
              this.$message.success(data.msg)
              this.$emit('refreshDataList')
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
