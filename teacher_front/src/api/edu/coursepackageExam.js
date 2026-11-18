import request from '@/router/axios';

export const getList = (current, size, params) => {
  return request({
    url: '/api/edu/coursepackageExam/page',
    method: 'get',
    params: {
      ...params,
      current,
      size,
    }
  })
}


export const examPage = (params) => {
  return request({
    url: '/api/edu/coursepackageExam/examPage',
    method: 'get',
    params: {
      ...params
    }
  })
}


export const clazzExamlist = (current, size, params) => {
  return request({
    url: '/api/edu/coursepackageExam/clazzExamlist',
    method: 'get',
    params: {
      ...params,
      current,
      size,
    }
  })
}
export const completeCoursePackage = ( params) => {
  return request({
    url: '/api/edu/coursepackage/end',
    method: 'post',
    data: params
  })
}

export const getDetail = (id) => {
  return request({
    url: '/api/edu/coursepackageExam/detail',
    method: 'get',
    params: {
      id
    }
  })
}

export const remove = (ids) => {
  return request({
    url: '/api/edu/coursepackageExam/remove',
    method: 'post',
    params: {
      ids,
    }
  })
}

export const add = (row) => {
  return request({
    url: '/api/edu/coursepackage/submit',
    method: 'post',
    data: row
  })
}


export const addList = (row,courseId) => {
  return request({
    url: '/api/edu/coursepackageExam/save?courseId='+courseId,
    method: 'post',
    data: row
  })
}

export const setKpointExam = (row) => {
  return request({
    url: '/api/edu/coursepackageExam/setKpointExam',
    method: 'post',
    data: row
  })
}


export const update = (row) => {
  return request({
    url: '/api/edu/coursepackageExam/submit',
    method: 'post',
    data: row
  })
}
export const updateStatus = (row) => {
  return request({
    url: '/api/edu/coursepackageExam/updateStatus',
    method: 'post',
    data: row
  })
}

