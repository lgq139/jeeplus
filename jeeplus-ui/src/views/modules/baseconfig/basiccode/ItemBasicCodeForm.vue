<template>
  <ch-dialog
    :title="title + '四级四同目录'"
    :visible.sync="visible"
    width="1080px"
    @submit="save"
    @close="close"
  >
    <el-form ref="form" :model="form" :rules="rules" label-width="160px" size="small">
      <el-row :gutter="10">
        <el-col :span="12">
          <el-form-item label="事项目录名称" prop="name">
            <el-input v-model="form.name" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="事项目录编码" prop="code">
            <el-input v-model="form.code" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="10">
        <el-col :span="12">
          <el-form-item label="业务指导部门" prop="superviseOrgName">
            <el-input v-model="form.superviseOrgName" />
<!--        <ch-office-select dialog-title="业务指导部门" v-model="form.formId" placeholder="请选业务指导部门" clearable />-->
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="10">
        <el-col :span="12">
          <el-form-item label="事项类型" prop="type">
            <el-select v-model="form.type" clearable style="width: 100%;">
              <el-option
                v-for="(item, index) in this.$dictUtils.getDictList('item_type')"
                :key="index"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="事项表单" prop="formObject">
            <ch-form-select v-model="form.formObject"></ch-form-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="10">
        <el-col :span="24">
          <el-form-item label="行驶层级" prop="approvalLevelList">
            <el-checkbox-group v-model="form.approvalLevelList">
              <el-checkbox
                v-for="(item, index) in this.$dictUtils.getDictList('item_approve_level')"
                :key="index"
                :label="item.value"
              >{{ item.label }}</el-checkbox>
            </el-checkbox-group>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="10">
        <el-col :span="12">
          <el-form-item label="所属阶段" prop="stageCode">
            <el-select v-model="form.stageCode" clearable style="width: 100%;">
              <el-option
                v-for="(item, index) in this.$dictUtils.getDictList('basic_item_stage')"
                :key="index"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="10">
        <el-col :span="24">
          <el-form-item label="备注">
            <el-input v-model="form.remarks" type="textarea" />
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
  </ch-dialog>
</template>

<script>
import ChDialog from "@/components/ChDialog";
import {saveItemBasicCode} from "@/api/modules/baseconfig/basiccode";
import ChOfficeSelect from "@/components/ChOfficeSelect";
import ChFormSelect from "@/components/ChFormSelect";
export default {
  name: "ItemBasicCodeForm",
  components: {ChFormSelect, ChOfficeSelect, ChDialog},
  data() {
    return {
      title: '',
      visible: false,
      form: {
        id: undefined,
        name: undefined,
        code: undefined,
        superviseOrgName: undefined,
        approvalLevel: undefined,
        approvalLevelList: [],
        type: undefined,
        remarks: undefined,
        formObject: undefined,
        formId: undefined,
        formName: undefined,
        stageCode: undefined,
      },
      rules: {
        name: [
          {required: true, message: '事项目录名称不能为空', trigger: 'blur'}
        ],
        code: [
          {required: true, message: '事项目录编码不能为空', trigger: 'blur'}
        ],
        superviseOrgName: [
          {required: true, message: '业务指导部门不能为空', trigger: 'blur'}
        ],
        approvalLevelList: [
          {required: true, message: '行驶层级为必须项', trigger: 'change'}
        ],
        type: [
          {required: true, message: '事项类型为必选项', trigger: 'change'}
        ],
        stageCode: [
          {required: true, message: '归属阶段为必选项', trigger: 'change'}
        ],
        formObject: [
          {required: true, message: '事项表单为必选项', trigger: 'change'}
        ]
      },
    }
  },
  methods: {
    showEdit(action, row) {
      if (action === 'add') {
        this.title = '添加'
      } else if (action === 'edit') {
        this.title = '编辑'
        Object.keys(this.form).forEach(key => {
          if (row[key]) {
            this.form[key] = row[key]
          }
        })
        if (row.formId && row.formName) {
          this.form.formObject = {formId: row.formId, formName: row.formName}
        }
        this.form.approvalLevelList = row['approvalLevel'].split(',')
      }
      this.visible = true
    },
    close() {
      this.$refs.form.resetFields()
      this.form = this.$options.data().form
    },
    save() {
      this.$refs.form.validate(valid => {
        if (valid) {
          if (this.form.approvalLevelList && this.form.approvalLevelList.length > 0) {
            this.form.approvalLevel = this.form.approvalLevelList.sort().join(',')
          }
          if (this.form.formObject && this.form.formObject.formId && this.form.formObject.formName) {
            this.form.formId = this.form.formObject.formId
            this.form.formName = this.form.formObject.formName
          }
          saveItemBasicCode(this.form).then(response => {
            if (response.data.success) {
              this.$message.success(response.data.msg)
              this.$emit('submit-success')
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
