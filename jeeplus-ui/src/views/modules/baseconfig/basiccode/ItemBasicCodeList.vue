<!--
  四级四同目录列表显示页
-->
<template>
  <div>
    <el-form v-show="isSearchCollapse" ref="searchForm" :inline="true" :model="searchForm" class="query-form"
             @keyup.enter.native="refreshList()"
             @submit.native.prevent>
      <el-form-item prop="loginName">
        <el-input v-model="searchForm.name" clearable placeholder="事项目录名称" size="small"></el-input>
      </el-form-item>
      <el-form-item prop="name">
        <el-input v-model="searchForm.code" clearable placeholder="事项目录编码" size="small"></el-input>
      </el-form-item>
      <el-form-item prop="superviseOrgName">
        <el-input v-model="searchForm.superviseOrgName" clearable placeholder="业务指导部门" size="small"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button size="small" type="primary" @click="refreshList()">查询</el-button>
        <el-button size="small" @click="resetSearch()">重置</el-button>
      </el-form-item>
    </el-form>
    <el-row>
      <el-button v-if="hasPermission('baseconfig:item:itemBasicCode:add')" icon="el-icon-plus" size="small"
                 type="primary" @click="add()">新建
      </el-button>
      <el-button v-if="hasPermission('baseconfig:item:itemBasicCode:edit')" :disabled="dataListSelections.length != 1"
                 icon="el-icon-edit-outline"
                 plain size="small"
                 type="warning" @click="edit()">修改
      </el-button>
      <el-button v-if="hasPermission('baseconfig:item:itemBasicCode:del')" :disabled="dataListSelections.length <= 0"
                 icon="el-icon-delete"
                 plain size="small"
                 type="danger" @click="del()">删除
      </el-button>
      <el-button v-if="hasPermission('baseconfig:item:itemBasicCode:edit')" icon="el-icon-upload2" plain
                 size="small" type="info" @click="handleImport">导入
      </el-button>
      <el-button disabled icon="el-icon-download" plain size="small" type="info">导出</el-button>
      <el-button v-if="hasPermission('baseconfig:item:itemBasicCode:edit')" disabled icon="el-icon-refresh"
                 plain size="small" type="warning">一键事项数据同步
      </el-button>
      <el-button v-if="hasPermission('baseconfig:item:itemBasicCode:edit')" icon="el-icon-s-data" plain
                 size="small" type="info" @click="exportReport">导出事项认领情况
      </el-button>

      <el-button-group class="pull-right">
        <el-tooltip class="item" content="搜索" effect="dark" placement="top">
          <el-button
            icon="el-icon-search"
            size="small"
            type="default"
            @click="isSearchCollapse = !isSearchCollapse, isImportCollapse=false">
          </el-button>
        </el-tooltip>
        <el-tooltip class="item" content="刷新" effect="dark" placement="top">
          <el-button
            icon="el-icon-refresh"
            size="small"
            type="default"
            @click="refreshList">
          </el-button>
        </el-tooltip>
      </el-button-group>
    </el-row>

    <el-table
      v-loading="loading"
      :data="dataList"
      class="table"
      size="medium"
      @selection-change="selectionChangeHandle"
      @sort-change="sortChangeHandle"
    >
      <el-table-column align="center" type="selection" width="50"></el-table-column>
      <el-table-column align="center" label="序号" type="index" width="60"/>
      <el-table-column label="事项目录名称" min-width="240" prop="name" show-overflow-tooltip>
        <template slot-scope="scope">
          <el-link v-if="hasPermission('baseconfig:item:itemBasicCode:edit')" :underline="false" type="primary"
                   @click="edit(scope.row)">{{ scope.row.name }}
          </el-link>
          <el-link v-else-if="hasPermission('baseconfig:item:itemBasicCode:view')" :underline="false" type="primary"
                   @click="view(scope.row)">{{ scope.row.name }}
          </el-link>
          <span v-else>{{ scope.row.name }}</span>
        </template>
      </el-table-column>
      <el-table-column label="事项目录编码" min-width="140" prop="code" show-overflow-tooltip/>
      <el-table-column label="业务指导部门" min-width="160" prop="superviseOrgName" show-overflow-tooltip/>
      <el-table-column :formatter="formatterApprovalLevel" label="行驶层级" min-width="120" prop="approvalLevel"
                       show-overflow-tooltip/>
      <el-table-column :formatter="formatterItemType" label="事项类型" min-width="120" prop="type"
                       show-tooltip-when-overflow/>
      <el-table-column label="备注" prop="remarks"/>
      <el-table-column align="center" fixed="right" header-align="center" label="操作" width="300">
        <template slot-scope="scope">
          <el-button v-if="hasPermission('baseconfig:item:itemBasicCode:edit')" size="small" type="text"
                     @click="edit(scope.row)">修改
          </el-button>
          <el-divider v-if="hasPermission('baseconfig:item:itemBasicCode:edit')" direction="vertical"></el-divider>
          <el-button v-if="hasPermission('baseconfig:item:itemBasicCode:edit')" size="small" type="text"
                     @click="handleRouterItemInfo(scope.row)">事项信息
          </el-button>
          <el-divider v-if="hasPermission('baseconfig:item:itemBasicCode:edit')" direction="vertical"></el-divider>
          <el-button size="small" type="text" @click="handleRouterItemStandard(scope.row)">标准事项</el-button>
          <el-divider v-if="hasPermission('baseconfig:item:itemBasicCode:del')" direction="vertical"></el-divider>
          <el-button v-if="hasPermission('baseconfig:item:itemBasicCode:del')" size="small" type="text"
                     @click="del(scope.row.id)">删除
          </el-button>
          <!--          <el-dropdown size="small"  @command="handleCommand">-->
          <!--            <span class="el-dropdown-link">-->
          <!--              更多<i class="el-icon-arrow-down el-icon&#45;&#45;right"></i>-->
          <!--            </span>-->
          <!--            <el-dropdown-menu slot="dropdown">-->
          <!--              <el-dropdown-item v-if="hasPermission('baseconfig:item:itemBasicCode:del')" :command="{method:'delete', data: scope.row.id}">删除</el-dropdown-item>-->
          <!--            </el-dropdown-menu>-->
          <!--          </el-dropdown>-->
        </template>
      </el-table-column>
    </el-table>
    <ch-pagination
      v-show="total>0"
      :limit.sync="pageSize"
      :page.sync="pageNo"
      :total="total"
      @pagination="refreshList"
    />

    <item-basic-code-form ref="itemBasicCodeForm" @submit-success="refreshList"/>
    <!-- 事项目录批量导入 -->
    <ch-file-temp-import
      :url="importUrl"
      :visible.sync="importVisible"
      templateUrl="/baseconfig/item/itemBasicCode/import/template"
      title="四级四同事项目录数据批量导入"
      type="card"
      @import-success="resetSearch"
    />
  </div>
</template>

<script>
import ChPagination from "@/components/ChPagination";
import {deleteItemBasicCode, listItemBasicCode} from "@/api/modules/baseconfig/basiccode";
import ItemBasicCodeForm from "@/views/modules/baseconfig/basiccode/ItemBasicCodeForm";
import ChFileTempImport from "@/components/ChFileTempImport";

export default {
  data() {
    return {
      searchForm: {
        loginName: '',
        name: '',
        company: {
          id: ''
        },
        office: {
          id: ''
        },
        code: undefined,
        superviseOrgName: undefined,
      },
      isShow: true,
      filterText: '',
      dataList: [],
      total: 0,
      pageNo: 1,
      pageSize: 10,
      officeTreeData: [],
      orderBy: '',
      dataListSelections: [],
      isSearchCollapse: false,
      isImportCollapse: false,
      loading: false,
      selectOfficeName: '',
      importVisible: false,
      importUrl: process.env.VUE_APP_BASE_API + "/baseconfig/item/itemBasicCode/import"
    }
  },
  components: {
    ChFileTempImport,
    ItemBasicCodeForm,
    ChPagination

  },
  activated() {
    this.refreshList()
  },
  watch: {
    filterText(val) {
      this.$refs.officeTree.filter(val)
    }
  },
  computed: {
    contentViewHeight() {
      let height = this.$store.state.common.documentClientHeight - 122
      return {minHeight: height + 'px'}
    }
  },
  methods: {
    // 获取数据列表
    async refreshList() {
      this.loading = true
      const {data} = await listItemBasicCode({
        pageNo: this.pageNo,
        pageSize: this.pageSize,
        orderBy: this.orderBy,
        ...this.searchForm
      })
      if (data.code === 200) {
        this.total = data.data.total
        this.dataList = data.data.records
      }
      this.loading = false
    },
    // 格式化显示行驶层级
    formatterApprovalLevel(row, column, cellValue, index) {
      const valueArray = cellValue.split(",");
      const labelArray = [];
      valueArray.forEach(item => {
        labelArray.push(this.$dictUtils.getDictLabel('item_approve_level', item))
      })
      return labelArray.join(",");
    },
    // 格式化显示事项类型
    formatterItemType(row, column, cellValue, index) {
      return this.$dictUtils.getDictLabel('item_type', cellValue);
    },

    // 多选
    selectionChangeHandle(val) {
      this.dataListSelections = val
    },
    // 排序
    sortChangeHandle(obj) {
      if (obj.prop === 'office.name') {
        obj.prop = 'o.name'
      }
      if (obj.prop === 'company.name') {
        obj.prop = 'c.name'
      }
      if (obj.order === 'ascending') {
        this.orderBy = obj.prop + ' asc'
      } else if (obj.order === 'descending') {
        this.orderBy = obj.prop + ' desc'
      } else {
        this.orderBy = ''
      }
      this.refreshList()
    },

    // 新增
    add() {
      this.$refs.itemBasicCodeForm.showEdit('add')
    },
    // 修改
    edit(row) {
      this.$refs.itemBasicCodeForm.showEdit('edit', row)
    },
    // 查看
    view(row) {
      this.$refs.itemBasicCodeForm.showEdit('view', row)
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
        this.loading = true
        deleteItemBasicCode({ids: ids}).then(response => {
          this.loading = false
          if (response.data.code === 200) {
            this.$message.success({dangerouslyUseHTMLString: true, message: response.data.msg})
            this.refreshList()
          }
        })
      })
    },

    // 跳转至事项信息页面
    handleRouterItemInfo(row) {
      if (row) {
        this.$router.push({
          path: '/baseconfig/iteminfo/ItemInfoList',
          query: {
            title: '事项信息' + ' / ' + row.name,
            basicCode: row.code
          }
        })
      }
    },

    // 跳转至标准事项页面
    handleRouterItemStandard(row) {
      if (row) {
        this.$router.push({
          path: '/baseconfig/basicStandard/ItemBasicStandardList',
          query: {
            title: '基本标准事项 / ' + row.name,
            basicCode: row.code,
            basicName: row.name,
            superviseOrgName: row.superviseOrgName,
            formId: row.formId,
            formName: row.formName,
            approvalLevel: row.approvalLevel,
            stageCode: row.stageCode,
            type: row.type
          }
        })
      }
    },

    resetSearch() {
      this.searchForm.company.id = ''
      this.searchForm.office.id = ''
      this.selectOfficeName = ''
      this.$refs.searchForm.resetFields()
      this.refreshList()
    },

    handleImport() {
      this.importVisible = true
    },


    exportReport() {
      // downloadXlsx(this.templateUrl)
      this.$utils.download("/baseconfig/item/itemInfo/export/analyze")
    },

    handleCommand({method, data}) {
      if (method === 'material') {
        // row
        this.handleRouterItemMaterial(data)
      } else if (method === 'delete') {
        // row.id
        this.del(data)
      }
    },

  }
}
</script>

<style scoped>

</style>
