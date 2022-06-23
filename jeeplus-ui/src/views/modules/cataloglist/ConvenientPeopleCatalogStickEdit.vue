<template>
  <div class="add-shade">
    <ch-dialog
      :title="title"
      :visible.sync="visible"
      :isConfirmShow="isConfirmShow"
      v-if="visible"
      @submit="doSubmit"
      @close="addDialogClosed"
    >
      <div v-if="isAdd">
        <el-form ref="form" :model="formData" :rules="rules" label-width="80px">
          <el-form-item label="基本编码" prop="baseCode">
            <el-input v-model="formData.baseCode" placeholder="请输入基本编码"></el-input>
          </el-form-item>
          <el-form-item label="目录名称" prop="cataName">
            <el-input v-model="formData.cataName" placeholder="请输入目录名称"></el-input>
          </el-form-item>
          <el-form-item label="事项类型" prop="cataType">
            <el-select v-model="formData.cataType" placeholder="请选择事项类型">
              <el-option
                v-for="(item,key) in stateData"
                :key="key"
                :label="item"
                :value="key">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="目录层级" prop="cataLevel">
            <el-radio-group v-model="formData.cataLevel">
              <el-radio v-for="(item,key) in cataLevelData" :label="key" :key="key">{{item}}</el-radio>
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
          <el-form-item label="基本编码" prop="baseCode">
            <el-input v-model="formData.baseCode" placeholder="请输入基本编码" :readonly="true"></el-input>
          </el-form-item>
          <el-form-item label="目录名称" prop="cataName">
            <el-input v-model="formData.cataName" placeholder="请输入目录名称"></el-input>
          </el-form-item>
          <el-form-item label="事项类型" prop="cataType">
            <el-select v-model="stateData[formData.cataType]" :disabled="true">
            </el-select>
          </el-form-item>
          <el-form-item label="目录层级" prop="cataLevel">
            <el-radio-group v-model="formData.cataLevel">
              <el-radio v-for="(item,key) in cataLevelData" :label="key" :key="key">{{item}}</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="版本号" prop="cataVersion">
            <el-input oninput="if(value<1)value=1" v-model="formData.cataVersion" type="number" placeholder="请输入版本号" :min="1" :readonly="true"></el-input>
          </el-form-item>
          <el-form-item label="备注(选填)" prop="remarks">
            <el-input type="textarea" v-model="formData.remarks"></el-input>
          </el-form-item>
        </el-form>
      </div>
      <div v-else>
        <el-form ref="form" :model="formData" label-width="80px">
          <el-form-item label="基本编码" prop="baseCode">
            <el-input v-model="formData.baseCode" :readonly="true"></el-input>
          </el-form-item>
          <el-form-item label="目录名称" prop="cataName" >
            <el-input v-model="formData.cataName" :readonly="true"></el-input>
          </el-form-item>
          <el-form-item label="事项类型" prop="cataType">
            <el-select v-model="stateData[formData.cataType]" :disabled="true">
            </el-select>
          </el-form-item>
          <el-form-item label="目录层级" prop="cataLevel">
            <el-input v-model="formData.cataLevel" :readonly="true"></el-input>
          </el-form-item>
          <el-form-item label="版本号" prop="cataVersion">
            <el-input oninput="if(value<1)value=1" v-model="formData.cataVersion" type="number" :min="1" :readonly="true"></el-input>
          </el-form-item>
          <el-form-item label="备注(选填)" prop="cataVersion">
            <el-input type="textarea" v-model="formData.remarks" :readonly="true"></el-input>
          </el-form-item>
        </el-form>
      </div>
    </ch-dialog>

  </div>
</template>

<script>
  import {addCatalogStickData,editCatalogStickData,lookCatalogStickData,getStateData} from "@/api/modules/catalog/catalogStick";
  // 便民目录新增、查看、变更
  import ChDialog from "@/components/ChDialog";
  export default {
    data () {
      return {
        isConfirmShow:true, // 查看时隐藏确定按钮
        method:'', // 判断新增，查看或变更
        isAdd:true, // 是否为新增页
        visible: false, // 弹框显示状态
        loading: false,
        title:'',  // 弹框显示标题
        isEdit:false,  //  是否可编辑 flase表示可编辑
        formData: {  // 表单数据
          baseCode: '',
          cataName: '',
          cataType: '',
          cataLevel: '',
          cataVersion: '',
          remarks: ''
        },
        initFormData:{  // 初始化表单数据
          baseCode: '',
          cataName: '',
          cataType: '',
          cataLevel: '',
          cataVersion: '',
          remarks: ''
        },
        stateData:[], // 事项类型数据
        cataLevelData:[], //  目录层级数据
        rules: { // 表单验证规则
          baseCode: [
            { required: true, message: '请输入基本编码', trigger: 'blur' }
          ],
          cataName: [
            { required: true, message: '请输入目录名称', trigger: 'blur' }
          ],
          cataType: [
            { required: true, message: '请选择事项类型', trigger: 'change' }
          ],
          cataLevel: [
            { required: true, message: '请选择目录层级', trigger: 'change' }
          ],
          cataVersion: [
            { required: true, message: '请输入版本号', trigger: 'blur' }
          ]
        }
      }
    },
    components: {
      ChDialog
    },
    methods: {
      init(method, obj) {
        this.formData=this.initFormData
        this.isConfirmShow=true
        this.method = method
        if (method === 'add') {
          this.title = '新增'
          this.isAdd=true
          this.isEdit=false
          this.visible = true
        } else if (method === 'change') {
          this.title = '变更'
          this.isAdd=false
          this.isEdit=true
        } else if (method === 'view') {
          this.isConfirmShow=false
          this.title = '查看'
          this.isAdd=false
          this.isEdit=false
        }
        getStateData({dictType:'con_item_type'}).then(({data})=>{
          this.stateData=data.keyAndValue
        }).then(()=>{
          getStateData({dictType:'item_level'}).then(({data})=>{
            this.cataLevelData=data.keyAndValue
          })
        }).then(()=>{
          if (method === 'change' ||method === 'view' ){
            lookCatalogStickData(obj).then(({data}) => {
              if (data.success){
                this.setData(data.data[0])
              }
            })
          }
        })

      },
      // 处理返回数据
      setData(data){
        this.visible = true
        if (this.method==='view'){
          var cataLevel=data.cataLevel
          data.cataLevel=this.cataLevelData[cataLevel]
          this.$nextTick(() => {
            this.formData=data
          })
        }else{
          this.$nextTick(() => {
            this.formData=data
          })
        }
      },
      // 表单提交
      doSubmit() {
        if (this.method === 'add'){
          this.$refs['form'].validate((valid) => {
            if (valid) {
              addCatalogStickData(this.formData).then(({data})=>{
                this.$message.success(data.msg)
                this.visible = false
                this.$emit('refreshDataList')
              })
            } else {
              return false;
            }
          })
        }else if(this.method==='change'){
          this.$refs['form'].validate((valid) => {
            if (valid) {
              editCatalogStickData(this.formData).then(({data})=>{
                this.$message.success(data.msg)
                this.visible = false
                this.$emit('refreshDataList')
              })
            } else {
              return false;
            }
          })
        }

      },
      addDialogClosed(){
        // 关闭弹框，初始化表单数据
        this.$refs.form .resetFields()
      }
    },
    mounted() {
      // this.resetForm('form')
    }
  }
</script>
<style scoped>
   .el-input[readonly]{
     outline: none;
   }
</style>
