import request from '@/router/axios';

export const getList = (current, size, params) => {
  return request({
    url: '/api/user/student/list',
    method: 'get',
    params: {
      ...params,
      current,
      size,
    }
  })
}


export const getStudentList = (current, size, params) => {
  return request({
    url: '/api/user/student/studentList',
    method: 'get',
    params: {
      ...params,
      current,
      size,
    }
  })
}


export const getStudentsNotinClazz = (current, size, params) => {
  return request({
    url: '/api/user/student/getStudentsNotinClazz',
    method: 'get',
    params: {
      ...params,
      current,
      size,
    }
  })
}

export const removeStudentClazz = (params) => {
  return request({
    url: '/api/user/student/removeStudentClazz',
    method: 'get',
    params: {
      ...params
    }
  })
}


export const getStudentsClazz = (params) => {
  return request({
    url: '/api/user/student/getStudentsClazz',
    method: 'get',
    params: {
      ...params
    }
  })
}


export const selectClazzStudentPage = (params) => {
  return request({
    url: '/api/user/student/selectClazzStudentPage',
    method: 'get',
    params: {
      ...params
    }
  })
}

export const getStudentScores = (params) => {
  return request({
    url: '/api/edu/coursepackageExam/getStudentScores',
    method: 'get',
    params: {
      ...params
    }
  })
}

export const getDetail = (id) => {
  return request({
    url: '/api/user/student/detail',
    method: 'get',
    params: {
      id
    }
  })
}

export const remove = (ids) => {
  return request({
    url: '/api/user/student/remove',
    method: 'post',
    params: {
      ids,
    }
  })
}

export const add = (row) => {
  return request({
    url: '/api/user/student/save',
    method: 'post',
    data: row
  })
}

export const update = (row) => {
  return request({
    url: '/api/user/student/submit',
    method: 'post',
    data: row
  })
}

export const setPassword = (row) => {
  return request({
    url: '/api/user/student/setPassword',
    method: 'post',
    data: row
  })
}

export const setPasswordParent = (row) => {
  return request({
    url: '/api/user/student/setPassword',
    method: 'post',
    data: row
  })
}


export const getAdminUserList = () => {
  return request({
    url: '/api/crazy-user/getAdminUserList',
    method: 'get',
  })
}

