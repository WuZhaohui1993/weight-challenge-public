/**
 * 网络请求封装
 * 统一处理请求/响应拦截、Token 管理等
 */

import type { ApiEnvelope, CommonUploadPayload, TokenRefreshPayload, WxLoginPayload } from '@/types/api'
import { getUploadSizeLimitText, normalizeUploadErrorMessage } from '@/utils/upload'

const DEV_FALLBACK_BASE_URL = 'http://127.0.0.1:8080'
const CONFIGURED_BASE_URL = normalizeBaseUrl(import.meta.env.VITE_API_BASE_URL)
const DEFAULT_BASE_URL: string = CONFIGURED_BASE_URL || (import.meta.env.DEV ? DEV_FALLBACK_BASE_URL : '')
const API_BASE_URL_STORAGE_KEY = 'apiBaseUrl'
const ACCESS_TOKEN_STORAGE_KEY = 'token'
const REFRESH_TOKEN_STORAGE_KEY = 'refreshToken'
const ACCESS_EXPIRES_AT_STORAGE_KEY = 'accessTokenExpiresAt'
const REFRESH_EXPIRES_AT_STORAGE_KEY = 'refreshTokenExpiresAt'
export const AUTH_SESSION_CHANGED_EVENT = 'auth:session-changed'
export const AUTH_SESSION_CLEARED_EVENT = 'auth:session-cleared'
const LOGIN_PAGE_ROUTE = 'pages/login/index'
const LOGIN_PAGE_URL = `/${LOGIN_PAGE_ROUTE}`
const ACCESS_TOKEN_REFRESH_AHEAD_MS = 5 * 60 * 1000

const TAB_BAR_ROUTES = new Set([
    'pages/index/index',
    'pages/circle/index',
    'pages/analytics/index',
    'pages/profile/index'
])

interface RequestConfig {
    url: string
    method?: 'GET' | 'POST' | 'PUT' | 'DELETE'
    data?: any
    header?: Record<string, string>
    showLoading?: boolean
    showError?: boolean
    skipAuth?: boolean
    skipAuthRefresh?: boolean
    _retried?: boolean
}

let refreshTokenPromise: Promise<void> | null = null

const LOCAL_HOST_SET = new Set(['127.0.0.1', 'localhost', '::1'])
const PRIVATE_IP_REGEXP = /^(10\.\d{1,3}\.\d{1,3}\.\d{1,3}|192\.168\.\d{1,3}\.\d{1,3}|172\.(1[6-9]|2\d|3[0-1])\.\d{1,3}\.\d{1,3})$/

function sanitizeRequestData<T>(value: T): T {
    if (Array.isArray(value)) {
        return value.map((item) => sanitizeRequestData(item)) as T
    }

    if (value && Object.prototype.toString.call(value) === '[object Object]') {
        const sanitized: Record<string, unknown> = {}
        Object.entries(value as Record<string, unknown>).forEach(([key, item]) => {
            if (item !== undefined) {
                sanitized[key] = sanitizeRequestData(item)
            }
        })
        return sanitized as T
    }

    return value
}

function getToken(): string {
    return uni.getStorageSync(ACCESS_TOKEN_STORAGE_KEY) || ''
}

function getRefreshToken(): string {
    return uni.getStorageSync(REFRESH_TOKEN_STORAGE_KEY) || ''
}

function getAccessTokenExpiresAt(): number {
    return Number(uni.getStorageSync(ACCESS_EXPIRES_AT_STORAGE_KEY) || 0)
}

function getRefreshTokenExpiresAt(): number {
    return Number(uni.getStorageSync(REFRESH_EXPIRES_AT_STORAGE_KEY) || 0)
}

export function setToken(token: string): void {
    uni.setStorageSync(ACCESS_TOKEN_STORAGE_KEY, token)
}

export function setRefreshToken(token: string): void {
    uni.setStorageSync(REFRESH_TOKEN_STORAGE_KEY, token)
}

function setAccessTokenExpiresAt(expiresAt: number): void {
    uni.setStorageSync(ACCESS_EXPIRES_AT_STORAGE_KEY, expiresAt)
}

function setRefreshTokenExpiresAt(expiresAt: number): void {
    uni.setStorageSync(REFRESH_EXPIRES_AT_STORAGE_KEY, expiresAt)
}

function emitAuthSessionChanged(): void {
    uni.$emit(AUTH_SESSION_CHANGED_EVENT)
}

function emitAuthSessionCleared(): void {
    uni.$emit(AUTH_SESSION_CLEARED_EVENT)
}

export function setAuthSession(
    payload: Pick<WxLoginPayload, 'accessToken' | 'refreshToken' | 'accessExpiresAt' | 'refreshExpiresAt'>
): void {
    setToken(payload.accessToken)
    setRefreshToken(payload.refreshToken)
    setAccessTokenExpiresAt(payload.accessExpiresAt)
    setRefreshTokenExpiresAt(payload.refreshExpiresAt)
    emitAuthSessionChanged()
}

export function clearToken(): void {
    uni.removeStorageSync(ACCESS_TOKEN_STORAGE_KEY)
    uni.removeStorageSync(REFRESH_TOKEN_STORAGE_KEY)
    uni.removeStorageSync(ACCESS_EXPIRES_AT_STORAGE_KEY)
    uni.removeStorageSync(REFRESH_EXPIRES_AT_STORAGE_KEY)
    uni.removeStorageSync('userInfo')
    emitAuthSessionCleared()
}

export function hasStoredAuthSession(): boolean {
    return Boolean(getToken() || getRefreshToken())
}

export function isAuthSessionExpired(): boolean {
    const token = getToken()
    const refreshToken = getRefreshToken()
    const refreshExpiresAt = getRefreshTokenExpiresAt()

    if (refreshToken && refreshExpiresAt) {
        return Date.now() >= refreshExpiresAt
    }

    if (!refreshToken && token) {
        const accessExpiresAt = getAccessTokenExpiresAt()
        return Boolean(accessExpiresAt && Date.now() >= accessExpiresAt)
    }

    return false
}

export function ensureStoredAuthSessionActive(): boolean {
    if (!hasStoredAuthSession()) {
        return false
    }

    if (isAuthSessionExpired()) {
        clearToken()
        return false
    }

    return true
}

function createUploadRequestError(message: string, extras?: Record<string, unknown>) {
    return Object.assign(new Error(message), extras || {})
}

export async function uploadFile(filePath: string, url = '/common/upload'): Promise<CommonUploadPayload> {
    await ensureValidAccessToken({ url, method: 'POST' })

    const token = getToken()
    const resolvedBaseUrl = getBaseUrl()
    ensureBaseUrlConfigured(resolvedBaseUrl)

    return new Promise((resolve, reject) => {
        uni.uploadFile({
            url: `${resolvedBaseUrl}${url}`,
            filePath,
            name: 'file',
            header: token ? { Authorization: `Bearer ${token}` } : {},
            success: (res) => {
                const rawResponseText = typeof res.data === 'string' ? res.data : ''
                if (res.statusCode === 413) {
                    reject(createUploadRequestError(
                        normalizeUploadErrorMessage(rawResponseText, `上传文件不能超过 ${getUploadSizeLimitText()}`),
                        { statusCode: res.statusCode }
                    ))
                    return
                }

                let response: CommonUploadPayload | null = null
                try {
                    response = JSON.parse(res.data || '{}') as CommonUploadPayload
                } catch (error) {
                    if (res.statusCode >= 400) {
                        reject(createUploadRequestError(
                            normalizeUploadErrorMessage(rawResponseText, '上传失败'),
                            { statusCode: res.statusCode }
                        ))
                        return
                    }
                    reject(createUploadRequestError('上传响应解析失败', { statusCode: res.statusCode, cause: error }))
                    return
                }

                if (res.statusCode === 401 || response?.code === 401) {
                    try {
                        handleUnauthorized(response?.msg || '登录已过期')
                    } catch (error) {
                        reject(Object.assign(
                            error instanceof Error ? error : new Error(response?.msg || '登录已过期'),
                            { code: 401, statusCode: res.statusCode || 401 }
                        ))
                    }
                    return
                }

                if (!response || response.code !== 200 || !response.url) {
                    reject(createUploadRequestError(
                        normalizeUploadErrorMessage(response?.msg, '上传失败'),
                        { code: response?.code, statusCode: res.statusCode }
                    ))
                    return
                }

                resolve(response)
            },
            fail: (error) => {
                reject(createUploadRequestError(
                    normalizeUploadErrorMessage(error?.errMsg, '上传失败'),
                    { cause: error }
                ))
            }
        })
    })
}

function extractHost(baseUrl: string) {
    const normalized = normalizeBaseUrl(baseUrl)
    const matched = normalized.match(/^https?:\/\/([^/:?#]+)/i)
    return matched?.[1]?.toLowerCase() || ''
}

export function isLoopbackBaseUrl(baseUrl: string) {
    return LOCAL_HOST_SET.has(extractHost(baseUrl))
}

export function isLocalNetworkBaseUrl(baseUrl: string) {
    return PRIVATE_IP_REGEXP.test(extractHost(baseUrl))
}

function isLikelyInsecureBaseUrl(baseUrl: string) {
    return /^http:\/\//i.test(normalizeBaseUrl(baseUrl))
}

function isMpWeixinRuntime() {
    return typeof globalThis !== 'undefined' && typeof (globalThis as { wx?: unknown }).wx !== 'undefined'
}

export function getDefaultBaseUrl(): string {
    return DEFAULT_BASE_URL
}

function normalizeBaseUrl(value?: string): string {
    if (!value) {
        return ''
    }

    const trimmed = String(value).trim()
    if (!trimmed) {
        return ''
    }

    return trimmed.replace(/\/+$/, '')
}

export function getBaseUrl(): string {
    const storedBaseUrl = uni.getStorageSync(API_BASE_URL_STORAGE_KEY)
    if (import.meta.env.DEV && !CONFIGURED_BASE_URL && storedBaseUrl) {
        return normalizeBaseUrl(storedBaseUrl)
    }
    return DEFAULT_BASE_URL
}

export function setBaseUrl(baseUrl: string): void {
    uni.setStorageSync(API_BASE_URL_STORAGE_KEY, normalizeBaseUrl(baseUrl))
}

export function resetBaseUrl(): void {
    uni.removeStorageSync(API_BASE_URL_STORAGE_KEY)
}

export function getBaseUrlWarning(baseUrl: string): string {
    const normalized = normalizeBaseUrl(baseUrl)
    if (!normalized) {
        return '正式构建未配置接口域名，请设置 VITE_API_BASE_URL 为已备案并加入微信合法域名的 HTTPS 地址'
    }

    if (isLoopbackBaseUrl(normalized)) {
        return '当前接口仍指向开发机本地地址，真机预览请改成同一 Wi-Fi 下电脑可访问的局域网 IP'
    }

    if (isMpWeixinRuntime() && isLocalNetworkBaseUrl(normalized)) {
        return '局域网联调可直接使用 http://局域网IP:8080；手机和电脑必须连接同一 Wi-Fi，正式环境仍需 HTTPS 域名'
    }

    if (isMpWeixinRuntime() && isLikelyInsecureBaseUrl(normalized)) {
        return '微信真机预览要求 HTTPS，并且域名必须加入 request 合法域名'
    }

    if (isMpWeixinRuntime()) {
        return '请确认当前接口域名已配置到微信小程序 request 合法域名'
    }

    return ''
}

function getMissingBaseUrlMessage() {
    return '未配置接口域名，请在正式构建前设置 VITE_API_BASE_URL'
}

function ensureBaseUrlConfigured(baseUrl: string) {
    if (!baseUrl) {
        throw new Error(getMissingBaseUrlMessage())
    }
}

function getNetworkFailureMessage(err: any, baseUrl: string) {
    const rawMessage = typeof err?.errMsg === 'string' ? err.errMsg : ''

    if (/url not in domain list/i.test(rawMessage)) {
        return '当前接口未配置到微信 request 合法域名'
    }

    if (isMpWeixinRuntime() && isLoopbackBaseUrl(baseUrl)) {
        return '当前接口仍指向开发机本地地址，请改成同一 Wi-Fi 下电脑可访问的局域网 IP'
    }

    if (isMpWeixinRuntime() && isLocalNetworkBaseUrl(baseUrl)) {
        return '局域网联调失败，请确认手机和电脑连接同一 Wi-Fi，并使用开发机的局域网地址'
    }

    if (isMpWeixinRuntime() && isLikelyInsecureBaseUrl(baseUrl)) {
        return '微信真机预览需要 HTTPS 接口'
    }

    return '网络请求失败'
}

function getCurrentPageRoute() {
    const pages = getCurrentPages()
    const currentPage = pages[pages.length - 1] as { route?: string } | undefined
    return currentPage?.route || ''
}

function buildQueryString(query?: Record<string, unknown>) {
    if (!query) {
        return ''
    }

    return Object.entries(query)
        .filter(([, value]) => value !== undefined && value !== null && String(value) !== '')
        .map(([key, value]) => `${encodeURIComponent(key)}=${encodeURIComponent(String(value))}`)
        .join('&')
}

function getCurrentPageUrl() {
    const pages = getCurrentPages()
    const currentPage = pages[pages.length - 1] as { route?: string, options?: Record<string, unknown> } | undefined
    const route = currentPage?.route || ''
    if (!route || route === LOGIN_PAGE_ROUTE) {
        return ''
    }

    const queryString = buildQueryString(currentPage?.options)
    return `/${route}${queryString ? `?${queryString}` : ''}`
}

function reLaunchLoginPage(url = LOGIN_PAGE_URL) {
    uni.reLaunch({ url })
}

function buildLoginPageUrl(redirectUrl?: string) {
    if (!redirectUrl) {
        return LOGIN_PAGE_URL
    }
    return `${LOGIN_PAGE_URL}?redirect=${encodeURIComponent(redirectUrl)}`
}

export function openLoginPage(mode: 'safe' | 'reset' = 'safe', redirectUrl?: string) {
    const currentRoute = getCurrentPageRoute()
    if (mode !== 'reset' && currentRoute === LOGIN_PAGE_ROUTE) {
        return
    }

    const targetUrl = buildLoginPageUrl(redirectUrl)

    if (mode === 'reset') {
        reLaunchLoginPage(targetUrl)
        return
    }

    if (!currentRoute || TAB_BAR_ROUTES.has(currentRoute)) {
        uni.navigateTo({
            url: targetUrl,
            fail: () => reLaunchLoginPage()
        })
        return
    }

    uni.redirectTo({
        url: targetUrl,
        fail: () => reLaunchLoginPage()
    })
}

function handleUnauthorized(message = '登录已过期'): never {
    clearToken()
    openLoginPage('reset', getCurrentPageUrl())
    throw Object.assign(new Error(message), { code: 401 })
}

function shouldAttachToken(config: RequestConfig) {
    return !config.skipAuth
}

function shouldTryRefresh(config: RequestConfig) {
    return !config.skipAuth && !config.skipAuthRefresh
}

function isRefreshTokenAvailable() {
    const refreshToken = getRefreshToken()
    const refreshExpiresAt = getRefreshTokenExpiresAt()
    if (!refreshToken) {
        return false
    }
    if (!refreshExpiresAt) {
        return true
    }
    return Date.now() < refreshExpiresAt
}

function isAccessTokenExpiringSoon() {
    const accessExpiresAt = getAccessTokenExpiresAt()
    if (!accessExpiresAt) {
        return false
    }
    return Date.now() >= accessExpiresAt - ACCESS_TOKEN_REFRESH_AHEAD_MS
}

function requestRaw<T = any>(config: RequestConfig): Promise<T> {
    const {
        url,
        method = 'GET',
        data,
        header = {},
        showError = true
    } = config
    const requestData = sanitizeRequestData(data)
    const requestHeader: Record<string, string> = {
        'Content-Type': 'application/json',
        ...header
    }

    if (shouldAttachToken(config)) {
        const token = getToken()
        if (token) {
            requestHeader['Authorization'] = `Bearer ${token}`
        }
    }

    return new Promise((resolve, reject) => {
        const resolvedBaseUrl = getBaseUrl()
        if (!resolvedBaseUrl) {
            const errorMessage = getMissingBaseUrlMessage()
            if (showError) {
                uni.showToast({ title: errorMessage, icon: 'none' })
            }
            reject(new Error(errorMessage))
            return
        }

        uni.request({
            url: `${resolvedBaseUrl}${url}`,
            method,
            data: requestData,
            header: requestHeader,
            success: (res: any) => {
                const response = res.data as ApiEnvelope<T>

                if (res.statusCode === 401 || response?.code === 401) {
                    reject(Object.assign(new Error(response?.msg || '登录已过期'), { code: 401 }))
                    return
                }

                if (!response || typeof response.code !== 'number') {
                    if (showError) {
                        uni.showToast({ title: '服务器响应异常', icon: 'none' })
                    }
                    reject(new Error('服务器响应异常'))
                    return
                }

                if (response.code === 200) {
                    resolve(response.data)
                    return
                }

                if (showError) {
                    uni.showToast({ title: response.msg || '请求失败', icon: 'none' })
                }
                reject(new Error(response.msg))
            },
            fail: (err) => {
                const errorMessage = getNetworkFailureMessage(err, resolvedBaseUrl)
                if (showError) {
                    uni.showToast({ title: errorMessage, icon: 'none' })
                }
                reject(Object.assign(new Error(errorMessage), { cause: err }))
            }
        })
    })
}

async function refreshAccessToken() {
    if (refreshTokenPromise) {
        return refreshTokenPromise
    }

    if (!isRefreshTokenAvailable()) {
        handleUnauthorized('登录已过期')
    }

    refreshTokenPromise = (async () => {
        try {
            const response = await requestRaw<TokenRefreshPayload>({
                url: '/api/auth/refreshToken',
                method: 'POST',
                data: { refreshToken: getRefreshToken() },
                showError: false,
                skipAuth: true,
                skipAuthRefresh: true
            })

            setAuthSession({
                accessToken: response.accessToken,
                refreshToken: response.refreshToken,
                accessExpiresAt: response.accessExpiresAt,
                refreshExpiresAt: response.refreshExpiresAt
            })
        } catch (error) {
            clearToken()
            throw error
        } finally {
            refreshTokenPromise = null
        }
    })()

    return refreshTokenPromise
}

async function ensureValidAccessToken(config: RequestConfig) {
    if (!shouldTryRefresh(config)) {
        return
    }

    if (!getToken()) {
        if (isRefreshTokenAvailable()) {
            await refreshAccessToken()
        }
        return
    }

    if (isAccessTokenExpiringSoon()) {
        await refreshAccessToken()
    }
}

export async function request<T = any>(config: RequestConfig): Promise<T> {
    const {
        showLoading = true,
        _retried = false
    } = config

    if (showLoading) {
        uni.showLoading({ title: '加载中...', mask: true })
    }

    try {
        await ensureValidAccessToken(config)
        return await requestRaw<T>(config)
    } catch (error: any) {
        if (shouldTryRefresh(config) && error?.code === 401 && !_retried) {
            try {
                await refreshAccessToken()
                return await request<T>({ ...config, _retried: true, showLoading: false })
            } catch (refreshError: any) {
                handleUnauthorized(refreshError?.message || '登录已过期')
            }
        }

        if (shouldTryRefresh(config) && error?.code === 401) {
            handleUnauthorized(error?.message || '登录已过期')
        }
        throw error
    } finally {
        if (showLoading) {
            uni.hideLoading()
        }
    }
}

export const http = {
    get<T = any>(url: string, data?: any, config?: Partial<RequestConfig>) {
        return request<T>({ url, method: 'GET', data, ...config })
    },
    post<T = any>(url: string, data?: any, config?: Partial<RequestConfig>) {
        return request<T>({ url, method: 'POST', data, ...config })
    },
    put<T = any>(url: string, data?: any, config?: Partial<RequestConfig>) {
        return request<T>({ url, method: 'PUT', data, ...config })
    },
    delete<T = any>(url: string, data?: any, config?: Partial<RequestConfig>) {
        return request<T>({ url, method: 'DELETE', data, ...config })
    }
}

export default http
