<template>
  <view class="page-container wc-page-enter">
    <prototype-header :title="detailTitle" back-url="/pages/circle/index" />

    <scroll-view class="content wc-section-enter" scroll-y>
      <view v-if="loading" class="loading-block">圈子详情加载中...</view>

      <view v-else-if="inviteLoadErrorMessage || inviteRequiresLogin || privateInviteReady || privateInvitePending" class="invite-wrap">
        <view class="invite-panel" :class="`invite-panel--${invitePanelTone}`">
          <view class="invite-mark">
            <text>{{ inviteInitial }}</text>
          </view>
          <text class="invite-kicker">{{ inviteKicker }}</text>
          <text class="invite-title">{{ inviteStateTitle }}</text>
          <text class="invite-desc">{{ inviteStateDescription }}</text>

          <view class="invite-meta">
            <view class="invite-meta__item">
              <text class="invite-meta__label">可见范围</text>
              <text class="invite-meta__value">{{ inviteVisibilityText }}</text>
            </view>
            <view class="invite-meta__item">
              <text class="invite-meta__label">加入方式</text>
              <text class="invite-meta__value">{{ inviteJoinModeText }}</text>
            </view>
          </view>

          <view class="invite-actions">
            <button
              v-if="inviteRequiresLogin || privateInviteReady"
              class="flow-btn flow-btn--primary wc-pressable invite-action"
              :loading="joiningInvite || loadingInvite"
              @tap="inviteRequiresLogin ? goLoginForInvite() : joinInvitedCircle()"
            >
              {{ inviteActionText }}
            </button>
            <button
              v-if="inviteLoadErrorMessage"
              class="flow-btn flow-btn--secondary wc-pressable invite-action"
              @tap="goBack"
            >
              返回圈子页
            </button>
          </view>
          <text v-if="inviteFootnote" class="invite-footnote">{{ inviteFootnote }}</text>
        </view>
      </view>

      <view v-else-if="forbiddenState" class="empty-wrap">
        <app-empty-state
          icon="🔒"
          title="仅圈子成员可查看"
          description="这个圈子仅对成员开放，请通过有效邀请加入后查看完整内容。"
        />
      </view>

      <view v-else-if="detail" class="content-inner">
        <view class="group-info-card" :class="[groupCoverClass, { 'group-info-card--image': circleImageCover }]">
          <resolved-image
            class="group-cover-image"
            v-if="circleImageCover"
            :src="normalizeCircleCoverUrl(detail.circle.coverUrl)"
            mode="aspectFill"
          />
          <view class="group-info-bg"></view>
          <view class="group-tags">
            <text class="wc-badge wc-badge--primary">{{ circleTypeLabel(detail.circle.type) }}</text>
            <text v-if="detail.joined" class="wc-badge wc-badge--success">已加入</text>
            <text v-if="detail.memberRole" class="wc-badge wc-badge--soft">{{ circleRoleLabel(detail.memberRole) }}</text>
            <text v-if="detail.circle.lifecycleStatus === 'expired'" class="wc-badge wc-badge--warning">{{ circleLifecycleLabel(detail.circle.lifecycleStatus) }}</text>
          </view>
          <text class="group-name">{{ detail.circle.name }}</text>
          <text class="group-desc">{{ detail.circle.description || '这个圈子还没有补充详细介绍。' }}</text>
          <text v-if="detail.circle.readOnly" class="group-readonly">{{ circleLifecycleHint(detail.circle.readOnly, detail.circle.readOnlyReason) }}</text>

          <view class="group-meta-row">
            <view class="group-stat-chip">
              <text class="group-stat-chip__value">{{ detail.circle.memberCount || 0 }}</text>
              <text class="group-stat-chip__label">成员</text>
            </view>
            <view class="group-stat-chip">
              <text class="group-stat-chip__value">{{ detail.ruleSummary?.durationText || formatCircleDuration(detail.circle.durationDays) }}</text>
              <text class="group-stat-chip__label">周期</text>
            </view>
            <view class="meta-side">
              <text>{{ detail.circle.categoryName || '圈子' }}</text>
              <text>{{ detail.circle.endDate ? `结束于 ${detail.circle.endDate}` : '长期挑战' }}</text>
            </view>
          </view>
        </view>

        <view class="goal-card">
          <view class="goal-header">
            <text class="goal-label">🎯 圈子概览</text>
            <text class="wc-badge wc-badge--warning">{{ formatCircleDuration(detail.circle.durationDays) }}</text>
          </view>
          <view class="stat-grid">
            <view class="stat-item">
              <text class="stat-value">{{ detail.circle.memberCount || 0 }}</text>
              <text class="stat-label">成员数</text>
            </view>
            <view class="stat-item">
              <text class="stat-value">{{ formatDepositText(detail.circle.depositRequired) }}</text>
              <text class="stat-label">押金设置</text>
            </view>
            <view class="stat-item">
              <text class="stat-value">{{ detail.circle.lifecycleStatus === 'expired' ? '已结束' : '进行中' }}</text>
              <text class="stat-label">状态</text>
            </view>
          </view>

          <view class="action-row">
            <button class="btn-primary flow-btn flow-btn--primary flow-btn--compact" :loading="membershipLoading" @tap="handleMembership">
              {{ detail.joined ? '退出圈子' : '加入圈子' }}
            </button>
            <button
              v-if="detail.joined"
              class="btn-secondary flow-btn flow-btn--secondary flow-btn--compact"
              :class="{ active: isMainCircle }"
              :disabled="detail.circle.readOnly"
              :loading="settingMainCircle"
              @tap="setMainCircle"
            >
              {{ isMainCircle ? '当前主圈子' : '设为主圈子' }}
            </button>
          </view>
          <view v-if="canManageCircle || canInviteCircleMember" class="manage-row">
            <button v-if="canManageCircle" class="btn-secondary flow-btn flow-btn--secondary flow-btn--compact" :disabled="detail.circle.readOnly" @tap="goEditCircle">编辑圈子</button>
            <button
              v-if="canInviteCircleMember"
              class="btn-secondary flow-btn flow-btn--secondary flow-btn--compact"
              open-type="share"
              :disabled="detail.circle.readOnly || (detail.circle.type === '1' && !privateInviteToken)"
            >
              {{ detail.circle.readOnly ? '圈子已结束' : detail.circle.type === '1' && !privateInviteToken ? '准备邀请中' : '邀请成员' }}
            </button>
          </view>
          <view v-if="canInviteCircleMember && privateInviteErrorMessage" class="manage-inline-error">
            <text>{{ privateInviteErrorMessage }}</text>
            <button class="flow-btn flow-btn--secondary flow-btn--compact" @tap="ensureInviteToken">重新准备邀请</button>
          </view>
        </view>

        <view class="detail-tab-bar wc-tab-rail">
          <view
            v-for="tab in detailTabs"
            :key="tab.key"
            class="wc-tab-item wc-tab-item--full wc-pressable"
            :class="{ active: activeTab === tab.key }"
            @tap="selectTab(tab.key)"
          >
            {{ tab.label }}
          </view>
        </view>

        <view v-if="activeTab === 'overview'">
          <app-section-header compact title="规则与任务" subtitle="生命周期、今日任务、目标口径和押金状态统一收在这里" />
          <view class="overview-list">
            <view class="overview-card">
              <view class="overview-card__row">
                <text class="overview-card__label">当前状态</text>
                <text class="overview-card__value">{{ detail.circle.lifecycleStatus === 'expired' ? '圈子已结束，可继续查看历史内容' : '挑战进行中' }}</text>
              </view>
              <view class="overview-card__row" v-if="detail.circle.readOnlyReason">
                <text class="overview-card__label">状态说明</text>
                <text class="overview-card__value">{{ detail.circle.readOnlyReason }}</text>
              </view>
              <view class="overview-card__row">
                <text class="overview-card__label">加入方式</text>
                <text class="overview-card__value">{{ detail.ruleSummary?.joinRuleText || '暂未设置' }}</text>
              </view>
              <view class="overview-card__row">
                <text class="overview-card__label">挑战周期</text>
                <text class="overview-card__value">{{ detail.ruleSummary?.durationText || formatCircleDuration(detail.circle.durationDays) }}</text>
              </view>
              <view class="overview-card__row">
                <text class="overview-card__label">规则说明</text>
                <text class="overview-card__value">{{ detail.ruleSummary?.penaltyRuleText || '当前未设置额外规则' }}</text>
              </view>
            </view>

            <view class="overview-card">
              <view class="overview-card__header">
                <text class="overview-card__title">今日任务</text>
                <text class="wc-badge wc-badge--soft">{{ detail.taskSummary?.completedCount || 0 }}/{{ detail.taskSummary?.totalCount || 0 }}</text>
              </view>
              <view v-if="detail.todayTasks.length" class="task-list">
                <view v-for="task in detail.todayTasks" :key="task.id" class="task-item">
                  <view class="task-item__copy">
                    <text class="task-item__name">{{ task.goalName || '未命名任务' }}</text>
                    <text class="task-item__desc">{{ task.description || `${task.currentValue || 0}/${task.targetValue || 0}${task.targetUnit || ''}` }}</text>
                    <text class="task-item__meta">{{ task.periodLabel || '每日' }} · {{ task.verificationTypeLabel || '自动统计' }}</text>
                  </view>
                  <view class="task-item__side">
                    <text class="task-item__progress">{{ task.currentValue || 0 }}/{{ task.targetValue || 0 }}{{ task.targetUnit || '' }}</text>
                    <button
                      v-if="task.manual && !task.completed"
                      class="flow-btn flow-btn--secondary flow-btn--compact"
                      :disabled="task.readOnly || completingTaskId === task.id"
                      :loading="completingTaskId === task.id"
                      @tap="completeTask(task)"
                    >
                      完成
                    </button>
                    <text v-else class="wc-badge" :class="task.completed ? 'wc-badge--success' : 'wc-badge--soft'">
                      {{ task.completed ? '已完成' : '进行中' }}
                    </text>
                  </view>
                </view>
              </view>
              <view v-else class="empty-inline">{{ detail.circle.readOnly ? '圈子已结束，这里会保留之前的任务完成情况。' : '当前没有可执行的圈子任务。' }}</view>
              <view v-if="detail.taskSummary?.pendingPenaltyCount" class="overview-callout">
                <text>有 {{ detail.taskSummary.pendingPenaltyCount }} 项任务还没完成，后续可能按圈子规则处理</text>
                <text>{{ detail.taskSummary.penaltyRuleText || '请管理员后续处理。' }}</text>
              </view>
            </view>

            <view class="overview-card">
              <view class="overview-card__header">
                <text class="overview-card__title">目标列表</text>
                <text class="wc-badge wc-badge--soft">{{ detail.goalList.length }} 项</text>
              </view>
              <view v-if="detail.goalList.length" class="goal-list">
                <view v-for="goal in detail.goalList" :key="goal.id" class="goal-list__item">
                  <view class="goal-list__copy">
                    <text class="goal-list__name">{{ goal.goalName || '未命名目标' }}</text>
                    <text class="goal-list__desc">{{ goal.description || `${goal.targetValue || 0}${goal.targetUnit || ''}` }}</text>
                    <text class="goal-list__meta">{{ goal.periodLabel || '每日' }} · {{ goal.verificationTypeLabel || '自动统计' }}</text>
                  </view>
                  <text class="wc-badge wc-badge--soft">{{ goal.required ? '必做' : '可选' }}</text>
                </view>
              </view>
              <view v-else class="empty-inline">当前还没有配置圈子目标</view>
            </view>

            <view class="overview-card">
              <view class="overview-card__header">
                <text class="overview-card__title">排行规则摘要</text>
                <text class="wc-badge wc-badge--soft">{{ detail.rankingSummary?.periodLabel || '本周排行' }}</text>
              </view>
              <view v-if="detail.rankingSummary?.leader" class="ranking-summary-card" @tap="goUserProfile(detail.rankingSummary.leader.userId)">
                <text class="ranking-summary-card__rule">{{ detail.rankingSummary.ruleText || '按已完成任务数和圈内打卡次数排序' }}</text>
                <view class="ranking-summary-card__leader">
                  <resolved-image
                    class="ranking-list__avatar"
                    :src="detail.rankingSummary.leader.avatar"
                    mode="aspectFill"
                    shape="avatar"
                    fallback-class="ranking-list__avatar ranking-list__avatar--fallback"
                    :fallback-text="memberInitial(detail.rankingSummary.leader.nickname)"
                  />
                  <view class="ranking-list__copy">
                    <text class="ranking-list__name">当前领先：{{ detail.rankingSummary.leader.nickname || `用户${detail.rankingSummary.leader.userId}` }}</text>
                    <text class="ranking-list__desc">完成 {{ detail.rankingSummary.leader.completedTaskCount || 0 }} 项任务 · 圈内打卡 {{ detail.rankingSummary.leader.circleCheckins || 0 }} 次</text>
                  </view>
                </view>
                <text class="ranking-summary-card__time">更新时间 {{ detail.rankingSummary.updatedAt || '刚刚' }}</text>
              </view>
              <view v-else class="empty-inline">完整排行榜放在成员页统一查看</view>
            </view>

            <view class="overview-card">
              <view class="overview-card__header">
                <text class="overview-card__title">押金机制</text>
                <text class="wc-badge wc-badge--warning">暂未开放</text>
              </view>
              <view class="deposit-grid">
                <view class="deposit-grid__item">
                  <text class="deposit-grid__value">{{ currentDepositStatusText }}</text>
                  <text class="deposit-grid__label">我的状态</text>
                </view>
                <view class="deposit-grid__item">
                  <text class="deposit-grid__value">0</text>
                  <text class="deposit-grid__label">当前费用</text>
                </view>
                <view class="deposit-grid__item">
                  <text class="deposit-grid__value">待开放</text>
                  <text class="deposit-grid__label">后续接入</text>
                </view>
              </view>
            </view>
          </view>
        </view>

        <view v-else-if="activeTab === 'feeds'">
          <app-section-header
            compact
            title="圈内打卡"
            :subtitle="feedSectionSubtitle"
          >
            <template #actions>
              <button
                v-if="detail.joined && !detail.circle.readOnly"
                class="circle-checkin-button flow-btn flow-btn--primary flow-btn--compact wc-pressable"
                @tap="showComposer"
              >
                发布打卡
              </button>
            </template>
          </app-section-header>
          <view
            v-if="detail.joined && !detail.circle.readOnly"
            class="checkin-entry-card wc-pressable"
            @tap="showComposer"
          >
            <view class="checkin-entry-card__mark">✓</view>
            <view class="checkin-entry-card__copy">
              <text class="checkin-entry-card__title">今日圈内打卡</text>
              <text class="checkin-entry-card__desc">可关联体重、饮食、运动、饮水或习惯记录。</text>
            </view>
            <text class="checkin-entry-card__action">去打卡</text>
          </view>
          <view v-if="feedsLoading" class="loading-block">圈内打卡加载中...</view>
          <view v-else-if="feedLoadError" class="section-padding">
            <app-empty-state icon="⚠️" title="打卡加载失败" :description="feedLoadError">
              <button class="flow-btn flow-btn--secondary flow-btn--compact" @tap="reloadFeeds">重新整理</button>
            </app-empty-state>
          </view>
          <view v-else-if="circleFeeds.length" class="feed-list">
            <view v-for="feed in circleFeeds" :key="feed.id" class="feed-item feed-item--rich" @tap="goFeedDetail(feed.id)">
              <view class="feed-head">
                <view class="feed-author" @tap.stop="goUserProfile(feed.userId)">
                  <resolved-image
                    class="feed-author__avatar"
                    :src="feed.userAvatar"
                    mode="aspectFill"
                    shape="avatar"
                    fallback-class="feed-author__avatar feed-author__avatar--fallback"
                    :fallback-text="memberInitial(feed.userNickname)"
                  />
                  <view class="feed-head-copy">
                    <text class="feed-user">{{ feed.userNickname || '圈友' }}</text>
                    <text class="feed-time">{{ circleFeedTimeText(feed.createdAt) }}</text>
                  </view>
                </view>
                <view class="feed-tags">
                  <text v-if="feed.isFeatured" class="wc-badge wc-badge--warning">精选</text>
                  <text class="feed-visibility">{{ feed.visibilityScope === 'public' ? '公开并同步' : '圈内可见' }}</text>
                </view>
              </view>
              <text class="feed-content">{{ feed.content || '这条打卡还没有文字内容。' }}</text>
              <view v-if="feed.images?.length" class="feed-images" :class="`grid-${Math.min(feed.images.length, 3)}`">
                <resolved-image
                  v-for="(image, index) in feed.images.slice(0, 3)"
                  :key="`${feed.id}-${index}`"
                  class="feed-image"
                  :src="image"
                  mode="aspectFill"
                  :preview-list="feed.images"
                  :preview-index="index"
                />
              </view>
              <view class="feed-foot">
                <text>❤️ {{ feed.likesCount || 0 }}</text>
                <text>💬 {{ feed.commentsCount || 0 }}</text>
              </view>
            </view>
            <button v-if="feedHasMore && !feedsLoadingMore" class="flow-btn flow-btn--secondary flow-btn--compact load-more-btn" @tap="loadMoreFeeds">
              加载更多打卡
            </button>
            <view v-else-if="feedsLoadingMore" class="loading-inline">更多打卡加载中...</view>
          </view>
          <view v-else class="empty-inline">这个圈子还没有人打卡</view>
        </view>

        <view v-else>
          <app-section-header compact title="成员与排行" :subtitle="memberSectionSubtitle" />
          <view v-if="rankingLoading" class="loading-block">排行榜加载中...</view>
          <view v-else-if="rankingLoadError" class="section-padding">
            <app-empty-state icon="⚠️" title="排行榜加载失败" :description="rankingLoadError">
              <button class="flow-btn flow-btn--secondary flow-btn--compact" @tap="reloadRanking">重新整理</button>
            </app-empty-state>
          </view>
          <view v-else-if="circleRanking.length" class="ranking-list ranking-list--full">
            <view v-for="item in circleRanking" :key="`${item.userId}-${item.rankNum}`" class="ranking-list__item" @tap="goUserProfile(item.userId)">
              <text class="ranking-list__rank">#{{ item.rankNum || '-' }}</text>
              <resolved-image
                class="ranking-list__avatar"
                :src="item.avatar"
                mode="aspectFill"
                shape="avatar"
                fallback-class="ranking-list__avatar ranking-list__avatar--fallback"
                :fallback-text="memberInitial(item.nickname)"
              />
              <view class="ranking-list__copy">
                <text class="ranking-list__name">{{ item.nickname || `用户${item.userId}` }}</text>
                <text class="ranking-list__desc">完成 {{ item.completedTaskCount || 0 }} 项任务 · 圈内打卡 {{ item.circleCheckins || 0 }} 次</text>
              </view>
            </view>
          </view>
          <view v-else class="empty-inline">当前还没有可展示的排行榜</view>

          <view v-if="showJoinRequestSection" class="approval-section">
            <app-section-header
              compact
              title="待审批申请"
              :subtitle="detail.pendingJoinRequestCount ? `当前有 ${detail.pendingJoinRequestCount} 个待处理申请` : '加入申请会在这里集中处理'"
            />
            <view v-if="loadingJoinRequests" class="loading-block">申请列表加载中...</view>
            <view v-else-if="pendingJoinRequests.length" class="request-list">
              <view v-for="request in pendingJoinRequests" :key="request.id" class="request-item">
                <resolved-image
                  class="request-avatar"
                  :src="request.avatar"
                  mode="aspectFill"
                  shape="avatar"
                  fallback-class="request-avatar request-avatar--fallback"
                  :fallback-text="memberInitial(request.nickname)"
                />
                <view class="request-copy">
                  <text class="request-name">{{ request.nickname || `用户${request.userId}` }}</text>
                  <text class="request-time">{{ request.requestedAt || '刚刚申请' }}</text>
                </view>
                <view class="request-actions">
                  <button
                    class="flow-btn flow-btn--secondary flow-btn--compact"
                    :disabled="detail.circle.readOnly"
                    :loading="processingJoinRequestId === request.id"
                    @tap="reviewJoinRequest(request, false)"
                  >拒绝</button>
                  <button
                    class="flow-btn flow-btn--primary flow-btn--compact"
                    :disabled="detail.circle.readOnly"
                    :loading="processingJoinRequestId === request.id"
                    @tap="reviewJoinRequest(request, true)"
                  >通过</button>
                </view>
              </view>
            </view>
            <view v-else-if="joinRequestLoadError" class="empty-inline empty-inline--error">
              <text>{{ joinRequestLoadError }}</text>
              <button class="flow-btn flow-btn--secondary flow-btn--compact" @tap="loadJoinRequests">重新整理申请</button>
            </view>
            <view v-else class="empty-inline">当前没有待审批申请</view>
          </view>

          <view class="member-section" :class="{ 'member-section--after-approval': showJoinRequestSection }">
            <app-section-header compact title="成员列表" :subtitle="memberListSubtitle" />
            <view v-if="membersLoading" class="loading-block">成员列表加载中...</view>
            <view v-else-if="memberLoadError" class="section-padding">
              <app-empty-state icon="⚠️" title="成员加载失败" :description="memberLoadError">
                <button class="flow-btn flow-btn--secondary flow-btn--compact" @tap="reloadMembers">重新整理</button>
              </app-empty-state>
            </view>
            <view v-else-if="circleMembers.length" class="member-list">
              <view v-for="member in circleMembers" :key="member.membershipId" class="member-item member-item--full">
                <view class="member-main" @tap="goUserProfile(member.userId)">
                  <resolved-image
                    class="rank-avatar"
                    :src="member.avatar"
                    mode="aspectFill"
                    shape="avatar"
                    fallback-class="rank-avatar rank-avatar--fallback"
                    :fallback-text="memberInitial(member.nickname)"
                  />
                  <view class="rank-info">
                    <text class="rank-name">{{ member.nickname || `用户${member.userId}` }}</text>
                    <text class="rank-desc">{{ circleRoleLabel(member.role) }} · {{ member.joinedAt || '加入时间待补充' }}</text>
                    <text class="rank-desc">连续 {{ member.streakDays || 0 }} 天 · 打卡 {{ member.totalCheckins || 0 }} 次</text>
                  </view>
                </view>
                <view class="member-side">
                  <text class="rank-value">{{ depositStatusLabel(member.depositStatus, detail?.depositSummary?.enabled) }}</text>
                  <button
                    v-if="member.removable"
                    class="flow-btn flow-btn--secondary flow-btn--compact"
                    :loading="removingMemberUserId === member.userId"
                    @tap="confirmRemoveMember(member)"
                  >
                    移除
                  </button>
                </view>
              </view>
              <button v-if="memberHasMore && !membersLoadingMore" class="flow-btn flow-btn--secondary flow-btn--compact load-more-btn" @tap="loadMoreMembers">
                加载更多成员
              </button>
              <view v-else-if="membersLoadingMore" class="loading-inline">更多成员加载中...</view>
            </view>
            <view v-else class="empty-inline">还没有可展示的成员列表</view>
          </view>
        </view>
      </view>

      <view v-else class="empty-wrap">
        <app-empty-state
          :icon="loadErrorMessage ? '⚠️' : '⭕'"
          :title="loadErrorMessage ? '圈子详情加载失败' : '圈子不存在'"
          :description="loadErrorMessage || '请返回上一级重新选择圈子。'"
        >
          <button
            v-if="loadErrorMessage"
            class="flow-btn flow-btn--secondary flow-btn--compact wc-pressable"
            @tap="loadDetail"
          >
            重新整理
          </button>
        </app-empty-state>
      </view>

      <view class="page-bottom-space"></view>
    </scroll-view>

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
            <text class="composer-label">当前圈子</text>
            <view class="composer-current-circle">
              <text class="composer-current-name">{{ detail?.circle.name || '当前圈子' }}</text>
              <text class="composer-current-desc">默认发布到当前圈子里，圈友都能看到</text>
            </view>
          </view>

          <view class="composer-section">
            <text class="composer-label">打卡类型</text>
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
                <text class="composer-label">是否公开</text>
                <text class="composer-row-hint">打开后这条打卡会出现在发现页公开内容里，同时保留在当前圈子。</text>
              </view>
              <view class="composer-toggle">
                <view
                  class="composer-toggle-chip wc-pressable"
                  :class="{ active: composerVisibilityScope === 'circle' }"
                  @tap="composerVisibilityScope = 'circle'"
                >仅圈内</view>
                <view
                  class="composer-toggle-chip wc-pressable"
                  :class="{ active: composerVisibilityScope === 'public' }"
                  @tap="composerVisibilityScope = 'public'"
                >公开</view>
              </view>
            </view>
          </view>

          <view class="composer-section">
            <view class="composer-row">
              <view class="composer-row-copy">
                <text class="composer-label">同步至其他圈子</text>
                <text class="composer-row-hint">{{ composerCircleHint }}</text>
              </view>
              <view class="composer-toggle">
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

            <scroll-view v-if="composerSyncEnabled && syncCircleOptions.length" class="composer-rail" scroll-x>
              <view class="composer-chip-row">
                <view
                  v-for="circle in syncCircleOptions"
                  :key="`composer-circle-${circle.id}`"
                  class="composer-chip wc-pressable"
                  :class="{ active: composerSyncCircleIds.includes(circle.id) }"
                  @tap="toggleComposerCircle(circle.id)"
                >
                  {{ circle.name }}
                </view>
              </view>
            </scroll-view>

            <view v-else-if="composerSyncEnabled && !syncCircleOptions.length" class="composer-empty-tip">
              {{ syncCircleLoadError || '你当前没有其他可同步的圈子。' }}
            </view>
            <view v-if="composerSyncEnabled && syncCircleLoadError" class="composer-retry-row">
              <button class="flow-btn flow-btn--secondary flow-btn--compact" @tap="loadMyCircleOptions">重新整理圈子</button>
            </view>
          </view>

          <view v-if="!composerRecordBacked" class="composer-section">
            <text class="composer-label">打卡内容</text>
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
                <text class="composer-label">{{ composerRecordBacked ? composerRecordImageTitle : '打卡图片' }}</text>
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
import { onLoad, onShareAppMessage, onShareTimeline, onShow } from '@dcloudio/uni-app'
import AppEmptyState from '@/components/AppEmptyState/index.vue'
import AppSectionHeader from '@/components/AppSectionHeader/index.vue'
import PrototypeHeader from '@/components/PrototypeHeader/index.vue'
import ResolvedImage from '@/components/ResolvedImage/index.vue'
import {
  acceptCircleInvite,
  approveCircleJoinRequest,
  completeCircleTask,
  createCircleInviteToken,
  getCircleDetail,
  getCircleFeeds,
  getCircleJoinRequests,
  getCircleMembers,
  getCircleRanking,
  getMyCircles,
  joinCircle,
  leaveCircle,
  removeCircleMember,
  rejectCircleJoinRequest,
  resolveCircleInvite
} from '@/api/circle'
import { checkinHabit, getHabitStats, getTodayHabits } from '@/api/habit'
import { publishFeed, uploadFeedImage } from '@/api/feed'
import { createExerciseRecord, createFoodRecord, createWaterRecord, createWeightRecord, getExerciseRecordList } from '@/api/record'
import type {
  CircleFeedCard,
  CircleDetailPayload,
  CircleInvitePreview,
  CircleJoinRequestCard,
  CircleMemberItem,
  CircleRankingItem,
  CircleTaskItem,
  FeedCreatePayload,
  FeedVisibilityScope,
  HabitItem,
  MyCircleCard,
  RecordFeedDraftPayload
} from '@/types/api'
import {
  circleCoverClass,
  circleFeedTimeText,
  circleLifecycleHint,
  circleLifecycleLabel,
  circleRoleLabel,
  circleTypeLabel,
  formatCircleDuration,
  formatDepositText,
  isRemoteCircleCover,
  normalizeCircleCoverUrl,
  resolveCircleShareImage
} from '@/utils/circle'
import { useUserStore } from '@/stores/user'
import { captureAchievementState, notifyAchievementUnlocks } from '@/utils/achievementReminder'
import { normalizeExerciseType, normalizeExerciseTypeUsage } from '@/utils/exercise'
import { getSafeTop, getTopBarHeight } from '@/utils/mobile'
import { createOnShowSuspendGuard } from '@/utils/page'
import { openLoginPage } from '@/utils/request'
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

type ComposerRecordType = 'weight' | 'diet' | 'exercise' | 'water' | 'habit'

interface FeedDetailReturnSnapshot {
  id: number
  circleId?: number | null
  originCircleId?: number | null
  likesCount?: number | null
  commentsCount?: number | null
  likedByMe?: boolean | null
  commentEnabled?: boolean | null
  deleted?: boolean
}

const FEED_DETAIL_RETURN_STORAGE_KEY = 'circleFeedDetailReturnSnapshot'
const userStore = useUserStore()
const safeTop = getSafeTop(20)
const topBarHeight = getTopBarHeight(56)
const windowHeight = typeof uni.getWindowInfo === 'function' ? uni.getWindowInfo().windowHeight : uni.getSystemInfoSync().windowHeight
const composerTopGap = Math.max(topBarHeight + 12, safeTop + 72, 120)
const composerSheetHeight = Math.max(360, windowHeight - composerTopGap)
const composerSheetStyle = {
  height: `${composerSheetHeight}px`
}
const composerScrollStyle = {
  height: `${Math.max(220, composerSheetHeight - 196)}px`
}
const circleId = ref<number | null>(null)
const loading = ref(false)
const membershipLoading = ref(false)
const settingMainCircle = ref(false)
const forbiddenState = ref(false)
const loadErrorMessage = ref('')
const detail = ref<CircleDetailPayload | null>(null)
const inviteMode = ref<'public' | 'private' | ''>('')
const inviteToken = ref('')
const inviteCircleName = ref('')
const inviteCircleType = ref<'0' | '1' | ''>('')
const invitePreview = ref<CircleInvitePreview | null>(null)
const joiningInvite = ref(false)
const loadingInvite = ref(false)
const inviteLoadErrorMessage = ref('')
const privateInviteToken = ref('')
const privateInviteErrorMessage = ref('')
const pendingJoinRequests = ref<CircleJoinRequestCard[]>([])
const loadingJoinRequests = ref(false)
const joinRequestLoadError = ref('')
const processingJoinRequestId = ref<number | null>(null)
const composerVisible = ref(false)
const postingFeed = ref(false)
const composerUploading = ref(false)
const composerFeedType = ref<FeedCreatePayload['feedType']>('text')
const composerContent = ref('')
const composerVisibilityScope = ref<FeedVisibilityScope>('circle')
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
const myCircles = ref<MyCircleCard[]>([])
const syncCircleLoadError = ref('')
const activeTab = ref<'overview' | 'feeds' | 'members'>('overview')
const circleFeeds = ref<CircleFeedCard[]>([])
const feedPageNum = ref(1)
const feedHasMore = ref(false)
const feedsLoading = ref(false)
const feedsLoadingMore = ref(false)
const feedLoadError = ref('')
const circleMembers = ref<CircleMemberItem[]>([])
const memberPageNum = ref(1)
const memberHasMore = ref(false)
const membersLoading = ref(false)
const membersLoadingMore = ref(false)
const memberLoadError = ref('')
const circleRanking = ref<CircleRankingItem[]>([])
const rankingLoading = ref(false)
const rankingLoadError = ref('')
const removingMemberUserId = ref<number | null>(null)
const completingTaskId = ref<number | null>(null)
const onShowRefreshGuard = createOnShowSuspendGuard()
let skipNextFeedDetailReturnRefresh = false

const detailTitle = computed(() => inviteToken.value && !detail.value?.joined ? '圈子邀请' : detail.value?.circle.name || '圈子详情')
const isMainCircle = computed(() => detail.value?.circle.id === userStore.userInfo?.mainCircleId)
const canManageCircle = computed(() => detail.value?.memberRole === '0')
const canInviteCircleMember = computed(() => {
  const circle = detail.value?.circle
  if (!circle || !detail.value?.joined) {
    return false
  }
  return circle.type === '1' || canManageCircle.value
})
const canReviewJoinRequests = computed(() => canManageCircle.value && detail.value?.circle.type === '1')
const hasPendingJoinRequestSignal = computed(() =>
  pendingJoinRequests.value.length > 0 || Number(detail.value?.pendingJoinRequestCount || 0) > 0
)
const showJoinRequestSection = computed(() =>
  canReviewJoinRequests.value && (loadingJoinRequests.value || Boolean(joinRequestLoadError.value) || hasPendingJoinRequestSignal.value)
)
const memberListSubtitle = computed(() => {
  if (circleMembers.value.length) {
    return `已加载 ${circleMembers.value.length} 位成员`
  }
  return `${detail.value?.circle.memberCount || 0} 位成员`
})
const inviteRequiresLogin = computed(() => Boolean(inviteToken.value && !userStore.isLoggedIn))
const inviteApprovalRequired = computed(() => Boolean(invitePreview.value?.approvalRequired))
const privateInvitePending = computed(() =>
  invitePreview.value?.membershipStatus === 'pending' && inviteApprovalRequired.value
)
const privateInviteReady = computed(() =>
  Boolean(
    inviteToken.value
      && userStore.isLoggedIn
      && invitePreview.value?.circleType === '1'
      && (
        invitePreview.value?.membershipStatus === 'none'
        || (invitePreview.value?.membershipStatus === 'pending' && !inviteApprovalRequired.value)
      )
  )
)
const inviteCircleDisplayName = computed(() => invitePreview.value?.circleName || inviteCircleName.value || detail.value?.circle.name || '这个圈子')
const inviteCircleKind = computed(() => invitePreview.value?.circleType || inviteCircleType.value || detail.value?.circle.type || '1')
const invitePanelTone = computed(() => {
  if (inviteLoadErrorMessage.value) {
    return 'error'
  }
  if (privateInvitePending.value) {
    return 'pending'
  }
  if (inviteRequiresLogin.value) {
    return 'login'
  }
  return 'ready'
})
const inviteInitial = computed(() => String(inviteCircleDisplayName.value || '圈').trim().slice(0, 1) || '圈')
const inviteKicker = computed(() => {
  if (inviteLoadErrorMessage.value) {
    return '邀请状态'
  }
  if (privateInvitePending.value) {
    return '等待审批'
  }
  if (inviteRequiresLogin.value) {
    return '微信登录后继续'
  }
  return inviteApprovalRequired.value ? '圈子邀请' : '管理员邀请'
})
const inviteVisibilityText = computed(() => inviteCircleKind.value === '1' ? '仅成员可见' : '公开可见')
const inviteJoinModeText = computed(() => {
  if (inviteLoadErrorMessage.value) {
    return '重新邀请'
  }
  if (privateInvitePending.value) {
    return '等待管理员处理'
  }
  return inviteApprovalRequired.value ? '管理员审批' : '确认后加入'
})
const inviteActionText = computed(() => {
  if (inviteRequiresLogin.value) {
    return '登录后继续'
  }
  return inviteApprovalRequired.value ? '提交申请' : '立即加入'
})
const inviteStateTitle = computed(() =>
  inviteLoadErrorMessage.value
    ? '邀请暂不可用'
    : inviteRequiresLogin.value
    ? `加入「${inviteCircleDisplayName.value}」`
    : privateInvitePending.value
      ? '申请已发送'
      : inviteApprovalRequired.value
        ? `申请加入「${inviteCircleDisplayName.value}」`
        : `加入「${inviteCircleDisplayName.value}」`
)
const inviteStateDescription = computed(() => {
  if (inviteLoadErrorMessage.value) {
    return inviteLoadErrorMessage.value
  }
  if (inviteRequiresLogin.value) {
    return inviteApprovalRequired.value
      ? '登录后可提交加入申请，管理员通过后即可进入圈子。'
      : '登录后确认邀请，即可进入圈子查看打卡内容。'
  }
  if (privateInvitePending.value) {
    return '你的申请已发送给管理员，通过后即可查看圈内打卡。'
  }
  return inviteApprovalRequired.value
    ? '确认后会发送加入申请，管理员通过后即可进入。'
    : '确认后会直接进入圈子，和大家一起打卡。'
})
const inviteFootnote = computed(() => {
  if (inviteLoadErrorMessage.value) {
    return '可以让邀请人重新发送一次邀请。'
  }
  if (privateInvitePending.value) {
    return '审批通过后会在消息中心通知你。'
  }
  if (inviteRequiresLogin.value) {
    return '登录完成后会自动回到这个邀请页。'
  }
  return inviteApprovalRequired.value ? '提交后请等待管理员处理。' : '加入成功后将自动进入圈子页面。'
})
const circleImageCover = computed(() => isRemoteCircleCover(detail.value?.circle.coverUrl))
const groupCoverClass = computed(() =>
  detail.value?.circle.coverUrl ? circleCoverClass(detail.value.circle.coverUrl) : circleCoverClass()
)
const syncCircleOptions = computed(() =>
  myCircles.value.filter((item) => item.id !== detail.value?.circle.id && !item.readOnly)
)
const detailTabs = [
  { key: 'overview', label: '概览' },
  { key: 'feeds', label: '打卡' },
  { key: 'members', label: '成员' }
] as const
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
const composerRecordTypeLabel = computed(() => {
  const current = composerFeedTypes.find((item) => item.value === composerFeedType.value)
  return current?.label || '打卡'
})
const composerTitle = computed(() => (composerRecordBacked.value ? `${composerRecordTypeLabel.value}并发布` : '发布打卡'))
const composerSubtitle = computed(() =>
  composerRecordBacked.value
    ? '在当前弹层完成记录，保存成功后自动发布到这个圈子。'
    : '默认关联当前圈子，也可以一并公开或同步到其他圈子。'
)
const composerPrimaryLabel = computed(() => (composerRecordBacked.value ? '保存并发布' : '发布打卡'))
const composerRecordImageTitle = computed(() => `${composerRecordTypeLabel.value}图片`)
const composerRecordImageHint = computed(() =>
  composerRecordBacked.value
    ? `图片会保存到本次${composerRecordTypeLabel.value}记录，并随动态一起发布。`
    : '支持 1-9 张图片，圈内和公开打卡都可使用。'
)
const composerRecordGuideTitle = computed(() => `${composerRecordTypeLabel.value}动态会关联真实记录`)
const composerRecordGuideDesc = computed(() => {
  const scopeText = composerVisibilityScope.value === 'public'
    ? '发布后会出现在发现页公开内容，同时保留在当前圈子。'
    : '发布后仅当前圈子和同步圈子可见。'
  return `先保存${composerRecordTypeLabel.value}记录，再自动生成可追溯动态。${scopeText}`
})
const composerCircleHint = computed(() => {
  if (!syncCircleOptions.value.length) {
    return '当前没有其他可同步的圈子'
  }
  return '可把同一条打卡同步到你加入的其他圈子'
})
const composerHelperText = computed(() => {
  const current = composerFeedTypes.find((item) => item.value === composerFeedType.value)
  const baseText = current ? `将以“${current.label}”打卡发布` : '将发布打卡'
  const publicText = composerVisibilityScope.value === 'public' ? '，并显示在发现页公开内容' : '，仅在当前圈子可见'
  const syncText = composerSyncEnabled.value && composerSyncCircleIds.value.length
    ? `，同时同步到 ${composerSyncCircleIds.value.length} 个其他圈子`
    : ''
  const imageText = composerImages.value.length ? `，附带 ${composerImages.value.length} 张图片` : ''
  return `${baseText}${publicText}${syncText}${imageText}`
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
const feedSectionSubtitle = computed(() =>
  circleFeeds.value.length ? `已加载 ${circleFeeds.value.length} 条圈内打卡` : '这里会按时间顺序展示圈友打卡内容'
)
const memberSectionSubtitle = computed(() =>
  circleRanking.value.length || circleMembers.value.length
    ? `排行 ${circleRanking.value.length} 人 · 成员 ${circleMembers.value.length} 人`
    : '完整排行榜、成员列表和审批区统一收在这里'
)
const currentDepositStatusText = computed(() => {
  return '默认免押金'
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

onLoad((query) => {
  const parsed = Number(query?.circleId || 0)
  if (parsed > 0) {
    circleId.value = parsed
  }
  inviteToken.value = typeof query?.inviteToken === 'string' ? query.inviteToken : ''
  inviteMode.value = query?.invite === 'private' ? 'private' : query?.invite === 'public' ? 'public' : ''
  inviteCircleType.value = query?.circleType === '1' ? '1' : query?.circleType === '0' ? '0' : ''
  inviteCircleName.value = typeof query?.circleName === 'string' ? decodeURIComponent(query.circleName) : ''
  configureShareMenu(true, false)
})

onShow(() => {
  if (onShowRefreshGuard.isSuspended()) {
    return
  }
  if (skipNextFeedDetailReturnRefresh) {
    skipNextFeedDetailReturnRefresh = false
    applyFeedDetailReturnSnapshot()
    return
  }
  if (inviteToken.value) {
    void loadInvitePreview()
  }
  if (!circleId.value) {
    return
  }
  if (inviteRequiresLogin.value) {
    detail.value = null
    forbiddenState.value = false
    loadErrorMessage.value = ''
    return
  }
  void loadDetail()
})

async function loadInvitePreview() {
  if (!inviteToken.value) {
    invitePreview.value = null
    inviteLoadErrorMessage.value = ''
    return
  }
  loadingInvite.value = true
  try {
    invitePreview.value = await resolveCircleInvite(inviteToken.value)
    inviteLoadErrorMessage.value = ''
    if (invitePreview.value.circleId && !circleId.value) {
      circleId.value = invitePreview.value.circleId
    }
    inviteCircleType.value = invitePreview.value.circleType === '1' ? '1' : '0'
    inviteCircleName.value = invitePreview.value.circleName || ''
    inviteMode.value = invitePreview.value.circleType === '1' ? 'private' : 'public'
  } catch (error) {
    console.error('解析圈子邀请失败', error)
    invitePreview.value = null
    inviteLoadErrorMessage.value = '邀请已失效或已被管理员更新，请联系圈主重新发送邀请。'
  } finally {
    loadingInvite.value = false
  }
}

async function loadDetail() {
  if (!circleId.value) {
    return
  }
  loading.value = true
  forbiddenState.value = false
  loadErrorMessage.value = ''
  try {
    detail.value = await getCircleDetail(circleId.value)
    resetFeedList()
    resetMemberList()
    resetRankingList()
    if (detail.value.circle.type === '1' && detail.value.joined) {
      configureShareMenu(true, Boolean(privateInviteToken.value))
      void ensureInviteToken()
    } else {
      configureShareMenu(detail.value.circle.type === '1', detail.value.circle.type !== '1')
      privateInviteToken.value = ''
      privateInviteErrorMessage.value = ''
    }
    if (detail.value.circle.type === '1' && canManageCircle.value) {
      void loadJoinRequests()
    } else {
      pendingJoinRequests.value = []
    }
    if (detail.value.joined) {
      void loadMyCircleOptions()
    }
    void loadFeedList()
    void loadMemberList()
    void loadRankingList()
  } catch (error: any) {
    const message = String(error?.message || '')
    if (message.includes('仅圈子成员可查看')) {
      forbiddenState.value = true
      detail.value = null
    } else {
      console.error('加载圈子详情失败', error)
      detail.value = null
      loadErrorMessage.value = error instanceof Error ? error.message || '圈子详情接口请求失败' : '圈子详情接口请求失败'
    }
  } finally {
    loading.value = false
  }
}

async function ensureInviteToken() {
  if (!detail.value?.circle.id || detail.value.circle.type !== '1' || !detail.value.joined) {
    privateInviteToken.value = ''
    privateInviteErrorMessage.value = ''
    return
  }
  privateInviteErrorMessage.value = ''
  try {
    const preview = await createCircleInviteToken(detail.value.circle.id)
    privateInviteToken.value = preview.token
    configureShareMenu(true, true)
  } catch (error) {
    console.error('生成圈子邀请 token 失败', error)
    privateInviteToken.value = ''
    privateInviteErrorMessage.value = '邀请暂时不可用，请稍后重试。'
    configureShareMenu(true, false)
  }
}

async function loadJoinRequests() {
  if (!detail.value?.circle.id || !canManageCircle.value) {
    pendingJoinRequests.value = []
    joinRequestLoadError.value = ''
    return
  }
  loadingJoinRequests.value = true
  joinRequestLoadError.value = ''
  try {
    pendingJoinRequests.value = await getCircleJoinRequests(detail.value.circle.id)
  } catch (error) {
    console.error('加载待审批申请失败', error)
    pendingJoinRequests.value = []
    joinRequestLoadError.value = error instanceof Error ? error.message || '申请列表暂时不可用' : '申请列表暂时不可用'
  } finally {
    loadingJoinRequests.value = false
  }
}

async function loadMyCircleOptions() {
  if (!userStore.isLoggedIn) {
    myCircles.value = []
    syncCircleLoadError.value = ''
    return
  }
  syncCircleLoadError.value = ''
  try {
    const page = await getMyCircles({ pageNum: 1, pageSize: 100 })
    myCircles.value = page.list || []
  } catch (error) {
    console.error('加载同步圈子选项失败', error)
    myCircles.value = []
    syncCircleLoadError.value = error instanceof Error ? error.message || '其他圈子暂时不可用' : '其他圈子暂时不可用'
  }
}

function selectTab(tab: 'overview' | 'feeds' | 'members') {
  activeTab.value = tab
  if (tab === 'feeds' && !circleFeeds.value.length && !feedsLoading.value) {
    void loadFeedList()
  }
  if (tab === 'members' && !circleMembers.value.length && !membersLoading.value) {
    void loadMemberList()
  }
  if (tab === 'members' && !circleRanking.value.length && !rankingLoading.value) {
    void loadRankingList()
  }
}

function resetFeedList() {
  circleFeeds.value = []
  feedPageNum.value = 1
  feedHasMore.value = false
  feedLoadError.value = ''
}

function resetMemberList() {
  circleMembers.value = []
  memberPageNum.value = 1
  memberHasMore.value = false
  memberLoadError.value = ''
}

function resetRankingList() {
  circleRanking.value = []
  rankingLoadError.value = ''
}

async function loadFeedList(loadMore = false) {
  if (!circleId.value) {
    return
  }
  const nextPage = loadMore ? feedPageNum.value + 1 : 1
  if (loadMore) {
    feedsLoadingMore.value = true
  } else {
    feedsLoading.value = true
    feedLoadError.value = ''
  }
  try {
    const page = await getCircleFeeds(circleId.value, { pageNum: nextPage, pageSize: 10 })
    const list = sortFeedsByCreatedAt(page.list || [])
    circleFeeds.value = loadMore ? [...circleFeeds.value, ...list] : list
    feedPageNum.value = nextPage
    feedHasMore.value = circleFeeds.value.length < Number(page.total || 0)
  } catch (error) {
    console.error('加载圈内打卡失败', error)
    if (!loadMore) {
      circleFeeds.value = []
      feedLoadError.value = error instanceof Error ? error.message || '圈内打卡暂时不可用' : '圈内打卡暂时不可用'
    }
  } finally {
    feedsLoading.value = false
    feedsLoadingMore.value = false
  }
}

async function loadMemberList(loadMore = false) {
  if (!circleId.value) {
    return
  }
  const nextPage = loadMore ? memberPageNum.value + 1 : 1
  if (loadMore) {
    membersLoadingMore.value = true
  } else {
    membersLoading.value = true
    memberLoadError.value = ''
  }
  try {
    const page = await getCircleMembers(circleId.value, { pageNum: nextPage, pageSize: 12 })
    const list = page.list || []
    circleMembers.value = loadMore ? [...circleMembers.value, ...list] : list
    memberPageNum.value = nextPage
    memberHasMore.value = circleMembers.value.length < Number(page.total || 0)
  } catch (error) {
    console.error('加载圈子成员失败', error)
    if (!loadMore) {
      circleMembers.value = []
      memberLoadError.value = error instanceof Error ? error.message || '成员列表暂时不可用' : '成员列表暂时不可用'
    }
  } finally {
    membersLoading.value = false
    membersLoadingMore.value = false
  }
}

async function loadRankingList() {
  if (!circleId.value) {
    return
  }
  rankingLoading.value = true
  rankingLoadError.value = ''
  try {
    const page = await getCircleRanking(circleId.value, { pageNum: 1, pageSize: 100 })
    circleRanking.value = page.list || []
  } catch (error) {
    console.error('加载圈子排行榜失败', error)
    circleRanking.value = []
    rankingLoadError.value = error instanceof Error ? error.message || '排行榜暂时不可用' : '排行榜暂时不可用'
  } finally {
    rankingLoading.value = false
  }
}

function reloadFeeds() {
  void loadFeedList()
}

function reloadMembers() {
  void loadMemberList()
}

function reloadRanking() {
  void loadRankingList()
}

function loadMoreFeeds() {
  if (!feedHasMore.value || feedsLoadingMore.value) {
    return
  }
  void loadFeedList(true)
}

function loadMoreMembers() {
  if (!memberHasMore.value || membersLoadingMore.value) {
    return
  }
  void loadMemberList(true)
}

function showActionError(error: unknown, fallback: string) {
  const title = error instanceof Error && error.message ? error.message : fallback
  uni.showToast({
    title: title.length > 12 ? fallback : title,
    icon: 'none'
  })
}

async function handleMembership() {
  if (!detail.value?.circle.id || membershipLoading.value) {
    return
  }
  if (!ensureLoggedInForCircleAction()) {
    return
  }
  membershipLoading.value = true
  try {
    const beforeHabitStats = await getHabitStats().catch(() => ({ activeHabits: 0 }))
    const beforeMyCircles = await getMyCircles({ pageNum: 1, pageSize: 1 }).catch(() => ({ total: 0 }))
    const previousAchievementKeys = captureAchievementState(userStore.userInfo, {
      activeHabits: Number(beforeHabitStats.activeHabits || 0),
      joinedCircles: Number(beforeMyCircles.total || 0)
    })

    if (detail.value.joined) {
      await leaveCircle(detail.value.circle.id)
      uni.showToast({ title: '已退出圈子', icon: 'none' })
    } else {
      await joinCircle(detail.value.circle.id)
      uni.showToast({ title: '已加入圈子', icon: 'none' })
    }
    await userStore.fetchUserInfo()
    await loadDetail()
    const afterHabitStats = await getHabitStats().catch(() => ({ activeHabits: 0 }))
    const afterMyCircles = await getMyCircles({ pageNum: 1, pageSize: 1 }).catch(() => ({ total: 0 }))
    await notifyAchievementUnlocks(previousAchievementKeys, userStore.userInfo, {
      activeHabits: Number(afterHabitStats.activeHabits || 0),
      joinedCircles: Number(afterMyCircles.total || 0)
    })
  } catch (error) {
    console.error('更新圈子成员状态失败', error)
    showActionError(error, detail.value?.joined ? '退出失败' : '加入失败')
  } finally {
    membershipLoading.value = false
  }
}

async function setMainCircle() {
  if (!detail.value?.joined || !detail.value.circle.id || isMainCircle.value || settingMainCircle.value || detail.value.circle.readOnly) {
    return
  }
  if (!ensureLoggedInForCircleAction()) {
    return
  }
  settingMainCircle.value = true
  try {
    await userStore.updateUserInfo({ mainCircleId: detail.value.circle.id })
    uni.showToast({ title: '已设为主圈子', icon: 'success' })
  } catch (error) {
    console.error('设置主圈子失败', error)
    showActionError(error, '设为主圈子失败')
  } finally {
    settingMainCircle.value = false
  }
}

function goFeedDetail(feedId: number) {
  if (!feedId) {
    return
  }
  skipNextFeedDetailReturnRefresh = true
  uni.navigateTo({
    url: `/pages/feed-detail/index?feedId=${feedId}`,
    fail: () => {
      skipNextFeedDetailReturnRefresh = false
    }
  })
}

function readFeedDetailReturnSnapshot(): FeedDetailReturnSnapshot | null {
  try {
    const snapshot = uni.getStorageSync(FEED_DETAIL_RETURN_STORAGE_KEY)
    uni.removeStorageSync(FEED_DETAIL_RETURN_STORAGE_KEY)
    if (!snapshot || typeof snapshot !== 'object') {
      return null
    }
    const normalized = snapshot as FeedDetailReturnSnapshot
    return normalized.id ? normalized : null
  } catch (error) {
    console.warn('读取动态详情返回状态失败:', error)
    return null
  }
}

function applyFeedDetailReturnSnapshot() {
  const snapshot = readFeedDetailReturnSnapshot()
  if (!snapshot?.id) {
    return
  }

  const currentCircleId = circleId.value || detail.value?.circle.id || null
  const listHasFeed = circleFeeds.value.some((feed) => feed.id === snapshot.id)
  const belongsToCurrentCircle = !currentCircleId
    || snapshot.originCircleId === currentCircleId
    || snapshot.circleId === currentCircleId
    || listHasFeed

  if (!belongsToCurrentCircle) {
    return
  }

  if (snapshot.deleted) {
    circleFeeds.value = circleFeeds.value.filter((feed) => feed.id !== snapshot.id)
    return
  }

  circleFeeds.value = circleFeeds.value.map((feed) => {
    if (feed.id !== snapshot.id) {
      return feed
    }
    return {
      ...feed,
      likesCount: snapshot.likesCount ?? feed.likesCount,
      commentsCount: snapshot.commentsCount ?? feed.commentsCount,
      likedByMe: typeof snapshot.likedByMe === 'boolean' ? snapshot.likedByMe : feed.likedByMe,
      commentEnabled: typeof snapshot.commentEnabled === 'boolean' ? snapshot.commentEnabled : feed.commentEnabled
    }
  })
}

function goUserProfile(userId?: number | null) {
  if (!userId) {
    return
  }
  uni.navigateTo({ url: `/pages/user-profile/index?userId=${userId}` })
}

function goBack() {
  uni.navigateBack({
    fail: () => {
      uni.reLaunch({ url: '/pages/circle/index' })
    }
  })
}

function goEditCircle() {
  if (!detail.value?.circle.id || !canManageCircle.value || detail.value.circle.readOnly) {
    return
  }
  if (!ensureLoggedInForCircleAction()) {
    return
  }
  uni.navigateTo({ url: `/pages/circle-create/index?circleId=${detail.value.circle.id}` })
}

function buildCircleSharePayload() {
  if (!detail.value?.circle.id) {
    return {
      title: '邀请你加入打卡圈子',
      path: '/pages/circle/index',
      imageUrl: ''
    }
  }
  const encodedCircleName = encodeURIComponent(detail.value.circle.name || '')
  const invite = detail.value.circle.type === '1' ? 'private' : 'public'
  if (detail.value.circle.type === '1' && !privateInviteToken.value) {
    return {
      title: '圈子邀请准备中',
      path: `/pages/circle-private/index?circleId=${detail.value.circle.id}`,
      imageUrl: resolveShareImage()
    }
  }
  const tokenQuery = detail.value.circle.type === '1' && privateInviteToken.value ? `&inviteToken=${privateInviteToken.value}` : ''
  const inviteTitle = `加入「${detail.value.circle.name}」一起打卡`
  return {
    title: detail.value.circle.type === '1'
      ? inviteTitle
      : `查看「${detail.value.circle.name}」打卡圈`,
    path: `/pages/circle-private/index?circleId=${detail.value.circle.id}&invite=${invite}&circleType=${detail.value.circle.type}&circleName=${encodedCircleName}${tokenQuery}`,
    imageUrl: resolveShareImage()
  }
}

onShareAppMessage(() => buildCircleSharePayload())

onShareTimeline(() => ({
  title: detail.value?.circle.name ? `加入「${detail.value.circle.name}」一起打卡` : '加入打卡圈子',
  query: detail.value?.circle.id && detail.value?.circle.type !== '1'
    ? `circleId=${detail.value.circle.id}&invite=public&circleType=0&circleName=${encodeURIComponent(detail.value.circle.name || '')}`
    : '',
  imageUrl: resolveShareImage()
}))

function configureShareMenu(privateCircle: boolean, enabled = true) {
  if (!enabled) {
    uni.hideShareMenu({
      hideShareItems: ['shareAppMessage', 'shareTimeline']
    })
    return
  }
  uni.showShareMenu({
    menus: privateCircle ? ['shareAppMessage'] : ['shareAppMessage', 'shareTimeline']
  })
}

function buildInviteRoute() {
  if (!circleId.value) {
    return '/pages/circle/index'
  }
  const circleName = encodeURIComponent(inviteCircleDisplayName.value)
  const circleType = invitePreview.value?.circleType || inviteCircleType.value || detail.value?.circle.type || '0'
  const invite = inviteMode.value || (circleType === '1' ? 'private' : 'public')
  const tokenQuery = inviteToken.value ? `&inviteToken=${inviteToken.value}` : ''
  return `/pages/circle-private/index?circleId=${circleId.value}&invite=${invite}&circleType=${circleType}&circleName=${circleName}${tokenQuery}`
}

function resolveShareImage() {
  return resolveCircleShareImage(detail.value?.circle.coverUrl)
}

function goLoginForInvite() {
  openLoginPage('safe', buildInviteRoute())
}

function buildCircleDetailRoute() {
  if (inviteToken.value) {
    return buildInviteRoute()
  }
  return circleId.value ? `/pages/circle-private/index?circleId=${circleId.value}` : '/pages/circle/index'
}

function ensureLoggedInForCircleAction() {
  if (userStore.isLoggedIn) {
    return true
  }
  openLoginPage('safe', buildCircleDetailRoute())
  return false
}

function markInviteJoined(targetCircleId: number) {
  circleId.value = targetCircleId
  inviteToken.value = ''
  inviteLoadErrorMessage.value = ''
  invitePreview.value = invitePreview.value
    ? { ...invitePreview.value, membershipStatus: 'joined' }
    : null
}

function redirectToCircleAfterInvite(targetCircleId: number) {
  markInviteJoined(targetCircleId)
  return new Promise<void>((resolve) => {
    uni.redirectTo({
      url: `/pages/circle-private/index?circleId=${targetCircleId}`,
      success: () => resolve(),
      fail: async () => {
        await loadDetail()
        resolve()
      }
    })
  })
}

async function joinInvitedCircle() {
  if (!inviteToken.value || joiningInvite.value) {
    return
  }
  joiningInvite.value = true
  try {
    const beforeHabitStats = await getHabitStats().catch(() => ({ activeHabits: 0 }))
    const beforeMyCircles = await getMyCircles({ pageNum: 1, pageSize: 1 }).catch(() => ({ total: 0 }))
    const previousAchievementKeys = captureAchievementState(userStore.userInfo, {
      activeHabits: Number(beforeHabitStats.activeHabits || 0),
      joinedCircles: Number(beforeMyCircles.total || 0)
    })
    const result = await acceptCircleInvite(inviteToken.value)
    if (result.pendingApproval) {
      await loadInvitePreview()
      uni.showToast({ title: '申请已提交', icon: 'success' })
      return
    }
    uni.showToast({ title: '已加入圈子', icon: 'success' })
    await userStore.fetchUserInfo()
    forbiddenState.value = false
    const acceptedCircleId = Number(result.circleId || circleId.value || invitePreview.value?.circleId || 0)
    if (acceptedCircleId > 0) {
      await redirectToCircleAfterInvite(acceptedCircleId)
    } else {
      inviteToken.value = ''
      invitePreview.value = null
      await loadDetail()
    }
    const afterHabitStats = await getHabitStats().catch(() => ({ activeHabits: 0 }))
    const afterMyCircles = await getMyCircles({ pageNum: 1, pageSize: 1 }).catch(() => ({ total: 0 }))
    await notifyAchievementUnlocks(previousAchievementKeys, userStore.userInfo, {
      activeHabits: Number(afterHabitStats.activeHabits || 0),
      joinedCircles: Number(afterMyCircles.total || 0)
    })
  } catch (error) {
    console.error('通过邀请加入圈子失败', error)
    showActionError(error, '加入失败')
  } finally {
    joiningInvite.value = false
  }
}

async function reviewJoinRequest(request: CircleJoinRequestCard, approve: boolean) {
  if (!detail.value?.circle.id || !request.id || processingJoinRequestId.value) {
    return
  }
  if (!ensureLoggedInForCircleAction()) {
    return
  }
  processingJoinRequestId.value = request.id
  try {
    if (approve) {
      await approveCircleJoinRequest(detail.value.circle.id, request.id)
      uni.showToast({ title: '已通过申请', icon: 'success' })
    } else {
      await rejectCircleJoinRequest(detail.value.circle.id, request.id)
      uni.showToast({ title: '已拒绝申请', icon: 'none' })
    }
    await loadDetail()
  } catch (error) {
    console.error('处理加入申请失败', error)
    showActionError(error, approve ? '通过失败' : '拒绝失败')
  } finally {
    processingJoinRequestId.value = null
  }
}

function memberInitial(name?: string | null) {
  return (name || '圈友').slice(0, 1)
}

function depositStatusLabel(status?: string | null, enabled = true) {
  return '暂未开放'
}

function resetComposer() {
  composerFeedType.value = 'text'
  composerContent.value = ''
  composerVisibilityScope.value = 'circle'
  composerSyncEnabled.value = false
  composerSyncCircleIds.value = []
  composerImages.value = []
  resetComposerRecordForms()
}

function showComposer() {
  if (!ensureLoggedInForCircleAction()) {
    return
  }
  if (!detail.value?.joined) {
    uni.showToast({ title: '加入圈子后才能打卡', icon: 'none' })
    return
  }
  if (detail.value.circle.readOnly) {
    uni.showToast({ title: '圈子已到期，仅支持查看历史内容', icon: 'none' })
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
  void loadMyCircleOptions()
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
  if (!userStore.isLoggedIn) {
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
  if (!userStore.isLoggedIn) {
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

function ensureRecordComposerReady() {
  if (!detail.value?.circle.id) {
    uni.showToast({ title: '当前圈子不可用', icon: 'none' })
    return false
  }
  if (detail.value.circle.readOnly) {
    uni.showToast({ title: '圈子已到期，无法继续发布', icon: 'none' })
    return false
  }
  if (composerUploading.value) {
    uni.showToast({ title: '图片还在上传中，请稍后再发', icon: 'none' })
    return false
  }
  if (hasFailedImageAssets(composerImages.value)) {
    uni.showToast({ title: '有图片上传失败，请删除后重试', icon: 'none' })
    return false
  }
  if (composerSyncEnabled.value && !composerSyncCircleIds.value.length) {
    uni.showToast({ title: '请至少选择一个其他圈子', icon: 'none' })
    return false
  }
  return true
}

function getCurrentCircleId() {
  return detail.value?.circle.id || circleId.value || null
}

function getComposerSelectedCircleIds() {
  const currentCircleId = getCurrentCircleId()
  const selected = currentCircleId ? [currentCircleId] : []
  if (composerSyncEnabled.value) {
    selected.push(...composerSyncCircleIds.value)
  }
  return selected
    .map((circleIdValue) => Number(circleIdValue || 0))
    .filter((circleIdValue, index, array) => circleIdValue > 0 && array.indexOf(circleIdValue) === index)
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
  if (!ensureRecordComposerReady()) {
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
    await loadDetail()
    activeTab.value = 'feeds'
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
    visibilityScope: composerVisibilityScope.value,
    originCircleId: getCurrentCircleId(),
    syncCircleIds: composerSyncEnabled.value ? composerSyncCircleIds.value : [],
    images: normalizeHabitImageList(updatedTask.images),
    sourceType: 'habit_checkin',
    sourceId: updatedTask.checkinId
  }
}

async function publishComposerRecordDraft(draft: RecordFeedDraftPayload) {
  const currentCircleId = getCurrentCircleId()
  if (!currentCircleId) {
    throw new Error('当前圈子不可用')
  }
  if (!draft.sourceType || !draft.sourceId) {
    throw new Error('记录来源缺失，动态未发布')
  }

  await publishFeed({
    feedType: draft.feedType || composerFeedType.value,
    content: draft.content || '记录更新',
    images: Array.isArray(draft.images) ? draft.images : getComposerImageUrls(),
    visibilityScope: composerVisibilityScope.value,
    originCircleId: currentCircleId,
    syncCircleIds: composerSyncEnabled.value ? composerSyncCircleIds.value : [],
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

function toggleComposerCircle(circleIdValue: number) {
  if (composerSyncCircleIds.value.includes(circleIdValue)) {
    composerSyncCircleIds.value = composerSyncCircleIds.value.filter((item) => item !== circleIdValue)
    return
  }
  composerSyncCircleIds.value = [...composerSyncCircleIds.value, circleIdValue]
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
  if (!detail.value?.circle.id) {
    return
  }
  if (detail.value.circle.readOnly) {
    uni.showToast({ title: '圈子已到期，无法继续发布', icon: 'none' })
    return
  }
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
  if (composerSyncEnabled.value && !composerSyncCircleIds.value.length) {
    uni.showToast({ title: '请至少选择一个其他圈子', icon: 'none' })
    return
  }

  postingFeed.value = true
  try {
    await publishFeed({
      feedType: composerFeedType.value,
      content,
      visibilityScope: composerVisibilityScope.value,
      originCircleId: detail.value.circle.id,
      syncCircleIds: composerSyncEnabled.value ? composerSyncCircleIds.value : [],
      images: composerImages.value.map((item) => item.url).filter(Boolean)
    })
    uni.showToast({ title: '打卡已发布', icon: 'success' })
    closeComposer()
    resetComposer()
    await loadDetail()
    activeTab.value = 'feeds'
  } catch (error: any) {
    uni.showToast({ title: error?.message || '发布失败', icon: 'none' })
  } finally {
    postingFeed.value = false
  }
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

async function completeTask(task: CircleTaskItem) {
  if (!detail.value?.circle.id || !task.id || completingTaskId.value || task.readOnly || !task.manual) {
    return
  }
  if (!ensureLoggedInForCircleAction()) {
    return
  }
  completingTaskId.value = task.id
  try {
    const overview = await completeCircleTask(detail.value.circle.id, task.id)
    detail.value.todayTasks = overview.tasks || []
    detail.value.taskSummary = overview.summary || null
    uni.showToast({ title: '任务已完成', icon: 'success' })
    await loadRankingList()
    detail.value.rankingSummary = {
      periodLabel: detail.value.rankingSummary?.periodLabel || '本周排行',
      ruleText: detail.value.rankingSummary?.ruleText || '按已完成任务数、圈内打卡次数、连续打卡天数排序',
      leader: circleRanking.value.length ? circleRanking.value[0] : null,
      updatedAt: new Date().toLocaleString('zh-CN', { hour12: false }).replace(/\//g, '-')
    }
  } catch (error) {
    console.error('完成圈子任务失败', error)
    showActionError(error, '任务完成失败')
  } finally {
    completingTaskId.value = null
  }
}

function confirmRemoveMember(member: CircleMemberItem) {
  if (!detail.value?.circle.id || !member.userId || removingMemberUserId.value) {
    return
  }
  uni.showModal({
    title: '移除成员',
    content: `确认将 ${member.nickname || `用户${member.userId}`} 移出当前圈子吗？`,
    success: async (result) => {
      if (!result.confirm || !detail.value?.circle.id) {
        return
      }
      removingMemberUserId.value = member.userId
      try {
        await removeCircleMember(detail.value.circle.id, member.userId)
        uni.showToast({ title: '成员已移除', icon: 'success' })
        await loadDetail()
        activeTab.value = 'members'
      } catch (error: any) {
        uni.showToast({ title: error?.message || '移除失败', icon: 'none' })
      } finally {
        removingMemberUserId.value = null
      }
    }
  })
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

.empty-wrap {
  padding: 24rpx 30rpx 0;
}

.invite-wrap {
  min-height: 100%;
  display: flex;
  align-items: center;
  box-sizing: border-box;
  padding: 40rpx 30rpx 80rpx;
}

.invite-panel {
  width: 100%;
  box-sizing: border-box;
  background: #ffffff;
  border: 1rpx solid rgba(23, 37, 84, 0.08);
  border-top: 8rpx solid #4a90e2;
  border-radius: 30rpx;
  padding: 44rpx 36rpx 38rpx;
  box-shadow: 0 18rpx 48rpx rgba(35, 49, 82, 0.08);
}

.invite-panel--ready {
  border-top-color: #2ba779;
}

.invite-panel--pending {
  border-top-color: #f29b38;
}

.invite-panel--error {
  border-top-color: #e56767;
}

.invite-mark {
  width: 104rpx;
  height: 104rpx;
  border-radius: 34rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 28rpx;
  color: #ffffff;
  background: linear-gradient(135deg, #4a90e2, #2ba779);
  font-size: 42rpx;
  font-weight: 800;
  box-shadow: 0 14rpx 30rpx rgba(74, 144, 226, 0.22);
}

.invite-panel--pending .invite-mark {
  background: linear-gradient(135deg, #f29b38, #d77b22);
  box-shadow: 0 14rpx 30rpx rgba(242, 155, 56, 0.2);
}

.invite-panel--error .invite-mark {
  background: linear-gradient(135deg, #e56767, #c94d4d);
  box-shadow: 0 14rpx 30rpx rgba(229, 103, 103, 0.18);
}

.invite-kicker {
  display: block;
  margin-bottom: 12rpx;
  color: #5f6b7a;
  font-size: 24rpx;
  font-weight: 700;
}

.invite-title {
  display: block;
  color: #18202d;
  font-size: 44rpx;
  line-height: 1.24;
  font-weight: 850;
}

.invite-desc {
  display: block;
  margin-top: 18rpx;
  color: #5f6b7a;
  font-size: 27rpx;
  line-height: 1.65;
}

.invite-meta {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 20rpx;
  margin-top: 34rpx;
  padding: 24rpx 0;
  border-top: 1rpx solid rgba(23, 37, 84, 0.08);
  border-bottom: 1rpx solid rgba(23, 37, 84, 0.08);
}

.invite-meta__item {
  min-width: 0;
}

.invite-meta__label,
.invite-meta__value {
  display: block;
}

.invite-meta__label {
  color: #8b95a4;
  font-size: 22rpx;
  margin-bottom: 8rpx;
}

.invite-meta__value {
  color: #18202d;
  font-size: 28rpx;
  font-weight: 750;
  overflow-wrap: anywhere;
}

.invite-actions {
  margin-top: 34rpx;
}

.invite-action {
  width: 100%;
  min-height: 88rpx;
}

.invite-footnote {
  display: block;
  margin-top: 20rpx;
  color: #8b95a4;
  font-size: 23rpx;
  line-height: 1.5;
  text-align: center;
}

.content-inner {
  padding-bottom: 60rpx;
}

.group-info-card,
.goal-card {
  background: white;
  margin: 24rpx 30rpx 0;
  border-radius: 32rpx;
  padding: 32rpx;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.04);
  position: relative;
  overflow: hidden;
}

.group-cover-image {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
}

.group-info-bg {
  position: absolute;
  top: -30rpx;
  right: -30rpx;
  width: 180rpx;
  height: 180rpx;
  border-radius: 50%;
  background: linear-gradient(135deg, #4a90e2, #50e3c2);
  opacity: 0.1;
}

.group-info-card::after {
  content: '';
  position: absolute;
  inset: 0;
  background: linear-gradient(180deg, rgba(15, 23, 42, 0.06), rgba(15, 23, 42, 0.18));
  pointer-events: none;
}

.group-tags {
  display: flex;
  gap: 10rpx;
  flex-wrap: wrap;
  margin-bottom: 16rpx;
  position: relative;
  z-index: 1;
}

.group-name {
  display: block;
  font-size: 42rpx;
  font-weight: 800;
  margin-bottom: 10rpx;
  position: relative;
  z-index: 1;
}

.group-info-card--image .group-name,
.group-info-card.cover-ocean .group-name,
.group-info-card.cover-sunrise .group-name,
.group-info-card.cover-forest .group-name,
.group-info-card.cover-berry .group-name,
.group-info-card.cover-night .group-name,
.group-info-card.cover-sand .group-name {
  color: #fff;
}

.group-desc {
  display: block;
  font-size: 24rpx;
  line-height: 1.7;
  color: #888888;
  margin-bottom: 20rpx;
  position: relative;
  z-index: 1;
}

.group-info-card--image .group-desc,
.group-info-card.cover-ocean .group-desc,
.group-info-card.cover-sunrise .group-desc,
.group-info-card.cover-forest .group-desc,
.group-info-card.cover-berry .group-desc,
.group-info-card.cover-night .group-desc,
.group-info-card.cover-sand .group-desc {
  color: rgba(255, 255, 255, 0.82);
}

.group-meta-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20rpx;
}

.group-readonly {
  display: block;
  margin-bottom: 18rpx;
  font-size: 22rpx;
  line-height: 1.6;
  color: rgba(255, 255, 255, 0.92);
  position: relative;
  z-index: 1;
}

.group-stat-chip {
  min-width: 120rpx;
  padding: 18rpx 20rpx;
  border-radius: 22rpx;
  background: rgba(255, 255, 255, 0.16);
  backdrop-filter: blur(10rpx);
  position: relative;
  z-index: 1;
}

.group-stat-chip__value,
.group-stat-chip__label {
  display: block;
}

.group-stat-chip__value {
  font-size: 24rpx;
  font-weight: 800;
  color: #fff;
}

.group-stat-chip__label {
  margin-top: 6rpx;
  font-size: 20rpx;
  color: rgba(255, 255, 255, 0.76);
}

.member-stack {
  display: flex;
}

.member-avatar {
  width: 64rpx;
  height: 64rpx;
  border-radius: 50%;
  border: 4rpx solid white;
  margin-left: -16rpx;
}

.member-avatar--fallback,
.rank-avatar--fallback {
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #dbeafe, #bfdbfe);
  color: #1d4ed8;
  font-size: 24rpx;
  font-weight: 700;
}

.member-avatar:first-child {
  margin-left: 0;
}

.meta-side {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 6rpx;
  font-size: 22rpx;
  color: #666666;
}

.group-info-card--image .meta-side,
.group-info-card.cover-ocean .meta-side,
.group-info-card.cover-sunrise .meta-side,
.group-info-card.cover-forest .meta-side,
.group-info-card.cover-berry .meta-side,
.group-info-card.cover-night .meta-side,
.group-info-card.cover-sand .meta-side {
  color: rgba(255, 255, 255, 0.78);
}

.goal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 24rpx;
}

.goal-label {
  font-size: 28rpx;
  font-weight: 700;
}

.stat-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16rpx;
}

.stat-item {
  background: #f9fafc;
  border-radius: 20rpx;
  padding: 20rpx;
  text-align: center;
}

.stat-value {
  display: block;
  font-size: 26rpx;
  font-weight: 700;
  color: #333333;
}

.stat-label {
  display: block;
  margin-top: 8rpx;
  font-size: 20rpx;
  color: #888888;
}

.action-row {
  display: flex;
  gap: 16rpx;
  margin-top: 24rpx;
}

.manage-row {
  display: flex;
  gap: 16rpx;
  margin-top: 16rpx;
}

.manage-inline-error {
  margin-top: 14rpx;
  display: flex;
  align-items: center;
  gap: 12rpx;
  font-size: 22rpx;
  color: #8f4d18;
}

.btn-primary,
.btn-secondary {
  flex: 1;
}

.detail-tab-bar {
  margin: 24rpx 30rpx 0;
}

.circle-checkin-button {
  min-width: 148rpx;
  min-height: 60rpx;
  padding: 0 24rpx;
  box-shadow: 0 10rpx 22rpx rgba(74, 144, 226, 0.18);
}

.checkin-entry-card {
  margin: 0 30rpx 22rpx;
  padding: 24rpx;
  border-radius: 28rpx;
  display: flex;
  align-items: center;
  gap: 20rpx;
  background: linear-gradient(135deg, rgba(74, 144, 226, 0.12), rgba(43, 167, 121, 0.1));
  border: 1px solid rgba(74, 144, 226, 0.16);
  box-shadow: 0 12rpx 30rpx rgba(35, 49, 82, 0.08);
}

.checkin-entry-card__mark {
  width: 72rpx;
  height: 72rpx;
  border-radius: 24rpx;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #4a90e2;
  color: #ffffff;
  font-size: 34rpx;
  font-weight: 850;
  box-shadow: 0 10rpx 22rpx rgba(74, 144, 226, 0.22);
}

.checkin-entry-card__copy {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 6rpx;
}

.checkin-entry-card__title,
.checkin-entry-card__desc,
.checkin-entry-card__action {
  display: block;
}

.checkin-entry-card__title {
  color: #1f2937;
  font-size: 28rpx;
  font-weight: 800;
}

.checkin-entry-card__desc {
  color: #64748b;
  font-size: 21rpx;
  line-height: 1.5;
}

.checkin-entry-card__action {
  flex-shrink: 0;
  color: #1d4ed8;
  font-size: 23rpx;
  font-weight: 800;
}

.overview-list,
.section-padding {
  padding: 0 30rpx;
}

.overview-card {
  background: white;
  border-radius: 32rpx;
  padding: 26rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.02);
}

.overview-card__header,
.overview-card__row {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 20rpx;
}

.overview-card__header {
  margin-bottom: 18rpx;
}

.overview-card__row + .overview-card__row {
  margin-top: 14rpx;
}

.overview-card__title,
.overview-card__label {
  font-size: 26rpx;
  font-weight: 700;
  color: #1f2937;
}

.overview-card__label {
  flex-shrink: 0;
}

.overview-card__value {
  flex: 1;
  min-width: 0;
  font-size: 23rpx;
  line-height: 1.6;
  color: #64748b;
  text-align: right;
}

.goal-list,
.ranking-list {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.goal-list__item,
.ranking-list__item {
  display: flex;
  align-items: center;
  gap: 16rpx;
  padding: 18rpx 20rpx;
  border-radius: 24rpx;
  background: #f8fafc;
}

.goal-list__copy,
.ranking-list__copy {
  flex: 1;
  min-width: 0;
}

.goal-list__name,
.ranking-list__name {
  display: block;
  font-size: 24rpx;
  font-weight: 700;
  color: #1f2937;
}

.goal-list__desc,
.ranking-list__desc {
  display: block;
  margin-top: 6rpx;
  font-size: 21rpx;
  line-height: 1.5;
  color: #64748b;
}

.goal-list__meta {
  display: block;
  margin-top: 6rpx;
  font-size: 20rpx;
  color: #94a3b8;
}

.task-list {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.task-item {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 18rpx;
  padding: 20rpx;
  border-radius: 24rpx;
  background: #f8fafc;
}

.task-item__copy {
  flex: 1;
  min-width: 0;
}

.task-item__name,
.task-item__desc,
.task-item__meta,
.task-item__progress {
  display: block;
}

.task-item__name {
  font-size: 24rpx;
  font-weight: 700;
  color: #1f2937;
}

.task-item__desc {
  margin-top: 6rpx;
  font-size: 21rpx;
  line-height: 1.5;
  color: #64748b;
}

.task-item__meta {
  margin-top: 6rpx;
  font-size: 20rpx;
  color: #94a3b8;
}

.task-item__side {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 10rpx;
  flex-shrink: 0;
}

.task-item__progress {
  font-size: 22rpx;
  font-weight: 700;
  color: #1d4ed8;
}

.overview-callout {
  margin-top: 18rpx;
  padding: 18rpx 20rpx;
  border-radius: 24rpx;
  background: rgba(245, 158, 11, 0.08);
  border: 1px solid rgba(245, 158, 11, 0.14);
  color: #8f4d18;
  display: flex;
  flex-direction: column;
  gap: 6rpx;
  font-size: 22rpx;
  line-height: 1.6;
}

.ranking-summary-card {
  padding: 22rpx;
  border-radius: 26rpx;
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.08), rgba(16, 185, 129, 0.08));
  border: 1px solid rgba(59, 130, 246, 0.08);
}

.ranking-summary-card__rule,
.ranking-summary-card__time {
  display: block;
  font-size: 21rpx;
  line-height: 1.6;
  color: #64748b;
}

.ranking-summary-card__leader {
  display: flex;
  align-items: center;
  gap: 16rpx;
  margin-top: 16rpx;
}

.ranking-summary-card__time {
  margin-top: 14rpx;
}

.ranking-list--full {
  margin-bottom: 20rpx;
}

.ranking-list__rank {
  width: 64rpx;
  font-size: 28rpx;
  font-weight: 800;
  color: #1d4ed8;
}

.ranking-list__avatar {
  width: 72rpx;
  height: 72rpx;
  border-radius: 50%;
}

.ranking-list__avatar--fallback,
.feed-author__avatar--fallback {
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #dbeafe, #bfdbfe);
  color: #1d4ed8;
  font-size: 24rpx;
  font-weight: 700;
}

.deposit-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 14rpx;
}

.deposit-grid__item {
  padding: 18rpx 16rpx;
  border-radius: 22rpx;
  background: #f8fafc;
  text-align: center;
}

.deposit-grid__value {
  display: block;
  font-size: 24rpx;
  font-weight: 700;
  color: #1f2937;
}

.deposit-grid__label {
  display: block;
  margin-top: 8rpx;
  font-size: 20rpx;
  color: #64748b;
}

.group-info-card.cover-ocean,
.group-info-card.cover-sunrise,
.group-info-card.cover-forest,
.group-info-card.cover-berry,
.group-info-card.cover-night,
.group-info-card.cover-sand {
  color: #fff;
}

.group-info-card.cover-ocean {
  background: linear-gradient(135deg, #4f8ef7, #54d2d2);
}

.group-info-card.cover-sunrise {
  background: linear-gradient(135deg, #ff9966, #ff5e62);
}

.group-info-card.cover-forest {
  background: linear-gradient(135deg, #2d6a4f, #52b788);
}

.group-info-card.cover-berry {
  background: linear-gradient(135deg, #c850c0, #ffcc70);
}

.group-info-card.cover-night {
  background: linear-gradient(135deg, #0f172a, #334155);
}

.group-info-card.cover-sand {
  background: linear-gradient(135deg, #d9a066, #f2d0a4);
}

.member-list,
.feed-list,
.request-list {
  padding: 0 30rpx;
}

.approval-section {
  margin-bottom: 26rpx;
}

.member-section {
  margin-top: 10rpx;
}

.member-section--after-approval {
  margin-top: 8rpx;
}

.member-item,
.feed-item,
.request-item {
  background: white;
  border-radius: 32rpx;
  padding: 26rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.02);
}

.member-item {
  display: flex;
  align-items: center;
}

.member-item--full {
  justify-content: space-between;
  gap: 18rpx;
}

.rank-avatar {
  width: 80rpx;
  height: 80rpx;
  border-radius: 50%;
  margin-right: 18rpx;
}

.member-main {
  flex: 1;
  min-width: 0;
  display: flex;
  align-items: center;
}

.member-side {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 10rpx;
  flex-shrink: 0;
}

.rank-info {
  flex: 1;
}

.rank-name,
.feed-user {
  display: block;
  font-size: 28rpx;
  font-weight: 600;
}

.rank-desc,
.feed-time {
  display: block;
  margin-top: 6rpx;
  font-size: 22rpx;
  color: #888888;
}

.rank-value {
  font-size: 22rpx;
  color: #4a90e2;
}

.request-item {
  display: flex;
  align-items: center;
  gap: 18rpx;
}

.request-avatar {
  width: 80rpx;
  height: 80rpx;
  border-radius: 50%;
  flex-shrink: 0;
}

.request-avatar--fallback {
  display: flex;
  align-items: center;
  justify-content: center;
  background: #dbeafe;
  color: #2563eb;
  font-size: 28rpx;
  font-weight: 700;
}

.request-copy {
  flex: 1;
  min-width: 0;
}

.request-name,
.request-time {
  display: block;
}

.request-name {
  font-size: 28rpx;
  font-weight: 600;
  color: #1f2937;
}

.request-time {
  margin-top: 6rpx;
  font-size: 22rpx;
  color: #888888;
}

.request-actions {
  display: flex;
  gap: 12rpx;
  flex-shrink: 0;
}

.feed-head {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 20rpx;
  margin-bottom: 12rpx;
}

.feed-item--rich {
  gap: 0;
}

.feed-author {
  display: flex;
  align-items: center;
  min-width: 0;
  gap: 16rpx;
}

.feed-author__avatar {
  width: 76rpx;
  height: 76rpx;
  border-radius: 50%;
  flex-shrink: 0;
}

.feed-head-copy {
  flex: 1;
  min-width: 0;
}

.feed-tags {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 10rpx;
  flex-wrap: wrap;
  max-width: 240rpx;
}

.feed-visibility {
  flex-shrink: 0;
  display: inline-flex;
  align-items: center;
  height: 42rpx;
  padding: 0 14rpx;
  border-radius: 999rpx;
  background: rgba(74, 144, 226, 0.1);
  color: #4a90e2;
  font-size: 20rpx;
  font-weight: 700;
}

.feed-content {
  display: block;
  font-size: 26rpx;
  line-height: 1.7;
}

.feed-images {
  margin-top: 18rpx;
  display: grid;
  gap: 12rpx;
}

.feed-images.grid-1 {
  grid-template-columns: 1fr;
}

.feed-images.grid-2 {
  grid-template-columns: repeat(2, 1fr);
}

.feed-images.grid-3 {
  grid-template-columns: repeat(3, 1fr);
}

.feed-image {
  width: 100%;
  aspect-ratio: 1;
  border-radius: 22rpx;
}

.feed-foot {
  margin-top: 18rpx;
  padding-top: 18rpx;
  border-top: 1px solid rgba(15, 23, 42, 0.06);
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 20rpx;
  font-size: 22rpx;
  color: #64748b;
}

.load-more-btn {
  width: calc(100% - 60rpx);
  margin: 8rpx 30rpx 0;
}

.loading-inline {
  margin: 16rpx 30rpx 0;
  text-align: center;
  font-size: 22rpx;
  color: #64748b;
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
  color: #333333;
  margin-bottom: 8rpx;
}

.composer-subtitle {
  display: block;
  font-size: 22rpx;
  line-height: 1.5;
  color: #7b8794;
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
  color: #333333;
}

.composer-row-hint,
.composer-current-desc {
  display: block;
  font-size: 20rpx;
  line-height: 1.5;
  color: #7b8794;
}

.composer-current-circle {
  padding: 20rpx 22rpx;
  border-radius: 24rpx;
  background: #f6f8fb;
  border: 1px solid rgba(74, 144, 226, 0.08);
}

.composer-current-name {
  display: block;
  font-size: 24rpx;
  font-weight: 700;
  color: #333333;
  margin-bottom: 8rpx;
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
  background: #f6f8fb;
  border: 1px solid rgba(15, 23, 42, 0.06);
}

.composer-type.active {
  background: rgba(74, 144, 226, 0.12);
  border-color: rgba(74, 144, 226, 0.24);
}

.composer-type-icon {
  font-size: 28rpx;
}

.composer-type-text {
  font-size: 20rpx;
  color: #7b8794;
}

.composer-type.active .composer-type-text {
  color: #4a90e2;
  font-weight: 700;
}

.composer-toggle {
  display: inline-flex;
  align-items: center;
  gap: 10rpx;
  padding: 6rpx;
  border-radius: 999rpx;
  background: #f6f8fb;
  border: 1px solid rgba(15, 23, 42, 0.06);
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
  color: #7b8794;
}

.composer-toggle-chip.active {
  background: #4a90e2;
  color: white;
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
  background: #f6f8fb;
  border: 1px solid rgba(15, 23, 42, 0.06);
  color: #666666;
  font-size: 22rpx;
  font-weight: 600;
}

.composer-chip.active {
  background: rgba(74, 144, 226, 0.12);
  border-color: rgba(74, 144, 226, 0.24);
  color: #4a90e2;
}

.composer-empty-tip {
  padding: 18rpx 20rpx;
  border-radius: 22rpx;
  background: rgba(255, 193, 7, 0.08);
  border: 1px solid rgba(255, 193, 7, 0.14);
  font-size: 21rpx;
  line-height: 1.6;
  color: #666666;
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
  color: #2ba779;
  font-size: 24rpx;
  font-weight: 700;
  line-height: 1.4;
}

.composer-record-guide__desc {
  display: block;
  margin-top: 8rpx;
  color: #7b8794;
  font-size: 21rpx;
  line-height: 1.6;
}

.composer-form-card {
  padding: 22rpx;
  border-radius: 26rpx;
  background: rgba(248, 250, 252, 0.9);
  border: 1px solid rgba(15, 23, 42, 0.06);
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
  color: #7b8794;
  font-size: 21rpx;
  line-height: 1.6;
}

.composer-food-estimator {
  margin-top: 18rpx;
  padding: 18rpx;
  border-radius: 20rpx;
  background: rgba(74, 144, 226, 0.08);
  border: 1px solid rgba(74, 144, 226, 0.16);
}

.composer-food-estimator__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 18rpx;
  margin-bottom: 14rpx;
}

.composer-food-estimator__label {
  color: #7b8794;
  font-size: 22rpx;
  font-weight: 600;
}

.composer-food-estimator__portion {
  display: flex;
  align-items: center;
  gap: 10rpx;
  color: #7b8794;
  font-size: 22rpx;
}

.composer-food-estimator__portion-input {
  width: 92rpx;
  height: 52rpx;
  padding: 0 14rpx;
  box-sizing: border-box;
  border-radius: 14rpx;
  background: #ffffff;
  border: 1px solid rgba(15, 23, 42, 0.06);
  color: #333333;
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
  border: 1px solid rgba(15, 23, 42, 0.06);
  color: #333333;
  font-size: 24rpx;
  line-height: 1.25;
}

.composer-food-estimator__chip text:last-child {
  color: #7b8794;
  font-size: 20rpx;
}

.composer-food-estimator__chip.active {
  background: #ffffff;
  border-color: #4a90e2;
  color: #4a90e2;
}

.composer-unit {
  flex-shrink: 0;
  color: #7b8794;
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
  border: 1px solid rgba(15, 23, 42, 0.06);
  color: #7b8794;
  font-size: 21rpx;
  font-weight: 600;
  line-height: 1;
}

.composer-segment.active {
  background: rgba(74, 144, 226, 0.12);
  border-color: rgba(74, 144, 226, 0.24);
  color: #2563eb;
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
  border: 1px solid rgba(15, 23, 42, 0.06);
  color: #7b8794;
  font-size: 22rpx;
  font-weight: 600;
  line-height: 54rpx;
}

.composer-exercise-chip.active {
  background: rgba(74, 144, 226, 0.12);
  border-color: rgba(74, 144, 226, 0.24);
  color: #2563eb;
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
  border: 1px solid rgba(15, 23, 42, 0.06);
}

.composer-habit-item.active {
  background: rgba(74, 144, 226, 0.12);
  border-color: rgba(74, 144, 226, 0.24);
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
  color: #333333;
  font-size: 24rpx;
  font-weight: 700;
  line-height: 1.35;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.composer-habit-desc {
  display: block;
  color: #7b8794;
  font-size: 20rpx;
  line-height: 1.45;
}

.composer-habit-check {
  flex-shrink: 0;
  color: #4a90e2;
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
  color: #333333;
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

.composer-retry-row {
  margin-top: 10rpx;
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
  color: #98a2b3;
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
  border-top: 1px solid rgba(15, 23, 42, 0.06);
}

.composer-btn {
  flex: 1;
}

.loading-block,
.empty-inline {
  margin: 24rpx 30rpx 0;
  padding: 48rpx 36rpx;
  background: white;
  border-radius: 32rpx;
  text-align: center;
  color: #888888;
}

.empty-inline {
  padding: 28rpx;
}

.empty-inline--error {
  display: flex;
  flex-direction: column;
  gap: 12rpx;
  align-items: center;
}

.page-bottom-space {
  height: 40rpx;
}
</style>
