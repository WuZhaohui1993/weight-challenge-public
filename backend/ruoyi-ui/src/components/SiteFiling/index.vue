<template>
  <span class="site-filing">
    <span v-if="copyright" class="site-filing__copyright">{{ copyright }}</span>
    <a :href="filing.icpUrl" target="_blank" rel="noreferrer">{{ filing.icpText }}</a>
    <a :href="filing.policeUrl" target="_blank" rel="noreferrer">{{ filing.policeText }}</a>
  </span>
</template>

<script>
import defaultSettings from '@/settings'

export default {
  name: 'SiteFiling',
  props: {
    copyright: {
      type: String,
      default: defaultSettings.footerContent
    }
  },
  computed: {
    filing() {
      return defaultSettings.siteFiling
    }
  }
}
</script>

<style scoped>
.site-filing {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  flex-wrap: wrap;
  gap: 4px 12px;
  line-height: 1.6;
}

.site-filing a {
  color: inherit;
  text-decoration: none;
}

.site-filing a:hover {
  text-decoration: underline;
}
</style>
