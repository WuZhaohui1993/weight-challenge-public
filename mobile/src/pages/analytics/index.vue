<template>
  <view class="analytics-container wc-page-enter">
    <view class="header wc-tab-header" :style="{ paddingTop: `${safeTop}px` }">
      <view class="header-body wc-tab-header__body" :style="{ minHeight: `${topBarHeight - safeTop}px`, paddingRight: `${menuButtonSafeRight}px` }">
        <text class="page-title">健康数据分析</text>
      </view>
    </view>

    <scroll-view
      class="scroll-content wc-tab-scroll wc-tab-scroll--top-gap wc-section-enter"
      scroll-y
      enable-flex
      :style="{ height: `calc(100vh - ${topBarHeight}px)` }"
    >
      <view class="refresh-meta refresh-meta--quiet">
        <text class="refresh-meta__text">{{ dailyRefreshMetaText }}</text>
      </view>

      <view v-if="hasPartialFailure" class="status-banner status-banner--warning">
        <view class="status-banner__copy">
          <text class="status-banner__title">有些数据没拿到</text>
          <text class="status-banner__desc">{{ partialFailureMessage }}</text>
        </view>
        <view class="status-banner__actions">
          <button
            v-if="dashboardLoadFailed"
            class="flow-btn flow-btn--secondary flow-btn--compact"
            @tap.stop="reloadDashboardSummary"
          >再试一次</button>
          <button
            v-if="recordLoadFailed"
            class="flow-btn flow-btn--secondary flow-btn--compact"
            @tap.stop="reloadRecentRecords"
          >刷新记录</button>
          <button
            v-if="habitStatsInitialized && habitStatsLoadFailed"
            class="flow-btn flow-btn--secondary flow-btn--compact"
            @tap.stop="reloadHabitAnalytics"
          >刷新习惯</button>
        </view>
      </view>

      <app-empty-state
        v-if="!isLoggedIn"
        icon="📅"
        title="登录后看自己的复盘日历"
        description="这里会按每天的体重、吃饭、喝水、运动和习惯打卡生成分析。"
      >
        <button class="flow-btn flow-btn--primary flow-btn--compact" @tap.stop="ensureLoggedInForAnalyticsAction()">登录后查看</button>
      </app-empty-state>

      <template v-else>
        <view class="calendar-hero">
          <view class="calendar-panel">
            <view class="calendar-toolbar">
              <text class="calendar-nav wc-pressable" @tap="switchCalendarMonth(-1)">‹</text>
              <view class="calendar-toolbar__center">
                <text class="calendar-month">{{ selectedMonthTitle }}</text>
                <text class="calendar-summary">{{ monthReviewSummary }}</text>
              </view>
              <text class="calendar-nav wc-pressable" @tap="switchCalendarMonth(1)">›</text>
            </view>

            <view class="calendar-weekdays">
              <text v-for="item in calendarWeekdays" :key="item">{{ item }}</text>
            </view>

            <view class="calendar-grid">
              <view
                v-for="cell in calendarCells"
                :key="cell.key"
                class="calendar-day wc-pressable"
                :class="[
                  cell.outside ? 'is-outside' : `is-${cell.status}`,
                  { 'is-active': cell.active, 'is-today': cell.today }
                ]"
                @tap="selectCalendarDay(cell)"
              >
                <text class="calendar-day__date">{{ cell.today ? '今' : cell.dayText }}</text>
                <text v-if="!cell.outside" class="calendar-day__weight">{{ cell.weightText }}</text>
              </view>
            </view>

            <view class="calendar-legend">
              <view
                v-for="item in calendarLegendItems"
                :key="item.status"
                class="calendar-legend__item"
              >
                <view class="calendar-legend__dot" :class="`is-${item.status}`"></view>
                <text>{{ item.label }}</text>
              </view>
            </view>

            <view class="calendar-month-footer">
              <text class="calendar-month-footer__change">{{ monthWeightChangeText }}</text>
              <text class="calendar-month-footer__summary">{{ monthViewedSummary }}</text>
            </view>

            <view class="calendar-progress">
              <view
                v-for="segment in calendarProgressSegments"
                :key="segment.status"
                class="calendar-progress__segment"
                :class="`is-${segment.status}`"
                :style="{ width: `${segment.percent}%` }"
              ></view>
            </view>
          </view>
        </view>

        <view class="analytics-tabs">
          <text
            v-for="tab in analyticsTabs"
            :key="tab.key"
            class="analytics-tab wc-pressable"
            :class="{ active: activeTab === tab.key }"
            @tap="selectAnalyticsTab(tab.key)"
          >{{ tab.label }}</text>
        </view>

        <template v-if="activeTab === 'review'">
          <view class="day-review" :class="`day-review--${selectedDailyAnalysis.status}`">
            <view class="day-review__head">
              <view class="day-review__copy">
                <text class="day-review__eyebrow">这天解释</text>
                <text class="day-review__title">{{ selectedDailyAnalysis.title }}</text>
                <text class="day-review__desc">{{ selectedDailyAnalysis.text }}</text>
              </view>
              <view class="day-review__status">
                <text>{{ selectedDailyAnalysis.statusText }}</text>
              </view>
            </view>
            <view class="summary-grid">
              <view
                v-for="card in dailySummaryCards"
                :key="card.label"
                class="summary-card"
              >
                <text class="summary-card__label">{{ card.label }}</text>
                <text class="summary-card__value">{{ card.value }}</text>
                <text class="summary-card__meta">{{ card.meta }}</text>
              </view>
            </view>
          </view>

          <view class="chart-section">
            <view class="section-header section-header--stack">
              <view class="section-title-group">
                <text class="section-kicker">这天依据</text>
                <text class="section-title">为什么这样判断</text>
                <text class="section-desc">只看选中这一天，不把其他日期混进来。</text>
              </view>
            </view>

            <view class="composition-insights">
              <view
                v-for="item in reviewReasonCards"
                :key="item.title"
                class="composition-insight"
              >
                <view class="composition-insight__top">
                  <text class="composition-insight__title">{{ item.title }}</text>
                  <text class="composition-insight__tag" :class="`composition-insight__tag--${item.tone}`">{{ item.tag }}</text>
                </view>
                <text class="composition-insight__desc">{{ item.description }}</text>
              </view>
            </view>
          </view>
        </template>

        <template v-else-if="activeTab === 'body'">
          <view class="chart-section chart-section--priority">
            <view class="section-header section-header--stack">
              <view class="section-title-group">
                <text class="section-kicker">身体焦点</text>
                <text class="section-title">{{ bodyDailyTitle }}</text>
                <text class="section-desc">{{ bodyDailyDesc }}</text>
              </view>
            </view>
            <view class="summary-grid">
              <view
                v-for="card in bodyDailyCards"
                :key="card.label"
                class="summary-card"
              >
                <text class="summary-card__label">{{ card.label }}</text>
                <text class="summary-card__value">{{ card.value }}</text>
                <text class="summary-card__meta">{{ card.meta }}</text>
              </view>
            </view>
          </view>

          <view class="chart-section">
            <view class="section-header section-header--stack">
              <view class="section-title-group">
                <text class="section-kicker">这天依据</text>
                <text class="section-title">这天身体怎么判断</text>
                <text class="section-desc">只看这一天的体重记录和 BMI。</text>
              </view>
            </view>
            <view class="composition-insights">
              <view
                v-for="item in bodyReasonCards"
                :key="item.title"
                class="composition-insight"
              >
                <view class="composition-insight__top">
                  <text class="composition-insight__title">{{ item.title }}</text>
                  <text class="composition-insight__tag" :class="`composition-insight__tag--${item.tone}`">{{ item.tag }}</text>
                </view>
                <text class="composition-insight__desc">{{ item.description }}</text>
              </view>
            </view>
          </view>

          <view class="chart-section">
            <view class="section-header section-header--stack">
              <view class="section-title-group">
                <text class="section-kicker">记录</text>
                <text class="section-title">{{ selectedDateLabel }}身体记录</text>
                <text class="section-desc">只展示这一天的身体记录。</text>
              </view>
            </view>

            <view class="recent-record-list">
              <view class="recent-record-hub">
                <view class="recent-record-hub__summary">
                  <view class="recent-record-hub__copy">
                    <text class="recent-record-hub__eyebrow">这天记录</text>
                    <text class="recent-record-hub__title">{{ bodyRecordHubTitle }}</text>
                    <text class="recent-record-hub__desc">只看这一天，不混入其他日期。</text>
                  </view>
                  <view class="recent-record-hub__meta">
                    <text class="recent-record-hub__meta-value">{{ dailyBodyRecords.length }}</text>
                    <text class="recent-record-hub__meta-label">条记录</text>
                  </view>
                </view>

                <view
                  v-for="record in dailyBodyRecords"
                  :key="record.id"
                  class="recent-record-entry wc-pressable"
                  @tap="openRecordDetail(record)"
                >
                  <view class="recent-record__icon" :class="record.type">{{ record.icon }}</view>
                  <view class="recent-record__body">
                    <view class="recent-record-entry__meta">
                      <text class="recent-record-entry__type">{{ recordTypeLabel(record.type) }}</text>
                      <text class="recent-record__time">{{ record.timeText }}</text>
                    </view>
                    <view class="recent-record__title-row">
                      <text class="recent-record__title">{{ record.title }}</text>
                      <text class="recent-record-entry__arrow">›</text>
                    </view>
                    <text class="recent-record__note">{{ record.summaryText }}</text>
                  </view>
                </view>

                <view v-if="!dailyBodyRecords.length" class="recent-record-empty">
                  这天还没有体重记录，补完后这里会显示当天明细。
                </view>
                <button v-if="!dailyBodyRecords.length" class="flow-btn flow-btn--primary flow-btn--wide" @tap="goRecordPage('weight')">补体重记录</button>
              </view>
            </view>
          </view>
        </template>

        <template v-else-if="activeTab === 'lifestyle'">
          <view class="chart-section">
            <view class="section-header section-header--stack">
              <view class="section-title-group">
                <text class="section-kicker">生活执行</text>
                <text class="section-title">{{ lifestyleDailyTitle }}</text>
                <text class="section-desc">{{ lifestyleDailyDesc }}</text>
              </view>
            </view>

            <view class="habit-analysis-hero" :class="`habit-analysis-hero--${lifestyleDailyTone}`">
              <view class="habit-analysis-hero__copy">
                <text class="habit-analysis-hero__eyebrow">这天生活执行</text>
                <text class="habit-analysis-hero__title">{{ lifestyleDailyHeadline }}</text>
                <text class="habit-analysis-hero__desc">{{ lifestyleDailyNote }}</text>
              </view>
              <view class="habit-analysis-hero__rate">
                <text class="habit-analysis-hero__rate-value">{{ lifestyleDailyScore }}</text>
                <text class="habit-analysis-hero__rate-label">这天得分</text>
              </view>
            </view>
            <view class="summary-grid">
              <view
                v-for="card in lifestyleDailyCards"
                :key="card.label"
                class="summary-card"
              >
                <text class="summary-card__label">{{ card.label }}</text>
                <text class="summary-card__value">{{ card.value }}</text>
                <text class="summary-card__meta">{{ card.meta }}</text>
              </view>
            </view>
          </view>

          <view class="chart-section">
            <view class="section-header section-header--stack">
              <view class="section-title-group">
                <text class="section-kicker">这天依据</text>
                <text class="section-title">吃喝运动怎么看</text>
                <text class="section-desc">按选中日期的晚餐、饮水和运动记录判断。</text>
              </view>
            </view>
            <view class="composition-insights">
              <view
                v-for="item in lifestyleReasonCards"
                :key="item.title"
                class="composition-insight"
              >
                <view class="composition-insight__top">
                  <text class="composition-insight__title">{{ item.title }}</text>
                  <text class="composition-insight__tag" :class="`composition-insight__tag--${item.tone}`">{{ item.tag }}</text>
                </view>
                <text class="composition-insight__desc">{{ item.description }}</text>
              </view>
            </view>
          </view>

          <view class="chart-section">
            <view class="section-header section-header--stack">
              <view class="section-title-group">
                <text class="section-kicker">记录</text>
                <text class="section-title">{{ selectedDateLabel }}生活记录</text>
                <text class="section-desc">只看这一天的晚餐、饮水和运动。</text>
              </view>
            </view>
            <view class="recent-record-list">
              <view class="recent-record-hub">
                <view class="recent-record-hub__summary">
                  <view class="recent-record-hub__copy">
                    <text class="recent-record-hub__eyebrow">这天记录</text>
                    <text class="recent-record-hub__title">{{ lifestyleRecordHubTitle }}</text>
                    <text class="recent-record-hub__desc">这里不再混入其他日期。</text>
                  </view>
                  <view class="recent-record-hub__meta">
                    <text class="recent-record-hub__meta-value">{{ dailyLifestyleRecords.length }}</text>
                    <text class="recent-record-hub__meta-label">条记录</text>
                  </view>
                </view>

                <view
                  v-for="record in dailyLifestyleRecords"
                  :key="record.id"
                  class="recent-record-entry wc-pressable"
                  @tap="openRecordDetail(record)"
                >
                  <view class="recent-record__icon" :class="record.type">{{ record.icon }}</view>
                  <view class="recent-record__body">
                    <view class="recent-record-entry__meta">
                      <text class="recent-record-entry__type">{{ recordTypeLabel(record.type) }}</text>
                      <text class="recent-record__time">{{ record.timeText }}</text>
                    </view>
                    <view class="recent-record__title-row">
                      <text class="recent-record__title">{{ record.title }}</text>
                      <text class="recent-record-entry__arrow">›</text>
                    </view>
                    <text class="recent-record__note">{{ record.summaryText }}</text>
                  </view>
                </view>

                <view v-if="!dailyLifestyleRecords.length" class="recent-record-empty">
                  这天还没有生活记录，补完晚餐、饮水或运动后再回来看。
                </view>
                <button v-if="!dailyLifestyleRecords.length" class="flow-btn flow-btn--primary flow-btn--wide" @tap="goRecordPage('diet')">补生活记录</button>
              </view>
            </view>
          </view>
        </template>

        <template v-else>
          <view class="chart-section">
            <view class="section-header section-header--stack">
              <view class="section-title-group">
                <text class="section-kicker">习惯</text>
                <text class="section-title">{{ habitDailyTitle }}</text>
                <text class="section-desc">{{ habitDailyDesc }}</text>
              </view>
            </view>
            <view class="habit-analysis-hero" :class="`habit-analysis-hero--${habitDailyTone}`">
              <view class="habit-analysis-hero__copy">
                <text class="habit-analysis-hero__eyebrow">{{ habitDailyEyebrow }}</text>
                <text class="habit-analysis-hero__title">{{ habitDailyHeadline }}</text>
                <text class="habit-analysis-hero__desc">{{ habitDailyNote }}</text>
              </view>
              <view class="habit-analysis-hero__rate">
                <text class="habit-analysis-hero__rate-value">{{ habitDailyRate }}</text>
                <text class="habit-analysis-hero__rate-label">完成情况</text>
              </view>
            </view>

            <view class="summary-grid">
              <view
                v-for="card in habitDailyCards"
                :key="card.label"
                class="summary-card"
              >
                <text class="summary-card__label">{{ card.label }}</text>
                <text class="summary-card__value">{{ card.value }}</text>
                <text class="summary-card__meta">{{ card.meta }}</text>
              </view>
            </view>
          </view>

          <view class="chart-section">
            <view class="section-header section-header--stack">
              <view class="section-title-group">
                <text class="section-kicker">这天依据</text>
                <text class="section-title">习惯当天怎么算</text>
                <text class="section-desc">按照选中日期的打卡记录展示。</text>
              </view>
            </view>
            <view class="composition-insights">
              <view
                v-for="item in habitReasonCards"
                :key="item.title"
                class="composition-insight"
              >
                <view class="composition-insight__top">
                  <text class="composition-insight__title">{{ item.title }}</text>
                  <text class="composition-insight__tag" :class="`composition-insight__tag--${item.tone}`">{{ item.tag }}</text>
                </view>
                <text class="composition-insight__desc">{{ item.description }}</text>
              </view>
            </view>
          </view>

          <view class="chart-section">
            <view class="section-header section-header--stack">
              <view class="section-title-group">
                <text class="section-kicker">记录</text>
                <text class="section-title">{{ selectedDateLabel }}习惯清单</text>
                <text class="section-desc">只展示这一天要做和完成的习惯。</text>
              </view>
            </view>
            <view class="recent-record-list">
              <view class="recent-record-hub">
                <view class="recent-record-hub__summary">
                  <view class="recent-record-hub__copy">
                    <text class="recent-record-hub__eyebrow">这天清单</text>
                    <text class="recent-record-hub__title">{{ habitRecordHubTitle }}</text>
                    <text class="recent-record-hub__desc">这里只看选中日期。</text>
                  </view>
                  <view class="recent-record-hub__meta">
                    <text class="recent-record-hub__meta-value">{{ selectedDailyAnalysis.habit.completedCount }}/{{ selectedDailyAnalysis.habit.dueCount }}</text>
                    <text class="recent-record-hub__meta-label">完成</text>
                  </view>
                </view>

                <view
                  v-for="record in dailyHabitRecords"
                  :key="record.id"
                  class="recent-record-entry wc-pressable"
                  @tap="openRecordDetail(record)"
                >
                  <view class="recent-record__icon habit">{{ record.icon }}</view>
                  <view class="recent-record__body">
                    <view class="recent-record-entry__meta">
                      <text class="recent-record-entry__type">习惯</text>
                      <text class="recent-record__time">{{ record.timeText }}</text>
                    </view>
                    <view class="recent-record__title-row">
                      <text class="recent-record__title">{{ record.title }}</text>
                      <text class="recent-record-entry__arrow">›</text>
                    </view>
                    <text class="recent-record__note">{{ record.summaryText }}</text>
                  </view>
                </view>

                <view v-if="!dailyHabitRecords.length" class="recent-record-empty">
                  这天没有习惯打卡记录。
                </view>
              </view>
            </view>
          </view>
        </template>
      </template>

      <view class="page-bottom-space"></view>
    </scroll-view>

    <view v-if="selectedRecord" class="detail-mask" @tap="closeRecordDetail">
      <view class="detail-modal" @tap.stop>
        <view class="detail-header">
          <view>
            <text class="detail-title">{{ selectedRecord.detailTitle }}</text>
            <text class="detail-subtitle">{{ selectedRecord.detailSubtitle }}</text>
          </view>
          <text class="detail-close wc-inline-link wc-inline-link--action wc-inline-link--subtle" @tap="closeRecordDetail">关闭</text>
        </view>

        <scroll-view class="detail-body" scroll-y>
          <view v-for="item in selectedRecord.details" :key="item.label" class="detail-row">
            <text class="detail-row__label">{{ item.label }}</text>
            <text class="detail-row__value">{{ item.value }}</text>
          </view>

          <view v-if="selectedRecordImages.length" class="detail-images">
            <view class="detail-images__header">
              <text class="detail-row__label">记录图片</text>
              <text class="detail-images__count">{{ selectedRecordImages.length }} 张</text>
            </view>
            <view class="detail-image-grid" :class="`grid-${Math.min(selectedRecordImages.length, 3)}`">
              <resolved-image
                v-for="(image, index) in selectedRecordImages"
                :key="`${selectedRecord.id}-image-${index}`"
                class="detail-image"
                :src="image"
                mode="aspectFill"
                :preview-list="selectedRecordImages"
                :preview-index="index"
              />
            </view>
          </view>
        </scroll-view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import AppEmptyState from '@/components/AppEmptyState/index.vue'
import ResolvedImage from '@/components/ResolvedImage/index.vue'
import { getHabitCheckinList, getHabitStats } from '@/api/habit'
import { useUserStore } from '@/stores/user'
import http, { openLoginPage } from '@/utils/request'
import { getExerciseRecordList, getFoodRecordList, getWaterRecordList, getWeightRecordList } from '@/api/record'
import type {
  DashboardPayload,
  ExerciseRecordPayload,
  FoodRecordPayload,
  HabitCheckinPayload,
  HabitStats,
  WaterRecordPayload,
  WeightRecordPayload
} from '@/types/api'
import { formatApiDateTime, toApiDateKey, toApiTimestamp } from '@/utils/datetime'
import { getMenuButtonSafeRight, getSafeTop, getTopBarHeight, syncCustomTabBar } from '@/utils/mobile'
import { useTabBarOverlayVisibility } from '@/utils/page'
import { useDefaultPageShare } from '@/utils/share'

useDefaultPageShare()

const safeTop = getSafeTop(20)
const topBarHeight = getTopBarHeight(56)
const menuButtonSafeRight = getMenuButtonSafeRight(16, 18)
const userStore = useUserStore()
const isLoggedIn = computed(() => userStore.isLoggedIn)
type AnalyticsTabKey = 'review' | 'body' | 'lifestyle' | 'habit'
type TrendRangeKey = 'day' | 'week' | 'month'
type AnalyticsHeadlineTone = 'focus' | 'good' | 'caution' | 'missing'
type AnalyticsHeadline = {
  title: string
  description: string
  tag: string
  tone: AnalyticsHeadlineTone
}
type AnalyticsInsightCard = {
  title: string
  description: string
  tag?: string
  tone?: 'idle' | 'steady' | 'down' | 'up' | 'missing' | 'neutral'
}
type AnalyticsSummaryCard = {
  label: string
  value: string
  meta: string
}
type DailyStatus = 'stable' | 'wave' | 'missing' | 'future'
type CalendarProgressSegment = {
  status: DailyStatus
  percent: number
}
type CalendarCell = {
  key: string
  dateKey: string
  day: number
  dayText: string
  outside: boolean
  active: boolean
  today: boolean
  status: DailyStatus
  weightText: string
}
type DailyReasonCard = {
  title: string
  description: string
  tag: string
  tone: 'good' | 'warn' | 'missing' | 'future'
}
type BodyFatReminderCard = {
  tone: 'idle' | 'steady' | 'down' | 'up' | 'missing'
  tag: string
  title: string
  description: string
}
type AnalyticsRecordItem = {
  id: string
  type: 'weight' | 'food' | 'exercise' | 'water' | 'habit'
  icon: string
  title: string
  subtitle: string
  summaryText: string
  timeText: string
  detailTitle: string
  detailSubtitle: string
  details: Array<{ label: string; value: string }>
  recordImages: string[]
  sortTime: number
}
type DailyAnalysisItem = {
  dateKey: string
  dateLabel: string
  day: number
  status: DailyStatus
  statusText: string
  title: string
  text: string
  weightChangeText: string
  dinnerPercent: number | null
  waterPercent: number | null
  exerciseMinutes: number
  missing: string[]
  body: {
    records: AnalyticsRecordItem[]
    weight: string
    bmi: string
    bmiStatus: string
  }
  lifestyle: {
    records: AnalyticsRecordItem[]
    foodRecords: FoodRecordPayload[]
    waterRecords: WaterRecordPayload[]
    exerciseRecords: ExerciseRecordPayload[]
  }
  habit: {
    dueCount: number
    completedCount: number
    records: AnalyticsRecordItem[]
  }
}
type LifestyleProgressDay = {
  date: string
  label: string
  day: string
  active: boolean
  done: boolean
  fillHeight: number
  score: number
  scoreLabel: string
}
type LifestyleTrackKey = 'diet' | 'exercise' | 'water'
type RecordHistoryTypeKey = 'all' | 'weight' | 'food' | 'exercise' | 'water' | 'habit'
type RecordHistoryRangeKey = '7d' | '30d' | '90d' | 'all'
type RecordHistorySourceKey = 'body' | 'lifestyle'
type LifestyleTrackCard = {
  key: LifestyleTrackKey
  title: string
  description: string
  summaryValue: string
  summaryLabel: string
  tone: 'diet' | 'exercise' | 'water'
  historyType: RecordHistoryTypeKey
  actionLabel: string
  days: LifestyleProgressDay[]
}
type BodySignalChip = {
  label: string
  value: string
  tone?: 'positive' | 'warning' | 'neutral'
}

const activeTab = ref<AnalyticsTabKey>('review')
const selectedMonthDate = ref(getMonthStart(new Date()))
const selectedDateKey = ref(formatDateParam(new Date()))
const dashboard = ref<DashboardPayload | null>(null)
const latestWeightRecord = ref<WeightRecordPayload | null>(null)
const weightRecords = ref<WeightRecordPayload[]>([])
const trendWeightRecords = ref<WeightRecordPayload[]>([])
const foodRecords = ref<FoodRecordPayload[]>([])
const exerciseRecords = ref<ExerciseRecordPayload[]>([])
const waterRecords = ref<WaterRecordPayload[]>([])
const lifestyleFoodRecords = ref<FoodRecordPayload[]>([])
const lifestyleExerciseRecords = ref<ExerciseRecordPayload[]>([])
const lifestyleWaterRecords = ref<WaterRecordPayload[]>([])
const habitCheckins = ref<HabitCheckinPayload[]>([])
const habitStats = ref<HabitStats | null>(null)
const trendRange = ref<TrendRangeKey>('day')
const recordLoading = ref(false)
const recordLoadFailed = ref(false)
const dashboardLoading = ref(false)
const dashboardLoadFailed = ref(false)
const lifestyleHistoryLoading = ref(false)
const lifestyleHistoryLoadFailed = ref(false)
const lifestyleHistoryInitialized = ref(false)
const habitStatsLoading = ref(false)
const habitStatsLoadFailed = ref(false)
const habitStatsInitialized = ref(false)
const lastDashboardRefreshAt = ref('')
const lastRecordRefreshAt = ref('')
const lastLifestyleRefreshAt = ref('')
const lastHabitRefreshAt = ref('')

const trendRangeOptions: Array<{ key: TrendRangeKey; label: string }> = [
  { key: 'day', label: '日' },
  { key: 'week', label: '周' },
  { key: 'month', label: '月' }
]

const analyticsTabs: Array<{ key: AnalyticsTabKey; label: string }> = [
  { key: 'review', label: '这天解释' },
  { key: 'body', label: '身体数据' },
  { key: 'lifestyle', label: '生活执行' },
  { key: 'habit', label: '习惯复盘' }
]

onShow(() => {
  syncCustomTabBar(2)
  syncAnalyticsTabBarVisibility()
  void loadAnalyticsData()
})

watch(activeTab, (tab) => {
  if (tab === 'lifestyle') {
    void ensureLifestyleHistoryLoaded()
  }
  if (tab === 'habit') {
    void ensureHabitStatsLoaded()
  }
})

const selectedRecord = ref<AnalyticsRecordItem | null>(null)
const selectedRecordImages = computed(() => {
  if (!selectedRecord.value) {
    return []
  }
  return selectedRecord.value.recordImages
})
const syncAnalyticsTabBarVisibility = useTabBarOverlayVisibility(computed(() => Boolean(selectedRecord.value)))

const hasPartialFailure = computed(
  () =>
    dashboardLoadFailed.value ||
    recordLoadFailed.value ||
    (lifestyleHistoryInitialized.value && lifestyleHistoryLoadFailed.value) ||
    (habitStatsInitialized.value && habitStatsLoadFailed.value)
)
const isRefreshingAll = computed(
  () => dashboardLoading.value || recordLoading.value || lifestyleHistoryLoading.value || habitStatsLoading.value
)
const partialFailureMessage = computed(() => {
  if (dashboardLoadFailed.value && recordLoadFailed.value) {
    return '总览和最近记录都还没更新出来。'
  }
  if (dashboardLoadFailed.value) {
    return '总览加载失败，其他内容可继续查看。'
  }
  if (recordLoadFailed.value) {
    return '最近记录加载失败，身体总览可继续查看。'
  }
  if (lifestyleHistoryInitialized.value && lifestyleHistoryLoadFailed.value) {
    return '生活执行复盘加载失败，身体总览和习惯复盘可继续查看。'
  }
  if (habitStatsInitialized.value && habitStatsLoadFailed.value) {
    return '习惯复盘加载失败，身体和生活数据可继续查看。'
  }
  return ''
})
const refreshMetaText = computed(() => {
  if (!isLoggedIn.value) {
    return '登录后同步个人分析'
  }
  if (isRefreshingAll.value) {
    return '正在刷新数据...'
  }
  const parts: string[] = []
  if (lastDashboardRefreshAt.value) {
    parts.push(`总览 ${lastDashboardRefreshAt.value}`)
  }
  if (lastRecordRefreshAt.value) {
    parts.push(`记录 ${lastRecordRefreshAt.value}`)
  }
  if (lifestyleHistoryInitialized.value && lastLifestyleRefreshAt.value) {
    parts.push(`生活 ${lastLifestyleRefreshAt.value}`)
  }
  if (habitStatsInitialized.value && lastHabitRefreshAt.value) {
    parts.push(`习惯 ${lastHabitRefreshAt.value}`)
  }
  return parts.length ? parts.join(' · ') : '尚未刷新'
})
const refreshActionText = computed(() => (isLoggedIn.value ? '整理全部' : '登录后查看'))

const sharedRetryText = '重新整理'
const calendarWeekdays = ['一', '二', '三', '四', '五', '六', '日']
const calendarLegendItems: Array<{ status: DailyStatus; label: string }> = [
  { status: 'stable', label: '稳了' },
  { status: 'wave', label: '波动' },
  { status: 'missing', label: '待补' },
  { status: 'future', label: '还没到' }
]

const summary = computed(() => dashboard.value?.summary ?? null)
const currentUserGender = computed(() => userStore.userInfo?.gender || '0')
const latestBodyFatRecord = computed(() => {
  if (latestWeightRecord.value?.recordedAt) {
    return latestWeightRecord.value
  }
  const sorted = [...trendWeightRecords.value]
    .filter((record) => record.recordedAt)
    .sort(compareWeightRecordsDesc)
  return sorted[0] || null
})

const latestMeasuredBodyFatRecord = computed(() => {
  const sorted = [...trendWeightRecords.value]
    .filter((record) => record.recordedAt && record.bodyFatRate !== null && record.bodyFatRate !== undefined)
    .sort(compareWeightRecordsDesc)
  return sorted[0] || null
})

const metrics = computed(() => {
  const bmi = summary.value?.bmi
  const bodyFatRate = latestBodyFatRecord.value?.bodyFatRate ?? null
  return {
    weight: summary.value?.currentWeight ? `${summary.value.currentWeight}` : '--',
    weightChange: formatWeightChange(),
    bmi: bmi ? `${bmi}` : '--',
    bmiStatus: bmiStatusText(bmi),
    bodyFatRate: bodyFatRate !== null && bodyFatRate !== undefined ? `${bodyFatRate}%` : '--',
    bodyFatStatus: bodyFatStatusText(bodyFatRate, currentUserGender.value)
  }
})

const weightChangeBadgeClass = computed(() => {
  const value = metrics.value.weightChange
  if (value === '待对比' || value === '持平') {
    return 'wc-badge--soft'
  }
  if (value.startsWith('-')) {
    return 'wc-badge--success'
  }
  if (value.startsWith('+')) {
    return 'wc-badge--danger'
  }
  return 'wc-badge--soft'
})

const bmiBadgeClass = computed(() => {
  const bmi = summary.value?.bmi
  if (!bmi) return 'wc-badge--soft'
  if (bmi < 18.5) return 'wc-badge--warning'
  if (bmi < 24) return 'wc-badge--success'
  return 'wc-badge--danger'
})

const bodyFatBadgeClass = computed(() => bodyFatBadgeClassName(latestBodyFatRecord.value?.bodyFatRate ?? null, currentUserGender.value))

const bodyFatMetricNote = computed(() => {
  if (!latestBodyFatRecord.value?.recordedAt) {
    return '记录体脂率后，会展示最近一次测量结果。'
  }
  if (latestBodyFatRecord.value.bodyFatRate === null || latestBodyFatRecord.value.bodyFatRate === undefined) {
    return `${formatRecordTime(latestBodyFatRecord.value.recordedAt) || '最近一次记录'}未填写体脂率。`
  }
  const timeText = formatRecordTime(latestBodyFatRecord.value.recordedAt)
  if (currentUserGender.value === '0') {
    return `${timeText || '最近一次记录'}，补充性别后可显示更准确的区间判断。`
  }
  return `${timeText || '最近一次记录'}，按${currentUserGender.value === '1' ? '男性' : '女性'}口径判断。`
})

const currentTrendRangeLabel = computed(() => {
  const matched = trendRangeOptions.find((item) => item.key === trendRange.value)
  return matched ? `按${matched.label}` : '按日'
})

const bodyFocusTone = computed<AnalyticsHeadlineTone>(() => analyticsHeadline.value.tone)

const bodyFocusTitle = computed(() => analyticsHeadline.value.title)

const bodyFocusDescription = computed(() => {
  if (!trendChartPoints.value.length) {
    return analyticsHeadline.value.description
  }
  if (analyticsHeadline.value.tone === 'focus' || analyticsHeadline.value.tone === 'good') {
    return trendCardSummary.value
  }
  return analyticsHeadline.value.description
})

const bodySummaryCards = computed<AnalyticsSummaryCard[]>(() => [
  {
    label: '观察周期',
    value: currentTrendRangeLabel.value,
    meta: '体重与体脂都沿用这个周期'
  },
  {
    label: '体重变化',
    value: metrics.value.weightChange,
    meta: metrics.value.weight === '--' ? '等待体重记录' : `当前 ${metrics.value.weight} kg`
  },
  {
    label: 'BMI判断',
    value: metrics.value.bmiStatus,
    meta: metrics.value.bmi === '--' ? '等待系统计算' : `当前 BMI ${metrics.value.bmi}`
  },
  {
    label: '体脂提醒',
    value: bodyFatReminder.value.tag,
    meta: metrics.value.bodyFatRate === '--' ? '还没有最新体脂率' : `当前 ${metrics.value.bodyFatRate}`
  }
])

const analyticsHeadline = computed<AnalyticsHeadline>(() => {
  if (!isLoggedIn.value) {
    return {
      title: '登录后生成个人健康分析',
      description: '当前可先浏览分析页结构，登录后会按你的记录生成趋势、复盘和提醒。',
      tag: '游客预览',
      tone: 'missing'
    }
  }

  if (!latestWeightRecord.value?.recordedAt && metrics.value.weight === '--') {
    return {
      title: '先记录第一条体重，身体看板才会开始形成',
      description: '当前没有最新体重，BMI、趋势和联动解读都缺少基线。',
      tag: '待建基线',
      tone: 'missing'
    }
  }

  if (bodyFatReminder.value.tone === 'missing' || bodyFatReminder.value.tone === 'idle') {
    return {
      title: bodyFatReminder.value.title,
      description: bodyFatReminder.value.description,
      tag: bodyFatReminder.value.tag,
      tone: 'missing'
    }
  }

  if (bodyFatReminder.value.tone === 'up') {
    return {
      title: bodyFatReminder.value.title,
      description: bodyFatReminder.value.description,
      tag: bodyFatReminder.value.tag,
      tone: 'caution'
    }
  }

  if (bodyFatReminder.value.tone === 'down') {
    return {
      title: bodyFatReminder.value.title,
      description: bodyFatReminder.value.description,
      tag: bodyFatReminder.value.tag,
      tone: 'good'
    }
  }

  const leadInsight = bodyCompositionInsights.value[0]
  if (leadInsight && leadInsight.title !== '三指标联动解读还在积累中') {
    const caution =
      metrics.value.bmiStatus === '超重' ||
      metrics.value.bmiStatus === '肥胖' ||
      metrics.value.bodyFatStatus === '偏高' ||
      metrics.value.bodyFatStatus === '肥胖'
    return {
      title: leadInsight.title,
      description: leadInsight.description,
      tag: metrics.value.bodyFatStatus === '未填写' ? metrics.value.bmiStatus : metrics.value.bodyFatStatus,
      tone: caution ? 'caution' : 'focus'
    }
  }

  if (waterStats.value.completion < 70) {
    return {
      title: '今天的补水进度还偏慢',
      description: `当前只完成 ${waterStats.value.today}/${waterStats.value.target} 杯，补水不足会放大体重波动噪音。`,
      tag: '补水偏少',
      tone: 'focus'
    }
  }

  if (calorieStats.value.overTarget > 0) {
    return {
      title: '今天的热量摄入已经超过摄入上限',
      description: `当前已超出 ${calorieStats.value.overTarget} kcal，后面更值得回看饮食记录而不是再加更多模块。`,
      tag: '热量超标',
      tone: 'caution'
    }
  }

  return {
    title: '当前身体数据整体平稳',
    description: '继续保持连续记录，系统会更稳定地识别你的真实变化方向。',
    tag: metrics.value.weightChange === '待对比' ? '继续记录' : metrics.value.weightChange,
    tone: 'focus'
  }
})

const ANALYTICS_CHART_WIDTH = 560
const ANALYTICS_CHART_HEIGHT = 132
const EXERCISE_BASELINE_MINUTES = 30

const trendBuckets = computed(() => buildTrendBuckets(trendWeightRecords.value, trendRange.value, 'weight'))
const bodyFatTrendBuckets = computed(() => buildTrendBuckets(trendWeightRecords.value, trendRange.value, 'bodyFatRate'))

const trendChartPoints = computed(() => buildTrendChartPoints(trendBuckets.value, (value) => `${formatTrendWeight(value)}kg`))
const bodyFatChartPoints = computed(() => buildTrendChartPoints(bodyFatTrendBuckets.value, (value) => `${formatTrendBodyFat(value)}%`))

const trendChartSegments = computed(() => buildTrendChartSegments(trendChartPoints.value))
const bodyFatChartSegments = computed(() => buildTrendChartSegments(bodyFatChartPoints.value))

const trendCardSummary = computed(() => {
  if (!trendBuckets.value.length) {
    return '记录体重后即可查看趋势'
  }
  const latest = trendBuckets.value[trendBuckets.value.length - 1]
  const previous = trendBuckets.value.length > 1 ? trendBuckets.value[trendBuckets.value.length - 2] : null
  if (!previous) {
    return `${formatTrendWeight(latest.value)} kg`
  }
  const diff = Number((latest.value - previous.value).toFixed(1))
  if (diff === 0) {
    return `${formatTrendWeight(latest.value)} kg，较上一周期持平`
  }
  return `${formatTrendWeight(latest.value)} kg，较上一周期${diff > 0 ? '上升' : '下降'} ${Math.abs(diff)} kg`
})

const bodyTrendHeaderSummary = computed(() => {
  if (!trendBuckets.value.length) {
    return `${currentTrendRangeLabel.value}观察中，记录体重后会生成趋势摘要。`
  }
  return `${currentTrendRangeLabel.value}观察 · ${trendCardSummary.value}`
})

const trendCardDescription = computed(() => {
  if (trendRange.value === 'day') {
    return '按天查看最近 7 个记录日的最后一条体重。'
  }
  if (trendRange.value === 'week') {
    return '按周聚合查看最近 8 周的阶段变化。'
  }
  return '按月聚合查看最近 6 个月的体重走向。'
})

const bodyFatTrendSummary = computed(() => {
  if (!bodyFatTrendBuckets.value.length) {
    return '记录体脂率后即可查看趋势'
  }
  const latest = bodyFatTrendBuckets.value[bodyFatTrendBuckets.value.length - 1]
  const previous = bodyFatTrendBuckets.value.length > 1 ? bodyFatTrendBuckets.value[bodyFatTrendBuckets.value.length - 2] : null
  const statusText = bodyFatStatusText(latest.value, currentUserGender.value)
  if (!previous) {
    return `${formatTrendBodyFat(latest.value)}%，当前处于${statusText}`
  }
  const diff = Number((latest.value - previous.value).toFixed(1))
  if (diff === 0) {
    return `${formatTrendBodyFat(latest.value)}%，较上一周期持平，当前处于${statusText}`
  }
  return `${formatTrendBodyFat(latest.value)}%，较上一周期${diff > 0 ? '上升' : '下降'} ${Math.abs(diff)}%，当前处于${statusText}`
})

const bodyFatTrendDescription = computed(() => {
  if (currentUserGender.value === '0') {
    return '沿用上方日/周/月切换查看趋势，补充性别后可显示更准确的区间标签。'
  }
  return `沿用上方日/周/月切换查看趋势，按${currentUserGender.value === '1' ? '男性' : '女性'}口径标注区间。`
})

const bodyFatReminder = computed<BodyFatReminderCard>(() => {
  const latestRecord = latestWeightRecord.value
  const latestMeasuredRecord = latestMeasuredBodyFatRecord.value
  if (!latestRecord?.recordedAt) {
    return {
      tone: 'idle',
      tag: '等待记录',
      title: '还没有可用的体脂提醒',
      description: '先记录一条体重或体脂率，系统才会开始判断变化方向。'
    }
  }

  if (latestRecord.bodyFatRate === null || latestRecord.bodyFatRate === undefined) {
    const referenceText = latestMeasuredRecord?.bodyFatRate !== null && latestMeasuredRecord?.bodyFatRate !== undefined
      ? `最近一次有效体脂率仍参考 ${formatRecordTime(latestMeasuredRecord.recordedAt) || '较早记录'} 的 ${formatTrendBodyFat(latestMeasuredRecord.bodyFatRate)}%。`
      : '目前还没有任何有效体脂率记录。'
    return {
      tone: 'missing',
      tag: '待补记录',
      title: '最新一条体重记录没有填写体脂率',
      description: `${formatRecordTime(latestRecord.recordedAt) || '最近一次记录'} 这条缺少体脂率，${referenceText}`
    }
  }

  const latestMeasured = bodyFatTrendBuckets.value[bodyFatTrendBuckets.value.length - 1]
  const previousMeasured = bodyFatTrendBuckets.value.length > 1 ? bodyFatTrendBuckets.value[bodyFatTrendBuckets.value.length - 2] : null
  if (!latestMeasured || !previousMeasured) {
    return {
      tone: 'steady',
      tag: '基线已建',
      title: '体脂率基线已建立',
      description: `当前最新体脂率为 ${formatTrendBodyFat(latestRecord.bodyFatRate)}%，后续继续记录即可看到变化提醒。`
    }
  }

  const diff = Number((latestMeasured.value - previousMeasured.value).toFixed(1))
  if (diff <= -0.5) {
    return {
      tone: 'down',
      tag: `${Math.abs(diff)}%`,
      title: '最近一次体脂率明显下降',
      description: `相较上一条有效记录下降 ${Math.abs(diff)}%，当前最新体脂率为 ${formatTrendBodyFat(latestMeasured.value)}%。`
    }
  }
  if (diff >= 0.5) {
    return {
      tone: 'up',
      tag: `+${Math.abs(diff)}%`,
      title: '最近一次体脂率有上升',
      description: `相较上一条有效记录上升 ${Math.abs(diff)}%，建议回看最近几天的饮食和训练节奏。`
    }
  }
  return {
    tone: 'steady',
    tag: '平稳',
    title: '最近一次体脂率变化不大',
    description: `当前与上一条有效记录基本持平，说明最近身体组成整体比较稳定。`
  }
})

const bodyCompositionDescription = computed(() => {
  if (currentUserGender.value === '0') {
    return '先看三项指标的组合关系；补充性别后，体脂率判断会更准确。'
  }
  return `结合当前体重、BMI 和体脂率一起看，比单独看体重更接近真实变化。`
})

const bodyCompositionInsights = computed(() => {
  const insights: Array<{ title: string; description: string }> = []
  const bmi = summary.value?.bmi ?? null
  const bodyFatRate = latestBodyFatRecord.value?.bodyFatRate ?? null
  const weightChange = metrics.value.weightChange
  const latestMeasured = bodyFatTrendBuckets.value[bodyFatTrendBuckets.value.length - 1]
  const previousMeasured = bodyFatTrendBuckets.value.length > 1 ? bodyFatTrendBuckets.value[bodyFatTrendBuckets.value.length - 2] : null
  const bodyFatDiff = latestMeasured && previousMeasured ? Number((latestMeasured.value - previousMeasured.value).toFixed(1)) : null

  if (bmi !== null && bodyFatRate !== null && bodyFatRate !== undefined) {
    const bmiStatus = bmiStatusText(bmi)
    const bodyFatStatus = bodyFatStatusText(bodyFatRate, currentUserGender.value)
    if (bmi >= 18.5 && bmi < 24 && bodyFatStatus === '偏高') {
      insights.push({
        title: '体重正常但脂肪占比偏高',
        description: 'BMI 处于正常区间，但体脂率提示脂肪占比偏高，接下来更适合优先降脂而不是单纯追求降秤。'
      })
    } else if (bmi >= 24 && (bodyFatStatus === '标准' || bodyFatStatus === '偏低')) {
      insights.push({
        title: 'BMI 偏高但体脂不高',
        description: '这类情况可能受肌肉量、体型或短期补水影响，先别只盯着秤重，最好结合围度和训练表现一起看。'
      })
    } else if (bmi >= 24 && (bodyFatStatus === '偏高' || bodyFatStatus === '肥胖')) {
      insights.push({
        title: '体重和体脂都在提示减脂压力',
        description: `当前 BMI ${bmiStatus}、体脂率 ${bodyFatStatus}，更适合坚持热量管理和稳定运动，把节奏拉长看。`
      })
    } else if (bmi < 18.5 && (bodyFatStatus === '偏高' || bodyFatStatus === '肥胖')) {
      insights.push({
        title: '可能存在隐性肥胖倾向',
        description: '体重不高但体脂率偏高，通常说明肌肉量不足，后续比起继续减重，更应该关注力量训练和蛋白摄入。'
      })
    } else if (bmi >= 18.5 && bmi < 24 && bodyFatStatus === '标准') {
      insights.push({
        title: '三项核心指标目前比较协调',
        description: '当前体重、BMI 和体脂率都处在相对理想的组合区间，重点转向稳定维持和减少波动即可。'
      })
    }
  } else if (bmi !== null) {
    insights.push({
      title: '当前以 BMI 为主判断',
      description: '体脂率记录还不完整，现阶段主要依赖 BMI 和体重趋势判断；后续补录体脂率后，解读会更准确。'
    })
  }

  if (bodyFatDiff !== null && weightChange !== '待对比' && weightChange !== '持平') {
    const weightChangeNumber = Number(weightChange.replace(' kg', ''))
    if (!Number.isNaN(weightChangeNumber)) {
      if (weightChangeNumber < 0 && bodyFatDiff < 0) {
        insights.push({
          title: '最近减重方向较健康',
          description: '最近体重和体脂率都在下降，说明当前减重更偏向减脂，而不是单纯掉水分。'
        })
      } else if (weightChangeNumber < 0 && bodyFatDiff > 0) {
        insights.push({
          title: '体重下降但体脂没有同步变好',
          description: '这通常意味着肌肉或水分下降较多，建议关注蛋白摄入、力量训练和恢复质量。'
        })
      } else if (weightChangeNumber > 0 && bodyFatDiff < 0) {
        insights.push({
          title: '体重上升但体脂下降',
          description: '这种组合不一定是坏事，可能来自增肌、补糖原或补水，建议继续观察一到两周趋势。'
        })
      } else if (weightChangeNumber > 0 && bodyFatDiff > 0) {
        insights.push({
          title: '近期热量赤字可能不够',
          description: '体重和体脂率都在走高，优先回看最近的摄入、活动量和作息是否明显偏离计划。'
        })
      }
    }
  }

  if (!insights.length) {
    insights.push({
      title: '三指标联动解读还在积累中',
      description: '继续补充连续的体重和体脂率记录，系统才能更稳定地判断你最近的真实变化方向。'
    })
  }

  return insights.slice(0, 3)
})

const bodyInsightCards = computed<AnalyticsInsightCard[]>(() => {
  if (!latestWeightRecord.value?.recordedAt && metrics.value.weight === '--') {
    return [
      {
        title: '身体解读还没有足够数据',
        description: '先补一条体重记录，系统才能开始判断体重、BMI 和体脂率的联动关系。',
        tag: '待补记录',
        tone: 'missing'
      }
    ]
  }

  const reminderCard: AnalyticsInsightCard = {
    title: bodyFatReminder.value.title,
    description: bodyFatReminder.value.description,
    tag: bodyFatReminder.value.tag,
    tone: bodyFatReminder.value.tone
  }

  if (
    bodyFatReminder.value.tone === 'missing' ||
    bodyFatReminder.value.tone === 'idle' ||
    bodyCompositionInsights.value[0]?.title === '三指标联动解读还在积累中'
  ) {
    return [reminderCard]
  }

  const extraCards = bodyCompositionInsights.value
    .filter((item) => item.title !== reminderCard.title)
    .slice(0, 2)
    .map<AnalyticsInsightCard>((item) => ({
      ...item,
      tone: 'neutral'
    }))

  return [reminderCard, ...extraCards].slice(0, 3)
})

const bodySignalChips = computed<BodySignalChip[]>(() => {
  const chips: BodySignalChip[] = [
    {
      label: '体重变化',
      value: metrics.value.weightChange,
      tone: metrics.value.weightChange.startsWith('-')
        ? 'positive'
        : metrics.value.weightChange.startsWith('+')
          ? 'warning'
          : 'neutral'
    },
    {
      label: 'BMI区间',
      value: metrics.value.bmiStatus,
      tone: metrics.value.bmiStatus === '正常' ? 'positive' : metrics.value.bmiStatus === '未知' ? 'neutral' : 'warning'
    },
    {
      label: '体脂判断',
      value: metrics.value.bodyFatStatus,
      tone: metrics.value.bodyFatStatus === '标准' ? 'positive' : metrics.value.bodyFatStatus === '未填写' ? 'neutral' : 'warning'
    }
  ]
  return chips
})

const calorieStats = computed(() => {
  const intake = summary.value?.calorieIntake || 0
  const burn = summary.value?.exerciseCalories || 0
  const target = summary.value?.calorieTarget || 0
  const deficitTarget = summary.value?.dailyCalorieDeficitTarget || 0
  const maxValue = Math.max(target, intake, burn, 1)
  const remaining = target > 0 ? Math.max(0, target - intake) : 0
  const overTarget = target > 0 ? Math.max(0, intake - target) : 0
  const expectedDeficit = Math.max(0, deficitTarget + burn - overTarget)
  const exerciseContribution = burn
  const deficitConsumed = overTarget
  return {
    intake,
    burn,
    deficitTarget,
    remaining,
    overTarget,
    expectedDeficit,
    exerciseContribution,
    deficitConsumed,
    gap: remaining,
    intakePercent: Math.min(100, Math.round((intake / maxValue) * 100)),
    burnPercent: Math.min(100, Math.round((burn / maxValue) * 100))
  }
})

const waterStats = computed(() => {
  const today = summary.value?.waterCups || 0
  const target = summary.value?.waterTarget || 8
  const completion = target > 0 ? Math.min(100, Math.round((today / target) * 100)) : 0
  return {
    today,
    target,
    completion,
    status: completion >= 100 ? '达标' : completion >= 70 ? '接近' : '偏少'
  }
})

const lifestyleTodayExerciseMinutes = computed(() => summary.value?.exerciseMinutes || 0)
const lifestyleTodayExerciseScore = computed(() => {
  if (lifestyleTodayExerciseMinutes.value <= 0) {
    return 0
  }
  return Math.min(100, Math.round((lifestyleTodayExerciseMinutes.value / EXERCISE_BASELINE_MINUTES) * 100))
})

const lifestyleDietTrackDays = computed<LifestyleProgressDay[]>(() => {
  const calorieTarget = summary.value?.calorieTarget || 0
  const dayMap = new Map<string, number>()

  lifestyleFoodRecords.value.forEach((record) => {
    const dateKey = toApiDateKey(record.recordedAt)
    if (!dateKey) {
      return
    }
    dayMap.set(dateKey, (dayMap.get(dateKey) || 0) + Number(record.calories || 0))
  })

  return buildRecentDateSeries(7).map((date) => {
    const dateKey = formatDateParam(date)
    const intake = dayMap.get(dateKey) || 0
    const hasData = intake > 0
    const score = !hasData || calorieTarget <= 0
      ? 0
      : intake <= calorieTarget
        ? 100
        : Math.max(0, Math.round((calorieTarget / intake) * 100))
    return {
      date: dateKey,
      label: getWeekdayLabel(date),
      day: `${`${date.getMonth() + 1}`.padStart(2, '0')}/${`${date.getDate()}`.padStart(2, '0')}`,
      active: isSameCalendarDay(date, new Date()),
      done: hasData && calorieTarget > 0 && intake <= calorieTarget,
      fillHeight: hasData ? Math.max(score, 12) : 0,
      score,
      scoreLabel: hasData ? `${intake}` : '--'
    }
  })
})

const lifestyleExerciseTrackDays = computed<LifestyleProgressDay[]>(() => {
  const dayMap = new Map<string, number>()

  lifestyleExerciseRecords.value.forEach((record) => {
    const dateKey = toApiDateKey(record.recordedAt)
    if (!dateKey) {
      return
    }
    dayMap.set(dateKey, (dayMap.get(dateKey) || 0) + Number(record.durationMinutes || 0))
  })

  const weekMaxMinutes = Math.max(EXERCISE_BASELINE_MINUTES, ...Array.from(dayMap.values()), 1)
  return buildRecentDateSeries(7).map((date) => {
    const dateKey = formatDateParam(date)
    const minutes = dayMap.get(dateKey) || 0
    const hasData = minutes > 0
    const score = hasData ? Math.min(100, Math.round((minutes / weekMaxMinutes) * 100)) : 0
    return {
      date: dateKey,
      label: getWeekdayLabel(date),
      day: `${`${date.getMonth() + 1}`.padStart(2, '0')}/${`${date.getDate()}`.padStart(2, '0')}`,
      active: isSameCalendarDay(date, new Date()),
      done: minutes >= EXERCISE_BASELINE_MINUTES,
      fillHeight: hasData ? Math.max(score, 12) : 0,
      score,
      scoreLabel: hasData ? `${minutes}分` : '--'
    }
  })
})

const lifestyleWaterTrackDays = computed<LifestyleProgressDay[]>(() => {
  const waterTarget = summary.value?.waterTarget || 8
  const dayMap = new Map<string, number>()

  lifestyleWaterRecords.value.forEach((record) => {
    const dateKey = toApiDateKey(record.recordedAt)
    if (!dateKey) {
      return
    }
    dayMap.set(dateKey, (dayMap.get(dateKey) || 0) + Number(record.cups || 0))
  })

  return buildRecentDateSeries(7).map((date) => {
    const dateKey = formatDateParam(date)
    const cups = dayMap.get(dateKey) || 0
    const hasData = cups > 0
    const score = hasData ? Math.min(100, Math.round((cups / waterTarget) * 100)) : 0
    return {
      date: dateKey,
      label: getWeekdayLabel(date),
      day: `${`${date.getMonth() + 1}`.padStart(2, '0')}/${`${date.getDate()}`.padStart(2, '0')}`,
      active: isSameCalendarDay(date, new Date()),
      done: cups >= waterTarget,
      fillHeight: hasData ? Math.max(score, 12) : 0,
      score,
      scoreLabel: hasData ? `${cups}杯` : '--'
    }
  })
})

const lifestyleDietOnTrackDays = computed(() => lifestyleDietTrackDays.value.filter((day) => day.done).length)
const lifestyleExerciseActiveDays = computed(() => lifestyleExerciseTrackDays.value.filter((day) => day.score > 0).length)
const lifestyleWaterOnTrackDays = computed(() => lifestyleWaterTrackDays.value.filter((day) => day.done).length)

const lifestyleTrackCards = computed<LifestyleTrackCard[]>(() => {
  const calorieTarget = summary.value?.calorieTarget || 0
  return [
    {
      key: 'diet',
      title: '饮食节奏',
      description: calorieTarget > 0
        ? `按每日摄入上限 ${calorieTarget} kcal 看近 7 天控量表现。`
        : '完善基础资料和目标后，系统会生成每日摄入上限。',
      summaryValue: `${lifestyleDietOnTrackDays.value}/7`,
      summaryLabel: '上限内天数',
      tone: 'diet',
      historyType: 'food',
      actionLabel: '查看饮食记录',
      days: lifestyleDietTrackDays.value
    },
    {
      key: 'exercise',
      title: '运动节奏',
      description: `按时长看近 7 天活动分布，${EXERCISE_BASELINE_MINUTES} 分钟以上记为活跃日。`,
      summaryValue: `${lifestyleExerciseActiveDays.value}/7`,
      summaryLabel: '有运动天数',
      tone: 'exercise',
      historyType: 'exercise',
      actionLabel: '查看运动记录',
      days: lifestyleExerciseTrackDays.value
    },
    {
      key: 'water',
      title: '饮水节奏',
      description: `按每日饮水目标 ${summary.value?.waterTarget || 8} 杯看补水稳定性。`,
      summaryValue: `${lifestyleWaterOnTrackDays.value}/7`,
      summaryLabel: '达标天数',
      tone: 'water',
      historyType: 'water',
      actionLabel: '查看饮水记录',
      days: lifestyleWaterTrackDays.value
    }
  ]
})

const lifestyleCompletionRate = computed(() => {
  const calorieTarget = summary.value?.calorieTarget || 0
  const waterTarget = summary.value?.waterTarget || 8
  const calorieScore = calorieStats.value.intake === 0 || calorieTarget <= 0
    ? 0
    : calorieStats.value.intake <= calorieTarget
      ? 100
      : Math.max(0, Math.round((calorieTarget / calorieStats.value.intake) * 100))
  const waterScore = waterStats.value.today === 0 ? 0 : Math.min(100, Math.round((waterStats.value.today / waterTarget) * 100))
  return Math.round((calorieScore + waterScore + lifestyleTodayExerciseScore.value) / 3)
})

const lifestyleHeroTone = computed<'idle' | 'progress' | 'done'>(() => {
  if (calorieStats.value.intake === 0 && calorieStats.value.burn === 0 && waterStats.value.today === 0) {
    return 'idle'
  }
  if (
    calorieStats.value.overTarget === 0 &&
    waterStats.value.completion >= 100 &&
    lifestyleTodayExerciseMinutes.value >= EXERCISE_BASELINE_MINUTES
  ) {
    return 'done'
  }
  return 'progress'
})

const lifestyleHeadline = computed(() => {
  if (lifestyleHeroTone.value === 'idle') {
    return '今天的生活执行还没有开始记录'
  }
  if (calorieStats.value.overTarget > 0) {
    return '今天摄入已经超过摄入上限，优先回看饮食'
  }
  if (waterStats.value.completion < 100) {
    return `今天还差 ${Math.max(waterStats.value.target - waterStats.value.today, 0)} 杯水`
  }
  if (lifestyleTodayExerciseMinutes.value < EXERCISE_BASELINE_MINUTES) {
    return `今天还差 ${Math.max(EXERCISE_BASELINE_MINUTES - lifestyleTodayExerciseMinutes.value, 0)} 分钟活动`
  }
  return '今天的饮食、运动和补水节奏都比较稳'
})

const lifestyleAnalysisNote = computed(() => {
  if (lifestyleHeroTone.value === 'idle') {
    return '还没有饮食、运动或饮水记录，今天补一条后这里会开始形成执行复盘。'
  }
  if (!summary.value?.calorieTarget) {
    return `今日摄入 ${calorieStats.value.intake} kcal，运动消耗 ${calorieStats.value.burn} kcal；完善目标后会判断缺口达成情况。`
  }
  if (calorieStats.value.overTarget > 0) {
    return `今日摄入超出上限 ${calorieStats.value.overTarget} kcal，运动贡献 ${calorieStats.value.exerciseContribution} kcal 缺口，但不直接折算为可吃额度。`
  }
  return `今日摄入仍在上限内，运动额外贡献 ${calorieStats.value.exerciseContribution} kcal 缺口，预计缺口 ${calorieStats.value.expectedDeficit} kcal。`
})

const lifestyleSummaryCards = computed(() => [
  {
    label: '今日摄入',
    value: `${calorieStats.value.intake}`,
    meta: calorieStats.value.overTarget > 0
      ? `超出 ${calorieStats.value.overTarget} kcal`
      : calorieStats.value.deficitTarget > 0 || summary.value?.calorieTarget
        ? `还可摄入 ${calorieStats.value.remaining} kcal`
        : '完善资料后生成上限'
  },
  {
    label: '今日消耗',
    value: `${calorieStats.value.burn}`,
    meta: calorieStats.value.burn > 0
      ? `贡献 ${calorieStats.value.exerciseContribution} kcal 缺口`
      : `还差 ${Math.max(EXERCISE_BASELINE_MINUTES - lifestyleTodayExerciseMinutes.value, 0)} 分钟`
  },
  {
    label: '今日饮水',
    value: `${waterStats.value.today}/${waterStats.value.target}`,
    meta: `${waterStats.value.completion}% 完成`
  },
  {
    label: '预计缺口',
    value: `${calorieStats.value.expectedDeficit || 0}`,
    meta: calorieStats.value.deficitTarget > 0
      ? `目标${calorieStats.value.deficitTarget} / 运动+${calorieStats.value.exerciseContribution} / 侵占${calorieStats.value.deficitConsumed}`
      : '完善目标日期后计算'
  }
])

const lifestyleFootnote = computed(() => {
  if (!lifestyleHistoryInitialized.value) {
    return '切到这里后会按近 7 天记录生成节奏图。'
  }
  return '饮食按摄入上限、运动按时长分布、饮水按目标杯数分别复盘；每日缺口目标单独作为目标节奏参考。'
})

const lifestyleHeaderSummary = computed(() =>
  `近 7 天饮食 ${lifestyleDietOnTrackDays.value}/7 在目标内，运动 ${lifestyleExerciseActiveDays.value}/7 活跃，饮水 ${lifestyleWaterOnTrackDays.value}/7 达标。`
)

const hasHabitAnalytics = computed(() => (habitStats.value?.totalHabits || 0) > 0)
const habitDueCount = computed(() => habitStats.value?.activeHabits || 0)
const habitCompletedCount = computed(() => habitStats.value?.completedToday || 0)
const habitRemainingCount = computed(() => Math.max(habitDueCount.value - habitCompletedCount.value, 0))
const habitTodayRate = computed(() => habitStats.value?.todayCompletionRate || 0)
const habitHeroTone = computed<'idle' | 'progress' | 'done'>(() => {
  if (!habitStats.value || habitDueCount.value === 0) {
    return 'idle'
  }
  return habitRemainingCount.value === 0 ? 'done' : 'progress'
})

const habitHeadline = computed(() => {
  if (!habitStats.value || habitDueCount.value === 0) {
    return '今天没有习惯安排'
  }
  if (habitRemainingCount.value === 0) {
    return '今天的习惯已全部完成'
  }
  return `今天还差 ${habitRemainingCount.value} 项习惯待完成`
})

const habitSummaryCards = computed(() => {
  const stats = habitStats.value
  if (!stats) {
    return []
  }
  return [
    {
      label: '今日应做',
      value: `${habitDueCount.value}`,
      meta: '命中今天的习惯'
    },
    {
      label: '今日完成',
      value: `${habitCompletedCount.value}`,
      meta: habitDueCount.value ? (habitRemainingCount.value ? `还差 ${habitRemainingCount.value} 项` : '今天已完成') : '今日无安排'
    },
    {
      label: '近 7 天完成率',
      value: `${stats.weeklyCompletionRate}%`,
      meta: '按应做口径'
    },
    {
      label: '最佳连续',
      value: `${stats.bestStreak}天`,
      meta: '持续记录'
    }
  ]
})

const habitProgressDays = computed(() =>
  (habitStats.value?.weeklyProgress || []).map((day) => {
    const completionRate = day.totalCount > 0 ? Math.round((day.completedCount / day.totalCount) * 100) : 0
    return {
      ...day,
      completionRate,
      fillHeight: day.totalCount > 0 ? Math.max(completionRate, day.done ? 18 : 8) : 0
    }
  })
)

const habitAnalysisNote = computed(() => {
  const stats = habitStats.value
  if (!stats) {
    return ''
  }
  if (habitDueCount.value === 0) {
    return `共 ${stats.totalHabits} 个习惯，今天没有安排。`
  }
  if (habitRemainingCount.value === 0) {
    return `共 ${stats.totalHabits} 个习惯，今天 ${habitDueCount.value} 项应做已全部完成。`
  }
  return `共 ${stats.totalHabits} 个习惯，今天已完成 ${habitCompletedCount.value}/${habitDueCount.value}，还差 ${habitRemainingCount.value} 项。`
})

const habitHeaderSummary = computed(() => {
  const stats = habitStats.value
  if (!stats) {
    return '首次进入后会按今日应做项和近 7 天节奏生成习惯复盘。'
  }
  if (habitDueCount.value === 0) {
    return `当前共 ${stats.totalHabits} 个习惯，今天没有安排，近 7 天完成率 ${stats.weeklyCompletionRate}%。`
  }
  return `今天完成 ${habitCompletedCount.value}/${habitDueCount.value}，近 7 天完成率 ${stats.weeklyCompletionRate}%，最佳连续 ${stats.bestStreak} 天。`
})

const allRecords = computed<AnalyticsRecordItem[]>(() => {
  const items: AnalyticsRecordItem[] = []

  weightRecords.value.forEach((record) => {
    if (record.weight === null || record.weight === undefined) {
      return
    }
    const timeText = formatRecordTime(record.recordedAt) || '时间待补'
    const summaryText = record.bodyFatRate ? `体脂 ${record.bodyFatRate}%` : '体重记录'
    items.push({
      id: `weight-${record.id}`,
      type: 'weight',
      icon: '⚖️',
      title: `${record.weight} kg`,
      subtitle: formatRecordMeta(record.recordedAt, summaryText),
      summaryText,
      timeText,
      detailTitle: '体重记录',
      detailSubtitle: timeText,
      details: [
        { label: '体重', value: `${record.weight} kg` },
        { label: '体脂率', value: record.bodyFatRate ? `${record.bodyFatRate}%` : '未填写' },
        { label: 'BMI', value: record.bmi ? `${record.bmi}` : '未生成' },
        { label: '备注', value: record.remark || '无' }
      ],
      recordImages: parseRecordImages(record.images),
      sortTime: toRecordTimestamp(record.recordedAt)
    })
  })

  foodRecords.value.forEach((record) => {
    const timeText = formatRecordTime(record.recordedAt) || '时间待补'
    const summaryText = `${record.calories || 0} kcal`
    items.push({
      id: `food-${record.id}`,
      type: 'food',
      icon: '🥗',
      title: `${mealTypeLabel(record.mealType)} · ${record.foodName || '未命名食物'}`,
      subtitle: formatRecordMeta(record.recordedAt, summaryText),
      summaryText,
      timeText,
      detailTitle: '饮食记录',
      detailSubtitle: timeText,
      details: [
        { label: '餐次', value: mealTypeLabel(record.mealType) },
        { label: '食物', value: record.foodName || '未填写' },
        { label: '热量', value: `${record.calories || 0} kcal` },
        { label: '备注', value: record.remark || '无' }
      ],
      recordImages: parseRecordImages(record.images, record.imageUrl ? [record.imageUrl] : []),
      sortTime: toRecordTimestamp(record.recordedAt)
    })
  })

  exerciseRecords.value.forEach((record) => {
    const timeText = formatRecordTime(record.recordedAt) || '时间待补'
    const summaryText = `${record.durationMinutes || 0} 分钟 · ${record.caloriesBurned || 0} kcal`
    items.push({
      id: `exercise-${record.id}`,
      type: 'exercise',
      icon: '🏃',
      title: record.exerciseType || '运动记录',
      subtitle: formatRecordMeta(record.recordedAt, summaryText),
      summaryText,
      timeText,
      detailTitle: '运动记录',
      detailSubtitle: timeText,
      details: [
        { label: '运动类型', value: record.exerciseType || '未填写' },
        { label: '时长', value: `${record.durationMinutes || 0} 分钟` },
        { label: '消耗热量', value: `${record.caloriesBurned || 0} kcal` },
        { label: '距离', value: record.distance ? `${record.distance} km` : '未填写' },
        { label: '备注', value: record.remark || '无' }
      ],
      recordImages: parseRecordImages(record.images),
      sortTime: toRecordTimestamp(record.recordedAt)
    })
  })

  waterRecords.value.forEach((record) => {
    const timeText = formatRecordTime(record.recordedAt) || '时间待补'
    const summaryText = `${record.ml || 0} ml`
    items.push({
      id: `water-${record.id}`,
      type: 'water',
      icon: '💧',
      title: `${record.cups || 0} 杯饮水`,
      subtitle: formatRecordMeta(record.recordedAt, summaryText),
      summaryText,
      timeText,
      detailTitle: '饮水记录',
      detailSubtitle: timeText,
      details: [
        { label: '杯数', value: `${record.cups || 0} 杯` },
        { label: '毫升', value: `${record.ml || 0} ml` }
      ],
      recordImages: parseRecordImages(record.images),
      sortTime: toRecordTimestamp(record.recordedAt)
    })
  })

  return items.sort((a, b) => b.sortTime - a.sortTime)
})

const habitRecords = computed<AnalyticsRecordItem[]>(() =>
  habitCheckins.value
    .map((record) => {
      const timeText = formatRecordTime(record.checkedAt) || '时间待补'
      return {
        id: `habit-${record.id}`,
        type: 'habit' as const,
        icon: record.habitIcon || '习',
        title: record.habitName || '习惯打卡',
        subtitle: formatRecordMeta(record.checkedAt, record.note || '已完成'),
        summaryText: record.note || '这天已打卡',
        timeText,
        detailTitle: '习惯打卡',
        detailSubtitle: timeText,
        details: [
          { label: '习惯', value: record.habitName || '习惯打卡' },
          { label: '完成时间', value: timeText },
          { label: '备注', value: record.note || '无' }
        ],
        recordImages: parseRecordImages(record.images),
        sortTime: toRecordTimestamp(record.checkedAt)
      }
    })
    .sort((a, b) => b.sortTime - a.sortTime)
)

const selectedMonthTitle = computed(() => formatMonthTitle(selectedMonthDate.value))
const selectedDateLabel = computed(() => formatDateLabel(selectedDateKey.value))
const dailyRefreshMetaText = computed(() => {
  if (!isLoggedIn.value) {
    return '登录后同步个人复盘'
  }
  if (isRefreshingAll.value) {
    return '正在刷新这页数据...'
  }
  const parts: string[] = []
  if (lastRecordRefreshAt.value) {
    parts.push(`记录 ${lastRecordRefreshAt.value}`)
  }
  if (lastDashboardRefreshAt.value) {
    parts.push(`身体 ${lastDashboardRefreshAt.value}`)
  }
  if (lastHabitRefreshAt.value) {
    parts.push(`习惯 ${lastHabitRefreshAt.value}`)
  }
  return parts.length ? `正在看 ${selectedDateLabel.value} · ${parts.join(' · ')}` : `正在看 ${selectedDateLabel.value}`
})

const dailyAnalysisMap = computed(() => {
  const map = new Map<string, DailyAnalysisItem>()
  const monthStart = selectedMonthDate.value
  const totalDays = daysInMonth(monthStart)
  for (let day = 1; day <= totalDays; day += 1) {
    const date = new Date(monthStart.getFullYear(), monthStart.getMonth(), day)
    const dateKey = formatDateParam(date)
    map.set(dateKey, buildDailyAnalysis(date, dateKey, day))
  }
  return map
})

const selectedDailyAnalysis = computed(() =>
  dailyAnalysisMap.value.get(selectedDateKey.value) || buildDailyAnalysis(parseDateKey(selectedDateKey.value), selectedDateKey.value, Number(selectedDateKey.value.slice(-2)))
)

const monthStatusCounts = computed(() => {
  const days = Array.from(dailyAnalysisMap.value.values())
  return {
    stable: days.filter((item) => item.status === 'stable').length,
    wave: days.filter((item) => item.status === 'wave').length,
    missing: days.filter((item) => item.status === 'missing').length,
    future: days.filter((item) => item.status === 'future').length
  }
})

const monthReviewSummary = computed(() => {
  const counts = monthStatusCounts.value
  return `稳了 ${counts.stable}天 · 波动 ${counts.wave}天 · 待补 ${counts.missing}天`
})

const monthViewedSummary = computed(() => {
  const counts = monthStatusCounts.value
  const viewed = counts.stable + counts.wave + counts.missing
  return `已看 ${viewed}天：${counts.stable} 天稳了，${counts.missing} 天待补`
})

const monthWeightChangeText = computed(() => {
  const monthStart = selectedMonthDate.value
  const startTime = new Date(monthStart.getFullYear(), monthStart.getMonth(), 1).getTime()
  const endTime = new Date(monthStart.getFullYear(), monthStart.getMonth() + 1, 1).getTime()
  const records = weightRecords.value
    .filter((record) => {
      const time = toRecordTimestamp(record.recordedAt)
      return record.weight !== null && record.weight !== undefined && time >= startTime && time < endTime
    })
    .sort((a, b) => toRecordTimestamp(a.recordedAt) - toRecordTimestamp(b.recordedAt))
  if (records.length < 2) {
    return '本月变化 --'
  }
  const first = Number(records[0].weight)
  const last = Number(records[records.length - 1].weight)
  const diff = Number((last - first).toFixed(1))
  if (Number.isNaN(diff) || diff === 0) {
    return '本月基本持平'
  }
  return diff < 0 ? `本月少了 ${Math.abs(diff)}kg` : `本月多了 ${diff}kg`
})

const calendarProgressSegments = computed<CalendarProgressSegment[]>(() => {
  const counts = monthStatusCounts.value
  const total = Math.max(counts.stable + counts.wave + counts.missing + counts.future, 1)
  return calendarLegendItems.map((item) => ({
    status: item.status,
    percent: Math.max(0, Math.round((counts[item.status] / total) * 1000) / 10)
  }))
})

const calendarCells = computed<CalendarCell[]>(() => {
  const cells: CalendarCell[] = []
  const monthStart = selectedMonthDate.value
  const totalDays = daysInMonth(monthStart)
  const offset = mondayFirstOffset(monthStart)
  const previousMonth = new Date(monthStart.getFullYear(), monthStart.getMonth() - 1, 1)
  const previousDays = daysInMonth(previousMonth)

  for (let index = 0; index < offset; index += 1) {
    const day = previousDays - offset + index + 1
    cells.push({
      key: `prev-${day}`,
      dateKey: '',
      day,
      dayText: `${day}`,
      outside: true,
      active: false,
      today: false,
      status: 'future',
      weightText: ''
    })
  }

  for (let day = 1; day <= totalDays; day += 1) {
    const date = new Date(monthStart.getFullYear(), monthStart.getMonth(), day)
    const dateKey = formatDateParam(date)
    const item = dailyAnalysisMap.value.get(dateKey)
    cells.push({
      key: dateKey,
      dateKey,
      day,
      dayText: `${day}`,
      outside: false,
      active: dateKey === selectedDateKey.value,
      today: isSameCalendarDay(date, new Date()),
      status: item?.status || 'future',
      weightText: formatCalendarWeightText(item?.weightChangeText)
    })
  }

  const nextCells = cells.length % 7 === 0 ? 0 : 7 - (cells.length % 7)
  for (let day = 1; day <= nextCells; day += 1) {
    cells.push({
      key: `next-${day}`,
      dateKey: '',
      day,
      dayText: `${day}`,
      outside: true,
      active: false,
      today: false,
      status: 'future',
      weightText: ''
    })
  }
  return cells
})

const dailySummaryCards = computed<AnalyticsSummaryCard[]>(() => [
  { label: '体重变化', value: selectedDailyAnalysis.value.weightChangeText, meta: selectedDailyAnalysis.value.body.records.length ? '这天有体重记录' : '这天没记体重' },
  { label: '晚餐预算', value: formatNullablePercent(selectedDailyAnalysis.value.dinnerPercent), meta: selectedDailyAnalysis.value.lifestyle.foodRecords.length ? '按这天饮食估算' : '晚餐待补' },
  { label: '饮水完成', value: formatNullablePercent(selectedDailyAnalysis.value.waterPercent), meta: selectedDailyAnalysis.value.lifestyle.waterRecords.length ? '按当天饮水看' : '饮水待补' },
  { label: '记录够不够', value: selectedDailyAnalysis.value.missing.length ? '待补' : '够看', meta: selectedDailyAnalysis.value.missing.length ? `还差${selectedDailyAnalysis.value.missing.join('、')}` : '这天可以参考' }
])

const reviewReasonCards = computed<DailyReasonCard[]>(() => {
  const day = selectedDailyAnalysis.value
  if (day.status === 'future') {
    return [
      { title: '这天还没到', description: '等记录出现后再看，不需要提前下结论。', tag: '等等', tone: 'future' },
      { title: '先看已经发生的日期', description: '当前月日历里，颜色只对已有日期有意义。', tag: '提示', tone: 'future' }
    ]
  }
  if (day.status === 'missing') {
    return [
      { title: `还差${day.missing.join('、')}`, description: '缺了这些记录，这天的解释会容易跑偏。', tag: '待补', tone: 'missing' },
      { title: '补完再看更准', description: '体重、晚餐、饮水和运动补齐后，复盘判断会更清楚。', tag: '先补', tone: 'warn' }
    ]
  }
  if (day.status === 'wave') {
    return [
      { title: '这天有点波动', description: '更像吃饭、饮水或活动带来的短期变化，先别急着改计划。', tag: '波动', tone: 'warn' },
      { title: '看下一天再决定', description: '单天回弹不一定代表真的变胖，连续两三天再处理。', tag: '观察', tone: 'warn' }
    ]
  }
  return [
    { title: '这天能放心参考', description: '体重没乱跳，吃喝记录也比较完整。', tag: '稳了', tone: 'good' },
    { title: '继续按这个节奏走', description: '不用因为一天数据做大调整，明天照常记录就好。', tag: '保持', tone: 'good' }
  ]
})

const bodyDailyTitle = computed(() => selectedDailyAnalysis.value.body.records.length ? `${selectedDateLabel.value}体重挺稳` : `${selectedDateLabel.value}少一条体重`)
const bodyDailyDesc = computed(() => selectedDailyAnalysis.value.body.records.length ? `这天体重变化 ${selectedDailyAnalysis.value.weightChangeText}，可以和吃喝记录一起看。` : '这天没看到体重记录，先补体重再看身体判断。')
const bodyDailyCards = computed<AnalyticsSummaryCard[]>(() => [
  { label: '体重变化', value: selectedDailyAnalysis.value.weightChangeText, meta: selectedDailyAnalysis.value.body.records.length ? '这天可参考' : '体重待补' },
  { label: 'BMI状态', value: selectedDailyAnalysis.value.body.bmiStatus, meta: selectedDailyAnalysis.value.body.bmi === '--' ? '等待体重记录' : `当前 BMI ${selectedDailyAnalysis.value.body.bmi}` }
])
const bodyReasonCards = computed<DailyReasonCard[]>(() => [
  {
    title: selectedDailyAnalysis.value.body.records.length ? '体重有记录' : '这天没记体重',
    description: selectedDailyAnalysis.value.body.records.length ? '这天体重可以和晚餐、饮水放在一起看。' : '缺体重时，身体判断只能先参考生活记录。',
    tag: selectedDailyAnalysis.value.body.records.length ? '可参考' : '待补',
    tone: selectedDailyAnalysis.value.body.records.length ? 'good' : 'missing'
  },
  {
    title: 'BMI只做背景参考',
    description: 'BMI不需要每天反复判断，这里只用来提醒当前大区间。',
    tag: selectedDailyAnalysis.value.body.bmiStatus,
    tone: selectedDailyAnalysis.value.body.records.length ? 'good' : 'future'
  }
])
const dailyBodyRecords = computed(() => selectedDailyAnalysis.value.body.records)
const bodyRecordHubTitle = computed(() => dailyBodyRecords.value.length ? `${selectedDateLabel.value}有 ${dailyBodyRecords.value.length} 条体重记录` : `${selectedDateLabel.value}暂无体重记录`)

const lifestyleDailyTitle = computed(() => `${selectedDateLabel.value}生活执行`)
const lifestyleDailyDesc = computed(() => '先看这一天吃得怎样、动没动、喝水够不够。')
const lifestyleDailyTone = computed(() => selectedDailyAnalysis.value.status === 'stable' ? 'done' : selectedDailyAnalysis.value.status === 'wave' ? 'progress' : 'idle')
const lifestyleDailyScore = computed(() => {
  const dinner = selectedDailyAnalysis.value.dinnerPercent ?? 0
  const water = selectedDailyAnalysis.value.waterPercent ?? 0
  const move = selectedDailyAnalysis.value.exerciseMinutes > 0 ? 100 : 0
  return `${Math.round((Math.min(dinner, 100) + Math.min(water, 100) + move) / 3)}%`
})
const lifestyleDailyHeadline = computed(() => selectedDailyAnalysis.value.status === 'missing' ? '这天记录还差点' : selectedDailyAnalysis.value.status === 'wave' ? '这天有点波动' : '这天记录够看')
const lifestyleDailyNote = computed(() => selectedDailyAnalysis.value.status === 'missing' ? '补齐晚餐、饮水或运动后，这天会更好解释。' : '晚餐、饮水和运动放在一起看，体重变化更容易说清。')
const lifestyleDailyCards = computed<AnalyticsSummaryCard[]>(() => [
  { label: '晚餐预算', value: formatNullablePercent(selectedDailyAnalysis.value.dinnerPercent), meta: selectedDailyAnalysis.value.dinnerPercent === null ? '晚餐待补' : selectedDailyAnalysis.value.dinnerPercent > 100 ? '稍微超了点' : '在目标内' },
  { label: '饮水完成', value: formatNullablePercent(selectedDailyAnalysis.value.waterPercent), meta: selectedDailyAnalysis.value.waterPercent === null ? '饮水待补' : selectedDailyAnalysis.value.waterPercent >= 90 ? '补水节奏稳' : '还能再喝点' },
  { label: '运动记录', value: selectedDailyAnalysis.value.exerciseMinutes ? '已记录' : '待补', meta: selectedDailyAnalysis.value.exerciseMinutes ? `${selectedDailyAnalysis.value.exerciseMinutes} 分钟` : '这天没看到运动' },
  { label: '记录够不够', value: selectedDailyAnalysis.value.missing.length ? '待补' : '够看', meta: selectedDailyAnalysis.value.missing.length ? '先补关键记录' : '不用纠结' }
])
const lifestyleReasonCards = computed<DailyReasonCard[]>(() => [
  {
    title: selectedDailyAnalysis.value.dinnerPercent === null ? '晚餐还没记' : selectedDailyAnalysis.value.dinnerPercent > 100 ? '晚餐有点超' : '晚餐在目标内',
    description: selectedDailyAnalysis.value.dinnerPercent === null ? '缺晚餐时，体重变化会少一个重要原因。' : '先和体重变化一起看，不用单独放大这一个数字。',
    tag: selectedDailyAnalysis.value.dinnerPercent === null ? '待补' : '可参考',
    tone: selectedDailyAnalysis.value.dinnerPercent === null ? 'missing' : selectedDailyAnalysis.value.dinnerPercent > 100 ? 'warn' : 'good'
  },
  {
    title: selectedDailyAnalysis.value.waterPercent === null ? '饮水还没记' : selectedDailyAnalysis.value.waterPercent >= 90 ? '喝水跟上了' : '喝水还差一点',
    description: selectedDailyAnalysis.value.waterPercent === null ? '缺饮水时，短期体重波动不好解释。' : '饮水和作息都会影响短期体重，不代表真的胖了。',
    tag: selectedDailyAnalysis.value.waterPercent === null ? '待补' : selectedDailyAnalysis.value.waterPercent >= 90 ? '够看' : '补点',
    tone: selectedDailyAnalysis.value.waterPercent === null ? 'missing' : selectedDailyAnalysis.value.waterPercent >= 90 ? 'good' : 'warn'
  }
])
const dailyLifestyleRecords = computed(() => selectedDailyAnalysis.value.lifestyle.records)
const lifestyleRecordHubTitle = computed(() => dailyLifestyleRecords.value.length ? `${selectedDateLabel.value}有 ${dailyLifestyleRecords.value.length} 条生活记录` : `${selectedDateLabel.value}生活记录待补`)

const habitDailyTitle = computed(() => `${selectedDateLabel.value}习惯复盘`)
const habitDailyDesc = computed(() => '看这天习惯有没有跟上，不和体重混在一起。')
const habitDailyTone = computed(() => selectedDailyAnalysis.value.habit.completedCount >= selectedDailyAnalysis.value.habit.dueCount && selectedDailyAnalysis.value.habit.dueCount > 0 ? 'done' : selectedDailyAnalysis.value.habit.completedCount > 0 ? 'progress' : 'idle')
const habitDailyEyebrow = computed(() => `这天要做 ${selectedDailyAnalysis.value.habit.dueCount} 项`)
const habitDailyRate = computed(() => selectedDailyAnalysis.value.habit.dueCount ? `${Math.round((selectedDailyAnalysis.value.habit.completedCount / selectedDailyAnalysis.value.habit.dueCount) * 100)}%` : '0%')
const habitDailyHeadline = computed(() => {
  const habit = selectedDailyAnalysis.value.habit
  if (!habit.dueCount) return '这天还没有习惯安排'
  if (habit.completedCount >= habit.dueCount) return '这天习惯完成了'
  if (habit.completedCount > 0) return '这天习惯完成了一部分'
  return '这天习惯还没打卡'
})
const habitDailyNote = computed(() => selectedDailyAnalysis.value.habit.dueCount ? `这天完成 ${selectedDailyAnalysis.value.habit.completedCount}/${selectedDailyAnalysis.value.habit.dueCount} 项。` : '当前只能看到实际打卡记录，历史应做项需要后端继续补齐。')
const habitDailyCards = computed<AnalyticsSummaryCard[]>(() => [
  { label: '这天要做', value: `${selectedDailyAnalysis.value.habit.dueCount}`, meta: '按当前可用数据估算' },
  { label: '这天完成', value: `${selectedDailyAnalysis.value.habit.completedCount}`, meta: selectedDailyAnalysis.value.habit.completedCount ? '已打卡' : '暂无打卡' }
])
const habitReasonCards = computed<DailyReasonCard[]>(() => [
  {
    title: selectedDailyAnalysis.value.habit.completedCount ? '这天有习惯打卡' : '这天没有习惯打卡',
    description: selectedDailyAnalysis.value.habit.completedCount ? '这里只看选中日期，不把其他日期混进来。' : '没有打卡记录时，先不算完成。',
    tag: selectedDailyAnalysis.value.habit.completedCount ? '完成' : '暂无',
    tone: selectedDailyAnalysis.value.habit.completedCount ? 'good' : 'future'
  }
])
const dailyHabitRecords = computed(() => selectedDailyAnalysis.value.habit.records)
const habitRecordHubTitle = computed(() => selectedDailyAnalysis.value.habit.dueCount ? `${selectedDateLabel.value}完成 ${selectedDailyAnalysis.value.habit.completedCount}/${selectedDailyAnalysis.value.habit.dueCount} 项习惯` : `${selectedDateLabel.value}暂无要做习惯`)

const hasLifestyleHistoryData = computed(
  () =>
    lifestyleFoodRecords.value.length > 0 ||
    lifestyleExerciseRecords.value.length > 0 ||
    lifestyleWaterRecords.value.length > 0
)

const bodyTrendEmptyText = computed(() => (
  isLoggedIn.value ? '身体趋势还在积累中，补一条体重记录后这里会开始生成。' : '登录后会展示你的体重趋势。'
))
const bodyFatTrendEmptyText = computed(() => (
  isLoggedIn.value ? '身体趋势还在积累中，填写过体脂率后这里会开始生成。' : '登录后会展示你的体脂率趋势。'
))
const bodyRecentRecordLoadingText = computed(() => '正在整理身体记录...')
const bodyRecentRecordErrorTitle = computed(() => '身体记录暂时没加载出来')
const bodyRecentRecordErrorDescription = computed(() => '身体总览仍可查看，重新整理一次后再看看明细入口。')
const bodyRecentRecordEmptyTitle = computed(() => (isLoggedIn.value ? '身体记录还在积累中' : '登录后查看身体分析'))
const bodyRecentRecordEmptyDescription = computed(() => (
  isLoggedIn.value
    ? '补一条体重、饮食、运动或饮水记录后，这里会开始形成身体明细入口。'
    : '登录后会按你的体重、饮食、运动和饮水记录生成分析。'
))
const bodyRecentRecordEmptyAction = computed(() => (isLoggedIn.value ? '记录身体数据' : '登录后查看'))

const lifestyleLoadingText = computed(() => '正在整理生活执行...')
const lifestyleErrorTitle = computed(() => '生活执行暂时没加载出来')
const lifestyleErrorDescription = computed(() => '今日摘要仍可查看，重新整理一次后再看近 7 天节奏。')
const lifestyleEmptyTitle = computed(() => (isLoggedIn.value ? '生活执行还在积累中' : '登录后查看生活执行'))
const lifestyleEmptyDescription = computed(() => (
  isLoggedIn.value
    ? '补一条饮食、运动或饮水记录后，这里会开始生成近 7 天执行节奏。'
    : '登录后会按你的饮食、运动和饮水记录生成近 7 天执行节奏。'
))
const lifestyleEmptyAction = computed(() => (isLoggedIn.value ? '记录生活数据' : '登录后查看'))

const habitLoadingText = computed(() => '正在整理习惯复盘...')
const habitErrorTitle = computed(() => '习惯复盘暂时没加载出来')
const habitErrorDescription = computed(() => '习惯管理仍可使用，重新整理一次后再看今日概览和近 7 天节奏。')
const habitEmptyTitle = computed(() => (isLoggedIn.value ? '习惯复盘还在积累中' : '登录后查看习惯复盘'))
const habitEmptyDescription = computed(() => (
  isLoggedIn.value
    ? '完成一次习惯后，这里会开始生成今日概览和近 7 天节奏。'
    : '登录后会按你的习惯配置和完成记录生成复盘。'
))
const habitEmptyAction = computed(() => (isLoggedIn.value ? '管理习惯' : '登录后查看'))

async function loadAnalyticsData() {
  if (!isLoggedIn.value) {
    resetAnalyticsData()
    return
  }

  await Promise.all([loadDashboardSummary(), loadRecentRecords()])
  if (activeTab.value === 'lifestyle' || lifestyleHistoryInitialized.value) {
    await loadLifestyleHistory()
  }
  if (activeTab.value === 'habit' || habitStatsInitialized.value) {
    await loadHabitAnalytics()
  }
}

function resetAnalyticsData() {
  dashboard.value = null
  latestWeightRecord.value = null
  weightRecords.value = []
  trendWeightRecords.value = []
  foodRecords.value = []
  exerciseRecords.value = []
    waterRecords.value = []
    lifestyleFoodRecords.value = []
    lifestyleExerciseRecords.value = []
    lifestyleWaterRecords.value = []
    habitCheckins.value = []
  habitStats.value = null
  recordLoading.value = false
  recordLoadFailed.value = false
  dashboardLoading.value = false
  dashboardLoadFailed.value = false
  lifestyleHistoryLoading.value = false
  lifestyleHistoryLoadFailed.value = false
  lifestyleHistoryInitialized.value = false
  habitStatsLoading.value = false
  habitStatsLoadFailed.value = false
  habitStatsInitialized.value = false
  lastDashboardRefreshAt.value = ''
  lastRecordRefreshAt.value = ''
  lastLifestyleRefreshAt.value = ''
  lastHabitRefreshAt.value = ''
  selectedRecord.value = null
}

async function loadDashboardSummary() {
  if (!isLoggedIn.value) {
    dashboard.value = null
    latestWeightRecord.value = null
    trendWeightRecords.value = []
    dashboardLoadFailed.value = false
    dashboardLoading.value = false
    return
  }

  dashboardLoading.value = true
  dashboardLoadFailed.value = false
  try {
    const [dashboardResult, latestWeightResult, trendWeightPageResult] = await Promise.allSettled([
      http.get<DashboardPayload>('/api/dashboard', undefined, { showLoading: false }),
      http.get<WeightRecordPayload | null>('/api/record/weight/latest', undefined, { showLoading: false }),
      getWeightRecordList({ pageNum: 1, pageSize: 180 })
    ])
    if (
      dashboardResult.status === 'rejected' ||
      latestWeightResult.status === 'rejected' ||
      trendWeightPageResult.status === 'rejected'
    ) {
      throw new Error('dashboard summary failed')
    }
    dashboard.value = dashboardResult.value
    latestWeightRecord.value = latestWeightResult.value
    trendWeightRecords.value = trendWeightPageResult.value.list || []
    lastDashboardRefreshAt.value = formatRefreshTime(new Date())
  } catch (error) {
    console.error('加载分析页数据失败:', error)
    dashboard.value = null
    latestWeightRecord.value = null
    trendWeightRecords.value = []
    dashboardLoadFailed.value = true
  } finally {
    dashboardLoading.value = false
  }
}

async function loadRecentRecords() {
  if (!isLoggedIn.value) {
    weightRecords.value = []
    foodRecords.value = []
    exerciseRecords.value = []
    waterRecords.value = []
    habitCheckins.value = []
    recordLoadFailed.value = false
    recordLoading.value = false
    return
  }

  recordLoading.value = true
  recordLoadFailed.value = false
  try {
    const [weightPage, foodPage, exercisePage, waterPage, habitPage] = await Promise.all([
      getWeightRecordList({ pageNum: 1, pageSize: 180 }),
      getFoodRecordList({ pageNum: 1, pageSize: 180 }),
      getExerciseRecordList({ pageNum: 1, pageSize: 180 }),
      getWaterRecordList({ pageNum: 1, pageSize: 180 }),
      getHabitCheckinList({ pageNum: 1, pageSize: 180 })
    ])
    weightRecords.value = weightPage.list || []
    foodRecords.value = foodPage.list || []
    exerciseRecords.value = exercisePage.list || []
    waterRecords.value = waterPage.list || []
    habitCheckins.value = habitPage.list || []
    lastRecordRefreshAt.value = formatRefreshTime(new Date())
  } catch (error) {
    console.error('加载最近记录失败:', error)
    weightRecords.value = []
    foodRecords.value = []
    exerciseRecords.value = []
    waterRecords.value = []
    habitCheckins.value = []
    recordLoadFailed.value = true
  } finally {
    recordLoading.value = false
  }
}

async function ensureLifestyleHistoryLoaded() {
  if (!isLoggedIn.value) {
    return
  }
  if (lifestyleHistoryInitialized.value || lifestyleHistoryLoading.value) {
    return
  }
  await loadLifestyleHistory()
}

async function loadLifestyleHistory() {
  if (!isLoggedIn.value) {
    lifestyleFoodRecords.value = []
    lifestyleExerciseRecords.value = []
    lifestyleWaterRecords.value = []
    lifestyleHistoryLoadFailed.value = false
    lifestyleHistoryLoading.value = false
    lifestyleHistoryInitialized.value = false
    return
  }

  lifestyleHistoryLoading.value = true
  lifestyleHistoryLoadFailed.value = false
  try {
    const [foodPage, exercisePage, waterPage] = await Promise.all([
      getFoodRecordList({ pageNum: 1, pageSize: 120 }),
      getExerciseRecordList({ pageNum: 1, pageSize: 120 }),
      getWaterRecordList({ pageNum: 1, pageSize: 120 })
    ])
    lifestyleFoodRecords.value = foodPage.list || []
    lifestyleExerciseRecords.value = exercisePage.list || []
    lifestyleWaterRecords.value = waterPage.list || []
    lastLifestyleRefreshAt.value = formatRefreshTime(new Date())
  } catch (error) {
    console.error('加载生活执行复盘失败:', error)
    lifestyleFoodRecords.value = []
    lifestyleExerciseRecords.value = []
    lifestyleWaterRecords.value = []
    lifestyleHistoryLoadFailed.value = true
  } finally {
    lifestyleHistoryLoading.value = false
    lifestyleHistoryInitialized.value = true
  }
}

async function ensureHabitStatsLoaded() {
  if (!isLoggedIn.value) {
    return
  }
  if (habitStatsInitialized.value || habitStatsLoading.value) {
    return
  }
  await loadHabitAnalytics()
}

async function loadHabitAnalytics() {
  if (!isLoggedIn.value) {
    habitStats.value = null
    habitStatsLoadFailed.value = false
    habitStatsLoading.value = false
    habitStatsInitialized.value = false
    return
  }

  habitStatsLoading.value = true
  habitStatsLoadFailed.value = false
  try {
    habitStats.value = await getHabitStats()
    lastHabitRefreshAt.value = formatRefreshTime(new Date())
  } catch (error) {
    console.error('加载习惯分析失败:', error)
    habitStats.value = null
    habitStatsLoadFailed.value = true
  } finally {
    habitStatsLoading.value = false
    habitStatsInitialized.value = true
  }
}

function formatWeightChange() {
  const trendItems = dashboard.value?.weightTrend || []
  if (trendItems.length < 2) {
    return '待对比'
  }
  const latest = Number(trendItems[trendItems.length - 1].weight || 0)
  const previous = Number(trendItems[trendItems.length - 2].weight || 0)
  const diff = Number((latest - previous).toFixed(1))
  if (Number.isNaN(diff) || diff === 0) {
    return '持平'
  }
  return `${diff > 0 ? '+' : ''}${diff} kg`
}

function bmiStatusText(bmi?: number | null) {
  if (!bmi) return '未知'
  if (bmi < 18.5) return '偏瘦'
  if (bmi < 24) return '正常'
  if (bmi < 28) return '超重'
  return '肥胖'
}

function bodyFatStatusText(bodyFatRate?: number | null, gender?: string | null) {
  if (bodyFatRate === null || bodyFatRate === undefined) return '未填写'
  const profile = getBodyFatRangeProfile(gender)
  if (!profile) return '待补性别'
  if (bodyFatRate < profile.low) return '偏低'
  if (bodyFatRate < profile.normalHigh) return '标准'
  if (bodyFatRate < profile.high) return '偏高'
  return '肥胖'
}

function bodyFatBadgeClassName(bodyFatRate?: number | null, gender?: string | null) {
  if (bodyFatRate === null || bodyFatRate === undefined) return 'wc-badge--soft'
  const profile = getBodyFatRangeProfile(gender)
  if (!profile) return 'wc-badge--soft'
  if (bodyFatRate < profile.low) return 'wc-badge--warning'
  if (bodyFatRate < profile.normalHigh) return 'wc-badge--success'
  return 'wc-badge--danger'
}

function selectAnalyticsTab(tab: AnalyticsTabKey) {
  activeTab.value = tab
}

function selectTrendRange(range: TrendRangeKey) {
  trendRange.value = range
}

function selectCalendarDay(cell: CalendarCell) {
  if (cell.outside || !cell.dateKey) {
    return
  }
  selectedDateKey.value = cell.dateKey
}

function switchCalendarMonth(offset: number) {
  const current = selectedMonthDate.value
  const next = new Date(current.getFullYear(), current.getMonth() + offset, 1)
  selectedMonthDate.value = next
  const today = new Date()
  const fallbackDay = next.getFullYear() === today.getFullYear() && next.getMonth() === today.getMonth()
    ? today.getDate()
    : 1
  selectedDateKey.value = formatDateParam(new Date(next.getFullYear(), next.getMonth(), fallbackDay))
}

function openRecordDetail(record: AnalyticsRecordItem) {
  selectedRecord.value = record
}

function closeRecordDetail() {
  selectedRecord.value = null
}

function toRecordTimestamp(value?: string | null) {
  return toApiTimestamp(value)
}

function formatRecordMeta(recordedAt?: string | null, extra?: string) {
  const segments: string[] = []
  if (recordedAt) {
    segments.push(formatRecordTime(recordedAt))
  }
  if (extra) {
    segments.push(extra)
  }
  return segments.join(' · ') || '时间待补'
}

function formatRecordTime(value?: string | null) {
  return formatApiDateTime(value, '')
}

function parseRecordImages(rawImages?: string[] | string | null, fallback: string[] = []) {
  let images: string[] = []
  if (Array.isArray(rawImages)) {
    images = rawImages
  } else if (typeof rawImages === 'string' && rawImages.trim()) {
    const text = rawImages.trim()
    try {
      const parsed = JSON.parse(text)
      images = Array.isArray(parsed) ? parsed : [text]
    } catch {
      images = [text]
    }
  }
  return Array.from(new Set([...images, ...fallback].map((item) => `${item || ''}`.trim()).filter(Boolean))).slice(0, 9)
}

function formatRefreshTime(date: Date) {
  const hours = `${date.getHours()}`.padStart(2, '0')
  const minutes = `${date.getMinutes()}`.padStart(2, '0')
  return `${hours}:${minutes}`
}

function formatDateParam(date: Date) {
  const year = date.getFullYear()
  const month = `${date.getMonth() + 1}`.padStart(2, '0')
  const day = `${date.getDate()}`.padStart(2, '0')
  return `${year}-${month}-${day}`
}

function parseDateKey(dateKey: string) {
  const [year, month, day] = dateKey.split('-').map((item) => Number(item))
  return new Date(year || new Date().getFullYear(), (month || 1) - 1, day || 1)
}

function getMonthStart(date: Date) {
  return new Date(date.getFullYear(), date.getMonth(), 1)
}

function daysInMonth(date: Date) {
  return new Date(date.getFullYear(), date.getMonth() + 1, 0).getDate()
}

function mondayFirstOffset(date: Date) {
  const day = new Date(date.getFullYear(), date.getMonth(), 1).getDay()
  return day === 0 ? 6 : day - 1
}

function formatMonthTitle(date: Date) {
  return `${date.getFullYear()}年${date.getMonth() + 1}月`
}

function formatDateLabel(dateKey: string) {
  const date = parseDateKey(dateKey)
  return `${date.getMonth() + 1}月${date.getDate()}日`
}

function toDateKey(value?: string | null) {
  return toApiDateKey(value)
}

function buildRecentDateSeries(days: number) {
  const result: Date[] = []
  const today = new Date()
  today.setHours(0, 0, 0, 0)
  for (let offset = days - 1; offset >= 0; offset -= 1) {
    const date = new Date(today)
    date.setDate(today.getDate() - offset)
    result.push(date)
  }
  return result
}

function isSameCalendarDay(left: Date, right: Date) {
  return (
    left.getFullYear() === right.getFullYear() &&
    left.getMonth() === right.getMonth() &&
    left.getDate() === right.getDate()
  )
}

function getWeekdayLabel(date: Date) {
  const labels = ['日', '一', '二', '三', '四', '五', '六']
  return labels[date.getDay()] || ''
}

function buildDailyAnalysis(date: Date, dateKey: string, day: number): DailyAnalysisItem {
  const today = new Date()
  today.setHours(0, 0, 0, 0)
  const targetDate = new Date(date)
  targetDate.setHours(0, 0, 0, 0)
  const isFuture = targetDate.getTime() > today.getTime()

  const bodyRecords = allRecords.value
    .filter((record) => record.type === 'weight' && recordDateKey(record) === dateKey)
    .sort((a, b) => b.sortTime - a.sortTime)
  const lifestyleRecords = allRecords.value
    .filter((record) => ['food', 'exercise', 'water'].includes(record.type) && recordDateKey(record) === dateKey)
    .sort((a, b) => b.sortTime - a.sortTime)
  const foodForDay = foodRecords.value.filter((record) => toDateKey(record.recordedAt) === dateKey)
  const waterForDay = waterRecords.value.filter((record) => toDateKey(record.recordedAt) === dateKey)
  const exerciseForDay = exerciseRecords.value.filter((record) => toDateKey(record.recordedAt) === dateKey)
  const habitRecordsForDay = habitRecords.value.filter((record) => recordDateKey(record) === dateKey)

  const latestWeightRecordForDay = weightRecords.value
    .filter((record) => toDateKey(record.recordedAt) === dateKey && record.weight !== null && record.weight !== undefined)
    .sort(compareWeightRecordsDesc)[0] || null
  const previousWeightRecord = latestWeightRecordForDay
    ? weightRecords.value
      .filter((record) => {
        const time = toRecordTimestamp(record.recordedAt)
        return record.weight !== null && record.weight !== undefined && time > 0 && time < toRecordTimestamp(latestWeightRecordForDay.recordedAt)
      })
      .sort(compareWeightRecordsDesc)[0] || null
    : null
  const weightChangeText = formatDailyWeightChange(latestWeightRecordForDay, previousWeightRecord)
  const dinnerCalories = foodForDay
    .filter((record) => mealTypeLabel(record.mealType) === '晚餐')
    .reduce((sum, record) => sum + Number(record.calories || 0), 0)
  const calorieTarget = summary.value?.calorieTarget || 0
  const dinnerPercent = dinnerCalories > 0 && calorieTarget > 0
    ? Math.round((dinnerCalories / Math.max(calorieTarget * 0.45, 1)) * 100)
    : null
  const waterCups = waterForDay.reduce((sum, record) => sum + Number(record.cups || 0), 0)
  const waterTarget = summary.value?.waterTarget || 8
  const waterPercent = waterCups > 0 ? Math.round((waterCups / Math.max(waterTarget, 1)) * 100) : null
  const exerciseMinutes = exerciseForDay.reduce((sum, record) => sum + Number(record.durationMinutes || 0), 0)
  const habitDueCount = isSameCalendarDay(targetDate, today)
    ? habitStats.value?.activeHabits || habitRecordsForDay.length
    : habitRecordsForDay.length
  const habitCompletedCount = habitRecordsForDay.length

  const missing: string[] = []
  if (!isFuture) {
    if (!bodyRecords.length) missing.push('体重')
    if (!foodForDay.length) missing.push('晚餐')
    if (!waterForDay.length) missing.push('饮水')
  }

  const status = getDailyStatus({
    isFuture,
    missing,
    weightChangeText,
    dinnerPercent,
    waterPercent
  })
  const statusText = dailyStatusText(status)
  const title = getDailyTitle(status, weightChangeText)
  const text = getDailyText(status, missing)

  return {
    dateKey,
    dateLabel: formatDateLabel(dateKey),
    day,
    status,
    statusText,
    title,
    text,
    weightChangeText,
    dinnerPercent,
    waterPercent,
    exerciseMinutes,
    missing,
    body: {
      records: bodyRecords,
      weight: latestWeightRecordForDay?.weight !== null && latestWeightRecordForDay?.weight !== undefined ? `${latestWeightRecordForDay.weight}` : '--',
      bmi: latestWeightRecordForDay?.bmi !== null && latestWeightRecordForDay?.bmi !== undefined ? `${latestWeightRecordForDay.bmi}` : '--',
      bmiStatus: bmiStatusText(latestWeightRecordForDay?.bmi)
    },
    lifestyle: {
      records: lifestyleRecords,
      foodRecords: foodForDay,
      waterRecords: waterForDay,
      exerciseRecords: exerciseForDay
    },
    habit: {
      dueCount: habitDueCount,
      completedCount: habitCompletedCount,
      records: habitRecordsForDay
    }
  }
}

function recordDateKey(record: AnalyticsRecordItem) {
  if (record.sortTime > 0) {
    return formatDateParam(new Date(record.sortTime))
  }
  return ''
}

function formatDailyWeightChange(current?: WeightRecordPayload | null, previous?: WeightRecordPayload | null) {
  if (!current?.weight) {
    return '--'
  }
  if (!previous?.weight) {
    return '新记录'
  }
  const diff = Number((Number(current.weight) - Number(previous.weight)).toFixed(1))
  if (Number.isNaN(diff) || diff === 0) {
    return '持平'
  }
  return `${diff > 0 ? '+' : ''}${diff} kg`
}

function formatCalendarWeightText(value?: string) {
  if (!value || value === '--' || value === '新记录') {
    return ''
  }
  if (value === '持平') {
    return '0kg'
  }
  if (!value.startsWith('+') && !value.startsWith('-')) {
    return ''
  }
  return value.replace(/\s+/g, '')
}

function formatNullablePercent(value: number | null) {
  return value === null ? '--' : `${value}%`
}

function getDailyStatus(input: {
  isFuture: boolean
  missing: string[]
  weightChangeText: string
  dinnerPercent: number | null
  waterPercent: number | null
}): DailyStatus {
  if (input.isFuture) return 'future'
  if (input.missing.length) return 'missing'
  const weightWave = input.weightChangeText.startsWith('+') && Number(input.weightChangeText.replace(/[+ kg]/g, '')) >= 0.2
  const dinnerWave = input.dinnerPercent !== null && input.dinnerPercent > 110
  const waterWave = input.waterPercent !== null && input.waterPercent < 80
  return weightWave || dinnerWave || waterWave ? 'wave' : 'stable'
}

function dailyStatusText(status: DailyStatus) {
  const map: Record<DailyStatus, string> = {
    stable: '稳了',
    wave: '波动',
    missing: '待补',
    future: '还没到'
  }
  return map[status]
}

function getDailyTitle(status: DailyStatus, weightChangeText: string) {
  if (status === 'future') return '这天还没到'
  if (status === 'missing') return '先把记录补齐'
  if (status === 'wave') return '这天波动有原因'
  if (weightChangeText.startsWith('-')) return '这天挺稳的'
  return '这天可以参考'
}

function getDailyText(status: DailyStatus, missing: string[]) {
  if (status === 'future') return '现在先不用看这天，等记录出现后再复盘。'
  if (status === 'missing') return `少了${missing.join('、')}，现在看容易跑偏。`
  if (status === 'wave') return '更像吃饭、喝水或作息带来的短期变化，不用马上改计划。'
  return '体重没乱跳，吃喝记录也比较完整，可以放心参考。'
}

function compareWeightRecordsDesc(a: WeightRecordPayload, b: WeightRecordPayload) {
  const timeDiff = toRecordTimestamp(b.recordedAt) - toRecordTimestamp(a.recordedAt)
  if (timeDiff !== 0) {
    return timeDiff
  }
  return (b.id || 0) - (a.id || 0)
}

function mealTypeLabel(mealType?: string | null) {
  const map: Record<string, string> = {
    '0': '早餐',
    '1': '午餐',
    '2': '晚餐',
    '3': '加餐'
  }
  return map[mealType || ''] || '饮食'
}

function recordTypeLabel(type: AnalyticsRecordItem['type']) {
  const map: Record<AnalyticsRecordItem['type'], string> = {
    weight: '体重',
    food: '饮食',
    exercise: '运动',
    water: '饮水',
    habit: '习惯'
  }
  return map[type]
}

function formatTrendWeight(weight: number) {
  return Number.isInteger(weight) ? `${weight}` : weight.toFixed(1)
}

function formatTrendBodyFat(bodyFatRate: number) {
  return Number.isInteger(bodyFatRate) ? `${bodyFatRate}` : bodyFatRate.toFixed(1)
}

function buildTrendBuckets(records: WeightRecordPayload[], range: TrendRangeKey, metric: 'weight' | 'bodyFatRate') {
  const sorted = records
    .filter((record) => record.recordedAt && record[metric] !== null && record[metric] !== undefined)
    .map((record) => ({
      ...record,
      timestamp: toRecordTimestamp(record.recordedAt),
      metricValue: Number(record[metric] as number)
    }))
    .filter((record) => !Number.isNaN(record.timestamp) && !Number.isNaN(record.metricValue))
    .sort((a, b) => a.timestamp - b.timestamp)

  const bucketMap = new Map<string, { label: string; value: number; sortKey: number }>()

  sorted.forEach((record) => {
    const date = new Date(record.timestamp)
    const bucket = getTrendBucket(date, record.metricValue, range)
    bucketMap.set(bucket.key, bucket)
  })

  const limit = range === 'day' ? 7 : range === 'week' ? 8 : 6
  return Array.from(bucketMap.values())
    .sort((a, b) => a.sortKey - b.sortKey)
    .slice(-limit)
}

function buildTrendChartPoints(
  buckets: Array<{ label: string; value: number; sortKey: number }>,
  formatter: (value: number) => string
) {
  if (!buckets.length) {
    return []
  }
  const values = buckets.map((item) => item.value).filter((value) => !Number.isNaN(value))
  const min = Math.min(...values)
  const max = Math.max(...values)
  const range = max - min
  return buckets.map((item, index) => {
    const ratio = buckets.length === 1 ? 0.5 : index / (buckets.length - 1)
    const yRatio = range === 0 ? 0.5 : (item.value - min) / range
    return {
      label: item.label,
      value: formatter(item.value),
      x: Math.round(ratio * ANALYTICS_CHART_WIDTH),
      y: Math.round(ANALYTICS_CHART_HEIGHT - yRatio * 92)
    }
  })
}

function buildTrendChartSegments(points: Array<{ x: number; y: number }>) {
  if (points.length < 2) {
    return []
  }
  return points.slice(0, -1).map((point, index) => {
    const nextPoint = points[index + 1]
    const deltaX = nextPoint.x - point.x
    const deltaY = nextPoint.y - point.y
    return {
      left: point.x,
      top: point.y,
      width: Math.sqrt(deltaX * deltaX + deltaY * deltaY),
      angle: (Math.atan2(deltaY, deltaX) * 180) / Math.PI
    }
  })
}

function getTrendBucket(date: Date, value: number, range: TrendRangeKey) {
  const year = date.getFullYear()
  const month = `${date.getMonth() + 1}`.padStart(2, '0')
  const day = `${date.getDate()}`.padStart(2, '0')

  if (range === 'day') {
    const bucketDate = new Date(date)
    bucketDate.setHours(0, 0, 0, 0)
    return {
      key: `${year}-${month}-${day}`,
      label: `${month}-${day}`,
      value,
      sortKey: bucketDate.getTime()
    }
  }

  if (range === 'week') {
    const monday = getWeekStart(date)
    const sunday = new Date(monday)
    sunday.setDate(monday.getDate() + 6)
    const label = `${`${monday.getMonth() + 1}`.padStart(2, '0')}/${`${monday.getDate()}`.padStart(2, '0')}`
    return {
      key: `${monday.getFullYear()}-${monday.getMonth()}-${monday.getDate()}`,
      label,
      value,
      sortKey: monday.getTime()
    }
  }

  const monthStart = new Date(year, date.getMonth(), 1)
  return {
    key: `${year}-${month}`,
    label: `${year}.${month}`,
    value,
    sortKey: monthStart.getTime()
  }
}

function getBodyFatRangeProfile(gender?: string | null) {
  if (gender === '1') {
    return {
      low: 10,
      normalHigh: 20,
      high: 25
    }
  }
  if (gender === '2') {
    return {
      low: 18,
      normalHigh: 28,
      high: 33
    }
  }
  return null
}

function getWeekStart(date: Date) {
  const result = new Date(date)
  result.setHours(0, 0, 0, 0)
  const day = result.getDay()
  const offset = day === 0 ? -6 : 1 - day
  result.setDate(result.getDate() + offset)
  return result
}

function reloadRecentRecords() {
  if (!ensureLoggedInForAnalyticsAction()) {
    return
  }
  void loadRecentRecords()
}

function reloadLifestyleHistory() {
  if (!ensureLoggedInForAnalyticsAction()) {
    return
  }
  void loadLifestyleHistory()
}

function reloadDashboardSummary() {
  if (!ensureLoggedInForAnalyticsAction()) {
    return
  }
  void loadDashboardSummary()
}

function reloadHabitAnalytics() {
  if (!ensureLoggedInForAnalyticsAction()) {
    return
  }
  void loadHabitAnalytics()
}

function reloadAllData() {
  if (!ensureLoggedInForAnalyticsAction()) {
    return
  }
  void loadAnalyticsData()
}

function goHabitPage() {
  if (!ensureLoggedInForAnalyticsAction('/pages/habit/index')) {
    return
  }
  uni.navigateTo({ url: '/pages/habit/index' })
}

function goHabitHistoryPage() {
  goRecordHistoryPage({ type: 'habit', range: '30d', source: 'lifestyle' })
}

function goRecordPage(tab: 'weight' | 'diet' | 'exercise' | 'water' = 'weight') {
  const url = `/pages/record/index?tab=${tab}`
  if (!ensureLoggedInForAnalyticsAction(url)) {
    return
  }
  uni.navigateTo({ url })
}

function openLifestyleTrack(track: LifestyleTrackCard) {
  goRecordHistoryPage({ type: track.historyType, range: '7d', source: 'lifestyle' })
}

function goRecordHistoryPage(
  options: { type?: RecordHistoryTypeKey; range?: RecordHistoryRangeKey; source?: RecordHistorySourceKey } = {}
) {
  const queryParts: string[] = []
  if (options.type && options.type !== 'all') {
    queryParts.push(`type=${encodeURIComponent(options.type)}`)
  }
  if (options.range && options.range !== 'all') {
    queryParts.push(`range=${encodeURIComponent(options.range)}`)
  }
  if (options.source) {
    queryParts.push(`source=${encodeURIComponent(options.source)}`)
  }
  const url = queryParts.length
    ? `/pages/record-history/index?${queryParts.join('&')}`
    : '/pages/record-history/index'
  if (!ensureLoggedInForAnalyticsAction(url)) {
    return
  }
  uni.navigateTo({ url })
}

function ensureLoggedInForAnalyticsAction(redirectUrl = '/pages/analytics/index') {
  if (isLoggedIn.value) {
    return true
  }
  openLoginPage('safe', redirectUrl)
  return false
}
</script>

<style scoped lang="scss">
$primary: var(--wc-primary);
$bg-page: var(--wc-bg);
$text-main: var(--wc-text);
$text-sub: var(--wc-text-soft);

@use '../../styles/flow-button.scss';

.analytics-container {
  height: 100vh;
  background: $bg-page;
  overflow: hidden;
  position: relative;
}

.header {
  padding: 0 30rpx;
}

.page-title {
  font-size: 36rpx;
  font-weight: 700;
}

.scroll-content {
  padding-right: 30rpx;
  padding-left: 30rpx;
  position: relative;
}

.refresh-meta {
  display: flex;
  flex-wrap: wrap;
  align-items: flex-start;
  justify-content: space-between;
  gap: 18rpx;
  margin-bottom: 22rpx;
  padding: 0 6rpx;
}

.refresh-meta__text {
  width: 100%;
  font-size: 22rpx;
  color: var(--wc-text-soft);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.refresh-meta--quiet {
  margin-bottom: 16rpx;
}

.calendar-hero {
  display: flex;
  flex-direction: column;
  padding: 24rpx 22rpx 26rpx;
  margin-bottom: 24rpx;
  border-radius: 30rpx;
  border: 1px solid rgba(39, 92, 72, 0.055);
  box-shadow: none;
  background:
    radial-gradient(circle at 8% 0%, rgba(232, 248, 241, 0.82), transparent 34%),
    linear-gradient(180deg, rgba(255, 254, 250, 0.96), rgba(251, 253, 248, 0.98));
}

.calendar-panel {
  display: flex;
  flex-direction: column;
}

.calendar-toolbar {
  display: grid;
  grid-template-columns: 64rpx minmax(0, 1fr) 64rpx;
  align-items: center;
  gap: 14rpx;
  margin-bottom: 14rpx;
}

.calendar-nav {
  width: 64rpx;
  height: 64rpx;
  line-height: 60rpx;
  text-align: center;
  border-radius: 50%;
  border: 2rpx solid rgba(39, 92, 72, 0.1);
  background: rgba(255, 255, 255, 0.92);
  color: #61716a;
  font-size: 34rpx;
  font-weight: 700;
  box-sizing: border-box;
}

.calendar-toolbar__center {
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 4rpx;
  align-items: center;
}

.calendar-month {
  font-size: 30rpx;
  line-height: 1.25;
  font-weight: 800;
  color: #17231f;
}

.calendar-summary {
  max-width: 100%;
  font-size: 22rpx;
  line-height: 1.35;
  color: #7f8e86;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.calendar-weekdays,
.calendar-grid {
  display: grid;
  grid-template-columns: repeat(7, minmax(0, 1fr));
  gap: 10rpx;
}

.calendar-weekdays {
  margin-bottom: 10rpx;
}

.calendar-weekdays text {
  text-align: center;
  font-size: 20rpx;
  color: #93a39b;
  font-weight: 700;
}

.calendar-day {
  min-width: 0;
  height: 72rpx;
  border-radius: 16rpx;
  border: 0;
  background: #f6f8f4;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 5rpx;
  box-sizing: border-box;
}

.calendar-day__date {
  font-size: 22rpx;
  line-height: 1;
  font-weight: 800;
  color: #829189;
}

.calendar-day__weight {
  max-width: 100%;
  font-size: 16rpx;
  line-height: 1;
  font-weight: 700;
  color: #8b9a92;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.calendar-day.is-stable {
  background: #e5f8ef;
}

.calendar-day.is-wave {
  background: #fff1d6;
}

.calendar-day.is-missing {
  background: #eef7f2;
}

.calendar-day.is-future,
.calendar-day.is-outside {
  background: #f5f8f4;
}

.calendar-day.is-outside {
  opacity: 0.62;
}

.calendar-day.is-active {
  background: linear-gradient(135deg, #1fa873, #35c58d);
  box-shadow: 0 8rpx 18rpx rgba(47, 179, 123, 0.18);
}

.calendar-day.is-stable .calendar-day__date,
.calendar-day.is-stable .calendar-day__weight {
  color: #18a66c;
}

.calendar-day.is-wave .calendar-day__date,
.calendar-day.is-wave .calendar-day__weight {
  color: #c97910;
}

.calendar-day.is-missing .calendar-day__date,
.calendar-day.is-missing .calendar-day__weight,
.calendar-day.is-future .calendar-day__date,
.calendar-day.is-future .calendar-day__weight,
.calendar-day.is-outside .calendar-day__date,
.calendar-day.is-outside .calendar-day__weight {
  color: #8fa098;
}

.calendar-day.is-active .calendar-day__date,
.calendar-day.is-active .calendar-day__weight,
.calendar-day.is-today.is-active .calendar-day__date {
  color: #fff;
}

.calendar-legend {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 18rpx;
  margin-top: 18rpx;
}

.calendar-legend__item {
  display: flex;
  align-items: center;
  gap: 7rpx;
  font-size: 20rpx;
  line-height: 1;
  color: #77887f;
  font-weight: 600;
}

.calendar-legend__dot {
  width: 13rpx;
  height: 13rpx;
  border-radius: 50%;
  background: #cbd5e1;
}

.calendar-legend__dot.is-stable,
.calendar-progress__segment.is-stable {
  background: #2fb37b;
}

.calendar-legend__dot.is-wave,
.calendar-progress__segment.is-wave {
  background: #f4a338;
}

.calendar-legend__dot.is-missing,
.calendar-progress__segment.is-missing {
  background: #a6c6b8;
}

.calendar-legend__dot.is-future,
.calendar-progress__segment.is-future {
  background: #dbe8df;
}

.calendar-month-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16rpx;
  margin-top: 26rpx;
}

.calendar-month-footer__change {
  flex-shrink: 0;
  font-size: 24rpx;
  line-height: 1.2;
  font-weight: 800;
  color: #17231f;
}

.calendar-month-footer__summary {
  min-width: 0;
  font-size: 20rpx;
  line-height: 1.2;
  font-weight: 700;
  color: #6f8077;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  text-align: right;
}

.calendar-progress {
  display: flex;
  width: 100%;
  height: 13rpx;
  margin-top: 18rpx;
  border-radius: 999rpx;
  overflow: hidden;
  background: #edf5ef;
}

.calendar-progress__segment {
  height: 100%;
  min-width: 0;
}

.analytics-overview {
  display: flex;
  flex-direction: column;
  gap: 24rpx;
  padding: 30rpx;
  margin-bottom: 24rpx;
  border-radius: 34rpx;
  border: 1px solid rgba(210, 218, 230, 0.82);
  box-shadow: var(--wc-shadow-soft);
  background:
    radial-gradient(circle at top right, rgba(47, 179, 123, 0.16), transparent 36%),
    linear-gradient(180deg, rgba(246, 250, 255, 0.98), rgba(255, 255, 255, 0.98));
}

.analytics-overview--good {
  background:
    radial-gradient(circle at top right, rgba(43, 167, 121, 0.16), transparent 34%),
    linear-gradient(180deg, rgba(241, 252, 247, 0.98), rgba(255, 255, 255, 0.98));
}

.analytics-overview--caution {
  background:
    radial-gradient(circle at top right, rgba(242, 155, 56, 0.18), transparent 34%),
    linear-gradient(180deg, rgba(255, 249, 242, 0.98), rgba(255, 255, 255, 0.98));
}

.analytics-overview--missing {
  background:
    radial-gradient(circle at top right, rgba(148, 163, 184, 0.18), transparent 34%),
    linear-gradient(180deg, rgba(248, 250, 252, 0.98), rgba(255, 255, 255, 0.98));
}

.analytics-overview__copy {
  display: flex;
  flex-direction: column;
  gap: 12rpx;
}

.analytics-overview__eyebrow {
  font-size: 20rpx;
  font-weight: 700;
  letter-spacing: 0.08em;
  color: var(--wc-primary-strong);
}

.analytics-overview__title-row {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16rpx;
}

.analytics-overview__title {
  flex: 1;
  font-size: 38rpx;
  font-weight: 700;
  line-height: 1.3;
  color: var(--wc-text);
}

.analytics-overview__tag {
  flex-shrink: 0;
  min-width: 96rpx;
  padding: 10rpx 16rpx;
  border-radius: 999rpx;
  background: rgba(255, 255, 255, 0.88);
  border: 1px solid rgba(210, 218, 230, 0.72);
  text-align: center;
  font-size: 20rpx;
  font-weight: 700;
  color: var(--wc-text);
}

.analytics-overview__desc {
  font-size: 22rpx;
  line-height: 1.7;
  color: var(--wc-text-soft);
}

.overview-metrics {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16rpx;
}

.overview-metric {
  min-width: 0;
  padding: 22rpx 20rpx;
  border-radius: 24rpx;
  background: rgba(255, 255, 255, 0.88);
  border: 1px solid rgba(226, 232, 240, 0.92);
  display: flex;
  flex-direction: column;
  gap: 10rpx;
}

.overview-metric--weight {
  background: linear-gradient(180deg, rgba(47, 179, 123, 0.1), rgba(255, 255, 255, 0.95));
}

.overview-metric--bodyfat {
  background: linear-gradient(180deg, rgba(43, 167, 121, 0.1), rgba(255, 255, 255, 0.95));
}

.overview-metric__label {
  font-size: 20rpx;
  color: var(--wc-text-soft);
}

.overview-metric__value {
  font-size: 38rpx;
  font-weight: 700;
  color: var(--wc-text);
  line-height: 1.2;
}

.overview-metric__note {
  font-size: 20rpx;
  line-height: 1.6;
  color: var(--wc-text-soft);
}

.analytics-tabs {
  display: flex;
  align-items: center;
  gap: 10rpx;
  padding: 8rpx;
  margin-bottom: 24rpx;
  border-radius: 999rpx;
  background: rgba(239, 247, 241, 0.74);
  border: 1px solid rgba(39, 92, 72, 0.055);
  box-shadow: none;
}

.analytics-tab {
  flex: 1;
  min-width: 0;
  min-height: 62rpx;
  line-height: 62rpx;
  text-align: center;
  border-radius: 999rpx;
  font-size: 24rpx;
  font-weight: 600;
  color: var(--wc-text-soft);
}

.analytics-tab.active {
  background: rgba(255, 255, 255, 0.86);
  color: var(--wc-primary-strong);
  box-shadow: none;
}

.day-review {
  display: flex;
  flex-direction: column;
  gap: 22rpx;
  padding: 28rpx;
  margin-bottom: 24rpx;
  border-radius: 34rpx;
  border: 1px solid rgba(39, 92, 72, 0.055);
  box-shadow: none;
  background: linear-gradient(180deg, rgba(248, 252, 247, 0.98), rgba(255, 255, 255, 0.98));
}

.day-review--stable {
  background: linear-gradient(180deg, rgba(236, 253, 245, 0.96), rgba(255, 255, 255, 0.98));
}

.day-review--wave {
  background: linear-gradient(180deg, rgba(255, 247, 237, 0.96), rgba(255, 255, 255, 0.98));
}

.day-review--missing,
.day-review--future {
  background: linear-gradient(180deg, rgba(248, 250, 252, 0.98), rgba(255, 255, 255, 0.98));
}

.day-review__head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 20rpx;
}

.day-review__copy {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 8rpx;
}

.day-review__eyebrow {
  font-size: 20rpx;
  font-weight: 700;
  color: var(--wc-primary-strong);
}

.day-review__title {
  font-size: 34rpx;
  line-height: 1.3;
  font-weight: 700;
  color: var(--wc-text);
}

.day-review__desc {
  font-size: 22rpx;
  line-height: 1.65;
  color: var(--wc-text-soft);
}

.day-review__status {
  min-width: 0;
  padding-top: 6rpx;
  display: flex;
  align-items: center;
  gap: 8rpx;
  color: var(--wc-primary-strong);
  font-size: 22rpx;
  font-weight: 700;
}

.day-review__status::before {
  content: '';
  width: 12rpx;
  height: 12rpx;
  border-radius: 50%;
  background: currentColor;
}

.summary-grid {
  display: flex;
  flex-direction: column;
  gap: 0;
  padding: 4rpx 2rpx;
  border-radius: 28rpx;
  background: rgba(255, 255, 255, 0.52);
}

.summary-card {
  min-width: 0;
  padding: 20rpx 8rpx;
  border-radius: 0;
  background: transparent;
  border: 0;
  border-bottom: 1px solid rgba(39, 92, 72, 0.06);
  display: grid;
  grid-template-columns: 150rpx minmax(0, 1fr);
  align-items: center;
  column-gap: 18rpx;
  row-gap: 6rpx;
}

.summary-card:last-child {
  border-bottom: 0;
}

.summary-card__label {
  font-size: 20rpx;
  line-height: 1.25;
  color: var(--wc-text-soft);
  font-weight: 600;
}

.summary-card__value {
  font-size: 30rpx;
  line-height: 1.18;
  font-weight: 700;
  color: var(--wc-text);
  text-align: right;
}

.summary-card__meta {
  grid-column: 2;
  font-size: 20rpx;
  line-height: 1.45;
  color: var(--wc-text-faint);
  text-align: right;
}

.scroll-content > .metrics-grid {
  margin-top: 0;
}

.analysis-spotlight {
  display: flex;
  flex-direction: column;
  gap: 24rpx;
  padding: 30rpx;
  margin-bottom: 24rpx;
  border-radius: 34rpx;
  background:
    radial-gradient(circle at top right, rgba(47, 179, 123, 0.16), transparent 36%),
    linear-gradient(180deg, rgba(246, 250, 255, 0.98), rgba(255, 255, 255, 0.98));
  border: 1px solid rgba(210, 218, 230, 0.82);
  box-shadow: var(--wc-shadow-soft);
}

.analysis-spotlight--focus {
  background:
    radial-gradient(circle at top right, rgba(47, 179, 123, 0.16), transparent 36%),
    linear-gradient(180deg, rgba(246, 250, 255, 0.98), rgba(255, 255, 255, 0.98));
}

.analysis-spotlight--good {
  background:
    radial-gradient(circle at top right, rgba(43, 167, 121, 0.16), transparent 34%),
    linear-gradient(180deg, rgba(241, 252, 247, 0.98), rgba(255, 255, 255, 0.98));
}

.analysis-spotlight--caution {
  background:
    radial-gradient(circle at top right, rgba(242, 155, 56, 0.18), transparent 34%),
    linear-gradient(180deg, rgba(255, 249, 242, 0.98), rgba(255, 255, 255, 0.98));
}

.analysis-spotlight--missing {
  background:
    radial-gradient(circle at top right, rgba(148, 163, 184, 0.18), transparent 34%),
    linear-gradient(180deg, rgba(248, 250, 252, 0.98), rgba(255, 255, 255, 0.98));
}

.analysis-spotlight__copy {
  display: flex;
  flex-direction: column;
  gap: 10rpx;
}

.analysis-spotlight__eyebrow {
  font-size: 20rpx;
  font-weight: 700;
  letter-spacing: 0.08em;
  color: var(--wc-primary-strong);
}

.analysis-spotlight__title {
  font-size: 38rpx;
  font-weight: 700;
  line-height: 1.28;
  color: var(--wc-text);
}

.analysis-spotlight__desc {
  font-size: 22rpx;
  line-height: 1.7;
  color: var(--wc-text-soft);
}

.analysis-spotlight__meta {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16rpx;
}

.analysis-spotlight__metric {
  padding: 18rpx 20rpx;
  border-radius: 22rpx;
  background: rgba(255, 255, 255, 0.88);
  border: 1px solid rgba(226, 232, 240, 0.92);
  display: flex;
  flex-direction: column;
  gap: 8rpx;
}

.analysis-spotlight__metric-label {
  font-size: 18rpx;
  color: var(--wc-text-faint);
}

.analysis-spotlight__metric-value {
  font-size: 28rpx;
  font-weight: 700;
  color: var(--wc-text);
}

.body-summary-grid {
  margin-top: 0;
}

.metrics-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 18rpx;
  margin-bottom: 24rpx;
}

.metric-card,
.chart-section {
  background: var(--wc-surface-strong);
  border-radius: 32rpx;
  padding: 28rpx;
  border: 1px solid var(--wc-line);
  box-shadow: none;
  margin-bottom: 24rpx;
}

.metric-card--lead {
  grid-column: span 2;
  background: linear-gradient(180deg, rgba(47, 179, 123, 0.1), rgba(255, 255, 255, 0.98));
}

.metric-card--status {
  background: linear-gradient(180deg, rgba(47, 179, 123, 0.06), rgba(255, 255, 255, 0.98));
}

.metric-card--bodyfat {
  background: linear-gradient(180deg, rgba(43, 167, 121, 0.1), rgba(255, 255, 255, 0.98));
}

.metric-label {
  display: block;
  font-size: 22rpx;
  color: $text-sub;
  margin-bottom: 12rpx;
}

.metric-val {
  display: block;
  font-size: 40rpx;
  font-weight: 700;
  margin-bottom: 10rpx;
}

.metric-card--lead .metric-val {
  font-size: 56rpx;
}

.metric-note {
  display: block;
  margin-top: 12rpx;
  font-size: 20rpx;
  line-height: 1.6;
  color: var(--wc-text-soft);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20rpx;
  gap: 16rpx;
}

.section-header--stack {
  align-items: flex-start;
}

.section-header--feature {
  align-items: flex-start;
}

.section-header__main {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 10rpx;
}

.section-header__summary {
  font-size: 22rpx;
  line-height: 1.6;
  color: var(--wc-text-soft);
}

.section-header__action {
  flex-shrink: 0;
  display: flex;
  align-items: flex-start;
  gap: 12rpx;
  flex-wrap: wrap;
  justify-content: flex-end;
}

.section-title-group {
  display: flex;
  flex-direction: column;
  gap: 8rpx;
}

.section-kicker {
  font-size: 18rpx;
  font-weight: 700;
  letter-spacing: 0.08em;
  color: var(--wc-primary-strong);
}

.section-title {
  font-size: 30rpx;
  font-weight: 700;
}

.section-desc {
  font-size: 22rpx;
  line-height: 1.6;
  color: var(--wc-text-soft);
}

.chart-filter {
  flex-shrink: 0;
}

.record-controls {
  margin-bottom: 20rpx;
}

.record-tab-scroll {
  width: 100%;
}

.record-range-dropdown {
  position: relative;
  z-index: 3;
  flex-shrink: 0;
}

.history-range-trigger {
  flex-shrink: 0;
  display: inline-flex;
  align-items: center;
  gap: 10rpx;
  white-space: nowrap;
  min-height: 64rpx;
  padding: 0 24rpx;
  border-radius: 999rpx;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.98), rgba(244, 247, 251, 0.96));
  border: 1px solid rgba(210, 218, 230, 0.9);
  box-shadow: 0 10rpx 24rpx rgba(30, 41, 59, 0.06), inset 0 1px 0 rgba(255, 255, 255, 0.8);
  color: var(--wc-text);
  font-size: 24rpx;
  font-weight: 600;
}

.history-range-menu {
  position: absolute;
  top: calc(100% + 10rpx);
  left: 0;
  min-width: 196rpx;
  padding: 10rpx;
  border-radius: 24rpx;
  background: rgba(255, 255, 255, 0.98);
  border: 1px solid rgba(210, 218, 230, 0.9);
  box-shadow: 0 18rpx 40rpx rgba(15, 23, 42, 0.12);
  backdrop-filter: blur(16rpx);
}

.history-range-option {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16rpx;
  min-height: 64rpx;
  padding: 0 18rpx;
  border-radius: 18rpx;
  font-size: 24rpx;
  color: var(--wc-text-soft);
}

.history-range-option.active {
  background: var(--wc-primary-soft);
  color: var(--wc-primary-strong);
  font-weight: 600;
}

.record-filter-rail {
  display: inline-flex;
  white-space: nowrap;
  min-width: 100%;
}

.history-range-arrow {
  font-size: 18rpx;
  color: var(--wc-text-soft);
  transition: transform 0.2s ease;
}

.history-range-arrow.open {
  transform: rotate(180deg);
}

.filter-btn {
}

.trend-line-chart {
  position: relative;
  height: 188rpx;
  width: 560rpx;
  margin: 0 auto;
}

.trend-line-chart-shell {
  margin-top: 22rpx;
  padding: 18rpx 20rpx 10rpx;
  border-radius: 28rpx;
  background: linear-gradient(180deg, rgba(248, 251, 255, 0.98), rgba(255, 255, 255, 0.98));
  border: 1px solid rgba(210, 218, 230, 0.75);
}

.trend-line-chart-shell--bodyfat {
  background: linear-gradient(180deg, rgba(244, 252, 248, 0.98), rgba(255, 255, 255, 0.98));
}

.trend-range-rail {
  display: inline-flex;
  align-items: center;
  gap: 10rpx;
  padding: 8rpx;
  border-radius: 999rpx;
  background: var(--wc-surface-muted);
  border: 1px solid var(--wc-line);
}

.trend-range-chip {
  min-width: 72rpx;
  height: 52rpx;
  line-height: 52rpx;
  text-align: center;
  border-radius: 999rpx;
  font-size: 22rpx;
  color: var(--wc-text-soft);
}

.trend-range-chip.active {
  background: white;
  color: var(--wc-primary-strong);
  font-weight: 700;
  box-shadow: 0 8rpx 18rpx rgba(30, 41, 59, 0.08);
}

.trend-card-copy {
  display: flex;
  flex-direction: column;
  gap: 8rpx;
}

.trend-card-copy__title {
  display: block;
  font-size: 28rpx;
  font-weight: 700;
  color: var(--wc-text);
}

.trend-card-copy__desc {
  display: block;
  font-size: 22rpx;
  line-height: 1.6;
  color: var(--wc-text-soft);
}

.chart-section--priority {
  background:
    radial-gradient(circle at top right, rgba(47, 179, 123, 0.12), transparent 30%),
    linear-gradient(180deg, rgba(255, 255, 255, 0.99), rgba(247, 250, 255, 0.98));
}

.chart-section--support {
  background: linear-gradient(180deg, rgba(250, 252, 255, 0.98), rgba(255, 255, 255, 0.98));
  box-shadow: 0 12rpx 28rpx rgba(15, 23, 42, 0.06);
}

.chart-section--composition {
  background:
    radial-gradient(circle at top right, rgba(43, 167, 121, 0.12), transparent 30%),
    linear-gradient(180deg, rgba(255, 255, 255, 0.99), rgba(246, 252, 249, 0.98));
}

.chart-section--history {
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.99), rgba(249, 250, 251, 0.98));
}

.composition-reminder {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 18rpx;
  padding: 24rpx;
  border-radius: 24rpx;
  border: 1px solid rgba(210, 218, 230, 0.72);
  margin-bottom: 20rpx;
}

.composition-reminder--idle,
.composition-reminder--steady {
  background: rgba(255, 255, 255, 0.92);
}

.composition-reminder--down {
  background: rgba(43, 167, 121, 0.12);
}

.composition-reminder--up {
  background: rgba(242, 155, 56, 0.14);
}

.composition-reminder--missing {
  background: rgba(15, 23, 42, 0.05);
}

.composition-reminder__copy {
  display: flex;
  flex-direction: column;
  gap: 8rpx;
  flex: 1;
}

.composition-reminder__title {
  font-size: 28rpx;
  font-weight: 700;
  color: var(--wc-text);
}

.composition-reminder__desc {
  font-size: 22rpx;
  line-height: 1.7;
  color: var(--wc-text-soft);
}

.composition-reminder__tag {
  flex-shrink: 0;
  min-width: 88rpx;
  padding: 10rpx 16rpx;
  border-radius: 999rpx;
  background: rgba(255, 255, 255, 0.9);
  border: 1px solid rgba(210, 218, 230, 0.72);
  text-align: center;
  font-size: 20rpx;
  font-weight: 700;
  color: var(--wc-text);
}

.body-signal-strip {
  display: flex;
  flex-wrap: wrap;
  gap: 12rpx;
  margin-bottom: 20rpx;
}

.body-signal-chip {
  min-width: 0;
  flex: 1 1 180rpx;
  padding: 18rpx 20rpx;
  border-radius: 20rpx;
  background: rgba(255, 255, 255, 0.92);
  border: 1px solid rgba(210, 218, 230, 0.72);
  display: flex;
  flex-direction: column;
  gap: 8rpx;
}

.body-signal-chip--positive {
  background: rgba(43, 167, 121, 0.12);
}

.body-signal-chip--warning {
  background: rgba(242, 155, 56, 0.14);
}

.body-signal-chip__label {
  font-size: 18rpx;
  color: var(--wc-text-faint);
}

.body-signal-chip__value {
  font-size: 26rpx;
  font-weight: 700;
  color: var(--wc-text);
}

.composition-insights {
  display: flex;
  flex-direction: column;
  gap: 14rpx;
}

.composition-insight {
  padding: 22rpx 24rpx;
  border-radius: 22rpx;
  background: rgba(255, 255, 255, 0.9);
  border: 1px solid rgba(210, 218, 230, 0.7);
  display: flex;
  flex-direction: column;
  gap: 8rpx;
}

.composition-insight--down {
  background: rgba(43, 167, 121, 0.12);
}

.composition-insight--up {
  background: rgba(242, 155, 56, 0.14);
}

.composition-insight--missing,
.composition-insight--idle {
  background: rgba(15, 23, 42, 0.05);
}

.composition-insight--steady,
.composition-insight--neutral {
  background: rgba(255, 255, 255, 0.92);
}

.composition-insight__top {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16rpx;
}

.composition-insight__title {
  flex: 1;
  font-size: 24rpx;
  font-weight: 700;
  color: var(--wc-text);
}

.composition-insight__tag {
  flex-shrink: 0;
  min-width: 88rpx;
  padding: 8rpx 14rpx;
  border-radius: 999rpx;
  background: rgba(255, 255, 255, 0.92);
  border: 1px solid rgba(210, 218, 230, 0.72);
  text-align: center;
  font-size: 18rpx;
  font-weight: 700;
  color: var(--wc-text);
}

.composition-insight__tag--good {
  color: #15803d;
  background: rgba(220, 252, 231, 0.86);
  border-color: rgba(34, 197, 94, 0.26);
}

.composition-insight__tag--warn {
  color: #a76716;
  background: rgba(255, 247, 237, 0.92);
  border-color: rgba(245, 158, 11, 0.28);
}

.composition-insight__tag--missing,
.composition-insight__tag--future {
  color: var(--wc-text-soft);
  background: rgba(248, 250, 252, 0.96);
  border-color: rgba(148, 163, 184, 0.28);
}

.composition-insight__desc {
  font-size: 22rpx;
  line-height: 1.7;
  color: var(--wc-text-soft);
}

.trend-line-segment {
  position: absolute;
  height: 4rpx;
  transform-origin: left center;
  background: linear-gradient(90deg, rgba(47, 179, 123, 0.48), rgba(47, 179, 123, 0.92));
  border-radius: 999rpx;
}

.trend-line-segment--bodyfat {
  background: linear-gradient(90deg, rgba(43, 167, 121, 0.45), rgba(43, 167, 121, 0.92));
}

.trend-point-group {
  position: static;
}

.trend-point {
  position: absolute;
  width: 18rpx;
  height: 18rpx;
  margin-left: -9rpx;
  margin-top: -9rpx;
  border-radius: 50%;
  background: white;
  border: 4rpx solid rgba(47, 179, 123, 0.62);
  box-sizing: border-box;
}

.trend-point--bodyfat {
  border-color: rgba(43, 167, 121, 0.62);
}

.trend-point.active {
  width: 22rpx;
  height: 22rpx;
  margin-left: -11rpx;
  margin-top: -11rpx;
  border-color: $primary;
  box-shadow: 0 0 0 10rpx rgba(47, 179, 123, 0.12);
}

.trend-point--bodyfat.active {
  border-color: #2ba779;
  box-shadow: 0 0 0 10rpx rgba(43, 167, 121, 0.12);
}

.trend-point-value {
  position: absolute;
  transform: translateX(-50%);
  font-size: 18rpx;
  font-weight: 700;
  color: $text-main;
  white-space: nowrap;
}

.trend-point-label {
  position: absolute;
  top: 156rpx;
  transform: translateX(-50%);
  font-size: 16rpx;
  color: $text-sub;
  white-space: nowrap;
}

.calorie-stats {
  display: flex;
  align-items: center;
  gap: 24rpx;
}

.calorie-bars {
  flex: 1;
}

.nutrient-bar {
  margin-bottom: 18rpx;
}

.nutrient-info {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10rpx;
  font-size: 24rpx;
}

.progress-track {
  height: 12rpx;
  background: var(--wc-surface-muted);
  border-radius: 999rpx;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
}

.progress-fill.intake {
  background: linear-gradient(90deg, #4a90e2, #7fb5f0);
}

.progress-fill.burn {
  background: linear-gradient(90deg, #50e3c2, #83efda);
}

.calorie-gap {
  width: 160rpx;
  text-align: center;
}

.gap-label,
.gap-unit {
  display: block;
  font-size: 20rpx;
  color: $text-sub;
}

.gap-value {
  display: block;
  font-size: 42rpx;
  font-weight: 700;
  color: $text-main;
}

.water-stats-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 18rpx;
}

.water-stat-card {
  border-radius: 24rpx;
  padding: 22rpx;
}

.water-stat-card.blue {
  background: var(--wc-primary-soft);
}

.water-stat-card.green {
  background: rgba(43, 167, 121, 0.12);
}

.water-stat-card.orange {
  background: rgba(242, 155, 56, 0.14);
}

.stat-label,
.stat-unit {
  display: block;
  font-size: 18rpx;
  color: $text-sub;
}

.stat-value {
  display: block;
  font-size: 34rpx;
  font-weight: 700;
  color: $text-main;
  margin: 8rpx 0;
}

.water-tip {
  margin-top: 20rpx;
  font-size: 22rpx;
  line-height: 1.7;
  color: $text-sub;
}

.habit-analysis-hero {
  display: flex;
  align-items: stretch;
  justify-content: space-between;
  gap: 20rpx;
  padding: 26rpx;
  border-radius: 28rpx;
  border: 1px solid rgba(210, 218, 230, 0.72);
  margin-bottom: 20rpx;
}

.habit-analysis-hero--idle {
  background: linear-gradient(180deg, rgba(248, 250, 252, 0.98), rgba(255, 255, 255, 0.98));
}

.habit-analysis-hero--progress {
  background: linear-gradient(180deg, rgba(255, 247, 237, 0.96), rgba(255, 255, 255, 0.98));
}

.habit-analysis-hero--done {
  background: linear-gradient(180deg, rgba(236, 253, 245, 0.96), rgba(255, 255, 255, 0.98));
}

.habit-analysis-hero__copy {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 10rpx;
}

.habit-analysis-hero__eyebrow {
  font-size: 20rpx;
  font-weight: 700;
  color: var(--wc-text-soft);
}

.habit-analysis-hero__title {
  font-size: 32rpx;
  font-weight: 700;
  color: var(--wc-text);
  line-height: 1.35;
}

.habit-analysis-hero__desc {
  font-size: 22rpx;
  line-height: 1.7;
  color: var(--wc-text-soft);
}

.habit-analysis-hero__rate {
  width: 148rpx;
  min-width: 148rpx;
  border-radius: 24rpx;
  background: rgba(255, 255, 255, 0.86);
  border: 1px solid rgba(210, 218, 230, 0.78);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8rpx;
  padding: 18rpx 12rpx;
}

.habit-analysis-hero__rate-value {
  font-size: 36rpx;
  font-weight: 700;
  color: var(--wc-text);
}

.habit-analysis-hero__rate-label {
  font-size: 18rpx;
  color: var(--wc-text-soft);
}

.habit-analytics-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 18rpx;
}

.habit-analytics-card {
  padding: 24rpx;
  border-radius: 26rpx;
  background: linear-gradient(180deg, rgba(248, 251, 255, 0.98), rgba(255, 255, 255, 0.98));
  border: 1px solid rgba(210, 218, 230, 0.75);
  display: flex;
  flex-direction: column;
  gap: 10rpx;
}

.habit-analytics-card__label {
  font-size: 22rpx;
  color: var(--wc-text-soft);
}

.habit-analytics-card__value {
  font-size: 34rpx;
  font-weight: 700;
  color: var(--wc-text);
}

.habit-analytics-card__meta {
  font-size: 20rpx;
  color: var(--wc-text-faint);
}

.habit-trend-card {
  margin-top: 20rpx;
  padding: 24rpx;
  border-radius: 28rpx;
  background: linear-gradient(180deg, rgba(248, 251, 255, 0.98), rgba(255, 255, 255, 0.98));
  border: 1px solid rgba(210, 218, 230, 0.75);
}

.habit-trend-card__header {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 18rpx;
  margin-bottom: 18rpx;
}

.habit-trend-card__title {
  font-size: 26rpx;
  font-weight: 700;
  color: var(--wc-text);
}

.habit-trend-card__meta {
  font-size: 20rpx;
  color: var(--wc-text-soft);
}

.habit-progress-bars {
  display: grid;
  grid-template-columns: repeat(7, minmax(0, 1fr));
  gap: 12rpx;
}

.habit-progress-bar {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10rpx;
  text-align: center;
}

.habit-progress-bar__label {
  font-size: 18rpx;
  color: var(--wc-text-soft);
}

.habit-progress-bar__track {
  width: 100%;
  height: 120rpx;
  padding: 8rpx;
  border-radius: 20rpx;
  background: linear-gradient(180deg, rgba(241, 245, 249, 0.9), rgba(255, 255, 255, 0.96));
  border: 1px solid rgba(226, 232, 240, 0.92);
  display: flex;
  align-items: flex-end;
  justify-content: center;
  box-sizing: border-box;
}

.habit-progress-bar__fill {
  width: 100%;
  border-radius: 14rpx;
  background: linear-gradient(180deg, rgba(47, 179, 123, 0.72), rgba(47, 179, 123, 0.96));
}

.habit-progress-bar.active .habit-progress-bar__track {
  border-color: rgba(47, 179, 123, 0.28);
  box-shadow: inset 0 0 0 2rpx rgba(47, 179, 123, 0.06);
}

.habit-progress-bar.done .habit-progress-bar__fill {
  background: linear-gradient(180deg, rgba(34, 197, 94, 0.76), rgba(34, 197, 94, 0.96));
}

.habit-progress-bar__ratio {
  font-size: 20rpx;
  font-weight: 600;
  color: var(--wc-text);
}

.habit-progress-bar__date {
  font-size: 18rpx;
  color: var(--wc-text-faint);
}

.habit-analysis-footnote {
  display: block;
  margin-top: 18rpx;
  font-size: 22rpx;
  line-height: 1.7;
  color: var(--wc-text-soft);
}

.lifestyle-track-stack {
  display: flex;
  flex-direction: column;
  gap: 18rpx;
}

.lifestyle-track-card {
  padding: 24rpx;
  border-radius: 28rpx;
  background: linear-gradient(180deg, rgba(248, 251, 255, 0.98), rgba(255, 255, 255, 0.98));
  border: 1px solid rgba(210, 218, 230, 0.75);
  transition: transform 0.16s ease, box-shadow 0.16s ease;
}

.lifestyle-track-card--diet {
  background: linear-gradient(180deg, rgba(245, 249, 255, 0.98), rgba(255, 255, 255, 0.98));
}

.lifestyle-track-card--exercise {
  background: linear-gradient(180deg, rgba(241, 252, 247, 0.98), rgba(255, 255, 255, 0.98));
}

.lifestyle-track-card--water {
  background: linear-gradient(180deg, rgba(240, 249, 255, 0.98), rgba(255, 255, 255, 0.98));
}

.lifestyle-track-card__header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 20rpx;
  margin-bottom: 18rpx;
}

.lifestyle-track-card__copy {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8rpx;
}

.lifestyle-track-card__title {
  font-size: 26rpx;
  font-weight: 700;
  color: var(--wc-text);
}

.lifestyle-track-card__desc {
  font-size: 20rpx;
  line-height: 1.6;
  color: var(--wc-text-soft);
}

.lifestyle-track-card__summary {
  min-width: 124rpx;
  padding: 14rpx 16rpx;
  border-radius: 20rpx;
  background: rgba(255, 255, 255, 0.9);
  border: 1px solid rgba(210, 218, 230, 0.72);
  display: flex;
  flex-direction: column;
  gap: 6rpx;
  text-align: center;
}

.lifestyle-track-card__summary-value {
  font-size: 30rpx;
  font-weight: 700;
  color: var(--wc-text);
}

.lifestyle-track-card__summary-label {
  font-size: 18rpx;
  color: var(--wc-text-soft);
}

.lifestyle-track-card__action {
  margin-top: 18rpx;
  padding-top: 18rpx;
  border-top: 1px solid rgba(210, 218, 230, 0.68);
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16rpx;
}

.lifestyle-track-card__action-text {
  font-size: 22rpx;
  font-weight: 600;
  color: var(--wc-primary);
}

.lifestyle-track-card__action-arrow {
  font-size: 24rpx;
  font-weight: 700;
  color: var(--wc-primary);
}

.lifestyle-progress-bars {
  margin-top: 0;
}

.lifestyle-track-card--exercise .habit-progress-bar.done .habit-progress-bar__fill {
  background: linear-gradient(180deg, rgba(43, 167, 121, 0.76), rgba(43, 167, 121, 0.96));
}

.lifestyle-track-card--water .habit-progress-bar.done .habit-progress-bar__fill {
  background: linear-gradient(180deg, rgba(56, 189, 248, 0.76), rgba(56, 189, 248, 0.96));
}

.record-list {
  display: flex;
  flex-direction: column;
  gap: 18rpx;
}

.recent-record-list {
  display: flex;
  flex-direction: column;
}

.recent-record-hub {
  display: flex;
  flex-direction: column;
  gap: 18rpx;
  padding: 24rpx;
  border-radius: 28rpx;
  background: linear-gradient(180deg, rgba(248, 251, 255, 0.98), rgba(255, 255, 255, 0.98));
  border: 1px solid rgba(210, 218, 230, 0.74);
}

.recent-record-hub__summary {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 18rpx;
}

.recent-record-hub__copy {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8rpx;
}

.recent-record-hub__eyebrow {
  font-size: 18rpx;
  font-weight: 700;
  letter-spacing: 0.08em;
  color: var(--wc-primary-strong);
}

.recent-record-hub__title {
  font-size: 28rpx;
  font-weight: 700;
  color: var(--wc-text);
}

.recent-record-hub__desc {
  font-size: 22rpx;
  line-height: 1.6;
  color: var(--wc-text-soft);
}

.recent-record-hub__meta {
  min-width: 108rpx;
  padding: 14rpx 16rpx;
  border-radius: 20rpx;
  background: rgba(255, 255, 255, 0.9);
  border: 1px solid rgba(210, 218, 230, 0.72);
  display: flex;
  flex-direction: column;
  gap: 6rpx;
  text-align: center;
}

.recent-record-hub__meta-value {
  font-size: 30rpx;
  font-weight: 700;
  color: var(--wc-text);
}

.recent-record-hub__meta-label {
  font-size: 18rpx;
  color: var(--wc-text-soft);
}

.recent-record-hub__chips {
  display: flex;
  flex-wrap: wrap;
  gap: 12rpx;
}

.recent-record-hub__chip {
  min-height: 48rpx;
  line-height: 48rpx;
  padding: 0 16rpx;
  border-radius: 999rpx;
  background: rgba(255, 255, 255, 0.92);
  border: 1px solid rgba(210, 218, 230, 0.72);
  font-size: 20rpx;
  font-weight: 600;
  color: var(--wc-text);
}

.recent-record-entry {
  display: flex;
  align-items: flex-start;
  gap: 18rpx;
  padding: 18rpx 0;
  border-top: 1px solid rgba(210, 218, 230, 0.68);
}

.recent-record-entry:first-of-type {
  border-top: none;
  padding-top: 0;
}

.recent-record__icon {
  width: 72rpx;
  height: 72rpx;
  border-radius: 20rpx;
  background: var(--wc-surface-muted);
  border: 1px solid var(--wc-line);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 34rpx;
  flex-shrink: 0;
}

.recent-record__icon.habit {
  background: rgba(236, 253, 245, 0.92);
  border-color: rgba(34, 197, 94, 0.24);
}

.recent-record__body {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 8rpx;
}

.recent-record-entry__meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16rpx;
}

.recent-record-entry__type {
  min-height: 40rpx;
  line-height: 40rpx;
  padding: 0 12rpx;
  border-radius: 999rpx;
  background: rgba(244, 247, 251, 0.96);
  border: 1px solid rgba(210, 218, 230, 0.7);
  font-size: 18rpx;
  font-weight: 600;
  color: var(--wc-text-soft);
}

.recent-record__title-row {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16rpx;
}

.recent-record__title {
  flex: 1;
  min-width: 0;
  font-size: 28rpx;
  font-weight: 700;
  color: var(--wc-text);
}

.recent-record__time {
  flex-shrink: 0;
  font-size: 20rpx;
  color: var(--wc-text-faint);
}

.recent-record__note {
  font-size: 22rpx;
  line-height: 1.6;
  color: var(--wc-text-soft);
}

.recent-record-entry__arrow {
  flex-shrink: 0;
  font-size: 24rpx;
  font-weight: 700;
  color: var(--wc-primary);
}

.recent-record-empty {
  padding: 20rpx;
  border-radius: 22rpx;
  background: rgba(248, 250, 252, 0.98);
  border: 1px dashed rgba(148, 163, 184, 0.38);
  font-size: 22rpx;
  line-height: 1.6;
  color: var(--wc-text-soft);
}

.record-item {
  display: flex;
  align-items: center;
  padding: 22rpx 0;
  border-top: 1px solid var(--wc-line);
}

.record-item:first-child {
  border-top: none;
  padding-top: 0;
}

.record-icon {
  width: 72rpx;
  height: 72rpx;
  border-radius: 20rpx;
  background: var(--wc-surface-muted);
  border: 1px solid var(--wc-line);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 34rpx;
  margin-right: 18rpx;
}

.record-info {
  flex: 1;
}

.record-main {
  display: block;
  font-size: 28rpx;
  font-weight: 600;
}

.record-sub {
  display: block;
  margin-top: 6rpx;
  font-size: 22rpx;
  color: $text-sub;
}

.record-arrow {
  font-size: 34rpx;
  color: var(--wc-text-soft);
}

.record-list-footer {
  padding-top: 18rpx;
  text-align: center;
}

.record-list-footer__btn {
  margin: 0 auto;
  min-width: 220rpx;
  height: 64rpx;
  line-height: 64rpx;
  border-radius: 999rpx;
  border: 1px solid var(--wc-line);
  background: rgba(255, 255, 255, 0.92);
  color: var(--wc-text);
  font-size: 24rpx;
  font-weight: 600;
}

.record-list-footer__done {
  display: block;
  font-size: 22rpx;
  color: var(--wc-text-soft);
}

.empty-inline {
  padding: 28rpx 0 8rpx;
  font-size: 24rpx;
  line-height: 1.7;
  color: $text-sub;
  text-align: center;
}

.status-banner {
  margin-bottom: 24rpx;
  padding: 24rpx 26rpx;
  border-radius: 28rpx;
  border: 1px solid rgba(242, 155, 56, 0.24);
  background: linear-gradient(180deg, rgba(255, 248, 240, 0.96), rgba(255, 255, 255, 0.98));
  box-shadow: var(--wc-shadow-soft);
}

.status-banner__copy {
  display: flex;
  flex-direction: column;
  gap: 8rpx;
}

.status-banner__title {
  font-size: 26rpx;
  font-weight: 700;
  color: var(--wc-text);
}

.status-banner__desc {
  font-size: 22rpx;
  line-height: 1.6;
  color: var(--wc-text-soft);
}

.status-banner__actions {
  display: flex;
  flex-wrap: wrap;
  gap: 14rpx;
  margin-top: 18rpx;
}

.status-banner .flow-btn--secondary {
  border-color: rgba(242, 155, 56, 0.28);
  color: #9a5a16;
}

.detail-mask {
  position: fixed;
  inset: 0;
  background: rgba(15, 23, 42, 0.32);
  display: flex;
  align-items: flex-end;
  justify-content: center;
  padding: 30rpx;
  z-index: 1100;
}

.detail-modal {
  width: 100%;
  max-width: 690rpx;
  border-radius: 32rpx;
  background: var(--wc-surface-strong);
  border: 1px solid var(--wc-line);
  box-shadow: 0 20rpx 60rpx rgba(15, 23, 42, 0.18);
  padding: 30rpx;
}

.detail-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 20rpx;
}

.detail-title {
  display: block;
  font-size: 32rpx;
  font-weight: 700;
  color: var(--wc-text);
}

.detail-subtitle {
  display: block;
  margin-top: 8rpx;
  font-size: 22rpx;
  color: var(--wc-text-soft);
}

.detail-body {
  margin-top: 24rpx;
  max-height: 66vh;
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.detail-row {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 20rpx;
  padding: 18rpx 0;
  border-top: 1px solid var(--wc-line);
}

.detail-row:first-child {
  border-top: none;
  padding-top: 0;
}

.detail-row__label {
  font-size: 24rpx;
  color: var(--wc-text-soft);
}

.detail-row__value {
  flex: 1;
  text-align: right;
  font-size: 24rpx;
  line-height: 1.6;
  color: var(--wc-text);
}

.detail-images {
  margin-top: 6rpx;
  padding-top: 22rpx;
  border-top: 1px solid var(--wc-line);
}

.detail-images__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20rpx;
  margin-bottom: 16rpx;
}

.detail-images__count {
  font-size: 22rpx;
  font-weight: 700;
  color: var(--wc-primary);
}

.detail-image-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12rpx;
}

.detail-image-grid.grid-1 {
  grid-template-columns: minmax(0, 1fr);
}

.detail-image-grid.grid-2 {
  grid-template-columns: repeat(2, minmax(0, 1fr));
}

.detail-image {
  width: 100%;
  aspect-ratio: 1 / 1;
  border-radius: 18rpx;
  overflow: hidden;
  background: var(--wc-surface-muted);
}

.page-bottom-space {
  height: 120rpx;
}
</style>
