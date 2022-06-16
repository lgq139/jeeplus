<template>
  <div class="home-sub-card">
    <h3>{{ title }}</h3>
    <div class="ec-tit">
      <slot name="content" />
    </div>
  </div>
</template>

<script>
export default {
  name: "HomeSubCard",
  props: {
    title: {
      type: String,
      default: '自定义标题'
    },
    subTitle: {
      type: String,
      default: '自定义子标题'
    }
  },
}
</script>

<style scoped lang="scss">
.home-sub-card {
  h3 {
    font-size: 16px;
    color: #999999;
    margin-bottom: 8px;
    margin-top: 8px;
  }
  .ec-tit {
    width: 100%;
    padding: 10px 0;
    //background: #F2F3F5;
  }
}
</style>
