import request from '@/router/axios';

export const getDetail = (code) => {
  return request({
    url: '/api/web/statdata/detail',
    method: 'get',
    params: {
      code
    }
  })
}

export const getDataByDay = (param) =>
{
  if (param.days == 1) {
    return request({
      url: '/api/web/statdata/detail?code=statadminuserdata',
      method: 'post',
      data:param
    })
  }else if (param.days == 2){
    return request({
      url: '/api/web/statdata/user/statistics?code=statadminuserdata',
      method: 'post',
      data:param
    })
  }else if (param.days == 3){
    return request({
      url: '/api/web/statdata/detail',
      method: 'post',
      data:param
    })
  }
}


