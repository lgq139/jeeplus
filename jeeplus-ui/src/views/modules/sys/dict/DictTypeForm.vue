<template>
  <ch-dialog
    :title="title"
    :visible.sync="visible"
    width="700px"
    @close="close"
    @submit="doSubmit"
  >
    <el-form :model="inputForm"
             :rules="dataRule"
             v-loading="loading"
             ref="inputForm"
             size="small"
             @keyup.enter.native="doSubmit()"
             label-width="80px"
             @submit.native.prevent>
      <el-form-item label="类型" prop="type">
        <el-input v-model="inputForm.type" maxlength="50" placeholder="类型"></el-input>
      </el-form-item>
      <el-form-item label="描述" prop="description">
        <el-input type="textarea" v-model="inputForm.description" maxlength="50" placeholder="描述"></el-input>
      </el-form-item>
      <el-row :gutter="10">
        <el-col :span="12">
          <el-form-item label="字典分组">
            <el-select v-model="inputForm.dictGroup" clearable style="width: 100%;">
              <el-option v-for="(item, index) in this.$dictUtils.getDictList('sys_dict_group')" :key="index" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="10">
        <el-col :span="12">
          <el-form-item label="布局" prop="layout">
            <el-radio-group v-model="inputForm.layout">
              <el-radio v-for="(item, index) in this.$dictUtils.getDictList('sys_dict_type')" :key="index" :label="item.value">{{item.label}}</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="对外开放" prop="open">
            <el-radio-group v-model="inputForm.open">
              <el-radio v-for="(item, index) in this.$dictUtils.getDictList('sys_open')" :key="index" :label="item.value">{{item.label}}</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
  </ch-dialog>
</template>

<script>
  import ChDialog from "@/components/ChDialog";
  import {saveDictType} from "@/api/modules/sys/dict";
  export default {
    components: {ChDialog},
    data () {
      return {
        visible: false,
        loading: false,
        title: '',
        method: '',
        inputForm: {
          id: undefined,
          type: undefined,
          description: undefined,
          dictGroup: 'SYSTEM_DEFAULT',
          // 默认列表i
          layout: '1',
          // 默认不开放
          open: '2'
        },
        dataRule: {
          type: [
            {required: true, message: '类型不能为空', trigger: 'blur'}
          ],
          description: [
            {required: true, message: '描述不能为空', trigger: 'blur'}
          ],
          dictGroup: [
            {required: true, message: '字典分组不能为空', trigger: 'change'}
          ]
        }
      }
    },
    methods: {
      showEdit(method, row) {
        this.method = method
        if (method === 'add') {
          this.title = `新增字典`
        } else if (method === 'edit') {
          this.title = '编辑字典'
          Object.keys(this.inputForm).forEach(key => this.inputForm[key] = row[key])
        }
        this.visible = true
      },
      close() {
        this.$refs.inputForm.resetFields()
        this.inputForm = this.$options.data().inputForm
      },
      // 表单提交
      doSubmit () {
        this.$refs['inputForm'].validate((valid) => {
          if (valid) {
            this.loading = true
            saveDictType(this.inputForm).then(({data}) => {
              this.loading = false
              if (data && data.success) {
                this.$message({
                  message: '操作成功',
                  type: 'success',
                  duration: 1500
                })
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
