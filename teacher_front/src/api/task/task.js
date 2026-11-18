import request from '@/router/axios';

export const getList = (current, size, params) => {
  return request({
    url: '/api/task/task/list',
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
    url: '/api/task/task/detail',
    method: 'get',
    params: {
      id
    }
  })
}


export const subjectExamTreeList = () => {
  return request({
    url: '/api/edu/subject/exam/tree',
    method: 'get',
    params: {
    }
  })
}
export const remove = (ids) => {
  return request({
    url: '/api/task/task/remove',
    method: 'post',
    params: {
      ids,
    }
  })
}

export const add = (row) => {
  return request({
    url: '/api/task/task/submit',
    method: 'post',
    data: row
  })
}

export const update = (row) => {
  return request({
    url: '/api/task/task/submit',
    method: 'post',
    data: row
  })
}

