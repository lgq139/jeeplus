<!--
  自定义抽屉组件，主要变更一些基础样式
-->
<template>
  <el-drawer
    class="ch-drawer"
    :visible.sync="show"
    :modal="false"
    v-bind="$attrs"
  >
    <slot />
  </el-drawer>
</template>

<script>
export default {
  name: "ChDrawer",
  props: {
    visible: Boolean,
  },
  computed: {
    show: {
      get() {
        return this.visible
      },
      set(value) {
        this.$emit('update:visible', value)
      }
    }
  }
}
</script>

<style lang="scss">
@import "~@/assets/scss/theme.scss";

.ch-drawer {
  .el-drawer__header {
    padding: 16px 20px;
    margin-bottom: 0;
    border-bottom: 1px solid $border-color-base;
    > span {
      font-weight: bold;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }
  }
  .el-drawer__body {
    padding: 20px;
  }
}
</style>
