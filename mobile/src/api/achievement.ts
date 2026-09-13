import http from '@/utils/request'
import type { AchievementItem } from '@/types/api'

function normalizeAchievement(item: Partial<AchievementItem>): AchievementItem {
  return {
    id: Number(item.id || 0),
    badgeType: item.badgeType || '',
    badgeName: item.badgeName || '',
    badgeIcon: item.badgeIcon || '🏆',
    badgeDescription: item.badgeDescription || '',
    unlockedAt: item.unlockedAt || null,
    progress: item.progress === null || item.progress === undefined ? 0 : Number(item.progress),
    unlocked: Boolean(item.unlocked)
  }
}

export async function getAchievements() {
  const achievements = await http.get<AchievementItem[]>('/api/achievements', undefined, {
    showLoading: false
  })
  return (achievements || []).map((item) => normalizeAchievement(item))
}
