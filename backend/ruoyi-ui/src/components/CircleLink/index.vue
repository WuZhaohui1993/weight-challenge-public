<template>
  <el-link 
    type="success" 
    :underline="false" 
    @click="handleClick"
    class="circle-link"
  >
    <span v-if="showIcon" class="circle-icon">
      {{ displayIcon }}
    </span>
    <span class="circle-text">
      {{ displayName }}
    </span>
  </el-link>
</template>

<script>
import { getCircleInfo, getCircleFromCache } from '@/utils/circleCache'
import { drawerBus, DRAWER_EVENTS } from '@/utils/drawerBus'

export default {
  name: 'CircleLink',
  props: {
    circleId: {
      type: [Number, String],
      required: true
    },
    name: {
      type: String,
      default: ''
    },
    icon: {
      type: String,
      default: ''
    },
    showIcon: {
      type: Boolean,
      default: true
    },
    autoFetch: {
      type: Boolean,
      default: true
    }
  },
  data() {
    return {
      defaultIcon: '🎯',
      fetchedCircle: null
    }
  },
  computed: {
    displayName() {
      if (this.name) return this.name
      if (this.fetchedCircle && this.fetchedCircle.name) return this.fetchedCircle.name
      return `圈子#${this.circleId}`
    },
    displayIcon() {
      if (this.icon) return this.icon
      if (this.fetchedCircle && this.fetchedCircle.icon) return this.fetchedCircle.icon
      return this.defaultIcon
    }
  },
  watch: {
    circleId: {
      immediate: true,
      handler(newVal) {
        if (newVal && this.autoFetch && !this.name) {
          this.loadCircleInfo()
        }
      }
    }
  },
  methods: {
    async loadCircleInfo() {
      const cached = getCircleFromCache(this.circleId)
      if (cached) {
        this.fetchedCircle = cached
        return
      }

      const result = await getCircleInfo(this.circleId)
      if (result) this.fetchedCircle = result
    },
    handleClick() {
      // 通过事件总线通知全局容器打开圈子画像抽屉
      if (this.circleId) {
        drawerBus.$emit(DRAWER_EVENTS.OPEN_CIRCLE_PROFILE, this.circleId)
      }
    }
  }
}
</script>

<style scoped>
.circle-link {
  display: inline-flex;
  align-items: center;
  cursor: pointer;
}

.circle-icon {
  margin-right: 4px;
  font-size: 16px;
}

.circle-text {
  font-size: 14px;
  max-width: 100px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.circle-link:hover .circle-text {
  text-decoration: underline;
}
</style>
