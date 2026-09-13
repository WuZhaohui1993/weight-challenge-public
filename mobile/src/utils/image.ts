import { getBaseUrl, isLocalNetworkBaseUrl } from '@/utils/request'

const ABSOLUTE_URL_REGEXP = /^(https?):\/\/([^/?#]+)([^?#]*)?(\?[^#]*)?(#.*)?$/i
const TEMP_FILE_PREFIXES = ['wxfile://', 'file://', 'data:', 'blob:', 'http://tmp/', 'https://tmp/', 'http://usr/', 'https://usr/']
const PROFILE_PATH_SEGMENT = '/profile/'

const resolvedImageCache = new Map<string, string>()
const pendingImageDownloads = new Map<string, Promise<string>>()

interface ParsedAbsoluteUrl {
  protocol: string
  host: string
  pathname: string
  search: string
  hash: string
}

function isMpWeixinRuntime() {
  return typeof globalThis !== 'undefined' && typeof (globalThis as { wx?: unknown }).wx !== 'undefined'
}

function parseAbsoluteUrl(url: string): ParsedAbsoluteUrl | null {
  const matched = String(url || '').trim().match(ABSOLUTE_URL_REGEXP)
  if (!matched) {
    return null
  }

  return {
    protocol: matched[1].toLowerCase(),
    host: matched[2].toLowerCase(),
    pathname: matched[3] || '/',
    search: matched[4] || '',
    hash: matched[5] || ''
  }
}

function isTempFilePath(url: string) {
  const normalizedUrl = String(url || '').toLowerCase()
  return TEMP_FILE_PREFIXES.some((prefix) => normalizedUrl.startsWith(prefix))
}

function isProfileAssetPath(pathname: string) {
  return pathname.startsWith('/profile/') || pathname.includes(PROFILE_PATH_SEGMENT)
}

function stripBaseUrl(pathname: string) {
  const profileIndex = pathname.indexOf(PROFILE_PATH_SEGMENT)
  if (profileIndex < 0) {
    return pathname
  }
  return pathname.slice(profileIndex)
}

function toEncodedUrl(url: string) {
  try {
    return encodeURI(url)
  } catch (error) {
    return url
  }
}

export function normalizeStoredImageValue(rawUrl?: string | null) {
  const value = String(rawUrl || '').trim()
  if (!value) {
    return ''
  }

  if (isTempFilePath(value)) {
    return ''
  }

  if (value.startsWith(PROFILE_PATH_SEGMENT)) {
    return value
  }

  const absolute = parseAbsoluteUrl(value)
  if (!absolute || !isProfileAssetPath(absolute.pathname)) {
    return value
  }

  return stripBaseUrl(absolute.pathname)
}

function normalizeBackendImageUrl(rawUrl?: string | null) {
  const rawValue = String(rawUrl || '').trim()
  if (!rawValue) {
    return ''
  }

  if (isTempFilePath(rawValue)) {
    return rawValue
  }

  const value = normalizeStoredImageValue(rawValue)
  if (!value) {
    return ''
  }

  if (value.startsWith('/profile/')) {
    return toEncodedUrl(`${getBaseUrl()}${value}`)
  }

  const absolute = parseAbsoluteUrl(value)
  if (!absolute) {
    return toEncodedUrl(value)
  }

  if (!isProfileAssetPath(absolute.pathname)) {
    return toEncodedUrl(value)
  }

  return toEncodedUrl(`${getBaseUrl()}${stripBaseUrl(absolute.pathname)}${absolute.search}${absolute.hash}`)
}

export function getImmediateDisplayImage(rawUrl?: string | null) {
  return normalizeBackendImageUrl(rawUrl)
}

export function shouldResolveImageAsTempFile(rawUrl?: string | null) {
  const normalizedUrl = normalizeBackendImageUrl(rawUrl)
  if (!normalizedUrl) {
    return false
  }

  return shouldDownloadAsTempFile(normalizedUrl)
}

function shouldDownloadAsTempFile(url: string) {
  if (!isMpWeixinRuntime()) {
    return false
  }

  if (isTempFilePath(url)) {
    return false
  }

  const target = parseAbsoluteUrl(url)
  if (!target) {
    return false
  }

  if (target.protocol === 'http') {
    return true
  }

  if (!isProfileAssetPath(target.pathname)) {
    return false
  }

  const baseUrl = getBaseUrl()
  if (!isLocalNetworkBaseUrl(baseUrl)) {
    return false
  }

  const base = parseAbsoluteUrl(baseUrl)
  if (!base) {
    return false
  }

  return base.host === target.host
}

function downloadToTempFile(url: string): Promise<string> {
  const cached = resolvedImageCache.get(url)
  if (cached) {
    return Promise.resolve(cached)
  }

  const pending = pendingImageDownloads.get(url)
  if (pending) {
    return pending
  }

  const task = new Promise<string>((resolve) => {
    uni.downloadFile({
      url,
      success: (res) => {
        if (res.statusCode >= 200 && res.statusCode < 300 && res.tempFilePath) {
          resolvedImageCache.set(url, res.tempFilePath)
          resolve(res.tempFilePath)
          return
        }
        resolve('')
      },
      fail: () => {
        resolve('')
      },
      complete: () => {
        pendingImageDownloads.delete(url)
      }
    })
  })

  pendingImageDownloads.set(url, task)
  return task
}

export async function resolveDisplayImage(rawUrl?: string | null) {
  const normalizedUrl = getImmediateDisplayImage(rawUrl)
  if (!normalizedUrl) {
    return ''
  }

  if (!shouldDownloadAsTempFile(normalizedUrl)) {
    return normalizedUrl
  }

  return downloadToTempFile(normalizedUrl)
}
