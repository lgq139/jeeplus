<template>
  <main class="jp-content" :class="{ 'jp-content--tabs': isTab }">
    <!-- 主入口标签页 s -->
<!--    <div v-if="contextmenuFlag" class="jp-tags__contentmenu" :style="{left:contentmenuX+'px',top:contentmenuY+'px'}">-->
<!--      <el-dropdown-item v-if="selectTabName !=='/home'" @click.native="tabsCloseCurrentHandle(selectTabName)">关闭当前标签页-->
<!--      </el-dropdown-item>-->
<!--      <el-dropdown-item @click.native="tabsCloseOtherHandle(selectTabName)">关闭其它标签页</el-dropdown-item>-->
<!--      <el-dropdown-item @click.native="tabsCloseAllHandle">关闭全部标签页</el-dropdown-item>-->
<!--      <el-dropdown-item @click.native="$router.push({path: selectTabName}), contextmenuFlag = false">刷新当前标签页-->
<!--      </el-dropdown-item>-->
<!--    </div>-->

    <!-- 多页签模式 -->
    <el-tabs
      v-if="isTab"
      type="card"
      v-model="mainTabsActiveName"
      @contextmenu.native="handleContextmenu"
      @tab-click="selectedTabHandle"
      @tab-remove="removeTabHandle">
<!--      <el-dropdown class="jp-tabs__tools" :show-timeout="0">-->
<!--        <div>-->
<!--          <el-divider direction="vertical"></el-divider>-->
<!--          <i class="el-icon-arrow-down el-icon&#45;&#45;right"></i>-->
<!--        </div>-->
<!--        <el-dropdown-menu slot="dropdown">-->
<!--          <el-dropdown-item v-if="mainTabsActiveName !=='/home'" @click.native="tabsCloseCurrentHandle('')">关闭当前标签页</el-dropdown-item>-->
<!--          <el-dropdown-item @click.native="tabsCloseOtherHandle('')">关闭其它标签页</el-dropdown-item>-->
<!--          <el-dropdown-item @click.native="tabsCloseAllHandle">关闭全部标签页</el-dropdown-item>-->
<!--          <el-dropdown-item @click.native="tabsRefreshCurrentHandle">刷新当前标签页</el-dropdown-item>-->
<!--        </el-dropdown-menu>-->
<!--      </el-dropdown>-->
      <el-tab-pane
        v-for="(item) in mainTabs"
        :key="item.fullPath.replace('/', '-').replace('?', '-').replace('&', '-').replace('=', '-')"
        :closable="item.name !== 'home'"
        :label="item.title"
        :name="item.fullPath">
        <div :class="$route.meta.backgroundType !== '2'? 'bg-white':''" :style="siteContentViewHeight">
          <iframe
            v-if="item.type === 'iframe'"
            :src="$route.query.iframeUrl || item.iframeUrl"
            width="100%" height="100%" frameborder="0" scrolling="yes">
          </iframe>
          <keep-alive v-else>
            <router-view v-if="isRouterAlive && item.fullPath === mainTabsActiveName"/>
          </keep-alive>
        </div>
      </el-tab-pane>
    </el-tabs>

    <!-- 主入口标签页 e -->
    <div v-else>
      <el-breadcrumb v-if="defaultLayout === 'top'" separator="/" style="padding-top:10px; padding-bottom:15px; padding-left: 10px;">
        <el-breadcrumb-item>
          <router-link to="/home">首页</router-link>
        </el-breadcrumb-item>
        <el-breadcrumb-item :key="index" v-for="(breadcrumb, index) in breadcrumbs">
          {{breadcrumb}}
        </el-breadcrumb-item>
      </el-breadcrumb>
      <div :class="$route.meta.backgroundType !== '2'? 'bg-white':''" :style="siteContentViewHeight">
        <iframe
          v-if="$route.meta.type === 'iframe'"
          :src="$route.query.iframeUrl ||$route.meta.iframeUrl"
          width="100%" height="100%" frameborder="0" scrolling="yes">
        </iframe>
        <keep-alive v-else>
          <router-view v-if="isRouterAlive"/>
        </keep-alive>
      </div>
    </div>
  </main>
</template>

<script>
import {isURL} from '@/utils/validate'
import Vue from 'vue'

export default {
  data() {
    return {
      contentmenuX: '',
      contentmenuY: '',
      contextmenuFlag: false,
      isRouterAlive: true,
      selectTabName: '',
      breadcrumbs: [],
      eventHub: new Vue()
    }
  },
  computed: {
    documentClientHeight: {
      get() {
        return this.$store.state.common.documentClientHeight
      }
    },
    isTab() {
      return this.$store.state.common.isTab
    },
    menuActiveName: {
      get() {
        return this.$store.state.common.menuActiveName
      },
      set(val) {
        this.$store.commit('common/updateMenuActiveName', val)
      }
    },
    mainTabs: {
      get() {
        return this.$store.state.common.mainTabs
      },
      set(val) {
        this.$store.commit('common/updateMainTabs', val)
      }
    },
    leftMenuCategory: {
      get() {
        return this.$store.state.common.leftMenuCategory
      }
    },
    mainTabsActiveName: {
      get() {
        return this.$store.state.common.mainTabsActiveName
      },
      set(val) {
        this.$store.commit('common/updateMainTabsActiveName', val)
      }
    },
    siteContentViewHeight() {
      let height = this.documentClientHeight - 122
      return isURL(this.$route.query.iframeUrl) || isURL(this.$route.meta.iframeUrl) ? 'height:' + height + 'px' : 'minHeight:' + height + 'px'
    },
    tagLen() {
      return this.mainTabs.length || 0
    },
    defaultLayout() {
      return this.$store.state.config.defaultLayout
    },
  },
  created() {
    this.$events.$on('closeTab', (name) => {
      this.removeTabHandle(name)
    })
  },
  beforeDestroy() {
    this.$events.$off('closeTab', {})
  },
  watch: {
    contextmenuFlag() {
      window.addEventListener('mousedown', this.watchContextmenu)
    },
    $route: {
      handler(val) {
        this.breadcrumbs = []
        if (val.meta && val.meta.parentIds) {
          let ids = val.meta.parentIds.split(',')
          ids.forEach((id) => {
            if (id && id !== '0' && id !== '1') {
              let obj = {title: ''}
              this.getTitle(JSON.parse(sessionStorage.getItem('allMenuList') || '[]'), id, obj)
              this.breadcrumbs.push(obj.title)
            }
          })
          this.breadcrumbs.push(this.$route.query.title || this.$route.meta.title)
        }
      },
      immediate: true,
      deep: false
    }
  },
  methods: {
    // tabs, 选中tab
    selectedTabHandle(tab) {
      tab = this.mainTabs.filter(item => item.fullPath === tab.name)
      if (!tab) {
        tab = this.mainTabs.filter(item => item.name === tab.name)
      }
      if (tab.length >= 1) {
        this.$router.push({path: tab[0].fullPath})
      }
    },
    // tabs, 删除tab
    removeTabHandle(tabName) {
      var obj = this.mainTabs.filter(item => item.fullPath === tabName)[0]
      var index = this.mainTabs.indexOf(obj)
      this.mainTabs = this.mainTabs.filter(item => item.fullPath !== tabName)
      if (this.mainTabs.length > 1) {
        // 当前选中tab被删除
        if (tabName === this.mainTabsActiveName) {
          if (index < this.mainTabs.length) {
            this.$router.push({path: this.mainTabs[index].fullPath}, () => {
              this.mainTabsActiveName = this.$route.fullPath
            })
          } else {
            this.$router.push({path: this.mainTabs[index - 1].fullPath}, () => {
              this.mainTabsActiveName = this.$route.fullPath
            })
          }
        }
      } else {
        this.menuActiveName = ''
        this.$router.push({name: 'home'})
      }
    },
    // 获取路由名字
    getTitle(menus, id, obj) {
      menus.forEach((menu) => {
        if (menu.id === id) {
          obj.title = menu.name
        } else if (menu.children) {
          this.getTitle(menu.children, id, obj)
        }
      })
    },
    // tabs, 关闭当前
    tabsCloseCurrentHandle(tabName) {
      this.contextmenuFlag = false
      this.removeTabHandle(tabName || this.mainTabsActiveName)
    },
    // tabs, 关闭其它
    tabsCloseOtherHandle(tabName) {
      this.contextmenuFlag = false
      this.mainTabs = this.mainTabs.filter(item => item.fullPath === (tabName || this.mainTabsActiveName) || item.name === 'home')
      if (tabName) {
        this.$router.push({path: tabName}, () => {
          this.mainTabsActiveName = tabName
        })
      }
    },
    // tabs, 关闭全部
    tabsCloseAllHandle() {
      this.contextmenuFlag = false
      // this.mainTabs = []
      this.mainTabs = this.mainTabs.filter(item => item.name === 'home')
      this.menuActiveName = 'home'
      this.$router.push({name: 'home'})
    },
    handleContextmenu(event) {
      let target = event.target
      let flag = false
      if (target && target.className && target.className.indexOf('el-tabs__item') > -1) flag = true
      else if (target.parentNode && target.parentNode.className && target.parentNode.className.indexOf('el-tabs__item') > -1) {
        target = target.parentNode
        flag = true
      }
      if (flag) {
        event.preventDefault()
        event.stopPropagation()
        this.contentmenuX = event.clientX
        this.contentmenuY = event.clientY
        this.selectTabName = target.getAttribute('aria-controls').slice(5)
        this.contextmenuFlag = true
      }
    },
    watchContextmenu(event) {
      if (!event.target.className.includes('el-dropdown-menu__item') || event.button !== 0) {
        this.contextmenuFlag = false
      }
      window.removeEventListener('mousedown', this.watchContextmenu)
    },
    reload() {
      this.isRouterAlive = false
      this.$nextTick(function () {
        this.isRouterAlive = true
      })
    },
    // tabs, 刷新当前
    tabsRefreshCurrentHandle() {
      this.contextmenuFlag = false
      this.reload()
    }
  }
}
</script>

<style>
.jp-tags__contentmenu {
  position: fixed;
  width: 150px;
  background-color: #fff;
  z-index: 1024;
  border-radius: 5px;
  -webkit-box-shadow: 1px 2px 10px #ccc;
  box-shadow: 1px 2px 10px #ccc;
}

.jp-navbar__menu .el-badge__content.is-fixed {
  top: 15px;
}
</style>
