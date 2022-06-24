<template>
  <div>
    <div>
      <div class="public-search-box">
        <el-form :inline="true" class="demo-form-inline" :model="searchData">
          <el-form-item label="基本编码:">
            <el-input placeholder="请输入基本编码" v-model="searchData.basecode"></el-input>
          </el-form-item>
          <el-form-item label="目录名称:">
            <el-input placeholder="请输入目录名称" v-model="searchData.cataname"></el-input>
          </el-form-item>
          <el-form-item label="事项类型:">
            <el-select v-model="searchData.state" placeholder="请选择事项类型">
              <el-option
                v-for="(item,key) in stateData"
                :key="key"
                :label="item"
                :value="key">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="searchCatalogListData(searchData)">查询</el-button>
            <el-button @click="refreshCatalogList()">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
      <div class="public-table-box">
        <el-table
          :data="tableData"
          style="width: 100%">
          <el-table-column
            type="index"
            label="序号"
            width="100">
          </el-table-column>
          <el-table-column
            prop="baseCode"
            label="基本编码"
            min-width="30%">
          </el-table-column>
          <el-table-column
            prop="cataName"
            label="目录名称"
            min-width="20%">
          </el-table-column>
          <el-table-column
            prop="cataType"
            label="事项类型"
            min-width="20%"
            :formatter="statusFormatter"
          >
          </el-table-column>
          <el-table-column
            prop="cataVersion"
            label="版本号"
            min-width="10%">
          </el-table-column>
          <el-table-column
            label="操作"
            min-width="10%">
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
    <ConvenientPeopleCatalogListDetail ref="ConvenientPeopleCatalogListDetail"></ConvenientPeopleCatalogListDetail>
  </div>


</template>
<!--便民目录清单-->
<script>
  import {getCatalogListData} from "@/api/modules/catalog/catalogList";
  import {getStateData} from "@/api/modules/catalog/dict";
  import ConvenientPeopleCatalogListDetail from './ConvenientPeopleCatalogListDetail'
  export default {
    data () {
      return {
        searchData: {
          basecode: '',
          cataname: '',
          state:''
        },
        total:0,
        pageNumber:1,
        pageSize:20,
        tableData: [],
        loading:true,
        stateData:''// 事项类型数据
      }
    },
    components: {
      ConvenientPeopleCatalogListDetail
    },
    methods: {
      // 分页相关操作
      handleSizeChange(val){
        this.pageSize=val
        this.getCatalogClaimData()
      },
      handleCurrentChange(val){
        this.pageNumber=val
        this.getCatalogClaimData()
      },
      // 获取认领列表数据
      getCatalogListData(){
        this.tableData=[]
        this.loading=true
        getStateData({dictType:'con_item_type'}).then(({data})=>{
          this.stateData=data.keyAndValue
        }).then(()=>{
          getCatalogListData({pageNo: this.pageNumber, pageSize: this.pageSize}).then(({data}) => {
            if (data && data.success) {
              this.total=data.data.total
              this.tableData=data.data.records
            }
            this.loading = false
          })
        })
      },
      // 搜索
      searchCatalogListData(searchData){
        if (searchData.basecode===''&&searchData.cataname===''&&searchData.state===''){
          this.$message('请输入基本编码、目录名称或选择事项类型进行查询')
        }else {
          this.tableData = []
          this.loading = true
          getStateData({dictType: 'con_item_type'}).then(({data}) => {
            this.stateData = data.keyAndValue
          }).then(() => {
            getCatalogListData({
              pageNo: this.pageNumber,
              pageSize: this.pageSize,
              baseCode: searchData.basecode,
              cataName: searchData.cataname,
              cataType: searchData.state
            }).then(({data}) => {
              if (data && data.success) {
                this.total = data.data.total
                this.tableData = data.data.records
              }
              this.loading = false
            })
          })
        }
      },
      lookDetails(row){
        this.$refs.ConvenientPeopleCatalogListDetail.init('view', row)
      },
      // 事项类型转换文字展示
      statusFormatter(row, column){
        const type = row.cataType
        return this.stateData[type]
      },
      // 重置表单
      refreshCatalogList(){
        this.searchData.basecode=''
        this.searchData.cataname=''
        this.searchData.state=''
        this.pageNumber=1
        this.pageSize=20
        this.getCatalogListData()
      }
    },
    mounted() {
      this.getCatalogListData()
    }
  }
</script>
<style scoped>


</style>
