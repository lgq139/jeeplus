<template>
  <div>
    <vxe-toolbar :refresh="{query: refreshList}" custom>
      <template #buttons>
        <el-button
          v-if="hasPermission('sys:areaAuth:edit')"
          type="success"
          size="small"
          icon="el-icon-refresh"
          @click="refreshCache"
          plain>
          同步区划数据</el-button>
      </template>
    </vxe-toolbar>
    <vxe-table
      resizable
      row-id="id"
      v-loading="loading"
      :tree-config="{children: 'children', expandRowKeys: ['140000']}"
      :row-config="{keyField: 'id'}"
      :data="dataList"
      :height="tableViewHeight"
      show-overflow="tooltip"
      size="medium"
    >
      <vxe-column field="name" title="区划名称" tree-node width="300"></vxe-column>
      <vxe-column field="code" title="标准区划编码"></vxe-column>
      <vxe-column field="sortCode" title="一体化平台区划编码"></vxe-column>
      <vxe-column align="center" width="150" title="授权创建场景">
        <template slot-scope="scope">
          <el-switch
            v-model="scope.row.authCatalog"
            @change="statusChange(scope.row, 'authCatalog')"
            active-color="#13ce66"
            inactive-color="#ff4949"
            active-value="1"
            inactive-value="0">
          </el-switch>
        </template>
      </vxe-column>
      <vxe-column align="center" width="150" title="授权发布场景">
        <template slot-scope="scope">
          <el-switch
            v-model="scope.row.authDeploy"
            @change="statusChange(scope.row, 'authDeploy')"
            active-color="#13ce66"
            inactive-color="#ff4949"
            active-value="1"
            inactive-value="0">
          </el-switch>
        </template>
      </vxe-column>
      <vxe-column title="操作" width="80" align="center">
        <template #default="{ row }">
          <el-button v-if="hasPermission('sys:areaAuth:edit')" type="text" size="small" @click="edit(row)">修改</el-button>
<!--          <el-divider direction="vertical" />-->
        </template>
      </vxe-column>
    </vxe-table>
  </div>
</template>

<script>
import {listAreaAuth, refreshAreaAuth, saveAreaAuth} from "@/api/modules/sys/area";

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
      listAreaAuth().then(({data}) => {
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
    statusChange(row, field) {
      this.loading = true
      saveAreaAuth({id: row.id, [field]: row[field]})
        .then(({data}) => {
          if (data && data.success) {
            this.loading = false
            this.$message({message: data.msg, type: 'success', duration: 1500})
            // this.refreshList()
          }
        })
    },
    refreshCache() {
      this.$confirm(`确定刷新当前区划数据吗?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.loading = true
        refreshAreaAuth().then(({data}) => {
          this.loading = false
          if (data && data.success) {
            this.$message.success('数据刷新成功')
          }
        })
      })
    },
  }
}
</script>

<style scoped>

</style>
