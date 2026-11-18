import request from '@/router/axios';

export const getList = (current, size, params) => {
    return request({
        url: '/api/skils/plan/page',
        method: 'get',
        params: {
            ...params,
            current,
            size,
        }
    })
}

export const all = (current, size, params) => {
    return request({
        url: "/api/skils/plan/all",
        method: "get",
        params: {
            ...params,
            current,
            size
        }
    });
};

export const getDetail = (id) => {
    return request({
        url: '/api/skils/plan/detail',
        method: 'get',
        params: {
            id
        }
    })
}

export const remove = (ids) => {
    return request({
        url: '/api/skils/plan/deleted',
        method: 'post',
        params: {
            ids,
        }
    })
}

export const add = (row) => {
    return request({
        url: '/api/skils/plan/submit',
        method: 'post',
        data: row
    })
}

export const update = (row) => {
    return request({
        url: '/api/skils/plan/submit',
        method: 'post',
        data: row
    })
}