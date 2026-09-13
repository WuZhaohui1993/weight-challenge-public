export { applyUploadConfig, getUploadSizeLimitBytes, getUploadSizeLimitText, resetUploadConfig, uploadSizeLimitText } from '@/utils/upload-config'

import { getUploadSizeLimitBytes, getUploadSizeLimitText } from '@/utils/upload-config'

const PROFILE_PATH_SEGMENT = '/profile/'

interface ChooseImageTempFile {
  path?: string
  size?: number
}

interface UploadResultPayload {
  url?: string | null
  fileName?: string | null
}

export interface UploadedImageAsset {
  id: string
  localPath: string
  url: string
  uploading: boolean
  errorMessage?: string
}

interface UploadSelectedImagesOptions<T extends UploadResultPayload> {
  filePaths: string[]
  upload: (filePath: string) => Promise<T>
  fallbackErrorMessage?: string
  assets?: UploadedImageAsset[]
  onAssetUpdated?: (asset: UploadedImageAsset) => void
}

interface ChooseAndUploadImagesOptions<T extends UploadResultPayload> {
  currentCount: number
  maxCount?: number
  upload: (filePath: string) => Promise<T>
  fallbackErrorMessage?: string
  setUploading?: (uploading: boolean) => void
  onLocalAssetsSelected?: (assets: UploadedImageAsset[]) => void
  onAssetUpdated?: (asset: UploadedImageAsset) => void
}

interface RetryUploadedImageAssetOptions<T extends UploadResultPayload> {
  asset: UploadedImageAsset
  upload: (filePath: string) => Promise<T>
  fallbackErrorMessage?: string
  setUploading?: (uploading: boolean) => void
  onAssetUpdated?: (asset: UploadedImageAsset) => void
}

interface UploadSingleLocalImageOptions<T extends UploadResultPayload> {
  filePath: string
  upload: (filePath: string) => Promise<T>
  fallbackErrorMessage?: string
  oversizeMessage?: string
}

interface ChooseLocalImagesOptions {
  currentCount?: number
  maxCount?: number
  sourceType?: Array<'album' | 'camera'>
  sizeType?: Array<'original' | 'compressed'>
}

function normalizeFileSize(value: unknown) {
  const size = Number(value || 0)
  return Number.isFinite(size) && size > 0 ? size : 0
}

function createUploadAssetId(filePath: string, index: number) {
  const safePath = String(filePath || '').slice(-32).replace(/[^a-zA-Z0-9]+/g, '')
  return `upload-${Date.now()}-${index}-${safePath || 'image'}`
}

function createPendingUploadedImageAssets(filePaths: string[]) {
  return filePaths.map((filePath, index) => ({
    id: createUploadAssetId(filePath, index),
    localPath: filePath,
    url: '',
    uploading: true,
    errorMessage: ''
  }))
}

function createUploadError(error: any, fallback = '上传失败') {
  const nextError = new Error(error?.message || fallback)

  if (typeof error?.code !== 'undefined') {
    Object.assign(nextError, { code: error.code })
  }
  if (typeof error?.statusCode !== 'undefined') {
    Object.assign(nextError, { statusCode: error.statusCode })
  }
  if (typeof error?.cause !== 'undefined') {
    Object.assign(nextError, { cause: error.cause })
  }

  return nextError
}

export function normalizeUploadErrorMessage(rawMessage?: string | null, fallback = '上传失败') {
  const message = String(rawMessage || '').replace(/<br\s*\/?>/gi, '，').replace(/\s+/g, ' ').trim()
  if (!message) {
    return fallback
  }

  const normalized = message.toLowerCase()
  if (
    normalized.includes('maximum upload size exceeded')
    || normalized.includes('maxuploadsizeexceededexception')
    || normalized.includes('sizelimitexceededexception')
    || normalized.includes('upload.exceed.maxsize')
    || normalized.includes('allowed file maximum size')
    || normalized.includes('允许的文件最大大小')
    || normalized.includes('文件大小超出')
    || normalized.includes('超过最大大小')
  ) {
    return `上传文件不能超过 ${getUploadSizeLimitText()}`
  }

  return message
}

export function getUploadFileSize(filePath: string): Promise<number | null> {
  return new Promise((resolve) => {
    const normalizedPath = String(filePath || '').trim()
    if (!normalizedPath) {
      resolve(null)
      return
    }
    uni.getFileInfo({
      filePath: normalizedPath,
      success: (res) => {
        const size = normalizeFileSize(res?.size)
        resolve(size > 0 ? size : null)
      },
      fail: () => {
        resolve(null)
      }
    })
  })
}

export async function isUploadFileOversize(filePath: string) {
  const size = await getUploadFileSize(filePath)
  return size != null && size > getUploadSizeLimitBytes()
}

export async function resolveChooseImageUpload(result: any) {
  const tempFiles = Array.isArray(result?.tempFiles)
    ? (result.tempFiles as ChooseImageTempFile[])
    : []
  const tempFilePaths = Array.isArray(result?.tempFilePaths)
    ? result.tempFilePaths.filter((item: unknown): item is string => typeof item === 'string' && !!item)
    : []

  const validPaths: string[] = []
  let oversizeCount = 0
  const sourceFiles = tempFiles.length
    ? tempFiles
    : tempFilePaths.map((path: string) => ({ path }))

  for (const [index, file] of sourceFiles.entries()) {
    const filePath = typeof file?.path === 'string' && file.path ? file.path : tempFilePaths[index]
    if (!filePath) {
      continue
    }

    let fileSize = normalizeFileSize(file?.size)
    if (!fileSize) {
      fileSize = normalizeFileSize(await getUploadFileSize(filePath))
    }

    if (fileSize > getUploadSizeLimitBytes()) {
      oversizeCount += 1
      continue
    }

    validPaths.push(filePath)
  }

  return {
    validPaths,
    oversizeCount,
    totalCount: sourceFiles.length
  }
}

export function getChooseImageOversizeMessage(oversizeCount: number, totalCount: number) {
  if (oversizeCount <= 0) {
    return ''
  }
  if (oversizeCount >= totalCount) {
    return `单张图片不能超过 ${getUploadSizeLimitText()}`
  }
  return `已跳过 ${oversizeCount} 张超过 ${getUploadSizeLimitText()} 的图片`
}

export function getUploadBatchResultMessage(successCount: number, failedCount: number, fallback = '图片上传失败') {
  if (failedCount <= 0) {
    return ''
  }
  if (successCount > 0) {
    return `成功${successCount}张，失败${failedCount}张`
  }
  return fallback
}

export function replaceUploadedImageAsset(assets: UploadedImageAsset[], nextAsset: UploadedImageAsset) {
  return assets.map((item) => (item.id === nextAsset.id ? nextAsset : item))
}

export function hasFailedImageAssets(assets: UploadedImageAsset[]) {
  return assets.some((item) => !item.uploading && (!item.url || !!item.errorMessage))
}

export function getUploadedImagePreviewSource(asset: UploadedImageAsset) {
  return asset.localPath || asset.url
}

function getUploadedImageUrl(payload: UploadResultPayload) {
  const normalized = normalizeUploadedImageValue(payload.fileName || payload.url)
  return normalized || String(payload.url || payload.fileName || '').trim()
}

function normalizeUploadedImageValue(rawUrl?: string | null) {
  const value = String(rawUrl || '').trim()
  if (!value) {
    return ''
  }
  if (value.startsWith(PROFILE_PATH_SEGMENT)) {
    return value
  }

  const profileIndex = value.indexOf(PROFILE_PATH_SEGMENT)
  if (profileIndex >= 0 && /^https?:\/\//i.test(value)) {
    return value.slice(profileIndex)
  }

  return value
}

export async function uploadSelectedImages<T extends UploadResultPayload>({
  filePaths,
  upload,
  fallbackErrorMessage = '图片上传失败',
  assets,
  onAssetUpdated
}: UploadSelectedImagesOptions<T>) {
  let successCount = 0
  let failedCount = 0
  let firstErrorMessage = ''
  let authInterrupted = false
  const uploadedUrls: string[] = []
  const uploadedAssets: UploadedImageAsset[] = []
  let interruptedIndex = -1

  for (const [index, filePath] of filePaths.entries()) {
    const currentAsset = assets?.[index]
    try {
      const payload = await upload(filePath)
      const uploadedUrl = getUploadedImageUrl(payload)
      if (!uploadedUrl) {
        throw new Error(fallbackErrorMessage)
      }
      const uploadedAsset: UploadedImageAsset = {
        id: currentAsset?.id || createUploadAssetId(filePath, index),
        localPath: filePath,
        url: uploadedUrl,
        uploading: false,
        errorMessage: ''
      }
      uploadedUrls.push(uploadedUrl)
      uploadedAssets.push(uploadedAsset)
      onAssetUpdated?.(uploadedAsset)
      successCount += 1
    } catch (error: any) {
      const errorMessage = error?.message || fallbackErrorMessage
      if (currentAsset) {
        onAssetUpdated?.({
          ...currentAsset,
          uploading: false,
          errorMessage
        })
      }
      if (error?.code === 401) {
        authInterrupted = true
        interruptedIndex = index
        break
      }
      failedCount += 1
      if (!firstErrorMessage) {
        firstErrorMessage = errorMessage
      }
    }
  }

  if (authInterrupted && assets?.length && interruptedIndex >= 0) {
    for (const asset of assets.slice(interruptedIndex + 1)) {
      onAssetUpdated?.({
        ...asset,
        uploading: false,
        errorMessage: '登录已过期'
      })
    }
  }

  return {
    uploadedUrls,
    uploadedAssets,
    authInterrupted,
    batchMessage: authInterrupted
      ? ''
      : getUploadBatchResultMessage(successCount, failedCount, firstErrorMessage || fallbackErrorMessage)
  }
}

export async function chooseAndUploadImages<T extends UploadResultPayload>({
  currentCount,
  maxCount = 9,
  upload,
  fallbackErrorMessage = '图片上传失败',
  setUploading,
  onLocalAssetsSelected,
  onAssetUpdated
}: ChooseAndUploadImagesOptions<T>) {
  if (currentCount >= maxCount) {
    return {
      uploadedUrls: [] as string[],
      uploadedAssets: [] as UploadedImageAsset[],
      authInterrupted: false,
      cancelled: false
    }
  }

  try {
    const chooseResult = await chooseLocalImages({ currentCount, maxCount })
    if (!chooseResult.filePaths.length) {
      return {
        uploadedUrls: [] as string[],
        uploadedAssets: [] as UploadedImageAsset[],
        authInterrupted: false,
        cancelled: chooseResult.cancelled
      }
    }

    const localAssets = createPendingUploadedImageAssets(chooseResult.filePaths)
    onLocalAssetsSelected?.(localAssets)

    setUploading?.(true)
    try {
      const uploadResult = await uploadSelectedImages({
        filePaths: chooseResult.filePaths,
        upload,
        fallbackErrorMessage,
        assets: localAssets,
        onAssetUpdated
      })

      if (!uploadResult.authInterrupted && uploadResult.batchMessage) {
        uni.showToast({ title: uploadResult.batchMessage, icon: 'none' })
      }

      return {
        uploadedUrls: uploadResult.uploadedUrls,
        uploadedAssets: uploadResult.uploadedAssets,
        authInterrupted: uploadResult.authInterrupted,
        cancelled: false
      }
    } finally {
      setUploading?.(false)
    }
  } catch (error: any) {
    if (String(error?.errMsg || '').includes('cancel')) {
      return {
        uploadedUrls: [] as string[],
        uploadedAssets: [] as UploadedImageAsset[],
        authInterrupted: false,
        cancelled: true
      }
    }
    throw error
  }
}

export async function retryUploadedImageAsset<T extends UploadResultPayload>({
  asset,
  upload,
  fallbackErrorMessage = '图片上传失败',
  setUploading,
  onAssetUpdated
}: RetryUploadedImageAssetOptions<T>) {
  const uploadingAsset: UploadedImageAsset = {
    ...asset,
    url: '',
    uploading: true,
    errorMessage: ''
  }

  onAssetUpdated?.(uploadingAsset)
  setUploading?.(true)
  try {
    const payload = await upload(asset.localPath)
    const uploadedUrl = getUploadedImageUrl(payload)
    if (!uploadedUrl) {
      throw new Error(fallbackErrorMessage)
    }

    const uploadedAsset: UploadedImageAsset = {
      ...uploadingAsset,
      url: uploadedUrl,
      uploading: false,
      errorMessage: ''
    }
    onAssetUpdated?.(uploadedAsset)
    return uploadedAsset
  } catch (error: any) {
    const failedAsset: UploadedImageAsset = {
      ...uploadingAsset,
      uploading: false,
      errorMessage: error?.message || fallbackErrorMessage
    }
    onAssetUpdated?.(failedAsset)
    throw error
  } finally {
    setUploading?.(false)
  }
}

export async function chooseLocalImages({
  currentCount = 0,
  maxCount = 9,
  sourceType = ['album', 'camera'],
  sizeType = ['compressed']
}: ChooseLocalImagesOptions = {}) {
  if (currentCount >= maxCount) {
    return {
      filePaths: [] as string[],
      cancelled: false
    }
  }

  try {
    const result = await uni.chooseImage({
      count: maxCount - currentCount,
      sizeType,
      sourceType
    })

    const { validPaths, oversizeCount, totalCount } = await resolveChooseImageUpload(result)
    if (oversizeCount > 0) {
      uni.showToast({
        title: getChooseImageOversizeMessage(oversizeCount, totalCount),
        icon: 'none'
      })
    }

    return {
      filePaths: validPaths,
      cancelled: false
    }
  } catch (error: any) {
    if (String(error?.errMsg || '').includes('cancel')) {
      return {
        filePaths: [] as string[],
        cancelled: true
      }
    }
    throw error
  }
}

export async function uploadSingleLocalImage<T extends UploadResultPayload>({
  filePath,
  upload,
  fallbackErrorMessage = '上传失败',
  oversizeMessage = `上传文件不能超过 ${getUploadSizeLimitText()}`
}: UploadSingleLocalImageOptions<T>) {
  if (await isUploadFileOversize(filePath)) {
    throw new Error(oversizeMessage)
  }

  try {
    return await upload(filePath)
  } catch (error: any) {
    throw createUploadError(error, fallbackErrorMessage)
  }
}
