<template>
<el-tabs tab-position="left">
    <el-tab-pane label="基本信息">
      <h3 style="margin-left:40px">基本信息</h3>
      <el-form :model="generalForm" ref="generalForm" label-width="100px" size="small">
        <el-form-item label="姓名">
          <el-input v-model="generalForm.name" readonly></el-input>
        </el-form-item>
         <el-form-item label="签名" prop="sign">
          <el-input v-model="generalForm.sign" readonly></el-input>
        </el-form-item>
         <el-form-item label="地区" prop="company.name">
          <el-input v-model="generalForm.regionName" readonly></el-input>
        </el-form-item>
        <el-form-item label="部门" prop="office.name">
          <el-input v-model="generalForm.orgName" readonly></el-input>
        </el-form-item>
        <el-form-item label="角色" prop="roles">
          <el-input v-model="generalForm.roleNames" readonly></el-input>
        </el-form-item>
        <el-form-item label="备注" prop="remarks">
          <el-input v-model="generalForm.remarks" readonly></el-input>
        </el-form-item>
      </el-form>
    </el-tab-pane>
    <el-tab-pane label="联系方式">
      <h3 style="margin-left:40px">联系方式</h3>
      <el-form :model="concatForm" ref="concatForm" label-width="100px" size="small">
        <el-form-item label="邮箱" :rules="[{type:'email', message:'请输入正确的邮箱地址', trigger:'blur'}]">
          <el-input v-model="concatForm.email" readonly></el-input>
        </el-form-item>
         <el-form-item label="手机" prop="mobile" :rules="[{validator:validator.isMobile, trigger:'blur'}]">
          <el-input v-model="concatForm.mobile" readonly></el-input>
        </el-form-item>
         <el-form-item label="电话" prop="phone" :rules="[{validator:validator.isPhone, trigger:'blur'}]">
          <el-input v-model="concatForm.phone" readonly></el-input>
        </el-form-item>
      </el-form>
    </el-tab-pane>
    <el-tab-pane label="我的日志">
        <el-menu  default-active="1" mode="horizontal" @select="changeLog">
          <el-menu-item index="1">
            <i class="el-icon-setting"></i>
            <span slot="title">登陆日志</span>
          </el-menu-item>
          <el-menu-item index="2">
            <i class="el-icon-setting"></i>
            <span slot="title">访问日志</span>
          </el-menu-item>
        </el-menu>
        <el-table :data="dataList" border v-loading = "loading" class="table">
          <el-table-column prop="title" header-align="center" align="center" label="操作菜单"></el-table-column>
          <el-table-column prop="orgName" header-align="center" align="center" label="归属机构"></el-table-column>
          <el-table-column prop="requestUri" header-align="center" align="center" width="150" :show-overflow-tooltip="true" label="URI"></el-table-column>
          <el-table-column prop="method" header-align="center" align="center" width="150" :show-overflow-tooltip="true" label="提交方式"></el-table-column>
          <el-table-column prop="remoteAddr" header-align="center" align="center" width="150" :show-overflow-tooltip="true" label="操作者IP"></el-table-column>
          <el-table-column prop="createDate" header-align="center" align="center" width="150" :show-overflow-tooltip="true" label="操作时间"></el-table-column>
        </el-table>
      <ch-pagination
        v-show="total>0"
        :total="total"
        :page.sync="pageNo"
        :limit.sync="pageSize"
        @pagination="refreshList"
      />
    </el-tab-pane>
  </el-tabs>
</template>

<script>
  import config from '@/config'
  import ChPagination from "@/components/ChPagination";
  import {listLogDataMine} from "@/api/modules/sys/log";

  export default {
    components: {ChPagination},
    data () {
      var validatePass2 = (rule, value, callback) => {
        if (value !== this.pwdForm.newPassword) {
          callback(new Error('两次输入密码不一致!'))
        } else {
          callback()
        }
      }
      return {
        config: config,
        id: '',
        type: '1',
        user: null,
        generalForm: {
          name: '',
          sign: '',
          regionName: '',
          orgName: '',
          roleNames: '',
          company: {
            name: ''
          },
          office: {
            name: ''
          },
          remarks: ''
        },
        concatForm: {
          email: '',
          phone: '',
          mobile: ''
        },
        pwdForm: {
          oldPassword: '',
          newPassword: '',
          confirmNewPassword: ''
        },
        photo: '',
        dataList: [],
        pageNo: 1,
        pageSize: 10,
        total: 0,
        loading: false,
        rule: [{ required: true, message: '必填项不能为空', trigger: 'blur' }, {validator: validatePass2, trigger: 'blur'}]
      }
    },
    watch: {
      '$store.state.user.id': {
        handler (userId) {
          if (userId) {
            this.$http.get(`/sys/user/queryById?id=${userId}`).then(({data}) => {
              this.recover(this.generalForm, data.user)
              this.recover(this.concatForm, data.user)
              this.photo = data.user.photo
            })
            this.refreshList()
          }
        },

        immediate: true,
        deep: false
      }
    },
    methods: {
      submitGeneralForm () {
        this.$refs['generalForm'].validate((valid) => {
          if (valid) {
            const loading = this.$loading({
              lock: true,
              text: 'Loading',
              spinner: 'el-icon-loading',
              background: 'rgba(0, 0, 0, 0.7)'
            })
            this.$http({
              url: `/sys/user/saveInfo`,
              method: 'post',
              headers: {arrayFormat: 'repeat'},
              data: this.generalForm
            }).then(({data}) => {
              loading.close()
              if (data && data.success) {
                this.$store.commit('user/updateName', this.generalForm.name)
                this.$message.success(data.msg)
              }
            })
          }
        })
      },
      submitConcatForm () {
        this.$refs['concatForm'].validate((valid) => {
          if (valid) {
            const loading = this.$loading({
              lock: true,
              text: 'Loading',
              spinner: 'el-icon-loading',
              background: 'rgba(0, 0, 0, 0.7)'
            })
            this.$http({
              url: `/sys/user/saveInfo`,
              method: 'post',
              headers: {arrayFormat: 'repeat'},
              data: this.concatForm
            }).then(({data}) => {
              if (data && data.success) {
                this.$message.success(data.msg)
              }
              loading.close()
            })
          }
        })
      },
      submitPwdForm () {
        this.$refs['pwdForm'].validate((valid) => {
          if (valid) {
            const loading = this.$loading({
              lock: true,
              text: 'Loading',
              spinner: 'el-icon-loading',
              background: 'rgba(0, 0, 0, 0.7)'
            })
            this.$http({
              url: `/sys/user/savePwd`,
              method: 'post',
              headers: {arrayFormat: 'repeat'},
              data: this.pwdForm
            }).then(({data}) => {
              if (data && data.success) {
                this.$message.success(data.msg)
              }
              loading.close()
            })
          }
        })
      },
      // 获取数据列表
      refreshList () {
        this.loading = true
        listLogDataMine({
          'pageNo': this.pageNo,
          'pageSize': this.pageSize,
          type: this.type,
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
        this.type = index
        this.refreshList()
      },
      handleAvatarSuccess (res, file) {
        this.photo = res.path
        this.$message.success(res.msg)
        this.$store.commit('user/updatePhoto', res.path)
      },

      beforeAvatarUpload (file) {
        const isJPG = file.type.indexOf('image/') >= 0
        const isLt2M = file.size / 1024 / 1024 < 5

        if (!isJPG) {
          this.$message.error('上传头像只能是图片格式!')
          return false
        }
        if (!isLt2M) {
          this.$message.error('上传头像图片大小不能超过 2MB!')
          return false
        }
        return true
      }
    }
  }
</script>

