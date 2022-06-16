<template>
  <div class="jp-home">
    <div>
      <el-row :gutter="10">
        <el-col :span="14">
          <home-card>
            <home-header slot="card-head" title="事项配置情况" />
            <div slot="card-content" class="stat-cont">
              <el-row>
                <el-col :span="8">
                  <home-indicator v-model="basicCatalogTotal" title="目录总数" />
                </el-col>
                <el-col :span="8">
                  <home-indicator v-model="standardItemTotal" title="标准事项总数" />
                </el-col>
                <el-col :span="8">
                  <home-indicator v-model="itemTotal" title="事项总数" />
                </el-col>
              </el-row>
            </div>
          </home-card>
        </el-col>
        <el-col :span="10">
          <home-card>
            <home-header slot="card-head" title="场景式配置情况" />
            <div slot="card-content" class="stat-cont">
              <el-row>
                <el-col :span="12">
                  <home-indicator v-model="catalogModelTotal" title="场景式定义总数" />
                </el-col>
                <el-col :span="12">
                  <home-indicator v-model="catalogDefinitionTotal" title="场景式发布总数" />
                </el-col>
              </el-row>
            </div>
          </home-card>
        </el-col>
      </el-row>
    </div>
    <!-- 业务统计 -->
    <div style="margin-top: 10px;">
      <home-card>
        <home-header slot="card-head" title="业务申报情况"></home-header>
        <div slot="card-content">
          <el-row>
            <el-col :span="16">
              <home-sub-card title="年度事项申报情况">
                <template slot="content">
                  <ve-histogram
                    :legend-visible="false"
                    auto-resize
                    :data="chartData"
                    :colors="['#409EFF']"
                    :settings="chartSettings"
                    :events="events"
                  ></ve-histogram>
                </template>
              </home-sub-card>
            </el-col>
            <el-col :span="1">
              <div class="divide"></div>
            </el-col>
            <el-col :span="7">
              <home-sub-card title="热点场景事项申报排行榜">
                <template slot="content">
                  <ul class="list">
                    <li :key="index" v-for="(item, index) in rankList">
                      <span :class="index < 3 ? 'active' : null">{{ index + 1 }}</span>
                      <span>{{ item.name }}</span>
                      <span style="color: #4b62ea;">{{ item.count }}</span>
                    </li>
                  </ul>
                </template>
              </home-sub-card>
            </el-col>
          </el-row>
        </div>
      </home-card>
    </div>
  </div>
</template>
<script>
import HomeSearchBar from './compoments/HomeSearchBar'
import HomeCard from "./compoments/HomeCard";
import HomeHeader from "./compoments/HomeHeader";
import HomeIndicator from "./compoments/HomeIndicator";
import HomeSubCard from "./compoments/HomeSubCard";
import VeHistogram from 'v-charts/lib/histogram'
export default {
  components: {
    HomeSubCard,
    HomeIndicator,
    HomeHeader,
    HomeCard,
    HomeSearchBar,
    VeHistogram
  },
  data() {
    return {
      active: 'all',
      beginDate: '',
      endDate: '',
      // 目录总量
      basicCatalogTotal: 70,
      // 标准事项总量
      standardItemTotal: 83,
      // 事项总量
      itemTotal: 8725,
      // 场景式模型总量
      catalogModelTotal: 93,
      // 场景式定义总量
      catalogDefinitionTotal: 216,
      //
      rankList: [],

      //------------------
      chartData: {
        columns: ['月份', '申报量'],
        rows: []
      },
      chartSettings: {
        label: {
          show: true,
          position: 'top',
          formatter: '{c}',
          color: '#333'
        }
      },
    }
  },
  computed: {
    events() {
      return {
        click: (event) => {
          this.$message.success(event.data)
        }
      }
    }
  },
  activated() {
    this.doQuery()
  },
  methods: {
    //
    handleQueryTypeChange(value) {
      this.active = value
      this.doQuery()
    },
    //
    handlePickerChange(value) {
      this.beginDate = value?.[0]
      this.endDate = value?.[1]
      this.doQuery()
    },
    //
    doQuery() {
      this.refreshMonitorData()
      this.refreshHotData()
      this.refreshProjectAnalyzeData()
    },
    // 监察业务统计数据
    refreshMonitorData() {

    },
    //
    refreshHotData() {
      this.rankList = [
        {name: '申报事项一', count: 1980},
        {name: '申报事项二', count: 568},
        {name: '申报事项三', count: 10},
      ]
    },
    // 业务申请办理情况统计
    refreshProjectAnalyzeData() {
      this.chartData.rows = [
        {'月份': '1月', '申报量': 0},
        {'月份': '2月', '申报量': 0},
        {'月份': '3月', '申报量': 76},
        {'月份': '4月', '申报量': 129},
        {'月份': '5月', '申报量': 28},
        {'月份': '6月', '申报量': 0},
        {'月份': '7月', '申报量': 0},
        {'月份': '8月', '申报量': 0},
        {'月份': '9月', '申报量': 0},
        {'月份': '10月', '申报量': 0},
        {'月份': '11月', '申报量': 0},
        {'月份': '12月', '申报量': 0},
      ]
    },
  },
}
</script>

<style lang="scss" scoped>

.divide {
  width: 1px;
  height: 400px;
  background: #E5E6E8;
  margin: 0 auto;
}

.list {
  margin: 25px 0 0;
  padding: 0;
  list-style: none;

  li {
    margin-top: 16px;

    span {
      color: rgba(0, 0, 0, .65);
      font-size: 14px;
      line-height: 22px;

      &:first-child {
        background-color: #f5f5f5;
        border-radius: 20px;
        display: inline-block;
        font-size: 12px;
        font-weight: 600;
        margin-right: 24px;
        height: 20px;
        line-height: 20px;
        width: 20px;
        text-align: center;
      }

      &.active {
        background-color: #314659;
        color: #fff;
      }

      &:last-child {
        float: right;
      }
    }
  }
}
</style>
