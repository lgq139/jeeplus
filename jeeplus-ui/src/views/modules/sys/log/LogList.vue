<template>
  <div>
    <el-row :gutter="15">
      <el-col :span="4">
        <el-menu @select="changeLog">
          <el-menu-item index="1">
            <i class="el-icon-setting"></i>
            <span slot="title">登陆日志</span>
          </el-menu-item>
          <el-menu-item index="2">
            <i class="el-icon-setting"></i>
            <span slot="title">访问日志</span>
          </el-menu-item>
          <el-menu-item index="3">
            <i class="el-icon-setting"></i>
            <span slot="title">异常日志</span>
          </el-menu-item>
        </el-menu>
      </el-col>
      <el-col :span="20">
      <el-form :inline="true" v-show="isSearchCollapse" class="query-form" ref="searchForm" :model="searchForm" @keyup.enter.native="refreshList()" @submit.native.prevent>
        <el-form-item prop="searchDates">
          <el-date-picker
            v-model="searchDates"
            type="daterange"
            size="small"
            align="right"
            value-format="yyyy-MM-dd hh:mm:ss"
            unlink-panels
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            :picker-options="pickerOptions">
          </el-date-picker>
        </el-form-item>
        <el-form-item prop="title">
            <el-input size="small" v-model="searchForm.title" placeholder="操作菜单" clearable></el-input>
        </el-form-item>
        <el-form-item prop="createBy.name">
            <el-input size="small" v-model="searchForm.createBy.name" placeholder="操作用户" clearable></el-input>
        </el-form-item>
        <el-form-item prop="requestUri">
            <el-input size="small" v-model="searchForm.requestUri" placeholder="URI" clearable></el-input>
        </el-form-item>
        <el-form-item>
          <el-button  type="primary" @click="refreshList()" size="small">查询</el-button>
          <el-button @click="resetSearch()" size="small">重置</el-button>
        </el-form-item>
      </el-form>
      <el-row>
        <el-button v-if="hasPermission('sys:log:del')" type="danger"   size="small" icon="el-icon-delete" @click="del()"
                  :disabled="dataListSelections.length <= 0" plain>删除
        </el-button>
         <el-button v-if="hasPermission('sys:log:del')" type="danger"   size="small" icon="el-icon-delete" @click="empty()" plain>清空
        </el-button>
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
          border
          v-loading = "loading"
          size="medium"
          @selection-change="selectionChangeHandle"
          class="table">
          <el-table-column type="selection" header-align="center" align="center" width="50" />
          <el-table-column v-if="searchForm.type == '3'" type="expand">
            <template slot-scope="props">
              <el-form label-position="left" inline class="demo-table-expand">
                <el-form-item label="异常信息：">
                  <span style="color:red">{{ props.row.exception }}</span>
                </el-form-item>
              </el-form>
            </template>
          </el-table-column>
          <el-table-column prop="title" show-overflow-tooltip width="150" label="操作菜单" />
          <el-table-column prop="userName" label="操作用户" />
          <el-table-column prop="orgName" label="归属机构" />
          <el-table-column prop="requestUri" width="150" show-overflow-tooltip label="URI" />
          <el-table-column prop="method" width="100" show-overflow-tooltip label="提交方式" />
          <el-table-column prop="remoteAddr" width="100" :show-overflow-tooltip="true" label="操作者IP" />
          <el-table-column prop="createDate" width="150" :show-overflow-tooltip="true" label="操作时间" />
        </el-table>
        <ch-pagination
          v-show="total>0"
          :total="total"
          :page.sync="pageNo"
          :limit.sync="pageSize"
          @pagination="refreshList"
        />
      </el-col>
    </el-row>
  </div>
</template>

<script>
  import ChPagination from "@/components/ChPagination";
  import {listLog} from "@/api/modules/sys/log";
  export default {
    components: {ChPagination},
    data () {
      return {
        searchForm: {
          type: '1',
          title: '',
          createBy: {
            name: ''
          },
          requestUri: '',
          beginDate: '',
          endDate: ''
        },
        searchDates: [],
        dataList: [],
        pageNo: 1,
        pageSize: 10,
        total: 0,
        isSearchCollapse: false,
        loading: false,
        dataListSelections: [],
        pickerOptions: {
          shortcuts: [{
            text: '最近一周',
            onClick (picker) {
              const end = new Date()
              const start = new Date()
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 7)
              picker.$emit('pick', [start, end])
            }
          }, {
            text: '最近一个月',
            onClick (picker) {
              const end = new Date()
              const start = new Date()
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 30)
              picker.$emit('pick', [start, end])
            }
          }, {
            text: '最近三个月',
            onClick (picker) {
              const end = new Date()
              const start = new Date()
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 90)
              picker.$emit('pick', [start, end])
            }
          }]
        }
      }
    },
    activated () {
      this.refreshList()
    },
    watch: {
      searchDates () {
        if (this.searchDates) {
          this.searchForm.beginDate = this.searchDates[0]
          this.searchForm.endDate = this.searchDates[1]
        } else {
          this.searchForm.beginDate = ''
          this.searchForm.endDate = ''
        }
      }
    },
    methods: {
      // 获取数据列表
      refreshList () {
        this.loading = true
        listLog({
          'pageNo': this.pageNo,
          'pageSize': this.pageSize,
          ...this.searchForm
        }).then(({data}) => {
          this.loading = false
          if (data && data.success) {
            this.dataList = data.data.records
            this.total = data.data.total
          }
        })
      },
      changeLog (index) {
        this.searchForm.type = index
        this.refreshList()
      },
         // 多选
      selectionChangeHandle (val) {
        this.dataListSelections = val
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
          this.$http.delete(
             '/sys/log/delete',
             {params: {'ids': ids}}
          ).then(({data}) => {
            if (data && data.success) {
              this.$message.success({dangerouslyUseHTMLString: true,
                message: data.msg})
              this.refreshList()
            }
          })
        })
      },
      empty () {
        this.$confirm(`确定清空日志吗?`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.$http.delete(
             '/sys/log/empty'
          ).then(({data}) => {
            if (data && data.success) {
              this.$message.success({dangerouslyUseHTMLString: true,
                message: data.msg})
              this.refreshList()
            }
          })
        })
      },
      resetSearch () {
        this.searchDates = []
        this.$refs.searchForm.resetFields()
        this.$nextTick(() => {
          this.refreshList()
        })
      }
    }
  }
</script>
