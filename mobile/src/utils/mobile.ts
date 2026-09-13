function getSystemInfoSafe() {
  const info: {
    statusBarHeight?: number
    windowWidth?: number
    windowHeight?: number
  } = {}

  try {
    if (typeof uni.getWindowInfo === 'function') {
      const windowInfo = uni.getWindowInfo()
      info.statusBarHeight = windowInfo.statusBarHeight
      info.windowWidth = windowInfo.windowWidth
      info.windowHeight = windowInfo.windowHeight
    }
  } catch (error) {
    console.warn('获取窗口信息失败', error)
  }

  if (info.statusBarHeight || info.windowWidth || info.windowHeight) {
    return info
  }

  try {
    return uni.getSystemInfoSync()
  } catch (error) {
    console.warn('获取系统信息失败', error)
    return null
  }
}

function getMenuButtonInfoSafe() {
  // #ifdef MP-WEIXIN
  try {
    return uni.getMenuButtonBoundingClientRect()
  } catch (error) {
    console.warn('获取胶囊按钮信息失败', error)
  }
  // #endif

  return null
}

type WechatMiniProgramApi = {
  nextTick?: (callback: () => void) => void
}

export function runAfterMiniProgramRender(callback: () => void) {
  // #ifdef MP-WEIXIN
  const wxApi = typeof globalThis !== 'undefined'
    ? (globalThis as { wx?: WechatMiniProgramApi }).wx
    : undefined

  if (typeof wxApi?.nextTick === 'function') {
    wxApi.nextTick(() => {
      setTimeout(callback, 0)
    })
    return
  }
  // #endif

  setTimeout(callback, 0)
}

export function getSafeTop(defaultValue = 20) {
  const systemInfo = getSystemInfoSafe()
  return systemInfo?.statusBarHeight || defaultValue
}

export function getTopBarHeight(minBarHeight = 56, extraHeight = 0) {
  const safeTop = getSafeTop()
  return safeTop + minBarHeight + extraHeight
}

export function getHeaderPaddingTop(defaultValue = 20) {
  return getSafeTop(defaultValue)
}

export function getMenuButtonSafeRight(defaultValue = 12, extraGap = 12) {
  const systemInfo = getSystemInfoSafe()
  const menuButtonInfo = getMenuButtonInfoSafe()

  if (!systemInfo?.windowWidth || !menuButtonInfo?.left) {
    return defaultValue
  }

  return Math.max(defaultValue, systemInfo.windowWidth - menuButtonInfo.left + extraGap)
}

function getCurrentPage() {
  // #ifdef MP-WEIXIN
  const pages = getCurrentPages()
  return pages[pages.length - 1] as any
  // #endif

  return null
}

function getCurrentRoute() {
  return String(getCurrentPage()?.route || '')
}

function syncCustomTabBarData(data: Record<string, unknown>, expectedRoute?: string) {
  // #ifdef MP-WEIXIN
  runAfterMiniProgramRender(() => {
    const page = getCurrentPage()

    if (expectedRoute && getCurrentRoute() !== expectedRoute) {
      return
    }

    const pageTabBar = page?.getTabBar?.()
    const appTabBar = typeof getApp !== 'undefined' ? getApp().globalData?.tabBar : null
    const tabBars = [pageTabBar, appTabBar].filter((item, index, list) =>
      item?.setData && list.indexOf(item) === index
    )

    tabBars.forEach((tabBar) => {
      try {
        tabBar.setData(data)
      } catch (error) {
        console.warn('同步自定义 TabBar 失败', error)
      }
    })
  })
  // #endif
}

export function syncCustomTabBar(selected: number) {
  syncCustomTabBarData({ selected }, getCurrentRoute())
}

export function setCustomTabBarVisible(visible: boolean) {
  syncCustomTabBarData({ visible, actionSheetVisible: false })
}

export function navigateBackOr(url: string) {
  const pages = getCurrentPages()
  if (pages.length > 1) {
    uni.navigateBack()
    return
  }
  uni.reLaunch({ url })
}
