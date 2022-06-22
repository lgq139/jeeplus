<template>
  <div>
    <div class="public-search-box">
      <el-form :inline="true" class="demo-form-inline" :model="searchData" ref="searchForm">
        <el-form-item label="操作人员:" prop="userName">
          <el-input placeholder="请输入操作人员" v-model="searchData.userName"></el-input>
        </el-form-item>
        <el-form-item label="操作部门:" prop="branch">
          <el-input placeholder="请输入操作部门" v-model="searchData.branch"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="getCatalogListData(searchData)">查询</el-button>
          <el-button @click="refreshCatalogList()">重置</el-button>
        </el-form-item>
      </el-form>
    </div>
    <div class="public-table-box">
      <div class="public-menu-box">
        <button class="public-btn"><i class="fa fa-download"></i>模板下载</button>
        <button class="public-btn"><i class="fa fa-download"></i>批量上传</button>
      </div>
      <el-table
        v-loading="loading"
        :data="tableData"
        style="width: 100%">
        <el-table-column
          type="index"
          label="序号"
          width="100">
        </el-table-column>
        <el-table-column
          prop="createDate"
          label="导入时间"
          min-width="16%">
        </el-table-column>
        <el-table-column
          prop="importCount"
          label="导入数量"
          min-width="12%">
        </el-table-column>
        <el-table-column
          prop="importLevel"
          label="操作层级"
          min-width="16%">
        </el-table-column>
        <el-table-column
          prop="importOrgName"
          label="操作部门"
          min-width="16%">
        </el-table-column>
        <el-table-column
          prop="importUserName"
          label="操作人员"
          min-width="16%">
        </el-table-column>
        <el-table-column
          label="操作"
          min-width="16%">
          <template slot-scope="scope">
            <el-button @click="lookDetails(scope.row)" type="text" size="small">查看</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="pageNumber"
        :page-sizes="[20, 40, 60, 100]"
        :page-size="pageSize"
        layout="total, sizes, prev, pager, next"
        :total="total">
      </el-pagination>
    </div>

  </div>
</template>
<!--便民目录导入-->
<script>
  import {getCatalogListData} from "@/api/modules/catalog/catalogList";
  export default {
    data () {
      return {
        searchData: {
          userName: '',
          branch: ''
        },
        total:0,
        pageNumber:1,
        pageSize:20,
        tableData: [],
        loading:true
      }
    },
    components: {
    },
    methods: {
      handleSizeChange(val){
        this.pageSize=val
        this.getCatalogListData(this.searchData)
      },
      handleCurrentChange(val){
        this.pageNumber=val
        this.getCatalogListData(this.searchData)
      },
      lookDetails(row){
        // console.log(row)
      },
      getCatalogListData(searchData){
        this.tableData=[]
        this.loading=true
        getCatalogListData({pageNo: this.pageNumber, pageSize: this.pageSize,importOrgName:searchData.branch,importUserName:searchData.userName}).then(({data}) => {
          if (data && data.success) {
            this.total=data.data.total
            this.tableData=data.data.records
          }
          this.loading = false
        })
      },
      //  刷新列表
      refreshCatalogList(){
        this.searchData.branch=''
        this.searchData.userName=''
        this.pageNumber=1
        this.pageSize=20
        this.getCatalogListData(this.searchData)
      }
    },
    mounted() {
      // 获取列表数据
      this.getCatalogListData(this.searchData)
    }
  }
</script>
<style scoped>


</style>
