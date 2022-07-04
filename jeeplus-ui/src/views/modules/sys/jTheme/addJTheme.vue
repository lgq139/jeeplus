<template>
  <div>
    <ch-dialog
      v-if="open"
      :title="title"
      :visible.sync="open"
      append-to-body
      class="table-card"
      width="700px"
      @close="closeDialog"
      @submit="submitForm">
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="主题名称" prop="themeName">
          <el-input v-model="form.themeName" placeholder="请输入主题名称"></el-input>
        </el-form-item>
        <el-form-item label="主题编码" prop="themeCode">
          <el-input v-model="form.themeCode" placeholder="请输入主题编码"></el-input>
        </el-form-item>
        <el-form-item label="服务对象" prop="serviceObject">
          <el-input v-model="form.serviceObject" placeholder="请输入服务对象"></el-input>
        </el-form-item>
        <el-form-item label="备注(选填)" prop="remarks">
          <el-input v-model="form.remarks" type="textarea"></el-input>
        </el-form-item>
      </el-form>
    </ch-dialog>
  </div>
</template>

<script>
import ChDialog from "@/components/ChDialog";
import {addJTheme, updateJTheme, getJTheme} from "@/api/modules/sys/jTheme";

export default {
  name: "addJTheme",
  components: {
    ChDialog
  },
  props: {
    getList: {
      type: Function,
      default: () => {
      }
    },
  },
  data() {
    return {
      open: false,
      title: "",
      rules: {},
      form: {},
    }
  },
  created() {
  },
  methods: {
    openUpdateDialog(row) {
      const id = row.id;
      getJTheme(id).then(res => {
        console.log(res)
        this.form = res.data.data;
        this.open = true;
        this.title = "修改主题";
      });
    },
    // 表单重置
    reset() {
      this.form = {
        id: null,
        themeName: null,
        themeCode: null,
        serviceObject: null,
        remarks: null,
        sortOrder: null,
        createBy: null,
        createDate: null,
        updateBy: null,
        updateDate: null,
        delFlag: null,
      };
      this.resetForm("form");
    },

    //提交表单
    submitForm() {
      this.$refs["form"].validate((valid) => {
        if (valid) {
          if (this.form.id != null) {
            this.form.updateDate = this.moment().format("YYYY-MM-DD HH:mm:ss");
            updateJTheme(this.form).then((response) => {
              this.$message.success("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            this.form.createDate = this.moment().format("YYYY-MM-DD HH:mm:ss");
            addJTheme(this.form).then((response) => {
              this.$message.success("添加成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    openAddDialog() {
      this.reset();
      this.open = true;
      this.title = "新增主题";
    },
    closeDialog() {
      this.open = false;
    },
  }
}
</script>

<style scoped>

</style>
