<template>
  <ch-dialog
    title="权限设置"
    width="450px"
    :visible.sync="visible">
    <el-form class="auth" :model="inputForm" status-icon v-loading="loading" ref="inputForm"
             @keyup.enter.native="inputFormSubmit()"
             @submit.native.prevent>
      <div style="padding: 5px 20px 0 20px;">
        <el-tabs>
          <el-tab-pane>
            <span slot="label"><i class="fa fa-navicon"></i> 菜单权限</span>
            <el-scrollbar :style="{height: maxHeight + 'px'}">
              <el-tree
                :data="menuList"
                :props=" {label: 'name', children: 'children'}"
                node-key="id"
                ref="menuListTree"
                :default-expanded-keys="['1']"
                :default-checked-keys="menuCheckedKeys"
                show-checkbox>
              </el-tree>
            </el-scrollbar>
          </el-tab-pane>
          <el-tab-pane label="数据权限">
            <span slot="label"><i class="fa fa-database"></i> 数据权限</span>
            <el-scrollbar :style="{height: maxHeight + 'px'}">
              <el-tree
                :data="dataRuleList"
                :props=" {label: 'name', children: 'children'}"
                node-key="id"
                ref="dataRuleTree"
                :default-expanded-keys="['1']"
                :default-checked-keys="dataRuleCheckedKeys"
                show-checkbox>
              </el-tree>
            </el-scrollbar>
          </el-tab-pane>
        </el-tabs>
      </div>
    </el-form>
    <template slot="action">
      <el-button type="primary" icon="el-icon-check" size="small" @click="inputFormSubmit()" v-noMoreClick>保存</el-button>
    </template>
  </ch-dialog>
</template>

<script>
import ChDialog from "@/components/ChDialog";
export default {
  components: {ChDialog},
  data() {
    return {
      visible: false,
      loading: false,
      title: '',
      menuList: [],
      dataRuleList: [],
      menuCheckedKeys: [],
      dataRuleCheckedKeys: [],
      inputForm: {
        id: '',
        menuIds: '',
        dataRuleIds: '',
        oldName: '',
        oldEname: ''
      }
    }
  },
  computed: {
    maxHeight () {
      return this.$store.state.common.documentClientHeight - 220
    }
  },
  methods: {
    init(id) {
      this.inputForm.id = id
      this.visible = true
      this.$nextTick(() => {
        this.$refs.inputForm.resetFields()
        this.$refs.menuListTree.setCheckedKeys([])
        this.$refs.dataRuleTree.setCheckedKeys([])
        let p1 = this.$http({
          url: '/sys/menu/treeData',
          method: 'get'
        })
        let p2 = this.$http({
          url: `/sys/dataRule/treeData`,
          method: 'get'
        })
        if (this.inputForm.id) {
          this.loading = true
          let p3 = this.$http({
            url: `/sys/role/queryById?id=${this.inputForm.id}`,
            method: 'get'
          })
          Promise.all([p1, p2, p3]).then((result) => {
            this.menuList = result[0].data.data
            this.dataRuleList = result[1].data.data
            let data = result[2].data
            if (data && data.success) {
              this.loading = false
              this.inputForm = this.recover(this.inputForm, data.role)
              this.inputForm.oldName = data.role.name
              this.inputForm.oldEnname = data.role.enname
              this.$refs.menuListTree.setCheckedKeys(data.role.menuIds.split(','))
              this.$refs.dataRuleTree.setCheckedKeys(data.role.dataRuleIds.split(','))
            }
          })
        } else {
          Promise.all([p1, p2]).then((result) => {
            this.menuList = result[0].data.data
            this.dataRuleList = result[1].data.data
            this.loading = false
          })
        }
      })
    },
    // 表单提交
    inputFormSubmit() {
      this.$refs['inputForm'].validate((valid) => {
        this.inputForm.menuIds = this.$refs.menuListTree.getCheckedKeys().concat(this.$refs.menuListTree.getHalfCheckedKeys()).join(',')
        this.inputForm.dataRuleIds = this.$refs.dataRuleTree.getCheckedKeys().concat(this.$refs.dataRuleTree.getHalfCheckedKeys()).join(',')
        if (valid) {
          this.loading = true
          this.$http({
            url: `/sys/role/save`,
            method: 'post',
            data: this.inputForm
          }).then(({data}) => {
            this.loading = false
            if (data && data.success) {
              this.$message.success(data.msg)
              this.visible = false
              this.$emit('refreshDataList')
            }
          })
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>

.el-tree {
  min-width: 100%;
  display: inline-block !important;
}

.auth {
  margin-top: -30px;
  margin-right: -19px;
  margin-bottom: -30px;
  margin-left: -19px;
}

.el-scrollbar {
  height: 100%;
}

</style>
