import request from '@/router/axios';

export const getList = (current, size, params) => {
  return request({
    url: '/api/edu/coursestudyhistory/getcourselist',
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
    url: '/api/edu/coursestudyhistory/detail',
    method: 'get',
    params: {
      id
    }
  })
}

export const pinfenStatistic = (data) => {
  return request({
    url: '/api/edu/coursestudyhistory/pinfenStatistic',
    method: 'post',
    data
  })
}

export const getClazzScoreSetting = (params) => {
  return request({
    url: '/api/common/clazz/scoreSetting',
    method: 'get',
    params
  })
}

export const saveClazzScoreSetting = (data) => {
  return request({
    url: '/api/common/clazz/scoreSetting',
    method: 'post',
    data
  })
}

export const pinfenStatisticByStudent = (data) => {
  return request({
    url: '/api/edu/coursestudyhistory/pinfenStatisticByStudent',
    method: 'post',
    data
  })
}


export const remove = (ids) => {
  return request({
    url: '/api/edu/coursestudyhistory/remove',
    method: 'post',
    params: {
      ids,
    }
  })
}

export const add = (row) => {
  return request({
    url: '/api/edu/coursestudyhistory/submit',
    method: 'post',
    data: row
  })
}

export const update = (row) => {
  return request({
    url: '/api/edu/coursestudyhistory/submit',
    method: 'post',
    data: row
  })
}




export const updateQuanzhong = (row) => {
  return request({
    url: '/api/edu/coursestudyhistory/updateQuanzhong',
    method: 'post',
    data: row
  })
}

export const getCourseBaseQuanzhong = (params) => {
  return request({
    url: '/api/edu/coursestudyhistory/courseBaseQuanzhong',
    method: 'get',
    params: params
  })
}

