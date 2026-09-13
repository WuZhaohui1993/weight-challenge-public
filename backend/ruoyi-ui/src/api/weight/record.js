import request from '@/utils/request'

// 查询运动记录列表
export function listRecord(query) {
  return request({
    url: '/ruoyi-weight/exercise-record/list',
    method: 'get',
    params: query
  })
}

// 查询运动记录详细
export function getRecord(id) {
  return request({
    url: '/ruoyi-weight/exercise-record/' + id,
    method: 'get'
  })
}

// 新增运动记录
export function addRecord(data) {
  return request({
    url: '/ruoyi-weight/exercise-record',
    method: 'post',
    data: data
  })
}

// 修改运动记录
export function updateRecord(data) {
  return request({
    url: '/ruoyi-weight/exercise-record',
    method: 'put',
    data: data
  })
}

// 删除运动记录
export function delRecord(id) {
  return request({
    url: '/ruoyi-weight/exercise-record/' + id,
    method: 'delete'
  })
}
