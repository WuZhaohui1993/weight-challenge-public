import http from '@/utils/request'
import type { UploadConfigPayload } from '@/types/api'

export function getUploadConfig() {
  return http.get<UploadConfigPayload>('/api/common/upload-config', undefined, {
    showLoading: false,
    showError: false,
    skipAuth: true,
    skipAuthRefresh: true
  })
}
