import request from '@/router/axios';

export const getList = (current, size, params) => {
  return request({
    url: '/api/edu/coursepackage/page',
    method: 'get',
    params: {
      ...params,
      current,
      size,
    }
  })
}


export const coursePage = (params) => {
  return request({
    url: '/api/edu/coursepackage/coursePage',
    method: 'get',
    params: {
      ...params
    }
  })
}
export const syncStudentsCourse = (params) => {
  return request({
    url: '/api/edu/coursepackage/syncStudentsCourse',
    method: 'get',
    params: {
      ...params
    }
  })
}


export const startCoursePackages = ( params) => {
  return request({
    url: '/api/edu/coursepackage/startAll',
    method: 'post',
    data: params
  })
}
export const endCoursePackages = ( params) => {
  return request({
    url: '/api/edu/coursepackage/endAll',
    method: 'post',
    data: params
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
    url: '/api/edu/coursepackage/detail',
    method: 'get',
    params: {
      id
    }
  })
}

export const remove = (ids) => {
  return request({
    url: '/api/edu/coursepackage/remove',
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
    url: '/api/edu/coursepackage/save?courseId='+courseId,
    method: 'post',
    data: row
  })
}

export const update = (row) => {
  return request({
    url: '/api/edu/coursepackage/submit',
    method: 'post',
    data: row
  })
}

