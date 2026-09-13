import request from '@/utils/request'

// 查询消息通知列表
export function listNotification(query) {
  return request({
    url: '/ruoyi-weight/notification/list',
    method: 'get',
    params: query
  })
}

// 查询消息通知详细
export function getNotification(id) {
  return request({
    url: '/ruoyi-weight/notification/' + id,
    method: 'get'
  })
}

// 新增消息通知
export function addNotification(data) {
  return request({
    url: '/ruoyi-weight/notification',
    method: 'post',
    data: data
  })
}

// 管理端发送系统通知
export function broadcastNotification(data) {
  return request({
    url: '/ruoyi-weight/notification/broadcast',
    method: 'post',
    data: data
  })
}

// 修改消息通知
export function updateNotification(data) {
  return request({
    url: '/ruoyi-weight/notification',
    method: 'put',
    data: data
  })
}

// 删除消息通知
export function delNotification(id) {
  return request({
    url: '/ruoyi-weight/notification/' + id,
    method: 'delete'
  })
}
