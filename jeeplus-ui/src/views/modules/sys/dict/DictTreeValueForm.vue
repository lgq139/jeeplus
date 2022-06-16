<template>
  <ch-dialog
    :title="title + '字典项'"
    :visible.sync="visible"
    width="700px"
    @close="close"
    @submit="doSubmit"
  >
    <el-form ref="inputForm" :model="inputForm" :rules="dataRule" v-loading="loading" label-width="80px" size="small">
      <el-form-item label="上级字典" v-if="layout === '2'">
        <el-tree-select
          size="small"
          ref="dictParentTree"
          :data="treeList"
          :value="inputForm.parentId"
          :clearable="true"
          :accordion="true"
          @getValue="(value) => {inputForm.parentId = value}"/>
      </el-form-item>
      <el-form-item label="标签" prop="label">
        <el-input v-model="inputForm.label" placeholder="标签"></el-input>
      </el-form-item>
      <el-form-item label="键值" prop="value">
        <el-input v-model="inputForm.value" placeholder="键值"></el-input>
      </el-form-item>
      <el-form-item label="排序号" prop="sort">
        <el-input-number :step="10" v-model="inputForm.sort" placeholder="排序号"></el-input-number>
      </el-form-item>
    </el-form>
  </ch-dialog>
</template>

<script>
import ChDialog from "@/components/ChDialog";
import {saveDictValue} from "@/api/modules/sys/dict";
import ElTreeSelect from "@/components/treeSelect/treeSelect";
export default {
  name: 'DictTreeValueForm',
  components: {ElTreeSelect, ChDialog},
  data () {
    return {
      title: '',
      visible: false,
      loading: false,
      inputForm: {
        id: undefined,
        parentId: undefined,
        parentIds: undefined,
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
      },
      treeList: [],
      layout: '1',
    }
  },
  methods: {

    /**
     *
     * @param action 操作类型
     * @param treeList 列表数据
     * @param obj 当前行数据
     * @param dictType 字典类型对象
     */
    showEdit(action, treeList, obj, dictType) {
      this.treeList = treeList
      this.layout = dictType.layout
      if (action === 'add') {
        this.title = '新增'
        this.inputForm.dictType = dictType.type
        this.inputForm.dictTypeId = dictType.id
        this.inputForm.parentId = undefined
      } else if (action === 'addSub') {
        this.title = '新增'
        this.inputForm.dictType = obj.dictType
        this.inputForm.dictTypeId = obj.dictTypeId
        this.$nextTick(() => {
          //this.treeList = treeList
          this.inputForm.parentId = obj.id
        })
      }
      else if (action === 'edit') {
        this.title = '修改'
        Object.keys(this.inputForm).forEach(key => this.inputForm[key] = obj[key])
        if (obj.parentId === '0') {
          this.inputForm.parentId = undefined
        }
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
          if (!this.inputForm.parentId) {
            this.inputForm.parentId = '0'
          }
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
