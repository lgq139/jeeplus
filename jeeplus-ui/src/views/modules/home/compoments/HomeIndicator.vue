<!--
  数据指标
-->
<template>
  <div class="home-indicator">
    <div v-if="$slots['img']">
      <slot name="img" />
    </div>
    <div class="count_card">
      <div class="count_card__title">{{ title }}</div>
      <div class="count_card__body">
        <count-to :startVal='0' :endVal='endVal' :duration='2000'></count-to>
      </div>
    </div>
  </div>
</template>

<script>
import countTo from 'vue-count-to';
export default {
  components: {
    countTo
  },
  model: {
    prop: 'endVal'
  },
  props: {
    endVal: Number,
    title: {
      type: String,
      default: ''
    }
  }
}
</script>

<style scoped lang="scss">
.home-indicator {
  display: flex;
  align-items: center;
  .stat-img {
    width: 40px;
    height: 40px;
    margin-right: 20px;
  }
  .count_card {
    float: left;
    min-width: 100px;
    &__title {
      font-size: 14px;
      color: #999999;
      line-height: 22px;
      padding-bottom: 8px;
    }
    &__body {
      font-size: 32px;
      font-weight: bold;
      color: #1A1A1A;
      line-height: 50px;

      &:hover {
        cursor: pointer;
        color: #1890ff;
      }
    }
  }
}
</style>
