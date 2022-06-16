<template>
  <el-input v-bind="$attrs">
    <ch-dialog title="选择图标" :visible.sync="visible">

    </ch-dialog>
  </el-input>
</template>

<script>
import ChDialog from "@/components/ChDialog";
export default {
  name: "ChIconInput",
  components: {ChDialog},
  data() {
    return {
      visible: false
    }
  }
}
</script>

<style scoped>

</style>
