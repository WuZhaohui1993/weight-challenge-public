import type { Ref } from 'vue'
import { watch } from 'vue'
import { onHide, onUnload } from '@dcloudio/uni-app'
import { setCustomTabBarVisible } from '@/utils/mobile'

type BooleanRef = Readonly<Ref<boolean>>

export function createOnShowSuspendGuard() {
  let suspended = false

  return {
    isSuspended() {
      return suspended
    },
    suspend() {
      suspended = true
    },
    resume() {
      suspended = false
    },
    async runWhileSuspended<T>(task: () => Promise<T>) {
      suspended = true
      try {
        return await task()
      } finally {
        suspended = false
      }
    }
  }
}

export function useTabBarOverlayVisibility(overlayVisible: BooleanRef) {
  const syncTabBarVisibility = () => {
    setCustomTabBarVisible(!overlayVisible.value)
  }

  watch(overlayVisible, syncTabBarVisibility)

  onHide(() => {
    setCustomTabBarVisible(true)
  })

  onUnload(() => {
    setCustomTabBarVisible(true)
  })

  return syncTabBarVisibility
}
