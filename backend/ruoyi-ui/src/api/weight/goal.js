import request from '@/utils/request'

// 查询圈子目标列表
export function listGoal(query) {
  return request({
    url: '/ruoyi-weight/goal/list',
    method: 'get',
    params: query
  })
}

// 查询圈子目标详细
export function getGoal(id) {
  return request({
    url: '/ruoyi-weight/goal/' + id,
    method: 'get'
  })
}

// 新增圈子目标
export function addGoal(data) {
  return request({
    url: '/ruoyi-weight/goal',
    method: 'post',
    data: data
  })
}

// 修改圈子目标
export function updateGoal(data) {
  return request({
    url: '/ruoyi-weight/goal',
    method: 'put',
    data: data
  })
}

// 删除圈子目标
export function delGoal(id) {
  return request({
    url: '/ruoyi-weight/goal/' + id,
    method: 'delete'
  })
}
