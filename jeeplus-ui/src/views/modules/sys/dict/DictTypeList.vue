<template>
  <div>
    <el-form :inline="true" v-show="isSearchCollapse" class="query-form" ref="searchForm" :model="searchForm"
             @keyup.enter.native="refreshList()" @submit.native.prevent>
      <el-form-item prop="type">
        <el-input size="small" v-model="searchForm.type" placeholder="字典类型" clearable></el-input>
      </el-form-item>
      <el-form-item prop="description">
        <el-input size="small" v-model="searchForm.description" placeholder="字典描述" clearable></el-input>
      </el-form-item>
      <el-form-item prop="dictGroup">
        <el-select size="small" v-model="searchForm.dictGroup" placeholder="字典分组" clearable>
          <el-option v-for="(item, index) in this.$dictUtils.getDictList('sys_dict_group')" :key="index" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item prop="layout">
        <el-select size="small" v-model="searchForm.layout" placeholder="布局类型" clearable>
          <el-option v-for="(item, index) in this.$dictUtils.getDictList('sys_dict_type')" :key="index" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item prop="open">
        <el-select size="small" v-model="searchForm.open" placeholder="是否对外开饭" clearable>
          <el-option v-for="(item, index) in this.$dictUtils.getDictList('sys_open')" :key="index" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="refreshList()" size="small">查询</el-button>
        <el-button @click="resetSearch()" size="small">重置</el-button>
      </el-form-item>
    </el-form>
    <vxe-toolbar :refresh="{query: refreshList}" custom>
      <template #buttons>
        <el-button v-if="hasPermission('sys:dict:add')" type="primary" size="small" icon="el-icon-plus" @click="add()">新建</el-button>
        <el-button v-if="hasPermission('sys:dict:edit')" type="warning" size="small" icon="el-icon-edit-outline" @click="edit()"
                   :disabled="dataListSelections.length !== 1" plain>修改
        </el-button>
        <el-button v-if="hasPermission('sys:dict:del')" type="danger" size="small" icon="el-icon-delete" @click="del()"
                   :disabled="dataListSelections.length <= 0" plain>删除
        </el-button>
        <el-button type="success" size="small" icon="el-icon-refresh" @click="refreshDictCache" plain>刷新缓存</el-button>
      </template>
      <template #tools>
        <vxe-button
          title="筛选查询"
          icon="vxe-icon--search"
          circle
          @click="isSearchCollapse = !isSearchCollapse"
          style="margin-right: 12px;"
        />
      </template>
    </vxe-toolbar>
    <vxe-table
      v-loading="loading"
      :data="dataList"
      :height="tableViewHeight + 'px'"
      show-overflow="tooltip"
      size="medium"
      @checkbox-change="selectionChangeDictHandle"
      @checkbox-all="selectionChangeDictHandle"
    >
      <vxe-column type="checkbox" align="center" width="50" />
      <vxe-column field="type" title="类型">
        <template slot-scope="scope">
          <el-link type="primary" :underline="false">{{scope.row.type}}</el-link>
        </template>
      </vxe-column>
      <vxe-column field="description" title="描述" />
      <vxe-column field="dictGroup" title="分组" :formatter="formatterGroup" />
      <vxe-column field="layout" title="布局类型" :formatter="formatterLayout" />
      <vxe-column field="open" title="开放" :formatter="formatterOpen" />
      <vxe-column fixed="right" title="操作" align="center" width="250">
        <template slot-scope="scope">
          <el-button v-if="hasPermission('sys:dict:edit')" type="text" size="small" @click="edit(scope.row)">修改</el-button>
          <el-divider direction="vertical"></el-divider>
          <el-button v-if="hasPermission('sys:dict:del')" type="text" size="small" @click="del(scope.row.id)">删除</el-button>
          <el-divider direction="vertical"></el-divider>
          <el-button v-if="hasPermission('sys:dict:edit')" type="text" size="small" @click="showRight(scope.row)">管理键值</el-button>
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

    <!-- 弹窗, 新增 / 修改 -->
    <dict-type-form ref="dictTypeForm" @refreshDataList="refreshList" />
    <!-- 树形字典值 -->
    <dict-tree-value-list ref="dictTreeValueList" />

  </div>
</template>

<script>
import DictTypeForm from './DictTypeForm'
import ChPagination from "@/components/ChPagination";
import {deleteDictType, listDictType, refreshCache} from "@/api/modules/sys/dict";
import DictTreeValueList from "@/views/modules/sys/dict/DictTreeValueList";

export default {
  data() {
    return {
      searchForm: {
        type: '',
        description: '',
        layout: '',
        open: '',
        dictGroup: ''
      },
      dataList: [],
      pageNo: 1,
      pageSize: 10,
      total: 0,
      orderBy: '',
      dataListSelections: [],
      isSearchCollapse: false,
      loading: false,
    }
  },
  components: {
    DictTreeValueList,
    ChPagination,
    DictTypeForm
  },
  computed: {
    tableViewHeight () {
      return this.$store.state.common.documentClientHeight - 220
    }
  },
  activated() {
    this.refreshList()
  },
  methods: {
    // 获取数据列表
    refreshList() {
      this.loading = true
      listDictType({
        pageNo: this.pageNo,
        pageSize: this.pageSize,
        orderBy: this.orderBy,
        ...this.searchForm
      }).then(({data}) => {
        if (data && data.success) {
          this.dataList = data.page.records
          this.total = data.page.total
          this.loading = false
        }
      })
    },

    formatterLayout({cellValue, row, column}) {
      return this.$dictUtils.getDictLabel('sys_dict_type', cellValue, '--')
    },

    formatterOpen({cellValue, row, column}) {
      return this.$dictUtils.getDictLabel('sys_open', cellValue, '--')
    },

    formatterGroup({cellValue, row, column}) {
      return this.$dictUtils.getDictLabel('sys_dict_group', cellValue, '--')
    },

    // 多选
    selectionChangeDictHandle({ records }) {
      this.dataListSelections = records
    },
    // 新增
    add() {
      this.$refs.dictTypeForm.showEdit('add', '')
    },
    // 修改
    edit(row) {
      this.$refs.dictTypeForm.showEdit('edit', row)
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
        deleteDictType({ids: ids}).then(({data}) => {
          if (data && data.success) {
            this.$message.success(data.msg)
            this.refreshList()
            this.$dictUtils.refreshDictList()
          }
        })
      })
    },
    resetSearch() {
      this.$refs.searchForm.resetFields()
      this.refreshList()
    },
    // 查看字典值
    showRight(row) {
      this.$refs.dictTreeValueList.showEdit(row)
    },

    refreshDictCache() {
      this.$confirm(`确定刷新当前数据缓存吗?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        refreshCache().then(({data}) => {
          if (data && data.success) {
            this.$message.success(data.msg)
            this.refreshList()
          }
        })
      })
    }
  }
}
</script>
