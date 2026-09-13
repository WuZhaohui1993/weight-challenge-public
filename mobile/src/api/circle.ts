import http, { uploadFile } from '@/utils/request'
import type {
  CircleCreatePayload,
  ApiPagePayload,
  CircleCard,
  CircleCategoryOption,
  CommonUploadPayload,
  CircleDetailPayload,
  CircleFeedCard,
  CircleGoalCatalogPayload,
  CircleInvitePreview,
  CircleJoinRequestCard,
  CircleMemberItem,
  CircleMembershipState,
  CircleRankingItem,
  CircleTaskOverviewPayload,
  MyCircleCard
} from '@/types/api'

export function getCircleList(params?: {
  pageNum?: number
  pageSize?: number
  keyword?: string
  categoryId?: number | string
  type?: string
}) {
  return http.get<ApiPagePayload<CircleCard>>('/api/circle/list', params, { showLoading: false })
}

export function getCircleCategories() {
  return http.get<CircleCategoryOption[]>('/api/circle/categories', undefined, { showLoading: false })
}

export function getCircleGoalCatalog() {
  return http.get<CircleGoalCatalogPayload>('/api/circle/goal-templates', undefined, { showLoading: false })
}

export function getMyCircles(params?: { pageNum?: number; pageSize?: number }) {
  return http.get<ApiPagePayload<MyCircleCard>>('/api/circle/my', params, { showLoading: false })
}

export function getCircleDetail(circleId: number) {
  return http.get<CircleDetailPayload>(`/api/circle/${circleId}`, undefined, { showLoading: false })
}

export function createCircle(data: CircleCreatePayload) {
  return http.post<CircleDetailPayload>('/api/circle', data, { showLoading: false })
}

export function updateCircle(circleId: number, data: CircleCreatePayload) {
  return http.put<CircleDetailPayload>(`/api/circle/${circleId}`, data, { showLoading: false })
}

export function uploadCircleCoverImage(filePath: string): Promise<CommonUploadPayload> {
  return uploadFile(filePath, '/api/feed/upload-image')
}

export function createCircleInviteToken(circleId: number) {
  return http.post<CircleInvitePreview>(`/api/circle/${circleId}/invite-token`, undefined, { showLoading: false })
}

export function resolveCircleInvite(token: string) {
  return http.get<CircleInvitePreview>('/api/circle/invite/resolve', { token }, { showLoading: false })
}

export function acceptCircleInvite(token: string) {
  return http.post<CircleMembershipState>('/api/circle/invite/accept', { token }, { showLoading: false })
}

export function getCircleFeeds(circleId: number, params?: { pageNum?: number; pageSize?: number }) {
  return http.get<ApiPagePayload<CircleFeedCard>>(`/api/circle/${circleId}/feeds`, params, { showLoading: false })
}

export function getCircleMembers(circleId: number, params?: { pageNum?: number; pageSize?: number }) {
  return http.get<ApiPagePayload<CircleMemberItem>>(`/api/circle/${circleId}/members`, params, { showLoading: false })
}

export function getCircleRanking(circleId: number, params?: { pageNum?: number; pageSize?: number }) {
  return http.get<ApiPagePayload<CircleRankingItem>>(`/api/circle/${circleId}/ranking`, params, { showLoading: false })
}

export function getCircleTodayTasks(circleId: number) {
  return http.get<CircleTaskOverviewPayload>(`/api/circle/${circleId}/tasks/today`, undefined, { showLoading: false })
}

export function completeCircleTask(circleId: number, taskId: number) {
  return http.post<CircleTaskOverviewPayload>(`/api/circle/${circleId}/tasks/${taskId}/complete`, undefined, { showLoading: false })
}

export function removeCircleMember(circleId: number, memberUserId: number) {
  return http.delete(`/api/circle/${circleId}/members/${memberUserId}`, undefined, { showLoading: false })
}

export function joinCircle(circleId: number) {
  return http.post<CircleMembershipState>(`/api/circle/${circleId}/join`, undefined, { showLoading: false })
}

export function leaveCircle(circleId: number) {
  return http.post<CircleMembershipState>(`/api/circle/${circleId}/leave`, undefined, { showLoading: false })
}

export function getCircleJoinRequests(circleId: number) {
  return http.get<CircleJoinRequestCard[]>(`/api/circle/${circleId}/join-requests`, undefined, { showLoading: false })
}

export function approveCircleJoinRequest(circleId: number, requestId: number) {
  return http.post<CircleMembershipState>(`/api/circle/${circleId}/join-requests/${requestId}/approve`, undefined, { showLoading: false })
}

export function rejectCircleJoinRequest(circleId: number, requestId: number) {
  return http.post<CircleJoinRequestCard[]>(`/api/circle/${circleId}/join-requests/${requestId}/reject`, undefined, { showLoading: false })
}
