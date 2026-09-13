/**
 * 圈子信息缓存服务
 * 用于跨组件共享圈子基础信息（名称、图标等）
 */
import { getCircle } from '@/api/weight/circle'

const circleCache = new Map()
const pendingRequests = new Map()

export async function getCircleInfo(circleId) {
  if (!circleId) return null

  const id = String(circleId)

  if (circleCache.has(id)) {
    return circleCache.get(id)
  }

  if (pendingRequests.has(id)) {
    return pendingRequests.get(id)
  }

  const promise = getCircle(circleId)
    .then(response => {
      const circle = response.data
      if (circle) {
        const circleInfo = {
          id: circle.id,
          name: circle.name || '',
          icon: circle.icon || ''
        }
        circleCache.set(id, circleInfo)
        return circleInfo
      }
      return null
    })
    .catch(() => null)
    .finally(() => {
      pendingRequests.delete(id)
    })

  pendingRequests.set(id, promise)
  return promise
}

export async function preloadCircles(circleIds) {
  if (!circleIds || !circleIds.length) return

  const idsToLoad = circleIds
    .map(id => String(id))
    .filter(id => !circleCache.has(id) && !pendingRequests.has(id))

  const batchSize = 5
  for (let i = 0; i < idsToLoad.length; i += batchSize) {
    const batch = idsToLoad.slice(i, i + batchSize)
    await Promise.all(batch.map(id => getCircleInfo(id)))
  }
}

export function setCircleCache(circle) {
  if (circle && circle.id) {
    circleCache.set(String(circle.id), {
      id: circle.id,
      name: circle.name || '',
      icon: circle.icon || ''
    })
  }
}

export function setCircleCacheBatch(circles) {
  if (circles && circles.length) {
    circles.forEach(circle => setCircleCache(circle))
  }
}

export function getCircleFromCache(circleId) {
  if (!circleId) return null
  return circleCache.get(String(circleId)) || null
}

export function clearCircleCache(circleId) {
  if (circleId) {
    circleCache.delete(String(circleId))
  } else {
    circleCache.clear()
  }
}

export default {
  getCircleInfo,
  preloadCircles,
  setCircleCache,
  setCircleCacheBatch,
  getCircleFromCache,
  clearCircleCache
}
