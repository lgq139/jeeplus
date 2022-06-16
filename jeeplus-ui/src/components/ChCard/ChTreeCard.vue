<template>
  <el-card shadow="never" class="ch-tree-card">
    <template slot="header">
      <div class="ch-tree-card__header">
        <span>{{ title }}</span>
<!--        <span class="operate__btn">-->
<!--          <span><i class="el-icon-refresh"></i></span>-->
<!--          <span><i class="el-icon-refresh"></i></span>-->
<!--        </span>-->
      </div>
    </template>
    <z-tree
      :style="{height: (height ? height : viewHeight ) + 'px', overflowY: 'auto'}"
      :setting="setting"
      :nodes="treeData"
      @onClick="handleTreeClick"
    />
  </el-card>
</template>

<script>
import zTree from 'vue-giant-tree'

export default {
  name: "ChTreeCard",
  components: {
    zTree
  },
  props: {

    title: {
      type: String,
      default: '列表'
    },

    setting: {
      type: Object,
      default: () => {
        return {}
      }
    },

    treeData: {
      type: Array,
      default: () => {
        return []
      }
    },
    //
    height: {
      type: Number,
      required: false
    }
  },
  data() {
    return {

    }
  },
  computed: {
    viewHeight () {
      return this.$store.state.common.documentClientHeight - 246
    }
  },
  methods: {
    //
    handleTreeClick(event, treeId, treeNode, clickFlag) {
      this.$emit('onClick', event, treeId, treeNode, clickFlag)
    }
  }
}
</script>

<style scoped lang="scss">
.ch-tree-card {
  ::v-deep .el-card__header {
    padding: 10px 10px;
  }
  width: 300px;
  //margin-right: 10px;
  &__header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    .operate__btn {
      span {
        i {
          font-size: 18px;
          font-weight: bold;
        }
        cursor: pointer;
        &:not(:last-child) {
          margin-right: 10px;
        }
      }
    }
  }
}
</style>
