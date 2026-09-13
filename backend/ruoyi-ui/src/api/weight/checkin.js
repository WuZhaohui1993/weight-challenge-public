import request from '@/utils/request'

// 查询习惯打卡记录列表
export function listCheckin(query) {
  return request({
    url: '/ruoyi-weight/checkin/list',
    method: 'get',
    params: query
  })
}

// 查询习惯打卡记录详细
export function getCheckin(id) {
  return request({
    url: '/ruoyi-weight/checkin/' + id,
    method: 'get'
  })
}

// 新增习惯打卡记录
export function addCheckin(data) {
  return request({
    url: '/ruoyi-weight/checkin',
    method: 'post',
    data: data
  })
}

// 修改习惯打卡记录
export function updateCheckin(data) {
  return request({
    url: '/ruoyi-weight/checkin',
    method: 'put',
    data: data
  })
}

// 删除习惯打卡记录
export function delCheckin(id) {
  return request({
    url: '/ruoyi-weight/checkin/' + id,
    method: 'delete'
  })
}
