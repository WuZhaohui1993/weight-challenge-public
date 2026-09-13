<template>
  <div id="app">
    <router-view />
    <theme-picker />
    <!-- 全局抽屉容器，统一管理用户/圈子画像抽屉 -->
    <global-drawer-container />
  </div>
</template>

<script>
import ThemePicker from "@/components/ThemePicker"

export default {
  name: "App",
  components: { ThemePicker }
}
</script>
<style scoped>
#app .theme-picker {
  display: none;
}
</style>
