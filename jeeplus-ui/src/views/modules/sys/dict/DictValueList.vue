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
    <el-table
      :data="dataList"
      v-loading="loading"
      border
      size="medium"
      @selection-change="selectionChangeDictHandle"
      class="table">
      <el-table-column type="selection" header-align="center" align="center" width="50"></el-table-column>
      <el-table-column prop="label" label="标签">
        <template slot-scope="scope">
          <el-link type="primary" :underline="false" v-if="hasPermission('sys:dict:edit')" @click="edit(scope.row)">
            {{scope.row.label}}
          </el-link>
          <span v-else>{{scope.row.label}}</span>
        </template>
      </el-table-column>
      <el-table-column prop="value" show-overflow-tooltip label="键值"></el-table-column>
      <el-table-column prop="sort" label="排序"></el-table-column>
      <el-table-column fixed="right" header-align="center" align="center" width="150" label="操作">
        <template slot-scope="scope">
          <el-button v-if="hasPermission('sys:dict:edit')" type="text" size="small" @click="edit(scope.row)">修改</el-button>
          <el-divider direction="vertical"></el-divider>
          <el-button v-if="hasPermission('sys:dict:del')" type="text" size="small" @click="del(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 弹窗, 新增 / 修改 -->
    <dict-value-form ref="dictValueForm" @refreshDataList="refreshList" />
  </ch-drawer>

</template>

<script>
import DictValueForm from './DictValueForm'
import ChDrawer from "@/components/ChDrawer";
import {deleteDictValue, listDictValue} from "@/api/modules/sys/dict";

export default {
  data() {
    return {
      title: '',
      visible: false,
      dataList: [],
      dictType: '',
      dictTypeId: '',
      dataListSelections: [],
      loading: false
    }
  },
  components: {
    ChDrawer,
    DictValueForm
  },
  methods: {
    showEdit(dictType) {
      if (dictType) {
        this.title = `数据字典值列表，所属类型: ${dictType.description}（${dictType.type}）`
        this.dictType = dictType
        this.dictTypeId = dictType.id
        this.refreshList()
        this.visible = true
      }
    },
    // 获取数据列表
    refreshList() {
      this.loading = true
      listDictValue({dictTypeId: this.dictTypeId}).then(({data}) => {
        if (data && data.success) {
          this.dataList = data.data
          this.loading = false
        }
      })
    },
    // 多选
    selectionChangeDictHandle(val) {
      this.dataListSelections = val
    },
    // 新增
    add() {
      this.$refs.dictValueForm.showEdit('add', {
        dictType: this.dictType.type,
        dictTypeId: this.dictType.id
      })
    },
    // 修改
    edit(row) {
      this.$refs.dictValueForm.showEdit('edit', row)
    },
    // 删除
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
