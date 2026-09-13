<script setup lang="ts">
import { onLaunch, onShow, onHide } from '@dcloudio/uni-app'
import { syncUploadConfig } from '@/utils/app-config'
import { runAfterMiniProgramRender } from '@/utils/mobile'
import { ensureStoredAuthSessionActive } from '@/utils/request'

const LOGIN_PAGE_ROUTE = 'pages/login/index'
const LOGIN_PAGE_URL = `/${LOGIN_PAGE_ROUTE}`
const HOME_TAB_URL = '/pages/index/index'
const PUBLIC_ROUTES = new Set([
  'pages/index/index',
  'pages/circle/index',
  'pages/analytics/index',
  'pages/circle-all/index',
  'pages/circle-private/index',
  'pages/feed-detail/index',
  'pages/user-profile/index',
  LOGIN_PAGE_ROUTE,
  'pages/legal/privacy/index',
  'pages/legal/terms/index'
])

type AuthNavigationType = 'switchTab' | 'reLaunch'

let authNavigationKey = ''
let authNavigationSequence = 0

function normalizeRoute(value?: string) {
  return String(value || '').replace(/^\/+/, '').split('?')[0]
}

function buildQueryString(query?: Record<string, unknown>) {
  if (!query) {
    return ''
  }

  return Object.entries(query)
    .filter(([, value]) => value !== undefined && value !== null && String(value) !== '')
    .map(([key, value]) => `${encodeURIComponent(key)}=${encodeURIComponent(String(value))}`)
    .join('&')
}

function buildRouteUrl(path?: string, query?: Record<string, unknown>) {
  const [pathPart, ...queryParts] = String(path || '').replace(/^\/+/, '').split('?')
  const route = normalizeRoute(pathPart)
  if (!route) {
    return ''
  }

  const existingQueryString = queryParts.join('?')
  const queryString = buildQueryString(query)
  const combinedQueryString = [existingQueryString, queryString].filter(Boolean).join('&')
  return `/${route}${combinedQueryString ? `?${combinedQueryString}` : ''}`
}

function buildLoginPageUrl(redirectUrl?: string) {
  if (!redirectUrl) {
    return LOGIN_PAGE_URL
  }

  return `${LOGIN_PAGE_URL}?redirect=${encodeURIComponent(redirectUrl)}`
}

function getCurrentRoute() {
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1] as { route?: string } | undefined
  return normalizeRoute(currentPage?.route)
}

function scheduleAuthNavigation(type: AuthNavigationType, url: string, routeHint?: string) {
  const expectedRoute = normalizeRoute(routeHint)
  const key = `${type}:${url}:${expectedRoute}`

  if (authNavigationKey === key) {
    return
  }

  authNavigationKey = key
  const sequence = ++authNavigationSequence

  runAfterMiniProgramRender(() => {
    if (sequence !== authNavigationSequence) {
      return
    }

    authNavigationKey = ''
    const route = getCurrentRoute() || expectedRoute

    if (expectedRoute && route && route !== expectedRoute) {
      return
    }

    if (type === 'switchTab') {
      uni.switchTab({ url })
      return
    }

    uni.reLaunch({ url })
  })
}

function ensureAuthEntry(routeHint?: string, redirectUrl?: string) {
  const route = getCurrentRoute() || normalizeRoute(routeHint)

  if (ensureStoredAuthSessionActive()) {
    if (route === LOGIN_PAGE_ROUTE) {
      scheduleAuthNavigation('switchTab', HOME_TAB_URL, route)
    }
    return
  }

  if (!route || PUBLIC_ROUTES.has(route)) {
    return
  }

  scheduleAuthNavigation('reLaunch', buildLoginPageUrl(redirectUrl), route)
}

onLaunch((options: any) => {
  console.log('App Launch')
  void syncUploadConfig(true)
  ensureAuthEntry(options?.path, buildRouteUrl(options?.path, options?.query))
})

onShow((options: any) => {
  console.log('App Show')
  void syncUploadConfig()
  ensureAuthEntry(options?.path, buildRouteUrl(options?.path, options?.query))
})

onHide(() => {
  console.log('App Hide')
})
</script>
<style lang="scss">
@use './styles/flow-tertiary.scss';

:root,
page {
  --wc-bg: #f6f9f4;
  --wc-bg-soft: #fbfcf8;
  --wc-surface: rgba(255, 255, 255, 0.9);
  --wc-surface-strong: rgba(255, 255, 255, 0.94);
  --wc-surface-muted: #f1f8f1;
  --wc-line: rgba(39, 92, 72, 0.06);
  --wc-line-strong: rgba(39, 92, 72, 0.1);
  --wc-text: #17231f;
  --wc-text-soft: #61716a;
  --wc-text-faint: #9aa79f;
  --wc-primary: #2fb37b;
  --wc-primary-soft: #e8f8f1;
  --wc-primary-strong: #168a62;
  --wc-success: #2fb37b;
  --wc-warning: #f4a338;
  --wc-danger: #e66f7b;
  --wc-radius-s: 18rpx;
  --wc-radius-m: 24rpx;
  --wc-radius-l: 32rpx;
  --wc-radius-xl: 40rpx;
  --wc-form-height: 80rpx;
  --wc-form-height-medium: 84rpx;
  --wc-form-height-large: 92rpx;
  --wc-form-radius: 24rpx;
  --wc-form-padding-x: 24rpx;
  --wc-btn-height: 88rpx;
  --wc-btn-height-compact: 76rpx;
  --wc-btn-radius: 999rpx;
  --wc-icon-btn-size: 80rpx;
  --wc-pill-height: 64rpx;
  --wc-back-size: 72rpx;
  --wc-back-radius: 22rpx;
  --wc-shadow-soft: 0 4rpx 12rpx rgba(39, 92, 72, 0.025);
  --wc-shadow-panel: 0 8rpx 22rpx rgba(39, 92, 72, 0.04);
  --wc-shadow-float: 0 14rpx 32rpx rgba(47, 179, 123, 0.18);
  --wc-tab-first-gap: 20rpx;
  --wc-tab-header-bg: rgba(248, 251, 246, 0.9);
  --wc-tab-header-blur: 24rpx;
}

page {
  background:
    radial-gradient(circle at 20% 0%, rgba(232, 248, 241, 0.9), transparent 38%),
    linear-gradient(180deg, #fbfcf8 0%, #f4f8f3 100%);
  color: var(--wc-text);
}

view,
text,
scroll-view,
image,
button,
input,
textarea {
  box-sizing: border-box;
}

button::after {
  border: none;
}

@keyframes wc-rise-in {
  from {
    opacity: 0;
  }

  to {
    opacity: 1;
  }
}

@keyframes wc-section-rise-in {
  from {
    opacity: 0;
    transform: translateY(20rpx);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.wc-page-enter {
  animation: wc-rise-in 0.32s ease both;
}

.wc-section-enter {
  animation: wc-section-rise-in 0.32s ease both;
}

.wc-tab-header {
  background: var(--wc-tab-header-bg);
  box-sizing: border-box;
  flex-shrink: 0;
  backdrop-filter: blur(var(--wc-tab-header-blur));
  border-bottom: 1px solid var(--wc-line);
}

.wc-tab-header__body {
  min-height: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.wc-tab-scroll {
  flex: 1;
  min-height: 0;
  box-sizing: border-box;
}

.wc-tab-scroll--top-gap {
  padding-top: var(--wc-tab-first-gap);
}

.wc-pressable {
  transition: transform 0.18s ease, box-shadow 0.18s ease, background-color 0.18s ease;
}

.wc-pressable:active {
  transform: scale(0.985);
}

.wc-back-chip {
  width: var(--wc-back-size);
  height: var(--wc-back-size);
  border-radius: var(--wc-back-radius);
  display: inline-flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  background: rgba(255, 255, 255, 0.9);
  border: 1px solid var(--wc-line);
  box-shadow: var(--wc-shadow-soft);
}

.wc-back-chip__icon {
  font-size: 38rpx;
  line-height: 1;
  color: var(--wc-text);
}

.wc-footer-action {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  padding: 24rpx 30rpx calc(24rpx + env(safe-area-inset-bottom));
  background: rgba(248, 251, 246, 0.94);
  backdrop-filter: blur(24rpx);
  border-top: 1px solid var(--wc-line);
  box-shadow: 0 -4rpx 20rpx rgba(0, 0, 0, 0.03);
}

.wc-btn-wechat {
  background: #07c160;
  color: #ffffff;
  box-shadow: 0 10rpx 24rpx rgba(7, 193, 96, 0.22);
}

.wc-btn-danger {
  background: transparent;
  color: var(--wc-danger);
  border: 2rpx solid rgba(229, 103, 103, 0.36);
  box-shadow: none;
}

.wc-icon-button {
  width: var(--wc-icon-btn-size);
  height: var(--wc-icon-btn-size);
  border-radius: 26rpx;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  background: rgba(255, 255, 255, 0.92);
  border: 1px solid var(--wc-line);
  box-shadow: var(--wc-shadow-soft);
  color: var(--wc-text);
  font-size: 38rpx;
}

.wc-icon-button--soft {
  background: rgba(255, 255, 255, 0.88);
}

.wc-icon-button--primary {
  background: linear-gradient(135deg, #1d9d70, var(--wc-primary));
  border-color: transparent;
  box-shadow: var(--wc-shadow-float);
  color: #ffffff;
}

.wc-icon-button--s {
  width: 60rpx;
  height: 60rpx;
  border-radius: 20rpx;
  font-size: 30rpx;
}

.wc-icon-button--circle {
  border-radius: 50%;
}

.wc-tab-rail {
  display: flex;
  align-items: center;
  gap: 10rpx;
  padding: 8rpx;
  border-radius: 999rpx;
  background: rgba(255, 255, 255, 0.82);
  border: 1px solid var(--wc-line);
  box-shadow: var(--wc-shadow-soft);
  box-sizing: border-box;
}

.wc-tab-rail--compact {
  gap: 8rpx;
  padding: 6rpx;
}

.wc-tab-item {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 104rpx;
  min-height: 56rpx;
  padding: 0 20rpx;
  border-radius: 999rpx;
  border: 1px solid transparent;
  color: var(--wc-text-soft);
  font-size: 24rpx;
  font-weight: 600;
  line-height: 1.2;
  white-space: nowrap;
  flex-shrink: 0;
}

.wc-tab-item.active {
  background: linear-gradient(135deg, #21a873, var(--wc-primary));
  color: #ffffff;
  box-shadow: 0 8rpx 18rpx rgba(47, 179, 123, 0.22);
}

.wc-tab-item--compact {
  min-width: 88rpx;
  min-height: 50rpx;
  padding: 0 18rpx;
  font-size: 22rpx;
}

.wc-tab-item--full {
  flex: 1;
  min-width: 0;
}

.wc-info-chip {
  min-height: 76rpx;
  border-radius: 999rpx;
  background: rgba(255, 255, 255, 0.66);
  border: 1px solid var(--wc-line);
  display: flex;
  align-items: center;
  box-shadow: none;
}

.wc-tile-action {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.wc-tile-action__icon {
  width: 92rpx;
  height: 92rpx;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 44rpx;
  margin-bottom: 16rpx;
  color: var(--wc-primary-strong);
  border: 1px solid rgba(47, 179, 123, 0.08);
  box-shadow: none;
  background: linear-gradient(180deg, rgba(232, 248, 241, 0.82), rgba(255, 255, 255, 0.9));
}

.wc-tile-action__label {
  font-size: 24rpx;
  color: var(--wc-text);
}

.wc-reaction-chip {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8rpx;
  min-width: 92rpx;
  height: 54rpx;
  padding: 0 18rpx;
  border-radius: 999rpx;
  background: var(--wc-surface-muted);
  color: var(--wc-text-soft);
  font-size: 24rpx;
}

.wc-signal-pill {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 76rpx;
  height: 42rpx;
  padding: 0 14rpx;
  border-radius: 999rpx;
  background: rgba(255, 255, 255, 0.86);
  border: 1px solid var(--wc-line);
  color: var(--wc-text-soft);
  font-size: 20rpx;
  font-weight: 700;
  line-height: 1;
}

.wc-signal-pill--primary {
  background: var(--wc-primary-soft);
  color: var(--wc-primary-strong);
}

.wc-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-height: 42rpx;
  padding: 0 16rpx;
  border-radius: 999rpx;
  font-size: 20rpx;
  font-weight: 700;
  line-height: 1;
}

.wc-badge--soft {
  background: var(--wc-surface-muted);
  color: rgba(31, 41, 55, 0.72);
}

.wc-badge--primary {
  background: var(--wc-primary-soft);
  color: var(--wc-primary-strong);
}

.wc-badge--success {
  background: rgba(43, 167, 121, 0.14);
  color: var(--wc-success);
}

.wc-badge--warning {
  background: rgba(242, 155, 56, 0.16);
  color: var(--wc-warning);
}

.wc-badge--danger {
  background: rgba(229, 103, 103, 0.14);
  color: var(--wc-danger);
}

.wc-badge--glass {
  background: rgba(255, 255, 255, 0.72);
  color: rgba(24, 39, 75, 0.76);
  border: 1px solid rgba(255, 255, 255, 0.5);
}

.wc-badge--dark {
  background: rgba(39, 92, 72, 0.78);
  color: #ffffff;
}

.wc-form-label {
  display: block;
  margin-bottom: 12rpx;
  font-size: 24rpx;
  color: var(--wc-text-soft);
}

.wc-form-row {
  display: flex;
  align-items: center;
  gap: 16rpx;
}

.wc-form-input,
.wc-form-textarea {
  width: 100%;
  color: var(--wc-text);
  background: var(--wc-surface-muted);
  border: 1px solid var(--wc-line);
  border-radius: var(--wc-form-radius);
  box-sizing: border-box;
}

.wc-form-input {
  height: var(--wc-form-height);
  padding: 0 var(--wc-form-padding-x);
  line-height: var(--wc-form-height);
  font-size: 28rpx;
}

.wc-form-input--medium {
  height: var(--wc-form-height-medium);
  line-height: var(--wc-form-height-medium);
  font-size: 32rpx;
  font-weight: 700;
}

.wc-form-input--large {
  height: var(--wc-form-height-large);
  line-height: var(--wc-form-height-large);
  font-size: 40rpx;
  font-weight: 700;
}

.wc-form-input--pill {
  border-radius: 999rpx;
  font-size: 26rpx;
}

.wc-form-textarea {
  min-height: 220rpx;
  padding: 24rpx;
  font-size: 28rpx;
  line-height: 1.6;
}

.wc-form-segment-rail {
  display: flex;
  gap: 10rpx;
  width: 100%;
  padding: 8rpx;
  border-radius: 999rpx;
  background: rgba(255, 255, 255, 0.78);
  border: 1px solid var(--wc-line);
  box-shadow: var(--wc-shadow-soft);
  box-sizing: border-box;
}

.wc-form-segment {
  flex: 1;
  padding: 18rpx 10rpx;
  text-align: center;
  border-radius: 999rpx;
  border: 1px solid transparent;
  background: transparent;
  color: var(--wc-text-soft);
  font-size: 24rpx;
  line-height: 1.2;
}

.wc-form-segment.active {
  color: #ffffff;
  background: linear-gradient(135deg, #21a873, var(--wc-primary));
  font-weight: 600;
  box-shadow: 0 8rpx 18rpx rgba(47, 179, 123, 0.22);
}
</style>
