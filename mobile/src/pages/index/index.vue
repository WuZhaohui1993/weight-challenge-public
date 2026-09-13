<template>
  <view class="home-container wc-page-enter">
    <!-- 顶部导航（自定义，整体在胶囊按钮下方） -->
    <view class="header wc-tab-header" :style="{ paddingTop: `${safeTop}px`, paddingRight: `${menuButtonSafeRight}px` }">
      <view class="header-body wc-tab-header__body" :style="{ minHeight: `${topBarHeight - safeTop}px` }">
        <view class="header-copy">
          <text class="header-kicker">{{ headerKickerText }}</text>
          <text class="user-welcome">{{ greetingText }}，{{ userInfo?.nickname || '用户' }} 👋</text>
          <view class="header-meta">
            <text class="date-display">{{ currentDate }}</text>
            <text class="header-divider">/</text>
            <text class="header-status">{{ headerStatusText }}</text>
          </view>
          <text class="daily-quote">今日一句：{{ dailyQuoteText }}</text>
        </view>
      </view>

      <view class="header-tools wc-section-enter">
        <view class="notification-chip wc-info-chip wc-pressable" @tap="goNotifications">
          <text class="chip-icon">🔔</text>
          <text class="chip-text">{{ hasNotification ? '有新提醒' : '消息中心' }}</text>
          <view class="notification-badge" v-if="hasNotification"></view>
        </view>

        <view class="profile-chip wc-info-chip wc-pressable" @tap="goProfile">
          <resolved-image
            class="profile-chip-avatar"
            :src="userInfo?.avatar"
            mode="aspectFill"
            shape="avatar"
            fallback-class="profile-chip-avatar profile-chip-avatar--fallback"
            :fallback-text="initialText(userInfo?.nickname, '我')"
          />
          <view class="profile-chip-copy">
            <text class="profile-chip-name">{{ userInfo?.nickname || '我的' }}</text>
            <text class="profile-chip-note">{{ profileHintText }}</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 滚动内容 -->
    <scroll-view
      class="scroll-content wc-tab-scroll wc-tab-scroll--top-gap wc-section-enter"
      scroll-y
    >
      <view v-if="homeLoadError" class="home-load-banner">
        <view class="home-load-banner__copy">
          <text class="home-load-banner__title">首页数据暂未更新</text>
          <text class="home-load-banner__desc">{{ homeLoadError }}</text>
        </view>
        <button class="flow-btn flow-btn--secondary flow-btn--compact home-load-banner__action" @tap="loadHomeData">重新整理</button>
      </view>

      <view v-if="showGuestIntro" class="guest-intro-card">
        <view class="guest-intro-copy">
          <text class="guest-intro-kicker">先浏览，再登录</text>
          <text class="guest-intro-title">先了解记录、分析和圈子功能</text>
          <text class="guest-intro-desc">你可以先查看首页能力说明；开始记录、加入圈子或查看个人数据时，再由你主动选择微信授权登录。</text>
        </view>
        <view class="guest-intro-actions">
          <button class="flow-btn flow-btn--primary flow-btn--compact guest-intro-btn" @tap="goLoginFromGuest">登录后开始记录</button>
          <button class="flow-btn flow-btn--secondary flow-btn--compact guest-intro-btn" @tap="continueGuestBrowsing">继续浏览首页</button>
        </view>
      </view>

      <!-- 1. 今日摘要 -->
      <view class="card">
        <view class="card-header">
          <text class="card-title">今天状态</text>
          <text class="card-more wc-pressable" @tap="goAnalytics">去复盘 ›</text>
        </view>
        <view class="today-brief">
          <view class="today-weight">
            <view class="today-weight__copy">
              <text class="today-weight__label">今天体重</text>
              <view class="today-weight__value-row">
                <text class="today-weight__value">{{ todayStats.weight || '--' }}</text>
                <text class="today-weight__unit">kg</text>
              </view>
              <text class="today-weight__trend" :class="{ down: weightTrend < 0, up: weightTrend > 0 }">{{ weightTrendInlineText }}</text>
            </view>
            <view class="today-weight__mark">~</view>
          </view>

          <view class="today-note-list">
            <view class="today-note">
              <text class="today-note__label">身体状态</text>
              <text class="today-note__value">{{ bmiStatus }}</text>
              <text class="today-note__meta">{{ goalSummaryText }}</text>
            </view>
            <view class="today-note">
              <text class="today-note__label">今天吃了</text>
              <text class="today-note__value">{{ todayStats.calories || 0 }} kcal</text>
              <text class="today-note__meta" :class="calorieReferenceClass">{{ calorieReferenceText }}</text>
            </view>
            <view class="today-note">
              <text class="today-note__label">今天动了</text>
              <text class="today-note__value">{{ todayStats.exercise || 0 }} kcal</text>
              <text class="today-note__meta">{{ exerciseDeficitContributionText }}</text>
            </view>
          </view>
        </view>
      </view>

      <!-- 2. 快捷入口 -->
      <text class="section-label">快速记录</text>
      <view class="quick-actions-grid">
        <view class="action-btn wc-tile-action wc-pressable" @tap="goRecord('diet')">
          <view class="action-icon-bg wc-tile-action__icon diet">🥗</view>
          <text class="action-label wc-tile-action__label">记饮食</text>
        </view>
        <view class="action-btn wc-tile-action wc-pressable" @tap="goRecord('weight')">
          <view class="action-icon-bg wc-tile-action__icon weight">⚖️</view>
          <text class="action-label wc-tile-action__label">记体重</text>
        </view>
        <view class="action-btn wc-tile-action wc-pressable" @tap="goRecord('exercise')">
          <view class="action-icon-bg wc-tile-action__icon food">🏃</view>
          <text class="action-label wc-tile-action__label">记运动</text>
        </view>
        <view class="action-btn wc-tile-action wc-pressable" @tap="goRecord('habit')">
          <view class="action-icon-bg wc-tile-action__icon habit">✅</view>
          <text class="action-label wc-tile-action__label">记习惯</text>
        </view>
      </view>

      <!-- 好友圈卡片（正常显示） -->
      <view class="card buddy-card wc-pressable" @tap="goCircleDetail" v-if="hasCircle">
        <view class="card-header">
          <text class="card-title">🔥 {{ currentCircle.name }}</text>
          <view class="circle-switch-btn wc-pill-action wc-pill-action--tool wc-pressable" @tap.stop="showCircleSwitcher">
            <text>🔁</text>
            <text>切换</text>
          </view>
        </view>
        <!-- 成员打卡状态 -->
        <view class="buddy-members">
          <view
            class="buddy-member"
            :class="member.tone"
            v-for="member in circleMembers"
            :key="member.userId"
          >
            <resolved-image
              class="buddy-avatar"
              :src="member.avatar"
              mode="aspectFill"
              shape="avatar"
              fallback-class="buddy-avatar buddy-avatar--fallback"
              :fallback-text="initialText(member.nickname, '友')"
            />
            <text class="buddy-name">{{ member.nickname }}</text>
            <text class="buddy-status">{{ member.statusText }}</text>
            <text class="buddy-meta">{{ member.metaText }}</text>
          </view>
        </view>
        <!-- 本周排行 -->
        <view class="buddy-pk">
          <view class="pk-item winner">
            <text class="pk-name">{{ pkRanking[0]?.nickname || '--' }}</text>
            <text class="pk-value">{{ pkRanking[0]?.change || '--' }}</text>
          </view>
          <text class="pk-vs">⚔️</text>
          <view class="pk-item">
            <text class="pk-name">{{ pkRanking[1]?.nickname || '--' }}</text>
            <text class="pk-value">{{ pkRanking[1]?.change || '--' }}</text>
          </view>
        </view>
      </view>

      <!-- 未加入圈子时的引导卡片 -->
      <view class="card circle-guide-card" v-else-if="showCircleGuide">
        <text class="guide-icon">🎯</text>
        <text class="guide-title">一起挑战更有动力！</text>
        <text class="guide-desc">加入减重圈子，和小伙伴互相监督</text>
        <view class="guide-actions">
          <button class="guide-btn flow-btn flow-btn--primary flow-btn--compact wc-pressable" @tap="goCreateCircle">创建圈子</button>
          <button class="guide-btn flow-btn flow-btn--secondary flow-btn--compact wc-pressable" @tap="goBrowseCircle">浏览圈子</button>
        </view>
        <text class="guide-skip" @tap="skipGuide">暂不需要，以后再说</text>
      </view>

      <!-- 3. 饮水记录 -->
      <view class="card water-card">
        <view class="water-progress">
          <view class="water-ring" :style="waterRingStyle"></view>
          <view class="water-circle">
            <text class="water-icon">💧</text>
          </view>
        </view>
        <view class="water-text">
          <text class="card-title">今日饮水</text>
          <view class="water-count-row">
            <text class="water-count">{{ waterCups }}</text>
            <text class="water-target-unit"> / {{ waterTarget }} 杯</text>
          </view>
          <text class="water-tip">保持水分充足有助于代谢</text>
        </view>
        <view class="btn-add-water wc-icon-button wc-icon-button--primary wc-icon-button--circle wc-pressable" @tap="addWater">+</view>
      </view>

      <view class="page-bottom-space"></view>
    </scroll-view>

    <view v-if="profileReminderVisible" class="profile-reminder-mask">
      <view class="profile-reminder-card" @tap.stop>
        <view class="profile-reminder-header">
          <text class="profile-reminder-title">{{ profileReminderTitle }}</text>
          <text class="profile-reminder-subtitle">{{ profileReminderDescription }}</text>
        </view>

        <view class="profile-reminder-tags">
          <text v-for="item in profileReminderMissingItems" :key="item" class="profile-reminder-tag">{{ item }}</text>
        </view>

        <text class="profile-reminder-tip">
          去完善后会直接进入“我的”页资料编辑弹层，你也可以先进入首页继续使用，后面再补。
        </text>

        <view class="profile-reminder-actions">
          <button class="profile-reminder-btn flow-btn flow-btn--primary profile-reminder-btn--primary" @tap="goCompleteProfile">完善资料</button>
          <button class="profile-reminder-btn flow-btn flow-btn--secondary profile-reminder-btn--secondary" @tap="closeProfileReminder">稍后再说</button>
          <button class="profile-reminder-btn flow-btn flow-btn--secondary profile-reminder-btn--ghost" @tap="disableReminderAndClose">以后不再提醒</button>
        </view>
      </view>
    </view>

    <onboarding-guide
      :visible="onboardingGuideVisible"
      :current-step="onboardingGuideStep"
      @close="closeOnboardingGuide"
      @disable="disableOnboardingGuideAndClose"
      @primary="handleOnboardingPrimary"
      @skip="skipOnboardingStep"
      @select-step="selectOnboardingStep"
    />
  </view>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import ResolvedImage from '@/components/ResolvedImage/index.vue'
import OnboardingGuide from '@/components/OnboardingGuide/index.vue'
import { useUserStore } from '@/stores/user'
import http, { openLoginPage } from '@/utils/request'
import type { DashboardPayload, WaterRecordPayload } from '@/types/api'
import { getMyCircles } from '@/api/circle'
import { initialText } from '@/utils/circle'
import { getMenuButtonSafeRight, getSafeTop, getTopBarHeight, syncCustomTabBar } from '@/utils/mobile'
import { useTabBarOverlayVisibility } from '@/utils/page'
import { useDefaultPageShare } from '@/utils/share'
import {
  advanceOnboardingGuideStep,
  completeOnboardingGuide,
  disableOnboardingGuide,
  getOnboardingGuideStep,
  setOnboardingGuideStep,
  shouldPromptOnboardingGuide,
  type OnboardingGuideStep
} from '@/utils/onboarding'
import { consumeProfileReminderIntent, disableProfileReminder, listMissingProfileItems, queueProfileEditorIntent, shouldPromptProfileSetup } from '@/utils/profileSetup'

useDefaultPageShare()

const userStore = useUserStore()
const userInfo = computed(() => userStore.userInfo)
const isLoggedIn = computed(() => userStore.isLoggedIn)
const dashboard = ref<DashboardPayload | null>(null)
const dismissingCircleGuide = ref(false)
const guestIntroDismissed = ref(false)
const addingWater = ref(false)
const profileReminderVisible = ref(false)
const profileReminderIsNewUser = ref(false)
const onboardingGuideVisible = ref(false)
const onboardingGuideStep = ref<OnboardingGuideStep>('profile')
const homeLoadError = ref('')
const currentNow = ref(new Date())

const dailyQuotes = [
  '体重看趋势，记录看今天。',
  '先完成一条记录，再慢慢优化节奏。',
  '今天的每一次选择，都会被身体记住。',
  '不用追求完美，稳定才是最难得的进步。',
  '吃得清楚，动得明白，目标就会更近一点。',
  '把今天过扎实，比补昨天更重要。',
  '记录不是约束，是帮你看见真实的自己。'
]

const safeTop = getSafeTop(20)
const topBarHeight = getTopBarHeight(76)
const menuButtonSafeRight = getMenuButtonSafeRight(16, 18)
const overlayVisible = computed(() => profileReminderVisible.value || onboardingGuideVisible.value)
const syncOverlayTabBarVisibility = useTabBarOverlayVisibility(overlayVisible)

onShow(async () => {
  currentNow.value = new Date()
  syncCustomTabBar(0)
  syncOverlayTabBarVisibility()
  await loadHomeData()
  checkProfileReminder()
  tryShowOnboardingGuide()
})

const currentDate = computed(() => {
  const now = currentNow.value
  const month = now.getMonth() + 1
  const date = now.getDate()
  const weekDays = ['日', '一', '二', '三', '四', '五', '六']
  return `${month}月${date}日 星期${weekDays[now.getDay()]}`
})

const greetingText = computed(() => {
  const hour = currentNow.value.getHours()
  if (hour < 5) return '夜深了'
  if (hour < 11) return '早安'
  if (hour < 13) return '午安'
  if (hour < 18) return '下午好'
  if (hour < 22) return '晚上好'
  return '夜深了'
})

const headerKickerText = computed(() => {
  const hour = currentNow.value.getHours()
  if (hour < 5) return '夜间简报'
  if (hour < 11) return '晨间简报'
  if (hour < 13) return '午间简报'
  if (hour < 18) return '午后简报'
  return '晚间简报'
})

const dailyQuoteText = computed(() => {
  const today = currentNow.value
  const startOfYear = new Date(today.getFullYear(), 0, 0)
  const dayOfYear = Math.floor((today.getTime() - startOfYear.getTime()) / 86400000)
  return dailyQuotes[dayOfYear % dailyQuotes.length]
})

const summary = computed(() => dashboard.value?.summary ?? null)
const hasNotification = computed(() => (dashboard.value?.unreadNotificationCount || 0) > 0)
const showGuestIntro = computed(() => !isLoggedIn.value && !guestIntroDismissed.value)

const todayStats = computed(() => ({
  weight: summary.value?.currentWeight ?? null,
  bmi: summary.value?.bmi ?? null,
  calories: summary.value?.calorieIntake ?? 0,
  exercise: summary.value?.exerciseCalories ?? 0,
  exerciseMinutes: summary.value?.exerciseMinutes ?? 0
}))

const bmiStatus = computed(() => {
  const bmi = todayStats.value.bmi
  if (!bmi) return '未知'
  if (bmi < 18.5) return '偏瘦'
  if (bmi < 24) return '正常'
  if (bmi < 28) return '超重'
  return '肥胖'
})

const bmiBadgeClass = computed(() => {
  const bmi = todayStats.value.bmi
  if (!bmi) return 'wc-badge--soft'
  if (bmi < 18.5) return 'wc-badge--warning'
  if (bmi < 24) return 'wc-badge--success'
  return 'wc-badge--danger'
})

const targetWeightText = computed(() => {
  const targetWeight = summary.value?.targetWeight ?? userInfo.value?.targetWeight
  return targetWeight ? `${targetWeight} kg` : '--'
})

const headerStatusText = computed(() => {
  const currentWeight = summary.value?.currentWeight
  if (currentWeight) {
    return `当前 ${currentWeight} kg`
  }
  return hasCircle.value ? '今日进度待更新' : '准备开始今天的记录'
})

const profileHintText = computed(() => {
  return targetWeightText.value !== '--' ? `目标 ${targetWeightText.value}` : '完善个人资料'
})
const dailyDeficitTarget = computed(() =>
  summary.value?.dailyCalorieDeficitTarget ?? userInfo.value?.dailyCalorieDeficitTarget ?? 0
)
const goalSummaryText = computed(() => {
  const target = targetWeightText.value !== '--' ? `目标 ${targetWeightText.value}` : '完善个人资料'
  const deficit = Number(dailyDeficitTarget.value || 0)
  return deficit > 0 ? `${target} · 缺口 ${deficit} kcal/天` : target
})
const profileReminderMissingItems = computed(() => listMissingProfileItems(userStore.userInfo))
const hasWechatProfileGap = computed(() => profileReminderMissingItems.value.some((item) => ['微信头像', '微信昵称'].includes(item)))
const hasBasicProfileGap = computed(() => profileReminderMissingItems.value.some((item) => !['微信头像', '微信昵称'].includes(item)))
const profileReminderTitle = computed(() => {
  if (hasWechatProfileGap.value && hasBasicProfileGap.value) {
    return profileReminderIsNewUser.value ? '先补全一下个人资料' : '资料还差几项，顺手补一下'
  }

  if (hasBasicProfileGap.value) {
    return '先完善一下基础资料'
  }

  return '同步一下微信头像和昵称'
})
const profileReminderDescription = computed(() => {
  if (hasWechatProfileGap.value && hasBasicProfileGap.value) {
    return '补充头像昵称和基础资料后，主页展示、数据分析、目标计算和功能推荐会更准确。'
  }

  if (hasBasicProfileGap.value) {
    return '补充基础资料后，后续的数据分析、目标计算和功能推荐会更准确。'
  }

  return '检测到你的头像或昵称还没有同步，更新后个人主页、圈子和排行榜展示会更完整。'
})

const dailyCalorieLimit = computed(() => summary.value?.calorieTarget ?? userInfo.value?.dailyCalorieTarget ?? 0)
const caloriesRemaining = computed(() => {
  const target = Number(dailyCalorieLimit.value || 0)
  if (target <= 0) {
    return 0
  }
  return summary.value?.calorieRemaining ?? Math.max(0, target - Number(todayStats.value.calories || 0))
})
const calorieOverLimit = computed(() => {
  const target = Number(dailyCalorieLimit.value || 0)
  if (target <= 0) {
    return 0
  }
  return Math.max(0, Number(todayStats.value.calories || 0) - target)
})
const calorieReferenceText = computed(() => {
  if (Number(dailyCalorieLimit.value || 0) <= 0) {
    return '完善资料后生成摄入上限'
  }
  if (calorieOverLimit.value > 0) {
    return `已超出 ${calorieOverLimit.value} kcal`
  }
  return `还可摄入 ${caloriesRemaining.value} kcal`
})
const calorieReferenceClass = computed(() => {
  if (Number(dailyCalorieLimit.value || 0) <= 0) {
    return 'muted'
  }
  return calorieOverLimit.value > 0 ? 'danger' : 'accent'
})
const exerciseDeficitContributionText = computed(() => {
  const exerciseCalories = Number(todayStats.value.exercise || 0)
  if (exerciseCalories <= 0) {
    return `${todayStats.value.exerciseMinutes || 0}分钟运动`
  }
  return `额外贡献 ${exerciseCalories} kcal 缺口`
})
const waterCups = computed(() => summary.value?.waterCups ?? 0)
const waterTarget = computed(() => summary.value?.waterTarget ?? userInfo.value?.dailyWaterTarget ?? 8)
const waterProgressRatio = computed(() => {
  const cups = Number(waterCups.value || 0)
  const target = Number(waterTarget.value || 0)
  if (target <= 0) {
    return 0
  }
  return Math.min(Math.max(cups / target, 0), 1)
})
const waterRingStyle = computed(() => {
  const angle = Math.round(waterProgressRatio.value * 360)
  return {
    background: `conic-gradient(from -90deg, #2FB37B 0deg ${angle}deg, rgba(47, 179, 123, 0.16) ${angle}deg 360deg)`
  }
})

const weightTrend = computed(() => {
  const trendItems = dashboard.value?.weightTrend || []
  if (trendItems.length < 2) return 0
  const latest = Number(trendItems[trendItems.length - 1].weight || 0)
  const previous = Number(trendItems[trendItems.length - 2].weight || 0)
  return Number((latest - previous).toFixed(1))
})

const weightTrendInlineText = computed(() => {
  if (!todayStats.value.weight) {
    return '先记一条体重，今天就有参照了'
  }
  if (weightTrend.value < 0) {
    return `比昨天轻了 ${Math.abs(weightTrend.value)}kg`
  }
  if (weightTrend.value > 0) {
    return `比昨天多了 ${weightTrend.value}kg，先看是不是短期波动`
  }
  return '和昨天差不多，继续按今天节奏来'
})

const HOME_TREND_CHART_WIDTH = 360
const HOME_TREND_CHART_HEIGHT = 100

async function addWater() {
  if (!ensureLoggedInForAction()) {
    return
  }
  if (addingWater.value) {
    return
  }

  addingWater.value = true
  try {
    await http.post<WaterRecordPayload>(
      '/api/record/water',
      {
        cups: 1,
        ml: 250
      },
      { showLoading: false }
    )
    await loadHomeData()
    uni.showToast({ title: '已记录 1 杯水', icon: 'success' })
  } catch (error) {
    console.error('提交饮水记录失败:', error)
    showActionError(error, '记录失败')
  } finally {
    addingWater.value = false
  }
}

const hasCircle = computed(() => !!dashboard.value?.mainCircle)
const showCircleGuide = computed(() =>
  !hasCircle.value
  && !dismissingCircleGuide.value
  && Number(userInfo.value?.circleFeatureEnabled ?? 1) !== 0
)
const currentCircle = computed(() => ({
  name: dashboard.value?.mainCircle?.name || ''
}))

const circleMembers = computed(() =>
  (dashboard.value?.mainCircle?.members || []).slice(0, 3).map(member => ({
    userId: member.userId,
    nickname: member.nickname || `用户${member.userId}`,
    avatar: member.avatar || null,
    tone: memberTone(member.role, member.streakDays),
    statusText: memberRoleText(member.role),
    metaText: `连续 ${member.streakDays || 0} 天`
  }))
)

const pkRanking = computed(() =>
  (dashboard.value?.mainCircle?.ranking || []).slice(0, 2).map(item => ({
    nickname: item.nickname || `用户${item.userId}`,
    change: formatRankingSummary(item.completedTaskCount, item.circleCheckins)
  }))
)

function showCircleSwitcher() {
  void openCircleSwitcher()
}

const weekTrendPoints = computed(() => {
  const trendItems = dashboard.value?.weightTrend || []
  if (trendItems.length === 0) {
    return []
  }

  const weights = trendItems.map(item => Number(item.weight || 0)).filter(value => !Number.isNaN(value))
  const min = Math.min(...weights)
  const max = Math.max(...weights)
  const range = max - min

  return trendItems.map((item, index) => {
    const weight = Number(item.weight || 0)
    const ratio = trendItems.length === 1 ? 0.5 : index / (trendItems.length - 1)
    const yRatio = range === 0 ? 0.5 : (weight - min) / range
    return {
      label: item.date || `第${index + 1}天`,
      x: Math.round(ratio * HOME_TREND_CHART_WIDTH),
      y: Math.round(HOME_TREND_CHART_HEIGHT - yRatio * 64)
    }
  })
})

const weekTrendSegments = computed(() => {
  if (weekTrendPoints.value.length < 2) {
    return []
  }
  return weekTrendPoints.value.slice(0, -1).map((point, index) => {
    const nextPoint = weekTrendPoints.value[index + 1]
    const deltaX = nextPoint.x - point.x
    const deltaY = nextPoint.y - point.y
    return {
      left: point.x,
      top: point.y,
      width: Math.sqrt(deltaX * deltaX + deltaY * deltaY),
      angle: (Math.atan2(deltaY, deltaX) * 180) / Math.PI
    }
  })
})

async function loadHomeData() {
  if (!isLoggedIn.value) {
    dashboard.value = null
    homeLoadError.value = ''
    return
  }

  if (!userStore.userInfo) {
    const currentUser = await userStore.fetchUserInfo()
    if (!currentUser) {
      return
    }
  }

  homeLoadError.value = ''
  try {
    dashboard.value = await http.get<DashboardPayload>('/api/dashboard', undefined, { showLoading: false })
  } catch (error) {
    console.error('加载首页数据失败:', error)
    homeLoadError.value = error instanceof Error ? error.message || '请检查服务后重试' : '请检查服务后重试'
  }
}

function showActionError(error: unknown, fallback: string) {
  const title = error instanceof Error && error.message ? error.message : fallback
  uni.showToast({
    title: title.length > 12 ? fallback : title,
    icon: 'none'
  })
}

function checkProfileReminder() {
  if (!isLoggedIn.value) {
    profileReminderVisible.value = false
    return
  }

  const reminderIntent = consumeProfileReminderIntent()
  if (!reminderIntent) {
    profileReminderVisible.value = false
    return
  }

  profileReminderIsNewUser.value = Boolean(reminderIntent.isNewUser)
  profileReminderVisible.value = shouldPromptProfileSetup(userStore.userInfo, { isNewUser: reminderIntent.isNewUser })
}

function tryShowOnboardingGuide() {
  if (!isLoggedIn.value) {
    onboardingGuideVisible.value = false
    return
  }

  if (profileReminderVisible.value || onboardingGuideVisible.value) {
    return
  }
  const userId = userStore.userInfo?.userId
  if (!shouldPromptOnboardingGuide(userId)) {
    return
  }
  onboardingGuideStep.value = getOnboardingGuideStep(userId)
  onboardingGuideVisible.value = true
}

function closeOnboardingGuide() {
  onboardingGuideVisible.value = false
}

function disableOnboardingGuideAndClose() {
  disableOnboardingGuide(userStore.userInfo?.userId)
  onboardingGuideVisible.value = false
  uni.showToast({ title: '已关闭新手提示', icon: 'none' })
}

function hideOnboardingGuideForAction(nextStep?: OnboardingGuideStep) {
  if (nextStep) {
    setOnboardingGuideStep(userStore.userInfo?.userId, nextStep)
    onboardingGuideStep.value = nextStep
  }
  onboardingGuideVisible.value = false
}

function handleOnboardingPrimary() {
  if (onboardingGuideStep.value === 'profile') {
    hideOnboardingGuideForAction('record')
    queueProfileEditorIntent('profile', 'login-reminder')
    uni.switchTab({ url: '/pages/profile/index' })
    return
  }

  if (onboardingGuideStep.value === 'record') {
    hideOnboardingGuideForAction('record')
    goRecord('weight')
    return
  }

  completeOnboardingGuide(userStore.userInfo?.userId)
  onboardingGuideVisible.value = false
  uni.switchTab({ url: '/pages/analytics/index' })
}

function skipOnboardingStep() {
  if (onboardingGuideStep.value === 'explore') {
    completeOnboardingGuide(userStore.userInfo?.userId)
    onboardingGuideVisible.value = false
    uni.switchTab({ url: '/pages/circle/index' })
    return
  }

  const nextStep = advanceOnboardingGuideStep(userStore.userInfo?.userId)
  if (!nextStep) {
    onboardingGuideVisible.value = false
    return
  }
  onboardingGuideStep.value = nextStep
}

function selectOnboardingStep(step: OnboardingGuideStep) {
  setOnboardingGuideStep(userStore.userInfo?.userId, step)
  onboardingGuideStep.value = step
}

function formatRankingSummary(completedTaskCount?: number | null, circleCheckins?: number | null) {
  const completed = Number(completedTaskCount || 0)
  const checkins = Number(circleCheckins || 0)
  if (completed <= 0 && checkins <= 0) {
    return '等待首个打卡'
  }
  return `${completed} 项任务 / ${checkins} 次打卡`
}

function memberRoleText(role?: string | null) {
  return ['0', 'admin'].includes(String(role || '')) ? '管理员' : '成员'
}

function memberTone(role?: string | null, streakDays?: number | null) {
  if (['0', 'admin'].includes(String(role || ''))) {
    return 'leader'
  }
  if ((streakDays || 0) > 0) {
    return 'active'
  }
  return 'neutral'
}

async function openCircleSwitcher() {
  if (!ensureLoggedInForAction()) {
    return
  }

  try {
    const page = await getMyCircles({ pageNum: 1, pageSize: 100 })
    const circles = page.list || []
    if (!circles.length) {
      uni.showToast({ title: '还没有可切换的圈子', icon: 'none' })
      return
    }

    const itemList = circles.map((item) =>
      item.id === userStore.userInfo?.mainCircleId ? `✓ ${item.name}` : item.name
    )
    uni.showActionSheet({
      itemList,
      success: async (res) => {
        const selected = circles[res.tapIndex]
        if (!selected || selected.id === userStore.userInfo?.mainCircleId) {
          return
        }
        try {
          await userStore.updateUserInfo({ mainCircleId: selected.id })
          uni.showToast({ title: `已切换到${selected.name}`, icon: 'success' })
          await loadHomeData()
        } catch (error) {
          console.error('切换主圈子失败', error)
          showActionError(error, '切换失败')
        }
      }
    })
  } catch (error) {
    console.error('加载可切换圈子失败', error)
    showActionError(error, '圈子加载失败')
  }
}

function goNotifications() {
  if (!ensureLoggedInForAction()) {
    return
  }
  uni.navigateTo({ url: '/pages/notifications/index' })
}

function goProfile() {
  if (!ensureLoggedInForAction()) {
    return
  }
  uni.switchTab({ url: '/pages/profile/index' })
}

function goAnalytics() {
  uni.switchTab({ url: '/pages/analytics/index' })
}

function goRecord(tab: string) {
  if (!ensureLoggedInForAction()) {
    return
  }
  uni.navigateTo({ url: `/pages/record/index?tab=${tab}` })
}

function goCircleDetail() {
  const mainCircleId = dashboard.value?.mainCircle?.id
  if (!mainCircleId) {
    uni.navigateTo({ url: '/pages/circle-all/index' })
    return
  }
  uni.navigateTo({ url: `/pages/circle-private/index?circleId=${mainCircleId}` })
}

function goCreateCircle() {
  if (!ensureLoggedInForAction()) {
    return
  }
  uni.navigateTo({ url: '/pages/circle-create/index' })
}

function goBrowseCircle() {
  uni.navigateTo({ url: '/pages/circle-all/index' })
}

async function skipGuide() {
  if (!isLoggedIn.value) {
    dismissingCircleGuide.value = true
    return
  }

  if (dismissingCircleGuide.value) {
    return
  }

  dismissingCircleGuide.value = true
  try {
    await userStore.updateUserInfo({ circleFeatureEnabled: 0 })
    uni.showToast({ title: '已隐藏首页圈子引导', icon: 'none' })
  } catch (error) {
    dismissingCircleGuide.value = false
    console.error('更新圈子引导偏好失败', error)
    showActionError(error, '操作失败')
  }
}

function closeProfileReminder() {
  profileReminderVisible.value = false
  tryShowOnboardingGuide()
}

function disableReminderAndClose() {
  disableProfileReminder(userStore.userInfo?.userId)
  profileReminderVisible.value = false
  tryShowOnboardingGuide()
}

function goCompleteProfile() {
  if (!ensureLoggedInForAction()) {
    return
  }
  queueProfileEditorIntent('profile', 'login-reminder')
  profileReminderVisible.value = false
  uni.switchTab({ url: '/pages/profile/index' })
}

function ensureLoggedInForAction() {
  if (isLoggedIn.value) {
    return true
  }
  openLoginPage('safe')
  return false
}

function goLoginFromGuest() {
  openLoginPage('safe', '/pages/index/index')
}

function continueGuestBrowsing() {
  guestIntroDismissed.value = true
}
</script>

<style lang="scss" scoped>
// 原型精确还原设计规范
$primary: var(--wc-primary);
$accent: var(--wc-success);
$danger: var(--wc-danger);
$warning: var(--wc-warning);
$bg-page: var(--wc-bg);
$card-bg: var(--wc-surface-strong);
$text-main: var(--wc-text);
$text-sub: var(--wc-text-soft);
$shadow: var(--wc-shadow-soft);
$radius-l: var(--wc-radius-xl);
$radius-m: var(--wc-radius-l);
$radius-s: var(--wc-radius-m);

@use '../../styles/flow-button.scss';

.home-container {
  height: 100vh;
  background: $bg-page;
  display: flex;
  flex-direction: column;
  width: 100%;
  max-width: 100vw;
  overflow-x: hidden;
  box-sizing: border-box;
}

// 顶部导航
.header {
  padding: 0 30rpx 18rpx;
}

.header-body {
  display: flex;
  align-items: flex-end;
}

.header-copy {
  display: flex;
  flex-direction: column;
  gap: 10rpx;
  min-width: 0;
}

.header-kicker {
  font-size: 20rpx;
  letter-spacing: 0;
  text-transform: uppercase;
  color: rgba(97, 113, 106, 0.76);
}

.user-welcome {
  font-size: 44rpx;
  line-height: 1.08;
  font-weight: 800;
  letter-spacing: 0;
}

.header-meta {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 12rpx;
}

.date-display {
  font-size: 24rpx;
  color: $text-sub;
  font-weight: normal;
}

.header-divider {
  font-size: 20rpx;
  color: rgba(95, 107, 122, 0.5);
}

.header-status {
  padding: 6rpx 14rpx;
  border-radius: 999rpx;
  background: rgba(232, 248, 241, 0.98);
  color: var(--wc-primary-strong);
  font-size: 20rpx;
  font-weight: 600;
}

.daily-quote {
  max-width: 100%;
  font-size: 23rpx;
  line-height: 1.48;
  color: rgba(83, 101, 93, 0.9);
}

.header-tools {
  margin-top: 18rpx;
  display: flex;
  align-items: center;
  gap: 14rpx;
}

.notification-chip {
  position: relative;
  gap: 12rpx;
  padding: 0 22rpx;
  flex-shrink: 0;
}

.chip-icon {
  font-size: 30rpx;
}

.chip-text {
  font-size: 24rpx;
  font-weight: 600;
  color: $text-main;
}

.notification-badge {
  position: absolute;
  top: 14rpx;
  right: 16rpx;
  width: 16rpx;
  height: 16rpx;
  background: $danger;
  border-radius: 50%;
  border: 4rpx solid #ffffff;
}

.profile-chip {
  flex: 1;
  min-width: 0;
  gap: 14rpx;
  padding: 10rpx 12rpx;
}

.profile-chip-avatar {
  width: 56rpx;
  height: 56rpx;
  border-radius: 50%;
  background: var(--wc-primary-soft);
  border: 1px solid rgba(47, 179, 123, 0.12);
  overflow: hidden;
  flex-shrink: 0;
}

.profile-chip-avatar--fallback,
.buddy-avatar--fallback {
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #e8f8f1, #fff4dd);
  color: #168a62;
  font-weight: 700;
}

.profile-chip-copy {
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 4rpx;
}

.profile-chip-name {
  font-size: 24rpx;
  font-weight: 700;
  color: $text-main;
}

.profile-chip-note {
  font-size: 20rpx;
  color: $text-sub;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

// 滚动区域
.scroll-content {
  padding-left: 24rpx;
  padding-right: 24rpx;
  width: 100%;
}

.home-load-banner {
  margin: 0 0 24rpx;
  padding: 20rpx 24rpx;
  border-radius: 28rpx;
  background: rgba(255, 244, 224, 0.92);
  border: 1px solid rgba(230, 149, 75, 0.18);
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20rpx;
}

.home-load-banner__copy {
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 6rpx;
}

.home-load-banner__title {
  font-size: 24rpx;
  font-weight: 700;
  color: #8f4d18;
}

.home-load-banner__desc {
  font-size: 21rpx;
  line-height: 1.5;
  color: #ad6a33;
}

.home-load-banner__action {
  flex-shrink: 0;
}

.guest-intro-card {
  margin: 0 0 24rpx;
  padding: 28rpx;
  border-radius: 30rpx;
  background: linear-gradient(135deg, rgba(232, 248, 241, 0.96), rgba(255, 250, 240, 0.92));
  border: 1px solid rgba(47, 179, 123, 0.12);
  box-shadow: var(--wc-shadow-soft);
}

.guest-intro-copy {
  display: flex;
  flex-direction: column;
  gap: 10rpx;
}

.guest-intro-kicker {
  font-size: 21rpx;
  font-weight: 700;
  color: var(--wc-primary-strong);
}

.guest-intro-title {
  font-size: 32rpx;
  line-height: 1.28;
  font-weight: 800;
  color: $text-main;
}

.guest-intro-desc {
  font-size: 24rpx;
  line-height: 1.55;
  color: $text-sub;
}

.guest-intro-actions {
  margin-top: 24rpx;
  display: flex;
  gap: 16rpx;
}

.guest-intro-btn {
  flex: 1;
  min-width: 0;
}

// 通用卡片
.card {
  background: $card-bg;
  border-radius: $radius-m;
  padding: 30rpx;
  margin-bottom: 32rpx;
  border: 1px solid rgba(39, 92, 72, 0.055);
  box-shadow: none;
  position: relative;
  overflow: hidden;
  box-sizing: border-box;
  width: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30rpx;
}

.card-title {
  font-size: 32rpx;
  font-weight: 700;
  color: $text-main;
}

.card-more {
  font-size: 24rpx;
  color: $primary;
  font-weight: 600;
}

.today-brief {
  display: flex;
  flex-direction: column;
  gap: 22rpx;
}

.today-weight {
  min-height: 188rpx;
  border-radius: 30rpx;
  padding: 30rpx;
  background:
    radial-gradient(circle at 86% 12%, rgba(255, 238, 210, 0.9), transparent 38%),
    linear-gradient(135deg, rgba(232, 248, 241, 0.96), rgba(255, 251, 243, 0.98));
  border: 1px solid rgba(47, 179, 123, 0.1);
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20rpx;
}

.today-weight__copy {
  display: flex;
  flex-direction: column;
  gap: 8rpx;
  min-width: 0;
}

.today-weight__label {
  font-size: 24rpx;
  color: $text-sub;
}

.today-weight__value-row {
  display: flex;
  align-items: baseline;
}

.today-weight__value {
  font-size: 62rpx;
  line-height: 1;
  font-weight: 800;
  color: $text-main;
}

.today-weight__unit {
  margin-left: 8rpx;
  font-size: 26rpx;
  color: $text-sub;
}

.today-weight__trend {
  font-size: 24rpx;
  line-height: 1.45;
  color: rgba(83, 101, 93, 0.92);

  &.down {
    color: $accent;
  }

  &.up {
    color: $danger;
  }
}

.today-weight__mark {
  width: 72rpx;
  height: 72rpx;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.72);
  color: rgba(47, 179, 123, 0.72);
  font-size: 48rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.today-note-list {
  display: flex;
  flex-direction: column;
  gap: 2rpx;
}

.today-note {
  min-height: 86rpx;
  padding: 18rpx 4rpx;
  border-bottom: 1px solid rgba(39, 92, 72, 0.07);
  display: grid;
  grid-template-columns: 132rpx minmax(112rpx, auto) 1fr;
  align-items: center;
  column-gap: 18rpx;

  &:last-child {
    border-bottom: none;
  }
}

.today-note__label {
  font-size: 24rpx;
  color: $text-sub;
}

.today-note__value {
  font-size: 28rpx;
  font-weight: 800;
  color: $text-main;
  white-space: nowrap;
}

.today-note__meta {
  min-width: 0;
  font-size: 22rpx;
  line-height: 1.4;
  color: var(--wc-text-faint);
  text-align: right;

  &.accent {
    color: $accent;
  }

  &.danger {
    color: $danger;
  }

  &.muted {
    color: var(--wc-text-faint);
  }
}

// 今日摘要
.summary-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 22rpx;
}

.summary-item {
  text-align: left;
  padding: 22rpx;
  background: linear-gradient(180deg, rgba(246, 251, 247, 0.98), rgba(255, 255, 255, 0.98));
  border-radius: 26rpx;
  border: 1px solid rgba(39, 92, 72, 0.06);
}

.summary-label {
  font-size: 24rpx;
  color: $text-sub;
  margin-bottom: 8rpx;
  display: block;
}

.summary-label-row {
  display: flex;
  justify-content: flex-start;
  align-items: center;
  gap: 10rpx;
  margin-bottom: 8rpx;
}

.summary-value-row {
  display: flex;
  justify-content: flex-start;
  align-items: baseline;
  margin: 4rpx 0;
}

.summary-value {
  font-size: 40rpx;
  font-weight: 800;
  color: $text-main;
}

.summary-unit {
  font-size: 24rpx;
  font-weight: normal;
  color: $text-sub;
  margin-left: 4rpx;
}

.weight-trend {
  font-size: 20rpx;
  display: block;
  margin-top: 4rpx;
  &.down { color: $accent; }
  &.up { color: $danger; }
}

.summary-sub {
  font-size: 20rpx;
  color: var(--wc-text-faint);
  display: block;
  &.accent { color: $accent; }
  &.danger { color: $danger; }
  &.muted { color: var(--wc-text-faint); }
}

// 快捷入口
.section-label {
  font-size: 28rpx;
  color: $text-sub;
  font-weight: 600;
  margin-bottom: 20rpx;
  margin-left: 10rpx;
  display: block;
}

.quick-actions-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20rpx;
  margin-bottom: 34rpx;
  padding: 18rpx 10rpx;
  border-radius: 30rpx;
  background: rgba(255, 255, 255, 0.62);
  border: 1px solid rgba(39, 92, 72, 0.045);
}

.action-btn {
}

.action-icon-bg {
  &.diet { background: linear-gradient(180deg, rgba(232, 248, 241, 0.98), rgba(255, 255, 255, 0.98)); }
  &.weight { background: linear-gradient(180deg, rgba(255, 244, 221, 0.96), rgba(255, 255, 255, 0.98)); }
  &.food { background: linear-gradient(180deg, rgba(232, 248, 241, 0.92), rgba(255, 255, 255, 0.98)); }
  &.habit { background: linear-gradient(180deg, rgba(239, 246, 255, 0.84), rgba(255, 255, 255, 0.98)); }
}

.action-label {
}

// 好友圈卡片
.buddy-card {
  background: linear-gradient(135deg, rgba(232, 248, 241, 0.95), rgba(255, 250, 240, 0.88));
  border: 1px solid rgba(47, 179, 123, 0.12);
}

.circle-switch-btn {
  display: flex;
  align-items: center;
  gap: 8rpx;
}

.buddy-members {
  display: flex;
  justify-content: space-around;
  margin-bottom: 30rpx;
}

.buddy-member {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8rpx;
}

.buddy-avatar {
  width: 96rpx;
  height: 96rpx;
  border-radius: 50%;
  border: 6rpx solid rgba(255, 255, 255, 0.9);
  margin-bottom: 12rpx;
}

.buddy-member.leader .buddy-avatar {
  border-color: $accent;
}

.buddy-member.active .buddy-avatar {
  border-color: $primary;
}

.buddy-member.neutral .buddy-avatar {
  border-color: rgba(39, 92, 72, 0.08);
  opacity: 0.78;
}

.buddy-name {
  font-size: 24rpx;
  color: $text-main;
}

.buddy-status {
  min-width: 72rpx;
  padding: 8rpx 16rpx;
  border-radius: 18rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20rpx;
  color: white;
  font-weight: bold;
}

.buddy-member.leader .buddy-status {
  background: $accent;
}

.buddy-member.active .buddy-status {
  background: $primary;
}

.buddy-member.neutral .buddy-status {
  background: rgba(15, 23, 42, 0.45);
}

.buddy-meta {
  font-size: 20rpx;
  color: $text-sub;
}

.buddy-pk {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 40rpx;
  padding: 24rpx;
  background: var(--wc-surface-muted);
  border-radius: 24rpx;
  border: 1px solid var(--wc-line);
}

.pk-item {
  text-align: center;
}

.pk-name {
  font-size: 24rpx;
  color: $text-sub;
  display: block;
}

.pk-value {
  font-size: 32rpx;
  font-weight: 700;
  color: $text-main;
}

.pk-item.winner .pk-value {
  color: $accent;
}

.pk-vs {
  font-size: 40rpx;
}

// 圈子引导卡片
.circle-guide-card {
  text-align: center;
  padding: 48rpx !important;
}

.guide-icon {
  font-size: 80rpx;
  margin-bottom: 24rpx;
  display: block;
}

.guide-title {
  font-size: 32rpx;
  font-weight: 700;
  margin-bottom: 12rpx;
  display: block;
}

.guide-desc {
  font-size: 26rpx;
  color: $text-sub;
  margin-bottom: 32rpx;
  display: block;
}

.guide-actions {
  display: flex;
  gap: 20rpx;
  justify-content: center;
  margin-bottom: 24rpx;
}

.guide-btn {
  font-size: 26rpx;
  font-weight: 600;
  flex: 1;
}

.guide-skip {
  font-size: 24rpx;
  color: var(--wc-text-faint);
}

// 饮水卡片
.water-card {
  display: flex;
  align-items: center;
  background: linear-gradient(135deg, rgba(232, 248, 241, 0.96), rgba(255, 255, 255, 0.98));
}

.water-progress {
  position: relative;
  width: 160rpx;
  height: 160rpx;
  margin-right: 40rpx;
  flex-shrink: 0;
}

.water-ring {
  position: absolute;
  inset: 0;
  border-radius: 50%;
}

.water-circle {
  position: absolute;
  inset: 12rpx;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.96);
  box-shadow: inset 0 0 0 2rpx rgba(47, 179, 123, 0.08);
  display: flex;
  align-items: center;
  justify-content: center;
}

.water-icon {
  font-size: 48rpx;
  color: $primary;
}

.water-text {
  flex: 1;
}

.water-count-row {
  display: flex;
  align-items: baseline;
  margin: 8rpx 0;
}

.water-count {
  font-size: 48rpx;
  font-weight: bold;
  color: $text-main;
}

.water-target-unit {
  font-size: 28rpx;
  font-weight: normal;
  color: $text-sub;
}

.water-tip {
  font-size: 24rpx;
  color: $text-sub;
  display: block;
}

.btn-add-water {
  font-size: 48rpx;
}

// 趋势图
.home-trend-card {
  display: block;
}

.home-trend-chart {
  position: relative;
  width: 360rpx;
  height: 128rpx;
  margin: 0 auto;
}

.home-trend-point-group {
  position: static;
}

.home-trend-segment {
  position: absolute;
  height: 4rpx;
  transform-origin: left center;
  background: linear-gradient(90deg, rgba(47, 179, 123, 0.38), rgba(47, 179, 123, 0.88));
  border-radius: 999rpx;
}

.home-trend-point {
  position: absolute;
  width: 14rpx;
  height: 14rpx;
  margin-left: -7rpx;
  margin-top: -7rpx;
  border-radius: 50%;
  background: white;
  border: 3rpx solid rgba(47, 179, 123, 0.58);
  box-sizing: border-box;
}

.home-trend-point.active {
  width: 18rpx;
  height: 18rpx;
  margin-left: -9rpx;
  margin-top: -9rpx;
  border-color: $primary;
  box-shadow: 0 0 0 8rpx rgba(47, 179, 123, 0.1);
}

.home-trend-label {
  position: absolute;
  top: 110rpx;
  transform: translateX(-50%);
  font-size: 18rpx;
  color: $text-sub;
  white-space: nowrap;
}

.trend-empty {
  padding: 36rpx 0;
  text-align: center;
  font-size: 24rpx;
  color: $text-sub;
}

// 悬浮记录按钮
.fab-record {
  position: fixed;
  bottom: 180rpx;
  right: 40rpx;
  width: 112rpx;
  height: 112rpx;
  background: linear-gradient(135deg, #1fa873, #35c58d);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8rpx 32rpx rgba(47, 179, 123, 0.3);
  z-index: 99;
}

.fab-icon {
  font-size: 56rpx;
  color: white;
  font-weight: 300;
  line-height: 1;
}

// 记录菜单弹窗
.action-sheet {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(20, 32, 28, 0.42);
  z-index: 1000;
  display: none;
  opacity: 0;
  transition: opacity 0.3s;
  flex-direction: column;
  justify-content: flex-end;
}

.action-sheet.show {
  display: flex;
  opacity: 1;
}

.action-sheet-content {
  background: rgba(255, 255, 255, 0.98);
  border-top-left-radius: 48rpx;
  border-top-right-radius: 48rpx;
  padding: 60rpx 40rpx;
  transform: translateY(0);
  box-shadow: 0 -10rpx 30rpx rgba(39, 92, 72, 0.12);
}

.action-sheet-header {
  font-size: 36rpx;
  font-weight: bold;
  text-align: center;
  margin-bottom: 40rpx;
  color: $text-main;
}

.action-item {
  background: var(--wc-surface-muted);
  border: 1px solid var(--wc-line);
  border-radius: 32rpx;
  padding: 30rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  margin-bottom: 20rpx;
}

.action-item.primary {
  background: linear-gradient(135deg, rgba(232, 248, 241, 0.98), rgba(255, 250, 240, 0.92));
  flex-direction: row;
  justify-content: flex-start;
  gap: 30rpx;
  padding: 40rpx;
}

.action-icon {
  width: 80rpx;
  height: 80rpx;
  border-radius: 24rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 40rpx;
  background: white;
}

.action-info {
  display: flex;
  flex-direction: column;
}

.action-item.primary .action-name {
  font-size: 36rpx;
  font-weight: bold;
  color: $text-main;
}

.action-desc {
  font-size: 24rpx;
  color: $text-sub;
  margin-top: 8rpx;
}

.action-sub-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20rpx;
}

.action-sub-grid .action-item {
  height: 160rpx;
}

.action-sub-grid .action-name {
  font-size: 28rpx;
  margin-top: 16rpx;
  color: $text-main;
}

.action-sheet-close {
  margin-top: 40rpx;
  text-align: center;
  font-size: 48rpx;
  color: var(--wc-text-faint);
  padding: 20rpx;
}

.page-bottom-space {
  height: 120rpx;
}

.profile-reminder-mask {
  position: fixed;
  inset: 0;
  z-index: 1100;
  display: flex;
  align-items: flex-end;
  padding: 24rpx;
  background: rgba(15, 23, 42, 0.32);
}

.profile-reminder-card {
  width: 100%;
  padding: 32rpx;
  border-radius: 36rpx;
  background: rgba(255, 255, 255, 0.98);
  box-shadow: 0 18rpx 48rpx rgba(15, 23, 42, 0.18);
}

.profile-reminder-header {
  display: flex;
  flex-direction: column;
  gap: 12rpx;
}

.profile-reminder-title {
  font-size: 34rpx;
  font-weight: 700;
  color: #18202d;
}

.profile-reminder-subtitle {
  font-size: 24rpx;
  line-height: 1.6;
  color: #5f6b7a;
}

.profile-reminder-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 12rpx;
  margin-top: 24rpx;
}

.profile-reminder-tag {
  padding: 10rpx 18rpx;
  border-radius: 999rpx;
  background: #edf5ff;
  color: #2e6fc8;
  font-size: 22rpx;
  font-weight: 600;
}

.profile-reminder-tip {
  display: block;
  margin-top: 20rpx;
  font-size: 22rpx;
  line-height: 1.6;
  color: #8b95a4;
}

.profile-reminder-actions {
  display: flex;
  flex-direction: column;
  gap: 14rpx;
  margin-top: 28rpx;
}

.profile-reminder-btn {
  width: 100%;
  height: 88rpx;
  line-height: 88rpx;
  font-size: 28rpx;
  font-weight: 600;
}

.profile-reminder-btn--primary {
  background: #3b82f6;
  color: #ffffff;
  box-shadow: 0 12rpx 28rpx rgba(59, 130, 246, 0.2);
}

.profile-reminder-btn--secondary {
  background: rgba(255, 255, 255, 0.92);
  color: #18202d;
  border: 2rpx solid rgba(39, 92, 72, 0.08);
}

.profile-reminder-btn--ghost {
  background: transparent;
  color: #5f6b7a;
  border: 2rpx solid rgba(39, 92, 72, 0.08);
  box-shadow: none;
}
</style>
