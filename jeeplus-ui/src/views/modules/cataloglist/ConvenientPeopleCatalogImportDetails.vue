<template>
  <div class="add-shade">
    <ch-dialog
      title="导入详情"
      :visible.sync="visible"
      :isConfirmShow="false"
      v-if="visible"
      width="70%"
    >
      <div class="public-table-box">
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
            min-width="20%">
          </el-table-column>
          <el-table-column
            prop="cataName"
            label="目录名称"
            min-width="20%">
          </el-table-column>
          <el-table-column
            prop="cataType"
            label="目录类型"
            min-width="15%"
            :formatter="statusFormatter">
          </el-table-column>
          <el-table-column
            prop="cataLevel"
            label="目录层级"
            min-width="15%"
            :formatter="levelFormatter">
          </el-table-column>
          <el-table-column
            prop="cataVersion"
            label="目录版本"
            min-width="20%">
          </el-table-column>
          <el-table-column
            prop="remarks"
            label="备注"
            min-width="20%">
          </el-table-column>
          <el-table-column
            prop="importReport"
            class-name="isValid"
            label="导入报告"
            min-width="20%">
            <template slot-scope="scope">
              <div v-if="scope.row.isValid==='1'">{{scope.row.importReport}}</div>
              <div v-else  style="color:#FA5555">{{scope.row.importReport}}</div>
            </template>
          </el-table-column>
        </el-table>
        <el-pagination
          align="right"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="pageNumber"
          :page-sizes="[10, 20, 30, 40]"
          :page-size="pageSize"
          layout="total, sizes, prev, pager, next"
          :total="total">
        </el-pagination>
      </div>
    </ch-dialog>

  </div>
</template>
<script>
  import {lookCatalogListData} from "@/api/modules/catalog/catalogImport";
  import {getStateData} from "@/api/modules/catalog/dict";
  // 便民目录新增、查看、变更
  import ChDialog from "@/components/ChDialog";
  export default {
    data () {
      return {
        visible: false, // 弹框显示状态
        loading: true,
        total:0,
        pageNumber:1,
        pageSize:10,
        tableData: [],
        stateData:[], // 事项类型数据
        cataLevelData:[], //  目录层级数据
      }
    },
    components: {
      ChDialog
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
      init(obj) {
        this.tableData=[]
        this.loading=true
        getStateData({dictType:'con_item_type'}).then(({data})=>{
          this.stateData=data.keyAndValue
        }).then(()=>{
          getStateData({dictType:'item_level'}).then(({data})=>{
            this.cataLevelData=data.keyAndValue
          })
        }).then(()=>{
          lookCatalogListData({pageNo: this.pageNumber, pageSize: this.pageSize,id:obj.id}).then(({data}) => {
              if (data.success){
            console.log(data)
                this.total=data.data.total
                this.tableData=data.data.records
                this.loading=false
                this.visible=true
              }
            })
        })
      },
      // 事项类型转换文字展示
      statusFormatter(row, column){
        const type = row.cataType
        return this.stateData[type]
      },
    //  目录层级转换文字显示
      levelFormatter(row, column){
        const type = row.cataLevel
        return this.cataLevelData[type]
      },
      // 导入报告
    }
  }
</script>
<style scoped>
</style>
