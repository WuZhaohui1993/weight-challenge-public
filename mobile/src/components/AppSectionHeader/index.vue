<template>
  <view class="section-header" :class="{ compact }">
    <view class="title-block">
      <text class="title">{{ title }}</text>
      <text v-if="subtitle" class="subtitle">{{ subtitle }}</text>
    </view>
    <view v-if="$slots.actions || actionText" class="actions">
      <slot name="actions">
        <text class="action-text wc-inline-link wc-inline-link--action wc-inline-link--strong wc-pressable" @tap="$emit('actionTap')">{{ actionText }}</text>
      </slot>
    </view>
  </view>
</template>

<script setup lang="ts">
defineProps<{
  title: string
  subtitle?: string
  actionText?: string
  compact?: boolean
}>()

defineEmits<{
  (event: 'actionTap'): void
}>()
</script>

<style scoped lang="scss">
.section-header {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 24rpx;
  padding: 34rpx 30rpx 18rpx;
}

.section-header.compact {
  padding-top: 18rpx;
}

.title-block {
  display: flex;
  flex-direction: column;
  gap: 8rpx;
}

.title {
  font-size: 34rpx;
  font-weight: 700;
  color: var(--wc-text);
  line-height: 1.2;
}

.subtitle {
  font-size: 22rpx;
  color: var(--wc-text-faint);
  line-height: 1.5;
}

.actions {
  display: flex;
  align-items: center;
  gap: 16rpx;
}

.action-text {
  line-height: 1.2;
}
</style>
