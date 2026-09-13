import { resolveAssetUrl } from '@/utils/asset'

export function parseImageList(images) {
  if (!images) return []
  if (Array.isArray(images)) {
    return normalizeImageArray(images)
  }

  const value = String(images).trim()
  if (!value) return []

  try {
    const parsed = JSON.parse(value)
    if (Array.isArray(parsed)) {
      return normalizeImageArray(parsed)
    }
  } catch (error) {
    // Some older generated pages stored comma separated paths.
  }

  return normalizeImageArray(value.split(','))
}

export function resolveImageList(images) {
  return parseImageList(images).map(resolveAssetUrl).filter(Boolean)
}

export function hasImages(images) {
  return parseImageList(images).length > 0
}

function normalizeImageArray(images) {
  return images
    .map(item => String(item || '').trim())
    .filter(Boolean)
}
