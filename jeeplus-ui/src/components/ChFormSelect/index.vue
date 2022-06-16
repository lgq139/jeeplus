<!--
  表单选择组件
-->
<template>
  <div>
    <el-input v-bind="$attrs" v-model="inputValue" placeholder="请选择事项表单" readonly clearable>
      <template slot="append">
        <el-button icon="el-icon-search" @click="handleOpenSelect">选择</el-button>
        <el-divider direction="vertical" />
        <el-button icon="el-icon-close" @click="$emit('select', '')">重置</el-button>
      </template>
    </el-input>
    <ch-dialog
      title="选择事项表单"
      :visible.sync="visible"
      width="960px"
      @submit="doSubmit"
    >
      <el-row :gutter="10">
        <el-col :span="24">
          <el-form inline size="small" @keyup.enter.native="refreshList()" @submit.native.prevent>
            <el-form-item>
              <el-input v-model="formName" placeholder="表单名称"></el-input>
            </el-form-item>
            <el-button type="primary" size="small" @click="refreshList">查 询</el-button>
            <el-button type="primary" size="small" @click="reset">重 置</el-button>
          </el-form>
          <vxe-table
            :data="tableData"
            v-loading="loading"
            size="small"
            class="table"
            ref="xTable"
          >
            <vxe-column type="radio" width="60" align="center"></vxe-column>
            <vxe-column field="form_name" title="表单名称"></vxe-column>
            <vxe-column field="form_id" title="表单ID"></vxe-column>
            <vxe-column title="操作" width="100" align="center">
              <template slot-scope="scope">
                <el-button type="text" size="small" @click="handlePreview(scope.row)">预览</el-button>
              </template>
            </vxe-column>
          </vxe-table>
          <ch-pagination
            v-show="total>0"
            :total="total"
            :page.sync="pageNo"
            :limit.sync="pageSize"
            @pagination="refreshList"
          />
        </el-col>
      </el-row>
      <ch-form-preview ref="chFormPreview" />
    </ch-dialog>
  </div>
</template>

<script>
import ChDialog from "@/components/ChDialog";
import {getFormListPaginate} from "@/api/modules/form";
import ChPagination from "@/components/ChPagination";
import ChFormPreview from "@/components/ChFormSelect/ChFormPreview";
import emitter from 'element-ui/src/mixins/emitter';
export default {
  name: "ChFormSelect",
  components: {ChFormPreview, ChPagination, ChDialog},
  model: {
    prop: 'value',
    event: 'select'
  },

  mixins: [emitter],

  inject: {
    elForm: {
      default: ''
    },

    elFormItem: {
      default: ''
    }
  },

  props: {
    value: {
      type: String | Object
    }
  },
  data() {
    return {
      visible: false,
      loading: false,
      tableData: [],
      pageNo: 1,
      pageSize: 20,
      total: 0,
      formName: undefined
    }
  },
  computed: {
    inputValue: {
      get() {
        if (this.value) {
          return this.value['formName']
        }
        return ''
      },
      set(val) {
        //
      }
    }
  },
  methods: {
    handleOpenSelect() {
      this.formName = ''
      this.refreshList()
      this.visible = true
    },

    refreshList() {
      this.loading = true
      getFormListPaginate({
        Page: this.pageNo,
        Size: this.pageSize,
        isOnlyReleased: true,
        formName: this.formName
      }).then(({data}) => {
        this.loading = false
        if (data && data.code && data.data['State'] === 200) {
          this.total = data.data.Total
          this.tableData = data.data.rows
        }
      })
    },

    handlePreview(row) {
      this.$refs.chFormPreview.showEdit(row['form_id'])
    },

    reset() {
      this.pageNo = 1
      this.pageSize = 20
      this.formName = ''
      this.refreshList()
    },

    doSubmit() {
      const records = this.$refs.xTable.getRadioRecord()
      if (records) {
        const data = {formId: records['form_id'], formName: records['form_name']}
        this.$emit('select', data)
        this.dispatch('ElFormItem', 'el.form.change', data)
        this.visible = false
        return
      }
      this.$message.warning('请选择需绑定的表单')
    }
  }
}
</script>

<style scoped>

</style>
