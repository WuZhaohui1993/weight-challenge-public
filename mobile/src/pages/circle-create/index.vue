<template>
  <view class="page-container wc-page-enter">
    <prototype-header :title="pageTitle" :back-url="backUrl" />

    <scroll-view class="content wc-section-enter" scroll-y>
      <view class="intro-card">
        <text class="intro-title">{{ editingCircleId ? '调整圈子的展示和规则' : '发起一个新的打卡小队' }}</text>
        <text class="intro-desc">
          {{ editingCircleId
            ? '管理员可以修改名称、封面、挑战周期和基础说明，让圈子状态保持一致。'
            : '创建后你会自动加入圈子，公开圈子会出现在发现页，私密圈子更适合固定小组互相监督。' }}
        </text>
      </view>

      <view v-if="pageLoading" class="loading-block">圈子信息加载中...</view>

      <view v-else-if="pageLoadError" class="empty-wrap">
        <app-empty-state
          icon="⚠️"
          title="圈子信息加载失败"
          :description="pageLoadError"
        >
          <button class="retry-btn flow-btn flow-btn--secondary flow-btn--compact wc-pressable" @tap="initializePage">重新整理</button>
        </app-empty-state>
      </view>

      <view v-else class="form-card">
        <view class="form-item">
          <text class="wc-form-label">圈子名称</text>
          <input
            v-model="form.name"
            class="wc-form-input"
            maxlength="32"
            placeholder="例如：四月减脂冲刺小队"
            placeholder-style="color:#9aa4b2;"
          />
        </view>

        <view class="form-item">
          <text class="wc-form-label">圈子类型</text>
          <view class="segment-row wc-form-segment-rail">
            <view
              v-for="item in typeOptions"
              :key="item.value"
              class="segment wc-form-segment"
              :class="{ active: form.type === item.value }"
              @tap="form.type = item.value"
            >
              {{ item.label }}
            </view>
          </view>
          <text class="field-tip">{{ typeDescription }}</text>
        </view>

        <view class="form-item">
          <text class="wc-form-label">圈子分类</text>
          <view v-if="categoryLoading && !categories.length" class="inline-loading">圈子分类加载中...</view>
          <view v-else-if="categories.length" class="category-grid">
            <view
              v-for="category in categories"
              :key="category.id"
              class="category-chip wc-pressable"
              :class="{ active: form.categoryId === category.id }"
              @tap="form.categoryId = category.id"
            >
              <text>{{ category.icon || '🏷️' }}</text>
              <text>{{ category.name }}</text>
            </view>
          </view>
          <app-empty-state
            v-else
            icon="⚠️"
            title="圈子分类加载失败"
            :description="categoryLoadError || '暂时拿不到圈子分类，请刷新后再试。'"
          >
            <button class="retry-btn flow-btn flow-btn--secondary flow-btn--compact wc-pressable" @tap="loadCategories">重新整理分类</button>
          </app-empty-state>
        </view>

        <view class="form-item">
          <view class="cover-label-row">
            <text class="wc-form-label">圈子封面</text>
            <text class="cover-label-status">{{ selectedCoverLabel }}</text>
          </view>
          <view class="cover-preview" :class="[circleCoverClass(form.coverUrl), { 'cover-preview--image': selectedCoverIsImage }]">
            <resolved-image
              v-if="selectedCoverIsImage"
              class="cover-preview-image"
              :src="normalizedSelectedCoverUrl"
              mode="aspectFill"
            />
            <view v-if="selectedCoverIsImage" class="cover-preview-shade"></view>
            <view class="cover-preview-glow"></view>
            <text class="cover-preview-kicker">{{ form.type === '1' ? '私密圈子' : '公开圈子' }}</text>
            <text class="cover-preview-title">{{ form.name.trim() || '圈子封面预览' }}</text>
            <text class="cover-preview-desc">{{ selectedCoverLabel }}</text>
          </view>
          <view class="cover-action-row">
            <button
              class="cover-action-btn flow-btn flow-btn--secondary flow-btn--compact wc-pressable"
              :loading="coverUploading"
              :disabled="coverUploading"
              @tap="chooseCustomCover"
            >
              {{ selectedCoverIsImage ? '更换封面' : '上传图片' }}
            </button>
            <button
              v-if="selectedCoverIsImage"
              class="cover-action-btn flow-btn flow-btn--secondary flow-btn--compact wc-pressable"
              :disabled="coverUploading"
              @tap="resetCoverToPreset"
            >
              改用风格
            </button>
          </view>
          <text class="field-tip">可以使用预设风格，也可以从相册或相机上传一张封面图。</text>
          <view class="cover-grid">
            <view
              v-for="cover in CIRCLE_COVER_PRESETS"
              :key="cover.value"
              class="cover-chip wc-pressable"
              :class="[circleCoverClass(cover.value), { active: form.coverUrl === cover.value }]"
              @tap="form.coverUrl = cover.value"
            >
              <text class="cover-chip-emoji">{{ cover.emoji }}</text>
              <text class="cover-chip-label">{{ cover.label }}</text>
            </view>
          </view>
        </view>

        <view class="form-item">
          <text class="wc-form-label">圈子简介</text>
          <textarea
            v-model="form.description"
            class="wc-form-textarea"
            maxlength="500"
            placeholder="写下挑战目标、打卡规则和适合加入的人群"
            placeholder-style="color:#9aa4b2;"
          />
        </view>

        <view class="form-grid">
          <view class="form-item">
            <text class="wc-form-label">持续天数</text>
            <input
              v-model="form.durationDays"
              type="number"
              class="wc-form-input"
              placeholder="0 表示长期"
              placeholder-style="color:#9aa4b2;"
            />
          </view>
          <view class="form-item deposit-form-item">
            <text class="wc-form-label">押金机制</text>
            <view class="wc-form-input deposit-todo-field">
              <text class="deposit-todo-title">暂未开放</text>
              <text class="deposit-todo-desc">当前默认免押金，押金支付、退还和扣除会在后续版本接入。</text>
            </view>
          </view>
        </view>

        <view class="goal-section">
          <view class="goal-section-header">
            <view>
              <text class="wc-form-label">目标设置</text>
              <text class="field-tip">目标决定后续圈子任务如何生成，也会影响提醒和排行榜。</text>
            </view>
            <view class="goal-summary-chip">{{ selectedGoalTemplateIds.length + customGoals.length }} 项</view>
          </view>

          <view v-if="goalCatalogLoading && !goalCatalog" class="inline-loading">目标模板加载中...</view>
          <app-empty-state
            v-else-if="goalCatalogError && !goalCatalog"
            icon="⚠️"
            title="目标配置加载失败"
            :description="goalCatalogError"
          >
            <button class="retry-btn flow-btn flow-btn--secondary flow-btn--compact wc-pressable" @tap="loadGoalCatalog">重新整理目标</button>
          </app-empty-state>
          <template v-else>
            <view class="goal-block">
              <view class="goal-block-header">
                <view>
                  <text class="goal-block-title">标准目标</text>
                  <text class="goal-block-desc">从后台维护好的模板里直接选，适合快速开圈。</text>
                </view>
                <text class="goal-block-count">已选 {{ selectedGoalTemplateIds.length }}</text>
              </view>
              <view v-if="goalCatalog?.templates?.length" class="template-grid">
                <view
                  v-for="template in goalCatalog.templates"
                  :key="template.id"
                  class="template-card wc-pressable"
                  :class="{ active: selectedGoalTemplateIds.includes(template.id) }"
                  @tap="toggleGoalTemplate(template.id)"
                >
                  <view class="template-card-top">
                    <text class="template-title">{{ template.goalName || metricLabel(template.metricCode) }}</text>
                    <text class="template-check">{{ selectedGoalTemplateIds.includes(template.id) ? '已选' : '选择' }}</text>
                  </view>
                  <text class="template-desc">{{ template.description || template.periodLabel || '标准目标模板' }}</text>
                  <view class="template-meta">
                    <text class="wc-badge wc-badge--soft">{{ template.targetValue }} {{ template.targetUnit }}</text>
                    <text class="wc-badge wc-badge--soft">{{ template.verificationTypeLabel }}</text>
                  </view>
                </view>
              </view>
              <text v-else class="field-tip">当前没有标准模板，可以先添加自定义目标。</text>
            </view>

            <view class="goal-block">
              <view class="goal-block-header">
                <view>
                  <text class="goal-block-title">自定义目标</text>
                  <text class="goal-block-desc">只支持结构化指标，保证任务和排行都能自动计算。</text>
                </view>
                <button class="flow-btn flow-btn--secondary flow-btn--compact wc-pressable" @tap="addCustomGoal">新增目标</button>
              </view>

              <view v-if="customGoals.length" class="custom-goal-list">
                <view v-for="(goal, index) in customGoals" :key="goal.uid" class="custom-goal-card">
                  <view class="custom-goal-top">
                    <text class="custom-goal-title">自定义目标 {{ index + 1 }}</text>
                    <text class="custom-goal-remove wc-pressable" @tap="removeCustomGoal(goal.uid)">删除</text>
                  </view>

                  <view class="form-item">
                    <text class="mini-label">目标名称</text>
                    <input
                      v-model="goal.goalName"
                      class="wc-form-input"
                      maxlength="24"
                      placeholder="例如：每周运动 4 次"
                      placeholder-style="color:#9aa4b2;"
                    />
                  </view>

                  <view class="form-item">
                    <text class="mini-label">统计指标</text>
                    <picker class="picker-trigger" :range="metricOptionLabels" :value="metricIndex(goal.metricCode)" @change="handleGoalMetricChange(index, $event)">
                      <view class="picker-trigger__body">
                        <text>{{ metricLabel(goal.metricCode) }}</text>
                        <text>⌄</text>
                      </view>
                    </picker>
                    <text class="field-tip">{{ metricDescription(goal.metricCode) }}</text>
                  </view>

                  <view class="goal-inline-grid">
                    <view class="form-item">
                      <text class="mini-label">目标值</text>
                      <input
                        v-model="goal.targetValue"
                        type="digit"
                        class="wc-form-input"
                        placeholder="1"
                        placeholder-style="color:#9aa4b2;"
                      />
                    </view>
                    <view class="form-item">
                      <text class="mini-label">单位</text>
                      <input
                        v-model="goal.targetUnit"
                        class="wc-form-input"
                        maxlength="8"
                        placeholder="次 / kg / 分钟"
                        placeholder-style="color:#9aa4b2;"
                      />
                    </view>
                  </view>

                  <view class="goal-inline-grid">
                    <view class="form-item">
                      <text class="mini-label">周期</text>
                      <picker class="picker-trigger" :range="periodOptionLabels" :value="periodIndex(goal.period)" @change="handleGoalPeriodChange(index, $event)">
                        <view class="picker-trigger__body">
                          <text>{{ periodLabel(goal.period) }}</text>
                          <text>⌄</text>
                        </view>
                      </picker>
                    </view>
                    <view class="form-item">
                      <text class="mini-label">校验方式</text>
                      <picker class="picker-trigger" :range="verificationLabelsForGoal(goal)" :value="verificationIndex(goal)" @change="handleGoalVerificationChange(index, $event)">
                        <view class="picker-trigger__body">
                          <text>{{ verificationLabel(goal.verificationType) }}</text>
                          <text>⌄</text>
                        </view>
                      </picker>
                    </view>
                  </view>

                  <view class="goal-required-row wc-pressable" @tap="goal.isRequired = !goal.isRequired">
                    <view>
                      <text class="mini-label">是否必做</text>
                      <text class="field-tip">必做目标会优先展示在圈子任务区。</text>
                    </view>
                    <view class="required-switch" :class="{ active: goal.isRequired }">{{ goal.isRequired ? '必做' : '可选' }}</view>
                  </view>
                </view>
              </view>
              <text v-else class="field-tip">还没有自定义目标。你可以只选模板，也可以新增自己的结构化目标。</text>
            </view>
          </template>
        </view>

        <view class="helper-card">
          <text class="helper-title">{{ editingCircleId ? '保存后会发生什么' : '创建后会发生什么' }}</text>
          <text class="helper-desc">
            {{ editingCircleId
              ? '圈子详情、发现页和管理页会同步使用新的封面和规则。'
              : '你会成为管理员，并自动加入该圈子。之后可在圈子详情里发动态、邀请成员和维护打卡氛围。' }}
          </text>
        </view>
      </view>
    </scroll-view>

    <view class="footer-action wc-footer-action">
      <button class="flow-btn flow-btn--primary flow-btn--wide" :loading="submitting" :disabled="submitting || coverUploading || categoryLoading || pageLoading" @tap="submitCircle">
        {{ submitLabel }}
      </button>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, reactive, ref } from 'vue'
import { onLoad, onShow } from '@dcloudio/uni-app'
import AppEmptyState from '@/components/AppEmptyState/index.vue'
import PrototypeHeader from '@/components/PrototypeHeader/index.vue'
import ResolvedImage from '@/components/ResolvedImage/index.vue'
import { createCircle, getCircleCategories, getCircleDetail, getCircleGoalCatalog, updateCircle, uploadCircleCoverImage } from '@/api/circle'
import type {
  CircleCategoryOption,
  CircleCreateGoalPayload,
  CircleCreatePayload,
  CircleGoalCatalogPayload,
  CircleGoalItem
} from '@/types/api'
import { useUserStore } from '@/stores/user'
import {
  CIRCLE_COVER_PRESETS,
  circleCoverClass,
  isRemoteCircleCover,
  normalizeCircleCoverUrl
} from '@/utils/circle'
import { captureAchievementState, fetchAchievementMetrics, notifyAchievementUnlocks } from '@/utils/achievementReminder'
import { createOnShowSuspendGuard } from '@/utils/page'
import { useDefaultPageShare } from '@/utils/share'
import { chooseLocalImages, getUploadSizeLimitText, uploadSingleLocalImage } from '@/utils/upload'

useDefaultPageShare()

const userStore = useUserStore()
const onShowRefreshGuard = createOnShowSuspendGuard()

const categories = ref<CircleCategoryOption[]>([])
const categoryLoading = ref(false)
const categoryLoadError = ref('')
const pageLoading = ref(false)
const pageLoadError = ref('')
const submitting = ref(false)
const editingCircleId = ref<number | null>(null)
const goalCatalog = ref<CircleGoalCatalogPayload | null>(null)
const goalCatalogLoading = ref(false)
const goalCatalogError = ref('')
const coverUploading = ref(false)
const selectedGoalTemplateIds = ref<number[]>([])
const customGoals = ref<Array<CircleCreateGoalPayload & { uid: string }>>([])
const customGoalSeed = ref(0)

const form = reactive<{
  name: string
  type: string
  categoryId: number | null
  coverUrl: string | null
  description: string
  durationDays: string
  depositRequired: string
}>({
  name: '',
  type: '0',
  categoryId: null as number | null,
  coverUrl: CIRCLE_COVER_PRESETS[0].value,
  description: '',
  durationDays: '',
  depositRequired: ''
})

const DEPOSIT_FEATURE_ENABLED = false

const typeOptions = [
  { label: '公开圈子', value: '0' },
  { label: '私密圈子', value: '1' }
]

const pageTitle = computed(() => (editingCircleId.value ? '编辑圈子' : '创建圈子'))
const backUrl = computed(() =>
  editingCircleId.value ? `/pages/circle-private/index?circleId=${editingCircleId.value}` : '/pages/circle-manage/index'
)
const submitLabel = computed(() => (editingCircleId.value ? '保存圈子修改' : '创建并加入圈子'))
const selectedCoverLabel = computed(
  () => selectedCoverIsImage.value ? '自定义封面' : CIRCLE_COVER_PRESETS.find((item) => item.value === form.coverUrl)?.label || '封面风格'
)
const selectedCoverIsImage = computed(() => isRemoteCircleCover(form.coverUrl))
const normalizedSelectedCoverUrl = computed(() => normalizeCircleCoverUrl(form.coverUrl))
const typeDescription = computed(() =>
  form.type === '1' ? '仅成员可见，适合固定小组、熟人监督。' : '会出现在发现页，更适合招募同路人一起坚持。'
)
const metricOptionLabels = computed(() => (goalCatalog.value?.metricOptions || []).map((item) => item.label))
const periodOptionLabels = computed(() => (goalCatalog.value?.periodOptions || []).map((item) => item.label))

onLoad((query) => {
  const parsedCircleId = Number(query?.circleId || 0)
  if (parsedCircleId > 0) {
    editingCircleId.value = parsedCircleId
  }
})

onShow(() => {
  if (onShowRefreshGuard.isSuspended()) {
    return
  }
  void initializePage()
})

async function initializePage() {
  pageLoadError.value = ''
  await Promise.all([loadCategories(), loadGoalCatalog()])
  if (editingCircleId.value) {
    await loadCircleForEdit()
  }
}

async function loadCategories() {
  if (categoryLoading.value) {
    return
  }
  categoryLoading.value = true
  categoryLoadError.value = ''
  try {
    categories.value = await getCircleCategories()
    if (!form.categoryId && categories.value.length) {
      form.categoryId = categories.value[0].id
    }
  } catch (error) {
    console.error('加载圈子分类失败', error)
    categories.value = []
    categoryLoadError.value = error instanceof Error ? error.message || '圈子分类接口请求失败' : '圈子分类接口请求失败'
  } finally {
    categoryLoading.value = false
  }
}

async function loadGoalCatalog() {
  if (goalCatalogLoading.value) {
    return
  }
  goalCatalogLoading.value = true
  goalCatalogError.value = ''
  try {
    goalCatalog.value = await getCircleGoalCatalog()
  } catch (error) {
    console.error('加载目标配置失败', error)
    goalCatalog.value = null
    goalCatalogError.value = error instanceof Error ? error.message || '目标配置接口请求失败' : '目标配置接口请求失败'
  } finally {
    goalCatalogLoading.value = false
  }
}

async function loadCircleForEdit() {
  if (!editingCircleId.value) {
    return
  }
  pageLoading.value = true
  pageLoadError.value = ''
  try {
    const detail = await getCircleDetail(editingCircleId.value)
    form.name = detail.circle.name || ''
    form.type = detail.circle.type || '0'
    form.categoryId = detail.circle.categoryId || form.categoryId
    form.coverUrl = detail.circle.coverUrl || CIRCLE_COVER_PRESETS[0].value
    form.description = detail.circle.description || ''
    form.durationDays = detail.circle.durationDays ? String(detail.circle.durationDays) : ''
    form.depositRequired = detail.circle.depositRequired ? String(detail.circle.depositRequired) : ''
    selectedGoalTemplateIds.value = []
    customGoals.value = (detail.goalList || []).map((goal) => mapGoalToCustomGoal(goal))
    if (!form.categoryId && categories.value.length) {
      form.categoryId = categories.value[0].id
    }
  } catch (error) {
    console.error('加载圈子详情失败', error)
    pageLoadError.value = error instanceof Error ? error.message || '圈子详情接口请求失败' : '圈子详情接口请求失败'
  } finally {
    pageLoading.value = false
  }
}

function normalizePayload(): CircleCreatePayload | null {
  const name = form.name.trim()
  if (name.length < 2 || name.length > 32) {
    uni.showToast({ title: '圈子名称需为 2-32 个字符', icon: 'none' })
    return null
  }

  if (!form.categoryId) {
    uni.showToast({ title: '请选择圈子分类', icon: 'none' })
    return null
  }

  const durationDays = form.durationDays.trim() ? Number(form.durationDays) : 0
  if (!Number.isFinite(durationDays) || durationDays < 0 || durationDays > 365) {
    uni.showToast({ title: '持续天数需在 0-365 之间', icon: 'none' })
    return null
  }

  const depositRequired = DEPOSIT_FEATURE_ENABLED && form.depositRequired.trim() ? Number(form.depositRequired) : 0
  if (DEPOSIT_FEATURE_ENABLED && (!Number.isFinite(depositRequired) || depositRequired < 0)) {
    uni.showToast({ title: '押金金额不能小于 0', icon: 'none' })
    return null
  }

  const normalizedCustomGoals: CircleCreateGoalPayload[] = []
  for (const goal of customGoals.value) {
    const metricCode = goal.metricCode.trim()
    const goalName = goal.goalName.trim() || metricLabel(metricCode)
    const targetValue = Number(goal.targetValue)
    const targetUnit = goal.targetUnit.trim()
    if (!metricCode) {
      uni.showToast({ title: '请选择目标指标', icon: 'none' })
      return null
    }
    if (!goalName) {
      uni.showToast({ title: '请填写目标名称', icon: 'none' })
      return null
    }
    if (!Number.isFinite(targetValue) || targetValue <= 0) {
      uni.showToast({ title: '目标值需大于 0', icon: 'none' })
      return null
    }
    if (!targetUnit) {
      uni.showToast({ title: '请填写目标单位', icon: 'none' })
      return null
    }
    normalizedCustomGoals.push({
      metricCode,
      goalName,
      targetValue,
      targetUnit,
      period: goal.period,
      verificationType: goal.verificationType,
      isRequired: Boolean(goal.isRequired)
    })
  }

  return {
    name,
    type: form.type,
    categoryId: form.categoryId,
    coverUrl: form.coverUrl,
    description: form.description.trim(),
    durationDays,
    depositRequired,
    selectedGoalTemplateIds: [...selectedGoalTemplateIds.value],
    customGoals: normalizedCustomGoals
  }
}

function toggleGoalTemplate(templateId: number) {
  if (selectedGoalTemplateIds.value.includes(templateId)) {
    selectedGoalTemplateIds.value = selectedGoalTemplateIds.value.filter((item) => item !== templateId)
    return
  }
  selectedGoalTemplateIds.value = [...selectedGoalTemplateIds.value, templateId]
}

function addCustomGoal() {
  customGoals.value = [...customGoals.value, createEmptyCustomGoal()]
}

function removeCustomGoal(uid: string) {
  customGoals.value = customGoals.value.filter((goal) => goal.uid !== uid)
}

function mapGoalToCustomGoal(goal: CircleGoalItem): CircleCreateGoalPayload & { uid: string } {
  const metricCode = goal.metricCode || defaultMetricCode()
  return {
    uid: nextCustomGoalUid(),
    metricCode,
    goalName: goal.goalName || metricLabel(metricCode),
    targetValue: Number(goal.targetValue || 1),
    targetUnit: goal.targetUnit || metricDefaultUnit(metricCode),
    period: goal.period || '0',
    verificationType: goal.verificationType || defaultVerificationType(metricCode),
    isRequired: Boolean(goal.required)
  }
}

function createEmptyCustomGoal(): CircleCreateGoalPayload & { uid: string } {
  const metricCode = defaultMetricCode()
  return {
    uid: nextCustomGoalUid(),
    metricCode,
    goalName: metricLabel(metricCode),
    targetValue: 1,
    targetUnit: metricDefaultUnit(metricCode),
    period: '0',
    verificationType: defaultVerificationType(metricCode),
    isRequired: true
  }
}

function nextCustomGoalUid() {
  customGoalSeed.value += 1
  return `goal-${customGoalSeed.value}`
}

function metricOptions() {
  return goalCatalog.value?.metricOptions || []
}

function defaultMetricCode() {
  return metricOptions()[0]?.value || 'weight_record_count'
}

function metricOption(metricCode?: string | null) {
  return metricOptions().find((item) => item.value === metricCode) || null
}

function metricLabel(metricCode?: string | null) {
  return metricOption(metricCode)?.label || '请选择指标'
}

function metricDescription(metricCode?: string | null) {
  return metricOption(metricCode)?.description || '选择一个支持自动统计或手动完成的目标指标。'
}

function metricDefaultUnit(metricCode?: string | null) {
  return metricOption(metricCode)?.defaultUnit || '次'
}

function defaultVerificationType(metricCode?: string | null) {
  return metricOption(metricCode)?.verificationTypes?.[0] || '0'
}

function metricIndex(metricCode?: string | null) {
  const index = metricOptions().findIndex((item) => item.value === metricCode)
  return index >= 0 ? index : 0
}

function periodLabel(value?: string | null) {
  return goalCatalog.value?.periodOptions.find((item) => item.value === value)?.label || '每日'
}

function periodIndex(value?: string | null) {
  const index = goalCatalog.value?.periodOptions.findIndex((item) => item.value === value) ?? 0
  return index >= 0 ? index : 0
}

function verificationLabel(value?: string | null) {
  return goalCatalog.value?.verificationOptions.find((item) => item.value === value)?.label || '自动统计'
}

function verificationOptionsForGoal(goal: CircleCreateGoalPayload & { uid: string }) {
  const allowed = metricOption(goal.metricCode)?.verificationTypes || []
  const allOptions = goalCatalog.value?.verificationOptions || []
  if (!allowed.length) {
    return allOptions
  }
  return allOptions.filter((item) => allowed.includes(item.value))
}

function verificationLabelsForGoal(goal: CircleCreateGoalPayload & { uid: string }) {
  return verificationOptionsForGoal(goal).map((item) => item.label)
}

function verificationIndex(goal: CircleCreateGoalPayload & { uid: string }) {
  const options = verificationOptionsForGoal(goal)
  const index = options.findIndex((item) => item.value === goal.verificationType)
  return index >= 0 ? index : 0
}

function handleGoalMetricChange(index: number, event: any) {
  const option = metricOptions()[Number(event?.detail?.value || 0)]
  const current = customGoals.value[index]
  if (!option || !current) {
    return
  }
  const previousLabel = metricLabel(current.metricCode)
  const nextGoals = [...customGoals.value]
  nextGoals[index] = {
    ...current,
    metricCode: option.value,
    targetUnit: option.defaultUnit,
    verificationType: option.verificationTypes?.[0] || '0',
    goalName: !current.goalName.trim() || current.goalName.trim() === previousLabel ? option.label : current.goalName
  }
  customGoals.value = nextGoals
}

function handleGoalPeriodChange(index: number, event: any) {
  const option = goalCatalog.value?.periodOptions[Number(event?.detail?.value || 0)]
  if (!option || !customGoals.value[index]) {
    return
  }
  const nextGoals = [...customGoals.value]
  nextGoals[index] = {
    ...nextGoals[index],
    period: option.value
  }
  customGoals.value = nextGoals
}

function handleGoalVerificationChange(index: number, event: any) {
  const current = customGoals.value[index]
  if (!current) {
    return
  }
  const options = verificationOptionsForGoal(current)
  const option = options[Number(event?.detail?.value || 0)]
  if (!option) {
    return
  }
  const nextGoals = [...customGoals.value]
  nextGoals[index] = {
    ...nextGoals[index],
    verificationType: option.value
  }
  customGoals.value = nextGoals
}

async function submitCircle() {
  if (coverUploading.value) {
    uni.showToast({ title: '封面上传中，请稍后', icon: 'none' })
    return
  }

  const payload = normalizePayload()
  if (!payload || submitting.value) {
    return
  }

  submitting.value = true
  try {
    if (editingCircleId.value) {
      const detail = await updateCircle(editingCircleId.value, payload)
      uni.showToast({ title: '圈子已更新', icon: 'success' })
      uni.redirectTo({ url: `/pages/circle-private/index?circleId=${detail.circle.id}` })
      return
    }

    const previousMetrics = await fetchAchievementMetrics()
    const previousAchievementKeys = captureAchievementState(userStore.userInfo, previousMetrics)
    const detail = await createCircle(payload)
    await userStore.fetchUserInfo()
    uni.showToast({ title: '圈子已创建', icon: 'success' })
    await notifyAchievementUnlocks(previousAchievementKeys, userStore.userInfo, await fetchAchievementMetrics())
    uni.redirectTo({ url: `/pages/circle-private/index?circleId=${detail.circle.id}` })
  } catch (error) {
    console.error(editingCircleId.value ? '更新圈子失败' : '创建圈子失败', error)
    uni.showToast({
      title: editingCircleId.value ? '保存失败' : '创建失败',
      icon: 'none'
    })
  } finally {
    submitting.value = false
  }
}

async function chooseCustomCover() {
  if (coverUploading.value) {
    return
  }

  await onShowRefreshGuard.runWhileSuspended(async () => {
    try {
      const chooseResult = await chooseLocalImages({ currentCount: 0, maxCount: 1 })
      if (chooseResult.cancelled || !chooseResult.filePaths.length) {
        return
      }

      coverUploading.value = true
      uni.showLoading({ title: '上传封面中...', mask: true })
      const uploadResult = await uploadSingleLocalImage({
        filePath: chooseResult.filePaths[0],
        upload: uploadCircleCoverImage,
        fallbackErrorMessage: '封面上传失败',
        oversizeMessage: `封面不能超过 ${getUploadSizeLimitText()}`
      })
      const uploadedCoverUrl = String(uploadResult.fileName || uploadResult.url || '').trim()
      if (!uploadedCoverUrl) {
        throw new Error('封面上传失败')
      }
      form.coverUrl = uploadedCoverUrl
      uni.showToast({ title: '封面已上传', icon: 'success' })
    } catch (error: any) {
      uni.showToast({ title: error?.message || '封面上传失败', icon: 'none' })
    } finally {
      coverUploading.value = false
      uni.hideLoading()
    }
  })
}

function resetCoverToPreset() {
  if (coverUploading.value) {
    return
  }
  form.coverUrl = CIRCLE_COVER_PRESETS[0].value
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
  padding-bottom: 180rpx;
}

.intro-card,
.form-card {
  margin: 24rpx 30rpx 0;
  padding: 32rpx;
  background: white;
  border-radius: 32rpx;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.04);
}

.intro-title {
  display: block;
  font-size: 30rpx;
  font-weight: 700;
  color: #333333;
  margin-bottom: 12rpx;
}

.intro-desc,
.field-tip,
.helper-desc {
  display: block;
  font-size: 24rpx;
  color: #7a8594;
  line-height: 1.7;
}

.goal-section {
  margin-top: 20rpx;
}

.goal-section-header,
.goal-block-header,
.custom-goal-top,
.goal-required-row,
.picker-trigger__body {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.goal-section-header {
  gap: 16rpx;
}

.goal-section-header > view:first-child {
  flex: 1;
  min-width: 0;
}

.goal-summary-chip {
  flex-shrink: 0;
  padding: 10rpx 18rpx;
  border-radius: 999rpx;
  background: #edf4ff;
  color: #2d6cdf;
  font-size: 22rpx;
  font-weight: 600;
  line-height: 1;
  white-space: nowrap;
}

.form-item {
  margin-bottom: 24rpx;
}

.goal-block {
  margin-top: 24rpx;
  padding: 24rpx;
  border-radius: 28rpx;
  background: #f7f9fc;
  border: 2rpx solid #edf1f7;
}

.goal-block-title,
.custom-goal-title,
.mini-label {
  display: block;
  color: #243244;
  font-weight: 700;
}

.goal-block-title {
  font-size: 26rpx;
}

.goal-block-desc {
  display: block;
  margin-top: 8rpx;
  font-size: 22rpx;
  color: #7a8594;
  line-height: 1.6;
}

.goal-block-count {
  color: #5b6574;
  font-size: 22rpx;
}

.template-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16rpx;
  margin-top: 18rpx;
}

.template-card,
.custom-goal-card {
  border-radius: 24rpx;
  background: #ffffff;
  border: 2rpx solid #e6ebf2;
}

.template-card {
  padding: 22rpx;
}

.template-card.active {
  border-color: #7cb3ff;
  box-shadow: 0 12rpx 24rpx rgba(45, 108, 223, 0.08);
}

.template-card-top,
.template-meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12rpx;
}

.template-title {
  flex: 1;
  color: #243244;
  font-size: 24rpx;
  font-weight: 700;
}

.template-check {
  color: #2d6cdf;
  font-size: 22rpx;
  font-weight: 600;
}

.template-desc {
  display: block;
  margin-top: 10rpx;
  font-size: 22rpx;
  color: #7a8594;
  line-height: 1.6;
  min-height: 70rpx;
}

.template-meta {
  margin-top: 14rpx;
  justify-content: flex-start;
  flex-wrap: wrap;
}

.custom-goal-list {
  display: flex;
  flex-direction: column;
  gap: 18rpx;
  margin-top: 18rpx;
}

.custom-goal-card {
  padding: 24rpx;
}

.custom-goal-remove {
  color: #cf3f3f;
  font-size: 22rpx;
  font-weight: 600;
}

.mini-label {
  margin-bottom: 10rpx;
  font-size: 22rpx;
}

.goal-inline-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 18rpx;
}

.picker-trigger {
  width: 100%;
}

.picker-trigger__body {
  min-height: 88rpx;
  padding: 0 24rpx;
  border-radius: 24rpx;
  background: #ffffff;
  border: 2rpx solid #e6ebf2;
  color: #243244;
  font-size: 24rpx;
}

.required-switch {
  min-width: 104rpx;
  padding: 12rpx 18rpx;
  border-radius: 999rpx;
  background: #eef2f7;
  color: #5b6574;
  text-align: center;
  font-size: 22rpx;
  font-weight: 600;
}

.required-switch.active {
  background: #edf4ff;
  color: #2d6cdf;
}

.segment-row {
  gap: 12rpx;
}

.segment {
  padding-top: 18rpx;
  padding-bottom: 18rpx;
}

.segment.active {
  font-weight: 600;
}

.category-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16rpx;
}

.category-chip {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10rpx;
  min-height: 88rpx;
  padding: 0 20rpx;
  border-radius: 24rpx;
  background: #f3f6fa;
  color: #5b6574;
  font-size: 24rpx;
  border: 2rpx solid transparent;
}

.category-chip.active {
  background: #edf4ff;
  border-color: #7cb3ff;
  color: #2d6cdf;
  font-weight: 600;
}

.cover-label-row,
.cover-action-row {
  display: flex;
  align-items: center;
}

.cover-label-row {
  justify-content: space-between;
  gap: 16rpx;
  margin-bottom: 12rpx;
}

.cover-label-row .wc-form-label {
  margin-bottom: 0;
}

.cover-label-status {
  flex-shrink: 0;
  padding: 8rpx 16rpx;
  border-radius: 999rpx;
  background: #edf4ff;
  color: #2d6cdf;
  font-size: 22rpx;
  font-weight: 600;
}

.cover-preview {
  position: relative;
  min-height: 188rpx;
  padding: 28rpx;
  border-radius: 28rpx;
  overflow: hidden;
  color: #ffffff;
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
  margin-bottom: 18rpx;
}

.cover-preview-image,
.cover-preview-shade {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
}

.cover-preview-shade {
  background: linear-gradient(180deg, rgba(15, 23, 42, 0.1), rgba(15, 23, 42, 0.58));
}

.cover-preview-glow {
  position: absolute;
  top: -46rpx;
  right: -20rpx;
  width: 180rpx;
  height: 180rpx;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.22);
}

.cover-preview-kicker,
.cover-preview-title,
.cover-preview-desc {
  position: relative;
  z-index: 1;
}

.cover-preview-kicker {
  font-size: 22rpx;
  opacity: 0.82;
  margin-bottom: 8rpx;
}

.cover-preview-title {
  font-size: 34rpx;
  font-weight: 800;
  margin-bottom: 8rpx;
}

.cover-preview-desc {
  font-size: 22rpx;
  opacity: 0.88;
}

.cover-action-row {
  gap: 14rpx;
  margin-bottom: 12rpx;
}

.cover-action-btn {
  min-width: 180rpx;
}

.cover-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 14rpx;
  margin-top: 16rpx;
}

.cover-chip {
  min-height: 104rpx;
  border-radius: 24rpx;
  padding: 16rpx;
  color: #ffffff;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  border: 2rpx solid transparent;
}

.cover-chip.active {
  border-color: rgba(17, 24, 39, 0.18);
  box-shadow: 0 10rpx 20rpx rgba(24, 39, 75, 0.08);
}

.cover-chip-emoji {
  font-size: 28rpx;
}

.cover-chip-label {
  font-size: 22rpx;
  font-weight: 700;
}

.cover-ocean {
  background: linear-gradient(135deg, #0f4c81, #4fa3d1);
}

.cover-sunrise {
  background: linear-gradient(135deg, #ff8a5b, #ffd166);
}

.cover-forest {
  background: linear-gradient(135deg, #215732, #6bbf59);
}

.cover-berry {
  background: linear-gradient(135deg, #8f2d56, #e76f90);
}

.cover-night {
  background: linear-gradient(135deg, #1f2a44, #5c6bc0);
}

.cover-sand {
  background: linear-gradient(135deg, #9c6644, #ddb892);
}

.inline-loading {
  padding: 24rpx 0;
  color: #8a94a3;
  font-size: 24rpx;
}

.loading-block {
  margin: 24rpx 30rpx 0;
  padding: 56rpx 36rpx;
  border-radius: 32rpx;
  background: white;
  text-align: center;
  color: #888888;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.04);
}

.retry-btn {
  min-width: 220rpx;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20rpx;
}

.deposit-form-item {
  grid-column: 1 / -1;
}

.deposit-todo-field {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 6rpx;
  height: auto;
  min-height: var(--wc-form-height);
  padding-top: 18rpx;
  padding-bottom: 18rpx;
  line-height: 1.45;
  color: var(--wc-text-soft);
  background: var(--wc-surface-muted);
}

.deposit-todo-title {
  display: block;
  font-size: 28rpx;
  font-weight: 700;
  color: var(--wc-text);
}

.deposit-todo-desc {
  display: block;
  font-size: 24rpx;
  color: var(--wc-text-faint);
}

.helper-card {
  margin-top: 8rpx;
  padding: 24rpx 26rpx;
  border-radius: 24rpx;
  background: linear-gradient(135deg, #f6fbff, #eef5ff);
}

.helper-title {
  display: block;
  margin-bottom: 8rpx;
  font-size: 26rpx;
  font-weight: 700;
  color: #334155;
}
</style>
