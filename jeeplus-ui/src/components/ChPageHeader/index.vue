<template>
  <div class="ch-page-header">
    <div>
      <span class="title">{{title}}</span>
      <span class="sub_title">{{subTitle}}</span>
    </div>
    <div>
      <slot></slot>
    </div>
  </div>
</template>

<script>
export default {
  name: "ChPageHeader",
  props: {
    title: String,
    subTitle: String
  }
}
</script>

<style scoped lang="scss">
.ch-page-header {
  margin: 0 0 20px 0;
  display: flex;
  justify-content: space-between;
  .title {
    font-size: 24px;
    font-weight: bold;
  }
  .sub_title {
    margin-left: 10px;
    font-size: 14px;
  }
}
</style>
