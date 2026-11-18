import request from '@/router/axios';

export const getList = (current, size, params) => {
  return request({
    url: '/api/edu/coursehomework/list',
    method: 'get',
    params: {
      ...params,
      current,
      size,
    }
  })
}


export const remove = (ids) => {
  return request({
    url: '/api/edu/coursehomework/remove',
    method: 'post',
    params: {
      ids,
    }
  })
}

export const add = (row) => {
  return request({
    url: '/api/edu/coursehomework/save',
    method: 'post',
    data: row
  })
}

export const update = (row) => {
  return request({
    url: '/api/edu/coursehomework/update',
    method: 'post',
    data: row
  })
}


