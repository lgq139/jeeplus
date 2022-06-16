<template>
  <ch-dialog
    title="修改密码"
    :visible.sync="visible"
    :append-to-body="true"
    @submit="dataFormSubmit"
  >
    <el-form :model="dataForm" :rules="dataRule" ref="dataForm" @keyup.enter.native="dataFormSubmit()"
             label-width="80px" @submit.native.prevent>
      <el-form-item label="账号">
        <span>{{ userName }}</span>
      </el-form-item>
      <el-form-item label="原密码" prop="password">
        <el-input type="password" v-model="dataForm.password"></el-input>
      </el-form-item>
      <el-form-item label="新密码" prop="newPassword">
        <el-input type="password" v-model="dataForm.newPassword"></el-input>
      </el-form-item>
      <el-form-item label="确认密码" prop="confirmPassword">
        <el-input type="password" v-model="dataForm.confirmPassword"></el-input>
      </el-form-item>
    </el-form>
  </ch-dialog>
</template>

<script>
  import {clearLoginInfo} from '@/utils'
  import ChDialog from "@/components/ChDialog";

  export default {
    components: {ChDialog},
    data () {
      let validateConfirmPassword = (rule, value, callback) => {
        if (this.dataForm.newPassword !== value) {
          callback(new Error('确认密码与新密码不一致'))
        } else {
          callback()
        }
      }
      return {
        visible: false,
        dataForm: {
          password: '',
          newPassword: '',
          confirmPassword: ''
        },
        dataRule: {
          password: [
            {required: true, message: '原密码不能为空', trigger: 'blur'}
          ],
          newPassword: [
            {required: true, message: '新密码不能为空', trigger: 'blur'}
          ],
          confirmPassword: [
            {required: true, message: '确认密码不能为空', trigger: 'blur'},
            {validator: validateConfirmPassword, trigger: 'blur'}
          ]
        }
      }
    },
    computed: {
      userName: {
        get () {
          return this.$store.state.user.name
        }
      },
      mainTabs: {
        get () {
          return this.$store.state.common.mainTabs
        },
        set (val) {
          this.$store.commit('common/updateMainTabs', val)
        }
      }
    },
    methods: {
      // 初始化
      init () {
        this.visible = true
        this.$nextTick(() => {
          this.$refs['dataForm'].resetFields()
        })
      },
      // 表单提交
      dataFormSubmit () {
        this.$refs['dataForm'].validate((valid) => {
          if (valid) {
            this.$http({
              url: '/sys/user/savePwd',
              method: 'post',
              data: {
                'oldPassword': this.dataForm.password,
                'newPassword': this.dataForm.newPassword
              }
            }).then(({data}) => {
              if (data && data.code === 0) {
                this.$message({
                  message: '操作成功',
                  type: 'success',
                  duration: 1500
                })
                this.visible = false
                this.$nextTick(() => {
                  this.mainTabs = []
                  clearLoginInfo()
                  this.$router.replace({name: 'login'})
                })
              }
            })
          }
        })
      }
    }
  }
</script>

