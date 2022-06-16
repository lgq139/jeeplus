<template>
  <div>
    <el-form :inline="true" v-show="isSearchCollapse" class="query-form" ref="searchForm" :model="searchForm"
             @keyup.enter.native="refreshList()" @submit.native.prevent>
      <!-- 搜索框-->
      <el-form-item prop="name">
        <el-input size="small" v-model="searchForm.name" placeholder="任务名" clearable></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="refreshList()" size="small">查询</el-button>
        <el-button @click="resetSearch()" size="small">重置</el-button>
      </el-form-item>
    </el-form>
    <el-row>
      <el-button v-if="hasPermission('quartz:scheduleJob:add')" type="primary" size="small" icon="el-icon-plus"
                 @click="add()">新建
      </el-button>
      <el-button v-if="hasPermission('quartz:scheduleJob:edit')" type="warning" size="small" icon="el-icon-edit-outline"
                 @click="edit()"
                 :disabled="dataListSelections.length != 1" plain>修改
      </el-button>
      <el-button v-if="hasPermission('quartz:scheduleJob:del')" type="danger" size="small" icon="el-icon-delete"
                 @click="del()"
                 :disabled="dataListSelections.length <= 0" plain>删除
      </el-button>
      <el-button v-if="hasPermission('quartz:scheduleJob:startNow')" type="success" size="small"
                 icon="el-icon-edit-outline" @click="startNow()"
                 :disabled="dataListSelections.length != 1" plain>立即执行一次
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
      size="medium"
      @selection-change="selectionChangeHandle"
      v-loading="loading"
      class="table">
      <el-table-column type="selection" header-align="center" align="center" width="50"></el-table-column>
      <el-table-column prop="name" label="任务名称" />
      <el-table-column prop="jobGroup" label="任务组" width="120" >
        <template slot-scope="scope">
          {{ $dictUtils.getDictLabel("schedule_task_group", scope.row.jobGroup, '--') }}
        </template>
      </el-table-column>
      <el-table-column prop="cronExpression" label="定时规则" width="140" />
      <el-table-column prop="status" label="执行状态" width="100" align="center">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.status === '1'" size="small" type="success">运行中</el-tag>
          <el-tag v-if="scope.row.status === '0'" size="small" type="info">未执行</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="invokeTarget" label="执行方法"></el-table-column>
      <el-table-column prop="description" label="描述"></el-table-column>
      <el-table-column header-align="center" align="center" width="150" label="操作">
        <template slot-scope="scope">
          <el-button v-if="hasPermission('quartz:scheduleJob:resume') && scope.row.status === '0'" type="text" icon="el-icon-video-play"
                     size="small" @click="start(scope.row.id)">启动
          </el-button>
          <el-button v-if="hasPermission('quartz:scheduleJob:stop')  && scope.row.status === '1'" type="text" icon="el-icon-video-pause" size="small"
                     @click="stop(scope.row.id)">暂停
          </el-button>
          <el-divider direction="vertical" />
          <el-button type="text" size="small" @click="viewJobLog(scope.row)">执行记录</el-button>
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

    <!-- 弹窗, 新增 / 修改 -->
    <ScheduleJobForm ref="scheduleJobForm" @refreshDataList="refreshList" />
    <!-- 任务执行日志 -->
    <ScheduleJobLogDrawer ref="scheduleJobLogDrawer" />
  </div>
</template>

<script>
  import ScheduleJobForm from './ScheduleJobForm'
  import ChPagination from "@/components/ChPagination";
  import {listScheduleJob} from "@/api/modules/sys/quartz";
  import {
    deleteScheduleJob,
    resumeScheduleJob,
    startNowScheduleJob,
    stopScheduleJob
  } from "@/api/modules/sys/quartz";
  import ScheduleJobLogDrawer from "./ScheduleJobLogDrawer";
  export default {
    data () {
      return {
        searchForm: {
          name: ''
        },
        dataList: [],
        pageNo: 1,
        pageSize: 10,
        total: 0,
        dataListSelections: [],
        isSearchCollapse: false,
        isImportCollapse: false,
        loading: false
      }
    },
    components: {
      ScheduleJobLogDrawer,
      ChPagination,
      ScheduleJobForm
    },
    activated () {
      this.refreshList()
    },

    methods: {
      // 获取数据列表
      refreshList () {
        this.loading = true
        listScheduleJob({
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
      // 多选
      selectionChangeHandle (val) {
        this.dataListSelections = val
      },
      // 新增
      add () {
        this.$refs.scheduleJobForm.showEdit('add', '')
      },
      // 修改
      edit (id) {
        id = id || this.dataListSelections.map(item => {
          return item.id
        })[0]
        this.$refs.scheduleJobForm.showEdit('edit', id)
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
          deleteScheduleJob({'ids': ids}).then(({data}) => {
              this.loading = false
              if (data && data.success) {
                this.$message.success(data.msg)
                this.refreshList()
              }
            })
        })
      },
       // 启动
      start(id) {
        id = id || this.dataListSelections.map(item => {
          return item.id
        })[0]
        this.$confirm(`确定要启动任务吗?`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.loading = true
          resumeScheduleJob(id).then(({data}) => {
              this.loading = false
              if (data && data.success) {
                this.$message.success(data.msg)
                this.refreshList()
              }
            })
        })
      },
      // 暂停
      stop (id) {
        id = id || this.dataListSelections.map(item => {
          return item.id
        })[0]
        this.$confirm(`确定要暂停任务吗?`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.loading = true
          stopScheduleJob(id).then(({data}) => {
            this.loading = false
            if (data && data.success) {
              this.$message.success(data.msg)
              this.refreshList()
            }
          })
        })
      },
             // 立即执行一次
      startNow (id) {
        id = id || this.dataListSelections.map(item => {
          return item.id
        })[0]
        this.$confirm(`确定要立即执行一次该任务吗?`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.loading = true
          startNowScheduleJob(id).then(({data}) => {
              this.loading = false
              if (data && data.success) {
                this.$message.success(data.msg)
                this.refreshList()
              }
            })
        })
      },
      resetSearch () {
        this.$refs.searchForm.resetFields()
        this.refreshList()
      },
      // 查看任务执行日志
      viewJobLog(row) {
        this.$refs.scheduleJobLogDrawer.showEdit(row)
      }
    }
  }
</script>
