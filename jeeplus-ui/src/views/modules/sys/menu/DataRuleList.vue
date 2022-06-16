<template>
  <div style="padding:10px">
    <el-row style="margin-bottom:10px">
      <el-button type="primary" size="small" @click="add()">新建</el-button>
    </el-row>
    <el-table
      :data="rows"
      border
      v-loading="loading"
      class="table"
      style="width: 100%;">
      <el-table-column
        type="selection"
        header-align="center"
        align="center"
        width="50">
      </el-table-column>
      <el-table-column
        prop="name"
        header-align="center"
        align="center"
        width="120px"
        label="数据规则名称">
      </el-table-column>
      <el-table-column
        prop="className"
        header-align="center"
        align="center"
        label="实体类名">
      </el-table-column>
      <el-table-column
        show-overflow-tooltip
        prop="field"
        header-align="center"
        align="center"
        label="规则字段">
      </el-table-column>
      <el-table-column
        show-overflow-tooltip
        prop="express"
        header-align="center"
        align="center"
        label="规则条件">
        <template slot-scope="scope">
          {{$dictUtils.getDictLabel("t_express", scope.row.express, "")}}
        </template>
      </el-table-column>
      <el-table-column
        show-overflow-tooltip
        prop="value"
        header-align="center"
        align="center"
        label="规则值">
      </el-table-column>
      <el-table-column
        show-overflow-tooltip
        prop="sqlSegment"
        header-align="center"
        align="center"
        width="100px"
        label="自定义sql">
      </el-table-column>
      <el-table-column
        show-overflow-tooltip
        prop="remarks"
        header-align="center"
        align="center"
        label="备注信息">
      </el-table-column>
      <el-table-column
        fixed="right"
        header-align="center"
        align="center"
        width="150"
        label="操作">
        <template slot-scope="scope">
          <el-button  size="mini"
                    @click="edit(scope.row.id)">修改
          </el-button>
          <el-button type="danger" size="mini" @click="del(scope.row.id)">
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <data-rule-form ref="dataRuleForm"   @refreshDataList="refreshList"></data-rule-form>
  </div>
</template>

<script>
  import DataRuleForm from './DataRuleForm'

  export default {
    data () {
      return {
        dataList: [],
        loading: false,
        rows: [],
        menuId: '',
        pageNo: 1,
        pageSize: -1,
        total: 0,
        title: '',
        dataListSelections: []
      }
    },
    props: ['dataRuleTitle'],
    components: {
      DataRuleForm
    },
    activated () {
      this.refreshList(this.menuId)
    },
    methods: {
      // 获取数据列表
      refreshList (menuId) {
        this.menuId = menuId
        this.loading = true
        this.$http({
          url: '/sys/dataRule/list',
          method: 'get',
          params: {
            'pageNo': this.pageNo,
            'pageSize': this.pageSize,
            'menuId': menuId
          }
        }).then(({data}) => {
          if (data && data.success) {
            this.rows = data.page.list
            this.total = data.page.count
            this.loading = false
          }
        })
      },
      // 新增
      add () {
        this.$refs.dataRuleForm.init('add', {id: '', menuId: this.menuId})
      },
      // 修改
      edit (id) {
        this.$refs.dataRuleForm.init('edit', {id: id, menuId: this.menuId})
      },
      // 查看
      view (id) {
        this.$refs.dataRuleForm.init('view', {id: id, menuId: this.menuId})
      },
      // 删除
      del (id) {
        this.$confirm(`确定删除该条记录吗?`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.loading = true
          this.$http({
            url: `/sys/dataRule/delete`,
            method: 'delete',
            params: {id: id}
          }).then(({data}) => {
            this.loading = false
            if (data && data.success) {
              this.$message.success(data.msg)
              this.refreshList(this.menuId)
            }
          })
        })
      }
    }
  }
</script>

<style>
  .el-dropdown-link {
    cursor: pointer;
    color: #409EFF;
    font-size: 12px;
    font-weight: 500;
  }
  .el-icon-arrow-down {
    font-size: 12px;
  }
  
</style>
