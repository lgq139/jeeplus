<template>
  <ch-dialog
    title="图标选择"
    :close-on-click-modal="false"
    :visible.sync="visible"
    @submit="doSubmit"
  >
    <el-tabs value="first" type="card">
      <el-tab-pane label="Element图标" name="first">
        <ul class="icon-list">
          <li v-for="(item, index) in iconList" :key="index">
            <el-button @click="iconActiveHandle(item)" :class="{ 'is-active': item === icon }" :title="item">
              <i :class="item"></i>
            </el-button>
          </li>
        </ul>
      </el-tab-pane>
      <el-tab-pane label="Font Awesome图标" name="second">
        <ul class="icon-list">
          <li v-for="(item, index) in iconList2" :key="index">
            <el-button @click="iconActiveHandle(item)" :class="{ 'is-active': item === icon }" :title="item">
              <i :class="item"></i>
            </el-button>
          </li>
        </ul>
      </el-tab-pane>
    </el-tabs>
  </ch-dialog>
</template>

<script>
import icons from '@/utils/icons'
import icons2 from '@/utils/icons2'
import ChDialog from "@/components/ChDialog";

export default {
  name: 'Icon',
  components: {ChDialog},
  data() {
    return {
      visible: false,
      iconList: icons,
      iconList2: icons2,
      icon: ''
    }
  },
  methods: {
    // 图标选中
    iconActiveHandle(iconName) {
      this.icon = iconName
    },
    doSubmit() {
      this.$emit('getValue', this.icon)
      this.visible = false
    }
  }
}
</script>

<style scoped>
ul {
  margin: 10px 0;
  padding: 0 0 0 20px;
  font-size: 14px;
  color: #5e6d82;
  line-height: 2em;
}

ul.icon-list {
  overflow: hidden;
  list-style: none;
  padding: 0 !important;
  border: 1px solid #eaeefb;
  border-radius: 4px;
}

.icon-list li {
  float: left;
  width: 10%;
  text-align: center;
  height: 50px;
  line-height: 50px;
  color: #666;
  font-size: 13px;
  border-right: 1px solid #eee;
  border-bottom: 1px solid #eee;
  margin-right: -1px;
  margin-bottom: -1px;
}
</style>
