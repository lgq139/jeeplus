<template>
  <ch-dialog
    :title="title + '字典项'"
    :visible.sync="visible"
    @close="close"
  >
    <el-form ref="inputForm" :model="inputForm" :rules="dataRule" v-loading="loading" label-width="80px" size="small">
      <el-form-item label="标签" prop="label">
        <el-input v-model="inputForm.label" placeholder="标签"></el-input>
      </el-form-item>
      <el-form-item label="键值" prop="value">
        <el-input v-model="inputForm.value" placeholder="键值"></el-input>
      </el-form-item>
      <el-form-item label="排序号" prop="sort">
        <el-input-number  :step="10" v-model="inputForm.sort" placeholder="排序号"></el-input-number>
      </el-form-item>
    </el-form>
    <template slot="action">
      <el-button @click="visible = false">关闭</el-button>
      <el-button type="primary" @click="doSubmit()" v-noMoreClick>确定</el-button>
    </template>
  </ch-dialog>
</template>

<script>
  import ChDialog from "@/components/ChDialog";
  import {saveDictValue} from "@/api/modules/sys/dict";
  export default {
    components: {ChDialog},
    data () {
      return {
        title: '',
        visible: false,
        loading: false,
        inputForm: {
          id: undefined,
          dictType: undefined,
          dictTypeId: undefined,
          label: undefined,
          value: undefined,
          sort: 10
        },
        dataRule: {
          label: [
            {required: true, message: '标签不能为空', trigger: 'blur'}
          ],
          value: [
            {required: true, message: '键值不能为空', trigger: 'blur'}
          ]
        }
      }
    },
    methods: {

      showEdit(action, obj) {
        if (action === 'add') {
          this.title = '新增'
          this.inputForm.dictTypeId = obj.dictTypeId
          this.inputForm.dictType = obj.dictType
        } else if (action === 'edit') {
          this.title = '修改'
          Object.keys(this.inputForm).forEach(key => this.inputForm[key] = obj[key])
        }
        this.loading = false
        this.visible = true
      },

      close() {
        this.$refs.inputForm.resetFields()
        this.inputForm = this.$options.data().inputForm
      },

      doSubmit () {
        this.$refs['inputForm'].validate((valid) => {
          if (valid) {
            this.loading = true
            saveDictValue(this.inputForm).then(({data}) => {
              this.loading = false
              if (data && data.success) {
                this.$message.success(data.msg)
                this.visible = false
                this.$emit('refreshDataList')
                this.$dictUtils.refreshDictList()
              }
            })
          }
        })
      }
    }
  }
</script>
