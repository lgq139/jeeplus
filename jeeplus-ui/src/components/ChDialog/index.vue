<template>
  <el-dialog
    class="ch-dialog"
    :visible.sync="show"
    :width="width"
    :fullscreen="fullscreen"
    :top="top"
    :center="center"
    :close-on-click-modal="false"
    lock-scroll
    append-to-body
    @open="$emit('open')"
    @close="$emit('close')"
  >
    <template slot="title">
      <span class="el-dialog__title" :title="title">{{title}}</span>
    </template>
    <slot />
    <template v-if="showFooter" #footer>
      <slot name="action">
        <el-button type="primary" size="small" @click="submit" v-if="isConfirmShow">{{ confirmText}}</el-button>
        <el-button size="small" @click="show = false">{{ cancelText}}</el-button>
      </slot>
    </template>
  </el-dialog>
</template>

<script>

export default {
  name: 'ChDialog',

  props: {
    title: String,
    visible: Boolean,
    width: String,
    fullscreen: Boolean,
    top: {
      type: String,
      default: '15vh'
    },
    center: Boolean,
    /**
     * 是否显示Footer
     */
    showFooter: {
      type: Boolean,
      default: true
    },
    cancelText: {
      type: String,
      default: '取 消'
    },
    confirmText: {
      type: String,
      default: '确 定'
    },
    isConfirmShow:{
      type: Boolean,
      default: true
    }
  },
  mounted() {
    console.log(this.isConfirmShow)
  },
  computed: {
    show: {
      get() {

        return this.visible
      },
      set(value) {
        this.$emit('update:visible', value)
      }
    }
  },

  data() {
    return {

    }
  },

  methods: {
    /**
     *
     * @param e
     */
    submit(e) {
      this.$emit('submit', e)
    }
  }
}
</script>

<style lang="scss">
@import "~@/assets/scss/theme.scss";

.ch-dialog {
  /* 弹框默认居中 */
  display: flex;
  justify-content: center;
  align-items: center;
  > .el-dialog {
    margin: 0;
    max-height: 100vh;
    overflow:hidden;
    &:not(.is-fullscreen) {
      margin-top: 0 !important;
    }

  }
  .el-dialog__header {
    padding: 16px 20px;
    border-bottom: 1px solid $border-color-base !important;
    display: flex;
    .el-dialog__title {
      flex: 1;
      font-size: 16px;
      line-height: normal;
      font-weight: bold;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }
    .el-dialog__headerbtn {
      position: static;
      :hover{
        color: #267FD3 !important;
      }
    }
  }
  .el-dialog__body {
    //@include scrollBar;
    max-height: calc(100vh - 160px);
    overflow-y: auto;
    overflow-x: hidden;
  }
  .el-dialog__footer {
    padding: 10px 20px;
    border-top: 1px solid $border-color-base !important;
    .el-button{
      color: #666;
      height: 32px;
      padding: 0 16px;
      text-align: center;
      line-height: 32px;
      border-radius: 2px;
      background: #fff;
      border: 1px solid #E6E6E6;
    }
    /*.el-button:hover{*/
    /*  background: #fff !important;*/
    /*  border: 1px solid #267FD3 !important;*/
    /*  color: #267FD3 !important;*/
    /*}*/
    .el-button--default:hover{
      background: #fff !important;
      border: 1px solid #267FD3 !important;
      color: #267FD3 !important;
    }
    .el-button--primary{
      color: #FFF;
      background-color: #267FD3;
      border-color: #267FD3;
    }
    /*.el-button--primary:hover{*/
    /*  color: #FFF;*/
    /*  background-color: #267FD3;*/
    /*  border-color: #267FD3;*/
    /*}*/
    .el-form-item{
      margin-bottom: 0;
    }
  }
}
</style>
