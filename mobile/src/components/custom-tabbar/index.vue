<template>
  <view class="custom-tabbar">
    <!-- 首页 -->
    <view 
      class="tab-item" 
      :class="{ active: currentIndex === 0 }"
      @tap="switchTab(0, '/pages/index/index')"
    >
      <image 
        class="tab-icon" 
        :src="currentIndex === 0 ? '/static/tabbar/home_active.png' : '/static/tabbar/home.png'" 
      />
      <text class="tab-label">首页</text>
    </view>

    <!-- 圈子 -->
    <view 
      class="tab-item" 
      :class="{ active: currentIndex === 1 }"
      @tap="switchTab(1, '/pages/circle/index')"
    >
      <image 
        class="tab-icon" 
        :src="currentIndex === 1 ? '/static/tabbar/circle_active.png' : '/static/tabbar/circle.png'" 
      />
      <text class="tab-label">圈子</text>
    </view>

    <!-- 中间的 + 按钮 -->
    <view class="tab-item tab-center">
      <view class="center-btn" @tap="goQuickRecord">
        <text class="center-icon">+</text>
      </view>
    </view>

    <!-- 分析 -->
    <view 
      class="tab-item" 
      :class="{ active: currentIndex === 2 }"
      @tap="switchTab(2, '/pages/analytics/index')"
    >
      <image 
        class="tab-icon" 
        :src="currentIndex === 2 ? '/static/tabbar/record_active.png' : '/static/tabbar/record.png'" 
      />
      <text class="tab-label">分析</text>
    </view>

    <!-- 我的 -->
    <view 
      class="tab-item" 
      :class="{ active: currentIndex === 3 }"
      @tap="switchTab(3, '/pages/profile/index')"
    >
      <image 
        class="tab-icon" 
        :src="currentIndex === 3 ? '/static/tabbar/profile_active.png' : '/static/tabbar/profile.png'" 
      />
      <text class="tab-label">我的</text>
    </view>

  </view>

  <view v-if="actionSheetVisible" class="action-sheet" @tap="closeActionSheet">
    <view class="action-sheet-content" @tap.stop>
      <view class="action-sheet-header">记录一下今天的进展</view>

      <view class="action-sub-grid">
        <view class="action-item" @tap="openRecord('weight')">
          <view class="action-icon weight">⚖️</view>
          <text class="action-name">记体重</text>
        </view>
        <view class="action-item" @tap="openRecord('diet')">
          <view class="action-icon diet">🥗</view>
          <text class="action-name">记饮食</text>
        </view>
        <view class="action-item" @tap="openRecord('exercise')">
          <view class="action-icon exercise">🏃</view>
          <text class="action-name">记运动</text>
        </view>
        <view class="action-item" @tap="openRecord('habit')">
          <view class="action-icon habit">📋</view>
          <text class="action-name">记习惯</text>
        </view>
      </view>

      <view class="action-sheet-close" @tap="closeActionSheet">✕</view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const props = defineProps<{
  currentIndex: number
}>()

const actionSheetVisible = ref(false)

function switchTab(index: number, url: string) {
  if (index === props.currentIndex) return
  uni.switchTab({ url })
}

function goQuickRecord() {
  actionSheetVisible.value = true
}

function closeActionSheet() {
  actionSheetVisible.value = false
}

function openRecord(tab: string) {
  closeActionSheet()
  uni.navigateTo({ url: `/pages/record/index?tab=${tab}` })
}
</script>

<style lang="scss" scoped>
$primary: var(--wc-primary);
$text-main: var(--wc-text);
$text-sub: var(--wc-text-faint);

.custom-tabbar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  height: 180rpx;
  background: rgba(255, 255, 255, 0.94);
  display: flex;
  justify-content: space-around;
  align-items: flex-start;
  padding-top: 14rpx;
  padding-bottom: env(safe-area-inset-bottom);
  box-shadow: 0 -10rpx 30rpx rgba(16, 24, 40, 0.06);
  border-top-left-radius: 36rpx;
  border-top-right-radius: 36rpx;
  border-top: 1px solid var(--wc-line);
  z-index: 999;
}

.tab-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: $text-sub;
  transition: transform 0.18s ease;
}

.tab-item.active {
  color: var(--wc-text);
}

.tab-item:active {
  transform: scale(0.98);
}

.tab-icon {
  width: 46rpx;
  height: 46rpx;
  margin-bottom: 8rpx;
}

.tab-label {
  font-size: 20rpx;
  font-weight: 600;
}

// 中间突出按钮
.tab-center {
  position: relative;
  top: -42rpx;
}

.center-btn {
  width: 108rpx;
  height: 108rpx;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--wc-primary-strong), var(--wc-primary));
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 18rpx 40rpx rgba(15, 23, 42, 0.22);
}

.center-icon {
  font-size: 56rpx;
  font-weight: 300;
  line-height: 1;
}

// Action Sheet 弹窗
.action-sheet {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  z-index: 1000;
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
  opacity: 1;
}

.action-sheet-content {
  background: rgba(255, 255, 255, 0.97);
  border-top-left-radius: 44rpx;
  border-top-right-radius: 44rpx;
  padding: 56rpx 32rpx;
  box-shadow: 0 -10rpx 30rpx rgba(16, 24, 40, 0.12);
}

.action-sheet-header {
  font-size: 34rpx;
  font-weight: 700;
  text-align: center;
  margin-bottom: 32rpx;
  color: var(--wc-text);
}

.action-item {
  background: var(--wc-surface-muted);
  border: 1px solid var(--wc-line);
  border-radius: 28rpx;
  padding: 28rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  margin-bottom: 16rpx;
}

.action-item.primary {
  background: linear-gradient(135deg, rgba(74, 144, 226, 0.14), rgba(248, 251, 255, 0.98));
  flex-direction: row;
  justify-content: flex-start;
  gap: 24rpx;
  padding: 32rpx;
}

.action-icon {
  width: 76rpx;
  height: 76rpx;
  border-radius: 22rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 36rpx;
  background: white;
  
  &.camera { color: $primary; }
  &.weight { color: var(--wc-primary-strong); }
  &.diet { color: var(--wc-primary-strong); }
  &.exercise { color: var(--wc-primary-strong); }
  &.habit { color: var(--wc-primary-strong); }
}

.action-info {
  display: flex;
  flex-direction: column;
}

.action-item.primary .action-name {
  font-size: 32rpx;
  font-weight: 700;
  color: var(--wc-text);
}

.action-desc {
  font-size: 22rpx;
  color: var(--wc-text-soft);
  margin-top: 8rpx;
}

.action-sub-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20rpx;
}

.action-sub-grid .action-item {
  height: 160rpx;
}

.action-sub-grid .action-name {
  font-size: 24rpx;
  margin-top: 16rpx;
  color: var(--wc-text);
}

.action-sheet-close {
  margin-top: 40rpx;
  text-align: center;
  font-size: 48rpx;
  color: var(--wc-text-faint);
  padding: 20rpx;
}
</style>
