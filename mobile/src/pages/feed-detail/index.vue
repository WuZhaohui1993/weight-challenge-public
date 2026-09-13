<template>
  <view class="page-container wc-page-enter">
    <prototype-header title="动态详情" back-url="/pages/circle/index" />

    <scroll-view
      class="content wc-section-enter"
      scroll-y
      scroll-with-animation
      scroll-anchoring
      :scroll-top="contentScrollTop"
      :scroll-into-view="commentScrollIntoView"
      @scroll="handleContentScroll"
    >
      <view v-if="loading" class="loading-block">动态加载中...</view>

      <template v-else-if="feed">
        <app-section-header compact :title="feedSectionTitle" :subtitle="feedSectionSubtitle" />

        <view class="feed-detail">
          <view class="scope-chip-row">
            <text v-if="feed.isFeatured" class="scope-chip scope-chip--featured">精选动态</text>
            <text class="scope-chip" :class="feed.visibilityScope === 'public' ? 'scope-chip--public' : 'scope-chip--private'">
              {{ feed.visibilityScope === 'public' ? '公开动态' : '仅圈子成员可见' }}
            </text>
            <text v-if="feed.originCircleName" class="scope-chip scope-chip--soft">
              {{ feed.visibilityScope === 'public' ? `同步自 ${feed.originCircleName}` : feed.originCircleName }}
            </text>
          </view>

          <view class="feed-header">
            <resolved-image
              class="avatar"
              :src="feed.userAvatar"
              mode="aspectFill"
              shape="avatar"
              fallback-class="avatar avatar--fallback"
              :fallback-text="initialText(feed.userNickname)"
              @tap.stop="goUserProfile(feed.userId)"
            />
            <view class="user-info" @tap.stop="goUserProfile(feed.userId)">
              <text class="user-name">{{ feed.userNickname || '圈友' }}</text>
              <text class="feed-meta">{{ circleFeedTimeText(feed.createdAt) }} · {{ feed.originCircleName || '圈子' }}</text>
            </view>
          </view>

          <text class="feed-content">{{ feed.content || '这条动态还没有文字内容。' }}</text>

          <view v-if="feed.images?.length" class="feed-images" :class="`grid-${Math.min(feed.images.length, 3)}`">
            <resolved-image
              class="feed-img"
              v-for="(img, idx) in feed.images"
              :key="`${feed.id}-${idx}`"
              :src="img"
              mode="widthFix"
              :preview-list="feed.images"
              :preview-index="idx"
            />
          </view>

          <view class="feed-actions">
            <view class="action-item wc-reaction-chip wc-pressable" :class="{ active: feed.likedByMe }" @tap="handleFeedLike">
              <text class="action-icon">{{ feed.likedByMe ? '❤️' : '🤍' }}</text>
              <text>{{ feed.likesCount || 0 }}</text>
            </view>
            <view class="action-item wc-reaction-chip wc-pressable" @tap="scrollToComment">
              <text class="action-icon">💬</text>
              <text>{{ feed.commentsCount || 0 }}</text>
            </view>
            <view v-if="feed.originCircleId" class="action-item wc-reaction-chip wc-pressable" @tap="goCircleDetail">
              <text class="action-icon">⭕</text>
              <text>看圈子</text>
            </view>
            <view v-if="feed.ownedByMe" class="action-item wc-reaction-chip wc-pressable" @tap="confirmDeleteFeed">
              <text class="action-icon">🗑️</text>
              <text>删除</text>
            </view>
          </view>
        </view>

        <app-section-header compact :title="commentSectionTitle" :subtitle="commentSectionSubtitle" />

        <view id="feed-comment-section" class="comment-section">
          <template v-if="canViewComments">
            <view v-if="commentLoading" class="comment-loading">评论加载中...</view>

            <app-empty-state
              v-else-if="commentLoadFailed"
              icon="⚠️"
              title="评论加载失败"
              :description="commentLoadErrorMessage || '稍后再试一次。'"
            >
              <button class="flow-btn flow-btn--secondary flow-btn--compact" @tap="loadComments">重新整理评论</button>
            </app-empty-state>

            <view v-else-if="commentThreads.length" class="comment-list">
              <view
                v-for="thread in commentThreads"
                :key="thread.root.id"
                :id="commentAnchorId(thread.root.id)"
                class="comment-item"
                :class="{ 'comment-item--highlight': highlightedCommentId === thread.root.id }"
              >
                <resolved-image
                  class="comment-avatar"
                  :src="thread.root.userAvatar"
                  mode="aspectFill"
                  shape="avatar"
                  fallback-class="comment-avatar comment-avatar--fallback"
                  :fallback-text="initialText(thread.root.userNickname)"
                />
                <view class="comment-body">
                  <view class="comment-user-row">
                    <text class="comment-user" @tap="goUserProfile(thread.root.userId)">{{ thread.root.userNickname || `用户${thread.root.userId}` }}</text>
                    <text class="comment-time">{{ circleFeedTimeText(thread.root.createdAt) }}</text>
                  </view>
                  <text
                    v-if="thread.root.content || thread.root.deleted"
                    class="comment-text"
                    :class="{ 'comment-text--deleted': thread.root.deleted }"
                  >{{ thread.root.content }}</text>
                  <view v-if="thread.root.images?.length && !thread.root.deleted" class="comment-image-grid">
                    <view
                      v-for="(image, imageIndex) in thread.root.images"
                      :key="`${thread.root.id}-${imageIndex}`"
                      class="comment-image-item"
                    >
                      <resolved-image
                        class="comment-image-preview"
                        :src="image"
                        mode="aspectFill"
                        :preview-list="thread.root.images"
                        :preview-index="imageIndex"
                      />
                    </view>
                  </view>
                  <view v-if="canCommentFeed" class="comment-actions">
                    <text
                      v-if="!thread.root.deleted"
                      class="comment-action wc-inline-link wc-inline-link--action wc-inline-link--strong"
                      @tap="startReply(thread.root)"
                    >回复</text>
                    <text
                      v-if="thread.root.ownedByMe"
                      class="comment-action wc-inline-link wc-inline-link--action wc-inline-link--subtle"
                      @tap="confirmDeleteComment(thread.root)"
                    >删除</text>
                  </view>

                  <view v-if="thread.replies.length" class="comment-replies">
                    <view
                      v-for="reply in thread.replies"
                      :key="reply.comment.id"
                      :id="commentAnchorId(reply.comment.id)"
                      class="comment-reply-item"
                      :class="{
                        'comment-reply-item--highlight': highlightedCommentId === reply.comment.id,
                        'comment-reply-item--nested': reply.depth > 1
                      }"
                      :style="{ marginLeft: `${Math.max(0, reply.depth - 1) * 28}rpx` }"
                    >
                      <view class="comment-reply-head">
                        <text class="comment-user" @tap="goUserProfile(reply.comment.userId)">{{ reply.comment.userNickname || `用户${reply.comment.userId}` }}</text>
                        <text class="comment-time">{{ circleFeedTimeText(reply.comment.createdAt) }}</text>
                      </view>
                      <text
                        v-if="reply.comment.content || reply.comment.deleted"
                        class="comment-text"
                        :class="{ 'comment-text--deleted': reply.comment.deleted }"
                      >
                        <text v-if="reply.comment.replyToUserNickname" class="comment-reply-target">回复 {{ reply.comment.replyToUserNickname }}：</text>
                        {{ reply.comment.content }}
                      </text>
                      <view v-if="reply.comment.images?.length && !reply.comment.deleted" class="comment-image-grid">
                        <view
                          v-for="(image, imageIndex) in reply.comment.images"
                          :key="`${reply.comment.id}-${imageIndex}`"
                          class="comment-image-item"
                        >
                          <resolved-image
                            class="comment-image-preview"
                            :src="image"
                            mode="aspectFill"
                            :preview-list="reply.comment.images"
                            :preview-index="imageIndex"
                          />
                        </view>
                      </view>
                      <view v-if="canCommentFeed" class="comment-actions">
                        <text
                          v-if="!reply.comment.deleted"
                          class="comment-action wc-inline-link wc-inline-link--action wc-inline-link--strong"
                          @tap="startReply(reply.comment)"
                        >回复</text>
                        <text
                          v-if="reply.comment.ownedByMe"
                          class="comment-action wc-inline-link wc-inline-link--action wc-inline-link--subtle"
                          @tap="confirmDeleteComment(reply.comment)"
                        >删除</text>
                      </view>
                    </view>
                  </view>
                </view>
              </view>
            </view>

            <app-empty-state
              v-else
              icon="💬"
              :title="emptyCommentTitle"
              :description="canCommentFeed ? '做第一个参与讨论的人。' : '当前仅可浏览这里的讨论。'"
            />

            <view v-if="replyTarget && canCommentFeed" class="reply-banner">
              <view class="reply-banner-copy">
                <text class="reply-banner-title">正在回复 {{ replyTarget.userNickname || `用户${replyTarget.userId}` }}</text>
                <text class="reply-banner-desc">{{ getCommentSummary(replyTarget) }}</text>
              </view>
              <text class="reply-banner-action wc-inline-link wc-inline-link--action wc-inline-link--subtle" @tap="clearReplyTarget">取消</text>
            </view>

            <view v-if="canCommentFeed" class="comment-editor">
              <textarea
                v-model="commentContent"
                class="wc-form-textarea comment-textarea"
                maxlength="200"
                :placeholder="commentPlaceholder"
                placeholder-style="color:#9aa4b2;"
              />
              <view class="comment-editor-tools">
                <view class="comment-editor-copy">
                  <text class="comment-editor-label">评论图片</text>
                  <text class="comment-editor-hint">支持 1-9 张图片，可与文字一起评论，单张不超过 {{ getUploadSizeLimitText() }}。</text>
                </view>
                <button
                  class="flow-btn flow-btn--secondary flow-btn--compact"
                  :loading="commentUploading"
                  :disabled="commentUploading || commentImages.length >= 9"
                  @tap="chooseCommentImages"
                >
                  {{ commentImages.length >= 9 ? '已满 9 张' : '添加图片' }}
                </button>
              </view>
              <view v-if="commentImages.length" class="comment-image-grid">
                <view
                  v-for="(image, index) in commentImages"
                  :key="image.id"
                  class="comment-image-item"
                >
                  <resolved-image
                    class="comment-image-preview"
                    :src="getUploadedImagePreviewSource(image)"
                    mode="aspectFill"
                    :preview-list="commentImagePreviewSources"
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
                    @tap.stop="retryCommentImage(index)"
                  >重试</view>
                  <view class="comment-image-remove" @tap="removeCommentImage(index)">×</view>
                </view>
              </view>
              <view class="comment-editor-footer">
                <text>{{ commentContent.length }}/200</text>
                <button
                  class="comment-submit flow-btn flow-btn--primary flow-btn--compact"
                  :loading="commentSubmitting"
                  :disabled="commentUploading"
                  @tap="submitComment"
                >
                  {{ replyTarget ? '发布回复' : '发布评论' }}
                </button>
              </view>
            </view>

            <view v-else class="scope-note">
              <text class="scope-note-title">{{ commentReadonlyTitle }}</text>
              <text class="scope-note-text">{{ commentReadonlyText }}</text>
            </view>
          </template>

          <template v-else>
            <view class="scope-note">
              <text class="scope-note-title">评论暂不可用</text>
              <text class="scope-note-text">动态信息还没有加载完成，请稍后再试。</text>
            </view>
            <app-empty-state
              icon="🔒"
              title="评论未加载"
              description="返回后重新进入动态详情即可再次尝试。"
            />
          </template>
        </view>
      </template>

      <view v-else class="empty-wrap">
        <app-empty-state
          :icon="feedLoadErrorMessage ? '⚠️' : '📝'"
          :title="feedLoadErrorMessage ? '动态加载失败' : '没有找到这条动态'"
          :description="feedLoadErrorMessage || '请返回圈子页重新选择动态。'"
        >
          <button
            v-if="feedLoadErrorMessage"
            class="flow-btn flow-btn--secondary flow-btn--compact"
            @tap="loadFeedDetail"
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
import { computed, nextTick, ref } from 'vue'
import { onLoad, onShow, onUnload } from '@dcloudio/uni-app'
import AppEmptyState from '@/components/AppEmptyState/index.vue'
import AppSectionHeader from '@/components/AppSectionHeader/index.vue'
import PrototypeHeader from '@/components/PrototypeHeader/index.vue'
import ResolvedImage from '@/components/ResolvedImage/index.vue'
import { deleteComment, deleteFeed, getFeedComments, getFeedDetail, postFeedComment, toggleFeedLike, uploadFeedImage } from '@/api/feed'
import { useUserStore } from '@/stores/user'
import type { CircleFeedCard, FeedCommentCard } from '@/types/api'
import { circleFeedTimeText, initialText } from '@/utils/circle'
import { toApiTimestamp } from '@/utils/datetime'
import { createOnShowSuspendGuard } from '@/utils/page'
import { openLoginPage } from '@/utils/request'
import { useDefaultPageShare } from '@/utils/share'
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

interface FeedCommentReplyNode {
  comment: FeedCommentCard
  depth: number
}

interface FeedCommentThread {
  root: FeedCommentCard
  replies: FeedCommentReplyNode[]
}

interface FeedDetailReturnSnapshot {
  id: number
  circleId: number | null
  originCircleId: number | null
  likesCount: number | null
  commentsCount: number | null
  likedByMe: boolean | null
  commentEnabled: boolean | null
  deleted?: boolean
  updatedAt: number
}

const FEED_DETAIL_RETURN_STORAGE_KEY = 'circleFeedDetailReturnSnapshot'
const COMMENT_SECTION_ID = 'feed-comment-section'
const feedId = ref<number | null>(null)
const userStore = useUserStore()
const feed = ref<CircleFeedCard | null>(null)
const feedDeleted = ref(false)
const loading = ref(false)
const feedLoadErrorMessage = ref('')
const comments = ref<FeedCommentCard[]>([])
const commentLoading = ref(false)
const commentLoadFailed = ref(false)
const commentLoadErrorMessage = ref('')
const commentSubmitting = ref(false)
const commentContent = ref('')
const commentImages = ref<UploadedImageAsset[]>([])
const commentImagePreviewSources = computed(() => commentImages.value
  .map((item) => getUploadedImagePreviewSource(item))
  .filter(Boolean))
const commentUploading = ref(false)
const replyTarget = ref<FeedCommentCard | null>(null)
const targetCommentId = ref<number | null>(null)
const highlightedCommentId = ref<number | null>(null)
const commentScrollIntoView = ref('')
const contentScrollTop = ref(0)
let lastContentScrollTop = 0
const onShowReloadGuard = createOnShowSuspendGuard()

const isPublicFeed = computed(() => feed.value?.visibilityScope === 'public')
const canViewComments = computed(() => Boolean(feed.value?.id))
const canCommentFeed = computed(() => Boolean(feed.value?.commentEnabled))
const feedSectionTitle = computed(() => (isPublicFeed.value ? '公开动态' : '圈内动态'))
const feedSectionSubtitle = computed(() =>
  isPublicFeed.value
    ? '这条内容会显示在发现页公开动态里，同时保留原圈子的上下文信息。'
    : '动态正文、图片和互动数据仅在当前圈子成员内展示。'
)
const commentSectionTitle = computed(() => (isPublicFeed.value ? '公开评论' : '圈内评论'))
const commentSectionSubtitle = computed(() =>
  canCommentFeed.value
    ? (isPublicFeed.value ? '已加入相关圈子的成员可以在这里参与公开评论。' : '圈子成员可以在这里评论、回复和补充图片。')
    : (isPublicFeed.value ? '这条公开动态仍保留圈子评论门槛，未加入相关圈子的用户只能查看评论。' : '当前账号只能查看评论，暂不能参与互动。')
)
const commentPlaceholder = computed(() =>
  replyTarget.value
    ? `回复 ${replyTarget.value.userNickname || `用户${replyTarget.value.userId}`}...`
    : '写下你的评论...'
)
const emptyCommentTitle = computed(() => (isPublicFeed.value ? '还没有公开评论' : '还没有圈内评论'))
const commentReadonlyTitle = computed(() => (isPublicFeed.value ? '加入圈子后可参与评论' : '当前仅可查看评论'))
const commentReadonlyText = computed(() =>
  isPublicFeed.value
    ? '这条动态虽然公开展示，但评论仍按圈子成员权限控制。先加入相关圈子，再来参与讨论。'
    : '只有具备圈子评论权限的成员才能发布评论或回复。'
)
const commentThreads = computed<FeedCommentThread[]>(() => {
  const sortedComments = [...comments.value].sort((left, right) => {
    const leftTime = toApiTimestamp(left.createdAt)
    const rightTime = toApiTimestamp(right.createdAt)
    if (leftTime !== rightTime) {
      return leftTime - rightTime
    }
    return Number(left.id || 0) - Number(right.id || 0)
  })
  const commentMap = new Map(sortedComments.map((item) => [item.id, item] as const))
  const childrenMap = new Map<number, FeedCommentCard[]>()
  const roots: FeedCommentCard[] = []

  for (const comment of sortedComments) {
    if (comment.replyToCommentId && commentMap.has(comment.replyToCommentId)) {
      const currentChildren = childrenMap.get(comment.replyToCommentId) || []
      currentChildren.push(comment)
      childrenMap.set(comment.replyToCommentId, currentChildren)
      continue
    }
    roots.push(comment)
  }

  function collectReplies(parentId: number, depth: number): FeedCommentReplyNode[] {
    const children = childrenMap.get(parentId) || []
    const nodes: FeedCommentReplyNode[] = []
    for (const child of children) {
      nodes.push({
        comment: child,
        depth
      })
      nodes.push(...collectReplies(child.id, Math.min(depth + 1, 3)))
    }
    return nodes
  }

  return roots.map((root) => ({
    root,
    replies: collectReplies(root.id, 1)
  }))
})

onLoad((query) => {
  const parsedFeedId = Number(query?.feedId || 0)
  if (parsedFeedId > 0) {
    feedId.value = parsedFeedId
  }
  const parsedCommentId = Number(query?.commentId || 0)
  if (parsedCommentId > 0) {
    targetCommentId.value = parsedCommentId
  }
})

onShow(() => {
  if (onShowReloadGuard.isSuspended()) {
    return
  }
  if (feedId.value) {
    void loadFeedDetail()
  }
})

onUnload(() => {
  persistFeedDetailReturnSnapshot()
})

async function loadFeedDetail() {
  if (!feedId.value) {
    return
  }
  loading.value = true
  feedLoadErrorMessage.value = ''
  try {
    feed.value = await getFeedDetail(feedId.value)
    await loadComments()
  } catch (error) {
    console.error('加载动态详情失败', error)
    feed.value = null
    feedLoadErrorMessage.value = error instanceof Error ? error.message || '请稍后再试一次。' : '请稍后再试一次。'
  } finally {
    loading.value = false
  }
}

async function loadComments() {
  if (!feedId.value) {
    comments.value = []
    commentLoadFailed.value = false
    commentLoadErrorMessage.value = ''
    return
  }
  commentLoading.value = true
  commentLoadFailed.value = false
  commentLoadErrorMessage.value = ''
  try {
    const page = await getFeedComments(feedId.value, { pageNum: 1, pageSize: 50 })
    comments.value = page.list || []
    await focusTargetComment()
  } catch (error) {
    console.error('加载评论失败', error)
    comments.value = []
    commentLoadFailed.value = true
    commentLoadErrorMessage.value = error instanceof Error ? error.message : '评论接口请求失败'
  } finally {
    commentLoading.value = false
  }
}

function goCircleDetail() {
  const targetCircleId = feed.value?.originCircleId
  if (!targetCircleId) {
    return
  }
  uni.navigateTo({ url: `/pages/circle-private/index?circleId=${targetCircleId}` })
}

function goUserProfile(userId?: number | null) {
  if (!userId) {
    return
  }
  uni.navigateTo({ url: `/pages/user-profile/index?userId=${userId}` })
}

function buildFeedDetailRoute() {
  return feedId.value ? `/pages/feed-detail/index?feedId=${feedId.value}` : '/pages/circle/index'
}

function ensureLoggedInForFeedAction() {
  if (userStore.isLoggedIn) {
    return true
  }
  openLoginPage('safe', buildFeedDetailRoute())
  return false
}

function scrollToComment() {
  commentScrollIntoView.value = ''
  nextTick(() => {
    commentScrollIntoView.value = COMMENT_SECTION_ID
  })

  if (!canCommentFeed.value) {
    uni.showToast({ title: isPublicFeed.value ? '可查看评论，加入圈子后才能参与' : '当前仅可查看评论', icon: 'none' })
  }
}

function startReply(comment: FeedCommentCard) {
  if (!canCommentFeed.value) {
    uni.showToast({ title: isPublicFeed.value ? '加入圈子后才能回复评论' : '当前不能回复评论', icon: 'none' })
    return
  }
  if (comment.deleted) {
    uni.showToast({ title: '已删除评论不能继续回复', icon: 'none' })
    return
  }
  replyTarget.value = comment
}

function clearReplyTarget() {
  replyTarget.value = null
}

function getCommentSummary(comment?: FeedCommentCard | null) {
  if (!comment) {
    return ''
  }
  const content = comment.content.trim()
  if (content) {
    return content
  }
  if (comment.images?.length) {
    return '[图片评论]'
  }
  return '这条评论暂时没有文字内容'
}

function commentAnchorId(commentId: number) {
  return `comment-${commentId}`
}

async function focusTargetComment() {
  if (!targetCommentId.value) {
    return
  }
  if (!comments.value.some((item) => item.id === targetCommentId.value)) {
    return
  }
  highlightedCommentId.value = targetCommentId.value
  await nextTick()
  commentScrollIntoView.value = commentAnchorId(targetCommentId.value)
  setTimeout(() => {
    commentScrollIntoView.value = ''
  }, 800)
  setTimeout(() => {
    if (highlightedCommentId.value === targetCommentId.value) {
      highlightedCommentId.value = null
    }
  }, 2800)
}

function handleContentScroll(event: any) {
  const nextTop = Number(event?.detail?.scrollTop || 0)
  lastContentScrollTop = nextTop
}

function buildFeedDetailReturnSnapshot(overrides: Partial<FeedDetailReturnSnapshot> = {}): FeedDetailReturnSnapshot | null {
  if (!feed.value?.id) {
    return null
  }
  return {
    id: feed.value.id,
    circleId: feed.value.circleId || null,
    originCircleId: feed.value.originCircleId || feed.value.circleId || null,
    likesCount: feed.value.likesCount ?? null,
    commentsCount: feed.value.commentsCount ?? null,
    likedByMe: typeof feed.value.likedByMe === 'boolean' ? feed.value.likedByMe : null,
    commentEnabled: typeof feed.value.commentEnabled === 'boolean' ? feed.value.commentEnabled : null,
    deleted: feedDeleted.value || undefined,
    updatedAt: Date.now(),
    ...overrides
  }
}

function persistFeedDetailReturnSnapshot(overrides: Partial<FeedDetailReturnSnapshot> = {}) {
  const snapshot = buildFeedDetailReturnSnapshot(overrides)
  if (!snapshot) {
    return
  }
  try {
    uni.setStorageSync(FEED_DETAIL_RETURN_STORAGE_KEY, snapshot)
  } catch (error) {
    console.warn('保存动态详情返回状态失败:', error)
  }
}

async function handleFeedLike() {
  if (!feed.value?.id) {
    return
  }
  if (!ensureLoggedInForFeedAction()) {
    return
  }
  try {
    feed.value = await toggleFeedLike(feed.value.id)
  } catch (error: any) {
    uni.showToast({ title: error?.message || '点赞操作失败', icon: 'none' })
  }
}

function confirmDeleteFeed() {
  if (!feed.value?.id || !feed.value.ownedByMe) {
    return
  }
  if (!ensureLoggedInForFeedAction()) {
    return
  }
  uni.showModal({
    title: '删除动态',
    content: '删除后这条动态将从圈子和公开流中移除，确认继续吗？',
    success: async (result) => {
      if (!result.confirm || !feed.value?.id) {
        return
      }
      try {
        await deleteFeed(feed.value.id)
        feedDeleted.value = true
        persistFeedDetailReturnSnapshot({ deleted: true })
        uni.showToast({ title: '动态已删除', icon: 'success' })
        setTimeout(() => {
          uni.navigateBack({
            fail: () => {
              uni.reLaunch({ url: '/pages/circle/index' })
            }
          })
        }, 300)
      } catch (error: any) {
        uni.showToast({ title: error?.message || '删除失败', icon: 'none' })
      }
    }
  })
}

function confirmDeleteComment(comment: FeedCommentCard) {
  if (!comment.id || !comment.ownedByMe) {
    return
  }
  if (!ensureLoggedInForFeedAction()) {
    return
  }
  uni.showModal({
    title: '删除评论',
    content: comment.deleted ? '这条评论已经是删除占位。若继续删除，将尝试隐藏它。' : '删除后可能显示为“该评论已删除”占位，确认继续吗？',
    success: async (result) => {
      if (!result.confirm) {
        return
      }
      try {
        await deleteComment(comment.id)
        if (replyTarget.value?.id === comment.id) {
          clearReplyTarget()
        }
        uni.showToast({ title: '评论已删除', icon: 'success' })
        await loadFeedDetail()
      } catch (error: any) {
        uni.showToast({ title: error?.message || '删除失败', icon: 'none' })
      }
    }
  })
}

async function submitComment() {
  const content = commentContent.value.trim()
  if (!feedId.value) {
    uni.showToast({ title: '动态信息异常，暂不能评论', icon: 'none' })
    return
  }
  if (!ensureLoggedInForFeedAction()) {
    return
  }
  if (!canCommentFeed.value) {
    uni.showToast({ title: isPublicFeed.value ? '加入圈子后才能评论这条动态' : '当前不能评论这条动态', icon: 'none' })
    return
  }
  if (commentUploading.value) {
    uni.showToast({ title: '图片还在上传中，请稍后发布', icon: 'none' })
    return
  }
  if (hasFailedImageAssets(commentImages.value)) {
    uni.showToast({ title: '有图片上传失败，请删除后重试', icon: 'none' })
    return
  }
  if (!content && !commentImages.value.length) {
    uni.showToast({ title: '请先填写评论内容或上传图片', icon: 'none' })
    return
  }

  commentSubmitting.value = true
  try {
    const isReply = Boolean(replyTarget.value)
    await postFeedComment(feedId.value, {
      content,
      images: commentImages.value.map((item) => item.url).filter(Boolean),
      replyToCommentId: replyTarget.value?.id || null,
      replyToUserId: replyTarget.value?.userId || null
    })
    commentContent.value = ''
    commentImages.value = []
    clearReplyTarget()
    uni.showToast({ title: isReply ? '回复已发布' : '评论已发布', icon: 'success' })
    await loadFeedDetail()
  } catch (error: any) {
    uni.showToast({ title: error?.message || '评论发布失败', icon: 'none' })
  } finally {
    commentSubmitting.value = false
  }
}

async function chooseCommentImages() {
  if (!ensureLoggedInForFeedAction()) {
    return
  }
  if (commentUploading.value || commentImages.value.length >= 9) {
    return
  }
  await onShowReloadGuard.runWhileSuspended(async () => {
    try {
      await chooseAndUploadImages({
        currentCount: commentImages.value.length,
        upload: uploadFeedImage,
        fallbackErrorMessage: '评论图片上传失败',
        setUploading: (uploading) => {
          commentUploading.value = uploading
        },
        onLocalAssetsSelected: (assets) => {
          commentImages.value = [...commentImages.value, ...assets].slice(0, 9)
        },
        onAssetUpdated: (asset) => {
          commentImages.value = replaceUploadedImageAsset(commentImages.value, asset)
        }
      })
    } catch (error: any) {
      uni.showToast({ title: error?.message || '评论图片上传失败', icon: 'none' })
    } finally {
      await restoreContentScrollPosition()
    }
  })
}

function removeCommentImage(index: number) {
  commentImages.value = commentImages.value.filter((_, currentIndex) => currentIndex !== index)
}

async function retryCommentImage(index: number) {
  const image = commentImages.value[index]
  if (!image || image.uploading) {
    return
  }
  try {
    await retryUploadedImageAsset({
      asset: image,
      upload: uploadFeedImage,
      fallbackErrorMessage: '评论图片上传失败',
      setUploading: (uploading) => {
        commentUploading.value = uploading
      },
      onAssetUpdated: (asset) => {
        commentImages.value = replaceUploadedImageAsset(commentImages.value, asset)
      }
    })
  } catch (error: any) {
    uni.showToast({ title: error?.message || '评论图片上传失败', icon: 'none' })
  }
}

async function restoreContentScrollPosition() {
  const targetTop = Math.max(0, lastContentScrollTop)
  contentScrollTop.value = 0
  await nextTick()
  contentScrollTop.value = targetTop
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

.feed-detail,
.comment-section {
  background: var(--wc-surface-strong);
  padding: 30rpx;
  margin: 24rpx 30rpx 0;
  border-radius: 32rpx;
  border: 1px solid var(--wc-line);
  box-shadow: var(--wc-shadow-soft);
}

.scope-chip-row {
  display: flex;
  align-items: center;
  gap: 12rpx;
  margin-bottom: 18rpx;
}

.scope-chip {
  display: inline-flex;
  align-items: center;
  height: 46rpx;
  padding: 0 16rpx;
  border-radius: 999rpx;
  font-size: 20rpx;
  font-weight: 700;
}

.scope-chip--private {
  background: rgba(74, 144, 226, 0.12);
  color: var(--wc-primary);
}

.scope-chip--public {
  background: rgba(79, 209, 197, 0.14);
  color: #0f9b8e;
}

.scope-chip--featured {
  background: rgba(242, 155, 56, 0.16);
  color: var(--wc-warning);
}

.scope-chip--soft {
  background: var(--wc-surface-muted);
  color: var(--wc-text-soft);
}

.comment-section {
  margin-top: 0;
}

.scope-note {
  margin-bottom: 24rpx;
  padding: 22rpx 24rpx;
  border-radius: 24rpx;
  background: rgba(74, 144, 226, 0.08);
  border: 1px solid rgba(74, 144, 226, 0.12);
}

.scope-note-title {
  display: block;
  font-size: 24rpx;
  font-weight: 700;
  color: var(--wc-text);
  margin-bottom: 8rpx;
}

.scope-note-text {
  display: block;
  font-size: 22rpx;
  line-height: 1.6;
  color: var(--wc-text-soft);
}

.comment-loading {
  padding: 18rpx 0 24rpx;
  font-size: 22rpx;
  color: var(--wc-text-soft);
}

.comment-list {
  display: flex;
  flex-direction: column;
  gap: 18rpx;
}

.comment-item {
  display: flex;
  gap: 16rpx;
  align-items: flex-start;
}

.comment-item--highlight .comment-body,
.comment-reply-item--highlight {
  box-shadow: 0 0 0 2rpx rgba(77, 138, 255, 0.22);
  background: rgba(239, 246, 255, 0.96);
}

.comment-avatar {
  width: 72rpx;
  height: 72rpx;
  border-radius: 50%;
  flex-shrink: 0;
}

.avatar--fallback,
.comment-avatar--fallback {
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #dbeafe, #bfdbfe);
  color: #1d4ed8;
  font-weight: 700;
}

.comment-body {
  flex: 1;
  min-width: 0;
  padding: 18rpx 20rpx;
  border-radius: 24rpx;
  background: var(--wc-surface-muted);
}

.comment-user-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16rpx;
  margin-bottom: 8rpx;
}

.comment-user {
  font-size: 24rpx;
  font-weight: 700;
  color: var(--wc-text);
}

.comment-time {
  font-size: 20rpx;
  color: var(--wc-text-faint);
}

.comment-text {
  display: block;
  font-size: 24rpx;
  line-height: 1.65;
  color: var(--wc-text-soft);
}

.comment-text--deleted {
  color: var(--wc-text-faint);
  font-style: italic;
}

.comment-image-grid {
  margin-top: 14rpx;
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 14rpx;
}

.comment-image-item {
  position: relative;
  aspect-ratio: 1;
  border-radius: 20rpx;
  overflow: hidden;
  background: rgba(15, 23, 42, 0.06);
}

.comment-image-preview {
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
  padding: 8rpx 16rpx;
  border-radius: 999rpx;
  background: rgba(15, 23, 42, 0.72);
  color: #fff;
  font-size: 20rpx;
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
  padding: 7rpx 12rpx;
  border-radius: 999rpx;
  background: var(--wc-primary);
  color: #fff;
  font-size: 19rpx;
  font-weight: 700;
  line-height: 1;
  box-shadow: 0 8rpx 18rpx rgba(47, 128, 237, 0.24);
}

.comment-image-remove {
  position: absolute;
  top: 10rpx;
  left: 10rpx;
  z-index: 2;
  width: 38rpx;
  height: 38rpx;
  border-radius: 50%;
  background: rgba(15, 23, 42, 0.72);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24rpx;
  font-weight: 700;
}

.comment-actions {
  margin-top: 12rpx;
  display: flex;
  justify-content: flex-end;
  gap: 20rpx;
}

.comment-action {
  font-size: 22rpx;
}

.reply-banner {
  margin-top: 22rpx;
  padding: 20rpx 22rpx;
  border-radius: 22rpx;
  background: rgba(77, 138, 255, 0.08);
  border: 1px solid rgba(77, 138, 255, 0.14);
  display: flex;
  align-items: flex-start;
  gap: 20rpx;
}

.reply-banner-copy {
  flex: 1;
  min-width: 0;
}

.reply-banner-title {
  display: block;
  font-size: 24rpx;
  font-weight: 700;
  color: var(--wc-text);
  margin-bottom: 6rpx;
}

.reply-banner-desc {
  display: block;
  font-size: 22rpx;
  line-height: 1.6;
  color: var(--wc-text-soft);
}

.reply-banner-action {
  flex-shrink: 0;
  font-size: 22rpx;
}

.comment-replies {
  margin-top: 18rpx;
  padding-top: 18rpx;
  border-top: 1px dashed rgba(28, 37, 54, 0.08);
  display: flex;
  flex-direction: column;
  gap: 14rpx;
}

.comment-reply-item {
  padding: 16rpx 18rpx;
  border-radius: 20rpx;
  background: rgba(255, 255, 255, 0.78);
}

.comment-reply-item--nested {
  border-left: 4rpx solid rgba(77, 138, 255, 0.14);
}

.comment-reply-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16rpx;
  margin-bottom: 8rpx;
}

.comment-reply-target {
  color: var(--wc-primary);
  font-weight: 600;
}

.comment-editor {
  margin-top: 24rpx;
  padding-top: 20rpx;
  border-top: 1px solid var(--wc-line);
}

.comment-textarea {
  min-height: 148rpx;
}

.comment-editor-tools {
  margin-top: 18rpx;
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 20rpx;
}

.comment-editor-copy {
  flex: 1;
  min-width: 0;
}

.comment-editor-label {
  display: block;
  margin-bottom: 10rpx;
  font-size: 24rpx;
  font-weight: 600;
  color: var(--wc-text);
}

.comment-editor-hint {
  display: block;
  font-size: 20rpx;
  line-height: 1.55;
  color: var(--wc-text-faint);
}

.comment-editor-footer {
  margin-top: 12rpx;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16rpx;
  font-size: 20rpx;
  color: var(--wc-text-faint);
}

.comment-submit {
  min-width: 168rpx;
}

.feed-header {
  display: flex;
  align-items: center;
  margin-bottom: 20rpx;
}

.avatar {
  width: 96rpx;
  height: 96rpx;
  border-radius: 50%;
  margin-right: 18rpx;
}

.user-info {
  flex: 1;
}

.user-name {
  display: block;
  font-size: 30rpx;
  font-weight: 600;
  margin-bottom: 6rpx;
}

.feed-meta {
  display: block;
  font-size: 22rpx;
  color: #888888;
}

.feed-content {
  display: block;
  font-size: 28rpx;
  line-height: 1.7;
  margin-bottom: 20rpx;
}

.feed-images {
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

.feed-img {
  width: 100%;
  border-radius: 24rpx;
}

.feed-actions {
  display: flex;
  justify-content: flex-end;
  gap: 16rpx;
  padding-top: 20rpx;
  margin-top: 20rpx;
  border-top: 1px solid var(--wc-line);
}

.action-item {
  min-width: 108rpx;
}

.action-item.active {
  color: #d8486b;
  border-color: rgba(216, 72, 107, 0.22);
  background: rgba(255, 239, 243, 0.92);
}

.action-icon {
  font-size: 30rpx;
}

.loading-block {
  margin: 24rpx 30rpx 0;
  padding: 56rpx 36rpx;
  border-radius: 32rpx;
  background: var(--wc-surface-strong);
  border: 1px solid var(--wc-line);
  box-shadow: var(--wc-shadow-soft);
  text-align: center;
  color: var(--wc-text-soft);
}

.page-bottom-space {
  height: 60rpx;
}
</style>
