<template>
  <view class="circle-container wc-page-enter">
    <view class="header wc-tab-header" :style="{ paddingTop: `${safeTop}px` }">
      <view class="header-body wc-tab-header__body" :style="{ minHeight: `${topBarHeight - safeTop}px`, paddingRight: `${menuButtonSafeRight}px` }">
        <view class="header-left">
          <view class="header-tabs">
            <text
              class="header-tab"
              :class="{ active: currentTab === 'discover' }"
              @tap="switchTab('discover')"
            >发现</text>
            <text
              class="header-tab"
              :class="{ active: currentTab === 'my' }"
              @tap="switchTab('my')"
            >我的圈子</text>
          </view>
        </view>
        <view class="header-right wc-icon-button wc-icon-button--soft wc-pressable" @tap="goCircleAll">🔍</view>
      </view>
    </view>

    <scroll-view
      v-if="currentTab === 'discover'"
      class="content wc-tab-scroll wc-tab-scroll--top-gap wc-section-enter"
      scroll-y
      :style="{ height: `calc(100vh - ${topBarHeight}px)` }"
    >
      <view class="discover-hero">
        <view class="discover-hero-copy">
          <text class="discover-kicker">圈子发现</text>
          <text class="discover-title">{{ discoverHeadline }}</text>
          <text class="discover-subtitle">{{ discoverSubtitle }}</text>
        </view>
        <view class="discover-hero-side">
          <view class="hero-avatar-stack">
            <view
              v-for="(circle, index) in heroPreviewCircles"
              :key="`hero-${circle.id}`"
              class="hero-avatar"
              :class="circleToneClass(index)"
            >
              {{ initialText(circle.name, '圈') }}
            </view>
          </view>
          <view class="hero-stats">
            <view class="hero-stat">
              <text class="hero-stat-value">{{ publicCircles.length }}</text>
              <text class="hero-stat-label">可逛圈子</text>
            </view>
            <view class="hero-stat">
              <text class="hero-stat-value">{{ totalVisibleMembers }}</text>
              <text class="hero-stat-label">圈友在打卡</text>
            </view>
          </view>
        </view>
      </view>

      <app-section-header title="热门圈子" subtitle="挑一个今天就能加入的" action-text="查看全部 >" @actionTap="goCircleAll" />

      <view v-if="discoverLoading" class="loading-block">圈子加载中...</view>
      <scroll-view v-else-if="publicCircles.length" class="circle-scroll" scroll-x>
        <view
          v-for="(circle, index) in publicCircles"
          :key="circle.id"
          class="circle-card wc-pressable"
          :class="circleToneClass(index)"
          @tap="goCircleDetail(circle.id)"
        >
          <view class="circle-cover" :class="circleCoverClass(circle.coverUrl)">
            <resolved-image
              class="circle-cover-image"
              v-if="isRemoteCircleCover(circle.coverUrl)"
              :src="normalizeCircleCoverUrl(circle.coverUrl)"
              mode="aspectFill"
            />
            <view class="circle-bg orb-one"></view>
            <view class="circle-bg orb-two"></view>
            <view class="circle-cover-top">
              <view class="circle-cover-badges">
                <text class="wc-badge wc-badge--dark">{{ circle.categoryName || circleTypeLabelText(circle.type) }}</text>
                <text class="wc-badge wc-badge--glass">{{ circleDurationText(circle.durationDays) }}</text>
              </view>
              <view class="circle-emblem">{{ circleEmblem(circle) }}</view>
            </view>
            <view class="circle-cover-bottom">
              <text class="circle-cover-kicker">{{ circle.joined ? '挑战进行中' : '现在可加入' }}</text>
              <text class="circle-cover-caption">{{ circleHighlightText(circle) }}</text>
            </view>
          </view>
          <view class="circle-info">
            <view class="circle-name-row">
              <text class="circle-name">{{ circle.name }}</text>
              <text class="circle-status">{{ circle.joined ? '已加入' : '招募中' }}</text>
            </view>
            <text class="circle-desc">{{ circle.description || '一起记录体重和打卡，坚持完成挑战。' }}</text>
            <view class="circle-stat-grid">
              <view class="circle-stat-chip">
                <text class="circle-stat-value">{{ formatMemberCount(circle.memberCount) }}</text>
                <text class="circle-stat-label">圈友</text>
              </view>
              <view class="circle-stat-chip">
                <text class="circle-stat-value">{{ circleDurationStatText(circle.durationDays) }}</text>
                <text class="circle-stat-label">节奏</text>
              </view>
              <view class="circle-stat-chip">
                <text class="circle-stat-value">{{ circleDepositStatText(circle.depositRequired) }}</text>
                <text class="circle-stat-label">押金</text>
              </view>
            </view>
            <view class="circle-meta-row">
              <text class="wc-badge wc-badge--soft">{{ circleDepositText(circle.depositRequired) }}</text>
              <text class="wc-badge wc-badge--soft">{{ circle.categoryName || circleTypeLabelText(circle.type) }}</text>
            </view>
          </view>
          <view class="circle-footer">
            <view class="circle-footer-copy">
              <text class="circle-meta">{{ circle.joined ? '你已经在这个圈子里，可以继续打卡互动。' : '打开详情查看规则、成员和最新动态。' }}</text>
            </view>
            <text class="circle-join wc-signal-pill wc-signal-pill--primary">{{ circle.joined ? '继续查看' : '查看详情' }}</text>
          </view>
        </view>
      </scroll-view>
      <view v-else class="section-padding">
        <app-empty-state
          :icon="publicCirclesErrorMessage ? '⚠️' : '⭕'"
          :title="publicCirclesErrorMessage ? '圈子加载失败' : '暂无可展示的公开圈子'"
          :description="publicCirclesErrorMessage || '后端返回公开圈子后，这里会自动展示真实列表。'"
        >
          <button
            v-if="publicCirclesErrorMessage"
            class="retry-btn flow-btn flow-btn--secondary flow-btn--compact wc-pressable"
            @tap="loadDiscoverData"
          >
            重新整理
          </button>
        </app-empty-state>
      </view>

      <app-section-header title="公开动态" :subtitle="`${recommendedFeeds.length} 条圈友日常`" />

      <view v-if="discoverLoading" class="loading-block">动态加载中...</view>
      <view v-else-if="recommendedFeeds.length" class="feed-list">
        <view
          v-for="(feed, index) in recommendedFeeds"
          :key="feed.id"
          class="feed-card wc-pressable"
          :class="feedToneClass(index)"
          @tap="goFeedDetail(feed)"
        >
          <view class="feed-topline">
            <text v-if="feed.isFeatured" class="wc-badge wc-badge--warning">精选</text>
            <text class="wc-badge wc-badge--primary">公开动态</text>
            <text class="wc-badge wc-badge--soft">{{ feedTypeText(feed.feedType) }}</text>
            <text v-if="feed.circleName" class="wc-badge wc-badge--soft">同步至 {{ feed.circleName }}</text>
          </view>
          <view class="feed-header">
            <resolved-image
              class="avatar"
              :src="feed.userAvatar"
              mode="aspectFill"
              shape="avatar"
              fallback-class="avatar avatar-fallback"
              :fallback-text="initialText(feed.userNickname)"
              @tap.stop="goUserProfile(feed.userId)"
            />
            <view class="user-info" @tap.stop="goUserProfile(feed.userId)">
              <text class="user-name">{{ feed.userNickname || '圈友' }}</text>
              <text class="time">{{ circleFeedTimeText(feed.createdAt) }} · {{ feed.visibilityScope === 'public' ? '公开可见' : '圈内可见' }}</text>
            </view>
            <view class="feed-signal wc-signal-pill">{{ feedSignalText(feed) }}</view>
          </view>
          <view class="feed-story">
            <view class="feed-story-mark">{{ feedGlyph(feed.feedType) }}</view>
            <text class="feed-content">{{ feed.content || '这条动态还没有文字内容。' }}</text>
          </view>
          <view v-if="feed.images?.length" class="feed-images" :class="`grid-${Math.min(feed.images.length, 3)}`">
            <resolved-image
              class="feed-img"
              v-for="(img, idx) in feed.images.slice(0, 3)"
              :key="`${feed.id}-${idx}`"
              :src="img"
              mode="aspectFill"
              :preview-list="feed.images"
              :preview-index="idx"
            />
          </view>
          <view class="feed-actions">
            <view class="action-btn wc-reaction-chip" :class="{ active: feed.likedByMe }" @tap.stop="handleFeedLike(feed)">
              <text>{{ feed.likedByMe ? '❤️' : '🤍' }}</text>
              <text>{{ feed.likesCount || 0 }}</text>
            </view>
            <view class="action-btn wc-reaction-chip" @tap.stop="goFeedDetail(feed)">
              <text>💬</text>
              <text>{{ feed.commentsCount || 0 }}</text>
            </view>
          </view>
        </view>
      </view>
      <view v-else class="section-padding">
        <app-empty-state
          :icon="recommendedFeedsErrorMessage ? '⚠️' : '🗂️'"
          :title="recommendedFeedsErrorMessage ? '动态加载失败' : '还没有推荐动态'"
          :description="recommendedFeedsErrorMessage || '当前只显示后端返回的真实圈子动态。'"
        >
          <button
            v-if="recommendedFeedsErrorMessage"
            class="retry-btn flow-btn flow-btn--secondary flow-btn--compact wc-pressable"
            @tap="loadDiscoverData"
          >
            重新整理
          </button>
        </app-empty-state>
      </view>

      <view class="page-bottom-space"></view>
    </scroll-view>

    <scroll-view
      v-else
      class="content wc-tab-scroll wc-tab-scroll--top-gap wc-section-enter"
      scroll-y
      :style="{ height: `calc(100vh - ${topBarHeight}px)` }"
    >
      <view class="my-circle-hero">
        <view class="my-circle-copy">
          <text class="my-circle-kicker">我的圈子</text>
          <text class="my-circle-title">把常去的挑战放在手边</text>
          <text class="my-circle-subtitle">主圈子、私密陪跑和公开挑战都在这里统一管理。</text>
        </view>
        <view class="my-circle-stats">
          <view class="my-circle-stat">
            <text class="my-circle-stat-value">{{ myCircles.length }}</text>
            <text class="my-circle-stat-label">已加入</text>
          </view>
          <view class="my-circle-stat">
            <text class="my-circle-stat-value">{{ myPrivateCircles.length }}</text>
            <text class="my-circle-stat-label">私密</text>
          </view>
          <view class="my-circle-stat">
            <text class="my-circle-stat-value">{{ myPublicCircles.length }}</text>
            <text class="my-circle-stat-label">公开</text>
          </view>
        </view>
      </view>

      <app-section-header title="我的圈子" subtitle="管理主圈子、加入状态和挑战入口">
        <template #actions>
          <view v-if="isLoggedIn" class="section-actions">
            <text class="section-link wc-inline-link wc-inline-link--action wc-inline-link--subtle" @tap="goCircleManage">管理全部</text>
            <text class="section-link wc-inline-link wc-inline-link--action wc-inline-link--strong" @tap="goCircleCreate">+ 创建</text>
          </view>
        </template>
      </app-section-header>

      <view v-if="!isLoggedIn" class="section-padding">
        <app-empty-state
          icon="⭕"
          title="登录后查看我的圈子"
          description="你可以先继续浏览公开圈子和动态，决定加入、发布或管理时再登录。"
        >
          <button class="btn-primary flow-btn flow-btn--primary flow-btn--compact wc-pressable" @tap="goLoginFromCircle">去登录</button>
        </app-empty-state>
      </view>

      <app-section-header
        v-if="isLoggedIn"
        compact
        title="🔒 私密圈子"
        :subtitle="`${myPrivateCircles.length} 个已加入私密圈子`"
      />

      <view v-if="isLoggedIn && myLoading" class="loading-block">我的圈子加载中...</view>
      <view v-else-if="myPrivateCircles.length" class="my-groups-list">
        <view
          v-for="(group, index) in myPrivateCircles"
          :key="group.id"
          class="group-item wc-pressable"
          :class="circleToneClass(index)"
          @tap="goCircleDetail(group.id)"
        >
          <view class="group-cover-thumb" :class="circleCoverClass(group.coverUrl)">
            <resolved-image
              v-if="isRemoteCircleCover(group.coverUrl)"
              class="group-cover-thumb__image"
              :src="normalizeCircleCoverUrl(group.coverUrl)"
              mode="aspectFill"
            />
            <template v-else>
              <view class="group-cover-thumb__overlay"></view>
              <text class="group-cover-thumb__icon">{{ group.icon || groupEmblem(group) }}</text>
            </template>
          </view>
          <view class="group-info">
            <text class="group-name">{{ group.name }}</text>
            <text class="group-desc">{{ group.memberCount || 0 }} 人 · {{ joinedAtText(group.joinedAt) }}</text>
            <view class="group-tags">
              <text class="wc-badge wc-badge--soft">{{ group.role === '0' ? '管理员' : '成员' }}</text>
              <text class="wc-badge wc-badge--soft">{{ circleDepositText(group.depositRequired) }}</text>
              <text v-if="group.readOnly" class="wc-badge wc-badge--warning">{{ circleLifecycleLabel(group.lifecycleStatus) }}</text>
            </view>
            <text v-if="group.readOnly" class="group-readonly">{{ group.readOnlyReason || '圈子已到期，仅支持查看历史内容。' }}</text>
          </view>
          <button class="btn-checkin flow-btn flow-btn--primary flow-btn--compact wc-pressable" @tap.stop="goCircleDetail(group.id)">
            查看详情
          </button>
        </view>
      </view>
      <view v-else-if="isLoggedIn && !myErrorMessage" class="empty-inline">还没有加入私密圈子</view>

      <app-section-header
        v-if="isLoggedIn"
        compact
        title="⭕ 公开圈子"
        :subtitle="`${myPublicCircles.length} 个公开挑战`"
      />

      <view v-if="isLoggedIn && myLoading" class="loading-block">我的圈子加载中...</view>
      <view v-else-if="myPublicCircles.length" class="my-groups-list">
        <view
          v-for="(group, index) in myPublicCircles"
          :key="group.id"
          class="group-item wc-pressable"
          :class="circleToneClass(index + myPrivateCircles.length)"
          @tap="goCircleDetail(group.id)"
        >
          <view class="group-cover-thumb" :class="circleCoverClass(group.coverUrl)">
            <resolved-image
              v-if="isRemoteCircleCover(group.coverUrl)"
              class="group-cover-thumb__image"
              :src="normalizeCircleCoverUrl(group.coverUrl)"
              mode="aspectFill"
            />
            <template v-else>
              <view class="group-cover-thumb__overlay"></view>
              <text class="group-cover-thumb__icon">{{ group.icon || groupEmblem(group) }}</text>
            </template>
          </view>
          <view class="group-info">
            <text class="group-name">{{ group.name }}</text>
            <text class="group-desc">{{ group.memberCount || 0 }} 人 · 连续 {{ group.streakDays || 0 }} 天</text>
            <view class="group-tags">
              <text class="wc-badge wc-badge--soft">{{ group.role === '0' ? '管理员' : '成员' }}</text>
              <text class="wc-badge wc-badge--soft">{{ circleDurationText(group.durationDays) }}</text>
              <text v-if="group.readOnly" class="wc-badge wc-badge--warning">{{ circleLifecycleLabel(group.lifecycleStatus) }}</text>
            </view>
            <text v-if="group.readOnly" class="group-readonly">{{ group.readOnlyReason || '圈子已到期，仅支持查看历史内容。' }}</text>
          </view>
          <button class="btn-checkin secondary flow-btn flow-btn--secondary flow-btn--compact wc-pressable" @tap.stop="goCircleDetail(group.id)">
            查看详情
          </button>
        </view>
      </view>
      <view v-else-if="isLoggedIn && !myErrorMessage" class="empty-inline">还没有加入公开圈子</view>

      <view v-if="isLoggedIn && !myLoading && myErrorMessage" class="section-padding">
        <app-empty-state
          icon="⚠️"
          title="我的圈子加载失败"
          :description="myErrorMessage"
        >
          <button class="btn-primary flow-btn flow-btn--secondary flow-btn--compact wc-pressable" @tap="loadMyCircleData">重新整理</button>
        </app-empty-state>
      </view>

      <view v-if="isLoggedIn && !myLoading && !myCircles.length" class="section-padding">
        <app-empty-state
          icon="🎯"
          title="还没有加入任何圈子"
          description="先去发现页看看公开圈子，再决定要不要加入。"
        >
          <button class="btn-primary flow-btn flow-btn--primary flow-btn--compact wc-pressable" @tap="switchTab('discover')">浏览圈子</button>
        </app-empty-state>
      </view>

      <view class="page-bottom-space"></view>
    </scroll-view>

    <view v-if="currentTab === 'discover'" class="fab-post wc-pressable" @tap="showComposer">
      <text>✏️</text>
    </view>

    <view v-if="composerVisible" class="composer-mask" @tap="closeComposer">
      <view class="composer-sheet" :style="composerSheetStyle" @tap.stop>
        <view class="composer-header">
          <view>
            <text class="composer-title">{{ composerTitle }}</text>
            <text class="composer-subtitle">{{ composerSubtitle }}</text>
          </view>
          <text class="composer-close wc-inline-link wc-inline-link--action wc-inline-link--subtle" @tap="closeComposer">关闭</text>
        </view>

        <scroll-view
          class="composer-scroll"
          scroll-y
          scroll-anchoring
          :show-scrollbar="false"
          :style="composerScrollStyle"
        >
          <view class="composer-section">
            <text class="composer-label">可见范围</text>
            <view class="composer-scope-grid">
              <view
                class="composer-scope-card wc-pressable"
                :class="{ active: composerVisibilityScope === 'public' }"
                @tap="composerVisibilityScope = 'public'"
              >
                <text class="composer-scope-title">公开动态</text>
                <text class="composer-scope-desc">会显示在发现页公开动态里</text>
              </view>
              <view
                class="composer-scope-card wc-pressable"
                :class="{ active: composerVisibilityScope === 'circle' }"
                @tap="composerVisibilityScope = 'circle'"
              >
                <text class="composer-scope-title">仅圈子可见</text>
                <text class="composer-scope-desc">不会显示在发现页公开动态里</text>
              </view>
            </view>
          </view>

          <view class="composer-section">
            <text class="composer-label">动态类型</text>
            <view class="composer-type-grid">
              <view
                v-for="option in composerFeedTypes"
                :key="option.value"
                class="composer-type wc-pressable"
                :class="{ active: composerFeedType === option.value }"
                @tap="selectComposerFeedType(option.value)"
              >
                <text class="composer-type-icon">{{ option.icon }}</text>
                <text class="composer-type-text">{{ option.label }}</text>
              </view>
            </view>
            <view v-if="composerRecordBacked" class="composer-record-guide">
              <text class="composer-record-guide__title">{{ composerRecordGuideTitle }}</text>
              <text class="composer-record-guide__desc">{{ composerRecordGuideDesc }}</text>
            </view>
          </view>

          <view v-if="composerRecordBacked" class="composer-section">
            <view v-if="composerRecordType === 'weight'" class="composer-form-card">
              <view class="composer-form-group">
                <text class="composer-label">当前体重</text>
                <view class="wc-form-row">
                  <input
                    v-model="weightForm.weight"
                    type="digit"
                    class="wc-form-input wc-form-input--large"
                    placeholder="请输入体重"
                    placeholder-style="color:#9aa4b2;"
                  />
                  <text class="composer-unit">kg</text>
                </view>
              </view>
              <view class="composer-form-group">
                <text class="composer-label">体脂率 (选填)</text>
                <view class="wc-form-row">
                  <input
                    v-model="weightForm.bodyFat"
                    type="digit"
                    class="wc-form-input wc-form-input--medium"
                    placeholder="可不填"
                    placeholder-style="color:#9aa4b2;"
                  />
                  <text class="composer-unit">%</text>
                </view>
              </view>
              <view class="composer-form-group">
                <text class="composer-label">备注 (选填)</text>
                <textarea
                  v-model="weightForm.remark"
                  class="wc-form-textarea composer-record-textarea"
                  maxlength="80"
                  placeholder="可记录今天的状态、饮食或运动感受"
                  placeholder-style="color:#9aa4b2;"
                />
              </view>
            </view>

            <view v-else-if="composerRecordType === 'diet'" class="composer-form-card">
              <view class="composer-form-group">
                <text class="composer-label">餐次</text>
                <view class="composer-segment-row">
                  <view
                    v-for="meal in mealOptions"
                    :key="meal"
                    class="composer-segment wc-pressable"
                    :class="{ active: selectedMeal === meal }"
                    @tap="selectedMeal = meal"
                  >
                    {{ meal }}
                  </view>
                </view>
              </view>
              <view class="composer-form-group">
                <text class="composer-label">食物名称</text>
                <input
                  v-model="dietForm.name"
                  class="wc-form-input"
                  placeholder="例如：鸡胸肉沙拉"
                  placeholder-style="color:#9aa4b2;"
                />
                <view class="composer-food-estimator">
                  <view class="composer-food-estimator__header">
                    <text class="composer-food-estimator__label">常见食物估算</text>
                    <view class="composer-food-estimator__portion">
                      <text>份数</text>
                      <input
                        v-model="dietForm.portionMultiplier"
                        type="digit"
                        class="composer-food-estimator__portion-input"
                        placeholder="1"
                        placeholder-style="color:#9aa4b2;"
                      />
                    </view>
                  </view>
                  <scroll-view class="composer-food-estimator__scroll" scroll-x enable-flex>
                    <view class="composer-food-estimator__options">
                      <view
                        v-for="food in commonFoodOptions"
                        :key="food.name"
                        class="composer-food-estimator__chip wc-pressable"
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
              <view class="composer-form-group">
                <text class="composer-label">热量估算</text>
                <input
                  v-model="dietForm.calories"
                  type="number"
                  class="wc-form-input"
                  placeholder="420"
                  placeholder-style="color:#9aa4b2;"
                  @input="markDietCaloriesManual"
                />
                <text class="composer-row-hint composer-field-tip">{{ dietEstimateHint }}</text>
              </view>
            </view>

            <view v-else-if="composerRecordType === 'exercise'" class="composer-form-card">
              <view class="composer-form-group">
                <text class="composer-label">运动类型</text>
                <input
                  v-model="exerciseForm.type"
                  class="wc-form-input"
                  placeholder="例如：户外跑步"
                  placeholder-style="color:#9aa4b2;"
                />
                <view class="composer-exercise-dictionary">
                  <text class="composer-row-hint">常用运动</text>
                  <scroll-view class="composer-exercise-scroll" scroll-x enable-flex>
                    <view class="composer-exercise-options">
                      <view
                        v-for="type in exerciseTypeOptions"
                        :key="type"
                        class="composer-exercise-chip wc-pressable"
                        :class="{ active: isExerciseTypeSelected(type) }"
                        @tap="selectExerciseType(type)"
                      >
                        {{ type }}
                      </view>
                    </view>
                  </scroll-view>
                </view>
              </view>
              <view class="composer-form-grid">
                <view class="composer-form-group">
                  <text class="composer-label">运动时长</text>
                  <input
                    v-model="exerciseForm.minutes"
                    type="number"
                    class="wc-form-input"
                    placeholder="45"
                    placeholder-style="color:#9aa4b2;"
                  />
                </view>
                <view class="composer-form-group">
                  <text class="composer-label">消耗热量</text>
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
              <view class="composer-hint-card">
                <text>{{ exerciseEstimateHint }}</text>
              </view>
            </view>

            <view v-else-if="composerRecordType === 'water'" class="composer-form-card">
              <view class="composer-form-group">
                <text class="composer-label">快捷记录</text>
                <view class="composer-segment-row">
                  <view
                    v-for="preset in waterPresetOptions"
                    :key="preset.label"
                    class="composer-segment wc-pressable"
                    :class="{ active: Number(waterForm.cups) === preset.cups && Number(waterForm.ml) === preset.ml }"
                    @tap="applyWaterPreset(preset.cups, preset.ml)"
                  >
                    {{ preset.label }}
                  </view>
                </view>
              </view>
              <view class="composer-form-grid">
                <view class="composer-form-group">
                  <text class="composer-label">杯数</text>
                  <view class="wc-form-row">
                    <input
                      v-model="waterForm.cups"
                      type="number"
                      class="wc-form-input"
                      placeholder="1"
                      placeholder-style="color:#9aa4b2;"
                    />
                    <text class="composer-unit">杯</text>
                  </view>
                </view>
                <view class="composer-form-group">
                  <text class="composer-label">毫升</text>
                  <view class="wc-form-row">
                    <input
                      v-model="waterForm.ml"
                      type="number"
                      class="wc-form-input"
                      placeholder="250"
                      placeholder-style="color:#9aa4b2;"
                    />
                    <text class="composer-unit">ml</text>
                  </view>
                </view>
              </view>
            </view>

            <view v-else-if="composerRecordType === 'habit'" class="composer-form-card">
              <view class="composer-row composer-row--center">
                <view class="composer-row-copy">
                  <text class="composer-label">今日习惯</text>
                  <text class="composer-row-hint">选择一个习惯，发布时会先完成今日打卡。</text>
                </view>
                <button
                  class="flow-btn flow-btn--secondary flow-btn--compact"
                  :loading="habitTasksLoading"
                  @tap="loadTodayHabitTasks"
                >
                  重新整理
                </button>
              </view>

              <view v-if="habitTasksLoading" class="composer-empty-tip">今日习惯加载中...</view>
              <view v-else-if="habitTasks.length" class="composer-habit-list">
                <view
                  v-for="task in habitTasks"
                  :key="task.id"
                  class="composer-habit-item wc-pressable"
                  :class="{ active: selectedHabitId === task.id }"
                  @tap="selectHabitTask(task.id)"
                >
                  <view class="composer-habit-main">
                    <text class="composer-habit-icon">{{ task.icon || '✅' }}</text>
                    <view class="composer-habit-copy">
                      <text class="composer-habit-name">{{ task.name }}</text>
                      <text class="composer-habit-desc">{{ task.checkedToday ? '今天已打卡，可发布动态' : '发布时会完成今日打卡' }}</text>
                    </view>
                  </view>
                  <text class="composer-habit-check">{{ selectedHabitId === task.id ? '✓' : '○' }}</text>
                </view>
              </view>
              <view v-else class="composer-empty-tip" :class="{ 'composer-empty-tip--error': habitTaskLoadError }">
                <text class="composer-empty-title">{{ habitEmptyTitle }}</text>
                <text class="composer-empty-desc">{{ habitEmptyDescription }}</text>
              </view>
            </view>
          </view>

          <view class="composer-section">
            <view class="composer-row">
              <view class="composer-row-copy">
                <text class="composer-label">{{ composerVisibilityScope === 'public' ? '同步至圈子' : '选择可见圈子' }}</text>
                <text class="composer-row-hint">{{ composerCircleHint }}</text>
              </view>
              <view v-if="composerVisibilityScope === 'public'" class="composer-toggle">
                <view
                  class="composer-toggle-chip wc-pressable"
                  :class="{ active: !composerSyncEnabled }"
                  @tap="composerSyncEnabled = false"
                >不同步</view>
                <view
                  class="composer-toggle-chip wc-pressable"
                  :class="{ active: composerSyncEnabled }"
                  @tap="composerSyncEnabled = true"
                >同步</view>
              </view>
            </view>

            <scroll-view v-if="composerNeedsCircleSelection && myCircles.length" class="composer-rail" scroll-x>
              <view class="composer-chip-row">
                <view
                  v-for="circle in myCircles"
                  :key="`composer-circle-${circle.id}`"
                  class="composer-chip wc-pressable"
                  :class="{ active: composerSyncCircleIds.includes(circle.id) }"
                  @tap="toggleComposerCircle(circle.id)"
                >
                  {{ circle.name }}
                </view>
              </view>
            </scroll-view>

            <view v-else-if="composerNeedsCircleSelection && !myCircles.length" class="composer-empty-tip">
              你还没有已加入的圈子，当前不能发“仅圈子可见”动态。
            </view>
          </view>

          <view v-if="!composerRecordBacked" class="composer-section">
            <text class="composer-label">动态内容</text>
            <textarea
              v-model="composerContent"
              class="wc-form-textarea composer-textarea"
              maxlength="160"
              placeholder="写一句今天的进展、体感或打卡内容。"
              placeholder-style="color:#9aa4b2;"
            />
            <view class="composer-meta">
              <text>{{ composerHelperText }}</text>
              <text>{{ composerContent.length }}/160</text>
            </view>
          </view>

          <view class="composer-section">
            <view class="composer-row">
              <view class="composer-row-copy">
                <text class="composer-label">{{ composerRecordBacked ? composerRecordImageTitle : '动态图片' }}</text>
                <text class="composer-row-hint">{{ composerRecordImageHint }}单张不超过 {{ getUploadSizeLimitText() }}。</text>
              </view>
              <button
                class="flow-btn flow-btn--secondary flow-btn--compact"
                :loading="composerUploading"
                :disabled="composerUploading || composerImages.length >= 9"
                @tap="chooseComposerImages"
              >
                {{ composerImages.length >= 9 ? '已满 9 张' : composerImages.length ? '继续添加' : '添加图片' }}
              </button>
            </view>
            <view v-if="composerImages.length" class="composer-image-grid">
              <view
                v-for="(image, index) in composerImages"
                :key="image.id"
                class="composer-image-item"
              >
                <resolved-image
                  class="composer-image-preview"
                  :src="getUploadedImagePreviewSource(image)"
                  mode="aspectFill"
                  :preview-list="composerImagePreviewSources"
                  :preview-index="index"
                />
                <view v-if="image.uploading" class="upload-image-status">
                  <text class="upload-image-badge">上传中</text>
                </view>
                <view v-else-if="image.errorMessage" class="upload-image-status upload-image-status--error">
                  <text class="upload-image-badge upload-image-badge--error">上传失败</text>
                </view>
                <view
                  v-if="image.errorMessage && !image.uploading"
                  class="upload-image-retry"
                  @tap.stop="retryComposerImage(index)"
                >重试</view>
                <view class="composer-image-remove" @tap="removeComposerImage(index)">×</view>
              </view>
            </view>
          </view>
        </scroll-view>

        <view class="composer-actions">
          <button class="composer-btn flow-btn flow-btn--secondary" @tap="closeComposer">取消</button>
          <button class="composer-btn flow-btn flow-btn--primary" :loading="postingFeed" @tap="submitComposer">{{ composerPrimaryLabel }}</button>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import AppEmptyState from '@/components/AppEmptyState/index.vue'
import AppSectionHeader from '@/components/AppSectionHeader/index.vue'
import ResolvedImage from '@/components/ResolvedImage/index.vue'
import { getCircleList, getMyCircles } from '@/api/circle'
import { getPublicFeedList, publishFeed, toggleFeedLike, uploadFeedImage } from '@/api/feed'
import { checkinHabit, getTodayHabits } from '@/api/habit'
import { createExerciseRecord, createFoodRecord, createWaterRecord, createWeightRecord, getExerciseRecordList } from '@/api/record'
import { useUserStore } from '@/stores/user'
import type {
  CircleCard,
  CircleFeedCard,
  FeedCreatePayload,
  FeedVisibilityScope,
  HabitItem,
  MyCircleCard,
  RecordFeedDraftPayload
} from '@/types/api'
import {
  circleCoverClass,
  circleFeedTimeText,
  circleLifecycleLabel,
  initialText,
  isRemoteCircleCover,
  joinedAtText,
  normalizeCircleCoverUrl
} from '@/utils/circle'
import { normalizeExerciseType, normalizeExerciseTypeUsage } from '@/utils/exercise'
import { getMenuButtonSafeRight, getSafeTop, getTopBarHeight, syncCustomTabBar } from '@/utils/mobile'
import { createOnShowSuspendGuard, useTabBarOverlayVisibility } from '@/utils/page'
import { openLoginPage } from '@/utils/request'
import { useDefaultPageShare } from '@/utils/share'
import { toApiTimestamp } from '@/utils/datetime'
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
  chooseAndUploadImages,
  getUploadedImagePreviewSource,
  getUploadSizeLimitText,
  hasFailedImageAssets,
  replaceUploadedImageAsset,
  retryUploadedImageAsset,
  type UploadedImageAsset
} from '@/utils/upload'

useDefaultPageShare()

type CircleTab = 'discover' | 'my'
type ComposerRecordType = 'weight' | 'diet' | 'exercise' | 'water' | 'habit'

const safeTop = getSafeTop(20)
const topBarHeight = getTopBarHeight(56)
const menuButtonSafeRight = getMenuButtonSafeRight(16, 18)
const windowHeight = typeof uni.getWindowInfo === 'function' ? uni.getWindowInfo().windowHeight : uni.getSystemInfoSync().windowHeight
const composerTopGap = Math.max(topBarHeight + 12, safeTop + 72, 120)
const composerSheetHeight = Math.max(360, windowHeight - composerTopGap)
const composerSheetStyle = {
  height: `${composerSheetHeight}px`
}
const composerScrollStyle = {
  height: `${Math.max(220, composerSheetHeight - 196)}px`
}
const currentTab = ref<CircleTab>('discover')
const userStore = useUserStore()
const isLoggedIn = computed(() => userStore.isLoggedIn)
const discoverLoading = ref(false)
const myLoading = ref(false)
const composerVisible = ref(false)
const postingFeed = ref(false)
const composerUploading = ref(false)
const publicCirclesErrorMessage = ref('')
const recommendedFeedsErrorMessage = ref('')
const myErrorMessage = ref('')

const publicCircles = ref<CircleCard[]>([])
const recommendedFeeds = ref<CircleFeedCard[]>([])
const myCircles = ref<MyCircleCard[]>([])
const composerFeedType = ref<FeedCreatePayload['feedType']>('text')
const composerContent = ref('')
const composerVisibilityScope = ref<FeedVisibilityScope>('public')
const composerSyncEnabled = ref(false)
const composerSyncCircleIds = ref<number[]>([])
const composerImages = ref<UploadedImageAsset[]>([])
const composerImagePreviewSources = computed(() => composerImages.value
  .map((item) => getUploadedImagePreviewSource(item))
  .filter(Boolean))
const selectedMeal = ref('早餐')
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
const waterForm = ref({
  cups: '1',
  ml: '250'
})
const habitTasks = ref<HabitItem[]>([])
const habitTaskLoadError = ref('')
const habitTasksLoading = ref(false)
const selectedHabitId = ref<number | null>(null)
const exerciseTypeUsage = ref<Record<string, number>>({})
const exerciseHistoryTypeUsage = ref<Record<string, number>>({})
const exerciseHistoryUsageLoading = ref(false)
const exerciseHistoryUsageLoaded = ref(false)
const onShowRefreshGuard = createOnShowSuspendGuard()
const syncComposerTabBarVisibility = useTabBarOverlayVisibility(composerVisible)

const myPrivateCircles = computed(() => myCircles.value.filter((item) => item.type === '1'))
const myPublicCircles = computed(() => myCircles.value.filter((item) => item.type !== '1'))
const heroPreviewCircles = computed(() => publicCircles.value.slice(0, 3))
const totalVisibleMembers = computed(() =>
  publicCircles.value.reduce((sum, item) => sum + Number(item.memberCount || 0), 0)
)
const discoverHeadline = computed(() => {
  if (!publicCircles.value.length) {
    return '今天的公开挑战还在刷新中'
  }
  return `${publicCircles.value.length} 个圈子可以一起打卡`
})
const discoverSubtitle = computed(() => {
  if (!publicCircles.value.length) {
    return '新的圈子与推荐动态会在这里持续出现。'
  }
  const names = publicCircles.value.slice(0, 2).map((item) => item.name).join(' · ')
  return `${names}${publicCircles.value.length > 2 ? ' 等圈子今天有人在记录。' : ' 今天有人在记录。'}`
})
const composerFeedTypes = [
  { value: 'text', label: '日常', icon: '✦' },
  { value: 'weight', label: '体重', icon: '⚖️' },
  { value: 'food', label: '饮食', icon: '🥗' },
  { value: 'exercise', label: '运动', icon: '🏃' },
  { value: 'water', label: '饮水', icon: '💧' },
  { value: 'habit', label: '习惯', icon: '✅' }
] as const
const composerRecordBacked = computed(() => composerFeedType.value !== 'text')
const composerRecordType = computed<ComposerRecordType | ''>(() => recordTabForFeedType(composerFeedType.value) as ComposerRecordType | '')
const composerTitle = computed(() => (composerRecordBacked.value ? `${composerRecordTypeLabel.value}并发布` : '发布动态'))
const composerSubtitle = computed(() =>
  composerRecordBacked.value
    ? '在当前弹层完成记录，保存成功后自动发布动态。'
    : '发现页默认发布公开动态，你也可以选择只同步到圈子。'
)
const composerPrimaryLabel = computed(() => (composerRecordBacked.value ? '保存并发布' : '发布动态'))
const composerRecordTypeLabel = computed(() => {
  const current = composerFeedTypes.find((item) => item.value === composerFeedType.value)
  return current?.label || '打卡'
})
const composerRecordImageTitle = computed(() => `${composerRecordTypeLabel.value}图片`)
const composerRecordImageHint = computed(() =>
  composerRecordBacked.value
    ? `图片会保存到本次${composerRecordTypeLabel.value}记录，并随动态一起发布。`
    : '支持 1-9 张图片，可与文字一起发布。'
)
const composerRecordGuideTitle = computed(() => `${composerRecordTypeLabel.value}动态会关联真实记录`)
const composerRecordGuideDesc = computed(() => {
  const scopeText = composerVisibilityScope.value === 'circle'
    ? '发布后仅选中的圈子可见。'
    : composerSyncCircleIds.value.length
      ? '发布后会公开展示，并同步到选中的圈子。'
      : '发布后会显示在发现页公开动态。'
  return `先保存${composerRecordTypeLabel.value}记录，再自动生成可追溯动态。${scopeText}`
})
const composerHelperText = computed(() => {
  if (composerRecordBacked.value) {
    return composerRecordGuideDesc.value
  }
  const current = composerFeedTypes.find((item) => item.value === composerFeedType.value)
  const currentLabel = current ? `将以“${current.label}”动态发布` : '将发布动态'
  if (composerVisibilityScope.value === 'public') {
    return composerSyncCircleIds.value.length
      ? `${currentLabel}，并同步到 ${composerSyncCircleIds.value.length} 个圈子`
      : `${currentLabel}，并显示在发现页公开动态`
  }
  return composerSyncCircleIds.value.length
    ? `${currentLabel}，仅在 ${composerSyncCircleIds.value.length} 个圈子内可见`
    : `${currentLabel}，请至少选择 1 个圈子`
})
const composerNeedsCircleSelection = computed(() =>
  composerVisibilityScope.value === 'circle' || composerSyncEnabled.value
)
const composerCircleHint = computed(() => {
  if (!myCircles.value.length) {
    return composerVisibilityScope.value === 'public'
      ? '当前没有可同步的圈子'
      : '需要先加入圈子后才能仅圈子可见'
  }
  if (composerVisibilityScope.value === 'public') {
    return '公开动态可额外同步到你已加入的圈子'
  }
  return '至少选择 1 个圈子作为可见范围'
})
const mealOptions = ['早餐', '午餐', '晚餐', '加餐']
const mealTypeMap: Record<string, string> = {
  早餐: '0',
  午餐: '1',
  晚餐: '2',
  加餐: '3'
}
const waterPresetOptions = [
  { cups: 1, ml: 250, label: '1 杯' },
  { cups: 2, ml: 500, label: '2 杯' },
  { cups: 3, ml: 750, label: '3 杯' },
  { cups: 4, ml: 1000, label: '4 杯' }
]
const EXERCISE_TYPE_USAGE_STORAGE_KEY = 'recordExerciseTypeUsage'
exerciseTypeUsage.value = loadExerciseTypeUsage()
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
const habitEmptyTitle = computed(() =>
  habitTaskLoadError.value ? '今日习惯加载失败' : '今天没有可打卡习惯'
)
const habitEmptyDescription = computed(() =>
  habitTaskLoadError.value || '可以先去习惯管理里创建习惯，之后就能在这里直接打卡发布。'
)
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
    return '选择运动并填写时长后，会按当前体重自动估算消耗热量。'
  }
  if (!met) {
    return '这个运动暂未配置估算参数，请手动填写消耗热量。'
  }
  if (!bodyWeight) {
    return '完善个人资料中的当前体重后，可自动估算运动消耗。'
  }
  return `按 ${exerciseType} MET ${met}、当前体重 ${bodyWeight}kg 和时长估算，结果可修改。`
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

onShow(() => {
  if (onShowRefreshGuard.isSuspended()) {
    syncCustomTabBar(1)
    syncComposerTabBarVisibility()
    return
  }
  syncCustomTabBar(1)
  syncComposerTabBarVisibility()
  void refreshCirclePage()
})

function switchTab(tab: CircleTab) {
  currentTab.value = tab
}

async function refreshCirclePage() {
  await Promise.all([loadDiscoverData(), loadMyCircleData()])
}

async function loadDiscoverData() {
  discoverLoading.value = true
  publicCirclesErrorMessage.value = ''
  recommendedFeedsErrorMessage.value = ''
  try {
    const [circleResult, feedResult] = await Promise.allSettled([
      getCircleList({ pageNum: 1, pageSize: 12, type: '0' }),
      getPublicFeedList({ pageNum: 1, pageSize: 6 })
    ])

    if (circleResult.status === 'fulfilled') {
      publicCircles.value = circleResult.value.list || []
    } else {
      console.error('加载公开圈子失败', circleResult.reason)
      publicCircles.value = []
      publicCirclesErrorMessage.value =
        circleResult.reason instanceof Error ? circleResult.reason.message || '公开圈子接口请求失败' : '公开圈子接口请求失败'
    }

    if (feedResult.status === 'fulfilled') {
      recommendedFeeds.value = sortFeedsByCreatedAt(feedResult.value.list || [])
    } else {
      console.error('加载公开动态失败', feedResult.reason)
      recommendedFeeds.value = []
      recommendedFeedsErrorMessage.value =
        feedResult.reason instanceof Error ? feedResult.reason.message || '公开动态接口请求失败' : '公开动态接口请求失败'
    }
  } finally {
    discoverLoading.value = false
  }
}

async function loadMyCircleData() {
  if (!isLoggedIn.value) {
    myCircles.value = []
    composerSyncCircleIds.value = []
    myErrorMessage.value = ''
    myLoading.value = false
    return
  }
  myLoading.value = true
  myErrorMessage.value = ''
  try {
    const page = await getMyCircles({ pageNum: 1, pageSize: 100 })
    myCircles.value = page.list || []
  } catch (error) {
    console.error('加载我的圈子失败', error)
    myCircles.value = []
    composerSyncCircleIds.value = []
    myErrorMessage.value = error instanceof Error ? error.message || '我的圈子接口请求失败' : '我的圈子接口请求失败'
  } finally {
    myLoading.value = false
  }
}

function goCircleAll() {
  uni.navigateTo({ url: '/pages/circle-all/index' })
}

function goCircleCreate() {
  if (!ensureLoggedInForCircleAction()) {
    return
  }
  uni.navigateTo({ url: '/pages/circle-create/index' })
}

function goCircleManage() {
  if (!ensureLoggedInForCircleAction()) {
    return
  }
  uni.navigateTo({ url: '/pages/circle-manage/index' })
}

function goCircleDetail(circleId: number) {
  uni.navigateTo({ url: `/pages/circle-private/index?circleId=${circleId}` })
}

function goFeedDetail(feed: CircleFeedCard) {
  uni.navigateTo({ url: `/pages/feed-detail/index?feedId=${feed.id}` })
}

function goUserProfile(userId?: number | null) {
  if (!userId) {
    return
  }
  uni.navigateTo({ url: `/pages/user-profile/index?userId=${userId}` })
}

function resetComposer() {
  composerVisibilityScope.value = 'public'
  composerSyncEnabled.value = false
  composerSyncCircleIds.value = []
  composerFeedType.value = 'text'
  composerContent.value = ''
  composerImages.value = []
  resetComposerRecordForms()
}

function ensureLoggedInForCircleAction(redirectUrl = '/pages/circle/index') {
  if (isLoggedIn.value) {
    return true
  }
  openLoginPage('safe', redirectUrl)
  return false
}

function goLoginFromCircle() {
  openLoginPage('safe', '/pages/circle/index')
}

function showComposer() {
  if (!ensureLoggedInForCircleAction()) {
    return
  }
  composerVisible.value = true
  if (composerRecordType.value === 'habit') {
    void loadTodayHabitTasks()
  }
  if (composerRecordType.value === 'exercise') {
    void loadExerciseTypeHistoryUsage()
  }
  if (composerRecordType.value === 'diet') {
    syncMealSelectionForCurrentTime()
  }
}

function closeComposer() {
  composerVisible.value = false
}

function selectComposerFeedType(feedType: FeedCreatePayload['feedType']) {
  composerFeedType.value = feedType
  const recordType = recordTabForFeedType(feedType)
  if (recordType === 'diet') {
    syncMealSelectionForCurrentTime()
  }
  if (recordType === 'exercise') {
    void loadExerciseTypeHistoryUsage()
  }
  if (recordType === 'habit') {
    void loadTodayHabitTasks()
  }
}

async function submitComposer() {
  if (!ensureLoggedInForCircleAction()) {
    return
  }
  if (composerRecordBacked.value) {
    await submitRecordBackedComposer()
    return
  }

  const content = composerContent.value.trim()
  if (!content && !composerImages.value.length) {
    uni.showToast({ title: '请先填写内容或上传图片', icon: 'none' })
    return
  }
  if (composerUploading.value) {
    uni.showToast({ title: '图片还在上传中，请稍后再发', icon: 'none' })
    return
  }
  if (hasFailedImageAssets(composerImages.value)) {
    uni.showToast({ title: '有图片上传失败，请删除后重试', icon: 'none' })
    return
  }
  if (composerNeedsCircleSelection.value && !composerSyncCircleIds.value.length) {
    uni.showToast({ title: '请至少选择一个圈子', icon: 'none' })
    return
  }

  postingFeed.value = true
  try {
    const payload: FeedCreatePayload = {
      feedType: composerFeedType.value,
      content,
      visibilityScope: composerVisibilityScope.value,
      originCircleId: composerVisibilityScope.value === 'circle' ? composerSyncCircleIds.value[0] || null : null,
      syncCircleIds: composerNeedsCircleSelection.value ? composerSyncCircleIds.value : [],
      images: composerImages.value.map((item) => item.url).filter(Boolean)
    }
    await publishFeed(payload)
    uni.showToast({
      title: payload.visibilityScope === 'public' ? '公开动态已发布' : '圈内动态已发布',
      icon: 'success'
    })
    closeComposer()
    resetComposer()
    await refreshCirclePage()
    currentTab.value = 'discover'
  } catch (error: any) {
    uni.showToast({ title: error?.message || '发布失败', icon: 'none' })
  } finally {
    postingFeed.value = false
  }
}

function recordTabForFeedType(feedType?: string | null) {
  if (feedType === 'weight' || feedType === 'exercise' || feedType === 'water' || feedType === 'habit') {
    return feedType
  }
  if (feedType === 'food' || feedType === 'diet') {
    return 'diet'
  }
  return ''
}

function resetComposerRecordForms() {
  selectedMeal.value = inferMealByCurrentTime()
  weightForm.value = { weight: '', bodyFat: '', remark: '' }
  dietForm.value = { name: '', calories: '', portionMultiplier: '1' }
  exerciseForm.value = { type: '', minutes: '', calories: '' }
  dietCaloriesManual.value = false
  exerciseCaloriesManual.value = false
  waterForm.value = { cups: '1', ml: '250' }
  selectedHabitId.value = null
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

function applyWaterPreset(cups: number, ml: number) {
  waterForm.value = {
    cups: `${cups}`,
    ml: `${ml}`
  }
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
  if (!isLoggedIn.value) {
    return
  }
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

async function loadTodayHabitTasks() {
  if (!isLoggedIn.value) {
    habitTasks.value = []
    habitTaskLoadError.value = ''
    selectedHabitId.value = null
    return
  }
  if (habitTasksLoading.value) {
    return
  }
  habitTasksLoading.value = true
  habitTaskLoadError.value = ''
  try {
    const tasks = await getTodayHabits()
    habitTasks.value = tasks
    if (!tasks.some((task) => task.id === selectedHabitId.value)) {
      selectedHabitId.value = tasks[0]?.id || null
    }
  } catch (error) {
    console.error('加载今日习惯失败:', error)
    habitTasks.value = []
    selectedHabitId.value = null
    habitTaskLoadError.value = error instanceof Error ? error.message || '今日习惯暂时不可用' : '今日习惯暂时不可用'
  } finally {
    habitTasksLoading.value = false
  }
}

function selectHabitTask(habitId: number) {
  selectedHabitId.value = habitId
}

function ensureComposerReadyForPublish() {
  if (composerUploading.value) {
    uni.showToast({ title: '图片还在上传中，请稍后再发', icon: 'none' })
    return false
  }
  if (hasFailedImageAssets(composerImages.value)) {
    uni.showToast({ title: '有图片上传失败，请删除后重试', icon: 'none' })
    return false
  }
  if (composerNeedsCircleSelection.value && !composerSyncCircleIds.value.length) {
    uni.showToast({ title: '请至少选择一个圈子', icon: 'none' })
    return false
  }
  return true
}

function getComposerSelectedCircleIds() {
  if (!composerNeedsCircleSelection.value) {
    return []
  }
  return composerSyncCircleIds.value
    .map((circleId) => Number(circleId || 0))
    .filter((circleId, index, array) => circleId > 0 && array.indexOf(circleId) === index)
}

function getComposerImageUrls() {
  return composerImages.value.map((item) => item.url).filter(Boolean)
}

function getComposerImagesPayload() {
  const images = getComposerImageUrls()
  return images.length ? JSON.stringify(images) : null
}

function getComposerSyncToCirclesPayload() {
  const selectedCircleIds = getComposerSelectedCircleIds()
  return selectedCircleIds.length ? JSON.stringify(selectedCircleIds) : null
}

async function submitRecordBackedComposer() {
  if (!ensureComposerReadyForPublish()) {
    return
  }

  postingFeed.value = true
  try {
    const draft = await createComposerRecordDraft()
    if (!draft) {
      return
    }
    await userStore.fetchUserInfo().catch((error) => {
      console.warn('刷新用户信息失败:', error)
    })
    await publishComposerRecordDraft(draft)
    uni.showToast({
      title: composerVisibilityScope.value === 'public' ? '记录和动态已发布' : '圈内打卡已发布',
      icon: 'success'
    })
    closeComposer()
    resetComposer()
    await refreshCirclePage()
    currentTab.value = 'discover'
  } catch (error: any) {
    uni.showToast({ title: error?.message || '发布失败', icon: 'none' })
  } finally {
    postingFeed.value = false
  }
}

async function createComposerRecordDraft() {
  if (composerRecordType.value === 'weight') {
    return await createWeightComposerDraft()
  }
  if (composerRecordType.value === 'diet') {
    return await createDietComposerDraft()
  }
  if (composerRecordType.value === 'exercise') {
    return await createExerciseComposerDraft()
  }
  if (composerRecordType.value === 'water') {
    return await createWaterComposerDraft()
  }
  if (composerRecordType.value === 'habit') {
    return await createHabitComposerDraft()
  }
  uni.showToast({ title: '暂不支持该打卡类型', icon: 'none' })
  return null
}

async function createWeightComposerDraft() {
  const weight = Number(weightForm.value.weight)
  if (!weight || Number.isNaN(weight) || weight <= 0) {
    uni.showToast({ title: '请输入正确的体重数值', icon: 'none' })
    return null
  }

  const bodyFatValue = weightForm.value.bodyFat ? Number(weightForm.value.bodyFat) : null
  if (bodyFatValue !== null && (Number.isNaN(bodyFatValue) || bodyFatValue < 0)) {
    uni.showToast({ title: '体脂率格式不正确', icon: 'none' })
    return null
  }

  const result = await createWeightRecord({
    weight,
    bodyFatRate: bodyFatValue,
    remark: weightForm.value.remark || null,
    syncToCircles: getComposerSyncToCirclesPayload(),
    images: getComposerImagesPayload()
  })
  return result.draft
}

async function createDietComposerDraft() {
  const foodName = dietForm.value.name.trim()
  if (!foodName) {
    uni.showToast({ title: '请输入食物名称', icon: 'none' })
    return null
  }

  const calories = Number(dietForm.value.calories)
  if (!calories || Number.isNaN(calories) || calories <= 0 || !Number.isInteger(calories)) {
    uni.showToast({ title: '请输入正确的热量数值', icon: 'none' })
    return null
  }

  const result = await createFoodRecord({
    mealType: mealTypeMap[selectedMeal.value] || '0',
    foodName,
    calories,
    syncToCircles: getComposerSyncToCirclesPayload(),
    remark: null,
    images: getComposerImagesPayload()
  })
  return result.draft
}

async function createExerciseComposerDraft() {
  const exerciseType = normalizeExerciseType(exerciseForm.value.type)
  if (!exerciseType) {
    uni.showToast({ title: '请输入运动类型', icon: 'none' })
    return null
  }

  const durationMinutes = Number(exerciseForm.value.minutes)
  if (!durationMinutes || Number.isNaN(durationMinutes) || durationMinutes <= 0 || !Number.isInteger(durationMinutes)) {
    uni.showToast({ title: '请输入正确的运动时长', icon: 'none' })
    return null
  }

  const caloriesBurned = Number(exerciseForm.value.calories)
  if (!caloriesBurned || Number.isNaN(caloriesBurned) || caloriesBurned <= 0 || !Number.isInteger(caloriesBurned)) {
    uni.showToast({ title: '请输入正确的消耗热量', icon: 'none' })
    return null
  }

  const result = await createExerciseRecord({
    exerciseType,
    durationMinutes,
    caloriesBurned,
    syncToCircles: getComposerSyncToCirclesPayload(),
    remark: null,
    images: getComposerImagesPayload()
  })
  rememberExerciseType(exerciseType)
  return result.draft
}

async function createWaterComposerDraft() {
  const cups = Number(waterForm.value.cups)
  if (!cups || Number.isNaN(cups) || cups <= 0 || !Number.isInteger(cups)) {
    uni.showToast({ title: '请输入正确的饮水杯数', icon: 'none' })
    return null
  }

  const ml = Number(waterForm.value.ml)
  if (!ml || Number.isNaN(ml) || ml <= 0 || !Number.isInteger(ml)) {
    uni.showToast({ title: '请输入正确的饮水毫升', icon: 'none' })
    return null
  }

  const result = await createWaterRecord({
    cups,
    ml,
    images: getComposerImagesPayload()
  })
  return result.draft
}

async function createHabitComposerDraft(): Promise<RecordFeedDraftPayload | null> {
  if (!habitTasks.value.length && !habitTaskLoadError.value) {
    await loadTodayHabitTasks()
  }
  const task = habitTasks.value.find((item) => item.id === selectedHabitId.value)
  if (!task) {
    uni.showToast({ title: '请选择要打卡的习惯', icon: 'none' })
    return null
  }

  const currentImages = normalizeHabitImageList(task.images)
  const nextImages = mergeHabitImageUrls([...currentImages, ...getComposerImageUrls()])
  let updatedTask = task
  if (!task.checkedToday || getComposerImageUrls().length || !task.checkinId) {
    updatedTask = await checkinHabit(task.id, nextImages.length ? { images: nextImages } : undefined)
    habitTasks.value = habitTasks.value.map((item) => (item.id === updatedTask.id ? updatedTask : item))
  }

  if (!updatedTask.checkinId) {
    await loadTodayHabitTasks()
    updatedTask = habitTasks.value.find((item) => item.id === task.id) || updatedTask
  }
  if (!updatedTask.checkinId) {
    throw new Error('习惯打卡来源异常')
  }

  return {
    feedType: 'habit',
    content: `${updatedTask.icon || '✅'} 今天完成了「${updatedTask.name}」打卡，继续保持。`,
    visibilityScope: 'public',
    originCircleId: null,
    syncCircleIds: [],
    images: normalizeHabitImageList(updatedTask.images),
    sourceType: 'habit_checkin',
    sourceId: updatedTask.checkinId
  }
}

async function publishComposerRecordDraft(draft: RecordFeedDraftPayload) {
  if (!draft.sourceType || !draft.sourceId) {
    throw new Error('记录来源缺失，动态未发布')
  }

  const selectedCircleIds = getComposerSelectedCircleIds()
  const originCircleId = selectedCircleIds[0] || null
  const syncCircleIds = originCircleId ? selectedCircleIds.slice(1) : selectedCircleIds
  const visibilityScope = originCircleId ? composerVisibilityScope.value || draft.visibilityScope || 'public' : 'public'
  const images = Array.isArray(draft.images) ? draft.images : getComposerImageUrls()

  await publishFeed({
    feedType: draft.feedType || composerFeedType.value,
    content: draft.content || '记录更新',
    images,
    visibilityScope,
    originCircleId,
    syncCircleIds,
    sourceType: draft.sourceType,
    sourceId: draft.sourceId
  })
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

function toggleComposerCircle(circleId: number) {
  if (composerSyncCircleIds.value.includes(circleId)) {
    composerSyncCircleIds.value = composerSyncCircleIds.value.filter((item) => item !== circleId)
    return
  }
  composerSyncCircleIds.value = [...composerSyncCircleIds.value, circleId]
}

async function chooseComposerImages() {
  if (!ensureLoggedInForCircleAction()) {
    return
  }
  if (composerUploading.value || composerImages.value.length >= 9) {
    return
  }
  await onShowRefreshGuard.runWhileSuspended(async () => {
    try {
      await chooseAndUploadImages({
        currentCount: composerImages.value.length,
        upload: uploadFeedImage,
        fallbackErrorMessage: '图片上传失败',
        setUploading: (uploading) => {
          composerUploading.value = uploading
        },
        onLocalAssetsSelected: (assets) => {
          composerImages.value = [...composerImages.value, ...assets].slice(0, 9)
        },
        onAssetUpdated: (asset) => {
          composerImages.value = replaceUploadedImageAsset(composerImages.value, asset)
        }
      })
    } catch (error: any) {
      uni.showToast({ title: error?.message || '图片上传失败', icon: 'none' })
    }
  })
}

function removeComposerImage(index: number) {
  composerImages.value = composerImages.value.filter((_, currentIndex) => currentIndex !== index)
}

async function retryComposerImage(index: number) {
  const image = composerImages.value[index]
  if (!image || image.uploading) {
    return
  }
  try {
    await retryUploadedImageAsset({
      asset: image,
      upload: uploadFeedImage,
      fallbackErrorMessage: '图片上传失败',
      setUploading: (uploading) => {
        composerUploading.value = uploading
      },
      onAssetUpdated: (asset) => {
        composerImages.value = replaceUploadedImageAsset(composerImages.value, asset)
      }
    })
  } catch (error: any) {
    uni.showToast({ title: error?.message || '图片上传失败', icon: 'none' })
  }
}

async function handleFeedLike(feed: CircleFeedCard) {
  if (!ensureLoggedInForCircleAction('/pages/circle/index')) {
    return
  }
  try {
    const updatedFeed = await toggleFeedLike(feed.id)
    recommendedFeeds.value = recommendedFeeds.value.map((item) => (item.id === feed.id ? updatedFeed : item))
  } catch (error: any) {
    uni.showToast({ title: error?.message || '点赞操作失败', icon: 'none' })
  }
}

function formatMemberCount(count?: number | null) {
  const value = Number(count || 0)
  if (value >= 10000) {
    return `${(value / 10000).toFixed(1)}w`
  }
  if (value >= 1000) {
    return `${(value / 1000).toFixed(1)}k`
  }
  return `${value}`
}

function circleToneClass(index: number) {
  const tones = ['tone-ocean', 'tone-mint', 'tone-sun', 'tone-dusk']
  return tones[index % tones.length]
}

function circleTypeLabelText(type?: string | null) {
  return type === '1' ? '私密圈子' : '公开挑战'
}

function circleDurationText(durationDays?: number | null) {
  if (!durationDays || durationDays <= 0) {
    return '长期计划'
  }
  return `${durationDays} 天`
}

function circleDepositText(depositRequired?: number | null) {
  return '押金暂未开放'
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

function circleHighlightText(circle: CircleCard) {
  if (circle.joined) {
    return '保持更新，和圈友一起完成目标。'
  }
  if (Number(circle.memberCount || 0) >= 50) {
    return '成员活跃，适合想要监督感的挑战。'
  }
  return '现在加入，今天就能开始第一条打卡。'
}

function circleEmblem(circle: CircleCard | MyCircleCard) {
  return initialText(circle.name, '圈')
}

function groupEmblem(group: MyCircleCard) {
  return initialText(group.name, '圈')
}

function feedTypeText(feedType?: string | null) {
  const mapping: Record<string, string> = {
    weight: '体重打卡',
    diet: '饮食分享',
    food: '饮食分享',
    exercise: '运动记录',
    water: '饮水记录',
    habit: '习惯',
    text: '日常更新'
  }
  return mapping[feedType || ''] || '圈子动态'
}

function feedGlyph(feedType?: string | null) {
  const mapping: Record<string, string> = {
    weight: '⚖️',
    diet: '🥗',
    food: '🥗',
    exercise: '🏃',
    water: '💧',
    habit: '✅',
    text: '✦'
  }
  return mapping[feedType || ''] || '✦'
}

function feedSignalText(feed: CircleFeedCard) {
  if (feed.isFeatured) {
    return '精选'
  }
  if (feed.visibilityScope === 'public') {
    return '公开'
  }
  const score = Number(feed.likesCount || 0) + Number(feed.commentsCount || 0)
  if (score >= 15) {
    return '热议'
  }
  if (score >= 5) {
    return '活跃'
  }
  return '更新'
}

function feedToneClass(index: number) {
  const tones = ['feed-ocean', 'feed-mint', 'feed-sun', 'feed-dusk']
  return tones[index % tones.length]
}

function sortFeedsByCreatedAt(feeds: CircleFeedCard[]) {
  return [...feeds].sort((a, b) => {
    const featuredDiff = Number(Boolean(b.isFeatured)) - Number(Boolean(a.isFeatured))
    if (featuredDiff !== 0) {
      return featuredDiff
    }
    const aTime = toApiTimestamp(a.createdAt)
    const bTime = toApiTimestamp(b.createdAt)
    if (aTime !== bTime) {
      return bTime - aTime
    }
    return Number(b.id || 0) - Number(a.id || 0)
  })
}
</script>

<style scoped lang="scss">
$primary: var(--wc-primary);
$accent: var(--wc-success);
$bg-page: var(--wc-bg);
$card-bg: var(--wc-surface-strong);
$text-main: var(--wc-text);
$text-sub: var(--wc-text-soft);
$shadow: var(--wc-shadow-soft);
$surface-muted: var(--wc-surface-muted);

@use '../../styles/flow-button.scss';

.circle-container {
  height: 100vh;
  background: $bg-page;
  overflow: hidden;
}

.header {
  padding: 0 30rpx;
}

.header-tabs {
  display: flex;
  gap: 32rpx;
}

.header-tab {
  font-size: 32rpx;
  color: $text-sub;
  padding-bottom: 12rpx;
  border-bottom: 6rpx solid transparent;
  font-weight: 500;
}

.header-tab.active {
  color: $text-main;
  border-color: $primary;
  font-size: 36rpx;
  font-weight: 800;
}

.header-right {
  font-size: 36rpx;
}

.discover-hero,
.my-circle-hero {
  margin: 0 30rpx 8rpx;
  padding: 30rpx;
  border-radius: 40rpx;
  position: relative;
  overflow: hidden;
  border: 1px solid rgba(39, 92, 72, 0.055);
  box-shadow: none;
}

.discover-hero {
  background:
    radial-gradient(circle at 82% 18%, rgba(255, 255, 255, 0.86), transparent 28%),
    linear-gradient(135deg, rgba(232, 248, 241, 0.98), rgba(255, 244, 221, 0.7) 58%, rgba(255, 255, 255, 0.96));
}

.my-circle-hero {
  background:
    radial-gradient(circle at 20% 20%, rgba(255, 255, 255, 0.76), transparent 26%),
    linear-gradient(135deg, rgba(232, 248, 241, 0.94), rgba(255, 255, 255, 0.96));
}

.discover-kicker,
.my-circle-kicker {
  display: inline-flex;
  align-items: center;
  height: 44rpx;
  padding: 0 18rpx;
  border-radius: 999rpx;
  background: rgba(255, 255, 255, 0.86);
  color: var(--wc-primary-strong);
  font-size: 20rpx;
  font-weight: 700;
  letter-spacing: 0;
}

.discover-title,
.my-circle-title {
  display: block;
  margin-top: 18rpx;
  font-size: 40rpx;
  line-height: 1.2;
  font-weight: 800;
  color: $text-main;
}

.discover-subtitle,
.my-circle-subtitle {
  display: block;
  margin-top: 12rpx;
  font-size: 24rpx;
  line-height: 1.7;
  color: rgba(31, 41, 55, 0.72);
}

.discover-hero-side {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 24rpx;
  margin-top: 28rpx;
}

.hero-avatar-stack {
  display: flex;
  align-items: center;
}

.hero-avatar {
  width: 72rpx;
  height: 72rpx;
  margin-left: -16rpx;
  border-radius: 50%;
  border: 4rpx solid rgba(255, 255, 255, 0.88);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 26rpx;
  font-weight: 800;
  color: white;
  box-shadow: none;
}

.hero-avatar:first-child {
  margin-left: 0;
}

.hero-stats,
.my-circle-stats {
  display: flex;
  align-items: center;
  gap: 18rpx;
}

.hero-stat,
.my-circle-stat {
  min-width: 120rpx;
  padding: 14rpx 18rpx;
  border-radius: 24rpx;
  background: rgba(255, 255, 255, 0.58);
  border: 1px solid rgba(255, 255, 255, 0.68);
}

.hero-stat-value,
.my-circle-stat-value {
  display: block;
  font-size: 30rpx;
  font-weight: 800;
  color: $text-main;
}

.hero-stat-label,
.my-circle-stat-label {
  display: block;
  margin-top: 6rpx;
  font-size: 20rpx;
  color: rgba(31, 41, 55, 0.62);
}

.section-title {
  padding: 36rpx 30rpx 20rpx;
  font-size: 34rpx;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.section-padding {
  padding: 0 30rpx 24rpx;
}

.section-title.compact {
  padding-top: 20rpx;
}

.section-link {
}

.section-link.strong {
}

.section-meta {
  font-size: 24rpx;
  color: $text-sub;
  font-weight: 500;
}

.section-actions {
  display: flex;
  align-items: center;
  gap: 20rpx;
}

.loading-block,
.empty-inline {
  margin: 0 30rpx 24rpx;
  padding: 28rpx;
  border-radius: 24rpx;
  background: rgba(255, 255, 255, 0.88);
  border: 1px solid var(--wc-line);
  color: $text-sub;
  text-align: center;
}

.circle-scroll {
  white-space: nowrap;
  padding: 0 30rpx 24rpx;
}

.circle-card {
  position: relative;
  display: inline-flex;
  flex-direction: column;
  width: 438rpx;
  height: 560rpx;
  margin-right: 20rpx;
  padding: 0;
  border-radius: 40rpx;
  background: $card-bg;
  border: 1px solid rgba(39, 92, 72, 0.055);
  box-shadow: none;
  overflow: hidden;
}

.circle-cover {
  position: relative;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  min-height: 236rpx;
  padding: 28rpx 28rpx 24rpx;
  overflow: hidden;
}

.circle-cover::after {
  content: '';
  position: absolute;
  inset: auto 0 0;
  height: 120rpx;
  background: linear-gradient(180deg, rgba(15, 23, 42, 0), rgba(15, 23, 42, 0.26));
}

.circle-cover-image {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
}

.circle-cover.cover-ocean {
  background: linear-gradient(135deg, #4f8ef7, #54d2d2);
}

.circle-cover.cover-sunrise {
  background: linear-gradient(135deg, #ff9966, #ff5e62);
}

.circle-cover.cover-forest {
  background: linear-gradient(135deg, #2d6a4f, #52b788);
}

.circle-cover.cover-berry {
  background: linear-gradient(135deg, #c850c0, #ffcc70);
}

.circle-cover.cover-night {
  background: linear-gradient(135deg, #0f172a, #334155);
}

.circle-cover.cover-sand {
  background: linear-gradient(135deg, #d9a066, #f2d0a4);
}

.circle-bg {
  position: absolute;
  border-radius: 50%;
  opacity: 0.85;
}

.circle-bg.orb-one {
  top: -44rpx;
  right: -20rpx;
  width: 168rpx;
  height: 168rpx;
}

.circle-bg.orb-two {
  bottom: -60rpx;
  left: -20rpx;
  width: 160rpx;
  height: 160rpx;
  opacity: 0.45;
}

.circle-cover-top {
  position: relative;
  z-index: 1;
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 18rpx;
}

.circle-cover-badges {
  display: flex;
  flex-wrap: wrap;
  gap: 10rpx;
  max-width: 260rpx;
}

.circle-emblem {
  width: 70rpx;
  height: 70rpx;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.72);
  color: rgba(17, 24, 39, 0.82);
  font-size: 28rpx;
  font-weight: 800;
  box-shadow: none;
}

.circle-cover-bottom {
  position: relative;
  z-index: 1;
  display: flex;
  flex-direction: column;
  gap: 8rpx;
}

.circle-cover-kicker {
  display: inline-flex;
  align-items: center;
  align-self: flex-start;
  height: 38rpx;
  padding: 0 14rpx;
  border-radius: 999rpx;
  background: rgba(255, 255, 255, 0.18);
  backdrop-filter: blur(12rpx);
  color: rgba(255, 255, 255, 0.92);
  font-size: 20rpx;
  font-weight: 700;
  letter-spacing: 1rpx;
}

.circle-cover-caption {
  display: block;
  max-width: 290rpx;
  font-size: 22rpx;
  line-height: 1.6;
  color: rgba(255, 255, 255, 0.88);
  white-space: normal;
}

.circle-card.tone-ocean .circle-bg {
  background: linear-gradient(135deg, rgba(47, 179, 123, 0.28), rgba(255, 244, 221, 0.18));
}

.circle-card.tone-mint .circle-bg {
  background: linear-gradient(135deg, rgba(43, 167, 121, 0.34), rgba(131, 239, 218, 0.16));
}

.circle-card.tone-sun .circle-bg {
  background: linear-gradient(135deg, rgba(255, 193, 92, 0.38), rgba(255, 231, 182, 0.18));
}

.circle-card.tone-dusk .circle-bg {
  background: linear-gradient(135deg, rgba(125, 102, 255, 0.32), rgba(221, 214, 254, 0.18));
}

.circle-info,
.circle-footer {
  position: relative;
  z-index: 1;
}

.circle-info {
  display: flex;
  flex: 1;
  flex-direction: column;
  padding: 24rpx 28rpx 14rpx;
}

.circle-name-row {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16rpx;
  margin-bottom: 14rpx;
}

.circle-name {
  display: -webkit-box;
  flex: 1;
  font-size: 34rpx;
  line-height: 1.28;
  font-weight: 800;
  white-space: normal;
  overflow: hidden;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  min-height: 86rpx;
}

.circle-status {
  flex-shrink: 0;
  display: inline-flex;
  align-items: center;
  height: 40rpx;
  padding: 0 14rpx;
  border-radius: 999rpx;
  background: rgba(232, 248, 241, 0.96);
  color: var(--wc-primary-strong);
  font-size: 20rpx;
  font-weight: 700;
}

.circle-desc {
  display: -webkit-box;
  font-size: 24rpx;
  line-height: 1.7;
  color: $text-sub;
  white-space: normal;
  overflow: hidden;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  min-height: 82rpx;
}

.circle-stat-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12rpx;
  margin-top: 20rpx;
}

.circle-stat-chip {
  min-width: 0;
  padding: 14rpx 12rpx;
  border-radius: 22rpx;
  background: rgba(246, 251, 247, 0.68);
  border: 1px solid rgba(39, 92, 72, 0.055);
}

.circle-stat-value {
  display: block;
  font-size: 24rpx;
  line-height: 1.2;
  font-weight: 800;
  color: rgba(17, 24, 39, 0.92);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.circle-stat-label {
  display: block;
  margin-top: 6rpx;
  font-size: 18rpx;
  color: rgba(100, 116, 139, 0.82);
}

.circle-meta-row {
  display: flex;
  flex-wrap: wrap;
  gap: 10rpx;
  margin-top: 16rpx;
}

.circle-footer {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 18rpx;
  font-size: 24rpx;
  padding: 0 28rpx 28rpx;
}

.circle-footer-copy {
  flex: 1;
  min-width: 0;
}

.circle-meta {
  display: block;
  font-size: 22rpx;
  line-height: 1.5;
  color: $text-sub;
}

.circle-join {
  flex-shrink: 0;
}

.feed-list {
  padding: 0 30rpx;
}

.feed-card {
  background: $card-bg;
  border-radius: 34rpx;
  padding: 28rpx;
  margin-bottom: 24rpx;
  border: 1px solid rgba(39, 92, 72, 0.055);
  box-shadow: none;
  position: relative;
  overflow: hidden;
}

.feed-card:before {
  content: '';
  position: absolute;
  left: 0;
  top: 26rpx;
  bottom: 26rpx;
  width: 8rpx;
  border-radius: 999rpx;
  opacity: 0.9;
}

.feed-card.feed-ocean:before {
  background: linear-gradient(180deg, rgba(47, 179, 123, 0.92), rgba(255, 193, 92, 0.52));
}

.feed-card.feed-mint:before {
  background: linear-gradient(180deg, rgba(43, 167, 121, 0.92), rgba(131, 239, 218, 0.58));
}

.feed-card.feed-sun:before {
  background: linear-gradient(180deg, rgba(255, 176, 32, 0.92), rgba(255, 223, 128, 0.56));
}

.feed-card.feed-dusk:before {
  background: linear-gradient(180deg, rgba(99, 102, 241, 0.88), rgba(196, 181, 253, 0.52));
}

.feed-topline {
  display: flex;
  align-items: center;
  justify-content: flex-start;
  flex-wrap: wrap;
  gap: 12rpx;
  margin-bottom: 18rpx;
  padding-left: 18rpx;
}

.feed-header {
  display: flex;
  align-items: center;
  margin-bottom: 20rpx;
  padding-left: 18rpx;
}

.avatar {
  width: 80rpx;
  height: 80rpx;
  border-radius: 50%;
  margin-right: 20rpx;
  background: var(--wc-primary-soft);
}

.avatar-fallback {
  display: flex;
  align-items: center;
  justify-content: center;
  color: $primary;
  font-size: 28rpx;
  font-weight: 700;
}

.user-info {
  flex: 1;
}

.user-name {
  display: block;
  font-size: 30rpx;
  font-weight: 600;
}

.time {
  display: block;
  margin-top: 4rpx;
  font-size: 22rpx;
  color: var(--wc-text-faint);
}

.feed-signal {
  flex-shrink: 0;
}

.feed-story {
  margin-left: 18rpx;
  padding: 24rpx;
  border-radius: 28rpx;
  background:
    linear-gradient(135deg, rgba(232, 248, 241, 0.94), rgba(255, 255, 255, 0.94));
  border: 1px solid rgba(39, 92, 72, 0.07);
  display: flex;
  gap: 18rpx;
  align-items: flex-start;
}

.feed-story-mark {
  width: 54rpx;
  height: 54rpx;
  flex-shrink: 0;
  border-radius: 18rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.92);
  font-size: 26rpx;
  box-shadow: 0 10rpx 20rpx rgba(24, 39, 75, 0.06);
}

.feed-content {
  display: flex;
  flex: 1;
  font-size: 28rpx;
  line-height: 1.7;
  color: rgba(17, 24, 39, 0.92);
}

.feed-images {
  display: grid;
  gap: 12rpx;
  border-radius: 24rpx;
  overflow: hidden;
  margin: 16rpx 0 0 18rpx;
}

.feed-images.grid-1 {
  grid-template-columns: 1fr;
  aspect-ratio: 16 / 9;
}

.feed-images.grid-2 {
  grid-template-columns: repeat(2, 1fr);
  aspect-ratio: 2 / 1;
}

.feed-images.grid-3 {
  grid-template-columns: repeat(3, 1fr);
  aspect-ratio: 3 / 1;
}

.feed-img {
  width: 100%;
  height: 100%;
}

.feed-actions {
  display: flex;
  justify-content: flex-end;
  gap: 36rpx;
  margin-top: 20rpx;
  color: $text-sub;
  font-size: 24rpx;
  padding-left: 18rpx;
}

.feed-actions .action-btn.active {
  color: #d8486b;
  border-color: rgba(216, 72, 107, 0.22);
  background: rgba(255, 239, 243, 0.92);
}

.my-groups-list {
  padding: 0 30rpx 12rpx;
}

.group-item {
  display: flex;
  align-items: flex-start;
  padding: 28rpx;
  border-radius: 34rpx;
  background: $card-bg;
  border: 1px solid var(--wc-line);
  box-shadow: 0 18rpx 34rpx rgba(24, 39, 75, 0.06);
  margin-bottom: 20rpx;
  position: relative;
  overflow: hidden;
}

.group-item::before {
  content: '';
  position: absolute;
  left: 0;
  top: 24rpx;
  bottom: 24rpx;
  width: 8rpx;
  border-radius: 999rpx;
  opacity: 0.92;
}

.group-cover-thumb {
  position: relative;
  width: 108rpx;
  height: 108rpx;
  margin-right: 22rpx;
  border-radius: 30rpx;
  overflow: hidden;
  flex-shrink: 0;
  box-shadow: 0 16rpx 28rpx rgba(24, 39, 75, 0.12);
}

.group-cover-thumb__image,
.group-cover-thumb__overlay {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
}

.group-cover-thumb__overlay {
  background: linear-gradient(180deg, rgba(15, 23, 42, 0.08), rgba(15, 23, 42, 0.24));
}

.group-cover-thumb__icon {
  position: relative;
  z-index: 1;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 38rpx;
  color: white;
  font-weight: 800;
}

.group-cover-thumb.cover-ocean {
  background: linear-gradient(135deg, #4f8ef7, #54d2d2);
}

.group-cover-thumb.cover-sunrise {
  background: linear-gradient(135deg, #ff9966, #ff5e62);
}

.group-cover-thumb.cover-forest {
  background: linear-gradient(135deg, #2d6a4f, #52b788);
}

.group-cover-thumb.cover-berry {
  background: linear-gradient(135deg, #c850c0, #ffcc70);
}

.group-cover-thumb.cover-night {
  background: linear-gradient(135deg, #0f172a, #334155);
}

.group-cover-thumb.cover-sand {
  background: linear-gradient(135deg, #d9a066, #f2d0a4);
}

.group-item.tone-ocean::before {
  background: linear-gradient(180deg, rgba(47, 179, 123, 0.95), rgba(255, 193, 92, 0.58));
}

.group-item.tone-mint::before {
  background: linear-gradient(180deg, rgba(43, 167, 121, 0.95), rgba(131, 239, 218, 0.58));
}

.group-item.tone-sun::before {
  background: linear-gradient(180deg, rgba(255, 176, 32, 0.95), rgba(255, 214, 102, 0.54));
}

.group-item.tone-dusk::before {
  background: linear-gradient(180deg, rgba(99, 102, 241, 0.92), rgba(196, 181, 253, 0.56));
}

.group-info {
  flex: 1;
  min-width: 0;
}

.group-name {
  display: block;
  font-size: 30rpx;
  font-weight: 700;
  margin-bottom: 8rpx;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.group-desc {
  display: block;
  font-size: 22rpx;
  color: $text-sub;
  line-height: 1.6;
}

.group-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 10rpx;
  margin-top: 12rpx;
}

.group-readonly {
  display: block;
  margin-top: 12rpx;
  font-size: 21rpx;
  line-height: 1.6;
  color: rgba(148, 79, 27, 0.92);
}

.btn-checkin {
  min-width: 132rpx;
  margin-left: 20rpx;
  align-self: center;
}

.btn-checkin.secondary {
}

.empty-tip,
.empty-block {
  margin: 0 30rpx 24rpx;
  padding: 48rpx 36rpx;
  border-radius: 32rpx;
  background: white;
  text-align: center;
  box-shadow: $shadow;
}

.empty-icon {
  display: block;
  font-size: 64rpx;
  margin-bottom: 12rpx;
}

.empty-title {
  display: block;
  font-size: 30rpx;
  font-weight: 600;
  margin-bottom: 10rpx;
}

.empty-desc {
  display: block;
  font-size: 24rpx;
  line-height: 1.7;
  color: $text-sub;
}

.btn-primary {
  margin-top: 24rpx;
}

.fab-post {
  position: fixed;
  right: 32rpx;
  bottom: 176rpx;
  width: 96rpx;
  height: 96rpx;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--wc-primary-strong), var(--wc-primary));
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: var(--wc-shadow-float);
  font-size: 42rpx;
  z-index: 40;
}

.composer-mask {
  position: fixed;
  inset: 0;
  z-index: 1100;
  background: rgba(7, 15, 28, 0.32);
  display: flex;
  align-items: flex-end;
}

.composer-sheet {
  width: 100%;
  border-radius: 36rpx 36rpx 0 0;
  background: var(--wc-surface-strong);
  padding: 30rpx 24rpx 0;
  box-sizing: border-box;
  box-shadow: 0 -18rpx 48rpx rgba(15, 23, 42, 0.18);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.composer-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 20rpx;
  margin-bottom: 24rpx;
}

.composer-title {
  display: block;
  font-size: 34rpx;
  font-weight: 700;
  color: $text-main;
  margin-bottom: 8rpx;
}

.composer-subtitle {
  display: block;
  font-size: 22rpx;
  line-height: 1.5;
  color: $text-sub;
}

.composer-section {
  margin-bottom: 22rpx;
}

.composer-scroll {
  padding-bottom: 12rpx;
}

.composer-row {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 20rpx;
}

.composer-row-copy {
  flex: 1;
  min-width: 0;
}

.composer-label {
  display: block;
  margin-bottom: 14rpx;
  font-size: 24rpx;
  font-weight: 600;
  color: $text-main;
}

.composer-row-hint {
  display: block;
  font-size: 20rpx;
  line-height: 1.5;
  color: var(--wc-text-faint);
}

.composer-scope-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 14rpx;
}

.composer-scope-card {
  min-width: 0;
  padding: 20rpx 18rpx;
  border-radius: 26rpx;
  background: var(--wc-surface-muted);
  border: 1px solid var(--wc-line);
}

.composer-scope-card.active {
  background: rgba(47, 179, 123, 0.12);
  border-color: rgba(47, 179, 123, 0.24);
}

.composer-scope-title {
  display: block;
  font-size: 24rpx;
  font-weight: 700;
  color: $text-main;
  margin-bottom: 8rpx;
}

.composer-scope-card.active .composer-scope-title {
  color: $primary;
}

.composer-scope-desc {
  display: block;
  font-size: 20rpx;
  line-height: 1.5;
  color: $text-sub;
}

.composer-toggle {
  display: inline-flex;
  align-items: center;
  gap: 10rpx;
  padding: 6rpx;
  border-radius: 999rpx;
  background: var(--wc-surface-muted);
  border: 1px solid var(--wc-line);
}

.composer-toggle-chip {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 88rpx;
  height: 52rpx;
  padding: 0 18rpx;
  border-radius: 999rpx;
  font-size: 20rpx;
  font-weight: 600;
  color: $text-sub;
}

.composer-toggle-chip.active {
  background: var(--wc-primary);
  color: #fff;
}

.composer-rail {
  white-space: nowrap;
}

.composer-chip-row {
  display: inline-flex;
  gap: 12rpx;
}

.composer-chip {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 132rpx;
  height: 64rpx;
  padding: 0 22rpx;
  border-radius: 999rpx;
  background: var(--wc-surface-muted);
  border: 1px solid var(--wc-line);
  color: $text-sub;
  font-size: 22rpx;
  font-weight: 600;
}

.composer-chip.active {
  background: rgba(47, 179, 123, 0.12);
  border-color: rgba(47, 179, 123, 0.24);
  color: $primary;
}

.composer-empty-tip {
  padding: 18rpx 20rpx;
  border-radius: 22rpx;
  background: rgba(255, 193, 7, 0.08);
  border: 1px solid rgba(255, 193, 7, 0.14);
  font-size: 21rpx;
  line-height: 1.6;
  color: $text-sub;
}

.composer-type-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 12rpx;
}

.composer-type {
  min-width: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8rpx;
  padding: 18rpx 10rpx;
  border-radius: 24rpx;
  background: var(--wc-surface-muted);
  border: 1px solid var(--wc-line);
}

.composer-type.active {
  background: rgba(47, 179, 123, 0.12);
  border-color: rgba(47, 179, 123, 0.24);
}

.composer-type-icon {
  font-size: 28rpx;
}

.composer-type-text {
  font-size: 20rpx;
  color: $text-sub;
}

.composer-type.active .composer-type-text {
  color: $primary;
  font-weight: 700;
}

.composer-record-guide {
  margin-top: 16rpx;
  padding: 20rpx 22rpx;
  border-radius: 24rpx;
  background: rgba(43, 167, 121, 0.1);
  border: 1px solid rgba(43, 167, 121, 0.18);
}

.composer-record-guide__title {
  display: block;
  font-size: 24rpx;
  line-height: 1.4;
  font-weight: 700;
  color: var(--wc-success);
}

.composer-record-guide__desc {
  display: block;
  margin-top: 8rpx;
  font-size: 21rpx;
  line-height: 1.6;
  color: $text-sub;
}

.composer-form-card {
  padding: 22rpx;
  border-radius: 26rpx;
  background: rgba(248, 250, 252, 0.9);
  border: 1px solid var(--wc-line);
}

.composer-form-group {
  margin-bottom: 22rpx;
}

.composer-form-group:last-child {
  margin-bottom: 0;
}

.composer-form-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 18rpx;
}

.composer-field-tip {
  margin-top: 10rpx;
}

.composer-hint-card {
  margin-top: 18rpx;
  font-size: 21rpx;
  line-height: 1.6;
  color: $text-sub;
}

.composer-food-estimator {
  margin-top: 18rpx;
  padding: 18rpx;
  border-radius: 20rpx;
  background: rgba(47, 179, 123, 0.08);
  border: 1px solid rgba(47, 179, 123, 0.16);
}

.composer-food-estimator__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 18rpx;
  margin-bottom: 14rpx;
}

.composer-food-estimator__label {
  font-size: 22rpx;
  font-weight: 600;
  color: $text-sub;
}

.composer-food-estimator__portion {
  display: flex;
  align-items: center;
  gap: 10rpx;
  font-size: 22rpx;
  color: $text-sub;
}

.composer-food-estimator__portion-input {
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

.composer-food-estimator__scroll {
  width: 100%;
  white-space: nowrap;
}

.composer-food-estimator__options {
  display: inline-flex;
  gap: 12rpx;
  padding: 2rpx 2rpx 6rpx;
}

.composer-food-estimator__chip {
  display: inline-flex;
  flex-direction: column;
  justify-content: center;
  gap: 4rpx;
  flex-shrink: 0;
  min-width: 132rpx;
  min-height: 76rpx;
  padding: 0 18rpx;
  border-radius: 18rpx;
  background: rgba(255, 255, 255, 0.96);
  border: 1px solid var(--wc-line);
  color: $text-main;
  font-size: 24rpx;
  line-height: 1.25;
}

.composer-food-estimator__chip text:last-child {
  font-size: 20rpx;
  color: $text-sub;
}

.composer-food-estimator__chip.active {
  border-color: var(--wc-primary);
  color: var(--wc-primary);
  background: #fff;
}

.composer-unit {
  flex-shrink: 0;
  color: $text-sub;
  font-size: 24rpx;
}

.composer-record-textarea {
  min-height: 132rpx;
}

.composer-segment-row {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 10rpx;
}

.composer-segment {
  min-width: 0;
  height: 56rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 18rpx;
  background: rgba(255, 255, 255, 0.92);
  border: 1px solid var(--wc-line);
  color: $text-sub;
  font-size: 21rpx;
  font-weight: 600;
  line-height: 1;
}

.composer-segment.active {
  background: var(--wc-primary-soft);
  border-color: rgba(47, 179, 123, 0.24);
  color: var(--wc-primary-strong);
}

.composer-exercise-dictionary {
  margin-top: 18rpx;
}

.composer-exercise-scroll {
  width: 100%;
  margin-top: 12rpx;
  white-space: nowrap;
}

.composer-exercise-options {
  display: inline-flex;
  gap: 12rpx;
  padding: 2rpx 2rpx 6rpx;
}

.composer-exercise-chip {
  flex-shrink: 0;
  min-height: 54rpx;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 0 22rpx;
  border-radius: 999rpx;
  background: rgba(255, 255, 255, 0.92);
  border: 1px solid var(--wc-line);
  color: $text-sub;
  font-size: 22rpx;
  font-weight: 600;
  line-height: 54rpx;
}

.composer-exercise-chip.active {
  background: var(--wc-primary-soft);
  border-color: rgba(47, 179, 123, 0.24);
  color: var(--wc-primary-strong);
}

.composer-row--center {
  align-items: center;
}

.composer-habit-list {
  display: flex;
  flex-direction: column;
  gap: 12rpx;
  margin-top: 18rpx;
}

.composer-habit-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16rpx;
  padding: 18rpx;
  border-radius: 22rpx;
  background: rgba(255, 255, 255, 0.92);
  border: 1px solid var(--wc-line);
}

.composer-habit-item.active {
  background: var(--wc-primary-soft);
  border-color: rgba(47, 179, 123, 0.24);
}

.composer-habit-main {
  min-width: 0;
  display: flex;
  align-items: center;
  gap: 16rpx;
}

.composer-habit-icon {
  width: 52rpx;
  height: 52rpx;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 16rpx;
  background: rgba(255, 255, 255, 0.9);
  font-size: 26rpx;
}

.composer-habit-copy {
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 4rpx;
}

.composer-habit-name {
  display: block;
  overflow: hidden;
  color: $text-main;
  font-size: 24rpx;
  font-weight: 700;
  line-height: 1.35;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.composer-habit-desc {
  display: block;
  color: $text-sub;
  font-size: 20rpx;
  line-height: 1.45;
}

.composer-habit-check {
  flex-shrink: 0;
  color: $primary;
  font-size: 28rpx;
  font-weight: 700;
}

.composer-empty-tip--error {
  background: rgba(239, 68, 68, 0.08);
  border-color: rgba(239, 68, 68, 0.16);
  color: #b42318;
}

.composer-empty-title,
.composer-empty-desc {
  display: block;
}

.composer-empty-title {
  color: $text-main;
  font-size: 23rpx;
  font-weight: 700;
  line-height: 1.4;
}

.composer-empty-desc {
  margin-top: 6rpx;
  color: inherit;
  font-size: 21rpx;
  line-height: 1.6;
}

.composer-textarea {
  min-height: 180rpx;
}

.composer-meta {
  margin-top: 12rpx;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16rpx;
  font-size: 20rpx;
  color: var(--wc-text-faint);
}

.composer-image-grid {
  margin-top: 18rpx;
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16rpx;
}

.composer-image-item {
  position: relative;
  aspect-ratio: 1;
  border-radius: 24rpx;
  overflow: hidden;
  background: rgba(15, 23, 42, 0.06);
}

.composer-image-preview {
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
  color: #fff;
  font-size: 22rpx;
  font-weight: 600;
  line-height: 1;
}

.upload-image-badge--error {
  background: #ef4444;
}

.upload-image-retry {
  position: absolute;
  top: 12rpx;
  right: 12rpx;
  z-index: 3;
  padding: 8rpx 14rpx;
  border-radius: 999rpx;
  background: var(--wc-primary);
  color: #fff;
  font-size: 20rpx;
  font-weight: 700;
  line-height: 1;
  box-shadow: 0 8rpx 18rpx rgba(47, 128, 237, 0.24);
}

.composer-image-remove {
  position: absolute;
  top: 12rpx;
  left: 12rpx;
  z-index: 2;
  width: 40rpx;
  height: 40rpx;
  border-radius: 50%;
  background: rgba(15, 23, 42, 0.72);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 26rpx;
  font-weight: 700;
}

.composer-actions {
  display: flex;
  gap: 16rpx;
  margin-top: 18rpx;
  padding-top: 18rpx;
  padding-bottom: calc(12rpx + env(safe-area-inset-bottom));
  border-top: 1px solid var(--wc-line);
}

.composer-btn {
  flex: 1;
}

.page-bottom-space {
  height: 120rpx;
}
</style>
