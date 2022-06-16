<!--
  四级四同目录列表显示页
-->
<template>
  <div>
    <el-form :inline="true" v-show="isSearchCollapse" class="query-form" ref="searchForm" :model="searchForm"
             @keyup.enter.native="refreshList()"
             @submit.native.prevent>
      <el-form-item prop="loginName">
        <el-input size="small" v-model="searchForm.name" placeholder="事项目录名称" clearable></el-input>
      </el-form-item>
      <el-form-item prop="name">
        <el-input size="small" v-model="searchForm.code" placeholder="事项目录编码" clearable></el-input>
      </el-form-item>
      <el-form-item prop="superviseOrgName">
        <el-input size="small" v-model="searchForm.superviseOrgName" placeholder="业务指导部门" clearable></el-input>
      </el-form-item>
      <el-form-item>
        <el-button  type="primary" @click="refreshList()" size="small">查询</el-button>
        <el-button @click="resetSearch()" size="small">重置</el-button>
      </el-form-item>
    </el-form>
    <el-row>
      <el-button v-if="hasPermission('baseconfig:item:itemBasicCode:add')" type="primary" size="small" icon="el-icon-plus" @click="add()">新建</el-button>
      <el-button v-if="hasPermission('baseconfig:item:itemBasicCode:edit')" type="warning" size="small" icon="el-icon-edit-outline" @click="edit()"
                 :disabled="dataListSelections.length != 1" plain>修改</el-button>
      <el-button v-if="hasPermission('baseconfig:item:itemBasicCode:del')" type="danger"   size="small" icon="el-icon-delete" @click="del()"
                 :disabled="dataListSelections.length <= 0" plain>删除
      </el-button>
      <el-button v-if="hasPermission('baseconfig:item:itemBasicCode:edit')" type="info" size="small" icon="el-icon-upload2" plain @click="handleImport">导入</el-button>
      <el-button type="info" size="small" icon="el-icon-download" plain disabled>导出</el-button>
      <el-button v-if="hasPermission('baseconfig:item:itemBasicCode:edit')"  type="warning" size="small" icon="el-icon-refresh" plain disabled>一键事项数据同步</el-button>
      <el-button v-if="hasPermission('baseconfig:item:itemBasicCode:edit')" type="info" size="small" icon="el-icon-s-data" plain @click="exportReport">导出事项认领情况</el-button>

      <el-button-group class="pull-right">
        <el-tooltip class="item" effect="dark" content="搜索" placement="top">
          <el-button
            type="default"
            size="small"
            icon="el-icon-search"
            @click="isSearchCollapse = !isSearchCollapse, isImportCollapse=false">
          </el-button>
        </el-tooltip>
        <el-tooltip class="item" effect="dark" content="刷新" placement="top">
          <el-button
            type="default"
            size="small"
            icon="el-icon-refresh"
            @click="refreshList">
          </el-button>
        </el-tooltip>
      </el-button-group>
    </el-row>

    <el-table
      :data="dataList"
      v-loading="loading"
      @selection-change="selectionChangeHandle"
      @sort-change="sortChangeHandle"
      size="medium"
      class="table"
    >
      <el-table-column type="selection" align="center" width="50"></el-table-column>
      <el-table-column type="index" label="序号" width="60" align="center" />
      <el-table-column prop="name" min-width="240" show-overflow-tooltip label="事项目录名称">
        <template slot-scope="scope">
          <el-link  type="primary" :underline="false" v-if="hasPermission('baseconfig:item:itemBasicCode:edit')" @click="edit(scope.row)">{{scope.row.name}}</el-link>
          <el-link  type="primary" :underline="false" v-else-if="hasPermission('baseconfig:item:itemBasicCode:view')"  @click="view(scope.row)">{{scope.row.name}}</el-link>
          <span v-else>{{scope.row.name}}</span>
        </template>
      </el-table-column>
      <el-table-column prop="code" min-width="140" show-overflow-tooltip label="事项目录编码" />
      <el-table-column prop="superviseOrgName" min-width="160" show-overflow-tooltip label="业务指导部门" />
      <el-table-column prop="approvalLevel" min-width="120" show-overflow-tooltip label="行驶层级" :formatter="formatterApprovalLevel" />
      <el-table-column prop="type" label="事项类型" min-width="120" show-tooltip-when-overflow :formatter="formatterItemType"  />
      <el-table-column prop="remarks" label="备注" />
      <el-table-column fixed="right" header-align="center" align="center" width="300" label="操作">
        <template slot-scope="scope">
          <el-button v-if="hasPermission('baseconfig:item:itemBasicCode:edit')" type="text" size="small" @click="edit(scope.row)">修改</el-button>
          <el-divider v-if="hasPermission('baseconfig:item:itemBasicCode:edit')" direction="vertical"></el-divider>
          <el-button v-if="hasPermission('baseconfig:item:itemBasicCode:edit')" type="text" size="small" @click="handleRouterItemInfo(scope.row)">事项信息</el-button>
          <el-divider v-if="hasPermission('baseconfig:item:itemBasicCode:edit')" direction="vertical"></el-divider>
          <el-button type="text" size="small" @click="handleRouterItemStandard(scope.row)">标准事项</el-button>
          <el-divider v-if="hasPermission('baseconfig:item:itemBasicCode:del')" direction="vertical"></el-divider>
          <el-button v-if="hasPermission('baseconfig:item:itemBasicCode:del')" type="text" size="small" @click="del(scope.row.id)">删除</el-button>
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
      :total="total"
      :page.sync="pageNo"
      :limit.sync="pageSize"
      @pagination="refreshList"
    />

    <item-basic-code-form ref="itemBasicCodeForm" @submit-success="refreshList" />
    <!-- 事项目录批量导入 -->
    <ch-file-temp-import
      :visible.sync="importVisible"
      title="四级四同事项目录数据批量导入"
      type="card"
      :url="importUrl"
      templateUrl="/baseconfig/item/itemBasicCode/import/template"
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
  data () {
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
  activated () {
    this.refreshList()
  },
  watch: {
    filterText (val) {
      this.$refs.officeTree.filter(val)
    }
  },
  computed: {
    contentViewHeight () {
      let height = this.$store.state.common.documentClientHeight - 122
      return {minHeight: height + 'px'}
    }
  },
  methods: {
    // 获取数据列表
    async refreshList () {
      this.loading = true
      const { data } = await listItemBasicCode({
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
    selectionChangeHandle (val) {
      this.dataListSelections = val
    },
    // 排序
    sortChangeHandle (obj) {
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
    add () {
      this.$refs.itemBasicCodeForm.showEdit('add')
    },
    // 修改
    edit (row) {
      this.$refs.itemBasicCodeForm.showEdit('edit', row)
    },
    // 查看
    view (row) {
      this.$refs.itemBasicCodeForm.showEdit('view', row)
    },
    // 删除
    del (id) {
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

    resetSearch () {
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
