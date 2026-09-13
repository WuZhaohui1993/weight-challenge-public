import { getMyCircles } from '@/api/circle'
import { getHabitStats } from '@/api/habit'
import type { UserInfo } from '@/types/api'
import { buildAchievementBadges, getUnlockedAchievementKeys, type AchievementMetrics } from '@/utils/achievement'

export async function fetchAchievementMetrics(): Promise<AchievementMetrics> {
  const [habitStatsResult, myCirclesResult] = await Promise.allSettled([
    getHabitStats(),
    getMyCircles({ pageNum: 1, pageSize: 1 })
  ])

  return {
    activeHabits: habitStatsResult.status === 'fulfilled' ? Number(habitStatsResult.value.activeHabits || 0) : 0,
    joinedCircles: myCirclesResult.status === 'fulfilled' ? Number(myCirclesResult.value.total || 0) : 0
  }
}

export function captureAchievementState(userInfo?: UserInfo | null, metrics?: Partial<AchievementMetrics> | null) {
  return getUnlockedAchievementKeys(buildAchievementBadges(userInfo, metrics))
}

export async function notifyAchievementUnlocks(
  previousKeys: string[],
  userInfo?: UserInfo | null,
  metrics?: Partial<AchievementMetrics> | null
) {
  const previousSet = new Set(previousKeys)
  const unlockedItems = buildAchievementBadges(userInfo, metrics).filter(
    (item) => item.unlocked && !previousSet.has(item.key)
  )

  if (!unlockedItems.length) {
    return false
  }

  const title =
    unlockedItems.length > 1
      ? `解锁 ${unlockedItems.length} 项新成就`
      : `解锁成就 ${unlockedItems[0].icon} ${unlockedItems[0].name}`
  const content =
    unlockedItems.length > 1
      ? unlockedItems.map((item) => `${item.icon} ${item.name}`).join('、')
      : `${unlockedItems[0].desc}，当前进度已达成。`

  await new Promise<void>((resolve) => {
    setTimeout(() => {
      uni.showModal({
        title,
        content,
        showCancel: false,
        confirmText: '知道了',
        success: () => resolve(),
        fail: () => resolve()
      })
    }, 500)
  })

  return true
}
