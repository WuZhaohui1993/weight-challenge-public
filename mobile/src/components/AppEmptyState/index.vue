<template>
  <view class="empty-state wc-page-enter">
    <text class="empty-icon">{{ icon }}</text>
    <text class="empty-title">{{ title }}</text>
    <text class="empty-desc">{{ description }}</text>
    <view v-if="$slots.default" class="empty-action">
      <slot />
    </view>
  </view>
</template>

<script setup lang="ts">
defineProps<{
  icon: string
  title: string
  description: string
}>()
</script>

<style scoped lang="scss">
.empty-state {
  background: var(--wc-surface-strong);
  border: 1px solid var(--wc-line);
  border-radius: var(--wc-radius-l);
  padding: 72rpx 40rpx;
  text-align: center;
  box-shadow: var(--wc-shadow-soft);
}

.empty-icon {
  display: block;
  font-size: 68rpx;
  margin-bottom: 18rpx;
}

.empty-title {
  display: block;
  font-size: 30rpx;
  font-weight: 700;
  color: var(--wc-text);
  margin-bottom: 12rpx;
}

.empty-desc {
  display: block;
  font-size: 24rpx;
  line-height: 1.7;
  color: var(--wc-text-soft);
}

.empty-action {
  margin-top: 24rpx;
}
</style>
