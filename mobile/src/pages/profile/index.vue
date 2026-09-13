<template>
  <view class="profile-container wc-page-enter">
    <!-- 固定顶部导航栏 (仿首页样式) -->
    <view class="header wc-tab-header" :style="{ paddingTop: `${safeTop}px` }">
      <view class="header-body wc-tab-header__body" :style="{ minHeight: `${topBarHeight - safeTop}px`, paddingRight: `${menuButtonSafeRight}px` }">
        <view class="header-left">
          <text class="page-title">我的</text>
          <text class="sub-title">个人中心</text>
        </view>
      </view>
    </view>

    <scroll-view
      class="content wc-tab-scroll wc-tab-scroll--top-gap wc-section-enter"
      scroll-y
      :style="{ height: `calc(100vh - ${topBarHeight}px)` }"
    >
      <!-- 顶部头像区 -->
      <view class="profile-header">
        <resolved-image
          class="avatar-large"
          :src="userInfo?.avatar"
          mode="aspectFill"
          shape="avatar"
          fallback-class="avatar-large avatar-large--fallback"
          :fallback-text="initialText(userInfo?.nickname, '我')"
        />
        <view class="user-info">
          <text class="nickname">{{ userInfo?.nickname || '未登录用户' }}</text>
          <text class="user-id">{{ profileSubtitleText }}</text>
          <view class="user-meta-chips">
            <text class="wc-badge wc-badge--soft user-meta-chip">{{ genderText(userInfo?.gender) }}</text>
            <text class="wc-badge wc-badge--soft user-meta-chip">{{ userInfo?.birthday || '未设置生日' }}</text>
            <text class="wc-badge wc-badge--soft user-meta-chip">{{ sourceText(userInfo?.source) }}</text>
          </view>
        </view>
      </view>

      <!-- 统计数据 -->
      <view class="user-stats">
        <view class="stat-item">
          <text class="stat-val">{{ userInfo?.streakDays || 0 }}</text>
          <text class="stat-label">连续记录</text>
        </view>
        <view class="stat-item">
          <text class="stat-val">{{ weightGap }}</text>
          <text class="stat-label">离目标kg</text>
        </view>
        <view class="stat-item">
          <text class="stat-val">{{ earnedBadgeCount }}</text>
          <text class="stat-label">小成就</text>
        </view>
      </view>

      <!-- 账户摘要 -->
      <view class="badges-section">
        <view class="section-header">
          <text class="section-title">我的成就</text>
          <text class="view-all wc-inline-link wc-inline-link--action wc-inline-link--strong" @tap="goAchievements">查看全部 ›</text>
        </view>
        <view class="badge-list">
          <view v-for="badge in achievementPreview" :key="badge.key" class="badge" :class="{ 'badge--locked': !badge.unlocked }">
            {{ badge.icon }}
          </view>
        </view>
      </view>

      <!-- 身体信息 -->
      <view class="body-info-card">
        <view class="body-info-header">
          <text>📋 个人资料</text>
          <text class="edit-btn wc-inline-link wc-inline-link--action wc-inline-link--strong" @tap="goSettings">编辑</text>
        </view>
        <view class="body-info-grid">
          <view class="body-info-item">
            <text class="body-info-label">身高</text>
            <text class="body-info-value">{{ userInfo?.height || '--' }} <text class="unit">cm</text></text>
          </view>
          <view class="body-info-item">
            <text class="body-info-label">当前体重</text>
            <text class="body-info-value">{{ userInfo?.currentWeight || '--' }} <text class="unit">kg</text></text>
          </view>
          <view class="body-info-item">
            <text class="body-info-label">目标体重</text>
            <text class="body-info-value">{{ userInfo?.targetWeight || '--' }} <text class="unit">kg</text></text>
          </view>
          <view class="body-info-item">
            <text class="body-info-label">目标日期</text>
            <text class="body-info-value body-info-value--text">{{ userInfo?.targetCompletionDate || '--' }}</text>
          </view>
          <view class="body-info-item">
            <text class="body-info-label">摄入上限</text>
            <text class="body-info-value">{{ userDailyCalorieTargetText }} <text v-if="userDailyCalorieTargetText !== '--'" class="unit">kcal</text></text>
          </view>
          <view class="body-info-item">
            <text class="body-info-label">每日缺口</text>
            <text class="body-info-value">{{ userDailyDeficitText }} <text class="unit">kcal</text></text>
          </view>
          <view class="body-info-item">
            <text class="body-info-label">BMI</text>
            <text class="body-info-value">
              {{ userInfo?.bmi || '--' }} 
              <text class="wc-badge bmi-tag" :class="bmiClass">{{ bmiStatus }}</text>
            </text>
          </view>
        </view>
      </view>

      <!-- 常用功能 -->
      <view class="menu-list">
        <view class="menu-item wc-pressable" @tap="goHabit">
          <text class="menu-icon">✅</text>
          <text class="menu-text">习惯管理</text>
          <text class="menu-arrow">›</text>
        </view>
        <view class="menu-item wc-pressable" @tap="openOnboardingGuide">
          <text class="menu-icon">🧭</text>
          <text class="menu-text">新手指南</text>
          <text class="menu-note">3 步上手</text>
          <text class="menu-arrow">›</text>
        </view>
        <view class="menu-item wc-pressable" @tap="goMyCircles">
          <text class="menu-icon">⭕</text>
          <text class="menu-text">我的圈子</text>
          <text class="wc-badge wc-badge--primary menu-badge">{{ userInfo?.mainCircleId ? '已加入' : '未加入' }}</text>
          <text class="menu-arrow">›</text>
        </view>
        <view class="menu-item wc-pressable" @tap="goReminder">
          <text class="menu-icon">🔔</text>
          <text class="menu-text">消息通知</text>
          <text class="menu-note">互动、系统提醒</text>
          <text class="menu-arrow">›</text>
        </view>
        <view class="menu-item wc-pressable" @tap="goFeedback">
          <text class="menu-icon">💬</text>
          <text class="menu-text">建议 / Bug 反馈</text>
          <text class="menu-note">问题、建议、体验</text>
          <text class="menu-arrow">›</text>
        </view>
      </view>

      <!-- 退出登录 -->
      <view class="logout-section" v-if="isLoggedIn">
        <button class="btn-logout flow-btn flow-btn--secondary flow-btn--wide wc-pressable" @tap="handleLogout">退出登录</button>
      </view>

      <view class="page-bottom-space"></view>
    </scroll-view>

    <view v-if="editorVisible" class="editor-mask" @tap="closeProfileEditor">
      <view class="editor-modal" @tap.stop>
        <view class="editor-header">
          <view class="editor-title-wrap">
            <text class="editor-title">{{ editorTitle }}</text>
            <text class="editor-subtitle">{{ editorSubtitle }}</text>
          </view>
          <text class="editor-close wc-inline-link wc-inline-link--action wc-inline-link--subtle" @tap="closeProfileEditor">关闭</text>
        </view>

        <view class="editor-scroll">
          <view class="editor-card">
            <text class="editor-section-title">基础资料</text>

            <view class="form-group">
              <text class="wc-form-label">头像</text>
              <view class="avatar-picker-row">
                <resolved-image
                  class="avatar-picker-preview"
                  :src="profileAvatarPreview || profileForm.avatar"
                  mode="aspectFill"
                  shape="avatar"
                  fallback-class="avatar-picker-preview avatar-picker-preview--fallback"
                  :fallback-text="initialText(profileForm.nickname, '我')"
                />
                <view class="avatar-picker-copy">
                  <text class="avatar-picker-title">{{ profileForm.avatar ? '已同步头像' : '还没有头像' }}</text>
                  <text class="avatar-picker-tip">建议同步微信头像，个人主页和圈子动态会直接使用，单张不超过 {{ getUploadSizeLimitText() }}。</text>
                </view>
                <!-- #ifdef MP-WEIXIN -->
                <button
                  class="avatar-picker-btn flow-btn flow-btn--secondary flow-btn--compact"
                  open-type="chooseAvatar"
                  :disabled="uploadingAvatar"
                  @chooseavatar="onChooseAvatar"
                >
                  {{ uploadingAvatar ? '上传中...' : '使用微信头像' }}
                </button>
                <!-- #endif -->
                <!-- #ifndef MP-WEIXIN -->
                <button class="avatar-picker-btn flow-btn flow-btn--secondary flow-btn--compact" @tap="showComingSoon('头像上传')">更换头像</button>
                <!-- #endif -->
              </view>
            </view>

            <view class="form-group">
              <text class="wc-form-label">昵称</text>
              <!-- #ifdef MP-WEIXIN -->
              <input
                v-model="profileForm.nickname"
                class="wc-form-input"
                type="nickname"
                maxlength="20"
                placeholder="请输入昵称"
                placeholder-style="color:#9aa4b2;"
              />
              <!-- #endif -->
              <!-- #ifndef MP-WEIXIN -->
              <input
                v-model="profileForm.nickname"
                class="wc-form-input"
                maxlength="20"
                placeholder="请输入昵称"
                placeholder-style="color:#9aa4b2;"
              />
              <!-- #endif -->
            </view>

            <view class="form-group">
              <text class="wc-form-label">性别</text>
              <view class="editor-tab-rail wc-tab-rail wc-tab-rail--compact">
                <view
                  v-for="option in genderOptions"
                  :key="option.value"
                  class="wc-tab-item wc-tab-item--compact wc-tab-item--full wc-pressable"
                  :class="{ active: profileForm.gender === option.value }"
                  @tap="profileForm.gender = option.value"
                >
                  {{ option.label }}
                </view>
              </view>
            </view>

            <view class="form-group">
              <text class="wc-form-label">生日</text>
              <picker mode="date" :value="profileForm.birthday || todayDate" start="1960-01-01" :end="todayDate" @change="onBirthdayChange">
                <view class="wc-form-input picker-field">
                  <text :class="{ 'picker-placeholder': !profileForm.birthday }">{{ profileForm.birthday || '请选择生日' }}</text>
                  <text class="picker-arrow">›</text>
                </view>
              </picker>
            </view>
          </view>

          <view class="editor-card">
            <text class="editor-section-title">身体与目标</text>

            <view class="form-grid">
              <view class="form-group">
                <text class="wc-form-label">身高 (cm)</text>
                <input
                  v-model="profileForm.height"
                  type="digit"
                  class="wc-form-input"
                  placeholder="170"
                  placeholder-style="color:#9aa4b2;"
                />
              </view>

              <view class="form-group">
                <text class="wc-form-label">当前体重 (kg)</text>
                <input
                  v-model="profileForm.currentWeight"
                  type="digit"
                  class="wc-form-input"
                  placeholder="72.5"
                  placeholder-style="color:#9aa4b2;"
                />
              </view>

              <view class="form-group">
                <text class="wc-form-label">目标体重 (kg)</text>
                <input
                  v-model="profileForm.targetWeight"
                  type="digit"
                  class="wc-form-input"
                  placeholder="65"
                  placeholder-style="color:#9aa4b2;"
                />
              </view>

              <view class="form-group">
                <text class="wc-form-label">每日饮水目标</text>
                <input
                  v-model="profileForm.dailyWaterTarget"
                  type="number"
                  class="wc-form-input"
                  placeholder="8"
                  placeholder-style="color:#9aa4b2;"
                />
              </view>

              <view class="form-group form-group--wide">
                <text class="wc-form-label">目标完成日期</text>
                <picker mode="date" :value="profileForm.targetCompletionDate || todayDate" :start="todayDate" @change="onTargetCompletionDateChange">
                  <view class="wc-form-input picker-field">
                    <text class="picker-field__text" :class="{ 'picker-placeholder': !profileForm.targetCompletionDate }">{{ profileForm.targetCompletionDate || '请选择目标完成日期' }}</text>
                    <text class="picker-arrow">›</text>
                  </view>
                </picker>
              </view>
            </view>
            <view class="goal-calorie-card">
              <view class="goal-calorie-card__grid">
                <view class="goal-calorie-card__metric">
                  <text class="goal-calorie-card__label">建议每日摄入上限</text>
                  <text class="goal-calorie-card__value">{{ profileDailyCalorieTargetText }}</text>
                </view>
                <view class="goal-calorie-card__metric">
                  <text class="goal-calorie-card__label">每日热量缺口目标</text>
                  <text class="goal-calorie-card__value">{{ profileDailyDeficitPreview }} kcal</text>
                </view>
              </view>
              <text class="goal-calorie-card__tip">{{ profileDailyTargetTip }}</text>
              <text class="goal-calorie-card__tip">{{ profileDailyDeficitTip }}</text>
            </view>
          </view>

          <view class="editor-bottom-space"></view>
        </view>

        <view class="editor-actions">
          <button class="editor-btn flow-btn flow-btn--secondary" @tap="closeProfileEditor">取消</button>
          <button class="editor-btn flow-btn flow-btn--primary" :loading="savingProfile" @tap="saveProfileSettings">保存资料</button>
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
import { getAchievements } from '@/api/achievement'
import { useUserStore } from '@/stores/user'
import { openLoginPage, uploadFile } from '@/utils/request'
import {
  buildAchievementBadges,
  buildAchievementBadgesFromRecords,
  buildAchievementPreview,
  countUnlockedAchievements
} from '@/utils/achievement'
import { captureAchievementState, fetchAchievementMetrics, notifyAchievementUnlocks } from '@/utils/achievementReminder'
import { initialText } from '@/utils/circle'
import { normalizeStoredImageValue } from '@/utils/image'
import { getMenuButtonSafeRight, getSafeTop, getTopBarHeight, syncCustomTabBar } from '@/utils/mobile'
import { useTabBarOverlayVisibility } from '@/utils/page'
import { useDefaultPageShare } from '@/utils/share'
import { getUploadSizeLimitText, uploadSingleLocalImage } from '@/utils/upload'
import {
  advanceOnboardingGuideStep,
  completeOnboardingGuide,
  disableOnboardingGuide,
  getOnboardingGuideStep,
  setOnboardingGuideStep,
  shouldPromptOnboardingGuide,
  type OnboardingGuideStep
} from '@/utils/onboarding'
import { consumeProfileEditorIntent } from '@/utils/profileSetup'
import type { AchievementItem } from '@/types/api'

useDefaultPageShare()

const safeTop = getSafeTop(20)
const topBarHeight = getTopBarHeight(56)
const menuButtonSafeRight = getMenuButtonSafeRight(16, 18)

const userStore = useUserStore()
const userInfo = computed(() => userStore.userInfo)
const isLoggedIn = computed(() => userStore.isLoggedIn)
const achievementMetrics = ref({
  activeHabits: 0,
  joinedCircles: 0
})
const achievementRecords = ref<AchievementItem[]>([])
const editorVisible = ref(false)
const onboardingGuideVisible = ref(false)
const onboardingGuideStep = ref<OnboardingGuideStep>('profile')
const savingProfile = ref(false)
const uploadingAvatar = ref(false)
const profileAvatarPreview = ref('')
const today = new Date()
const todayDate = `${today.getFullYear()}-${`${today.getMonth() + 1}`.padStart(2, '0')}-${`${today.getDate()}`.padStart(2, '0')}`
const genderOptions = [
  { label: '未知', value: '0' },
  { label: '男', value: '1' },
  { label: '女', value: '2' }
]
const profileForm = ref({
  avatar: '',
  nickname: '',
  gender: '0',
  birthday: '',
  height: '',
  currentWeight: '',
  targetWeight: '',
  targetCompletionDate: '',
  dailyWaterTarget: ''
})
const overlayVisible = computed(() => editorVisible.value || onboardingGuideVisible.value)
const syncEditorTabBarVisibility = useTabBarOverlayVisibility(overlayVisible)

onShow(() => {
  void handlePageShow()
})

const bmiStatus = computed(() => {
  const bmi = userInfo.value?.bmi
  if (bmi === null || bmi === undefined) return '未知'
  if (bmi < 18.5) return '偏瘦'
  if (bmi < 24) return '正常'
  if (bmi < 28) return '超重'
  return '肥胖'
})

const bmiClass = computed(() => {
  const bmi = userInfo.value?.bmi
  if (bmi === null || bmi === undefined) return 'unknown'
  if (bmi < 18.5) return 'underweight'
  if (bmi < 24) return 'normal'
  if (bmi < 28) return 'overweight'
  return 'obese'
})

const editorTitle = '编辑个人资料'
const editorSubtitle = '基础资料和身体目标会用于自动计算摄入上限与每日缺口。'

function sourceText(source?: string | null) {
  const map: Record<string, string> = {
    '0': '微信小程序',
    '1': 'iOS',
    '2': 'Android',
    '3': 'H5',
    '4': '后台'
  }
  return map[source || ''] || '未知来源'
}

const weightGap = computed(() => {
  const currentWeight = Number(userInfo.value?.currentWeight || 0)
  const targetWeight = Number(userInfo.value?.targetWeight || 0)
  if (!currentWeight || !targetWeight || Number.isNaN(currentWeight) || Number.isNaN(targetWeight)) {
    return '--'
  }
  return Math.max(0, Number((currentWeight - targetWeight).toFixed(1))).toString()
})
const profileSubtitleText = computed(() => {
  if (!isLoggedIn.value) {
    return '登录后同步你的记录'
  }
  const targetWeight = userInfo.value?.targetWeight
  if (targetWeight) {
    return `目标 ${targetWeight} kg · ID ${userInfo.value?.userId || '--'}`
  }
  return `先补目标体重 · ID ${userInfo.value?.userId || '--'}`
})
const userDailyCalorieTargetText = computed(() => formatKcalText(userInfo.value?.dailyCalorieTarget, true))
const userDailyDeficitText = computed(() => formatKcalText(userInfo.value?.dailyCalorieDeficitTarget, false))
const profileDailyCalorieTargetPreview = computed(() => calculateDailyCalorieTarget(
  profileForm.value.currentWeight,
  profileForm.value.height,
  profileForm.value.birthday,
  profileForm.value.gender,
  profileForm.value.targetWeight,
  profileForm.value.targetCompletionDate
))
const profileDailyCalorieTargetText = computed(() => (
  profileDailyCalorieTargetPreview.value > 0 ? `${profileDailyCalorieTargetPreview.value} kcal` : '--'
))
const profileDailyDeficitPreview = computed(() => calculateDailyCalorieDeficit(
  profileForm.value.currentWeight,
  profileForm.value.targetWeight,
  profileForm.value.targetCompletionDate
))
const profileDailyTargetTip = computed(() => {
  if (!profileForm.value.height || !profileForm.value.birthday || !profileForm.value.currentWeight) {
    return '完善身高、生日和当前体重后，系统会估算每日摄入上限。'
  }
  if (profileDailyCalorieTargetPreview.value <= 0) {
    return '当前资料暂时无法生成摄入上限，保存后可继续补充基础信息。'
  }
  if (profileDailyDeficitPreview.value > 0) {
    return '摄入上限按基础代谢估算后扣除目标缺口，用于判断今天还可摄入多少。'
  }
  return '未形成减重缺口时，摄入上限会先按维持当前体重的参考量估算。'
})
const profileDailyDeficitTip = computed(() => {
  const deficit = profileDailyDeficitPreview.value
  if (!profileForm.value.targetCompletionDate) {
    return '选择目标完成日期后，系统会按剩余天数估算每日需要形成的热量缺口。'
  }
  if (deficit <= 0) {
    return '当前体重、目标体重或日期不足以形成减重缺口，保存后会按 0 处理。'
  }
  if (deficit > 1000) {
    return '这个目标节奏偏激进，建议适当拉长完成时间。'
  }
  return '这是根据当前体重、目标体重和目标日期估算的参考值，保存后由服务端重新计算。'
})

const achievementBadges = computed(() => (
  achievementRecords.value.length
    ? buildAchievementBadgesFromRecords(achievementRecords.value)
    : buildAchievementBadges(userInfo.value, achievementMetrics.value)
))
const earnedBadgeCount = computed(() => countUnlockedAchievements(achievementBadges.value))
const achievementPreview = computed(() => buildAchievementPreview(achievementBadges.value))

function showComingSoon(feature: string) {
  uni.showToast({ title: `${feature}将在下一阶段接入`, icon: 'none' })
}

async function loadProfileContext() {
  const [userResult, achievementResult, metricsResult] = await Promise.allSettled([
    userStore.fetchUserInfo(),
    getAchievements(),
    fetchAchievementMetrics()
  ])

  achievementRecords.value = achievementResult.status === 'fulfilled' ? achievementResult.value : []
  achievementMetrics.value = metricsResult.status === 'fulfilled'
    ? metricsResult.value
    : { activeHabits: 0, joinedCircles: 0 }

  if (userResult.status === 'rejected') {
    throw userResult.reason
  }
}

async function handlePageShow() {
  syncCustomTabBar(3)
  syncEditorTabBarVisibility()

  if (!userStore.isLoggedIn) {
    return
  }

  await loadProfileContext()

  const editorIntent = consumeProfileEditorIntent()
  if (editorIntent) {
    openProfileEditor()
  }
}

function handleLogout() {
  uni.showModal({
    title: '提示',
    content: '确定要退出登录吗？',
    success: (res) => {
      if (res.confirm) {
        userStore.logout()
      }
    }
  })
}

function genderText(gender?: string | null) {
  const option = genderOptions.find((item) => item.value === String(gender ?? '0'))
  return option?.label || '未知'
}

function toInputValue(value?: string | number | null) {
  if (value === null || value === undefined || value === '') {
    return ''
  }
  return String(value)
}

function formatKcalText(value?: number | null, emptyAsDash = true) {
  const parsed = Number(value || 0)
  if (!parsed || Number.isNaN(parsed) || parsed <= 0) {
    return emptyAsDash ? '--' : '0'
  }
  return String(Math.round(parsed))
}

function parseOptionalDecimal(raw: string, fieldLabel: string) {
  const value = raw.trim()
  if (!value) {
    return null
  }
  const parsed = Number(value)
  if (Number.isNaN(parsed) || parsed <= 0) {
    uni.showToast({ title: `${fieldLabel}格式不正确`, icon: 'none' })
    return undefined
  }
  return Number(parsed.toFixed(1))
}

function parseOptionalInteger(raw: string, fieldLabel: string) {
  const value = raw.trim()
  if (!value) {
    return null
  }
  const parsed = Number(value)
  if (Number.isNaN(parsed) || parsed <= 0 || !Number.isInteger(parsed)) {
    uni.showToast({ title: `${fieldLabel}格式不正确`, icon: 'none' })
    return undefined
  }
  return parsed
}

function calculateDailyCalorieDeficit(currentWeightRaw: string, targetWeightRaw: string, targetDateRaw: string) {
  const currentWeight = Number(currentWeightRaw)
  const targetWeight = Number(targetWeightRaw)
  if (!currentWeight || !targetWeight || Number.isNaN(currentWeight) || Number.isNaN(targetWeight) || !targetDateRaw) {
    return 0
  }
  const weightGap = currentWeight - targetWeight
  if (weightGap <= 0) {
    return 0
  }
  const todayStart = new Date()
  todayStart.setHours(0, 0, 0, 0)
  const targetDate = new Date(`${targetDateRaw}T00:00:00`)
  const days = Math.ceil((targetDate.getTime() - todayStart.getTime()) / 86400000)
  if (days <= 0) {
    return 0
  }
  return Math.max(0, Math.round((weightGap * 7700) / days))
}

function calculateDailyCalorieTarget(
  currentWeightRaw: string,
  heightRaw: string,
  birthdayRaw: string,
  genderRaw: string,
  targetWeightRaw: string,
  targetDateRaw: string
) {
  const currentWeight = Number(currentWeightRaw)
  const height = Number(heightRaw)
  const age = calculateAge(birthdayRaw)
  if (!currentWeight || !height || !age || Number.isNaN(currentWeight) || Number.isNaN(height)) {
    return 0
  }

  const genderOffset = genderRaw === '1' ? 5 : genderRaw === '2' ? -161 : -78
  const bmr = currentWeight * 10 + height * 6.25 - age * 5 + genderOffset
  if (bmr <= 0) {
    return 0
  }

  const deficit = calculateDailyCalorieDeficit(currentWeightRaw, targetWeightRaw, targetDateRaw)
  const minimumIntake = genderRaw === '1' ? 1500 : 1200
  return Math.max(minimumIntake, Math.round(bmr * 1.2 - deficit))
}

function calculateAge(birthdayRaw: string) {
  if (!birthdayRaw) {
    return 0
  }
  const birthday = new Date(`${birthdayRaw}T00:00:00`)
  if (Number.isNaN(birthday.getTime())) {
    return 0
  }

  const today = new Date()
  let age = today.getFullYear() - birthday.getFullYear()
  const monthDiff = today.getMonth() - birthday.getMonth()
  if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthday.getDate())) {
    age -= 1
  }
  if (age < 10 || age > 100) {
    return 0
  }
  return age
}

function fillProfileForm() {
  profileForm.value = {
    avatar: normalizeStoredImageValue(userInfo.value?.avatar),
    nickname: userInfo.value?.nickname || '',
    gender: userInfo.value?.gender || '0',
    birthday: userInfo.value?.birthday || '',
    height: toInputValue(userInfo.value?.height),
    currentWeight: toInputValue(userInfo.value?.currentWeight),
    targetWeight: toInputValue(userInfo.value?.targetWeight),
    targetCompletionDate: userInfo.value?.targetCompletionDate || '',
    dailyWaterTarget: toInputValue(userInfo.value?.dailyWaterTarget)
  }
  profileAvatarPreview.value = ''
}

function openProfileEditor() {
  if (!isLoggedIn.value) {
    openLoginPage('safe')
    return
  }
  fillProfileForm()
  editorVisible.value = true
}

function closeProfileEditor() {
  if (savingProfile.value) {
    return
  }
  editorVisible.value = false
  profileAvatarPreview.value = ''
  showOnboardingContinuationAfterProfile()
}

function onBirthdayChange(event: { detail: { value: string } }) {
  profileForm.value.birthday = event.detail.value
}

function onTargetCompletionDateChange(event: { detail: { value: string } }) {
  profileForm.value.targetCompletionDate = event.detail.value
}

async function onChooseAvatar(event: { detail?: { avatarUrl?: string } }) {
  const avatarUrl = String(event?.detail?.avatarUrl || '').trim()
  if (!avatarUrl || uploadingAvatar.value) {
    return
  }

  uploadingAvatar.value = true
  uni.showLoading({ title: '上传头像中...', mask: true })
  try {
    const uploadResult = await uploadSingleLocalImage({
      filePath: avatarUrl,
      upload: (filePath) => uploadFile(filePath, '/api/user/avatar/upload'),
      fallbackErrorMessage: '头像上传失败',
      oversizeMessage: `头像不能超过 ${getUploadSizeLimitText()}`
    })
    profileForm.value.avatar = uploadResult.fileName || uploadResult.url
    profileAvatarPreview.value = avatarUrl
    uni.showToast({ title: '头像已同步', icon: 'success' })
  } catch (error: any) {
    console.error('上传头像失败:', error)
    uni.showToast({ title: error?.message || '头像上传失败', icon: 'none' })
  } finally {
    uploadingAvatar.value = false
    uni.hideLoading()
  }
}

async function saveProfileSettings() {
  const previousAchievementKeys = captureAchievementState(userStore.userInfo, achievementMetrics.value)
  const nickname = profileForm.value.nickname.trim()
  if (!nickname) {
    uni.showToast({ title: '请输入昵称', icon: 'none' })
    return
  }

  const height = parseOptionalDecimal(profileForm.value.height, '身高')
  if (height === undefined) return

  const currentWeight = parseOptionalDecimal(profileForm.value.currentWeight, '当前体重')
  if (currentWeight === undefined) return

  const targetWeight = parseOptionalDecimal(profileForm.value.targetWeight, '目标体重')
  if (targetWeight === undefined) return

  const dailyWaterTarget = parseOptionalInteger(profileForm.value.dailyWaterTarget, '每日饮水目标')
  if (dailyWaterTarget === undefined) return

  savingProfile.value = true
  try {
    const payload: Record<string, string | number | null> = {
      nickname,
      avatar: normalizeStoredImageValue(profileForm.value.avatar) || null,
      gender: profileForm.value.gender || '0',
      birthday: profileForm.value.birthday || null
    }

    if (height !== null) {
      payload.height = height
    }
    if (currentWeight !== null) {
      payload.currentWeight = currentWeight
    }
    if (targetWeight !== null) {
      payload.targetWeight = targetWeight
    }
    if (profileForm.value.targetCompletionDate) {
      payload.targetCompletionDate = profileForm.value.targetCompletionDate
    }
    if (dailyWaterTarget !== null) {
      payload.dailyWaterTarget = dailyWaterTarget
    }

    await userStore.updateUserInfo(payload)
    const [metricsResult, achievementResult] = await Promise.allSettled([
      fetchAchievementMetrics(),
      getAchievements()
    ])
    achievementMetrics.value = metricsResult.status === 'fulfilled'
      ? metricsResult.value
      : { activeHabits: 0, joinedCircles: 0 }
    achievementRecords.value = achievementResult.status === 'fulfilled' ? achievementResult.value : []
    editorVisible.value = false
    profileAvatarPreview.value = ''
    uni.showToast({ title: '资料已更新', icon: 'success' })
    await notifyAchievementUnlocks(previousAchievementKeys, userStore.userInfo, achievementMetrics.value)
    showOnboardingContinuationAfterProfile()
  } catch (error) {
    console.error('更新个人资料失败:', error)
    uni.showToast({ title: '资料保存失败', icon: 'none' })
  } finally {
    savingProfile.value = false
  }
}

// 导航函数
function goSettings() { openProfileEditor() }

function ensureLoggedInForProfileAction(redirectUrl = '/pages/profile/index') {
  if (isLoggedIn.value) {
    return true
  }
  openLoginPage('safe', redirectUrl)
  return false
}

function goPrivatePage(url: string) {
  if (!ensureLoggedInForProfileAction(url)) {
    return
  }
  uni.navigateTo({ url })
}

function goAchievements() { goPrivatePage('/pages/achievements/index') }
function goHabit() { goPrivatePage('/pages/habit/index') }
function goMyCircles() { goPrivatePage('/pages/circle-manage/index') }
function goReminder() { goPrivatePage('/pages/notifications/index') }
function goFeedback() { goPrivatePage('/pages/feedback/index') }

function openOnboardingGuide() {
  if (!isLoggedIn.value) {
    openLoginPage('safe')
    return
  }
  if (editorVisible.value) {
    return
  }
  onboardingGuideStep.value = getOnboardingGuideStep(userStore.userInfo?.userId)
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
    openProfileEditor()
    return
  }

  if (onboardingGuideStep.value === 'record') {
    hideOnboardingGuideForAction('record')
    uni.navigateTo({ url: '/pages/record/index?tab=weight' })
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

function showOnboardingContinuationAfterProfile() {
  const userId = userStore.userInfo?.userId
  if (!shouldPromptOnboardingGuide(userId) || getOnboardingGuideStep(userId) !== 'record') {
    return
  }
  onboardingGuideStep.value = 'record'
  onboardingGuideVisible.value = true
}
</script>

<style lang="scss" scoped>
$primary: var(--wc-primary);
$accent: var(--wc-success);
$bg-page: var(--wc-bg);
$card-bg: var(--wc-surface-strong);
$text-main: var(--wc-text);
$text-sub: var(--wc-text-soft);

@use '../../styles/flow-button.scss';

.profile-container {
  height: 100vh;
  background: $bg-page;
  overflow: hidden;
}

// 固定顶部导航栏
.header {
  padding: 0 30rpx;
}

.header-left {
  display: flex;
  flex-direction: column;
}

.page-title {
  font-size: 40rpx;
  font-weight: bold;
}

.sub-title {
  font-size: 24rpx;
  color: $text-sub;
  margin-top: 4rpx;
}

.content {
  box-sizing: border-box;
}

.profile-header {
  background:
    radial-gradient(circle at 10% 0%, rgba(255, 255, 255, 0.82), transparent 34%),
    linear-gradient(135deg, rgba(232, 248, 241, 0.98), rgba(255, 250, 240, 0.92));
  padding: 38rpx 34rpx 30rpx;
  display: flex;
  align-items: center;
  margin: 0 24rpx;
  border: 1px solid rgba(39, 92, 72, 0.055);
  border-bottom: none;
  border-top-left-radius: var(--wc-radius-xl);
  border-top-right-radius: var(--wc-radius-xl);
  box-shadow: none;
}

.avatar-large {
  width: 140rpx;
  height: 140rpx;
  border-radius: 50%;
  background: #fff;
  margin-right: 40rpx;
  border: 4rpx solid rgba(255, 255, 255, 0.92);
  box-shadow: var(--wc-shadow-soft);
}

.avatar-large--fallback {
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #e8f8f1, #fff4dd);
  color: #168a62;
  font-size: 48rpx;
  font-weight: 700;
}

.user-info {
  .nickname {
    font-size: 42rpx;
    font-weight: 800;
    display: block;
    margin-bottom: 10rpx;
  }
  
  .user-id {
    font-size: 24rpx;
    color: $text-sub;
    line-height: 1.45;
  }
}

.user-meta-chips {
  margin-top: 16rpx;
  display: flex;
  flex-wrap: wrap;
  gap: 10rpx;
}

.user-meta-chip {
  line-height: 1.2;
}

.user-stats {
  display: flex;
  justify-content: space-between;
  gap: 16rpx;
  background: rgba(255, 255, 255, 0.68);
  margin: 0 24rpx 20rpx;
  padding: 0 28rpx 30rpx;
  border: 1px solid rgba(39, 92, 72, 0.055);
  border-top: none;
  border-bottom-left-radius: var(--wc-radius-xl);
  border-bottom-right-radius: var(--wc-radius-xl);
  box-shadow: none;
}

.stat-item {
  flex: 1;
  min-width: 0;
  min-height: 112rpx;
  padding: 20rpx 12rpx;
  border-radius: 28rpx;
  background: rgba(255, 255, 255, 0.58);
  text-align: center;
}

.stat-val {
  font-size: 34rpx;
  font-weight: 800;
  color: $text-main;
  display: block;
}

.stat-label {
  font-size: 22rpx;
  color: $text-sub;
  margin-top: 8rpx;
  display: block;
}

.badges-section {
  background: rgba(255, 255, 255, 0.72);
  padding: 34rpx;
  margin: 0 24rpx 20rpx;
  border-radius: var(--wc-radius-l);
  border: 1px solid rgba(39, 92, 72, 0.055);
  box-shadow: none;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30rpx;
}

.section-title {
  font-weight: bold;
  font-size: 32rpx;
}

.view-all {
  line-height: 1.2;
}

.badge-list {
  display: flex;
  gap: 20rpx;
  flex-wrap: wrap;
}

.badge {
  min-height: 72rpx;
  padding: 18rpx 24rpx;
  background: linear-gradient(180deg, rgba(232, 248, 241, 0.96), rgba(255, 255, 255, 0.98));
  border-radius: 22rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24rpx;
  color: var(--wc-primary-strong);
}

.badge--locked {
  background: #f3f7f2;
  color: #a4b1aa;
}

.body-info-card {
  background: rgba(255, 255, 255, 0.74);
  margin: 20rpx 24rpx;
  border-radius: 34rpx;
  padding: 32rpx;
  border: 1px solid rgba(39, 92, 72, 0.055);
  box-shadow: none;
}

.body-info-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24rpx;
  font-weight: 600;
  font-size: 28rpx;
}

.edit-btn {
  line-height: 1.2;
}

.body-info-grid {
  display: flex;
  flex-direction: column;
  gap: 0;
}

.body-info-item {
  min-height: 76rpx;
  background: transparent;
  padding: 18rpx 0;
  border-radius: 0;
  border: 0;
  border-bottom: 1px solid rgba(39, 92, 72, 0.06);
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 24rpx;
}

.body-info-item:last-child {
  border-bottom: 0;
}

.body-info-label {
  flex-shrink: 0;
  font-size: 24rpx;
  color: $text-sub;
  margin-bottom: 0;
  display: block;
}

.body-info-value {
  min-width: 0;
  font-size: 28rpx;
  font-weight: 700;
  text-align: right;
  
  .unit {
    font-size: 22rpx;
    font-weight: normal;
    color: $text-sub;
  }
}

.body-info-value--text {
  display: block;
  font-size: 28rpx;
  line-height: 1.4;
}

.bmi-tag {
  margin-left: 8rpx;
  
  &.normal {
    background: rgba(43, 167, 121, 0.14);
    color: var(--wc-success);
  }
  
  &.overweight,
  &.obese {
    background: rgba(229, 103, 103, 0.14);
    color: var(--wc-danger);
  }
  
  &.underweight {
    background: var(--wc-primary-soft);
    color: var(--wc-primary-strong);
  }
}

.menu-list {
  background: rgba(255, 255, 255, 0.74);
  margin: 0 24rpx 20rpx;
  padding: 0 28rpx;
  border-radius: var(--wc-radius-l);
  border: 1px solid rgba(39, 92, 72, 0.055);
  box-shadow: none;
}

.menu-divider {
  height: 0;
}

.menu-item {
  display: flex;
  align-items: center;
  padding: 36rpx 0;
  border-bottom: 1px solid rgba(39, 92, 72, 0.07);
  
  &:last-child {
    border-bottom: none;
  }
  
  &:active {
    background: rgba(239, 247, 241, 0.72);
  }
}

.menu-icon {
  width: 48rpx;
  font-size: 40rpx;
  margin-right: 30rpx;
}

.menu-text {
  flex: 1;
  font-size: 32rpx;
}

.menu-badge {
  margin-right: 16rpx;
}

.menu-note {
  font-size: 22rpx;
  color: $text-sub;
  margin-right: 16rpx;
}

.menu-arrow {
  color: var(--wc-text-faint);
  font-size: 36rpx;
}

.logout-section {
  padding: 60rpx 40rpx;
}

.btn-logout {
  width: 100%;
  font-size: 32rpx;
  color: var(--wc-danger);
  border-color: rgba(229, 103, 103, 0.24);
  background: rgba(255, 255, 255, 0.94);
}

.editor-mask {
  position: fixed;
  inset: 0;
  z-index: 1100;
  background: rgba(15, 23, 42, 0.26);
  display: flex;
  align-items: flex-end;
}

.editor-modal {
  width: 100%;
  max-height: 86vh;
  background: $card-bg;
  border-radius: 36rpx 36rpx 0 0;
  box-shadow: 0 -18rpx 48rpx rgba(15, 23, 42, 0.12);
  padding: 28rpx 24rpx calc(28rpx + env(safe-area-inset-bottom));
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
}

.editor-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 20rpx;
  margin-bottom: 20rpx;
}

.editor-title-wrap {
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 8rpx;
}

.editor-title {
  font-size: 34rpx;
  font-weight: 700;
  color: $text-main;
}

.editor-subtitle {
  font-size: 22rpx;
  line-height: 1.5;
  color: $text-sub;
}

.editor-close {
  flex-shrink: 0;
  line-height: 1.2;
}

.editor-scroll {
  flex: 1;
  min-height: 0;
  box-sizing: border-box;
  overflow-y: auto;
  -webkit-overflow-scrolling: touch;
  padding-bottom: 24rpx;
}

.editor-card {
  background: var(--wc-surface);
  border: 1px solid var(--wc-line);
  border-radius: 28rpx;
  padding: 24rpx;
  margin-bottom: 20rpx;
  box-shadow: var(--wc-shadow-soft);
}

.editor-section-title {
  display: block;
  margin-bottom: 18rpx;
  font-size: 26rpx;
  font-weight: 600;
  color: $text-main;
}

.editor-card .form-group {
  margin-bottom: 20rpx;
}

.editor-card .form-group:last-child {
  margin-bottom: 0;
}

.avatar-picker-row {
  display: flex;
  align-items: center;
  gap: 18rpx;
  padding: 18rpx;
  border-radius: 24rpx;
  border: 1px solid var(--wc-line);
  background: var(--wc-surface-muted);
}

.avatar-picker-preview {
  width: 88rpx;
  height: 88rpx;
  flex-shrink: 0;
  border-radius: 50%;
  border: 1px solid rgba(47, 179, 123, 0.16);
  background: var(--wc-primary-soft);
}

.avatar-picker-preview--fallback {
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--wc-primary-strong);
  font-size: 28rpx;
  font-weight: 700;
}

.avatar-picker-copy {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 6rpx;
}

.avatar-picker-title {
  font-size: 26rpx;
  font-weight: 600;
  color: $text-main;
}

.avatar-picker-tip {
  font-size: 22rpx;
  line-height: 1.5;
  color: $text-sub;
}

.avatar-picker-btn {
  flex-shrink: 0;
  min-width: 176rpx;
  height: 72rpx;
  line-height: 72rpx;
  padding: 0 24rpx;
  font-size: 24rpx;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20rpx;
}

.form-grid .form-group {
  margin-bottom: 0;
  min-width: 0;
}

.form-grid .form-group--wide {
  grid-column: 1 / -1;
}

.editor-tab-rail {
  width: 100%;
}

.picker-field {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16rpx;
  min-width: 0;
}

.picker-field__text {
  flex: 1;
  min-width: 0;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}

.picker-placeholder {
  color: #9aa4b2;
}

.picker-arrow {
  color: var(--wc-text-faint);
  font-size: 32rpx;
  line-height: 1;
}

.goal-calorie-card {
  margin-top: 24rpx;
  padding: 24rpx;
  border-radius: 24rpx;
  border: 1px solid rgba(43, 167, 121, 0.18);
  background: rgba(43, 167, 121, 0.08);
}

.goal-calorie-card__grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 20rpx;
  margin-bottom: 12rpx;
}

.goal-calorie-card__metric {
  min-width: 0;
}

.goal-calorie-card__label,
.goal-calorie-card__tip {
  display: block;
  font-size: 22rpx;
  color: $text-sub;
  line-height: 1.5;
}

.goal-calorie-card__value {
  display: block;
  margin: 8rpx 0;
  font-size: 34rpx;
  font-weight: 800;
  color: var(--wc-success);
}

.editor-actions {
  display: flex;
  gap: 16rpx;
  margin-top: 8rpx;
  padding: 18rpx 0 calc(24rpx + env(safe-area-inset-bottom));
  background: $card-bg;
  border-top: 1px solid var(--wc-line);
}

.editor-btn {
  flex: 1;
}

.editor-bottom-space {
  height: 120rpx;
}

.page-bottom-space {
  height: 120rpx;
}
</style>
