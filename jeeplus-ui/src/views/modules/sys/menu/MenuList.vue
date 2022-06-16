<template>
  <div>
    <vxe-toolbar :refresh="{query: refreshList}" custom>
      <template #buttons>
        <el-button v-if="hasPermission('sys:menu:add')" type="primary" size="small" icon="el-icon-plus" @click="add()">新建</el-button>
      </template>
    </vxe-toolbar>
    <vxe-table
      resizable
      row-id="id"
      v-loading="loading"
      :tree-config="{children: 'children', reserve: true}"
      :data="dataList"
      show-overflow="tooltip"
      max-height="1200"
      size="medium"
      class="table"
      border
    >
      <vxe-column field="name" title="名称" tree-node width="200">
        <template slot-scope="scope">
          <el-link type="primary" :underline="false" @click="view(scope.row.id)">
            {{scope.row.name}}
          </el-link>
        </template>
      </vxe-column>
      <vxe-column field="icon" title="图标" align="center" width="150">
        <template slot-scope="scope">
          <i :class="scope.row.icon"></i>
        </template>
      </vxe-column>
      <vxe-column field="remarks" align="center" title="类型" width="150">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.type === '0'" size="small">目录</el-tag>
          <el-tag v-else-if="scope.row.type === '1'" size="small" type="success">菜单</el-tag>
          <el-tag v-else-if="scope.row.type === '2'" size="small" type="info">按钮</el-tag>
          <el-tag v-else-if="scope.row.type === '3'" size="small" type="info">路由</el-tag>
        </template>
      </vxe-column>
      <vxe-column field="sort" align="center" width="160" title="排序号">
        <template slot-scope="scope">
          <el-input-number
            size="small"
            v-model="scope.row.sort"
            @change="sortChange(scope.row)"
            :step="30"
            :min="0"
            :max="10000"
            label="描述文字">
          </el-input-number>
        </template>
      </vxe-column>
      <vxe-column align="center" width="150" title="是否显示">
        <template slot-scope="scope">
          <el-switch
            v-model="scope.row.isShow"
            v-if="scope.row.type === '0' || scope.row.type === '1'"
            @change="statusChange(scope.row)"
            active-color="#13ce66"
            inactive-color="#ff4949"
            active-value="1"
            inactive-value="0">
          </el-switch>
        </template>
      </vxe-column>
      <vxe-column field="href" align="left" min-width="150" title="菜单路由" />
      <vxe-column field="permission" align="left" min-width="150" title="权限标识" />
      <vxe-column title="操作" width="150" align="center" fixed="right">
        <template slot-scope="scope">
          <el-button type="text" size="small" @click="showRight(scope.row)">数据规则</el-button>
          <el-divider direction="vertical"></el-divider>
          <el-dropdown size="small" @command="handleCommand">
            <span class="el-dropdown-link">
              更多<i class="el-icon-arrow-down el-icon--right"></i>
            </span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item v-if="hasPermission('sys:menu:edit')" :command="{method:'edit', id:scope.row.id}">修改</el-dropdown-item>
              <el-dropdown-item v-if="hasPermission('sys:menu:del')" :command="{method:'del', id:scope.row.id}">删除</el-dropdown-item>
              <el-dropdown-item v-if="hasPermission('sys:menu:add')" :command="{method:'addChild', row:scope.row}">添加下级菜单</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </template>
      </vxe-column>
    </vxe-table>

    <el-drawer
      size="700px"
      :title="`数据规则列表，所属菜单: ${this.dataRuleTitle}`"
      :visible.sync="rightVisible"
      direction="rtl">
      <data-rule-list ref="dataRuleList" @closeRight="closeRight"></data-rule-list>
    </el-drawer>

    <!-- 弹窗, 新增 / 修改 -->
    <menu-form ref="menuForm" @refreshDataList="refreshList"></menu-form>
  </div>
</template>

<script>
import MenuForm from './MenuForm'
import DataRuleList from './DataRuleList'
import {deleteMenu, getTreeData2, saveMenu} from "@/api/modules/sys/menu";

export default {
  data() {
    return {
      rightVisible: false,
      loading: false,
      dataRuleTitle: '',
      dataList: []
    }
  },
  components: {
    MenuForm,
    DataRuleList
  },
  activated() {
    this.refreshList()
  },
  methods: {
    // 获取数据列表
    refreshList() {
      this.loading = true
      getTreeData2().then(({data}) => {
        this.loading = false
        if (data && data.success) {
          this.dataList = data.data
        }
      })
    },
    // 新增下级
    addChild(row) {
      this.$refs.menuForm.init('addChild', {id: '', parent: {id: row.id, name: ''}})
    },
    // 新增
    add() {
      this.$refs.menuForm.init('add', {id: '', parent: {id: '', name: ''}})
    },
    // 修改
    edit(id) {
      this.$refs.menuForm.init('edit', {id: id, parent: {id: '', name: ''}})
    },
    // 查看
    view(id) {
      this.$refs.menuForm.init('view', {id: id, parent: {id: '', name: ''}})
    },
    handleCommand(command) {
      if (command.method === 'view') {
        this.view(command.id)
      } else if (command.method === 'edit') {
        this.edit(command.id)
      } else if (command.method === 'del') {
        this.del(command.id)
      } else if (command.method === 'addChild') {
        this.addChild(command.row)
      }
    },
    // 删除
    del(id) {
      this.$confirm(`确定删除该条记录吗?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.loading = true
        deleteMenu({id: id}).then(({data}) => {
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
    },
    statusChange(row) {
      this.loading = true
      saveMenu({id: row.id, isShow: row.isShow}).then(({data}) => {
        if (data && data.success) {
          this.$message({
            message: data.msg,
            type: 'success',
            duration: 1500
          })
        }
        this.loading = false
        this.refreshList()
      })
    },
    sortChange(row) {
      this.loading = true
      saveMenu({id: row.id, sort: row.sort}).then(({data}) => {
        if (data && data.success) {
          this.$message({
            message: data.msg,
            type: 'success',
            duration: 1500
          })
        }
        this.loading = false
        this.refreshList()
      })
    },
    showRight(row) {
      this.rightVisible = true
      this.$nextTick(() => {
        this.$refs.dataRuleList.refreshList(row.id)
        this.dataRuleTitle = row.name
      })
    },
    closeRight() {
      this.rightVisible = false
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
