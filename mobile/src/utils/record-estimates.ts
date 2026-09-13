import { normalizeExerciseType } from '@/utils/exercise'

export interface FoodCalorieOption {
  name: string
  aliases: string[]
  serving: string
  calories: number
}

export const commonExerciseTypes = [
  '户外跑步',
  '室内跑步',
  '健走',
  '快走',
  '散步',
  '骑行',
  '动感单车',
  '游泳',
  '跳绳',
  'HIIT',
  '力量训练',
  '瑜伽',
  '普拉提',
  '羽毛球',
  '乒乓球',
  '篮球',
  '足球',
  '网球',
  '椭圆机',
  '划船机',
  '爬楼',
  '登山',
  '健身操',
  '舞蹈',
  '拉伸',
  '太极'
]

export const exerciseMetMap: Record<string, number> = {
  户外跑步: 8.3,
  室内跑步: 7.0,
  健走: 4.3,
  快走: 5.0,
  散步: 3.0,
  骑行: 6.8,
  动感单车: 7.5,
  游泳: 8.0,
  跳绳: 11.8,
  HIIT: 8.0,
  力量训练: 5.0,
  瑜伽: 2.5,
  普拉提: 3.0,
  羽毛球: 5.5,
  乒乓球: 4.0,
  篮球: 6.5,
  足球: 7.0,
  网球: 7.3,
  椭圆机: 5.0,
  划船机: 7.0,
  爬楼: 8.8,
  登山: 6.0,
  健身操: 6.5,
  舞蹈: 5.0,
  拉伸: 2.3,
  太极: 3.0
}

export const commonFoodOptions: FoodCalorieOption[] = [
  { name: '米饭', aliases: ['白米饭', '米饭'], serving: '1 碗 150g', calories: 174 },
  { name: '馒头', aliases: ['馒头'], serving: '1 个 100g', calories: 236 },
  { name: '鸡胸肉', aliases: ['鸡胸', '鸡胸肉'], serving: '1 份 100g', calories: 165 },
  { name: '鸡蛋', aliases: ['鸡蛋', '水煮蛋'], serving: '1 个 50g', calories: 70 },
  { name: '牛奶', aliases: ['牛奶', '纯牛奶'], serving: '1 杯 250ml', calories: 150 },
  { name: '燕麦', aliases: ['燕麦', '燕麦片'], serving: '1 份 40g', calories: 150 },
  { name: '苹果', aliases: ['苹果'], serving: '1 个 200g', calories: 104 },
  { name: '香蕉', aliases: ['香蕉'], serving: '1 根 120g', calories: 107 },
  { name: '红薯', aliases: ['红薯', '地瓜'], serving: '1 个 200g', calories: 180 },
  { name: '牛肉', aliases: ['牛肉', '瘦牛肉'], serving: '1 份 100g', calories: 180 },
  { name: '三文鱼', aliases: ['三文鱼'], serving: '1 份 100g', calories: 208 },
  { name: '西兰花', aliases: ['西兰花'], serving: '1 份 100g', calories: 34 },
  { name: '沙拉', aliases: ['沙拉', '蔬菜沙拉'], serving: '1 份', calories: 180 },
  { name: '面条', aliases: ['面条', '汤面'], serving: '1 碗', calories: 330 },
  { name: '饺子', aliases: ['饺子'], serving: '10 个', calories: 420 },
  { name: '火锅', aliases: ['火锅'], serving: '1 餐', calories: 800 }
]

export function normalizeFoodName(name: string) {
  return String(name || '').replace(/\s+/g, '').toLowerCase()
}

export function parsePositiveNumber(rawValue: string | number | null | undefined, fallback = 1) {
  const parsed = Number(rawValue)
  return Number.isFinite(parsed) && parsed > 0 ? parsed : fallback
}

export function findFoodEstimate(foodName: string) {
  const normalizedName = normalizeFoodName(foodName)
  if (!normalizedName) {
    return null
  }
  return commonFoodOptions.find((food) =>
    food.aliases.some((alias) => normalizedName.includes(normalizeFoodName(alias)))
  ) || null
}

export function estimateFoodCalories(food: FoodCalorieOption, portionMultiplier: string | number = 1) {
  return Math.max(1, Math.round(food.calories * parsePositiveNumber(portionMultiplier, 1)))
}

export function getExerciseMet(exerciseType: string) {
  return exerciseMetMap[normalizeExerciseType(exerciseType)] || 0
}

export function estimateExerciseCalories(exerciseType: string, minutes: string | number, bodyWeight: string | number) {
  const met = getExerciseMet(exerciseType)
  const durationMinutes = Number(minutes)
  const normalizedBodyWeight = Number(bodyWeight)
  if (!met || !durationMinutes || !normalizedBodyWeight) {
    return 0
  }
  return Math.max(1, Math.round(met * normalizedBodyWeight * (durationMinutes / 60)))
}
