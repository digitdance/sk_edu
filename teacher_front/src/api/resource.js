import request from '@/router/axios';

export const getDetail = (params) => {
  return request({
    url: '/api/resource/detail',
    method: 'get',
    params: params
  })
}
