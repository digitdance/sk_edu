import request from '@/router/axios';
import { baseUrl } from '@/config/env';
// export const ws = "/x/ws"
// 获取考试计划
export const myExamApply = (current, size, params) => {
    return request({
        url: baseUrl + "/front/myExamApply",
        method: "get",
        params: {
            ...params,
            current,
            size
        }
    });
};

//获取banner图
export const getBannerList = (typeId) => {
    return request({
        url: baseUrl + '/front/websiteImages/list',
        method: 'get',
        params: {
            typeId
        }
    })
}

export const getNoticeList = (typeId) => {
    return request({
        url: baseUrl + '/front/noticeList',
        method: 'get',
        params: {
            typeId
        }
    })
}


//获取推荐课程
export const recommendCategory = (frontType) => {
    return request({
        url: baseUrl + '/front/recommendCategory/list',
        method: 'get',
        params: {
            frontType
        }
    })
}

//获取导航条
export const websiteNavigate = (type) => {
    return request({
        url: baseUrl + '/front/websiteNavigate/list',
        method: 'get',
        params: {
            type
        }
    })
}


//获取站点配置
export const websiteProfile = (configType) => {
    return request({
        url: baseUrl + '/front/websiteProfile/list',
        method: 'get',
        params: {
            configType
        }
    })
}


//获取课程科目树
export const subjectTree = () => {
        return request({
            url: baseUrl + '/front/subject/tree',
            method: 'get',
            params: {}
        })
    }
    //获取面授地域
export const addressTree = () => {
    return request({
        url: baseUrl + '/front/subject/addressTree',
        method: 'get',
        params: {}
    })
}



//获取考试科目树
export const subjectExamTree = () => {
    return request({
        url: baseUrl + '/front/subjectExam/tree',
        method: 'get',
        params: {}
    })
}

//获取课程科目列表
export const subjectLevel2List = () => {
    return request({
        url: baseUrl + '/front/subject/subjectLevel2List',
        method: 'get',
        params: {}
    })
}

//获取考试科目列表
export const subjectExamList = () => {
    return request({
        url: baseUrl + '/front/subject/subjectExamList',
        method: 'get',
        params: {}
    })
}

//获取考试科目
export const getExamSubjectList = () => {
    return request({
        url: baseUrl + '/front/subject/getExamSubjectList',
        method: 'get',
        params: {}
    })
}

//获取课程
export const course = (current, size, sellType, level, subjectId, courseName, teacherId, orderBy, addressActive) => {
    return request({
        url: baseUrl + '/front/course/list',
        method: 'get',
        params: {
            current,
            size,
            sellType,
            level,
            subjectId,
            orderBy,
            teacherId,
            courseName,
            addressActive
        }
    })
}

//获取课程
export const lineCourse = (current, size, sellType, level, subjectId, courseName, teacherId, orderBy, addressActive) => {
    return request({
        url: baseUrl + '/front/lineCourse/list',
        method: 'get',
        params: {
            current,
            size,
            sellType,
            level,
            subjectId,
            orderBy,
            teacherId,
            courseName,
            addressActive
        }
    })
}


//获取培训课程
export const trainingCourse = (current, size, sellType, level, subjectId, courseName, teacherId, orderBy, addressActive) => {
    return request({
        url: baseUrl + '/front/trainingCourse/list',
        method: 'get',
        params: {
            current,
            size,
            sellType,
            level,
            subjectId,
            orderBy,
            teacherId,
            courseName,
            addressActive
        }
    })
}

export const examPage = (current, size, params) => {
    return request({
        url: baseUrl + "/front/exam/list",
        method: "get",
        params: {
            ...params,
            current,
            size
        }
    });
};
export const clazzExamPage = (current, size, params) => {
    return request({
        url: baseUrl + "/front/exam/clazzList",
        method: "get",
        params: {
            ...params,
            current,
            size
        }
    });
};

export const courseExamPage = (current, size, params) => {
    return request({
        url: baseUrl + "/front/exam/courseExamList",
        method: "get",
        params: {
            ...params,
            current,
            size
        }
    });
};



//获取试卷
export const exam = (current, size, sellType, level, subjectId, examName, orderBy) => {
    return request({
        url: baseUrl + '/front/exam/list',
        method: 'get',
        params: {
            current,
            size,
            sellType,
            level,
            subjectId,
            orderBy,
            examName
        }
    })
}


export const examKOfPoint = (params) => {
    return request({
        url: baseUrl + '/front/exam/kpointExam',
        method: 'get',
        params: params
    })
}

export const examOfCourse = (params) => {
    return request({
        url: baseUrl + '/front/exam/courseExam',
        method: 'get',
        params: params
    })
}


//获取试卷详情
export const examById = (id) => {
    return request({
        url: baseUrl + '/front/exam/examPaper/' + id,
        method: 'get',
    })
}
export const clazzExamById = (params) => {
    return request({
        url: baseUrl + '/front/exam/clazzExamPaper' ,
        method: 'get',
        params
    })
}

//获取试卷详情
export const paperTypeList = () => {
    return request({
        url: baseUrl + '/front/exam/paperType/list',
        method: 'get',
    })
}



//获取试卷详情
export const examDetail = (id) => {
    return request({
        url: baseUrl + '/front/exam/examPaper/detail',
        method: 'get',
        params: {
            id,
        }
    })
}

export const clazzExamDetail = (params) => {
    return request({
        url: baseUrl + '/front/exam/clazzExamPaper/detail',
        method: 'get',
        params: params
    })
}

//获取收藏列表
export const favoritesqstListAll = (params) => {
    return request({
        url: baseUrl + '/front/exam/favoritesqst/listAll',
        method: 'get',
        params: params
    })
}

//收藏
export const favoritesqstSave = (params) => {
    return request({
        url: baseUrl + '/front/exam/favoritesqst/save',
        method: 'post',
        data: params
    })
}

//取消收藏
export const favoritesqstRemove = (params) => {
    return request({
        url: baseUrl + '/front/exam/favoritesqst/remove',
        method: 'post',
        data: params
    })
}

//获取验证码
export const captcha = () => {
    return request({
        url: baseUrl + '/crazy-auth/captcha',
        method: 'get',
        params: {}
    })
}

//发送手机短信
export const sendSMS = (key, mobile, code) => {
    return request({
        url: baseUrl + '/front/user/sendSMS',
        method: 'get',
        params: {
            key,
            mobile,
            code
        }
    })
}


//发送手机短信
export const sendSMSByMobile = (mobile, type) => {
    return request({
        url: baseUrl + '/front/user/sendSMSByMobile',
        method: 'get',
        params: {
            mobile,
            type
        }
    })
}

//查询资讯类型
export const cmsSubject = () => {
    return request({
        url: baseUrl + '/front/cmsSubject/list',
        method: 'get',
    })
}


//查询资讯列表
export const articleList = (current, size, subjectId) => {
        return request({
            url: baseUrl + '/front/article/list',
            method: 'get',
            params: {
                current,
                size,
                subjectId,
            }
        })
    }
    //查询热门资讯
export const articleHotList = (current, size, subjectId) => {
    return request({
        url: baseUrl + '/front/article/hotList',
        method: 'get',
        params: {
            current,
            size,
            subjectId,
        }
    })
}


//查询资讯详情
export const articleInfo = (id) => {
    return request({
        url: baseUrl + '/front/article/' + id,
        method: 'get',
    })
}

//查询课程详情
export const courseKpoint = (id, courseIds) => {
    return request({
        url: baseUrl + '/edu/coursekpoint/detail?id=' + id,
        method: 'get',
        params: {
            courseIds,
        }
    })
}
export const kpointDetail = (id, courseIds,action) => {
    return request({
        url: baseUrl + '/front/kpointDetail?id=' + id,
        method: 'get',
        params: {
            courseIds,
            action
        }
    })
}


//查询课程详情
export const courseInfo = (id, courseIds) => {
    return request({
        url: baseUrl + '/front/course/' + id,
        method: 'get',
        params: {
            courseIds,
        }
    })
}
export const courseInfoSimple = (id, courseIds) => {
    return request({
        url: baseUrl + '/front/course/simple/' + id,
        method: 'get',
        params: {
            courseIds,
        }
    })
}
//查询用户最新考试记录
export const examPaperByUserId = (subjectId) => {
    return request({
        url: baseUrl + '/front/exam/examHistoy',
        method: 'get',
        params: {
            subjectId: subjectId
        }
    })
}

//查询章节资料
export const materialInfo = (id) => {
    return request({
        url: baseUrl + '/front/material/' + id,
        method: 'get',
    })
}

export const getMaterials = (ids) => {
    return request({
        url: baseUrl + '/front/materials/' + ids,
        method: 'get',
    })
}


export const msgreceiveList = (params) => {
    return request({
        url: baseUrl + '/msg/msgreceive/list',
        method: 'get',
        params: params
    })
}


//查询提问
export const commentPage = (params) => {
    return request({
        url: baseUrl + '/front/cms/commentPage',
        method: 'get',
        params: params
    })
}

//删除站内信
export const removeMsgreceive = (ids) => {
    return request({
        url: baseUrl + '/msg/msgreceive/remove',
        method: 'post',
        params: {
            ids,
        }
    })
}

//通过邮件重置密码
export const sendEmailResetPwd = (params) => {
    return request({
        url: baseUrl + '/front/user/sendEmailResetPwd',
        method: 'get',
        params: params
    })
}

//检查验证码
export const checkCaptcha = (params) => {
    return request({
        url: baseUrl + '/front/user/checkCaptcha',
        method: 'get',
        params: params
    })
}

//通过手机号修改密码
export const updatePwdByMobile = (params) => {
    return request({
        url: baseUrl + '/front/user/updatePwdByMobile',
        method: 'get',
        params: params
    })
}


//获取帮助中心
export const helpMenu = (params) => {
        return request({
            url: baseUrl + '/front/helpMenu/list',
            method: 'get',
            params: params
        })
    }
    //更新站内信状态
export const msgReceiveUpdateState = (userId) => {
    return request({
        url: baseUrl + '/msg/msgreceive/updateState',
        method: 'post',
        params: {
            userId,
        }
    })
}


//获取课程评论
export const commentList = (params) => {
    return request({
        url: baseUrl + '/front/comment/page',
        method: 'get',
        params: params
    })
}


//检查用户是否可以评论
export const checkComment = (params) => {
    return request({
        url: baseUrl + '/cms/comment/checkComment',
        method: 'get',
        params: params
    })
}


//检查是否拥有课程
export const checkHaveCourse = (courseId) => {
    return request({
        url: baseUrl + '/front/user/checkHaveCourse',
        method: 'get',
        params: {
            courseId
        }
    })
}



//收藏
export const examRecordSave = (params) => {
    return request({
        url: baseUrl + '/front/exam/examRecord/save',
        method: 'post',
        data: params
    })
}

//
export const exampaperRecordById = (id) => {
    return request({
        url: baseUrl + '/front/exam/exampaperRecord/' + id,
        method: 'get',
    })
}


export const linkList = (params) => {
    return request({
        url: baseUrl + '/front/websiteNavigate/list',
        method: 'get',
        params: params
    })
}

export const studentLinks = (params) => {
    return request({
        url: baseUrl + '/front/websiteNavigate/schoolLinks',
        method: 'get',
        params: params
    })
}
export const getMyData = (params) => {
    return request({
        url: baseUrl + '/front/getMyData',
        method: 'get',
        params: params
    })
}



export const courseScoreList = (params) => {
    return request({
      url: baseUrl + '/front/courseScoreList',
      method: 'get',
      params: params
    })
}

export const myScoreList = (params) => {
    return request({
      url: baseUrl + '/front/myScoreList',
      method: 'get',
      params: params
    })
}
  