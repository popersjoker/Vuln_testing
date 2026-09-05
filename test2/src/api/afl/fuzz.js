import request from '@/utils/request'

export function startAfl(data) {
  return request({
    url: '/afl/testafl/startAfl',
    method: 'post',
    data: data
  })
}

export function getResults() {
  return request({
    url: '/afl/testafl/getResults',
    method: 'get',
  })
}

// 用于截断当前命令
export function stopAFl(uuid) {
  return request({
    url: '/afl/testafl/stopAFl/' + uuid,
    method: 'get',
  })
}

export function ClearFuzzOut() {
  return request({
    url: '/afl/testafl/deleteFuzzOut',
    method: 'get',
  })
}
