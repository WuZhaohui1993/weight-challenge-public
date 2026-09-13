import { onShareAppMessage, onShow } from '@dcloudio/uni-app'

const DEFAULT_SHARE_TITLE = '星绾同行｜记录体重变化，和伙伴一起健康打卡'
const DEFAULT_SHARE_PATH = '/pages/index/index'

interface DefaultPageShareOptions {
  title?: string
  path?: string
  imageUrl?: string
}

interface PageSharePayload {
  title: string
  path: string
  imageUrl?: string
}

function normalizeSharePath(path?: string) {
  const value = String(path || DEFAULT_SHARE_PATH).trim()
  if (!value) {
    return DEFAULT_SHARE_PATH
  }
  return value.startsWith('/') ? value : `/${value}`
}

function buildDefaultPageSharePayload(options: DefaultPageShareOptions = {}): PageSharePayload {
  return {
    title: options.title || DEFAULT_SHARE_TITLE,
    path: normalizeSharePath(options.path),
    ...(options.imageUrl ? { imageUrl: options.imageUrl } : {})
  }
}

export function showDefaultShareMenu() {
  // #ifdef MP-WEIXIN
  uni.showShareMenu({
    menus: ['shareAppMessage']
  })
  // #endif
}

export function useDefaultPageShare(options: DefaultPageShareOptions = {}) {
  onShareAppMessage(() => buildDefaultPageSharePayload(options))

  onShow(() => {
    showDefaultShareMenu()
  })
}
