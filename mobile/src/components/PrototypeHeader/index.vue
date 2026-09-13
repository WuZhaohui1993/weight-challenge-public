<template>
  <view class="header" :style="{ paddingTop: `${safeTop}px`, paddingRight: `${menuButtonSafeRight}px` }">
    <view class="header-body" :style="{ minHeight: `${topBarHeight - safeTop}px` }">
      <view class="header-main">
        <view class="header-back wc-back-chip wc-pressable" @tap="handleBack">
          <text class="wc-back-chip__icon">‹</text>
        </view>
        <text class="header-title">{{ title }}</text>
      </view>
      <view v-if="$slots.right || rightText" class="header-right wc-inline-link wc-inline-link--action wc-inline-link--strong wc-pressable" @tap="$emit('rightTap')">
        <slot name="right">
          <text>{{ rightText }}</text>
        </slot>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { getMenuButtonSafeRight, getSafeTop, getTopBarHeight, navigateBackOr } from '@/utils/mobile'

const props = defineProps<{
  title: string
  backUrl?: string
  rightText?: string
}>()

defineEmits<{
  (event: 'rightTap'): void
}>()

const safeTop = computed(() => getSafeTop(20))
const topBarHeight = computed(() => getTopBarHeight(52))
const menuButtonSafeRight = computed(() => getMenuButtonSafeRight(16, 18))

function handleBack() {
  navigateBackOr(props.backUrl || '/pages/index/index')
}
</script>

<style scoped lang="scss">
.header {
  padding: 0 30rpx;
  box-sizing: border-box;
  flex-shrink: 0;
  background: rgba(255, 255, 255, 0.88);
  position: sticky;
  top: 0;
  z-index: 50;
  backdrop-filter: blur(14px);
  border-bottom: 1px solid var(--wc-line);
}

.header-body {
  min-height: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20rpx;
}

.header-main {
  display: flex;
  align-items: center;
  gap: 16rpx;
  min-width: 0;
  flex: 1;
}

.header-back {
  margin-right: 2rpx;
}

.header-title {
  font-size: 34rpx;
  font-weight: 700;
  color: var(--wc-text);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.header-right {
  display: flex;
  align-items: center;
  flex-shrink: 0;
}
</style>
