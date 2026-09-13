<template>
  <view class="page-container wc-page-enter">
    <prototype-header title="圈子管理" back-url="/pages/circle/index" />

    <app-section-header title="我的圈子" subtitle="设置主圈子和退出状态" />
    <view v-if="loading" class="loading-block">圈子加载中...</view>
    <view v-else-if="loadErrorMessage" class="empty-wrap">
      <app-empty-state
        icon="⚠️"
        title="圈子管理加载失败"
        :description="loadErrorMessage"
      >
        <button class="flow-btn flow-btn--secondary flow-btn--compact wc-pressable" @tap="loadCircles">重新整理</button>
      </app-empty-state>
    </view>
    <view v-else-if="circles.length" class="circle-list">
      <view v-for="(circle, index) in circles" :key="circle.id" class="circle-item" :class="circleToneClass(index)">
        <view
          class="circle-cover"
          :class="[circleCoverClass(circle.coverUrl), { 'circle-cover--image': isRemoteCircleCover(circle.coverUrl) }]"
        >
          <resolved-image
            v-if="isRemoteCircleCover(circle.coverUrl)"
            class="circle-cover-image"
            :src="normalizeCircleCoverUrl(circle.coverUrl)"
            mode="aspectFill"
          />
          <view class="circle-cover-top">
            <text class="wc-badge wc-badge--dark">{{ circleTypeLabelText(circle.type) }}</text>
            <text v-if="circle.id === userStore.userInfo?.mainCircleId" class="wc-badge wc-badge--glass">主圈子</text>
            <text v-else-if="circle.readOnly" class="wc-badge wc-badge--glass">{{ circleLifecycleLabel(circle.lifecycleStatus) }}</text>
          </view>
          <view class="circle-avatar">{{ circle.icon || circleTypeIcon(circle.type) }}</view>
          <text class="circle-cover-caption">{{ circle.role === '0' ? '管理员视角' : '成员视角' }}</text>
        </view>
        <view class="circle-content">
          <view class="circle-info" @tap="goCircleDetail(circle.id)">
            <view class="circle-name">
              <text class="circle-name-text">{{ circle.name }}</text>
            </view>
            <text class="circle-desc">{{ circle.description || '一起完成减重目标。' }}</text>
            <text v-if="circle.readOnly" class="circle-readonly">{{ circle.readOnlyReason || '当前仅支持查看历史内容。' }}</text>
            <view class="circle-highlight-row">
              <view class="circle-highlight">
                <text class="circle-highlight-value">{{ circle.memberCount || 0 }}</text>
                <text class="circle-highlight-label">成员</text>
              </view>
              <view class="circle-highlight">
                <text class="circle-highlight-value">{{ circleDurationText(circle.durationDays) }}</text>
                <text class="circle-highlight-label">周期</text>
              </view>
              <view class="circle-highlight">
                <text class="circle-highlight-value">{{ circle.role === '0' ? '管理员' : '成员' }}</text>
                <text class="circle-highlight-label">身份</text>
              </view>
            </view>
            <view class="circle-meta">
              <text class="wc-badge wc-badge--soft">{{ circleDepositText(circle.depositRequired) }}</text>
              <text class="wc-badge wc-badge--soft">{{ joinedAtText(circle.joinedAt) }}</text>
            </view>
          </view>
          <view class="circle-actions">
            <button
              class="action-btn wc-pill-action wc-pill-action--secondary"
              :class="{ active: circle.id === userStore.userInfo?.mainCircleId }"
              :disabled="circle.readOnly"
              @tap.stop="setMainCircle(circle.id)"
            >
              {{ circle.id === userStore.userInfo?.mainCircleId ? '当前主圈子' : '设为主圈子' }}
            </button>
            <button class="btn-quit wc-pill-action wc-pill-action--danger" @tap.stop="quitCircle(circle)">退出</button>
          </view>
        </view>
      </view>
    </view>
    <view v-else class="empty-wrap">
      <app-empty-state
        icon="⭕"
        title="还没有加入圈子"
        description="先去发现页浏览公开圈子，再回来管理主圈子展示。"
      />
    </view>

    <view class="footer-action">
      <button class="flow-btn flow-btn--primary flow-btn--wide" @tap="goCreateCircle">创建圈子</button>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import AppEmptyState from '@/components/AppEmptyState/index.vue'
import AppSectionHeader from '@/components/AppSectionHeader/index.vue'
import PrototypeHeader from '@/components/PrototypeHeader/index.vue'
import { getMyCircles, leaveCircle } from '@/api/circle'
import type { MyCircleCard } from '@/types/api'
import {
  circleCoverClass,
  circleLifecycleLabel,
  circleTypeIcon,
  isRemoteCircleCover,
  normalizeCircleCoverUrl
} from '@/utils/circle'
import { useUserStore } from '@/stores/user'
import { useDefaultPageShare } from '@/utils/share'

useDefaultPageShare()

const userStore = useUserStore()
const circles = ref<MyCircleCard[]>([])
const loading = ref(false)
const loadErrorMessage = ref('')

onShow(() => {
  void loadCircles()
})

function showActionError(error: unknown, fallback: string) {
  const title = error instanceof Error && error.message ? error.message : fallback
  uni.showToast({
    title: title.length > 12 ? fallback : title,
    icon: 'none'
  })
}

async function loadCircles() {
  loading.value = true
  loadErrorMessage.value = ''
  try {
    const page = await getMyCircles({ pageNum: 1, pageSize: 100 })
    circles.value = page.list || []
  } catch (error) {
    console.error('加载圈子管理列表失败', error)
    circles.value = []
    loadErrorMessage.value = error instanceof Error ? error.message || '圈子管理接口请求失败' : '圈子管理接口请求失败'
  } finally {
    loading.value = false
  }
}

async function setMainCircle(circleId: number) {
  if (userStore.userInfo?.mainCircleId === circleId) {
    return
  }
  try {
    await userStore.updateUserInfo({ mainCircleId: circleId })
    uni.showToast({ title: '主圈子已更新', icon: 'success' })
  } catch (error) {
    console.error('设置主圈子失败', error)
    showActionError(error, '更新失败')
  }
}

function quitCircle(circle: MyCircleCard) {
  uni.showModal({
    title: '退出圈子',
    content: `确定要退出「${circle.name}」吗？`,
    success: async (res) => {
      if (!res.confirm) {
        return
      }
      try {
        await leaveCircle(circle.id)
        await userStore.fetchUserInfo()
        uni.showToast({ title: '已退出圈子', icon: 'none' })
        await loadCircles()
      } catch (error) {
        console.error('退出圈子失败', error)
        showActionError(error, '退出失败')
      }
    }
  })
}

function goCircleDetail(circleId: number) {
  uni.navigateTo({ url: `/pages/circle-private/index?circleId=${circleId}` })
}

function goCreateCircle() {
  uni.navigateTo({ url: '/pages/circle-create/index' })
}

function circleToneClass(index: number) {
  const tones = ['tone-ocean', 'tone-mint', 'tone-sun', 'tone-dusk']
  return tones[index % tones.length]
}

function circleTypeLabelText(type?: string | null) {
  return type === '1' ? '私密圈子' : '公开挑战'
}

function circleDurationText(durationDays?: number | null) {
  if (!durationDays || durationDays <= 0) {
    return '长期'
  }
  if (durationDays >= 100) {
    return `${Math.round(durationDays / 30)} 月`
  }
  return `${durationDays} 天`
}

function circleDepositText(depositRequired?: number | null) {
  return '押金暂未开放'
}

function joinedAtText(joinedAt?: string | null) {
  if (!joinedAt) {
    return '已加入'
  }
  return `加入于 ${joinedAt.slice(0, 10)}`
}
</script>

<style scoped lang="scss">
@use '../../styles/flow-button.scss';

.page-container {
  min-height: 100vh;
  background: #f7f8fa;
}

.circle-list {
  padding: 0 30rpx;
}

.empty-wrap {
  padding: 0 30rpx;
}

.circle-item {
  display: flex;
  align-items: stretch;
  background: white;
  border-radius: 36rpx;
  padding: 26rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 18rpx 34rpx rgba(24, 39, 75, 0.06);
  border: 1px solid rgba(74, 144, 226, 0.08);
  gap: 24rpx;
  overflow: hidden;
}

.circle-cover {
  width: 184rpx;
  min-height: 244rpx;
  border-radius: 30rpx;
  padding: 18rpx;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  position: relative;
  overflow: hidden;
  color: white;
}

.circle-cover-image {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  z-index: 0;
}

.circle-cover.cover-custom {
  background: linear-gradient(145deg, #334155, #64748b);
}

.circle-cover--image::before {
  content: '';
  position: absolute;
  inset: 0;
  z-index: 1;
  background: linear-gradient(180deg, rgba(15, 23, 42, 0.18), rgba(15, 23, 42, 0.68));
}

.circle-cover.cover-ocean {
  background: linear-gradient(145deg, #4f8ef7, #54d2d2);
}

.circle-cover.cover-sunrise {
  background: linear-gradient(145deg, #f59e0b, #fb7185);
}

.circle-cover.cover-forest {
  background: linear-gradient(145deg, #2d6a4f, #52b788);
}

.circle-cover.cover-berry {
  background: linear-gradient(145deg, #d946ef, #fb7185);
}

.circle-cover.cover-night {
  background: linear-gradient(145deg, #334155, #6366f1);
}

.circle-cover.cover-sand {
  background: linear-gradient(145deg, #d4a373, #e9c46a);
}

.circle-cover::after {
  content: '';
  position: absolute;
  inset: auto -20rpx -40rpx auto;
  z-index: 1;
  width: 120rpx;
  height: 120rpx;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.16);
}

.circle-cover-top {
  position: relative;
  z-index: 2;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 10rpx;
}

.circle-avatar {
  position: relative;
  z-index: 2;
  width: 76rpx;
  height: 76rpx;
  border-radius: 24rpx;
  background: rgba(255, 255, 255, 0.18);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 36rpx;
  box-shadow: 0 14rpx 24rpx rgba(15, 23, 42, 0.12);
}

.circle-cover-caption {
  position: relative;
  z-index: 2;
  font-size: 22rpx;
  line-height: 1.5;
  color: rgba(255, 255, 255, 0.86);
}

.circle-content {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
}

.circle-info {
  min-width: 0;
}

.circle-name {
  display: flex;
  margin-bottom: 12rpx;
}

.circle-name-text {
  display: -webkit-box;
  font-size: 32rpx;
  line-height: 1.28;
  font-weight: 800;
  color: #0f172a;
  overflow: hidden;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.circle-desc {
  display: -webkit-box;
  font-size: 23rpx;
  line-height: 1.7;
  color: #64748b;
  overflow: hidden;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  min-height: 78rpx;
}

.circle-readonly {
  margin-top: 8rpx;
  font-size: 22rpx;
  line-height: 1.6;
  color: #8a5b2c;
}

.circle-highlight-row {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12rpx;
  margin-top: 18rpx;
}

.circle-highlight {
  min-width: 0;
  padding: 14rpx 12rpx;
  border-radius: 22rpx;
  background: linear-gradient(135deg, rgba(74, 144, 226, 0.08), rgba(255, 255, 255, 0.96));
  border: 1px solid rgba(74, 144, 226, 0.08);
}

.circle-highlight-value {
  display: block;
  font-size: 24rpx;
  font-weight: 800;
  color: #0f172a;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.circle-highlight-label {
  display: block;
  margin-top: 6rpx;
  font-size: 18rpx;
  color: #64748b;
}

.circle-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 10rpx;
  margin-top: 16rpx;
}

.action-btn {
  flex: 1;
  min-width: 0;
  margin-left: 0;
}

.action-btn.active {
  box-shadow: none;
}

.btn-quit {
  flex: 1;
  min-width: 0;
  margin-left: 0;
}

.circle-actions {
  display: flex;
  gap: 12rpx;
  margin-top: auto;
  padding-top: 18rpx;
}

.footer-action {
  margin: 40rpx 30rpx;
}

.loading-block {
  margin: 24rpx 30rpx 0;
  padding: 60rpx 36rpx;
  background: white;
  border-radius: 32rpx;
  text-align: center;
  color: #888888;
}
</style>
