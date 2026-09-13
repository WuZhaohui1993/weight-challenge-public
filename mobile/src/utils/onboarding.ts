const ONBOARDING_GUIDE_SEEN_PREFIX = 'onboardingGuideSeen'
const ONBOARDING_GUIDE_DISABLED_PREFIX = 'onboardingGuideDisabled'
const ONBOARDING_GUIDE_STEP_PREFIX = 'onboardingGuideStep'
const ONBOARDING_GUIDE_VERSION_PREFIX = 'onboardingGuideVersion'
const ONBOARDING_GUIDE_VERSION = '2'

export type OnboardingGuideStep = 'profile' | 'record' | 'explore'

export const ONBOARDING_GUIDE_STEPS: OnboardingGuideStep[] = ['profile', 'record', 'explore']

function buildStorageKey(prefix: string, userId?: number | null) {
  return `${prefix}:${userId || 0}`
}

function ensureOnboardingGuideVersion(userId?: number | null) {
  if (!userId) {
    return
  }

  const versionKey = buildStorageKey(ONBOARDING_GUIDE_VERSION_PREFIX, userId)
  if (uni.getStorageSync(versionKey) === ONBOARDING_GUIDE_VERSION) {
    return
  }

  uni.setStorageSync(versionKey, ONBOARDING_GUIDE_VERSION)
  if (isOnboardingGuideDisabled(userId)) {
    return
  }

  uni.removeStorageSync(buildStorageKey(ONBOARDING_GUIDE_SEEN_PREFIX, userId))
  if (!uni.getStorageSync(buildStorageKey(ONBOARDING_GUIDE_STEP_PREFIX, userId))) {
    setOnboardingGuideStep(userId, 'profile')
  }
}

export function isOnboardingGuideSeen(userId?: number | null) {
  if (!userId) {
    return false
  }
  ensureOnboardingGuideVersion(userId)
  return uni.getStorageSync(buildStorageKey(ONBOARDING_GUIDE_SEEN_PREFIX, userId)) === '1'
}

export function markOnboardingGuideSeen(userId?: number | null) {
  if (!userId) {
    return
  }
  uni.setStorageSync(buildStorageKey(ONBOARDING_GUIDE_SEEN_PREFIX, userId), '1')
}

export function completeOnboardingGuide(userId?: number | null) {
  if (!userId) {
    return
  }
  markOnboardingGuideSeen(userId)
  uni.removeStorageSync(buildStorageKey(ONBOARDING_GUIDE_STEP_PREFIX, userId))
}

export function isOnboardingGuideDisabled(userId?: number | null) {
  if (!userId) {
    return false
  }
  return uni.getStorageSync(buildStorageKey(ONBOARDING_GUIDE_DISABLED_PREFIX, userId)) === '1'
}

export function disableOnboardingGuide(userId?: number | null) {
  if (!userId) {
    return
  }
  uni.setStorageSync(buildStorageKey(ONBOARDING_GUIDE_DISABLED_PREFIX, userId), '1')
  markOnboardingGuideSeen(userId)
}

export function getOnboardingGuideStep(userId?: number | null): OnboardingGuideStep {
  if (!userId) {
    return 'profile'
  }

  ensureOnboardingGuideVersion(userId)
  const storedStep = uni.getStorageSync(buildStorageKey(ONBOARDING_GUIDE_STEP_PREFIX, userId))
  return ONBOARDING_GUIDE_STEPS.includes(storedStep) ? storedStep : 'profile'
}

export function setOnboardingGuideStep(userId: number | null | undefined, step: OnboardingGuideStep) {
  if (!userId) {
    return
  }
  uni.setStorageSync(buildStorageKey(ONBOARDING_GUIDE_STEP_PREFIX, userId), step)
}

export function advanceOnboardingGuideStep(userId?: number | null) {
  if (!userId) {
    return null
  }

  const currentStep = getOnboardingGuideStep(userId)
  const nextStep = ONBOARDING_GUIDE_STEPS[ONBOARDING_GUIDE_STEPS.indexOf(currentStep) + 1]
  if (!nextStep) {
    completeOnboardingGuide(userId)
    return null
  }

  setOnboardingGuideStep(userId, nextStep)
  return nextStep
}

export function restartOnboardingGuide(userId?: number | null) {
  if (!userId) {
    return
  }
  uni.removeStorageSync(buildStorageKey(ONBOARDING_GUIDE_SEEN_PREFIX, userId))
  setOnboardingGuideStep(userId, 'profile')
}

export function shouldPromptOnboardingGuide(userId?: number | null) {
  if (!userId) {
    return false
  }
  return !isOnboardingGuideDisabled(userId) && !isOnboardingGuideSeen(userId)
}
