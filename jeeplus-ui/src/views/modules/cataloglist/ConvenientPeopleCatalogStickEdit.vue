<template>
  <div class="add-shade">
    <ch-dialog
      :title="title"
      :visible.sync="visible"
      @submit="doSubmit"
      @close="addDialogClosed"
    >
      <div v-if="isAdd">
        <el-form ref="form" :model="formData" label-width="80px">
          <el-form-item label="基本编码" prop="baseCode">
            <el-input v-model="formData.baseCode" placeholder="请输入基本编码"></el-input>
          </el-form-item>
          <el-form-item label="目录名称" prop="cataName">
            <el-input v-model="formData.cataName" placeholder="请输入目录名称"></el-input>
          </el-form-item>
          <el-form-item label="事项类型" prop="cataType">
            <el-select v-model="formData.cataType" placeholder="请选择事项类型">
              <el-option label="查询事项" value="search"></el-option>
              <el-option label="咨询事项" value="refer"></el-option>
              <el-option label="证明事项" value="prove"></el-option>
              <el-option label="便民服务事项" value="convenient"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="目录层级" prop="cataLevel">
            <el-radio-group v-model="formData.cataLevel">
              <el-radio label="国家级"></el-radio>
              <el-radio label="省级"></el-radio>
              <el-radio label="市级"></el-radio>
              <el-radio label="县级"></el-radio>
              <el-radio label="镇(乡、街道)级"></el-radio>
              <el-radio label="村(社区)级"></el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="版本号" prop="cataVersion">
            <el-input oninput="if(value<1)value=1" v-model="formData.cataVersion" type="number" placeholder="请输入版本号" :min="1"></el-input>
          </el-form-item>
          <el-form-item label="备注(选填)" prop="remarks">
            <el-input type="textarea" v-model="formData.remarks"></el-input>
          </el-form-item>
        </el-form>
      </div>
      <div v-else-if="isEdit">
        <el-form ref="form" :model="formData" label-width="80px">
          <el-form-item label="基本编码">
            <el-input v-model="formData.coding" placeholder="请输入基本编码" :readonly="true"></el-input>
          </el-form-item>
          <el-form-item label="目录名称">
            <el-input v-model="formData.name" placeholder="请输入目录名称"></el-input>
          </el-form-item>
          <el-form-item label="事项类型">
            <el-select v-model="formData.state" placeholder="请选择事项类型" :disabled="true">
              <el-option label="查询事项" value="search"></el-option>
              <el-option label="咨询事项" value="refer"></el-option>
              <el-option label="证明事项" value="prove"></el-option>
              <el-option label="便民服务事项" value="convenient"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="目录层级">
            <el-radio-group v-model="formData.type">
              <el-radio label="1">国家级</el-radio>
              <el-radio label="2">省级</el-radio>
              <el-radio label="3">市级</el-radio>
              <el-radio label="4">县级</el-radio>
              <el-radio label="5">镇(乡、街道)级</el-radio>
              <el-radio label="6">村(社区)级</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="版本号">
            <el-input oninput="if(value<1)value=1" v-model="formData.version" type="number" placeholder="请输入版本号" :min="1" :readonly="true"></el-input>
          </el-form-item>
          <el-form-item label="备注(选填)">
            <el-input type="textarea" v-model="formData.desc"></el-input>
          </el-form-item>
        </el-form>
      </div>
      <div v-else>
        <el-form ref="form" :model="formData" label-width="80px">
          <el-form-item label="基本编码">
            <el-input v-model="formData.coding" placeholder="请输入基本编码" :readonly="true"></el-input>
          </el-form-item>
          <el-form-item label="目录名称">
            <el-input v-model="formData.name" placeholder="请输入目录名称" :readonly="true"></el-input>
          </el-form-item>
          <el-form-item label="事项类型">
            <el-select v-model="formData.state" placeholder="请选择事项类型" :disabled="true">
              <el-option label="查询事项" value="search"></el-option>
              <el-option label="咨询事项" value="refer"></el-option>
              <el-option label="证明事项" value="prove"></el-option>
              <el-option label="便民服务事项" value="convenient"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="目录层级">
            <el-input v-model="formData.type" placeholder="请输入目录名称" :readonly="true"></el-input>
          </el-form-item>
          <el-form-item label="版本号">
            <el-input oninput="if(value<1)value=1" v-model="formData.version" type="number" placeholder="请输入版本号" :min="1" :readonly="true"></el-input>
          </el-form-item>
          <el-form-item label="备注(选填)">
            <el-input type="textarea" v-model="formData.remarks" :readonly="true"></el-input>
          </el-form-item>
        </el-form>
      </div>
    </ch-dialog>

  </div>
</template>

<script>
  import {editCatalogStickData,lookCatalogStickData} from "@/api/modules/catalog/catalogStick";
  // 便民目录新增、查看、变更
  import ChDialog from "@/components/ChDialog";
  export default {
    data () {
      return {
        isAdd:true, // 是否为新增页
        visible: false, // 弹框显示状态
        loading: false,
        title:'',  // 弹框显示标题
        isEdit:false,  //  是否可编辑 flase表示可编辑
        formData: {  // 表单数据
          coding: '',
          name: '',
          state: '',
          type: '',
          version: '',
          desc: ''
        }
      }
    },
    components: {
      ChDialog
    },
    methods: {
      init(method, obj) {
        console.log(method)
        console.log(obj)
        // this.method = method
        // this.inputForm.id = obj.id
        if (method === 'add') {
          this.title = '新增'
          this.isAdd=true
          this.isEdit=false
        } else if (method === 'change') {
          this.title = '变更'
          this.isAdd=false
          this.isEdit=true
        } else if (method === 'view') {
          this.title = '查看'
          this.isAdd=false
          this.isEdit=false
        }
        // this.visible = true
        lookCatalogStickData(obj).then(({data}) => {
           console.log(data)
          // this.visible = true
          this.$nextTick(() => {
            // this.$refs.menuParentTree.clearHandle()
            // this.$refs['inputForm'].resetFields()
            // this.inputForm.parentId = obj.parent.id
          })
        }).then(() => {

        })
        //   .then(() => {
        //   if (method === 'edit' || method === 'view') { // 修改或者查看
        //     getMenuById({id: this.inputForm.id}).then(({data}) => {
        //       this.inputForm = this.recover(this.inputForm, data.data)
        //     })
        //   }
        // })
      },
      // 表单提交
      doSubmit() {
        console.log(this.formData)
        // this.$refs['inputForm'].validate((valid) => {
        //   if (valid) {
        //     this.loading = true
        //     if (this.inputForm.type === '0') {
        //       this.inputForm.href = ''
        //     }
        //     if (this.inputForm.type === '2' || this.inputForm.type === '3') {
        //       this.inputForm.isShow = '0'
        //     }
        //     saveMenu(this.inputForm).then(({data}) => {
        //       this.loading = false
        //       if (data && data.success) {
        //         this.$message.success(data.msg)
        //         this.visible = false
        //         this.$emit('refreshDataList')
        //       }
        //     })
        //   }
        // })
      },
      // 监听添加用户对话框的关闭事件
      addDialogClosed(){
        // 对整个表单进行重置，将所有字段值重置为初始值并移除校验结果
        this.$refs.form.resetFields()
      }
    }
  }
</script>
<style scoped>
   .el-input[readonly]{
     outline: none;
   }
</style>
