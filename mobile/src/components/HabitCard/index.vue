<template>
  <view class="habit-card wc-pressable" :class="{ inactive: !habit.active, compact }" @tap="$emit('cardTap')">
    <view class="habit-main">
      <view class="habit-icon">{{ habit.icon || '✅' }}</view>
      <view class="habit-info">
        <view class="habit-name-row">
          <text class="habit-name">{{ habit.name }}</text>
          <text class="habit-badge" :class="{ active: habit.active }">{{ habit.active ? '启用中' : '已停用' }}</text>
        </view>
        <text class="habit-meta">{{ frequencyLabel }}</text>
        <text class="habit-progress">{{ progressText }}</text>
      </view>
    </view>

    <view v-if="habitImages.length" class="habit-image-grid">
      <resolved-image
        v-for="(image, index) in habitImages"
        :key="`habit-image-${habit.id}-${index}`"
        class="habit-image"
        :src="image"
        mode="aspectFill"
        :preview-list="habitImages"
        :preview-index="index"
      />
    </view>

    <slot name="media"></slot>

    <view v-if="secondaryActionText || primaryActionText" class="habit-actions">
      <view
        v-if="secondaryActionText"
        class="habit-action wc-pill-action wc-pressable"
        :class="secondaryActionToneClass"
        @tap.stop="$emit('secondaryTap')"
      >
        {{ secondaryActionText }}
      </view>
      <view
        v-if="primaryActionText"
        class="habit-action wc-pill-action wc-pressable"
        :class="primaryActionToneClass"
        @tap.stop="$emit('primaryTap')"
      >
        {{ primaryActionText }}
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import ResolvedImage from '@/components/ResolvedImage/index.vue'
import type { HabitItem } from '@/types/api'

const props = withDefaults(defineProps<{
  habit: HabitItem
  primaryActionText?: string
  primaryActionTone?: 'primary' | 'secondary' | 'soft'
  secondaryActionText?: string
  secondaryActionTone?: 'soft' | 'danger'
  compact?: boolean
}>(), {
  primaryActionText: '',
  primaryActionTone: 'primary',
  secondaryActionText: '',
  secondaryActionTone: 'soft',
  compact: false
})

defineEmits<{
  (event: 'cardTap'): void
  (event: 'primaryTap'): void
  (event: 'secondaryTap'): void
}>()

const frequencyLabel = computed(() => {
  if (props.habit.frequency === '1') {
    return props.habit.reminderTime
      ? `每周${weekdayLabel(props.habit.reminderWeekday)} ${props.habit.reminderTime}`
      : `每周${weekdayLabel(props.habit.reminderWeekday)}`
  }
  return props.habit.reminderTime ? `每日 ${props.habit.reminderTime}` : '每日'
})

function weekdayLabel(value?: string | null) {
  if (!value) {
    return '待设置'
  }
  return value
    .split(',')
    .map((item) => {
      switch (item) {
        case '1':
          return '一'
        case '2':
          return '二'
        case '3':
          return '三'
        case '4':
          return '四'
        case '5':
          return '五'
        case '6':
          return '六'
        case '0':
          return '日'
        default:
          return null
      }
    })
    .filter(Boolean)
    .join(' / ') || '待设置'
}

const progressText = computed(() => {
  if (props.habit.checkedToday) {
    return `今日已完成 · 连续 ${props.habit.currentStreak || 0} 天`
  }
  return `连续 ${props.habit.currentStreak || 0} 天 · 最佳 ${props.habit.longestStreak || 0} 天`
})

const habitImages = computed(() => parseHabitImages(props.habit.images))

const primaryActionToneClass = computed(() => {
  if (props.primaryActionTone === 'secondary') {
    return 'wc-pill-action--secondary'
  }
  if (props.primaryActionTone === 'soft') {
    return 'wc-pill-action--soft'
  }
  return 'wc-pill-action--primary'
})

const secondaryActionToneClass = computed(() => {
  if (props.secondaryActionTone === 'danger') {
    return 'wc-pill-action--danger'
  }
  return 'wc-pill-action--soft'
})

function parseHabitImages(rawImages?: string[] | string | null) {
  if (Array.isArray(rawImages)) {
    return rawImages.map((item) => `${item || ''}`.trim()).filter(Boolean).slice(0, 9)
  }
  if (typeof rawImages !== 'string' || !rawImages.trim()) {
    return []
  }
  const text = rawImages.trim()
  try {
    const parsed = JSON.parse(text)
    return Array.isArray(parsed)
      ? parsed.map((item) => `${item || ''}`.trim()).filter(Boolean).slice(0, 9)
      : [text]
  } catch {
    return [text]
  }
}

</script>

<style scoped lang="scss">
.habit-card {
  background: var(--wc-surface-strong);
  border-radius: 30rpx;
  padding: 26rpx;
  border: 1px solid var(--wc-line);
  box-shadow: var(--wc-shadow-soft);
}

.habit-card.inactive {
  opacity: 0.74;
}

.habit-card.compact {
  padding: 24rpx;
}

.habit-main {
  display: flex;
  align-items: flex-start;
  gap: 18rpx;
}

.habit-icon {
  width: 88rpx;
  height: 88rpx;
  border-radius: 24rpx;
  background: linear-gradient(180deg, rgba(74, 144, 226, 0.1), rgba(255, 255, 255, 0.98));
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 40rpx;
  flex-shrink: 0;
}

.habit-info {
  flex: 1;
  min-width: 0;
}

.habit-name-row {
  display: flex;
  align-items: center;
  gap: 12rpx;
  margin-bottom: 10rpx;
}

.habit-name {
  font-size: 30rpx;
  font-weight: 700;
  color: var(--wc-text);
}

.habit-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  height: 38rpx;
  padding: 0 14rpx;
  border-radius: 999rpx;
  background: rgba(28, 37, 54, 0.08);
  color: var(--wc-text-soft);
  font-size: 20rpx;
  flex-shrink: 0;
}

.habit-badge.active {
  background: rgba(77, 138, 255, 0.12);
  color: #4d8aff;
}

.habit-meta,
.habit-progress {
  display: block;
  font-size: 22rpx;
  line-height: 1.7;
  color: var(--wc-text-soft);
}

.habit-actions {
  margin-top: 20rpx;
  display: flex;
  justify-content: flex-end;
  gap: 12rpx;
}

.habit-action {
  min-width: 148rpx;
  justify-content: center;
}

.habit-image-grid {
  margin-top: 20rpx;
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 10rpx;
}

.habit-image {
  width: 100%;
  height: 144rpx;
  border-radius: 18rpx;
  overflow: hidden;
  background: var(--wc-surface-muted);
}
</style>
