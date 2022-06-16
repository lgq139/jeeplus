<template>
  <el-dialog
    :title="title"
    :close-on-click-modal="false"
     v-dialogDrag
    :visible.sync="visible">
    <el-form :model="inputForm" :rules="dataRule" ref="inputForm" @keyup.enter.native="doSubmit()"
             label-width="80px" v-loading="loading" :class="method==='view'?'readonly':''" :disabled="method==='view'" @submit.native.prevent>
      <el-row :gutter="15">
        <el-col :span="12">
          <el-form-item label="上级机构" prop="parent.id">
           <SelectTree 
             ref="officeTree"
             :props="{
                value: 'id',             // ID字段名
                label: 'name',         // 显示名称
                children: 'children'    // 子级字段名
              }"
            :url="`/sys/office/treeData?extId=${inputForm.id}`"
            :value="inputForm.parent.id"
            :clearable="true" 
            :accordion="true"
            v-if="visible"
            @getValue="(value) => {inputForm.parent.id=value}"/>
          </el-form-item>
        </el-col>
         <el-col :span="12">
          <el-form-item label="归属区域" prop="area.id">
               <SelectTree 
                ref="areaTree"
                :props="{
                    value: 'id',             // ID字段名
                    label: 'name',         // 显示名称
                    children: 'children'    // 子级字段名
                  }"
                url="/sys/area/treeData"
                :value="inputForm.area.id"
                :clearable="true"
                :accordion="true"
                v-if="visible"
                @getValue="(value) => {inputForm.area.id=value}"/>

          </el-form-item>
          
        </el-col>
        <el-col :span="12">
          <el-form-item label="机构名称" prop="name">
            <el-input v-model="inputForm.name" placeholder="机构名称"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="机构编码" prop="code">
            <el-input v-model="inputForm.code" placeholder="机构编码"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="机构类型" prop="type">
            <el-select v-model="inputForm.type" placeholder="请选择"  style="width: 100%;">
                <el-option
                  v-for="item in this.$dictUtils.getDictList('sys_office_type')"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value">
                </el-option>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="机构等级" prop="grade">
             <el-select v-model="inputForm.grade" placeholder="请选择"  style="width: 100%;">
                <el-option
                  v-for="item in this.$dictUtils.getDictList('sys_office_grade')"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value">
                </el-option>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="是否可用" prop="useable">
            <el-select v-model="inputForm.useable" placeholder="请选择"  style="width: 100%;">
                <el-option
                  v-for="item in this.$dictUtils.getDictList('yes_no')"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value">
                </el-option>
            </el-select>
          </el-form-item>
        </el-col>
         <el-col :span="12">
          <el-form-item label="排序号" prop="sort">
            <el-input-number style="width:100%" :step="30" v-model="inputForm.sort"></el-input-number>
          </el-form-item>
         </el-col>
      </el-row>
   
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="visible = false">关闭</el-button>
      <el-button v-if="method != 'view'" type="primary" @click="doSubmit()" v-noMoreClick>确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
  import SelectTree from '@/components/treeSelect/treeSelect.vue'
  export default {
    data () {
      return {
        title: '',
        method: '',
        officeParentTreeData: [],
        areaTreeData: [],
        visible: false,
        loading: false,
        inputForm: {
          id: '0',
          name: '',
          parent: {
            id: ''
          },
          sort: '30',
          area: {
            id: '',
            name: '111'
          }, // 归属区域
          code: '', // 机构编码
          type: '', // 机构类型（1：公司；2：部门；3：小组）
          grade: '', // 机构等级（1：一级；2：二级；3：三级；4：四级）
          address: '', // 联系地址
          zipCode: '', // 邮政编码
          master: '', // 负责人
          phone: '', // 电话
          fax: '', // 传真
          email: '', // 邮箱
          useable: '', // 是否可用
          primaryPerson: {
            id: '',
            name: ''
          }, // 主负责人
          deputyPerson: {
            id: '',
            name: ''
          }// 副负责人
        },
        dataRule: {
          name: [
            {required: true, message: '名称不能为空', trigger: 'blur'}
          ],
          grade: [
            {required: true, message: '机构等级不能为空', trigger: 'blur'}
          ],
          'type': [
            {required: true, message: '机构类型不能为空', trigger: 'blur'}
          ],
          code: [
            {max: 50, message: '最大长度不能超过50个字符', trigger: 'blur'}
          ],
          useable: [
             {required: true, message: '是否可用不能为空', trigger: 'blur'}
          ]
        }
      }
    },
    components: {
      SelectTree
    },
    methods: {
      init (method, obj) {
        this.inputForm.id = obj.id
        this.method = method
        if (method === 'add') {
          this.title = '新增机构'
        } else if (method === 'addChild') {
          this.title = '添加下级机构'
        } else if (method === 'edit') {
          this.title = '修改机构'
        } else if (method === 'view') {
          this.title = '查看机构'
        }
  
        this.visible = true
        this.loading = false
        this.$nextTick(() => {
          this.$refs['inputForm'].resetFields()
          this.inputForm.parent.id = obj.parent.id
          if (method === 'edit' || method === 'view') { // 修改或者查看
            this.loading = true
            this.$http({
              url: `/sys/office/queryById?id=${this.inputForm.id}`,
              method: 'get'
            }).then(({data}) => {
              this.inputForm = this.recover(this.inputForm, data.office)
              this.loading = false
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
              url: `/sys/office/save`,
              method: 'post',
              data: this.inputForm
            }).then(({data}) => {
              this.loading = false
              if (data && data.success) {
                this.$message({
                  message: '操作成功',
                  type: 'success',
                  duration: 1500
                })
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
