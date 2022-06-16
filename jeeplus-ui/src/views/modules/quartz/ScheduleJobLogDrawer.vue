<!--
  任务执行记录
-->
<template>
  <ch-drawer
    :title="title"
    :visible.sync="visible"
    size="80%"
  >
    <el-row>
      <el-button
        v-if="hasPermission('quartz:scheduleJob:del')"
        type="danger"
        size="small"
        icon="el-icon-delete"
        @click="clean()"
        :disabled="dataList.length <= 0"
        plain
      >清空执行日志</el-button>
      <el-button-group class="pull-right">
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
      v-loading="loading"
      class="table">
      <el-table-column prop="jobName" label="任务名称" show-tooltip-when-overflow />
      <el-table-column prop="jobGroup" label="任务组" width="120" >
        <template slot-scope="scope">
          {{ $dictUtils.getDictLabel("schedule_task_group", scope.row.jobGroup, '--') }}
        </template>
      </el-table-column>
      <el-table-column prop="invokeTarget" label="执行方法" show-tooltip-when-overflow />
      <el-table-column prop="jobMessage" label="执行内容" width="140" show-tooltip-when-overflow />
      <el-table-column prop="status" label="执行状态" width="100" align="center">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.status === '0'" size="small" type="success">执行成功</el-tag>
          <el-tag v-if="scope.row.status === '1'" size="small" type="danger">执行失败</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="exceptionInfo" label="异常信息" show-tooltip-when-overflow />
      <el-table-column prop="startTime" label="开始时间" width="160" />
      <el-table-column prop="stopTime" label="结束时间" width="160" />
    </el-table>
    <ch-pagination
      v-show="total>0"
      :total="total"
      :page.sync="pageNo"
      :limit.sync="pageSize"
      @pagination="refreshList"
    />
  </ch-drawer>
</template>

<script>
import ChDrawer from "@/components/ChDrawer";
import {listScheduleJobLog} from "@/api/modules/sys/quartz";
import ChPagination from "@/components/ChPagination";
import {cleanScheduleJobLogByJobId} from "@/api/modules/sys/quartz";
export default {
  name: "ScheduleJobLogDrawer",
  components: {ChPagination, ChDrawer},
  data() {
    return {
      title: '',
      visible: false,
      searchForm: {
        jobName: ''
      },
      dataList: [],
      pageNo: 1,
      pageSize: 10,
      total: 0,
      jobId: '',
      dataListSelections: [],
      loading: false
    }
  },
  methods: {
    showEdit(job) {
      this.title = job.name
      this.jobId = job.id
      this.refreshList()
      this.visible = true
    },

    // 获取数据列表
    refreshList () {
      this.loading = true
      listScheduleJobLog({
        pageNo: this.pageNo,
        pageSize: this.pageSize,
        jobId: this.jobId,
        ...this.searchForm
      }).then(({data}) => {
        this.loading = false
        if (data && data.success) {
          this.dataList = data.data.records
          this.total = data.data.total
        }
      })
    },

    clean() {
      this.$confirm(`确定清空任务执行日志吗?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.loading = true
        cleanScheduleJobLogByJobId(this.jobId).then(({data}) => {
          this.loading = false
          if (data && data.success) {
            this.$message.success(data.msg)
            this.refreshList()
          }
        })
      })
    }
  }
}
</script>

<style scoped>

</style>
