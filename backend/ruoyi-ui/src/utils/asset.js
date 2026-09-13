import { isHttp } from '@/utils/validate'

const PROFILE_PATH_SEGMENT = '/profile/'

function normalizePath(path) {
  return String(path || '').trim()
}

function getBaseApi() {
  return normalizePath(process.env.VUE_APP_BASE_API || '')
}

function isBaseApiPath(path) {
  const baseApi = getBaseApi()
  return Boolean(baseApi && (path === baseApi || path.startsWith(baseApi + '/')))
}

function withBaseApi(path) {
  const baseApi = getBaseApi()
  if (!baseApi || isBaseApiPath(path)) {
    return path
  }
  return baseApi + path
}

function stripToProfilePath(path) {
  const profileIndex = path.indexOf(PROFILE_PATH_SEGMENT)
  if (profileIndex < 0) {
    return path
  }
  return path.slice(profileIndex)
}

export function resolveAssetUrl(path) {
  const value = normalizePath(path)
  if (!value) {
    return ''
  }

  if (!isHttp(value)) {
    return withBaseApi(value)
  }

  const profilePath = stripToProfilePath(value)
  if (profilePath.startsWith(PROFILE_PATH_SEGMENT)) {
    return withBaseApi(profilePath)
  }

  return value
}

export function normalizeUploadedAsset(payload) {
  if (payload && payload.fileName) {
    return payload.fileName
  }
  if (payload && payload.imgUrl) {
    return normalizeStoredAsset(payload.imgUrl)
  }
  return normalizeStoredAsset(payload?.url || '')
}

export function normalizeStoredAsset(path) {
  const value = normalizePath(path)
  if (!value) {
    return ''
  }

  if (!isHttp(value)) {
    if (isBaseApiPath(value)) {
      return stripToProfilePath(value)
    }
    return value
  }

  return stripToProfilePath(value)
}
