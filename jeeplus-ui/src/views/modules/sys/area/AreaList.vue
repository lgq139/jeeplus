<template>
  <div>
    <el-row>
      <el-button v-if="hasPermission('sys:area:add')" type="primary" icon="el-icon-plus" size="small" @click="add()">新增</el-button>
    </el-row>
    <el-table
      :data="dataList"
      border
      stripe
      row-key="id"
      max-height="1200"
      v-loading="loading"
      class="table">
      <el-table-column prop="name" show-overflow-tooltip label="区域名称">
        <template slot-scope="scope">
          <el-link  type="primary" :underline="false" v-if="hasPermission('sys:area:edit')" @click="edit(scope.row.id)">{{scope.row.name}}</el-link>
          <el-link  type="primary" :underline="false" v-else-if="hasPermission('sys:area:view')"  @click="view(scope.row.id)">{{scope.row.name}}</el-link>
          <span v-else>{{scope.row.name}}</span>
        </template>
      </el-table-column>
      <el-table-column prop="code" label="区域编码"></el-table-column>
      <el-table-column prop="type" label="区域类型">
        <template slot-scope="scope">
          {{$dictUtils.getDictLabel("sys_area_type", scope.row.type)}}
        </template>
      </el-table-column>
      <el-table-column prop="sort" label="排序号"></el-table-column>
      <el-table-column prop="remarks" label="备注"></el-table-column>
      <el-table-column fixed="right" header-align="center" align="center" width="300" label="操作">
        <template slot-scope="scope">
          <el-button v-if="hasPermission('sys:area:view')" type="text" size="small"  icon="el-icon-view" @click="view(scope.row.id)">查看</el-button>
          <el-button v-if="hasPermission('sys:area:edit')" type="text" size="small" icon="el-icon-edit" @click="edit(scope.row.id)">修改</el-button>
          <el-button v-if="hasPermission('sys:area:del')" type="text" size="small" icon="el-icon-delete" @click="del(scope.row.id)">删除</el-button>
          <el-button v-if="hasPermission('sys:area:add')" type="text" size="small" icon="el-icon-circle-plus-outline" @click="addChild(scope.row.id, scope.row.name)">添加下级区域</el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 弹窗, 新增 / 修改 -->
    <AreaForm ref="areaForm" @refreshDataList="refreshList"></AreaForm>
  </div>
</template>

<script>
  import AreaForm from './AreaForm'

  export default {
    data () {
      return {
        loading: false,
        dataList: []
      }
    },
    components: {
      AreaForm
    },
    activated () {
      this.refreshList()
    },
    methods: {
      // 获取数据列表
      refreshList () {
        this.loading = true
        this.$http({
          url: '/sys/area/treeData',
          method: 'get'
        }).then(({data}) => {
          this.dataList = data.treeData
          this.loading = false
        })
      },
      // 新增下级
      addChild (id, name) {
        this.$refs.areaForm.init('addChild', {id: '', parent: {id: id, name: name}})
      },
       // 新增
      add () {
        this.$refs.areaForm.init('add', {id: '', parent: {id: '', name: ''}})
      },
      // 修改
      edit (id) {
        this.$refs.areaForm.init('edit', {id: id, parent: {id: '', name: ''}})
      },
      // 查看
      view (id) {
        this.$refs.areaForm.init('view', {id: id, parent: {id: '', name: ''}})
      },
      // 删除
      del (id) {
        this.$confirm(`确定删除该记录吗?`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.loading = true
          this.$http({
            url: '/sys/area/delete',
            method: 'delete',
            params: {id: id}
          }).then(({data}) => {
            this.loading = false
            if (data && data.success) {
              this.$message({
                message: data.msg,
                type: 'success',
                duration: 1500
              })
              this.refreshList()
            }
          })
        })
      }
    }
  }
</script>
