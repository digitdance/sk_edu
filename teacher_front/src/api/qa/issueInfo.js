import request from '@/router/axios';

export const getList = (current, size, params) => {
  return request({
    url: '/api/qa/issueInfo/page',
    method: 'get',
    params: {
      ...params,
      current,
      size,
    }
  })
}

export const getDetail = (id) => {
  return request({
    url: '/api/qa/issueInfo/detail',
    method: 'get',
    params: {
      id
    }
  })
}

export const remove = (ids) => {
  return request({
    url: '/api/qa/issueInfo/remove',
    method: 'post',
    params: {
      ids,
    }
  })
}

export const add = (row) => {
  return request({
    url: '/api/qa/issueInfo/submit',
    method: 'post',
    data: row
  })
}

export const reply = (row) => {
  return request({
    url: '/api/edu/issue/answerIssue',
    method: 'post',
    data: row
  })
}
export const jinghua = (row) => {
  return request({
    url: '/api/edu/issue/answerIssue',
    method: 'post',
    data: row
  })
}

export const update = (row) => {
  return request({
    url: '/api/qa/issueInfo/submit',
    method: 'post',
    data: row
  })
}

