<template>
  <view class="page-container wc-page-enter">
    <prototype-header title="成就" back-url="/pages/profile/index" />

    <scroll-view class="content wc-section-enter" scroll-y>
      <view v-if="loading" class="loading-block">成就加载中...</view>

      <view v-else-if="loadError" class="empty-wrap">
        <app-empty-state
          icon="⚠️"
          title="成就加载失败"
          description="请检查当前网络和后端服务，然后再试一次。"
        >
          <button class="flow-btn flow-btn--secondary flow-btn--compact" @tap="loadAchievementPage">重新整理</button>
        </app-empty-state>
      </view>

      <view v-else-if="userInfo" class="content-inner">
        <view class="stats-card">
          <view class="stats-bg-decor"></view>
          <text class="stats-title">当前成就积分</text>
          <text class="stats-value">{{ userInfo.achievementPoints || 0 }}</text>
          <text class="stats-sub">连续打卡 {{ userInfo.streakDays || 0 }} 天</text>
          <view class="stats-row">
            <view class="stat-item">
              <text class="stat-num">{{ unlockedBadges }}</text>
              <text class="stat-label">已点亮</text>
            </view>
            <view class="stat-item">
              <text class="stat-num">{{ achievementMetrics.activeHabits }}</text>
              <text class="stat-label">活跃习惯</text>
            </view>
            <view class="stat-item">
              <text class="stat-num">{{ achievementMetrics.joinedCircles }}</text>
              <text class="stat-label">已加入圈子</text>
            </view>
          </view>
        </view>

        <app-section-header compact title="勋章墙" :subtitle="achievementWallSubtitle" />
        <view class="badge-grid">
          <view
            v-for="badge in badgeItems"
            :key="badge.key"
            class="badge-item"
            :class="{ locked: !badge.unlocked }"
          >
            <text class="badge-icon">{{ badge.icon }}</text>
            <text class="badge-name">{{ badge.name }}</text>
            <text class="badge-desc">{{ badge.desc }}</text>
            <text class="badge-progress-text">{{ badge.progressText }}</text>
            <view class="badge-progress">
              <view class="badge-progress-fill" :style="{ width: `${badge.progress}%` }"></view>
            </view>
          </view>
        </view>

        <app-section-header compact title="当前进展" subtitle="先看离你最近的一步，再决定今天补哪项。" />
        <view class="recent-list">
          <view v-if="nextBadge" class="recent-item">
            <text class="recent-icon">{{ nextBadge.icon }}</text>
            <view class="recent-info">
              <text class="recent-title">下一个目标：{{ nextBadge.name }}</text>
              <text class="recent-desc">{{ nextBadge.desc }}，当前进度 {{ nextBadge.progressText }}</text>
            </view>
          </view>

          <view class="recent-item">
            <text class="recent-icon">📌</text>
            <view class="recent-info">
              <text class="recent-title">当前资料完整度</text>
              <text class="recent-desc">{{ profileProgressText }}</text>
            </view>
          </view>

          <view v-if="allUnlocked" class="recent-item">
            <text class="recent-icon">🎉</text>
            <view class="recent-info">
              <text class="recent-title">当前成就位已全部点亮</text>
              <text class="recent-desc">可以继续围绕习惯、圈子和连续打卡积累更稳定的行为节奏。</text>
            </view>
          </view>
        </view>
      </view>

      <view v-else class="empty-wrap">
        <app-empty-state
          icon="🏅"
          title="登录后查看成就"
          description="先回到个人中心完成登录，再查看当前进展。"
        />
      </view>

      <view class="page-bottom-space"></view>
    </scroll-view>
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import AppEmptyState from '@/components/AppEmptyState/index.vue'
import AppSectionHeader from '@/components/AppSectionHeader/index.vue'
import PrototypeHeader from '@/components/PrototypeHeader/index.vue'
import { getAchievements } from '@/api/achievement'
import { getMyCircles } from '@/api/circle'
import { getHabitStats } from '@/api/habit'
import { useUserStore } from '@/stores/user'
import {
  buildAchievementBadges,
  buildAchievementBadgesFromRecords,
  countUnlockedAchievements
} from '@/utils/achievement'
import { useDefaultPageShare } from '@/utils/share'
import type { AchievementItem } from '@/types/api'

useDefaultPageShare()

const userStore = useUserStore()
const userInfo = computed(() => userStore.userInfo)
const loading = ref(false)
const loadError = ref('')
const achievementMetrics = ref({
  activeHabits: 0,
  joinedCircles: 0
})
const achievementRecords = ref<AchievementItem[]>([])

onShow(() => {
  void loadAchievementPage()
})

const hasBackendAchievements = computed(() => achievementRecords.value.length > 0)
const fallbackBadgeItems = computed(() => buildAchievementBadges(userInfo.value, achievementMetrics.value))
const badgeItems = computed(() => (
  hasBackendAchievements.value
    ? buildAchievementBadgesFromRecords(achievementRecords.value)
    : fallbackBadgeItems.value
))
const unlockedBadges = computed(() => countUnlockedAchievements(badgeItems.value))
const nextBadge = computed(() => badgeItems.value.find((item) => !item.unlocked) ?? null)
const allUnlocked = computed(() => badgeItems.value.length > 0 && unlockedBadges.value === badgeItems.value.length)
const achievementWallSubtitle = computed(() => (
  hasBackendAchievements.value
    ? '展示后台成就管理中为当前用户维护的徽章记录。'
    : '当前用户暂无后台徽章记录，先按资料、习惯和圈子状态展示本地进度。'
))
const profileProgressText = computed(() => {
  const parts = [
    userInfo.value?.targetWeight ? '目标体重已设置' : '还没设置目标体重',
    userInfo.value?.dailyWaterTarget ? `饮水目标 ${userInfo.value.dailyWaterTarget} 杯/天` : '还没设置饮水目标',
    achievementMetrics.value.activeHabits > 0 ? `已启用 ${achievementMetrics.value.activeHabits} 个习惯` : '还没有启用习惯'
  ]
  return parts.join('，')
})

async function loadAchievementPage() {
  loading.value = true
  loadError.value = ''
  try {
    const [userResult, achievementResult, habitStatsResult, myCirclesResult] = await Promise.allSettled([
      userStore.fetchUserInfo(),
      getAchievements(),
      getHabitStats(),
      getMyCircles({ pageNum: 1, pageSize: 1 })
    ])

    achievementRecords.value = achievementResult.status === 'fulfilled' ? achievementResult.value : []
    achievementMetrics.value = {
      activeHabits: habitStatsResult.status === 'fulfilled' ? Number(habitStatsResult.value.activeHabits || 0) : 0,
      joinedCircles: myCirclesResult.status === 'fulfilled' ? Number(myCirclesResult.value.total || 0) : 0
    }

    if (userStore.isLoggedIn) {
      if (userResult.status === 'rejected') {
        throw userResult.reason
      }
      if (achievementResult.status === 'rejected') {
        throw achievementResult.reason
      }
    }
  } catch (error) {
    console.error('加载成就页失败', error)
    loadError.value = error instanceof Error ? error.message : '成就数据加载失败'
  } finally {
    loading.value = false
  }
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

.content-inner {
  display: flex;
  flex-direction: column;
  gap: 24rpx;
}

.loading-block,
.empty-wrap {
  padding: 24rpx 30rpx 0;
}

.loading-block {
  font-size: 26rpx;
  color: var(--wc-text-soft);
}

.stats-card {
  background: var(--wc-surface-strong);
  margin: 24rpx 30rpx 0;
  border-radius: 32rpx;
  padding: 36rpx;
  border: 1px solid var(--wc-line);
  box-shadow: var(--wc-shadow-soft);
  text-align: center;
  position: relative;
  overflow: hidden;
}

.stats-bg-decor {
  position: absolute;
  top: -24rpx;
  right: -24rpx;
  width: 140rpx;
  height: 140rpx;
  background: linear-gradient(135deg, #ffd700, #ffa000);
  opacity: 0.1;
  border-radius: 50%;
}

.stats-title {
  display: block;
  font-size: 24rpx;
  color: #888888;
  margin-bottom: 8rpx;
}

.stats-value {
  display: block;
  font-size: 64rpx;
  font-weight: 800;
  color: #333333;
  margin-bottom: 8rpx;
}

.stats-sub {
  display: inline-block;
  font-size: 22rpx;
  color: #888888;
  background: #f5f5f5;
  padding: 6rpx 14rpx;
  border-radius: 999rpx;
}

.stats-row {
  display: flex;
  justify-content: space-around;
  margin-top: 30rpx;
}

.stat-item {
  text-align: center;
}

.stat-num {
  display: block;
  font-size: 34rpx;
  font-weight: 700;
}

.stat-label {
  display: block;
  margin-top: 8rpx;
  font-size: 20rpx;
  color: #888888;
}

.badge-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 18rpx;
  padding: 0 30rpx;
}

.badge-item {
  background: var(--wc-surface-strong);
  border-radius: 28rpx;
  padding: 24rpx 16rpx;
  text-align: center;
  border: 1px solid var(--wc-line);
  box-shadow: var(--wc-shadow-soft);
}

.badge-item.locked {
  background: var(--wc-bg-soft);
  box-shadow: none;
}

.badge-icon {
  display: block;
  font-size: 44rpx;
  margin-bottom: 12rpx;
}

.badge-name {
  display: block;
  font-size: 24rpx;
  font-weight: 600;
  color: #333333;
  margin-bottom: 8rpx;
}

.badge-item.locked .badge-name {
  color: #999999;
}

.badge-desc {
  display: block;
  min-height: 64rpx;
  font-size: 18rpx;
  line-height: 1.5;
  color: #888888;
}

.badge-progress-text {
  display: block;
  margin-top: 10rpx;
  font-size: 18rpx;
  color: var(--wc-text-soft);
}

.badge-progress {
  margin-top: 14rpx;
  height: 8rpx;
  background: #f0f0f0;
  border-radius: 999rpx;
  overflow: hidden;
}

.badge-progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #4a90e2, #50e3c2);
}

.recent-list {
  padding: 0 30rpx;
}

.recent-item {
  display: flex;
  align-items: center;
  background: var(--wc-surface-strong);
  border-radius: 32rpx;
  padding: 28rpx;
  border: 1px solid var(--wc-line);
  box-shadow: var(--wc-shadow-soft);
}

.recent-icon {
  font-size: 40rpx;
  margin-right: 16rpx;
}

.recent-info {
  flex: 1;
}

.recent-title {
  display: block;
  font-size: 28rpx;
  font-weight: 600;
  color: #333333;
  margin-bottom: 8rpx;
}

.recent-desc {
  display: block;
  font-size: 22rpx;
  color: #888888;
  line-height: 1.6;
}

.page-bottom-space {
  height: 60rpx;
}
</style>
