import request from '@/utils/request'

// 查询afl测试列表
export function listTestafl(query) {
  return request({
    url: '/afl/testafl/list',
    method: 'get',
    params: query
  })
}

// 查询afl测试详细
export function getTestafl(fid) {
  return request({
    url: '/afl/testafl/' + fid,
    method: 'get'
  })
}

// 新增afl测试
export function addTestafl(data) {
  return request({
    url: '/afl/testafl',
    method: 'post',
    data: data
  })
}

// 修改afl测试
export function updateTestafl(data) {
  return request({
    url: '/afl/testafl',
    method: 'put',
    data: data
  })
}

// 删除afl测试
export function delTestafl(fid) {
  return request({
    url: '/afl/testafl/' + fid,
    method: 'delete'
  })
}
export function testafl() {
  return request({
    url: '/afl/testafl/test',
    method: 'get'
  })
}
