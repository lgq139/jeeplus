<template>
  <div>
    <div>
      <div class="public-search-box">
        <el-form :inline="true" class="demo-form-inline" :model="searchData" ref="searchForm">
          <el-form-item label="基本编码:" prop="basecode">
            <el-input placeholder="请输入基本编码" v-model="searchData.basecode"></el-input>
          </el-form-item>
          <el-form-item label="目录名称:" prop="cataname">
            <el-input placeholder="请输入目录名称" v-model="searchData.cataname"></el-input>
          </el-form-item>
          <el-form-item label="事项类型:" prop="state">
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
            <el-button type="primary" @click="refreshList(searchData)">查询</el-button>
            <el-button @click="refreshCatalogStick()">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
      <div class="public-table-box">
        <div class="public-menu-box">
          <button class="public-btn" @click="add()"><i class="fa fa-plus"></i>新增</button>
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
            prop="baseCode"
            label="基本编码"
            min-width="18%">
          </el-table-column>
          <el-table-column
            prop="cataName"
            label="目录名称"
            min-width="18%">
          </el-table-column>
          <el-table-column
            prop="cataType"
            label="事项类型"
            min-width="18%"
            :formatter="statusFormatter">
          </el-table-column>
          <el-table-column
            prop="state"
            label="操作状态"
            min-width="18%">
            <template slot-scope="scope">
              <div slot="reference" class="name-wrapper" >
                <el-tag size="medium" type="success" v-if="scope.row.enableStatus==1">启用</el-tag>
                <el-tag size="medium" type="danger" v-else>停用</el-tag>
              </div>
            </template>
          </el-table-column>
          <el-table-column
            label="操作"
            min-width="18%">
            <template slot-scope="scope">
              <el-button @click="endUse(scope.row)" type="text" size="small" class="public-btn-danger" v-if="scope.row.enableStatus==1">停用</el-button>
              <el-button @click="startUse(scope.row)" type="text" size="small" class="public-btn-success" v-else>启用</el-button>
              <el-button @click="lookDetails(scope.row)" type="text" size="small">查看</el-button>
              <el-button @click="changeDetails(scope.row)" type="text" size="small">变更</el-button>
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
    <ConvenientPeopleCatalogStickEdit ref="ConvenientPeopleCatalogStickEdit" @refreshDataList="updateList"></ConvenientPeopleCatalogStickEdit>
  </div>


</template>
<!--便民目录认领-->
<script>
  import {getCatalogStickData,editCatalogStickData} from "@/api/modules/catalog/catalogStick";
  import {getStateData} from "@/api/modules/catalog/dict";
  import ConvenientPeopleCatalogStickEdit from './ConvenientPeopleCatalogStickEdit'
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
        tableData: [
        ],
        loading:false,
        stateData:[], // 事项类型数据
        handleId:''
      }
    },
    components: {
      ConvenientPeopleCatalogStickEdit
    },
    methods: {
      handleSizeChange(val){
        this.pageSize=val
        this.refreshList(this.searchData)
      },
      handleCurrentChange(val){
        this.pageNumber=val
        this.refreshList(this.searchData)
      },
      // 启用
      startUse(row){
        this.handleId=row.id
        this.$confirm('是否启用该目录', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          // dangerouslyUseHTMLString: true,
          // type: 'info'
        }).then(()=>{
          this.loading =true
          editCatalogStickData({id:this.handleId,enableStatus:'1'}).then(({data})=>{
            if (data.success){
              this.$message.success('启用成功')
              this.refreshList(this.searchData)
            }
          })
        }).catch((err)=>{
          this.$message('取消启用')
        })
      },
      // 停用
      endUse(row){
        this.handleId=row.id
        this.$confirm('是否停运该目录', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          // dangerouslyUseHTMLString: true,
          // type: 'info'
        }).then(()=>{
          this.loading =true
          editCatalogStickData({id:this.handleId,enableStatus:'0'}).then(({data})=>{
              if (data.success){
                this.$message.success('停运成功')
                this.refreshList(this.searchData)
              }
          })
        }).catch((err)=>{
          this.$message('取消停运')
        })
      },
      // 查看
      lookDetails(row){
        this.$refs.ConvenientPeopleCatalogStickEdit.init('view', row)
      },
      // 变更
      changeDetails(row){
        this.$refs.ConvenientPeopleCatalogStickEdit.init('change', row)
      },
      // 新增
      add() {
        this.$refs.ConvenientPeopleCatalogStickEdit.init('add', '')
      },
      // 表单数据展示
      refreshList(searchData) {
        this.tableData=[]
        this.loading = true
        getStateData({dictType:'con_item_type'}).then(({data})=>{
          this.stateData=data.keyAndValue
        }).then(()=>{
          getCatalogStickData({pageNo: this.pageNumber, pageSize: this.pageSize,baseCode:searchData.basecode,cataName:searchData.cataname,cataType:searchData.state}).then(({data}) => {
            this.loading = false
            if (data && data.success) {
              this.total=data.data.total
              this.tableData=data.data.records
            }
          })
        })
      },
      // 新增变更后刷新列表
      updateList(){
        this.tableData=[]
        this.loading = true
        getStateData({dictType:'con_item_type'}).then(({data})=>{
          this.stateData=data.keyAndValue
        }).then(()=>{
          getCatalogStickData({pageNo: this.pageNumber, pageSize: this.pageSize}).then(({data}) => {
            this.loading = false
            if (data && data.success) {
              this.total=data.data.total
              this.tableData=data.data.records
            }
          })
        })
      },
      // 重置表单
      refreshCatalogStick(){
        this.searchData.basecode=''
        this.searchData.cataname=''
        this.searchData.state=''
        this.pageNumber=1
        this.pageSize=20
        this.refreshList(this.searchData)
      },
     getStateData(){

     },
      // 事项类型转换文字展示
      statusFormatter(row, column){
        const type = row.cataType
        return this.stateData[type]
      }
    },
    mounted() {
      this.refreshList(this.searchData)
    }
  }
</script>
<style scoped>


</style>
