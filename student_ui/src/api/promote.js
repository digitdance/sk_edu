import request from '@/router/axios';

export const getList = (current, size, params) => {
    return request({
        url: '/api/edu/promote/page',
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
        url: '/api/edu/promote/detail',
        method: 'get',
        params: {
            id
        }
    })
}

export const remove = (ids) => {
    return request({
        url: '/api/edu/promote/remove',
        method: 'post',
        params: {
            ids,
        }
    })
}

export const add = (row) => {
    return request({
        url: '/api/edu/promote/submit',
        method: 'post',
        data: row
    })
}

export const update = (row) => {
    return request({
        url: '/api/edu/promote/submit',
        method: 'post',
        data: row
    })
}

export const gePage = (current, size, params) => {
    return request({
        url: '/api/edu/course/list',
        method: 'get',
        params: {
            ...params,
            current,
            size,
        }
    })
}