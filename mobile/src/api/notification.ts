import http from '@/utils/request'
import type { ApiPagePayload, NotificationFilterType, NotificationItem } from '@/types/api'

function normalizeNotification(item: Partial<NotificationItem>): NotificationItem {
  return {
    id: Number(item.id || 0),
    type: item.type || 'system',
    fromUserId: item.fromUserId == null ? null : Number(item.fromUserId),
    fromUserNickname: item.fromUserNickname ?? null,
    fromUserAvatar: item.fromUserAvatar ?? null,
    targetType: item.targetType ?? null,
    targetId: item.targetId == null ? null : Number(item.targetId),
    feedId: item.feedId == null ? null : Number(item.feedId),
    commentId: item.commentId == null ? null : Number(item.commentId),
    content: item.content ?? null,
    preview: item.preview ?? null,
    read: Boolean(item.read),
    createdAt: item.createdAt ?? null
  }
}

export async function getNotifications(params?: {
  type?: NotificationFilterType
  pageNum?: number
  pageSize?: number
}) {
  const page = await http.get<ApiPagePayload<NotificationItem>>('/api/notifications', params, {
    showLoading: false
  })
  return {
    ...page,
    list: (page.list || []).map((item) => normalizeNotification(item))
  }
}

export function getUnreadNotificationCount() {
  return http.get<number>('/api/notifications/unread-count', undefined, {
    showLoading: false
  })
}

export function markNotificationRead(notificationId: number) {
  return http.put<NotificationItem>(`/api/notifications/${notificationId}/read`, undefined, {
    showLoading: false
  }).then((item) => normalizeNotification(item))
}

export function markAllNotificationsRead() {
  return http.put<number>('/api/notifications/read-all', undefined, {
    showLoading: false
  })
}
