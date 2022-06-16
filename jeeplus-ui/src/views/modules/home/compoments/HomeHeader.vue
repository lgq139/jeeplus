<template>
  <div class="home-head">
    <div class="title">{{title}}</div>
    <div v-if="showTip" style="margin-left: 10px;">
      <el-tooltip placement="top">
        <i class="el-icon-warning-outline cursor-pointer"></i>
        <div slot="content">
          <slot name="tip" />
        </div>
      </el-tooltip>
    </div>
  </div>
</template>

<script>
export default {
  props: {
    title: {
      type: String,
      default: ''
    },
    showTip: Boolean
  }
}
</script>

<style scoped lang="scss">
.home-head {
  display: -webkit-box;
  display: -ms-flexbox;
  display: flex;
  align-items: center;
  padding: 10px 0 20px 0;
}
.title {
  font-size: 20px;
  font-weight: bold;
}
</style>
