import request from '@/utils/request'

// 查询成就徽章列表
export function listAchievement(query) {
  return request({
    url: '/ruoyi-weight/achievement/list',
    method: 'get',
    params: query
  })
}

// 查询成就徽章详细
export function getAchievement(id) {
  return request({
    url: '/ruoyi-weight/achievement/' + id,
    method: 'get'
  })
}

// 新增成就徽章
export function addAchievement(data) {
  return request({
    url: '/ruoyi-weight/achievement',
    method: 'post',
    data: data
  })
}

// 修改成就徽章
export function updateAchievement(data) {
  return request({
    url: '/ruoyi-weight/achievement',
    method: 'put',
    data: data
  })
}

// 删除成就徽章
export function delAchievement(id) {
  return request({
    url: '/ruoyi-weight/achievement/' + id,
    method: 'delete'
  })
}
