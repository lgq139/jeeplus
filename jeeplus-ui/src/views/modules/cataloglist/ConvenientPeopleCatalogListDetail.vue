<template>
  <div class="add-shade">
    <ch-dialog
      title="查看"
      :visible.sync="visible"
      :isConfirmShow="false"
      v-if="visible"
    >
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
    </ch-dialog>

  </div>
</template>

<script>
  import {getStateData} from "@/api/modules/catalog/dict";
  import {lookCatalogListData} from "@/api/modules/catalog/catalogList";
  // 便民目录新增、查看、变更
  import ChDialog from "@/components/ChDialog";
  export default {
    data () {
      return {
        visible: false, // 弹框显示状态
        loading: false,
        formData: {  // 表单数据
          baseCode: '',
          cataName: '',
          cataType: '',
          cataLevel: '',
          cataVersion: '',
          remarks: ''
        },
        stateData:[], // 事项类型数据
        cataLevelData:[] //  目录层级数据

      }
    },
    components: {
      ChDialog
    },
    methods: {
      init(method, obj) {
        getStateData({dictType:'con_item_type'}).then(({data})=>{
          this.stateData=data.keyAndValue
        }).then(()=>{
          getStateData({dictType:'item_level'}).then(({data})=>{
            this.cataLevelData=data.keyAndValue
          })
        }).then(()=>{
          if (method === 'change' ||method === 'view' ){
            lookCatalogListData(obj).then(({data}) => {
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
        var cataLevel=data.cataLevel
        data.cataLevel=this.cataLevelData[cataLevel]
        this.formData=data
      }
    },

  }
</script>
<style scoped>

</style>
