<template>
  <view v-if="visible" class="onboarding-mask" @tap="closeGuide">
    <view class="onboarding-panel" @tap.stop>
      <view class="onboarding-header">
        <view class="onboarding-title-wrap">
          <text class="onboarding-kicker">新手指南 · 第 {{ currentStepIndex + 1 }} / {{ steps.length }} 步</text>
          <text class="onboarding-title">{{ currentStepConfig.title }}</text>
          <text class="onboarding-desc">{{ currentStepConfig.description }}</text>
        </view>
        <view class="onboarding-close wc-pressable" @tap="closeGuide">×</view>
      </view>

      <view class="onboarding-steps">
        <view
          v-for="(step, index) in steps"
          :key="step.key"
          class="onboarding-step wc-pressable"
          :class="{
            'onboarding-step--active': step.key === currentStep,
            'onboarding-step--done': index < currentStepIndex
          }"
          @tap="selectStep(step.key)"
        >
          <view class="step-index" :class="step.indexClass">{{ index + 1 }}</view>
          <view class="step-copy">
            <text class="step-title">{{ step.shortTitle }}</text>
            <text class="step-desc">{{ step.shortDescription }}</text>
          </view>
          <text class="step-action">{{ stepStatusText(index) }}</text>
        </view>
      </view>

      <view class="onboarding-actions">
        <button class="onboarding-btn flow-btn flow-btn--primary" @tap="handlePrimary">{{ currentStepConfig.primaryText }}</button>
        <button class="onboarding-btn flow-btn flow-btn--secondary" @tap="skipCurrentStep">{{ currentStepConfig.secondaryText }}</button>
      </view>

      <view class="onboarding-secondary">
        <text class="onboarding-link" @tap="closeGuide">稍后继续</text>
        <text class="onboarding-divider">/</text>
        <text class="onboarding-link onboarding-link--muted" @tap="disableGuide">不再提示</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { OnboardingGuideStep } from '@/utils/onboarding'

const props = defineProps<{
  visible: boolean
  currentStep: OnboardingGuideStep
}>()

const emit = defineEmits<{
  (event: 'close'): void
  (event: 'disable'): void
  (event: 'primary'): void
  (event: 'skip'): void
  (event: 'select-step', step: OnboardingGuideStep): void
}>()

const steps: Array<{
  key: OnboardingGuideStep
  title: string
  description: string
  shortTitle: string
  shortDescription: string
  primaryText: string
  secondaryText: string
  indexClass: string
}> = [
  {
    key: 'profile',
    title: '先完善资料',
    description: '头像、昵称和基础目标会影响首页摘要、趋势分析和圈子展示。',
    shortTitle: '完善资料',
    shortDescription: '让首页摘要和分析结果更准确。',
    primaryText: '去完善资料',
    secondaryText: '跳过本步',
    indexClass: ''
  },
  {
    key: 'record',
    title: '完成第一条记录',
    description: '先记录一次体重、饮食、运动或饮水，首页就会开始更新。',
    shortTitle: '完成第一条记录',
    shortDescription: '让首页和趋势从真实数据开始。',
    primaryText: '去记录',
    secondaryText: '跳过本步',
    indexClass: 'step-index--primary'
  },
  {
    key: 'explore',
    title: '接着看趋势或圈子',
    description: '记录稳定后，可以看分析，也可以加入圈子一起目标打卡。',
    shortTitle: '看趋势或圈子',
    shortDescription: '用分析和圈子把记录持续下来。',
    primaryText: '看分析',
    secondaryText: '去圈子',
    indexClass: 'step-index--success'
  }
]

const currentStepIndex = computed(() => {
  const index = steps.findIndex((step) => step.key === props.currentStep)
  return index >= 0 ? index : 0
})

const currentStepConfig = computed(() => steps[currentStepIndex.value])

function stepStatusText(index: number) {
  if (index < currentStepIndex.value) {
    return '已完成'
  }
  if (index === currentStepIndex.value) {
    return '当前'
  }
  return '待进行'
}

function closeGuide() {
  emit('close')
}

function disableGuide() {
  emit('disable')
}

function handlePrimary() {
  emit('primary')
}

function skipCurrentStep() {
  emit('skip')
}

function selectStep(step: OnboardingGuideStep) {
  emit('select-step', step)
}
</script>

<style scoped lang="scss">
@use '../../styles/flow-button.scss';

.onboarding-mask {
  position: fixed;
  inset: 0;
  z-index: 1200;
  display: flex;
  align-items: flex-end;
  background: rgba(15, 23, 42, 0.32);
}

.onboarding-panel {
  width: 100%;
  max-height: 88vh;
  padding: 32rpx 28rpx calc(30rpx + env(safe-area-inset-bottom));
  box-sizing: border-box;
  border-radius: 38rpx 38rpx 0 0;
  background: var(--wc-surface-strong);
  box-shadow: 0 -18rpx 50rpx rgba(15, 23, 42, 0.14);
}

.onboarding-header {
  display: flex;
  align-items: flex-start;
  gap: 20rpx;
  margin-bottom: 24rpx;
}

.onboarding-title-wrap {
  flex: 1;
  min-width: 0;
}

.onboarding-kicker,
.onboarding-title,
.onboarding-desc {
  display: block;
}

.onboarding-kicker {
  margin-bottom: 8rpx;
  font-size: 22rpx;
  font-weight: 700;
  color: var(--wc-primary-strong);
}

.onboarding-title {
  font-size: 34rpx;
  line-height: 1.35;
  font-weight: 800;
  color: var(--wc-text);
}

.onboarding-desc {
  margin-top: 10rpx;
  font-size: 24rpx;
  line-height: 1.6;
  color: var(--wc-text-soft);
}

.onboarding-close {
  width: 64rpx;
  height: 64rpx;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background: var(--wc-surface-muted);
  color: var(--wc-text-faint);
  font-size: 40rpx;
  line-height: 1;
}

.onboarding-steps {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.onboarding-step {
  display: flex;
  align-items: center;
  gap: 18rpx;
  padding: 22rpx 20rpx;
  border-radius: 26rpx;
  border: 1px solid var(--wc-line);
  background: var(--wc-bg-soft);
}

.onboarding-step--active {
  border-color: rgba(74, 144, 226, 0.34);
  background: var(--wc-primary-soft);
}

.onboarding-step--done {
  background: rgba(43, 167, 121, 0.08);
}

.step-index {
  width: 52rpx;
  height: 52rpx;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background: rgba(242, 155, 56, 0.14);
  color: var(--wc-warning);
  font-size: 26rpx;
  font-weight: 800;
}

.step-index--primary {
  background: var(--wc-primary-soft);
  color: var(--wc-primary-strong);
}

.step-index--success {
  background: rgba(43, 167, 121, 0.14);
  color: var(--wc-success);
}

.step-copy {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 6rpx;
}

.step-title {
  font-size: 28rpx;
  font-weight: 700;
  color: var(--wc-text);
}

.step-desc {
  font-size: 22rpx;
  line-height: 1.5;
  color: var(--wc-text-soft);
}

.step-action {
  flex-shrink: 0;
  font-size: 24rpx;
  font-weight: 700;
  color: var(--wc-primary-strong);
}

.onboarding-step--done .step-action {
  color: var(--wc-success);
}

.onboarding-step:not(.onboarding-step--active):not(.onboarding-step--done) .step-action {
  color: var(--wc-text-faint);
}

.onboarding-actions {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 18rpx;
  margin-top: 28rpx;
}

.onboarding-btn {
  width: 100%;
}

.onboarding-secondary {
  margin-top: 22rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16rpx;
}

.onboarding-link,
.onboarding-divider {
  font-size: 24rpx;
  color: var(--wc-primary-strong);
}

.onboarding-link--muted,
.onboarding-divider {
  color: var(--wc-text-faint);
}
</style>
