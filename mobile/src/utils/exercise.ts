const EXERCISE_DURATION_SUFFIX_PATTERN = /[\s,，、-]*(?:\d+(?:\.\d+)?|[一二两三四五六七八九十半]+)\s*(?:分钟|分|min(?:ute)?s?|m)\s*$/i

export function normalizeExerciseType(type: string) {
  const normalizedType = String(type || '').replace(/\s+/g, ' ').trim()
  if (!normalizedType) {
    return ''
  }

  const typeWithoutDuration = normalizedType
    .replace(EXERCISE_DURATION_SUFFIX_PATTERN, '')
    .replace(/[\s,，、-]+$/g, '')
    .trim()

  return typeWithoutDuration || normalizedType
}

export function normalizeExerciseTypeUsage(usageMap: Record<string, unknown>, limit = 50) {
  const normalizedUsage = Object.entries(usageMap).reduce<Record<string, number>>((acc, [type, count]) => {
    const normalizedType = normalizeExerciseType(type)
    const normalizedCount = Number(count || 0)
    if (normalizedType && Number.isFinite(normalizedCount) && normalizedCount > 0) {
      acc[normalizedType] = (acc[normalizedType] || 0) + Math.floor(normalizedCount)
    }
    return acc
  }, {})

  return Object.fromEntries(
    Object.entries(normalizedUsage)
      .sort((left, right) => right[1] - left[1])
      .slice(0, limit)
  )
}
