<template>
  <chart-card>
    <div slot="card-head" style="margin-bottom: 10px;">
      <div class="flex justify-between">
        <div style="font-weight: bold;">四级四同目录总数</div>
        <div>
          <el-tooltip content="指标说明" placement="top">
            <i class="el-icon-warning-outline cursor-pointer"></i>
          </el-tooltip>
        </div>
      </div>
    </div>
    <div slot="card-content">
      <div class="text-3xl">
        <count-to :start-val="0" :end-val="87654" :duration="3000" class="card-panel-num"/>
      </div>
    </div>
  </chart-card>
</template>

<script>
import ChartCard from './ChartCard'
import dayjs from 'dayjs'
import CountTo from 'vue-count-to'
export default {
  name: "CountCard",
  components: {
    ChartCard,
    CountTo
  }
}
</script>

<style scoped lang="scss">
.echarts {
  width: 100%;
  height: 60px;
}

.justify-between {
  -webkit-box-pack: justify;
  -ms-flex-pack: justify;
  justify-content: space-between;
}

.flex {
  display: -webkit-box;
  display: -ms-flexbox;
  display: flex;
}

.text-3xl {
  font-size: 1.875rem;
}

.pt-3 {
  padding-top: 0.75rem;
}

.border-t {
  border-top-width: 1px;
}

.border-solid {
  border-style: solid;
}

.border-gray-300 {
  border-color: #e2e8f0;
}

*, *::before, *::after {
  -webkit-box-sizing: inherit;
  box-sizing: inherit;
}

.pl-2 {
  padding-left: 0.5rem;
}

.pr-1 {
  padding-right: 0.25rem;
}

.pl-3 {
  padding-left: 0.75rem;
}


*, *::before, *::after {
  border-width: 0;
  border-style: solid;
  border-color: #e2e8f0;
}

.chart-container {
  height: 60px;
  line-height: 60px;
}

.progress-container {
  height: 60px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.text-red-400 {
  color: #fc8181;
}

.text-green-400 {
  color: #68d391;
}

.vue-grid-item {
  &:nth-child(1) {

  }

  &:nth-child(2) {

  }

  &:nth-child(3) {

  }

  &:nth-child(4) {

  }

  background: #fff;
}

.el-progress-bar__outer {
  border-radius: 0;
}

.el-progress-bar__inner {
  border-radius: 0;
}
</style>
