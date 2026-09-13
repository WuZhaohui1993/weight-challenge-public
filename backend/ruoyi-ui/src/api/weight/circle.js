import request from '@/utils/request'

// 查询圈子主列表
export function listCircle(query) {
  return request({
    url: '/ruoyi-weight/circle/list',
    method: 'get',
    params: query
  })
}

// 查询圈子主详细
export function getCircle(id) {
  return request({
    url: '/ruoyi-weight/circle/' + id,
    method: 'get'
  })
}

// 新增圈子主
export function addCircle(data) {
  return request({
    url: '/ruoyi-weight/circle',
    method: 'post',
    data: data
  })
}

// 修改圈子主
export function updateCircle(data) {
  return request({
    url: '/ruoyi-weight/circle',
    method: 'put',
    data: data
  })
}

// 删除圈子主
export function delCircle(id) {
  return request({
    url: '/ruoyi-weight/circle/' + id,
    method: 'delete'
  })
}
