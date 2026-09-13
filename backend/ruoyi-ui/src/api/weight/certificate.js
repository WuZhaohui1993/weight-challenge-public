import request from '@/utils/request'

// 查询HTTPS证书列表
export function listCertificate(query) {
  return request({
    url: '/ruoyi-weight/certificate/list',
    method: 'get',
    params: query
  })
}

// 查询HTTPS证书概览
export function getCertificateSummary() {
  return request({
    url: '/ruoyi-weight/certificate/summary',
    method: 'get'
  })
}

// 查询HTTPS证书详细
export function getCertificate(id) {
  return request({
    url: '/ruoyi-weight/certificate/' + id,
    method: 'get'
  })
}

// 新增HTTPS证书
export function addCertificate(data) {
  return request({
    url: '/ruoyi-weight/certificate',
    method: 'post',
    data: data
  })
}

// 修改HTTPS证书
export function updateCertificate(data) {
  return request({
    url: '/ruoyi-weight/certificate',
    method: 'put',
    data: data
  })
}

// 删除HTTPS证书
export function delCertificate(id) {
  return request({
    url: '/ruoyi-weight/certificate/' + id,
    method: 'delete'
  })
}

// 检测HTTPS证书
export function checkCertificate(id) {
  return request({
    url: '/ruoyi-weight/certificate/' + id + '/check',
    method: 'post'
  })
}

// 申请或续签HTTPS证书
export function renewCertificate(id, force) {
  return request({
    url: '/ruoyi-weight/certificate/' + id + '/renew',
    method: 'post',
    params: { force: !!force }
  })
}
