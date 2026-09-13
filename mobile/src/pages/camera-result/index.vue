<template>
  <view class="page-container">
    <view class="image-container">
      <view class="camera-header" :style="{ paddingTop: `${safeTop}px`, paddingRight: `${menuButtonSafeRight}px` }">
        <view class="camera-header-body" :style="{ minHeight: `${topBarHeight - safeTop}px` }">
          <view class="wc-back-chip wc-pressable" @tap="goBack">
            <text class="wc-back-chip__icon">‹</text>
          </view>
          <text class="header-title">{{ pageTitle }}</text>
          <view class="wc-back-chip wc-pressable" @tap="pickImage('album')">
            <text class="header-action-icon">＋</text>
          </view>
        </view>
      </view>

      <view v-if="previewImage" class="preview-stage">
        <image class="preview-image" :src="previewImage" mode="aspectFill" />
        <view class="preview-overlay">
          <text class="preview-kicker">{{ sceneKicker }}</text>
          <text class="preview-title">{{ previewTitle }}</text>
          <text class="preview-desc">{{ previewDescription }}</text>
        </view>
      </view>

      <view v-else class="image-placeholder">
        <text class="image-icon">{{ sceneIcon }}</text>
        <text class="image-text">{{ emptyTitle }}</text>
        <text class="image-subtext">{{ emptyDescription }}</text>
      </view>
    </view>

    <view class="result-panel">
      <view class="panel-handle"></view>
      <view class="result-header">
        <view>
          <text class="total-calories">{{ panelHeadline }}</text>
          <text class="total-label">{{ panelSubline }}</text>
        </view>
        <text class="panel-status">{{ panelStatus }}</text>
      </view>

      <view class="empty-state">
        <text class="empty-title">{{ actionTitle }}</text>
        <text class="empty-desc">{{ actionDescription }}</text>
      </view>

      <view class="tip-card">
        <text class="tip-title">当前阶段怎么用</text>
        <text class="tip-desc">{{ phaseTip }}</text>
      </view>

      <view class="action-row">
        <button class="btn flow-btn flow-btn--secondary flow-btn--compact" @tap="pickImage('camera')">重拍</button>
        <button class="btn flow-btn flow-btn--secondary flow-btn--compact" @tap="pickImage('album')">从相册选</button>
      </view>

      <view class="action-row">
        <button class="btn flow-btn flow-btn--primary flow-btn--wide" @tap="goManualRecord">{{ primaryActionText }}</button>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { getMenuButtonSafeRight, getSafeTop, getTopBarHeight, navigateBackOr } from '@/utils/mobile'
import { useDefaultPageShare } from '@/utils/share'
import { chooseLocalImages, getUploadSizeLimitText } from '@/utils/upload'

useDefaultPageShare()

const safeTop = getSafeTop(20)
const topBarHeight = getTopBarHeight(52)
const menuButtonSafeRight = getMenuButtonSafeRight(16, 18)

const previewImage = ref('')

onLoad((query) => {
  if (query?.scene === 'diet' || query?.scene === 'food') {
    uni.showToast({ title: '该入口下个版本开放', icon: 'none' })
    uni.redirectTo({ url: '/pages/record/index?tab=diet' })
    return
  }
  if (typeof query?.image === 'string') {
    previewImage.value = decodeURIComponent(query.image)
  }
})

const pageTitle = computed(() => {
  return '照片记录'
})

const sceneIcon = computed(() => '⚖️')
const sceneKicker = computed(() => '体重照片')
const previewTitle = computed(() =>
  '照片已准备好'
)
const previewDescription = computed(() =>
  '当前先保留为记录参考，后续再补图片上传和识别。'
)
const emptyTitle = computed(() =>
  '先拍一张上秤照片'
)
const emptyDescription = computed(() =>
  '这一步先作为体重记录的辅助照片入口，帮助你保留当天状态。'
)
const panelHeadline = computed(() => (previewImage.value ? '已准备好手动记录' : '等待一张照片'))
const panelSubline = computed(() =>
  '先拍照留存，再回到体重记录完成填写。'
)
const panelStatus = computed(() => (previewImage.value ? '已选照片' : '待选择'))
const actionTitle = computed(() =>
  '当前先承接到体重手动记录'
)
const actionDescription = computed(() =>
  '这页不直接上传或识别照片，但会把“拍照”这个高频入口变成稳定过渡页，避免点进来只有占位提示。'
)
const phaseTip = computed(() =>
  `这轮先支持拍照预览和回到体重记录。单张不超过 ${getUploadSizeLimitText()}，图片上传、OCR 和自动识别继续放在后续阶段。`
)
const primaryActionText = computed(() =>
  '回到记体重'
)

function goBack() {
  navigateBackOr('/pages/index/index')
}

async function pickImage(sourceType: 'camera' | 'album') {
  try {
    const result = await chooseLocalImages({
      maxCount: 1,
      sourceType: [sourceType]
    })
    if (!result.filePaths.length) {
      return
    }
    previewImage.value = result.filePaths[0] || ''
  } catch (error: any) {
    uni.showToast({ title: error?.message || '图片读取失败', icon: 'none' })
  }
}

function goManualRecord() {
  uni.navigateTo({ url: '/pages/record/index?tab=weight' })
}
</script>

<style scoped lang="scss">
@use '../../styles/flow-button.scss';

.page-container {
  min-height: 100vh;
  background: #000000;
  color: white;
  display: flex;
  flex-direction: column;
}

.image-container {
  flex: 1;
  position: relative;
  background: radial-gradient(circle at top, rgba(74, 144, 226, 0.24), rgba(0, 0, 0, 0.92));
}

.camera-header {
  padding: 0 30rpx 24rpx;
  position: relative;
  z-index: 2;
}

.camera-header-body {
  min-height: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 18rpx;
}

.header-title {
  flex: 1;
  min-width: 0;
  text-align: center;
  font-size: 30rpx;
  font-weight: 700;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.header-action-icon {
  font-size: 34rpx;
  line-height: 1;
  color: var(--wc-text);
}

.preview-stage,
.image-placeholder {
  position: absolute;
  inset: 0;
}

.preview-image {
  width: 100%;
  height: 100%;
}

.preview-overlay {
  position: absolute;
  left: 30rpx;
  right: 30rpx;
  bottom: 40rpx;
  padding: 28rpx;
  border-radius: 28rpx;
  background: linear-gradient(180deg, rgba(15, 23, 42, 0.22), rgba(15, 23, 42, 0.72));
  backdrop-filter: blur(16rpx);
}

.preview-kicker,
.preview-title,
.preview-desc {
  display: block;
}

.preview-kicker {
  font-size: 22rpx;
  color: rgba(255, 255, 255, 0.72);
  margin-bottom: 8rpx;
}

.preview-title {
  font-size: 34rpx;
  font-weight: 700;
  margin-bottom: 10rpx;
}

.preview-desc {
  font-size: 24rpx;
  line-height: 1.65;
  color: rgba(255, 255, 255, 0.82);
}

.image-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 16rpx;
  padding: 0 48rpx;
  text-align: center;
}

.image-icon {
  font-size: 96rpx;
}

.image-text {
  font-size: 30rpx;
  font-weight: 700;
}

.image-subtext {
  font-size: 24rpx;
  line-height: 1.65;
  color: rgba(255, 255, 255, 0.72);
}

.result-panel {
  background: white;
  color: #333333;
  border-top-left-radius: 48rpx;
  border-top-right-radius: 48rpx;
  padding: 32rpx 30rpx calc(30rpx + env(safe-area-inset-bottom));
  min-height: 48vh;
}

.panel-handle {
  width: 72rpx;
  height: 8rpx;
  background: #dddddd;
  border-radius: 999rpx;
  margin: 0 auto 24rpx;
}

.result-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  gap: 20rpx;
  margin-bottom: 28rpx;
}

.total-calories {
  display: block;
  font-size: 44rpx;
  font-weight: 800;
  color: #4a90e2;
}

.total-label,
.panel-status {
  display: block;
  font-size: 22rpx;
  color: #888888;
}

.empty-state {
  padding: 12rpx 0 28rpx;
}

.empty-title {
  display: block;
  font-size: 30rpx;
  font-weight: 700;
  margin-bottom: 12rpx;
}

.empty-desc {
  display: block;
  font-size: 24rpx;
  line-height: 1.7;
  color: #666666;
}

.tip-card {
  padding: 24rpx;
  border-radius: 24rpx;
  background: #f6f8fb;
  border: 1px solid rgba(74, 144, 226, 0.08);
  margin-bottom: 24rpx;
}

.tip-title {
  display: block;
  font-size: 24rpx;
  font-weight: 700;
  margin-bottom: 8rpx;
}

.tip-desc {
  display: block;
  font-size: 22rpx;
  line-height: 1.6;
  color: #666666;
}

.action-row {
  margin-top: 16rpx;
  display: flex;
  gap: 18rpx;
}

.btn {
  flex: 1;
}
</style>
