<template>
  <view class="feedback-page wc-page-enter">
    <prototype-header title="建议 / Bug 反馈" back-url="/pages/profile/index" />

    <scroll-view class="feedback-scroll" scroll-y>
      <view class="feedback-tabs wc-tab-rail">
        <view
          class="wc-tab-item wc-tab-item--full wc-pressable"
          :class="{ active: activeTab === 'submit' }"
          @tap="activeTab = 'submit'"
        >
          提交反馈
        </view>
        <view
          class="wc-tab-item wc-tab-item--full wc-pressable"
          :class="{ active: activeTab === 'records' }"
          @tap="switchToRecords"
        >
          处理进度
        </view>
      </view>

      <template v-if="activeTab === 'submit'">
        <view class="feedback-panel">
          <view class="form-group">
            <text class="wc-form-label">反馈类型</text>
            <view class="category-grid">
              <view
                v-for="item in categoryOptions"
                :key="item.value"
                class="category-chip wc-pressable"
                :class="{ active: form.category === item.value }"
                @tap="form.category = item.value"
              >
                <text class="category-chip__icon">{{ item.icon }}</text>
                <text class="category-chip__label">{{ item.label }}</text>
              </view>
            </view>
          </view>

          <view class="form-group">
            <view class="form-label-row">
              <text class="wc-form-label">问题描述</text>
              <text class="field-count">{{ contentLength }}/500</text>
            </view>
            <textarea
              v-model="form.content"
              class="feedback-textarea"
              maxlength="500"
              placeholder="请写清楚发生了什么、你期待怎么改"
              placeholder-style="color:#9aa4b2;"
            />
          </view>

          <view class="form-group">
            <view class="form-label-row">
              <text class="wc-form-label">截图</text>
              <text class="field-count">{{ feedbackImages.length }}/3</text>
            </view>
            <view class="image-grid">
              <view
                v-for="(image, index) in feedbackImages"
                :key="image.id"
                class="image-tile"
              >
                <resolved-image
                  class="image-preview"
                  :src="getUploadedImagePreviewSource(image)"
                  mode="aspectFill"
                  :preview-list="feedbackPreviewSources"
                  :preview-index="index"
                />
                <view v-if="image.uploading" class="image-status">
                  <text class="image-status__text">上传中</text>
                </view>
                <view v-else-if="image.errorMessage" class="image-status image-status--error">
                  <text class="image-status__text">上传失败</text>
                  <text class="image-retry" @tap.stop="retryFeedbackImage(index)">重试</text>
                </view>
                <text class="image-remove" @tap.stop="removeFeedbackImage(index)">×</text>
              </view>
              <view
                v-if="feedbackImages.length < 3"
                class="image-add wc-pressable"
                @tap="chooseFeedbackImages"
              >
                <text class="image-add__plus">+</text>
                <text class="image-add__text">添加截图</text>
              </view>
            </view>
          </view>

          <view class="form-group">
            <text class="wc-form-label">联系方式</text>
            <input
              v-model="form.contact"
              class="wc-form-input"
              maxlength="80"
              placeholder="手机号 / 微信号 / 邮箱，可不填"
              placeholder-style="color:#9aa4b2;"
            />
          </view>

          <button
            class="flow-btn flow-btn--primary flow-btn--wide submit-btn"
            :loading="submitting"
            :disabled="submitting || uploading"
            @tap="submitFeedbackForm"
          >
            提交反馈
          </button>
        </view>
      </template>

      <template v-else>
        <view class="records-header">
          <app-section-header compact title="处理进度" :subtitle="recordsSubtitle" />
          <button class="flow-btn flow-btn--secondary flow-btn--compact" @tap="loadFeedbackRecords">刷新</button>
        </view>

        <view v-if="loadingRecords" class="feedback-panel feedback-panel--state">反馈加载中...</view>

        <view v-else-if="recordsError" class="feedback-panel">
          <app-empty-state icon="⚠️" title="反馈加载失败" :description="recordsError">
            <button class="flow-btn flow-btn--secondary flow-btn--compact" @tap="loadFeedbackRecords">重新整理</button>
          </app-empty-state>
        </view>

        <view v-else-if="!feedbackRecords.length" class="feedback-panel">
          <app-empty-state icon="💬" title="还没有反馈记录" description="提交后可以在这里查看处理进度。" />
        </view>

        <view v-else class="record-list">
          <view v-for="item in feedbackRecords" :key="item.id" class="record-card">
            <view class="record-card__top">
              <view class="record-card__title">
                <text>{{ item.categoryLabel }}</text>
                <text class="record-id">#{{ item.id }}</text>
              </view>
              <text class="status-badge" :class="`status-badge--${item.status}`">{{ item.statusLabel }}</text>
            </view>
            <text class="record-content">{{ item.content }}</text>
            <view v-if="item.images.length" class="record-images">
              <resolved-image
                v-for="(image, index) in item.images.slice(0, 3)"
                :key="image + index"
                class="record-image"
                :src="image"
                mode="aspectFill"
                :preview-list="item.images"
                :preview-index="index"
              />
            </view>
            <view v-if="item.replyContent" class="reply-box">
              <text class="reply-label">处理回复</text>
              <text class="reply-content">{{ item.replyContent }}</text>
              <text v-if="item.replyTime" class="reply-time">{{ item.replyTime }}</text>
            </view>
            <view class="record-footer">
              <text>{{ item.createTime || '刚刚提交' }}</text>
              <text>{{ item.sourcePage || '小程序' }}</text>
            </view>
          </view>
        </view>
      </template>

      <view class="page-bottom-space"></view>
    </scroll-view>
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { onLoad, onShow } from '@dcloudio/uni-app'
import AppEmptyState from '@/components/AppEmptyState/index.vue'
import AppSectionHeader from '@/components/AppSectionHeader/index.vue'
import PrototypeHeader from '@/components/PrototypeHeader/index.vue'
import ResolvedImage from '@/components/ResolvedImage/index.vue'
import { getMyFeedback, submitFeedback, uploadFeedbackImage } from '@/api/feedback'
import { useUserStore } from '@/stores/user'
import { openLoginPage } from '@/utils/request'
import { useDefaultPageShare } from '@/utils/share'
import {
  chooseAndUploadImages,
  getUploadedImagePreviewSource,
  hasFailedImageAssets,
  replaceUploadedImageAsset,
  retryUploadedImageAsset,
  type UploadedImageAsset
} from '@/utils/upload'
import type { FeedbackCategory, FeedbackItem } from '@/types/api'

useDefaultPageShare()

const APP_VERSION = '1.0.0'

const userStore = useUserStore()
const activeTab = ref<'submit' | 'records'>('submit')
const submitting = ref(false)
const uploading = ref(false)
const loadingRecords = ref(false)
const recordsLoaded = ref(false)
const recordsError = ref('')
const feedbackImages = ref<UploadedImageAsset[]>([])
const feedbackPreviewSources = computed(() => feedbackImages.value
  .map((image) => getUploadedImagePreviewSource(image))
  .filter(Boolean))
const feedbackRecords = ref<FeedbackItem[]>([])
const form = ref<{
  category: FeedbackCategory
  content: string
  contact: string
}>({
  category: 'bug',
  content: '',
  contact: ''
})

const categoryOptions: Array<{ label: string; value: FeedbackCategory; icon: string }> = [
  { label: 'Bug 问题', value: 'bug', icon: '🐞' },
  { label: '功能建议', value: 'suggestion', icon: '💡' },
  { label: '体验问题', value: 'experience', icon: '✨' },
  { label: '账号数据', value: 'account', icon: '🔐' },
  { label: '其他', value: 'other', icon: '💬' }
]

const contentLength = computed(() => form.value.content.trim().length)
const recordsSubtitle = computed(() => (
  feedbackRecords.value.length ? `共 ${feedbackRecords.value.length} 条反馈` : '提交记录会按时间倒序展示'
))

onShow(() => {
  if (!ensureLoggedIn()) {
    return
  }
  if (activeTab.value === 'records') {
    void loadFeedbackRecords()
  }
})

onLoad((options) => {
  if (options?.tab === 'records') {
    activeTab.value = 'records'
  }
})

function ensureLoggedIn() {
  if (userStore.isLoggedIn) {
    return true
  }
  openLoginPage('safe', getCurrentRoutePath() || '/pages/feedback/index')
  return false
}

function getCurrentRoutePath() {
  try {
    const pages = getCurrentPages()
    const currentPage = pages[pages.length - 1] as { route?: string; options?: Record<string, unknown> } | undefined
    if (!currentPage?.route) {
      return ''
    }
    const query = currentPage.options
      ? Object.entries(currentPage.options)
        .map(([key, value]) => `${encodeURIComponent(key)}=${encodeURIComponent(String(value))}`)
        .join('&')
      : ''
    return `/${currentPage.route}${query ? `?${query}` : ''}`
  } catch (error) {
    return ''
  }
}

function getNetworkType() {
  return new Promise<string>((resolve) => {
    uni.getNetworkType({
      success: (res) => resolve(res.networkType || 'unknown'),
      fail: () => resolve('unknown')
    })
  })
}

async function collectEnvironment() {
  let systemInfo: Record<string, unknown> = {}
  try {
    systemInfo = uni.getSystemInfoSync() as unknown as Record<string, unknown>
  } catch (error) {
    systemInfo = {}
  }

  return {
    appVersion: APP_VERSION,
    platform: 'mp-weixin',
    route: getCurrentRoutePath(),
    networkType: await getNetworkType(),
    brand: systemInfo.brand,
    model: systemInfo.model,
    system: systemInfo.system,
    platformName: systemInfo.platform,
    SDKVersion: systemInfo.SDKVersion,
    version: systemInfo.version,
    windowWidth: systemInfo.windowWidth,
    windowHeight: systemInfo.windowHeight,
    language: systemInfo.language
  }
}

async function chooseFeedbackImages() {
  if (!ensureLoggedIn() || uploading.value || feedbackImages.value.length >= 3) {
    return
  }
  try {
    await chooseAndUploadImages({
      currentCount: feedbackImages.value.length,
      maxCount: 3,
      upload: uploadFeedbackImage,
      fallbackErrorMessage: '截图上传失败',
      setUploading: (nextUploading) => {
        uploading.value = nextUploading
      },
      onLocalAssetsSelected: (assets) => {
        feedbackImages.value = [...feedbackImages.value, ...assets].slice(0, 3)
      },
      onAssetUpdated: (asset) => {
        feedbackImages.value = replaceUploadedImageAsset(feedbackImages.value, asset)
      }
    })
  } catch (error: any) {
    uni.showToast({ title: error?.message || '截图上传失败', icon: 'none' })
  }
}

function removeFeedbackImage(index: number) {
  feedbackImages.value = feedbackImages.value.filter((_, currentIndex) => currentIndex !== index)
}

async function retryFeedbackImage(index: number) {
  const image = feedbackImages.value[index]
  if (!image || image.uploading) {
    return
  }
  try {
    await retryUploadedImageAsset({
      asset: image,
      upload: uploadFeedbackImage,
      fallbackErrorMessage: '截图上传失败',
      setUploading: (nextUploading) => {
        uploading.value = nextUploading
      },
      onAssetUpdated: (asset) => {
        feedbackImages.value = replaceUploadedImageAsset(feedbackImages.value, asset)
      }
    })
  } catch (error: any) {
    uni.showToast({ title: error?.message || '截图上传失败', icon: 'none' })
  }
}

function resetForm() {
  form.value = {
    category: 'bug',
    content: '',
    contact: ''
  }
  feedbackImages.value = []
}

async function submitFeedbackForm() {
  if (!ensureLoggedIn() || submitting.value) {
    return
  }

  const content = form.value.content.trim()
  if (content.length < 10) {
    uni.showToast({ title: '请至少填写 10 个字', icon: 'none' })
    return
  }
  if (hasFailedImageAssets(feedbackImages.value)) {
    uni.showToast({ title: '请先处理上传失败的截图', icon: 'none' })
    return
  }
  if (feedbackImages.value.some((image) => image.uploading)) {
    uni.showToast({ title: '截图上传中，请稍后提交', icon: 'none' })
    return
  }

  submitting.value = true
  try {
    await submitFeedback({
      category: form.value.category,
      content,
      contact: form.value.contact.trim() || null,
      sourcePage: getCurrentRoutePath(),
      images: feedbackImages.value.map((image) => image.url).filter(Boolean),
      environment: await collectEnvironment()
    })
    uni.showToast({ title: '反馈已提交', icon: 'success' })
    resetForm()
    activeTab.value = 'records'
    await loadFeedbackRecords()
  } catch (error: any) {
    uni.showToast({ title: error?.message || '提交失败', icon: 'none' })
  } finally {
    submitting.value = false
  }
}

async function loadFeedbackRecords() {
  if (!ensureLoggedIn()) {
    return
  }
  loadingRecords.value = true
  recordsError.value = ''
  try {
    const page = await getMyFeedback({ pageNum: 1, pageSize: 20 })
    feedbackRecords.value = page.list || []
    recordsLoaded.value = true
  } catch (error: any) {
    recordsError.value = error?.message || '反馈记录加载失败'
  } finally {
    loadingRecords.value = false
  }
}

function switchToRecords() {
  activeTab.value = 'records'
  if (!recordsLoaded.value) {
    void loadFeedbackRecords()
  }
}
</script>

<style lang="scss" scoped>
@use '../../styles/flow-button.scss';

.feedback-page {
  min-height: 100vh;
  background: var(--wc-bg);
}

.feedback-scroll {
  height: calc(100vh - 112rpx);
  box-sizing: border-box;
  padding: 24rpx;
}

.feedback-tabs {
  margin-bottom: 24rpx;
}

.feedback-panel {
  background: var(--wc-surface-strong);
  border: 1px solid var(--wc-line);
  border-radius: var(--wc-radius-l);
  padding: 28rpx;
  box-shadow: var(--wc-shadow-soft);
}

.feedback-panel--state {
  text-align: center;
  color: var(--wc-text-soft);
  font-size: 26rpx;
}

.form-group {
  margin-bottom: 28rpx;
}

.form-label-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16rpx;
}

.field-count {
  font-size: 22rpx;
  color: var(--wc-text-faint);
}

.category-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16rpx;
  margin-top: 14rpx;
}

.category-chip {
  min-width: 0;
  min-height: 84rpx;
  padding: 18rpx;
  border: 1px solid var(--wc-line);
  border-radius: 22rpx;
  background: var(--wc-surface);
  display: flex;
  align-items: center;
  gap: 12rpx;
  box-sizing: border-box;
}

.category-chip.active {
  border-color: rgba(37, 99, 235, 0.42);
  background: var(--wc-primary-soft);
  color: var(--wc-primary-strong);
}

.category-chip__icon {
  font-size: 30rpx;
  flex-shrink: 0;
}

.category-chip__label {
  font-size: 26rpx;
  font-weight: 600;
  white-space: nowrap;
}

.feedback-textarea {
  width: 100%;
  min-height: 240rpx;
  margin-top: 14rpx;
  box-sizing: border-box;
  padding: 22rpx;
  border: 1px solid var(--wc-line);
  border-radius: 22rpx;
  background: var(--wc-surface);
  color: var(--wc-text);
  font-size: 28rpx;
  line-height: 1.6;
}

.image-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 14rpx;
  margin-top: 14rpx;
}

.image-tile,
.image-add {
  position: relative;
  aspect-ratio: 1;
  border-radius: 22rpx;
  overflow: hidden;
  background: var(--wc-surface);
  border: 1px solid var(--wc-line);
}

.image-preview {
  width: 100%;
  height: 100%;
}

.image-status {
  position: absolute;
  inset: 0;
  background: rgba(15, 23, 42, 0.48);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8rpx;
}

.image-status--error {
  background: rgba(127, 29, 29, 0.62);
}

.image-status__text,
.image-retry {
  font-size: 22rpx;
  color: #fff;
}

.image-retry {
  font-weight: 700;
}

.image-remove {
  position: absolute;
  top: 8rpx;
  right: 8rpx;
  width: 36rpx;
  height: 36rpx;
  border-radius: 50%;
  background: rgba(15, 23, 42, 0.58);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28rpx;
  line-height: 1;
}

.image-add {
  border-style: dashed;
  color: var(--wc-text-soft);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8rpx;
}

.image-add__plus {
  font-size: 46rpx;
  line-height: 1;
}

.image-add__text {
  font-size: 22rpx;
}

.submit-btn {
  margin-top: 8rpx;
}

.records-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16rpx;
  margin-bottom: 18rpx;
}

.record-list {
  display: flex;
  flex-direction: column;
  gap: 18rpx;
}

.record-card {
  background: var(--wc-surface-strong);
  border: 1px solid var(--wc-line);
  border-radius: var(--wc-radius-l);
  padding: 24rpx;
  box-shadow: var(--wc-shadow-soft);
}

.record-card__top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16rpx;
  margin-bottom: 16rpx;
}

.record-card__title {
  min-width: 0;
  display: flex;
  align-items: center;
  gap: 10rpx;
  font-size: 28rpx;
  font-weight: 700;
  color: var(--wc-text);
}

.record-id {
  font-size: 22rpx;
  color: var(--wc-text-faint);
}

.status-badge {
  flex-shrink: 0;
  padding: 8rpx 14rpx;
  border-radius: 999rpx;
  font-size: 22rpx;
  line-height: 1;
  background: #eef2f7;
  color: #64748b;
}

.status-badge--pending {
  background: rgba(245, 158, 11, 0.14);
  color: #b45309;
}

.status-badge--processing {
  background: var(--wc-primary-soft);
  color: var(--wc-primary-strong);
}

.status-badge--resolved {
  background: rgba(43, 167, 121, 0.14);
  color: var(--wc-success);
}

.record-content {
  display: block;
  font-size: 26rpx;
  line-height: 1.6;
  color: var(--wc-text);
}

.record-images {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12rpx;
  margin-top: 16rpx;
}

.record-image {
  width: 100%;
  aspect-ratio: 1;
}

.reply-box {
  margin-top: 18rpx;
  padding: 18rpx;
  border-radius: 20rpx;
  background: var(--wc-primary-soft);
}

.reply-label,
.reply-content,
.reply-time {
  display: block;
}

.reply-label {
  font-size: 22rpx;
  font-weight: 700;
  color: var(--wc-primary-strong);
  margin-bottom: 8rpx;
}

.reply-content {
  font-size: 25rpx;
  color: var(--wc-text);
  line-height: 1.55;
}

.reply-time {
  margin-top: 8rpx;
  font-size: 22rpx;
  color: var(--wc-text-soft);
}

.record-footer {
  margin-top: 18rpx;
  display: flex;
  justify-content: space-between;
  gap: 16rpx;
  font-size: 22rpx;
  color: var(--wc-text-faint);
}

.page-bottom-space {
  height: calc(56rpx + env(safe-area-inset-bottom));
}
</style>
