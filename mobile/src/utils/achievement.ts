import type { AchievementItem, UserInfo } from '@/types/api'

export type AchievementMetrics = {
  activeHabits: number
  joinedCircles: number
}

export type AchievementBadgeItem = {
  key: string
  icon: string
  name: string
  desc: string
  unlocked: boolean
  progress: number
  progressText: string
}

export type AchievementPreviewItem = {
  key: string
  icon: string
  name: string
  unlocked: boolean
}

function clampProgress(value: number) {
  return Math.max(0, Math.min(100, Math.round(value)))
}

function normalizeMetrics(metrics?: Partial<AchievementMetrics> | null): AchievementMetrics {
  return {
    activeHabits: Number(metrics?.activeHabits || 0),
    joinedCircles: Number(metrics?.joinedCircles || 0)
  }
}

export function buildAchievementBadges(userInfo?: UserInfo | null, metrics?: Partial<AchievementMetrics> | null): AchievementBadgeItem[] {
  const points = Number(userInfo?.achievementPoints || 0)
  const streakDays = Number(userInfo?.streakDays || 0)
  const dailyWaterTarget = Number(userInfo?.dailyWaterTarget || 0)
  const hasTargetWeight = Number(userInfo?.targetWeight || 0) > 0
  const hasCurrentWeight = Number(userInfo?.currentWeight || 0) > 0
  const normalizedMetrics = normalizeMetrics(metrics)

  return [
    {
      key: 'first-record',
      icon: '🌟',
      name: '新手起步',
      desc: '完成首次真实记录',
      unlocked: points >= 1,
      progress: clampProgress(points >= 1 ? 100 : 0),
      progressText: points >= 1 ? '已完成首条记录' : '完成一次记录即可点亮'
    },
    {
      key: 'streak-week',
      icon: '🔥',
      name: '连续打卡',
      desc: '连续 7 天保持记录',
      unlocked: streakDays >= 7,
      progress: clampProgress((streakDays / 7) * 100),
      progressText: `${Math.min(streakDays, 7)}/7 天`
    },
    {
      key: 'water-plan',
      icon: '💧',
      name: '补水计划',
      desc: '设置每日饮水目标',
      unlocked: dailyWaterTarget > 0,
      progress: clampProgress(dailyWaterTarget > 0 ? 100 : 0),
      progressText: dailyWaterTarget > 0 ? `目标 ${dailyWaterTarget} 杯/天` : '先补充每日饮水目标'
    },
    {
      key: 'weight-goal',
      icon: '⚖️',
      name: '目标明确',
      desc: '补齐当前体重和目标体重',
      unlocked: hasTargetWeight && hasCurrentWeight,
      progress: clampProgress((Number(hasTargetWeight) + Number(hasCurrentWeight)) * 50),
      progressText: hasTargetWeight && hasCurrentWeight ? '体重基线已完整' : '还缺当前体重或目标体重'
    },
    {
      key: 'habit-builder',
      icon: '✅',
      name: '习惯养成',
      desc: '启用 3 个习惯',
      unlocked: normalizedMetrics.activeHabits >= 3,
      progress: clampProgress((normalizedMetrics.activeHabits / 3) * 100),
      progressText: `${Math.min(normalizedMetrics.activeHabits, 3)}/3 个习惯`
    },
    {
      key: 'circle-companion',
      icon: '⭕',
      name: '圈子同行',
      desc: '加入至少 1 个圈子',
      unlocked: normalizedMetrics.joinedCircles >= 1,
      progress: clampProgress(normalizedMetrics.joinedCircles >= 1 ? 100 : 0),
      progressText: normalizedMetrics.joinedCircles >= 1 ? `已加入 ${normalizedMetrics.joinedCircles} 个圈子` : '加入圈子后即可点亮'
    }
  ]
}

export function buildAchievementBadgesFromRecords(records?: AchievementItem[] | null): AchievementBadgeItem[] {
  return (records || []).map((item) => {
    const progress = clampProgress(Number(item.progress || 0))
    const unlocked = Boolean(item.unlocked)
    return {
      key: item.badgeType || `${item.id}`,
      icon: item.badgeIcon || '🏆',
      name: item.badgeName || '未命名成就',
      desc: item.badgeDescription || '后台配置成就',
      unlocked,
      progress,
      progressText: unlocked
        ? (item.unlockedAt ? `已于 ${item.unlockedAt} 解锁` : '已解锁')
        : `进度 ${progress}%`
    }
  })
}

export function countUnlockedAchievements(items: AchievementBadgeItem[]) {
  return items.filter((item) => item.unlocked).length
}

export function getUnlockedAchievementKeys(items: AchievementBadgeItem[]) {
  return items.filter((item) => item.unlocked).map((item) => item.key)
}

export function buildAchievementPreview(items: AchievementBadgeItem[], minCount = 3) {
  const sorted = [...items].sort((left, right) => {
    if (left.unlocked !== right.unlocked) {
      return Number(right.unlocked) - Number(left.unlocked)
    }
    if (left.progress !== right.progress) {
      return right.progress - left.progress
    }
    return left.name.localeCompare(right.name)
  })

  const unlocked = sorted.filter((item) => item.unlocked)
  const visible = unlocked.length ? unlocked : sorted.slice(0, minCount)

  return visible.map((item) => ({
    key: item.key,
    icon: item.icon,
    name: item.name,
    unlocked: item.unlocked
  }))
}
