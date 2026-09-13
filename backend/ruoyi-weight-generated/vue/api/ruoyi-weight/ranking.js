import request from '@/utils/request'

// 查询排行榜缓存列表
export function listRanking(query) {
  return request({
    url: '/ruoyi-weight/ranking/list',
    method: 'get',
    params: query
  })
}

// 查询排行榜缓存详细
export function getRanking(id) {
  return request({
    url: '/ruoyi-weight/ranking/' + id,
    method: 'get'
  })
}

// 新增排行榜缓存
export function addRanking(data) {
  return request({
    url: '/ruoyi-weight/ranking',
    method: 'post',
    data: data
  })
}

// 修改排行榜缓存
export function updateRanking(data) {
  return request({
    url: '/ruoyi-weight/ranking',
    method: 'put',
    data: data
  })
}

// 删除排行榜缓存
export function delRanking(id) {
  return request({
    url: '/ruoyi-weight/ranking/' + id,
    method: 'delete'
  })
}
