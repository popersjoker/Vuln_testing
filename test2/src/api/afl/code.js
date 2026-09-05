import request from "@/utils/request";

export function getCode(yid) {
  return request({
    url: '/afl/code/getCode/' + yid,
    method: 'get',
  })
}

export function AflGcc(yid) {
  return request({
    url: '/afl/code/AflGcc/' + yid,
    method: 'get',
  })
}

export function getCompile(yid) {
  return request({
    url: 'afl/compile/getCompile/' + yid,
    method: 'get',
  })
}
export function getCompileInfo(cid) {
  return request({
    url: 'afl/compile/' + cid,
    method: 'get',
  })
}

export function getInputFile(yid) {
  return request({
    url: 'afl/uploadInput/getInputFile/' + yid,
    method: 'get',
  })
}

export function getInputInfo(yid) {
  return request({
    url: 'afl/uploadInput/getInputInfo/' + yid,
    method: 'get',
  })
}
