<!--
  区划两级checkbox
-->
<template>
  <div class="region-box">
    <el-row type="flex" :gutter="10">
      <el-col :span="8" class="box-border__right">
        <el-tree
          ref="tree"
          style="overflow-y: auto; height: auto; min-height: 340px;"
          :data="regionTreeData"
          node-key="id"
          :props="{label: 'text', children: 'children'}"
          default-expand-all
          show-checkbox
          :check-on-click-node="false"
          :expand-on-click-node="false"
          check-strictly
          @node-click="handleTreeNodeClick"
          @check-change="handleTreeNodeCheckChange"
        />
      </el-col>
      <el-col :span="16">
        <div style="padding: 0 10px;">
          <el-alert title="若需将当前场景应用到区县，则在下方选取需授权区县，否则无需选择" type="info" :closable="false" show-icon style="margin-bottom: 20px;" />
          <el-checkbox :indeterminate="isIndeterminate" :disabled="disabled"
                       v-model="checkAll" @change="handleCheckAllChange">全选</el-checkbox>
          <el-divider />
          <el-checkbox-group v-model="checkedCities" @change="handleCheckedCitiesChange" :disabled="disabled">
            <el-checkbox v-for="(item, index) in regionTableData" :label="item.id" :key="index">{{item.text}}</el-checkbox>
          </el-checkbox-group>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import {listRegionCities, listRegionCounties, listRegionTreeData} from "@/api/modules/sys/area";
import emitter from "element-ui/src/mixins/emitter";

export default {
  name: "ChRegionCheckbox",

  mixins: [emitter],

  inject: {
    elForm: {
      default: ''
    },

    elFormItem: {
      default: ''
    }
  },
  model: {
    prop: 'value',
    event: 'check'
  },
  props: {
    value: {
      type: Array,
      required: true
    }
  },
  data() {
    return {
      regionTreeData: [],
      regionTableData: [],
      currentRegion: undefined,
      // 市级被选中
      // regionChecked: this.value,

      checkAll: false,
      checkedCities: [],
      isIndeterminate: false,
    }
  },
  computed: {
    disabled() {
      return !(this.currentRegion && this.regionChecked.filter(it => it.regionCode === this.currentRegion)[0]);
    },
    regionChecked: {
      get() {
        return this.value
      },
      set(val) {
        this.$emit('check', val)
        this.dispatch('ElFormItem', 'el.form.change', this.regionChecked)
      }
    }
  },
  mounted() {
    this.loadRegionTree()
  },
  methods: {

    refreshTree() {
      this.loadRegionTree()
    },

    loadRegionTree() {
      listRegionCities({grade: 4}).then(({data}) => {
        if (data.success) {
          this.regionTreeData = this.treeDataTranslate(data.data, 'id', 'parent')
        } else {
          this.regionTreeData = []
        }
      })
    },

    loadRegionTable(regionCode) {
      listRegionCounties({grade: 5, regionCode: regionCode}).then(({data}) => {
        if (data.success) {
          // this.regionTableData = data.data.filter(it => it.parent !== '#')
          this.regionTableData = data.data
          const region = this.regionChecked.filter(it => it.regionCode === this.currentRegion)[0]
          if (region && region['authList']) {
            this.checkedCities = region['authList']
          } else {
            this.checkedCities = []
          }
        } else {
          this.regionTableData = []
        }
      })
    },

    // Tree节点点击回调处理
    handleTreeNodeClick(data, node, self) {
      if (data.id === '140000') {
        this.regionTableData = []
        return
      }
      this.currentRegion = data.id
      this.loadRegionTable(data.id)
    },

    // Tree节点Check状态变化回调处理
    handleTreeNodeCheckChange(data, checked, self) {
      if (checked) {
        if (!this.regionChecked.filter(it => it.regionCode === data.id)[0]) {
          if (this.currentRegion != data.id) {
            this.handleTreeNodeClick(data)
          }
          this.regionChecked.push({ regionCode: data.id, regionName: data.text, regionShortCode: data.shortCode })
        }
      } else {
        this.regionChecked.forEach((it, index) => {
          if (it.regionCode === data.id) {
            this.regionChecked.splice(index, 1)
          }
        })
      }
      // this.$emit('check', this.regionChecked)
      // this.dispatch('ElFormItem', 'el.form.change', this.regionChecked)
    },

    handleCheckAllChange(val) {
      this.checkedCities = val ? this.regionTableData.map(it => it.id) : [];
      this.isIndeterminate = false;
      this.regionChecked.filter(it => it.regionCode === this.currentRegion)[0]['authList'] = this.checkedCities
      // this.$emit('check', this.regionChecked)
      // this.dispatch('ElFormItem', 'el.form.change', this.regionChecked)
    },

    handleCheckedCitiesChange(value) {
      let checkedCount = value.length;
      this.checkAll = checkedCount === this.regionTableData.length;
      this.isIndeterminate = checkedCount > 0 && checkedCount < this.regionTableData.length;
      this.regionChecked.filter(it => it.regionCode === this.currentRegion)[0]['authList'] = value
      // this.$emit('check', this.regionChecked)
      // this.dispatch('ElFormItem', 'el.form.change', this.regionChecked)
    },
  }
}
</script>

<style scoped lang="scss">
@import "~@/assets/scss/theme.scss";

.region-box {
  width: 100%;
  height: auto;
  border: 1px solid $border-color-base;
  padding: 10px;
}
.box-border__right {
  border-right: 1px solid $border-color-base;
}
</style>
