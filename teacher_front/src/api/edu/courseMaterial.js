import request from '@/router/axios';

export const getList = (current, size, params) => {
  return request({
    url: '/api/edu/coursematerial/list',
    method: 'get',
    params: {
      ...params,
      current,
      size,
    }
  })
}

export const getMyList = (current, size, params) => {
  return request({
    url: '/api/edu/coursematerial/myPage',
    method: 'get',
    params: {
      ...params,
      current,
      size,
    }
  })
}

export const getPage = (current, size, params) => {
  return request({
    url: '/api/edu/coursematerial/page',
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
    url: '/api/edu/coursematerial/remove',
    method: 'post',
    params: {
      ids,
    }
  })
}

export const add = (row) => {
  return request({
    url: '/api/edu/coursematerial/save',
    method: 'post',
    data: row
  })
}

export const update = (row) => {
  return request({
    url: '/api/edu/coursematerial/update',
    method: 'post',
    data: row
  })
}


export const getDetail = (id) => {
  return request({
    url: '/api/edu/coursematerial/detail',
    method: 'get',
    params: {
      id
    }
  })
}



