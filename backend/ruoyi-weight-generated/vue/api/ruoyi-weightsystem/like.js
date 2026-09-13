import request from '@/utils/request'

// 查询动态点赞列表
export function listLike(query) {
  return request({
    url: '/ruoyi-weightsystem/like/list',
    method: 'get',
    params: query
  })
}

// 查询动态点赞详细
export function getLike(id) {
  return request({
    url: '/ruoyi-weightsystem/like/' + id,
    method: 'get'
  })
}

// 新增动态点赞
export function addLike(data) {
  return request({
    url: '/ruoyi-weightsystem/like',
    method: 'post',
    data: data
  })
}

// 修改动态点赞
export function updateLike(data) {
  return request({
    url: '/ruoyi-weightsystem/like',
    method: 'put',
    data: data
  })
}

// 删除动态点赞
export function delLike(id) {
  return request({
    url: '/ruoyi-weightsystem/like/' + id,
    method: 'delete'
  })
}
