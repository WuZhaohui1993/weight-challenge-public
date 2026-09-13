<template>
  <view class="login-container">
    <view class="ambient-blobs">
      <view class="blob blob-1"></view>
      <view class="blob blob-2"></view>
      <view class="blob blob-3"></view>
    </view>

    <!-- 主视觉区域：品牌头像与核心能力 -->
    <view class="hero-section">
      <view class="glass-widget">
        <image class="brand-avatar" src="/static/brand/xingwan-tongxing-logo.png" mode="aspectFit" />
        <view class="widget-info">
          <text class="widget-title">今日健康打卡</text>
          <view class="widget-stats">
            <view class="stat-item">
              <view class="stat-dot dot-blue"></view>
              <text class="stat-label">称重</text>
            </view>
            <view class="stat-item">
              <view class="stat-dot dot-green"></view>
              <text class="stat-label">饮食</text>
            </view>
            <view class="stat-item">
              <view class="stat-dot dot-orange"></view>
              <text class="stat-label">打卡</text>
            </view>
          </view>
        </view>
      </view>

      <view class="title-area">
        <text class="app-title">星绾同行</text>
        <text class="app-subtitle">记录体重变化，和伙伴一起完成健康挑战</text>
      </view>
    </view>

    <!-- 登录区域 -->
    <view class="login-panel">
      <!-- 微信按钮 -->
      <button class="btn-wechat" :loading="loading" @tap="handleWechatLogin">
        <text class="wechat-icon">💬</text>
        <text>微信一键登录</text>
      </button>

      <view
        class="btn-browse wc-pressable"
        hover-class="btn-browse--active"
        @tap.stop="continueBrowsing"
        @click.stop="continueBrowsing"
      >
        <text>暂不登录，继续浏览</text>
      </view>

      <!-- 协议 -->
      <view class="agreement" @tap="agreed = !agreed">
        <view class="checkbox" :class="{ checked: agreed }">
          <text v-if="agreed" class="check-icon">✓</text>
        </view>
        <text class="agreement-text">
          我已阅读并同意 <text class="link" @tap.stop="goPrivacy">《隐私政策》</text> 与 <text class="link" @tap.stop="goTerms">《用户协议》</text>
        </text>
      </view>
    </view>

    <view v-if="canEditBaseUrl" class="footer-note">
      <view class="footer-env">
        <text>当前环境: {{ currentBaseUrl }}</text>
        <text class="footer-link" @tap="toggleBaseUrlEditor">
          {{ showBaseUrlEditor ? '收起接口设置' : '切换接口' }}
        </text>
      </view>
      <text v-if="baseUrlWarning" class="footer-warning">{{ baseUrlWarning }}</text>
      <view v-if="showBaseUrlEditor" class="base-url-panel">
        <text class="base-url-help">本地多手机联调可填 `http://开发机局域网IP:8080`，正式环境再切到 HTTPS 域名。</text>
        <input
          :value="baseUrlDraft"
          class="base-url-input"
          placeholder="请输入局域网 IP 或 HTTPS 域名"
          @input="onBaseUrlInput"
        />
        <view class="base-url-actions">
          <view class="base-url-action base-url-action--secondary" @tap="resetApiBaseUrl">恢复默认</view>
          <view class="base-url-action base-url-action--primary" @tap="saveBaseUrl">保存接口</view>
        </view>
      </view>
    </view>

  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { onLoad, onShow } from '@dcloudio/uni-app'
import { useUserStore } from '@/stores/user'
import { getBaseUrl, getBaseUrlWarning, resetBaseUrl, setBaseUrl } from '@/utils/request'
import { syncUploadConfig } from '@/utils/app-config'
import { runAfterMiniProgramRender } from '@/utils/mobile'
import { queueProfileReminderIntent, shouldPromptProfileSetup } from '@/utils/profileSetup'
import { useDefaultPageShare } from '@/utils/share'
import { resetUploadConfig } from '@/utils/upload'

useDefaultPageShare()

const userStore = useUserStore()
const canEditBaseUrl = import.meta.env.DEV && import.meta.env.VITE_SHOW_API_BASE_URL_EDITOR === 'true'
const loading = ref(false)
const agreed = ref(false)
const currentBaseUrl = ref(getBaseUrl())
const baseUrlDraft = ref(currentBaseUrl.value)
const showBaseUrlEditor = ref(false)
const baseUrlWarning = computed(() => getBaseUrlWarning(currentBaseUrl.value))
const redirectUrl = ref('')
let loginNavigationSequence = 0
let continueBrowsingSequence = 0

const LOGIN_PAGE_ROUTE = 'pages/login/index'
const HOME_TAB_URL = '/pages/index/index'
const TAB_BAR_ROUTES = new Set([
  'pages/index/index',
  'pages/circle/index',
  'pages/analytics/index',
  'pages/profile/index'
])

onLoad((query) => {
  redirectUrl.value = typeof query?.redirect === 'string' ? decodeURIComponent(query.redirect) : ''
})

onShow(() => {
  if (userStore.isLoggedIn) {
    navigateAfterLogin()
  }
})

async function handleWechatLogin() {
  if (!agreed.value) {
    uni.showToast({ title: '请先阅读并同意用户协议', icon: 'none' })
    return
  }

  loading.value = true

  try {
    const loginPayload = await userStore.wxLogin()
    if (shouldPromptProfileSetup(loginPayload.user, { isNewUser: loginPayload.isNewUser })) {
      queueProfileReminderIntent({ isNewUser: loginPayload.isNewUser })
    }

    uni.showToast({ title: '登录成功', icon: 'success' })
    setTimeout(() => {
      navigateAfterLogin()
    }, 800)
  } catch (error: any) {
    uni.showToast({
      title: error.message || '登录失败',
      icon: 'none'
    })
  } finally {
    loading.value = false
  }
}

function navigateAfterLogin() {
  const sequence = ++loginNavigationSequence

  runAfterMiniProgramRender(() => {
    if (sequence !== loginNavigationSequence) {
      return
    }

    const pages = getCurrentPages()
    const currentPage = pages[pages.length - 1] as { route?: string } | undefined

    if (currentPage?.route && currentPage.route !== 'pages/login/index') {
      return
    }

    if (redirectUrl.value) {
      uni.reLaunch({
        url: redirectUrl.value,
        fail: () => {
          uni.switchTab({ url: '/pages/index/index' })
        }
      })
      return
    }

    uni.switchTab({ url: '/pages/index/index' })
  })
}

function normalizeRoute(value?: string) {
  return String(value || '').replace(/^\/+/, '').split('?')[0]
}

function routeToUrl(route: string) {
  const normalizedRoute = normalizeRoute(route)
  return normalizedRoute ? `/${normalizedRoute}` : HOME_TAB_URL
}

function getGuestContinueFallbackUrl() {
  const redirectRoute = normalizeRoute(redirectUrl.value)
  if (redirectRoute && redirectRoute !== LOGIN_PAGE_ROUTE && TAB_BAR_ROUTES.has(redirectRoute)) {
    return routeToUrl(redirectRoute)
  }
  return HOME_TAB_URL
}

function navigateGuestContinueFallback(url = HOME_TAB_URL) {
  const route = normalizeRoute(url)
  if (TAB_BAR_ROUTES.has(route)) {
    uni.switchTab({
      url: routeToUrl(route),
      fail: () => {
        uni.reLaunch({ url: HOME_TAB_URL })
      }
    })
    return
  }

  uni.reLaunch({ url: HOME_TAB_URL })
}

function continueBrowsing() {
  const sequence = ++continueBrowsingSequence

  runAfterMiniProgramRender(() => {
    if (sequence !== continueBrowsingSequence) {
      return
    }

    const fallbackUrl = getGuestContinueFallbackUrl()
    const pages = getCurrentPages()
    if (pages.length > 1) {
      uni.navigateBack({
        fail: () => navigateGuestContinueFallback(fallbackUrl)
      })
      return
    }

    navigateGuestContinueFallback(fallbackUrl)
  })
}

function goPrivacy() {
  uni.navigateTo({ url: '/pages/legal/privacy/index' })
}

function goTerms() {
  uni.navigateTo({ url: '/pages/legal/terms/index' })
}

function toggleBaseUrlEditor() {
  showBaseUrlEditor.value = !showBaseUrlEditor.value
  baseUrlDraft.value = currentBaseUrl.value
}

function onBaseUrlInput(event: any) {
  baseUrlDraft.value = String(event?.detail?.value || '')
}

function saveBaseUrl() {
  const nextBaseUrl = String(baseUrlDraft.value || '').trim()
  if (!/^https?:\/\//i.test(nextBaseUrl)) {
    uni.showToast({ title: '请输入 http:// 或 https:// 开头的地址', icon: 'none' })
    return
  }

  setBaseUrl(nextBaseUrl)
  resetUploadConfig()
  currentBaseUrl.value = getBaseUrl()
  baseUrlDraft.value = currentBaseUrl.value
  showBaseUrlEditor.value = false
  void syncUploadConfig(true)
  uni.showToast({ title: '接口地址已更新', icon: 'success' })
}

function resetApiBaseUrl() {
  resetBaseUrl()
  resetUploadConfig()
  currentBaseUrl.value = getBaseUrl()
  baseUrlDraft.value = currentBaseUrl.value
  showBaseUrlEditor.value = false
  void syncUploadConfig(true)
  uni.showToast({ title: '已恢复默认接口', icon: 'none' })
}
</script>

<style scoped lang="scss">
.login-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  padding: calc(140rpx + env(safe-area-inset-top)) 60rpx calc(60rpx + env(safe-area-inset-bottom));
  background: linear-gradient(135deg, #f0f4f8 0%, #eef2f6 100%);
  position: relative;
  overflow: hidden;
  box-sizing: border-box;
}

/* 丰富的背景环境光晕 */
.ambient-blobs {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 0;
  pointer-events: none;
}

.blob {
  position: absolute;
  filter: blur(80rpx);
  border-radius: 50%;
  animation: float 8s ease-in-out infinite alternate;
}

.blob-1 {
  width: 600rpx;
  height: 600rpx;
  background: rgba(59, 130, 246, 0.12);
  top: -150rpx;
  right: -150rpx;
}

.blob-2 {
  width: 500rpx;
  height: 500rpx;
  background: rgba(16, 185, 129, 0.1);
  bottom: 100rpx;
  left: -150rpx;
  animation-delay: -3s;
}

.blob-3 {
  width: 300rpx;
  height: 300rpx;
  background: rgba(139, 92, 246, 0.08); /* 轻微的紫色渐变增加层次 */
  top: 40%;
  left: 50%;
  transform: translate(-50%, -50%);
  animation-delay: -5s;
}

@keyframes float {
  0% { transform: translateY(0) scale(1); }
  100% { transform: translateY(40rpx) scale(1.05); }
}

/* 主视觉区 */
.hero-section {
  position: relative;
  z-index: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-top: 140rpx; /* 增加上边距，使其整体下沉，填补中间空白 */
}

/* 玻璃态微件，增加层次感和丰富度 */
.glass-widget {
  width: 100%;
  max-width: 540rpx;
  background: rgba(255, 255, 255, 0.6);
  backdrop-filter: blur(20px);
  border-radius: 40rpx;
  padding: 40rpx;
  box-shadow: 0 40rpx 80rpx rgba(0, 0, 0, 0.04), inset 0 2rpx 0 rgba(255, 255, 255, 0.8);
  border: 2rpx solid rgba(255, 255, 255, 0.6);
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 60rpx;
  animation: floatWidget 4s ease-in-out infinite alternate;
}

@keyframes floatWidget {
  0% { transform: translateY(0); }
  100% { transform: translateY(-16rpx); }
}

.brand-avatar {
  width: 176rpx;
  height: 176rpx;
  margin-bottom: 30rpx;
  border-radius: 42rpx;
  box-shadow: 0 18rpx 36rpx rgba(47, 140, 235, 0.18);
}

.widget-info {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 100%;
}

.widget-title {
  font-size: 32rpx;
  font-weight: 800;
  color: #1e293b;
  margin-bottom: 24rpx;
}

.widget-stats {
  display: flex;
  width: 100%;
  justify-content: space-between;
  padding: 0 20rpx;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8rpx;
}

.stat-dot {
  width: 20rpx;
  height: 20rpx;
  border-radius: 50%;
}
.dot-blue { background: #3b82f6; box-shadow: 0 4rpx 10rpx rgba(59,130,246,0.3); }
.dot-green { background: #10b981; box-shadow: 0 4rpx 10rpx rgba(16,185,129,0.3); }
.dot-orange { background: #f59e0b; box-shadow: 0 4rpx 10rpx rgba(245,158,11,0.3); }

.stat-label {
  font-size: 22rpx;
  color: #64748b;
  font-weight: 600;
}

/* 标题区 */
.title-area {
  text-align: center;
}

.app-title {
  display: block;
  font-size: 60rpx;
  font-weight: 900;
  color: #0f172a;
  margin-bottom: 20rpx;
  letter-spacing: 0;
}

.app-subtitle {
  display: block;
  font-size: 28rpx;
  color: #64748b;
  line-height: 1.5;
  padding: 0 40rpx;
}

/* 登录面板 */
.login-panel {
  position: relative;
  z-index: 1;
  width: 100%;
  margin-top: auto;
  margin-bottom: 20rpx;
}

.btn-wechat {
  width: 100%;
  height: 104rpx;
  background: #07c160;
  color: #ffffff;
  font-size: 34rpx;
  font-weight: 600;
  border-radius: 52rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16rpx;
  border: none;
  box-shadow: 0 16rpx 32rpx rgba(7, 193, 96, 0.25);
  transition: all 0.2s;
}

.btn-wechat:active {
  transform: scale(0.96);
  box-shadow: 0 8rpx 16rpx rgba(7, 193, 96, 0.15);
}

.btn-browse {
  width: 100%;
  height: 92rpx;
  margin-top: 22rpx;
  background: rgba(255, 255, 255, 0.74);
  color: #475569;
  font-size: 30rpx;
  font-weight: 600;
  border-radius: 46rpx;
  border: 2rpx solid rgba(148, 163, 184, 0.32);
  display: flex;
  align-items: center;
  justify-content: center;
}

.btn-browse:active {
  transform: scale(0.98);
  background: rgba(241, 245, 249, 0.92);
}

.wechat-icon {
  font-size: 40rpx;
}

.agreement {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: 32rpx;
  gap: 12rpx;
}

.checkbox {
  width: 32rpx;
  height: 32rpx;
  border-radius: 8rpx;
  border: 4rpx solid #cbd5e1;
  background: transparent;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
  box-sizing: border-box;
}

.checkbox.checked {
  background: #07c160;
  border-color: #07c160;
}

.check-icon {
  color: #fff;
  font-size: 24rpx;
  font-weight: bold;
}

.agreement-text {
  font-size: 24rpx;
  color: #64748b;
}

.link {
  color: #3b82f6;
  font-weight: 500;
}

.footer-note {
  position: relative;
  z-index: 1;
  text-align: center;
  font-size: 22rpx;
  color: #94a3b8;
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.footer-env {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16rpx;
  flex-wrap: wrap;
}

.footer-link {
  color: #3b82f6;
  font-weight: 600;
}

.footer-warning {
  display: block;
  color: #f97316;
  line-height: 1.5;
}

.base-url-panel {
  margin: 0 auto;
  width: 100%;
  max-width: 620rpx;
  padding: 24rpx;
  border-radius: 28rpx;
  background: rgba(255, 255, 255, 0.72);
  border: 2rpx solid rgba(148, 163, 184, 0.12);
  box-shadow: 0 20rpx 40rpx rgba(15, 23, 42, 0.06);
}

.base-url-help {
  display: block;
  margin-bottom: 16rpx;
  color: rgba(56, 68, 89, 0.78);
  font-size: 24rpx;
  line-height: 1.6;
}

.base-url-input {
  width: 100%;
  min-height: 84rpx;
  padding: 0 24rpx;
  border-radius: 20rpx;
  background: rgba(255, 255, 255, 0.96);
  color: #0f172a;
  text-align: left;
}

.base-url-actions {
  margin-top: 20rpx;
  display: flex;
  gap: 16rpx;
}

.base-url-action {
  flex: 1;
  min-height: 76rpx;
  border-radius: 999rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 26rpx;
  font-weight: 600;
}

.base-url-action--secondary {
  background: rgba(255, 255, 255, 0.92);
  color: #475569;
}

.base-url-action--primary {
  background: #3b82f6;
  color: #ffffff;
}

</style>
