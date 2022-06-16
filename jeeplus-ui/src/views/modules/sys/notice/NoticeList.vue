<template>
  <div>
    <vxe-toolbar :refresh="{query: refreshList}" custom>
      <template #buttons>
        <el-button v-if="hasPermission('sys:notice:add')" type="primary" size="small" icon="el-icon-plus" @click="add()">新建</el-button>
      </template>
    </vxe-toolbar>
    <vxe-table
      resizable
      :loading="loading"
      :data="dataList"
      show-overflow="tooltip"
      size="medium"
      class="table"
    >
      <vxe-column field="title" title="标题" width="200" align="left">
        <template slot-scope="scope">
          <span>{{scope.row.title}}</span>
        </template>
      </vxe-column>
      <vxe-column field="enabled" title="发布状态" align="center" width="150">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.enabled === '0'" size="small" type="success">已发布</el-tag>
          <el-tag v-else-if="scope.row.enabled === '1'" size="small">未发布</el-tag>
        </template>
      </vxe-column>
      <vxe-column field="publishTime" align="center" title="发布时间" width="200" />
      <vxe-column field="createDate" align="center" title="创建时间时间" width="200" />
      <vxe-column field="remarks" align="left" title="备注" />
      <vxe-column title="操作" width="150" align="center" fixed="right">
        <template slot-scope="scope">
          <el-button type="text" size="small" @click="view(scope.row)">预览</el-button>
          <el-divider direction="vertical"></el-divider>
          <el-button type="text" size="small" @click="edit(scope.row)">修改</el-button>
          <el-divider direction="vertical"></el-divider>
          <el-button type="text" size="small" @click="del(scope.row.id)">删除</el-button>
        </template>
      </vxe-column>
    </vxe-table>
    <ch-pagination
      v-show="total>0"
      :total="total"
      :page.sync="pageNo"
      :limit.sync="pageSize"
      @pagination="refreshList"
    />

    <notice-form ref="noticeForm" @submit-success="refreshList" />
    <!-- 通告预览 -->
    <notice-preview ref="noticePreview" />
  </div>
</template>

<script>

import {deleteNotice, listNotice} from "@/api/modules/sys/notice";
import ChPagination from "@/components/ChPagination";
import NoticeForm from "@/views/modules/sys/notice/NoticeForm";
import NoticePreview from "@/views/modules/sys/notice/NoticePreview";

export default {
  data() {
    return {
      rightVisible: false,
      loading: false,
      dataRuleTitle: '',
      dataList: [],
      total: 0,
      pageNo: 1,
      pageSize: 10
    }
  },
  components: {
    NoticePreview,
    NoticeForm,
    ChPagination

  },
  activated() {
    this.refreshList()
  },
  methods: {
    // 获取数据列表
    refreshList() {
      this.loading = true
      listNotice({
        pageNo: this.pageNo,
        pageSize: this.pageSize
      }).then(({data}) => {
        this.loading = false
        if (data && data.success) {
          this.total = data.data.total
          this.dataList = data.data.records
        }
      })
    },

    // 新增
    add() {
      this.$refs.noticeForm.showEdit()
      // this.$router.push({
      //   path: '/sys/notice/NoticeEditor'
      // })
    },
    // 修改
    edit(row) {
      this.$refs.noticeForm.showEdit(row)
    },
    // 查看
    view(row) {
      this.$refs.noticePreview.showEdit(row)
    },
    // 删除
    del(id) {
      this.$confirm(`确定删除该条记录吗?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.loading = true
        deleteNotice({ids: id}).then(({data}) => {
          this.loading = false
          if (data && data.success) {
            this.$message({
              message: data.msg,
              type: 'success',
              duration: 1500
            })
            this.refreshList()
          }
        })
      })
    }
  }
}
</script>

<style>
.el-dropdown-link {
  cursor: pointer;
  color: #409EFF;
  font-size: 12px;
  font-weight: 500;
}

.el-icon-arrow-down {
  font-size: 12px;
}
</style>

