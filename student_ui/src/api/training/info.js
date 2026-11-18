import request from '@/router/axios';
import {baseUrl} from '@/config/env';
export const getList = (current, size, params) => {
    return request({
        url: baseUrl + '/training/info/list',
        method: 'get',
        params: {
            ...params,
            current,
            size,
        },
    })
}
export const searchList = (current, size, params) => {
  return request({
    url: baseUrl + '/training/info/list',
    method: 'get',
    params: {
      params,
      current,
      size,
    }
  })
}

export const getDetail = (id) => {
    return request({
        url: '/api/training/info/detail',
        method: 'get',
        params: {
            id
        }
    })
}

// export const remove = (ids) => {
//     return request({
//         url: '/api/training/info/deleted',
//         method: 'post',
//         params: {
//             ids,
//         }
//     })
// }

// 逻辑删除
export const remove = (ids) => {
    return request({
        url: '/api/training/info/deleted',
        method: 'post',
        params: {
            ids,
        }
    })
}

export const add = (row) => {
    return request({
        url: '/api/training/info/submit',
        method: 'post',
        data: row
    })
}

export const update = (row) => {
    return request({
        url: '/api/training/info/submit',
        method: 'post',
        data: row
    })
}
