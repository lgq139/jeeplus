import {get, post} from "@/api/tool";

export function getNotice(params) {
  return get('/sys/notice/getById', params)
}

export function listNotice(parames) {
  return get('/sys/notice/list', parames)
}


export function saveNotice(parames) {
  return post('/sys/notice/save', parames)
}


export function deleteNotice(parames) {
  return post('/sys/notice/delete', parames)
}
