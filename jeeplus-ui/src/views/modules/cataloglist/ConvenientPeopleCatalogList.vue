<template>
  <div>
    <div>
      <div class="public-search-box">
        <el-form :inline="true" class="demo-form-inline" :model="searchData">
          <el-form-item label="基本编码:">
            <el-input placeholder="请输入基本编码" v-model="searchData.user"></el-input>
          </el-form-item>
          <el-form-item label="目录名称:">
            <el-input placeholder="请输入目录名称" v-model="searchData.branch"></el-input>
          </el-form-item>
          <el-form-item label="事项类型:">
            <el-select v-model="searchData.state" placeholder="全部">
              <el-option label="查询事项" value="search"></el-option>
              <el-option label="咨询事项" value="refer"></el-option>
              <el-option label="证明事项" value="prove"></el-option>
              <el-option label="便民服务事项" value="convenient_people"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary">查询</el-button>
            <el-button>重置</el-button>
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
            prop="coding"
            label="基本编码"
            min-width="30%">
          </el-table-column>
          <el-table-column
            prop="name"
            label="目录名称"
            min-width="20%">
          </el-table-column>
          <el-table-column
            prop="type"
            label="事项类型"
            min-width="20%">
          </el-table-column>
          <el-table-column
            prop="version"
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
          :current-page="page"
          :page-sizes="[100, 200, 300, 400]"
          :page-size="100"
          layout="total, sizes, prev, pager, next"
          :total="total">
        </el-pagination>
      </div>

    </div>
    <ConvenientPeopleCatalogStickEdit ref="ConvenientPeopleCatalogStickEdit" @refreshDataList="refreshList"></ConvenientPeopleCatalogStickEdit>
  </div>


</template>
<script>
  // 便民目录清单
  import ConvenientPeopleCatalogStickEdit from './ConvenientPeopleCatalogStickEdit'
  export default {
    data () {
      return {
        searchData: {
          user: '',
          branch: '',
          state:''
        },
        total:5,
        page:1,
        tableData: [{
          id:1,
          coding:'012123453412',
          name:'事项接口',
          type:'查询事项',
          version:'v1'
        },
          {
            id:2,
            coding:'02132456',
            name:'便民事项',
            type:'证明事项',
            version:'v1'
          }
        ],
        loading:false
      }
    },
    components: {
      ConvenientPeopleCatalogStickEdit
    },
    methods: {
      handleSizeChange(){

      },
      handleCurrentChange(){

      },
      lookDetails(row){
        console.log(row)
      },
      // 新增
      add() {
        this.$refs.ConvenientPeopleCatalogStickEdit.init('view', {id: '', parent: {id: '', name: ''}})
      },
      refreshList() {
        this.loading = true
        console.log(2)
        // getTreeData2().then(({data}) => {
        //   this.loading = false
        //   if (data && data.success) {
        //     this.dataList = data.data
        //   }
        // })
      },
    }
  }
</script>
<style scoped>


</style>
