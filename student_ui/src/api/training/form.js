import request from '@/router/axios';
import {baseUrl} from '@/config/env';
export const getList = (current, size, params) => {
    return request({
        url: '/api/training/form/page',
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
        url: '/api/training/form/detail',
        method: 'get',
        params: {
            id
        }
    })
}

export const remove = (ids) => {
    return request({
        url: '/api/training/form/deleted',
        method: 'post',
        params: {
            ids,
        }
    })
}

export const add = (row) => {
    return request({
        url: baseUrl + '/training/form/submit',
        method: 'post',
        data: row
    })
}

export const update = (row) => {
    return request({
        url: '/api/training/form/submit',
        method: 'post',
        data: row
    })
}
