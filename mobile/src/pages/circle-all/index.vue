<template>
  <view class="page-container wc-page-enter">
    <prototype-header title="全部圈子" back-url="/pages/circle/index" />

    <view class="search-box">
      <input
        v-model="keyword"
        class="search-input wc-form-input wc-form-input--pill"
        placeholder="🔍 搜索圈子名称..."
        placeholder-style="color:#9aa4b2;"
        confirm-type="search"
        @confirm="loadCircles"
      />
    </view>

    <view class="type-tabs wc-tab-rail">
      <view
        v-for="item in typeTabs"
        :key="item.value"
        class="type-tab wc-tab-item wc-tab-item--full wc-pressable"
        :class="{ active: selectedType === item.value }"
        @tap="selectType(item.value)"
      >
        {{ item.label }}
      </view>
    </view>

    <scroll-view class="category-tabs" scroll-x>
      <view class="category-tabs-inner wc-tab-rail wc-tab-rail--compact">
        <view
          class="category-tab wc-tab-item wc-tab-item--compact wc-pressable"
          :class="{ active: !selectedCategoryId }"
          @tap="selectCategory(null)"
        >
          全部
        </view>
        <view
          v-for="category in categories"
          :key="category.id"
          class="category-tab wc-tab-item wc-tab-item--compact wc-pressable"
          :class="{ active: selectedCategoryId === category.id }"
          @tap="selectCategory(category.id)"
        >
          {{ category.icon || '🏷️' }} {{ category.name }}
        </view>
      </view>
    </scroll-view>
    <view v-if="categoryLoadError" class="category-error">
      <view class="category-error__copy">
        <text class="category-error__title">分类加载失败</text>
        <text class="category-error__desc">{{ categoryLoadError }}</text>
      </view>
      <button class="flow-btn flow-btn--secondary flow-btn--compact" @tap="loadCategories">重新整理分类</button>
    </view>

    <scroll-view class="content wc-section-enter" scroll-y>
      <view v-if="loading" class="loading-block">圈子加载中...</view>
      <view v-else-if="circles.length" class="circle-list">
        <view
          v-for="(circle, index) in circles"
          :key="circle.id"
          class="circle-item wc-pressable"
          :class="circleToneClass(index)"
          @tap="goCircleDetail(circle.id)"
        >
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
              <text class="wc-badge wc-badge--dark">{{ circleTypeLabel(circle.type) }}</text>
              <text v-if="circle.joined" class="wc-badge wc-badge--glass">已加入</text>
              <text v-else-if="circle.readOnly" class="wc-badge wc-badge--glass">{{ circleLifecycleLabel(circle.lifecycleStatus) }}</text>
            </view>
            <view class="circle-cover-icon">{{ circle.icon || circleTypeIcon(circle.type) }}</view>
            <text class="circle-cover-caption">{{ circle.categoryName || '减重挑战' }}</text>
          </view>
          <view class="circle-info">
            <view class="circle-name">
              <text class="circle-name-text">{{ circle.name }}</text>
            </view>
            <text class="circle-desc">{{ circle.description || '一起坚持记录，互相监督完成目标。' }}</text>
            <text v-if="circle.readOnly" class="circle-readonly">{{ circle.readOnlyReason || '当前仅支持查看历史内容。' }}</text>
            <view class="circle-highlight-row">
              <view class="circle-highlight">
                <text class="circle-highlight-value">{{ circle.memberCount || 0 }}</text>
                <text class="circle-highlight-label">成员</text>
              </view>
              <view class="circle-highlight">
                <text class="circle-highlight-value">{{ circleDurationStatText(circle.durationDays) }}</text>
                <text class="circle-highlight-label">周期</text>
              </view>
              <view class="circle-highlight">
                <text class="circle-highlight-value">{{ circleDepositStatText(circle.depositRequired) }}</text>
                <text class="circle-highlight-label">门槛</text>
              </view>
            </view>
            <view class="circle-footer">
              <view class="circle-meta">
                <text class="wc-badge wc-badge--soft">{{ circle.categoryName || '未分类' }}</text>
                <text class="wc-badge wc-badge--soft">{{ formatCircleDuration(circle.durationDays) }}</text>
                <text class="wc-badge wc-badge--soft">{{ circleDepositText(circle.depositRequired) }}</text>
              </view>
              <button
                class="btn-join flow-btn flow-btn--compact wc-pressable"
                :class="circle.joined ? 'flow-btn--secondary' : 'flow-btn--primary'"
                @tap.stop="goCircleDetail(circle.id)"
              >
                {{ circle.joined ? '继续查看' : '查看详情' }}
              </button>
            </view>
          </view>
        </view>
      </view>
      <view v-else class="empty-wrap">
        <app-empty-state
          :icon="loadErrorMessage ? '⚠️' : '⭕'"
          :title="loadErrorMessage ? '圈子加载失败' : '没有找到匹配的圈子'"
          :description="loadErrorMessage || '试试切换分类或调整关键词。'"
        >
          <button
            v-if="loadErrorMessage"
            class="retry-btn flow-btn flow-btn--secondary flow-btn--compact wc-pressable"
            @tap="loadCircles"
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
import { ref } from 'vue'
import { onLoad, onShow } from '@dcloudio/uni-app'
import AppEmptyState from '@/components/AppEmptyState/index.vue'
import PrototypeHeader from '@/components/PrototypeHeader/index.vue'
import { getCircleCategories, getCircleList } from '@/api/circle'
import type { CircleCard, CircleCategoryOption } from '@/types/api'
import {
  circleCoverClass,
  circleLifecycleLabel,
  circleTypeIcon,
  circleTypeLabel,
  formatCircleDuration,
  isRemoteCircleCover,
  normalizeCircleCoverUrl
} from '@/utils/circle'
import { useDefaultPageShare } from '@/utils/share'

useDefaultPageShare()

const categories = ref<CircleCategoryOption[]>([])
const circles = ref<CircleCard[]>([])
const keyword = ref('')
const selectedCategoryId = ref<number | null>(null)
const selectedType = ref('')
const loading = ref(false)
const loadErrorMessage = ref('')
const categoryLoadError = ref('')

const typeTabs = [
  { label: '全部', value: '' },
  { label: '公开', value: '0' },
  { label: '私密', value: '1' }
]

onLoad((query) => {
  selectedType.value = typeof query?.type === 'string' ? query.type : ''
  if (typeof query?.keyword === 'string') {
    keyword.value = query.keyword
  }
})

onShow(() => {
  void initializePage()
})

async function initializePage() {
  if (!categories.value.length) {
    await loadCategories()
  }
  await loadCircles()
}

async function loadCategories() {
  categoryLoadError.value = ''
  try {
    categories.value = await getCircleCategories()
  } catch (error) {
    console.error('加载圈子分类失败', error)
    categories.value = []
    categoryLoadError.value = error instanceof Error ? error.message || '圈子分类接口请求失败' : '圈子分类接口请求失败'
  }
}

async function loadCircles() {
  loading.value = true
  loadErrorMessage.value = ''
  try {
    const page = await getCircleList({
      pageNum: 1,
      pageSize: 100,
      keyword: keyword.value || undefined,
      categoryId: selectedCategoryId.value || undefined,
      type: selectedType.value || undefined
    })
    circles.value = page.list || []
  } catch (error) {
    console.error('加载全部圈子失败', error)
    circles.value = []
    loadErrorMessage.value = error instanceof Error ? error.message || '圈子接口请求失败' : '圈子接口请求失败'
  } finally {
    loading.value = false
  }
}

function selectCategory(categoryId: number | null) {
  selectedCategoryId.value = categoryId
  void loadCircles()
}

function selectType(type: string) {
  selectedType.value = type
  void loadCircles()
}

function goCircleDetail(circleId: number) {
  uni.navigateTo({ url: `/pages/circle-private/index?circleId=${circleId}` })
}

function circleToneClass(index: number) {
  const tones = ['tone-ocean', 'tone-mint', 'tone-sun', 'tone-dusk']
  return tones[index % tones.length]
}

function circleDurationStatText(durationDays?: number | null) {
  if (!durationDays || durationDays <= 0) {
    return '长期'
  }
  if (durationDays >= 100) {
    return `${Math.round(durationDays / 30)} 月`
  }
  return `${durationDays} 天`
}

function circleDepositStatText(depositRequired?: number | null) {
  return '免押'
}

function circleDepositText(depositRequired?: number | null) {
  return '押金暂未开放'
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

.search-box {
  background: white;
  padding: 24rpx 30rpx 18rpx;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.04);
}

.search-input {
  width: 100%;
}

.type-tabs {
  margin: 0 30rpx 18rpx;
}

.category-tabs {
  padding: 0 30rpx 18rpx;
  white-space: nowrap;
}

.category-tabs-inner {
  display: inline-flex;
  min-width: 100%;
}

.content {
  flex: 1;
  min-height: 0;
}

.category-error {
  margin: 0 30rpx 18rpx;
  padding: 18rpx 22rpx;
  border-radius: 24rpx;
  background: rgba(255, 244, 224, 0.9);
  border: 1px solid rgba(230, 149, 75, 0.18);
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20rpx;
}

.category-error__copy {
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 4rpx;
}

.category-error__title {
  font-size: 24rpx;
  font-weight: 600;
  color: #8f4d18;
}

.category-error__desc {
  font-size: 21rpx;
  color: #ad6a33;
}

.empty-wrap {
  padding: 24rpx 30rpx 0;
}

.retry-btn {
  min-width: 220rpx;
}

.circle-list {
  padding: 24rpx 30rpx;
}

.circle-item {
  display: flex;
  align-items: stretch;
  padding: 28rpx;
  border-radius: 36rpx;
  background: white;
  box-shadow: 0 18rpx 36rpx rgba(24, 39, 75, 0.06);
  border: 1px solid rgba(74, 144, 226, 0.08);
  margin-bottom: 22rpx;
  gap: 24rpx;
  position: relative;
  overflow: hidden;
}

.circle-cover {
  width: 184rpx;
  min-height: 212rpx;
  border-radius: 30rpx;
  padding: 18rpx;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  flex-shrink: 0;
  color: white;
  position: relative;
  overflow: hidden;
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

.circle-cover-icon {
  position: relative;
  z-index: 2;
  width: 76rpx;
  height: 76rpx;
  border-radius: 24rpx;
  background: rgba(255, 255, 255, 0.18);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 40rpx;
  box-shadow: 0 14rpx 24rpx rgba(15, 23, 42, 0.12);
}

.circle-cover-caption {
  position: relative;
  z-index: 2;
  font-size: 22rpx;
  line-height: 1.5;
  color: rgba(255, 255, 255, 0.86);
}

.circle-info {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
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
  gap: 10rpx;
  flex-wrap: wrap;
}

.circle-footer {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 18rpx;
  margin-top: auto;
  padding-top: 18rpx;
}

.btn-join {
  min-width: 132rpx;
  flex-shrink: 0;
}

.loading-block {
  text-align: center;
  padding: 120rpx 40rpx;
  color: #888888;
}

.page-bottom-space {
  height: 60rpx;
}
</style>
