<template>
  <div
    class="el-select"
    :class="[selectSize ? 'el-select--' + selectSize : '']"
    style="width: 100%;"
    @click.stop="toggleMenu"
  >
    <div
      class="el-select__tags"
      ref="tags"
      :style="{ 'max-width': inputWidth - 32 + 'px', width: '100%' }">
      <transition-group @after-leave="resetInputHeight">
        <el-tag
          v-for="item in selected"
          :key="item[props.value]"
          :closable="closable"
          :size="collapseTagSize"
          :type="tagType"
          @close="deleteTag($event, item)"
          disable-transitions>
          <span class="el-select__tags-text">{{ item[props.label] }}</span>
        </el-tag>
      </transition-group>
    </div>
    <el-input
      ref="reference"
      v-model="selectedLabel"
      type="text"
      :placeholder="currentPlaceholder"
      :name="name"
      :id="id"
      :size="selectSize"
      :disabled="selectDisabled"
      :readonly="true"
      :validate-event="false"
      :class="{ 'is-focus': visible }"
      :tabindex="null"
      @focus="handleFocus"
      @blur="handleBlur"
      @keydown.native.esc.stop.prevent="visible = false"
      @keydown.native.tab="visible = false"
      @mouseenter.native="inputHovering = true"
      @mouseleave.native="inputHovering = false">
      <template slot="prefix" v-if="$slots.prefix">
        <slot name="prefix"></slot>
      </template>
      <template slot="suffix">
        <i v-show="!showClose" :class="['el-select__caret', 'el-input__icon', 'el-icon-' + iconClass]"></i>
        <i v-if="showClose" class="el-select__caret el-input__icon el-icon-circle-close" @click="handleClearClick"></i>
      </template>
    </el-input>

    <ch-dialog
      :title="title"
      :visible.sync="visible"
      :width="dialogWidth"
      @open="$emit('open')"
      @close="$emit('close')"
      @submit="handleSubmit">
      <slot />
    </ch-dialog>

  </div>
</template>

<script>
import Emitter from 'element-ui/src/mixins/emitter';
import Focus from 'element-ui/src/mixins/focus';
import Locale from 'element-ui/src/mixins/locale';
import {addResizeListener, removeResizeListener} from 'element-ui/src/utils/resize-event';
import { valueEquals } from 'element-ui/src/utils/util';
import NavigationMixin from "element-ui/packages/select/src/navigation-mixin";
import ChDialog from "@/components/ChDialog";

export default {

  name: "ChTagsSelect",

  mixins: [Emitter, Locale, Focus('reference'), NavigationMixin],

  inject: {
    elForm: {
      default: ''
    },

    elFormItem: {
      default: ''
    }
  },

  provide() {
    return {
      'select': this
    };
  },

  computed: {

    _elFormItemSize() {
      return (this.elFormItem || {}).elFormItemSize;
    },

    showClose() {
      let hasValue = Array.isArray(this.value) && this.value.length > 0;
      return this.clearable && !this.selectDisabled && this.inputHovering && hasValue;
    },

    iconClass() {
      return this.visible ? 'arrow-up is-reverse' : 'arrow-up';
    },

    debounce() {
      return 0;
    },

    selectSize() {
      return this.size || this._elFormItemSize || (this.$ELEMENT || {}).size;
    },

    selectDisabled() {
      return this.disabled || (this.elForm || {}).disabled;
    },

    collapseTagSize() {
      return ['small', 'mini'].indexOf(this.selectSize) > -1 ? 'mini' : 'small';
    },
    propPlaceholder() {
      return typeof this.placeholder !== 'undefined' ? this.placeholder : this.t('el.select.placeholder');
    }
  },

  components: { ChDialog },

  props: {
    props: {
      type: Object,
      default: () => {
        return {
          label: 'name',
          value: 'id'
        }
      }
    },
    name: String,
    id: String,
    value: {
      type: Array,
      default: () => {
        return []
      }
    },
    size: String,
    disabled: Boolean,
    clearable: Boolean,
    placeholder: {
      type: String,
      required: false
    },
    tagType: {
      type: String,
      default: 'info'
    },
    multiple: Boolean,
    title: {
      type: String,
      default: '请选择'
    },
    dialogWidth: {
      type: String,
      default: '70%'
    },
    closable: {
      type: Boolean,
      default: true
    }
  },

  data() {
    return {
      createdLabel: null,
      createdSelected: false,
      selected: [],
      inputLength: 20,
      inputWidth: 0,
      initialInputHeight: 0,
      cachedPlaceHolder: '',
      visible: false,
      softFocus: false,
      selectedLabel: '',
      inputHovering: false,
      currentPlaceholder: '',
      isOnComposition: false,
      isSilentBlur: false,
    };
  },

  watch: {

    selectDisabled() {
      this.$nextTick(() => {
        this.resetInputHeight();
      });
    },

    propPlaceholder(val) {
      this.cachedPlaceHolder = this.currentPlaceholder = val;
    },

    value(val, oldVal) {
      this.resetInputHeight();
      if ((val && val.length > 0) || this.$refs.input) {
        this.currentPlaceholder = '';
      } else {
        this.currentPlaceholder = this.cachedPlaceHolder;
      }
      this.setSelected();
      if (!valueEquals(val, oldVal)) {
        this.dispatch('ElFormItem', 'el.form.change', val);
      }
    },

    visible(val) {
      if (!val) {
        this.broadcast('ElSelectDropdown', 'destroyPopper');
        if (this.$refs.input) {
          this.$refs.input.blur();
        }
        this.selectedLabel = '';
        this.inputLength = 20;

        this.$nextTick(() => {
          if (this.$refs.input && this.$refs.input.value === '' && this.selected.length === 0) {
            this.currentPlaceholder = this.cachedPlaceHolder;
          }
        });
      } else {

      }
      this.$emit('visible-change', val);
    },

  },

  methods: {

    emitChange(val) {
      if (!valueEquals(this.value, val)) {
        this.$emit('change', val);
      }
    },

    setSelected() {
      let result = [];
      if (Array.isArray(this.value)) {
        this.value.forEach(value => {
          result.push(value);
        });
      }
      this.selected = result;
      this.$nextTick(() => {
        this.resetInputHeight();
      });
    },

    handleFocus(event) {
      if (!this.softFocus) {
        this.$emit('focus', event);
      } else {
        this.softFocus = false;
      }
    },

    blur() {
      this.visible = false;
      this.$refs.reference.blur();
    },

    handleBlur(event) {
      setTimeout(() => {
        if (this.isSilentBlur) {
          this.isSilentBlur = false;
        } else {
          this.$emit('blur', event);
        }
      }, 50);
      this.softFocus = false;
    },

    handleClearClick(event) {
      this.deleteSelected(event);
    },

    resetInputHeight() {
      this.$nextTick(() => {
        if (!this.$refs.reference) return;
        let inputChildNodes = this.$refs.reference.$el.childNodes;
        let input = [].filter.call(inputChildNodes, item => item.tagName === 'INPUT')[0];
        const tags = this.$refs.tags;
        const sizeInMap = this.initialInputHeight || 40;
        input.style.height = this.selected.length === 0
          ? sizeInMap + 'px'
          : Math.max(
          tags ? (tags.clientHeight + (tags.clientHeight > sizeInMap ? 6 : 0)) : 0,
          sizeInMap
        ) + 'px';
      });
    },

    setSoftFocus() {
      this.softFocus = true;
      const input = this.$refs.input || this.$refs.reference;
      if (input) {
        input.focus();
      }
    },

    toggleMenu() {
      if (!this.selectDisabled) {
        this.visible = !this.visible;
        if (this.visible) {
          (this.$refs.input || this.$refs.reference).focus();
        }
      }
    },

    deleteSelected(event) {
      event.stopPropagation();
      const value = [];
      this.$emit('input', value);
      this.emitChange(value);
      this.visible = false;
      this.$emit('clear');
    },

    deleteTag(event, tag) {
      let index = this.selected.indexOf(tag);
      if (index > -1 && !this.selectDisabled) {
        const value = this.value.slice();
        value.splice(index, 1);
        this.$emit('input', value);
        this.emitChange(value);
        this.$emit('remove-tag', tag.value);
      }
      event.stopPropagation();
    },

    resetInputWidth() {
      this.inputWidth = this.$refs.reference.$el.getBoundingClientRect().width;
    },

    handleResize() {
      this.resetInputWidth();
      this.resetInputHeight();
    },

    handleSubmit() {
      this.$emit('submit', this.value)
      this.visible = false
    }

  },

  created() {
    this.cachedPlaceHolder = this.currentPlaceholder = this.propPlaceholder;
    if (!Array.isArray(this.value)) {
      this.$emit('input', []);
    }
    this.$on('setSelected', this.setSelected);
  },

  mounted() {
    if (Array.isArray(this.value) && this.value.length > 0) {
      this.currentPlaceholder = '';
    }
    addResizeListener(this.$el, this.handleResize);

    const reference = this.$refs.reference;
    if (reference && reference.$el) {
      const sizeMap = {
        medium: 36,
        small: 32,
        mini: 28
      };
      const input = reference.$el.querySelector('input');
      this.initialInputHeight = input.getBoundingClientRect().height || sizeMap[this.selectSize];
    }
    this.$nextTick(() => {
      if (reference && reference.$el) {
        this.inputWidth = reference.$el.getBoundingClientRect().width;
      }
    });
    this.setSelected();
  },

  beforeDestroy() {
    if (this.$el && this.handleResize) removeResizeListener(this.$el, this.handleResize);
  }
}
</script>

<style scoped>

</style>
