import request from '@/utils/request'

// 查询评论点赞列表
export function listLike(query) {
  return request({
    url: '/ruoyi-weight/like/list',
    method: 'get',
    params: query
  })
}

// 查询评论点赞详细
export function getLike(id) {
  return request({
    url: '/ruoyi-weight/like/' + id,
    method: 'get'
  })
}

// 新增评论点赞
export function addLike(data) {
  return request({
    url: '/ruoyi-weight/like',
    method: 'post',
    data: data
  })
}

// 修改评论点赞
export function updateLike(data) {
  return request({
    url: '/ruoyi-weight/like',
    method: 'put',
    data: data
  })
}

// 删除评论点赞
export function delLike(id) {
  return request({
    url: '/ruoyi-weight/like/' + id,
    method: 'delete'
  })
}
