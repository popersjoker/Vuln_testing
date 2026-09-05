import request from "@/utils/request";
import {exportTypedArrayMethod} from "core-js/internals/array-buffer-view-core";

// 查询测试记录列表
export function getRecordList(query) {
  return request({
    url: '/afl/record/recordList',
    method: 'get',
    params: query
  })
}

// 查询测试记录详细
export function getRecord(fid) {
  return request({
    url: '/afl/record/' + fid,
    method: 'get'
  })
}

export function getRecordCrashes(fid) {
  return request({
    url: '/afl/record/getRecordCrashes/' + fid,
    method: 'get',
  })
}

export function getRecordHangs(fid) {
  return request({
    url: '/afl/record/getRecordHangs/' + fid,
    method: 'get',
  })
}

export function getRecordQueue(fid) {
  return request({
    url: '/afl/record/getRecordQueue/' + fid,
    method: 'get',
  })
}

export function getRecordFuzzerStats(fid) {
  return request({
    url: '/afl/record/getRecordFuzzerStats/' + fid,
    method: 'get',
  })
}

// 新增测试记录
export function addRecord(data) {
  return request({
    url: '/afl/record',
    method: 'post',
    data: data
  })
}

// 修改测试记录
export function updateRecord(data) {
  return request({
    url: '/afl/record',
    method: 'put',
    data: data
  })
}

// 删除测试记录
export function delRecord(fid) {
  return request({
    url: '/afl/record/' + fid,
    method: 'delete'
  })
}

export function getCrashesFile(data) {
  return request({
    url: '/afl/record/getCrashesFile/',
    method: 'post',
    data: data,
  })
}

export function getHangsFile(data) {
  return request({
    url: '/afl/record/getHangsFile/',
    method: 'post',
    data: data,
  })
}

export function getQueueFile(data) {
  return request({
    url: '/afl/record/getQueueFile/',
    method: 'post',
    data: data,
  })
}

export function getPaintHtml(fid) {
  return request({
    url: '/afl/record/getPaintHtml/' + fid,
    method: 'get',
  })
}

export function getPaintFile(data) {
  return request({
    url: '/afl/record/getPaintFile/',
    method: 'post',
    data: data,
  })
}

