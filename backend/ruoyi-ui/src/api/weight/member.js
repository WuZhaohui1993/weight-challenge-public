import request from '@/utils/request'

// 查询圈子成员列表
export function listMember(query) {
  return request({
    url: '/ruoyi-weight/member/list',
    method: 'get',
    params: query
  })
}

// 查询圈子成员详细
export function getMember(id) {
  return request({
    url: '/ruoyi-weight/member/' + id,
    method: 'get'
  })
}

// 新增圈子成员
export function addMember(data) {
  return request({
    url: '/ruoyi-weight/member',
    method: 'post',
    data: data
  })
}

// 修改圈子成员
export function updateMember(data) {
  return request({
    url: '/ruoyi-weight/member',
    method: 'put',
    data: data
  })
}

// 删除圈子成员
export function delMember(id) {
  return request({
    url: '/ruoyi-weight/member/' + id,
    method: 'delete'
  })
}
