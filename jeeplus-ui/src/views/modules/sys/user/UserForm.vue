<template>
  <el-dialog
    :title="title"
    :close-on-click-modal="false"
     v-dialogDrag
    :visible.sync="visible">
    <el-form :model="inputForm" :rules="dataRule" ref="inputForm" @keyup.enter.native="doSubmit()"
             label-width="120px" v-loading="loading" :class="method==='view'?'readonly':''" :disabled="method==='view'" @submit.native.prevent>
    <el-row :gutter="15">
        <el-col :span="12">
          <el-form-item prop="photo" label="头像">
            <el-upload
              class="avatar-uploader"
              :action="`${this.$http.BASE_URL}/sys/file/webupload/upload?uploadPath=photo`"
              :headers="{token: $cookie.get(config.tokenName)}"
              :on-success="handleAvatarSuccess"
              :before-upload="beforeAvatarUpload"
               :show-file-list="false">
              <img v-if="inputForm.photo" :src="inputForm.photo" class="avatar">
              <i v-else class="el-icon-plus avatar-uploader-icon"></i>
            </el-upload>
          </el-form-item>
        </el-col>
        <el-col :span="12">
           <el-form-item label="登录名" prop="loginName">
              <el-input v-model="inputForm.loginName" maxlength="50" placeholder=""></el-input>
            </el-form-item>
        </el-col>
        <el-col :span="12">
            <el-form-item label="姓名" prop="name">
              <el-input v-model="inputForm.name" maxlength="50" placeholder=""></el-input>
            </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item prop="company.id" label="公司">
            <SelectTree
              ref="companyTree"
              :props="{
                  value: 'id',             // ID字段名
                  label: 'name',         // 显示名称
                  children: 'children'    // 子级字段名
                }"
              :url="`/sys/office/treeData?type=1&&extId=${this.inputForm.id}`"
              :value="inputForm.company.id"
              :clearable="true"
              :accordion="true"
              @getValue="(value) => {inputForm.company.id=value}"/>
          </el-form-item>
        </el-col>
        <el-col :span="12">
           <el-form-item prop="office.id" label="部门">
            <SelectTree
              ref="officeTree"
              :props="{
                  value: 'id',             // ID字段名
                  label: 'name',         // 显示名称
                  children: 'children'    // 子级字段名
                }"

              :url="`/sys/office/treeData?type=2&&extId=${this.inputForm.id}`"
              :value="inputForm.office.id"
              :clearable="true"
              :accordion="true"
              @getValue="(value) => {inputForm.office.id=value}"/>
          </el-form-item>
        </el-col>
        <el-col :span="12">
            <el-form-item label="工号" prop="no" :rules="[{required: true, message:'工号不能为空', trigger:'blur'}]">
              <el-input v-model="inputForm.no" maxlength="50" placeholder=""></el-input>
            </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="岗位" prop="post.id">
               <el-select v-model="inputForm.post.id" style="width:100%" placeholder="请选择">
                <el-option
                  v-for="item in postList"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id">
                </el-option>
              </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="角色" prop="roleIdList">
              <el-checkbox-group
                v-model="inputForm.roleIdList">
                <el-checkbox v-for="role in roleList" :label="role.id" :key="role.id">{{role.name}}</el-checkbox>
              </el-checkbox-group>
          </el-form-item>
        </el-col>

        <el-col :span="12">
           <el-form-item label="密码:" prop="newPassword" :rules="inputForm.id?[]:[{required: true, message:'密码不能为空', trigger:'blur'}]">
              <el-input v-model="inputForm.newPassword" maxlength="50" placeholder="若不修改，请留空" show-password></el-input>
            </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="确认密码" prop="confirmNewPassword" :rules="inputForm.id?[]:[{required: true, message:'确认密码不能为空', trigger:'blur'}]">
            <el-input v-model="inputForm.confirmNewPassword" maxlength="50" placeholder="" show-password></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="手机" prop="mobile" :rules="[{validator:validator.isMobile, trigger:'blur'}]">
            <el-input v-model="inputForm.mobile" maxlength="50" placeholder=""></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
           <el-form-item label="电话" prop="phone" :rules="[{validator:validator.isPhone, trigger:'blur'}]">
            <el-input v-model="inputForm.phone" maxlength="50" placeholder=""></el-input>
           </el-form-item>
        </el-col>
        <el-col :span="12">
           <el-form-item label="邮箱" prop="email" :rules="[{type:'email', message:'请输入正确的邮箱地址', trigger:'blur'}]">
            <el-input v-model="inputForm.email" maxlength="50" placeholder=""></el-input>
           </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="是否允许登录" prop="loginFlag">
            <el-radio-group v-model="inputForm.loginFlag">
              <el-radio v-for="item in this.$dictUtils.getDictList('yes_no')" :label="item.value" :key="item.id">{{item.label}}</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>

         <el-col :span="12">
           <el-form-item label="备注" prop="remarks">
            <el-input type="textarea" v-model="inputForm.remarks"  maxlength="200" placeholder=""></el-input>
           </el-form-item>
        </el-col>
    </el-row>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="visible = false">关闭</el-button>
      <el-button type="primary" v-if="method != 'view'" @click="doSubmit()" v-noMoreClick>确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import SelectTree from '@/components/treeSelect/treeSelect.vue'
import config from '@/config'
export default {
  data () {
    var validatePass2 = (rule, value, callback) => {
      if (value !== this.inputForm.newPassword) {
        callback(new Error('两次输入密码不一致!'))
      } else {
        callback()
      }
    }
    return {
      config: config,
      visible: false,
      loading: false,
      title: '',
      method: '',
      roleList: [],
      postList: [],
      inputForm: {
        id: '',
        company: { // 归属公司
          id: '',
          name: ''
        },
        office: {
          id: '',
          name: ''
        },
        post: {
          id: '',
          name: ''
        },
        roleIdList: [],
        loginName: '', // 登录名
        no: '', // 工号
        name: '', // 姓名
        email: '', // 邮箱
        phone: '', // 电话
        mobile: '', // 手机
        loginIp: '', // 最后登陆IP
        loginDate: '', // 最后登陆日期
        loginFlag: '1', // 是否允许登陆
        photo: '', // 头像
        qrCode: '', // 二维码
        oldLoginName: '', // 原登录名
        newPassword: '', // 新密码
        confirmNewPassword: '',
        sign: '', // 签名
        oldLoginIp: '', // 上次登陆IP
        oldLoginDate: '', // 上次登陆日期,
        remarks: '' // 备注

      },
      dataRule: {
        'company.id': [{required: true, message: '公司不能为空', trigger: 'blur'}],
        'office.id': [{required: true, message: '部门不能为空', trigger: 'blur'}],
        name: [{required: true, message: '姓名不能为空', trigger: 'blur'}],
        loginName: [{required: true, message: '登录名不能为空', trigger: 'blur'}],
        roleIdList: [{required: true, message: '角色不能为空', trigger: 'blur'}],
        confirmNewPassword: [{validator: validatePass2, trigger: 'blur'}]
      }
    }
  },
  components: {
    SelectTree
  },
  methods: {
    init (method, id) {
      this.method = method
      this.inputForm.id = id
      if (method === 'add') {
        this.title = `新建用户`
      } else if (method === 'edit') {
        this.title = '修改用户'
      } else if (method === 'view') {
        this.title = '查看用户'
      }
      this.visible = true
      this.$nextTick(() => {
        this.$refs.inputForm.resetFields()
        this.inputForm.oldLoinName = ''
        this.$http({
          url: `/sys/role/list?pageNo=1&pageSize=-1`,
          method: 'get'
        }).then(({data}) => {
          this.roleList = data.page.list
        })
        this.$http({
          url: `/sys/post/list?pageNo=1&pageSize=-1&status=1`,
          method: 'get'
        }).then(({data}) => {
          this.postList = data.page.list
        })

        if (method === 'edit' || method === 'view') { // 修改或者查看
          this.$http({
            url: `/sys/user/queryById?id=${this.inputForm.id}`,
            method: 'get'
          }).then(({data}) => {
            this.inputForm = this.recover(this.inputForm, data.user)
            this.inputForm.oldLoginName = this.inputForm.loginName
          })
        }
      })
    },

    handleAvatarSuccess (res, file) {
      this.inputForm.photo = res.url
    },

    beforeAvatarUpload (file) {
      const isJPG = file.type.indexOf('image/') >= 0
      const isLt2M = file.size / 1024 / 1024 < 2

      if (!isJPG) {
        this.$message.error('上传头像只能是图片格式!')
        return false
      }
      if (!isLt2M) {
        this.$message.error('上传头像图片大小不能超过 2MB!')
        return false
      }
      return true
    },
      // 表单提交
    doSubmit () {
      this.$refs['inputForm'].validate((valid) => {
        if (valid) {
          this.loading = true
          this.$http({
            url: `/sys/user/save`,
            method: 'post',
            headers: {arrayFormat: 'repeat'},
            data: this.inputForm
          }).then(({data}) => {
            this.loading = false
            if (data && data.success) {
              this.visible = false
              this.$message.success(data.msg)
              this.$emit('refreshDataList')
            }
          })
        }
      })
    }
  }
}
</script>
<style scoped>
.avatar{
  height: 100px;
}
</style>
