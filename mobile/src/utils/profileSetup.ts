import type { UserInfo } from '@/types/api'

type ProfileEditorMode = 'profile'

interface ProfileEditorIntent {
  mode: ProfileEditorMode
  source?: 'login-reminder' | 'manual'
}

interface ProfileReminderIntent {
  isNewUser?: boolean
}

const PROFILE_REMINDER_DISABLED_PREFIX = 'profileSetupReminderDisabled'
const PROFILE_EDITOR_INTENT_KEY = 'profileEditorIntent'
const PROFILE_REMINDER_INTENT_KEY = 'profileReminderIntent'

function buildStorageKey(prefix: string, userId?: number | null) {
  return `${prefix}:${userId || 0}`
}

function hasValue(value?: string | number | null) {
  return value !== null && value !== undefined && String(value).trim() !== ''
}

export function isWechatProfileReady(user?: UserInfo | null) {
  if (!user) {
    return false
  }

  return hasValue(user.avatar) && hasValue(user.nickname) && String(user.nickname).trim() !== '微信用户'
}

export function isBasicProfileReady(user?: UserInfo | null) {
  if (!user) {
    return false
  }

  return (
    hasValue(user.gender) &&
    user.gender !== '0' &&
    hasValue(user.birthday) &&
    user.height !== null &&
    user.height !== undefined &&
    user.currentWeight !== null &&
    user.currentWeight !== undefined &&
    user.targetWeight !== null &&
    user.targetWeight !== undefined &&
    hasValue(user.targetCompletionDate)
  )
}

export function listMissingProfileItems(user?: UserInfo | null) {
  if (!user) {
    return []
  }

  const missingItems: string[] = []

  if (!isWechatProfileReady(user)) {
    if (!hasValue(user.avatar)) {
      missingItems.push('微信头像')
    }
    if (!hasValue(user.nickname) || String(user.nickname).trim() === '微信用户') {
      missingItems.push('微信昵称')
    }
  }

  if (!hasValue(user.gender) || user.gender === '0') {
    missingItems.push('性别')
  }
  if (!hasValue(user.birthday)) {
    missingItems.push('生日')
  }
  if (user.height === null || user.height === undefined) {
    missingItems.push('身高')
  }
  if (user.currentWeight === null || user.currentWeight === undefined) {
    missingItems.push('当前体重')
  }
  if (user.targetWeight === null || user.targetWeight === undefined) {
    missingItems.push('目标体重')
  }
  if (!hasValue(user.targetCompletionDate)) {
    missingItems.push('目标完成日期')
  }

  return Array.from(new Set(missingItems))
}

export function isProfileReminderDisabled(userId?: number | null) {
  return uni.getStorageSync(buildStorageKey(PROFILE_REMINDER_DISABLED_PREFIX, userId)) === '1'
}

export function disableProfileReminder(userId?: number | null) {
  if (!userId) {
    return
  }
  uni.setStorageSync(buildStorageKey(PROFILE_REMINDER_DISABLED_PREFIX, userId), '1')
}

export function clearProfileReminderDisabled(userId?: number | null) {
  if (!userId) {
    return
  }
  uni.removeStorageSync(buildStorageKey(PROFILE_REMINDER_DISABLED_PREFIX, userId))
}

export function shouldPromptProfileSetup(user?: UserInfo | null, options?: { isNewUser?: boolean }) {
  if (!user?.userId) {
    return false
  }

  if (isProfileReminderDisabled(user.userId)) {
    return false
  }

  const missingItems = listMissingProfileItems(user)
  if (!missingItems.length) {
    return false
  }

  return true
}

export function queueProfileEditorIntent(mode: ProfileEditorMode = 'profile', source: ProfileEditorIntent['source'] = 'manual') {
  const payload: ProfileEditorIntent = { mode, source }
  uni.setStorageSync(PROFILE_EDITOR_INTENT_KEY, JSON.stringify(payload))
}

export function consumeProfileEditorIntent(): ProfileEditorIntent | null {
  const rawValue = uni.getStorageSync(PROFILE_EDITOR_INTENT_KEY)
  if (!rawValue) {
    return null
  }

  uni.removeStorageSync(PROFILE_EDITOR_INTENT_KEY)

  try {
    return JSON.parse(rawValue) as ProfileEditorIntent
  } catch (error) {
    console.warn('解析资料编辑触发器失败', error)
    return null
  }
}

export function queueProfileReminderIntent(payload: ProfileReminderIntent = {}) {
  uni.setStorageSync(PROFILE_REMINDER_INTENT_KEY, JSON.stringify(payload))
}

export function consumeProfileReminderIntent(): ProfileReminderIntent | null {
  const rawValue = uni.getStorageSync(PROFILE_REMINDER_INTENT_KEY)
  if (!rawValue) {
    return null
  }

  uni.removeStorageSync(PROFILE_REMINDER_INTENT_KEY)

  try {
    return JSON.parse(rawValue) as ProfileReminderIntent
  } catch (error) {
    console.warn('解析资料提醒触发器失败', error)
    return null
  }
}
