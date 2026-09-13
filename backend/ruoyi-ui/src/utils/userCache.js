/**
 * 用户信息缓存服务
 * 用于跨组件共享用户基础信息（头像、昵称等）
 */
import { getUser } from '@/api/weight/user'

// 用户信息缓存（内存缓存）
const userCache = new Map()

// 正在请求中的用户 ID（避免重复请求）
const pendingRequests = new Map()

/**
 * 获取用户信息（优先从缓存获取）
 * @param {Number|String} userId 用户 ID
 * @returns {Promise<Object>} 用户信息 { userId, nickname, avatar }
 */
export async function getUserInfo(userId) {
    if (!userId) return null

    const id = String(userId)

    // 1. 优先从缓存获取
    if (userCache.has(id)) {
        return userCache.get(id)
    }

    // 2. 如果正在请求中，返回同一个 Promise（避免重复请求）
    if (pendingRequests.has(id)) {
        return pendingRequests.get(id)
    }

    // 3. 发起请求
    const promise = getUser(userId)
        .then(response => {
            const user = response.data
            if (user) {
                const userInfo = {
                    userId: user.userId,
                    nickname: user.nickname || '',
                    avatar: user.avatar || ''
                }
                userCache.set(id, userInfo)
                return userInfo
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

/**
 * 批量预加载用户信息
 * @param {Array} userIds 用户 ID 数组
 */
export async function preloadUsers(userIds) {
    if (!userIds || !userIds.length) return

    const idsToLoad = userIds
        .map(id => String(id))
        .filter(id => !userCache.has(id) && !pendingRequests.has(id))

    // 并发加载（限制并发数）
    const batchSize = 5
    for (let i = 0; i < idsToLoad.length; i += batchSize) {
        const batch = idsToLoad.slice(i, i + batchSize)
        await Promise.all(batch.map(id => getUserInfo(id)))
    }
}

/**
 * 设置用户信息到缓存（从列表数据中提取）
 * @param {Object} user 用户对象 { userId, nickname, avatar }
 */
export function setUserCache(user) {
    if (user && user.userId) {
        userCache.set(String(user.userId), {
            userId: user.userId,
            nickname: user.nickname || '',
            avatar: user.avatar || ''
        })
    }
}

/**
 * 批量设置用户信息到缓存
 * @param {Array} users 用户对象数组
 */
export function setUserCacheBatch(users) {
    if (users && users.length) {
        users.forEach(user => setUserCache(user))
    }
}

/**
 * 从缓存中获取用户信息（同步，不触发请求）
 * @param {Number|String} userId 用户 ID
 * @returns {Object|null} 用户信息
 */
export function getUserFromCache(userId) {
    if (!userId) return null
    return userCache.get(String(userId)) || null
}

/**
 * 清除用户缓存
 * @param {Number|String} userId 可选，指定用户 ID；不传则清除全部
 */
export function clearUserCache(userId) {
    if (userId) {
        userCache.delete(String(userId))
    } else {
        userCache.clear()
    }
}

export default {
    getUserInfo,
    preloadUsers,
    setUserCache,
    setUserCacheBatch,
    getUserFromCache,
    clearUserCache
}
