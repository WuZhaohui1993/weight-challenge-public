import http from '@/utils/request'
import type { ApiPagePayload, HabitCheckinPayload, HabitItem, HabitStats, HabitUpsertPayload } from '@/types/api'

function normalizeImages(rawImages?: string[] | string | null) {
  if (Array.isArray(rawImages)) {
    return rawImages.map((item) => `${item || ''}`.trim()).filter(Boolean).slice(0, 9)
  }
  if (typeof rawImages !== 'string' || !rawImages.trim()) {
    return []
  }
  const text = rawImages.trim()
  try {
    const parsed = JSON.parse(text)
    return Array.isArray(parsed)
      ? parsed.map((item) => `${item || ''}`.trim()).filter(Boolean).slice(0, 9)
      : [text]
  } catch {
    return [text]
  }
}

function normalizeHabit(item: Partial<HabitItem>): HabitItem {
  return {
    id: Number(item.id || 0),
    name: item.name || '',
    icon: item.icon ?? '✅',
    period: item.period ?? '1',
    frequency: item.frequency ?? '0',
    reminderTime: item.reminderTime ?? null,
    reminderWeekday: item.reminderWeekday ?? null,
    active: Boolean(item.active),
    currentStreak: Number(item.currentStreak || 0),
    longestStreak: Number(item.longestStreak || 0),
    totalCheckins: Number(item.totalCheckins || 0),
    checkedToday: Boolean(item.checkedToday),
    checkedAt: item.checkedAt ?? null,
    checkinId: item.checkinId ?? null,
    images: normalizeImages(item.images)
  }
}

function normalizeHabitCheckin(item: Partial<HabitCheckinPayload>): HabitCheckinPayload {
  return {
    id: Number(item.id || 0),
    habitId: Number(item.habitId || 0),
    habitName: item.habitName ?? '习惯打卡',
    habitIcon: item.habitIcon ?? '✅',
    checkedAt: item.checkedAt ?? null,
    note: item.note ?? null,
    images: normalizeImages(item.images)
  }
}

export async function getHabitList() {
  const habits = await http.get<HabitItem[]>('/api/habits', undefined, {
    showLoading: false
  })
  return (habits || []).map((item) => normalizeHabit(item))
}

export async function getTodayHabits() {
  const habits = await http.get<HabitItem[]>('/api/habits/today', undefined, {
    showLoading: false
  })
  return (habits || []).map((item) => normalizeHabit(item))
}

export async function getHabitStats() {
  return await http.get<HabitStats>('/api/habits/stats', undefined, {
    showLoading: false
  })
}

export async function getHabitCheckinList(params?: {
  pageNum?: number
  pageSize?: number
  startDate?: string | null
  endDate?: string | null
}) {
  const page = await http.get<ApiPagePayload<HabitCheckinPayload>>('/api/habits/checkins', params, {
    showLoading: false
  })
  return {
    ...page,
    list: (page.list || []).map((item) => normalizeHabitCheckin(item))
  }
}

export async function createHabit(payload: HabitUpsertPayload) {
  const habit = await http.post<HabitItem>('/api/habits', payload, {
    showLoading: false
  })
  return normalizeHabit(habit)
}

export async function updateHabit(habitId: number, payload: Partial<HabitUpsertPayload>) {
  const habit = await http.put<HabitItem>(`/api/habits/${habitId}`, payload, {
    showLoading: false
  })
  return normalizeHabit(habit)
}

export async function checkinHabit(habitId: number, payload?: { images?: string[] }) {
  const habit = await http.post<HabitItem>(`/api/habits/${habitId}/checkin`, payload || undefined, {
    showLoading: false
  })
  return normalizeHabit(habit)
}

export async function undoHabitCheckin(habitId: number) {
  const habit = await http.delete<HabitItem>(`/api/habits/${habitId}/checkin`, undefined, {
    showLoading: false
  })
  return normalizeHabit(habit)
}

export async function deleteHabit(habitId: number) {
  await http.delete(`/api/habits/${habitId}`, undefined, {
    showLoading: false
  })
}
