import request from '@/utils/request'

// 查询每日任务列表
export function listTask(query) {
  return request({
    url: '/ruoyi-weight/task/list',
    method: 'get',
    params: query
  })
}

// 查询每日任务详细
export function getTask(id) {
  return request({
    url: '/ruoyi-weight/task/' + id,
    method: 'get'
  })
}

// 新增每日任务
export function addTask(data) {
  return request({
    url: '/ruoyi-weight/task',
    method: 'post',
    data: data
  })
}

// 修改每日任务
export function updateTask(data) {
  return request({
    url: '/ruoyi-weight/task',
    method: 'put',
    data: data
  })
}

// 删除每日任务
export function delTask(id) {
  return request({
    url: '/ruoyi-weight/task/' + id,
    method: 'delete'
  })
}
