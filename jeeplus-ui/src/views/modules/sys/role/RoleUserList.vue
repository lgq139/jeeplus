<template>
    <div style="padding:10px">
        <el-form :inline="true" v-show="isSearchCollapse" class="query-form" ref="searchForm" :model="searchForm" @keyup.enter.native="refreshList()" @submit.native.prevent>
          <el-form-item prop="loginName">
            <el-input size="small" v-model="searchForm.loginName" placeholder="登录名" clearable></el-input>
          </el-form-item>
          <el-form-item prop="name">
            <el-input size="small" v-model="searchForm.name" placeholder="姓名" clearable></el-input>
          </el-form-item>
        <el-form-item>
          <el-button  type="primary" @click="refreshList()" size="small">查询</el-button>
          <el-button @click="resetSearch()" size="small">重置</el-button>
        </el-form-item>
        </el-form>

        <el-row>
          <el-button v-if="hasPermission('sys:role:assign')" type="primary" size="small" icon="el-icon-plus" @click="add()">添加用户</el-button>
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
          v-loading="loading"
          @render-header ="elTableHeadFunction"
          border
          size = "medium"
          @selection-change="selectionChangeHandle"
          @sort-change="sortChangeHandle"
          class="table">
          <el-table-column
            type="selection"
            header-align="center"
            align="center"
            width="50">
          </el-table-column>
          <el-table-column
            prop="photo"
            header-align="center"
            align="center"
            label="头像">
            <template slot-scope="scope">
              <img :src="scope.row.photo === ''?'/static/img/avatar.png':scope.row.photo" style="height:50px"/>
            </template>
          </el-table-column>
          <el-table-column
            prop="loginName"
            sortable="custom"
            min-width="100px"
            label="登录名">
          </el-table-column>
          <el-table-column
            prop="name"
            min-width="100px"
            sortable="custom"
            label="用户名">
          </el-table-column>
          <el-table-column
            prop="company.name"
            sortable="custom"
            min-width="120px"
            label="所属机构">
          </el-table-column>
          <el-table-column
            prop="office.name"
            sortable="custom"
            min-width="120px"
            label="所属部门">
          </el-table-column>
          <el-table-column
            prop="email"
            sortable="custom"
            min-width="100px"
            show-overflow-tooltip
            label="邮箱">
          </el-table-column>
          <el-table-column
            prop="mobile"
            sortable="custom"
            show-overflow-tooltip
            min-width="100px"
            label="手机号">
          </el-table-column>
          <el-table-column
            prop="loginFlag"
            min-width="100px"
            label="状态">
            <template slot-scope="scope">
              <el-tag v-if="scope.row.loginFlag === '1'" size="small" type="success">正常</el-tag>
              <el-tag v-else-if="scope.row.loginFlag === '0'" size="small" type="danger">禁用</el-tag>
            </template>
          </el-table-column>
          <el-table-column
            fixed="right"
            header-align="center"
            align="center"
            width="100"
            label="操作">
            <template slot-scope="scope">
              <el-button v-if="hasPermission('sys:role:assign')" type="text" size="small" @click="del(scope.row.id)">
                移除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-pagination
          @size-change="sizeChangeHandle"
          @current-change="currentChangeHandle"
          :current-page="pageNo"
          :page-sizes="[10, 20, 50, 100]"
          :page-size="pageSize"
          :total="total"
          background
          layout="total, sizes, prev, pager, next, jumper">
        </el-pagination>
      <user-select ref="userSelect" @doSubmit="selectUsersToRole"></user-select>
  </div>
</template>

<script>
  import UserSelect from '@/components/userSelect/UserSelectDialog'
  export default {
    data () {
      return {
        searchForm: {
          loginName: '',
          name: ''
        },
        dataList: [],
        pageNo: 1,
        pageSize: 10,
        total: 0,
        orderBy: '',
        dataListSelections: [],
        isSearchCollapse: false,
        isImportCollapse: false,
        loading: false
      }
    },
    components: {
      UserSelect
    },
    props: ['roleUserTitle', 'roleId'],
    activated () {
      this.refreshList()
    },
    methods: {
      elTableHeadFunction (h, l, fontSize) {
        let f = 14
        if (typeof (fontSize) !== 'undefined' && fontSize != null) {
          f = fontSize
        }
// 列头的实际宽度
        let width = l.column.realWidth
// 14：字体大小 32 是el表格的左右 padding 的合
        let maxFontCount = Math.floor((width - 32) / f) - 1
        let chars = l.column.label.split('')
        var label = ''
        if (maxFontCount < chars.length) {
          for (let i = 0; i < maxFontCount; i++) {
            label += chars[i]
          }
          label += '..'
        } else {
          label = l.column.label
        }
        return label
      },

      // 获取数据列表
      refreshList () {
        this.loading = true
        this.$http({
          url: '/sys/role/assign',
          method: 'get',
          params: {
            'pageNo': this.pageNo,
            'pageSize': this.pageSize,
            'orderBy': this.orderBy,
            'role.id': this.roleId,
            ...this.searchForm
          }
        }).then(({data}) => {
          if (data && data.success) {
            this.dataList = data.page.list
            this.total = data.page.count
            this.loading = false
          }
        })
      },
      // 每页数
      sizeChangeHandle (val) {
        this.pageSize = val
        this.pageNo = 1
        this.refreshList()
      },
      // 当前页
      currentChangeHandle (val) {
        this.pageNo = val
        this.refreshList()
      },
      // 多选
      selectionChangeHandle (val) {
        this.dataListSelections = val
      },
       // 排序
      sortChangeHandle (obj) {
        if (obj.prop === 'office.name') {
          obj.prop = 'o.name'
        }
        if (obj.prop === 'company.name') {
          obj.prop = 'c.name'
        }
        if (obj.order === 'ascending') {
          this.orderBy = obj.prop + ' asc'
        } else if (obj.order === 'descending') {
          this.orderBy = obj.prop + ' desc'
        } else {
          this.orderBy = ''
        }
        this.refreshList()
      },
      // 新增
      add () {
        this.$refs.userSelect.init()
      },

      // 删除
      del (id) {
        this.$confirm(`确认把用户从角色中移除吗?`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.loading = true
          this.$http.delete(
            '/sys/role/outrole',
             {params: {userId: id, roleId: this.roleId}}
          ).then(({data}) => {
            this.loading = false
            if (data && data.success) {
              this.$message.success({dangerouslyUseHTMLString: true,
                message: data.msg})
              this.refreshList()
            }
          })
        })
      },
      closeRight () {
        this.$emit('closeRight')
      },
      resetSearch () {
        this.$refs.searchForm.resetFields()
        this.refreshList()
      },
      selectUsersToRole (users) {
        let ids = users.map(user => { return user.id }).join(',')
        this.loading = true
        this.$http.post('/sys/role/assignrole', {id: this.roleId, ids: ids}).then(({data}) => {
          this.loading = false
          if (data && data.success) {
            this.$message.success({dangerouslyUseHTMLString: true,
              message: data.msg})
            this.refreshList()
          }
        })
      }
    }
  }
</script>
