<template>
  <view class="record-container wc-page-enter">
    <view class="type-tabs" :style="{ paddingTop: `${safeTop}px`, paddingRight: `${menuButtonSafeRight}px` }">
      <view class="type-tabs-body" :style="{ minHeight: `${topBarHeight - safeTop}px` }">
        <view class="type-tabs-shell">
          <view class="wc-back-chip wc-pressable" @tap="goBack">
            <text class="wc-back-chip__icon">‹</text>
          </view>

          <view class="type-tabs-rail wc-form-segment-rail">
            <view
              v-for="tab in tabs"
              :key="tab.key"
              class="type-tab wc-form-segment wc-pressable"
              :class="{ active: currentTab === tab.key }"
              @tap="switchTab(tab.key)"
            >
              <text class="type-tab-icon">{{ tab.icon }}</text>
              <text class="type-tab-label">{{ tab.label }}</text>
            </view>
          </view>
        </view>
      </view>
    </view>

    <scroll-view
      class="content wc-section-enter"
      scroll-y
      :style="{ height: `calc(100vh - ${topBarHeight}px)` }"
    >
      <template v-if="currentTab === 'weight'">
        <view class="summary-card" v-if="latestWeightText">
          <text class="summary-label">最近一次体重记录</text>
          <view class="summary-value-row">
            <text class="summary-value">{{ latestWeightText }}</text>
            <text class="summary-sub">{{ latestRecordedAtText }}</text>
          </view>
        </view>
        <view v-else-if="latestRecordLoadError" class="summary-card summary-card--error">
          <text class="summary-label">最近一次体重记录</text>
          <view class="summary-value-row">
            <text class="summary-value summary-value--compact">记录加载失败</text>
            <text class="summary-sub">{{ latestRecordLoadError }}</text>
          </view>
          <button class="flow-btn flow-btn--secondary flow-btn--compact summary-action" @tap="loadLatestRecord">{{ sharedRetryText }}</button>
        </view>

        <view class="form-card">
          <view class="form-group">
            <text class="wc-form-label">当前体重</text>
            <view class="wc-form-row">
              <input
                v-model="weightForm.weight"
                type="digit"
                class="wc-form-input wc-form-input--large"
                placeholder="请输入体重"
                placeholder-style="color:#9aa4b2;"
              />
              <text class="unit">kg</text>
            </view>
          </view>

          <view class="form-group">
            <text class="wc-form-label">体脂率 (选填)</text>
            <view class="wc-form-row">
              <input
                v-model="weightForm.bodyFat"
                type="digit"
                class="wc-form-input wc-form-input--medium"
                placeholder="可不填"
                placeholder-style="color:#9aa4b2;"
              />
              <text class="unit">%</text>
            </view>
          </view>

          <view class="form-group">
            <text class="wc-form-label">备注 (选填)</text>
            <textarea
              v-model="weightForm.remark"
              class="wc-form-textarea"
              placeholder="可记录今天的状态、饮食或运动感受"
              placeholder-style="color:#9aa4b2;"
            />
          </view>
        </view>
      </template>

      <template v-else-if="currentTab === 'diet'">
        <view class="form-card">
          <view class="form-group">
            <text class="wc-form-label">餐次</text>
            <view class="meal-tags wc-tab-rail wc-tab-rail--compact">
              <view
                v-for="meal in mealOptions"
                :key="meal"
                class="meal-tag wc-tab-item wc-tab-item--compact wc-tab-item--full wc-pressable"
                :class="{ active: selectedMeal === meal }"
                @tap="selectedMeal = meal"
              >
                {{ meal }}
              </view>
            </view>
          </view>

          <view class="form-group">
            <text class="wc-form-label">食物名称</text>
            <input
              v-model="dietForm.name"
              class="wc-form-input"
              placeholder="例如：鸡胸肉沙拉"
              placeholder-style="color:#9aa4b2;"
            />
            <view class="food-estimator">
              <view class="food-estimator__header">
                <text class="food-estimator__label">常见食物估算</text>
                <view class="food-estimator__portion">
                  <text>份数</text>
                  <input
                    v-model="dietForm.portionMultiplier"
                    type="digit"
                    class="food-estimator__portion-input"
                    placeholder="1"
                    placeholder-style="color:#9aa4b2;"
                  />
                </view>
              </view>
              <scroll-view class="food-estimator__scroll" scroll-x enable-flex>
                <view class="food-estimator__options">
                  <view
                    v-for="food in commonFoodOptions"
                    :key="food.name"
                    class="food-estimator__chip wc-pressable"
                    :class="{ active: isFoodEstimateSelected(food.name) }"
                    @tap="selectFoodEstimate(food)"
                  >
                    <text>{{ food.name }}</text>
                    <text>{{ food.serving }}</text>
                  </view>
                </view>
              </scroll-view>
            </view>
          </view>
          <view class="form-group">
            <text class="wc-form-label">热量估算 (kcal)</text>
            <input
              v-model="dietForm.calories"
              type="number"
              class="wc-form-input"
              placeholder="420"
              placeholder-style="color:#9aa4b2;"
              @input="markDietCaloriesManual"
            />
            <text class="field-tip">{{ dietEstimateHint }}</text>
          </view>
        </view>
      </template>

      <template v-else-if="currentTab === 'exercise'">
        <view class="form-card">
          <view class="form-group">
            <text class="wc-form-label">运动类型</text>
            <input
              v-model="exerciseForm.type"
              class="wc-form-input"
              placeholder="例如：户外跑步"
              placeholder-style="color:#9aa4b2;"
            />
            <view class="exercise-dictionary">
              <text class="exercise-dictionary__label">常用运动</text>
              <scroll-view class="exercise-type-scroll" scroll-x enable-flex>
                <view class="exercise-type-options">
                  <view
                    v-for="type in exerciseTypeOptions"
                    :key="type"
                    class="exercise-type-chip wc-pressable"
                    :class="{ active: isExerciseTypeSelected(type) }"
                    @tap="selectExerciseType(type)"
                  >
                    {{ type }}
                  </view>
                </view>
              </scroll-view>
            </view>
          </view>
          <view class="form-grid">
            <view class="form-group">
              <text class="wc-form-label">运动时长</text>
              <input
                v-model="exerciseForm.minutes"
                type="number"
                class="wc-form-input"
                placeholder="45"
                placeholder-style="color:#9aa4b2;"
              />
            </view>
            <view class="form-group">
              <text class="wc-form-label">消耗热量</text>
              <input
                v-model="exerciseForm.calories"
                type="number"
                class="wc-form-input"
                placeholder="320"
                placeholder-style="color:#9aa4b2;"
                @input="markExerciseCaloriesManual"
              />
            </view>
          </view>
          <view class="hint-card">
            <text>{{ exerciseEstimateHint }}</text>
          </view>
        </view>
      </template>

      <template v-else-if="currentTab === 'water'">
        <view class="form-card">
          <view class="form-group">
            <text class="wc-form-label">快捷记录</text>
            <view class="meal-tags wc-tab-rail wc-tab-rail--compact">
              <view
                v-for="preset in waterPresetOptions"
                :key="preset.label"
                class="meal-tag wc-tab-item wc-tab-item--compact wc-tab-item--full wc-pressable"
                :class="{ active: Number(waterForm.cups) === preset.cups && Number(waterForm.ml) === preset.ml }"
                @tap="applyWaterPreset(preset.cups, preset.ml)"
              >
                {{ preset.label }}
              </view>
            </view>
          </view>

          <view class="form-grid">
            <view class="form-group">
              <text class="wc-form-label">杯数</text>
              <view class="wc-form-row">
                <input
                  v-model="waterForm.cups"
                  type="number"
                  class="wc-form-input"
                  placeholder="1"
                  placeholder-style="color:#9aa4b2;"
                />
                <text class="unit">杯</text>
              </view>
            </view>
            <view class="form-group">
              <text class="wc-form-label">毫升</text>
              <view class="wc-form-row">
                <input
                  v-model="waterForm.ml"
                  type="number"
                  class="wc-form-input"
                  placeholder="250"
                  placeholder-style="color:#9aa4b2;"
                />
                <text class="unit">ml</text>
              </view>
            </view>
          </view>

          <view class="hint-card">
            <text>💧 默认按 1 杯约 250 ml 估算，也可以按你的杯型直接调整。</text>
          </view>
        </view>
      </template>

      <template v-else>
        <view class="form-card">
          <view class="task-header">
            <text class="wc-form-label">今日习惯</text>
            <button class="flow-btn flow-btn--secondary flow-btn--compact" @tap="goHabitPage">管理习惯</button>
          </view>
          <app-empty-state
            v-if="habitTaskLoadError"
            icon="⚠️"
            title="今日习惯加载失败"
            :description="habitTaskLoadError"
          >
            <button class="flow-btn flow-btn--secondary flow-btn--compact" @tap="loadTodayHabitTasks">{{ sharedRetryText }}</button>
          </app-empty-state>
          <view v-else-if="habitTasks.length" class="habit-task-list">
            <habit-card
              v-for="task in habitTasks"
              :key="task.id"
              :habit="task"
              compact
              :primary-action-text="task.checkedToday ? '撤销打卡' : '今日打卡'"
              :primary-action-tone="task.checkedToday ? 'secondary' : 'primary'"
              @primaryTap="handleHabitTaskTap(task)"
            >
              <template #media>
                <view class="habit-checkin-images" @tap.stop>
                  <view class="habit-checkin-images__header">
                    <view class="habit-checkin-images__copy">
                      <text class="habit-checkin-images__title">{{ habitTaskImageTitle(task) }}</text>
                      <text class="habit-checkin-images__desc">{{ habitTaskImageSummary(task) }}</text>
                    </view>
                    <button
                      class="flow-btn flow-btn--secondary flow-btn--compact habit-checkin-images__action"
                      :loading="isHabitTaskImageUploading(task.id)"
                      :disabled="isHabitTaskImageUploading(task.id) || getHabitTaskTotalImageCount(task) >= 9"
                      @tap.stop="chooseHabitTaskImages(task)"
                    >
                      {{ habitTaskImageActionText(task) }}
                    </button>
                  </view>

                  <view v-if="getHabitTaskImageDrafts(task.id).length" class="draft-image-grid habit-checkin-image-grid">
                    <view v-for="(image, index) in getHabitTaskImageDrafts(task.id)" :key="image.id" class="draft-image-item">
                      <resolved-image
                        class="draft-image-preview"
                        :src="getUploadedImagePreviewSource(image)"
                        mode="aspectFill"
                        :preview-list="getHabitTaskImagePreviewSources(task.id)"
                        :preview-index="index"
                      />
                      <view v-if="image.uploading" class="upload-image-status">
                        <text class="upload-image-badge">上传中</text>
                      </view>
                      <view v-else-if="image.errorMessage" class="upload-image-status upload-image-status--error">
                        <text class="upload-image-badge upload-image-badge--error">上传失败</text>
                      </view>
                      <text
                        v-if="image.errorMessage && !image.uploading"
                        class="upload-image-retry"
                        @tap.stop="retryHabitTaskImage(task, index)"
                      >重试</text>
                      <text class="draft-image-remove" @tap.stop="removeHabitTaskImage(task.id, index)">✕</text>
                    </view>
                  </view>
                </view>
              </template>
            </habit-card>
          </view>
          <app-empty-state
            v-else
            icon="📋"
            :title="habitEmptyTitle"
            :description="habitEmptyDescription"
          >
            <button class="flow-btn flow-btn--primary flow-btn--compact" @tap="goHabitPage">管理习惯</button>
          </app-empty-state>
        </view>

      </template>

      <view v-if="currentTab !== 'habit'" class="draft-trigger">
        <view class="draft-section__header">
          <view class="draft-trigger__copy">
            <text class="draft-trigger__title">{{ currentRecordImageTitle }}</text>
            <text class="draft-trigger__desc">{{ currentRecordImageSummary }}</text>
          </view>
          <button
            class="flow-btn flow-btn--secondary flow-btn--compact"
            :loading="recordImageUploading"
            :disabled="recordImageUploading || recordImages.length >= 9"
            @tap="chooseRecordImages"
          >
            {{ recordImages.length ? '继续添加' : '添加图片' }}
          </button>
        </view>

        <view v-if="recordImages.length" class="draft-image-grid">
          <view v-for="(image, index) in recordImages" :key="image.id" class="draft-image-item">
            <resolved-image
              class="draft-image-preview"
              :src="getUploadedImagePreviewSource(image)"
              mode="aspectFill"
              :preview-list="recordImagePreviewSources"
              :preview-index="index"
            />
            <view v-if="image.uploading" class="upload-image-status">
              <text class="upload-image-badge">上传中</text>
            </view>
            <view v-else-if="image.errorMessage" class="upload-image-status upload-image-status--error">
              <text class="upload-image-badge upload-image-badge--error">上传失败</text>
            </view>
            <text
              v-if="image.errorMessage && !image.uploading"
              class="upload-image-retry"
              @tap.stop="retryRecordImage(index)"
            >重试</text>
            <text class="draft-image-remove" @tap="removeRecordImage(index)">✕</text>
          </view>
        </view>
      </view>

      <view v-if="currentTab === 'habit'" class="draft-trigger">
        <view class="draft-trigger__row">
          <view class="draft-trigger__copy">
            <text class="draft-trigger__title">{{ hasRequiredCircle ? '习惯打卡同步圈子' : '习惯打卡同步动态' }}</text>
            <text class="draft-trigger__desc">{{ habitShareSummary }}</text>
          </view>
          <view class="required-switch wc-pressable" :class="{ active: habitShareFeedEnabled }" @tap="toggleHabitShareFeed">
            {{ habitShareFeedEnabled ? '已开启' : '已关闭' }}
          </view>
        </view>
      </view>

      <view v-if="supportsCircleSync" class="sync-trigger wc-pressable" @tap="syncModalVisible = true">
        <text>关联圈子</text>
        <text>{{ selectedCircleSummary }}</text>
      </view>

      <view v-if="supportsRecordFeedShare" class="draft-trigger">
        <view class="draft-trigger__row">
          <view class="draft-trigger__copy">
            <text class="draft-trigger__title">{{ hasRequiredCircle ? '同步当前圈子' : '同时发动态' }}</text>
            <text class="draft-trigger__desc">{{ currentRecordShareSummary }}</text>
          </view>
          <view class="required-switch wc-pressable" :class="{ active: currentShareFeedEnabled }" @tap="toggleCurrentShareFeed">
            {{ currentShareFeedEnabled ? '已开启' : '已关闭' }}
          </view>
        </view>

      </view>

      <view v-if="shareScopePanelVisible" class="share-scope-panel">
        <view class="share-scope-panel__copy">
          <text class="share-scope-panel__label">发布范围</text>
          <text class="share-scope-panel__desc">{{ shareScopeSummary }}</text>
        </view>
        <view class="share-scope-toggle">
          <view
            class="share-scope-chip wc-pressable"
            :class="{ active: effectiveShareVisibilityScope === 'public' }"
            @tap="setRecordVisibilityScope('public')"
          >
            公开
          </view>
          <view
            class="share-scope-chip wc-pressable"
            :class="{ active: effectiveShareVisibilityScope === 'circle', disabled: !selectedShareCircleCount }"
            @tap="setRecordVisibilityScope('circle')"
          >
            圈内
          </view>
        </view>
      </view>

      <view class="page-bottom-space"></view>
    </scroll-view>

    <view v-if="currentTab !== 'habit'" class="footer-action wc-footer-action">
      <button class="flow-btn flow-btn--primary flow-btn--wide" :loading="submitting" @tap="submitCurrentTab">{{ submitActionText }}</button>
    </view>

    <view v-if="syncModalVisible" class="sync-mask" @tap="syncModalVisible = false">
      <view class="sync-modal" @tap.stop>
        <view class="sync-header">
          <text class="sync-title">关联圈子</text>
          <text class="sync-close" @tap="syncModalVisible = false">✕</text>
        </view>
        <text class="sync-tip">{{ syncSelectionTip }}</text>
        <view v-if="myCircles.length" class="sync-list">
          <view
            v-for="circle in myCircles"
            :key="circle.id"
            class="sync-circle-item wc-pressable"
            :class="{ selected: selectedCircleIds.includes(circle.id), disabled: !canUseCircle(circle), locked: isRequiredCircle(circle.id) }"
            @tap="toggleCircleSelection(circle.id)"
          >
            <view>
              <text class="sync-circle-name">{{ circle.name }}</text>
              <text class="sync-circle-desc">{{ syncCircleDesc(circle) }}</text>
            </view>
            <text class="sync-check">{{ isRequiredCircle(circle.id) ? '必选' : selectedCircleIds.includes(circle.id) ? '✓' : '○' }}</text>
          </view>
        </view>
        <view v-else-if="myCircleLoadError" class="sync-empty sync-empty--error">
          <text>{{ myCircleLoadError }}</text>
          <button class="flow-btn flow-btn--secondary flow-btn--compact" @tap="loadMyCircleList">{{ sharedRetryText }}</button>
        </view>
        <view v-else class="sync-empty">当前还没有加入圈子，先去圈子页看看吧。</view>

        <view class="sync-actions">
          <button class="flow-btn flow-btn--primary flow-btn--wide" @tap="confirmSyncSelection">
            确认关联圈子
          </button>
          <button v-if="!hasRequiredCircle" class="flow-btn flow-btn--secondary flow-btn--wide" @tap="saveLocalOnly">本次只记个人记录</button>
        </view>
      </view>
    </view>

    <view v-if="habitSharePromptVisible" class="habit-share-mask" @tap="dismissHabitSharePrompt">
      <view class="habit-share-sheet" @tap.stop>
        <text class="habit-share-sheet__title">打卡已完成</text>
        <text class="habit-share-sheet__desc">这次习惯打卡已经保存。你可以继续发一条动态，让公开页或圈友看到你的进展。</text>
        <view class="habit-share-sheet__actions">
          <button class="flow-btn flow-btn--secondary flow-btn--wide" @tap="dismissHabitSharePrompt">稍后再说</button>
          <button class="flow-btn flow-btn--primary flow-btn--wide" @tap="openHabitFeedDraft">发布动态</button>
        </view>
      </view>
    </view>

    <view v-if="feedDraftVisible" class="draft-mask" @tap="closeFeedDraft">
      <view class="draft-modal" :style="draftSheetStyle" @tap.stop>
        <view class="draft-header">
          <view>
            <text class="draft-title">{{ draftTitle }}</text>
            <text class="draft-subtitle">{{ draftSubtitle }}</text>
          </view>
          <text class="sync-close" @tap="closeFeedDraft">✕</text>
        </view>

        <scroll-view class="draft-scroll" scroll-y :style="draftScrollStyle">
          <view class="draft-section">
            <text class="wc-form-label">发布范围</text>
            <view class="segment-row wc-form-segment-rail">
              <view
                class="segment wc-form-segment wc-pressable"
                :class="{ active: draftVisibilityScope === 'public' }"
                @tap="draftVisibilityScope = 'public'"
              >
                公开动态
              </view>
              <view
                class="segment wc-form-segment wc-pressable"
                :class="{ active: draftVisibilityScope === 'circle' }"
                @tap="draftVisibilityScope = 'circle'"
              >
                仅圈子可见
              </view>
            </view>
            <text class="field-tip">{{ draftVisibilityHint }}</text>
          </view>

          <view class="draft-section">
            <view class="draft-section__header">
              <view class="draft-section__copy">
                <text class="wc-form-label">关联圈子</text>
                <text class="field-tip">选中的第一个圈子会作为主圈子，其他选中项会作为额外同步圈子。</text>
              </view>
              <text class="draft-section__meta">{{ draftSelectedCircleIds.length ? `已选 ${draftSelectedCircleIds.length} 个` : '未选择' }}</text>
            </view>
            <view v-if="draftCircleOptions.length" class="draft-circle-list">
              <view
                v-for="circle in draftCircleOptions"
                :key="`draft-circle-${circle.id}`"
                class="draft-circle-item wc-pressable"
                :class="{ selected: draftSelectedCircleIds.includes(circle.id) }"
                @tap="toggleDraftCircle(circle.id)"
              >
                <view class="draft-circle-item__copy">
                  <text class="draft-circle-item__name">{{ circle.name }}</text>
                  <text class="draft-circle-item__desc">
                    {{ draftSelectedCircleIds[0] === circle.id ? '主圈子' : '可同步圈子' }} · {{ circle.memberCount || 0 }} 人
                  </text>
                </view>
                <text class="draft-circle-item__check">{{ draftSelectedCircleIds.includes(circle.id) ? '✓' : '○' }}</text>
              </view>
            </view>
            <text v-else class="field-tip">当前没有可同步的有效圈子，你仍然可以只发公开动态。</text>
          </view>

          <view class="draft-section">
            <text class="wc-form-label">动态内容</text>
            <textarea
              v-model="draftContent"
              class="wc-form-textarea draft-textarea"
              maxlength="220"
              placeholder="记录会先保存，这里可以补一段想说的话。"
              placeholder-style="color:#9aa4b2;"
            />
            <text class="field-tip">{{ draftFeedHint }}</text>
          </view>

          <view class="draft-section">
            <view class="draft-section__header">
              <view class="draft-section__copy">
                <text class="wc-form-label">动态图片</text>
                <text class="field-tip">支持 1-9 张图片，可与文字一起发布，单张不超过 {{ getUploadSizeLimitText() }}。</text>
              </view>
              <button
                class="flow-btn flow-btn--secondary flow-btn--compact"
                :loading="feedDraftUploading"
                :disabled="feedDraftUploading || draftImages.length >= 9"
                @tap="chooseDraftImages"
              >
                {{ draftImages.length ? '继续添加' : '添加图片' }}
              </button>
            </view>

            <view v-if="draftImages.length" class="draft-image-grid">
              <view v-for="(image, index) in draftImages" :key="image.id" class="draft-image-item">
                <resolved-image
                  class="draft-image-preview"
                  :src="getUploadedImagePreviewSource(image)"
                  mode="aspectFill"
                  :preview-list="draftImagePreviewSources"
                  :preview-index="index"
                />
                <view v-if="image.uploading" class="upload-image-status">
                  <text class="upload-image-badge">上传中</text>
                </view>
                <view v-else-if="image.errorMessage" class="upload-image-status upload-image-status--error">
                  <text class="upload-image-badge upload-image-badge--error">上传失败</text>
                </view>
                <text
                  v-if="image.errorMessage && !image.uploading"
                  class="upload-image-retry"
                  @tap.stop="retryDraftImage(index)"
                >重试</text>
                <text class="draft-image-remove" @tap="removeDraftImage(index)">✕</text>
              </view>
            </view>
          </view>
        </scroll-view>

        <view class="draft-actions">
          <button class="flow-btn flow-btn--secondary flow-btn--wide" @tap="closeFeedDraft">稍后再发</button>
          <button class="flow-btn flow-btn--primary flow-btn--wide" :loading="feedDraftSubmitting" @tap="submitFeedDraft">发布动态</button>
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
import { computed, ref, watch } from 'vue'
import { onLoad, onShow } from '@dcloudio/uni-app'
import AppEmptyState from '@/components/AppEmptyState/index.vue'
import HabitCard from '@/components/HabitCard/index.vue'
import OnboardingGuide from '@/components/OnboardingGuide/index.vue'
import ResolvedImage from '@/components/ResolvedImage/index.vue'
import http, { openLoginPage } from '@/utils/request'
import type {
  FeedCreatePayload,
  FeedVisibilityScope,
  HabitItem,
  HabitStats,
  RecordFeedDraftPayload,
  RecordSubmitPayload,
  MyCircleCard,
  WeightRecordPayload
} from '@/types/api'
import { useUserStore } from '@/stores/user'
import { getMyCircles } from '@/api/circle'
import { checkinHabit, getHabitStats, getTodayHabits, undoHabitCheckin } from '@/api/habit'
import { publishFeed, uploadFeedImage } from '@/api/feed'
import { createExerciseRecord, createFoodRecord, createWaterRecord, createWeightRecord, getExerciseRecordList } from '@/api/record'
import { captureAchievementState, fetchAchievementMetrics, notifyAchievementUnlocks } from '@/utils/achievementReminder'
import { formatApiDateTime } from '@/utils/datetime'
import { normalizeExerciseType, normalizeExerciseTypeUsage } from '@/utils/exercise'
import { getMenuButtonSafeRight, getSafeTop, getTopBarHeight, navigateBackOr } from '@/utils/mobile'
import { createOnShowSuspendGuard } from '@/utils/page'
import { useDefaultPageShare } from '@/utils/share'
import {
  commonExerciseTypes,
  commonFoodOptions,
  estimateExerciseCalories,
  estimateFoodCalories,
  findFoodEstimate,
  getExerciseMet,
  normalizeFoodName,
  parsePositiveNumber,
  type FoodCalorieOption
} from '@/utils/record-estimates'
import {
  advanceOnboardingGuideStep,
  completeOnboardingGuide,
  disableOnboardingGuide,
  getOnboardingGuideStep,
  setOnboardingGuideStep,
  shouldPromptOnboardingGuide,
  type OnboardingGuideStep
} from '@/utils/onboarding'
import {
  chooseAndUploadImages,
  getUploadedImagePreviewSource,
  getUploadSizeLimitText,
  hasFailedImageAssets,
  replaceUploadedImageAsset,
  retryUploadedImageAsset,
  type UploadedImageAsset
} from '@/utils/upload'

useDefaultPageShare()

type RecordTab = 'weight' | 'diet' | 'exercise' | 'water' | 'habit'
type ReturnMode = 'home' | 'history'
type HistorySourceKey = 'default' | 'body' | 'lifestyle'
type HistoryRecordTypeKey = 'all' | 'weight' | 'food' | 'exercise' | 'water'
type HistoryRangeKey = '7d' | '30d' | '90d' | 'all'
type ShareEnabledTab = Exclude<RecordTab, 'habit'>

const safeTop = getSafeTop(20)
const topBarHeight = getTopBarHeight(52)
const menuButtonSafeRight = getMenuButtonSafeRight(16, 18)
const windowHeight = typeof uni.getWindowInfo === 'function' ? uni.getWindowInfo().windowHeight : uni.getSystemInfoSync().windowHeight
const draftTopGap = Math.max(topBarHeight + 16, safeTop + 84, 132)
const draftSheetHeight = Math.max(420, windowHeight - draftTopGap)
const draftSheetStyle = {
  height: `${draftSheetHeight}px`
}
const draftScrollStyle = {
  height: `${Math.max(260, draftSheetHeight - 204)}px`
}
const currentTab = ref<RecordTab>('weight')
const returnMode = ref<ReturnMode>('home')
const returnHistorySource = ref<HistorySourceKey>('default')
const returnHistoryType = ref<HistoryRecordTypeKey>('all')
const returnHistoryRange = ref<HistoryRangeKey>('all')
const submitting = ref(false)
const latestRecord = ref<WeightRecordPayload | null>(null)
const userStore = useUserStore()
const syncModalVisible = ref(false)
const myCircles = ref<MyCircleCard[]>([])
const selectedCircleIds = ref<number[]>([])
const requiredCircleId = ref<number | null>(null)
const requiredCircleName = ref('')
const recordFeedVisibilityScope = ref<FeedVisibilityScope>('public')
const habitTasks = ref<HabitItem[]>([])
const habitStats = ref<HabitStats | null>(null)
const habitSharePromptVisible = ref(false)
const onboardingGuideVisible = ref(false)
const onboardingGuideStep = ref<OnboardingGuideStep>('record')
const pendingHabitDraft = ref<RecordFeedDraftPayload | null>(null)
const latestRecordLoadError = ref('')
const myCircleLoadError = ref('')
const habitTaskLoadError = ref('')
const onShowReloadGuard = createOnShowSuspendGuard()
const shareFeedEnabled = ref<Record<ShareEnabledTab, boolean>>({
  weight: false,
  diet: false,
  exercise: false,
  water: false
})
const habitShareFeedEnabled = ref(false)
const recordImageUploading = ref(false)
const recordImages = ref<UploadedImageAsset[]>([])
const recordImagePreviewSources = computed(() => recordImages.value
  .map((item) => getUploadedImagePreviewSource(item))
  .filter(Boolean))
const habitImageUploadingMap = ref<Record<number, boolean>>({})
const habitImageDraftMap = ref<Record<number, UploadedImageAsset[]>>({})
const feedDraftVisible = ref(false)
const feedDraftSubmitting = ref(false)
const feedDraftUploading = ref(false)
const draftFeedType = ref<FeedCreatePayload['feedType']>('text')
const draftContent = ref('')
const draftVisibilityScope = ref<FeedVisibilityScope>('public')
const draftSelectedCircleIds = ref<number[]>([])
const draftImages = ref<UploadedImageAsset[]>([])
const draftImagePreviewSources = computed(() => draftImages.value
  .map((item) => getUploadedImagePreviewSource(item))
  .filter(Boolean))
const draftSourceType = ref<string | null>(null)
const draftSourceId = ref<number | null>(null)
const draftContext = ref<{ navigateAfterClose: boolean }>({ navigateAfterClose: false })
const selectedMeal = ref('早餐')
const mealTypeMap: Record<string, string> = {
  早餐: '0',
  午餐: '1',
  晚餐: '2',
  加餐: '3'
}

const sharedRetryText = '重新整理'

const tabs = [
  { key: 'weight', label: '体重', icon: '⚖️' },
  { key: 'diet', label: '饮食', icon: '🥗' },
  { key: 'exercise', label: '运动', icon: '🏃' },
  { key: 'water', label: '饮水', icon: '💧' },
  { key: 'habit', label: '习惯', icon: '📋' }
] as const

const mealOptions = ['早餐', '午餐', '晚餐', '加餐']
const waterPresetOptions = [
  { cups: 1, ml: 250, label: '1 杯' },
  { cups: 2, ml: 500, label: '2 杯' },
  { cups: 3, ml: 750, label: '3 杯' },
  { cups: 4, ml: 1000, label: '4 杯' }
]
const EXERCISE_TYPE_USAGE_STORAGE_KEY = 'recordExerciseTypeUsage'

const weightForm = ref({
  weight: '',
  bodyFat: '',
  remark: ''
})

const dietForm = ref({
  name: '',
  calories: '',
  portionMultiplier: '1'
})

const exerciseForm = ref({
  type: '',
  minutes: '',
  calories: ''
})
const dietCaloriesManual = ref(false)
const exerciseCaloriesManual = ref(false)
const exerciseTypeUsage = ref<Record<string, number>>(loadExerciseTypeUsage())
const exerciseHistoryTypeUsage = ref<Record<string, number>>({})
const exerciseHistoryUsageLoading = ref(false)
const exerciseHistoryUsageLoaded = ref(false)

const waterForm = ref({
  cups: '1',
  ml: '250'
})

const latestWeightText = computed(() => {
  if (!latestRecord.value?.weight) {
    return ''
  }
  return `${latestRecord.value.weight} kg`
})

const latestRecordedAtText = computed(() => {
  if (!latestRecord.value?.recordedAt) {
    return '暂无记录时间'
  }
  return formatApiDateTime(latestRecord.value.recordedAt, latestRecord.value.recordedAt)
})

const selectedCircleSummary = computed(() => {
  const count = normalizeDraftCircleIds(selectedCircleIds.value).length
  if (!count) {
    return '未选择'
  }
  if (hasRequiredCircle.value) {
    const extraCount = Math.max(count - 1, 0)
    return extraCount ? `当前圈子必选，另选 ${extraCount} 个` : '当前圈子必选'
  }
  return `已选 ${count} 个圈子`
})

const submitActionText = computed(() => {
  if (currentTab.value === 'weight') {
    return '记录体重'
  }
  if (currentTab.value === 'diet') {
    return '记录饮食'
  }
  if (currentTab.value === 'exercise') {
    return '记录运动'
  }
  if (currentTab.value === 'water') {
    return '记录饮水'
  }
  return '记录内容'
})
const exerciseTypeOptions = computed(() => {
  const optionIndex = new Map<string, number>()

  commonExerciseTypes.forEach((type, index) => {
    optionIndex.set(type, index)
  })

  Object.keys(exerciseHistoryTypeUsage.value).forEach((type) => {
    if (!optionIndex.has(type)) {
      optionIndex.set(type, commonExerciseTypes.length + optionIndex.size)
    }
  })

  Object.keys(exerciseTypeUsage.value).forEach((type) => {
    if (!optionIndex.has(type)) {
      optionIndex.set(type, commonExerciseTypes.length + optionIndex.size)
    }
  })

  return Array.from(optionIndex.entries())
    .map(([type, index]) => ({
      type,
      index,
      usage: (exerciseHistoryTypeUsage.value[type] || 0) + (exerciseTypeUsage.value[type] || 0)
    }))
    .sort((left, right) => right.usage - left.usage || left.index - right.index)
    .map((item) => item.type)
})

const supportsCircleSync = computed(() => true)
const supportsRecordFeedShare = computed(() => currentTab.value !== 'habit')
const hasRequiredCircle = computed(() => Boolean(requiredCircleId.value))
const selectedShareCircleCount = computed(() => normalizeDraftCircleIds(selectedCircleIds.value).length)
const effectiveShareVisibilityScope = computed<FeedVisibilityScope>(() =>
  selectedShareCircleCount.value ? recordFeedVisibilityScope.value : 'public'
)
const shareScopeSummary = computed(() => {
  if (!selectedShareCircleCount.value) {
    return '未关联圈子时默认公开，发布后会显示在发现页公开动态。'
  }
  if (effectiveShareVisibilityScope.value === 'circle') {
    return `仅 ${selectedShareCircleCount.value} 个已选圈子成员可见，不进入发现页公开内容。`
  }
  return `默认公开，会显示在发现页，并同步到 ${selectedShareCircleCount.value} 个已选圈子。`
})
const syncSelectionTip = computed(() =>
  hasRequiredCircle.value
    ? `当前圈子${requiredCircleName.value ? `「${requiredCircleName.value}」` : ''}为本次打卡必选项，可再选择其他圈子同步。`
    : '选择圈子会自动开启同步动态；关闭同步动态时会清空圈子选择。'
)
const currentShareFeedEnabled = computed({
  get() {
    if (currentTab.value === 'habit') {
      return false
    }
    return shareFeedEnabled.value[currentTab.value as ShareEnabledTab]
  },
  set(value: boolean) {
    if (currentTab.value === 'habit') {
      return
    }
    shareFeedEnabled.value = {
      ...shareFeedEnabled.value,
      [currentTab.value]: value
    }
  }
})
const shareScopeEnabled = computed(() => currentTab.value === 'habit' ? habitShareFeedEnabled.value : currentShareFeedEnabled.value)
const shareScopePanelVisible = computed(() => shareScopeEnabled.value)
const draftCircleOptions = computed(() => myCircles.value.filter((circle) => canUseCircle(circle)))
const draftTitle = computed(() => (draftContext.value.navigateAfterClose ? '记录已保存，继续发动态' : '发布动态'))
const draftSubtitle = computed(() =>
  draftContext.value.navigateAfterClose
    ? '记录已经成功保存。你可以补充文案、图片，并决定公开或同步到圈子。'
    : '这次打卡已经完成。现在可以整理成一条动态发出去。'
)
const draftVisibilityHint = computed(() => {
  if (draftVisibilityScope.value === 'circle') {
    if (!draftSelectedCircleIds.value.length) {
      return '仅圈子可见时，至少需要选择一个圈子。'
    }
    const primaryCircle = myCircles.value.find((circle) => circle.id === draftSelectedCircleIds.value[0])
    return `这条动态会发布到 ${primaryCircle?.name || '已选圈子'}，仅相关圈友可见。`
  }
  if (!draftSelectedCircleIds.value.length) {
    return '这条动态会显示在发现页公开动态，你也可以再关联到一个或多个圈子。'
  }
  return '这条动态会显示在发现页公开动态，同时同步到你选中的圈子。'
})
const draftFeedHint = computed(() => {
  const mapping: Record<string, string> = {
    weight: '已按体重记录预生成草稿，你可以补充今天的状态或感受。',
    food: '已按饮食记录预生成草稿，你可以补充餐次感受或图片。',
    exercise: '已按运动记录预生成草稿，你可以补充运动感受或结果。',
    water: '已按饮水记录预生成草稿，适合在需要时补充打卡内容。',
    habit: '已按习惯打卡预生成草稿，你可以补充执行感受或打卡原因。'
  }
  return mapping[String(draftFeedType.value || '')] || '你可以继续编辑这条动态。'
})
const currentRecordShareSummary = computed(() => {
  const linkedCircleCount = selectedShareCircleCount.value
  if (currentTab.value === 'water') {
    return currentShareFeedEnabled.value
      ? '保存记录后会直接用本次饮水内容和记录图片发布动态。'
      : '高频饮水记录默认不发动态，需要时再手动打开。'
  }
  if (!currentShareFeedEnabled.value) {
    return '记录会直接保存，不会自动跳转到动态发布。'
  }
  if (linkedCircleCount) {
    return effectiveShareVisibilityScope.value === 'circle'
      ? `记录保存后会自动发布仅圈内可见动态，并关联 ${linkedCircleCount} 个圈子。`
      : `记录保存后会自动发布公开动态，并同步到 ${linkedCircleCount} 个圈子。`
  }
  return '记录保存后会自动用本次记录内容和记录图片发布公开动态。'
})
const currentRecordImageTitle = computed(() => {
  if (currentTab.value === 'weight') {
    return '体重记录图片'
  }
  if (currentTab.value === 'diet') {
    return '饮食记录图片'
  }
  if (currentTab.value === 'exercise') {
    return '运动记录图片'
  }
  if (currentTab.value === 'water') {
    return '饮水记录图片'
  }
  return '习惯打卡图片'
})
const currentRecordImageSummary = computed(() => {
  if (recordImages.value.length) {
    return `已添加 ${recordImages.value.length} 张图片，会保存到本次${currentTab.value === 'habit' ? '打卡' : '记录'}。`
  }
  return `支持 1-9 张图片，单张不超过 ${getUploadSizeLimitText()}。`
})
const habitShareSummary = computed(() => {
  const linkedCircleCount = selectedShareCircleCount.value
  if (!habitShareFeedEnabled.value) {
    return linkedCircleCount
      ? `已选 ${linkedCircleCount} 个圈子，开启后会把习惯打卡同步到圈子动态。`
      : '习惯打卡默认只保存记录，开启后会用对应习惯的打卡内容和图片发布动态。'
  }
  if (linkedCircleCount) {
    return effectiveShareVisibilityScope.value === 'circle'
      ? `打卡成功后会自动发布仅圈内可见动态，并同步到 ${linkedCircleCount} 个圈子。`
      : `打卡成功后会自动发布公开动态，并同步到 ${linkedCircleCount} 个圈子。`
  }
  return '打卡成功后会自动发布公开动态，图片来自被打卡的那个习惯卡片。'
})
const habitEmptyTitle = computed(() => {
  if ((habitStats.value?.totalHabits || 0) > 0) {
    return '今天没有应完成的习惯'
  }
  return '还没有创建习惯'
})
const habitEmptyDescription = computed(() => {
  if ((habitStats.value?.totalHabits || 0) > 0) {
    return '今天不需要完成额外习惯，明天再来看也可以。'
  }
  return '先在习惯管理里创建一个习惯，它会在应做当天自动同步到这里。'
})
const selectedFoodEstimate = computed(() => findFoodEstimate(dietForm.value.name))
const dietEstimateHint = computed(() => {
  const food = selectedFoodEstimate.value
  if (!food) {
    return '输入或选择常见食物后会自动估算，结果可修改。'
  }
  const multiplier = parsePositiveNumber(dietForm.value.portionMultiplier, 1)
  return `按 ${food.name} ${food.serving} x ${multiplier} 估算，保存前可手动调整。`
})
const exerciseEstimateHint = computed(() => {
  const exerciseType = normalizeExerciseType(exerciseForm.value.type)
  const met = getExerciseMet(exerciseType)
  const bodyWeight = getExerciseEstimateBodyWeight()
  if (!exerciseType) {
    return '🏃 选择运动并填写时长后，会按当前体重自动估算消耗热量。'
  }
  if (!met) {
    return '🏃 这个运动暂未配置估算参数，请手动填写消耗热量。'
  }
  if (!bodyWeight) {
    return '🏃 完善个人资料中的当前体重后，可自动估算运动消耗。'
  }
  return `🏃 按 ${exerciseType} MET ${met}、当前体重 ${bodyWeight}kg 和时长估算，结果可修改。`
})

watch(
  () => [dietForm.value.name, dietForm.value.portionMultiplier],
  () => {
    applyDietCalorieEstimate()
  }
)

watch(
  () => [exerciseForm.value.type, exerciseForm.value.minutes, userStore.userInfo?.currentWeight],
  () => {
    applyExerciseCalorieEstimate()
  }
)

watch(selectedShareCircleCount, (count) => {
  if (!count && recordFeedVisibilityScope.value !== 'public') {
    recordFeedVisibilityScope.value = 'public'
  }
})

onLoad((query) => {
  const requestedTab = String(query?.tab || 'weight') as RecordTab
  if (tabs.some((item) => item.key === requestedTab)) {
    currentTab.value = requestedTab
  }
  const requestedVisibilityScope = String(query?.visibilityScope || '') as FeedVisibilityScope
  if (requestedVisibilityScope === 'public' || requestedVisibilityScope === 'circle') {
    recordFeedVisibilityScope.value = requestedVisibilityScope
  }
  const requestedRequiredCircleId = Number(query?.requiredCircleId || 0)
  if (requestedRequiredCircleId > 0) {
    requiredCircleId.value = requestedRequiredCircleId
    requiredCircleName.value = typeof query?.requiredCircleName === 'string'
      ? decodeURIComponent(query.requiredCircleName)
      : ''
    selectedCircleIds.value = normalizeCircleSelection([requestedRequiredCircleId])
    setShareFeedForCurrentTab(true)
  }
  const requestedCircleIds = parseCircleIdsQuery(query?.circleIds)
  if (requestedCircleIds.length) {
    selectedCircleIds.value = normalizeCircleSelection([...selectedCircleIds.value, ...requestedCircleIds])
    setShareFeedForCurrentTab(true)
  }
  const requestedShareFeed = String(query?.shareFeed || '')
  if (requestedShareFeed === '1' || requestedShareFeed === 'true') {
    setShareFeedForCurrentTab(true)
  }
  const requestedReturnMode = String(query?.returnMode || 'home') as ReturnMode
  if (requestedReturnMode === 'history') {
    returnMode.value = requestedReturnMode
  }
  const requestedReturnSource = String(query?.returnSource || 'default') as HistorySourceKey
  if (requestedReturnSource === 'body' || requestedReturnSource === 'lifestyle') {
    returnHistorySource.value = requestedReturnSource
  }
  const requestedReturnType = String(query?.returnType || 'all') as HistoryRecordTypeKey
  if (['all', 'weight', 'food', 'exercise', 'water'].includes(requestedReturnType)) {
    returnHistoryType.value = requestedReturnType
  }
  const requestedReturnRange = String(query?.returnRange || 'all') as HistoryRangeKey
  if (['7d', '30d', '90d', 'all'].includes(requestedReturnRange)) {
    returnHistoryRange.value = requestedReturnRange
  }
  syncMealSelectionForCurrentTime()
})

onShow(async () => {
  if (onShowReloadGuard.isSuspended()) {
    return
  }
  if (!userStore.isLoggedIn) {
    openLoginPage('safe')
    return
  }
  await Promise.all([
    loadLatestRecord(),
    loadMyCircleList(),
    loadTodayHabitTasks(),
    currentTab.value === 'exercise' ? loadExerciseTypeHistoryUsage() : Promise.resolve()
  ])
})

function parseCircleIdsQuery(rawValue: unknown) {
  if (typeof rawValue !== 'string' || !rawValue.trim()) {
    return []
  }
  return rawValue
    .split(',')
    .map((item) => Number(item.trim()))
    .filter((circleId, index, array) => circleId > 0 && array.indexOf(circleId) === index)
}

function switchTab(tab: RecordTab) {
  if (currentTab.value !== tab) {
    resetRecordImages()
    resetHabitTaskImages()
  }
  currentTab.value = tab
  clearCircleSelectionIfCurrentShareDisabled()
  if (tab === 'habit') {
    syncModalVisible.value = false
  }
  if (tab === 'diet') {
    syncMealSelectionForCurrentTime()
  }
  if (tab === 'exercise') {
    void loadExerciseTypeHistoryUsage()
  }
}

function toggleCurrentShareFeed() {
  if (hasRequiredCircle.value && currentShareFeedEnabled.value) {
    uni.showToast({ title: '当前圈子为必选同步项', icon: 'none' })
    return
  }
  setShareFeedForCurrentTab(!currentShareFeedEnabled.value)
}

function toggleHabitShareFeed() {
  if (hasRequiredCircle.value && habitShareFeedEnabled.value) {
    uni.showToast({ title: '当前圈子为必选同步项', icon: 'none' })
    return
  }
  setShareFeedForCurrentTab(!habitShareFeedEnabled.value)
}

function setRecordVisibilityScope(scope: FeedVisibilityScope) {
  if (scope === 'circle' && !selectedShareCircleCount.value) {
    uni.showToast({ title: '先关联圈子后才能仅圈内可见', icon: 'none' })
    return
  }
  recordFeedVisibilityScope.value = scope
}

function setShareFeedForCurrentTab(enabled: boolean) {
  if (!enabled && hasRequiredCircle.value) {
    setCurrentShareFeedEnabled(true)
    ensureRequiredCircleSelection()
    return
  }
  setCurrentShareFeedEnabled(enabled)
  if (!enabled) {
    selectedCircleIds.value = []
    return
  }
  ensureRequiredCircleSelection()
}

function setCurrentShareFeedEnabled(enabled: boolean) {
  if (currentTab.value === 'habit') {
    habitShareFeedEnabled.value = enabled
  } else if (supportsRecordFeedShare.value) {
    currentShareFeedEnabled.value = enabled
  }
}

function enableShareFeedForCurrentTab() {
  setShareFeedForCurrentTab(true)
}

function clearCircleSelectionIfCurrentShareDisabled() {
  if (hasRequiredCircle.value) {
    setCurrentShareFeedEnabled(true)
    ensureRequiredCircleSelection()
    return
  }
  const enabled = currentTab.value === 'habit'
    ? habitShareFeedEnabled.value
    : currentShareFeedEnabled.value
  if (!enabled) {
    selectedCircleIds.value = []
  }
}

function normalizeCircleSelection(circleIds: number[]) {
  const uniqueIds = circleIds
    .map((circleId) => Number(circleId || 0))
    .filter((circleId, index, array) => circleId > 0 && array.indexOf(circleId) === index)
  const requiredId = requiredCircleId.value
  if (!requiredId) {
    return uniqueIds
  }
  return [requiredId, ...uniqueIds.filter((circleId) => circleId !== requiredId)]
}

function isRequiredCircle(circleId: number) {
  return Boolean(requiredCircleId.value && circleId === requiredCircleId.value)
}

function ensureRequiredCircleSelection() {
  if (!requiredCircleId.value) {
    return
  }
  selectedCircleIds.value = normalizeCircleSelection(selectedCircleIds.value)
}

function syncCircleDesc(circle: MyCircleCard) {
  if (isRequiredCircle(circle.id)) {
    return `当前圈子 · 必选${circle.readOnly ? ' · 已结束' : ''}`
  }
  return `${circle.categoryName || '圈子'} · ${circle.memberCount || 0} 人${circle.readOnly ? ' · 已结束' : ''}`
}

function applyDietCalorieEstimate(force = false) {
  const food = selectedFoodEstimate.value
  if (!food) {
    if (!dietCaloriesManual.value) {
      dietForm.value.calories = ''
    }
    return
  }
  if (!force && dietCaloriesManual.value) {
    return
  }
  dietForm.value.calories = `${estimateFoodCalories(food, dietForm.value.portionMultiplier)}`
}

function selectFoodEstimate(food: FoodCalorieOption) {
  dietCaloriesManual.value = false
  dietForm.value.name = food.name
  applyDietCalorieEstimate(true)
}

function isFoodEstimateSelected(name: string) {
  return normalizeFoodName(dietForm.value.name).includes(normalizeFoodName(name))
}

function markDietCaloriesManual() {
  dietCaloriesManual.value = true
}

function getExerciseEstimateBodyWeight() {
  const currentWeight = Number(userStore.userInfo?.currentWeight || 0)
  return Number.isFinite(currentWeight) && currentWeight > 0 ? currentWeight : 0
}

function applyExerciseCalorieEstimate(force = false) {
  if (!force && exerciseCaloriesManual.value) {
    return
  }
  const durationMinutes = Number(exerciseForm.value.minutes)
  const bodyWeight = getExerciseEstimateBodyWeight()
  const calories = estimateExerciseCalories(exerciseForm.value.type, durationMinutes, bodyWeight)
  if (calories > 0) {
    exerciseForm.value.calories = `${calories}`
  } else if (!exerciseCaloriesManual.value) {
    exerciseForm.value.calories = ''
  }
}

function markExerciseCaloriesManual() {
  exerciseCaloriesManual.value = true
}

function loadExerciseTypeUsage() {
  try {
    const stored = uni.getStorageSync(EXERCISE_TYPE_USAGE_STORAGE_KEY)
    if (!stored || typeof stored !== 'object' || Array.isArray(stored)) {
      return {}
    }

    return normalizeExerciseTypeUsage(stored as Record<string, unknown>)
  } catch (error) {
    console.warn('读取常用运动失败:', error)
    return {}
  }
}

function persistExerciseTypeUsage(usageMap: Record<string, number>) {
  try {
    const nextUsage = normalizeExerciseTypeUsage(usageMap)
    uni.setStorageSync(EXERCISE_TYPE_USAGE_STORAGE_KEY, nextUsage)
    exerciseTypeUsage.value = nextUsage
  } catch (error) {
    console.warn('保存常用运动失败:', error)
  }
}

function rememberExerciseType(type: string) {
  const normalizedType = normalizeExerciseType(type)
  if (!normalizedType) {
    return
  }
  persistExerciseTypeUsage({
    ...exerciseTypeUsage.value,
    [normalizedType]: (exerciseTypeUsage.value[normalizedType] || 0) + 1
  })
}

function selectExerciseType(type: string) {
  exerciseCaloriesManual.value = false
  exerciseForm.value.type = type
  applyExerciseCalorieEstimate(true)
}

function isExerciseTypeSelected(type: string) {
  return normalizeExerciseType(exerciseForm.value.type) === type
}

async function loadExerciseTypeHistoryUsage() {
  if (exerciseHistoryUsageLoaded.value || exerciseHistoryUsageLoading.value) {
    return
  }
  exerciseHistoryUsageLoading.value = true
  try {
    const page = await getExerciseRecordList({ pageNum: 1, pageSize: 50 })
    const usageMap = (page.list || []).reduce<Record<string, number>>((acc, record) => {
      const type = normalizeExerciseType(record.exerciseType || '')
      if (type) {
        acc[type] = (acc[type] || 0) + 1
      }
      return acc
    }, {})
    exerciseHistoryTypeUsage.value = usageMap
    exerciseHistoryUsageLoaded.value = true
  } catch (error) {
    console.warn('加载常用运动失败:', error)
  } finally {
    exerciseHistoryUsageLoading.value = false
  }
}

function resetRecordImages() {
  recordImages.value = []
}

function resetHabitTaskImages(habitId?: number) {
  if (typeof habitId === 'number') {
    const nextDraftMap = { ...habitImageDraftMap.value }
    const nextUploadingMap = { ...habitImageUploadingMap.value }
    delete nextDraftMap[habitId]
    delete nextUploadingMap[habitId]
    habitImageDraftMap.value = nextDraftMap
    habitImageUploadingMap.value = nextUploadingMap
    return
  }
  habitImageDraftMap.value = {}
  habitImageUploadingMap.value = {}
}

function applyWaterPreset(cups: number, ml: number) {
  waterForm.value = {
    cups: `${cups}`,
    ml: `${ml}`
  }
}

function goBack() {
  navigateBackOr('/pages/index/index')
}

function inferMealByCurrentTime() {
  const hour = new Date().getHours()
  if (hour < 10) {
    return '早餐'
  }
  if (hour < 15) {
    return '午餐'
  }
  if (hour < 20) {
    return '晚餐'
  }
  return '加餐'
}

function syncMealSelectionForCurrentTime() {
  if (dietForm.value.name.trim() || dietForm.value.calories.trim()) {
    return
  }
  selectedMeal.value = inferMealByCurrentTime()
}

async function loadLatestRecord() {
  latestRecordLoadError.value = ''
  try {
    latestRecord.value = await http.get<WeightRecordPayload | null>('/api/record/weight/latest', undefined, {
      showLoading: false
    })
  } catch (error) {
    console.error('加载最新体重记录失败:', error)
    latestRecord.value = null
    latestRecordLoadError.value = error instanceof Error ? error.message || '最近体重记录暂时不可用' : '最近体重记录暂时不可用'
  }
}

async function loadMyCircleList() {
  myCircleLoadError.value = ''
  try {
    const page = await getMyCircles({ pageNum: 1, pageSize: 100 })
    myCircles.value = page.list || []
    selectedCircleIds.value = normalizeCircleSelection(
      selectedCircleIds.value.filter((circleId) => isCircleSelectableForSync(circleId))
    )
    draftSelectedCircleIds.value = normalizeCircleSelection(
      draftSelectedCircleIds.value.filter((circleId) => isCircleSelectableForSync(circleId))
    )
    clearCircleSelectionIfCurrentShareDisabled()
  } catch (error) {
    console.error('加载可同步圈子失败:', error)
    myCircles.value = []
    myCircleLoadError.value = error instanceof Error ? error.message || '圈子列表暂时不可用' : '圈子列表暂时不可用'
  }
}

async function loadTodayHabitTasks() {
  habitTaskLoadError.value = ''
  try {
    const [todayHabits, stats] = await Promise.all([getTodayHabits(), getHabitStats()])
    habitTasks.value = todayHabits
    habitStats.value = stats
  } catch (error) {
    console.error('加载今日习惯失败:', error)
    habitTasks.value = []
    habitStats.value = null
    habitTaskLoadError.value = error instanceof Error ? error.message || '今日习惯暂时不可用' : '今日习惯暂时不可用'
  }
}

function toggleCircleSelection(circleId: number) {
  if (isRequiredCircle(circleId)) {
    uni.showToast({ title: '当前圈子为必选项', icon: 'none' })
    return
  }
  const circle = myCircles.value.find((item) => item.id === circleId)
  if (circle && !canUseCircle(circle)) {
    uni.showToast({ title: circle.readOnly ? '圈子已结束，不能继续关联' : '当前圈子暂不可选', icon: 'none' })
    return
  }
  if (selectedCircleIds.value.includes(circleId)) {
    selectedCircleIds.value = normalizeCircleSelection(selectedCircleIds.value.filter((id) => id !== circleId))
    return
  }
  selectedCircleIds.value = normalizeCircleSelection([...selectedCircleIds.value, circleId])
  enableShareFeedForCurrentTab()
}

async function submitCurrentTab() {
  if (currentTab.value === 'weight') {
    await submitWeightRecord()
    return
  }
  if (currentTab.value === 'diet') {
    await submitDietRecord()
    return
  }
  if (currentTab.value === 'exercise') {
    await submitExerciseRecord()
    return
  }
  if (currentTab.value === 'water') {
    await submitWaterRecord()
  }
}

function showActionError(error: unknown, fallback: string) {
  const title = error instanceof Error && error.message ? error.message : fallback
  uni.showToast({
    title: title.length > 12 ? fallback : title,
    icon: 'none'
  })
}

async function chooseRecordImages() {
  if (recordImageUploading.value || recordImages.value.length >= 9) {
    return
  }
  await onShowReloadGuard.runWhileSuspended(async () => {
    try {
      await chooseAndUploadImages({
        currentCount: recordImages.value.length,
        upload: uploadFeedImage,
        fallbackErrorMessage: '记录图片上传失败',
        setUploading: (uploading) => {
          recordImageUploading.value = uploading
        },
        onLocalAssetsSelected: (assets) => {
          recordImages.value = [...recordImages.value, ...assets].slice(0, 9)
        },
        onAssetUpdated: (asset) => {
          recordImages.value = replaceUploadedImageAsset(recordImages.value, asset)
        }
      })
    } catch (error: any) {
      uni.showToast({ title: error?.message || '记录图片上传失败', icon: 'none' })
    }
  })
}

function removeRecordImage(index: number) {
  recordImages.value = recordImages.value.filter((_, currentIndex) => currentIndex !== index)
}

async function retryRecordImage(index: number) {
  const image = recordImages.value[index]
  if (!image || image.uploading) {
    return
  }
  try {
    await retryUploadedImageAsset({
      asset: image,
      upload: uploadFeedImage,
      fallbackErrorMessage: '记录图片上传失败',
      setUploading: (uploading) => {
        recordImageUploading.value = uploading
      },
      onAssetUpdated: (asset) => {
        recordImages.value = replaceUploadedImageAsset(recordImages.value, asset)
      }
    })
  } catch (error: any) {
    uni.showToast({ title: error?.message || '记录图片上传失败', icon: 'none' })
  }
}

function ensureRecordImagesReady() {
  if (recordImageUploading.value) {
    uni.showToast({ title: '图片还在上传中，请稍后再保存', icon: 'none' })
    return false
  }
  if (hasFailedImageAssets(recordImages.value)) {
    uni.showToast({ title: '有图片上传失败，请删除后重试', icon: 'none' })
    return false
  }
  return true
}

function getRecordImageUrls() {
  return recordImages.value.map((item) => item.url).filter(Boolean)
}

function getRecordImagesPayload() {
  const images = getRecordImageUrls()
  return images.length ? JSON.stringify(images) : null
}

function normalizeHabitImageList(rawImages?: string[] | string | null) {
  if (Array.isArray(rawImages)) {
    return rawImages.map((item) => `${item || ''}`.trim()).filter(Boolean).slice(0, 9)
  }
  if (typeof rawImages !== 'string' || !rawImages.trim()) {
    return []
  }
  const text = rawImages.trim()
  try {
    const parsed = JSON.parse(text)
    return Array.isArray(parsed)
      ? parsed.map((item) => `${item || ''}`.trim()).filter(Boolean).slice(0, 9)
      : [text]
  } catch {
    return [text]
  }
}

function mergeHabitImageUrls(urls: string[]) {
  const result: string[] = []
  urls.forEach((url) => {
    const normalized = `${url || ''}`.trim()
    if (normalized && !result.includes(normalized)) {
      result.push(normalized)
    }
  })
  return result.slice(0, 9)
}

function getHabitTaskImageDrafts(habitId: number) {
  return habitImageDraftMap.value[habitId] || []
}

function setHabitTaskImageDrafts(habitId: number, assets: UploadedImageAsset[]) {
  const nextMap = { ...habitImageDraftMap.value }
  if (assets.length) {
    nextMap[habitId] = assets
  } else {
    delete nextMap[habitId]
  }
  habitImageDraftMap.value = nextMap
}

function setHabitTaskImageUploading(habitId: number, uploading: boolean) {
  habitImageUploadingMap.value = {
    ...habitImageUploadingMap.value,
    [habitId]: uploading
  }
}

function isHabitTaskImageUploading(habitId: number) {
  return Boolean(habitImageUploadingMap.value[habitId])
}

function getHabitTaskSavedImages(task: HabitItem) {
  return normalizeHabitImageList(task.images)
}

function getHabitTaskImageUrls(habitId: number) {
  return getHabitTaskImageDrafts(habitId).map((item) => item.url).filter(Boolean)
}

function getHabitTaskImagePreviewSources(habitId: number) {
  return getHabitTaskImageDrafts(habitId)
    .map((item) => getUploadedImagePreviewSource(item))
    .filter(Boolean)
}

function getHabitTaskTotalImageCount(task: HabitItem) {
  return getHabitTaskSavedImages(task).length + getHabitTaskImageDrafts(task.id).length
}

function habitTaskImageTitle(task: HabitItem) {
  return task.checkedToday ? '补充打卡图片' : '本习惯打卡图片'
}

function habitTaskImageSummary(task: HabitItem) {
  const savedCount = getHabitTaskSavedImages(task).length
  const draftCount = getHabitTaskImageDrafts(task.id).length
  if (isHabitTaskImageUploading(task.id)) {
    return '图片正在上传，完成后会归到这条习惯打卡。'
  }
  if (task.checkedToday) {
    if (draftCount) {
      return `还有 ${draftCount} 张图片待处理，成功后会补充到今天这条打卡。`
    }
    if (savedCount) {
      return `今天这条打卡已保存 ${savedCount} 张图片，还可以继续补充。`
    }
    return '今天已打卡，可继续补充图片到这条习惯记录。'
  }
  if (draftCount) {
    return `已为「${task.name}」添加 ${draftCount} 张图片，打卡时会一起保存。`
  }
  return `给「${task.name}」添加图片，打卡时会保存到这个习惯。`
}

function habitTaskImageActionText(task: HabitItem) {
  if (isHabitTaskImageUploading(task.id)) {
    return '上传中'
  }
  if (getHabitTaskTotalImageCount(task) >= 9) {
    return '已满 9 张'
  }
  if (getHabitTaskTotalImageCount(task) > 0) {
    return '继续添加'
  }
  return task.checkedToday ? '补充图片' : '添加图片'
}

function ensureHabitTaskImagesReady(habitId: number) {
  if (isHabitTaskImageUploading(habitId)) {
    uni.showToast({ title: '图片还在上传中，请稍后再打卡', icon: 'none' })
    return false
  }
  if (hasFailedImageAssets(getHabitTaskImageDrafts(habitId))) {
    uni.showToast({ title: '有图片上传失败，请删除后重试', icon: 'none' })
    return false
  }
  return true
}

async function persistCheckedHabitImages(task: HabitItem) {
  const habitId = task.id
  const nextImages = mergeHabitImageUrls([
    ...getHabitTaskSavedImages(task),
    ...getHabitTaskImageUrls(habitId)
  ])
  if (!nextImages.length) {
    return
  }
  try {
    const updated = await checkinHabit(habitId, { images: nextImages })
    habitTasks.value = habitTasks.value.map((item) => (item.id === updated.id ? updated : item))
    const unresolvedAssets = getHabitTaskImageDrafts(habitId).filter((asset) => asset.uploading || asset.errorMessage || !asset.url)
    setHabitTaskImageDrafts(habitId, unresolvedAssets)
    uni.showToast({ title: '图片已保存', icon: 'success' })
  } catch (error) {
    console.error('补充习惯图片失败:', error)
    showActionError(error, '图片保存失败')
  }
}

async function chooseHabitTaskImages(task: HabitItem) {
  const habitId = task.id
  if (isHabitTaskImageUploading(habitId) || getHabitTaskTotalImageCount(task) >= 9) {
    return
  }
  await onShowReloadGuard.runWhileSuspended(async () => {
    try {
      const uploadResult = await chooseAndUploadImages({
        currentCount: getHabitTaskTotalImageCount(task),
        upload: uploadFeedImage,
        fallbackErrorMessage: '习惯图片上传失败',
        setUploading: (uploading) => {
          setHabitTaskImageUploading(habitId, uploading)
        },
        onLocalAssetsSelected: (assets) => {
          setHabitTaskImageDrafts(habitId, [...getHabitTaskImageDrafts(habitId), ...assets])
        },
        onAssetUpdated: (asset) => {
          setHabitTaskImageDrafts(habitId, replaceUploadedImageAsset(getHabitTaskImageDrafts(habitId), asset))
        }
      })
      if (task.checkedToday && uploadResult.uploadedUrls.length) {
        await persistCheckedHabitImages(task)
      }
    } catch (error: any) {
      uni.showToast({ title: error?.message || '习惯图片上传失败', icon: 'none' })
    }
  })
}

function removeHabitTaskImage(habitId: number, index: number) {
  setHabitTaskImageDrafts(habitId, getHabitTaskImageDrafts(habitId).filter((_, currentIndex) => currentIndex !== index))
}

async function retryHabitTaskImage(task: HabitItem, index: number) {
  const habitId = task.id
  const image = getHabitTaskImageDrafts(habitId)[index]
  if (!image || image.uploading) {
    return
  }
  try {
    await retryUploadedImageAsset({
      asset: image,
      upload: uploadFeedImage,
      fallbackErrorMessage: '习惯图片上传失败',
      setUploading: (uploading) => {
        setHabitTaskImageUploading(habitId, uploading)
      },
      onAssetUpdated: (asset) => {
        setHabitTaskImageDrafts(habitId, replaceUploadedImageAsset(getHabitTaskImageDrafts(habitId), asset))
      }
    })
    if (task.checkedToday) {
      await persistCheckedHabitImages(task)
    }
  } catch (error: any) {
    uni.showToast({ title: error?.message || '习惯图片上传失败', icon: 'none' })
  }
}

async function submitWeightRecord() {
  const previousAchievementKeys = captureAchievementState(userStore.userInfo, await fetchAchievementMetrics())
  const weight = Number(weightForm.value.weight)
  if (!weight || Number.isNaN(weight) || weight <= 0) {
    uni.showToast({ title: '请输入正确的体重数值', icon: 'none' })
    return
  }

  const bodyFatValue = weightForm.value.bodyFat ? Number(weightForm.value.bodyFat) : null
  if (bodyFatValue !== null && (Number.isNaN(bodyFatValue) || bodyFatValue < 0)) {
    uni.showToast({ title: '体脂率格式不正确', icon: 'none' })
    return
  }
  if (!ensureRecordImagesReady()) {
    return
  }

  submitting.value = true
  try {
    const result = await createWeightRecord({
      weight,
      bodyFatRate: bodyFatValue,
      remark: weightForm.value.remark || null,
      syncToCircles: getSyncToCirclesPayload(),
      images: getRecordImagesPayload()
    })

    latestRecord.value = result.record
    weightForm.value = { weight: '', bodyFat: '', remark: '' }
    await userStore.fetchUserInfo()
    await handleRecordSubmitResult(result, '体重记录已保存', previousAchievementKeys)
  } catch (error) {
    console.error('提交体重记录失败:', error)
    showActionError(error, '保存失败')
  } finally {
    submitting.value = false
  }
}

async function submitDietRecord() {
  const previousAchievementKeys = captureAchievementState(userStore.userInfo, await fetchAchievementMetrics())
  const foodName = dietForm.value.name.trim()
  if (!foodName) {
    uni.showToast({ title: '请输入食物名称', icon: 'none' })
    return
  }

  const calories = Number(dietForm.value.calories)
  if (!calories || Number.isNaN(calories) || calories <= 0 || !Number.isInteger(calories)) {
    uni.showToast({ title: '请输入正确的热量数值', icon: 'none' })
    return
  }
  if (!ensureRecordImagesReady()) {
    return
  }

  submitting.value = true
  try {
    const result = await createFoodRecord({
      mealType: mealTypeMap[selectedMeal.value] || '0',
      foodName,
      calories,
      syncToCircles: getSyncToCirclesPayload(),
      remark: null,
      images: getRecordImagesPayload()
    })

    dietForm.value = { name: '', calories: '', portionMultiplier: '1' }
    dietCaloriesManual.value = false
    syncMealSelectionForCurrentTime()
    await userStore.fetchUserInfo()
    await handleRecordSubmitResult(result, '饮食记录已保存', previousAchievementKeys)
  } catch (error) {
    console.error('提交饮食记录失败:', error)
    showActionError(error, '保存失败')
  } finally {
    submitting.value = false
  }
}

async function submitExerciseRecord() {
  const previousAchievementKeys = captureAchievementState(userStore.userInfo, await fetchAchievementMetrics())
  const exerciseType = normalizeExerciseType(exerciseForm.value.type)
  if (!exerciseType) {
    uni.showToast({ title: '请输入运动类型', icon: 'none' })
    return
  }

  const durationMinutes = Number(exerciseForm.value.minutes)
  if (!durationMinutes || Number.isNaN(durationMinutes) || durationMinutes <= 0 || !Number.isInteger(durationMinutes)) {
    uni.showToast({ title: '请输入正确的运动时长', icon: 'none' })
    return
  }

  const caloriesBurned = Number(exerciseForm.value.calories)
  if (!caloriesBurned || Number.isNaN(caloriesBurned) || caloriesBurned <= 0 || !Number.isInteger(caloriesBurned)) {
    uni.showToast({ title: '请输入正确的消耗热量', icon: 'none' })
    return
  }
  if (!ensureRecordImagesReady()) {
    return
  }

  submitting.value = true
  try {
    const result = await createExerciseRecord({
      exerciseType,
      durationMinutes,
      caloriesBurned,
      syncToCircles: getSyncToCirclesPayload(),
      remark: null,
      images: getRecordImagesPayload()
    })

    rememberExerciseType(exerciseType)
    exerciseForm.value = { type: '', minutes: '', calories: '' }
    exerciseCaloriesManual.value = false
    await userStore.fetchUserInfo()
    await handleRecordSubmitResult(result, '运动记录已保存', previousAchievementKeys)
  } catch (error) {
    console.error('提交运动记录失败:', error)
    showActionError(error, '保存失败')
  } finally {
    submitting.value = false
  }
}

async function submitWaterRecord() {
  const previousAchievementKeys = captureAchievementState(userStore.userInfo, await fetchAchievementMetrics())
  const cups = Number(waterForm.value.cups)
  if (!cups || Number.isNaN(cups) || cups <= 0 || !Number.isInteger(cups)) {
    uni.showToast({ title: '请输入正确的饮水杯数', icon: 'none' })
    return
  }

  const ml = Number(waterForm.value.ml)
  if (!ml || Number.isNaN(ml) || ml <= 0 || !Number.isInteger(ml)) {
    uni.showToast({ title: '请输入正确的饮水毫升', icon: 'none' })
    return
  }
  if (!ensureRecordImagesReady()) {
    return
  }

  submitting.value = true
  try {
    const result = await createWaterRecord({
      cups,
      ml,
      images: getRecordImagesPayload()
    })

    waterForm.value = { cups: '1', ml: '250' }
    await userStore.fetchUserInfo()
    await handleRecordSubmitResult(result, '饮水记录已保存', previousAchievementKeys)
  } catch (error) {
    console.error('提交饮水记录失败:', error)
    showActionError(error, '保存失败')
  } finally {
    submitting.value = false
  }
}

function getSyncToCirclesPayload() {
  const activeIds = normalizeDraftCircleIds(selectedCircleIds.value)
  return activeIds.length ? JSON.stringify(activeIds) : null
}

async function handleRecordSubmitResult<T>(result: RecordSubmitPayload<T>, title: string, previousAchievementKeys?: string[]) {
  syncModalVisible.value = false
  if (previousAchievementKeys) {
    await notifyAchievementUnlocks(previousAchievementKeys, userStore.userInfo, await fetchAchievementMetrics())
  }
  if (currentShareFeedEnabled.value && result.draft) {
    await publishInlineFeedDraft(result.draft, title)
    return
  }
  await handleSubmitSuccess(title)
}

async function publishInlineFeedDraft(draft: RecordFeedDraftPayload, recordSavedTitle: string) {
  const selectedCircleIdsValue = normalizeDraftCircleIds(selectedCircleIds.value)
  const originCircleId = selectedCircleIdsValue[0] || null
  const syncCircleIds = originCircleId ? selectedCircleIdsValue.slice(1) : selectedCircleIdsValue
  const visibilityScope = originCircleId ? recordFeedVisibilityScope.value || draft.visibilityScope || 'public' : 'public'
  const content = draft.content || '记录更新'
  const images = Array.isArray(draft.images) ? draft.images : getRecordImageUrls()

  try {
    await publishFeed({
      feedType: draft.feedType || 'text',
      content,
      images,
      visibilityScope,
      originCircleId,
      syncCircleIds,
      sourceType: draft.sourceType || null,
      sourceId: draft.sourceId ?? null
    })
    resetRecordImages()
    uni.showToast({ title: '记录和动态已发布', icon: 'success' })
    if (showOnboardingContinuationAfterRecord()) {
      return
    }
    setTimeout(() => {
      navigateAfterSubmit()
    }, 500)
  } catch (error) {
    console.error('记录动态发布失败:', error)
    resetRecordImages()
    uni.showToast({ title: '记录已保存，动态发布失败', icon: 'none' })
    setTimeout(() => {
      navigateAfterSubmit()
    }, 800)
  }
}

async function handleSubmitSuccess(title: string) {
  resetRecordImages()
  uni.showToast({ title, icon: 'success' })
  if (showOnboardingContinuationAfterRecord()) {
    return
  }
  setTimeout(() => {
    navigateAfterSubmit()
  }, 800)
}

function showOnboardingContinuationAfterRecord() {
  const userId = userStore.userInfo?.userId
  if (!shouldPromptOnboardingGuide(userId) || getOnboardingGuideStep(userId) !== 'record') {
    return false
  }

  setOnboardingGuideStep(userId, 'explore')
  onboardingGuideStep.value = 'explore'
  onboardingGuideVisible.value = true
  return true
}

function closeOnboardingGuide() {
  onboardingGuideVisible.value = false
}

function disableOnboardingGuideAndClose() {
  disableOnboardingGuide(userStore.userInfo?.userId)
  onboardingGuideVisible.value = false
  uni.showToast({ title: '已关闭新手提示', icon: 'none' })
}

function handleOnboardingPrimary() {
  if (onboardingGuideStep.value === 'profile') {
    setOnboardingGuideStep(userStore.userInfo?.userId, 'record')
    onboardingGuideStep.value = 'record'
    onboardingGuideVisible.value = false
    uni.switchTab({ url: '/pages/profile/index' })
    return
  }

  if (onboardingGuideStep.value === 'record') {
    onboardingGuideVisible.value = false
    uni.showToast({ title: '先保存一条记录', icon: 'none' })
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

function navigateAfterSubmit() {
  if (returnMode.value === 'history') {
    uni.navigateBack({
      delta: 1,
      fail: () => {
        const historyUrl = buildReturnHistoryUrl()
        if (historyUrl) {
          uni.redirectTo({ url: historyUrl })
          return
        }
        uni.switchTab({ url: '/pages/index/index' })
      }
    })
    return
  }
  uni.switchTab({ url: '/pages/index/index' })
}

function confirmSyncSelection() {
  syncModalVisible.value = false
  if (selectedCircleIds.value.length) {
    enableShareFeedForCurrentTab()
  }
  uni.showToast({ title: currentTab.value === 'habit' ? '已选择同步圈子' : '已确认关联圈子', icon: 'success' })
}

function saveLocalOnly() {
  if (hasRequiredCircle.value) {
    ensureRequiredCircleSelection()
    uni.showToast({ title: '当前圈子为必选同步项', icon: 'none' })
    return
  }
  setShareFeedForCurrentTab(false)
  syncModalVisible.value = false
  uni.showToast({ title: '本次记录只保留个人数据', icon: 'none' })
}

function buildReturnHistoryUrl() {
  if (returnMode.value !== 'history') {
    return ''
  }
  const queryParts: string[] = []
  if (returnHistorySource.value !== 'default') {
    queryParts.push(`source=${encodeURIComponent(returnHistorySource.value)}`)
  }
  if (returnHistoryType.value !== 'all') {
    queryParts.push(`type=${encodeURIComponent(returnHistoryType.value)}`)
  }
  if (returnHistoryRange.value !== 'all') {
    queryParts.push(`range=${encodeURIComponent(returnHistoryRange.value)}`)
  }
  return queryParts.length
    ? `/pages/record-history/index?${queryParts.join('&')}`
    : '/pages/record-history/index'
}

function goHabitPage() {
  uni.navigateTo({ url: '/pages/habit/index' })
}

async function handleHabitTaskTap(task: HabitItem) {
  if (!task.checkedToday && !ensureHabitTaskImagesReady(task.id)) {
    return
  }
  const previousAchievementKeys = captureAchievementState(userStore.userInfo, await fetchAchievementMetrics())
  try {
    const updated = task.checkedToday ? await undoHabitCheckin(task.id) : await checkinHabit(task.id, { images: getHabitTaskImageUrls(task.id) })
    habitTasks.value = habitTasks.value.map((item) => (item.id === updated.id ? updated : item))
    await userStore.fetchUserInfo()
    uni.showToast({ title: updated.checkedToday ? '打卡成功' : '已撤销打卡', icon: 'success' })
    await notifyAchievementUnlocks(previousAchievementKeys, userStore.userInfo, await fetchAchievementMetrics())
    if (updated.checkedToday) {
      if (habitShareFeedEnabled.value) {
        await publishHabitInlineDraft(updated)
      }
      resetHabitTaskImages(task.id)
      showOnboardingContinuationAfterRecord()
    } else {
      dismissHabitSharePrompt()
      resetHabitTaskImages(task.id)
    }
  } catch (error) {
    console.error('切换习惯打卡失败:', error)
    showActionError(error, task.checkedToday ? '撤销失败' : '打卡失败')
  }
}

async function publishHabitInlineDraft(task: HabitItem) {
  const draft = buildHabitDraft(task)
  const selectedCircleIdsValue = normalizeDraftCircleIds(selectedCircleIds.value)
  const originCircleId = selectedCircleIdsValue[0] || null
  const syncCircleIds = originCircleId ? selectedCircleIdsValue.slice(1) : selectedCircleIdsValue
  const visibilityScope = originCircleId ? recordFeedVisibilityScope.value || draft.visibilityScope : 'public'
  const content = draft.content
  const images = normalizeHabitImageList(task.images)

  try {
    await publishFeed({
      feedType: 'habit',
      content,
      images,
      visibilityScope,
      originCircleId,
      syncCircleIds,
      sourceType: draft.sourceType,
      sourceId: draft.sourceId
    })
    resetHabitTaskImages(task.id)
    uni.showToast({ title: '打卡动态已发布', icon: 'success' })
  } catch (error) {
    console.error('习惯动态发布失败:', error)
    uni.showToast({ title: '打卡已保存，动态发布失败', icon: 'none' })
  }
}

function canUseCircle(circle: MyCircleCard) {
  return circle.status === '0' && !circle.readOnly
}

function isCircleSelectableForSync(circleId: number) {
  const circle = myCircles.value.find((item) => item.id === circleId)
  if (circle) {
    return canUseCircle(circle)
  }
  return isRequiredCircle(circleId)
}

function normalizeDraftCircleIds(circleIds?: number[] | null) {
  const selected = Array.isArray(circleIds) ? circleIds : []
  const filtered = selected.filter((circleId, index, array) =>
    array.indexOf(circleId) === index && isCircleSelectableForSync(circleId)
  )
  return normalizeCircleSelection(filtered)
}

function openFeedDraft(draft: RecordFeedDraftPayload, context: { navigateAfterClose: boolean }) {
  const normalizedCircleIds = normalizeDraftCircleIds(draft.syncCircleIds)
  draftFeedType.value = draft.feedType || 'text'
  draftContent.value = draft.content || ''
  draftVisibilityScope.value = draft.visibilityScope || 'public'
  draftSelectedCircleIds.value = normalizedCircleIds
  draftImages.value = []
  draftSourceType.value = draft.sourceType || null
  draftSourceId.value = draft.sourceId ?? null
  draftContext.value = context
  feedDraftVisible.value = true
}

function resetFeedDraft() {
  feedDraftVisible.value = false
  feedDraftSubmitting.value = false
  feedDraftUploading.value = false
  draftFeedType.value = 'text'
  draftContent.value = ''
  draftVisibilityScope.value = 'public'
  draftSelectedCircleIds.value = []
  draftImages.value = []
  draftSourceType.value = null
  draftSourceId.value = null
  draftContext.value = { navigateAfterClose: false }
}

function closeFeedDraft() {
  const shouldNavigate = draftContext.value.navigateAfterClose
  resetFeedDraft()
  if (shouldNavigate) {
    uni.showToast({ title: '记录已保存，动态未发布', icon: 'none' })
    setTimeout(() => {
      navigateAfterSubmit()
    }, 240)
  }
}

function toggleDraftCircle(circleId: number) {
  if (isRequiredCircle(circleId)) {
    uni.showToast({ title: '当前圈子为必选项', icon: 'none' })
    return
  }
  const circle = myCircles.value.find((item) => item.id === circleId)
  if (!circle || !canUseCircle(circle)) {
    uni.showToast({ title: '当前圈子暂不可同步', icon: 'none' })
    return
  }
  if (draftSelectedCircleIds.value.includes(circleId)) {
    draftSelectedCircleIds.value = normalizeCircleSelection(draftSelectedCircleIds.value.filter((item) => item !== circleId))
    return
  }
  draftSelectedCircleIds.value = normalizeCircleSelection([...draftSelectedCircleIds.value, circleId])
}

async function submitFeedDraft() {
  const content = draftContent.value.trim()
  const selectedCircleIdsValue = normalizeDraftCircleIds(draftSelectedCircleIds.value)
  const originCircleId = selectedCircleIdsValue[0] || null
  const syncCircleIds = originCircleId ? selectedCircleIdsValue.slice(1) : selectedCircleIdsValue

  if (!content && !draftImages.value.length) {
    uni.showToast({ title: '请先填写动态内容或上传图片', icon: 'none' })
    return
  }
  if (feedDraftUploading.value) {
    uni.showToast({ title: '图片还在上传中，请稍后再发', icon: 'none' })
    return
  }
  if (hasFailedImageAssets(draftImages.value)) {
    uni.showToast({ title: '有图片上传失败，请删除后重试', icon: 'none' })
    return
  }
  if (draftVisibilityScope.value === 'circle' && !originCircleId) {
    uni.showToast({ title: '仅圈子可见时请至少选择一个圈子', icon: 'none' })
    return
  }

  feedDraftSubmitting.value = true
  try {
    await publishFeed({
      feedType: draftFeedType.value,
      content,
      images: draftImages.value.map((item) => item.url).filter(Boolean),
      visibilityScope: draftVisibilityScope.value,
      originCircleId,
      syncCircleIds,
      sourceType: draftSourceType.value,
      sourceId: draftSourceId.value
    })
    const shouldNavigate = draftContext.value.navigateAfterClose
    resetFeedDraft()
    uni.showToast({ title: '动态已发布', icon: 'success' })
    if (shouldNavigate) {
      setTimeout(() => {
        navigateAfterSubmit()
      }, 260)
    }
  } catch (error: any) {
    uni.showToast({ title: error?.message || '动态发布失败', icon: 'none' })
  } finally {
    feedDraftSubmitting.value = false
  }
}

async function chooseDraftImages() {
  if (feedDraftUploading.value || draftImages.value.length >= 9) {
    return
  }
  await onShowReloadGuard.runWhileSuspended(async () => {
    try {
      await chooseAndUploadImages({
        currentCount: draftImages.value.length,
        upload: uploadFeedImage,
        fallbackErrorMessage: '动态图片上传失败',
        setUploading: (uploading) => {
          feedDraftUploading.value = uploading
        },
        onLocalAssetsSelected: (assets) => {
          draftImages.value = [...draftImages.value, ...assets].slice(0, 9)
        },
        onAssetUpdated: (asset) => {
          draftImages.value = replaceUploadedImageAsset(draftImages.value, asset)
        }
      })
    } catch (error: any) {
      uni.showToast({ title: error?.message || '动态图片上传失败', icon: 'none' })
    }
  })
}

function removeDraftImage(index: number) {
  draftImages.value = draftImages.value.filter((_, currentIndex) => currentIndex !== index)
}

async function retryDraftImage(index: number) {
  const image = draftImages.value[index]
  if (!image || image.uploading) {
    return
  }
  try {
    await retryUploadedImageAsset({
      asset: image,
      upload: uploadFeedImage,
      fallbackErrorMessage: '动态图片上传失败',
      setUploading: (uploading) => {
        feedDraftUploading.value = uploading
      },
      onAssetUpdated: (asset) => {
        draftImages.value = replaceUploadedImageAsset(draftImages.value, asset)
      }
    })
  } catch (error: any) {
    uni.showToast({ title: error?.message || '动态图片上传失败', icon: 'none' })
  }
}

function buildHabitDraft(task: HabitItem): RecordFeedDraftPayload {
  return {
    feedType: 'habit',
    content: `${task.icon || '✅'} 今天完成了「${task.name}」打卡，继续保持。`,
    visibilityScope: recordFeedVisibilityScope.value,
    originCircleId: null,
    syncCircleIds: [],
    sourceType: 'habit_checkin',
    sourceId: task.checkinId || task.id,
    images: normalizeHabitImageList(task.images)
  }
}

function openHabitFeedDraft() {
  const draft = pendingHabitDraft.value
  if (!draft) {
    return
  }
  habitSharePromptVisible.value = false
  pendingHabitDraft.value = null
  openFeedDraft(draft, { navigateAfterClose: false })
}

function dismissHabitSharePrompt() {
  habitSharePromptVisible.value = false
  pendingHabitDraft.value = null
}

</script>

<style scoped lang="scss">
$primary: var(--wc-primary);
$bg-page: var(--wc-bg);
$card-bg: var(--wc-surface-strong);
$text-main: var(--wc-text);
$text-sub: var(--wc-text-soft);

@use '../../styles/flow-button.scss';

.record-container {
  height: 100vh;
  background: $bg-page;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.type-tabs {
  display: flex;
  padding: 0 24rpx 16rpx;
  box-sizing: border-box;
  flex-shrink: 0;
  background: rgba(247, 249, 252, 0.88);
  position: sticky;
  top: 0;
  z-index: 20;
  backdrop-filter: blur(24rpx);
  border-bottom: 1px solid var(--wc-line);
}

.type-tabs-body {
  min-height: 100%;
  width: 100%;
  min-width: 0;
  display: flex;
  align-items: center;
}

.type-tabs-shell {
  display: flex;
  align-items: center;
  gap: 12rpx;
  width: 100%;
  min-width: 0;
}

.type-tabs-rail {
  flex: 1;
  min-width: 0;
  gap: 6rpx;
  padding: 6rpx;
  align-items: center;
  overflow: hidden;
}

.type-tab {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  flex: 1 1 0;
  min-width: 0;
  gap: 4rpx;
  padding-left: 0;
  padding-right: 0;
  white-space: nowrap;
  box-sizing: border-box;
}

.type-tab-icon {
  font-size: 22rpx;
  line-height: 1;
  flex-shrink: 0;
}

.type-tab-label {
  display: block;
  min-width: 0;
  max-width: 100%;
  overflow: hidden;
  font-size: 22rpx;
  line-height: 1.2;
  white-space: nowrap;
}

@media screen and (max-width: 430px) {
  .type-tab {
    flex-direction: column;
    gap: 2rpx;
    padding-top: 6rpx;
    padding-bottom: 6rpx;
  }

  .type-tab-icon,
  .type-tab-label {
    font-size: 20rpx;
  }
}

.content {
  flex: 1;
  min-height: 0;
  padding: 30rpx;
  padding-bottom: 220rpx;
  box-sizing: border-box;
}

.summary-card,
.form-card,
.hint-card,
.sync-trigger,
.draft-trigger,
.share-scope-panel {
  background: $card-bg;
  border-radius: 32rpx;
  padding: 28rpx;
  margin-bottom: 24rpx;
  border: 1px solid var(--wc-line);
  box-shadow: var(--wc-shadow-soft);
}

.summary-card {
  background: linear-gradient(135deg, rgba(74, 144, 226, 0.12), rgba(255, 255, 255, 0.98));
}

.summary-card--error {
  background: linear-gradient(135deg, rgba(255, 244, 224, 0.92), rgba(255, 255, 255, 0.98));
}

.summary-value-row {
  display: flex;
  flex-direction: column;
  gap: 8rpx;
}

.summary-value {
  font-size: 44rpx;
  font-weight: 700;
}

.summary-value--compact {
  font-size: 34rpx;
}

.summary-sub {
  font-size: 22rpx;
  color: $text-sub;
}

.summary-action {
  margin-top: 18rpx;
  width: fit-content;
}

.form-group {
  margin-bottom: 24rpx;
}

.form-group:last-child {
  margin-bottom: 0;
}

.unit {
  font-size: 26rpx;
  color: $text-sub;
}

.hint-card {
  font-size: 22rpx;
  line-height: 1.6;
  color: $text-sub;
}

.meal-tags {
  width: 100%;
}

.meal-tag {
}

.food-estimator {
  margin-top: 18rpx;
  padding: 18rpx;
  border-radius: 20rpx;
  background: rgba(74, 144, 226, 0.08);
  border: 1px solid rgba(74, 144, 226, 0.16);
}

.food-estimator__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 18rpx;
  margin-bottom: 14rpx;
}

.food-estimator__label {
  font-size: 22rpx;
  font-weight: 600;
  color: $text-sub;
}

.food-estimator__portion {
  display: flex;
  align-items: center;
  gap: 10rpx;
  font-size: 22rpx;
  color: $text-sub;
}

.food-estimator__portion-input {
  width: 92rpx;
  height: 52rpx;
  padding: 0 14rpx;
  box-sizing: border-box;
  border-radius: 14rpx;
  background: #fff;
  border: 1px solid var(--wc-line);
  color: $text-main;
  text-align: center;
}

.food-estimator__scroll {
  width: 100%;
  white-space: nowrap;
}

.food-estimator__options {
  display: inline-flex;
  gap: 12rpx;
  padding: 2rpx 2rpx 6rpx;
}

.food-estimator__chip {
  display: inline-flex;
  flex-direction: column;
  justify-content: center;
  gap: 4rpx;
  min-width: 132rpx;
  min-height: 76rpx;
  padding: 0 18rpx;
  border-radius: 18rpx;
  background: rgba(255, 255, 255, 0.96);
  border: 1px solid var(--wc-line);
  color: $text-main;
  font-size: 24rpx;
  line-height: 1.25;
  flex-shrink: 0;
}

.food-estimator__chip text:last-child {
  font-size: 20rpx;
  color: $text-sub;
}

.food-estimator__chip.active {
  border-color: var(--wc-primary);
  color: var(--wc-primary);
  background: #fff;
}

.exercise-dictionary {
  margin-top: 18rpx;
}

.exercise-dictionary__label {
  display: block;
  margin-bottom: 12rpx;
  font-size: 22rpx;
  font-weight: 600;
  color: $text-sub;
}

.exercise-type-scroll {
  width: 100%;
  white-space: nowrap;
}

.exercise-type-options {
  display: inline-flex;
  gap: 12rpx;
  padding: 2rpx 2rpx 6rpx;
}

.exercise-type-chip {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  min-height: 54rpx;
  padding: 0 22rpx;
  border-radius: 999rpx;
  border: 1px solid var(--wc-line);
  background: rgba(255, 255, 255, 0.92);
  color: $text-sub;
  font-size: 22rpx;
  font-weight: 600;
  line-height: 54rpx;
}

.exercise-type-chip.active {
  border-color: rgba(74, 144, 226, 0.24);
  background: var(--wc-primary-soft);
  color: var(--wc-primary-strong);
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20rpx;
}

.task-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16rpx;
  margin-bottom: 20rpx;
}

.habit-task-list {
  display: flex;
  flex-direction: column;
  gap: 18rpx;
}

.habit-checkin-images {
  margin-top: 20rpx;
  padding-top: 20rpx;
  border-top: 1px solid rgba(148, 163, 184, 0.16);
}

.habit-checkin-images__header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 18rpx;
}

.habit-checkin-images__copy {
  display: flex;
  flex: 1;
  min-width: 0;
  flex-direction: column;
  gap: 6rpx;
}

.habit-checkin-images__title,
.habit-checkin-images__desc {
  display: block;
}

.habit-checkin-images__title {
  font-size: 24rpx;
  font-weight: 700;
  color: $text-main;
}

.habit-checkin-images__desc {
  font-size: 21rpx;
  line-height: 1.55;
  color: $text-sub;
}

.habit-checkin-images__action {
  flex-shrink: 0;
}

.habit-checkin-image-grid {
  gap: 12rpx;
  margin-top: 16rpx;
}

.sync-trigger {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 24rpx;
  color: $text-sub;
}

.draft-trigger__row,
.draft-section__header,
.draft-circle-item,
.habit-share-sheet__actions {
  display: flex;
}

.draft-trigger__row,
.draft-section__header,
.draft-circle-item {
  justify-content: space-between;
  gap: 20rpx;
}

.draft-trigger__row,
.draft-section__header {
  align-items: flex-start;
}

.draft-circle-item {
  align-items: center;
}

.draft-trigger__copy,
.draft-section__copy,
.draft-circle-item__copy {
  display: flex;
  flex-direction: column;
  gap: 8rpx;
  flex: 1;
  min-width: 0;
}

.draft-trigger__row > .required-switch,
.draft-section__header > .flow-btn,
.draft-section__header > .draft-section__meta,
.draft-circle-item__check {
  flex-shrink: 0;
}

.draft-trigger__title,
.draft-title,
.habit-share-sheet__title,
.draft-trigger__desc,
.draft-subtitle,
.draft-section__meta,
.draft-circle-item__name,
.draft-circle-item__desc,
.habit-share-sheet__desc,
.sync-tip,
.field-tip {
  display: block;
}

.draft-trigger__title,
.draft-title,
.habit-share-sheet__title {
  font-size: 28rpx;
  font-weight: 700;
  color: $text-main;
}

.draft-trigger__desc,
.draft-subtitle,
.draft-section__meta,
.draft-circle-item__desc,
.habit-share-sheet__desc,
.sync-tip,
.field-tip {
  font-size: 22rpx;
  color: $text-sub;
  line-height: 1.6;
}

.draft-trigger__summary {
  margin-top: 18rpx;
  padding-top: 18rpx;
  border-top: 1px solid rgba(148, 163, 184, 0.16);
  font-size: 22rpx;
  color: $text-sub;
  line-height: 1.6;
}

.inline-draft {
  margin-top: 22rpx;
  padding-top: 6rpx;
  border-top: 1px solid rgba(148, 163, 184, 0.16);
}

.inline-draft .draft-section:last-child {
  padding-bottom: 0;
  border-bottom: 0;
}

.required-switch {
  min-width: 110rpx;
  padding: 12rpx 18rpx;
  border-radius: 999rpx;
  background: rgba(148, 163, 184, 0.16);
  color: $text-sub;
  text-align: center;
  font-size: 22rpx;
  font-weight: 600;
}

.required-switch.active {
  background: rgba(74, 144, 226, 0.14);
  color: $primary;
}

.share-scope-panel {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 22rpx;
  background: linear-gradient(135deg, rgba(74, 144, 226, 0.1), rgba(255, 255, 255, 0.98));
  border-color: rgba(74, 144, 226, 0.16);
}

.share-scope-panel__copy {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 8rpx;
}

.share-scope-panel__label,
.share-scope-panel__desc {
  display: block;
}

.share-scope-panel__label {
  color: $text-main;
  font-size: 26rpx;
  font-weight: 750;
}

.share-scope-panel__desc {
  color: $text-sub;
  font-size: 22rpx;
  line-height: 1.55;
}

.share-scope-toggle {
  flex-shrink: 0;
  display: inline-flex;
  align-items: center;
  gap: 8rpx;
  padding: 6rpx;
  border-radius: 999rpx;
  background: rgba(255, 255, 255, 0.88);
  border: 1px solid rgba(74, 144, 226, 0.12);
}

.share-scope-chip {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 76rpx;
  height: 52rpx;
  padding: 0 18rpx;
  border-radius: 999rpx;
  color: $text-sub;
  font-size: 22rpx;
  font-weight: 700;
}

.share-scope-chip.active {
  color: #ffffff;
  background: $primary;
  box-shadow: 0 8rpx 18rpx rgba(74, 144, 226, 0.18);
}

.share-scope-chip.disabled {
  color: rgba(100, 116, 139, 0.46);
}

.sync-mask {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.45);
  display: flex;
  align-items: flex-end;
  z-index: 1100;
}

.sync-modal {
  width: 100%;
  background: rgba(255, 255, 255, 0.98);
  border-top-left-radius: 40rpx;
  border-top-right-radius: 40rpx;
  padding: 30rpx 30rpx calc(30rpx + env(safe-area-inset-bottom));
  box-shadow: 0 -10rpx 30rpx rgba(16, 24, 40, 0.12);
}

.sync-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 24rpx;
}

.sync-tip {
  display: block;
  margin-bottom: 18rpx;
}

.sync-title {
  font-size: 30rpx;
  font-weight: 700;
}

.sync-close {
  font-size: 34rpx;
  color: var(--wc-text-faint);
}

.sync-list {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.sync-circle-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 24rpx;
  border-radius: 24rpx;
  background: var(--wc-surface-muted);
  border: 2rpx solid transparent;
}

.sync-circle-item.selected {
  border-color: $primary;
  background: var(--wc-primary-soft);
}

.sync-circle-item.locked {
  border-color: var(--wc-success);
  background: rgba(43, 167, 121, 0.1);
}

.sync-circle-item.disabled {
  opacity: 0.58;
}

.sync-circle-name {
  display: block;
  font-size: 28rpx;
  font-weight: 600;
  margin-bottom: 6rpx;
}

.sync-circle-desc {
  display: block;
  font-size: 22rpx;
  color: $text-sub;
}

.sync-check {
  font-size: 30rpx;
  color: $primary;
}

.sync-empty {
  padding: 28rpx;
  border-radius: 24rpx;
  background: var(--wc-surface-muted);
  color: $text-sub;
  text-align: center;
  font-size: 24rpx;
}

.sync-empty--error {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16rpx;
}

.sync-actions {
  display: flex;
  flex-direction: column;
  gap: 14rpx;
  margin-top: 24rpx;
}

.habit-share-mask,
.draft-mask {
  position: fixed;
  inset: 0;
  z-index: 1110;
  background: rgba(15, 23, 42, 0.42);
  display: flex;
  align-items: flex-end;
}

.habit-share-sheet,
.draft-modal {
  width: 100%;
  background: rgba(255, 255, 255, 0.98);
  border-top-left-radius: 40rpx;
  border-top-right-radius: 40rpx;
  box-shadow: 0 -10rpx 30rpx rgba(16, 24, 40, 0.12);
}

.habit-share-sheet {
  padding: 32rpx 30rpx calc(30rpx + env(safe-area-inset-bottom));
}

.habit-share-sheet__title {
  display: block;
  margin-bottom: 10rpx;
}

.habit-share-sheet__desc {
  display: block;
}

.habit-share-sheet__actions {
  flex-direction: column;
  gap: 14rpx;
  margin-top: 24rpx;
}

.draft-modal {
  padding: 30rpx 30rpx calc(30rpx + env(safe-area-inset-bottom));
}

.draft-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 20rpx;
}

.draft-subtitle {
  margin-top: 8rpx;
}

.draft-scroll {
  margin-top: 22rpx;
}

.draft-section {
  padding: 26rpx 0;
  border-bottom: 1px solid rgba(148, 163, 184, 0.14);
}

.draft-section:first-child {
  padding-top: 0;
}

.draft-section:last-child {
  border-bottom: 0;
  padding-bottom: 10rpx;
}

.draft-circle-list {
  display: flex;
  flex-direction: column;
  gap: 14rpx;
  margin-top: 18rpx;
}

.draft-circle-item {
  padding: 22rpx 24rpx;
  border-radius: 24rpx;
  background: var(--wc-surface-muted);
  border: 2rpx solid transparent;
}

.draft-circle-item.selected {
  border-color: $primary;
  background: var(--wc-primary-soft);
}

.draft-circle-item__name {
  font-size: 26rpx;
  font-weight: 700;
  color: $text-main;
}

.draft-circle-item__check {
  font-size: 30rpx;
  color: $primary;
}

.draft-textarea {
  margin-top: 18rpx;
}

.draft-image-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16rpx;
  margin-top: 18rpx;
}

.draft-image-item {
  position: relative;
  border-radius: 24rpx;
  overflow: hidden;
  background: var(--wc-surface-muted);
  aspect-ratio: 1 / 1;
}

.draft-image-preview {
  width: 100%;
  height: 100%;
}

.upload-image-status {
  position: absolute;
  inset: 0;
  z-index: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(15, 23, 42, 0.42);
}

.upload-image-status--error {
  background: rgba(185, 28, 28, 0.32);
}

.upload-image-badge {
  padding: 10rpx 18rpx;
  border-radius: 999rpx;
  background: rgba(15, 23, 42, 0.72);
  color: #ffffff;
  font-size: 22rpx;
  font-weight: 600;
  line-height: 1;
}

.upload-image-badge--error {
  background: #ef4444;
}

.upload-image-retry {
  position: absolute;
  top: 10rpx;
  right: 10rpx;
  z-index: 3;
  padding: 8rpx 14rpx;
  border-radius: 999rpx;
  background: var(--wc-primary);
  color: #ffffff;
  font-size: 20rpx;
  font-weight: 700;
  line-height: 1;
  box-shadow: 0 8rpx 18rpx rgba(47, 128, 237, 0.24);
}

.draft-image-remove {
  position: absolute;
  top: 10rpx;
  left: 10rpx;
  z-index: 2;
  width: 42rpx;
  height: 42rpx;
  border-radius: 50%;
  background: rgba(15, 23, 42, 0.72);
  color: #ffffff;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 22rpx;
}

.draft-actions {
  display: flex;
  flex-direction: column;
  gap: 14rpx;
  margin-top: 18rpx;
}

.page-bottom-space {
  height: 40rpx;
}
</style>
