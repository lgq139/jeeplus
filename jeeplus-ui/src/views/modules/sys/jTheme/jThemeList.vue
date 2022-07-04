<template>

  <div>

    <!--  <el-form v-show="isSearchCollapse" ref="searchForm" :inline="true" :model="searchForm" class="query-form"-->
    <!--           @keyup.enter.native="refreshList()"-->
    <!--           @submit.native.prevent>-->
    <!--    <el-form-item prop="loginName">-->
    <!--      <el-input v-model="searchForm.name" clearable placeholder="事项目录名称" size="small"></el-input>-->
    <!--    </el-form-item>-->
    <!--    <el-form-item prop="name">-->
    <!--      <el-input v-model="searchForm.code" clearable placeholder="事项目录编码" size="small"></el-input>-->
    <!--    </el-form-item>-->
    <!--    <el-form-item prop="superviseOrgName">-->
    <!--      <el-input v-model="searchForm.superviseOrgName" clearable placeholder="业务指导部门" size="small"></el-input>-->
    <!--    </el-form-item>-->
    <!--    <el-form-item>-->
    <!--      <el-button size="small" type="primary" @click="refreshList()">查询</el-button>-->
    <!--      <el-button size="small" @click="resetSearch()">重置</el-button>-->
    <!--    </el-form-item>-->
    <!--  </el-form>-->
    <!--  <el-row>-->
    <!--    <el-button v-if="hasPermission('baseconfig:item:itemBasicCode:add')" icon="el-icon-plus" size="small"-->
    <!--               type="primary" @click="add()">新建-->
    <!--    </el-button>-->
    <!--    <el-button v-if="hasPermission('baseconfig:item:itemBasicCode:edit')" :disabled="dataListSelections.length != 1"-->
    <!--               icon="el-icon-edit-outline"-->
    <!--               plain size="small"-->
    <!--               type="warning" @click="edit()">修改-->
    <!--    </el-button>-->
    <!--    <el-button v-if="hasPermission('baseconfig:item:itemBasicCode:del')" :disabled="dataListSelections.length <= 0"-->
    <!--               icon="el-icon-delete"-->
    <!--               plain size="small"-->
    <!--               type="danger" @click="del()">删除-->
    <!--    </el-button>-->
    <!--    <el-button v-if="hasPermission('baseconfig:item:itemBasicCode:edit')" icon="el-icon-upload2" plain-->
    <!--               size="small" type="info" @click="handleImport">导入-->
    <!--    </el-button>-->
    <!--    <el-button disabled icon="el-icon-download" plain size="small" type="info">导出</el-button>-->
    <!--    <el-button v-if="hasPermission('baseconfig:item:itemBasicCode:edit')" disabled icon="el-icon-refresh"-->
    <!--               plain size="small" type="warning">一键事项数据同步-->
    <!--    </el-button>-->
    <!--    <el-button v-if="hasPermission('baseconfig:item:itemBasicCode:edit')" icon="el-icon-s-data" plain-->
    <!--               size="small" type="info" @click="exportReport">导出事项认领情况-->
    <!--    </el-button>-->

    <!--    <el-button-group class="pull-right">-->
    <!--      <el-tooltip class="item" content="搜索" effect="dark" placement="top">-->
    <!--        <el-button-->
    <!--          icon="el-icon-search"-->
    <!--          size="small"-->
    <!--          type="default"-->
    <!--          @click="isSearchCollapse = !isSearchCollapse, isImportCollapse=false">-->
    <!--        </el-button>-->
    <!--      </el-tooltip>-->
    <!--      <el-tooltip class="item" content="刷新" effect="dark" placement="top">-->
    <!--        <el-button-->
    <!--          icon="el-icon-refresh"-->
    <!--          size="small"-->
    <!--          type="default"-->
    <!--          @click="refreshList">-->
    <!--        </el-button>-->
    <!--      </el-tooltip>-->
    <!--    </el-button-group>-->
    <!--  </el-row>-->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          icon="el-icon-plus"
          size="mini"
          type="primary"
          @click="handleAdd"
        >新增
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          v-hasPermi="['system:place:edit']"
          :disabled="single"
          icon="el-icon-edit"
          size="mini"
          type="success"
          @click="handleUpdate"
        >修改
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          v-hasPermi="['system:place:remove']"
          :disabled="multiple"
          icon="el-icon-delete"
          size="mini"
          type="danger"
          @click="handleDelete"
        >删除
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          v-hasPermi="['system:place:import']"
          icon="el-icon-upload2"
          size="mini"
          type="info"
          @click="handleImport"
        >导入
        </el-button
        >
      </el-col>
      <el-col :span="1.5">
        <el-button
          v-hasPermi="['system:place:export']"
          icon="el-icon-download"
          size="mini"
          type="warning"
          @click="handleExport"
        >导出
        </el-button>
      </el-col>
      <!--      <right-toolbar-->
      <!--        :showSearch.sync="showSearch"-->
      <!--        @queryTable="getList"-->
      <!--      ></right-toolbar>-->
    </el-row>
    <el-table
      v-loading="loading"
      :data="dataList"
      class="table"
      size="medium"
    >
      <el-table-column align="center" type="selection" width="50"></el-table-column>
      <el-table-column :index="indexMethod" align="center" label="序号" type="index" width="60"/>
      <el-table-column label="主题名称" prop="themeName" show-overflow-tooltip width="200">
      </el-table-column>
      <el-table-column label="主题编码" prop="themeCode" show-overflow-tooltip width="150"/>
      <el-table-column label="服务对象" prop="serviceObject" show-overflow-tooltip width="150"/>
      <el-table-column label="备注" prop="remarks"/>
      <el-table-column align="center" fixed="right" header-align="center" label="操作" width="300">
        <template slot-scope="scope">
          <el-button v-if="hasPermission('baseconfig:item:itemBasicCode:edit')" size="small" type="text"
                     @click="edit(scope.row)">修改
          </el-button>
          <el-button size="small" type="text"
                     @click="del(scope.row.id)">删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination
      :limit.sync="pageSize"
      :page.sync="pageNo"
      :total="total"
      @pagination="getList"
    />
    <!--    <ch-pagination-->
    <!--      v-show="total>0"-->
    <!--      :limit.sync="pageSize"-->
    <!--      :page.sync="pageNo"-->
    <!--      :total="total"-->
    <!--      @pagination="getList"-->
    <!--    />-->

    <!--  <item-basic-code-form ref="itemBasicCodeForm" @submit-success="refreshList"/>-->
    <!-- 事项目录批量导入 -->
    <!--  <ch-file-temp-import-->
    <!--    :url="importUrl"-->
    <!--    :visible.sync="importVisible"-->
    <!--    templateUrl="/baseconfig/item/itemBasicCode/import/template"-->
    <!--    title="四级四同事项目录数据批量导入"-->
    <!--    type="card"-->
    <!--    @import-success="resetSearch"-->
    <!--  />-->
    <ch-dialog
      v-if="open"
      :title="title"
      :visible.sync="open"
      append-to-body
      class="table-card"
      width="700px">
      <add-j-theme ref="addJTheme"></add-j-theme>
    </ch-dialog>

  </div>
</template>

<script>
import {listJTheme} from "@/api/modules/sys/jTheme";
import AddJTheme from "@/views/modules/sys/jTheme/addJTheme";
import ChDialog from "@/components/ChDialog";

export default {
  name: "jThemeList",
  components: {
    AddJTheme,
    ChDialog
  },
  data() {
    return {
      loading: false,
      dataList: [],
      title: "",
      open: false,
      queryParams: {},
      total: 0,
      pageNo: 1,
      pageSize: 10,
    }
  },
  created() {
    this.getList();
  },
  methods: {
    //新增
    handleAdd() {
      this.title = "新增主题";
      this.open = true;
    },
    //序号
    indexMethod(index) {
      return index + 1 + (this.pageNo - 1) * this.pageSize;
    },
    // 获取列表数据
    getList() {
      this.loading = true
      listJTheme({
        pageNo: this.pageNo,
        pageSize: this.pageSize,
        orderBy: this.orderBy,
        ...this.queryParams
      }).then(res => {
        this.loading = false
        this.total = res.data.data.total
        this.dataList = res.data.data.records
        console.log(this.dataList)
      })
    },
  },
}
</script>

<style scoped>

</style>
