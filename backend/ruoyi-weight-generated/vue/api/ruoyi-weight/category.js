import request from '@/utils/request'

// 查询圈子分类列表
export function listCategory(query) {
  return request({
    url: '/ruoyi-weight/category/list',
    method: 'get',
    params: query
  })
}

// 查询圈子分类详细
export function getCategory(id) {
  return request({
    url: '/ruoyi-weight/category/' + id,
    method: 'get'
  })
}

// 新增圈子分类
export function addCategory(data) {
  return request({
    url: '/ruoyi-weight/category',
    method: 'post',
    data: data
  })
}

// 修改圈子分类
export function updateCategory(data) {
  return request({
    url: '/ruoyi-weight/category',
    method: 'put',
    data: data
  })
}

// 删除圈子分类
export function delCategory(id) {
  return request({
    url: '/ruoyi-weight/category/' + id,
    method: 'delete'
  })
}
