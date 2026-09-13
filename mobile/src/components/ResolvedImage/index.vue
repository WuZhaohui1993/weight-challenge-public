<template>
  <image
    v-if="resolvedSrc && previewable"
    :class="imageNodeClass"
    :style="inheritedStyle"
    :src="resolvedSrc"
    :mode="mode"
    @error="handleError"
    @load="emit('load', $event)"
    @tap.stop="handleTap"
  />
  <image
    v-else-if="resolvedSrc"
    :class="imageNodeClass"
    :style="inheritedStyle"
    :src="resolvedSrc"
    :mode="mode"
    @error="handleError"
    @load="emit('load', $event)"
    @tap="handleTap"
  />
  <view
    v-else-if="showFallback"
    :class="fallbackNodeClass"
    :style="inheritedStyle"
    @tap="handleTap"
  >
    <slot name="fallback">{{ fallbackText }}</slot>
  </view>
  <view
    v-if="previewOpen"
    class="resolved-image-preview-layer"
    @tap.stop="closePreview"
    @touchmove.stop.prevent
  >
    <view class="resolved-image-preview__top" @tap.stop>
      <text class="resolved-image-preview__counter">{{ previewCurrentIndex + 1 }} / {{ previewSources.length }}</text>
      <text class="resolved-image-preview__close" @tap.stop="closePreview">关闭</text>
    </view>
    <swiper
      v-if="previewSources.length"
      class="resolved-image-preview__swiper"
      :current="previewCurrentIndex"
      circular
      @change="handlePreviewChange"
      @tap.stop
    >
      <swiper-item v-for="(source, index) in previewSources" :key="`${source}-${index}`">
        <view class="resolved-image-preview__stage" @tap.stop="closePreview">
          <image class="resolved-image-preview__image" :src="source" mode="aspectFit" @tap.stop="closePreview" />
        </view>
      </swiper-item>
    </swiper>
  </view>
</template>

<script setup lang="ts">
import { computed, ref, useAttrs, watch } from 'vue'
import { getImmediateDisplayImage, resolveDisplayImage, shouldResolveImageAsTempFile } from '@/utils/image'

const props = withDefaults(defineProps<{
  src?: string | null
  mode?: string
  shape?: 'default' | 'avatar'
  imageClass?: string
  fallbackClass?: string
  fallbackText?: string
  previewList?: string[]
  previewIndex?: number
}>(), {
  src: '',
  mode: 'scaleToFill',
  shape: 'default',
  imageClass: '',
  fallbackClass: '',
  fallbackText: '',
  previewList: () => [],
  previewIndex: 0
})

const emit = defineEmits<{
  (event: 'load', payload: unknown): void
  (event: 'error', payload?: unknown): void
  (event: 'tap', payload: unknown): void
}>()

const attrs = useAttrs()
const resolvedSrc = ref('')
const previewOpen = ref(false)
const previewCurrentIndex = ref(0)
const previewSources = ref<string[]>([])
const showFallback = computed(() => Boolean(props.fallbackClass || props.fallbackText))
const inheritedClass = computed(() => attrs.class)
const inheritedStyle = computed(() => attrs.style as string | Record<string, string> | undefined)
const avatarClass = computed(() => (props.shape === 'avatar' ? 'resolved-image--avatar' : ''))
const previewableClass = computed(() => (previewable.value ? 'resolved-image--previewable' : ''))
const modeClass = computed(() => (
  props.mode === 'widthFix'
    ? 'resolved-image--width-fix'
    : props.mode === 'heightFix'
      ? 'resolved-image--height-fix'
      : 'resolved-image--fill'
))
const imageNodeClass = computed(() => [
  'resolved-image',
  avatarClass.value,
  modeClass.value,
  previewableClass.value,
  props.imageClass,
  inheritedClass.value
])
const fallbackNodeClass = computed(() => [
  'resolved-image',
  'resolved-image--fallback',
  avatarClass.value,
  modeClass.value,
  previewableClass.value,
  props.fallbackClass,
  inheritedClass.value
])
const rawPreviewList = computed(() => (props.previewList || [])
  .map((item) => `${item || ''}`.trim())
  .filter(Boolean))
const previewable = computed(() => rawPreviewList.value.length > 0 && Boolean(resolvedSrc.value))
let resolveVersion = 0
let previewVersion = 0

watch(
  () => props.src,
  async (nextSrc) => {
    try {
      const currentVersion = ++resolveVersion
      const immediateSrc = getImmediateDisplayImage(nextSrc)

      if (!immediateSrc) {
        resolvedSrc.value = ''
        return
      }

      if (!shouldResolveImageAsTempFile(nextSrc)) {
        resolvedSrc.value = immediateSrc
        return
      }

      resolvedSrc.value = ''
      const nextResolvedSrc = await resolveDisplayImage(nextSrc)
      if (currentVersion === resolveVersion) {
        resolvedSrc.value = nextResolvedSrc
      }
    } catch (error) {
      console.warn('图片解析失败:', error)
      resolvedSrc.value = ''
    }
  },
  { immediate: true }
)

function handleError(event: unknown) {
  resolvedSrc.value = ''
  emit('error', event)
}

function handleTap(event: unknown) {
  if (previewable.value) {
    stopEvent(event)
    openPreview()
    return
  }
  emit('tap', event)
}

function stopEvent(event: unknown) {
  const target = event as { stopPropagation?: () => void } | null
  if (target && typeof target.stopPropagation === 'function') {
    target.stopPropagation()
  }
}

function clampIndex(index: number, length: number) {
  if (length <= 0) {
    return 0
  }
  if (index < 0) {
    return 0
  }
  if (index >= length) {
    return length - 1
  }
  return index
}

function openPreview() {
  const sourceItems = rawPreviewList.value
  if (!sourceItems.length) {
    return
  }

  const sourceIndex = clampIndex(Number(props.previewIndex || 0), sourceItems.length)
  const immediatePairs = sourceItems
    .map((source, index) => ({ index, url: getImmediateDisplayImage(source) }))
    .filter((item) => Boolean(item.url))

  if (!immediatePairs.length) {
    return
  }

  previewSources.value = immediatePairs.map((item) => item.url)
  const immediateCurrentIndex = immediatePairs.findIndex((item) => item.index === sourceIndex)
  previewCurrentIndex.value = clampIndex(immediateCurrentIndex >= 0 ? immediateCurrentIndex : 0, previewSources.value.length)
  previewOpen.value = true

  const currentPreviewVersion = ++previewVersion
  Promise.all(sourceItems.map(async (source, index) => ({ index, url: await resolveDisplayImage(source) })))
    .then((resolvedPairs) => {
      if (currentPreviewVersion !== previewVersion || !previewOpen.value) {
        return
      }
      const nextPairs = resolvedPairs.filter((item) => Boolean(item.url))
      if (!nextPairs.length) {
        return
      }
      previewSources.value = nextPairs.map((item) => item.url)
      const resolvedCurrentIndex = nextPairs.findIndex((item) => item.index === sourceIndex)
      previewCurrentIndex.value = clampIndex(resolvedCurrentIndex >= 0 ? resolvedCurrentIndex : 0, previewSources.value.length)
    })
}

function closePreview() {
  previewVersion += 1
  previewOpen.value = false
  previewSources.value = []
  previewCurrentIndex.value = 0
}

function handlePreviewChange(event: unknown) {
  const nextIndex = Number((event as { detail?: { current?: number } })?.detail?.current || 0)
  previewCurrentIndex.value = clampIndex(nextIndex, previewSources.value.length)
}
</script>

<style scoped lang="scss">
.resolved-image {
  display: block;
  box-sizing: border-box;
  max-width: 100%;
  overflow: hidden;
}

.resolved-image--fill {
  width: 100%;
  height: 100%;
}

.resolved-image--width-fix {
  width: 100%;
  height: auto;
}

.resolved-image--height-fix {
  width: auto;
  height: 100%;
}

.resolved-image--fallback {
  display: flex;
  align-items: center;
  justify-content: center;
}

.resolved-image--avatar {
  overflow: hidden;
  border-radius: 50%;
}

.resolved-image--previewable {
  cursor: pointer;
}

.resolved-image-preview-layer {
  position: fixed;
  left: 0;
  right: 0;
  top: 0;
  bottom: 0;
  z-index: 9999;
  width: 100vw;
  height: 100vh;
  background: rgba(10, 14, 24, 0.94);
}

.resolved-image-preview__top {
  position: absolute;
  left: 0;
  right: 0;
  top: 0;
  z-index: 2;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: calc(36rpx + env(safe-area-inset-top)) 32rpx 18rpx;
  color: #fff;
}

.resolved-image-preview__counter,
.resolved-image-preview__close {
  font-size: 26rpx;
  line-height: 1;
}

.resolved-image-preview__close {
  padding: 14rpx 18rpx;
  border-radius: 999rpx;
  background: rgba(255, 255, 255, 0.14);
}

.resolved-image-preview__swiper,
.resolved-image-preview__stage {
  width: 100%;
  height: 100%;
}

.resolved-image-preview__stage {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 96rpx 24rpx 56rpx;
  box-sizing: border-box;
}

.resolved-image-preview__image {
  width: 100%;
  height: 100%;
}
</style>
