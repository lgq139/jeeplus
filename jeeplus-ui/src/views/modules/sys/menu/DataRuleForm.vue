<template>
  <ch-dialog
    :title="title"
    width="50%"
    :visible.sync="visible"
    @submit="doSubmit"
  >
    <el-form :model="inputForm" v-loading="loading" :class="method==='view'?'readonly':''" :disabled="method==='view'" ref="inputForm" @keyup.enter.native="doSubmit()"
             label-width="120px" @submit.native.prevent>
          <el-form-item label="数据规则名称" prop="name">
              <el-input  v-model="inputForm.name" placeholder="数据规则名称"></el-input>
          </el-form-item>
          <el-form-item label="规则实体类" prop="className">
              <el-input  v-model="inputForm.className" placeholder="规则实体类"></el-input>
              <div class="help-block">请输入要进行数据过滤的的实体类名(不含包名,例如：User),将对findList和findAllList进行数据过滤。</div>
          </el-form-item>
          <el-form-item label="规则字段" prop="field">
              <el-input  v-model="inputForm.field" placeholder="规则字段"></el-input>
          </el-form-item>
          <el-form-item label="规则条件" prop="express">
            <el-select v-model="inputForm.express" placeholder="规则条件"  style="width: 100%;">
                <el-option
                  v-for="item in this.$dictUtils.getDictList('t_express')"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value">
                </el-option>
            </el-select>
          </el-form-item>
          <el-form-item  label="规则值" prop="value">
            <el-input v-model="inputForm.value" placeholder="value"></el-input>
          </el-form-item>
           <el-form-item label="自定义sql" prop="sqlSegment">
             <el-input v-model="inputForm.sqlSegment" type="textarea"
                  :rows="2" placeholder="备注"></el-input>
           </el-form-item>
          <el-form-item label="备注" prop="remarks">
            <el-input v-model="inputForm.remarks" type="textarea"
                  :rows="2" placeholder="备注"></el-input>
          </el-form-item>
    </el-form>
  </ch-dialog>
</template>

<script>
  import ChDialog from "@/components/ChDialog";
  export default {
    components: {ChDialog},
    data () {
      return {
        visible: false,
        loading: false,
        method: '',
        title: '',
        inputForm: {
          id: '',
          menuId: '', // 所属菜单
          name: '', // 数据规则名称
          className: '',   // 实体类名
          field: '', // 规则字段
          express: '', // 规则条件
          value: '', // 规则值
          sqlSegment: '', // 自定义sql
          remarks: '' // 备注
        }
      }
    },
    methods: {
      init (method, obj) {
        this.method = method
        this.inputForm.id = obj.id
        this.inputForm.menuId = obj.menuId
        if (method === 'add') {
          this.title = `新增数据规则`
        } else if (method === 'edit') {
          this.title = '修改数据规则'
        } else if (method === 'view') {
          this.title = '查看数据规则'
        }
        this.visible = true
        this.$nextTick(() => {
          this.$refs['inputForm'].resetFields()
          if (method === 'edit' || method === 'view') { // 修改或者查看
            this.$http({
              url: `/sys/dataRule/queryById?id=${this.inputForm.id}`,
              method: 'get'
            }).then(({data}) => {
              this.inputForm = this.recover(this.inputForm, data.dataRule)
            })
          }
        })
      },
      // 表单提交
      doSubmit () {
        this.$refs['inputForm'].validate((valid) => {
          if (valid) {
            this.loading = true
            this.$http({
              url: `/sys/dataRule/save`,
              method: 'post',
              data: this.inputForm
            }).then(({data}) => {
              this.loading = false
              if (data && data.success) {
                this.$message({
                  message: data.msg,
                  type: 'success',
                  duration: 1500
                })
                this.visible = false
                this.$emit('refreshDataList', this.inputForm.menuId)
              }
            })
          }
        })
      }
    }
  }
</script>
