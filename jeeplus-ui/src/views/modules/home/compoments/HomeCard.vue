<template>
  <el-card shadow="never">
    <slot name="card-head"></slot>
    <slot name="card-content"></slot>
    <slot name="card-footer"></slot>
  </el-card>
</template>

<script>
export default {
}
</script>
<style scoped>
.el-card__body {
  padding: 10px !important;
}
</style>
