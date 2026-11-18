import request from '@/router/axios';

export const getList = (current, size, params) => {
  return request({
    url: '/api/exam/question/list',
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
    url: '/api/exam/question/detail',
    method: 'get',
    params: {
      id
    }
  })
}

export const getShicaoList = (paperId) => {
  return request({
    url: '/api/exam/qstmiddle/shicaoList',
    method: 'get',
    params: {
      paperId
    }
  })
}
export const getQuestionsByids = (ids) => {
  return request({
    url: '/api/exam/question/getQuestionsByids',
    method: 'get',
    params: {
      ids
    }
  })
}




export const remove = (ids) => {
  return request({
    url: '/api/exam/question/remove',
    method: 'post',
    params: {
      ids,
    }
  })
}
export const removeMiddle = (ids) => {
  return request({
    url: '/api/exam/qstmiddle/remove',
    method: 'post',
    params: {
      ids,
    }
  })
}
export const add = (row) => {
  return request({
    url: '/api/exam/question/submit',
    method: 'post',
    data: row
  })
}

export const update = (row) => {
  return request({
    url: '/api/exam/question/submit',
    method: 'post',
    data: row
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
export const kpointTreeList = () => {
  return request({
    url: '/api/edu/coursekpoint/treeByCourse',
    method: 'get',
    params: {
    }
  })
}

export const questionTypeList = () => {
  return request({
    url: '/api/crazy-system/dict/dictionary?code=exam_question_type',
    method: 'get',
    params: {
    }
  })
}




