<template>
  <div class="nav">
    <div class="nav-bar">
      <div>
        <span class="nav-bar-btn"
          v-for="(itm, idx) in typeOptions"
          :key="idx"
          @click="handleChange(itm.value)"
          :class="{active: active === itm.value}"
        >{{ itm.label }}</span>
      </div>
      <el-date-picker
        v-if="filter"
        v-model="pickerValue"
        type="daterange"
        size="mini"
        align="right"
        unlink-panels
        range-separator="至"
        start-placeholder="开始日期"
        end-placeholder="结束日期"
        value-format="yyyy-MM-dd"
        :picker-options="pickerOptions"
        style="margin-right: 5px;"
        @change="handlePickerChange"
      />
    </div>
  </div>
</template>

<script>
export default {
  props: {
    filter: Boolean,
    typeOptions: {
      type: Array,
      default: () => {
        return [
          {label: '总览', value: 'all'},
          {label: '本周', value: 'currentWeek'},
          {label: '本月', value: 'currentMonth'},
          {label: '上月', value: 'lastMonth'},
          {label: '本季度', value: 'currentQuarter'},
          {label: '本年度', value: 'currentYear'}
        ]
      }
    }
  },
  data() {
    return {
      active: 'all',
      pickerValue: '',
      pickerOptions: {
        shortcuts: [{
          text: '最近一周',
          onClick(picker) {
            const end = new Date();
            const start = new Date();
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 7);
            picker.$emit('pick', [start, end]);
          }
        }, {
          text: '最近一个月',
          onClick(picker) {
            const end = new Date();
            const start = new Date();
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 30);
            picker.$emit('pick', [start, end]);
          }
        }, {
          text: '最近三个月',
          onClick(picker) {
            const end = new Date();
            const start = new Date();
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 90);
            picker.$emit('pick', [start, end]);
          }
        }]
      },
    };
  },
  methods: {
    // 选项变更
    handleChange(val) {
      this.active = val
      this.pickerValue = ''
      this.$emit('change', val)
    },
    // 日期范围选择变更
    handlePickerChange(val) {
      this.active = ''
      this.$emit('pickerChange', val)
    }
  }

}
</script>

<style lang="scss" scoped>

.el-date-editor {
  margin-right: 80px;
}

.el-menu--horizontal {
  border: 0;
}

.nav-bar {
  width: 100%;
  background: #FFFFFF;
  border-radius: 3px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 20px;

  .nav-bar-btn {
    display: inline-block;
    height: 32px;
    padding: 0 20px;
    line-height: 32px;
    text-align: center;
    font-size: 14px;
    margin-right: 10px;

    &:hover {
      cursor: pointer;
      font-size: 14px;
      border-radius: 16px;
      background: #F2F3F5;
    }
  }

  .active {
    background: #F2F3F5;
    color: #3478E2;
    font-weight: 600;
    border-radius: 16px;
  }
}
</style>
