<!--
  行政部门Select选择组件

  存在问题：
    1、多选未实现
    2、字段校验提示，存在问题
-->
<template>
  <div @click.stop="toggleInput">
    <el-input
      v-model="selectedValues"
      clearable
      readonly
      @mouseenter.native="inputHovering = true"
      @mouseleave.native="inputHovering = false"
      placeholder="请选机构"
    >
      <template slot="suffix">
        <i v-show="!showClose" :class="['el-select__caret', 'el-input__icon', 'el-icon-arrow-down']"></i>
        <i v-if="showClose" class="el-select__caret el-input__icon el-icon-circle-close el-input__clear"
           @click="handleClearClick" style=""
        ></i>
      </template>
    </el-input>
    <ch-dialog
      :title="dialogTitle"
      :visible.sync="visible"
      width="1080px"
      @submit="submit"
    >
      <el-row :gutter="10">
        <el-col :span="6">
          <el-card shadow="never" :body-style="{height: '440px'}">
            <el-input size="small" placeholder="请输入地区进行查询" suffix-icon="el-icon-search" />
            <el-skeleton :rows="10" animated :loading="treeLoading" style="margin-top: 10px;" />
            <el-tree
              style="margin-top: 20px; height: 360px; overflow-y: auto;"
              :data="treeData"
              :props="{children: 'children', label: 'text'}"
              :default-expanded-keys="['140000']"
              :expand-on-click-node="false"
              node-key="id"
              highlight-current
              @node-click="handleTreeNodeClick"
            />
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card shadow="never">
            <el-row>
              <el-form inline size="small" label-position="left">
                <el-form-item label="" label-width="0px">
                  <el-input
                    v-model="queryParams.orgName"
                    placeholder="输入机构名称筛选"
                    clearable
                    @keyup.enter.native="loadOfficeList" />
                </el-form-item>
                <el-button type="primary" size="small" @click="loadOfficeList">筛选</el-button>
              </el-form>
            </el-row>
            <el-table
              v-loading="tableLoading"
              :data="tableData"
              height="350px"
              highlight-current-row
              @selection-change="handleSelectionChange"
              size="small"
            >
              <el-table-column type="selection" width="50" align="center" />
              <el-table-column label="部门名称" prop="name" show-tooltip-when-overflow />
              <el-table-column label="部门代码" prop="code" />
            </el-table>
<!--            <ch-pagination-->
<!--              v-show="total>0"-->
<!--              :total="total"-->
<!--              :page.sync="pageNo"-->
<!--              :limit.sync="pageSize"-->
<!--              @pagination="loadOfficeList"-->
<!--            />-->
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="never" header="已选择部门" :body-style="{height: '387px', overflowY: 'auto'}">
            <div>
              <el-tag
                v-for="tag in currentRow"
                :key="tag.orgCode"
                closable
                type="primary"
                @close="handleTagCLose(tag)"
              >{{tag.orgName}}</el-tag>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </ch-dialog>
  </div>
</template>

<script>
import ChDialog from '@/components/ChDialog'
import {listRegionTreeData} from "@/api/modules/sys/area";
import {listOfficeData} from "@/api/modules/sys/office";
import emitter from 'element-ui/src/mixins/emitter';
import ChPagination from "@/components/ChPagination";

export default {
  name: "ChOfficeSelect",
  components: {ChPagination, ChDialog },

  mixins: [emitter],

  inject: {
    elForm: {
      default: ''
    },

    elFormItem: {
      default: ''
    }
  },

  model: {
    prop: 'value',
    event: 'select'
  },

  props: {
    value: {
      type: Array,
      default: () => {
        return []
      }
    },

    clearable: Boolean,

    disabled: Boolean,

    placeholder: {
      type: String,
      default: '请选择'
    },
    /**
     * @description 弹出选择面板的标题
     */
    dialogTitle: {
      type: String,
      default: '选择机构部门'
    }
  },
  data() {
    return {
      visible: false,
      treeLoading: false,
      tableLoading: false,

      inputHovering: false,
      // 区化树型数据
      treeData: [],
      // 部门数据
      tableData: [],
      // 组织部门查询参数
      queryParams: {
        regionCode: '140000',
        orgName: undefined,
      },
      total: 0,
      pageNo: 1,
      pageSize: 200,
      currentRow: undefined,
    }
  },
  computed: {
    selectedValues: {
      get() {
        if (this.value.length > 0) {
          return this.value[0]['orgName']
        }
        return ''
      },
      set(val) {
        if (!val) {
          this.$emit('select', '')
        }
      }
    },
    showClose() {
      let hasValue = Array.isArray(this.value) && this.value.length > 0;
      return this.clearable && !this.selectDisabled && this.inputHovering && this.value && hasValue;
    },

    selectDisabled() {
      return this.disabled || (this.elForm || {}).disabled;
    },
  },
  methods: {
    toggleInput() {
      this.visible = true
      // 初始化参数
      this.queryParams = this.$options.data().queryParams
      this.treeData = []
      this.tableData = []
      // 加载区划数据
      this.loadAreaTreeList()
      this.loadOfficeList()
    },
    // 加载区域数据
    loadAreaTreeList() {
      this.treeLoading = true
      listRegionTreeData().then(response => {
        this.treeLoading = false
        if (response.data.code === 200) {
          this.treeData = this.treeDataTranslate(response.data.data, 'id', 'parent')
        }
      })
    },
    // 加载区域下对应行政组织机构数据
    loadOfficeList() {
      this.tableLoading = true
      listOfficeData({
        pageNo: this.pageNo,
        pageSize: this.pageSize,
        ...this.queryParams
      }).then(({data}) => {
        this.tableLoading = false
        if (data.success) {
          this.total = data.data.total
          this.tableData = data.data.records
        }
      }).catch(() => {
        this.tableLoading = false
      })
    },
    handleTreeNodeClick(data, node, self) {
      this.queryParams.regionCode = data.sortCode
      this.loadOfficeList()
    },
    handleClearClick(event) {
      this.deleteSelected(event)
    },
    deleteSelected(event) {
      event.stopPropagation();
      const value = [];
      this.$emit('select', value);
      this.visible = false;
      this.$emit('clear');
    },
    handleSelectionChange(selection) {
      if (selection && selection.length > 0) {
        this.currentRow = selection.map(item => {
          return {
            orgCode: item.code,
            orgName: item.name
          }
        })
      } else {
        this.currentRow = []
      }
    },
    submit() {
      this.$emit('select', this.currentRow)
      this.dispatch('ElFormItem', 'el.form.change', this.currentRow)
      this.visible = false
    },
    handleTagCLose(tag) {
      if (tag) {
        let index = this.currentRow.indexOf(tag);
        //const value = this.currentRow.slice();
        this.currentRow.splice(index, 1);
      }
    }
  }
}
</script>

<style scoped lang="scss">
.el-tag {
  margin-left: 5px;
  margin-bottom: 5px;
}
</style>
