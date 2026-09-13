import request from '@/utils/request'

// 查询押金记录列表
export function listDeposit(query) {
  return request({
    url: '/ruoyi-weight/deposit/list',
    method: 'get',
    params: query
  })
}

// 查询押金记录详细
export function getDeposit(id) {
  return request({
    url: '/ruoyi-weight/deposit/' + id,
    method: 'get'
  })
}

// 新增押金记录
export function addDeposit(data) {
  return request({
    url: '/ruoyi-weight/deposit',
    method: 'post',
    data: data
  })
}

// 修改押金记录
export function updateDeposit(data) {
  return request({
    url: '/ruoyi-weight/deposit',
    method: 'put',
    data: data
  })
}

// 删除押金记录
export function delDeposit(id) {
  return request({
    url: '/ruoyi-weight/deposit/' + id,
    method: 'delete'
  })
}
