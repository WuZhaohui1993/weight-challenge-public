import request from '@/utils/request'

// 查询用户扩展信息列表
export function listUser(query) {
  return request({
    url: '/ruoyi-weight/user/list',
    method: 'get',
    params: query
  })
}

// 查询用户扩展信息详细
export function getUser(userId) {
  return request({
    url: '/ruoyi-weight/user/' + userId,
    method: 'get'
  })
}

// 新增用户扩展信息
export function addUser(data) {
  return request({
    url: '/ruoyi-weight/user',
    method: 'post',
    data: data
  })
}

// 修改用户扩展信息
export function updateUser(data) {
  return request({
    url: '/ruoyi-weight/user',
    method: 'put',
    data: data
  })
}

// 删除用户扩展信息
export function delUser(userId) {
  return request({
    url: '/ruoyi-weight/user/' + userId,
    method: 'delete'
  })
}
