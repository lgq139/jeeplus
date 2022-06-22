<template>
  <div class="add-shade">
    <ch-dialog
      :title="title"
      :visible.sync="visible"
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
              <el-option label="查询事项" value="1"></el-option>
              <el-option label="咨询事项" value="2"></el-option>
              <el-option label="证明事项" value="3"></el-option>
              <el-option label="便民服务事项" value="4"></el-option>
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
          <el-form-item label="基本编码" prop="baseCode">
            <el-input v-model="formData.baseCode" placeholder="请输入基本编码" :readonly="true"></el-input>
          </el-form-item>
          <el-form-item label="目录名称" prop="cataName">
            <el-input v-model="formData.cataName" placeholder="请输入目录名称"></el-input>
          </el-form-item>
          <el-form-item label="事项类型" prop="cataType">
            <el-select v-model="formData.cataType" placeholder="请选择事项类型" :disabled="true">
              <el-option label="查询事项" value="search"></el-option>
              <el-option label="咨询事项" value="refer"></el-option>
              <el-option label="证明事项" value="prove"></el-option>
              <el-option label="便民服务事项" value="convenient"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="目录层级" prop="cataLevel">
            <el-radio-group v-model="formData.cataLevel">
              <el-radio label="1">国家级</el-radio>
              <el-radio label="2">省级</el-radio>
              <el-radio label="3">市级</el-radio>
              <el-radio label="4">县级</el-radio>
              <el-radio label="5">镇(乡、街道)级</el-radio>
              <el-radio label="6">村(社区)级</el-radio>
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
            <el-select v-model="formData.cataType" :disabled="true">
              <el-option label="查询事项" value="search"></el-option>
              <el-option label="咨询事项" value="refer"></el-option>
              <el-option label="证明事项" value="prove"></el-option>
              <el-option label="便民服务事项" value="convenient"></el-option>
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
  import {addCatalogStickData,editCatalogStickData,lookCatalogStickData} from "@/api/modules/catalog/catalogStick";
  // 便民目录新增、查看、变更
  import ChDialog from "@/components/ChDialog";
  export default {
    data () {
      return {
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
        },
        cataLevelData:[{
          value:'1',
          label:'国家级'
        },
          {
            value:'2',
            label:'省级'
          },
          {
            value:'3',
            label:'市级'
          },
          {
            value:'4',
            label:'县级'
          },
          {
            value:'5',
            label:'镇(乡、街道)级'
          },
          {
            value:'6',
            label:'村(社区)级'
          }
        ]
      }
    },
    components: {
      ChDialog
    },
    methods: {
      init(method, obj) {
        console.log(method)
        console.log(obj)
        this.method = method
        // this.inputForm.id = obj.id
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
          this.title = '查看'
          this.isAdd=false
          this.isEdit=false
        }
        if (method === 'change' ||method === 'view' ){
          lookCatalogStickData(obj).then(({data}) => {
            if (data.success){
              this.setData(data.data[0])
            }
          })
        }
      },
      // 处理返回数据
      setData(data){
        if (this.method==='view'){
          var cataLevel=data.cataLevel
          switch(cataLevel)
          {
            case '1':
              data.cataLevel='国家级'
              break;
            case '2':
              data.cataLevel='省级'
              break;
            case '3':
              data.cataLevel='市级'
              break;
            case '4':
              data.cataLevel='县级'
              break;
            case '5':
              data.cataLevel='镇(乡、街道)级'
              break;
            case '6':
              data.cataLevel='村(社区)级'
              break;
            case '7':
            default:
              break;
          }
          this.formData=data

        }else{
          this.formData=data
        }
        this.visible = true
      },
      // 表单提交
      doSubmit() {
        this.$refs['form'].validate((valid) => {
          console.log(valid)
          if (valid) {
            addCatalogStickData(this.formData).then((data)=>{
              console.log(data);
            })

          } else {
            console.log('error submit!!');
            return false;
          }
        });
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
