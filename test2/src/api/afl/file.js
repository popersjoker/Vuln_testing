import request from '@/utils/request'
import {getToken} from "@/utils/auth";

// 查询源码文件信息列表
export function listFile(query) {
  return request({
    url: '/afl/file/list',
    method: 'get',
    params: query
  })
}

// 查询源码文件信息详细
export function getFile(yid) {
  return request({
    url: '/afl/file/' + yid,
    method: 'get'
  })
}

// 新增源码文件信息
export function addFile(data) {
  return request({
    url: '/afl/file',
    method: 'post',
    data: data
  })
}

// 修改源码文件信息
export function updateFile(data) {
  return request({
    url: '/afl/file',
    method: 'put',
    data: data
  })
}

// 删除源码文件信息
export function delFile(yid) {
  return request({
    url: '/afl/file/' + yid,
    method: 'delete'
  })
}

// 10.24 新写的一个功能，将上传文件的一些信息，通过异步请求发出去
export function uploadFiles(data) {
  return request({
    url: '/afl/file/upload',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'multipart/form-data',
       Authorization: "Bearer" + getToken()
    }
  })
}

export function uploadInput(data){
  return request({
    url: '/afl/file/upload-input',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'multipart/form-data',
      Authorization: "Bearer" + getToken()
    }
  })
}
// 10.26 新增功能 查询源码文件信息表的yid，file_name，upload_time 显示在表格处
export function listFileInfo3(query){
  return request({
    url: '/afl/file/list123',
    method: 'get',
    params: query
  })
}

// 根据文件名查找yid，根据找到的yid让它显示
export function getYidByFileName(data) {
  return request({
    url: '/afl/file/getYidByFileName',
    method: 'post',
    data: data
  })
}

export function getFileNameAndTwoTime(data){
  return request({
    url: '/afl/file/getFileNameAndTwoTime',
    method: 'post',
    data: data
  })
}

export function updateFiles(data) { //传回去的参数一定要有一个yid，还有一个现成的文件
  return request({
    url: '/afl/file/updateFiles',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'multipart/form-data',
      Authorization: "Bearer" + getToken()
    }
  })
}

