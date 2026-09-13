<template>
  <view class="record-history-container wc-page-enter">
    <view class="header wc-tab-header" :style="{ paddingTop: `${safeTop}px` }">
      <view class="header-body wc-tab-header__body" :style="{ minHeight: `${topBarHeight - safeTop}px`, paddingRight: `${menuButtonSafeRight}px` }">
        <view class="header-shell">
          <view class="wc-back-chip wc-pressable" @tap="goBack">
            <text class="wc-back-chip__icon">‹</text>
          </view>
          <view class="header-copy">
            <text class="page-title">{{ pageTitle }}</text>
            <text class="page-subtitle">{{ pageSubtitle }}</text>
          </view>
        </view>
      </view>
    </view>

    <scroll-view
      class="scroll-content wc-tab-scroll wc-tab-scroll--top-gap wc-section-enter"
      scroll-y
      :style="{ height: `calc(100vh - ${topBarHeight}px)` }"
    >
      <view class="toolbar-card">
        <template v-if="isLifestyleHistory">
          <view class="filter-summary-panel">
            <view class="filter-summary-panel__copy">
              <text class="filter-summary-panel__eyebrow">当前筛选摘要</text>
              <text class="filter-summary-panel__title">{{ filterSummaryTitle }}</text>
              <text class="filter-summary-panel__desc">{{ filterSummaryDescription }}</text>
            </view>
            <view class="filter-summary-panel__chips">
              <text class="filter-summary-panel__chip">{{ currentTypeLabel === '全部' ? '生活全部' : currentTypeLabel }}</text>
              <text class="filter-summary-panel__chip">{{ currentRangeLabel }}</text>
            </view>
          </view>

          <view class="toolbar-actions toolbar-actions--summary">
            <button class="flow-btn flow-btn--secondary flow-btn--compact toolbar-actions__btn" @tap="toggleFilterPanel">
              {{ showFilterControls ? '收起筛选' : '调整筛选' }}
            </button>
            <button class="flow-btn flow-btn--secondary flow-btn--compact" :loading="recordLoading || recordLoadingMore" @tap.stop="reloadRecordHistory">整理记录</button>
          </view>

          <view v-if="showFilterControls" class="toolbar-filter-panel">
            <scroll-view class="record-tab-scroll" scroll-x enable-flex>
              <view class="chart-filter wc-tab-rail wc-tab-rail--compact record-filter-rail">
                <text
                  v-for="tab in visibleRecordTabs"
                  :key="tab.key"
                  class="filter-btn wc-tab-item wc-tab-item--compact wc-pressable"
                  :class="{ active: recordType === tab.key }"
                  @tap="selectRecordType(tab.key)"
                >{{ tab.label }}</text>
              </view>
            </scroll-view>

            <view class="date-range-rail">
              <text
                v-for="option in dateRangeOptions"
                :key="option.key"
                class="date-range-chip wc-pressable"
                :class="{ active: dateRange === option.key }"
                @tap="selectDateRange(option.key)"
              >{{ option.label }}</text>
            </view>
          </view>
        </template>

        <template v-else>
          <view class="refresh-meta">
            <text class="refresh-meta__text">{{ refreshMetaText }}</text>
            <button class="flow-btn flow-btn--secondary flow-btn--compact" :loading="recordLoading || recordLoadingMore" @tap.stop="reloadRecordHistory">整理记录</button>
          </view>

          <scroll-view class="record-tab-scroll" scroll-x enable-flex>
            <view class="chart-filter wc-tab-rail wc-tab-rail--compact record-filter-rail">
              <text
                v-for="tab in visibleRecordTabs"
                :key="tab.key"
                class="filter-btn wc-tab-item wc-tab-item--compact wc-pressable"
                :class="{ active: recordType === tab.key }"
                @tap="selectRecordType(tab.key)"
              >{{ tab.label }}</text>
            </view>
          </scroll-view>

          <view class="date-range-rail">
            <text
              v-for="option in dateRangeOptions"
              :key="option.key"
              class="date-range-chip wc-pressable"
              :class="{ active: dateRange === option.key }"
              @tap="selectDateRange(option.key)"
            >{{ option.label }}</text>
          </view>
        </template>
      </view>

      <app-empty-state
        v-if="recordLoadFailed"
        icon="🗂️"
        title="历史记录加载失败"
        description="重新整理一次，看看历史记录是否回来。"
      >
        <button class="flow-btn flow-btn--secondary flow-btn--compact" @tap="reloadRecordHistory">{{ sharedRetryText }}</button>
      </app-empty-state>

      <view v-else-if="recordLoading" class="empty-inline">正在整理历史记录...</view>

      <view v-else-if="filteredRecords.length" class="history-card">
        <view v-for="record in filteredRecords" :key="record.id" class="record-item wc-pressable" @tap="openRecordDetail(record)">
          <view class="record-icon" :class="record.type">{{ record.icon }}</view>
          <view class="record-info">
            <text class="record-main">{{ record.title }}</text>
            <text class="record-sub">{{ record.subtitle }}</text>
          </view>
          <text class="record-arrow">›</text>
        </view>

        <view class="record-list-footer">
          <button
            v-if="currentRecordHasMore"
            class="flow-btn flow-btn--secondary flow-btn--compact"
            :loading="recordLoadingMore"
            @tap="loadMoreRecordHistory"
          >查看更多记录</button>
          <text v-else class="record-list-footer__done">已加载当前范围内的全部记录</text>
        </view>
      </view>

      <app-empty-state
        v-else
        icon="📭"
        :title="emptyStateTitle"
        :description="emptyStateDescription"
      >
        <button class="flow-btn flow-btn--primary flow-btn--compact" @tap="goRecordEntryPage">{{ emptyStateActionText }}</button>
      </app-empty-state>

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

        <view class="detail-body">
          <view v-for="item in selectedRecord.details" :key="item.label" class="detail-row">
            <text class="detail-row__label">{{ item.label }}</text>
            <text class="detail-row__value">{{ item.value }}</text>
          </view>

          <view v-if="selectedRecordImages.length" class="detail-images">
            <view class="detail-images__header">
              <text class="detail-row__label">记录图片</text>
              <text v-if="selectedRecord.feedId" class="detail-feed-link" @tap="openSelectedRecordFeed">查看动态</text>
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
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { onLoad, onShow } from '@dcloudio/uni-app'
import AppEmptyState from '@/components/AppEmptyState/index.vue'
import ResolvedImage from '@/components/ResolvedImage/index.vue'
import { getFeedBySource } from '@/api/feed'
import { getHabitCheckinList } from '@/api/habit'
import { getExerciseRecordList, getFoodRecordList, getWaterRecordList, getWeightRecordList } from '@/api/record'
import type { CircleFeedCard, ExerciseRecordPayload, FoodRecordPayload, HabitCheckinPayload, WaterRecordPayload, WeightRecordPayload } from '@/types/api'
import { useUserStore } from '@/stores/user'
import { formatApiDateTime, toApiTimestamp } from '@/utils/datetime'
import { getMenuButtonSafeRight, getSafeTop, getTopBarHeight, navigateBackOr } from '@/utils/mobile'
import { openLoginPage } from '@/utils/request'
import { useDefaultPageShare } from '@/utils/share'

useDefaultPageShare()

type DateRangeKey = '7d' | '30d' | '90d' | 'all'
type RecordTypeKey = 'all' | 'weight' | 'food' | 'exercise' | 'water' | 'habit'
type HistorySourceKey = 'default' | 'body' | 'lifestyle'
type RecordEntryTabKey = 'weight' | 'diet' | 'exercise' | 'water' | 'habit'
type HistoryRecordItem = {
  id: string
  type: 'weight' | 'food' | 'exercise' | 'water' | 'habit'
  icon: string
  title: string
  subtitle: string
  detailTitle: string
  detailSubtitle: string
  details: Array<{ label: string; value: string }>
  sourceType: string
  sourceId: number
  recordImages: string[]
  feedId: number | null
  feedImages: string[]
  sortTime: number
}

const safeTop = getSafeTop(20)
const topBarHeight = getTopBarHeight(56)
const menuButtonSafeRight = getMenuButtonSafeRight(16, 18)
const userStore = useUserStore()

const historySource = ref<HistorySourceKey>('default')
const recordType = ref<RecordTypeKey>('all')
const dateRange = ref<DateRangeKey>('all')
const filterPanelExpanded = ref(true)
const recordLoading = ref(false)
const recordLoadingMore = ref(false)
const recordLoadFailed = ref(false)
const lastRefreshAt = ref('')
const recordPageNum = ref(1)
const recordPageSize = 20
const recordHasMoreMap = ref({
  weight: true,
  food: true,
  exercise: true,
  water: true,
  habit: true
})

const weightRecords = ref<WeightRecordPayload[]>([])
const foodRecords = ref<FoodRecordPayload[]>([])
const exerciseRecords = ref<ExerciseRecordPayload[]>([])
const waterRecords = ref<WaterRecordPayload[]>([])
const habitCheckins = ref<HabitCheckinPayload[]>([])
const selectedRecord = ref<HistoryRecordItem | null>(null)
const recordFeedMap = ref<Record<string, CircleFeedCard>>({})
const selectedRecordImages = computed(() => {
  if (!selectedRecord.value) {
    return []
  }
  return selectedRecord.value.recordImages.length ? selectedRecord.value.recordImages : selectedRecord.value.feedImages
})

const dateRangeOptions: Array<{ key: DateRangeKey; label: string }> = [
  { key: '7d', label: '近 7 天' },
  { key: '30d', label: '近 30 天' },
  { key: '90d', label: '近 90 天' },
  { key: 'all', label: '全部时间' }
]

const sharedRetryText = '重新整理'

const recordTabs: Array<{ key: RecordTypeKey; label: string }> = [
  { key: 'all', label: '全部' },
  { key: 'weight', label: '体重' },
  { key: 'food', label: '饮食' },
  { key: 'exercise', label: '运动' },
  { key: 'water', label: '饮水' },
  { key: 'habit', label: '习惯' }
]

onLoad((query) => {
  const requestedSource = String(query?.source || 'default') as HistorySourceKey
  const requestedType = String(query?.type || 'all') as RecordTypeKey
  const requestedRange = String(query?.range || 'all') as DateRangeKey
  if (requestedSource === 'body' || requestedSource === 'lifestyle') {
    historySource.value = requestedSource
  }
  filterPanelExpanded.value = historySource.value !== 'lifestyle'
  if (isRecordTypeAllowed(requestedType, historySource.value)) {
    recordType.value = requestedType
  }
  if (dateRangeOptions.some((item) => item.key === requestedRange)) {
    dateRange.value = requestedRange
  }
})

onShow(() => {
  if (!userStore.isLoggedIn) {
    openLoginPage('safe')
    return
  }
  void loadRecordHistory(true)
})

const refreshMetaText = computed(() => {
  if (recordLoading.value || recordLoadingMore.value) {
    return historySource.value === 'lifestyle' ? '正在整理生活记录...' : '正在整理记录...'
  }
  return lastRefreshAt.value ? `最近更新 ${lastRefreshAt.value}` : '尚未更新'
})

const isLifestyleHistory = computed(() => historySource.value === 'lifestyle')
const showFilterControls = computed(() => !isLifestyleHistory.value || filterPanelExpanded.value)

const currentRecordHasMore = computed(() => {
  if (recordType.value === 'weight') return recordHasMoreMap.value.weight
  if (recordType.value === 'food') return recordHasMoreMap.value.food
  if (recordType.value === 'exercise') return recordHasMoreMap.value.exercise
  if (recordType.value === 'water') return recordHasMoreMap.value.water
  if (recordType.value === 'habit') return recordHasMoreMap.value.habit
  if (historySource.value === 'lifestyle') {
    return recordHasMoreMap.value.food || recordHasMoreMap.value.exercise || recordHasMoreMap.value.water || recordHasMoreMap.value.habit
  }
  return Object.values(recordHasMoreMap.value).some(Boolean)
})

const visibleRecordTabs = computed(() =>
  historySource.value === 'lifestyle'
    ? recordTabs.filter((item) => item.key !== 'weight')
    : recordTabs
)

const currentTypeLabel = computed(() => {
  const matched = recordTabs.find((item) => item.key === recordType.value)
  return matched?.label || '记录'
})

const currentRangeLabel = computed(() => {
  const matched = dateRangeOptions.find((item) => item.key === dateRange.value)
  return matched?.label || '当前范围'
})

const pageTitle = computed(() => {
  if (historySource.value === 'lifestyle') {
    return recordType.value === 'all' ? '生活记录历史' : `${currentTypeLabel.value}记录历史`
  }
  return '记录历史'
})

const pageSubtitle = computed(() => {
  if (historySource.value === 'lifestyle') {
    if (recordType.value === 'all') {
      return `${currentRangeLabel.value}的饮食、运动、饮水和习惯记录`
    }
    return `${currentRangeLabel.value}的${currentTypeLabel.value}记录，来自生活执行视图`
  }
  return '按类型和时间范围回看全部历史记录'
})

const filterSummaryTitle = computed(() => {
  if (recordType.value === 'all') {
    return `${currentRangeLabel.value}生活记录`
  }
  return `${currentRangeLabel.value}${currentTypeLabel.value}记录`
})

const filterSummaryDescription = computed(() => {
  const freshness = lastRefreshAt.value ? `最近更新 ${lastRefreshAt.value}` : '还没有更新时间'
  if (recordType.value === 'all') {
    return `${freshness} · 这里默认承接生活执行里的近况回看。`
  }
  return `${freshness} · 当前聚焦 ${currentTypeLabel.value} 这条执行线。`
})

const allRecords = computed<HistoryRecordItem[]>(() => {
  const items: HistoryRecordItem[] = []

  weightRecords.value.forEach((record) => {
    if (record.weight === null || record.weight === undefined) {
      return
    }
    items.push({
      id: `weight-${record.id}`,
      type: 'weight',
      icon: '⚖️',
      title: `${record.weight} kg`,
      subtitle: formatRecordMeta(record.recordedAt, record.bodyFatRate ? `体脂 ${record.bodyFatRate}%` : '体重记录'),
      detailTitle: '体重记录',
      detailSubtitle: formatRecordTime(record.recordedAt) || '时间待补',
      details: [
        { label: '体重', value: `${record.weight} kg` },
        { label: '体脂率', value: record.bodyFatRate ? `${record.bodyFatRate}%` : '未填写' },
        { label: 'BMI', value: record.bmi ? `${record.bmi}` : '未生成' },
        { label: '备注', value: record.remark || '无' }
      ],
      recordImages: parseRecordImages(record.images),
      ...buildRecordFeedMeta('weight_record', record.id),
      sortTime: toRecordTimestamp(record.recordedAt)
    })
  })

  foodRecords.value.forEach((record) => {
    items.push({
      id: `food-${record.id}`,
      type: 'food',
      icon: '🥗',
      title: `${mealTypeLabel(record.mealType)} · ${record.foodName || '未命名食物'}`,
      subtitle: formatRecordMeta(record.recordedAt, `${record.calories || 0} kcal`),
      detailTitle: '饮食记录',
      detailSubtitle: formatRecordTime(record.recordedAt) || '时间待补',
      details: [
        { label: '餐次', value: mealTypeLabel(record.mealType) },
        { label: '食物', value: record.foodName || '未填写' },
        { label: '热量', value: `${record.calories || 0} kcal` },
        { label: '备注', value: record.remark || '无' }
      ],
      recordImages: parseRecordImages(record.images, record.imageUrl ? [record.imageUrl] : []),
      ...buildRecordFeedMeta('food_record', record.id),
      sortTime: toRecordTimestamp(record.recordedAt)
    })
  })

  exerciseRecords.value.forEach((record) => {
    items.push({
      id: `exercise-${record.id}`,
      type: 'exercise',
      icon: '🏃',
      title: record.exerciseType || '运动记录',
      subtitle: formatRecordMeta(record.recordedAt, `${record.durationMinutes || 0} 分钟 · ${record.caloriesBurned || 0} kcal`),
      detailTitle: '运动记录',
      detailSubtitle: formatRecordTime(record.recordedAt) || '时间待补',
      details: [
        { label: '运动类型', value: record.exerciseType || '未填写' },
        { label: '时长', value: `${record.durationMinutes || 0} 分钟` },
        { label: '消耗热量', value: `${record.caloriesBurned || 0} kcal` },
        { label: '距离', value: record.distance ? `${record.distance} km` : '未填写' },
        { label: '备注', value: record.remark || '无' }
      ],
      recordImages: parseRecordImages(record.images),
      ...buildRecordFeedMeta('exercise_record', record.id),
      sortTime: toRecordTimestamp(record.recordedAt)
    })
  })

  waterRecords.value.forEach((record) => {
    items.push({
      id: `water-${record.id}`,
      type: 'water',
      icon: '💧',
      title: `${record.cups || 0} 杯饮水`,
      subtitle: formatRecordMeta(record.recordedAt, `${record.ml || 0} ml`),
      detailTitle: '饮水记录',
      detailSubtitle: formatRecordTime(record.recordedAt) || '时间待补',
      details: [
        { label: '杯数', value: `${record.cups || 0} 杯` },
        { label: '毫升', value: `${record.ml || 0} ml` }
      ],
      recordImages: parseRecordImages(record.images),
      ...buildRecordFeedMeta('water_record', record.id),
      sortTime: toRecordTimestamp(record.recordedAt)
    })
  })

  habitCheckins.value.forEach((record) => {
    const habitName = record.habitName || '习惯打卡'
    items.push({
      id: `habit-${record.id}`,
      type: 'habit',
      icon: record.habitIcon || '✅',
      title: `${habitName} · 已打卡`,
      subtitle: formatRecordMeta(record.checkedAt, '习惯打卡'),
      detailTitle: '习惯打卡',
      detailSubtitle: formatRecordTime(record.checkedAt) || '时间待补',
      details: [
        { label: '习惯', value: habitName },
        { label: '打卡时间', value: formatRecordTime(record.checkedAt) || '时间待补' },
        { label: '备注', value: record.note || '无' }
      ],
      recordImages: parseRecordImages(record.images),
      ...buildRecordFeedMeta('habit_checkin', record.id),
      sortTime: toRecordTimestamp(record.checkedAt)
    })
  })

  return items.sort((a, b) => b.sortTime - a.sortTime)
})

const filteredRecords = computed(() => {
  if (historySource.value === 'lifestyle' && recordType.value === 'all') {
    return allRecords.value.filter((item) => item.type !== 'weight')
  }
  if (recordType.value === 'all') {
    return allRecords.value
  }
  return allRecords.value.filter((item) => item.type === recordType.value)
})

const emptyStateTitle = computed(() => {
  if (historySource.value === 'lifestyle') {
    return recordType.value === 'all' ? '当前范围内暂无生活记录' : `当前范围内暂无${currentTypeLabel.value}记录`
  }
  return '当前范围内暂无记录'
})

const emptyStateDescription = computed(() => {
  if (historySource.value === 'lifestyle') {
    if (recordType.value === 'all') {
      return `${currentRangeLabel.value}内还没有饮食、运动、饮水或习惯记录，先记录一条生活数据吧。`
    }
    return `${currentRangeLabel.value}内还没有${currentTypeLabel.value}记录，先记录一条生活数据吧。`
  }
  return `${currentRangeLabel.value}内还没有${currentTypeLabel.value === '全部' ? '' : currentTypeLabel.value}记录，先记录一条吧。`
})

const emptyStateActionText = computed(() => {
  if (recordType.value === 'weight') {
    return '记录体重'
  }
  if (recordType.value === 'food') {
    return '记录饮食'
  }
  if (recordType.value === 'exercise') {
    return '记录运动'
  }
  if (recordType.value === 'water') {
    return '记录饮水'
  }
  if (recordType.value === 'habit') {
    return '记录习惯'
  }
  if (historySource.value === 'lifestyle') {
    return '记录生活数据'
  }
  return '记录身体数据'
})

async function loadRecordHistory(reset = false) {
  if (reset) {
    recordPageNum.value = 1
    recordHasMoreMap.value = {
      weight: true,
      food: true,
      exercise: true,
      water: true,
      habit: true
    }
  }

  if (reset) {
    recordLoading.value = true
  } else {
    recordLoadingMore.value = true
  }
  recordLoadFailed.value = false

  try {
    const nextPageNum = reset ? 1 : recordPageNum.value + 1
    const rangeParams = buildRecordRangeParams()
    const [weightPage, foodPage, exercisePage, waterPage, habitPage] = await Promise.all([
      getWeightRecordList({ pageNum: nextPageNum, pageSize: recordPageSize, ...rangeParams }),
      getFoodRecordList({ pageNum: nextPageNum, pageSize: recordPageSize, ...rangeParams }),
      getExerciseRecordList({ pageNum: nextPageNum, pageSize: recordPageSize, ...rangeParams }),
      getWaterRecordList({ pageNum: nextPageNum, pageSize: recordPageSize, ...rangeParams }),
      getHabitCheckinList({ pageNum: nextPageNum, pageSize: recordPageSize, ...rangeParams })
    ])

    if (reset) {
      weightRecords.value = weightPage.list || []
      foodRecords.value = foodPage.list || []
      exerciseRecords.value = exercisePage.list || []
      waterRecords.value = waterPage.list || []
      habitCheckins.value = habitPage.list || []
    } else {
      weightRecords.value = [...weightRecords.value, ...(weightPage.list || [])]
      foodRecords.value = [...foodRecords.value, ...(foodPage.list || [])]
      exerciseRecords.value = [...exerciseRecords.value, ...(exercisePage.list || [])]
      waterRecords.value = [...waterRecords.value, ...(waterPage.list || [])]
      habitCheckins.value = [...habitCheckins.value, ...(habitPage.list || [])]
    }

    await loadRecordFeeds()

    recordPageNum.value = nextPageNum
    recordHasMoreMap.value = {
      weight: hasNextPage(weightPage),
      food: hasNextPage(foodPage),
      exercise: hasNextPage(exercisePage),
      water: hasNextPage(waterPage),
      habit: hasNextPage(habitPage)
    }
    lastRefreshAt.value = formatRefreshTime(new Date())
  } catch (error) {
    console.error('加载历史记录失败:', error)
    if (reset) {
      weightRecords.value = []
      foodRecords.value = []
      exerciseRecords.value = []
      waterRecords.value = []
      habitCheckins.value = []
    }
    recordLoadFailed.value = true
  } finally {
    if (reset) {
      recordLoading.value = false
    } else {
      recordLoadingMore.value = false
    }
  }
}

function selectRecordType(type: RecordTypeKey) {
  if (!isRecordTypeAllowed(type, historySource.value) || recordType.value === type) {
    return
  }
  recordType.value = type
}

function selectDateRange(range: DateRangeKey) {
  if (dateRange.value === range) {
    return
  }
  dateRange.value = range
  void loadRecordHistory(true)
}

function loadMoreRecordHistory() {
  if (recordLoading.value || recordLoadingMore.value || !currentRecordHasMore.value) {
    return
  }
  void loadRecordHistory(false)
}

function reloadRecordHistory() {
  void loadRecordHistory(true)
}

function toggleFilterPanel() {
  filterPanelExpanded.value = !filterPanelExpanded.value
}

function openRecordDetail(record: HistoryRecordItem) {
  selectedRecord.value = record
}

function closeRecordDetail() {
  selectedRecord.value = null
}

function buildRecordFeedMeta(sourceType: string, sourceId?: number | null) {
  const normalizedSourceId = Number(sourceId || 0)
  const feed = normalizedSourceId ? recordFeedMap.value[buildRecordFeedKey(sourceType, normalizedSourceId)] : null
  return {
    sourceType,
    sourceId: normalizedSourceId,
    feedId: feed?.id || null,
    feedImages: feed?.images || []
  }
}

function buildRecordFeedKey(sourceType: string, sourceId: number) {
  return `${sourceType}:${sourceId}`
}

async function loadRecordFeeds() {
  const sourceItems = [
    ...weightRecords.value.map((record) => ({ sourceType: 'weight_record', sourceId: record.id })),
    ...foodRecords.value.map((record) => ({ sourceType: 'food_record', sourceId: record.id })),
    ...exerciseRecords.value.map((record) => ({ sourceType: 'exercise_record', sourceId: record.id })),
    ...waterRecords.value.map((record) => ({ sourceType: 'water_record', sourceId: record.id })),
    ...habitCheckins.value.map((record) => ({ sourceType: 'habit_checkin', sourceId: record.id }))
  ].filter((item) => item.sourceId)

  if (!sourceItems.length) {
    recordFeedMap.value = {}
    return
  }

  const uniqueItems = sourceItems.filter((item, index, array) =>
    array.findIndex((current) => current.sourceType === item.sourceType && current.sourceId === item.sourceId) === index
  )
  const results = await Promise.allSettled(
    uniqueItems.map((item) => getFeedBySource(item.sourceType, item.sourceId))
  )
  const nextMap: Record<string, CircleFeedCard> = {}
  results.forEach((result, index) => {
    if (result.status !== 'fulfilled' || !result.value) {
      return
    }
    const source = uniqueItems[index]
    nextMap[buildRecordFeedKey(source.sourceType, source.sourceId)] = result.value
  })
  recordFeedMap.value = nextMap
}

function openSelectedRecordFeed() {
  if (!selectedRecord.value?.feedId) {
    return
  }
  uni.navigateTo({ url: `/pages/feed-detail/index?feedId=${selectedRecord.value.feedId}` })
}

function goBack() {
  navigateBackOr('/pages/analytics/index')
}

function goRecordEntryPage() {
  const entryTab = resolveRecordEntryTab()
  const queryParts: string[] = ['returnMode=history']
  if (entryTab) {
    queryParts.push(`tab=${encodeURIComponent(entryTab)}`)
  }
  queryParts.push(`returnSource=${encodeURIComponent(historySource.value)}`)
  queryParts.push(`returnType=${encodeURIComponent(recordType.value)}`)
  queryParts.push(`returnRange=${encodeURIComponent(dateRange.value)}`)
  uni.navigateTo({ url: `/pages/record/index?${queryParts.join('&')}` })
}

function buildRecordRangeParams() {
  if (dateRange.value === 'all') {
    return {}
  }

  const days = dateRange.value === '7d' ? 7 : dateRange.value === '30d' ? 30 : 90
  const now = new Date()
  const startDate = new Date()
  startDate.setHours(0, 0, 0, 0)
  startDate.setDate(startDate.getDate() - (days - 1))
  return {
    startDate: formatDateParam(startDate),
    endDate: formatDateParam(now)
  }
}

function formatDateParam(date: Date) {
  const year = date.getFullYear()
  const month = `${date.getMonth() + 1}`.padStart(2, '0')
  const day = `${date.getDate()}`.padStart(2, '0')
  return `${year}-${month}-${day}`
}

function formatRefreshTime(date: Date) {
  const hours = `${date.getHours()}`.padStart(2, '0')
  const minutes = `${date.getMinutes()}`.padStart(2, '0')
  return `${hours}:${minutes}`
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

function toRecordTimestamp(value?: string | null) {
  return toApiTimestamp(value)
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

function hasNextPage<T>(page: { pageNum: number; pageSize: number; total: number; list: T[] }) {
  return page.pageNum * page.pageSize < page.total
}

function isRecordTypeAllowed(type: RecordTypeKey, source: HistorySourceKey) {
  if (source === 'lifestyle' && type === 'weight') {
    return false
  }
  return recordTabs.some((item) => item.key === type)
}

function resolveRecordEntryTab(): RecordEntryTabKey | '' {
  if (recordType.value === 'weight') {
    return 'weight'
  }
  if (recordType.value === 'food') {
    return 'diet'
  }
  if (recordType.value === 'exercise') {
    return 'exercise'
  }
  if (recordType.value === 'water') {
    return 'water'
  }
  if (recordType.value === 'habit') {
    return 'habit'
  }
  if (historySource.value === 'lifestyle') {
    return 'diet'
  }
  return ''
}
</script>

<style scoped lang="scss">
$bg-page: var(--wc-bg);
$text-main: var(--wc-text);
$text-sub: var(--wc-text-soft);

@use '../../styles/flow-button.scss';

.record-history-container {
  height: 100vh;
  background: $bg-page;
  overflow: hidden;
}

.header {
  padding: 0 30rpx;
}

.header-shell {
  display: flex;
  align-items: center;
  gap: 14rpx;
}

.header-copy {
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 4rpx;
}

.page-title {
  font-size: 34rpx;
  font-weight: 700;
  color: var(--wc-text);
}

.page-subtitle {
  font-size: 20rpx;
  line-height: 1.5;
  color: var(--wc-text-soft);
}

.scroll-content {
  padding: 0 30rpx;
}

.toolbar-card,
.history-card {
  background: var(--wc-surface-strong);
  border-radius: 32rpx;
  padding: 28rpx;
  border: 1px solid var(--wc-line);
  box-shadow: var(--wc-shadow-soft);
  margin-bottom: 24rpx;
}

.toolbar-card {
  background: linear-gradient(180deg, rgba(248, 251, 255, 0.98), rgba(255, 255, 255, 0.98));
}

.filter-summary-panel {
  display: flex;
  flex-direction: column;
  gap: 18rpx;
}

.filter-summary-panel__copy {
  display: flex;
  flex-direction: column;
  gap: 8rpx;
}

.filter-summary-panel__eyebrow {
  font-size: 18rpx;
  font-weight: 700;
  letter-spacing: 0.08em;
  color: var(--wc-primary-strong);
}

.filter-summary-panel__title {
  font-size: 30rpx;
  font-weight: 700;
  color: var(--wc-text);
}

.filter-summary-panel__desc {
  font-size: 22rpx;
  line-height: 1.6;
  color: var(--wc-text-soft);
}

.filter-summary-panel__chips {
  display: flex;
  flex-wrap: wrap;
  gap: 12rpx;
}

.filter-summary-panel__chip {
  min-height: 52rpx;
  line-height: 52rpx;
  padding: 0 18rpx;
  border-radius: 999rpx;
  background: rgba(255, 255, 255, 0.92);
  border: 1px solid rgba(210, 218, 230, 0.72);
  font-size: 20rpx;
  font-weight: 700;
  color: var(--wc-text);
}

.toolbar-actions {
  display: flex;
  align-items: center;
  gap: 14rpx;
}

.toolbar-actions--summary {
  margin-top: 18rpx;
}

.toolbar-actions__btn {
  flex: 1;
}

.toolbar-filter-panel {
  margin-top: 18rpx;
  padding-top: 18rpx;
  border-top: 1px solid rgba(210, 218, 230, 0.7);
}

.refresh-meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 18rpx;
  margin-bottom: 20rpx;
}

.refresh-meta__text {
  font-size: 22rpx;
  color: var(--wc-text-soft);
}

.record-tab-scroll {
  width: 100%;
}

.record-filter-rail {
  display: inline-flex;
  white-space: nowrap;
  min-width: 100%;
}

.date-range-rail {
  display: flex;
  flex-wrap: wrap;
  gap: 12rpx;
  margin-top: 18rpx;
}

.date-range-chip {
  min-width: 120rpx;
  min-height: 56rpx;
  line-height: 56rpx;
  padding: 0 20rpx;
  text-align: center;
  border-radius: 999rpx;
  border: 1px solid var(--wc-line);
  background: rgba(255, 255, 255, 0.92);
  color: var(--wc-text-soft);
  font-size: 22rpx;
  font-weight: 600;
}

.date-range-chip.active {
  background: var(--wc-primary-soft);
  color: var(--wc-primary-strong);
  border-color: rgba(74, 144, 226, 0.22);
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
  flex-shrink: 0;
}

.record-info {
  flex: 1;
  min-width: 0;
}

.record-main {
  display: block;
  font-size: 28rpx;
  font-weight: 700;
  color: $text-main;
}

.record-sub {
  display: block;
  margin-top: 6rpx;
  font-size: 22rpx;
  line-height: 1.6;
  color: $text-sub;
}

.record-arrow {
  font-size: 34rpx;
  color: var(--wc-text-soft);
}

.record-list-footer {
  padding-top: 20rpx;
  text-align: center;
}

.record-list-footer__done {
  display: block;
  font-size: 22rpx;
  color: var(--wc-text-soft);
}

.empty-inline {
  padding: 40rpx 0;
  font-size: 24rpx;
  line-height: 1.7;
  color: $text-sub;
  text-align: center;
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

.detail-feed-link {
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
