import http from '@/utils/request'
import type {
  ApiPagePayload,
  ExerciseRecordPayload,
  FoodRecordPayload,
  RecordSubmitPayload,
  WaterRecordPayload,
  WeightRecordPayload
} from '@/types/api'

interface RecordListParams {
  pageNum?: number
  pageSize?: number
  beginDate?: string | null
  endDate?: string | null
}

export function createWeightRecord(payload: {
  weight: number
  bodyFatRate?: number | null
  remark?: string | null
  syncToCircles?: string | null
  images?: string | null
}) {
  return http.post<RecordSubmitPayload<WeightRecordPayload>>('/api/record/weight', payload, {
    showLoading: false
  })
}

export function createFoodRecord(payload: {
  mealType: string
  foodName: string
  calories: number
  syncToCircles?: string | null
  remark?: string | null
  images?: string | null
}) {
  return http.post<RecordSubmitPayload<FoodRecordPayload>>('/api/record/food', payload, {
    showLoading: false
  })
}

export function createExerciseRecord(payload: {
  exerciseType: string
  durationMinutes: number
  caloriesBurned: number
  syncToCircles?: string | null
  remark?: string | null
  images?: string | null
}) {
  return http.post<RecordSubmitPayload<ExerciseRecordPayload>>('/api/record/exercise', payload, {
    showLoading: false
  })
}

export function createWaterRecord(payload: {
  cups: number
  ml: number
  images?: string | null
}) {
  return http.post<RecordSubmitPayload<WaterRecordPayload>>('/api/record/water', payload, {
    showLoading: false
  })
}

export function getWeightRecordList(params?: RecordListParams) {
  return http.get<ApiPagePayload<WeightRecordPayload>>('/api/record/weight/list', params, {
    showLoading: false
  })
}

export function getFoodRecordList(params?: RecordListParams) {
  return http.get<ApiPagePayload<FoodRecordPayload>>('/api/record/food/list', params, {
    showLoading: false
  })
}

export function getExerciseRecordList(params?: RecordListParams) {
  return http.get<ApiPagePayload<ExerciseRecordPayload>>('/api/record/exercise/list', params, {
    showLoading: false
  })
}

export function getWaterRecordList(params?: RecordListParams) {
  return http.get<ApiPagePayload<WaterRecordPayload>>('/api/record/water/list', params, {
    showLoading: false
  })
}
