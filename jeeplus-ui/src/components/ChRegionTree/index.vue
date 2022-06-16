<template>
  <div class="region-box">
    <el-tree
      ref="tree"
      style="overflow-y: auto; height: auto; min-height: 340px; max-height: 340px"
      :data="regionTreeData"
      node-key="id"
      :props="{label: 'text', children: 'children'}"
      default-expand-all
      show-checkbox
      :check-on-click-node="true"
      :expand-on-click-node="false"
      check-strictly
      @check-change="handleTreeNodeCheckChange"
    />
  </div>
</template>

<script>
import {ListRegionAuthCatalog} from "@/api/modules/sys/area";
import emitter from "element-ui/src/mixins/emitter";

export default {
  name: "ChRegionTree",
  mixins: [emitter],

  inject: {
    elForm: {
      default: ''
    },

    elFormItem: {
      default: ''
    }
  },
  data() {
    return {
      regionTreeData: []
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
    },
    grade: {
      type: Number,
      default: 4
    },
    code: String
  },
  computed: {
    getGrade() {
      return this.grade
    },
    regionChecked: {
      get() {
        return this.value
      },
      set(val) {
        this.$emit('check', val)
        this.dispatch('ElFormItem', 'el.form.change', val)
      }
    },
    authRegionCode() {
      let regionCode = this.$store.state.user.regionCode
      if (regionCode) {
        regionCode = regionCode === '140000' ? undefined : regionCode
      }
        return regionCode
    },
  },
  mounted() {
    this.loadRegionTree()
  },
  watch: {
    grade() {
      this.loadRegionTree()
    }
  },
  methods: {
    refreshTree() {
      this.loadRegionTree()
    },

    loadRegionTree() {
      ListRegionAuthCatalog({
        level: this.getGrade,
        authDeploy: '1',
        // code: this.authRegionCode,
        code: this.code
      }).then(({data}) => {
        if (data.success) {
          this.regionTreeData = this.treeDataTranslate(data.data, 'id', 'parent')
        } else {
          this.regionTreeData = []
        }
      })
    },

    handleTreeNodeCheckChange(data, checked, self) {
      if (checked) {
        if (!this.regionChecked.filter(it => it.regionCode === data.id)[0]) {
          this.regionChecked.push({ regionCode: data.id, regionName: data.text, regionShortCode: data.sortCode })
        }
      } else {
        this.regionChecked.forEach((it, index) => {
          if (it.regionCode === data.id) {
            this.regionChecked.splice(index, 1)
          }
        })
      }
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
