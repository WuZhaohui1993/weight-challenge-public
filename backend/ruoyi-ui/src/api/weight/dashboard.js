import request from '@/utils/request'

// 获取仪表盘概览数据
export function getOverview() {
    return request({
        url: '/ruoyi-weight/dashboard/overview',
        method: 'get'
    })
}

// 获取打卡趋势
export function getCheckinTrend() {
    return request({
        url: '/ruoyi-weight/dashboard/checkin-trend',
        method: 'get'
    })
}

// 获取热门圈子
export function getTopCircles() {
    return request({
        url: '/ruoyi-weight/dashboard/top-circles',
        method: 'get'
    })
}

// 获取内容运营统计
export function getContentStats() {
    return request({
        url: '/ruoyi-weight/dashboard/content-stats',
        method: 'get'
    })
}
