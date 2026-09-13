import request from '@/utils/request'

// 查询习惯定义列表
export function listHabit(query) {
  return request({
    url: '/ruoyi-weight/habit/list',
    method: 'get',
    params: query
  })
}

// 查询习惯定义详细
export function getHabit(id) {
  return request({
    url: '/ruoyi-weight/habit/' + id,
    method: 'get'
  })
}

// 新增习惯定义
export function addHabit(data) {
  return request({
    url: '/ruoyi-weight/habit',
    method: 'post',
    data: data
  })
}

// 修改习惯定义
export function updateHabit(data) {
  return request({
    url: '/ruoyi-weight/habit',
    method: 'put',
    data: data
  })
}

// 删除习惯定义
export function delHabit(id) {
  return request({
    url: '/ruoyi-weight/habit/' + id,
    method: 'delete'
  })
}
