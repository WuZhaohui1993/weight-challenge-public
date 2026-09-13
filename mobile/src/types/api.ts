export interface ApiEnvelope<T> {
  code: number
  msg: string
  data: T
}

export interface ApiPagePayload<T> {
  list: T[]
  total: number
  pageNum: number
  pageSize: number
}

export interface UserInfo {
  userId: number
  nickname: string
  avatar: string
  gender: string
  height: number | null
  birthday: string | null
  targetWeight: number | null
  targetCompletionDate: string | null
  currentWeight: number | null
  bmi: number | null
  dailyCalorieTarget: number | null
  dailyCalorieDeficitTarget: number | null
  dailyWaterTarget: number | null
  streakDays: number | null
  achievementPoints: number | null
  mainCircleId: number | null
  circleFeatureEnabled: number | null
  syncMode: string | null
  source: string | null
  phone: string | null
}

export interface WxLoginPayload {
  accessToken: string
  refreshToken: string
  accessExpiresIn: number
  refreshExpiresIn: number
  accessExpiresAt: number
  refreshExpiresAt: number
  isNewUser: boolean
  user: UserInfo
}

export interface TokenRefreshPayload {
  accessToken: string
  refreshToken: string
  accessExpiresIn: number
  refreshExpiresIn: number
  accessExpiresAt: number
  refreshExpiresAt: number
}

export interface CommonUploadPayload {
  code: number
  msg: string
  url: string
  fileName: string
  newFileName: string
  originalFilename: string
}

export interface UploadConfigPayload {
  maxFileSizeBytes: number
  maxFileSizeText: string
}

export interface AchievementItem {
  id: number
  badgeType: string
  badgeName: string
  badgeIcon: string | null
  badgeDescription: string | null
  unlockedAt: string | null
  progress: number | null
  unlocked: boolean
}

export type FeedbackCategory = 'bug' | 'suggestion' | 'experience' | 'account' | 'other'
export type FeedbackStatus = 'pending' | 'processing' | 'resolved' | 'closed'

export interface FeedbackItem {
  id: number
  userId: number
  category: FeedbackCategory
  categoryLabel: string
  content: string
  images: string[]
  contact: string | null
  sourcePage: string | null
  environmentJson: string | null
  status: FeedbackStatus
  statusLabel: string
  priority: string | null
  replyContent: string | null
  replyBy: string | null
  replyTime: string | null
  createTime: string | null
  updateTime: string | null
}

export interface FeedbackCreatePayload {
  category: FeedbackCategory
  content: string
  images?: string[]
  contact?: string | null
  sourcePage?: string | null
  environment?: Record<string, unknown>
}

export interface DashboardSummary {
  currentWeight: number | null
  bmi: number | null
  targetWeight: number | null
  targetCompletionDate: string | null
  calorieIntake: number
  calorieTarget: number
  dailyCalorieDeficitTarget: number | null
  calorieRemaining: number
  exerciseCalories: number
  exerciseMinutes: number
  waterCups: number
  waterTarget: number
}

export interface DashboardWeightTrendItem {
  date: string
  weight: number | null
}

export interface DashboardCircleMemberItem {
  userId: number
  nickname: string | null
  avatar: string | null
  role: string | null
  streakDays: number | null
  weightChange: number | null
}

export interface DashboardCircleRankingItem {
  rank: number
  userId: number
  nickname: string | null
  avatar: string | null
  weightChange: number | null
  completedTaskCount?: number | null
  circleCheckins?: number | null
}

export interface DashboardMainCircle {
  id: number
  name: string
  members: DashboardCircleMemberItem[]
  ranking: DashboardCircleRankingItem[]
}

export interface DashboardPayload {
  summary: DashboardSummary
  weightTrend: DashboardWeightTrendItem[]
  mainCircle: DashboardMainCircle | null
  unreadNotificationCount: number
}

export interface WeightRecordPayload {
  id: number
  userId: number
  weight: number | null
  bmi: number | null
  bodyFatRate: number | null
  remark: string | null
  recordedAt: string | null
  syncToCircles?: string | null
  images?: string[] | string | null
}

export interface RecordFeedDraftPayload {
  feedType: string
  content: string
  visibilityScope: FeedVisibilityScope
  originCircleId?: number | null
  syncCircleIds?: number[]
  images?: string[]
  sourceType?: string | null
  sourceId?: number | null
}

export interface RecordSubmitPayload<T> {
  record: T
  draft: RecordFeedDraftPayload | null
}

export interface FoodRecordPayload {
  id: number
  userId: number
  mealType: string | null
  foodName: string | null
  calories: number | null
  protein: number | null
  fat: number | null
  carbs: number | null
  imageUrl: string | null
  images?: string[] | string | null
  recordedAt: string | null
  syncToCircles: string | null
  remark: string | null
}

export interface ExerciseRecordPayload {
  id: number
  userId: number
  exerciseType: string | null
  durationMinutes: number | null
  caloriesBurned: number | null
  distance: number | null
  recordedAt: string | null
  syncToCircles: string | null
  remark: string | null
  images?: string[] | string | null
}

export interface WaterRecordPayload {
  id: number
  userId: number
  cups: number | null
  ml: number | null
  recordedAt: string | null
  images?: string[] | string | null
}

export interface CircleCard {
  id: number
  name: string
  categoryId: number | null
  icon: string | null
  coverUrl: string | null
  type: string
  description: string | null
  memberCount: number
  depositRequired: number | null
  durationDays: number | null
  startDate: string | null
  endDate: string | null
  categoryName: string | null
  status: string
  joined: boolean
  lifecycleStatus: 'active' | 'expired'
  readOnly: boolean
  readOnlyReason: string | null
}

export interface CircleCategoryOption {
  id: number
  code: string | null
  name: string
  icon: string | null
}

export interface MyCircleCard extends CircleCard {
  role: string | null
  joinedAt: string | null
  streakDays: number | null
  totalCheckins: number | null
  depositStatus: string | null
}

export type FeedVisibilityScope = 'public' | 'circle'

export interface CircleFeedCard {
  id: number
  circleId: number | null
  circleName: string | null
  originCircleName?: string | null
  userId: number
  userNickname: string | null
  userAvatar: string | null
  feedType: string | null
  content: string | null
  images: string[]
  likesCount: number | null
  commentsCount: number | null
  isFeatured?: boolean | null
  likedByMe?: boolean | null
  createdAt: string | null
  visibilityScope?: FeedVisibilityScope | null
  commentEnabled?: boolean | null
  originCircleId?: number | null
  syncCircleIds?: number[]
  ownedByMe?: boolean | null
  sourceType?: string | null
  sourceId?: number | null
}

export interface CirclePreviewMember {
  userId: number
  nickname: string | null
  avatar: string | null
  role: string | null
  streakDays: number | null
  totalCheckins: number | null
}

export interface CircleGoalItem {
  id: number
  goalType: string | null
  goalName: string | null
  description: string | null
  metricCode: string | null
  targetValue: number | null
  targetUnit: string | null
  period: string | null
  periodLabel: string | null
  verificationType: string | null
  verificationTypeLabel: string | null
  penaltyForFailure: string | null
  required: boolean
}

export interface CircleGoalTemplateItem {
  id: number
  goalType: string | null
  goalName: string | null
  description: string | null
  metricCode: string | null
  targetValue: number | null
  targetUnit: string | null
  period: string | null
  periodLabel: string | null
  verificationType: string | null
  verificationTypeLabel: string | null
  required: boolean
}

export interface CircleGoalMetricOption {
  value: string
  label: string
  goalType: string
  defaultUnit: string
  description: string
  verificationTypes: string[]
}

export interface CircleGoalConfigOption {
  value: string
  label: string
}

export interface CircleGoalCatalogPayload {
  templates: CircleGoalTemplateItem[]
  metricOptions: CircleGoalMetricOption[]
  periodOptions: CircleGoalConfigOption[]
  verificationOptions: CircleGoalConfigOption[]
}

export interface CircleTaskItem {
  id: number
  goalId: number | null
  goalName: string | null
  description: string | null
  metricCode: string | null
  targetValue: number | null
  currentValue: number | null
  targetUnit: string | null
  period: string | null
  periodLabel: string | null
  verificationType: string | null
  verificationTypeLabel: string | null
  completed: boolean
  manual: boolean
  readOnly: boolean
  readOnlyReason: string | null
  completedAt: string | null
  pendingPenalty: boolean
  penaltyRuleText: string | null
}

export interface CircleTaskSummary {
  totalCount: number
  completedCount: number
  remainingCount: number
  pendingPenaltyCount: number
  penaltyRuleText: string | null
  readOnly: boolean
  readOnlyReason: string | null
}

export interface CircleTaskOverviewPayload {
  tasks: CircleTaskItem[]
  summary: CircleTaskSummary | null
}

export interface CircleRankingItem {
  rankNum: number | null
  userId: number
  nickname: string | null
  avatar: string | null
  completedTaskCount: number | null
  circleCheckins: number | null
  streakDays: number | null
  joinedAt: string | null
  score: number | null
}

export interface CircleRankingSummary {
  periodLabel: string | null
  ruleText: string | null
  leader: CircleRankingItem | null
  updatedAt: string | null
}

export interface CircleRankingPreviewItem {
  rankNum: number | null
  userId: number
  nickname: string | null
  avatar: string | null
  score: number | null
}

export interface CircleRuleSummary {
  visibilityText: string | null
  joinRuleText: string | null
  durationText: string | null
  penaltyRuleText: string | null
  startDate: string | null
  endDate: string | null
}

export interface CircleDepositSummary {
  enabled: boolean
  amount: number | null
  currentUserStatus: string | null
  currentUserStatusLabel: string | null
  pendingCount: number | null
  paidCount: number | null
  refundedCount: number | null
  deductedCount: number | null
}

export interface CircleMemberItem {
  membershipId: number
  userId: number
  nickname: string | null
  avatar: string | null
  role: string | null
  joinedAt: string | null
  streakDays: number | null
  totalCheckins: number | null
  weightChange: number | null
  depositStatus: string | null
  removable: boolean
}

export interface CircleDetailPayload {
  circle: CircleCard
  joined: boolean
  memberRole: string | null
  previewMembers?: CirclePreviewMember[]
  recentFeeds?: CircleFeedCard[]
  pendingJoinRequestCount: number
  goalList: CircleGoalItem[]
  rankingPreview?: CircleRankingPreviewItem[]
  ruleSummary: CircleRuleSummary | null
  depositSummary: CircleDepositSummary | null
  todayTasks: CircleTaskItem[]
  taskSummary: CircleTaskSummary | null
  rankingSummary: CircleRankingSummary | null
}

export interface CircleMembershipState {
  circleId: number
  joined: boolean
  memberRole: string | null
  memberCount: number
  pendingApproval?: boolean | null
}

export interface CircleInvitePreview {
  token: string
  circleId: number
  circleName: string
  circleType: string
  approvalRequired: boolean
  expiresAt: number
  membershipStatus: 'none' | 'joined' | 'pending'
}

export interface CircleJoinRequestCard {
  id: number
  userId: number
  nickname: string | null
  avatar: string | null
  requestedAt: string | null
}

export interface CircleCreatePayload {
  name: string
  type: string
  categoryId: number
  description?: string
  coverUrl?: string | null
  durationDays?: number | null
  depositRequired?: number | null
  selectedGoalTemplateIds?: number[]
  customGoals?: CircleCreateGoalPayload[]
}

export interface CircleCreateGoalPayload {
  metricCode: string
  goalName: string
  targetValue: number
  targetUnit: string
  period: string
  verificationType: string
  isRequired: boolean
}

export interface CircleFeedCreatePayload {
  feedType: string
  content: string
  images?: string
  sourceRecordId?: number | null
  sourceRecordType?: string | null
}

export interface FeedCreatePayload {
  feedType: string
  content: string
  visibilityScope: FeedVisibilityScope
  originCircleId?: number | null
  syncCircleIds?: number[]
  images?: string[]
  sourceType?: string | null
  sourceId?: number | null
}

export interface FeedCommentCard {
  id: number
  feedId: number | null
  userId: number
  userNickname: string | null
  userAvatar: string | null
  content: string
  images: string[]
  replyToCommentId: number | null
  replyToUserId: number | null
  replyToUserNickname: string | null
  likesCount: number | null
  createdAt: string | null
  deleted?: boolean | null
  ownedByMe?: boolean | null
}

export interface FeedCommentCreatePayload {
  content: string
  images?: string[]
  replyToCommentId?: number | null
  replyToUserId?: number | null
}

export type NotificationFilterType = 'all' | 'like' | 'comment' | 'system'

export interface NotificationItem {
  id: number
  type: string
  fromUserId: number | null
  fromUserNickname: string | null
  fromUserAvatar: string | null
  targetType: string | null
  targetId: number | null
  feedId: number | null
  commentId: number | null
  content: string | null
  preview: string | null
  read: boolean
  createdAt: string | null
}

export interface PublicUserProfile {
  userId: number
  nickname: string | null
  avatar: string | null
  gender: string | null
  currentWeight: number | null
  targetWeight: number | null
  streakDays: number | null
  achievementPoints: number | null
  joinedCircleCount: number | null
  mainCircleId: number | null
  mainCircleName: string | null
  self: boolean
  recentPublicFeeds: CircleFeedCard[]
}

export interface HabitItem {
  id: number
  name: string
  icon: string | null
  period: string | null
  frequency: string | null
  reminderTime: string | null
  reminderWeekday: string | null
  active: boolean
  currentStreak: number
  longestStreak: number
  totalCheckins: number
  checkedToday: boolean
  checkedAt: string | null
  checkinId?: number | null
  images?: string[] | string | null
}

export interface HabitCheckinPayload {
  id: number
  habitId: number
  habitName: string | null
  habitIcon: string | null
  checkedAt: string | null
  note: string | null
  images?: string[] | string | null
}

export interface HabitProgressDay {
  date: string
  label: string
  day: string
  completedCount: number
  totalCount: number
  active: boolean
  done: boolean
}

export interface HabitStats {
  totalHabits: number
  activeHabits: number
  completedToday: number
  bestStreak: number
  todayCompletionRate: number
  weeklyCompletionRate: number
  weeklyProgress: HabitProgressDay[]
}

export interface HabitUpsertPayload {
  name: string
  icon?: string | null
  period?: string | null
  frequency?: string | null
  reminderTime?: string | null
  reminderWeekday?: string | null
  active?: boolean
}
