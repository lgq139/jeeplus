<template>

  <div>
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
          icon="el-icon-edit"
          size="mini"
          type="success"
          @click="handleUpdate"
        >修改
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          icon="el-icon-delete"
          size="mini"
          type="danger"
          @click="handleDelete"
        >删除
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
          <el-button size="small" type="text"
                     @click="handleUpdate(scope.row)">修改
          </el-button>
          <el-button size="small" type="text"
                     @click="handleDelete(scope.row.id)">删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <ch-pagination
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

    <add-j-theme ref="addJTheme" :getList="getList"></add-j-theme>
  </div>
</template>

<script>
import {listJTheme} from "@/api/modules/sys/jTheme";
import AddJTheme from "@/views/modules/sys/jTheme/addJTheme";
import ChDialog from "@/components/ChDialog";
import ChPagination from "@/components/ChPagination";

export default {
  name: "jThemeList",
  components: {
    ChPagination,
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
      form: {}
    }
  },
  created() {
    this.getList();
  },
  methods: {
    handleUpdate(row) {
      this.$refs.addJTheme.openUpdateDialog(row);
    },

    handleDelete() {

    },
    //新增
    handleAdd() {
      this.$refs.addJTheme.openAddDialog();
      // this.title = "新增主题";
      // this.open = true;
    },
    //序号
    indexMethod(index) {
      return index + 1 + (this.pageNo - 1) * this.pageSize;
    },
    // 获取列表数据
    getList() {
      console.log('12323145')
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
