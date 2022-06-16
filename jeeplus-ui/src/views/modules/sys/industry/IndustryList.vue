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
      <el-form-item>
        <el-button type="primary" @click="refreshList()" size="small">查询</el-button>
        <el-button @click="resetSearch()" size="small">重置</el-button>
      </el-form-item>
    </el-form>
    <vxe-toolbar :refresh="{query: refreshList}" custom>
      <template #buttons>
        <el-button v-if="hasPermission('sys:dict:add')" type="primary" size="small" icon="el-icon-plus" @click="add()">新建</el-button>
        <el-button v-if="hasPermission('sys:dict:edit')" type="success" size="small" icon="el-icon-refresh" @click="refreshCache" plain>刷新缓存</el-button>
      </template>
    </vxe-toolbar>
    <vxe-table
      resizable
      row-id="id"
      v-loading="loading"
      :tree-config="{children: 'children'}"
      :data="dataList"
      :height="tableViewHeight"
      show-overflow="tooltip"
      size="medium"
    >
      <vxe-column field="id" title="编码" tree-node width="240"></vxe-column>
      <vxe-column field="name" title="描述"></vxe-column>
      <vxe-column field="remarks" title="释义"></vxe-column>
      <vxe-column title="操作" width="200" align="center">
        <template #default="{ row }">
          <el-button v-if="hasPermission('sys:dict:edit')" type="text" size="small" @click="edit(row)">修改</el-button>
          <el-divider direction="vertical"></el-divider>
          <el-button v-if="hasPermission('sys:dict:del')" type="text" size="small" @click="del(row.id)">删除</el-button>
        </template>
      </vxe-column>
    </vxe-table>
  </div>
</template>

<script>
import {deleteDictType, listIndustry, refreshIndustryCache} from "@/api/modules/sys/dict";

export default {
  data() {
    return {
      searchForm: {
        type: '',
        description: '',
      },
      dataList: [],
      dataListSelections: [],
      isSearchCollapse: false,
      loading: false,

    }
  },
  activated() {
    this.refreshList()
  },
  computed: {
    tableViewHeight () {
      return this.$store.state.common.documentClientHeight - 220
    }
  },
  methods: {
    // 获取数据列表
    refreshList() {
      this.loading = true
      listIndustry().then(({data}) => {
        this.loading = false
        if (data && data.success) {
          this.dataList = data.data
        }
      })
    },
    // 添加
    add() {

    },
    // 修改
    edit(row) {

    },
    // 删除
    del(id) {
      this.$confirm(`确定删除所选项吗?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteDictType({ids: id}).then(({data}) => {
          if (data && data.success) {
            this.$message.success(data.msg)
            this.refreshList()
          }
        })
      })
    },
    refreshCache() {
      this.$confirm(`确定刷新缓存数据吗?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        refreshIndustryCache().then(({data}) => {
          if (data && data.success) {
            this.$message.success(data.msg)
          }
        })
      })
    },
    resetSearch() {
      this.$refs.searchForm.resetFields()
      this.refreshList()
    },
  }
}
</script>
