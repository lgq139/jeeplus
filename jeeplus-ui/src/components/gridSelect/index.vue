<template>
  <div>
    <el-input placeholder="请选择" :disabled="disabled" :size="size" :readonly="true" style="line-hight:40px" v-model="name" class="input-with-select" >
      <el-button slot="append" :disabled="disabled" @click="showSelectDialog" icon="el-icon-search"></el-button>
    </el-input>
    <el-dialog
    :title="title"
    :close-on-click-modal="false"
    :append-to-body="true"
     v-dialogDrag
    :visible.sync="visible">
    <el-row :gutter="15">
    <el-col :span="19">
    <el-form :inline="true" ref="searchForm" class="query-form"  @keyup.enter.native="refreshList()" @submit.native.prevent>
         <el-form-item v-for="(search,index) in searchs" :key="index" :prop="search.prop">
          <el-input size="small" v-model="searchForms[index]" :placeholder="search.label" clearable></el-input>
         </el-form-item>
        <el-form-item>
          <el-button  type="primary" @click="refreshList()" size="small">查询</el-button>
          <el-button @click="resetSearch()" size="small">重置</el-button>
        </el-form-item>
      </el-form>
    <el-table
      :data="dataList"
      v-loading="loading"
      border
      size="medium"
      ref="userTable"
      @selection-change="selectionChangeHandle"
      @sort-change="sortChangeHandle"
      style="width: 100%;">
      <el-table-column
        type="selection"
        header-align="center"
        align="center"
        width="50">
      </el-table-column>
      <template   v-for="(column, index) in columns">
      <el-table-column
        :key="index"
        :prop="column.prop"
        header-align="center"
        align="center"
        min-width="100px"
        :label="column.label">
      </el-table-column>
      </template>
    </el-table>
    <el-pagination
      @size-change="sizeChangeHandle"
      @current-change="currentChangeHandle"
      :current-page="pageNo"
      :page-sizes="[5, 10, 50, 100]"
      :page-size="pageSize"
      :total="total"
      layout="total, sizes, prev, pager, next, jumper">
    </el-pagination>
    </el-col>
    <el-col :span="5">
      <el-tag
        :key="tag.id"
        v-for="tag in dataListAllSelections"
        closable
        :disable-transitions="false"
        @close="del(tag)">
        {{tag[labelName]}}
      </el-tag>
    </el-col>
    </el-row>
     <span slot="footer" class="dialog-footer">
      <el-button @click="visible = false">关闭</el-button>
      <el-button type="primary" @click="doSubmit()">确定</el-button>
    </span>
    </el-dialog>
  
  </div>
</template>

<script>
  export default {
    data () {
      return {
        searchForms: [],
        filterText: '',
        dataListAllSelections: [],   // 所有选中的数据包含跨页数据
        dataListSelections: [],
        idKey: 'id', // 标识列表数据中每一行的唯一键的名称(需要按自己的数据改一下)
        dataList: [],
        dynamicTags: [],
        selectData: [],
        pageNo: 1,
        pageSize: 5,
        total: 0,
        orderBy: '',
        loading: false,
        visible: false,
        name: ''
      }
    },
    props: {
      limit: {
        type: Number,
        default: 999999
      },
      columns: {
        type: Array,
        default: () => { return [] }
      },
      disabled: {
        type: Boolean,
        default: false
      },
      searchs: {
        type: Array,
        default: () => { return [] }
      },
      dataListUrl: {
        type: String,
        default: () => { return null }
      },
      queryEntityUrl: {
        type: String,
        default: () => { return null }
      },
      entityBeanName: {
        type: String,
        default: () => { return null }
      },
      value: {
        type: String,
        default: () => { return null }
      },
      title: {
        type: String,
        default: () => { return '' }
      },
      labelName: {
        type: String,
        default: () => { return '' }
      },
      labelValue: {
        type: String,
        default: () => { return '' }
      },
      size: {
        type: String,
        default: () => { return 'default' }
      }
    },
    watch: {
      value: {
        handler (newVal) {
          this.selectData = []
          if (newVal) {
            newVal.split(',').forEach((value) => {
              if (value) {
                this.$http.get(`${this.queryEntityUrl}?${this.labelValue}=${value}`).then(({data}) => {
                  if (data[this.entityBeanName][this.labelValue] !== '') {
                    this.selectData.push(data[this.entityBeanName])
                  }
                })
              }
            })
          }
        },
        immediate: true,
        deep: false
      },
      selectData: {
        handler (newVal) {
          this.dataListAllSelections = JSON.parse(JSON.stringify(this.selectData))
          this.name = this.dataListAllSelections.map(item => { return item[this.labelName] }).join(',')
        },
        immediate: false,
        deep: false
      }
    },
    methods: {
      init () {
        this.visible = true
        this.$nextTick(() => {
          this.resetSearch()
        })
      },
           // 设置选中的方法
      setSelectRow () {
        if (!this.dataListAllSelections || this.dataListAllSelections.length <= 0) {
          this.$refs.userTable.clearSelection()
          return
        }
                // 标识当前行的唯一键的名称
        let idKey = this.idKey
        let selectAllIds = []
        this.dataListAllSelections.forEach(row => {
          selectAllIds.push(row[idKey])
        })
        this.$refs.userTable.clearSelection()
        for (var i = 0; i < this.dataList.length; i++) {
          if (selectAllIds.indexOf(this.dataList[i][idKey]) >= 0) {
                        // 设置选中，记住table组件需要使用ref="table"
            this.$refs.userTable.toggleRowSelection(this.dataList[i], true)
          }
        }
      },
            // 记忆选择核心方法
      changePageCoreRecordData () {
                // 标识当前行的唯一键的名称
        let idKey = this.idKey
        let that = this
              // 如果总记忆中还没有选择的数据，那么就直接取当前页选中的数据，不需要后面一系列计算
        if (this.dataListAllSelections.length <= 0) {
          this.dataListSelections.forEach(row => {
            that.dataListAllSelections.push(row)
          })
          return
        }
                // 总选择里面的key集合
        let selectAllIds = []
        this.dataListAllSelections.forEach(row => {
          selectAllIds.push(row[idKey])
        })
        let selectIds = []
                // 获取当前页选中的id
        this.dataListSelections.forEach(row => {
          selectIds.push(row[idKey])
                  // 如果总选择里面不包含当前页选中的数据，那么就加入到总选择集合里
          if (selectAllIds.indexOf(row[idKey]) < 0) {
            that.dataListAllSelections.push(row)
          }
        })
        let noSelectIds = []
              // 得到当前页没有选中的id
        this.dataList.forEach(row => {
          if (selectIds.indexOf(row[idKey]) < 0) {
            noSelectIds.push(row[idKey])
          }
        })
        noSelectIds.forEach(id => {
          if (selectAllIds.indexOf(id) >= 0) {
            for (let i = 0; i < that.dataListAllSelections.length; i++) {
              if (that.dataListAllSelections[i][idKey] === id) {
                                // 如果总选择中有未被选中的，那么就删除这条
                that.dataListAllSelections.splice(i, 1)
                break
              }
            }
          }
        })
      },
     // 得到选中的所有数据
      getAllSelectionData () {
         // 再执行一次记忆勾选数据匹配，目的是为了在当前页操作勾选后直接获取选中数据
        this.changePageCoreRecordData()
      },
      filterNode (value, data) {
        if (!value) return true
        return data.name.indexOf(value) !== -1
      },
      del (tag) {
        this.dataListAllSelections.splice(this.dataListAllSelections.indexOf(tag), 1)
        this.$nextTick(() => {
          this.setSelectRow()
        })
      },
      // 获取数据列表
      refreshList () {
        this.loading = true
        let searchForm = {}
        this.searchs.forEach((search, index) => {
          searchForm[search.prop] = this.searchForms[index]
        })
        this.$http({
          url: this.dataListUrl,
          method: 'get',
          params: {
            'pageNo': this.pageNo,
            'pageSize': this.pageSize,
            'orderBy': this.orderBy,
            ...searchForm
          }
        }).then(({data}) => {
          if (data && data.success) {
            this.dataList = data.page.list
            this.total = data.page.count
            this.loading = false
          }
          this.$nextTick(() => {
            this.setSelectRow()
          })
        })
      },
      // 每页数
      sizeChangeHandle (val) {
        this.pageSize = val
        this.pageNo = 1
        this.refreshList()
        this.$nextTick(() => {
          this.changePageCoreRecordData()
        })
      },
      // 当前页
      currentChangeHandle (val) {
        this.pageNo = val
        this.refreshList()
        this.$nextTick(() => {
          this.changePageCoreRecordData()
        })
      },
      // 多选
      selectionChangeHandle (val) {
        this.dataListSelections = val
        this.$nextTick(() => {
          this.changePageCoreRecordData()
        })
      },
       // 排序
      sortChangeHandle (obj) {
        if (obj.order === 'ascending') {
          this.orderBy = obj.prop + ' asc'
        } else if (obj.order === 'descending') {
          this.orderBy = obj.prop + ' desc'
        } else {
          this.orderBy = ''
        }
        this.refreshList()
      },
      resetSearch () {
        this.$refs.searchForm.resetFields()
        this.searchForms = []
        this.refreshList()
      },
      doSubmit () {
        if (this.limit < this.dataListAllSelections.length) {
          this.$message.error(`你最多只能选择${this.limit}条数据`)
          return
        }
        this.visible = false
        this.name = this.dataListSelections.map((item) => {
          return item[this.labelName]
        }).join(',')
        let value = this.dataListSelections.map((item) => {
          return item[this.labelValue]
        }).join(',')
        this.$emit('getValue', value)
      },
      showSelectDialog () {
        this.visible = true
        this.init()
      }
    }
  }
</script>
