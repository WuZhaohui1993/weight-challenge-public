<template>
  <footer v-if="visible" class="copyright">
    <site-filing :copyright="content" />
  </footer>
</template>

<script>
import SiteFiling from '@/components/SiteFiling'

export default {
  components: { SiteFiling },
  computed: {
    visible() {
      return this.$store.state.settings.footerVisible
    },
    content() {
      return this.$store.state.settings.footerContent
    }
  }
}
</script>

<style scoped>
.copyright {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  min-height: 36px;
  padding: 8px 20px;
  text-align: right;
  background-color: #f8f8f8;
  color: #666;
  font-size: 14px;
  border-top: 1px solid #e7e7e7;
  z-index: 999;
  box-sizing: border-box;
}
</style>
