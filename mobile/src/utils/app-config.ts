import { getUploadConfig } from '@/api/common'
import { getBaseUrl } from '@/utils/request'
import { applyUploadConfig, hasUploadConfigSnapshot } from '@/utils/upload-config'

const UPLOAD_CONFIG_BASE_URL_STORAGE_KEY = 'uploadConfigBaseUrl'

let uploadConfigPromise: Promise<void> | null = null

export async function syncUploadConfig(force = false) {
  const currentBaseUrl = getBaseUrl()
  const cachedBaseUrl = String(uni.getStorageSync(UPLOAD_CONFIG_BASE_URL_STORAGE_KEY) || '')

  if (!force && uploadConfigPromise) {
    return uploadConfigPromise
  }

  if (!force && cachedBaseUrl === currentBaseUrl && hasUploadConfigSnapshot()) {
    return
  }

  uploadConfigPromise = (async () => {
    try {
      const uploadConfig = await getUploadConfig()
      applyUploadConfig(uploadConfig)
      uni.setStorageSync(UPLOAD_CONFIG_BASE_URL_STORAGE_KEY, currentBaseUrl)
    } catch (error) {
      console.error('同步上传配置失败:', error)
    } finally {
      uploadConfigPromise = null
    }
  })()

  return uploadConfigPromise
}
