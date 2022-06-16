<template>
  <div>
    <el-form :inline="true" v-show="isSearchCollapse" class="query-form" ref="searchForm" :model="searchForm"
             @keyup.enter.native="refreshList()" @submit.native.prevent>
      <el-form-item prop="systemName">
        <el-input size="small" v-model="searchForm.systemName" placeholder="客户端名称" clearable></el-input>
      </el-form-item>
      <el-form-item prop="description">
        <el-input size="small" v-model="searchForm.description" placeholder="描述" clearable></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="refreshList()" size="small">查询</el-button>
        <el-button @click="resetSearch()" size="small">重置</el-button>
      </el-form-item>
    </el-form>
    <el-row>
      <el-button v-if="hasPermission('sys:apiclient:add')" type="primary" size="small" icon="el-icon-plus" @click="add()">新建</el-button>
      <el-button v-if="hasPermission('sys:apiclient:del')" type="danger" size="small" icon="el-icon-delete" @click="del()"
                 :disabled="dataListSelections.length <= 0" plain>删除
      </el-button>
      <el-button type="success" size="small" icon="el-icon-refresh" @click="refreshCache()">刷新缓存</el-button>
      <el-button-group class="pull-right">
        <el-tooltip class="item" effect="dark" content="搜索" placement="top">
          <el-button type="default" size="small" icon="el-icon-search" @click="isSearchCollapse = !isSearchCollapse"></el-button>
        </el-tooltip>
        <el-tooltip class="item" effect="dark" content="刷新" placement="top">
          <el-button type="default" size="small" icon="el-icon-refresh" @click="refreshList"></el-button>
        </el-tooltip>
      </el-button-group>
    </el-row>
    <el-table
      :data="dataList"
      v-loading="loading"
      size="medium"
      @selection-change="selectionChangeHandle"
      class="table">
      <el-table-column type="selection" header-align="center" align="center" width="50"></el-table-column>
      <el-table-column prop="type" label="客户端名称">
        <template slot-scope="scope">
          <el-link type="primary" :underline="false" v-if="hasPermission('sys:dict:view')" @click="edit(scope.row)">
            {{scope.row.systemName}}
          </el-link>
          <span v-else>{{scope.row.systemName}}</span>
        </template>
      </el-table-column>
      <el-table-column prop="id" label="客户端编码" />
      <el-table-column prop="secret" label="客户端密钥" />
      <el-table-column prop="description" label="描述" />
      <el-table-column fixed="right" header-align="center" align="center" width="160" label="操作">
        <template slot-scope="scope">
          <el-button v-if="hasPermission('sys:apiclient:edit')" type="text" size="small" @click="edit(scope.row)">修改</el-button>
          <el-divider direction="vertical"></el-divider>
          <el-button v-if="hasPermission('sys:apiclient:del')" type="text" size="small" @click="del(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <api-client-form ref="apiClientForm" @refreshDataList="refreshList"  />
  </div>
</template>

<script>
import ApiClientForm from "@/views/modules/sys/apiclient/ApiClientForm";
import {deleteApiClient, listApiClient, refreshApiClient} from "@/api/modules/sys/apiclient";

export default {
  data() {
    return {
      searchForm: {
        systemName: '',
        description: '',
      },
      dataList: [],
      dataListSelections: [],
      isSearchCollapse: false,
      loading: false,
    }
  },
  components: {
    ApiClientForm
  },
  activated() {
    this.refreshList()
  },
  methods: {

    refreshList() {
      this.loading = true
      listApiClient({
        ...this.searchForm
      }).then(({data}) => {
        if (data && data.success) {
          this.dataList = data.data
          this.loading = false
        }
      })
    },

    // 多选
    selectionChangeHandle(val) {
      this.dataListSelections = val
    },
    // 新增
    add() {
      this.$refs.apiClientForm.showEdit('add')
    },
    // 修改
    edit(row) {
      this.$refs.apiClientForm.showEdit('edit', row)
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
        deleteApiClient({ids: ids}).then(({data}) => {
          if (data && data.success) {
            this.$message.success(data.msg)
            this.refreshList()
            this.$dictUtils.refreshDictList()
          }
        })
      })
    },
    refreshCache() {
      refreshApiClient().then(({data}) => {
        if (data.success) {
          this.$message.success('数据刷新成功')
        }
      })
    },
    resetSearch() {
      this.$refs.searchForm.resetFields()
      this.refreshList()
    },
    // 查看字典值
    showRight(row) {
      if (row.layout === '1') {
        this.$refs.dictValueList.showEdit(row)
      } else if (row.layout === '2') {
        this.$refs.dictTreeValueList.showEdit(row)
      }
    },
  }
}
</script>
