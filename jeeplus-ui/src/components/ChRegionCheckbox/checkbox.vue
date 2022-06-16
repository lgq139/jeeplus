<template>
  <div style="padding: 0 10px;">
    <el-alert title="" type="info" :closable="false" show-icon style="margin-bottom: 20px;" />
    <el-checkbox :indeterminate="isIndeterminate" :disabled="disabled"
                 v-model="checkAll" @change="handleCheckAllChange">全选</el-checkbox>
    <el-divider />
    <el-checkbox-group v-model="checkedCities" @change="handleCheckedChange" :disabled="disabled">
      <el-checkbox v-for="(item, index) in getOptions" :label="item[props.label]" :key="index">{{item[props.text]}}</el-checkbox>
    </el-checkbox-group>
  </div>
</template>

<script>
export default {
  name: "ChCheckbox",

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
    },
    options: {
      type: Array,
      default: () => {
        return []
      }
    },
    props: {
      type: Object,
      default: () => {
        return {label: 'label', text: 'text'}
      }
    },

    disabled: Boolean,

    showTip: Boolean,
    /**
     *
     */
    tip: {
      type: String,
      default: '若需将当前场景应用到区县，则在下方选取需授权区县，否则无需选择'
    }
  },
  data() {
    return {
      checkAll: false,
      checkedCities: [],
      isIndeterminate: false,
    }
  },
  computed: {
    getOptions() {
      return this.options
    }
  },
  methods: {
    /**
     *
     * @param val
     */
    handleCheckAllChange(val) {
      this.checkedCities = val ? this.getOptions.map(it => it.id) : [];
      this.isIndeterminate = false;
      this.$emit('check', this.checkedCities)
      this.dispatch('ElFormItem', 'el.form.change', this.checkedCities)
    },
    /**
     *
     * @param value
     */
    handleCheckedChange(value) {
      let checkedCount = value.length;
      this.checkAll = checkedCount === this.getOptions.length;
      this.isIndeterminate = checkedCount > 0 && checkedCount < this.getOptions.length;
      this.$emit('check', value)
      this.dispatch('ElFormItem', 'el.form.change', value)
    },
  }
}
</script>

<style scoped>

</style>
