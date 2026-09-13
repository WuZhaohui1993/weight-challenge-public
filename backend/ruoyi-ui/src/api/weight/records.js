import request from '@/utils/request'

// 获取体重记录列表
export function listWeightRecords(query) {
    return request({
        url: '/ruoyi-weight/weight-record/list',
        method: 'get',
        params: query
    })
}

// 获取饮食记录列表
export function listFoodRecords(query) {
    return request({
        url: '/ruoyi-weight/food-record/list',
        method: 'get',
        params: query
    })
}

// 获取运动记录列表
export function listExerciseRecords(query) {
    return request({
        url: '/ruoyi-weight/exercise-record/list',
        method: 'get',
        params: query
    })
}

// 获取饮水记录列表
export function listWaterRecords(query) {
    return request({
        url: '/ruoyi-weight/water-record/list',
        method: 'get',
        params: query
    })
}
