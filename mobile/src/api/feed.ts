import http, { uploadFile } from '@/utils/request'
import type {
  ApiPagePayload,
  CircleFeedCard,
  CommonUploadPayload,
  FeedCommentCard,
  FeedCommentCreatePayload,
  FeedCreatePayload
} from '@/types/api'

function normalizeFeed(feed: Partial<CircleFeedCard>): CircleFeedCard {
  return {
    id: Number(feed.id || 0),
    circleId: feed.circleId ?? null,
    circleName: feed.circleName ?? null,
    originCircleName: feed.originCircleName ?? null,
    userId: Number(feed.userId || 0),
    userNickname: feed.userNickname ?? null,
    userAvatar: feed.userAvatar ?? null,
    feedType: feed.feedType ?? 'text',
    content: feed.content ?? null,
    images: Array.isArray(feed.images) ? feed.images : [],
    likesCount: typeof feed.likesCount === 'number' ? feed.likesCount : Number(feed.likesCount || 0),
    commentsCount: typeof feed.commentsCount === 'number' ? feed.commentsCount : Number(feed.commentsCount || 0),
    isFeatured: Boolean(feed.isFeatured),
    likedByMe: Boolean(feed.likedByMe),
    createdAt: feed.createdAt ?? null,
    visibilityScope: feed.visibilityScope ?? 'public',
    commentEnabled: typeof feed.commentEnabled === 'boolean' ? feed.commentEnabled : null,
    originCircleId: feed.originCircleId ?? null,
    syncCircleIds: Array.isArray(feed.syncCircleIds) ? feed.syncCircleIds : [],
    ownedByMe: typeof feed.ownedByMe === 'boolean' ? feed.ownedByMe : false,
    sourceType: feed.sourceType ?? null,
    sourceId: feed.sourceId ?? null
  }
}

function normalizeComment(comment: Partial<FeedCommentCard>): FeedCommentCard {
  return {
    id: Number(comment.id || 0),
    feedId: comment.feedId == null ? null : Number(comment.feedId),
    userId: Number(comment.userId || 0),
    userNickname: comment.userNickname ?? null,
    userAvatar: comment.userAvatar ?? null,
    content: comment.content || '',
    images: Array.isArray(comment.images) ? comment.images : [],
    replyToCommentId: comment.replyToCommentId ?? null,
    replyToUserId: comment.replyToUserId ?? null,
    replyToUserNickname: comment.replyToUserNickname ?? null,
    likesCount: typeof comment.likesCount === 'number' ? comment.likesCount : Number(comment.likesCount || 0),
    createdAt: comment.createdAt ?? null,
    deleted: typeof comment.deleted === 'boolean' ? comment.deleted : false,
    ownedByMe: typeof comment.ownedByMe === 'boolean' ? comment.ownedByMe : false
  }
}

export async function getPublicFeedList(params?: { pageNum?: number; pageSize?: number }) {
  const page = await http.get<ApiPagePayload<CircleFeedCard>>('/api/feed/public/list', params, {
    showLoading: false
  })
  return {
    ...page,
    list: (page.list || []).map((item) => normalizeFeed(item))
  }
}

export async function getFeedDetail(feedId: number) {
  const feed = await http.get<CircleFeedCard>(`/api/feed/${feedId}`, undefined, {
    showLoading: false
  })
  return normalizeFeed(feed)
}

export async function getFeedBySource(sourceType: string, sourceId: number) {
  const feed = await http.get<CircleFeedCard | null>('/api/feed/source', { sourceType, sourceId }, {
    showLoading: false
  })
  return feed ? normalizeFeed(feed) : null
}

export async function publishFeed(payload: FeedCreatePayload) {
  const feed = await http.post<CircleFeedCard>('/api/feed', payload, {
    showLoading: false
  })
  return normalizeFeed(feed)
}

export function uploadFeedImage(filePath: string): Promise<CommonUploadPayload> {
  return uploadFile(filePath, '/api/feed/upload-image')
}

export async function getFeedComments(feedId: number, params?: { pageNum?: number; pageSize?: number }) {
  const page = await http.get<ApiPagePayload<FeedCommentCard>>(`/api/feed/${feedId}/comments`, params, {
    showLoading: false
  })
  return {
    ...page,
    list: (page.list || []).map((item) => normalizeComment(item))
  }
}

export function postFeedComment(feedId: number, data: FeedCommentCreatePayload) {
  return http.post<FeedCommentCard>(`/api/feed/${feedId}/comments`, data, {
    showLoading: false
  }).then((item) => normalizeComment(item))
}

export async function toggleFeedLike(feedId: number) {
  const feed = await http.post<CircleFeedCard>(`/api/feed/${feedId}/like`, undefined, {
    showLoading: false
  })
  return normalizeFeed(feed)
}

export function deleteFeed(feedId: number) {
  return http.delete(`/api/feed/${feedId}`, undefined, {
    showLoading: false
  })
}

export function deleteComment(commentId: number) {
  return http.delete(`/api/feed/comments/${commentId}`, undefined, {
    showLoading: false
  })
}
