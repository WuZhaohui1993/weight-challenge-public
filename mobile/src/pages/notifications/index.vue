<template>
  <view class="page-container wc-page-enter">
    <prototype-header
      title="消息通知"
      back-url="/pages/index/index"
      :right-text="headerActionText"
      @rightTap="handleMarkAllRead"
    />

    <scroll-view class="content wc-section-enter" scroll-y>
      <view class="tab-bar wc-tab-rail">
        <view
          v-for="tab in tabs"
          :key="tab.key"
          class="tab-item wc-tab-item wc-tab-item--full wc-pressable"
          :class="{ active: currentTab === tab.key }"
          @tap="selectTab(tab.key)"
        >
          {{ tab.label }}
        </view>
      </view>

      <view v-if="unreadCountLoadError" class="notification-soft-tip">
        <view class="notification-soft-tip__copy">
          <text class="notification-soft-tip__title">未读提醒暂未同步</text>
          <text class="notification-soft-tip__desc">{{ unreadCountLoadError }}</text>
        </view>
        <button class="flow-btn flow-btn--secondary flow-btn--compact" @tap="loadUnreadCount">重新整理未读数</button>
      </view>

      <app-section-header compact title="通知列表" :subtitle="sectionSubtitle" />

      <view class="notification-list">
        <view v-if="loading" class="notification-panel notification-panel--loading">
          通知加载中...
        </view>

        <view v-else-if="loadError" class="notification-panel">
          <app-empty-state
            icon="⚠️"
            title="通知加载失败"
            :description="loadError"
          >
            <button class="flow-btn flow-btn--secondary flow-btn--compact" @tap="reloadPage">重新整理</button>
          </app-empty-state>
        </view>

        <view v-else-if="notifications.length" class="notification-stack">
          <view
            v-for="notification in notifications"
            :key="notification.id"
            class="notification-card wc-pressable"
            :class="{ unread: !notification.read }"
            @tap="openNotification(notification)"
          >
            <resolved-image
              class="notification-avatar"
              :src="notification.fromUserAvatar"
              mode="aspectFill"
              shape="avatar"
              fallback-class="notification-avatar notification-avatar--fallback"
              :fallback-text="notificationAvatarText(notification)"
              @tap.stop="goUserProfile(notification.fromUserId)"
            />

            <view class="notification-body">
              <view class="notification-header">
                <text class="notification-title">{{ notification.content || fallbackTitle(notification.type) }}</text>
                <text class="notification-time">{{ circleFeedTimeText(notification.createdAt) }}</text>
              </view>

              <view class="notification-meta">
                <text class="notification-tag">{{ notificationTypeLabel(notification.type) }}</text>
                <text v-if="!notification.read" class="notification-dot">未读</text>
              </view>

              <text v-if="notification.preview" class="notification-preview">{{ notification.preview }}</text>
            </view>
          </view>
        </view>

        <view v-else class="notification-panel">
          <app-empty-state
            icon="🔔"
            :title="emptyStateTitle"
            :description="emptyStateDescription"
          />
        </view>
      </view>

      <view class="page-bottom-space"></view>
    </scroll-view>

    <view v-if="pageNotice.visible" class="page-notice" :class="`page-notice--${pageNotice.type}`">
      <text class="page-notice__title">{{ pageNotice.title }}</text>
      <text class="page-notice__desc">{{ pageNotice.desc }}</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import AppEmptyState from '@/components/AppEmptyState/index.vue'
import AppSectionHeader from '@/components/AppSectionHeader/index.vue'
import PrototypeHeader from '@/components/PrototypeHeader/index.vue'
import ResolvedImage from '@/components/ResolvedImage/index.vue'
import {
  getNotifications,
  getUnreadNotificationCount,
  markAllNotificationsRead,
  markNotificationRead
} from '@/api/notification'
import { useUserStore } from '@/stores/user'
import type { NotificationFilterType, NotificationItem } from '@/types/api'
import { circleFeedTimeText } from '@/utils/circle'
import { openLoginPage } from '@/utils/request'
import { useDefaultPageShare } from '@/utils/share'

useDefaultPageShare()

const tabs: Array<{ key: NotificationFilterType; label: string }> = [
  { key: 'all', label: '全部' },
  { key: 'like', label: '点赞' },
  { key: 'comment', label: '评论' },
  { key: 'system', label: '系统' }
]

const userStore = useUserStore()
const currentTab = ref<NotificationFilterType>('all')
const notifications = ref<NotificationItem[]>([])
const unreadCount = ref(0)
const loading = ref(false)
const loadError = ref('')
const markingAll = ref(false)
const unreadCountLoadError = ref('')
const pageNotice = ref({
  visible: false,
  type: 'success' as 'success' | 'error',
  title: '',
  desc: ''
})
let pageNoticeTimer: ReturnType<typeof setTimeout> | null = null

const headerActionText = computed(() => (unreadCount.value > 0 ? '全部已读' : ''))
const sectionSubtitle = computed(() =>
  unreadCountLoadError.value
    ? '通知内容可继续浏览，未读计数稍后再同步。'
    : unreadCount.value > 0
    ? `还有 ${unreadCount.value} 条未读提醒`
    : '按类型查看最近互动，点击通知可直接进入对应内容。'
)
const emptyStateTitle = computed(() => {
  if (currentTab.value === 'like') {
    return '还没有点赞提醒'
  }
  if (currentTab.value === 'comment') {
    return '还没有评论提醒'
  }
  if (currentTab.value === 'system') {
    return '还没有系统消息'
  }
  return '通知中心暂时空闲'
})
const emptyStateDescription = computed(() => {
  if (currentTab.value === 'all') {
    return '点赞、评论和圈子状态变更后，会先回流到这里。'
  }
  return '切换到其他类型，或先去圈子页产生一条互动记录。'
})

onShow(() => {
  if (!ensureLoggedIn()) {
    return
  }
  void reloadPage()
})

watch(currentTab, () => {
  void loadNotifications()
})

async function reloadPage() {
  if (!ensureLoggedIn()) {
    return
  }
  await Promise.all([loadNotifications(), loadUnreadCount()])
}

async function loadNotifications() {
  if (!ensureLoggedIn()) {
    return
  }
  loading.value = true
  loadError.value = ''
  try {
    const page = await getNotifications({
      type: currentTab.value,
      pageNum: 1,
      pageSize: 50
    })
    notifications.value = page.list || []
  } catch (error) {
    console.error('加载通知列表失败', error)
    notifications.value = []
    loadError.value = '请检查后端服务是否可用，然后再试一次。'
  } finally {
    loading.value = false
  }
}

async function loadUnreadCount() {
  if (!ensureLoggedIn()) {
    return
  }
  unreadCountLoadError.value = ''
  try {
    unreadCount.value = Number(await getUnreadNotificationCount()) || 0
  } catch (error) {
    console.error('加载未读通知数失败', error)
    unreadCount.value = 0
    unreadCountLoadError.value = error instanceof Error ? error.message || '未读数量暂时不可用' : '未读数量暂时不可用'
  }
}

function selectTab(tab: NotificationFilterType) {
  if (currentTab.value === tab) {
    return
  }
  currentTab.value = tab
}

async function handleMarkAllRead() {
  if (!ensureLoggedIn()) {
    return
  }
  if (!unreadCount.value || markingAll.value) {
    return
  }

  markingAll.value = true
  try {
    await markAllNotificationsRead()
    notifications.value = notifications.value.map((item) => ({ ...item, read: true }))
    unreadCount.value = 0
    showPageNotice('已全部标记为已读', '消息列表已同步更新。', 'success')
  } catch (error) {
    console.error('全部标记已读失败', error)
    showPageNotice('全部已读失败', '请稍后重新尝试。', 'error')
  } finally {
    markingAll.value = false
  }
}

function showPageNotice(title: string, desc: string, type: 'success' | 'error') {
  if (pageNoticeTimer) {
    clearTimeout(pageNoticeTimer)
    pageNoticeTimer = null
  }
  pageNotice.value = {
    visible: true,
    type,
    title,
    desc
  }
  pageNoticeTimer = setTimeout(() => {
    pageNotice.value = {
      ...pageNotice.value,
      visible: false
    }
    pageNoticeTimer = null
  }, 2200)
}

async function openNotification(notification: NotificationItem) {
  if (!ensureLoggedIn()) {
    return
  }
  if (!notification.read) {
    try {
      const updated = await markNotificationRead(notification.id)
      notifications.value = notifications.value.map((item) => (item.id === updated.id ? updated : item))
      unreadCount.value = Math.max(0, unreadCount.value - 1)
    } catch (error) {
      console.error('标记通知已读失败', error)
      uni.showToast({ title: '标记已读失败', icon: 'none' })
      return
    }
  }

  if (notification.feedId) {
    const commentQuery = notification.commentId ? `&commentId=${notification.commentId}` : ''
    uni.navigateTo({ url: `/pages/feed-detail/index?feedId=${notification.feedId}${commentQuery}` })
    return
  }

  if (notification.targetType === 'circle' && notification.targetId) {
    uni.navigateTo({ url: `/pages/circle-private/index?circleId=${notification.targetId}` })
    return
  }

  if (notification.targetType === 'feedback') {
    uni.navigateTo({ url: '/pages/feedback/index?tab=records' })
    return
  }

  uni.showToast({ title: '当前通知暂无详情页', icon: 'none' })
}

function ensureLoggedIn() {
  if (userStore.isLoggedIn) {
    return true
  }

  notifications.value = []
  unreadCount.value = 0
  loadError.value = ''
  unreadCountLoadError.value = ''
  loading.value = false
  markingAll.value = false
  openLoginPage('safe', '/pages/notifications/index')
  return false
}

function goUserProfile(userId?: number | null) {
  if (!userId) {
    return
  }
  uni.navigateTo({ url: `/pages/user-profile/index?userId=${userId}` })
}

function notificationTypeLabel(type?: string | null) {
  if (type === 'like') {
    return '点赞'
  }
  if (type === 'comment') {
    return '评论'
  }
  if (type === 'system') {
    return '系统'
  }
  return '互动'
}

function fallbackTitle(type?: string | null) {
  if (type === 'like') {
    return '有人赞了你的动态'
  }
  if (type === 'comment') {
    return '有人评论了你的动态'
  }
  return '你收到一条新提醒'
}

function notificationAvatarText(notification: NotificationItem) {
  if (notification.type === 'system') {
    return '系'
  }
  return (notification.fromUserNickname || 'N').slice(0, 1)
}
</script>

<style scoped lang="scss">
@use '../../styles/flow-button.scss';

.page-container {
  height: 100vh;
  background: #f7f8fa;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.content {
  flex: 1;
  min-height: 0;
}

.tab-bar {
  margin: 20rpx 30rpx 0;
}

.notification-soft-tip {
  margin: 18rpx 30rpx 0;
  padding: 18rpx 22rpx;
  border-radius: 24rpx;
  background: rgba(255, 244, 224, 0.92);
  border: 1px solid rgba(230, 149, 75, 0.18);
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20rpx;
}

.notification-soft-tip__copy {
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 4rpx;
}

.notification-soft-tip__title {
  font-size: 24rpx;
  font-weight: 700;
  color: #8f4d18;
}

.notification-soft-tip__desc {
  font-size: 21rpx;
  color: #ad6a33;
  line-height: 1.5;
}

.notification-soft-tip .flow-btn {
  flex-shrink: 0;
}

.notification-list {
  padding: 0 30rpx;
}

.notification-panel {
  background: var(--wc-surface-strong);
  border: 1px solid var(--wc-line);
  box-shadow: var(--wc-shadow-soft);
  border-radius: 32rpx;
  padding: 28rpx;
}

.notification-panel--loading {
  text-align: center;
  color: var(--wc-text-soft);
  font-size: 26rpx;
}

.notification-stack {
  display: flex;
  flex-direction: column;
  gap: 18rpx;
}

.notification-card {
  display: flex;
  gap: 22rpx;
  background: var(--wc-surface-strong);
  border: 1px solid rgba(28, 37, 54, 0.08);
  box-shadow: var(--wc-shadow-soft);
  border-radius: 28rpx;
  padding: 26rpx 24rpx;
}

.notification-card.unread {
  border-color: rgba(77, 138, 255, 0.22);
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.98), rgba(244, 248, 255, 0.98));
}

.notification-avatar {
  width: 84rpx;
  height: 84rpx;
  border-radius: 50%;
  flex-shrink: 0;
  background: #e6ebf2;
}

.notification-avatar--fallback {
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #dbeafe, #bfdbfe);
  color: #1d4ed8;
  font-size: 28rpx;
  font-weight: 700;
}

.notification-body {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 10rpx;
}

.notification-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16rpx;
}

.notification-title {
  flex: 1;
  min-width: 0;
  font-size: 28rpx;
  font-weight: 700;
  line-height: 1.5;
  color: var(--wc-text);
}

.notification-time {
  flex-shrink: 0;
  font-size: 22rpx;
  color: var(--wc-text-faint);
  line-height: 1.5;
}

.notification-meta {
  display: flex;
  align-items: center;
  gap: 14rpx;
}

.notification-tag,
.notification-dot {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  height: 40rpx;
  padding: 0 14rpx;
  border-radius: 999rpx;
  font-size: 20rpx;
  line-height: 1;
}

.notification-tag {
  background: rgba(28, 37, 54, 0.06);
  color: var(--wc-text-soft);
}

.notification-dot {
  background: rgba(77, 138, 255, 0.12);
  color: #4d8aff;
}

.notification-preview {
  font-size: 24rpx;
  line-height: 1.7;
  color: var(--wc-text-soft);
  background: rgba(28, 37, 54, 0.04);
  border-radius: 20rpx;
  padding: 16rpx 18rpx;
}

.page-notice {
  position: fixed;
  left: 50%;
  bottom: calc(72rpx + env(safe-area-inset-bottom));
  transform: translateX(-50%);
  width: 560rpx;
  max-width: calc(100vw - 80rpx);
  padding: 22rpx 28rpx;
  border-radius: 24rpx;
  background: rgba(28, 37, 54, 0.94);
  box-shadow: 0 18rpx 44rpx rgba(16, 24, 40, 0.2);
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6rpx;
  z-index: 1200;
  box-sizing: border-box;
}

.page-notice--success {
  background: rgba(28, 37, 54, 0.94);
}

.page-notice--error {
  background: rgba(178, 40, 40, 0.94);
}

.page-notice__title,
.page-notice__desc {
  display: block;
  width: 100%;
  color: #fff;
  text-align: center;
  white-space: normal;
  word-break: break-word;
}

.page-notice__title {
  font-size: 26rpx;
  line-height: 1.35;
  font-weight: 700;
}

.page-notice__desc {
  font-size: 22rpx;
  line-height: 1.45;
  color: rgba(255, 255, 255, 0.82);
}

.page-bottom-space {
  height: 60rpx;
}
</style>
