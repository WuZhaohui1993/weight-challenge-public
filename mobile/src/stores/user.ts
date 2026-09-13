import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import http, {
    AUTH_SESSION_CHANGED_EVENT,
    AUTH_SESSION_CLEARED_EVENT,
    clearToken,
    openLoginPage,
    setAuthSession
} from '@/utils/request'
import type { UserInfo, WxLoginPayload } from '@/types/api'

export const useUserStore = defineStore('user', () => {
    // 状态
    const token = ref<string>(uni.getStorageSync('token') || '')
    const refreshToken = ref<string>(uni.getStorageSync('refreshToken') || '')
    const userInfo = ref<UserInfo | null>(uni.getStorageSync('userInfo') || null)
    const isLoggedIn = computed(() => !!token.value || !!refreshToken.value)

    function syncAuthStateFromStorage() {
        token.value = uni.getStorageSync('token') || ''
        refreshToken.value = uni.getStorageSync('refreshToken') || ''
        userInfo.value = uni.getStorageSync('userInfo') || null
    }

    uni.$on(AUTH_SESSION_CHANGED_EVENT, syncAuthStateFromStorage)
    uni.$on(AUTH_SESSION_CLEARED_EVENT, syncAuthStateFromStorage)

    function persistUserInfo(data: UserInfo | null) {
        userInfo.value = data
        if (data) {
            uni.setStorageSync('userInfo', data)
        } else {
            uni.removeStorageSync('userInfo')
        }
    }

    /**
     * 微信登录
     */
    async function wxLogin() {
        try {
            // 获取微信登录 code
            const loginRes = await uni.login({ provider: 'weixin' })
            if (!loginRes.code) {
                throw new Error('微信登录失败')
            }

            // 调用后端登录接口
            const res = await http.post<WxLoginPayload>('/api/auth/wxLogin', {
                code: loginRes.code
            }, {
                skipAuth: true,
                skipAuthRefresh: true
            })

            // 保存 Token 和用户信息
            token.value = res.accessToken
            refreshToken.value = res.refreshToken
            setAuthSession(res)
            persistUserInfo(res.user)

            return res
        } catch (error) {
            console.error('登录失败:', error)
            throw error
        }
    }

    /**
     * 获取用户信息
     */
    async function fetchUserInfo() {
        if (!token.value) return null

        try {
            const res = await http.get<UserInfo>('/api/user/me', undefined, { showLoading: false })
            persistUserInfo(res)
            return res
        } catch (error) {
            console.error('获取用户信息失败:', error)
            return null
        }
    }

    /**
     * 更新用户信息
     */
    async function updateUserInfo(data: Partial<UserInfo>) {
        const res = await http.put<UserInfo>('/api/user/settings', data)
        persistUserInfo(res)
        return res
    }

    /**
     * 退出登录
     */
    function logout() {
        const currentRefreshToken = refreshToken.value
        token.value = ''
        refreshToken.value = ''
        persistUserInfo(null)
        if (currentRefreshToken) {
            void http.post('/api/auth/logout', { refreshToken: currentRefreshToken }, {
                showLoading: false,
                showError: false,
                skipAuth: true,
                skipAuthRefresh: true
            })
        }
        clearToken()
        openLoginPage('reset')
    }

    return {
        token,
        refreshToken,
        userInfo,
        isLoggedIn,
        wxLogin,
        fetchUserInfo,
        updateUserInfo,
        logout
    }
})
