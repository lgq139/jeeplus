<template>
  <ch-drawer
    :title="title"
    :visible.sync="visible"
    size="960px"
  >
    <el-row>
      <el-button type="primary" size="small" icon="el-icon-plus" @click="add()">新建</el-button>
      <el-button v-if="hasPermission('sys:dict:del')" type="danger" size="small" icon="el-icon-delete" @click="del()"
                 :disabled="dataListSelections.length <= 0">删除
      </el-button>
    </el-row>
    <vxe-table
      ref="xTable"
      :data="dataList"
      v-loading="loading"
      size="small"
      class="table"
      border
      :tree-config="{}"
      @checkbox-change="selectionChangeDictHandle"
      @checkbox-all="selectionChangeDictHandle"
    >
      <vxe-column type="checkbox" width="60" align="center" />
      <vxe-column field="label" title="标签" tree-node>
        <template slot-scope="scope">
          <el-link type="primary" :underline="false">{{scope.row.label}}</el-link>
        </template>
      </vxe-column>
      <vxe-column field="value" show-overflow-tooltip title="键值"></vxe-column>
      <vxe-column field="value2" show-overflow-tooltip title="键值（工建）"></vxe-column>
      <vxe-column field="value3" show-overflow-tooltip title="键值（发改）"></vxe-column>
      <vxe-column field="sort" title="排序" width="120" align="center"></vxe-column>
      <vxe-column fixed="right" align="center" width="160" title="操作">
        <template slot-scope="scope">
          <el-button v-if="hasPermission('sys:dict:edit')" type="text" size="small" @click="edit(scope.row)">修改</el-button>
<!--          <el-divider direction="vertical"></el-divider>-->
<!--          <el-button v-if="hasPermission('sys:dict:del')" type="text" size="small" @click="del(scope.row.id)">删除</el-button>-->
          <el-divider direction="vertical" v-if="layout === '2'"></el-divider>
          <el-button v-if="hasPermission('sys:dict:del') && layout === '2'" type="text" size="small" @click="addSub(scope.row)">添加下级字典</el-button>
        </template>
      </vxe-column>
    </vxe-table>
    <!-- 弹窗, 新增 / 修改 -->
    <dict-tree-value-form ref="dictTreeValueForm" @refreshDataList="refreshList" />
  </ch-drawer>
</template>

<script>
import ChDrawer from "@/components/ChDrawer";
import {deleteDictValue, listDictValue} from "@/api/modules/sys/dict";
import DictTreeValueForm from "@/views/modules/sys/dict/DictTreeValueForm";
export default {
  name: "DictTreeValueList",
  components: {DictTreeValueForm, ChDrawer},
  data() {
    return {
      title: '',
      visible: false,
      loading: false,
      dataList: [],
      dataListSelections: [],
      dictType: '',
      dictTypeId: '',
      layout: '1',
    }
  },
  methods: {
    showEdit(dictType) {
      if (dictType) {
        this.title = `数据字典值列表，所属类型: ${dictType.description}（${dictType.type}）`
        this.dictType = dictType
        this.dictTypeId = dictType.id
        this.layout = dictType.layout
        this.dataListSelections = []
        this.refreshList()
        this.visible = true
      }
    },
    // 获取数据列表
    refreshList() {
      this.loading = true
      listDictValue({dictTypeId: this.dictTypeId}).then(({data}) => {
        this.loading = false
        if (data && data.success) {
          this.dataList = data.data
          this.$nextTick(() => {
            this.$refs.xTable.setAllTreeExpand(true)
          })
        }
      })
    },
    // 多选
    selectionChangeDictHandle({ records }) {
      this.dataListSelections = records
    },
    add() {
      this.$refs.dictTreeValueForm.showEdit('add', this.dataList, '', this.dictType)
    },
    addSub(row) {
      this.$refs.dictTreeValueForm.showEdit('addSub', this.dataList, row, this.dictType)
    },
    edit(row) {
      this.$refs.dictTreeValueForm.showEdit('edit', this.dataList, row, this.dictType)
    },
    del(id) {
      let ids = id || this.dataListSelections.map(item => {
        return item.id
      }).join(',')
      this.$confirm(`确定删除所选项吗?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteDictValue({ids: ids}).then(({data}) => {
          if (data && data.success) {
            this.$message.success(data.msg)
            this.refreshList()
            this.$dictUtils.refreshDictList()
          }
        })
      })
    }
  }
}
</script>

<style scoped>

</style>
