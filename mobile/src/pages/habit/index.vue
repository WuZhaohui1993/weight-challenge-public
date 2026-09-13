<template>
  <view class="page-container wc-page-enter">
    <prototype-header title="习惯管理" back-url="/pages/profile/index" right-text="新建" @rightTap="openCreator" />

    <scroll-view class="content" scroll-y>
      <view class="page-desc">
        <text>💡</text>
        <text>在这里创建你的基础习惯，启用后会同步到记录页的“今日习惯”。</text>
      </view>

      <view class="calendar-strip">
        <view
          v-for="day in calendarDays"
          :key="day.key"
          class="calendar-day"
          :class="{ active: day.active, done: day.done }"
        >
          <text class="day-label">{{ day.label }}</text>
          <text class="day-num">{{ day.day }}</text>
          <text class="day-progress">{{ day.completed }}/{{ day.total }}</text>
          <view class="day-dot"></view>
        </view>
      </view>

      <view class="stats-card">
        <view class="stats-row">
          <view class="stat-item">
            <text class="stat-num">{{ stats.totalHabits }}</text>
            <text class="stat-label">习惯总数</text>
          </view>
          <view class="stat-item">
            <text class="stat-num">{{ stats.completedToday }}/{{ stats.activeHabits }}</text>
            <text class="stat-label">今日完成</text>
          </view>
          <view class="stat-item">
            <text class="stat-num">{{ stats.weeklyCompletionRate }}%</text>
            <text class="stat-label">近 7 天完成率</text>
          </view>
        </view>
        <view class="stats-footnote">
          <text>今日完成率 {{ stats.todayCompletionRate }}%</text>
          <text>最佳连续 {{ stats.bestStreak }} 天</text>
        </view>
      </view>

      <view class="reminder-card">
        <view class="reminder-card__header">
          <text class="reminder-card__title">提醒概览</text>
          <text class="reminder-card__tip">点习惯卡片可编辑提醒</text>
        </view>
        <view class="reminder-chip-row">
          <view class="reminder-chip">
            <text class="reminder-chip__label">已设置提醒</text>
            <text class="reminder-chip__value">{{ reminderEnabledCount }}</text>
          </view>
          <view class="reminder-chip">
            <text class="reminder-chip__label">最近提醒</text>
            <text class="reminder-chip__value">{{ nextReminderLabel }}</text>
          </view>
        </view>
      </view>

      <app-section-header compact title="我的习惯" subtitle="点开卡片可编辑名称、频率和提醒。" />

      <view v-if="loading" class="habit-panel habit-panel--loading">习惯加载中...</view>

      <view v-else-if="loadError" class="habit-panel">
        <app-empty-state icon="⚠️" title="习惯加载失败" :description="loadError">
          <button class="flow-btn flow-btn--secondary flow-btn--compact" @tap="loadHabitPage">重新整理</button>
        </app-empty-state>
      </view>

      <view v-else-if="!habits.length" class="habit-panel">
        <app-empty-state
          icon="✅"
          title="还没有创建习惯"
          description="先建一个每天都能完成的小目标，比如喝水、散步或早睡。"
        >
          <button class="flow-btn flow-btn--primary flow-btn--compact" @tap="openCreator">创建第一个习惯</button>
        </app-empty-state>
      </view>

      <view v-else class="habit-list">
        <habit-card
          v-for="habit in habits"
          :key="habit.id"
          :habit="habit"
          :secondary-action-text="habit.active ? '停用' : '删除'"
          :secondary-action-tone="habit.active ? 'soft' : 'danger'"
          :primary-action-text="habit.active ? (habit.checkedToday ? '撤销打卡' : '今日打卡') : '启用'"
          :primary-action-tone="habit.active ? (habit.checkedToday ? 'secondary' : 'primary') : 'soft'"
          @cardTap="openEditor(habit)"
          @secondaryTap="toggleHabitActive(habit)"
          @primaryTap="toggleHabitCheckin(habit)"
        />
      </view>

      <view class="page-bottom-space"></view>
    </scroll-view>

    <view v-if="creatorVisible" class="editor-mask" @tap="closeCreator">
      <view class="editor-modal" @tap.stop>
        <view class="editor-header">
          <view class="editor-title-wrap">
            <text class="editor-title">{{ editingHabitId ? '编辑习惯' : '新建习惯' }}</text>
            <text class="editor-subtitle">{{ editingHabitId ? '可修改名称、频率和提醒设置。' : '先做一个容易坚持的小目标，再慢慢加到日常节奏里。' }}</text>
          </view>
          <text class="editor-close wc-inline-link wc-inline-link--action wc-inline-link--subtle" @tap="closeCreator">关闭</text>
        </view>

        <view class="editor-scroll">
          <view class="editor-card">
            <view class="form-group">
              <text class="wc-form-label">习惯名称</text>
              <input
                v-model="habitForm.name"
                class="wc-form-input"
                maxlength="20"
                placeholder="例如：晚饭后散步 20 分钟"
                placeholder-style="color:#9aa4b2;"
              />
            </view>

            <view class="form-group">
              <text class="wc-form-label">图标</text>
              <view class="icon-rail">
                <view
                  v-for="icon in iconOptions"
                  :key="icon"
                  class="icon-option wc-pressable"
                  :class="{ active: habitForm.icon === icon }"
                  @tap="habitForm.icon = icon"
                >
                  {{ icon }}
                </view>
              </view>
            </view>

            <view class="form-group">
              <text class="wc-form-label">频率</text>
              <view class="editor-tab-rail wc-tab-rail wc-tab-rail--compact">
                <view
                  v-for="option in frequencyOptions"
                  :key="option.value"
                  class="wc-tab-item wc-tab-item--compact wc-tab-item--full wc-pressable"
                  :class="{ active: habitForm.frequency === option.value }"
                  @tap="selectFrequency(option.value)"
                >
                  {{ option.label }}
                </view>
              </view>
            </view>

            <view v-if="habitForm.frequency === '1'" class="form-group">
              <text class="wc-form-label">每周提醒日</text>
              <view class="editor-tab-rail wc-tab-rail wc-tab-rail--compact">
                <view
                  v-for="option in weekdayOptions"
                  :key="option.value"
                  class="wc-tab-item wc-tab-item--compact wc-tab-item--full wc-pressable"
                  :class="{ active: selectedReminderWeekdays.includes(option.value) }"
                  @tap="toggleReminderWeekday(option.value)"
                >
                  周{{ option.label }}
                </view>
              </view>
            </view>

            <view class="form-group">
              <text class="wc-form-label">{{ habitForm.frequency === '1' ? '每周提醒时间' : '提醒时间' }}</text>
              <picker mode="time" :value="habitForm.reminderTime" @change="onReminderTimeChange">
                <view class="wc-form-input picker-field">
                  <text :class="{ 'picker-placeholder': !habitForm.reminderTime }">{{ habitForm.reminderTime || '可选，先不设置也可以' }}</text>
                  <text class="picker-arrow">›</text>
                </view>
              </picker>
              <text v-if="habitForm.reminderTime" class="picker-clear wc-inline-link wc-inline-link--subtle" @tap="clearReminderTime">清除提醒</text>
            </view>
          </view>
        </view>

        <button class="flow-btn flow-btn--primary flow-btn--wide" :loading="saving" @tap="submitHabit">{{ editingHabitId ? '保存习惯' : '创建习惯' }}</button>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import AppEmptyState from '@/components/AppEmptyState/index.vue'
import HabitCard from '@/components/HabitCard/index.vue'
import AppSectionHeader from '@/components/AppSectionHeader/index.vue'
import PrototypeHeader from '@/components/PrototypeHeader/index.vue'
import { useUserStore } from '@/stores/user'
import { captureAchievementState, fetchAchievementMetrics, notifyAchievementUnlocks } from '@/utils/achievementReminder'
import { openLoginPage } from '@/utils/request'
import { useDefaultPageShare } from '@/utils/share'
import { checkinHabit, createHabit, deleteHabit, getHabitList, getHabitStats, undoHabitCheckin, updateHabit } from '@/api/habit'
import type { HabitItem, HabitStats, HabitUpsertPayload } from '@/types/api'

useDefaultPageShare()

const userStore = useUserStore()
const habits = ref<HabitItem[]>([])
const editingHabitId = ref<number | null>(null)
const stats = ref<HabitStats>(createEmptyHabitStats())
const creatorVisible = ref(false)
const loading = ref(false)
const saving = ref(false)
const loadError = ref('')

function createEmptyHabitStats(): HabitStats {
  return {
    totalHabits: 0,
    activeHabits: 0,
    completedToday: 0,
    bestStreak: 0,
    todayCompletionRate: 0,
    weeklyCompletionRate: 0,
    weeklyProgress: []
  }
}

const iconOptions = ['✅', '💧', '🚶', '🌙', '📚', '🥗', '🏃', '🧘', '🛌']
const frequencyOptions = [
  { value: '0', label: '每日' },
  { value: '1', label: '每周' }
]
const weekdayOptions = [
  { value: '1', label: '一' },
  { value: '2', label: '二' },
  { value: '3', label: '三' },
  { value: '4', label: '四' },
  { value: '5', label: '五' },
  { value: '6', label: '六' },
  { value: '0', label: '日' }
]

const habitForm = ref<HabitUpsertPayload>({
  name: '',
  icon: '✅',
  period: '1',
  frequency: '0',
  reminderTime: currentTimeLabel(),
  reminderWeekday: `${new Date().getDay()}`
})

const reminderEnabledCount = computed(() => habits.value.filter((item) => item.active && item.reminderTime).length)
const selectedReminderWeekdays = computed(() =>
  (habitForm.value.reminderWeekday || '')
    .split(',')
    .map((item) => item.trim())
    .filter(Boolean)
)
const nextReminderLabel = computed(() => {
  const reminderTimes = habits.value
    .filter((item) => item.active && item.reminderTime)
    .map((item) => buildReminderSummary(item))
    .sort((left, right) => left.localeCompare(right))
  return reminderTimes[0] || '未设置'
})

const calendarDays = computed(() => {
  const today = new Date()
  if (stats.value.weeklyProgress.length) {
    return stats.value.weeklyProgress.map((day) => ({
      key: day.date,
      label: day.label,
      day: day.day,
      active: day.active,
      done: day.done,
      completed: day.completedCount,
      total: day.totalCount
    }))
  }
  return Array.from({ length: 7 }, (_, index) => {
    const date = new Date(today)
    date.setDate(today.getDate() - (6 - index))
    const isToday = index === 6
    return {
      key: `${date.getFullYear()}-${date.getMonth()}-${date.getDate()}`,
      label: ['日', '一', '二', '三', '四', '五', '六'][date.getDay()],
      day: `${date.getDate()}`.padStart(2, '0'),
      active: isToday,
      done: false,
      completed: 0,
      total: 0
    }
  })
})

onShow(() => {
  if (!ensureLoggedIn()) {
    return
  }
  void loadHabitPage()
})

function ensureLoggedIn() {
  if (userStore.isLoggedIn) {
    return true
  }
  habits.value = []
  stats.value = createEmptyHabitStats()
  loadError.value = ''
  loading.value = false
  creatorVisible.value = false
  openLoginPage('safe', '/pages/habit/index')
  return false
}

function resolveErrorMessage(error: unknown, fallback: string) {
  return error instanceof Error && error.message ? error.message : fallback
}

function showActionError(error: unknown, fallback: string) {
  const title = resolveErrorMessage(error, fallback)
  uni.showToast({
    title: title.length > 12 ? fallback : title,
    icon: 'none'
  })
}

async function loadHabitPage() {
  loading.value = true
  loadError.value = ''
  try {
    const [habitList, habitStats] = await Promise.all([getHabitList(), getHabitStats()])
    habits.value = habitList
    stats.value = habitStats || stats.value
  } catch (error) {
    console.error('加载习惯列表失败', error)
    habits.value = []
    stats.value = createEmptyHabitStats()
    loadError.value = '请检查后端服务是否可用，然后再试一次。'
  } finally {
    loading.value = false
  }
}

function openCreator() {
  if (!ensureLoggedIn()) {
    return
  }
  editingHabitId.value = null
  resetHabitForm()
  creatorVisible.value = true
}

function openEditor(habit: HabitItem) {
  if (!ensureLoggedIn()) {
    return
  }
  editingHabitId.value = habit.id
  habitForm.value = {
    name: habit.name,
    icon: habit.icon || '✅',
    frequency: habit.frequency || '0',
    reminderTime: habit.reminderTime || currentTimeLabel(),
    reminderWeekday: habit.reminderWeekday || `${new Date().getDay()}`
  }
  creatorVisible.value = true
}

function closeCreator() {
  if (saving.value) {
    return
  }
  creatorVisible.value = false
  editingHabitId.value = null
  resetHabitForm()
}

function resetHabitForm() {
  habitForm.value = {
    name: '',
    icon: '✅',
    frequency: '0',
    reminderTime: currentTimeLabel(),
    reminderWeekday: `${new Date().getDay()}`
  }
}

function onReminderTimeChange(event: { detail: { value: string } }) {
  habitForm.value.reminderTime = event.detail.value
}

function selectFrequency(value: string) {
  habitForm.value.frequency = value
  if (!habitForm.value.reminderTime) {
    habitForm.value.reminderTime = currentTimeLabel()
  }
  if (value === '1' && !habitForm.value.reminderWeekday) {
    habitForm.value.reminderWeekday = `${new Date().getDay()}`
  }
  if (value === '0') {
    habitForm.value.reminderWeekday = null
  }
}

function toggleReminderWeekday(value: string) {
  const next = new Set(selectedReminderWeekdays.value)
  if (next.has(value)) {
    next.delete(value)
  } else {
    next.add(value)
  }
  const ordered = weekdayOptions
    .map((item) => item.value)
    .filter((item) => next.has(item))
  habitForm.value.reminderWeekday = ordered.join(',')
}

function clearReminderTime() {
  habitForm.value.reminderTime = ''
}

function currentTimeLabel() {
  const now = new Date()
  return `${`${now.getHours()}`.padStart(2, '0')}:${`${now.getMinutes()}`.padStart(2, '0')}`
}

function buildReminderSummary(habit: HabitItem) {
  if (!habit.reminderTime) {
    return '未设置'
  }
  if (habit.frequency === '1') {
    return `周${weekdayText(habit.reminderWeekday)} ${habit.reminderTime}`
  }
  return `每日 ${habit.reminderTime}`
}

function weekdayText(value?: string | null) {
  if (!value) {
    return '待定'
  }
  return value
    .split(',')
    .map((item) => {
      switch (item) {
        case '1':
          return '一'
        case '2':
          return '二'
        case '3':
          return '三'
        case '4':
          return '四'
        case '5':
          return '五'
        case '6':
          return '六'
        case '0':
          return '日'
        default:
          return null
      }
    })
    .filter(Boolean)
    .join(' / ') || '待定'
}

async function submitHabit() {
  if (!ensureLoggedIn()) {
    return
  }
  const previousAchievementKeys = captureAchievementState(userStore.userInfo, await fetchAchievementMetrics())
  const name = (habitForm.value.name || '').trim()
  if (!name) {
    uni.showToast({ title: '请输入习惯名称', icon: 'none' })
    return
  }
  if (habitForm.value.frequency === '1' && !selectedReminderWeekdays.value.length) {
    uni.showToast({ title: '请至少选择一天', icon: 'none' })
    return
  }

  saving.value = true
  try {
    if (editingHabitId.value) {
      const updated = await updateHabit(editingHabitId.value, {
        ...habitForm.value,
        name
      })
      replaceHabit(updated)
      await loadHabitStatsOnly(true)
      await userStore.fetchUserInfo()
      uni.showToast({ title: '已保存', icon: 'success' })
      await notifyAchievementUnlocks(previousAchievementKeys, userStore.userInfo, await fetchAchievementMetrics())
    } else {
      const created = await createHabit({
        ...habitForm.value,
        name
      })
      habits.value = [created, ...habits.value]
      await loadHabitStatsOnly(true)
      await userStore.fetchUserInfo()
      uni.showToast({ title: '习惯已创建', icon: 'success' })
      await notifyAchievementUnlocks(previousAchievementKeys, userStore.userInfo, await fetchAchievementMetrics())
    }
    creatorVisible.value = false
    editingHabitId.value = null
    resetHabitForm()
  } catch (error) {
    console.error(editingHabitId.value ? '更新习惯失败' : '创建习惯失败', error)
    showActionError(error, editingHabitId.value ? '保存失败' : '创建失败')
  } finally {
    saving.value = false
  }
}

async function toggleHabitActive(habit: HabitItem) {
  if (!ensureLoggedIn()) {
    return
  }
  const previousAchievementKeys = captureAchievementState(userStore.userInfo, await fetchAchievementMetrics())
  if (!habit.active) {
    const confirmed = await confirmDeleteHabit(habit)
    if (!confirmed) {
      return
    }
    try {
      await deleteHabit(habit.id)
      habits.value = habits.value.filter((item) => item.id !== habit.id)
      await loadHabitStatsOnly(true)
      await userStore.fetchUserInfo()
      uni.showToast({ title: '已删除', icon: 'success' })
      await notifyAchievementUnlocks(previousAchievementKeys, userStore.userInfo, await fetchAchievementMetrics())
    } catch (error) {
      console.error('删除习惯失败', error)
      showActionError(error, '删除失败')
    }
    return
  }

  try {
    const updated = await updateHabit(habit.id, {
      active: false
    })
    replaceHabit(updated)
    await loadHabitStatsOnly(true)
    await userStore.fetchUserInfo()
    uni.showToast({ title: '已停用', icon: 'success' })
    await notifyAchievementUnlocks(previousAchievementKeys, userStore.userInfo, await fetchAchievementMetrics())
  } catch (error) {
    console.error('更新习惯状态失败', error)
    showActionError(error, '停用失败')
  }
}

async function toggleHabitCheckin(habit: HabitItem) {
  if (!ensureLoggedIn()) {
    return
  }
  const previousAchievementKeys = captureAchievementState(userStore.userInfo, await fetchAchievementMetrics())
  try {
    const updated = !habit.active
      ? await updateHabit(habit.id, { active: true })
      : habit.checkedToday
        ? await undoHabitCheckin(habit.id)
        : await checkinHabit(habit.id)
    replaceHabit(updated)
    await loadHabitStatsOnly(true)
    await userStore.fetchUserInfo()
    uni.showToast({
      title: !habit.active ? '已启用' : updated.checkedToday ? '打卡成功' : '已撤销打卡',
      icon: 'success'
    })
    await notifyAchievementUnlocks(previousAchievementKeys, userStore.userInfo, await fetchAchievementMetrics())
  } catch (error) {
    console.error('切换习惯打卡失败', error)
    showActionError(error, !habit.active ? '启用失败' : habit.checkedToday ? '撤销失败' : '打卡失败')
  }
}

function replaceHabit(updated: HabitItem) {
  habits.value = habits.value.map((item) => (item.id === updated.id ? updated : item))
}

async function loadHabitStatsOnly(showFeedback = false) {
  try {
    stats.value = await getHabitStats()
  } catch (error) {
    console.error('加载习惯统计失败', error)
    if (showFeedback) {
      uni.showToast({
        title: '统计未同步',
        icon: 'none'
      })
    }
  }
}

function confirmDeleteHabit(habit: HabitItem) {
  return new Promise<boolean>((resolve) => {
    uni.showModal({
      title: '删除习惯',
      content: `将删除“${habit.name}”及其历史打卡记录，确认继续吗？`,
      confirmColor: '#e56767',
      success: (res) => resolve(Boolean(res.confirm)),
      fail: () => resolve(false)
    })
  })
}

</script>

<style scoped lang="scss">
@use '../../styles/flow-button.scss';

$card-bg: var(--wc-surface-strong);
$text-main: var(--wc-text);
$text-sub: var(--wc-text-soft);

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
  padding-bottom: 80rpx;
  box-sizing: border-box;
}

.page-desc {
  background: linear-gradient(135deg, rgba(74, 144, 226, 0.12), rgba(255, 255, 255, 0.92));
  margin: 20rpx 30rpx 0;
  padding: 24rpx 26rpx;
  border-radius: 28rpx;
  font-size: 24rpx;
  line-height: 1.7;
  color: $text-sub;
  display: flex;
  align-items: flex-start;
  gap: 12rpx;
  border: 1px solid var(--wc-line);
}

.calendar-strip {
  margin: 22rpx 30rpx 0;
  background: $card-bg;
  border-radius: 28rpx;
  padding: 18rpx;
  display: grid;
  grid-template-columns: repeat(7, minmax(0, 1fr));
  gap: 10rpx;
  box-shadow: var(--wc-shadow-soft);
  border: 1px solid var(--wc-line);
}

.calendar-day {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6rpx;
  min-width: 0;
  padding: 14rpx 0 12rpx;
  border-radius: 22rpx;
  background: rgba(28, 37, 54, 0.03);
}

.calendar-day.active {
  background: rgba(77, 138, 255, 0.12);
  color: #4d8aff;
}

.calendar-day.done .day-dot {
  background: #34c759;
}

.day-label {
  font-size: 20rpx;
  color: var(--wc-text-faint);
}

.day-num {
  font-size: 28rpx;
  font-weight: 700;
}

.day-progress {
  font-size: 18rpx;
  color: var(--wc-text-faint);
}

.day-dot {
  width: 10rpx;
  height: 10rpx;
  border-radius: 50%;
  background: rgba(28, 37, 54, 0.12);
}

.stats-card {
  background: $card-bg;
  margin: 24rpx 30rpx 0;
  border-radius: 32rpx;
  padding: 30rpx 22rpx;
  border: 1px solid var(--wc-line);
  box-shadow: var(--wc-shadow-soft);
}

.stats-row {
  display: flex;
  justify-content: space-around;
  text-align: center;
}

.stat-num {
  display: block;
  font-size: 40rpx;
  font-weight: 800;
  color: $text-main;
}

.stat-label {
  display: block;
  margin-top: 10rpx;
  font-size: 22rpx;
  color: var(--wc-text-faint);
}

.stats-footnote {
  margin-top: 24rpx;
  padding-top: 22rpx;
  border-top: 1px solid var(--wc-line);
  display: flex;
  justify-content: space-between;
  gap: 16rpx;
  font-size: 22rpx;
  color: $text-sub;
}

.reminder-card {
  margin: 22rpx 30rpx 0;
  padding: 24rpx;
  border-radius: 28rpx;
  background: linear-gradient(180deg, rgba(250, 252, 255, 0.98), rgba(241, 246, 255, 0.92));
  border: 1px solid var(--wc-line);
  box-shadow: var(--wc-shadow-soft);
}

.reminder-card__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16rpx;
}

.reminder-card__title {
  font-size: 28rpx;
  font-weight: 700;
  color: $text-main;
}

.reminder-card__tip {
  font-size: 22rpx;
  color: var(--wc-text-faint);
}

.reminder-chip-row {
  margin-top: 18rpx;
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14rpx;
}

.reminder-chip {
  min-width: 0;
  padding: 18rpx 20rpx;
  border-radius: 22rpx;
  background: rgba(255, 255, 255, 0.8);
  border: 1px solid rgba(77, 138, 255, 0.08);
}

.reminder-chip__label,
.reminder-chip__value {
  display: block;
}

.reminder-chip__label {
  font-size: 20rpx;
  color: var(--wc-text-faint);
}

.reminder-chip__value {
  margin-top: 8rpx;
  font-size: 28rpx;
  font-weight: 700;
  color: $text-main;
}

.habit-panel {
  margin: 0 30rpx;
  background: $card-bg;
  border-radius: 32rpx;
  border: 1px solid var(--wc-line);
  box-shadow: var(--wc-shadow-soft);
  padding: 28rpx;
}

.habit-panel--loading {
  text-align: center;
  font-size: 26rpx;
  color: $text-sub;
}

.habit-list {
  display: flex;
  flex-direction: column;
  gap: 18rpx;
  padding: 0 30rpx;
}

.page-bottom-space {
  height: 60rpx;
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

.form-group {
  margin-bottom: 22rpx;
}

.form-group:last-child {
  margin-bottom: 0;
}

.editor-tab-rail {
  width: 100%;
}

.icon-rail {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 12rpx;
}

.icon-option {
  height: 72rpx;
  border-radius: 20rpx;
  border: 1px solid var(--wc-line);
  background: var(--wc-surface-muted);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 34rpx;
}

.icon-option.active {
  border-color: rgba(77, 138, 255, 0.28);
  background: rgba(77, 138, 255, 0.1);
  box-shadow: 0 10rpx 24rpx rgba(77, 138, 255, 0.14);
}

.picker-field {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16rpx;
}

.picker-placeholder {
  color: #9aa4b2;
}

.picker-arrow {
  color: var(--wc-text-faint);
  font-size: 32rpx;
  line-height: 1;
}

.picker-clear {
  display: inline-block;
  margin-top: 12rpx;
}
</style>
