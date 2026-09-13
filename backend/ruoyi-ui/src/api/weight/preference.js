import request from '@/utils/request'

// 查询同步偏好设置列表
export function listPreference(query) {
  return request({
    url: '/ruoyi-weight/preference/list',
    method: 'get',
    params: query
  })
}

// 查询同步偏好设置详细
export function getPreference(id) {
  return request({
    url: '/ruoyi-weight/preference/' + id,
    method: 'get'
  })
}

// 新增同步偏好设置
export function addPreference(data) {
  return request({
    url: '/ruoyi-weight/preference',
    method: 'post',
    data: data
  })
}

// 修改同步偏好设置
export function updatePreference(data) {
  return request({
    url: '/ruoyi-weight/preference',
    method: 'put',
    data: data
  })
}

// 删除同步偏好设置
export function delPreference(id) {
  return request({
    url: '/ruoyi-weight/preference/' + id,
    method: 'delete'
  })
}
