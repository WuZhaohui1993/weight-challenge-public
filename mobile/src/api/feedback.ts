import http, { uploadFile } from '@/utils/request'
import type { ApiPagePayload, CommonUploadPayload, FeedbackCreatePayload, FeedbackItem } from '@/types/api'

function normalizeFeedback(item: Partial<FeedbackItem>): FeedbackItem {
  return {
    id: Number(item.id || 0),
    userId: Number(item.userId || 0),
    category: item.category || 'other',
    categoryLabel: item.categoryLabel || '其他',
    content: item.content || '',
    images: Array.isArray(item.images) ? item.images : [],
    contact: item.contact ?? null,
    sourcePage: item.sourcePage ?? null,
    environmentJson: item.environmentJson ?? null,
    status: item.status || 'pending',
    statusLabel: item.statusLabel || '待处理',
    priority: item.priority ?? null,
    replyContent: item.replyContent ?? null,
    replyBy: item.replyBy ?? null,
    replyTime: item.replyTime ?? null,
    createTime: item.createTime ?? null,
    updateTime: item.updateTime ?? null
  }
}

export function uploadFeedbackImage(filePath: string): Promise<CommonUploadPayload> {
  return uploadFile(filePath, '/api/feedback/upload-image')
}

export async function submitFeedback(payload: FeedbackCreatePayload) {
  const feedback = await http.post<FeedbackItem>('/api/feedback', payload, {
    showLoading: false
  })
  return normalizeFeedback(feedback)
}

export async function getMyFeedback(params?: { pageNum?: number; pageSize?: number }) {
  const page = await http.get<ApiPagePayload<FeedbackItem>>('/api/feedback/my', params, {
    showLoading: false
  })
  return {
    ...page,
    list: (page.list || []).map((item) => normalizeFeedback(item))
  }
}

export async function getFeedbackDetail(feedbackId: number) {
  const feedback = await http.get<FeedbackItem>(`/api/feedback/${feedbackId}`, undefined, {
    showLoading: false
  })
  return normalizeFeedback(feedback)
}
