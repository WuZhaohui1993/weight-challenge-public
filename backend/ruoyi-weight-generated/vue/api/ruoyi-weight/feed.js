import request from '@/utils/request'

// 查询圈子动态列表
export function listFeed(query) {
  return request({
    url: '/ruoyi-weight/feed/list',
    method: 'get',
    params: query
  })
}

// 查询圈子动态详细
export function getFeed(id) {
  return request({
    url: '/ruoyi-weight/feed/' + id,
    method: 'get'
  })
}

// 新增圈子动态
export function addFeed(data) {
  return request({
    url: '/ruoyi-weight/feed',
    method: 'post',
    data: data
  })
}

// 修改圈子动态
export function updateFeed(data) {
  return request({
    url: '/ruoyi-weight/feed',
    method: 'put',
    data: data
  })
}

// 删除圈子动态
export function delFeed(id) {
  return request({
    url: '/ruoyi-weight/feed/' + id,
    method: 'delete'
  })
}
