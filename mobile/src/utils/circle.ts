import { parseApiDateTime } from './datetime'
import { getImmediateDisplayImage } from './image'

export const CIRCLE_COVER_PRESETS = [
  { value: 'preset:ocean', label: '海盐', emoji: '🌊' },
  { value: 'preset:sunrise', label: '晨光', emoji: '🌅' },
  { value: 'preset:forest', label: '森野', emoji: '🌿' },
  { value: 'preset:berry', label: '莓果', emoji: '🍓' },
  { value: 'preset:night', label: '夜跑', emoji: '🌙' },
  { value: 'preset:sand', label: '沙丘', emoji: '🏜️' }
] as const

export const DEFAULT_CIRCLE_COVER = CIRCLE_COVER_PRESETS[0].value
const PROFILE_PATH_SEGMENT = '/profile/'

export function circleTypeLabel(type?: string | null) {
  return type === '1' ? '私密圈子' : '公开圈子'
}

export function circleTypeIcon(type?: string | null) {
  return type === '1' ? '🔒' : '⭕'
}

export function formatCircleDuration(durationDays?: number | null) {
  if (!durationDays || durationDays <= 0) {
    return '长期挑战'
  }
  return `${durationDays} 天计划`
}

export function formatDepositText(amount?: number | null) {
  return '暂未开放'
}

export function circleRoleLabel(role?: string | null) {
  return role === '0' ? '管理员' : '成员'
}

export function circleDepositStatusLabel(status?: string | null) {
  return '暂未开放'
}

export function circleFeedTimeText(value?: string | null) {
  if (!value) {
    return '刚刚'
  }

  const parsedDate = parseApiDateTime(value)
  if (!parsedDate) {
    return value
  }

  const parsed = parsedDate.getTime()
  const diff = Date.now() - parsed
  if (diff < 60 * 60 * 1000) {
    return `${Math.max(1, Math.floor(diff / (60 * 1000)))} 分钟前`
  }
  if (diff < 24 * 60 * 60 * 1000) {
    return `${Math.max(1, Math.floor(diff / (60 * 60 * 1000)))} 小时前`
  }
  return value.slice(0, 10)
}

export function joinedAtText(joinedAt?: string | null) {
  if (!joinedAt) {
    return '最近加入'
  }
  return joinedAt.slice(0, 10)
}

export function initialText(name?: string | null, fallback = '友') {
  if (!name) {
    return fallback
  }
  return name.slice(0, 1)
}

export function isPresetCircleCover(coverUrl?: string | null) {
  return normalizeCircleCoverUrl(coverUrl).startsWith('preset:')
}

export function circleCoverClass(coverUrl?: string | null) {
  const normalized = normalizeCircleCoverUrl(coverUrl)
  if (!normalized.startsWith('preset:')) {
    return 'cover-custom'
  }
  const key = normalized.replace('preset:', '') || 'ocean'
  return `cover-${key}`
}

export function normalizeCircleCoverUrl(coverUrl?: string | null) {
  const value = (coverUrl || '').trim()
  if (!value) {
    return DEFAULT_CIRCLE_COVER
  }
  if (value.startsWith('preset:')) {
    return CIRCLE_COVER_PRESETS.some((item) => item.value === value) ? value : DEFAULT_CIRCLE_COVER
  }
  if (value.startsWith(PROFILE_PATH_SEGMENT)) {
    return value
  }
  if (/^https?:\/\//i.test(value) && !/localhost|127\.0\.0\.1/i.test(value)) {
    return value
  }
  const profileIndex = value.indexOf(PROFILE_PATH_SEGMENT)
  if (profileIndex >= 0) {
    return value.slice(profileIndex)
  }
  return DEFAULT_CIRCLE_COVER
}

export function isRemoteCircleCover(coverUrl?: string | null) {
  const normalized = normalizeCircleCoverUrl(coverUrl)
  return Boolean(normalized) && !normalized.startsWith('preset:')
}

export function resolveCircleShareImage(coverUrl?: string | null) {
  const normalized = normalizeCircleCoverUrl(coverUrl)
  return normalized.startsWith('preset:') ? '' : getImmediateDisplayImage(normalized)
}

export function circleLifecycleLabel(status?: string | null) {
  return status === 'expired' ? '已到期' : '进行中'
}

export function circleLifecycleHint(readOnly?: boolean | null, readOnlyReason?: string | null) {
  if (!readOnly) {
    return ''
  }
  return readOnlyReason || '当前圈子已到期，仅支持查看历史内容。'
}
