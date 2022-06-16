<template>
  <div class="ch-card-header">
    <div class="title">
      <span>{{ title }}</span>
      <span v-show="!badgeHidden" style=" color: #409eff;">（{{badgeValue}}）</span>
    </div>
    <div class="tool">
      <i class="el-icon-more" @click="(e) => $emit('moreClick', e)"></i>
    </div>
  </div>
</template>

<script>
export default {
  name: "ChCardHeader",
  props: {
    title: {
      type: String,
      default: ''
    },
    badgeValue: Number,
    badgeHidden: {
      type: Boolean,
      default: true
    }
  }
}
</script>

<style scoped lang="scss">
.ch-card-header {
  display: flex;
  .title {
    flex: 1;
  }
  .tool {
    width: 20px;
    cursor: pointer;
  }
}
</style>
