<template>
  <ch-dialog
    :title="title"
    width="700px"
    :visible.sync="visible"
  >
    <el-form :model="inputForm" status-icon :rules="dataRule"
             v-loading="loading"
             ref="inputForm"
             @keyup.enter.native="inputFormSubmit()"
             label-width="120px"
             @submit.native.prevent
             size="small"
    >
       <el-row :gutter="15">
        <el-col :span="24">
          <el-form-item label="角色名称" prop="name" :rules="[
            {required: true, message: '角色名称不能为空', trigger: 'blur'}
          ]">
            <el-input v-model="inputForm.name" placeholder="角色名称" maxlength="50"></el-input>
            <input type="hidden" v-model="inputForm.oldName"/>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="英文名称" prop="enname" :rules="[
            {required: true, message: '角色英文名称不能为空', trigger: 'blur'}
          ]">
            <el-input v-model="inputForm.enname" maxlength="50" placeholder="角色英文名称"></el-input>
            <input type="hidden" v-model="inputForm.oldEname"/>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="是否系统数据" prop="sysData" :rules="[
            {required: true, message: '是否系统数据不能为空', trigger: 'blur'}
          ]">
            <el-select v-model="inputForm.sysData" placeholder="请选择"  style="width: 100%;">
              <el-option
                v-for="item in this.$dictUtils.getDictList('yes_no')"
                :key="item.value"
                :label="item.label"
                :value="item.value">
              </el-option>
            </el-select>
            <div class="help-block">“是”代表此数据只有超级管理员能进行修改</div>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="是否可用" prop="useable" :rules="[
            {required: true, message: '是否可用不能为空', trigger: 'blur'}
          ]">
            <el-select v-model="inputForm.useable" placeholder="请选择"  style="width: 100%;">
              <el-option
                v-for="item in this.$dictUtils.getDictList('yes_no')"
                :key="item.value"
                :label="item.label"
                :value="item.value">
              </el-option>
            </el-select>
            <div class="help-block">“是”代表此数据可用，“否”则表示此数据不可用</div>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="备注" prop="remarks">
            <el-input type="textarea" v-model="inputForm.remarks" placeholder="备注"></el-input>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <template #action>
      <el-button @click="visible = false">关闭</el-button>
      <el-button type="primary" @click="inputFormSubmit()" v-noMoreClick>确定</el-button>
    </template>
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
        title: '',
        inputForm: {
          id: '',
          name: '',
          enname: '',
          remarks: '',
          oldName: '',
          oldEname: '',
          sysData: '1',
          useable: '1'
        },
        dataRule: {
          name: [
            {required: true, message: '角色名称不能为空', trigger: 'blur'}
          ],
          enname: [
            {required: true, message: '角色英文名称不能为空', trigger: 'blue'}
          ]
        }
      }
    },
    methods: {
      init (method, id) {
        this.method = method
        this.inputForm.id = id
        if (method === 'add') {
          this.title = `新建角色`
        } else if (method === 'edit') {
          this.title = '修改角色'
        } else if (method === 'view') {
          this.title = '查看角色'
        }
        this.visible = true
        this.$nextTick(() => {
          this.$refs.inputForm.resetFields()
          if (this.inputForm.id) {
            this.loading = true
            let p3 = this.$http({
              url: `/sys/role/queryById?id=${this.inputForm.id}`,
              method: 'get'
            })
            Promise.all([p3]).then((result) => {
              let data = result[0].data
              if (data && data.success) {
                this.loading = false
                this.inputForm = this.recover(this.inputForm, data.role)
                this.inputForm.oldName = data.role.name
                this.inputForm.oldEnname = data.role.enname
              }
            })
          }
        })
      },
      // 表单提交
      inputFormSubmit () {
        this.$refs['inputForm'].validate((valid) => {
          if (valid) {
            this.loading = true
            this.$http({
              url: `/sys/role/save`,
              method: 'post',
              data: this.inputForm
            }).then(({data}) => {
              this.loading = false
              if (data && data.success) {
                this.$message.success(data.msg)
                this.visible = false
                this.$emit('refreshDataList')
              }
            })
          }
        })
      }
    }
  }
</script>
