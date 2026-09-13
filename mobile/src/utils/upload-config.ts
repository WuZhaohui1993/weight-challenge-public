import { ref } from 'vue'
import type { UploadConfigPayload } from '@/types/api'

const UPLOAD_CONFIG_STORAGE_KEY = 'uploadConfig'
const DEFAULT_UPLOAD_LIMIT_MB = 10
const DEFAULT_UPLOAD_LIMIT_BYTES = DEFAULT_UPLOAD_LIMIT_MB * 1024 * 1024
const DEFAULT_UPLOAD_LIMIT_TEXT = `${DEFAULT_UPLOAD_LIMIT_MB}MB`

function normalizeLimitBytes(value: unknown) {
  const parsed = Number(value || 0)
  return Number.isFinite(parsed) && parsed > 0 ? Math.round(parsed) : 0
}

function normalizeLimitText(value: unknown) {
  const text = String(value || '').trim().toUpperCase()
  return text || ''
}

function readStoredUploadConfig() {
  const stored = uni.getStorageSync(UPLOAD_CONFIG_STORAGE_KEY)
  if (!stored || typeof stored !== 'object') {
    return null
  }

  const nextBytes = normalizeLimitBytes((stored as Partial<UploadConfigPayload>).maxFileSizeBytes)
  const nextText = normalizeLimitText((stored as Partial<UploadConfigPayload>).maxFileSizeText)
  if (!nextBytes && !nextText) {
    return null
  }

  return {
    maxFileSizeBytes: nextBytes || DEFAULT_UPLOAD_LIMIT_BYTES,
    maxFileSizeText: nextText || DEFAULT_UPLOAD_LIMIT_TEXT
  }
}

const storedUploadConfig = readStoredUploadConfig()
let hasUploadConfigSnapshotValue = Boolean(storedUploadConfig)

export const uploadSizeLimitBytes = ref(storedUploadConfig?.maxFileSizeBytes || DEFAULT_UPLOAD_LIMIT_BYTES)
export const uploadSizeLimitText = ref(storedUploadConfig?.maxFileSizeText || DEFAULT_UPLOAD_LIMIT_TEXT)

export function getUploadSizeLimitBytes() {
  return normalizeLimitBytes(uploadSizeLimitBytes.value) || DEFAULT_UPLOAD_LIMIT_BYTES
}

export function getUploadSizeLimitText() {
  return normalizeLimitText(uploadSizeLimitText.value) || DEFAULT_UPLOAD_LIMIT_TEXT
}

export function hasUploadConfigSnapshot() {
  return hasUploadConfigSnapshotValue
}

export function applyUploadConfig(config?: Partial<UploadConfigPayload> | null) {
  const nextBytes = normalizeLimitBytes(config?.maxFileSizeBytes) || DEFAULT_UPLOAD_LIMIT_BYTES
  const nextText = normalizeLimitText(config?.maxFileSizeText) || DEFAULT_UPLOAD_LIMIT_TEXT

  uploadSizeLimitBytes.value = nextBytes
  uploadSizeLimitText.value = nextText
  hasUploadConfigSnapshotValue = true

  uni.setStorageSync(UPLOAD_CONFIG_STORAGE_KEY, {
    maxFileSizeBytes: nextBytes,
    maxFileSizeText: nextText
  })
}

export function resetUploadConfig() {
  uploadSizeLimitBytes.value = DEFAULT_UPLOAD_LIMIT_BYTES
  uploadSizeLimitText.value = DEFAULT_UPLOAD_LIMIT_TEXT
  hasUploadConfigSnapshotValue = false
  uni.removeStorageSync(UPLOAD_CONFIG_STORAGE_KEY)
}
