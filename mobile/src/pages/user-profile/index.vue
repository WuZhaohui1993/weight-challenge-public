<template>
  <view class="page-container wc-page-enter">
    <prototype-header title="用户主页" back-url="/pages/circle/index" />

    <scroll-view class="content wc-section-enter" scroll-y>
      <view v-if="loading" class="loading-block">用户主页加载中...</view>

      <template v-else-if="profile">
        <view class="hero-card">
          <view class="hero-card__bg hero-card__bg--one"></view>
          <view class="hero-card__bg hero-card__bg--two"></view>

          <resolved-image
            class="hero-avatar"
            :src="profile.avatar"
            mode="aspectFill"
            shape="avatar"
            fallback-class="hero-avatar hero-avatar--fallback"
            :fallback-text="initialText(profile.nickname)"
          />

          <view class="hero-copy">
            <view class="hero-title-row">
              <text class="hero-name">{{ profile.nickname || `用户${profile.userId}` }}</text>
              <text v-if="profile.self" class="wc-badge wc-badge--soft">本人</text>
            </view>
            <text class="hero-subtitle">
              {{ profile.self ? '这是你在圈子和公开动态中的展示页。' : '最近公开动态和基础资料在这里汇总展示。' }}
            </text>

            <view class="hero-meta">
              <text v-if="profile.mainCircleName" class="wc-badge wc-badge--primary" @tap="goCircleDetail(profile.mainCircleId)">
                主圈子 · {{ profile.mainCircleName }}
              </text>
              <text v-if="profile.currentWeight" class="wc-badge wc-badge--soft">当前 {{ Number(profile.currentWeight).toFixed(1) }}kg</text>
              <text v-if="profile.targetWeight" class="wc-badge wc-badge--soft">目标 {{ Number(profile.targetWeight).toFixed(1) }}kg</text>
            </view>
          </view>

          <view class="hero-stats">
            <view class="hero-stat">
              <text class="hero-stat__value">{{ profile.joinedCircleCount || 0 }}</text>
              <text class="hero-stat__label">加入圈子</text>
            </view>
            <view class="hero-stat">
              <text class="hero-stat__value">{{ profile.streakDays || 0 }}</text>
              <text class="hero-stat__label">连续打卡</text>
            </view>
            <view class="hero-stat">
              <text class="hero-stat__value">{{ profile.achievementPoints || 0 }}</text>
              <text class="hero-stat__label">成就积分</text>
            </view>
          </view>
        </view>

        <app-section-header compact title="最近公开动态" :subtitle="feedSectionSubtitle" />

        <view v-if="profile.recentPublicFeeds.length" class="feed-list">
          <view
            v-for="feed in profile.recentPublicFeeds"
            :key="feed.id"
            class="feed-card wc-pressable"
            @tap="goFeedDetail(feed.id)"
          >
            <view class="feed-card__top">
              <view class="feed-card__badges">
                <text v-if="feed.isFeatured" class="wc-badge wc-badge--warning">精选</text>
                <text class="wc-badge wc-badge--primary">{{ feedTypeText(feed.feedType) }}</text>
              </view>
              <text class="feed-card__time">{{ circleFeedTimeText(feed.createdAt) }}</text>
            </view>
            <text class="feed-card__content">{{ feed.content || '这条公开动态没有填写文字内容。' }}</text>
            <view v-if="feed.images?.length" class="feed-card__images" :class="`grid-${Math.min(feed.images.length, 3)}`">
              <resolved-image
                v-for="(image, index) in feed.images.slice(0, 3)"
                :key="`${feed.id}-${index}`"
                class="feed-card__image"
                :src="image"
                mode="aspectFill"
                :preview-list="feed.images"
                :preview-index="index"
              />
            </view>
            <view class="feed-card__footer">
              <text>{{ feed.originCircleName || '公开动态' }}</text>
              <text>❤️ {{ feed.likesCount || 0 }} · 💬 {{ feed.commentsCount || 0 }}</text>
            </view>
          </view>
        </view>
        <view v-else class="section-padding">
          <app-empty-state
            icon="🗂️"
            title="还没有公开动态"
            :description="profile.self ? '去圈子页发布一条公开动态，这里就会自动出现。' : '对方暂时还没有公开展示的动态。'"
          />
        </view>
      </template>

      <view v-else class="section-padding">
        <app-empty-state
          :icon="loadErrorMessage ? '⚠️' : '👤'"
          :title="loadErrorMessage ? '用户主页加载失败' : '没有找到这个用户'"
          :description="loadErrorMessage || '请返回上一级重新进入。'"
        >
          <button
            v-if="loadErrorMessage"
            class="flow-btn flow-btn--secondary flow-btn--compact"
            @tap="loadProfile"
          >
            重新整理
          </button>
        </app-empty-state>
      </view>

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
import { getPublicUserProfile } from '@/api/user'
import type { PublicUserProfile } from '@/types/api'
import { circleFeedTimeText, initialText } from '@/utils/circle'
import { useDefaultPageShare } from '@/utils/share'

useDefaultPageShare()

const userId = ref<number | null>(null)
const loading = ref(false)
const loadErrorMessage = ref('')
const profile = ref<PublicUserProfile | null>(null)

const feedSectionSubtitle = computed(() =>
  profile.value?.recentPublicFeeds?.length
    ? `最近 ${profile.value.recentPublicFeeds.length} 条公开动态`
    : '这里只展示对外可见的动态内容'
)

onLoad((query) => {
  const parsed = Number(query?.userId || 0)
  if (parsed > 0) {
    userId.value = parsed
  }
})

onShow(() => {
  if (userId.value) {
    void loadProfile()
  }
})

async function loadProfile() {
  if (!userId.value) {
    return
  }
  loading.value = true
  loadErrorMessage.value = ''
  try {
    profile.value = await getPublicUserProfile(userId.value)
  } catch (error) {
    console.error('加载用户主页失败', error)
    profile.value = null
    loadErrorMessage.value = error instanceof Error ? error.message || '用户主页暂时不可用' : '用户主页暂时不可用'
  } finally {
    loading.value = false
  }
}

function goFeedDetail(feedId?: number | null) {
  if (!feedId) {
    return
  }
  uni.navigateTo({ url: `/pages/feed-detail/index?feedId=${feedId}` })
}

function goCircleDetail(circleId?: number | null) {
  if (!circleId) {
    return
  }
  uni.navigateTo({ url: `/pages/circle-private/index?circleId=${circleId}` })
}

function feedTypeText(feedType?: string | null) {
  const mapping: Record<string, string> = {
    weight: '体重',
    food: '饮食',
    exercise: '运动',
    habit: '习惯',
    text: '日常'
  }
  return mapping[feedType || ''] || '动态'
}
</script>

<style scoped lang="scss">
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

.section-padding {
  padding: 24rpx 30rpx 0;
}

.hero-card {
  position: relative;
  margin: 24rpx 30rpx 0;
  padding: 34rpx 30rpx;
  border-radius: 36rpx;
  overflow: hidden;
  background: linear-gradient(145deg, #0f766e, #1d4ed8 58%, #f97316);
  color: #fff;
}

.hero-card__bg {
  position: absolute;
  border-radius: 50%;
  opacity: 0.16;
}

.hero-card__bg--one {
  top: -80rpx;
  right: -50rpx;
  width: 240rpx;
  height: 240rpx;
  background: #fff;
}

.hero-card__bg--two {
  left: -80rpx;
  bottom: -120rpx;
  width: 320rpx;
  height: 320rpx;
  background: rgba(255, 255, 255, 0.7);
}

.hero-avatar {
  position: relative;
  z-index: 1;
  width: 128rpx;
  height: 128rpx;
  border-radius: 50%;
  border: 4rpx solid rgba(255, 255, 255, 0.28);
}

.hero-avatar--fallback {
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.16);
  font-size: 42rpx;
  font-weight: 700;
}

.hero-copy {
  position: relative;
  z-index: 1;
  margin-top: 22rpx;
}

.hero-title-row {
  display: flex;
  align-items: center;
  gap: 12rpx;
  flex-wrap: wrap;
}

.hero-name {
  font-size: 40rpx;
  font-weight: 800;
}

.hero-subtitle {
  display: block;
  margin-top: 12rpx;
  font-size: 24rpx;
  line-height: 1.7;
  color: rgba(255, 255, 255, 0.82);
}

.hero-meta {
  margin-top: 18rpx;
  display: flex;
  flex-wrap: wrap;
  gap: 12rpx;
}

.hero-stats {
  position: relative;
  z-index: 1;
  margin-top: 28rpx;
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16rpx;
}

.hero-stat {
  padding: 22rpx 18rpx;
  border-radius: 26rpx;
  background: rgba(255, 255, 255, 0.12);
}

.hero-stat__value {
  display: block;
  font-size: 34rpx;
  font-weight: 800;
}

.hero-stat__label {
  display: block;
  margin-top: 8rpx;
  font-size: 22rpx;
  color: rgba(255, 255, 255, 0.78);
}

.feed-list {
  padding: 0 30rpx;
}

.feed-card {
  margin-bottom: 20rpx;
  padding: 26rpx;
  border-radius: 30rpx;
  background: #fff;
  box-shadow: 0 10rpx 30rpx rgba(15, 23, 42, 0.05);
}

.feed-card__top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16rpx;
}

.feed-card__badges {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 10rpx;
  min-width: 0;
}

.feed-card__time {
  flex-shrink: 0;
  font-size: 22rpx;
  color: #64748b;
}

.feed-card__content {
  display: block;
  margin-top: 18rpx;
  font-size: 28rpx;
  line-height: 1.7;
  color: #0f172a;
}

.feed-card__images {
  margin-top: 18rpx;
  display: grid;
  gap: 12rpx;
}

.feed-card__images.grid-1 {
  grid-template-columns: 1fr;
}

.feed-card__images.grid-2 {
  grid-template-columns: repeat(2, 1fr);
}

.feed-card__images.grid-3 {
  grid-template-columns: repeat(3, 1fr);
}

.feed-card__image {
  width: 100%;
  aspect-ratio: 1;
  border-radius: 22rpx;
}

.feed-card__footer {
  margin-top: 18rpx;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16rpx;
  font-size: 22rpx;
  color: #64748b;
}

.page-bottom-space {
  height: 120rpx;
}
</style>
