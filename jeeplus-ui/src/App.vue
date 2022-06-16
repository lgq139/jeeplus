<template>
  <div id="jp-admin">
    <transition v-bind:css="false" name="fade">
      <router-view />
    </transition>
  </div>
</template>

<script>
  export default {}
</script>
<style scoped>
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0s;
}
.fade-enter,
.fade-leave-to {
  opacity: 0;
}

</style>
