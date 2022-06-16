<template>
  <div>
    <ch-dialog
      :title="title"
      :close-on-click-modal="false"
      v-dialogDrag
      :visible.sync="visible">
      <el-form :model="inputForm"
               :rules="rules"
               size="small"
               ref="inputForm"
               v-loading="loading"
               :class="method==='view'?'readonly':''"
               :disabled="method==='view'"
               @keyup.enter.native="doSubmit()"
               label-width="120px"
               @submit.native.prevent>
        <el-row :gutter="15">
          <el-col :span="24">
            <el-form-item label="任务名" prop="name">
              <el-input v-model="inputForm.name" placeholder="请填写任务名"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="任务组" prop="jobGroup">
              <el-select v-model="inputForm.jobGroup" placeholder="请选择" style="width: 100%;">
                <el-option
                  v-for="(item, index) in $dictUtils.getDictList('schedule_task_group')"
                  :key="index"
                  :label="item.label"
                  :value="item.value">
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="定时规则" prop="cronExpression">
              <el-input
                v-model="inputForm.cronExpression"
                placeholder="请填写定时规则"
                @focus="showTimeConfig('cronExpression','定时规则')"
              ></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="调用目标任务" prop="invokeTarget">
              <el-input v-model="inputForm.invokeTarget" placeholder="请填写任务类"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="启用并发执行">
              <el-select v-model="inputForm.concurrent" placeholder="请选择" style="width: 100%;">
                <el-option
                  v-for="item in $dictUtils.getDictList('yes_no')"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value">
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="描述" prop="description">
              <el-input type="textarea" v-model="inputForm.description" placeholder="请填写描述"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template slot="action">
        <el-button @click="visible = false">关闭</el-button>
        <el-button type="primary" v-if="method != 'view'" @click="doSubmit()" v-noMoreClick>确定</el-button>
      </template>
    </ch-dialog>
    <ch-dialog
      :title="dialogTitle"
      :visible.sync="dialogFormVisible"
    >
      <Cron v-model="inputForm[timeType]" v-if="dialogFormVisible"/>
      <template slot="action">
        <el-button @click="hideTimeConfig('cancel')">清 空</el-button>
        <el-button type="primary" @click="dialogFormVisible = false">确 定</el-button>
      </template>
    </ch-dialog>
  </div>
</template>

<script>
  import Cron from '@/components/Cron/Cron.vue'
  import ChDialog from "@/components/ChDialog";
  import {getScheduleJob, saveScheduleJob} from "@/api/modules/sys/quartz";

  export default {
    data () {
      return {
        dialogFormVisible: false,
        dialogTitle: '配置时间',
        timeType: '',
        title: '',
        method: '',
        visible: false,
        loading: false,
        inputForm: {
          id: undefined,
          name: undefined,
          jobGroup: 'DEFAULT',
          cronExpression: undefined,
          status: '0',
          concurrent: '0',
          invokeTarget: undefined,
          description: undefined
        },
        rules: {
          name: [{required: true, message:'任务名不能为空', trigger:'blur'}],
          jobGroup: [{required: true, message:'任务组不能为空', trigger:'blur'}],
          cronExpression: [{required: true, message:'定时规则不能为空', trigger:'blur'}],
          invokeTarget: [{required: true, message:'调用目标不能为空', trigger:'blur'}],
        },
      }
    },
    components: {
      ChDialog,
      Cron
    },
    methods: {
      showTimeConfig (type, title) {
        this.timeType = type
        this.dialogTitle = '配置' + title
        this.dialogFormVisible = true
      },
      hideTimeConfig () {
        this.inputForm[this.timeType] = ''
        this.dialogFormVisible = false
        this.timeType = ''
      },
      showEdit(method, id) {
        this.method = method
        this.inputForm.id = id
        if (method === 'add') {
          this.title = `新建定时任务`
        } else if (method === 'edit') {
          this.title = '修改定时任务'
        } else if (method === 'view') {
          this.title = '查看定时任务'
        }
        this.visible = true
        this.loading = false
        this.$nextTick(() => {
          this.$refs.inputForm.resetFields()
          if (method === 'edit' || method === 'view') { // 修改或者查看
            this.loading = true
            getScheduleJob(this.inputForm.id).then(({data}) => {
              this.loading = false
              if (data && data.success) {
                this.inputForm = this.recover(this.inputForm, data.data)
              }
            })
          }
        })
      },
      // 表单提交
      doSubmit () {
        this.$refs['inputForm'].validate((valid) => {
          if (valid) {
            this.loading = true
            saveScheduleJob(this.inputForm).then(({data}) => {
              this.loading = false
              if (data && data.success) {
                this.$message.success(data.msg)
                this.$emit('refreshDataList')
                this.visible = false
              }
            })
          }
        })
      }
    }
  }
</script>
