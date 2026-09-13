import http from '@/utils/request'
import type { PublicUserProfile } from '@/types/api'

export function getPublicUserProfile(userId: number) {
  return http.get<PublicUserProfile>(`/api/users/${userId}/profile`, undefined, {
    showLoading: false
  })
}
